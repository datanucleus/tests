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
package org.datanucleus.samples.annotations.one_many.map_join;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Sample persistable item that is stored as the value in a Map, with the key to the map being a field in the superclass.
 */
@Entity
@Table(name="JPA_AN_MAPJOINVALUE")
public class MapJoinValue implements Serializable
{
    private static final long serialVersionUID = 140210967289168477L;

    @Id
    long id;

    String name;
    String description;

    public MapJoinValue()
    {
    }
    public MapJoinValue(long id, String name, String description)
    {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}