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
package org.datanucleus.samples.softdelete;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.datanucleus.api.jdo.annotations.SoftDelete;

/**
 * Person class that supports soft-delete.
 */
@PersistenceCapable
@SoftDelete(indexed = "true", columns=@Column(name = "DELETED_RECORD"))
public class SDPerson
{
    @PrimaryKey
    Long id;

    String name;

    SDCar car;

    Set<SDAddress> addresses = new HashSet<>();

    public SDPerson(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public SDCar getCar()
    {
        return car;
    }
    public void setCar(SDCar car)
    {
        this.car = car;
    }

    public Set<SDAddress> getAddresses()
    {
        return addresses;
    }
}
