package org.opennms.ocs.inventory.client;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.opennms.ocs.inventory.client.response.Bios;
import org.opennms.ocs.inventory.client.response.Computer;

import java.io.File;
import java.io.IOException;

/**
 * @author <A HREF="mailto:sergey.ovsyuk@gmail.com">Sergey Ovsyuk </A>
 */
public class GroovyMappingLogic {

    private static String s_method_name = "mappingModelData";

    public RequisitionNode createRequisitionNodeFromGroovyScript(Computer cmp, String bodyClass) throws IOException, IllegalAccessException, InstantiationException {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        StringBuilder builder = new StringBuilder();
        builder.append("import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset \n");
        builder.append("import org.opennms.netmgt.provision.persist.requisition.RequisitionNode \n");
        builder.append("import org.opennms.netmgt.provision.persist.requisition.RequisitionCategory \n");
        builder.append("import org.opennms.netmgt.provision.persist.requisition.RequisitionInterface \n");
        builder.append("import org.opennms.ocs.inventory.client.response.Computer \n");
        builder.append("def ");
        builder.append(s_method_name);
        builder.append("(Computer computer){\n");
        builder.append(bodyClass);
        builder.append("}");

        Class groovyClass = loader.parseClass(builder.toString());

        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();


        return (RequisitionNode)groovyObject.invokeMethod(s_method_name, cmp);

    }
}
