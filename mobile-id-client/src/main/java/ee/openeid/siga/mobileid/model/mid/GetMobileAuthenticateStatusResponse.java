//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.26 at 05:42:17 PM EET 
//


package ee.openeid.siga.mobileid.model.mid;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl}AbstractGetStatusResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UserIDCode" type="{http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl}IdCodeType"/&gt;
 *         &lt;element name="UserGivenname" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="UserSurname" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="UserCountry" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="UserCN" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CertificateData" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&gt;
 *         &lt;element name="RevocationData" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "userIDCode",
        "userGivenname",
        "userSurname",
        "userCountry",
        "userCN",
        "certificateData",
        "revocationData"
})
@XmlRootElement(name = "GetMobileAuthenticateStatusResponse")
public class GetMobileAuthenticateStatusResponse
        extends AbstractGetStatusResponseType {

    @XmlElement(name = "UserIDCode", required = true)
    protected String userIDCode;
    @XmlElement(name = "UserGivenname", required = true)
    protected String userGivenname;
    @XmlElement(name = "UserSurname", required = true)
    protected String userSurname;
    @XmlElement(name = "UserCountry", required = true)
    protected String userCountry;
    @XmlElement(name = "UserCN", required = true)
    protected String userCN;
    @XmlElement(name = "CertificateData")
    protected byte[] certificateData;
    @XmlElement(name = "RevocationData")
    protected byte[] revocationData;

    /**
     * Gets the value of the userIDCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUserIDCode() {
        return userIDCode;
    }

    /**
     * Sets the value of the userIDCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUserIDCode(String value) {
        this.userIDCode = value;
    }

    /**
     * Gets the value of the userGivenname property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUserGivenname() {
        return userGivenname;
    }

    /**
     * Sets the value of the userGivenname property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUserGivenname(String value) {
        this.userGivenname = value;
    }

    /**
     * Gets the value of the userSurname property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUserSurname() {
        return userSurname;
    }

    /**
     * Sets the value of the userSurname property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUserSurname(String value) {
        this.userSurname = value;
    }

    /**
     * Gets the value of the userCountry property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUserCountry() {
        return userCountry;
    }

    /**
     * Sets the value of the userCountry property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUserCountry(String value) {
        this.userCountry = value;
    }

    /**
     * Gets the value of the userCN property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUserCN() {
        return userCN;
    }

    /**
     * Sets the value of the userCN property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUserCN(String value) {
        this.userCN = value;
    }

    /**
     * Gets the value of the certificateData property.
     *
     * @return possible object is
     * byte[]
     */
    public byte[] getCertificateData() {
        return certificateData;
    }

    /**
     * Sets the value of the certificateData property.
     *
     * @param value allowed object is
     *              byte[]
     */
    public void setCertificateData(byte[] value) {
        this.certificateData = value;
    }

    /**
     * Gets the value of the revocationData property.
     *
     * @return possible object is
     * byte[]
     */
    public byte[] getRevocationData() {
        return revocationData;
    }

    /**
     * Sets the value of the revocationData property.
     *
     * @param value allowed object is
     *              byte[]
     */
    public void setRevocationData(byte[] value) {
        this.revocationData = value;
    }

}