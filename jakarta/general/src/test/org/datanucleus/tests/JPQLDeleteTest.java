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
2007 Andy Jefferson - rewritten to new test.framework/samples
    ...
**********************************************************************/
package org.datanucleus.tests;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;

import org.datanucleus.samples.annotations.models.company.Employee;
import org.datanucleus.samples.annotations.models.company.Person;
import org.datanucleus.samples.annotations.one_many.bidir_2.House;
import org.datanucleus.samples.annotations.one_one.bidir.Boiler;
import org.datanucleus.samples.annotations.one_one.bidir.Timer;
import org.datanucleus.store.StoreManager;

/**
 * Tests for JPQL DELETE queries.
 */
public class JPQLDeleteTest extends JakartaPersistenceTestCase
{
    private static boolean initialised = false;

    public JPQLDeleteTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Person.class, Employee.class,
                    Boiler.class, Timer.class,
                });
        }
    }

    public void testBulkDelete()
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_QUERY_JPQL_BULK_DELETE))
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

                Timer t = new Timer("Seiko", true, null);
                em.persist(t);
                em.flush();

                Query q = em.createQuery("DELETE FROM " + Timer.class.getName() + " t");
                int number = q.executeUpdate();
                assertEquals(1, number);
                tx.rollback();
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
            clean(Timer.class);
        }
    }

    /**
     * Test of simple DELETE statement. See JIRA "NUCRDBMS-7"
     */
    public void testDeleteWithJoinedInheritance()
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_QUERY_JPQL_BULK_DELETE))
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

                Query q = em.createQuery("DELETE FROM Person_Ann p WHERE p.firstName = 'Fred'");
                int val = q.executeUpdate();
                assertEquals("Number of records updated by query was incorrect", 1, val);
                tx.commit();

                // TODO Check the datastore contents
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

    /**
     * Test of simple DELETE statement (where no other tables are involved).
     */
    public void testDeleteSimple()
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_QUERY_JPQL_BULK_DELETE))
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

                House h = new House(15, "The Street");
                em.persist(h);
                em.flush();

                Query q = em.createQuery("DELETE FROM House h");
                int val = q.executeUpdate();
                assertEquals("Number of records deleted by query was incorrect", 1, val);
                tx.commit();

                // TODO Check the datastore contents
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
            clean(House.class);
        }
    }
}