/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
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
import org.jpox.samples.types.properties.Properties1;

/**
 * Test case to test Properties SCO.
 * Tests should include 
 * <ul>
 * <li>JoinTable</li>
 * </ul>
 *
 * @version $Revision: 1.1 $ 
 **/
public class PropertiesTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public PropertiesTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        Properties1.class
                    });
            initialised = true;
        }
    }

    /**
     * Test case to check the addition of a Properties of elements.
     **/
    public void testNormalPutItems()
    throws Exception
    {
        try
        {
            SCOMapTests.checkPutItems(pmf,
                Properties1.class,
                String.class);
        }
        finally
        {
            clean(Properties1.class);
        }
    }

    /**
     * Test case to check the removal of an item from the Properties.
     **/
    public void testNormalRemoveItem()
    throws Exception
    {
        try
        {
            SCOMapTests.checkRemove(pmf,
                Properties1.class,
                String.class);
        }
        finally
        {
            clean(Properties1.class);
        }
    }

    /**
     * Test case to check the use of entrySet in the Properties.
     **/
    public void testNormalEntrySet()
    throws Exception
    {
        try
        {
            SCOMapTests.checkEntrySet(pmf,
                Properties1.class,
                String.class,
                String.class);
        }
        finally
        {
            clean(Properties1.class);
        }
    }

    /**
     * Test case to check the use of keySet in the Properties.
     **/
    public void testNormalKeySet()
    throws Exception
    {
        try
        {
            SCOMapTests.checkKeySet(pmf,
                Properties1.class,
                String.class,
                String.class);
        }
        finally
        {
            clean(Properties1.class);
        }
    }

    /**
     * Test case to check the use of values in the Properties.
     **/
    public void testNormalValues()
    throws Exception
    {
        try
        {
            SCOMapTests.checkValues(pmf,
                Properties1.class,
                String.class,
                String.class);
        }
        finally
        {
            clean(Properties1.class);
        }
    }    

    /**
     * Test case to check the use of clear/isEmpty on the Properties.
     **/
    public void testNormalClearIsEmpty()
    throws Exception
    {
        try
        {
            SCOMapTests.checkClearMap(pmf,
                Properties1.class,
                java.lang.String.class);
        }
        finally
        {
            clean(Properties1.class);
        }
    }


    /**
     * Test case to check the use of queries with Propertiess.
     **/
    public void testNormalAttachDetach()
    throws Exception
    {
        try
        {
            SCOMapTests.checkAttachDetach(pmf,
                Properties1.class,
                String.class);
        }
        finally
        {
            clean(Properties1.class);
        }
    }
}