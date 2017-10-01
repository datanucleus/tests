/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

/*
 * @(#)PartTimeEmployee.java	1.5 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.nonentity;

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
                        float wage) {
                this.id = id;
                this.firstName = firstName;
                this.lastName = lastName;
                this.hireDate = hireDate;
                this.wage = wage;
    }

    public PartTimeEmployee(int id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
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
