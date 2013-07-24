/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2012 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.provision.persist.requisition;

import org.junit.Before;
import org.junit.Test;
import org.opennms.core.test.MockLogAppender;
import org.opennms.netmgt.provision.persist.DefaultNodeProvisionService;
import org.opennms.netmgt.provision.persist.NodeProvisionService;
import org.opennms.netmgt.provision.persist.SurvCategoryConstants;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RequisitionImportTest {

	@Before
	public void setUp() {
		MockLogAppender.setupLogging();
	}

    @Test
    public void testRequisitionImport() {
        final List<RequisitionNode> nodes = new ArrayList<RequisitionNode>();

        final Requisition req = new Requisition();
        req.updateDateStamp();
        req.updateLastImported();

        req.setForeignSource("foreignSource1");
        NodeProvisionService service = new DefaultNodeProvisionService();
        List<String> criteria = new ArrayList<String>();
        criteria.add("mysql");
        Map<String, List<String>> namesCategCriteriaMap = new HashMap<String, List<String>>();
        namesCategCriteriaMap.put("DB_Server", criteria);
        List<String> typeCriteria = new ArrayList<String>();
        typeCriteria.add(SurvCategoryConstants.s_software);
        service.importProvisionNodes(req, "192.168.56.101", "dev", "dev", "foreignSource1",
                true, namesCategCriteriaMap, typeCriteria);
        assertFalse(req.getNodes().size()==0);
        for(RequisitionNode node: req.getNodes()) {
        System.out.println("Asset:");
        System.out.println(node.getAssets().toString());
        System.out.println("Categories:");
        System.out.println(node.getCategories().toString());
        }
        //assertFalse(req.getNodes().get(0).getCategories().isEmpty());
    }
}
