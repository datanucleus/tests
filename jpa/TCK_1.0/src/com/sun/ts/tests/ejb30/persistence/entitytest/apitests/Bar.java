/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Bar.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.apitests;


public class Bar implements java.io.Serializable {
    
    private Integer id;
    private String brandName;
    private float price;

    public Bar() {
    }


    public Bar(Integer id, String brandName, float price) {
	this.id = id;
	this.brandName = brandName;
	this.price = price;
    }

   public Integer getId() {
	return id;
   }
   public void setId(Integer id) {
	this.id = id;
   }

   public String getBrandName() {
        return brandName;
   }    

   public void setBrandName(String brandName) {
        this.brandName = brandName;
   }

   public float getPrice() {
        return price;
   }

   public void setPrice(float price) {
        this.price = price;
   }

}
