/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Personnel.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.nonentity;

/**
 * A non-entity super class (can be abstract or concrete) for entities.  
 */
public class Personnel {
    
    /**
     * non-persistence field, and it should not participate in any persistence
     * operations.
     */
    protected java.sql.Date hireDate;
    
    public java.sql.Date getHireDate() {
        return hireDate;
    }
    public void setHireDate(java.sql.Date hireDate) {
        this.hireDate = hireDate;
    }


    public String getFullTimeRep() {
            return "Mabel Murray";
        }

    public String getPartTimeRep() {
           return "John Cleveland";
    }


}
