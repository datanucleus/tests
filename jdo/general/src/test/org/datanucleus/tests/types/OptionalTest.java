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
package org.datanucleus.tests.types;

import java.util.Optional;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.types.optional.OptionalSample1;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for persistence of Java8 Optional.
 */
public class OptionalTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public OptionalTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    OptionalSample1.class,
                });
            initialised = true;
        }
    }

    /**
     * Test for Optional of basic types.
     */
    public void testOptionalBasic()
    {
        try
        {
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            Object id = null;
            Object id2 = null;
            try
            {
                tx.begin();
                OptionalSample1 s = new OptionalSample1(1, "First String", 123.45);
                pm.makePersistent(s);
                OptionalSample1 s2 = new OptionalSample1(2, null, 245.6);
                pm.makePersistent(s2);
                tx.commit();
                id = pm.getObjectId(s);
                id2 = pm.getObjectId(s2);
            }
            catch (Exception e)
            {
                LOG.error("Error persisting Optional samples", e);
                fail("Error persisting Optional samples");
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

            // Retrieve the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                OptionalSample1 s = pm.getObjectById(OptionalSample1.class, id);

                Optional<String> strField = s.getStringField();
                assertNotNull(strField);
                assertNotNull(strField.get());
                assertEquals("First String", strField.get());
                Optional<Double> dblField = s.getDoubleField();
                assertNotNull(dblField);
                assertNotNull(dblField.get());
                assertEquals(123.45, dblField.get(), 0.05);

                OptionalSample1 s2 = pm.getObjectById(OptionalSample1.class, id2);

                Optional<String> strField2 = s2.getStringField();
                assertNotNull(strField2);
                assertFalse(strField2.isPresent());
                Optional<Double> dblField2 = s2.getDoubleField();
                assertNotNull(dblField2);
                assertNotNull(dblField2.get());
                assertEquals(245.6, dblField2.get(), 0.05);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving Optional data", e);
                fail("Error retrieving Optional data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Query the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // TODO Add this

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error querying Optional data", e);
                fail("Error querying Optional data : " + e.getMessage());
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
            clean(OptionalSample1.class);
        }
    }
}