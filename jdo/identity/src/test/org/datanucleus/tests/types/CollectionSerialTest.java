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
import org.jpox.samples.types.collection.Collection3;
import org.jpox.samples.types.collection.Collection4;
import org.jpox.samples.types.collection.Collection4Item;
import org.jpox.samples.types.container.ContainerItem;
import org.jpox.samples.types.container.ContainerItemChild;

/**
 * Tests for Collection field which is serialised.
 */
public class CollectionSerialTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public CollectionSerialTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        Collection4Item.class,
                        Collection3.class,
                        Collection4.class,
                        ContainerItemChild.class,
                        ContainerItem.class
                    });
            initialised = true;
        }
    }
    /**
     * Test case to check the addition of elements.
     **/
    public void testUniAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf,
                Collection3.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection3.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the removal of elements.
     **/
    public void testUniRemoveCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveCollection(pmf,
                Collection3.class,
                java.util.Collection.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection3.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the retention of elements.
     **/
    public void testUniRetainCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRetainCollection(pmf,
                Collection3.class,
                java.util.Collection.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection3.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the clearing out of elements.
     **/
    public void testUniClearIsEmpty()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkClearCollection(pmf,
                Collection3.class,
                ContainerItem.class);
        }
        finally
        {
            clean(Collection3.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the attach/detach of the container.
     * TODO This test makes little sense since we are serialising the whole field, so in principle the same as the FK example below.
     **/
    public void xtestUniAttachDetach()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkAttachDetach(pmf,
                Collection3.class,
                ContainerItem.class,
                java.util.Collection.class);
        }
        finally
        {
            clean(Collection3.class);
            clean(ContainerItem.class);
        }
    }

    /**
     * Test case to check the addition of elements to a serialised collection.
     **/
    public void testBiAddCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkPersistCollectionByContainer(pmf,
                Collection4.class,
                Collection4Item.class);
        }
        finally
        {
            clean(Collection4.class);
            clean(Collection4Item.class);
        }
    }

    /**
     * Test case to check the removal of elements.
     **/
    public void testBiRemoveCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRemoveCollection(pmf,
                Collection4.class,
                java.util.Collection.class,
                Collection4Item.class);
        }
        finally
        {
            clean(Collection4.class);
            clean(Collection4Item.class);
        }
    }

    /**
     * Test case to check the retention of elements.
     **/
    public void testBiRetainCollection()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkRetainCollection(pmf,
                Collection4.class,
                java.util.Collection.class,
                Collection4Item.class);
        }
        finally
        {
            clean(Collection4.class);
            clean(Collection4Item.class);
        }
    }

    /**
     * Test case to check the clearing out of elements.
     **/
    public void testBiClearIsEmpty()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkClearCollection(pmf,
                Collection4.class,
                Collection4Item.class);
        }
        finally
        {
            clean(Collection4.class);
            clean(Collection4Item.class);
        }
    }

    /**
     * Test case to check the attach/detach of the container.
     **/
    public void testBiAttachDetach()
    throws Exception
    {
        try
        {
            SCOCollectionTests.checkAttachDetach(pmf,
                Collection4.class,
                Collection4Item.class,
                java.util.Collection.class);
        }
        finally
        {
            clean(Collection4.class);
            clean(Collection4Item.class);
        }
    }
}