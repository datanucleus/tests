/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

/*
 * @(#)PartTimeEmployee.java	1.1 06/02/21
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.mappedsc.descriptors;

import java.sql.Date;

/*
 * PartTimeEmployee entity extends an MappedSuperClass while overriding
 * mapping information.
 */

public class PartTimeEmployee extends Employee {

    private float wage;

    public PartTimeEmployee() {
    }

    public PartTimeEmployee(int id, String firstName, String lastName, Date hireDate,
                        float salary)
    {
                super(id, firstName, lastName, hireDate);
                this.wage = wage;
    }


   // ===========================================================
   // getters and setters for the state fields

    public float getWage() {
        return wage;
    }
    public void setWage(float wage) {
        this.wage = wage;
    }
    
}
