package org.opennms;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.opennms.ocs.inventory.client.ManagerScript;
import org.opennms.ocs.inventory.client.response.Bios;
import org.opennms.ocs.inventory.client.response.Computer;

/**
 * @author <A HREF="mailto:sergey.ovsyuk@gmail.com">Sergey Ovsyuk </A>
 */
public class GroovyTest {
    @Test
    public void test() throws IllegalAccessException, IOException, InstantiationException {
    	ManagerScript m_gr = new ManagerScript();
        Computer computer = new Computer();
        Bios bs = new Bios();
        bs.setSModel("Model");
        bs.setSManufacturer("Manufacture");
        computer.setBios(bs);
        StringBuilder builder = new StringBuilder();
        builder.append("import org.opennms.netmgt.provision.persist.requisition.RequisitionNode \n");
        builder.append("import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset \n");
        builder.append("RequisitionNode reqNode = new RequisitionNode() \n");
        builder.append("if (computer.bios != null) { \n");
        builder.append("reqNode.putAsset(new RequisitionAsset(\"BiosModel\", computer.bios.SModel)) \n");
        builder.append("}\n return reqNode");

        RequisitionNode reqNode = (RequisitionNode)m_gr.executeScript("groovy", builder.toString(), "", computer);
        assertNotNull(reqNode);
        System.out.println(reqNode);
    }


}
