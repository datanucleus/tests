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
 * Customer for petroleum.
 */
public class PetroleumCustomer
{
    protected long id; // PK for app id

    protected String name=null;

    Collection<PetroleumSupplier> suppliers = new HashSet<>();

    public PetroleumCustomer(String name)
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

    public int getNoOfSuppliers()
    {
        return suppliers.size();
    }

    public Collection<PetroleumSupplier> getSuppliers()
    {
        return suppliers;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void addSupplier(PetroleumSupplier supplier)
    {
        suppliers.add(supplier);
    }

    public void removeSupplier(PetroleumSupplier supplier)
    {
        suppliers.remove(supplier);
    }

    public void removeAllSuppliers()
    {
        suppliers.clear();
    }

    public String toString()
    {
        return "Customer : " + name + " [" + (suppliers != null ? "" + suppliers.size() : "null") + " suppliers]";
    }
}