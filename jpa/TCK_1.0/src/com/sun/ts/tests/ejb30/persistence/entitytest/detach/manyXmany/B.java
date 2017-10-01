/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)B.java	1.2 06/06/27
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.detach.manyXmany;


import java.util.Collection;
import java.io.Serializable;

import com.sun.ts.tests.ejb30.common.helper.TLogger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

@Entity
@Table(name="BEJB_MXM_BI_BTOB")
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

    @ManyToMany(targetEntity=com.sun.ts.tests.ejb30.persistence.entitytest.detach.manyXmany.A.class, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH}, mappedBy="bCol")
    protected Collection aCol = new java.util.ArrayList();

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

    public B(String id, String name, int value, Collection aCol)
    {
      this.id = id;
      this.name = name;
      this.value = value;
      this.aCol = aCol;
    }

    // ==========================================================
    // Business Methods for Test Cases


    public Collection getACol()
    {
        TLogger.log("getACol");
        return aCol;
    }

    public void setACol(Collection aCol)
    {
        TLogger.log("setACol");
        this.aCol = aCol;
    }

    public String getBId()
    {
	return id;
    }

    public String getBName()
    {
	return name;
    }

    public int getBValue()
    {
	return value;
    }
}
