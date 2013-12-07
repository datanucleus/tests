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
import org.jpox.samples.types.linkedhashset.LinkedHashSet1;
import org.jpox.samples.types.linkedhashset.LinkedHashSet1Child;
import org.jpox.samples.types.linkedhashset.LinkedHashSet2;
import org.jpox.samples.types.linkedhashset.LinkedHashSet2Item;
import org.jpox.samples.types.linkedhashset.LinkedHashSet2ItemChild;
 
/**
 * Test case to test LinkedHashSet SCO.
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
public class LinkedHashSetTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public LinkedHashSetTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        LinkedHashSet2Item.class,
                        LinkedHashSet2ItemChild.class,
                        LinkedHashSet1.class,
                        LinkedHashSet1Child.class,
                        LinkedHashSet2.class,
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
                LinkedHashSet1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashSet1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the addition of elements for container child.
     **/
    public void testNormalChildAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf,
                LinkedHashSet1Child.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashSet1Child.class);
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
                LinkedHashSet1.class,
                java.util.LinkedHashSet.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashSet1.class);
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
                LinkedHashSet1.class,
                java.util.LinkedHashSet.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashSet1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the removal of elements from container child.
     **/
    public void testNormalChildRemoveCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveCollection(pmf,
                LinkedHashSet1Child.class,
                java.util.LinkedHashSet.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashSet1Child.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the removal of an element.
     **/
    public void testNormalRemoveItem()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveItem(pmf,
                LinkedHashSet1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashSet1.class);
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
                LinkedHashSet1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(LinkedHashSet1.class);
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
                LinkedHashSet1.class,
                java.util.LinkedHashSet.class,
                ContainerItem.class,
                ContainerItemChild.class);
        }
        finally
        {
            clean(LinkedHashSet1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the use of queries on collections.
     **/
    public void testNormalQuery()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkQuery(pmf,
                LinkedHashSet1.class,
                ContainerItem.class,
                vendorID);
        }
        finally
        {
            clean(LinkedHashSet1.class);
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
                LinkedHashSet1.class,
                ContainerItem.class,
                java.util.LinkedHashSet.class);
        }
        finally
        {
            clean(LinkedHashSet1.class);
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
                LinkedHashSet2.class,
                LinkedHashSet2Item.class);
        }
        finally
        {
            clean(LinkedHashSet2.class);
            clean(LinkedHashSet2Item.class);
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
                LinkedHashSet2.class,
                java.util.LinkedHashSet.class,
                LinkedHashSet2Item.class);
        }
        finally
        {
            clean(LinkedHashSet2.class);
            clean(LinkedHashSet2Item.class);
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
                LinkedHashSet2.class,
                java.util.LinkedHashSet.class,
                LinkedHashSet2Item.class);
        }
        finally
        {
            clean(LinkedHashSet2.class);
            clean(LinkedHashSet2Item.class);
        }
    }

    /**
     * Test case to check the addition of element to container.
     **/
    public void testInverseAddElement()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkAddElement(pmf,
                LinkedHashSet2.class,
                LinkedHashSet2Item.class);
        }
        finally
        {
            clean(LinkedHashSet2.class);
            clean(LinkedHashSet2Item.class);
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
                LinkedHashSet2.class,
                LinkedHashSet2Item.class);
        }
        finally
        {
            clean(LinkedHashSet2.class);
            clean(LinkedHashSet2Item.class);
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
                LinkedHashSet2.class,
                java.util.LinkedHashSet.class,
                LinkedHashSet2Item.class,
                LinkedHashSet2ItemChild.class);
        }
        finally
        {
            clean(LinkedHashSet2.class);
            clean(LinkedHashSet2ItemChild.class);
            clean(LinkedHashSet2Item.class);
        }
    }

    /**
     * Test case to check the use of queries on collections.
     **/
    public void testInverseQuery()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkQuery(pmf,
                LinkedHashSet2.class,
                LinkedHashSet2Item.class,
                vendorID);
        }
        finally
        {
            clean(LinkedHashSet2.class);
            clean(LinkedHashSet2Item.class);
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
                LinkedHashSet2.class,
                LinkedHashSet2Item.class,
                java.util.LinkedHashSet.class);
        }
        finally
        {
            clean(LinkedHashSet2.class);
            clean(LinkedHashSet2Item.class);
        }
    }
}