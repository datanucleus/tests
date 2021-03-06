/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Person.java	1.8 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.relationship.defaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;
import java.util.Collection;

/*
 * Person
 */

@Entity
public class Person implements java.io.Serializable {

    private int	     	     		personid;
    private String	     		firstName;
    private String	     		lastName;
    private Project          		project;
    private Team       			team;
    private Collection<AnnualReview>	annualReviews = new java.util.ArrayList<AnnualReview>();
    private Collection<Insurance>	carriers = new java.util.ArrayList<Insurance>();
    private Collection<Project>		projects = new java.util.ArrayList<Project>();

    public Person() {
    }

    public Person(int personid, String firstName, String lastName)
    {
        	this.personid = personid;
        	this.firstName = firstName;
        	this.lastName = lastName;
    }

   // ===========================================================
   // getters and setters for the state fields


    @Id
    public int getPersonId() {
        return personid;
    }
    public void setPersonId(int personid) {
        this.personid = personid;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

   // ===========================================================
   // getters and setters for the association fields

    /* Uni-Directional Single-Valued Many(Persons)ToOne(Team) */
    @ManyToOne
    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }

    /* Bi-Directional OneProjectLead(Person)ToOneProject */
    @OneToOne
    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }

    /* Bi-Directional ManyPersonsToManyProjects */
    @ManyToMany(mappedBy="persons")
    public Collection<Project> getProjects() {
        return projects;
    }
    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    /* Uni-Directional Single-Valued OnePersonToManyReviews */
    @OneToMany(cascade=CascadeType.ALL)
    public Collection<AnnualReview> getAnnualReviews() {
        return annualReviews;
    }
    public void setAnnualReviews(Collection<AnnualReview> annualReviews) {
        this.annualReviews = annualReviews;
    }

    /* Uni-Directional Multi-Valued Relationship ManyInsuranceToManyPersons */
    @ManyToMany(cascade=CascadeType.ALL)
    public Collection<Insurance> getInsurance() {
        return carriers;
    }
    public void setInsurance(Collection<Insurance> carriers) {
        this.carriers = carriers;
    }


}

