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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.models.company.Employee;
import org.jpox.samples.annotations.models.company.Person;
import org.jpox.samples.annotations.one_one.bidir.Boiler;
import org.jpox.samples.annotations.one_one.bidir.Timer;

/**
 * Tests for JPQL "UPDATE" queries.
 */
public class JPQLUpdateTest extends JPAPersistenceTestCase
{
    private static boolean initialised = false;

    public JPQLUpdateTest(String name)
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

    /**
     * Test of bulk UPDATE statement with the field being modified in the supertable.
     */
    public void testBulkUpdateFieldInSupertable()
    {
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

                Query q = em.createQuery("UPDATE " + Timer.class.getName() + " t SET make=\"Sony\" WHERE t.digital = TRUE");
                int number = q.executeUpdate();
                assertEquals(1, number);

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception during BULK UPDATE", e);
                fail("Exception thrown during BULK UPDATE : " + e.getMessage());
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
     * Test of bulk UPDATE statement with the field being modified in the table of the class.
     */
    public void testBulkUpdateFieldInTable()
    {
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

                Query q = em.createQuery("UPDATE Person_Ann p SET p.emailAddress = :param WHERE p.firstName = 'Fred'");
                q.setParameter("param", "fred@flintstones.com");
                int val = q.executeUpdate();
                assertEquals("Number of records updated by query was incorrect", 1, val);

                tx.commit();

                // TODO Check the datastore contents
            }
            catch (Throwable e)
            {
                LOG.error("Exception thrown in bulk update", e);
                fail("Exception thrown on bulk update : " + e.getMessage());
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
     * Test of bulk UPDATE statement setting a field to null
     */
    public void testBulkUpdateSettingToNull()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            Person.PK pk = null;
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);
                em.flush();
                pk = p.getPK();
                tx.commit();
            }
            catch (Throwable e)
            {
                LOG.error("Exception thrown in persist", e);
                fail("Exception thrown on persist : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Query q = em.createQuery("UPDATE Person_Ann p SET p.emailAddress = NULL WHERE p.firstName = 'Fred'");
                int val = q.executeUpdate();
                assertEquals("Number of records updated by query was incorrect", 1, val);

                tx.commit();
            }
            catch (Throwable e)
            {
                LOG.error("Exception thrown in bulk update", e);
                fail("Exception thrown on bulk update : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = em.find(Person.class, pk);
                assertNull(p.getEmailAddress());

                tx.commit();
            }
            catch (Throwable e)
            {
                LOG.error("Exception thrown in bulk update", e);
                fail("Exception thrown on bulk update : " + e.getMessage());
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
     * Test of bulk UPDATE statement with a subquery in the WHERE clause.
     */
    public void testBulkUpdateUsingWhereSubquery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Employee e1 = new Employee(101, "Fred", "Flintstone", "fred.flintstone@jpox.com", 10000f, "12001");
                e1.setAge(35);
                em.persist(e1);
                Employee e2 = new Employee(101, "Fred", "Flintstone", "fred.flintstone@jpox.com", 20000f, "12000");
                e2.setAge(45);
                em.persist(e2);
                em.flush();

                Query q = em.createQuery("UPDATE Employee_Ann e SET e.salary = e.salary+:param WHERE e.age > (SELECT AVG(emp.age) FROM Employee_Ann emp)");
                q.setParameter("param", 100);
                int val = q.executeUpdate();
                assertEquals("Number of records updated by query was incorrect", 1, val);

                tx.commit();

                // TODO Check the datastore contents
            }
            catch (Throwable e)
            {
                LOG.error("Exception thrown in bulk update", e);
                fail("Exception thrown on bulk update : " + e.getMessage());
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
            clean(Employee.class);
        }
    }

    /**
     * Test of simple UPDATE statement then calling getSingleResult().
     * This should throw an IllegalStateException
     */
    public void testUpdateSingleResult()
    {
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

                Query q = em.createQuery("UPDATE " + Person.class.getName() + " p SET p.emailAddress = :param");
                q.setParameter("param", "fred@flintstones.com");
                try
                {
                    q.getSingleResult();
                }
                catch (IllegalStateException ise)
                {
                    // Expected
                    return;
                }
                fail("Called getSingleResult() on an UPDATE query!");
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