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
package org.datanucleus.samples.one_many.bidir;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Farm containing animals.
 */
public class Farm
{
    private String name;
    private Set<Animal> animals = new HashSet<>();
    private Animal pet;

    public Farm(String name)
    {
        super();
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Set<Animal> getAnimals()
    {
        return animals;
    }
    
    public void setAnimals(Set<Animal> animals)
    {
        this.animals = new HashSet<>(animals);
    }

    public Animal getPet()
    {
        return pet;
    }

    public void addAnimal(Animal animal)
    {
        animals.add(animal);
    }

    public void removeAnimal(Animal animal)
    {
        animals.remove(animal);
    }

    public void setPet(Animal pet)
    {
        this.pet = pet;
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