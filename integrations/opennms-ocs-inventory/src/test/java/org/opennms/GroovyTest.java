package org.opennms;

import org.junit.Test;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.opennms.ocs.inventory.client.GroovyMappingLogic;
import org.opennms.ocs.inventory.client.response.Bios;
import org.opennms.ocs.inventory.client.response.Computer;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * @author <A HREF="mailto:sergey.ovsyuk@gmail.com">Sergey Ovsyuk </A>
 */
public class GroovyTest {
    @Test
    public void test() throws IllegalAccessException, IOException, InstantiationException {
        GroovyMappingLogic gr = new GroovyMappingLogic();
        Computer computer = new Computer();
        Bios bs = new Bios();
        bs.setSModel("Model");
        bs.setSManufacturer("Manufacture");
        computer.setBios(bs);
        StringBuilder builder = new StringBuilder();
        builder.append("RequisitionNode reqNode = new RequisitionNode() \n");
        builder.append("if (computer.bios != null) { \n");
        builder.append("reqNode.putAsset(new RequisitionAsset(\"BiosModel\", computer.bios.SModel)) \n");
        builder.append("}\n return reqNode");

        RequisitionNode reqNode = gr.createRequisitionNodeFromGroovyScript(computer, builder.toString());
        assertNotNull(reqNode);
        System.out.println(reqNode.getAssets());
    }


}
