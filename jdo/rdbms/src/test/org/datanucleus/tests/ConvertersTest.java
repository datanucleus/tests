/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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
**********************************************************************/
package org.datanucleus.tests;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.PersistenceNucleusContext;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.samples.converters.PersonWithConverters;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.java.TypeConverterMapping;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for use of AttributeConverters.
 */
public class ConvertersTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor.
     * @param name Name of the test (not used)
     */
    public ConvertersTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    PersonWithConverters.class,
                });
            initialised = true;
        }
    }

    /**
     * Test the use of "@Convert" annotation on a field.
     */
    public void testUseOfConvert()
    {
        try
        {
            PersistenceNucleusContext nucCtx = ((JDOPersistenceManagerFactory)pmf).getNucleusContext();
            ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
            AbstractClassMetaData cmd = nucCtx.getMetaDataManager().getMetaDataForClass(PersonWithConverters.class, clr);

            // Check the converter is registered with metadata
            AbstractMemberMetaData mmd = cmd.getMetaDataForMember("myBool2");
            assertNotNull(mmd);
            assertTrue(mmd.hasExtension("type-converter-name"));
            String converterName = mmd.getValueForExtension("type-converter-name");
            assertEquals("org.datanucleus.samples.converters.BooleanYNConverter", converterName);

            // Check the correct mapping is chosen for this field
            RDBMSStoreManager storeMgr = (RDBMSStoreManager)nucCtx.getStoreManager();
            DatastoreClass tbl = storeMgr.getDatastoreClass(PersonWithConverters.class.getName(), clr);
            JavaTypeMapping mapping = tbl.getMemberMapping(mmd);
            assertTrue(mapping instanceof TypeConverterMapping);
        }
        catch (Exception e)
        {
            LOG.error("Exception during test", e);
            fail("Exception was thrown : " + e.getMessage());
        }
        finally
        {
        }
    }

    /**
     * Test the use of "@Persistent(converter="...")" annotation on a field.
     */
    public void testUseOfPersistentConverter()
    {
        try
        {
            PersistenceNucleusContext nucCtx = ((JDOPersistenceManagerFactory)pmf).getNucleusContext();
            ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
            AbstractClassMetaData cmd = nucCtx.getMetaDataManager().getMetaDataForClass(PersonWithConverters.class, clr);

            // Check the converter is registered with metadata
            AbstractMemberMetaData mmd = cmd.getMetaDataForMember("myBool1");
            assertNotNull(mmd);
            assertTrue(mmd.hasExtension("type-converter-name"));
            String converterName = mmd.getValueForExtension("type-converter-name");
            assertEquals("org.datanucleus.samples.converters.Boolean10Converter", converterName);

            // Check the correct mapping is chosen for this field
            RDBMSStoreManager storeMgr = (RDBMSStoreManager)nucCtx.getStoreManager();
            DatastoreClass tbl = storeMgr.getDatastoreClass(PersonWithConverters.class.getName(), clr);
            JavaTypeMapping mapping = tbl.getMemberMapping(mmd);
            assertTrue(mapping instanceof TypeConverterMapping);
        }
        catch (Exception e)
        {
            LOG.error("Exception during test", e);
            fail("Exception was thrown : " + e.getMessage());
        }
        finally
        {
        }
    }
}