 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

 /*
  * @(#)Coffee.java	1.2 06/02/11
  */

package com.sun.ts.tests.ejb30.persistence.annotations.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "cof")
@Table(name = "COFFEE")
public class Coffee implements java.io.Serializable {

    private Integer id;
    private String brandName;
    private float price;
    
    public Coffee() {
    }
    
    public Coffee(Integer id, String brandName, float price) {
	this.id = id;
	this.brandName = brandName;
        this.price = price;
    }
    
    @Id
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String bName) {
        this.brandName = bName;
    }
    
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }


    public String toString() {
        return "Coffee id=" + getId() + ", brandName=" + getBrandName() + ", price=" + getPrice();
    }
}
