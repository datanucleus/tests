/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Department.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.nonentity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;

/*
 * Department
 */

@Entity
public class Department implements java.io.Serializable {

    // Instance variables
    private int  id;
    private String  name;

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

}

