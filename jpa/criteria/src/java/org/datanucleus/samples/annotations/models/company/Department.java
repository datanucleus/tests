/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
 ...
***********************************************************************/
package org.datanucleus.samples.annotations.models.company;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Department in a company, using JPA Annotations.
 * Has a Manager, and a set of Projects being worked on.
 */
@Entity
@Table(name="JPA_AN_DEPARTMENT")
@SequenceGenerator(name="DepartmentGenerator", sequenceName="DEPT_SEQ")
public class Department implements Serializable
{
    @EmbeddedId
    private DepartmentPK primaryKey;

    private String name;

    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="JPA_AN_DEPT_PROJECTS", joinColumns=@JoinColumn(name="DEPT_ID"), 
        inverseJoinColumns=@JoinColumn(name="PROJECT_ID"))
    private Set<Project> projects = new HashSet<Project>();

    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @Column(name="MGR_ID")
    private Manager manager;

    public Department(String name)
    {
        this.name = name;
    }

    public DepartmentPK getPrimaryKey()
    {
        return primaryKey;
    }

    public void setPrimaryKey(DepartmentPK primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setManager(Manager mgr)
    {
        this.manager = mgr;
    }

    public Manager getManager()
    {
        return this.manager;
    }

    public Set<Project> getProjects()
    {
        return projects;
    }
}