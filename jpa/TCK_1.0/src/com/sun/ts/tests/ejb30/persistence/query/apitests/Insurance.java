/* * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Insurance.java	1.4 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.query.apitests;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.Collection;

/*
 * Insurance
 */

@Entity
@Table(name="INSURANCE")
public class Insurance implements java.io.Serializable {

    // Instance variables
    private int  id;
    private String  carrier;
    private Collection<Employee> employees = new java.util.ArrayList<Employee>();

    public Insurance() {
    }

    public Insurance(int id, String carrier) {
        this.id = id;
        this.carrier = carrier;
    }


    // ===========================================================
    // getters and setters for the state fields

    @Id
    @Column(name="INSID")
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Column(name="CARRIER")
    public String getCarrier() {
        return carrier;
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    // ===========================================================
    // getters and setters for the association fields

    @OneToMany(cascade=CascadeType.ALL, mappedBy="insurance")
    public Collection<Employee> getEmployees() {
        return employees;
    }
    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }

}
