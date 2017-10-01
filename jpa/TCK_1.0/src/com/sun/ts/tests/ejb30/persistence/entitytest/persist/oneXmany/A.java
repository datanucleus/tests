/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)A.java	1.4 06/06/27
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
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;

@Entity
@Table(name="AEJB_1XM_BI_BTOB")
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

    @OneToMany(targetEntity=com.sun.ts.tests.ejb30.persistence.entitytest.persist.oneXmany.B.class, cascade=CascadeType.PERSIST, mappedBy="a1")
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

    public void setAName(String name)
    {
        this.name = name;
    }

    public int getAValue()
    {
        return value;
    }

    public Collection getBInfoFromA()
    {
        TLogger.log("getBInfoFromA");
        Vector v = new Vector();
        if (getBCol().size() != 0 ) {
            Collection bcol = getBCol();
            Iterator iterator = bcol.iterator();
            while (iterator.hasNext()) {
                B b = (B)iterator.next();
                v.add(b);
            }
        }
        return v;
    }


}
