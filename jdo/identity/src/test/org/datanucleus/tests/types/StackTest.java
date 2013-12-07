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
import org.jpox.samples.types.stack.Stack1;
import org.jpox.samples.types.stack.Stack2;
import org.jpox.samples.types.stack.Stack2Item;
import org.jpox.samples.types.stack.Stack2ItemChild;
 
/**
 * Test case to test Stack SCO.
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
 * @version $Revision: 1.3 $ 
 **/
public class StackTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public StackTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        Stack2Item.class,
                        Stack2ItemChild.class,
                        Stack1.class,
                        Stack2.class,
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
                Stack1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Stack1.class);
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
                Stack1.class,
                java.util.Stack.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Stack1.class);
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
                Stack1.class,
                java.util.Stack.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Stack1.class);
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
                Stack1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Stack1.class);
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
                Stack1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Stack1.class);
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
                Stack1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Stack1.class);
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
                Stack1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Stack1.class);
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
                Stack1.class,
                java.util.Stack.class,
                ContainerItem.class,
                ContainerItemChild.class);
        }
        finally
        {
            clean(Stack1.class);
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
                Stack1.class,
                ContainerItem.class,
                vendorID);
        }
        finally
        {
            clean(Stack1.class);
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
                Stack1.class,
                ContainerItem.class,
                java.util.Stack.class);
        }
        finally
        {
            clean(Stack1.class);
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
                Stack2.class,
                Stack2Item.class);
        }
        finally
        {
            clean(Stack2.class);
            clean(Stack2Item.class);
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
                Stack2.class,
                java.util.Stack.class,
                Stack2Item.class);
        }
        finally
        {
            clean(Stack2.class);
            clean(Stack2Item.class);
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
                Stack2.class,
                java.util.Stack.class,
                Stack2Item.class);
        }
        finally
        {
            clean(Stack2.class);
            clean(Stack2Item.class);
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
                Stack2.class,
                Stack2Item.class);
        }
        finally
        {
            clean(Stack2.class);
            clean(Stack2Item.class);
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
                Stack2.class,
                Stack2Item.class);
        }
        finally
        {
            clean(Stack2.class);
            clean(Stack2Item.class);
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
                Stack2.class,
                Stack2Item.class);
        }
        finally
        {
            clean(Stack2.class);
            clean(Stack2Item.class);
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
                Stack2.class,
                java.util.Stack.class,
                Stack2Item.class,
                Stack2ItemChild.class);
        }
        finally
        {
            clean(Stack2.class);
            clean(Stack2ItemChild.class);
            clean(Stack2Item.class);
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
                Stack2.class,
                Stack2Item.class,
                vendorID);
        }
        finally
        {
            clean(Stack2.class);
            clean(Stack2Item.class);
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
                Stack2.class,
                Stack2Item.class,
                java.util.Stack.class);
        }
        finally
        {
            clean(Stack2.class);
            clean(Stack2Item.class);
        }
    }
}