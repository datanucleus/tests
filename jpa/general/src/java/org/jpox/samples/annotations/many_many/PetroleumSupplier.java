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
package org.jpox.samples.annotations.many_many;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Supplier of petroleum.
 *
 * @version $Revision: 1.3 $    
 */
@Entity
@Table(name="JPA_AN_PETROL_SUPPLIER")
public class PetroleumSupplier
{
    @Id
    @Column(name="SUPPLIER_ID")
    protected long id;

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