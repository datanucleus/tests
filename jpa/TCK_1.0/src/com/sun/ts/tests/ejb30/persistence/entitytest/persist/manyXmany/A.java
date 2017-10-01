/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)A.java	1.6 06/09/13
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.persist.manyXmany;

import java.util.Collection;
import java.io.Serializable;

import com.sun.ts.tests.ejb30.common.helper.TLogger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;

@Entity
@Table(name="AEJB_MXM_BI_BTOB")
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

    public A(String id, String name, int value, Collection bCol)
    {
      this.id = id;
      this.name = name;
      this.value = value;
      this.bCol = bCol;
    }

    // ===========================================================
    // relationship fields

    @ManyToMany(targetEntity=com.sun.ts.tests.ejb30.persistence.entitytest.persist.manyXmany.B.class, cascade=CascadeType.PERSIST)
    @JoinTable(name="FKEYS_MXM_BI_BTOB",
    	joinColumns=
        	@JoinColumn(
                	name="FK_FOR_AEJB_MXM_BI_BTOB", referencedColumnName="ID"),
        inverseJoinColumns=
        	@JoinColumn(
                	name="FK_FOR_BEJB_MXM_BI_BTOB", referencedColumnName="ID")
    )
    protected Collection bCol = new java.util.ArrayList();

    // =======================================================================
    // Business methods for test cases


    public Collection getBCol()
    {
        TLogger.log("getBCol");
	return bCol;
    }

    public void setBCol(Collection bCol)
    {
        TLogger.log("setBCol");
        this.bCol = bCol;
    }

    public String getAId()
    {   
        return id;
    }

    public String getAName()
    {
        return name;
    }

    public void setAName(String aName)
    {
        this.name = aName;
    }

    public int getAValue()
    {
        return value;
    }


}
