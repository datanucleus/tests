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

import org.datanucleus.PropertyNames;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.container.ContainerItem;
import org.jpox.samples.types.container.ContainerItemChild;
import org.jpox.samples.types.map.Map1;
import org.jpox.samples.types.map.Map2;
import org.jpox.samples.types.map.Map2Item;
import org.jpox.samples.types.map.Map2ItemChild;
import org.jpox.samples.types.map.Map3;
import org.jpox.samples.types.map.Map3Item;
import org.jpox.samples.types.map.Map4;
import org.jpox.samples.types.map.Map5;
import org.jpox.samples.types.map.Map5Child;

/**
 * Test case to test Map SCO.
 * Tests should include 
 * <ul>
 * <li>JoinTable</li>
 * <li>ForeignKey</li>
 * </ul>
 *
 * @version $Revision: 1.1 $ 
 **/
public class MapTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public MapTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        Map2Item.class,
                        Map2ItemChild.class,
                        Map1.class,
                        Map2.class,
                        Map3Item.class,
                        Map3.class,
                        Map4.class,
                        Map5Child.class,
                        Map5.class,
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
                Map1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map1.class);
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
                Map1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map1.class);
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
                Map1.class,
                ContainerItem.class,
                String.class);
        }
        finally
        {
            clean(Map1.class);
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
                Map1.class,
                ContainerItem.class,
                String.class);
        }
        finally
        {
            clean(Map1.class);
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
                Map1.class,
                ContainerItem.class,
                String.class);
        }
        finally
        {
            clean(Map1.class);
            clean(ContainerItem.class);
        }
    }    

    /**
     * Test case to check the use of entrySet in the Map.
     **/
    public void testNormalNonPrimitiveKeyEntrySet()
    throws Exception
    {
        try
        {
            SCOMapTests.checkEntrySet(pmf,
                Map4.class,
                ContainerItem.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map4.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the use of one instance in key and value.
     **/
    public void testNormalNonPrimitiveSameKeyValueInstances()
    throws Exception
    {
        try
        {
            java.util.Properties userProps = new java.util.Properties();
            userProps.setProperty(PropertyNames.PROPERTY_CACHE_COLLECTIONS, "false");
            getPMF(userProps);

            SCOMapTests.checkSameKeyValueInstances(pmf,
                Map4.class,
                ContainerItem.class);
        }
        finally
        {
            getPMF(null);
            clean(Map4.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the use of keySet in the Map.
     **/
    public void testNormalNonPrimitiveKeyKeySet()
    throws Exception
    {
        try
        {
            SCOMapTests.checkKeySet(pmf,
                Map4.class,
                ContainerItem.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map4.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the use of queries with non-primitive keys in the Map.
     **/
    public void testNormalNonPrimitiveKeyQuery()
    throws Exception
    {
        try
        {
            SCOMapTests.checkQueryNonPrimitiveKey(pmf,
                Map4.class,
                ContainerItem.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map4.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the use of values in the Map.
     **/
    public void testNormalNonPrimitiveKeyValues()
    throws Exception
    {
        try
        {
            SCOMapTests.checkValues(pmf,
                Map4.class,
                ContainerItem.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map4.class);
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
                Map1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map1.class);
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
                Map1.class,
                ContainerItem.class,
                ContainerItemChild.class);
        }
        finally
        {
            clean(Map1.class);
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
                Map1.class,
                ContainerItem.class,
                vendorID);
        }
        finally
        {
            clean(Map1.class);
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
                Map5.class,
                vendorID);
        }
        finally
        {
            clean(Map5.class);
            clean(ContainerItem.class);
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
                Map1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map1.class);
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
                Map2.class,
                Map2Item.class);
        }
        finally
        {
            clean(Map2.class);
            clean(Map2Item.class);
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
                Map2.class,
                Map2Item.class);
        }
        finally
        {
            clean(Map2.class);
            clean(Map2Item.class);
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
                Map2.class,
                Map2Item.class,
                String.class);
        }
        finally
        {
            clean(Map2.class);
            clean(Map2Item.class);
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
                Map2.class,
                Map2Item.class,
                String.class);
        }
        finally
        {
            clean(Map2.class);
            clean(Map2Item.class);
        }
    }    

    /**
     * Test case to check the use of values in the Map.
     **/
    public void testInverseValues()
    throws Exception
    {
        try
        {
            SCOMapTests.checkValues(pmf,
                Map2.class,
                Map2Item.class,
                String.class);
        }
        finally
        {
            clean(Map2.class);
            clean(Map2Item.class);
        }
    }

    /**
     * Test case to check the use of entrySet in the Map.
     **/
    public void testInverseNonPrimitiveKeyEntrySet()
    throws Exception
    {
        try
        {
            SCOMapTests.checkEntrySet(pmf,
                Map3.class,
                Map3Item.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map3.class);
            clean(Map3Item.class);
        }
    }

    /**
     * Test case to check the use of keySet in the Map.
     **/
    public void testInverseNonPrimitiveKeyKeySet()
    throws Exception
    {
        try
        {
            SCOMapTests.checkKeySet(pmf,
                Map3.class,
                Map3Item.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map3.class);
            clean(Map3Item.class);
        }
    }    

    /**
     * Test case to check the use of values in the Map.
     **/
    public void testInverseNonPrimitiveKeyValues()
    throws Exception
    {
        try
        {
            SCOMapTests.checkValues(pmf,
                Map3.class,
                Map3Item.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Map3.class);
            clean(Map3Item.class);
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
                Map2.class,
                Map2Item.class);
        }
        finally
        {
            clean(Map2.class);
            clean(Map2Item.class);
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
                Map2.class,
                Map2Item.class,
                Map2ItemChild.class);
        }
        finally
        {
            clean(Map2.class);
            clean(Map2ItemChild.class);
            clean(Map2Item.class);
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
                Map2.class,
                Map2Item.class,
                vendorID);
        }
        finally
        {
            clean(Map2.class);
            clean(Map2Item.class);
        }
    }

    /**
     * Test case to check the attach/detach of the container.
     **/
    public void testInverseAttachDetach()
    throws Exception
    {
        try
        {
            SCOMapTests.checkAttachDetach(pmf,
                Map2.class,
                Map2Item.class);
        }
        finally
        {
            clean(Map2.class);
            clean(Map2Item.class);
        }
    }
}