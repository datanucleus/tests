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

Contributors:
    ...
**********************************************************************/
package org.datanucleus.samples.annotations.models.company;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Project being worked in a Department, using JPA Annotations.
 */
@Entity
@Table(name="JPA_AN_PROJECT")
public class Project
{
    @Id
    String name;

    @Basic
    long budget;

    public Project(String name, long budget)
    {
        this.name = name;
        this.budget = budget;
    }

    public long getBudget()
    {
        return budget;
    }

    public void setBudget(long budget)
    {
        this.budget = budget;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}