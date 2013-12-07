/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.one_many.unidir_notnullable;

import java.util.Collection;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Column;

/**
 * Owner of a 1-N unidir relation.
 */
@PersistenceCapable(detachable="true")
public class OwnerOneManyUni3
{
    @PrimaryKey
    private long id;

    private String name;

    @Element(columns=@Column(name="OWNER_ID", allowsNull="false"))
    Collection<ChildOneManyUni3> children;

    public long getId() 
    {
        return id;
    }

    public void setId(long id) 
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

    public Collection<ChildOneManyUni3> getChildren() 
    {
        return children;
    }

    public void setChildren(Collection<ChildOneManyUni3> children) 
    {
        this.children = children;
    }
}