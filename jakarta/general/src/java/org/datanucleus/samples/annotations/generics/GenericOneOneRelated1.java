/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.generics;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class GenericOneOneRelated1 implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Set<GenericOneOneSub1> relatedObjects = new HashSet<GenericOneOneSub1>();
    int age;

    public GenericOneOneRelated1() 
    {
    }

    public GenericOneOneRelated1(String name) 
    {
        this.name = name;
    }

    @Id
    public Long getId() 
    {
        return id;
    }
    public void setId(Long id) 
    {
        this.id = id;
    }

    public void setAge(int age)
    {
        this.age = age;
    }
    public int getAge()
    {
        return age;
    }

    @Basic(optional = false)
    public String getName() 
    {
        return name;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    @OneToMany(mappedBy = "owner")
    public Set<GenericOneOneSub1> getRelatedObjects() 
    {
        return relatedObjects;
    }
    public void setRelatedObjects(Set<GenericOneOneSub1> relatedObjects) 
    {
        this.relatedObjects = relatedObjects;
    }
}
