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


Contributions
    ...
***********************************************************************/
package org.datanucleus.tests.types;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.container.ContainerItem;
import org.jpox.samples.types.container.ContainerItemChild;
import org.jpox.samples.types.linkedhashmap.LinkedHashMap1;
import org.jpox.samples.types.linkedhashmap.LinkedHashMap2;
import org.jpox.samples.types.linkedhashmap.LinkedHashMap2Item;
import org.jpox.samples.types.linkedhashmap.LinkedHashMap2ItemChild;
import org.jpox.samples.types.linkedhashmap.LinkedHashMap3;
import org.jpox.samples.types.linkedhashmap.LinkedHashMap3Child;

/**
 * Test case to test LinkedHashMap SCO.
 * Tests should include 
 * <ul>
 * <li>JoinTable</li>
 * <li>ForeignKey</li>
 * </ul>
 *
 * @version $Revision: 1.1 $ 
 **/
public class LinkedHashMapTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public LinkedHashMapTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        LinkedHashMap2Item.class,
                        LinkedHashMap2ItemChild.class,
                        LinkedHashMap1.class,
                        LinkedHashMap2.class,
                        LinkedHashMap3Child.class,
                        LinkedHashMap3.class,
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
                LinkedHashMap1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashMap1.class);
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
                LinkedHashMap1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashMap1.class);
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
                LinkedHashMap1.class,
                ContainerItem.class,
                String.class);
        }
        finally
        {
            clean(LinkedHashMap1.class);
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
                LinkedHashMap1.class,
                ContainerItem.class,
                String.class);
        }
        finally
        {
            clean(LinkedHashMap1.class);
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
                LinkedHashMap1.class,
                ContainerItem.class,
                String.class);
        }
        finally
        {
            clean(LinkedHashMap1.class);
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
                LinkedHashMap1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashMap1.class);
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
                LinkedHashMap1.class,
                ContainerItem.class,
                ContainerItemChild.class);
        }
        finally
        {
            clean(LinkedHashMap1.class);
            clean(ContainerItemChild.class);
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
                LinkedHashMap1.class,
                ContainerItem.class,
                vendorID);
        }
        finally
        {
            clean(LinkedHashMap1.class);
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
                LinkedHashMap3.class,
                vendorID);
        }
        finally
        {
            clean(LinkedHashMap3.class);
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
                LinkedHashMap1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashMap1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the addition of a Map of elements.
     **/
    public void testInversePutItems()
    throws Exception
    {
        try
        {
            SCOMapTests.checkPutItems(pmf,
                LinkedHashMap2.class,
                LinkedHashMap2Item.class);
        }
        finally
        {
            clean(LinkedHashMap2.class);
            clean(LinkedHashMap2Item.class);
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
                LinkedHashMap2.class,
                LinkedHashMap2Item.class);
        }
        finally
        {
            clean(LinkedHashMap2.class);
            clean(LinkedHashMap2Item.class);
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
                LinkedHashMap2.class,
                LinkedHashMap2Item.class,
                String.class);
        }
        finally
        {
            clean(LinkedHashMap2.class);
            clean(LinkedHashMap2Item.class);
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
                LinkedHashMap2.class,
                LinkedHashMap2Item.class,
                String.class);
        }
        finally
        {
            clean(LinkedHashMap2.class);
            clean(LinkedHashMap2Item.class);
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
                LinkedHashMap2.class,
                LinkedHashMap2Item.class,
                String.class);
        }
        finally
        {
            clean(LinkedHashMap2.class);
            clean(LinkedHashMap2Item.class);
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
                LinkedHashMap2.class,
                LinkedHashMap2Item.class);
        }
        finally
        {
            clean(LinkedHashMap2.class);
            clean(LinkedHashMap2Item.class);
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
                LinkedHashMap2.class,
                LinkedHashMap2Item.class,
                LinkedHashMap2ItemChild.class);
        }
        finally
        {
            clean(LinkedHashMap2.class);
            clean(LinkedHashMap2ItemChild.class);
            clean(LinkedHashMap2Item.class);
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
                LinkedHashMap2.class,
                LinkedHashMap2Item.class,
                vendorID);
        }
        finally
        {
            clean(LinkedHashMap2.class);
            clean(LinkedHashMap2Item.class);
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
                LinkedHashMap2.class,
                LinkedHashMap2Item.class);
        }
        finally
        {
            clean(LinkedHashMap2.class);
            clean(LinkedHashMap2Item.class);
        }
    }
}