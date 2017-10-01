/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)B.java	1.1 06/02/24
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.detach.oneXone;


import java.io.Serializable;
import com.sun.ts.tests.ejb30.common.helper.TLogger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;

@Entity
@Table(name="BEJB_1X1_BI_BTOB")
public class B implements java.io.Serializable {


    // ===========================================================
    // instance variables 
    @Id
    protected String id;

    @Basic
    protected String name;

    @Basic
    protected int value;

    // ===========================================================
    // relationship fields

    @OneToOne(targetEntity=com.sun.ts.tests.ejb30.persistence.entitytest.detach.oneXone.A.class, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})

    @JoinColumn(name="FK_FOR_AEJB_1X1_BI_BTOB")
    protected A a1;

    // ===========================================================
    // constructors

    public B() {
        TLogger.log("Entity B no arg constructor");
    }

    public B(String id, String name, int value)
    {
      this.id = id;
      this.name = name;
      this.value = value;
    }

    public B(String id, String name, int value, A a1)
    {
      this.id = id;
      this.name = name;
      this.value = value;
      this.a1 = a1;
    }


    // ==========================================================
    // Business Methods for Test Cases


    public A getA1() {
	return a1;
    }

    public void setA1(A a1) {
	this.a1 = a1;
    }

    public boolean isA()
    {
        TLogger.log("isA");
        if (getA1() != null)
            TLogger.log("Relationship for A is not null...");
        else
            TLogger.log("Relationship for A is null...");
        return getA1() != null;
    }

    public A getA1Info()
    {
        TLogger.log("getA1Info");
          if (isA()) {
            A a1 = getA1();
            return a1;
          }
          else
            return null;
    }

    public String getBId()
    {
	return id;
    }

    public String getBName()
    {
	return name;
    }

    public void setBName(String name)
    {
	this.name = name;
    }

    public int getBValue()
    {
	return value;
    }
}
