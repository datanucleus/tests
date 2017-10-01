/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Project.java	1.1 06/02/21
 */


package com.sun.ts.tests.ejb30.persistence.inheritance.mappedsc.descriptors;

import java.math.BigDecimal;

/*
 * Project
 */

public class Project implements java.io.Serializable  {

    // Instance Variables
    private long       		projId;
    private String     		name;
    private BigDecimal 		budget;
    private FullTimeEmployee 	projectLead;
    
    public Project() {
    }

    public Project(long projId, String name, BigDecimal budget) {
        this(projId, name, budget, (FullTimeEmployee) null);
    }

    public Project(long projId, String name, BigDecimal budget, FullTimeEmployee projectLead) {
        this.projId = projId;
        this.name = name;
        this.budget = budget;
        this.projectLead = projectLead;
    }

   // ===========================================================
   // getters and setters for the state fields

    public long getProjId() {
        return projId;
    }
    public void setProjId(long projId) {
        this.projId = projId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudget() {
        return budget;
    }
    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

   // ===========================================================
   // getters and setters for the association fields

    public FullTimeEmployee getProjectLead() {
        return projectLead;
    }
    public void setProjectLead(FullTimeEmployee projectLead) {
        this.projectLead = projectLead;
    }

}

