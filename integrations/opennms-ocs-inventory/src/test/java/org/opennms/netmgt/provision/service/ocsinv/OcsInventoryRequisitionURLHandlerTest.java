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

package org.opennms.netmgt.provision.service.ocsinv;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.opennms.core.test.MockLogAppender;
import org.opennms.core.utils.url.GenericURLFactory;

public class OcsInventoryRequisitionURLHandlerTest {

	@Before
	public void setUp() {
		MockLogAppender.setupLogging();
		GenericURLFactory.initialize();
	}

    @Test
    public void testRequisitionImport() throws IOException {
        URL ocsURL = new URL("ocsinv://dev:dev@192.168.56.101/foreignSource1");
    	//URL ocsURL = new URL("ocsinv://soapuser:ocsocsocs@192.168.34.28/foreignSource1");
    	InputStream ocsInput = ocsURL.openStream();
        StringWriter writer = new StringWriter();
        IOUtils.copy(ocsInput, writer, "UTF8");
    	System.out.println(writer.toString());
    	ocsInput.close();
    }
}
