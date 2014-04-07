/******************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
    ...
 *****************************************************************/
package org.datanucleus.tests;

import junit.framework.Assert;

import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.store.StoreManager;
import org.datanucleus.util.NucleusLogger;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * Base of all DataNucleus persistence test cases.
 */
@RunWith(MultiConfigRunner.class)
public abstract class PersistenceTestCase
{
    @Rule
    public DatanucleusTestWatcher testWatcher = new DatanucleusTestWatcher();
    
    /** Log for unit testing. */
    protected static final NucleusLogger LOG = NucleusLogger.getLoggerInstance("DataNucleus.Test");

    protected static boolean initOnCreate = true;

    /** The StoreManager in use. */
    protected static StoreManager storeMgr;

    /** The unique string identifying the datastore. */
    protected static String vendorID;
    
    public PersistenceTestCase()
    {
        super();
    }

    public PersistenceTestCase(String name)
    {
        // Constructor to support legacy JUnit3 style tests
    }

    @Before
    public final void before() throws Exception
    {
        setUp();
    }

    protected void setUp() throws Exception
    {
        LOG.info("********** " + testWatcher.getTestName() + " [setUp] **********");
    }

    @After
    public final void after() throws Exception
    {
        tearDown();
    }

    protected void tearDown() throws Exception
    {
        LOG.info("********** " + testWatcher.getTestName() + " [tearDown] **********");
    }

    protected static void skipWhen(boolean predicate, String message)
    {
        if (predicate)
        {
            LOG.info(message);
            Assume.assumeTrue(!predicate); // Skip the test run
        }
    }

    /**
     * Convenience method to help cleaning the database in teardown.
     * @param cls The class whose instances to remove
     */
    protected abstract void clean(Class<?> cls);

    /**
     * Method to add the specified classes to the schema.
     * @param classes The classes
     */
    protected void addClassesToSchema(Class<?>[] classes)
    {
        String[] classNames = new String[classes.length];
        for (int i = 0; i < classes.length; i++)
        {
            classNames[i] = classes[i].getName();
        }
        if (storeMgr == null)
        {
            throw new IllegalArgumentException("storeMgr is null");
        }
        storeMgr.manageClasses(new ClassLoaderResolverImpl(), classNames);
    }

    /**
     * Method to remove all existing classes from the schema.
     */
    protected void dropAllClassesFromSchema()
    {
        storeMgr.unmanageAllClasses(new ClassLoaderResolverImpl());
    }

    /***********************************************************
     * Assertion methods for transition to JUnit4
     ***********************************************************/

    /**
     * @deprecated Use {@link #fail(String)} and provide a description
     */
    @Deprecated
    protected final void fail()
    {
        Assert.fail();
    }

    protected final void fail(String message)
    {
        Assert.fail(message);
    }

    protected final void assertNotNull(Object object)
    {
        Assert.assertNotNull(object);
    }

    protected final void assertNotNull(String message, Object object)
    {
        Assert.assertNotNull(message, object);
    }

    protected final void assertEquals(Object expected, Object actual)
    {
        Assert.assertEquals(expected, actual);
    }

    protected final void assertEquals(String message, Object expected, Object actual)
    {
        Assert.assertEquals(message, expected, actual);
    }

    protected final void assertEquals(String message, long expected, long actual)
    {
        Assert.assertEquals(message, expected, actual);
    }

    protected final void assertEquals(long expected, long actual)
    {
        Assert.assertEquals(expected, actual);
    }

    protected final void assertTrue(String message, boolean condition)
    {
        Assert.assertTrue(message, condition);
    }

    protected final void assertTrue(boolean condition)
    {
        Assert.assertTrue(condition);
    }

    protected final void assertFalse(boolean condition)
    {
        Assert.assertFalse(condition);
    }

    protected final void assertFalse(String message, boolean condition)
    {
        Assert.assertFalse(message, condition);
    }

    protected final void assertNull(Object object)
    {
        Assert.assertNull(object);
    }

    protected final void assertNull(String message, Object object)
    {
        Assert.assertNull(message, object);
    }

    protected final void assertSame(String message, Object expected, Object actual)
    {
        Assert.assertSame(message, expected, actual);
    }

    protected final void assertSame(Object expected, Object actual)
    {
        Assert.assertSame(expected, actual);
    }

    protected final void assertEquals(String message, double expected,
            double actual, double delta)
    {
        Assert.assertEquals(message, expected, actual, delta);
    }

    protected final void assertEquals(double expected,
            double actual, double delta)
    {
        Assert.assertEquals(expected, actual, delta);
    }

    protected final void assertNotSame(String message, Object unexpected, Object actual)
    {
        Assert.assertNotSame(message, unexpected, actual);
    }
}