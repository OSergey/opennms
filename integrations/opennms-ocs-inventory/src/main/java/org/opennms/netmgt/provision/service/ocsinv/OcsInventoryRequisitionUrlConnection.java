/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2013 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2013 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.provision.service.ocsinv;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOExceptionWithCause;
import org.opennms.core.utils.url.GenericURLConnection;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.ocs.inventory.client.OcsInventoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class VmwareRequisitionUrlConnection
 * <p/>
 * This class is used for the automatic requisition of nodes from an
 * instance of OCS Inventory-NG.
 *
 * @author Jeff Gehlbach <jeffg@opennms.org>
 * @author Sergey Ovsyuk <sergey.ovsyuk@gmail.com>
 */
public class OcsInventoryRequisitionUrlConnection extends GenericURLConnection {
    /**
     * the logger
     */
    private Logger logger = LoggerFactory.getLogger(OcsInventoryRequisitionUrlConnection.class);

    private String m_hostname = null;
    private String m_username = null;
    private String m_password = null;
    private String m_foreignSource = null;
    private static final String s_engine = "engine";

    private double m_minQuality = 0;

    /**
     * the query args
     */
    private Map<String, String> m_args = null;

    /**
     * requisition object
     */
    private Requisition m_requisition = null;

    /**
     * Constructor for creating an instance of this class.
     *
     * @param url the URL to use
     * @throws MalformedURLException
     * @throws RemoteException
     */
    public OcsInventoryRequisitionUrlConnection(URL url) throws MalformedURLException, RemoteException {
        super(url);

        m_hostname = url.getHost();

        m_username = getUsername();
        m_password = getPassword();

        m_args = getQueryArgs();

        m_minQuality = queryParameter("minQuality", 0);

        String path = url.getPath();

        path = path.replaceAll("^/", "");
        path = path.replaceAll("/$", "");

        String pathElements[] = path.split("/");

        if (pathElements.length == 1) {
            if ("".equals(pathElements[0])) {
                m_foreignSource = "ocsinv-" + m_hostname;
            } else {
                m_foreignSource = pathElements[0];
            }
        } else {
            throw new MalformedURLException("Error processing path element of URL (ocsinv://username:password@host[/foreign-source]?keyA=valueA;keyB=valueB;...)");
        }
    }

    /**
     * Returns a boolean representation for a given on/off parameter.
     *
     * @param key          the parameter's name
     * @param defaultValue the default value to use
     * @return the boolean value
     */
    private boolean queryParameter(String key, boolean defaultValue) {
        if (m_args.get(key) == null) {
            return defaultValue;
        } else {
            String value = m_args.get(key).toLowerCase();

            return ("yes".equals(value) || "true".equals(value) || "on".equals(value) || "1".equals(value));
        }
    }
    
    private double queryParameter(String key, double defaultValue) {
        double value;
    	if (m_args.get(key) == null) {
            return defaultValue;
        } else {
        	try {
        		value = Double.valueOf(m_args.get(key));
        	} catch (NumberFormatException nfe) {
        		value = defaultValue;
        	}

            return value;
        }
    }

    @Override
    public void connect() throws IOException {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    /**
     * Builds the complete requisition object.
     *
     * @return the requisition object
     */
    private Requisition buildOCSRequisition() {
    	String engine = null;
    	if(m_args.containsKey(s_engine)){
    		engine = m_args.get(s_engine);
    	}
    	m_requisition = OcsInventoryUtils.importProvisionNodes(m_requisition, m_hostname, m_username, m_password, m_foreignSource, engine);

        return m_requisition;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Creates a ByteArrayInputStream implementation of InputStream of the XML
     * marshaled version of the Requisition class. Calling close on this stream
     * is safe.
     */
    @Override
    public InputStream getInputStream() throws IOException {

        InputStream stream = null;

        try {
            Requisition r = buildOCSRequisition();
            stream = new ByteArrayInputStream(jaxBMarshal(r).getBytes());
        } catch (Throwable e) {
            logger.warn("Problem getting input stream: '{}'", e);
            throw new IOExceptionWithCause("Problem getting input stream: " + e, e);
        }

        return stream;
    }

    /**
     * Utility to marshal the Requisition class into XML.
     *
     * @param r the requisition object
     * @return a String of XML encoding the Requisition class
     * @throws javax.xml.bind.JAXBException
     */
    private String jaxBMarshal(Requisition r) throws JAXBException {
        return JaxbUtils.marshal(r);
    }
}
