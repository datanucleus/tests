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

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.datanucleus.api.jdo.annotations.SoftDelete;

/**
 * Car class that supports soft-delete.
 */
@PersistenceCapable
@SoftDelete
public class SDCar
{
    @PrimaryKey
    Long id;

    String make;
    String model;

    public SDCar(long id, String make, String model)
    {
        this.id = id;
        this.make = make;
        this.model = model;
    }

    public Long getId()
    {
        return id;
    }

    public String getMake()
    {
        return make;
    }

    public String getModel()
    {
        return model;
    }
}
