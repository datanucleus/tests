/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.models.voting;

import java.util.HashSet;
import java.util.Set;

/**
 * @version $Revision: 1.1 $
 */
public class Category
{
    int id; // Used for PK with app id

    String name;
    Set votes = new HashSet();

    public Category()
    {
        super();
    }

    public Category(int id, String name)
    {
        super();
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Set getVotes()
    {
        return votes;
    }

    public void setVotes(Set votes)
    {
        this.votes = votes;
    }
}