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

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Holder of maps stored by join table.
 */
@Entity
@Table(name="JPA_AN_MAPJOINHOLDER")
public class MapJoinHolder
{
    @Id
    private long id;

    private String name;

    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="JPA_AN_MAPJOINHOLDER_MAP")
    private Map<String, MapJoinValue> map;

    @ElementCollection
    @CollectionTable(name="JPA_AN_MAPJOINHOLDER_MAP2")
    private Map<Integer, String> map2;

    @ElementCollection
    @CollectionTable(name="JPA_AN_MAPJOINHOLDER_MAP3")
    private Map<String, MapJoinEmbeddedValue> map3;

    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="JPA_AN_MAPJOINHOLDER_MAP4")
    private Map<MapJoinKey, MapJoinValue> map4;

    public MapJoinHolder()
    {
    }

    public MapJoinHolder(long id)
    {
        this.id = id;
    }

    public MapJoinHolder(String name)
    {
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Map<String, MapJoinValue> getMap()
    {
        if (map == null)
        {
            map = new HashMap<String, MapJoinValue>();
        }
        return map;
    }

    public Map<Integer, String> getMap2()
    {
        if (map2 == null)
        {
            map2 = new HashMap<Integer, String>();
        }
        return map2;
    }

    public Map<String, MapJoinEmbeddedValue> getMap3()
    {
        if (map3 == null)
        {
            map3 = new HashMap<String, MapJoinEmbeddedValue>();
        }
        return map3;
    }

    public Map<MapJoinKey, MapJoinValue> getMap4()
    {
        if (map4 == null)
        {
            map4 = new HashMap<MapJoinKey, MapJoinValue>();
        }
        return map4;
    }
}