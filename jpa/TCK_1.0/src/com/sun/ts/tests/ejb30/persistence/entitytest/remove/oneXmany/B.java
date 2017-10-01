/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)B.java	1.4 07/02/26
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.remove.oneXmany;


import java.util.Collection;
import java.io.Serializable;

import com.sun.ts.tests.ejb30.common.helper.TLogger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

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

    @ManyToOne(targetEntity=com.sun.ts.tests.ejb30.persistence.entitytest.remove.oneXmany.A.class)
    @JoinColumn(
        name="FK_FOR_AEJB_1XM_BI_BTOB")
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

    public boolean isA()
    {
        TLogger.log("isA");
        if (getA1() != null)
            TLogger.log("Relationship to A is not null...");
        else
            TLogger.log("Relationship to A is null...");
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

    public int getBValue()
    {
	return value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((id == null) ? 0 : id.hashCode());
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        result = PRIME * result + value;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final B other = (B) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (value != other.value)
            return false;
        return true;
    }
}
