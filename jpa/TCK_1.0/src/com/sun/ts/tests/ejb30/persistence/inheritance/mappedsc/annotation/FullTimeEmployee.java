/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

/*
 * @(#)FullTimeEmployee.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.mappedsc.annotation;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.Date;

/*
 * FullTimeEmployee entity extends an MappedSuperClass while overriding
 * mapping information.
 */

@Entity
@Table(name="EMPLOYEE")
@AttributeOverrides({
        @AttributeOverride(name="id",column=@Column(name="ID")),
        @AttributeOverride(name="firstName",column=@Column(name="FIRSTNAME")),
        @AttributeOverride(name="lastName",column=@Column(name="LASTNAME")),
        @AttributeOverride(name="hireDate",column=@Column(name="HIREDATE"))
})
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

    @Column(name="SALARY")
    public float getSalary() {
        return salary;
    }
    public void setSalary(float salary) {
        this.salary = salary;
    }
    
}
