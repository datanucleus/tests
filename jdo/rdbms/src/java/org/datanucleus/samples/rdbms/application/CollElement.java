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
package org.datanucleus.samples.rdbms.application;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Element, with 2 remote FKs from owning collections.
 */
@PersistenceCapable(table="COLL_ELEMENT")
public class CollElement
{
    @PrimaryKey
    @Column(name="COLL_ELEMENT_ID")
    Long id;

    @Column(name="COLL_ELEMENT_NAME")
    String name;

    public CollElement(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Long getId()
    {
        return id;
    }
}
