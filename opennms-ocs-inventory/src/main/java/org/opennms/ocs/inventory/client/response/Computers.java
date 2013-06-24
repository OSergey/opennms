//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the
// source schema.
// Generated on: 2013.06.24 at 02:50:53 PM EEST
//

package org.opennms.ocs.inventory.client.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * The following schema fragment specifies the expected content contained
 * within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Computer" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AccountInfo">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Entry">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="BIOS">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ASSETTAG" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="BDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                             &lt;element name="BMANUFACTURER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="BVERSION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="SMANUFACTURER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="SMODEL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="SSN" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="CONTROLLERS" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="CAPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="MANUFACTURER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="VERSION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="DICO_SOFT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="DRIVES" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="CREATEDATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="FILESYSTEM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="FREE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="LETTER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="NUMFILES" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="TOTAL" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="VOLUMN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="HARDWARE">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="CHECKSUM" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="DEFAULTGATEWAY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="DNS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="FIDELITY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="IPADDR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IPSRC" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="LASTCOME" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                             &lt;element name="LASTDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                             &lt;element name="MEMORY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="OSCOMMENTS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="OSNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="OSVERSION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="PROCESSORN" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="PROCESSORS" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="PROCESSORT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="QUALITY" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="SSTATE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="SWAP" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="USERAGENT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="USERDOMAIN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="USERID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="WINCOMPANY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="WINOWNER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="WINPRODID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="WINPRODKEY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="WORKGROUP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="NETWORKS">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IPADDRESS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IPDHCP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IPGATEWAY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IPMASK" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IPSUBNET" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="MACADDR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="SPEED" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="TYPEMIB" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="VIRTUALDEV" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="SOFTWARES" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="BITSWIDTH" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="COMMENTS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="FILESIZE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="FOLDER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="GUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="INSTALLDATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="LANGUAGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="PUBLISHER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="SOURCE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="VERSION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="SOUNDS" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="MANUFACTURER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="STORAGES">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="DISKSIZE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="FIRMWARE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="MANUFACTURER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="MODEL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="SERIALNUMBER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Videos">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="CHIPSET" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="MEMORY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="RESOLUTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author <A HREF="mailto:sergey.ovsyuk@gmail.com">Sergey Ovsyuk </A>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "_computer" })
@XmlRootElement(name = "COMPUTERS")
public class Computers {

    @XmlElement(name = "COMPUTER", required = true)
    protected List<Computer> _computer;

    /**
     * Gets the value of the m_computer property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the m_computer property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getComputer().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Computers.Computer }
     */
    public List<Computer> getComputer() {
        if (_computer == null) {
            _computer = new ArrayList<Computer>();
        }
        return this._computer;
    }
}
