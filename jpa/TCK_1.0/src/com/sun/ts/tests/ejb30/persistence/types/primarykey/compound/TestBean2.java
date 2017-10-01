/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)TestBean2.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.types.primarykey.compound;

import com.sun.ts.lib.util.*;
import com.sun.ts.lib.porting.*;
import com.sun.ts.tests.ejb30.common.helper.TLogger;

import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Basic;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@IdClass(com.sun.ts.tests.ejb30.persistence.types.primarykey.compound.CompoundPK2.class)
@Table(name= "PKEY")
public class TestBean2 implements java.io.Serializable{

   private Integer pmIDInteger;
   private String pmIDString;
   private Float pmIDFloat;
   private String brandName;
   private float price;

   public TestBean2() {
   }

   public TestBean2(Integer pmIDInteger, String pmIDString, Float pmIDFloat,
			 String brandName, float price) {
        this.pmIDInteger = pmIDInteger;
        this.pmIDString = pmIDString;
        this.pmIDFloat = pmIDFloat;
	this.brandName = brandName;
	this.price = price;
   }

    @Id
    public Integer getPmIDInteger() {
	return pmIDInteger;
    }
    public void setPmIDInteger(Integer intID) {
	
        this.pmIDInteger = intID;
    }

    @Id
    public String getPmIDString() {
	return pmIDString;
    }
    public void setPmIDString(String stringID) {
        this.pmIDString = stringID;
    }

    @Id
    public Float getPmIDFloat() {
	return pmIDFloat;
    }
    public void setPmIDFloat(Float floatID) {
        this.pmIDFloat = new Float(floatID);
    }

    @Basic
    public String getBrandName() {
        return brandName;
    } public void setBrandName(String v) {
        this.brandName = v;
    }

    @Basic
    public float getPrice() {
        return price;
    }
    public void setPrice(float v) {
        this.price = v;
    }


    public void ping() {
	TLogger.log("[TestBean] ping()");
    }

}

