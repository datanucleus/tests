/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

/*
 * @(#)FullTimeEmployee.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.abstractentity;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.DiscriminatorValue;
import java.sql.Date;

/*
 * FullTimeEmployee
 */

@Entity
@Table(name="EMPLOYEE")
@DiscriminatorValue("EXEMPT")
public class FullTimeEmployee extends Employee {

    private float salary;

    public FullTimeEmployee() {
    }

    public FullTimeEmployee(int id, String firstName, String lastName, Date hireDate,
                        float salary)
    {
                this.id = id;
                this.firstName = firstName;
                this.lastName = lastName;
                this.hireDate = hireDate;
                this.salary = salary;
    }

   // ===========================================================
   // getters and setters for the state fields

    @Column(name="SALARY")
    public float getSalary() {
        return salary;
    }
    public void setSalary(float salary) {
        this.salary = salary;
    }
    
}
