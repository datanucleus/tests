/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)A.java	1.3 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.remove.basic;

import java.io.Serializable;
import com.sun.ts.tests.ejb30.common.helper.TLogger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Basic;

@Entity
@Table(name="AEJB_1X1_BI_BTOB")
public class A implements java.io.Serializable {


    // ===========================================================
    // instance variables 

    @Id
    protected String id;

    @Basic
    protected String name;

    @Basic
    protected int value;


    // ===========================================================
    // constructors

    public A() {
        TLogger.log("Entity A no arg constructor");
    }

    public A(String id, String name, int value)
    {
      this.id = id;
      this.name = name;
      this.value = value;
    }

}
