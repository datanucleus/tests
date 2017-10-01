/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)CompoundPK.java	1.4  06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.types.primarykey.compound;

import javax.persistence.Embeddable;

/*
 * Class used to define a compound primary key for Entity beans.
 */

@Embeddable
public class CompoundPK implements java.io.Serializable {

    /* Fields */
   private Integer pmIDInteger;
   private String pmIDString;
   private Float pmIDFloat;


   public Integer getPmIDInteger() {
        return pmIDInteger;
    }
    public void setPmIDInteger(Integer pmIDInteger) {
        this.pmIDInteger = pmIDInteger;
    }
    public String getPmIDString() {
        return pmIDString;
    }
    public void setPmIDString(String pmIDString) {
        this.pmIDString = pmIDString;
    }
	
    public Float getPmIDFloat() {
        return pmIDFloat;
    }
    public void setPmIDFloat(Float pmIDFloat) {
        this.pmIDFloat = pmIDFloat;
    }
	
    /** No-arg Constructor */
    public CompoundPK() {
    }

    /** Standard Constructor */
    public CompoundPK(int intID, String strID, float floatID) {
        this.pmIDInteger = new Integer(intID);
        this.pmIDString = strID;
        this.pmIDFloat = new Float(floatID);
    }


    /** Override java.lang.Object method */
    public int hashCode() {
        int myHash;
	
	myHash = getPmIDInteger().hashCode() 
	         + getPmIDString().hashCode()
		 + getPmIDFloat().hashCode();

        return myHash;
    }


    /** Override java.lang.Object method */
    public boolean equals(Object o) {
        CompoundPK other;
	boolean same = true;

	if (! (o instanceof CompoundPK)) {
	    return false;
        }
	other = (CompoundPK) o;
	
	same &= getPmIDInteger().equals(other.getPmIDInteger());
	same &= getPmIDString().equals(other.getPmIDString());
	same &= getPmIDFloat().equals(other.getPmIDFloat());

        return same;
    }


    /** Override java.lang.Object method */
    public String toString() {
        return "CompoundPK [ "
	       + getPmIDInteger() + ", " 
	       + getPmIDString() + ", "
	       + getPmIDFloat() + " ]";
    }

}

