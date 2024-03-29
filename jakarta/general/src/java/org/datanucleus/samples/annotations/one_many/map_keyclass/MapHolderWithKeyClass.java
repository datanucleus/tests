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
package org.datanucleus.samples.annotations.one_many.map_keyclass;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyClass;
import jakarta.persistence.OneToMany;

/**
 * Sample class using @MapKeyClass and no generics for map types
 */
@Entity
public class MapHolderWithKeyClass
{
    @Id
    long id;

    @OneToMany(targetEntity=MapKeyClassTarget.class)
    @MapKeyClass(String.class)
    Map map = new HashMap();

    public MapHolderWithKeyClass(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }
    
    public Map getMap()
    {
        return map;
    }
}
