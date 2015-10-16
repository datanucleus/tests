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
package org.jpox.samples.typeconversion;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Holder for collections of various types to test use of AttributeConverters for element.
 */
@Entity
public class CollectionConverterHolder
{
    @Id
    long id;

    @ElementCollection
    @CollectionTable
    @Convert(converter=MyType1StringConverter.class)
    Set<MyType1> set1 = new HashSet<MyType1>();

    @ElementCollection
    @CollectionTable
    @Convert(converter=SetMyType1StringConverter.class)
    Set<MyType1> set2 = new HashSet<MyType1>();

    public CollectionConverterHolder(long id)
    {
        this.id = id;
    }

    public Set<MyType1> getSet1()
    {
        return set1;
    }

    public Set<MyType1> getSet2()
    {
        return set2;
    }
}
