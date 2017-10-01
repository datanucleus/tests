/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Employee.java	1.5 06/03/30
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.nonentity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;
import java.sql.Date;

/*
 * Employee
 */

@Entity
@Table(name="EMPLOYEE")
@Inheritance
@DiscriminatorColumn(name = "STATUS")
public abstract class Employee extends Personnel {

    protected int	     id;
    protected String	     firstName;
    protected String	     lastName;

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

