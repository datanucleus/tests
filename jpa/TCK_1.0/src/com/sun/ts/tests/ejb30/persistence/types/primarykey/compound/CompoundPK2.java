/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)CompoundPK2.java	1.4  06/05/03
 */

package com.sun.ts.tests.ejb30.persistence.types.primarykey.compound;

/*
 * Class used to define a compound primary key for Entity beans.
 */

public class CompoundPK2 implements java.io.Serializable {

    /* Fields */
    private Integer pmIDInteger;
    private String pmIDString;
    private Float pmIDFloat;


    /** No-arg Constructor */
    public CompoundPK2() {
    }

    /** Standard Constructor */
    public CompoundPK2(int intID, String strID, float floatID) {
        this.pmIDInteger = new Integer(intID);
        this.pmIDString = strID;
        this.pmIDFloat = new Float(floatID);
    }

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


    /** Override java.lang.Object method */
    public int hashCode() {
        int myHash;
	
	myHash = this.pmIDInteger.hashCode() 
	         + this.pmIDString.hashCode()
		 + this.pmIDFloat.hashCode();

        return myHash;
    }

    /** Override java.lang.Object method */
    public boolean equals(Object o) {
        CompoundPK2 other;
	boolean same = true;

	if (! (o instanceof CompoundPK2)) {
	    return false;
        }
	other = (CompoundPK2) o;
	
	same &= this.pmIDInteger.equals(other.pmIDInteger);
	same &= this.pmIDString.equals(other.pmIDString);
	same &= this.pmIDFloat.equals(other.pmIDFloat);

        return same;
    }


    /** Override java.lang.Object method */
    public String toString() {
        return "CompoundPK2 [ "
	       + pmIDInteger + ", " 
	       + pmIDString + ", "
	       + pmIDFloat + " ]";
    }

}

