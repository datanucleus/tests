/**********************************************************************
Copyright (c) 2017 Andy Jefferson and others. All rights reserved.
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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.datanucleus.samples.annotations.query.QueryTypeHolder;
import org.datanucleus.samples.annotations.query.QueryTypeResult;
import org.datanucleus.tests.JPAPersistenceTestCase;

/**
 * Tests for JPQL queries via JPA that are specific to RDBMS.
 */
public class JPQLQueryTest extends JPAPersistenceTestCase
{
    private static boolean initialised = false;

    public JPQLQueryTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    QueryTypeHolder.class
                });
        }
    }

    /**
     * Test of use of the JPQL "FUNCTION" function returning DOUBLE for invoking native SQL functions.
     */
    public void testFunctionDouble()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                // Persist an object
                QueryTypeHolder qt = new QueryTypeHolder(1, 0.0, 100, "MyFirstName");
                em.persist(qt);
                em.flush();

                // Perform some FUNCTION queries

                // FUNCTION in WHERE clause using = operator
                List result = em.createQuery("SELECT qt FROM QueryTypeHolder qt WHERE FUNCTION('sin', qt.doubleValue) = 0").getResultList();
                {
                    assertEquals(1, result.size());
                    QueryTypeHolder qtr = (QueryTypeHolder) result.iterator().next();
                    assertEquals(1, qtr.getId());
                    assertEquals("MyFirstName", qtr.getStringValue());
                }

                // FUNCTION in SELECT clause returned
                result = em.createQuery("SELECT qt.stringValue, FUNCTION('sin', qt.doubleValue) FROM QueryTypeHolder qt").getResultList();
                {
                    assertEquals(1, result.size());
                    Object[] qtr = (Object[]) result.iterator().next();
                    assertEquals(2, qtr.length);
                    assertEquals("MyFirstName", qtr[0]);
                    assertEquals(0.0, qtr[1]);
                }

                // FUNCTION in SELECT clause returned as part of CONSTRUCTOR expression
                result = em.createQuery("SELECT NEW org.datanucleus.samples.annotations.query.QueryTypeResult(FUNCTION('sin', qt.doubleValue), qt.longValue, qt.stringValue) FROM QueryTypeHolder qt").getResultList();
                {
                    assertEquals(1, result.size());
                    QueryTypeResult qtr = (QueryTypeResult) result.iterator().next();
                    assertEquals("MyFirstName", qtr.getStringValue());
                    assertEquals(0.0, qtr.getDoubleValue());
                    assertEquals(100, qtr.getLongValue());
                }

                tx.rollback(); // Dont persist the data
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(QueryTypeHolder.class);
        }
    }

    /**
     * Test of use of the JPQL "FUNCTION" function returning String for invoking native SQL functions.
     */
    public void testFunctionString()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                // Persist an object
                QueryTypeHolder qt = new QueryTypeHolder(1, 0.0, 100, "MyFirstName");
                em.persist(qt);
                em.flush();

                // Perform some FUNCTION queries

                // FUNCTION in WHERE clause using = operator
                List result = em.createQuery("SELECT qt FROM QueryTypeHolder qt WHERE FUNCTION('lower', qt.stringValue) = 'myfirstname'").getResultList();
                {
                    assertEquals(1, result.size());
                    QueryTypeHolder qtr = (QueryTypeHolder) result.iterator().next();
                    assertEquals(1, qtr.getId());
                    assertEquals("MyFirstName", qtr.getStringValue());
                }

                // FUNCTION in SELECT clause returned
                result = em.createQuery("SELECT qt.doubleValue, FUNCTION('lower', qt.stringValue) FROM QueryTypeHolder qt").getResultList();
                {
                    assertEquals(1, result.size());
                    Object[] qtr = (Object[]) result.iterator().next();
                    assertEquals(2, qtr.length);
                    assertEquals(0.0, qtr[0]);
                    assertEquals("myfirstname", qtr[1]);
                }

                // FUNCTION in SELECT clause returned as part of CONSTRUCTOR expression
                result = em.createQuery("SELECT NEW org.datanucleus.samples.annotations.query.QueryTypeResult(qt.doubleValue, qt.longValue, FUNCTION('lower', qt.stringValue)) FROM QueryTypeHolder qt").getResultList();
                {
                    assertEquals(1, result.size());
                    QueryTypeResult qtr = (QueryTypeResult) result.iterator().next();
                    assertEquals("myfirstname", qtr.getStringValue());
                    assertEquals(0.0, qtr.getDoubleValue());
                    assertEquals(100, qtr.getLongValue());
                }

                tx.rollback(); // Dont persist the data
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(QueryTypeHolder.class);
        }
    }
}