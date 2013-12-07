/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.api.jdo.JDOQuery;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.query.compiler.JDOQLCompiler;
import org.datanucleus.query.compiler.JavaQueryCompiler;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.query.evaluator.JDOQLEvaluator;
import org.datanucleus.query.evaluator.JavaQueryEvaluator;
import org.datanucleus.store.query.Query;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.one_many.collection.SetHolder;
import org.jpox.samples.one_many.map.MapHolder;
import org.jpox.samples.one_many.unidir_2.GroupMember;
import org.jpox.samples.one_many.unidir_2.UserGroup;

/**
 * Tests for generic JDOQL in-memory evaluator.
 * These are really unit tests for code in "core" but we need enhanced classes to run it so is placed here.
 * [adding as a unit test to "core" would mean that "core" is dependent on "enhancer", hence cyclic]
 */
public class JDOQLEvaluatorTest extends JDOPersistenceTestCase
{
    public JDOQLEvaluatorTest(String name)
    {
        super(name);
    }

    /**
     * Test of filter with == and != operators.
     */
    public void testFilterEqualityOperator()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            Person p1 = new Person(101, "Mickey", "Mouse", "mickey.mouse@warnerbros.com");
            Person p2 = new Person(102, "Donald", "Duck", "donald.duck@warnerbros.com");
            Person p3 = new Person(103, "Minnie", "Mouse", "minnie.mouse@warnerbros.com");
            instances.add(p1);
            instances.add(p2);
            instances.add(p3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(Person.class, "firstName == 'Mickey'");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            Person p = (Person)results.get(0);
            assertEquals("Result instance has wrong first name", "Mickey", p.getFirstName());
            assertEquals("Result instance has wrong last name", "Mouse", p.getLastName());
            assertEquals("Person number of result instance is wrong", 101, p.getPersonNum());

            // Compile the query
            q = (JDOQuery)pm.newQuery(Person.class, "lastName != 'Mouse'");
            query = q.getInternalQuery();
            clr = query.getExecutionContext().getClassLoaderResolver();
            mmgr = query.getExecutionContext().getMetaDataManager();
            compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            p = (Person)results.get(0);
            assertEquals("Result instance has wrong first name", "Donald", p.getFirstName());
            assertEquals("Result instance has wrong last name", "Duck", p.getLastName());
            assertEquals("Person number of result instance is wrong", 102, p.getPersonNum());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with OR and equality operator.
     */
    public void testFilterOrWithEqualityOperator()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            Person p1 = new Person(101, "Mickey", "Mouse", "mickey.mouse@warnerbros.com");
            Person p2 = new Person(102, "Donald", "Duck", "donald.duck@warnerbros.com");
            Person p3 = new Person(103, "Minnie", "Mouse", "minnie.mouse@warnerbros.com");
            instances.add(p1);
            instances.add(p2);
            instances.add(p3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(Person.class, "firstName == 'Mickey' || lastName == 'Mouse'");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 2, results.size());
            Iterator iter = results.iterator();
            while (iter.hasNext())
            {
                Person p = (Person)iter.next();
                if (p.getFirstName().equals("Mickey"))
                {
                    assertEquals("Last name of result instance is wrong", "Mouse", p.getLastName());
                    assertEquals("Person number of result instance is wrong", 101, p.getPersonNum());
                }
                else if (p.getFirstName().equals("Minnie"))
                {
                    assertEquals("Last name of result instance is wrong", "Mouse", p.getLastName());
                    assertEquals("Person number of result instance is wrong", 103, p.getPersonNum());
                }
                else
                {
                    fail("Incorrect Person found by query " + p.asString());
                }
            }

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with AND and equality operator.
     */
    public void testFilterAndWithEqualityOperator()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            Person p1 = new Person(101, "Mickey", "Mouse", "mickey.mouse@warnerbros.com");
            Person p2 = new Person(102, "Donald", "Duck", "donald.duck@warnerbros.com");
            Person p3 = new Person(103, "Minnie", "Mouse", "minnie.mouse@warnerbros.com");
            instances.add(p1);
            instances.add(p2);
            instances.add(p3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(Person.class, "firstName == 'Mickey' && lastName == 'Mouse'");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            Person p = (Person)results.get(0);
            assertEquals("Result instance has wrong first name", "Mickey", p.getFirstName());
            assertEquals("Result instance has wrong last name", "Mouse", p.getLastName());
            assertEquals("Person number of result instance is wrong", 101, p.getPersonNum());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with String.startsWith().
     */
    public void testFilterStringStartsWith()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            Person p1 = new Person(101, "Mickey", "Mouse", "mickey.mouse@warnerbros.com");
            Person p2 = new Person(102, "Donald", "Duck", "donald.duck@warnerbros.com");
            Person p3 = new Person(103, "Minnie", "Mouse", "minnie.mouse@warnerbros.com");
            instances.add(p1);
            instances.add(p2);
            instances.add(p3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(Person.class, "firstName.startsWith('Mi')");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 2, results.size());
            Iterator iter = results.iterator();
            while (iter.hasNext())
            {
                Person p = (Person)iter.next();
                if (p.getFirstName().equals("Mickey"))
                {
                    assertEquals("Last name of result instance is wrong", "Mouse", p.getLastName());
                    assertEquals("Person number of result instance is wrong", 101, p.getPersonNum());
                }
                else if (p.getFirstName().equals("Minnie"))
                {
                    assertEquals("Last name of result instance is wrong", "Mouse", p.getLastName());
                    assertEquals("Person number of result instance is wrong", 103, p.getPersonNum());
                }
                else
                {
                    fail("Incorrect Person found by query " + p.asString());
                }
            }

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with String.endsWith().
     */
    public void testFilterStringEndsWith()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            Person p1 = new Person(101, "Mickey", "Mouse", "mickey.mouse@warnerbros.com");
            Person p2 = new Person(102, "Donald", "Duck", "donald.duck@warnerbros.com");
            Person p3 = new Person(103, "Minnie", "Mouse", "minnie.mouse@warnerbros.com");
            instances.add(p1);
            instances.add(p2);
            instances.add(p3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(Person.class, "firstName.endsWith('d')");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            Person p = (Person)results.get(0);
            assertEquals("Result instance has wrong first name", "Donald", p.getFirstName());
            assertEquals("Result instance has wrong last name", "Duck", p.getLastName());
            assertEquals("Person number of result instance is wrong", 102, p.getPersonNum());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with ">", "<" and "+".
     */
    public void testFilterGreaterThanLessThan()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            Person p1 = new Person(101, "Mickey", "Mouse", "mickey.mouse@warnerbros.com");
            p1.setAge(34);
            Person p2 = new Person(102, "Donald", "Duck", "donald.duck@warnerbros.com");
            p2.setAge(38);
            Person p3 = new Person(103, "Minnie", "Mouse", "minnie.mouse@warnerbros.com");
            p3.setAge(31);
            instances.add(p1);
            instances.add(p2);
            instances.add(p3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(Person.class, "age > 34");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            Person p = (Person)results.get(0);
            assertEquals("Result instance has wrong first name", "Donald", p.getFirstName());
            assertEquals("Result instance has wrong last name", "Duck", p.getLastName());
            assertEquals("Person number of result instance is wrong", 102, p.getPersonNum());
            assertEquals("Age of result instance is wrong", 38, p.getAge());

            // Compile the query
            q = (JDOQuery)pm.newQuery(Person.class, "age+2 < 35");
            query = q.getInternalQuery();
            clr = query.getExecutionContext().getClassLoaderResolver();
            mmgr = query.getExecutionContext().getMetaDataManager();
            compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            p = (Person)results.get(0);
            assertEquals("Result instance has wrong first name", "Minnie", p.getFirstName());
            assertEquals("Result instance has wrong last name", "Mouse", p.getLastName());
            assertEquals("Person number of result instance is wrong", 103, p.getPersonNum());
            assertEquals("Age of result instance is wrong", 31, p.getAge());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with "instanceof".
     */
    public void testFilterInstanceOf()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            Person p1 = new Person(101, "Mickey", "Mouse", "mickey.mouse@warnerbros.com");
            p1.setAge(34);
            Employee p2 = new Employee(102, "Donald", "Duck", "donald.duck@warnerbros.com", 13400.0f, "12345");
            p2.setAge(38);
            Person p3 = new Person(103, "Minnie", "Mouse", "minnie.mouse@warnerbros.com");
            p3.setAge(31);
            instances.add(p1);
            instances.add(p2);
            instances.add(p3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(Person.class, "this instanceof " + Employee.class.getName());
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            Person p = (Person)results.get(0);
            assertEquals("Result instance has wrong first name", "Donald", p.getFirstName());
            assertEquals("Result instance has wrong last name", "Duck", p.getLastName());
            assertEquals("Person number of result instance is wrong", 102, p.getPersonNum());
            assertEquals("Age of result instance is wrong", 38, p.getAge());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with !(condition).
     */
    public void testFilterNegate()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            Person p1 = new Person(101, "Mickey", "Mouse", "mickey.mouse@warnerbros.com");
            p1.setAge(34);
            Employee p2 = new Employee(102, "Donald", "Duck", "donald.duck@warnerbros.com", 13400.0f, "12345");
            p2.setAge(38);
            Person p3 = new Person(103, "Minnie", "Mouse", "minnie.mouse@warnerbros.com");
            p3.setAge(31);
            instances.add(p1);
            instances.add(p2);
            instances.add(p3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(Person.class, "!(age > 32)");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            Person p = (Person)results.get(0);
            assertEquals("Result instance has wrong first name", "Minnie", p.getFirstName());
            assertEquals("Result instance has wrong last name", "Mouse", p.getLastName());
            assertEquals("Person number of result instance is wrong", 103, p.getPersonNum());
            assertEquals("Age of result instance is wrong", 31, p.getAge());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with collectionField.contains(nonPC).
     */
    public void testFilterCollectionContainsNonPC()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            SetHolder holder1 = new SetHolder("First");
            SetHolder holder2 = new SetHolder("Second");
            SetHolder holder3 = new SetHolder("Third");
            holder1.getJoinSetNonPC1().add("First Element");
            holder2.getJoinSetNonPC1().add("First Element");
            holder2.getJoinSetNonPC1().add("Second Element");
            holder3.getJoinSetNonPC1().add("Second Element");
            holder3.getJoinSetNonPC1().add("Third Element");
            instances.add(holder1);
            instances.add(holder2);
            instances.add(holder3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(SetHolder.class, "joinSetNonPC1.contains('Third Element')");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            SetHolder holder = (SetHolder)results.get(0);
            assertEquals("Result instance has wrong name", "Third", holder.getName());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with mapField.containsKey(nonPC).
     */
    public void testFilterMapContainsKeyNonPC()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            MapHolder holder1 = new MapHolder("First");
            MapHolder holder2 = new MapHolder("Second");
            MapHolder holder3 = new MapHolder("Third");
            holder1.getJoinMapNonNon().put("First", "First Element");
            holder2.getJoinMapNonNon().put("First", "First Element");
            holder2.getJoinMapNonNon().put("Second", "Second Element");
            holder3.getJoinMapNonNon().put("Second", "Second Element");
            holder3.getJoinMapNonNon().put("Third", "Third Element");
            instances.add(holder1);
            instances.add(holder2);
            instances.add(holder3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(MapHolder.class, "joinMapNonNon.containsKey('Third')");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            MapHolder holder = (MapHolder)results.get(0);
            assertEquals("Result instance has wrong name", "Third", holder.getName());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with mapField.containsValue(nonPC).
     */
    public void testFilterMapContainsValueNonPC()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            MapHolder holder1 = new MapHolder("First");
            MapHolder holder2 = new MapHolder("Second");
            MapHolder holder3 = new MapHolder("Third");
            holder1.getJoinMapNonNon().put("First", "First Element");
            holder2.getJoinMapNonNon().put("First", "First Element");
            holder2.getJoinMapNonNon().put("Second", "Second Element");
            holder3.getJoinMapNonNon().put("Second", "Second Element");
            holder3.getJoinMapNonNon().put("Third", "Third Element");
            instances.add(holder1);
            instances.add(holder2);
            instances.add(holder3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(MapHolder.class, "joinMapNonNon.containsValue('Third Element')");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            MapHolder holder = (MapHolder)results.get(0);
            assertEquals("Result instance has wrong name", "Third", holder.getName());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test of filter with collectionField.contains(element).
     */
    public void testFilterCollectionContains()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            UserGroup grp1 = new UserGroup(101, "First Group");
            UserGroup grp2 = new UserGroup(102, "Second Group");
            UserGroup grp3 = new UserGroup(103, "Third Group");
            GroupMember mem1 = new GroupMember(201, "Donald Duck");
            GroupMember mem2 = new GroupMember(202, "Mickey Mouse");
            GroupMember mem3 = new GroupMember(203, "Minnie Mouse");
            grp1.getMembers().add(mem1);
            grp2.getMembers().add(mem1);
            grp2.getMembers().add(mem2);
            grp3.getMembers().add(mem2);
            grp3.getMembers().add(mem3);
            instances.add(grp1);
            instances.add(grp2);
            instances.add(grp3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(UserGroup.class, "members.contains(el) && el.name == 'Minnie Mouse'");
            q.declareVariables(GroupMember.class.getName() + " el");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(new HashMap(), null);

            // Execute the query
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, null, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            UserGroup grp = (UserGroup)results.get(0);
            assertEquals("Result instance has wrong name", "Third Group", grp.getName());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.info(">> Unexpected exception thrown during test", e);
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test use of input params, positional.
     */
    public void testImplicitParametersPositional()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            Person p1 = new Person(101, "Mickey", "Mouse", "mickey.mouse@warnerbros.com");
            Person p2 = new Person(102, "Donald", "Duck", "donald.duck@warnerbros.com");
            Person p3 = new Person(103, "Minnie", "Mouse", "minnie.mouse@warnerbros.com");
            instances.add(p1);
            instances.add(p2);
            instances.add(p3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(Person.class, "firstName.endsWith(:param)");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(null, null);

            // Execute the query
            HashMap paramValues = new HashMap();
            paramValues.put(new Integer(0), "ald"); // Simulate what org.datanucleus.store.query.Query does
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, paramValues, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            Person p = (Person)results.get(0);
            assertEquals("Result instance has wrong first name", "Donald", p.getFirstName());
            assertEquals("Result instance has wrong last name", "Duck", p.getLastName());
            assertEquals("Person number of result instance is wrong", 102, p.getPersonNum());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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

    /**
     * Test use of input params, positional, with one repeated before the other appears
     */
    public void testImplicitParametersPositionalRepeated()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create some instances to query over
            List instances = new ArrayList();
            Person p1 = new Person(101, "Mickey", "Mouse", "mickey.mouse@warnerbros.com");
            Person p2 = new Person(102, "Donald", "Duck", "donald.duck@warnerbros.com");
            Person p3 = new Person(103, "Minnie", "Mouse", "minnie.mouse@warnerbros.com");
            instances.add(p1);
            instances.add(p2);
            instances.add(p3);

            // Compile the query
            JDOQuery q = (JDOQuery)pm.newQuery(Person.class,
                "firstName.endsWith(:param1) && !lastName.endsWith(:param1)"
                + " && lastName.length() == :param2");
            Query query = q.getInternalQuery();
            ClassLoaderResolver clr = query.getExecutionContext().getClassLoaderResolver();
            MetaDataManager mmgr = query.getExecutionContext().getMetaDataManager();
            JavaQueryCompiler compiler = new JDOQLCompiler(mmgr, clr, 
                null, query.getCandidateClass(), null, 
                query.getFilter(), query.getParsedImports(), query.getOrdering(), query.getResult(), 
                query.getGrouping(), query.getHaving(), query.getExplicitParameters(), query.getExplicitVariables(), null);
            QueryCompilation compilation = compiler.compile(null, null);

            // Execute the query
            HashMap paramValues = new HashMap();
            paramValues.put(new Integer(0), "ald"); // Simulate what org.datanucleus.store.query.Query does
            paramValues.put(new Integer(1), 4);
            JavaQueryEvaluator eval = new JDOQLEvaluator(query, instances, compilation, paramValues, clr);
            List results = (List)eval.execute(true, true, true, true, true);
            assertEquals("Number of result instances was wrong", 1, results.size());
            Person p = (Person)results.get(0);
            assertEquals("Result instance has wrong first name", "Donald", p.getFirstName());
            assertEquals("Result instance has wrong last name", "Duck", p.getLastName());
            assertEquals("Person number of result instance is wrong", 102, p.getPersonNum());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown during query execution " + e.getMessage());
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
}
