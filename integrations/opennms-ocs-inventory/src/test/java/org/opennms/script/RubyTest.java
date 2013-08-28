package org.opennms.script;

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
public class RubyTest {
    @Test
    public void test() throws IllegalAccessException, IOException, InstantiationException {
    	ManagerScript m_gr = new ManagerScript();
        Computer computer = new Computer();
        Bios bs = new Bios();
        bs.setSModel("Model");
        bs.setSManufacturer("Manufacture");
        computer.setBios(bs);
        StringBuilder builder = new StringBuilder();
        builder.append("java_import \"org.opennms.netmgt.provision.persist.requisition.RequisitionNode\" \n");
        builder.append("java_import \"org.opennms.netmgt.provision.persist.requisition.RequisitionAsset\" \n");
        builder.append("reqNode = RequisitionNode.new() \n");
        builder.append("unless $computer.bios.nil? \n");
        builder.append("reqNode.putAsset(RequisitionAsset.new(\"BiosModel\", $computer.bios.sModel)) \n");
        builder.append("end \n return reqNode");

        RequisitionNode reqNode = (RequisitionNode)m_gr.executeScript("ruby", builder.toString(), "", computer);
        assertNotNull(reqNode);
        System.out.println(reqNode);
        System.out.println(reqNode.getAssets());
    }


}
