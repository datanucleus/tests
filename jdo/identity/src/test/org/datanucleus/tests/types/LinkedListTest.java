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
import org.jpox.samples.types.linkedlist.LinkedList1;
import org.jpox.samples.types.linkedlist.LinkedList2;
import org.jpox.samples.types.linkedlist.LinkedList2Item;
import org.jpox.samples.types.linkedlist.LinkedList2ItemChild;
 
/**
 * Test case to test LinkedList SCO.
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
 **/
public class LinkedListTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public LinkedListTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        LinkedList2Item.class,
                        LinkedList2ItemChild.class,
                        LinkedList1.class,
                        LinkedList2.class,
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
                LinkedList1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedList1.class);
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
                LinkedList1.class,
                java.util.LinkedList.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedList1.class);
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
                LinkedList1.class,
                java.util.LinkedList.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedList1.class);
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
                LinkedList1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedList1.class);
            clean(ContainerItem.class);
        }
    }

    public void testEquals()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkEquals(pmf,
                LinkedList1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedList1.class);
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
                LinkedList1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedList1.class);
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
                LinkedList1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedList1.class);
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
                LinkedList1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedList1.class);
            clean(ContainerItem.class);
        }
    }

    /** 
     * Test case to check the use of inherited objects
     **/
    public void testNormalInheritance()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkElementInheritance(pmf,
                LinkedList1.class,
                java.util.LinkedList.class,
                ContainerItem.class,
                ContainerItemChild.class);
        }
        finally
        {
            clean(LinkedList1.class);
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
                LinkedList1.class,
                ContainerItem.class,
                vendorID);
        }
        finally
        {
            clean(LinkedList1.class);
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
                LinkedList1.class,
                ContainerItem.class,
                java.util.LinkedList.class);
        }
        finally
        {
            clean(LinkedList1.class);
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
                LinkedList2.class,
                LinkedList2Item.class);
        }
        finally
        {
            clean(LinkedList2.class);
            clean(LinkedList2Item.class);
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
                LinkedList2.class,
                java.util.LinkedList.class,
                LinkedList2Item.class);
        }
        finally
        {
            clean(LinkedList2.class);
            clean(LinkedList2Item.class);
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
                LinkedList2.class,
                java.util.LinkedList.class,
                LinkedList2Item.class);
        }
        finally
        {
            clean(LinkedList2.class);
            clean(LinkedList2Item.class);
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
                LinkedList2.class,
                LinkedList2Item.class);
        }
        finally
        {
            clean(LinkedList2.class);
            clean(LinkedList2Item.class);
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
                LinkedList2.class,
                LinkedList2Item.class);
        }
        finally
        {
            clean(LinkedList2.class);
            clean(LinkedList2Item.class);
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
                LinkedList2.class,
                LinkedList2Item.class);
        }
        finally
        {
            clean(LinkedList2.class);
            clean(LinkedList2Item.class);
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
                LinkedList2.class,
                java.util.LinkedList.class,
                LinkedList2Item.class,
                LinkedList2ItemChild.class);
        }
        finally
        {
            clean(LinkedList2.class);
            clean(LinkedList2ItemChild.class);
            clean(LinkedList2Item.class);
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
                LinkedList2.class,
                LinkedList2Item.class,
                vendorID);
        }
        finally
        {
            clean(LinkedList2.class);
            clean(LinkedList2Item.class);
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
                LinkedList2.class,
                LinkedList2Item.class,
                java.util.LinkedList.class);
        }
        finally
        {
            clean(LinkedList2.class);
            clean(LinkedList2Item.class);
        }
    }
}