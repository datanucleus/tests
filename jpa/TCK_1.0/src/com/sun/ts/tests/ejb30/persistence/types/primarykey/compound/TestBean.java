/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)TestBean.java	1.3 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.types.primarykey.compound;

import com.sun.ts.lib.util.*;
import com.sun.ts.lib.porting.*;
import com.sun.ts.tests.ejb30.common.helper.TLogger;

import javax.persistence.Entity;
import javax.persistence.EmbeddedId;
import javax.persistence.Basic;
import javax.persistence.Table;

@Entity
@Table(name= "PKEY")
public class TestBean implements java.io.Serializable{

   private CompoundPK compoundPK;
   private String brandName;
   private float price;

   public TestBean() {
   }

   public TestBean(CompoundPK pk, String brandName, float price) {
	this.compoundPK = pk;
	this.brandName = brandName;
	this.price = price;
   }

   @EmbeddedId
    public CompoundPK getCompoundPK() {
        return compoundPK;
    }
    public void setCompoundPK(CompoundPK compoundPK) {
        this.compoundPK = compoundPK;
    }

    @Basic
    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String v) {
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

