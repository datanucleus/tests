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

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Convert;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Value;

/**
 * Class that has Map fields persisted using converters.
 */
@PersistenceCapable
public class MapConverterHolder
{
    @PrimaryKey
    long id;

    String name;

    /** Map field which is stored as a String in the owner table. */
    @Convert(MapToStringConverter.class)
    @Column(name="CONVERTED_MAP")
    Map<String, String> convertedMap = new HashMap<String, String>();

    /** Map field which is stored in a join table, with the value converted to a String. */
    @Value(converter=MyType1ToStringConverter.class)
    @Join
    Map<String, MyType1> convertedValueMap = new HashMap<String, MyType1>();

    public MapConverterHolder(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public Map<String, String> getConvertedMap()
    {
        return convertedMap;
    }

    public Map<String, MyType1> getConvertedValueMap()
    {
        return convertedValueMap;
    }
}
