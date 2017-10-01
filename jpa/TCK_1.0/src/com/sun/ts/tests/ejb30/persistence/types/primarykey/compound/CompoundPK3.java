/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)CompoundPK3.java	1.1  06/05/03
 */

package com.sun.ts.tests.ejb30.persistence.types.primarykey.compound;

/*
 * Class used to define a compound primary key for Entity beans.
 */

public class CompoundPK3 implements java.io.Serializable {

    /* Fields */
    private Integer pmIDInteger;
    private String pmIDString;
    private Float pmIDFloat;


    /** No-arg Constructor */
    public CompoundPK3() {
    }

    /** Standard Constructor */
    public CompoundPK3(int intID, String strID, float floatID) {
        this.pmIDInteger = new Integer(intID);
        this.pmIDString = strID;
        this.pmIDFloat = new Float(floatID);
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
        CompoundPK3 other;
	boolean same = true;

	if (! (o instanceof CompoundPK3)) {
	    return false;
        }
	other = (CompoundPK3) o;
	
	same &= this.pmIDInteger.equals(other.pmIDInteger);
	same &= this.pmIDString.equals(other.pmIDString);
	same &= this.pmIDFloat.equals(other.pmIDFloat);

        return same;
    }


    /** Override java.lang.Object method */
    public String toString() {
        return "CompoundPK3 [ "
	       + pmIDInteger + ", " 
	       + pmIDString + ", "
	       + pmIDFloat + " ]";
    }

}

