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

import javax.persistence.Embeddable;

/**
 * Sample persistable item that is stored embedded as the value in a Map
 */
@Embeddable
public class MapJoinEmbeddedValue implements Serializable
{
    private static final long serialVersionUID = 140210967289168477L;

    String name;
    String description;

    public MapJoinEmbeddedValue(String name, String description)
    {
        this.name = name;
        this.description = description;
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