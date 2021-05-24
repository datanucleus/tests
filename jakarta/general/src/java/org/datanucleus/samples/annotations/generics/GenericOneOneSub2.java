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

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class GenericOneOneSub2 extends GenericOneOneRoot2<GenericOneOneRelated2>
{
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Basic(optional = false)
    private String name;

    private GenericEnumType type;

    int age;

    public GenericOneOneSub2() 
    {
    }

    public GenericOneOneSub2(String name) 
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

    public String getName() 
    {
        return name;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public void setType(GenericEnumType type)
    {
        this.type = type;
    }
    public GenericEnumType getType()
    {
        return type;
    }

    public void setAge(int age)
    {
        this.age = age;
    }
    public int getAge()
    {
        return age;
    }
}