 /* Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
 
/*
 * @(#)Insurance.java	1.3 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.relationship.annotations;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.Set;

/*
 * Insurance
 */

@Entity
public class Insurance implements java.io.Serializable {

    // Instance variables
    private int  insid;
    private String  carrier;

    public Insurance() {
    }

    public Insurance(int insid, String carrier) {
        this.insid = insid;
        this.carrier = carrier;
    }


    // ===========================================================
    // getters and setters for the state fields

    @Id
    public int getInsId() {
        return insid;
    }
    public void setInsId(int insid) {
        this.insid = insid;
    }

    public String getCarrier() {
        return carrier;
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

}

