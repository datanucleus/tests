/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)XProject.java	1.3 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.relationship.descriptors;

import com.sun.ts.lib.util.*;
import com.sun.ts.lib.porting.*;

import java.util.*;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

/*
 * XProject
 */

public class XProject implements java.io.Serializable  {

    // Instance Variables
    private long       		xProjId;
    private String     		xName;
    private BigDecimal 		xBudget;
    private XPerson 		xProjectLead;
    private Collection<XPerson>	xPersons = new java.util.ArrayList<XPerson>();

    public XProject() {
	TestUtil.logTrace("XProject no-arg constructor");
    }

    public XProject(long xProjId, String xName, BigDecimal xBudget) {
        this.xProjId = xProjId;
        this.xName = xName;
        this.xBudget = xBudget;
    }

   // ===========================================================
   // getters and setters for the state fields

    public long getXProjId() {
        return xProjId;
    }
    public void setXProjId(long xProjId) {
        this.xProjId = xProjId;
    }

    public String getXName() {
        return xName;
    }
    public void setXName(String xName) {
        this.xName = xName;
    }

    public BigDecimal getXBudget() {
        return xBudget;
    }
    public void setXBudget(BigDecimal xBudget) {
        this.xBudget = xBudget;
    }

   // ===========================================================
   // getters and setters for the association fields

    /* Bi-Directional OneProjectLeadToOnePerson */
    public XPerson getXProjectLead() {
        return xProjectLead;
    }
    public void setXProjectLead(XPerson xProjectLead) {
        this.xProjectLead = xProjectLead;
    }

    /* Bi-Directional ManyPersonsToManyProjects */
    public Collection<XPerson> getXPersons() {
        return xPersons;
    }
    public void setXPersons(Collection<XPerson> xPersons) {
        this.xPersons = xPersons;
    }

}

