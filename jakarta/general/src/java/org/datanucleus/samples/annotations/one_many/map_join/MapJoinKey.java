/**********************************************************************
Copyright (c) 2017 Andy Jefferson and others. All rights reserved.
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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Sample persistable item that is stored as the key in a Map
 */
@Entity
@Table(name="JPA_AN_MAPJOINKEY")
public class MapJoinKey implements Serializable
{
    private static final long serialVersionUID = 140210967289168477L;

    @Id
    long id;

    String name;
    String description;

    public MapJoinKey(long id, String name, String description)
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