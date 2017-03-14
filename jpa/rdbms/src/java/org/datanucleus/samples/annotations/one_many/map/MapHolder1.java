/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.one_many.map;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Holder of a Map field.
 */
@Entity
@Table(name="JPA_AN_MAPHOLDER1")
public class MapHolder1
{
    @Id
    @Column(name="JPA_AN_MAPHOLDER1_ID")
    long id;

    @ElementCollection
    @CollectionTable(name="JPA_AN_MAPHOLDER1_PROPS", joinColumns=@JoinColumn(name="MAPHOLDER1_ID"))
    @MapKeyColumn(name="PROP_NAME")
    @Column(name="PROP_VALUE")
    Map<String, String> properties = new HashMap<String, String>();

    @ElementCollection
    @CollectionTable
    Map<String, String> properties2 = new HashMap<String, String>();

    // Example of Map<Entity,Entity> with default namings
    @OneToMany
    @JoinTable
    Map<MapHolder1Key, MapHolder1Value> map3;

    // Example of Map<Entity,Entity> with specified namings
    @OneToMany
    @JoinTable(name="JPA_AN_MAPHOLDER1_MAP4", joinColumns=@JoinColumn(name="MAP4_VALUE"), inverseJoinColumns=@JoinColumn(name="MAP4_OWNER_ID"))
    @MapKeyJoinColumn(name="MAP4_KEY")
    Map<MapHolder1Key, MapHolder1Value> map4;

    public MapHolder1(long id)
    {
        this.id = id;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }
    public Map<String, String> getProperties2()
    {
        return properties2;
    }
}