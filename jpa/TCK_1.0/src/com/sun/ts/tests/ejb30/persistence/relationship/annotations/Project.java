/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Project.java	1.5 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.relationship.annotations;

import java.util.*;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;


/*
 * Project
 */

@Entity
public class Project implements java.io.Serializable  {

    // Instance Variables
    private long       		projId;
    private String     		name;
    private BigDecimal 		budget;
    private Person 		projectLead;
    private Collection<Person>	persons = new java.util.ArrayList<Person>();

    public Project() {
    }

    public Project(long projId, String name, BigDecimal budget) {
        this.projId = projId;
        this.name = name;
        this.budget = budget;
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

    /* Bi-Directional OneProjectLeadToOnePerson */
    @OneToOne(mappedBy="project")
    public Person getProjectLead() {
        return projectLead;
    }
    public void setProjectLead(Person projectLead) {
        this.projectLead = projectLead;
    }

    /* Bi-Directional ManyProjectsToManyPersons */
    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="PROJECT_PERSON",
    	joinColumns=
       		@JoinColumn(
                	name="PROJECTS_PROJID", referencedColumnName="PROJID"),
        inverseJoinColumns=
        	@JoinColumn(
                	name="PERSONS_PERSONID", referencedColumnName="PERSONID")
    )
    public Collection<Person> getPersons() {
        return persons;
    }
    public void setPersons(Collection<Person> persons) {
        this.persons = persons;
    }

}

