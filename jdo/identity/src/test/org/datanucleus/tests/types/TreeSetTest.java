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
import org.jpox.samples.types.treeset.TreeSet1;
import org.jpox.samples.types.treeset.TreeSet1Child;
import org.jpox.samples.types.treeset.TreeSet2;
import org.jpox.samples.types.treeset.TreeSet2Item;
import org.jpox.samples.types.treeset.TreeSet2ItemChild;
 
/**
 * Test case to test TreeSet SCO.
 * Tests should include 
 * <ul>
 * <li>Normal</li>
 * <li>Inverse</li>
 * </ul>
 * <ul>
 * <li>datastore identity</li>
 * <li>application identity</li>
 * </ul> 
 * and check basic things for combinations of each of these. 
 *
 * @version $Revision: 1.2 $ 
 **/
public class TreeSetTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public TreeSetTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        TreeSet2Item.class,
                        TreeSet2ItemChild.class,
                        TreeSet1.class,
                        TreeSet1Child.class,
                        TreeSet2.class,
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
                TreeSet1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(TreeSet1.class);
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
                TreeSet1Child.class,
                ContainerItem.class);
        }
        finally
        {
            clean(TreeSet1Child.class);
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
                TreeSet1.class,
                java.util.TreeSet.class,
                ContainerItem.class);
        }
        finally
        {
            clean(TreeSet1.class);
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
                TreeSet1.class,
                java.util.TreeSet.class,
                ContainerItem.class);
        }
        finally
        {
            clean(TreeSet1.class);
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
                TreeSet1Child.class,
                java.util.TreeSet.class,
                ContainerItem.class);
        }
        finally
        {
            clean(TreeSet1Child.class);
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
                TreeSet1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(TreeSet1.class);
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
                TreeSet1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(TreeSet1.class);
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
                TreeSet1.class,
                java.util.TreeSet.class,
                ContainerItem.class,
                ContainerItemChild.class);
        }
        finally
        {
            clean(TreeSet1.class);
            clean(ContainerItemChild.class);
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
                TreeSet1.class,
                ContainerItem.class,
                vendorID);
        }
        finally
        {
            clean(TreeSet1.class);
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
                TreeSet1.class,
                ContainerItem.class,
                java.util.TreeSet.class);
        }
        finally
        {
            clean(TreeSet1.class);
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
                TreeSet2.class,
                TreeSet2Item.class);
        }
        finally
        {
            clean(TreeSet2.class);
            clean(TreeSet2Item.class);
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
                TreeSet2.class,
                java.util.TreeSet.class,
                TreeSet2Item.class);
        }
        finally
        {
            clean(TreeSet2.class);
            clean(TreeSet2Item.class);
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
                TreeSet2.class,
                java.util.TreeSet.class,
                TreeSet2Item.class);
        }
        finally
        {
            clean(TreeSet2.class);
            clean(TreeSet2Item.class);
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
                TreeSet2.class,
                TreeSet2Item.class);
        }
        finally
        {
            clean(TreeSet2.class);
            clean(TreeSet2Item.class);
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
                TreeSet2.class,
                TreeSet2Item.class);
        }
        finally
        {
            clean(TreeSet2.class);
            clean(TreeSet2Item.class);
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
                TreeSet2.class,
                java.util.TreeSet.class,
                TreeSet2Item.class,
                TreeSet2ItemChild.class);
        }
        finally
        {
            clean(TreeSet2.class);
            clean(TreeSet2ItemChild.class);
            clean(TreeSet2Item.class);
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
                TreeSet2.class,
                TreeSet2Item.class,
                vendorID);
        }
        finally
        {
            clean(TreeSet2.class);
            clean(TreeSet2Item.class);
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
                TreeSet2.class,
                TreeSet2Item.class,
                java.util.TreeSet.class);
        }
        finally
        {
            clean(TreeSet2.class);
            clean(TreeSet2Item.class);
        }
    }
}