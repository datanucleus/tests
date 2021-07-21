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

import org.datanucleus.samples.types.container.ContainerItem;
import org.datanucleus.samples.types.container.ContainerItemChild;
import org.datanucleus.samples.types.list.List1;
import org.datanucleus.samples.types.list.List1Child;
import org.datanucleus.samples.types.list.List2;
import org.datanucleus.samples.types.list.List2Item;
import org.datanucleus.samples.types.list.List2ItemChild;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Test case to test List SCO.
 * Tests should include 
 * <ul>
 * <li>JoinTable</li>
 * <li>ForeignKey</li>
 * </ul>
 * <ul>
 * <li>datastore identity</li>
 * <li>application identity</li>
 * </ul> 
 * and check basic things for combinations of each of these. 
 *
 * @version $Revision: 1.2 $ 
 **/
public class ListTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ListTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        List2Item.class,
                        List2ItemChild.class,
                        List1.class,
                        List1Child.class,
                        List2.class,
                        ContainerItemChild.class,
                        ContainerItem.class
                    });
            initialised = true;
        }
    }

    /**
     * Test case to check the addition of elements.
     **/
    public void testNormalAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf,
                List1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the addition of elements to container child.
     **/
    public void testNormalChildAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf,
                List1Child.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1Child.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the removal of elements.
     **/
    public void testNormalRemoveCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveCollection(pmf,
                List1.class,
                java.util.List.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the retention of elements.
     **/
    public void testNormalRetainCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRetainCollection(pmf,
                List1.class,
                java.util.List.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the removal of elements from container child.
     **/
    public void testNormalChildRemovalCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveCollection(pmf,
                List1Child.class,
                java.util.List.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1Child.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to test the use of remove(Object)
     **/
    public void testNormalRemoveItem()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveItem(pmf,
                List1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    }

    public void testEquals()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkEquals(pmf,
                List1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    	
    }
    
    /**
     * Test case to test the use of removeAt etc
     **/
    public void testNormalRemoveAt()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveAt(pmf,
                List1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to test the use of get etc
     **/
    public void testNormalGetItem()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkGetAt(pmf,
                List1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to test the use of get etc
     **/
    public void testNormalContains()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkContains(pmf,
                List1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the clearing out of elements.
     **/
    public void testNormalClearIsEmpty()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkClearCollection(pmf,
                List1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    }

    /** 
     * Test case to check the use of inherited objects in the Normal List.
     **/
    public void testNormalInheritance()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkElementInheritance(pmf,
                List1.class,
                java.util.List.class,
                ContainerItem.class,
                ContainerItemChild.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItemChild.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the querying of collections.
     **/
    public void testNormalQuery()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkQuery(pmf,
                List1.class,
                ContainerItem.class,
                rdbmsVendorID);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the attach/detach of the container.
     **/
    public void testNormalAttachDetach()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkAttachDetach(pmf,
                List1.class,
                ContainerItem.class,
                java.util.List.class);
        }
        finally
        {
            clean(List1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the addition of elements.
     **/
    public void testInverseAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf,
                List2.class,
                List2Item.class);
        }
        finally
        {
            clean(List2.class);
            clean(List2Item.class);
        }
    }

    /**
     * Test case to check the removal of elements.
     **/
    public void testInverseRemoveCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveCollection(pmf,
                List2.class,
                java.util.List.class,
                List2Item.class);
        }
        finally
        {
            clean(List2.class);
            clean(List2Item.class);
        }
    }

    /**
     * Test case to check the retention of elements.
     **/
    public void testInverseRetainCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRetainCollection(pmf,
                List2.class,
                java.util.List.class,
                List2Item.class);
        }
        finally
        {
            clean(List2.class);
            clean(List2Item.class);
        }
    }

    /**
     * Test case to test the use of removeAt etc
     **/
    public void testInverseRemoveItem()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveAt(pmf,
                List2.class,
                List2Item.class);
        }
        finally
        {
            clean(List2.class);
            clean(List2Item.class);
        }
    }

    /**
     * Test case to test the use of get etc
     **/
    public void testInverseGetItem()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkGetAt(pmf,
                List2.class,
                List2Item.class);
        }
        finally
        {
            clean(List2.class);
            clean(List2Item.class);
        }
    }

    /**
     * Test case to check the clearing out of elements.
     **/
    public void testInverseClearIsEmpty()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkClearCollection(pmf,
                List2.class,
                List2Item.class);
        }
        finally
        {
            clean(List2.class);
            clean(List2Item.class);
        }
    }

    /** 
     * Test case to check the use of inherited objects
     **/
    public void testInverseInheritance()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkElementInheritance(pmf,
                List2.class,
                java.util.List.class,
                List2Item.class,
                List2ItemChild.class);
        }
        finally
        {
            clean(List2.class);
            clean(List2ItemChild.class);
            clean(List2Item.class);
        }
    }

    /**
     * Test case to check the querying of collections.
     **/
    public void testInverseQuery()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkQuery(pmf,
                List2.class,
                List2Item.class,
                rdbmsVendorID);
        }
        finally
        {
            clean(List2.class);
            clean(List2Item.class);
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
            SCOCollectionTests.checkAttachDetach(pmf,
                List2.class,
                List2Item.class,
                java.util.List.class);
        }
        finally
        {
            clean(List2.class);
            clean(List2Item.class);
        }
    }
}