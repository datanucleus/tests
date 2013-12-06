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

import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.store.StoreManager;
import org.datanucleus.util.NucleusLogger;
import org.junit.runner.RunWith;

import junit.framework.TestCase;

/**
 * Base of all DataNucleus persistence test cases.
 */
@RunWith(MultiConfigRunner.class)
public abstract class PersistenceTestCase extends TestCase
{
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
        super(name);
    }

    protected void setUp() throws Exception
    {
        LOG.info("********** " + toString() + " [setUp] **********");
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        LOG.info("********** " + toString() + " [tearDown] **********");
        super.tearDown();
    }

    /**
     * Convenience method to help cleaning the database in teardown.
     * @param cls The class whose instances to remove
     */
    protected abstract void clean(Class cls);

    /**
     * Method to add the specified classes to the schema.
     * @param classes The classes
     */
    protected void addClassesToSchema(Class[] classes)
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
        storeMgr.addClasses(classNames, new ClassLoaderResolverImpl());
    }

    /**
     * Method to remove all existing classes from the schema.
     */
    protected void dropAllClassesFromSchema()
    {
        storeMgr.removeAllClasses(new ClassLoaderResolverImpl());
    }
}