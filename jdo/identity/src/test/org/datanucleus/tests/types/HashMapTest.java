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
package org.datanucleus.tests.types;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.container.ContainerItem;
import org.jpox.samples.types.container.ContainerItemChild;
import org.jpox.samples.types.hashmap.HashMap1;
import org.jpox.samples.types.hashmap.HashMap2;
import org.jpox.samples.types.hashmap.HashMap2Item;
import org.jpox.samples.types.hashmap.HashMap2ItemChild;
import org.jpox.samples.types.hashmap.HashMap3;
import org.jpox.samples.types.hashmap.HashMap3Child;
 
/**
 * Test case to test HashMap SCO.
 * Tests should include 
 * <ul>
 * <li>JoinTable</li>
 * <li>ForeignKey</li>
 * </ul>
 */
public class HashMapTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public HashMapTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        HashMap2Item.class,
                        HashMap2ItemChild.class,
                        HashMap1.class,
                        HashMap2.class,
                        HashMap3Child.class,
                        HashMap3.class,
                        ContainerItemChild.class,
                        ContainerItem.class
                    });
            initialised = true;
        }
    }

    /**
     * Test case to check the addition of a Map of elements.
     **/
    public void testNormalPutItems()
    throws Exception
    {
        try
        {
            SCOMapTests.checkPutItems(pmf,
                HashMap1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(HashMap1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the addition of a Map of null values.
     **/
    public void testNormalPutNullValues()
    throws Exception
    {
        try
        {
            SCOMapTests.checkPutNullValues(pmf,
                HashMap1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(HashMap1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the removal of an item from the Map.
     **/
    public void testNormalRemoveItem()
    throws Exception
    {
        try
        {
            SCOMapTests.checkRemove(pmf,
                HashMap1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(HashMap1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the use of entrySet in the Map.
     **/
    public void testNormalEntrySet()
    throws Exception
    {
        try
        {
            SCOMapTests.checkEntrySet(pmf,
                HashMap1.class,
                ContainerItem.class,
                String.class);
        }
        finally
        {
            clean(HashMap1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the use of keySet in the Map.
     **/
    public void testNormalKeySet()
    throws Exception
    {
        try
        {
            SCOMapTests.checkKeySet(pmf,
                HashMap1.class,
                ContainerItem.class,
                String.class);
        }
        finally
        {
            clean(HashMap1.class);
            clean(ContainerItem.class);
        }
    }    

    /**
     * Test case to check the use of values in the Map.
     **/
    public void testNormalValues()
    throws Exception
    {
        try
        {
            SCOMapTests.checkValues(pmf,
                HashMap1.class,
                ContainerItem.class,
                String.class);
        }
        finally
        {
            clean(HashMap1.class);
            clean(ContainerItem.class);
        }
    }    

    /**
     * Test case to check the use of clear/isEmpty on the Map.
     **/
    public void testNormalClearIsEmpty()
    throws Exception
    {
        try
        {
            SCOMapTests.checkClearMap(pmf,
                HashMap1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(HashMap1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the inheritance of values in the Map.
     **/
    public void testNormalInheritance()
    throws Exception
    {
        try
        {
            SCOMapTests.checkValueInheritance(pmf,
                HashMap1.class,
                ContainerItem.class,
                ContainerItemChild.class);
        }
        finally
        {
            clean(HashMap1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the use of queries with Maps.
     **/
    public void testNormalQueries()
    throws Exception
    {
        try
        {
            SCOMapTests.checkQuery(pmf,
                HashMap1.class,
                ContainerItem.class,
                vendorID);
        }
        finally
        {
            clean(HashMap1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the use of queries with primitive Maps.
     **/
    public void testNormalQueriesPrimitive()
    throws Exception
    {
        try
        {
            SCOMapTests.checkQueryPrimitive(pmf,
                HashMap3.class,
                vendorID);
        }
        finally
        {
            clean(HashMap3.class);
        }
    }

    /**
     * Test case to check the use of queries with Maps.
     **/
    public void testNormalAttachDetach()
    throws Exception
    {
        try
        {
            SCOMapTests.checkAttachDetach(pmf,
                HashMap1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(HashMap1.class);
            clean(ContainerItem.class);
        }
    }

    // ====> Inverse HashMap

    /**
     * Test case to check the addition of a Map of elements.
     **/
    public void testInversePutItems()
    throws Exception
    {
        try
        {
            SCOMapTests.checkPutItems(pmf,
                HashMap2.class,
                HashMap2Item.class);
        }
        finally
        {
            clean(HashMap2.class);
            clean(HashMap2Item.class);
        }
    }

    /**
     * Test case to check the removal of an item from the Map.
     **/
    public void testInverseRemoveItem()
    throws Exception
    {
        try
        {
            SCOMapTests.checkRemove(pmf,
                HashMap2.class,
                HashMap2Item.class);
        }
        finally
        {
            clean(HashMap2.class);
            clean(HashMap2Item.class);
        }
    }

    /**
     * Test case to check the use of entrySet in the Map.
     **/
    public void testInverseEntrySet()
    throws Exception
    {
        try
        {
            SCOMapTests.checkEntrySet(pmf,
                HashMap2.class,
                HashMap2Item.class,
                String.class);
        }
        finally
        {
            clean(HashMap2.class);
            clean(HashMap2Item.class);
        }
    }

    /**
     * Test case to check the use of keySet in the Map.
     **/
    public void testInverseKeySet()
    throws Exception
    {
        try
        {
            SCOMapTests.checkKeySet(pmf,
                HashMap2.class,
                HashMap2Item.class,
                String.class);
        }
        finally
        {
            clean(HashMap2.class);
            clean(HashMap2Item.class);
        }
    }

    /**
     * Test case to check the use of keySet in the Map.
     **/
    public void testInverseValues()
    throws Exception
    {
        try
        {
            SCOMapTests.checkValues(pmf,
                HashMap2.class,
                HashMap2Item.class,
                String.class);
        }
        finally
        {
            clean(HashMap2.class);
            clean(HashMap2Item.class);
        }
    }

    /**
     * Test case to check the use of clear/isEmpty on the Map.
     **/
    public void testInverseClearIsEmpty()
    throws Exception
    {
        try
        {
            SCOMapTests.checkClearMap(pmf,
                HashMap2.class,
                HashMap2Item.class);
        }
        finally
        {
            clean(HashMap2.class);
            clean(HashMap2Item.class);
        }
    }

    /**
     * Test case to check the inheritance of values in the Map.
     **/
    public void testInverseInheritance()
    throws Exception
    {
        try
        {
            SCOMapTests.checkValueInheritance(pmf,
                HashMap2.class,
                HashMap2Item.class,
                HashMap2ItemChild.class);
        }
        finally
        {
            clean(HashMap2.class);
            clean(HashMap2ItemChild.class);
            clean(HashMap2Item.class);
        }
    }

    /**
     * Test case to check the use of queries with Maps.
     **/
    public void testInverseQueries()
    throws Exception
    {
        try
        {
            SCOMapTests.checkQuery(pmf,
                HashMap2.class,
                HashMap2Item.class,
                vendorID);
        }
        finally
        {
            clean(HashMap2.class);
            clean(HashMap2Item.class);
        }
    }

    /**
     * Test case to check the use of queries with Maps.
     **/
    public void testInverseAttachDetach()
    throws Exception
    {
        try
        {
            SCOMapTests.checkAttachDetach(pmf,
                HashMap2.class,
                HashMap2Item.class);
        }
        finally
        {
            clean(HashMap2.class);
            clean(HashMap2Item.class);
        }
    }
}