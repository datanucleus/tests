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
import org.jpox.samples.types.collection.Collection1;
import org.jpox.samples.types.collection.Collection1Child;
import org.jpox.samples.types.collection.Collection2;
import org.jpox.samples.types.collection.Collection2Item;
import org.jpox.samples.types.collection.Collection2ItemChild;
import org.jpox.samples.types.container.ContainerItem;
import org.jpox.samples.types.container.ContainerItemChild;

/**
 * Test case to test Collection SCO.
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
public class CollectionTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor
     * @param name Name of test 
     **/
    public CollectionTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        Collection2ItemChild.class,
                        Collection2Item.class,
                        Collection1.class,
                        Collection1Child.class,
                        Collection2.class,
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
                Collection1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the addition of elements from child container.
     **/
    public void testNormalChildAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf,
                Collection1Child.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection1Child.class);
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
                Collection1.class,
                java.util.Collection.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection1.class);
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
                Collection1.class,
                java.util.Collection.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection1.class);
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
                Collection1Child.class,
                java.util.Collection.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection1Child.class);
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
                Collection1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection1.class);
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
                Collection1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection1.class);
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
                Collection1.class,
                java.util.Collection.class,
                ContainerItem.class,
                ContainerItemChild.class);
        }
        finally
        {
            clean(Collection1.class);
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
                Collection1.class,
                ContainerItem.class,
                vendorID);
        }
        finally
        {
            clean(Collection1.class);
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
                Collection1.class,
                ContainerItem.class,
                java.util.Collection.class);
        }
        finally
        {
            clean(Collection1.class);
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
                Collection2.class,
                Collection2Item.class);
        }
        finally
        {
            clean(Collection2.class);
            clean(Collection2Item.class);
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
                Collection2.class,
                java.util.Collection.class,
                Collection2Item.class);
        }
        finally
        {
            clean(Collection2.class);
            clean(Collection2Item.class);
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
                Collection2.class,
                java.util.Collection.class,
                Collection2Item.class);
        }
        finally
        {
            clean(Collection2.class);
            clean(Collection2Item.class);
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
                Collection2.class,
                Collection2Item.class);
        }
        finally
        {
            clean(Collection2.class);
            clean(Collection2Item.class);
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
                Collection2.class,
                Collection2Item.class);
        }
        finally
        {
            clean(Collection2.class);
            clean(Collection2Item.class);
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
                Collection2.class,
                java.util.Collection.class,
                Collection2Item.class,
                Collection2ItemChild.class);
        }
        finally
        {
            clean(Collection2.class);
            clean(Collection2ItemChild.class);
            clean(Collection2Item.class);
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
                Collection2.class,
                Collection2Item.class,
                vendorID);
        }
        finally
        {
            clean(Collection2.class);
            clean(Collection2Item.class);
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
                Collection2.class,
                Collection2Item.class,
                java.util.Collection.class);
        }
        finally
        {
            clean(Collection2.class);
            clean(Collection2Item.class);
        }
    }
}