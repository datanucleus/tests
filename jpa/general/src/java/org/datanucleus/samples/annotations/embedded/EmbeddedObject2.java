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

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;

/**
 * Embedded 1-1 object, used by EmbeddedOwner2.
 */
@Embeddable
public class EmbeddedObject2
{
    String name;

    @ElementCollection
    @CollectionTable(name="EMB_OBJECT_STRINGS", joinColumns=@JoinColumn(name="EMB_OWNER_ID"))
    @JoinColumn(name="EMB_OBJECT_STRING")
    Set<String> stringSet = new HashSet<>();

    public EmbeddedObject2(String name)
    {
        this.name = name;
    }
    public Set<String> getStringSet()
    {
        return stringSet;
    }
    public String getName()
    {
        return name;
    }
}
