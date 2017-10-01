/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

/*
 * @(#)FullTimeEmployee.java	1.1 06/02/21
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.mappedsc.descriptors;

import java.sql.Date;

/*
 * FullTimeEmployee entity extends an MappedSuperClass while overriding
 * mapping information.
 */

public class FullTimeEmployee extends Employee {

    private float salary;

    public FullTimeEmployee() {
    }

    public FullTimeEmployee(int id, String firstName, String lastName, Date hireDate,
                        float salary)
    {
                super(id, firstName, lastName, hireDate);
                this.salary = salary;
    }

   // ===========================================================
   // getters and setters for the state fields

    public float getSalary() {
        return salary;
    }
    public void setSalary(float salary) {
        this.salary = salary;
    }
    
}
