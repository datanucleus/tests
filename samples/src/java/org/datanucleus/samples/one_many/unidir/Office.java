/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.one_many.unidir;

import java.util.Collection;
import java.util.HashSet;

/**
 * Representation of an office in a company.
 * Contains a collection of computers, where the computers use "subclass-table"
 * inheritance strategy, and so is used as a test for that relation.
 */
public class Office
{
    String name;
    Collection<Computer> computers = new HashSet<>();

    public Office(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void addComputer(Computer comp)
    {
        computers.add(comp);
    }

    public void removeComputer(Computer comp)
    {
        computers.remove(comp);
    }

    public int getNumberOfComputers()
    {
        return computers.size();
    }

    public boolean hasComputer(Computer comp)
    {
        return computers.contains(comp);
    }

    public Collection<Computer> getComputers()
    {
        return computers;
    }
}