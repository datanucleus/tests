/**********************************************************************
Copyright (c) 01-Nov-2003 Andy Jefferson and others.
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
package org.datanucleus.samples.store;

import java.util.Collection;
import java.util.HashSet;

/**
 * Definition of an Inventory of Products.
 *
 * @version $Revision: 1.1 $    
 **/
public class Inventory
{
    /**
     * Products in the Inventory.
     **/
    HashSet products=null;

    /** Default constructor. */
    public Inventory()
    {
    }

    // -------------------------------- Accessors ------------------------------
    /**
     * Accessor for the products from the Inventory.
     * @return  The products.
     **/
    public Collection getProducts()
    {
        return products;
    }

    // -------------------------------- Mutators -------------------------------

    /**
     * Method to return the number of products in the Inventory.
     * @return No of products.
     **/
    public int getNoOfProducts()
    {
        if (products == null)
        {
            return 0;
        }
        return products.size();
    }

    /**
     * Method to add a product to the Inventory.
     * @param product Product to add.
     **/
    public void addProduct(Product product)
    {
        if (products == null)
        {
            products = new HashSet();
        }
        products.add(product);
    }

    /**
     * Method to remove a product from the Inventory.
     * @param product Product to remove.
     **/
    public void removeProduct(Product product)
    {
        if (products == null)
        {
            return;
        }
        products.remove(product);
    }

    public String toString()
    {
        if (products == null)
        {
            return "Inventory : [0 products]";
        }

        return "Inventory : [" + products.size() + " products]";
    }
}
