/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Team.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.relationship.annotations;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/*
 * Team
 */

@Entity
public class Team implements java.io.Serializable {

    // Instance variables
    private int  teamid;
    private String  name;
    private Company company;

    public Team() {
    }

    public Team(int teamid, String name) {
        this.teamid = teamid;
        this.name = name;
    }


    // ===========================================================
    // getters and setters for the state fields

    @Id
    public int getTeamId() {
        return teamid;
    }
    public void setTeamId(int teamid) {
        this.teamid = teamid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // ===========================================================
    // getters and setters for the association fields


    /* Bi-Directional Many(Teams)ToOne(Company)  - Owner Team */
    @ManyToOne(cascade=CascadeType.REMOVE)
    @JoinColumn(name="COMPANY_COMPANYID")
    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }

}

