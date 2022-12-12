package com.zileanstdio.chatapp.Data.model;

import java.io.Serializable;
import java.util.Date;

public class Contact implements Serializable {
    private String numberPhone;
    private String contactName;
    private int relationship;
    private Date modifiedAt;

    public Contact() {
    }

    public Contact(String numberPhone, String contactName, int relationship, Date modifiedAt) {
        this.numberPhone = numberPhone;
        this.contactName = contactName;
        this.relationship = relationship;
        this.modifiedAt = modifiedAt;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "numberPhone='" + numberPhone + '\'' +
                ", contactName='" + contactName + '\'' +
                ", relationship=" + relationship +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}
