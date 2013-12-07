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
import org.jpox.samples.types.hashtable.Hashtable1;
import org.jpox.samples.types.hashtable.Hashtable2;
import org.jpox.samples.types.hashtable.Hashtable2Item;
import org.jpox.samples.types.hashtable.Hashtable2ItemChild;
import org.jpox.samples.types.hashtable.Hashtable3;
import org.jpox.samples.types.hashtable.Hashtable3Child;
 
/**
 * Test case to test Hashtable SCO.
 * Tests should include 
 * <ul>
 * <li>JoinTable</li>
 * <li>ForeignKey</li>
 * </ul>
 *
 * @version $Revision: 1.1 $ 
 **/
public class HashtableTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public HashtableTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        Hashtable2Item.class,
                        Hashtable2ItemChild.class,
                        Hashtable1.class,
                        Hashtable2.class,
                        Hashtable3Child.class,
                        Hashtable3.class,
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
                                  Hashtable1.class,
                                  ContainerItem.class);
        }
        finally
        {
            clean(Hashtable1.class);
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
                                Hashtable1.class,
                                ContainerItem.class);
        }
        finally
        {
            clean(Hashtable1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the use of entrySet for the Map.
     **/
    public void testNormalEntrySet()
    throws Exception
    {
        try
        {
        SCOMapTests.checkEntrySet(pmf,
                                  Hashtable1.class,
                                  ContainerItem.class,
                                  String.class);
        }
        finally
        {
            clean(Hashtable1.class);
            clean(ContainerItem.class);
        }
    }
    
    /**
     * Test case to check the use of keySet for the Map.
     **/
    public void testNormalKeySet()
    throws Exception
    {
        try
        {
        SCOMapTests.checkKeySet(pmf,
                                  Hashtable1.class,
                                  ContainerItem.class,
                                  String.class);
        }
        finally
        {
            clean(Hashtable1.class);
            clean(ContainerItem.class);
        }
    }    

    /**
     * Test case to check the use of values for the Map.
     **/
    public void testNormalValues()
    throws Exception
    {
        try
        {
        SCOMapTests.checkValues(pmf,
                                  Hashtable1.class,
                                  ContainerItem.class,
                                  String.class);
        }
        finally
        {
            clean(Hashtable1.class);
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
                                  Hashtable1.class,
                                  ContainerItem.class);
        }
        finally
        {
            clean(Hashtable1.class);
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
                                          Hashtable1.class,
                                          ContainerItem.class,
                                          ContainerItemChild.class);
        }
        finally
        {
            clean(Hashtable1.class);
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
                               Hashtable1.class,
                               ContainerItem.class,
                               vendorID);
        }
        finally
        {
            clean(Hashtable1.class);
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
                                        Hashtable3.class,
                                        vendorID);
        }
        finally
        {
            clean(Hashtable3.class);
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
                                      Hashtable1.class,
                                      ContainerItem.class);
        }
        finally
        {
            clean(Hashtable1.class);
            clean(ContainerItem.class);
        }
    }

    // ====> Inverse Hashtable

    /**
     * Test case to check the addition of a Map of elements.
     **/
    public void testInversePutItems()
    throws Exception
    {
        try
        {
        SCOMapTests.checkPutItems(pmf,
                                  Hashtable2.class,
                                  Hashtable2Item.class);
        }
        finally
        {
            clean(Hashtable2.class);
            clean(Hashtable2Item.class);
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
                                Hashtable2.class,
                                Hashtable2Item.class);
        }
        finally
        {
            clean(Hashtable2.class);
            clean(Hashtable2Item.class);
        }
    }

    /**
     * Test case to check the use of entrySet for the Map.
     **/
    public void testInverseEntrySet()
    throws Exception
    {
        try
        {
        SCOMapTests.checkEntrySet(pmf,
                                  Hashtable2.class,
                                  Hashtable2Item.class,
                                  String.class);
        }
        finally
        {
            clean(Hashtable2.class);
            clean(Hashtable2Item.class);
        }
    }

    /**
     * Test case to check the use of keySet for the Map.
     **/
    public void testInverseKeySet()
    throws Exception
    {
        try
        {
        SCOMapTests.checkKeySet(pmf,
                                  Hashtable2.class,
                                  Hashtable2Item.class,
                                  String.class);
        }
        finally
        {
            clean(Hashtable2.class);
            clean(Hashtable2Item.class);
        }
    }

    /**
     * Test case to check the use of values for the Map.
     **/
    public void testInverseValues()
    throws Exception
    {
        try
        {
        SCOMapTests.checkValues(pmf,
                                  Hashtable2.class,
                                  Hashtable2Item.class,
                                  String.class);
        }
        finally
        {
            clean(Hashtable2.class);
            clean(Hashtable2Item.class);
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
                                  Hashtable2.class,
                                  Hashtable2Item.class);
        }
        finally
        {
            clean(Hashtable2.class);
            clean(Hashtable2Item.class);
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
                                          Hashtable2.class,
                                          Hashtable2Item.class,
                                          Hashtable2ItemChild.class);
        }
        finally
        {
            clean(Hashtable2.class);
            clean(Hashtable2ItemChild.class);
            clean(Hashtable2Item.class);
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
                               Hashtable2.class,
                               Hashtable2Item.class,
                               vendorID);
        }
        finally
        {
            clean(Hashtable2.class);
            clean(Hashtable2Item.class);
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
                                      Hashtable2.class,
                                      Hashtable2Item.class);
        }
        finally
        {
            clean(Hashtable2.class);
            clean(Hashtable2Item.class);
        }
    }
}