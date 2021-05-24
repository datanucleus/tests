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

Contributions
    ...
***********************************************************************/
package org.datanucleus.samples.ann_xml.many_many;

import java.util.Collection;
import java.util.HashSet;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

/**
 * Supplier of petroleum.
 *
 * @version $Revision: 1.1 $    
 */
@Entity
public class PetroleumSupplier
{
    @Id
    protected long id;

    @Basic
    protected String name=null;

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    Collection<PetroleumCustomer> customers = new HashSet<PetroleumCustomer>();

    public PetroleumSupplier(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String  getName()
    {
        return name;
    }

    public Collection<PetroleumCustomer> getCustomers()
    {
        return customers;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return "Supplier : " + name + " [" + customers.size() + " customers]";
    }
}