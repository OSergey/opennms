package org.opennms.ocs.inventory.client;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.io.File;
import java.io.IOException;

/**
 * @author <A HREF="mailto:sergey.ovsyuk@gmail.com">Sergey Ovsyuk </A>
 */
public class GroovyScript {

    public void createGroovyScript() throws IOException, IllegalAccessException, InstantiationException {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class groovyClass = loader.parseClass(new File("src/test/groovy/script/HelloWorld.groovy"));

        // let's call some method on an instance
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
        Object[] args = {};
        groovyObject.invokeMethod("runEx", args);

    }

}
