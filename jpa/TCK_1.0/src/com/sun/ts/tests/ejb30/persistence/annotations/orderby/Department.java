/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Department.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.annotations.orderby;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/*
 * Department
 */

@Entity
@Table(name="DEPARTMENT")
public class Department implements java.io.Serializable {

    // Instance variables
    private int  id;
    private String  name;
    private List<Employee> employees;

    public Department() {
    }

    public Department(int id, String name) {
        this.id = id;
        this.name = name;
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

    @Column(name="NAME")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // ===========================================================
    // getters and setters for the association fields

    @OneToMany(mappedBy="department")
    @OrderBy("firstName ASC")
    public List <Employee> getEmployees() {
        return employees;
    }
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

}
