/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Department.java	1.5 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.abstractentity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.Collection;

/*
 * Department
 */

@Entity
public class Department implements java.io.Serializable {

    // Instance variables
    private int  id;
    private String  name;
    private Collection<Employee> employees = new java.util.ArrayList<Employee>();

    public Department() {
    }

    public Department(int id, String name) {
        this.id = id;
        this.name = name;
    }


    // ===========================================================
    // getters and setters for the state fields

    @Id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

   // ===========================================================
   // getters and setters for the association fields

    @OneToMany(cascade=CascadeType.ALL, mappedBy="department")
    public Collection<Employee> getEmployees() {
        return employees;
    }
    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }


}

