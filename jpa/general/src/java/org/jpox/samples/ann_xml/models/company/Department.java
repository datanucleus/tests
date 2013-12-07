/**********************************************************************
Copyright (c) 2003 Mike Martin (TJDO) and others. All rights reserved.
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
package org.jpox.samples.ann_xml.models.company;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Department in a company.
 * Has a Manager, and a set of Projects being worked on.
 *
 * @version $Revision: 1.1 $
 */
@Entity
public class Department implements Serializable
{
    @Id
    private String name;

    @ManyToOne
    private Manager manager;

    @OneToMany
    private Set<Project> projects = new HashSet();

    public Department(String name)
    {
        this.name = name;
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

    public Set getProjects()
    {
        return projects;
    }

    public void setProjects(Set projects)
    {
        this.projects = projects;
    }
    
    @SuppressWarnings("unchecked")
    public void addProject(Project proj)
    {
        this.projects.add(proj);
    }

    public String toString()
    {
        return name;
    }
}