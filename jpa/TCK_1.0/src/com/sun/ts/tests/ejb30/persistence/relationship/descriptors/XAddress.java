/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)XAddress.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.relationship.descriptors;


import java.util.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/*
 * XAddress
 */

public class XAddress implements java.io.Serializable {


    // Instance Variables
    private String xId;
    private String xStreet;
    private String xCity;
    private String xState;
    private String xZip;

    public XAddress() {
    }

    public XAddress(String xId, String xStreet, String xCity, String xState, String xZip)
    {
      this.xId = xId;
      this.xStreet = xStreet;
      this.xCity = xCity;
      this.xState = xState;
      this.xZip = xZip;
    }

    // ===========================================================
    // getters and setters for the state fields

    public String getXId() {
	return xId;
    }
    public void setXId(String xId) {
	this.xId = xId;
    }

    public String getXStreet() {
	return xStreet;
    }
    public void setXStreet(String xStreet) {
	this.xStreet = xStreet;
    }

    public String getXCity() {
	return xCity;
    }
    public void setXCity(String xCity) {
	this.xCity = xCity;
    }

    public String getXState() {
	return xState;
    }
    public void setXState(String xState) {
	this.xState = xState;
    }

    public String getXZip() {
	return xZip;
    }
    public void setXZip(String xZip) {
	this.xZip = xZip;
    }
}
