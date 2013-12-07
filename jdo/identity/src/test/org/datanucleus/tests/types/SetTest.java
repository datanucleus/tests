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
import org.jpox.samples.types.set.Set1;
import org.jpox.samples.types.set.Set1Child;
import org.jpox.samples.types.set.Set2;
import org.jpox.samples.types.set.Set2Item;
import org.jpox.samples.types.set.Set2ItemChild;
import org.jpox.samples.types.set.Set3;
import org.jpox.samples.types.set.Set3Item;

/**
 * Test case to test Set SCO.
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
public class SetTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public SetTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        Set2Item.class,
                        Set2ItemChild.class,
                        Set1.class,
                        Set1Child.class,
                        Set2.class,
                        ContainerItemChild.class,
                        ContainerItem.class
                    });
            initialised = true;
        }
    }

    /**
     * Test case to check the addition of elements, persisting the container.
     **/
    public void testNormalPersistByCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf, Set1.class, ContainerItem.class);
        }
        finally
        {
            clean(Set1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the addition of elements from container child.
     **/
    public void testNormalChildAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf, Set1Child.class, ContainerItem.class);
        }
        finally
        {
            clean(Set1.class);
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
            SCOCollectionTests.checkRemoveCollection(pmf, Set1.class, java.util.Set.class, ContainerItem.class);
        }
        finally
        {
            clean(Set1.class);
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
            SCOCollectionTests.checkRetainCollection(pmf, Set1.class, java.util.Set.class, ContainerItem.class);
        }
        finally
        {
            clean(Set1.class);
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
            SCOCollectionTests.checkRemoveCollection(pmf, Set1Child.class, java.util.Set.class, ContainerItem.class);
        }
        finally
        {
            clean(Set1Child.class);
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
            SCOCollectionTests.checkRemoveItem(pmf, Set1.class, ContainerItem.class);
        }
        finally
        {
            clean(Set1.class);
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
            SCOCollectionTests.checkClearCollection(pmf, Set1.class, ContainerItem.class);
        }
        finally
        {
            clean(Set1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the clearing out of elements.
     **/
    public void testNormalContains()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkContains(pmf, Set1.class, ContainerItem.class);
        }
        finally
        {
            clean(Set1.class);
            clean(ContainerItem.class);
        }
    }    
    /**
     * Test case to check the use of inherited objects in the Normal Set.
     **/
    public void testNormalInheritance()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkElementInheritance(pmf, Set1.class, java.util.Set.class,
                ContainerItem.class, ContainerItemChild.class);
        }
        finally
        {
            clean(Set1.class);
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
            SCOCollectionTests.checkQuery(pmf, Set1.class, ContainerItem.class, vendorID);
        }
        finally
        {
            clean(Set1.class);
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
            SCOCollectionTests.checkAttachDetach(pmf, Set1.class, ContainerItem.class, java.util.Set.class);
        }
        finally
        {
            clean(Set1.class);
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
            SCOCollectionTests.checkPersistCollectionByContainer(pmf, Set2.class, Set2Item.class);
        }
        finally
        {
            clean(Set2.class);
            clean(Set2Item.class);
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
            SCOCollectionTests.checkRemoveCollection(pmf, Set2.class, java.util.Set.class, Set2Item.class);
        }
        finally
        {
            clean(Set2.class);
            clean(Set2Item.class);
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
            SCOCollectionTests.checkRetainCollection(pmf, Set2.class, java.util.Set.class, Set2Item.class);
        }
        finally
        {
            clean(Set2.class);
            clean(Set2Item.class);
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
            SCOCollectionTests.checkAddElement(pmf, Set2.class, Set2Item.class);
        }
        finally
        {
            clean(Set2.class);
            clean(Set2Item.class);
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
                Set2.class,
                Set2Item.class);
        }
        finally
        {
            clean(Set2.class);
            clean(Set2Item.class);
        }
    }

    /** 
     * Test case to check the use of inherited objects in the Inverse Set.
     **/
    public void testInverseInheritance()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkElementInheritance(pmf,
                Set2.class,
                java.util.Set.class,
                Set2Item.class,
                Set2ItemChild.class);
        }
        finally
        {
            clean(Set2.class);
            clean(Set2ItemChild.class);
            clean(Set2Item.class);
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
                Set2.class,
                Set2Item.class,
                vendorID);
        }
        finally
        {
            clean(Set2.class);
            clean(Set2Item.class);
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
            SCOCollectionTests.checkAttachDetach(pmf, Set2.class, Set2Item.class, java.util.Set.class);
        }
        finally
        {
            clean(Set2.class);
            clean(Set2Item.class);
        }
    }

    public void testInversePersistByCollectionDFG()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf, Set3.class, Set3Item.class);
        }
        finally
        {
            clean(Set3.class);
            clean(Set3Item.class);
        }
    }

    public void testInversePersistByElementDFG()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByElement(pmf, Set3.class, Set3Item.class);
        }
        finally
        {
            clean(Set3.class);
            clean(Set3Item.class);
        }
    }
}