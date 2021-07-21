/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.many_many;

import java.util.Collection;
import java.util.HashSet;

/**
 * Supplier of petroleum.
 */
public class PetroleumSupplier
{
    protected long id; // PK for app id

    protected String name=null;

    Collection<PetroleumCustomer> customers=new HashSet<>();

    public PetroleumSupplier(String name)
    {
        this.name = name;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public String  getName()
    {
        return name;
    }

    public int getNoOfCustomers()
    {
        return customers.size();
    }

    public Collection<PetroleumCustomer> getCustomers()
    {
        return customers;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void addCustomer(PetroleumCustomer customer)
    {
        customers.add(customer);
    }

    public void removeCustomer(PetroleumCustomer customer)
    {
        customers.remove(customer);
    }

    public void removeAllCustomers()
    {
        customers.clear();
    }

    public String toString()
    {
        return "Supplier : " + name + " [" + customers.size() + " customers]";
    }
}