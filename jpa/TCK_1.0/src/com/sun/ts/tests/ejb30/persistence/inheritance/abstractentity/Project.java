/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Project.java	1.3 06/12/08
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.abstractentity;

import java.util.*;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;

/*
 * Project
 */

@Entity
public class Project implements java.io.Serializable  {

    // Instance Variables
    private long       		projId;
    private String     		name;
    private BigDecimal 		budget;
    private Employee 		projectLead;
    
    public Project() {
    }

    public Project(long projId, String name, BigDecimal budget) {
        this(projId, name, budget, (Employee) null);
    }

    public Project(long projId, String name, BigDecimal budget, Employee projectLead) {
        this.projId = projId;
        this.name = name;
        this.budget = budget;
        this.projectLead = projectLead;
    }

   // ===========================================================
   // getters and setters for the state fields

    @Id
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

    @OneToOne(mappedBy="project")
    public Employee getProjectLead() {
        return projectLead;
    }
    public void setProjectLead(Employee projectLead) {
        this.projectLead = projectLead;
    }

}

