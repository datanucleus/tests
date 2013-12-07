/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others.
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
package org.datanucleus.samples.annotations.one_many.bidir;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * An animal on the farm, using JPA annotations.
 * @version $Revision: 1.2 $
 */
@Entity
@Table(name="JPA_AN_ANIMAL")
public class Animal
{
    @Id
    private String name;

    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    private Farm farm;

    public Animal(String name)
    {
        super();
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Farm getFarm()
    {
        return farm;
    }

    public void setFarm(Farm farm)
    {
        this.farm = farm;
    }

    public String toString()
    {
        return name + (farm != null ? (" Farm [" + farm.getName() + "]") : "");
    }
}