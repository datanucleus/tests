/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.one_many.collection;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Sample element stored in a collection/list using a foreign key.
 */
@Entity
public class PCFKListElement implements Serializable
{
    private static final long serialVersionUID = -9193762304239228080L;

    @Id
    long id;

    String name;

    public PCFKListElement(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return this.name;
    }
}