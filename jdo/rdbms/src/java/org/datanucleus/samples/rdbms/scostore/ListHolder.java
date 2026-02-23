/**********************************************************************
Copyright (c) 2026 Contributors. All rights reserved.
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
package org.datanucleus.samples.rdbms.scostore;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;

/**
 * Owner class with a join-table List of elements, for testing bulk shift ordering.
 */
@PersistenceCapable
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY)
public class ListHolder
{
    String name;

    @Join
    List<ListElement> items = new ArrayList<>();

    public ListHolder(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public List<ListElement> getItems()
    {
        return items;
    }
}
