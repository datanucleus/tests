/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Address.java	1.5 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;
import java.util.Collection;

/*
 * Address
 */

@Entity
@Table(name="ADDRESS")
public class Address implements java.io.Serializable {


    // Instance Variables
    private String id;
    private String street;
    private String city;
    private String state;
    private String zip;
    private Collection<Phone> phones = new java.util.ArrayList<Phone>();

    public Address() {
    }

    public Address(String id, String street, String city, String state, String zip)
    {
      this.id = id;
      this.street = street;
      this.city = city;
      this.state = state;
      this.zip = zip;
    }

    public Address(String id, String street, String city, String state, String zip,
		Collection<Phone> phones)
    {
      this.id = id;
      this.street = street;
      this.city = city;
      this.state = state;
      this.zip = zip;
      this.phones = phones;
    }

    // ===========================================================
    // getters and setters for the persistent fields

    @Id
    @Column(name="ID")
    public String getId() {
	return id;
    }
    public void setId(String id) {
	this.id = id;
    }

    @Column(name="STREET")
    public String getStreet() {
	return street;
    }
    public void setStreet(String street) {
	this.street = street;
    }

    @Column(name="CITY")
    public String getCity() {
	return city;
    }
    public void setCity(String city) {
	this.city = city;
    }

    @Column(name="STATE")
    public String getState() {
	return state;
    }
    public void setState(String state) {
	this.state = state;
    }

    @Column(name="ZIP")
    public String getZip() {
	return zip;
    }
    public void setZip(String zip) {
	this.zip = zip;
    }

    // ===========================================================
    // getters and setters for association fields

    // 1xMANY
    @OneToMany(cascade=CascadeType.ALL, mappedBy="address")
    public Collection<Phone> getPhones() {
	return phones;
    }
    public void setPhones(Collection<Phone> phones) {
	this.phones = phones;
    }

}
