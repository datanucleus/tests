 /* Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
 
 /*
  * @(#)Insurance.java	1.2 06/02/11
  */

package com.sun.ts.tests.ejb30.persistence.annotations.orderby;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.List;

/*
 * Insurance
 */

@Entity
public class Insurance implements java.io.Serializable {

    // Instance variables
    private int  id;
    private String  carrier;
    private List<Employee> employees;

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

    public String getCarrier() {
        return carrier;
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    // ===========================================================
    // getters and setters for the association fields

    @OneToMany(mappedBy="insurance")
    @OrderBy("firstName DESC")
    public List<Employee> getEmployees() {
        return employees;
    }
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

}

