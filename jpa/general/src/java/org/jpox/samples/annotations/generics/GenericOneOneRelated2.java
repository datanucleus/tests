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
package org.jpox.samples.annotations.generics;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class GenericOneOneRelated2 implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Basic(optional = false)
    private String name;

    @OneToMany(mappedBy = "owner")
    private Set<GenericOneOneSub2> relatedObjects = new HashSet<GenericOneOneSub2>();

    int age;

    public GenericOneOneRelated2() 
    {
    }

    public GenericOneOneRelated2(String name) 
    {
        this.name = name;
    }

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

    public String getName() 
    {
        return name;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public Set<GenericOneOneSub2> getRelatedObjects() 
    {
        return relatedObjects;
    }
    public void setRelatedObjects(Set<GenericOneOneSub2> relatedObjects) 
    {
        this.relatedObjects = relatedObjects;
    }
}
