/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

/*
 * @(#)PartTimeEmployee.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.abstractentity;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import java.sql.Date;

/*
 * PartTimeEmployee
 */

@Entity
@DiscriminatorValue("NONEXEMPT")
public class PartTimeEmployee extends Employee {

    private float wage;

    public PartTimeEmployee() {
    }

    public PartTimeEmployee(int id, String firstName, String lastName, Date hireDate,
                        float salary)
    {
                this.id = id;
                this.firstName = firstName;
                this.lastName = lastName;
                this.hireDate = hireDate;
                this.wage = wage;
    }


   // ===========================================================
   // getters and setters for the state fields

    @Column(name="SALARY")
    public float getWage() {
        return wage;
    }
    public void setWage(float wage) {
        this.wage = wage;
    }
    
}
