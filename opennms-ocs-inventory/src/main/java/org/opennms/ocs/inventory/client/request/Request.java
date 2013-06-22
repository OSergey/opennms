//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.06.16 at 07:05:26 PM EEST 
//


package org.opennms.ocs.inventory.client.request;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "engine",
    "begin",
    "askingfor",
    "checksum",
    "offset",
    "wanted",
    "id",
    "tag",
    "userId"
})
@XmlRootElement(name = "REQUEST")
public class Request {
    
    @XmlElement(name = "ENGINE", required = true)
    protected Engine engine;
    @XmlElement(name = "BEGIN", required = true)
    protected Begin begin;
    @XmlElement(name = "ASKING_FOR", required = true)
    protected String askingfor;
    @XmlElement(name = "CHECKSUM", required = true)
    protected String checksum;
    @XmlElement(name = "WANTED", required = true)
    protected String wanted;
    @XmlElement(name = "OFFSET", required = true)
    protected String offset;
    @XmlElement(name = "ID")
    protected List<Id> id;
    @XmlElement(name = "TAG")
    protected List<Tag> tag;
    @XmlElement(name = "USERID")
    protected List<UserId> userId;

    /**
     * Gets the value of the begin property.
     * 
     * @return
     *     possible object is
     *     {@link begin }
     *     
     */
    public Begin getBegin() {
        return begin;
    }

    /**
     * Sets the value of the begin property.
     * 
     * @param value
     *     allowed object is
     *     {@link begin }
     *     
     */
    public void setBegin(Begin value) {
        this.begin = value;
    }

    /**
     * Gets the value of the askingfor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASKINGFOR() {
        return askingfor;
    }

    /**
     * Sets the value of the askingfor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASKINGFOR(String value) {
        this.askingfor = value;
    }

    /**
     * Gets the value of the checksum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCHECKSUM() {
        return checksum;
    }

    /**
     * Sets the value of the checksum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCHECKSUM(String value) {
        this.checksum = value;
    }

    /**
     * Gets the value of the wanted property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWANTED() {
        return wanted;
    }

    /**
     * Sets the value of the wanted property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWANTED(String value) {
        this.wanted = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the id property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Id }
     * 
     * 
     */
    public List<Id> getId() {
        if (id == null) {
            id = new ArrayList<Id>();
        }
        return this.id;
    }

    /**
     * Gets the value of the tag property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tag property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTag().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tag }
     * 
     * 
     */
    public List<Tag> getTag() {
        if (tag == null) {
            tag = new ArrayList<Tag>();
        }
        return this.tag;
    }

    /**
     * Gets the value of the userId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUSERId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link USERId }
     * 
     * 
     */
    public List<UserId> getUserId() {
        if (userId == null) {
            userId = new ArrayList<UserId>();
        }
        return this.userId;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

}
