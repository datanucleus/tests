/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)XTeam.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.relationship.descriptors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/*
 * XTeam
 */

public class XTeam implements java.io.Serializable {

    // Instance variables
    private int  xteamid;
    private String  xname;
    private XCompany xcompany;

    public XTeam() {
    }

    public XTeam(int xteamid, String xname) {
        this.xteamid = xteamid;
        this.xname = xname;
    }


    // ===========================================================
    // getters and setters for the state fields

    public int getXTeamId() {
        return xteamid;
    }
    public void setXTeamId(int xteamid) {
        this.xteamid = xteamid;
    }

    public String getXName() {
        return xname;
    }
    public void setXName(String xname) {
        this.xname = xname;
    }

    // ===========================================================
    // getters and setters for the association fields


    /* Bi-Directional Many(Teams)ToOne(Company)  - Owner Team */
    public XCompany getXCompany() {
        return xcompany;
    }
    public void setXCompany(XCompany xcompany) {
        this.xcompany = xcompany;
    }

}

