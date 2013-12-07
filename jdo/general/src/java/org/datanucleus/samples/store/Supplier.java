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
 * Definition of an Supplier of Products to a Customer.
 *
 * @version $Revision: 1.1 $    
 **/
public class Supplier
{
    /**
     * Name of the Supplier
     **/
    protected String name=null;

    /** 
     * Customers for the supplier
     **/
    Collection customers=new HashSet();

    /** Default constructor. */
    protected Supplier()
    {
    }

    /** Constructor. */
    public Supplier(String name)
    {
        this.name = name;
    }

    // -------------------------------- Accessors ------------------------------
    /**
     * Accessor for the name of the Supplier.
     * @return Supplier name
     */
    public String  getName()
    {
        return name;
    }

    /**
     * Accessor for the number of customers.
     * @return No of customers.
     **/
    public int getNoOfCustomers()
    {
        return customers.size();
    }

    /** Accessor for the Customers.
     * @return The customers. */
    public Collection getCustomers()
    {
        return customers;
    }

    // -------------------------------- Mutators -------------------------------
    /** Mutator for the name of the Supplier.
     * @param   name    Name of the Supplier.
     **/
    public void setName(String name)
    {
        this.name = name;
    }

    /** Method to add a Customer to the Supplier.
     * @param   customer  Customer to add. */
    public void addCustomer(Customer customer)
    {
        customers.add(customer);
    }

    /** Method to remove a Customer from the Supplier
     * @param   customer  Customer to remove. */
    public void removeCustomer(Customer customer)
    {
        customers.remove(customer);
    }

    /**
     * Method to remove all customers.
     **/
    public void removeAllCustomers()
    {
        customers.clear();
    }

    public String toString()
    {
        return "Supplier : " + name + " [" + customers.size() + " customers]";
    }
}