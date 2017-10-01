/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)A.java	1.3 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.remove.oneXone;

import java.io.Serializable;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.OneToOne;

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

    public A(String id, String name, int value, B b1)
    {
      this.id = id;
      this.name = name;
      this.value = value;
      this.b1 = b1;
    }


    // ===========================================================
    // relationship fields

    @OneToOne(targetEntity=com.sun.ts.tests.ejb30.persistence.entitytest.remove.oneXone.B.class, mappedBy="a1")
    protected B b1;

    // =======================================================================
    // Business methods for test cases

    public B getB1() {
        return b1;
    }

    public boolean isB1()
    {
        TLogger.log("isB");
        if (getB1() != null)
            TLogger.log("Relationship to B is not null...");
        else
            TLogger.log("Relationship to B is null...");
        return getB1() != null;
    }

    public B getB1Info()
    {
        TLogger.log("getBInfo");
          if (isB1()) {
            B b1 = getB1();
            return b1;
          } else 
            return null;
    }

    public String getAId()
    {   
        return id;
    }

    public String getAName()
    {
        return name;
    }

    public int getAValue()
    {
        return value;
    }

}
