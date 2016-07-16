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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.types.optional.OptionalSample1;
import org.datanucleus.samples.types.optional.OptionalSample2;
import org.datanucleus.samples.types.optional.OptionalSample3;
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
                    OptionalSample2.class,
                    OptionalSample3.class,
                });
            initialised = true;
        }
    }

    /**
     * Test for Optional of basic types.
     */
    public void testOptionalBasic()
    {
        Date d1 = new Date(1000000);
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
                s.setDateField(d1);
                pm.makePersistent(s);
                OptionalSample1 s2 = new OptionalSample1(2, null, 245.6);
                s2.setDateField(null);
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

                Query q = pm.newQuery("SELECT FROM " + OptionalSample1.class.getName() + " WHERE stringField.isPresent()");
                q.setClass(OptionalSample1.class);
                List<OptionalSample1> results = q.executeList();
                assertNotNull(results);
                assertEquals(1, results.size());
                OptionalSample1 result1 = results.get(0);
                assertEquals(1, result1.getId());

                Query q2 = pm.newQuery("SELECT id, stringField.get() FROM " + OptionalSample1.class.getName());
                List results2 = q2.executeResultList();
                assertNotNull(results2);
                assertEquals(2, results2.size());

                boolean row1Present = false;
                boolean row2Present = false;
                for (Object row : results2)
                {
                    Object[] rowValues = (Object[])row;
                    if (((Number)rowValues[0]).intValue() == 1)
                    {
                        row1Present = true;
                        assertTrue(rowValues[1] instanceof String);
                        assertEquals("First String", rowValues[1]);
                    }
                    else if (((Number)rowValues[0]).intValue() == 2)
                    {
                        row2Present = true;
                        assertNull(rowValues[1]);
                    }
                }
                assertTrue(row1Present);
                assertTrue(row2Present);

                Query q3 = pm.newQuery("SELECT id, stringField.orElse('NotPresent') FROM " + OptionalSample1.class.getName());
                List<Object[]> results3 = q3.executeResultList();
                assertNotNull(results3);
                assertEquals(2, results3.size());
                row1Present = false;
                row2Present = false;
                for (Object[] result : results3)
                {
                    if (((Number)result[0]).intValue() == 1)
                    {
                        row1Present = true;
                        assertEquals("First String", result[1]);
                    }
                    else if (((Number)result[0]).intValue() == 2)
                    {
                        row2Present = true;
                        assertEquals("NotPresent", result[1]);
                    }
                }
                assertTrue(row1Present);
                assertTrue(row2Present);

                Query q4 = pm.newQuery("SELECT FROM " + OptionalSample1.class.getName() + " WHERE stringField.orElse(:param) == null");
                Map<String, Object> paramMap = new HashMap();
                paramMap.put("param", null);
                q4.setNamedParameters(paramMap);
                List<OptionalSample1> results4 = q4.executeList();
                assertNotNull(results4);
                assertEquals(1, results4.size());
                OptionalSample1 os4_1 = results4.get(0);
                assertEquals(2, os4_1.getId());

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

    /**
     * Test for Optional of 1-1 relation.
     */
    public void testOptionalOneToOne()
    {
        try
        {
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            Object id1 = null;
            Object id2 = null;
            try
            {
                tx.begin();

                OptionalSample2 s2a = new OptionalSample2(1, "First");
                OptionalSample3 s3 = new OptionalSample3(101, "First S3");
                s2a.setSample3(s3);
                pm.makePersistent(s2a);

                OptionalSample2 s2b = new OptionalSample2(2, "Second");
                pm.makePersistent(s2b);

                tx.commit();
                id1 = pm.getObjectId(s2a);
                id2 = pm.getObjectId(s2b);
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

                OptionalSample2 s2a = pm.getObjectById(OptionalSample2.class, id1);
                assertEquals("First", s2a.getName());
                Optional<OptionalSample3> s3fielda = s2a.getSample3();
                assertNotNull(s3fielda);
                OptionalSample3 s3 = s3fielda.get();
                assertNotNull(s3);
                assertEquals("First S3", s3.getName());
                Optional s3s3 = s3.getSample3();
                assertNotNull(s3s3);
                assertFalse(s3s3.isPresent());

                OptionalSample2 s2b = pm.getObjectById(OptionalSample2.class, id2);
                assertEquals("Second", s2b.getName());
                Optional<OptionalSample3> s3fieldb = s2b.getSample3();
                assertNotNull(s3fieldb);
                assertFalse(s3fieldb.isPresent());

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

                Query q = pm.newQuery("SELECT FROM " + OptionalSample2.class.getName() + " WHERE !(sample3 != null)");
                q.setClass(OptionalSample2.class);
                List<OptionalSample2> results = q.executeList();
                assertNotNull(results);
                assertEquals(1, results.size());

                // Refer to optional field via get().
                Query q2 = pm.newQuery("SELECT FROM " + OptionalSample2.class.getName() + " WHERE sample3.get().id == :val");
                q2.setClass(OptionalSample2.class);
                Map namedParams = new HashMap();
                namedParams.put("val", 101);
                q2.setNamedParameters(namedParams);
                List<OptionalSample2> results2 = q2.executeList();
                assertNotNull(results2);
                assertEquals(1, results2.size());

                // Refer to optional field as if the wrapped object
                Query q3 = pm.newQuery("SELECT FROM " + OptionalSample2.class.getName() + " WHERE sample3.id == :val");
                q3.setClass(OptionalSample2.class);
                q3.setNamedParameters(namedParams);
                List<OptionalSample2> results3 = q3.executeList();
                assertNotNull(results3);
                assertEquals(1, results3.size());

                // Refer to optional field and then to another optional field (no Sample3 objects have the sample3 field set)
                Query q4 = pm.newQuery("SELECT FROM " + OptionalSample2.class.getName() + " WHERE sample3 != null && sample3.sample3.isPresent()");
                q4.setClass(OptionalSample2.class);
                List<OptionalSample2> results4 = q4.executeList();
                assertNotNull(results4);
                assertEquals(0, results4.size());

                tx.rollback();
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
            clean(OptionalSample2.class);
            clean(OptionalSample3.class);
        }
    }
}