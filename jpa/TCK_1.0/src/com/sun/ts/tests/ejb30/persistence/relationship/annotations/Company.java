/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Company.java	1.4 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.relationship.annotations;

import com.sun.ts.lib.util.*;
import com.sun.ts.lib.porting.*;

import java.util.*;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;

/*
 * Company
 */

@Entity
public class Company implements java.io.Serializable {

    private long        companyId;
    private String      name;
    private Address     address;
    private Collection<Team> teams = new java.util.ArrayList<Team>();

    public Company() {
	TestUtil.logTrace("Company no arg constructor");
    }

    public Company(long companyId, String name) {
        this.companyId = companyId;
        this.name = name;
    }

    public Company(long companyId, String name, Address addr) {
        this.companyId = companyId;
        this.name = name;
        this.address = addr;
    }

   // ===========================================================
   // getters and setters for the state fields

    @Id
    public long getCompanyId() {
        return companyId;
    }
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


   // ===========================================================
   // getters and setters for the association fields

    /* Uni-directional Single-Valued One(Company)ToOne(Address) - Company Owner */
    @OneToOne
    @JoinColumn(name="ADDRESS_ID")
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }

    /* Bi-directional One(Company)ToMany(Teams) - Owner Teams */
    @OneToMany(mappedBy="company")
    public Collection<Team> getTeams() {
        return teams;
    }
    public void setTeams(Collection<Team> teams) {
        this.teams = teams;
    }

    
}

