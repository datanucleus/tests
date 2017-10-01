/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)XCompany.java	1.5 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.relationship.descriptors;

import com.sun.ts.lib.util.*;
import com.sun.ts.lib.porting.*;

import java.util.*;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/*
 * XCompany
 */

public class XCompany implements java.io.Serializable {

    private long        xCompanyId;
    private String      xName;
    private XAddress    xAddress;
    private Collection<XTeam> xTeams = new java.util.ArrayList<XTeam>();

    public XCompany() {
	TestUtil.logTrace("Company no arg constructor");
    }

    public XCompany(long xCompanyId, String xName) {
        this.xCompanyId = xCompanyId;
        this.xName = xName;
    }

    public XCompany(long xCompanyId, String xName, XAddress xAddress) {
        this.xCompanyId = xCompanyId;
        this.xName = xName;
        this.xAddress = xAddress;
    }

   // ===========================================================
   // getters and setters for the state fields

    public long getXCompanyId() {
        return xCompanyId;
    }
    public void setXCompanyId(long xCompanyId) {
        this.xCompanyId = xCompanyId;
    }

    public String getXName() {
        return xName;
    }
    public void setXName(String xName) {
        this.xName = xName;
    }


   // ===========================================================
   // getters and setters for the association fields

    /* Uni-directional Single-Valued One(Company)ToOne(Address) - Company Owner */
    public XAddress getXAddress() {
        return xAddress;
    }
    public void setXAddress(XAddress xAddress) {
        this.xAddress = xAddress;
    }

    /* Bi-directional One(Company)ToMany(Teams) - Owner Teams */
    public Collection<XTeam> getXTeams() {
        return xTeams;
    }
    public void setXTeams(Collection<XTeam> xTeams) {
        this.xTeams = xTeams;
    }
    
}

