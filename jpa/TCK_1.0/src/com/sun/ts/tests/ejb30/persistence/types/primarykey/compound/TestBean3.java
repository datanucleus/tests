/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)TestBean3.java	1.5 06/05/03
 */

package com.sun.ts.tests.ejb30.persistence.types.primarykey.compound;

import com.sun.ts.lib.util.*;
import com.sun.ts.lib.porting.*;
import com.sun.ts.tests.ejb30.common.helper.TLogger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(com.sun.ts.tests.ejb30.persistence.types.primarykey.compound.CompoundPK3.class)
@Table(name= "PKEY")
public class TestBean3 implements java.io.Serializable{

   @Id
   private Integer pmIDInteger;
   @Id
   private String pmIDString;
   @Id
   private Float pmIDFloat;
   private String brandName;
   private float price;

   public TestBean3() {
   }

   public TestBean3(Integer pmIDInteger, String pmIDString, Float pmIDFloat,
			 String brandName, float price) {
        this.pmIDInteger = pmIDInteger;
        this.pmIDString = pmIDString;
        this.pmIDFloat = pmIDFloat;
	this.brandName = brandName;
	this.price = price;
   }

    public void ping() {
	TLogger.log("[TestBean] ping()");
    }

}

