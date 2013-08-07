package org.opennms.ocs.inventory.client;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.model.PrimaryType;
import org.opennms.netmgt.provision.persist.SurvCategoryConstants;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset;
import org.opennms.netmgt.provision.persist.requisition.RequisitionCategory;
import org.opennms.netmgt.provision.persist.requisition.RequisitionInterface;
import org.opennms.netmgt.provision.persist.requisition.RequisitionMonitoredService;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.opennms.ocs.inventory.client.request.logic.OcsInventoryClientLogic;
import org.opennms.ocs.inventory.client.request.logic.OcsInventoryClientLogicImp;
import org.opennms.ocs.inventory.client.response.Computer;
import org.opennms.ocs.inventory.client.response.Computers;
import org.opennms.ocs.inventory.client.response.Controller;
import org.opennms.ocs.inventory.client.response.Drive;
import org.opennms.ocs.inventory.client.response.Software;
import org.opennms.ocs.inventory.client.response.Sound;
import org.opennms.ocs.inventory.client.response.Storage;
import org.opennms.ocs.inventory.client.response.Video;
import org.springframework.transaction.annotation.Transactional;

public class OcsInventoryUtils {



    /**
     * {@inheritDoc}
     */
    @Transactional
    public static Requisition importProvisionNodes(Requisition req, String host, String login, String password, String foreignSource,
                                        String bodyClass) {
        log().info(String.format(" Import nodes from OCS Inventory host =%s, login =%s", host, login));
        //java.lang.System.setProperty("javax.xml.soap.MessageFactory", "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl");
        OcsInventoryClientLogic ocsInventoryClientLogic = new OcsInventoryClientLogicImp();
        req = new Requisition(foreignSource);
        GroovyMappingLogic groovyMappingLogic = new GroovyMappingLogic();
        RequisitionNode reqNode = null;
        try {
            ocsInventoryClientLogic.init(host, login, password);
            Computers comp = ocsInventoryClientLogic.getComputers();
            for (Computer computer : comp.getComputers()) {
                log().debug("import requisition nodes");
                RequisitionInterface reqIface = new RequisitionInterface();
                if (computer.getHardware() != null && computer.getHardware().getIpsrc() != null) {
                    reqIface.setIpAddr(computer.getHardware().getIpsrc());
                }
                reqIface.setManaged(true);
                reqIface.setSnmpPrimary(PrimaryType.get("P"));
                reqIface.setStatus(1);
                reqIface.putMonitoredService(new RequisitionMonitoredService("ICMP"));


                log().debug("set Interface");

                reqNode = groovyMappingLogic.createRequisitionNodeFromGroovyScript(computer, bodyClass);
                if( reqNode == null){
                    reqNode = new RequisitionNode();

                }

                if (computer.getHardware().getName() != null && reqNode.getNodeLabel() == null) {
                    reqNode.setNodeLabel(computer.getHardware().getName());
                }
                reqNode.setForeignId(String.valueOf(computer.getHardware().getId()));
                reqNode.putInterface(reqIface);
                log().debug("map manufacturer");
                if (computer.getBios() != null && computer.getBios().getSManufacturer() != null) {
                    reqNode.putAsset(new RequisitionAsset("manufacturer", computer.getBios().getSManufacturer()));
                }

                log().debug("map modelNumber");
                if (computer.getBios().getSModel() != null) {
                    reqNode.putAsset(new RequisitionAsset("modelNumber", computer.getBios().getSModel()));
                }

                log().debug("map serialNumber");
                reqNode.putAsset(new RequisitionAsset("serialNumber", String.valueOf(computer.getBios().getSSN())));

                log().debug("map operatingSystem");
                if (computer.getHardware().getOsname() != null && computer.getHardware().getOsversion() != null) {
                    reqNode.putAsset(new RequisitionAsset("operatingSystem", computer.getHardware().getOsname() + " " +
                            computer.getHardware().getOsversion()));
                }
                log().debug("set processors");
                StringBuilder infProcessors = new StringBuilder();
                infProcessors.append(String.valueOf(computer.getHardware().getProcessorn()));
                infProcessors.append("x ");
                infProcessors.append(String.valueOf(computer.getHardware().getProcessort()));
                infProcessors.append(" ");
                infProcessors.append(String.valueOf(computer.getHardware().getProcessors()));
                reqNode.putAsset(new RequisitionAsset("ram", String.valueOf(computer.getHardware().getMemory())));
                reqNode.putAsset(new RequisitionAsset("cpu", infProcessors.toString()));

                String url = "http://" + host + "/ocsreports/index.php?function=computer&head=1";
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
                reqNode.putAsset(new RequisitionAsset("comment", comment.toString()));

                if (computer.getHardware().getOscomments() != null) {
                    reqNode.putAsset(new RequisitionAsset("description", computer.getHardware().getOscomments()));
                }

                if (computer.getHardware().getUserId() != null) {
                    reqNode.putAsset(new RequisitionAsset("username", computer.getHardware().getUserId()));
                }
                int count = 0;
                log().debug("set storages");
                for (Storage storage : computer.getStorages()) {
                    StringBuilder disk = new StringBuilder();
                    disk.append(storage.getDisksize() / 1024);
                    disk.append(" MB, ");
                    if (storage.getName() != null) {
                        disk.append(storage.getName());
                    } else {
                        disk.append(storage.getModel());
                    }
                    reqNode.putAsset(new RequisitionAsset("hdd" + String.valueOf(count), disk.toString()));

                    count++;
                }
                //mapping category
                //addSurvCategories(computer, namesCategCriteriaMap, typeCateg, reqNode);

                req.putNode(reqNode);


                log().debug("saving requisition node");
            }
        } catch (Exception e) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            log().error(String.format("Error cause: %s; error message: %s stack trace: %s", e.getCause(), e.getMessage(), writer.toString()));
        }
/*        try {
            log().debug("saving requisition node");
            //m_foreignSourceRepository.save(req);
        } catch (ForeignSourceRepositoryException e) {
            throw new RuntimeException("unable to retrieve foreign source '" + foreignSource + "'", e);
        }
        Event e = new EventBuilder(EventConstants.RELOAD_IMPORT_UEI, "NodeProvisionService")
                .addParam("url", m_foreignSourceRepository.getRequisitionURL(foreignSource).toString())
                .getEvent();
        m_eventForwarder.sendNow(e);*/
        log().warn("about to return (" + System.currentTimeMillis() + ")");
        return req;
    }
    
    /**
     * This method contains mapping categories
     */
    private static void addSurvCategories(Computer computer, Map<String, List<String>> namesCategCriteriaMap,
                                   List<String> typeCateg, RequisitionNode reqNode) {

        for (String category : namesCategCriteriaMap.keySet()) {
            for (String criteria : namesCategCriteriaMap.get(category)) {
                boolean categoryAdded = false;
                Pattern pattern =
                        Pattern.compile(criteria, Pattern.CASE_INSENSITIVE);
                if (typeCateg.isEmpty()) {
                    if(mapBiosCategory(reqNode, pattern, computer, categoryAdded, category)){
                        break;
                    }
                    if(mapControllerCategory(reqNode, pattern, computer, categoryAdded, category)){
                        break;
                    }
                    if(mapDriveCategory(reqNode, pattern, computer, categoryAdded, category)){
                        break;
                    }
                    if(mapHardwareCategory(reqNode, pattern, computer, categoryAdded, category)){
                        break;
                    }
                    if(mapNetworkCategory(reqNode, pattern, computer, categoryAdded, category)){
                        break;
                    }
                    if(mapSoundCategory(reqNode, pattern, computer, categoryAdded, category)){
                        break;
                    }
                    if(mapStorageCategory(reqNode, pattern, computer, categoryAdded, category)){
                        break;
                    }
                    if(mapVideoCategory(reqNode, pattern, computer, categoryAdded, category)){
                        break;
                    }
                } else {
                    for (String type : typeCateg) {
                        if (type.equals(SurvCategoryConstants.s_bios)) {
                            categoryAdded = mapBiosCategory(reqNode, pattern, computer, categoryAdded, category);
                            break;
                        }
                        if (type.equals(SurvCategoryConstants.s_controller) && !categoryAdded) {
                            categoryAdded = mapControllerCategory(reqNode, pattern, computer, categoryAdded, category);
                            break;

                        }
                        if (type.equals(SurvCategoryConstants.s_drive) && !categoryAdded) {
                            categoryAdded = mapDriveCategory(reqNode, pattern, computer, categoryAdded, category);
                            break;

                        }
                        if (type.equals(SurvCategoryConstants.s_hardware) && !categoryAdded) {
                            categoryAdded = mapHardwareCategory(reqNode, pattern, computer, categoryAdded, category);
                            break;

                        }
                        if (type.equals(SurvCategoryConstants.s_network) && !categoryAdded) {
                            categoryAdded = mapNetworkCategory(reqNode, pattern, computer, categoryAdded, category);
                            break;

                        }
                        if (type.equals(SurvCategoryConstants.s_software) && !categoryAdded) {
                            categoryAdded = mapSoftwareCategory(reqNode, pattern, computer, categoryAdded, category);
                            break;

                        }
                        if (type.equals(SurvCategoryConstants.s_sound) && !categoryAdded) {
                            categoryAdded = mapSoundCategory(reqNode, pattern, computer, categoryAdded, category);
                            break;

                        }
                        if (type.equals(SurvCategoryConstants.s_storage) && !categoryAdded) {
                            categoryAdded = mapStorageCategory(reqNode, pattern, computer, categoryAdded, category);
                            break;

                        }
                        if (type.equals(SurvCategoryConstants.s_video) && !categoryAdded) {
                            categoryAdded = mapVideoCategory(reqNode, pattern, computer, categoryAdded, category);
                            break;

                        }

                    }
                }
                if (categoryAdded) {
                    break;
                }
            }
        }


    }
    
	/**
	 * This method contains mapping type bios category
	 */
	private static boolean mapBiosCategory(RequisitionNode reqNode, Pattern pattern,
	                             Computer computer, boolean categoryAdded, String category){
	    if (pattern.matcher(computer.getBios().getBVersion()).find() && !categoryAdded) {
	        reqNode.putCategory(new RequisitionCategory(category));
	        categoryAdded = true;
	        return categoryAdded;
	    }
	    if (pattern.matcher(computer.getBios().getBManufacturer()).find() && !categoryAdded) {
	        reqNode.putCategory(new RequisitionCategory(category));
	        categoryAdded = true;
	        return categoryAdded;
	    }
	    if (pattern.matcher(computer.getBios().getSModel()).find() && !categoryAdded) {
	        reqNode.putCategory(new RequisitionCategory(category));
	        categoryAdded = true;
	        return categoryAdded;
	    }
	    return false;
	}

	/**
	 * This method contains mapping type controller category
	 */
	private static boolean mapControllerCategory(RequisitionNode reqNode, Pattern pattern,
	                                Computer computer, boolean categoryAdded, String category){
	    for (Controller controller : computer.getControllers()) {
	        if (pattern.matcher(controller.getManufacturer()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	        if (pattern.matcher(controller.getName()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	    }
	    return false;
	}

	/**
	 * This method contains mapping type drive category
	 */
	private static boolean mapDriveCategory(RequisitionNode reqNode, Pattern pattern,
	                                      Computer computer, boolean categoryAdded, String category){
	    for (Drive drive : computer.getDrives()) {
	        if (pattern.matcher(drive.getFilesystem()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	        if (pattern.matcher(drive.getVolumn()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	    }
	    return false;
	}

	/**
	 * This method contains mapping type hardware category
	 */
	private static boolean mapHardwareCategory(RequisitionNode reqNode, Pattern pattern,
	                                Computer computer, boolean categoryAdded, String category){
	    if (pattern.matcher(computer.getHardware().getIpaddr()).find() && !categoryAdded) {
	        reqNode.putCategory(new RequisitionCategory(category));
	        categoryAdded = true;
	        return categoryAdded;
	    }
	    if (pattern.matcher(computer.getHardware().getName()).find() && !categoryAdded) {
	        reqNode.putCategory(new RequisitionCategory(category));
	        categoryAdded = true;
	        return categoryAdded;
	    }
	    if (pattern.matcher(computer.getHardware().getOsname()).find() && !categoryAdded) {
	        reqNode.putCategory(new RequisitionCategory(category));
	        categoryAdded = true;
	        return categoryAdded;
	    }
	    if (pattern.matcher(computer.getHardware().getOsversion()).find() && !categoryAdded) {
	        reqNode.putCategory(new RequisitionCategory(category));
	        categoryAdded = true;
	        return categoryAdded;
	    }
	    if (pattern.matcher(computer.getHardware().getProcessort()).find() && !categoryAdded) {
	        reqNode.putCategory(new RequisitionCategory(category));
	        categoryAdded = true;
	        return categoryAdded;
	    }
	    if (pattern.matcher(computer.getHardware().getWorkGroup()).find() && !categoryAdded) {
	        reqNode.putCategory(new RequisitionCategory(category));
	        categoryAdded = true;
	        return categoryAdded;
	    }
	    return false;
	}

	/**
	 * This method contains mapping type network category
	 */
	private static boolean mapNetworkCategory(RequisitionNode reqNode, Pattern pattern,
	                                 Computer computer, boolean categoryAdded, String category){
	        if (pattern.matcher(computer.getNetworks().getDescription()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	        if (pattern.matcher(computer.getNetworks().getIPSubnet()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	
	    return false;
	}

	/**
	 * This method contains mapping type software category
	 */
	private static boolean mapSoftwareCategory(RequisitionNode reqNode, Pattern pattern,
	                                 Computer computer, boolean categoryAdded, String category){
	    for (Software software : computer.getSoftwares()) {
	        if (pattern.matcher(software.getName()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	        if (pattern.matcher(software.getVersion()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	    }
	    return false;
	}

	/**
	 * This method contains mapping type sound category
	 */
	
	private static boolean mapSoundCategory(RequisitionNode reqNode, Pattern pattern,
	                                    Computer computer, boolean categoryAdded, String category){
	    for (Sound sound : computer.getSounds()) {
	        if (pattern.matcher(sound.getManufacturer()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	    }
	
	    return false;
	}

	/**
	 * This method contains mapping type storage category
	 */
	
	private static boolean mapStorageCategory(RequisitionNode reqNode, Pattern pattern,
	                                 Computer computer, boolean categoryAdded, String category){
	    for (Storage storage : computer.getStorages()) {
	        if (pattern.matcher(storage.getDescription()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	
	        if (pattern.matcher(String.valueOf(storage.getDisksize() / 1024)).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	
	        if (pattern.matcher(storage.getManufacturer()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	
	        if (pattern.matcher(storage.getModel()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	    }
	
	    return false;
	}

	/**
	 * This method contains mapping type video category
	 */
	
	private static boolean mapVideoCategory(RequisitionNode reqNode, Pattern pattern,
	                                   Computer computer, boolean categoryAdded, String category){
	    for (Video video : computer.getVideos()) {
	        if (pattern.matcher(video.getChipset()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	
	        if (pattern.matcher(video.getMemory()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	
	        if (pattern.matcher(video.getName()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	
	        if (pattern.matcher(video.getResolution()).find() && !categoryAdded) {
	            reqNode.putCategory(new RequisitionCategory(category));
	            categoryAdded = true;
	            return categoryAdded;
	        }
	    }
	
	    return false;
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
