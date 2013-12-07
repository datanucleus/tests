/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.annotations.one_many.bidir;

import java.util.ArrayList;
import java.util.Iterator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * A Farm containing animals, using JPA annotations.
 * This demonstrates an ordered FK list (bidirectional).
 * 
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name="JPA_AN_FARM")
public class Farm
{
    @Id
    private String name;

    @OneToMany(mappedBy="farm", cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("name ASC")
    private ArrayList<Animal> animals = new ArrayList<Animal>();

    public Farm(String name)
    {
        super();
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public ArrayList<Animal> getAnimals()
    {
        return animals;
    }

    public String toString()
    {
        StringBuffer str=new StringBuffer(name);
        
        str.append(" Animals=[");
        Iterator iter = animals.iterator();
        while (iter.hasNext())
        {
            str.append(iter.next().toString());
            if (iter.hasNext())
            {
                str.append(", ");
            }
        }
        str.append("]");
        return str.toString();
    }
}