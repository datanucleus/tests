 /* Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
 
 /*
  * @(#)XInsurance.java	1.2 06/02/11
  */

package com.sun.ts.tests.ejb30.persistence.relationship.descriptors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.Set;

/*
 * XInsurance
 */

public class XInsurance implements java.io.Serializable {

    // Instance variables
    private int  xInsId;
    private String  xCarrier;

    public XInsurance() {
    }

    public XInsurance(int xInsId, String xCarrier) {
        this.xInsId = xInsId;
        this.xCarrier = xCarrier;
    }


    // ===========================================================
    // getters and setters for the state fields

    public int getXInsId() {
        return xInsId;
    }
    public void setXInsId(int xInsId) {
        this.xInsId = xInsId;
    }

    public String getXCarrier() {
        return xCarrier;
    }
    public void setXCarrier(String xCarrier) {
        this.xCarrier = xCarrier;
    }

}

