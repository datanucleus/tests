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
package org.datanucleus.samples.typeconversion;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Holder for maps of various types to test use of AttributeConverters for key/value.
 */
@Entity
public class MapConverterHolder
{
    @Id
    long id;

    @ElementCollection
    @CollectionTable
    @Convert(attributeName="value", converter=MyType1StringConverter.class)
    Map<String, MyType1> map1 = new HashMap<>();

    @ElementCollection
    @CollectionTable
    @Convert(attributeName="key", converter=MyType2StringConverter.class)
    Map<MyType2, String> map2 = new HashMap<>();

    public MapConverterHolder(long id)
    {
        this.id = id;
    }

    public Map<String, MyType1> getMap1()
    {
        return map1;
    }
    public Map<MyType2, String> getMap2()
    {
        return map2;
    }
}
