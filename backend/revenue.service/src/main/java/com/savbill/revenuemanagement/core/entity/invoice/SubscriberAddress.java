package com.savbill.revenuemanagement.core.entity.invoice;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

/**
 * The persistent class for the tblmsubscriberaddressrel database table.
 * 
 */
public class SubscriberAddress
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String subscriberid;

    private String addresstype;

    private String landmark;

    private String address1;

    private String address2;

    private String city;

    private String country;

    private String pincode;

    private String state;
    private String area;

    public SubscriberAddress() {
    }

    /**
     * @return the subscriberid
     */
    public String getSubscriberid() {

        return subscriberid;
    }

    /**
     * @param subscriberid the subscriberid to set
     */
    @XmlElement(nillable = true)
    public void setSubscriberid(String subscriberid) {

        this.subscriberid = subscriberid;
    }

    /**
     * @return the addresstype
     */
    public String getAddresstype() {

        return addresstype;
    }

    /**
     * @param addresstype the addresstype to set
     */
    @XmlElement(nillable = true)
    public void setAddresstype(String addresstype) {

        this.addresstype = addresstype;
    }

    /**
     * @return the address1
     */
    public String getAddress1() {

        return address1;
    }

    /**
     * @param address1 the address1 to set
     */
    @XmlElement(nillable = true)
    public void setAddress1(String address1) {

        this.address1 = address1;
    }

    /**
     * @return the address2
     */
    public String getAddress2() {

        return address2;
    }

    /**
     * @param address2 the address2 to set
     */
    @XmlElement(nillable = true)
    public void setAddress2(String address2) {

        this.address2 = address2;
    }

    /**
     * @return the city
     */
    public String getCity() {

        return city;
    }

    /**
     * @param city the city to set
     */
    @XmlElement(nillable = true)
    public void setCity(String city) {

        this.city = city;
    }

    /**
     * @return the country
     */
    public String getCountry() {

        return country;
    }

    /**
     * @param country the country to set
     */
    @XmlElement(nillable = true)
    public void setCountry(String country) {

        this.country = country;
    }

    /**
     * @return the pincode
     */
    public String getPincode() {

        return pincode;
    }

    /**
     * @param pincode the pincode to set
     */
    @XmlElement(nillable = true)
    public void setPincode(String pincode) {

        this.pincode = pincode;
    }

    /**
     * @return the state
     */
    public String getState() {

        return state;
    }

    /**
     * @param state the state to set
     */
    @XmlElement(nillable = true)
    public void setState(String state) {

        this.state = state;
    }

    public String getLandmark() {
        return landmark;
    }

    @XmlElement(nillable = true)
    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "SubscriberAddress [subscriberid=" + subscriberid
            + ", addresstype=" + addresstype + ", address1=" + address1
            + ", address2=" + address2 + ", city=" + city + ", country="
            + country + ", pincode=" + pincode + ", state=" + state + "]";
    }

}
