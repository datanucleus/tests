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
import java.util.Set;

/**
 * Definition of an Inventory of Products.
 **/
public class Inventory
{
    Set<Product> products=null;

    public Collection<Product> getProducts()
    {
        return products;
    }

    public int getNoOfProducts()
    {
        if (products == null)
        {
            return 0;
        }
        return products.size();
    }

    public void addProduct(Product product)
    {
        if (products == null)
        {
            products = new HashSet<>();
        }
        products.add(product);
    }

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
