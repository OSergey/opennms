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

import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>NodeProvisionService interface.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public interface NodeProvisionService {

    /**
     * <p>getModelAndView</p>
     *
     * @param request a {@link javax.servlet.http.HttpServletRequest} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     */
    public ModelAndView getModelAndView(HttpServletRequest request) ;
    
    /**
     * <p>provisionNode</p>
     *
     * @param user a {@link java.lang.String} object.
     * @param foreignSource a {@link java.lang.String} object.
     * @param foreignId a {@link java.lang.String} object.
     * @param nodeLabel a {@link java.lang.String} object.
     * @param ipAddress a {@link java.lang.String} object.
     * @param categories an array of {@link java.lang.String} objects.
     * @param snmpCommunity a {@link java.lang.String} object.
     * @param snmpVersion a {@link java.lang.String} object.
     * @param deviceUsername a {@link java.lang.String} object.
     * @param devicePassword a {@link java.lang.String} object.
     * @param enablePassword a {@link java.lang.String} object.
     * @param accessMethd a {@link java.lang.String} object.
     * @param autoEnable a {@link java.lang.String} object.
     * @return a boolean.
     * @throws java.lang.Exception if any.
     */
    public boolean provisionNode(String user, String foreignSource, String foreignId, String nodeLabel, String ipAddress,
            String[] categories,
            String snmpCommunity, String snmpVersion,
            String deviceUsername, String devicePassword, String enablePassword,
            String accessMethd, String autoEnable, String noSNMP) throws Exception;

    /**
     * Import provision node.
     *
     * @param host the host
     * @param login the login
     * @param password the password
     * @return the boolean
     */
    public Requisition importProvisionNodes(Requisition req, String host, String login, String password, String foreignSource,
                                        boolean useIconLink,Map<String, List<String>> namesCategCriteriaMap, List<String> typeCateg);
}
