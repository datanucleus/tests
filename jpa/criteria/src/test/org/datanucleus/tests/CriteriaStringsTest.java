/**********************************************************************
Copyright (c) 2009 Andy Jefferson and others. All rights reserved.
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import org.datanucleus.samples.annotations.models.company.Account;
import org.datanucleus.samples.annotations.models.company.Employee;
import org.datanucleus.samples.annotations.models.company.Person;
import org.datanucleus.samples.annotations.models.company.Qualification;
import org.datanucleus.samples.annotations.one_many.bidir.Animal;
import org.datanucleus.samples.annotations.one_many.bidir.Farm;
import org.datanucleus.tests.JPAPersistenceTestCase;

/**
 * Tests for the Criteria API in JPA.
 */
public class CriteriaStringsTest extends JPAPersistenceTestCase
{
    private static boolean initialised = false;

    public CriteriaStringsTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Person.class, Employee.class, Account.class, Animal.class, Farm.class
                });
        }
    }

    /* (non-Javadoc)
     * @see org.datanucleus.tests.PersistenceTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
            p1.setAge(35);
            em.persist(p1);
            Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@datanucleus.org");
            p2.setAge(38);
            p2.setBestFriend(p1);
            em.persist(p2);

            // 1-1 relation sample
            Employee emp1 = new Employee(105, "Joe", "Bloggs", "joe.bloggs@datanucleus.org", 12000.0f, "10005");
            emp1.setAge(40);
            Employee emp2 = new Employee(106, "Nigel", "Bloggs", "nigel.bloggs@datanucleus.org", 13000.0f, "10006");
            emp2.setAge(41);
            Account acc1 = new Account();
            acc1.setId(105);
            acc1.setUsername("joebloggs");
            acc1.setEnabled(true);
            Account acc2 = new Account();
            acc2.setId(106);
            acc2.setUsername("nigelbloggs");
            acc2.setEnabled(true);
            emp1.setAccount(acc1);
            emp2.setAccount(acc2);
            Qualification q1 = new Qualification("BSc");
            q1.setPerson(emp1);
            Qualification q2 = new Qualification("MSc");
            q2.setPerson(emp2);
            em.persist(emp1);
            em.persist(emp2);
            em.persist(q1);
            em.persist(q2);

            // 1-N relation sample
            Farm farm1 = new Farm("Giles Farm");
            Farm farm2 = new Farm("Kiwi Farm");
            Animal an1 = new Animal("Brown Cow");
            Animal an2 = new Animal("Woolly Sheep");
            Animal an3 = new Animal("Sheepdog");
            farm1.getAnimals().add(an1);
            an1.setFarm(farm1);
            farm2.getAnimals().add(an2);
            an2.setFarm(farm2);
            farm2.getAnimals().add(an3);
            an3.setFarm(farm2);            
            em.persist(farm1);
            em.persist(farm2);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(">> Exception in setUp data", e);
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

    /* (non-Javadoc)
     * @see org.datanucleus.tests.PersistenceTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            Query q = em.createQuery("SELECT p FROM " + Person.class.getName() + " p");
            List<Person> results = q.getResultList();
            Iterator<Person> pIter = results.iterator();
            while (pIter.hasNext())
            {
                Person p = pIter.next();
                if (p.getBestFriend() != null)
                {
                    p.setBestFriend(null);
                }
            }
            em.flush();

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }

        clean(Qualification.class);
        clean(Person.class);
        clean(Employee.class);
        clean(Account.class);
        clean(Farm.class);
        clean(Animal.class);

        super.tearDown();
    }

    /**
     * Test basic generation of query with candidate and alias.
     */
    public void testBasic()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");
            crit.select(candidate);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p FROM org.datanucleus.samples.annotations.models.company.Person p", crit.toString());

            Query q = em.createQuery(crit);
            List<Person> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 4, results.size());
            Iterator<Person> iter = results.iterator();
            boolean nigelBloggsPresent = false;
            boolean joeBloggsPresent = false;
            boolean fredFlintstonePresent = false;
            boolean barneyRubblePresent = false;
            while (iter.hasNext())
            {
                Person pers = iter.next();
                if (pers.getFirstName().equals("Fred") && pers.getLastName().equals("Flintstone") && pers.getPersonNum() == 101)
                {
                    fredFlintstonePresent = true;
                }
                if (pers.getFirstName().equals("Barney") && pers.getLastName().equals("Rubble") && pers.getPersonNum() == 102)
                {
                    barneyRubblePresent = true;
                }
                if (pers.getFirstName().equals("Joe") && pers.getLastName().equals("Bloggs") && pers.getPersonNum() == 105)
                {
                    joeBloggsPresent = true;
                }
                if (pers.getFirstName().equals("Nigel") && pers.getLastName().equals("Bloggs") && pers.getPersonNum() == 106)
                {
                    nigelBloggsPresent = true;
                }
            }
            assertTrue("Fred Flintstone was not returned!", fredFlintstonePresent);
            assertTrue("Barney Rubble was not returned!", barneyRubblePresent);
            assertTrue("Joe Bloggs was not returned!", joeBloggsPresent);
            assertTrue("Fred Bloggs was not returned!", nigelBloggsPresent);

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

    /**
     * Test basic generation of query with candidate and without the alias.
     */
    public void testBasicWithoutAlias()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            crit.select(candidate);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT DN_THIS FROM org.datanucleus.samples.annotations.models.company.Person DN_THIS", crit.toString());

            Query q = em.createQuery(crit);
            List<Person> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 4, results.size());
            Iterator<Person> iter = results.iterator();
            boolean nigelBloggsPresent = false;
            boolean joeBloggsPresent = false;
            boolean fredFlintstonePresent = false;
            boolean barneyRubblePresent = false;
            while (iter.hasNext())
            {
                Person pers = iter.next();
                if (pers.getFirstName().equals("Fred") && pers.getLastName().equals("Flintstone") && pers.getPersonNum() == 101)
                {
                    fredFlintstonePresent = true;
                }
                if (pers.getFirstName().equals("Barney") && pers.getLastName().equals("Rubble") && pers.getPersonNum() == 102)
                {
                    barneyRubblePresent = true;
                }
                if (pers.getFirstName().equals("Joe") && pers.getLastName().equals("Bloggs") && pers.getPersonNum() == 105)
                {
                    joeBloggsPresent = true;
                }
                if (pers.getFirstName().equals("Nigel") && pers.getLastName().equals("Bloggs") && pers.getPersonNum() == 106)
                {
                    nigelBloggsPresent = true;
                }
            }
            assertTrue("Fred Flintstone was not returned!", fredFlintstonePresent);
            assertTrue("Barney Rubble was not returned!", barneyRubblePresent);
            assertTrue("Joe Bloggs was not returned!", joeBloggsPresent);
            assertTrue("Fred Bloggs was not returned!", nigelBloggsPresent);

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

    /**
     * Test basic generation of query with candidate and alias and filter (<).
     */
    public void testBasicWithFilterLessThan()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");
            crit.select(candidate);

            Path ageField = candidate.get("age");
            Predicate ageLessThan = qb.lessThan(ageField, 36);
            crit.where(ageLessThan);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p FROM org.datanucleus.samples.annotations.models.company.Person p WHERE (p.age < 36)", crit.toString());

            Query q = em.createQuery(crit);
            List<Person> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Iterator<Person> iter = results.iterator();
            boolean fredFlintstonePresent = false;
            while (iter.hasNext())
            {
                Person pers = iter.next();
                if (pers.getFirstName().equals("Fred") && pers.getLastName().equals("Flintstone") && pers.getPersonNum() == 101)
                {
                    fredFlintstonePresent = true;
                }
            }
            assertTrue("Fred was not returned!", fredFlintstonePresent);

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

    /**
     * Test basic generation of query with candidate and alias and filter (UPPER/LOWER).
     */
    public void testBasicWithFilterStringUPPERLOWER()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");
            crit.select(candidate);

            Path firstNameField = candidate.get("firstName");
            Expression firstNameUpperExpr = qb.upper(firstNameField);
            Predicate firstNameUpperEquals = qb.equal(firstNameUpperExpr, "FRED");

            Path lastNameField = candidate.get("lastName");
            Expression lastNameLowerExpr = qb.lower(lastNameField);
            Predicate lastNameUpperEquals = qb.equal(lastNameLowerExpr, "flintstone");

            crit.where(firstNameUpperEquals, lastNameUpperEquals);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p FROM org.datanucleus.samples.annotations.models.company.Person p WHERE" +
                " (UPPER(p.firstName) = 'FRED') AND (LOWER(p.lastName) = 'flintstone')",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Person> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Iterator<Person> iter = results.iterator();
            boolean fredPresent = false;
            while (iter.hasNext())
            {
                Person pers = iter.next();
                if (pers.getFirstName().equals("Fred") && pers.getLastName().equals("Flintstone") && pers.getPersonNum() == 101)
                {
                    fredPresent = true;
                }
            }
            assertTrue("Fred was not returned!", fredPresent);

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

    /**
     * Test basic generation of query with candidate and alias and filter (using OR).
     */
    public void testBasicWithFilterOr()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");
            crit.select(candidate);

            Path firstNameField = candidate.get("firstName");
            Predicate firstName1 = qb.equal(firstNameField, "Fred");
            Predicate firstName2 = qb.equal(firstNameField, "Joe");
            Predicate eitherFirstName = qb.or(firstName1, firstName2);
            crit.where(eitherFirstName);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p FROM org.datanucleus.samples.annotations.models.company.Person p WHERE" +
                " ((p.firstName = 'Fred') OR (p.firstName = 'Joe'))",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Person> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 2, results.size());
            Iterator<Person> iter = results.iterator();
            boolean fredPresent = false;
            boolean joePresent = false;
            while (iter.hasNext())
            {
                Person pers = iter.next();
                if (pers.getFirstName().equals("Fred") && pers.getLastName().equals("Flintstone") && pers.getPersonNum() == 101)
                {
                    fredPresent = true;
                }
                else if (pers.getFirstName().equals("Joe") && pers.getLastName().equals("Bloggs") && pers.getPersonNum() == 105)
                {
                    joePresent = true;
                }
            }
            assertTrue("Fred was not returned!", fredPresent);
            assertTrue("Joe was not returned!", joePresent);

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

    /**
     * Test basic generation of query with candidate and alias, with ordering.
     */
    public void testBasicWithOrder()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");
            crit.select(candidate);

            Path firstNameField = candidate.get("firstName");
            Order orderFirstName = qb.desc(firstNameField);
            crit.orderBy(orderFirstName);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p FROM org.datanucleus.samples.annotations.models.company.Person p ORDER BY p.firstName DESC",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Person> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 4, results.size());
            Person per0 = results.get(0);
            Person per1 = results.get(1);
            Person per2 = results.get(2);
            Person per3 = results.get(3);
            if (!per0.getFirstName().equals("Nigel") || !per0.getLastName().equals("Bloggs") || per0.getPersonNum() != 106)
            {
                fail("Nigel Bloggs was not result 0");
            }
            if (!per1.getFirstName().equals("Joe") || !per1.getLastName().equals("Bloggs") || per1.getPersonNum() != 105)
            {
                fail("Joe Bloggs was not result 1");
            }
            if (!per2.getFirstName().equals("Fred") || !per2.getLastName().equals("Flintstone") || per2.getPersonNum() != 101)
            {
                fail("Fred Flintstone was not result 2");
            }
            if (!per3.getFirstName().equals("Barney") || !per3.getLastName().equals("Rubble") || per3.getPersonNum() != 102)
            {
                fail("Barney Rubble was not result 3");
            }

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

    /**
     * Test basic generation of query with candidate and alias and filter with input params.
     */
    public void testBasicWithFilterAndParams()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");
            crit.select(candidate);

            Path firstNameField = candidate.get("firstName");
            ParameterExpression param1 = qb.parameter(String.class, "param1");
            Predicate firstNameEquals = qb.equal(firstNameField, param1);

            Path lastNameField = candidate.get("lastName");
            ParameterExpression param2 = qb.parameter(String.class, "param2");
            Predicate lastNameEquals = qb.equal(lastNameField, param2);

            crit.where(firstNameEquals, lastNameEquals);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p FROM org.datanucleus.samples.annotations.models.company.Person p WHERE" +
                " (p.firstName = :param1) AND (p.lastName = :param2)",
                crit.toString());

            Query q = em.createQuery(crit);
            q.setParameter("param1", "Fred");
            q.setParameter("param2", "Flintstone");
            List<Person> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Iterator<Person> iter = results.iterator();
            boolean fredPresent = false;
            while (iter.hasNext())
            {
                Person pers = iter.next();
                if (pers.getFirstName().equals("Fred") && pers.getLastName().equals("Flintstone") && pers.getPersonNum() == 101)
                {
                    fredPresent = true;
                }
            }
            assertTrue("Fred was not returned!", fredPresent);

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

    /**
     * Test basic generation of query with result.
     */
    public void testBasicWithResult()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");

            Path firstNameField = candidate.get("firstName");
            Path lastNameField = candidate.get("lastName");
            crit.multiselect(firstNameField, lastNameField);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p.firstName,p.lastName FROM org.datanucleus.samples.annotations.models.company.Person p",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Object> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 4, results.size());
            Iterator<Object> iter = results.iterator();
            boolean fredExists = false;
            boolean barneyExists = false;
            boolean joeExists = false;
            boolean nigelExists = false;
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Row of results is not Object[]", obj instanceof Object[]);
                Object[] row = (Object[])obj;
                if (row[0].equals("Fred") && row[1].equals("Flintstone"))
                {
                    fredExists = true;
                }
                else if (row[0].equals("Barney") && row[1].equals("Rubble"))
                {
                    barneyExists = true;
                }
                else if (row[0].equals("Joe") && row[1].equals("Bloggs"))
                {
                    joeExists = true;
                }
                else if (row[0].equals("Nigel") && row[1].equals("Bloggs"))
                {
                    nigelExists = true;
                }
            }
            assertTrue("Fred wasnt in the results", fredExists);
            assertTrue("Barney wasnt in the results", barneyExists);
            assertTrue("Joe wasnt in the results", joeExists);
            assertTrue("Nigel wasnt in the results", nigelExists);

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

    /**
     * Test use of result with "concat".
     */
    public void testResultWithConcat()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");

            crit.multiselect(qb.concat(candidate.<String> get("firstName"), candidate.<String> get("lastName")));

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT CONCAT(p.firstName,p.lastName) FROM org.datanucleus.samples.annotations.models.company.Person p",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Object> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 4, results.size());
            Iterator<Object> iter = results.iterator();
            boolean fredExists = false;
            boolean barneyExists = false;
            boolean joeExists = false;
            boolean nigelExists = false;
            while (iter.hasNext())
            {
                Object obj = iter.next();
                if (obj.equals("FredFlintstone"))
                {
                    fredExists = true;
                }
                else if (obj.equals("BarneyRubble"))
                {
                    barneyExists = true;
                }
                else if (obj.equals("JoeBloggs"))
                {
                    joeExists = true;
                }
                else if (obj.equals("NigelBloggs"))
                {
                    nigelExists = true;
                }
            }
            assertTrue("Fred wasnt in the results", fredExists);
            assertTrue("Barney wasnt in the results", barneyExists);
            assertTrue("Joe wasnt in the results", joeExists);
            assertTrue("Nigel wasnt in the results", nigelExists);

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

    /**
     * Test basic generation of query with candidate and alias and filter using field of superclass.
     */
    public void testBasicWithInheritanceInFilter()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Employee> crit = qb.createQuery(Employee.class);
            Root<Employee> candidate = crit.from(Employee.class);
            candidate.alias("e");
            crit.select(candidate);

            Path firstNameField = candidate.get("firstName");
            Predicate firstNameEquals = qb.equal(firstNameField, "Joe");
            crit.where(firstNameEquals);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT e FROM org.datanucleus.samples.annotations.models.company.Employee e WHERE" +
                " (e.firstName = 'Joe')",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Person> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Iterator<Person> iter = results.iterator();
            boolean joePresent = false;
            while (iter.hasNext())
            {
                Person pers = iter.next();
                if (pers.getFirstName().equals("Joe") && pers.getLastName().equals("Bloggs") && pers.getPersonNum() == 105)
                {
                    joePresent = true;
                }
            }
            assertTrue("Joe Bloggs was not returned!", joePresent);

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

    /**
     * Test basic generation of query with candidate and alias, and FROM join on 1-1.
     */
    public void testBasicWithFromJoinOneToOne()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Employee> crit = qb.createQuery(Employee.class);
            Root<Employee> candidate = crit.from(Employee.class);
            candidate.alias("e");
            crit.select(candidate);

            Metamodel model = emf.getMetamodel();
            ManagedType empType = model.managedType(Employee.class);
            Attribute bAttr = empType.getAttribute("account");
            Join accountJoin = candidate.join((SingularAttribute)bAttr);
            accountJoin.alias("a");

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT e FROM org.datanucleus.samples.annotations.models.company.Employee e JOIN e.account a",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Employee> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 2, results.size());
            Iterator<Employee> iter = results.iterator();
            boolean joeExists = false;
            boolean nigelExists = false;
            while (iter.hasNext())
            {
                Employee emp = iter.next();
                if (emp.getFirstName().equals("Nigel") && emp.getLastName().equals("Bloggs") && emp.getPersonNum() == 106)
                {
                    nigelExists = true;
                }
                else if (emp.getFirstName().equals("Joe") && emp.getLastName().equals("Bloggs") && emp.getPersonNum() == 105)
                {
                    joeExists = true;
                }
            }
            assertTrue("Nigel not present", nigelExists);
            assertTrue("Joe not present", joeExists);

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

    /**
     * Test basic generation of query with candidate and alias, and FROM join on 1-1.
     */
    public void testBasicWithFromJoinOneToOneUsingAttributeName()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Employee> crit = qb.createQuery(Employee.class);
            Root<Employee> candidate = crit.from(Employee.class);
            candidate.alias("e");
            crit.select(candidate);

            Join accountJoin = candidate.join("e.account");
            accountJoin.alias("a");

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT e FROM org.datanucleus.samples.annotations.models.company.Employee e JOIN e.account a",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Employee> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 2, results.size());
            Iterator<Employee> iter = results.iterator();
            boolean joeExists = false;
            boolean nigelExists = false;
            while (iter.hasNext())
            {
                Employee emp = iter.next();
                if (emp.getFirstName().equals("Nigel") && emp.getLastName().equals("Bloggs") && emp.getPersonNum() == 106)
                {
                    nigelExists = true;
                }
                else if (emp.getFirstName().equals("Joe") && emp.getLastName().equals("Bloggs") && emp.getPersonNum() == 105)
                {
                    joeExists = true;
                }
            }
            assertTrue("Nigel not present", nigelExists);
            assertTrue("Joe not present", joeExists);

            tx.rollback();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown during test", e);
            fail("Exception caught during test : " + e.getMessage());
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

    /**
     * Test basic generation of query with candidate and alias, and FROM join on 1-N.
     */
    public void testBasicWithFromJoinOneToMany()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Farm> crit = qb.createQuery(Farm.class);
            Root<Farm> candidate = crit.from(Farm.class);
            candidate.alias("f");
            crit.select(candidate);

            Metamodel model = emf.getMetamodel();
            ManagedType farmType = model.managedType(Farm.class);
            Attribute animalAttr = farmType.getAttribute("animals");
            Join animalJoin = candidate.join((ListAttribute)animalAttr);
            animalJoin.alias("a");

            Path nameField = animalJoin.get("name");
            Predicate nameEquals = qb.equal(nameField, "Woolly Sheep");
            crit.where(nameEquals);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT f FROM org.datanucleus.samples.annotations.one_many.bidir.Farm f JOIN f.animals a WHERE (a.name = 'Woolly Sheep')",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Farm> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Farm farm = (Farm)results.get(0);
            assertEquals("Farm is incorrect", "Kiwi Farm", farm.getName());

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

    /**
     * Test projection of many-to-one field.
     */
    public void testProjectionOnManyToOne()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Farm> crit = qb.createQuery(Farm.class);
            Root<Animal> candidate = crit.from(Animal.class);
            candidate.alias("a");
            Path<Farm> farmPath = candidate.get("farm");
            crit.select(farmPath);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT a.farm FROM org.datanucleus.samples.annotations.one_many.bidir.Animal a",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Farm> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 3, results.size());
            Object result = results.get(0);
            assertNotNull(result);
            assertTrue("Result is of incorrect type", result instanceof Farm);

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

    /**
     * Test basic generation of query with candidate and alias, and FROM join on 1-N (using attr name).
     */
    public void testBasicWithFromJoinOneToManyUsingAttributeName()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Farm> crit = qb.createQuery(Farm.class);
            Root<Farm> candidate = crit.from(Farm.class);
            candidate.alias("f");
            crit.select(candidate);

            Join animalJoin = candidate.join("f.animals");
            animalJoin.alias("a");

            Path nameField = animalJoin.get("name");
            Predicate nameEquals = qb.equal(nameField, "Woolly Sheep");
            crit.where(nameEquals);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT f FROM org.datanucleus.samples.annotations.one_many.bidir.Farm f JOIN f.animals a WHERE (a.name = 'Woolly Sheep')",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Farm> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Farm farm = (Farm)results.get(0);
            assertEquals("Farm is incorrect", "Kiwi Farm", farm.getName());

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

    /**
     * Test simple use of a subquery.
     */
    public void testSubqueryUncorrelated()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Employee> crit = qb.createQuery(Employee.class);
            Root<Employee> candidate = crit.from(Employee.class);
            candidate.alias("e");
            crit.select(candidate);

            Path salaryField = candidate.get("salary");

            Subquery<Double> subCrit = crit.subquery(Double.class);
            Root<Employee> subCandidate = subCrit.from(Employee.class);
            subCandidate.alias("e2");
            Path salary2Field = subCandidate.get("salary");
            Subquery<Double> avgSalary = subCrit.select(qb.avg(salary2Field));

            Predicate lessThanAvgSalary = qb.lessThan(salaryField, avgSalary);
            crit.where(lessThanAvgSalary);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT AVG(e2.salary) FROM org.datanucleus.samples.annotations.models.company.Employee e2",
                subCrit.toString());
            // Cannot check on precise query since the subquery name will be a random number
            assertTrue("Main query containing subquery is incorrect",
                crit.toString().startsWith("SELECT e FROM org.datanucleus.samples.annotations.models.company.Employee e WHERE (e.salary < SUB"));

            Query q = em.createQuery(crit);
            List<Employee> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Employee emp = (Employee)results.get(0);
            assertEquals("Employee first name is incorrect", "Joe", emp.getFirstName());
            assertEquals("Employee last name is incorrect", "Bloggs", emp.getLastName());

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

    /**
     * Test use of an EXISTS subquery.
     */
    public void testSubqueryExists()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");
            crit.select(candidate);

            Subquery<Person> subCrit = crit.subquery(Person.class);
            Root<Person> subCandidate = subCrit.from(Person.class);
            subCandidate.alias("p2");
            subCrit.select(subCandidate);
            Path bestFriendField = subCandidate.get("bestFriend");
            Predicate bestFriendEqual = qb.equal(bestFriendField, candidate);
            subCrit.where(bestFriendEqual);

            Predicate existsBestFriend = qb.exists(subCrit);
            crit.where(existsBestFriend);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p2 FROM org.datanucleus.samples.annotations.models.company.Person p2 WHERE (p2.bestFriend = p)",
                subCrit.toString());
            // Cannot check on precise query since the subquery name will be a random number
            assertTrue("Main query containing subquery is incorrect",
                crit.toString().startsWith("SELECT p FROM org.datanucleus.samples.annotations.models.company.Person p WHERE EXISTS "));

            Query q = em.createQuery(crit);
            List<Employee> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Person p = (Person)results.get(0);
            assertEquals("Employee first name is incorrect", "Fred", p.getFirstName());
            assertEquals("Employee last name is incorrect", "Flintstone", p.getLastName());

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

    /**
     * Test generation of filter with IN.
     */
    public void testFilterWithIn()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");
            crit.select(candidate);

            Collection<String> nameOptions = new HashSet<String>();
            nameOptions.add("Fred");
            nameOptions.add("George");
            Path firstNameField = candidate.get("firstName");
            Predicate nameIn = firstNameField.in(nameOptions);

            crit.where(nameIn);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p FROM org.datanucleus.samples.annotations.models.company.Person p WHERE p.firstName IN ('Fred','George')",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Person> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Iterator<Person> iter = results.iterator();
            boolean fredPresent = false;
            while (iter.hasNext())
            {
                Person pers = iter.next();
                if (pers.getFirstName().equals("Fred") && pers.getLastName().equals("Flintstone") && pers.getPersonNum() == 101)
                {
                    fredPresent = true;
                }
            }
            assertTrue("Fred was not returned!", fredPresent);

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

    /**
     * Test generation of filter with IN.
     */
    public void testInUsingQueryBuilder()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Person> crit = qb.createQuery(Person.class);
            Root<Person> candidate = crit.from(Person.class);
            candidate.alias("p");
            crit.select(candidate);

            // form IN via QueryBuilder
            In firstNameIn = qb.in(candidate.get("firstName"));
            firstNameIn.value("Fred");
            firstNameIn.value("George");

            crit.where(firstNameIn);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p FROM org.datanucleus.samples.annotations.models.company.Person p WHERE p.firstName IN ('Fred','George')",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Person> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Iterator<Person> iter = results.iterator();
            boolean fredPresent = false;
            while (iter.hasNext())
            {
                Person pers = iter.next();
                if (pers.getFirstName().equals("Fred") && pers.getLastName().equals("Flintstone") && pers.getPersonNum() == 101)
                {
                    fredPresent = true;
                }
            }
            assertTrue("Fred was not returned!", fredPresent);

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

    /**
     * Test generation of filter with TREAT.
     */
    public void testTREATInWhereClause()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Qualification> crit = qb.createQuery(Qualification.class);
            Root<Qualification> candidate = crit.from(Qualification.class);
            candidate.alias("q");
            crit.select(candidate);

            Path<Employee> empPath = qb.treat(candidate.get("person"), Employee.class);
            Path agePath = empPath.get("age");
            crit.where(qb.gt(agePath, 40));

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT q FROM org.datanucleus.samples.annotations.models.company.Qualification q WHERE (TREAT(q.person AS " + Employee.class.getName() + ").age > 40)",
                crit.toString());

            Query q = em.createQuery(crit);
            List<Qualification> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());

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
}