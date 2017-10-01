/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Address.java	1.3 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.relationship.defaults;


import java.util.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/*
 * Address
 */

@Entity
public class Address implements java.io.Serializable {


    // Instance Variables
    private String id;
    private String street;
    private String city;
    private String state;
    private String zip;

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

    // ===========================================================
    // getters and setters for the state fields

    @Id
    public String getId() {
	return id;
    }
    public void setId(String id) {
	this.id = id;
    }

    public String getStreet() {
	return street;
    }
    public void setStreet(String street) {
	this.street = street;
    }

    public String getCity() {
	return city;
    }
    public void setCity(String city) {
	this.city = city;
    }

    public String getState() {
	return state;
    }
    public void setState(String state) {
	this.state = state;
    }

    public String getZip() {
	return zip;
    }
    public void setZip(String zip) {
	this.zip = zip;
    }
}
