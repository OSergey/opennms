import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.opennms.ocs.inventory.client.request.Engine;
import org.opennms.ocs.inventory.client.request.ObjectFactory;
import org.opennms.ocs.inventory.client.request.Request;






public class main {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
           
            // Send SOAP Message to SOAP Server
            String url = "http://dev:dev@192.168.56.101/ocsinterface";
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);

            // Process the SOAP Response
            printSOAPResponse(soapResponse);

            soapConnection.close();
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
        }
        
    }

    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();    
        //String serverURI = "http://dev:dev@192.168.56.101/ocsinterface";
                      
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();

        
        String body = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?&gt;&lt;REQUEST&gt;&lt;ENGINE&gt;FIRST&lt;/ENGINE&gt;&lt;ASKING_FOR&gt;META&lt;/ASKING_FOR&gt;&lt;CHECKSUM&gt;65536&lt;/CHECKSUM&gt;&lt;OFFSET&gt;0&lt;/OFFSET&gt;&lt;WANTED&gt;131071&lt;/WANTED&gt;&lt;/REQUEST&gt;";


        SOAPHeader header     = envelope.getHeader(); 
        header.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        header.addNamespaceDeclaration("soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        header.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        header.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");

        // SOAP Body
        ObjectFactory objFact = new ObjectFactory();
        Request request = objFact.createRequest();
        Engine eng = new Engine();
        eng.setvalue("FIRST");
        request.setEngine(eng);
        request.setASKINGFOR("INVENTORY");
        request.setCHECKSUM("65536");
        request.setOffset("0");
        request.setWANTED("131071");
        JAXBContext jaxbContext = JAXBContext.newInstance(Request.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();
        jaxbMarshaller.marshal(request, writer);
        SOAPBody soapBody = envelope.getBody();
        
        QName bodyName = new QName("http://192.168.56.101/Apache/Ocsinventory/Interface", "get_computers_V1", XMLConstants.DEFAULT_NS_PREFIX);
        SOAPBodyElement bodyElement = soapBody.addBodyElement(bodyName);     
        SOAPElement soapBodyElem1 =  bodyElement.addChildElement("c-gensym3");
        soapBodyElem1.setTextContent(writer.toString());
        
        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }
    
    


    private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);      
        transformer.transform(sourceContent, result);
        //String content = StringEscapeUtils.unescapeHtml(writer.toString());
        //JAXBContext jaxbContext = JAXBContext.newInstance(Computers.class);
        //Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
        OutputStream outstr = new FileOutputStream( "./computers.xml" );
        Base64 decoder = new Base64();
        
        // it's necessary because body response have base64 format     

        byte[] decodedBytes = decoder.decodeBase64("PENPTVBVVEVSPgogIDxBQ0NPVU5USU5GTz4KICAgIDxFTlRSWSBOYW1lP".getBytes());
        System.out.println(new String(decodedBytes) + "\n") ; 
        outstr.write((new String(decodedBytes) + "\n").getBytes());
        outstr.write(writer.toString().getBytes());
        outstr.close();
/*        //File is = new File( "./computers.xml" );
        COMPUTERS comp = (COMPUTERS) jaxbMarshaller.unmarshal(is);
        for(COMPUTER cmp:comp.getCmp()){
        System.out.println(cmp.getDEVICEID());*/
        }

        
    
}
