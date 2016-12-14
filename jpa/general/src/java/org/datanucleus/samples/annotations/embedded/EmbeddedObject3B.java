/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.embedded;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Embeddable
public class EmbeddedObject3B
{
    String nameB;

    @OneToMany
    @JoinTable(name = "C_AS")
    private Set<EmbeddedOwner3> owners = new HashSet<>();

    public void setNameB(String name)
    {
        this.nameB = name;
    }
    public String getNameB()
    {
        return nameB;
    }

    public Set<EmbeddedOwner3> getOwners()
    {
        return owners;
    }
    public void setOwners(Set<EmbeddedOwner3> owners)
    {
        this.owners = owners;
    }
}
