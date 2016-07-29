/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved.
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

import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.datanucleus.samples.schema.ClassWithDefaultCols;
import org.datanucleus.store.rdbms.RDBMSPropertyNames;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Test for some features of column specification specific to RDBMS.
 */
public class SchemaColumnTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public SchemaColumnTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    ClassWithDefaultCols.class,
                });
            initialised = true;
        }
    }

    /**
     * Test of default values for columns, storing the default when a field is null at persist.
     */
    public void testColumnDefaultsStoringDefaultWhenNull()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ClassWithDefaultCols c1 = new ClassWithDefaultCols(1);
                pm.makePersistent(c1);

                tx.commit();
            }
            catch (Exception ex)
            {
                LOG.error("Exception during test : " + ex.getMessage());
                fail("Exception thrown during test : " + ex.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            pmf.getDataStoreCache().evictAll();

            // Retrieve and check data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ClassWithDefaultCols c1 = pm.getObjectById(ClassWithDefaultCols.class, 1);
                assertNull(c1.getDefaultedNameNull());
                assertEquals("Name 1", c1.getDefaultedName());
                assertEquals(new Long(3), c1.getDefaultedLong());

                tx.commit();
            }
            catch (Exception ex)
            {
                LOG.error("Exception during test : " + ex.getMessage());
                fail("Exception thrown during test : " + ex.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
        }
        finally
        {
            // Clean out our data
            clean(ClassWithDefaultCols.class);
        }
    }

    /**
     * Test of default values for columns, storing null when a field is null at persist.
     */
    public void testColumnDefaultsStoringNullWhenNull()
    {
        Properties props = new Properties();
        props.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_COLUMN_DEFAULT_WHEN_NULL, "false");
        PersistenceManagerFactory myPMF = TestHelper.getConfigurablePMF(1, props);

        try
        {
            PersistenceManager pm = myPMF.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ClassWithDefaultCols c1 = new ClassWithDefaultCols(1);
                pm.makePersistent(c1);

                tx.commit();
            }
            catch (Exception ex)
            {
                LOG.error("Exception during test : " + ex.getMessage());
                fail("Exception thrown during test : " + ex.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            myPMF.getDataStoreCache().evictAll();

            // Retrieve and check data
            pm = myPMF.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ClassWithDefaultCols c1 = pm.getObjectById(ClassWithDefaultCols.class, 1);
                assertNull(c1.getDefaultedNameNull());
                assertNull(c1.getDefaultedName());
                assertNull(c1.getDefaultedLong());

                tx.commit();
            }
            catch (Exception ex)
            {
                LOG.error("Exception during test : " + ex.getMessage());
                fail("Exception thrown during test : " + ex.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
        }
        finally
        {
            // Clean out our data
            clean(myPMF, ClassWithDefaultCols.class);
            myPMF.close();
        }
    }
}