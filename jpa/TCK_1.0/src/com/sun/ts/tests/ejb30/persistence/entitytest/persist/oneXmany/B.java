/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)B.java	1.3 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.persist.oneXmany;


import java.util.Collection;
import java.io.Serializable;
import java.util.Vector;
import java.util.Iterator;

import com.sun.ts.tests.ejb30.common.helper.TLogger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;

@Entity
@Table(name="BEJB_1XM_BI_BTOB")
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

    @ManyToOne(targetEntity=com.sun.ts.tests.ejb30.persistence.entitytest.persist.oneXmany.A.class)
    @JoinColumn(
        name="FK_FOR_AEJB_1XM_BI_BTOB", nullable=true)
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
	return  a1;
    }

    public void setA1(A a1) {
	this.a1 = a1;
    }

    public boolean isA()
    {
        TLogger.log("isA");
        if (getA1() != null)
            TLogger.log("Relationship set for A ...");
        else
            TLogger.log("Relationship not set for A ...");
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

    public void setBName(String bName)
    {
	this.name = bName;
    }

    public int getBValue()
    {
	return value;
    }

    public Collection getAInfoFromB()
    {
        Vector v = new Vector();
        if (getA1() != null) {
            Collection bcol = getA1().getBCol();
            Iterator iterator = bcol.iterator();
            while (iterator.hasNext()) {
                B b = (B)iterator.next();
                A a = b.getA1();
                v.add(a);
            }
        }
        return v;
    }

}
