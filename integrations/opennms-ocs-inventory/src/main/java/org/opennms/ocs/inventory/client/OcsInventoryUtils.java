package org.opennms.ocs.inventory.client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.opennms.core.utils.ConfigFileConstants;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.model.PrimaryType;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset;
import org.opennms.netmgt.provision.persist.requisition.RequisitionInterface;
import org.opennms.netmgt.provision.persist.requisition.RequisitionMonitoredService;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.opennms.ocs.inventory.client.request.logic.OcsInventoryClientLogicExecuter;
import org.opennms.ocs.inventory.client.response.Computer;
import org.opennms.ocs.inventory.client.response.Computers;
import org.opennms.ocs.inventory.client.response.Storage;
import org.springframework.transaction.annotation.Transactional;

public class OcsInventoryUtils {

/** The Constant s_path_to_script_folder. */
private final static String s_path_to_script_folder="/etc/ocs-scripts/";//"src/test/groovy/script/";

    /** The ocs inventory client logic. */
private static OcsInventoryClientLogicExecuter ocsInventoryClientLogic = new OcsInventoryClientLogicExecuter();

    /**
     * {@inheritDoc}
     */
    @Transactional
    public static Requisition importProvisionNodes(Requisition req, String host, String login, String password, String foreignSource,
                                        String engine, String checkSum) {
		log().info(
				String.format(
						" Import nodes from OCS Inventory host =%s, login =%s, foreignSource =%s, engine =%s",
						host, login, foreignSource, engine));
		String script = getContentScriptFromFile(foreignSource, engine);
		// java.lang.System.setProperty("javax.xml.soap.MessageFactory",
		// "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl");
		req = new Requisition(foreignSource);
		ManagerScript m_gr = new ManagerScript();
        try {
            Computers comp = ocsInventoryClientLogic.getComputers(host, login, password, checkSum);
            if( comp != null ){
				for (Computer computer : comp.getComputers()) {
					RequisitionNode reqNode = null;
					log().debug("import requisition nodes");
					RequisitionInterface reqIface = new RequisitionInterface();
					if (computer.getHardware() != null
							&& computer.getHardware().getIpsrc() != null) {
						reqIface.setIpAddr(computer.getHardware().getIpsrc());
					}
					reqIface.setManaged(true);
					reqIface.setSnmpPrimary(PrimaryType.get("P"));
					reqIface.setStatus(1);
					reqIface.putMonitoredService(new RequisitionMonitoredService("ICMP"));

					log().debug("set Interface");

					if (script != null) {
						Object obj = m_gr.executeScript(engine, script,
								foreignSource, computer);
						if (obj != null && obj instanceof RequisitionNode) {
							reqNode = (RequisitionNode) obj;
						}
					} else {
						
						log().warn(String.format("Script did not execute because content =%s", script));
					}
					if (reqNode == null) {
						reqNode = new RequisitionNode();

					}

					if (computer.getHardware()!=null && computer.getHardware().getName() != null
							&& reqNode.getNodeLabel() == null) {
						reqNode.setNodeLabel(computer.getHardware().getName());
					}
					if (reqNode.getForeignId() == null && computer.getHardware() != null) {
						reqNode.setForeignId(String.valueOf(computer
								.getHardware().getId()));
					}
					if (reqNode.getInterface().length == 0) {
						reqNode.putInterface(reqIface);
					}
					log().debug("map manufacturer");
					if (computer.getBios() != null
							&& computer.getBios().getSManufacturer() != null) {
						reqNode.putAsset(new RequisitionAsset("manufacturer",
								computer.getBios().getSManufacturer()));
					}

					log().debug("map modelNumber");
					if (computer.getBios().getSModel() != null) {
						reqNode.putAsset(new RequisitionAsset("modelNumber",
								computer.getBios().getSModel()));
					}

					log().debug("map serialNumber");
					reqNode.putAsset(new RequisitionAsset("serialNumber",
							String.valueOf(computer.getBios().getSSN())));

					log().debug("map operatingSystem");
					if (computer.getHardware()!=null && computer.getHardware().getOsname() != null
							&& computer.getHardware().getOsversion() != null) {
						reqNode.putAsset(new RequisitionAsset(
								"operatingSystem", computer.getHardware()
										.getOsname()
										+ " "
										+ computer.getHardware().getOsversion()));
					}
					log().debug("set processors");
					if(computer.getHardware() != null){
						StringBuilder infProcessors = new StringBuilder();
						infProcessors.append(String.valueOf(computer.getHardware().getProcessorn()));
						infProcessors.append("x ");
						infProcessors.append(String.valueOf(computer.getHardware().getProcessort()));
						infProcessors.append(" ");
						infProcessors.append(String.valueOf(computer.getHardware().getProcessors()));
						reqNode.putAsset(new RequisitionAsset("ram", String.valueOf(computer.getHardware()
								.getMemory())));
						reqNode.putAsset(new RequisitionAsset("cpu",infProcessors.toString()));

						String url = String.format("http://%s/ocsreports/index.php?function=computer&head=1", host);
						String info = "OCS Link";
						StringBuilder comment = new StringBuilder();
						comment.append(computer.getHardware().getUseragent());
						comment.append("- <a href=");
						comment.append('"' + url);
						comment.append("&systemid=");
						comment.append(computer.getHardware().getId() + '"');
						comment.append("target=\"_blank\">");
						comment.append(info);
						comment.append("</a>");
						reqNode.putAsset(new RequisitionAsset("comment",comment.toString()));

						if (computer.getHardware().getOscomments() != null) {
							reqNode.putAsset(new RequisitionAsset("description", computer.getHardware()
											.getOscomments()));
						}

						if (computer.getHardware().getUserId() != null) {
							reqNode.putAsset(new RequisitionAsset("username",computer.getHardware().getUserId()));
						}
					}
					int count = 0;
					log().debug("set storages");
					if (computer.getStorages() != null) {
						for (Storage storage : computer.getStorages()) {
							StringBuilder disk = new StringBuilder();
							disk.append(storage.getDisksize() / 1024);
							disk.append(" MB, ");
							if (storage.getName() != null) {
								disk.append(storage.getName());
							} else {
								disk.append(storage.getModel());
							}
							reqNode.putAsset(new RequisitionAsset("hdd"
									+ String.valueOf(count), disk.toString()));

							count++;
						}
					}
					req.putNode(reqNode);

					log().debug("saving requisition node");
				}
			}
        } catch (Exception e) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            log().error(String.format("Error cause: %s; error message: %s stack trace: %s", e.getCause(), e.getMessage(), writer.toString()));
        }

        log().warn("about to return (" + System.currentTimeMillis() + ")");
        return req;
    }


	/**
	 * Gets the content script from file.
	 *
	 * @param foreignSource
     * @param engine
	 * @return the content script from file
	 */
	private static String getContentScriptFromFile(String foreignSource, String engine){

		String content = null;
		if (foreignSource == null || foreignSource.isEmpty() || engine == null
				|| engine.isEmpty()) {
			return content;
		}
		StringBuilder builder = new StringBuilder();
        builder.append(ConfigFileConstants.getHome());
		builder.append(s_path_to_script_folder);
		builder.append(foreignSource);
		builder.append(".");
		builder.append(engine);

		String filePath = builder.toString();
		try {
			content = IOUtils.toString(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			log().error(String.format("File from path =%s not found", filePath));
		} catch (IOException e) {
			log().error(
					String.format(
							"Error while opening file from =%s, error message =%s",
							filePath, e.getMessage()));
		}
		return content;

	}

    /**
     * Sets ocs inventory client logic.
     *
     * @param ocsInventoryClientLogic the ocs inventory client logic
     */
    public static void setOcsInventoryClientLogic(OcsInventoryClientLogicExecuter ocsInventoryClientLogic) {
        OcsInventoryUtils.ocsInventoryClientLogic = ocsInventoryClientLogic;
    }

	/**
     * <p>log</p>
     *
     * @return a object.
     */
    protected static ThreadCategory log() {
        return ThreadCategory.getInstance(OcsInventoryUtils.class);
    }
}
