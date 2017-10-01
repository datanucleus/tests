/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)A.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.persist.manyXone;

import java.io.Serializable;
import com.sun.ts.tests.ejb30.common.helper.TLogger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Basic;

@Entity
@Table(name="AEJB_MX1_UNI_BTOB")
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

    // ===========================================================
    // uni-directional relationship fields
    // =======================================================================
    // Business methods for test cases

    public String getAId()
    {   
        return id;
    }

    public String getAName()
    {
        return name;
    }
    public void setAName(String name)
    {
        this.name = name;
    }

    public int getAValue()
    {
        return value;
    }
}
