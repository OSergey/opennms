package org.opennms.ocs.inventory.client;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.opennms.ocs.inventory.client.response.Bios;
import org.opennms.ocs.inventory.client.response.Computer;

import java.io.File;
import java.io.IOException;

/**
 * @author <A HREF="mailto:sergey.ovsyuk@gmail.com">Sergey Ovsyuk </A>
 */
public class GroovyLogic {

    private static String s_method_name = "mappingModelData";

    public void createGroovyScript() throws IOException, IllegalAccessException, InstantiationException {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class groovyClass = loader.parseClass(new File("src/test/groovy/script/HelloWorld.groovy"));

        // let's call some method on an instance
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
        Computer cmp =new Computer();
        Bios bs = new Bios();
        bs.setSModel("Model");
        bs.setSManufacturer("Manufacture");
        cmp.setBios(bs);
        String model = (String)groovyObject.invokeMethod(s_method_name, cmp);
        System.out.println(model);

    }
}
