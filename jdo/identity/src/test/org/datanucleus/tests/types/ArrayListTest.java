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
import org.jpox.samples.types.arraylist.ArrayList1;
import org.jpox.samples.types.arraylist.ArrayList1Child;
import org.jpox.samples.types.arraylist.ArrayList2;
import org.jpox.samples.types.arraylist.ArrayList2Item;
import org.jpox.samples.types.arraylist.ArrayList2ItemChild;
import org.jpox.samples.types.container.ContainerItem;
import org.jpox.samples.types.container.ContainerItemChild;

/**
 * Test case to test ArrayList SCO.
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
public class ArrayListTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ArrayListTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        ArrayList2Item.class,
                        ArrayList2ItemChild.class,
                        ArrayList1.class,
                        ArrayList1Child.class,
                        ArrayList2.class,
                        ContainerItemChild.class,
                        ContainerItem.class
                    });
            initialised = true;
        }
    }

    /**
     * Test case to test the use of addAll etc
     **/
    public void testNormalAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf,
                ArrayList1.class, ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1.class);
            clean(ContainerItem.class);
        }
    }
    
    /**
     * Test case to test the use of add(position, Object)
     **/
    public void testNormalAddItem()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkAddAt(pmf, 
                ArrayList1.class, ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to test the use of addAll for container child.
     **/
    public void testNormalChildAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf, 
                ArrayList1Child.class, ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1Child.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to test the use of removeAll etc
     **/
    public void testNormalRemoveCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveCollection(pmf, 
                ArrayList1.class, java.util.ArrayList.class, ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to test the use of retainAll etc
     **/
    public void testNormalRetainCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRetainCollection(pmf, 
                ArrayList1.class, java.util.ArrayList.class, ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to test the use of removeAll from container child.
     **/
    public void testNormalChildRemoveCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveCollection(pmf, 
                ArrayList1Child.class, java.util.ArrayList.class, ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1Child.class);
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
                ArrayList1.class, ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1.class);
            clean(ContainerItem.class);
        }
    }

    public void testEquals()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkEquals(pmf,
                ArrayList1.class,
                ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1.class);
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
                ArrayList1.class, ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to test the use of get etc in the Normal List.
     **/
    public void testNormalGetItem()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkGetAt(pmf,
                ArrayList1.class, ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1.class);
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
                ArrayList1.class, ContainerItem.class);
        }
        finally
        {
            clean(ArrayList1.class);
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
                ArrayList1.class, java.util.ArrayList.class,
                ContainerItem.class, ContainerItemChild.class);
        }
        finally
        {
            clean(ArrayList1.class);
            clean(ContainerItemChild.class);
            clean(ContainerItem.class);
        }
    }
 
    /**
     * Test case to test the querying of collections.
     **/
    public void testNormalQuery()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkQuery(pmf,
                ArrayList1.class, ContainerItem.class, vendorID);
        }
        finally
        {
            clean(ArrayList1.class);
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
                ArrayList1.class, ContainerItem.class, java.util.ArrayList.class);
        }
        finally
        {
            clean(ArrayList1.class);
            clean(ContainerItem.class);
        }
    }

    // ====> Inverse ArrayList
 
    /**
     * Test case to test the use of addAll etc
     **/
    public void testInverseAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf,
                ArrayList2.class, ArrayList2Item.class);
        }
        finally
        {
            clean(ArrayList2.class);
            clean(ArrayList2Item.class);
        }
    }

    /**
     * Test case to test the use of removeAll etc
     **/
    public void testInverseRemoveCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveCollection(pmf,
                ArrayList2.class, java.util.ArrayList.class, ArrayList2Item.class);
        }
        finally
        {
            clean(ArrayList2.class);
            clean(ArrayList2Item.class);
        }
    }

    /**
     * Test case to test the use of retainAll etc
     **/
    public void testInverseRetainCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRetainCollection(pmf,
                ArrayList2.class, java.util.ArrayList.class, ArrayList2Item.class);
        }
        finally
        {
            clean(ArrayList2.class);
            clean(ArrayList2Item.class);
        }
    }

    /**
     * Test case to test the use of remove(Object)
     **/
    public void testInverseRemoveItem()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveItem(pmf,
                ArrayList2.class, ArrayList2Item.class);
        }
        finally
        {
            clean(ArrayList2.class);
            clean(ArrayList2Item.class);
        }
    }
    
    /**
     * Test case to test the use of removeAt etc
     **/
    public void testInverseRemoveAt()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveAt(pmf,
                ArrayList2.class, ArrayList2Item.class);
        }
        finally
        {
            clean(ArrayList2.class);
            clean(ArrayList2Item.class);
        }
    }

    /**
     * Test case to test the use of get etc in the Inverse List.
     **/
    public void testInverseGetItem()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkGetAt(pmf,
                ArrayList2.class, ArrayList2Item.class);
        }
        finally
        {
            clean(ArrayList2.class);
            clean(ArrayList2Item.class);
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
                ArrayList2.class, ArrayList2Item.class);
        }
        finally
        {
            clean(ArrayList2.class);
            clean(ArrayList2Item.class);
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
                ArrayList2.class, java.util.ArrayList.class, 
                ArrayList2Item.class, ArrayList2ItemChild.class);
        }
        finally
        {
            clean(ArrayList2.class);
            clean(ArrayList2Item.class);
            clean(ArrayList2ItemChild.class);
        }
    }
 
    /**
     * Test case to test the querying of collections.
     **/
    public void testInverseQuery()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkQuery(pmf,
                ArrayList2.class, ArrayList2Item.class, vendorID);
        }
        finally
        {
            clean(ArrayList2.class);
            clean(ArrayList2Item.class);
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
                ArrayList2.class, ArrayList2Item.class, java.util.ArrayList.class);
        }
        finally
        {
            clean(ArrayList2.class);
            clean(ArrayList2Item.class);
        }
    }
}