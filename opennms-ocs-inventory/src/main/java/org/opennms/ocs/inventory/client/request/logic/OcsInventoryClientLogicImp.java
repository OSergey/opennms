package org.opennms.ocs.inventory.client.request.logic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.codec.binary.Base64;
import org.opennms.ocs.inventory.client.request.Engine;
import org.opennms.ocs.inventory.client.request.Request;
import org.opennms.ocs.inventory.client.request.RequestFactory;
import org.opennms.ocs.inventory.client.response.Computers;

/**
 * The Class OcsInventoryClientLogicImp.
 * 
 * @author <A HREF="mailto:sergey.ovsyuk@gmail.com">Sergey Ovsyuk </A>
 */
public class OcsInventoryClientLogicImp implements OcsInventoryClientLogic {

    /** The url. */
    private static String m_url;

    /** The url name space xml. */
    private static String m_urlNameSpaceXml;

    /** The login. */
    private static String m_login;

    /** The password. */
    private static String m_password;

    /** The soap connection. */
    private SOAPConnection soapConnection;

    /** The Constant ENGINE. */
    private static final String ENGINE = "FIRST";

    /** The Constant ASKINGFOR. */
    private static final String ASKINGFOR = "INVENTORY";

    /** The Constant CHECKSUM. */
    private static final String CHECKSUM = "119587";

    /** The Constant WEB__SERVICE_METHOD. */
    private static final String WEB__SERVICE_METHOD = "get_computers_V1";

    /*
     * (non-Javadoc)
     * @see
     * org.opennms.ocs.inventory.client.request.logic.OcsInventoryClientLogic
     * #init(java.lang.String, java.lang.String, java.lang.String)
     */
    public void init(String host, String login, String password)
            throws SOAPException {

        m_url = String.format("http://%s:%s@%s/ocsinterface", login,
                              password, host);
        m_urlNameSpaceXml = String.format("http://%s/Apache/Ocsinventory/Interface",
                                          host);

        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();

    }

    /*
     * (non-Javadoc)
     * @see
     * org.opennms.ocs.inventory.client.request.logic.OcsInventoryClientLogic
     * #getComputers()
     */
    public Computers getComputers() throws SOAPException, Exception {
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(),
                                                       m_url);

        SOAPBody spBody = soapResponse.getSOAPBody();
        SOAPElement soapElement = (SOAPElement) spBody.getChildElements().next();
        String content = "";
        for (int i = 1; i < soapElement.getChildNodes().getLength() - 1; i++) {
            content = soapElement.getChildNodes().item(i).getTextContent();
            byte[] decodedBytes = Base64.decodeBase64(content.getBytes());
            soapElement.getChildNodes().item(i).setTextContent(new String(
                                                                          decodedBytes)
                                                                       + "\n");
        }

        content = soapElement.getTextContent();
        JAXBContext jaxbContext = JAXBContext.newInstance(Computers.class);
        Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
        InputStream is = new ByteArrayInputStream(content.getBytes());
        Computers comp = (Computers) jaxbMarshaller.unmarshal(is);
        return comp;
    }

    /**
     * Creates the soap request.
     * 
     * @return the SOAP message
     * @throws Exception
     *             the exception
     */
    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPHeader header = envelope.getHeader();
        header.addNamespaceDeclaration("xsi",
                                       "http://www.w3.org/2001/XMLSchema-instance");
        header.addNamespaceDeclaration("soapenc",
                                       "http://schemas.xmlsoap.org/soap/encoding/");
        header.addNamespaceDeclaration("xsd",
                                       "http://www.w3.org/2001/XMLSchema");
        header.addNamespaceDeclaration("soap",
                                       "http://schemas.xmlsoap.org/soap/envelope/");

        // SOAP Body
        RequestFactory objFact = new RequestFactory();
        Request request = objFact.createRequest();
        Engine eng = new Engine();
        eng.setValue(ENGINE);
        request.setEngine(eng);
        request.setAskingfor(ASKINGFOR);
        request.setChecksum(CHECKSUM);
        request.setOffset("0");
        request.setWanted(CHECKSUM);
        JAXBContext jaxbContext = JAXBContext.newInstance(Request.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();
        jaxbMarshaller.marshal(request, writer);
        SOAPBody soapBody = envelope.getBody();

        QName bodyName = new QName(m_urlNameSpaceXml, WEB__SERVICE_METHOD,
                                   XMLConstants.DEFAULT_NS_PREFIX);
        SOAPBodyElement bodyElement = soapBody.addBodyElement(bodyName);
        SOAPElement soapBodyElem1 = bodyElement.addChildElement("c-gensym3");
        soapBodyElem1.setTextContent(writer.toString());

        soapMessage.saveChanges();

        return soapMessage;
    }

}
