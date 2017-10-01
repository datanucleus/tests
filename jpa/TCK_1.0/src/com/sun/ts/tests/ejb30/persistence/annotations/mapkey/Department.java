/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Department.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.annotations.mapkey;

import java.util.Map;
import java.util.HashMap;
import javax.persistence.MapKey;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/*
 * Department
 */

@Entity
@Table(name="DEPARTMENT")
public class Department implements java.io.Serializable {

    // Instance variables
    private int  id;
    private String  name;
    private Map<String, Employee> lastNameEmployees;

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
    @MapKey(name="lastName")
    public Map<String, Employee> getLastNameEmployees() {
        return lastNameEmployees;
    }
    public void setLastNameEmployees(Map<String, Employee> lastNameEmployees) {
        this.lastNameEmployees = lastNameEmployees;
    }

}
