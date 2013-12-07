/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.annotations.datastoreidentity;

import javax.persistence.Entity;
import javax.persistence.GenerationType;

import org.datanucleus.api.jpa.annotations.DatastoreIdentity;

/**
 * Simple example of an entity with datastore id.
 */
@Entity
@DatastoreIdentity(generationType=GenerationType.TABLE)
public class DSIDHolder
{
    String name;

    public DSIDHolder(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
