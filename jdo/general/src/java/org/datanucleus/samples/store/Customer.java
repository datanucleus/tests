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
package org.datanucleus.samples.store;

import java.util.Collection;
import java.util.HashSet;

/**
 * Definition of a Customer that sells products.
 *
 * @version $Revision: 1.1 $    
 **/
public class Customer
{
    /** 
     * Name of the Customer
     **/
    protected String name=null;

    /**
     * Suppliers for the Customer.
     **/
    Collection suppliers = new HashSet();

    /** Default constructor. */
    protected Customer()
    {
    }

    /** Constructor. */
    public Customer(String name)
    {
        this.name = name;
    }

    // -------------------------------- Accessors ------------------------------
    /** Accessor for the name of the Customer.
     * @return Customer name */
    public String  getName()
    {
        return name;
    }

    /**
     * Accessor for the number of suppliers.
     * @return No of suppliers
     **/
    public int getNoOfSuppliers()
    {
        return suppliers.size();
    }

    /** Accessor for the suppliers.
     * @return The suppliers. */
    public Collection getSuppliers()
    {
        return suppliers;
    }

    // -------------------------------- Mutators -------------------------------
    /** Mutator for the name of the Customer.
     * @param   name    Name of the Customer.
     **/
    public void setName(String name)
    {
        this.name = name;
    }

    /** Method to add a Supplier for the Customer.
     * @param   supplier  Supplier to add. */
    public void addSupplier(Supplier supplier)
    {
        suppliers.add(supplier);
    }

    /** Method to remove a Supplier from the Customer.
     * @param   supplier  Supplier to remove. */
    public void removeSupplier(Supplier supplier)
    {
        suppliers.remove(supplier);
    }

    /**
     * Method to remove all suppliers.
     **/
    public void removeAllSuppliers()
    {
        suppliers.clear();
    }

    public String toString()
    {
        return "Customer : " + name + " [" + suppliers.size() + " suppliers]";
    }
}