/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.provision.persist;

import org.opennms.core.utils.BeanUtils;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.config.SnmpEventInfo;
import org.opennms.netmgt.config.SnmpPeerFactory;
import org.opennms.netmgt.dao.CategoryDao;
import org.opennms.netmgt.dao.TransactionAwareEventForwarder;
import org.opennms.netmgt.model.PrimaryType;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.model.events.EventForwarder;
import org.opennms.netmgt.model.events.EventProxy;
import org.opennms.netmgt.model.events.EventProxyException;
import org.opennms.netmgt.provision.persist.requisition.*;
import org.opennms.netmgt.xml.event.Event;
import org.opennms.netmgt.xml.event.Log;
import org.opennms.ocs.inventory.client.request.logic.OcsInventoryClientLogic;
import org.opennms.ocs.inventory.client.request.logic.OcsInventoryClientLogicImp;
import org.opennms.ocs.inventory.client.response.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>DefaultNodeProvisionService class.</p>
 *
 * @author ranger
 * @version $Id : $
 */
public class DefaultNodeProvisionService implements NodeProvisionService, InitializingBean {

    private EventForwarder m_eventForwarder;
    
    @Autowired
    private CategoryDao m_categoryDao;
    
    @Autowired
    private SnmpPeerFactory m_snmpPeerFactory;

    private ForeignSourceRepository m_foreignSourceRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        BeanUtils.assertAutowiring(this);
    }

    /** {@inheritDoc} */
    @Override
    public ModelAndView getModelAndView(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("foreignSources", m_foreignSourceRepository.getForeignSources());
        modelAndView.addObject("requisitions", m_foreignSourceRepository.getRequisitions());
        modelAndView.addObject("categories", m_categoryDao.getAllCategoryNames());
        modelAndView.addObject("success", Boolean.parseBoolean(request.getParameter("success")));
        modelAndView.addObject("foreignSource", request.getParameter("foreignSource"));
        return modelAndView;
    }
    
    /** {@inheritDoc} */
    @Transactional
    public boolean provisionNode(final String user, String foreignSource, String foreignId, String nodeLabel, String ipAddress,
            String[] categories, String snmpCommunity, String snmpVersion,
            String deviceUsername, String devicePassword, String enablePassword,
            String accessMethod, String autoEnable, String noSNMP) throws NodeProvisionException {


        if (log().isDebugEnabled()) {
            log().debug(String.format("adding SNMP community %s (%s)", snmpCommunity, snmpVersion));
        }
        // Set the SNMP community name (if necessary)
        if (noSNMP == null &&  snmpCommunity != null && !snmpCommunity.equals("") && snmpVersion != null && !snmpVersion.equals("")) {
            try {
                SnmpEventInfo info = new SnmpEventInfo();
                info.setCommunityString(snmpCommunity);
                info.setFirstIPAddress(ipAddress);
                info.setVersion(snmpVersion);
                m_snmpPeerFactory.define(info);
                SnmpPeerFactory.saveCurrent();
            } catch (Throwable e) {
                throw new NodeProvisionException("unable to add SNMP community information", e);
            }
        }

        log().debug("creating requisition node");
        // Create a requisition node based on the form input
        RequisitionInterface reqIface = new RequisitionInterface();
        reqIface.setIpAddr(ipAddress);
        reqIface.setManaged(true);
        reqIface.setSnmpPrimary(PrimaryType.get("P"));
        reqIface.setStatus(1);

        reqIface.putMonitoredService(new RequisitionMonitoredService("ICMP"));
        if(noSNMP == null) {
            reqIface.putMonitoredService(new RequisitionMonitoredService("SNMP"));
        }
        
        RequisitionNode reqNode = new RequisitionNode();
        reqNode.setNodeLabel(nodeLabel);
        reqNode.setForeignId(foreignId);
        reqNode.putInterface(reqIface);

        for (String category : categories) {
            if (category != null && !category.equals("")) {
                reqNode.putCategory(new RequisitionCategory(category));
            }
        }

        if (deviceUsername != null && !deviceUsername.equals("")) {
            reqNode.putAsset(new RequisitionAsset("username", deviceUsername));
        }
        if (devicePassword != null && !devicePassword.equals("")) {
            reqNode.putAsset(new RequisitionAsset("password", devicePassword));
        }
        if (enablePassword != null && !enablePassword.equals("")) {
            reqNode.putAsset(new RequisitionAsset("enable", enablePassword));
        }
        if (accessMethod != null && !accessMethod.equals("")) {
            reqNode.putAsset(new RequisitionAsset("connection", accessMethod));
        }
        if (autoEnable != null) {
            reqNode.putAsset(new RequisitionAsset("autoenable", "A"));
        }

        // Now save it to the requisition
        try {
            Requisition req = m_foreignSourceRepository.getRequisition(foreignSource);
            req.putNode(reqNode);
            log().debug("saving requisition node");
            m_foreignSourceRepository.save(req);
        } catch (ForeignSourceRepositoryException e) {
            throw new RuntimeException("unable to retrieve foreign source '" + foreignSource + "'", e);
        }

        Event e = new EventBuilder(EventConstants.RELOAD_IMPORT_UEI, "NodeProvisionService")
            .addParam("url", m_foreignSourceRepository.getRequisitionURL(foreignSource).toString())
            .getEvent();
        m_eventForwarder.sendNow(e);

        log().warn("about to return (" + System.currentTimeMillis() + ")");
        return true;
    }

    /**
     * This method contains mapping categories
     */
    private void addSurvCategories(Computer computer, Map<String, List<String>> namesCategCriteriaMap,
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
    private boolean mapBiosCategory(RequisitionNode reqNode, Pattern pattern,
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
    private boolean mapControllerCategory(RequisitionNode reqNode, Pattern pattern,
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
    private boolean mapDriveCategory(RequisitionNode reqNode, Pattern pattern,
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
    private boolean mapHardwareCategory(RequisitionNode reqNode, Pattern pattern,
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
    private boolean mapNetworkCategory(RequisitionNode reqNode, Pattern pattern,
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
    private boolean mapSoftwareCategory(RequisitionNode reqNode, Pattern pattern,
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

    private boolean mapSoundCategory(RequisitionNode reqNode, Pattern pattern,
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

    private boolean mapStorageCategory(RequisitionNode reqNode, Pattern pattern,
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

    private boolean mapVideoCategory(RequisitionNode reqNode, Pattern pattern,
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
     * {@inheritDoc}
     */
    @Transactional
    public Requisition importProvisionNodes(Requisition req, String host, String login, String password, String foreignSource,
                                        boolean useIconLink, Map<String, List<String>> namesCategCriteriaMap,
                                        List<String> typeCateg) {
        log().info(String.format(" Import nodes from OCS Inventory host =%s, login =%s", host, login));
        //java.lang.System.setProperty("javax.xml.soap.MessageFactory", "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl");
        OcsInventoryClientLogic ocsInventoryClientLogic = new OcsInventoryClientLogicImp();
        //Requisition req = m_foreignSourceRepository.getRequisition(foreignSource);
        try {
            ocsInventoryClientLogic.init(host, login, password);
            Computers comp = ocsInventoryClientLogic.getComputers();
            for (Computer cmp : comp.getComputers()) {
                log().debug("import requisition nodes");
                RequisitionInterface reqIface = new RequisitionInterface();
                if (cmp.getHardware() != null && cmp.getHardware().getIpsrc() != null) {
                    reqIface.setIpAddr(cmp.getHardware().getIpsrc());
                }
                reqIface.setManaged(true);
                reqIface.setSnmpPrimary(PrimaryType.get("P"));
                reqIface.setStatus(1);
                reqIface.putMonitoredService(new RequisitionMonitoredService("ICMP"));


                log().debug("set Interface");
                RequisitionNode reqNode = new RequisitionNode();
                if (cmp.getHardware().getName() != null) {
                    reqNode.setNodeLabel(cmp.getHardware().getName());
                }
                reqNode.setForeignId(String.valueOf(cmp.getHardware().getId()));
                reqNode.putInterface(reqIface);
                log().debug("map manufacturer");
                if (cmp.getBios() != null && cmp.getBios().getSManufacturer() != null) {
                    reqNode.putAsset(new RequisitionAsset("manufacturer", cmp.getBios().getSManufacturer()));
                }

                log().debug("map modelNumber");
                if (cmp.getBios().getSModel() != null) {
                    reqNode.putAsset(new RequisitionAsset("modelNumber", cmp.getBios().getSModel()));
                }

                log().debug("map serialNumber");
                reqNode.putAsset(new RequisitionAsset("serialNumber", String.valueOf(cmp.getBios().getSSN())));

                log().debug("map operatingSystem");
                if (cmp.getHardware().getOsname() != null && cmp.getHardware().getOsversion() != null) {
                    reqNode.putAsset(new RequisitionAsset("operatingSystem", cmp.getHardware().getOsname() + " " +
                            cmp.getHardware().getOsversion()));
                }
                log().debug("set processors");
                StringBuilder infProcessors = new StringBuilder();
                infProcessors.append(String.valueOf(cmp.getHardware().getProcessorn()));
                infProcessors.append("x ");
                infProcessors.append(String.valueOf(cmp.getHardware().getProcessort()));
                infProcessors.append(" ");
                infProcessors.append(String.valueOf(cmp.getHardware().getProcessors()));
                reqNode.putAsset(new RequisitionAsset("ram", String.valueOf(cmp.getHardware().getMemory())));
                reqNode.putAsset(new RequisitionAsset("cpu", infProcessors.toString()));

                String url = "http://" + host + "/ocsreports/index.php?function=computer&head=1";
                String info = useIconLink ? "<img src=\"/opennms/images/ocsinventory.png\"/>" : "OCS Link";
                StringBuilder comment = new StringBuilder();
                comment.append(cmp.getHardware().getUseragent());
                comment.append("- <a href=");
                comment.append('"' + url);
                comment.append("&systemid=");
                comment.append(cmp.getHardware().getId() + '"');
                comment.append("target=\"_blank\">");
                comment.append(info);
                comment.append("</a>");
                reqNode.putAsset(new RequisitionAsset("comment", comment.toString()));

                if (cmp.getHardware().getOscomments() != null) {
                    reqNode.putAsset(new RequisitionAsset("description", cmp.getHardware().getOscomments()));
                }

                if (cmp.getHardware().getUserId() != null) {
                    reqNode.putAsset(new RequisitionAsset("username", cmp.getHardware().getUserId()));
                }
                int count = 0;
                log().debug("set storages");
                for (Storage storage : cmp.getStorages()) {
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
                addSurvCategories(cmp, namesCategCriteriaMap, typeCateg, reqNode);

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
     * <p>setForeignSourceRepository</p>
     *
     * @param repository a
     * object.
     */
    public void setForeignSourceRepository(ForeignSourceRepository repository) {
        m_foreignSourceRepository = repository;
    }

    /**
     * <p>setEventProxy</p>
     *
     * @param proxy a
     * object.
     * @throws Exception if any.
     */
    public void setEventProxy(final EventProxy proxy) throws Exception {
        EventForwarder proxyForwarder = new EventForwarder() {
            @Override
            public void sendNow(Event event) {
                try {
                    proxy.send(event);
                } catch (EventProxyException e) {
                    throw new NodeProvisionException("Unable to send "+event, e);
                }
            }

            @Override
            public void sendNow(Log eventLog) {
                try {
                    proxy.send(eventLog);
                } catch (EventProxyException e) {
                    throw new NodeProvisionException("Unable to send eventLog "+eventLog, e);
                }
            }
            
        };
        m_eventForwarder = new TransactionAwareEventForwarder(proxyForwarder);
    }

    /**
     * <p>log</p>
     *
     * @return a object.
     */
    protected ThreadCategory log() {
        return ThreadCategory.getInstance(getClass());
    }
}
