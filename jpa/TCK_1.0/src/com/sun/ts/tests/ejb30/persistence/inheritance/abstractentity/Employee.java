/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Employee.java	1.7 06/04/13
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.abstractentity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import java.sql.Date;

/*
 * Employee
 */

@Entity
@Table(name="EMPLOYEE")
@DiscriminatorColumn(name = "STATUS", discriminatorType = DiscriminatorType.STRING)
public abstract class Employee extends AbstractPersonnel {

    protected int	     id;
    protected String	     firstName;
    protected String	     lastName;
    protected Date	     hireDate;

    /** the project this Employee leads */
    protected Project project;

    /** the department this Employee belongs to */
    protected Department department;


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

    @ManyToOne
    @JoinColumn(name="FK_DEPT")
    public Department getDepartment() {
        return department;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }

    @OneToOne
    @JoinColumn(name="FK_PROJECT")
    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }

}

