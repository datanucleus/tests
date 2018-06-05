/**********************************************************************
Copyright (c) 2018 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.jpa.criteria;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

@MappedSuperclass
public abstract class AbstractEntity105
{
    @Id
    long id;
    
    String name;

    @OneToMany
    Set<OtherEntity105> others = new HashSet<>();

    public AbstractEntity105(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Set<OtherEntity105> getOthers()
    {
        return others;
    }
}