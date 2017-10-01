/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Department.java	1.1 06/02/21
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.mappedsc.descriptors;


/*
 * Department
 */

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

