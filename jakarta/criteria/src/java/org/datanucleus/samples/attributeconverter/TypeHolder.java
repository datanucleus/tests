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
package org.datanucleus.samples.attributeconverter;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Convert;

/**
 * Sample class holding another type to be converted for persistence.
 */
@Entity
public class TypeHolder
{
    @Id
    long id;

    String name;

    @Basic
    @Column(name="MY_DETAILS")
    @Convert(converter=ComplicatedTypeConverter.class)
    ComplicatedType details;

    public TypeHolder(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public void setDetails(ComplicatedType det)
    {
        this.details = det;
    }

    public ComplicatedType getDetails()
    {
        return details;
    }
}
