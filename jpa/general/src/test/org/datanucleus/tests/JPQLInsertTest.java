/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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
2007 Andy Jefferson - rewritten to new test.framework/samples
    ...
**********************************************************************/
package org.datanucleus.tests;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.datanucleus.samples.annotations.models.company.Employee;
import org.datanucleus.samples.annotations.models.company.Person;
import org.datanucleus.store.StoreManager;

/**
 * Tests for JPQL "INSERT" queries.
 */
public class JPQLInsertTest extends JPAPersistenceTestCase
{
    private static boolean initialised = false;

    public JPQLInsertTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Person.class, Employee.class,
                });
        }
    }

    /**
     * Test of bulk INSERT statement.
     */
    public void testBulkInsert()
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_QUERY_JPQL_BULK_INSERT))
        {
            return;
        }

        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);
                em.flush();

                Query q = em.createQuery("INSERT INTO MySimpleClass (id, name) SELECT p.personNum, p.lastName FROM Person_Ann p");
                int val = q.executeUpdate();
                assertEquals("Number of records inserted by query was incorrect", 1, val);

                tx.commit();
            }
            catch (Throwable e)
            {
                LOG.error("Exception thrown in bulk insert", e);
                fail("Exception thrown on bulk insert : " + e.getMessage());
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
            clean(Person.class);
        }
    }
}