package org.opennms.ocs.inventory.client.request.logic.test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.xml.soap.SOAPException;

import org.junit.Before;
import org.junit.Test;
import org.opennms.core.test.MockLogAppender;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.ocs.inventory.client.OcsInventoryUtils;
import org.opennms.ocs.inventory.client.request.logic.OcsInventoryClientLogic;
import org.opennms.ocs.inventory.client.response.Bios;
import org.opennms.ocs.inventory.client.response.Computer;
import org.opennms.ocs.inventory.client.response.Computers;

public class ClientLogicMockTest {

    private OcsInventoryClientLogic ocsInventoryClientLogic;

    @Before
    public void setUp() throws Exception {
        MockLogAppender.setupLogging();
        ocsInventoryClientLogic = mock(OcsInventoryClientLogic.class);
    }

    @Test
    public void test() throws SOAPException, Exception {
        Computer computer = new Computer();
        Bios bs = new Bios();
        bs.setSModel("Model");
        bs.setSManufacturer("Manufacture");
        computer.setBios(bs);
        Computers cmps = new Computers();
        cmps.getComputers().add(computer);
    	when(ocsInventoryClientLogic.getComputers()).thenReturn(cmps);
        OcsInventoryUtils.setOcsInventoryClientLogic(ocsInventoryClientLogic);
    	Requisition req = null;
    	req = OcsInventoryUtils.importProvisionNodes(req, "192.168.56.101", "dev", "dev", "foreignSource1", "groovy");
    	System.out.println(req);
    }

}
