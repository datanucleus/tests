/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Employee.java	1.6 06/02/14
 */

package com.sun.ts.tests.ejb30.persistence.query.apitests;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Date;

/*
 * Employee
 */

@Entity
@Table(name="EMPLOYEE")
public class Employee implements java.io.Serializable {

    private int	     	     id;
    private String	     firstName;
    private String	     lastName;
    private Date	     hireDate;
    private float	     salary;
    private Department       department;
    private Insurance        insurance;

    public Employee() {
    }

    public Employee(int id, String firstName, String lastName, Date hireDate,
			float salary)
    {
        	this.id = id;
        	this.firstName = firstName;
        	this.lastName = lastName;
		this.hireDate = hireDate;
		this.salary = salary;
    }

    public Employee(int id, String firstName, String lastName, Date hireDate,
			float salary, Department department, Insurance insurance)
    {
        	this.id = id;
        	this.firstName = firstName;
        	this.lastName = lastName;
		this.hireDate = hireDate;
		this.salary = salary;
        	this.department = department;
		this.insurance = insurance;
    }

   // ===========================================================
   // getters and setters for the state fields

    @Id
    @Column(name="ID")
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Column(name="FIRSTNAME")
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name="LASTNAME")
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name="HIREDATE")
    public Date getHireDate() {
        return hireDate;
    }
    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    @Column(name="SALARY")
    public float getSalary() {
        return salary;
    }
    public void setSalary(float salary) {
        this.salary = salary;
    }

   // ===========================================================
   // getters and setters for the association fields

    @ManyToOne
    @JoinColumn(name="FK_DEPT")
    public Department getDepartment() {
        return department;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne
    @JoinColumn(name="FK_INS")
    public Insurance getInsurance() {
        return insurance;
    }
    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

}

