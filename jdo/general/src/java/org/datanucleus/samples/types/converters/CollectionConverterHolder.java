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
package org.datanucleus.samples.types.converters;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Convert;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Class that has Collection fields persisted using converters.
 */
@PersistenceCapable
public class CollectionConverterHolder
{
    @PrimaryKey
    long id;

    String name;

    /** Collection field which is stored as a String in the owner table. */
    @Convert(CollectionToStringConverter.class)
    @Column(name="CONVERTED_COLLECTION")
    Collection<String> convertedCollection = new HashSet<String>();

    /** Collection field which is stored in a join table, with the element converted to a String. */
    @Element(converter=MyType1ToStringConverter.class)
    @Join
    Collection<MyType1> convertedElementCollection = new HashSet<MyType1>();

    public CollectionConverterHolder(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public Collection<String> getConvertedCollection()
    {
        return convertedCollection;
    }

    public Collection<MyType1> getConvertedElementCollection()
    {
        return convertedElementCollection;
    }
}
