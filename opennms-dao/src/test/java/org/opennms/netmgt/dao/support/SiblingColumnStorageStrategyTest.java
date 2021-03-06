/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2012 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.dao.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.config.StorageStrategyService;
import org.opennms.netmgt.config.datacollection.Parameter;
import org.opennms.netmgt.snmp.SnmpAgentConfig;

/**
 * @author <a href="mailto:jeffg@opennms.org">Jeff Gehlbach</a>
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class SiblingColumnStorageStrategyTest {

    private StorageStrategyService service;
    private SiblingColumnStorageStrategy strategy;

    @Before
    public void setUp() throws Exception {
        // Create Mocks
        service = EasyMock.createMock(StorageStrategyService.class);
        SnmpAgentConfig agentConfig = new SnmpAgentConfig(InetAddressUtils.addr("127.0.0.1"));
        agentConfig.setPort(1161);
        EasyMock.expect(service.getAgentConfig()).andReturn(agentConfig).anyTimes();
        EasyMock.replay(service);

        // Create Strategy and set for hrStorageTable
        strategy = new SiblingColumnStorageStrategy();
        strategy.setStorageStrategyService(service);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(service);
    }

    @Test
    public void testStrategy() throws Exception {
        strategy.setResourceTypeName("hrStorageIndex");

        // Create parameters for the strategy -- hrStorageTable
        List<Parameter> params = new ArrayList<Parameter>();
        params.add(createParameter("sibling-column-name", "hrStorageDescr"));
        params.add(createParameter("replace-first", "s/^-$/_root_fs/"));
        params.add(createParameter("replace-first", "s/^-//"));
        params.add(createParameter("replace-all", "s/\\s//"));
        params.add(createParameter("replace-all", "s/:\\\\.*//"));

        // Set the list of parameters into the strategy -- hrStorageTable
        strategy.setParameters(params);

        // Test Resource Name - root file system (hrStorageTable)
        String parentResource = "1";
        MockCollectionResource resource = new MockCollectionResource(parentResource, "1", "hrStorageIndex");
        resource.getAttribtueMap().put("hrStorageDescr", "/");
        String resourceName = strategy.getResourceNameFromIndex(resource);
        Assert.assertEquals("_root_fs", resourceName);

        // Test Resource Name - /Volumes/iDisk file system (hrStorageTable)
        resource.setInstance("8");
        resource.getAttribtueMap().put("hrStorageDescr", "Volumes-iDisk");
        Assert.assertEquals("Volumes-iDisk", strategy.getResourceNameFromIndex(resource));

        // Test RelativePath - hrStorageTable
        Assert.assertEquals("1" + File.separator + "hrStorageIndex" + File.separator + "_root_fs", strategy.getRelativePathForAttribute(parentResource, resourceName, null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadParameters() throws Exception {
        strategy.setResourceTypeName("hrStorageIndex");

        // Create parameters for the strategy -- hrStorageTable
        List<Parameter> params = new ArrayList<Parameter>();
        params.add(createParameter("sibling-column-oid", ".1.3.6.1.2.1.25.2.3.1.3"));
        params.add(createParameter("replace-first", "s/^-$/_root_fs/"));
        params.add(createParameter("replace-first", "s/^-//"));
        params.add(createParameter("replace-all", "s/\\s//"));
        params.add(createParameter("replace-all", "s/:\\\\.*//"));

        // Set the list of parameters into the strategy -- hrStorageTable
        strategy.setParameters(params);
    }

    @Test
    public void testMatchIndex() throws Exception {
        strategy.setResourceTypeName("macIndex");

        List<Parameter> params = new ArrayList<Parameter>();
        params.add(createParameter("sibling-column-name", "_index"));
        params.add(createParameter("replace-first", "s/^(([\\d]{1,3}\\.){8,8}).*$/$1/"));
        params.add(createParameter("replace-first", "s/\\.$//"));

        strategy.setParameters(params);

        String parentResource = "1";
        MockCollectionResource resource = new MockCollectionResource(parentResource, "0.132.43.51.76.89.2.144.10.1.1.1", "macIndex");
        String resourceName = strategy.getResourceNameFromIndex(resource);
        Assert.assertEquals("0.132.43.51.76.89.2.144", resourceName);
    }

    private Parameter createParameter(String key, String value) {
        Parameter p = new Parameter();
        p.setKey(key);
        p.setValue(value);
        return p;
    }
}
