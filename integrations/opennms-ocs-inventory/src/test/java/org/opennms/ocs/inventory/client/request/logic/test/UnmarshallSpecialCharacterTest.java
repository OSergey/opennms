package org.opennms.ocs.inventory.client.request.logic.test;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opennms.ocs.inventory.client.response.Bios;
import org.opennms.ocs.inventory.client.response.Computer;
import org.opennms.ocs.inventory.client.response.Computers;

public class UnmarshallSpecialCharacterTest {
  
    public void testMarshaller() throws JAXBException {
       Computers cmps = new Computers();
       Computer cmp = new Computer();
       Bios bs = new Bios();
       String content = "Manufact ®";
       byte[] encodedBytes = Base64.encodeBase64(content.getBytes());
       bs.setBManufacturer(new String(encodedBytes));
       cmp.setBios(bs);
       cmps.getComputers().add(cmp);
       File file = new File("src/test/java/test.xml");
	   JAXBContext jaxbContext = JAXBContext.newInstance(Computers.class);
	   Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	   jaxbMarshaller.marshal(cmps, file);

    }
    
    @Test
	public void testUnmarshaller() throws JAXBException, FileNotFoundException, IOException {
		
		//File file = new File("src/test/java/test.xml");
/*		String str = "TWFudWZhY3Qgwq4=";
		byte[] decodedBytes = Base64.decodeBase64(str.getBytes());
		System.out.println(new String(decodedBytes));*/
		String content = IOUtils.toString(new FileReader("src/test/java/test.xml"));
		System.out.println(content);
		InputStream is = new ByteArrayInputStream(content.getBytes());
     	JAXBContext jaxbContext = JAXBContext.newInstance(Computers.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Computers cmps = (Computers)jaxbUnmarshaller.unmarshal(is);
		System.out.println(cmps.getComputers().get(0).getBios().getBManufacturer());

	}

}
