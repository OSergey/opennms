package org.opennms;

import org.junit.Test;
import org.opennms.ocs.inventory.client.GroovyScript;

import java.io.IOException;

/**
 * @author <A HREF="mailto:sergey.ovsyuk@gmail.com">Sergey Ovsyuk </A>
 */
public class GroovyTest {

    @Test
    public void test() throws IllegalAccessException, IOException, InstantiationException {
        GroovyScript gr = new GroovyScript();
        gr.createGroovyScript();
    }
}

