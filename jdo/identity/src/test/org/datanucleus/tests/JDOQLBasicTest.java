/**********************************************************************
Copyright (c) 2003 Mike Martin (TJDO) and others. All rights reserved.
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
2004 Erik Bengtson - added many many tests
2005 Andy Jefferson - added setRange, single-quote tests and more
2005 Andy Jefferson - changed all tests to clean up data before end
2006 Andy Jefferson - added date/time method test
    ...
***********************************************************************/
package org.datanucleus.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jdo.Extent;
import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import junit.framework.Assert;

import org.datanucleus.PropertyNames;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.util.StringUtils;
import org.jpox.samples.inheritance.PBase;
import org.jpox.samples.inheritance.PSub1;
import org.jpox.samples.inheritance.PSub2;
import org.jpox.samples.linkedlist.DoubleLink;
import org.jpox.samples.models.company.CompanyHelper;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.InsuranceDepartment;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.Office;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.Qualification;
import org.jpox.samples.one_many.map.MapHolder;
import org.jpox.samples.one_many.map.MapValueItem;
import org.jpox.samples.one_many.unidir_2.ExpertGroupMember;
import org.jpox.samples.one_many.unidir_2.ModeratedUserGroup;
import org.jpox.samples.one_many.unidir_2.UserGroup;
import org.jpox.samples.one_one.bidir_3.AbstractJournal;
import org.jpox.samples.one_one.bidir_3.ElectronicJournal;
import org.jpox.samples.one_one.bidir_3.PrintJournal;
import org.jpox.samples.persistentinterfaces.ComputerPeripheral;
import org.jpox.samples.persistentinterfaces.Keyboard;
import org.jpox.samples.persistentinterfaces.Mouse;
import org.jpox.samples.reachability.ReachableItem;
import org.jpox.samples.types.basic.BasicTypeHolder;
import org.jpox.samples.types.basic.DateHolder;

/**
 * Tests for JDOQL basic operations.
 */
public class JDOQLBasicTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public JDOQLBasicTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Employee.class,
                    Manager.class,
                    Person.class,
                    Department.class, 
                    InsuranceDepartment.class,
                    Office.class,
                    Qualification.class,
                    PBase.class,
                    PSub1.class,
                    PSub2.class,
                    Mouse.class,
                    Keyboard.class
                });
            initialised = true;
        }        
    }

    /**
     * Test the checking for an invalid operator "=" in a JDOQL query.
     *
     */
    public void testInvalidOperator()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.begin();

        // Test checking for an "=" operator which isnt supported in JDOQL (should be "==")
        try
        {
            Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false),"serialNo = value");
            q.declareParameters("String value");
            q.compile();
            q.closeAll();
            fail("JDOQL query including the operator = has succeeded, yet this is invalid in JDOQL!");
        }
        catch (Exception e)
        {
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * Test for the use of declareImports for defining types of parameters.
     **/
    public void testDeclareImports()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Test the use of String (java.lang.*) without import
            // This likely gives a warning in the Log about not using an explicit
            // import, but shouldn't throw an Exception. 
            try
            {
                Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false),"serialNo.startsWith(value)");
                q.declareParameters("String value");
                q.compile();
                q.closeAll();
            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
                fail(e.getMessage());
            }

            // Test of use of wildcard syntax for imports
            try
            {
                Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Manager.class, false),"((Employee)this).salary > 0");
                q.declareImports("import org.jpox.samples.*; import org.jpox.samples.models.company.*");
                q.closeAll();
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }

            // Test of use of imports for parameter types
            try
            {
                Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.declareImports("import org.jpox.samples.*; import org.jpox.samples.models.company.*;");
                q.declareParameters("org.jpox.samples.models.company.Employee m");
                q.setFilter("this.salary > m.salary");
                q.execute(new Employee(1,"","","",0,""));
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                LOG.error("Exception during test", e);
                fail(e.getMessage());
            }

            // Test for use of ??
            try
            {
                Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.declareImports("import org.jpox.samples.models.company.*;");
                q.setFilter("this.salary > 0");
                q.compile();
                q.closeAll();
            }
            catch (Exception e)
            {
                fail(e.getMessage());
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
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }
 
    /**
     * Test for the specification of candidate collections
     **/
    public void testCandidateCollection()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            runPrepareTestCandidateCollection(pm);
            runTestCandidateCollection(pm);
        }
        finally
        {
            pm.close();

            // Clean out our data
            clean(Employee.class);
        }
    }

    HashSet allEmployeeIds = new HashSet();
    HashSet warnerEmployeeIds = new HashSet();
    HashSet expectedEmployeeIds = new HashSet();

    /**
     * Tests the map get method
     */
    protected void runPrepareTestCandidateCollection(PersistenceManager pm)
    {
        allEmployeeIds = new HashSet();
        warnerEmployeeIds = new HashSet();
        expectedEmployeeIds = new HashSet();
        
        Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
        Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
        Employee roadrunner = new Employee(4,"Road","Runner","road.runner@warnerbros.com",11,"serial 4"); //Beep! Beep!
        Employee coyote = new Employee(5,"Wile","E. Coyote","wile.coyote@acme.com",9,"serial 5"); //I hate the gravity
        Employee duck = new Employee(6,"Daffy","Duck","daffy.duck@warnerbros.com",7,"serial 6"); //paranoid, and neurotic
        Employee pepe = new Employee(7,"Pepe","le Pew","pepe.lepew@warnerbros.com",8,"serial 7"); //You are my peanut.

        Transaction tx = pm.currentTransaction();
        pm.currentTransaction().setNontransactionalRead(true);
        try
        {
            tx.begin();
            pm.newQuery(Employee.class).deletePersistentAll();
            pm.makePersistent(woody);
            pm.makePersistent(bart);
            pm.makePersistent(bunny);
            pm.makePersistent(roadrunner);
            pm.makePersistent(coyote);
            pm.makePersistent(duck);
            pm.makePersistent(pepe);
            tx.commit();
            Object id = JDOHelper.getObjectId(woody);
            allEmployeeIds.add(id);
            id = JDOHelper.getObjectId(bart);
            allEmployeeIds.add(id);
            id = JDOHelper.getObjectId(bunny);
            allEmployeeIds.add(id);
            warnerEmployeeIds.add(id);
            expectedEmployeeIds.add(id);
            id = JDOHelper.getObjectId(roadrunner);
            allEmployeeIds.add(id);
            warnerEmployeeIds.add(id);
            expectedEmployeeIds.add(id);
            id = JDOHelper.getObjectId(coyote);
            allEmployeeIds.add(id);
            warnerEmployeeIds.add(id);
            id = JDOHelper.getObjectId(duck);
            allEmployeeIds.add(id);
            warnerEmployeeIds.add(id);
            id = JDOHelper.getObjectId(pepe);
            allEmployeeIds.add(id);
            warnerEmployeeIds.add(id);
        }
        catch (JDOException jdoe)
        {
            LOG.error("Exception thrown preparing test for candidate collection", jdoe);
            throw jdoe;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
        }
    }

    protected void runTestCandidateCollection(PersistenceManager pm)
    {
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            try
            {
                Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                HashSet results = new HashSet((Collection) q.execute());
                Collection resultIds = new HashSet();
                Iterator iter = results.iterator();
                while (iter.hasNext())
                {
                    resultIds.add(JDOHelper.getObjectId(iter.next()));
                }
                assertTrue("Expected result was all candidates elements, but received: " + StringUtils.collectionToString(resultIds), 
                    resultIds.equals(allEmployeeIds));
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                LOG.error("Exception thrown in querying ", e);
                fail(e.getMessage());
            }
            try
            {
                Collection candidates = new HashSet();
                candidates.addAll(pm.getObjectsById(warnerEmployeeIds));

                Query q = pm.newQuery(org.jpox.samples.models.company.Employee.class, candidates);
                HashSet results = new HashSet((Collection) q.execute());
                Collection resultIds = new HashSet();
                Iterator iter = results.iterator();
                while (iter.hasNext())
                {
                    resultIds.add(JDOHelper.getObjectId(iter.next()));
                }
                assertTrue("Expected result was warner candidate elements, but received: " + StringUtils.collectionToString(resultIds), 
                    resultIds.equals(warnerEmployeeIds));
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            try
            {
                Query q = pm.newQuery(org.jpox.samples.models.company.Employee.class, (Collection) null);
                HashSet results = new HashSet((Collection) q.execute());
                Collection resultIds = new HashSet();
                Iterator iter = results.iterator();
                while (iter.hasNext())
                {
                    resultIds.add(JDOHelper.getObjectId(iter.next()));
                }
                assertTrue("Expected result was all candidate elements, but received: " + StringUtils.collectionToString(resultIds), 
                    resultIds.equals(allEmployeeIds));
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            try
            {
                Query q = pm.newQuery(org.jpox.samples.models.company.Employee.class);
                q.setCandidates((Collection) null);
                HashSet results = new HashSet((Collection) q.execute());
                Collection resultIds = new HashSet();
                Iterator iter = results.iterator();
                while (iter.hasNext())
                {
                    resultIds.add(JDOHelper.getObjectId(iter.next()));
                }
                assertTrue("Expected result was warner candidate elements, but received: " + StringUtils.collectionToString(resultIds), 
                    resultIds.equals(allEmployeeIds));
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            try
            {
                Query q = pm.newQuery(org.jpox.samples.models.company.Employee.class, new HashSet());
                HashSet results = new HashSet((Collection) q.execute());
                assertEquals("Number of elements is wrong", 0, results.size());
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            try
            {
                Query q = pm.newQuery(org.jpox.samples.models.company.Employee.class);
                q.setCandidates(new HashSet());
                HashSet results = new HashSet((Collection) q.execute());
                assertEquals("Number of elements is wrong", 0, results.size());
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            try
            {
                Query q = pm.newQuery(org.jpox.samples.models.company.Employee.class);
                q.setCandidates(new HashSet());
                q.setCandidates((Collection) null); // Replaces collection with null, so all candidates
                HashSet results = new HashSet((Collection) q.execute());
                Collection resultIds = new HashSet();
                Iterator iter = results.iterator();
                while (iter.hasNext())
                {
                    resultIds.add(JDOHelper.getObjectId(iter.next()));
                }
                assertTrue("Expected result was all candidate elements, but received: " + StringUtils.collectionToString(resultIds), 
                    resultIds.equals(allEmployeeIds));
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            try
            {
                Query q = pm.newQuery(org.jpox.samples.models.company.Employee.class);
                q.setCandidates((Extent) null);
                HashSet results = new HashSet((Collection) q.execute());
                Collection resultIds = new HashSet();
                Iterator iter = results.iterator();
                while (iter.hasNext())
                {
                    resultIds.add(JDOHelper.getObjectId(iter.next()));
                }
                assertTrue("Expected result was all candidate elements, but received: " + StringUtils.collectionToString(resultIds), 
                    resultIds.equals(allEmployeeIds));
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            try
            {
                Collection candidates = new HashSet();
                candidates.addAll(pm.getObjectsById(warnerEmployeeIds));

                Query q = pm.newQuery(org.jpox.samples.models.company.Employee.class, candidates, "salary > 10");
                HashSet results = new HashSet((Collection) q.execute());
                Collection resultIds = new HashSet();
                Iterator iter = results.iterator();
                while (iter.hasNext())
                {
                    resultIds.add(JDOHelper.getObjectId(iter.next()));
                }
                assertTrue("Expected result was all expected elements, but received: " + StringUtils.collectionToString(resultIds), 
                    resultIds.equals(expectedEmployeeIds));
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
        }
    }

    public void testQueryUsesInnerJoin()
    {
        try
        {
            Manager woody = new Manager(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Department deptA = new Department("DeptA");
            Department deptB = new Department("DeptB");
            deptB.setManager(bart);
            Department deptC = new Department("DeptC");
            deptC.setManager(boss);

            Person coyote = new Person(5,"Wile","E. Coyote","wile.coyote@acme.com"); //I hate the gravity
            Person duck = new Person(6,"Daffy","Duck","daffy.duck@warnerbros.com"); //paranoid, and neurotic
            Person pepe = new Person(7,"Pepe","le Pew","pepe.lepew@warnerbros.com"); //You are my peanut.
            Person pepe2 = new Person(8,"Pepe","le Dawn","pepe.dawn@warnerbros.com"); //You are my peanut.
            Qualification qA = new Qualification("QA");
            Qualification qB = new Qualification("QB");
            qB.setPerson(duck);
            Qualification qC = new Qualification("QC");
            qC.setPerson(pepe);        

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // test with inheritance
                tx.begin();
                pm.makePersistent(deptC);
                pm.makePersistent(deptA);
                pm.makePersistent(deptB);
                pm.makePersistent(woody);
                pm.makePersistent(boss2);
                tx.commit();
                tx.begin();
                Query q = pm.newQuery(Department.class,"manager.firstName == \"Boss\"");
                Collection c = (Collection) q.execute();
                assertEquals(c.size(),1);
                assertEquals(((Department)c.iterator().next()).getName(),"DeptC");
                tx.commit();

                //test without inheritance
                tx.begin();
                pm.makePersistent(qC);
                pm.makePersistent(qA);
                pm.makePersistent(qB);
                pm.makePersistent(coyote);
                pm.makePersistent(pepe2);           
                tx.commit();            
                tx.begin();
                q = pm.newQuery(Qualification.class,"person.firstName == \"Pepe\"");
                c = (Collection) q.execute();
                assertEquals(c.size(),1);
                assertEquals(((Qualification)c.iterator().next()).getName(),"QC");          
                tx.commit();
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * if the query execute twice, the returned object must be the same (a==b)
     */
    public void testQueryReturnSameObject()
    {
        Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
        Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
        Employee roadrunner = new Employee(4,"Road","Runner","road.runner@warnerbros.com",11,"road"); //Beep! Beep!
        Employee coyote = new Employee(5,"Wile","E. Coyote","wile.coyote@acme.com",9,"serial 5"); //I hate the gravity
        Employee duck = new Employee(6,"Daffy","Duck","daffy.duck@warnerbros.com",7,"daffy"); //paranoid, and neurotic
        Employee pepe = new Employee(7,"Pepe","le Pew","pepe.lepew@warnerbros.com",8,"pepe"); //You are my peanut.

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(woody);
            pm.makePersistent(bart);
            pm.makePersistent(bunny);
            pm.makePersistent(roadrunner);
            pm.makePersistent(coyote);
            pm.makePersistent(duck);
            pm.makePersistent(pepe);
            tx.commit();

            Object resultA;
            Object resultB;
            tx.begin();
            try
            {
                Query q =
                  pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, true), "serialNo.trim() == \"serial 1\"");
                HashSet results = new HashSet((Collection) q.execute());
                resultA = results.iterator().next();
                q.closeAll();
                q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, true), "serialNo.trim() == \"serial 1\"");
                results = new HashSet((Collection) q.execute());
                resultB = results.iterator().next();
                q.closeAll();
                assertTrue(resultA==resultB);
            }
            catch (JDOUserException e)
            {
                LOG.error("Exception during test", e);
                fail(e.getMessage());
            }
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Employee.class);
        }
    }

    /**
     * Test for serialisation of the query results.
     */
    public void testSerialiseQueryResult()
    {
        Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
        Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3");

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(woody);
            pm.makePersistent(bart);
            pm.makePersistent(bunny);
            tx.commit();
    
            tx.begin();
            try
            {
                Query q = pm.newQuery(Employee.class);
                List results = (List)q.execute();
                assertEquals("received: " + results, 3, results.size());

                try
                {
                    // serialise the results
                    FileOutputStream fos = new FileOutputStream("query_results.serial"); 
                    ObjectOutputStream oos = new ObjectOutputStream(fos); 
                    oos.writeObject(results);
                    oos.flush();
                    oos.close();
                }
                catch (Exception e)
                {
                    LOG.error("Exception serialising results", e);
                    fail("Exception serialising results : " + e.getMessage());
                }

                // Deserialise the results
                try
                {
                    FileInputStream fis = new FileInputStream("query_results.serial");
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    Object object2 = ois.readObject();
                    ois.close();
                    assertTrue("Deserialised form is not correct type", object2 instanceof List);
                    List deserialisedResults = (List)object2;
                    assertEquals("Invalid number of deserialised elements", 3, deserialisedResults.size());
                }
                catch (Exception e)
                {
                    LOG.error("Exception deserialising results", e);
                    fail("Exception deserialising results : " + e.getMessage());
                }
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Delete file
            File file = new File("query_results.serial");
            file.delete();

            // Clean out our data
            clean(Employee.class);
        }
    }

    /**
     * Test for the use of the String.indexOf() method.
     */
    public void testStringIndexOf()
    {
        Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
        Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
        Employee roadrunner = new Employee(4,"Road","Runner","road.runner@warnerbros.com",11,"serial 4"); //Beep! Beep!
        Employee coyote = new Employee(5,"Wile","E. Coyote","wile.coyote@acme.com",9,"serial 5"); //I hate the gravity
        Employee duck = new Employee(6,"Daffy","Duck","daffy.duck@warnerbros.com",7,"serial 6"); //paranoid, and neurotic
        Employee pepe = new Employee(7,"Pepe","le Pew","pepe.lepew@warnerbros.com",8,"serial 7"); //You are my peanut.

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(woody);
            pm.makePersistent(bart);
            pm.makePersistent(bunny);
            pm.makePersistent(roadrunner);
            pm.makePersistent(coyote);
            pm.makePersistent(duck);
            pm.makePersistent(pepe);
            tx.commit();
    
            tx.begin();
            try
            {
                Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setFilter("emailAddress.indexOf(\"@\") >= 0");
                HashSet results = new HashSet((Collection) q.execute());
                assertEquals("received: " + results, 7, results.size());
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            try
            {
                Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setFilter("emailAddress.indexOf(\"woodpecker\") == 6");
                HashSet results = new HashSet((Collection)q.execute());
                assertEquals("received: " + results, 1, results.size());
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            try
            {
                Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setFilter("emailAddress.indexOf(\"wood\",7) >= 0");
                HashSet results = new HashSet((Collection) q.execute());
                assertEquals("received: " + results, 0, results.size());
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Employee.class);
        }
    }

    /**
     * Test for the use of the static fields in query method.
     */
    public void testStaticFields()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
            Employee roadrunner = new Employee(4,"Road","Runner","road.runner@warnerbros.com",11,"serial 4"); //Beep! Beep!
            Employee coyote = new Employee(5,"Wile","E. Coyote","wile.coyote@acme.com",9,"serial 5"); //I hate the gravity
            Employee duck = new Employee(6,"Daffy","Duck","daffy.duck@warnerbros.com",7,"serial 6"); //paranoid, and neurotic
            Employee pepe = new Employee(7,"Pepe","le Pew","pepe.lepew@warnerbros.com",8,"serial 7"); //You are my peanut.

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                pm.makePersistent(bunny);
                pm.makePersistent(roadrunner);
                pm.makePersistent(coyote);
                pm.makePersistent(duck);
                pm.makePersistent(pepe);
                tx.commit();

                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("firstName == org.jpox.samples.models.company.Employee.FIRSTNAME");
                    HashSet results = new HashSet((Collection) q.execute());
                    assertEquals("received: " + results, 1, results.size());
                    assertEquals(org.jpox.samples.models.company.Employee.FIRSTNAME,((Person)results.iterator().next()).getFirstName());
                    q.closeAll();
                    q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("firstName == org.jpox.samples.models.company.Person.FIRSTNAME");
                    results = new HashSet((Collection) q.execute());
                    assertEquals("received: " + results, 1, results.size());
                    assertEquals(org.jpox.samples.models.company.Person.FIRSTNAME,((Person)results.iterator().next()).getFirstName());
                    q.closeAll();
                    q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("firstName == Person.FIRSTNAME");
                    results = new HashSet((Collection) q.execute());
                    assertEquals("received: " + results, 1, results.size());
                    assertEquals(org.jpox.samples.models.company.Person.FIRSTNAME,((Person)results.iterator().next()).getFirstName());
                    q.closeAll();
                    q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("firstName == Employee.FIRSTNAME");
                    results = new HashSet((Collection) q.execute());
                    assertEquals("received: " + results, 1, results.size());
                    assertEquals(org.jpox.samples.models.company.Employee.FIRSTNAME,((Person)results.iterator().next()).getFirstName());
                    q.closeAll();
                    q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("firstName == FIRSTNAME");
                    results = new HashSet((Collection) q.execute());
                    assertEquals("received: " + results, 1, results.size());
                    assertEquals(org.jpox.samples.models.company.Employee.FIRSTNAME,((Person)results.iterator().next()).getFirstName());
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }
                tx.commit();
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
            clean(Employee.class);
        }
    }
    
    /**
     * Test the use of the String.startsWith() method.
     */
    public void testStringStartsWith()
    {
        Employee woody = new Employee(1,"Woody","Woodpecker","Woody@woodpecker.com",13,"serial 1");
        Employee bart = new Employee(2,"Bart","Simpson","Bart@simpson.com",2,"serial 2");
        Employee bunny = new Employee(3,"Bugs","Bunny","Bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
        Employee roadrunner = new Employee(4,"Road","Runner","Road.runner@warnerbros.com",11,"serial 4"); //Beep! Beep!
        Employee coyote = new Employee(5,"Wile","E. Coyote","Wile.coyote@acme.com",9,"serial 5"); //I hate the gravity
        Employee duck = new Employee(6,"Daffy","Duck","Daffy.duck@warnerbros.com",7,"serial 6"); //paranoid, and neurotic
        Employee pepe = new Employee(7,"Pepe Changed","le Pew","Pepe.lepew@warnerbros.com",8,"serial 7"); //You are my peanut.

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(woody);
            pm.makePersistent(bart);
            pm.makePersistent(bunny);
            pm.makePersistent(roadrunner);
            pm.makePersistent(coyote);
            pm.makePersistent(duck);
            pm.makePersistent(pepe);
            tx.commit();
    
            tx.begin();
            try
            {
                //test 1
                Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setFilter("emailAddress.startsWith(\"Road\")");
                HashSet results = new HashSet((Collection) q.execute());
                assertTrue("received: "+results,results.size()==1);
                q.closeAll();
                    
                //test 2
                q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setFilter("emailAddress.startsWith(firstName)");
                results = new HashSet((Collection) q.execute());
                assertTrue("received: "+results,results.size()==6);
                q.closeAll();

                //test 3
                q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setFilter("\"Road Runner\".startsWith(firstName)");
                results = new HashSet((Collection) q.execute());
                assertTrue("received: "+results,results.size()==1);
                q.closeAll();               

                //test 4
                q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.declareParameters("java.lang.String var1");
                q.setFilter("var1.startsWith(firstName)");
                results = new HashSet((Collection) q.execute("Road Runner"));
                assertTrue("received: "+results,results.size()==1);
                q.closeAll();               
            
            }
            catch (JDOUserException e)
            {
                LOG.error("Exception during test", e);
                fail(e.getMessage());
            }
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Employee.class);
        }
    }

    /**
     * Test the use of the String.endsWith() method.
     */
    public void testStringEndsWith()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","Woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","Bart@simpson.com",2,"serial 2");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                tx.commit();

                tx.begin();
                try
                {
                    //test 1
                    Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("lastName.endsWith(\"pecker\")");
                    HashSet results = new HashSet((Collection) q.execute());
                    assertEquals("Received incorrect number of results to String.endsWith()", results.size(), 1);
                    Employee emp = (Employee)results.iterator().next();
                    assertEquals("First name of retrieved object is incorrect", emp.getFirstName(), "Woody");
                    assertEquals("Email of retrieved object is incorrect", emp.getEmailAddress(), "Woody@woodpecker.com");
                    q.closeAll();

                    //test 2
                    q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("\"Woody Woodpecker\".endsWith(lastName)");
                    results = new HashSet((Collection) q.execute());
                    assertTrue("received: "+results,results.size()==1);
                    q.closeAll();

                    //test 3
                    q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.declareParameters("java.lang.String var1");
                    q.setFilter("var1.endsWith(lastName)");
                    results = new HashSet((Collection) q.execute("Woody Woodpecker"));
                    assertTrue("received: "+results,results.size()==1);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error("Exception during test", e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            clean(Employee.class);
        }
    }

    /**
     * Test the use of the String.substring() method.
     */
    public void testStringSubstring()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","Woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","Bart@simpson.com",2,"serial 2");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                tx.commit();
                
                tx.begin();
                try
                {
                    // test 1
                    Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("firstName.substring(2) == \"ody\"");
                    HashSet results = new HashSet((Collection) q.execute());
                    assertEquals("Received incorrect number of results to String.substring()", results.size(), 1);
                    Employee emp = (Employee)results.iterator().next();
                    assertEquals("First name of retrieved object is incorrect", emp.getFirstName(), "Woody");
                    assertEquals("Email of retrieved object is incorrect", emp.getEmailAddress(), "Woody@woodpecker.com");
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error("Exception during test", e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            clean(Employee.class);
        }
    }

    /**
     * Test the use of the String.translate() method for DB2/Oracle/PostgreSQL.
     */
    public void testStringTranslate()
    {
        if (vendorID == null || 
            (vendorID != null &&
            !vendorID.equalsIgnoreCase("db2") &&
            !vendorID.equalsIgnoreCase("oracle") &&
            !vendorID.equalsIgnoreCase("postgresql")))
        {
            return;
        }

        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","Woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","Bart@simpson.com",2,"serial 2");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                tx.commit();
                
                tx.begin();
                try
                {
                    // test 1
                    Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("'Bert'.translate('e','a') == firstName");
                    HashSet results = new HashSet((Collection) q.execute());
                    assertEquals("Received incorrect number of results to String.translate()", results.size(), 1);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error("Exception during test", e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            clean(Employee.class);
        }
    }

    /**
     * Test the use of the String.toLowerCase() method.
     */
    public void testStringToLowerCase()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","Woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","Bart@simpson.com",2,"serial 2");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                tx.commit();

                tx.begin();
                try
                {
                    //test 1
                    Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("lastName.toLowerCase() == \"woodpecker\"");
                    HashSet results = new HashSet((Collection) q.execute());
                    assertEquals("Received incorrect number of results to String.toUpperCase()", results.size(), 1);
                    Employee emp = (Employee)results.iterator().next();
                    assertEquals("First name of retrieved object is incorrect", emp.getFirstName(), "Woody");
                    assertEquals("Email of retrieved object is incorrect", emp.getEmailAddress(), "Woody@woodpecker.com");
                    q.closeAll();

                    //test 2
                    q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("\"woody\" == firstName.toLowerCase()");
                    results = new HashSet((Collection) q.execute());
                    assertTrue("received: "+results,results.size()==1);
                    q.closeAll();

                    //test 3
                    q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.declareParameters("java.lang.String var1");
                    q.setFilter("var1.toLowerCase() == lastName.toLowerCase()");
                    results = new HashSet((Collection) q.execute("WoOdPeCkEr"));
                    assertTrue("received: "+results,results.size()==1);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error("Exception during test", e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            clean(Employee.class);
        }
    }

    /**
     * Test the use of the String.toUpperCase() method.
     */
    public void testStringToUpperCase()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","Woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","Bart@simpson.com",2,"serial 2");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                tx.commit();

                tx.begin();
                try
                {
                    //test 1
                    Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("lastName.toUpperCase() == \"WOODPECKER\"");
                    HashSet results = new HashSet((Collection) q.execute());
                    assertEquals("Received incorrect number of results to String.toUpperCase()", results.size(), 1);
                    Employee emp = (Employee)results.iterator().next();
                    assertEquals("First name of retrieved object is incorrect", emp.getFirstName(), "Woody");
                    assertEquals("Email of retrieved object is incorrect", emp.getEmailAddress(), "Woody@woodpecker.com");
                    q.closeAll();

                    //test 2
                    q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.setFilter("\"WOODY\" == firstName.toUpperCase()");
                    results = new HashSet((Collection) q.execute());
                    assertTrue("received: "+results,results.size()==1);
                    q.closeAll();

                    //test 3
                    q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    q.declareParameters("java.lang.String var1");
                    q.setFilter("var1.toUpperCase() == lastName.toUpperCase()");
                    results = new HashSet((Collection) q.execute("WoOdPeCkEr"));
                    assertTrue("received: "+results,results.size()==1);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error("Exception during test", e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            clean(Employee.class);
        }
    }

    /**
     * Tests the String.matches(pattern) expression
     */
    public void testStringMatches()
    {
        try
        {
            Object ids[] = new Object[7];
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Office offices[] = new Office[7];
                offices[0] = new Office(1, "Green", "downtown");
                offices[1] = new Office(1, "Red", "village");
                offices[2] = new Office(1, "Blue", "spring_field");
                offices[3] = new Office(1, "Orange", "percent%city");
                offices[4] = new Office(1, "Yellow", "slash\\city");
                offices[5] = new Office(1, "Grey", "Some name or other (Nr. 1507)");
                offices[6] = new Office(1, "Gold", "Some name or other (Nr# 1507)");
                pm.makePersistentAll(offices);
                tx.commit();
                ids[0] = JDOHelper.getObjectId(offices[0]);
                ids[1] = JDOHelper.getObjectId(offices[1]);
                ids[2] = JDOHelper.getObjectId(offices[2]);
                ids[3] = JDOHelper.getObjectId(offices[3]);
                ids[4] = JDOHelper.getObjectId(offices[4]);
                ids[5] = JDOHelper.getObjectId(offices[5]);
                ids[6] = JDOHelper.getObjectId(offices[6]);

                tx.begin();
                Query q = pm.newQuery(Office.class,"this.description.matches(\"village\")");
                Collection c = (Collection) q.execute();
                Assert.assertEquals(1, c.size());
                Assert.assertEquals(ids[1],JDOHelper.getObjectId(c.iterator().next()));
                
                q = pm.newQuery(Office.class,"this.description.matches(\".illag.\")");
                c = (Collection) q.execute();
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[1],JDOHelper.getObjectId(c.iterator().next()));
                
                q = pm.newQuery(Office.class,"this.description.matches(\".*illag.\")");
                c = (Collection) q.execute();
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[1],JDOHelper.getObjectId(c.iterator().next()));
                
                q = pm.newQuery(Office.class,"this.description.matches(\"(?i).*ILLAGE\")");
                c = (Collection) q.execute();
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[1],JDOHelper.getObjectId(c.iterator().next()));
                
                q = pm.newQuery(Office.class,"this.description.toUpperCase().matches(\".*ILLAGE\")");
                c = (Collection) q.execute();
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[1],JDOHelper.getObjectId(c.iterator().next()));
                
                q = pm.newQuery(Office.class,"this.description.toUpperCase().matches(\".*ILLAGA\")");
                c = (Collection) q.execute();
                Assert.assertEquals(0,c.size());
                
                q = pm.newQuery(Office.class,"this.description.toUpperCase().matches(\".*\")");
                c = (Collection) q.execute();
                Assert.assertEquals(offices.length, c.size());
                
                q = pm.newQuery(Office.class,"this.description.matches(\"spring_field\")");
                c = (Collection) q.execute();
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[2],JDOHelper.getObjectId(c.iterator().next()));
                
                q = pm.newQuery(Office.class,"this.description.matches(\"spring.field\")");
                c = (Collection) q.execute();
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[2],JDOHelper.getObjectId(c.iterator().next()));
                
                q = pm.newQuery(Office.class,"this.description.matches(\"percent%city\")");
                c = (Collection) q.execute();
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[3],JDOHelper.getObjectId(c.iterator().next()));
                
                q = pm.newQuery(Office.class,"this.description.matches(\"percent.city\")");
                c = (Collection) q.execute();
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[3],JDOHelper.getObjectId(c.iterator().next()));
                
                q = pm.newQuery(Office.class,"this.description.matches(\"Some name or other (Nr\\\\. 1507)\")");
                c = (Collection) q.execute();
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[5],JDOHelper.getObjectId(c.iterator().next()));
                
                String filter = "this.description.matches(\"slash\\\\city\")";
                q = pm.newQuery(Office.class,filter);
                c = (Collection) q.execute();
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[4],JDOHelper.getObjectId(c.iterator().next()));

                if (vendorID.equalsIgnoreCase("oracle") || vendorID.equalsIgnoreCase("derby"))
                {
                    q = pm.newQuery(Office.class,"\"spring_field\".matches(this.description)");
                    c = (Collection) q.execute();
                    Assert.assertEquals(1,c.size());
                }
                if (vendorID.equalsIgnoreCase("derby"))
                {
                    //only tested in derby 10.1, which correctly performs the match.
                    //oracle 10 does not work, since it returns 7, ignoring the pattern
                    Assert.assertEquals(ids[2],JDOHelper.getObjectId(c.iterator().next()));
                    q = pm.newQuery(Office.class,"this.description.matches(this.description)");
                    c = (Collection) q.execute();
                    Assert.assertEquals(4,c.size());
                }
                tx.commit();
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
            clean(Office.class);
        }
    }

    /**
     * Tests the Array.contains expression
     */
    public void testArrayContains()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Persist some objects
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField(-10-i);
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for (int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            // Test array.contains
            tx.begin();
            Query q = pm.newQuery(BasicTypeHolder.class,"{1,3,-11}.contains(this.longField)");
            Collection c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            q = pm.newQuery(BasicTypeHolder.class,"{1,3,4}.contains(this.longField)");
            c = (Collection) q.execute();
            Assert.assertEquals(0,c.size());
            q = pm.newQuery(BasicTypeHolder.class,"{1,3,4,this.longField}.contains(this.longField)");
            c = (Collection) q.execute();
            Assert.assertEquals(5,c.size());
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }
    
    /**
     * Tests the Array.length expression
     */
    public void testArrayLength()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Persist some objects
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField(-10-i);
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for (int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            // Query array.length
            tx.begin();
            Query q = pm.newQuery(BasicTypeHolder.class,"{1,3,-11}.length == 3");
            Collection c = (Collection) q.execute();
            Assert.assertEquals(5,c.size());
            q = pm.newQuery(BasicTypeHolder.class,"3 == {1,3,-11}.length");
            c = (Collection) q.execute();
            Assert.assertEquals(5,c.size());
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }

    /**
     * Tests the Array in parameter expression
     */
    public void testArrayParameter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField(-10-i);
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for (int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            tx.begin();
            Query q = pm.newQuery(BasicTypeHolder.class,"p.length() == 3");
            q.declareParameters("Integer[] p");
            Collection c = (Collection) q.execute(new Integer[] {new Integer(1),new Integer(3),new Integer(-11)});
            Assert.assertEquals(5,c.size());
            q = pm.newQuery(BasicTypeHolder.class,"3 == p.length()");
            q.declareParameters("Integer[] p");
            c = (Collection) q.execute(new Integer[] {new Integer(1),new Integer(3),new Integer(-11)});
            Assert.assertEquals(5,c.size());
            q = pm.newQuery(BasicTypeHolder.class,"p.contains(1)");
            q.declareParameters("Integer[] p");
            c = (Collection) q.execute(new Integer[] {new Integer(1),new Integer(3),new Integer(-11)});
            Assert.assertEquals(5,c.size());
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }
    
    /**
     * Tests the Math.abs expression
     */
    public void testMathAbs()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField(-10-i);
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for (int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            tx.begin();
            Query q = pm.newQuery(BasicTypeHolder.class,"Math.abs(this.longField) == 10");
            Collection c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"java.lang.Math.abs(this.longField) == 10");
            c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"Math.abs(this.longField) == Math.abs(-11)");
            c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[1],JDOHelper.getObjectId(c.iterator().next()));
        
            q = pm.newQuery(BasicTypeHolder.class,"Math.abs(this.longField) == Math.abs(-12.0)");
            c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[2],JDOHelper.getObjectId(c.iterator().next()));
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }

    /**
     * Tests the Math.sqrt expression
     */
    public void testMathSqrt()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField((i+2)*(i+2));
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for (int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            tx.begin();
            //use abs to make sure we are not taking square root from negative numbers
            Query q = pm.newQuery(BasicTypeHolder.class,"Math.sqrt(Math.abs(this.longField)) == 2");
            Collection c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"java.lang.Math.sqrt(Math.abs(this.longField)) == 2");
            c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"Math.sqrt(Math.abs(this.longField)) == Math.sqrt(9)");
            c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[1],JDOHelper.getObjectId(c.iterator().next()));
        
            q = pm.newQuery(BasicTypeHolder.class,"Math.sqrt(Math.abs(this.longField)) == Math.sqrt(16)");
            c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[2],JDOHelper.getObjectId(c.iterator().next()));
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }

    /**
     * Tests the avg expression
     */
    public void testAvg()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for( int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField(i+1);
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for( int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            tx.begin();
            Query q = pm.newQuery(BasicTypeHolder.class,"this.longField == avg(p.longField)");
            q.declareVariables("BasicTypeHolder p");
            Collection c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[2],JDOHelper.getObjectId(c.iterator().next()));

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown executing JDOQL query with AVG", e);
            fail("Exception thrown executing JDOQL query with AVG : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }

    public void testAvgWithHaving()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",10,"serial 1");
            Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",5,"serial 3");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",5,"serial 2");
            woody.setAge(30);
            bunny.setAge(30);
            bart.setAge(15);
            pm.makePersistent(woody);
            pm.makePersistent(bart);
            pm.makePersistent(bunny);
            tx.commit();

            tx.begin();
            try
            {
                LOG.info(">> QUERY WITH GROUPBY AVG");
                Query q = pm.newQuery("SELECT age, avg(salary) FROM " + Employee.class.getName() + " GROUP BY age HAVING avg(salary) > 6");
                List results = (List)q.execute();

                // Create a nice representation of the result for error reporting 
                StringBuffer resultString = new StringBuffer();
                for (Object object : results)
                {
                    resultString.append(Arrays.toString((Object[]) object));
                }
                assertEquals("Did not return the correct number of age groups: " + resultString, 1, results.size());

                Object[] ageGroup = (Object[]) results.get(0);
                assertEquals("Did not return the correct age group: " + resultString, 30, ageGroup[0]);
                assertEquals("Did not return the correct avg salary: " + resultString, 7.5, ageGroup[1]);
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Employee.class);
        }
    }

    /**
     * Test the use of the Date.getDay(), Date.getMonth(), Date.getYear(), Time.getHour(), Time.getMinute(), Time.getSecond() methods.
     */
    public void testDateTimeMethods()
    {
        try
        {
            DateHolder prim1 = new DateHolder();
            Calendar cal1 = Calendar.getInstance();
            cal1.set(Calendar.DAY_OF_MONTH, 25);
            cal1.set(Calendar.MONTH, 10);
            cal1.set(Calendar.YEAR, 2000);
            cal1.set(Calendar.HOUR_OF_DAY, 10);
            cal1.set(Calendar.MINUTE, 55);
            cal1.set(Calendar.SECOND, 5);
            prim1.setSQLDateField(new java.sql.Date(cal1.getTimeInMillis()));
            prim1.setSQLTimeField(new java.sql.Time(cal1.getTimeInMillis()));

            DateHolder prim2 = new DateHolder();
            Calendar cal2 = Calendar.getInstance();
            cal2.set(Calendar.DAY_OF_MONTH, 5);
            cal2.set(Calendar.MONTH, 3);
            cal2.set(Calendar.YEAR, 1982);
            cal2.set(Calendar.HOUR_OF_DAY, 4);
            cal2.set(Calendar.MINUTE, 13);
            cal2.set(Calendar.SECOND, 45);
            prim2.setSQLDateField(new java.sql.Date(cal2.getTimeInMillis()));
            prim2.setSQLTimeField(new java.sql.Time(cal2.getTimeInMillis()));

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id1 = null;
            Object id2 = null;
            try
            {
                tx.begin();
                pm.makePersistent(prim1);
                pm.makePersistent(prim2);
                tx.commit();
                id1 = JDOHelper.getObjectId(prim1);
                id2 = JDOHelper.getObjectId(prim2);

                tx.begin();
                try
                {
                    // test for date methods
                    Query q = pm.newQuery(pm.getExtent(DateHolder.class, false));
                    q.setFilter("sqlDateField.getDay() == 25");
                    List results = (List)q.execute();
                    assertEquals("Received incorrect number of results to Date.getDay() == 25", 1, results.size());
                    DateHolder prim = (DateHolder)results.iterator().next();
                    assertEquals("Retrieved object for Date.getDay() is incorrect", id1, JDOHelper.getObjectId(prim));
                    q.closeAll();

                    q = pm.newQuery(pm.getExtent(DateHolder.class, false));
                    q.setFilter("sqlDateField.getMonth() == 3");
                    results = (List)q.execute();
                    assertEquals("Received incorrect number of results to Date.getMonth() == 3", 1, results.size());
                    prim = (DateHolder)results.iterator().next();
                    assertEquals("Retrieved object for Date.month() is incorrect", id2, JDOHelper.getObjectId(prim));
                    q.closeAll();

                    q = pm.newQuery(pm.getExtent(DateHolder.class, false));
                    q.setFilter("sqlDateField.getYear() == 2000");
                    results = (List)q.execute();
                    assertEquals("Received incorrect number of results to Date.getYear() == 2000", 1, results.size());
                    prim = (DateHolder)results.iterator().next();
                    assertEquals("Retrieved object for Date.year() is incorrect", id1, JDOHelper.getObjectId(prim));
                    q.closeAll();

                    // test for time methods
                    q = pm.newQuery(pm.getExtent(DateHolder.class, false));
                    q.setFilter("sqlTimeField.getHour() == 10");
                    results = (List)q.execute();
                    assertEquals("Received incorrect number of results to Time.getHour() == 10", 1, results.size());
                    prim = (DateHolder)results.iterator().next();
                    assertEquals("Retrieved object for Time.hour() is incorrect", id1, JDOHelper.getObjectId(prim));
                    q.closeAll();

                    q = pm.newQuery(pm.getExtent(DateHolder.class, false));
                    q.setFilter("sqlTimeField.getMinute() == 13");
                    results = (List)q.execute();
                    assertEquals("Received incorrect number of results to Time.getMinute() == 13", 1, results.size());
                    prim = (DateHolder)results.iterator().next();
                    assertEquals("Retrieved object for Time.minute() is incorrect", id2, JDOHelper.getObjectId(prim));
                    q.closeAll();

                    q = pm.newQuery(pm.getExtent(DateHolder.class, false));
                    q.setFilter("sqlTimeField.getSecond() == 5");
                    results = (List)q.execute();
                    assertEquals("Received incorrect number of results to Time.getSecond() == 5", 1, results.size());
                    prim = (DateHolder)results.iterator().next();
                    assertEquals("Retrieved object for Time.second() is incorrect", id1, JDOHelper.getObjectId(prim));
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error("Exception during test", e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            clean(DateHolder.class);
        }
    }

    /**
     * Test the use of the Date in aggregates.
     */
    public void testAggregateOfDate()
    {
        try
        {
            Date maxDate = null;
            DateHolder prim1 = new DateHolder();
            Calendar cal1 = Calendar.getInstance();
            cal1.set(Calendar.DAY_OF_MONTH, 25);
            cal1.set(Calendar.MONTH, 10);
            cal1.set(Calendar.YEAR, 2000);
            cal1.set(Calendar.HOUR_OF_DAY, 10);
            cal1.set(Calendar.MINUTE, 55);
            cal1.set(Calendar.SECOND, 5);
            prim1.setDateField(cal1.getTime());
            maxDate = cal1.getTime();

            DateHolder prim2 = new DateHolder();
            Calendar cal2 = Calendar.getInstance();
            cal2.set(Calendar.DAY_OF_MONTH, 5);
            cal2.set(Calendar.MONTH, 3);
            cal2.set(Calendar.YEAR, 1982);
            cal2.set(Calendar.HOUR_OF_DAY, 4);
            cal2.set(Calendar.MINUTE, 13);
            cal2.set(Calendar.SECOND, 45);
            prim2.setDateField(cal2.getTime());

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id2 = null;
            try
            {
                tx.begin();
                pm.makePersistent(prim1);
                pm.makePersistent(prim2);
                tx.commit();
                id2 = JDOHelper.getObjectId(prim2);

                tx.begin();
                try
                {
                    Query q = pm.newQuery("SELECT MAX(dateField) FROM " + DateHolder.class.getName());
                    Date maxDateAgg = (Date)q.execute();
                    assertEquals("Max date is incorrect", maxDate, maxDateAgg);
                    q.closeAll();

                    q = pm.newQuery("SELECT FROM "+DateHolder.class.getName()+
                        " WHERE dateField < MAX(dateField)");
                    List<DateHolder> results = (List)q.execute();
                    assertEquals("Received incorrect number of results", 1, results.size());
                    DateHolder prim = (DateHolder)results.iterator().next();
                    assertEquals("Retrieved object is incorrect", id2, JDOHelper.getObjectId(prim));
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error("Exception during test", e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            clean(DateHolder.class);
        }
    }

    public void testPCLiteralOnQueryCompile()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Persist some simple objects
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField((i+2)*(i+2));
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for (int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            tx.begin();
            for (int i=0; i<basics.length; i++)
            {
                Query q = pm.newQuery(BasicTypeHolder.class,"this == obj");
                q.declareParameters ("BasicTypeHolder obj");
                q.compile(); //test that query can be compiled
                Collection c = (Collection) q.execute(basics[i]);
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[i],JDOHelper.getObjectId(c.iterator().next()));
            }
            tx.commit();

        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }

    /**
     * Tests the JDOHelper.getObjectId() expression
     */
    public void testJDOHelperGetObjectID1()
    {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Persist some other objects
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField((i+2)*(i+2));
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for (int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            tx.begin();
            Query q = pm.newQuery(BasicTypeHolder.class,"JDOHelper.getObjectId(this) == oid");
            q.declareParameters ("Object oid");
            Collection c = null;
            try
            {
                c = (Collection) q.execute("nononononnon"); // Invalid object identity
                Assert.assertEquals(0, c.size()); // In case the query compilation just resolves it to false
            }
            catch (JDOException jdoe)
            {
                // Arguable that we should get this with invalid input
            }

            for (int i=0; i<basics.length; i++)
            {
                q = pm.newQuery(BasicTypeHolder.class,"JDOHelper.getObjectId(this) == oid");
                q.declareParameters ("Object oid");
                c = (Collection) q.execute(ids[i]);
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[i],JDOHelper.getObjectId(c.iterator().next()));

                q = pm.newQuery(BasicTypeHolder.class,"oid == JDOHelper.getObjectId(this)");
                q.declareParameters ("Object oid");
                c = (Collection) q.execute(ids[i]);
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[i],JDOHelper.getObjectId(c.iterator().next()));

                q = pm.newQuery(BasicTypeHolder.class,"JDOHelper.getObjectId(this) == JDOHelper.getObjectId(obj)");
                q.declareParameters ("Object obj");
                q.compile(); //test that query can be compiled
                c = (Collection) q.execute(basics[i]);
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[i],JDOHelper.getObjectId(c.iterator().next()));

                q = pm.newQuery(BasicTypeHolder.class,"JDOHelper.getObjectId(this) == JDOHelper.getObjectId(obj)");
                q.declareParameters ("BasicTypeHolder obj");
                q.compile(); //test that query can be compiled
                c = (Collection) q.execute(basics[i]);
                Assert.assertEquals(1,c.size());
                Assert.assertEquals(ids[i],JDOHelper.getObjectId(c.iterator().next()));
            }
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error performing test", e);
            fail("Exception performing test : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }

    /**
     * Tests the JDOHelper.getObjectId() expression
     */
    public void testJDOHelperGetObjectID2()
    {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Persist some simple objects (uses datastore id, or composite application id depending on suite)
            tx.begin();
            Office o1 = new Office(1, "Green", "Big spacious office");
            Office o2 = new Office(2, "Blue", "Pokey office at the back of the building");
            Office o3 = new Office(1, "Yellow", "Massive open plan office");
            pm.newQuery(Office.class).deletePersistentAll();
            pm.makePersistent(o1);
            pm.makePersistent(o2);
            pm.makePersistent(o3);
            tx.commit();
            Object[] officeIds = new Object[3];
            officeIds[0] = pm.getObjectId(o1);
            officeIds[1] = pm.getObjectId(o2);
            officeIds[2] = pm.getObjectId(o3);

            tx.begin();
            Query q = pm.newQuery(Office.class, "JDOHelper.getObjectId(this) == oid");
            q.declareParameters("Object oid");
            Collection c = null;
            for (int i=0;i<officeIds.length;i++)
            {
                c = (Collection)q.execute(officeIds[i]);
                assertEquals(1, c.size());
                assertEquals(officeIds[i], JDOHelper.getObjectId(c.iterator().next()));
            }
            tx.commit();

            tx.begin();
            q = pm.newQuery(Office.class, "javax.jdo.JDOHelper.getObjectId(this) == oid");
            q.declareParameters("Object oid");
            c = null;
            for (int i=0;i<officeIds.length;i++)
            {
                c = (Collection)q.execute(officeIds[i]);
                assertEquals(1, c.size());
                assertEquals(officeIds[i], JDOHelper.getObjectId(c.iterator().next()));
            }
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Office.class);
        }
    }

    /**
     * Test case to use JDOQL single-string
     */
    public void testSingleString()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
            Employee roadrunner = new Employee(4,"Road","Runner","road.runner@warnerbros.com",11,"serial 4"); //Meep! Meep!

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                pm.makePersistent(bunny);
                pm.makePersistent(roadrunner);
                tx.commit();

                tx.begin();
                try
                {
                    Query q = pm.newQuery("SELECT UNIQUE FROM " + Employee.class.getName() + " EXCLUDE SUBCLASSES  WHERE firstName == \"Bugs\"");
                    q.setClass(Employee.class);
                    Object results=q.execute();
                    assertTrue("UNIQUE query returned an object of an incorrect type : should have been Employee but was " + results,
                        results instanceof Employee);
                    q.closeAll();
                    q = pm.newQuery("SELECT UNIQUE FROM " + Employee.class.getName() + " EXCLUDE SUBCLASSES  WHERE firstName == \"Bugs\" import "+Employee.class.getName());
                    q.setClass(Employee.class);
                    results=q.execute();
                    assertTrue("UNIQUE query returned an object of an incorrect type : should have been Employee but was " + results,
                        results instanceof Employee);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }

                tx.commit();
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
            clean(Employee.class);
        }
    }

    /**
     * Test case to use JDOQL single-string
     */
    public void testSingleStringKeywordAsFieldName()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            try
            {
                Query q = pm.newQuery("SELECT select FROM " + Employee.class.getName() + " EXCLUDE SUBCLASSES  WHERE firstName == \"Bugs\"");
                q.setClass(Employee.class);
                q.execute();
                q.closeAll();
                fail("Expected JDOUserException");

            }
            catch (JDOUserException e)
            {
                //expected
            }

            tx.commit();
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
     * Test case to use JDOQL single-string parameters
     */
    public void testSingleStringParameters()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
            Employee roadrunner = new Employee(4,"Road","Runner","road.runner@warnerbros.com",11,"serial 4"); //Meep! Meep!
            Manager bart2 = new Manager(5,"Bart","Smith","bart@smith.com",4,"serial 5");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                pm.makePersistent(bunny);
                pm.makePersistent(roadrunner);
                pm.makePersistent(bart2);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery("SELECT FROM " + Employee.class.getName() + " EXCLUDE SUBCLASSES WHERE firstName == christianname PARAMETERS java.lang.String christianname");
                List results = (List)q.execute("Bart");
                assertTrue("Query returned incorrect number of objects from single-string parameter query : was " + results.size() + " but should have been 1",
                    results.size() == 1);
                q.closeAll();
                tx.commit();
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test case to use the JDO 2.0 named queries defined in MetaData.
     */
    public void testNamedQueries()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
            Employee roadrunner = new Employee(4,"Road","Runner","road.runner@warnerbros.com",11,"serial 4"); //Meep! Meep!

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                pm.makePersistent(bunny);
                pm.makePersistent(roadrunner);
                tx.commit();

                tx.begin();

                // Simple named query with all in the query tag
                try
                {
                    Query q = pm.newNamedQuery(org.jpox.samples.models.company.Employee.class,"SalaryBelow12");
                    Collection results=(Collection)q.execute();
                    assertTrue("Named Query \"SalaryBelow12\" returned an incorrect number of Employees : expected 2 but returned " + results.size(),results.size() == 2);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }

                // Named query with filter and declare tags
                try
                {
                    Query q = pm.newNamedQuery(org.jpox.samples.models.company.Employee.class,"SerialNoStartsWith");
                    Collection results=(Collection)q.execute("serial");
                    assertTrue("Named Query \"SerialNoStartsWith\" returned an incorrect number of Employees : expected 4 but returned " + results.size(),results.size() == 4);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }

                tx.commit();
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test the use of non-standard characters in parameter names etc.
     */
    public void testNonstandardCharacters()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist some data to query
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                tx.commit();

                // Query using $ in parameter names
                tx.begin();
                try
                {
                    Query q = pm.newQuery(org.jpox.samples.models.company.Employee.class);
                    q.declareImports("import java.lang.String");
                    q.declareParameters("java.lang.String $theFirstName, java.lang.String $theLastName");
                    q.setFilter("firstName == $theFirstName && lastName == $theLastName");
                    Collection coll = (Collection) q.execute("Woody", "Woodpecker");
                    assertTrue("received: "+ coll, coll.size() == 1);
                    q.closeAll();
                }
                catch (Exception e)
                {
                    LOG.error("Exception during test", e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test case to use JDOQL "instanceof"
     */
    public void testInstanceof()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // 1. instanceof where the candidate class uses "new-table" (union)
                tx.begin();
                Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
                Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
                Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
                Manager roadrunner = new Manager(4,"Road","Runner","road.runner@warnerbros.com",11,"serial 4"); //Meep! Meep!

                pm.makePersistent(woody);
                pm.makePersistent(bart);
                pm.makePersistent(bunny);
                pm.makePersistent(roadrunner);
                tx.commit();

                tx.begin();
                try
                {
                    Query q = pm.newQuery(Employee.class, "this instanceof " + Manager.class.getName());
                    List results = (List)q.execute();
                    assertEquals("Number of objects returned from instanceof was incorrect", results.size(), 1);
                    Object obj = results.get(0);
                    assertTrue("Type of object returned should have been Manager but wasnt", obj instanceof Manager);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }

                tx.commit();

                // 2. instanceof where the candidate class using "superclass-table" (discriminator)
                tx.begin();
                ElectronicJournal elecJournal = new ElectronicJournal(1, "Electronics Weekly");
                PrintJournal printJournal = new PrintJournal(2, "Glossy magazine");
                pm.makePersistent(elecJournal);
                pm.makePersistent(printJournal);
                tx.commit();

                tx.begin();
                try
                {
                    Query q = pm.newQuery(AbstractJournal.class, "this instanceof " + PrintJournal.class.getName());
                    List results = (List)q.execute();
                    assertEquals("Number of objects returned from instanceof was incorrect", results.size(), 1);
                    Object obj = results.get(0);
                    assertTrue("Type of object returned should have been PrintJournal but wasnt", obj instanceof PrintJournal);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }
                tx.commit();
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
            clean(PrintJournal.class);
            clean(ElectronicJournal.class);
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test case to use single quoted strings.
     */
    public void testSingleQuotes()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
            Employee roadrunner = new Employee(4,"Road","Runner","road.runner@warnerbros.com",11,"serial 4"); //Meep! Meep!

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist some data to play with
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                pm.makePersistent(bunny);
                pm.makePersistent(roadrunner);
                tx.commit();

                // Query using a single-quoted StringLiteral
                tx.begin();
                try
                {
                    Query q=pm.newQuery(pm.getExtent(Employee.class, false));
                    q.setFilter("firstName == 'Bugs'");
                    List results = (List)q.execute();
                    assertTrue("Test for a singly-quoted string returned no objects!",results != null && results.size() > 0);
                    assertTrue("Test for a singly-quoted string returned incorrect number of objects - returned " + results.size() + " but should have been 1",
                        results.size() == 1);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error(e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test case to see what happens when we compare a CharacterLiteral with an invalid value.
     * It is arguable that this test is pointless because the user has fscked up the query
     * but it currently gives a NullPointerException which should be avoided.
     */
    public void testCharLiteralInvalidComparison()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
            Employee roadrunner = new Employee(4,"Road","Runner","road.runner@warnerbros.com",11,"serial 4"); //Meep! Meep!

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist some data to play with
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                pm.makePersistent(bunny);
                pm.makePersistent(roadrunner);
                tx.commit();

                // Query using a CharacterLiteral == int
                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Employee.class, false));
                    q.setFilter("'A' == 65");
                    List results = (List)q.execute();
                    assertTrue("Test for a CharLiteral with invalid clause returned objects!",results != null && results.size() == 4);
                    q.closeAll();

                    q=pm.newQuery(pm.getExtent(Employee.class, false));
                    q.setFilter("'A' != 65");
                    results = (List)q.execute();
                    assertTrue("Test for a CharLiteral with invalid clause returned objects!",results != null && results.size() == 0);
                    q.closeAll();
                }
                catch (Exception e)
                {
                    LOG.error("Exception during test", e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test case for autoamtic escaping of single quote within a parameter String.
     */
    public void testEscapeSingleQuoteInString()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"abc'def");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist some data to play with
                tx.begin();
                pm.makePersistent(woody);
                tx.commit();

                // Query for a parameter that contains a single-quote
                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Employee.class, false));
                    q.setFilter("serialNo == theSerial");
                    q.declareParameters("java.lang.String theSerial");
                    String serial = "abc'def";
                    List results = (List)q.execute(serial);
                    assertTrue("Test for a string containing a single-quote returned no results!",results != null && results.size() > 0);
                    assertTrue("Test for a string containing a single-quote returned incorrect number of objects - returned " + results.size() + " but should have been 1",
                        results.size() == 1);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error(e);
                    fail(e.getMessage());
                }

                // Query for something that starts with something containing a single-quote
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Employee.class, false));
                    q.setFilter("serialNo.startsWith(theSerial)");
                    q.declareParameters("java.lang.String theSerial");
                    String serial = "abc'";
                    List results = (List)q.execute(serial);
                    assertTrue("Test for a string.startsWith() containing a single-quote returned no results!",results != null && results.size() > 0);
                    assertTrue("Test for a string.startsWith() containing a single-quote returned incorrect number of objects - returned " + results.size() + " but should have been 1",
                        results.size() == 1);
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error(e);
                    fail(e.getMessage());
                }
                tx.commit();
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    public void testEq_Neq_UnboundVariables()
    {
        try
        {
            Person p1 = new Person(1, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");
            p1.setAge(27);
            Person p2 = new Person(2, "Road", "Runner", "road.runner@warnerbros.com");
            p2.setAge(28);
            Person p3 = new Person(3, "Bart", "Simpson", "bart.simpson@simpsons.com");
            p3.setAge(28);

            ReachableItem item1 = new ReachableItem("Bugs");
            ReachableItem item2 = new ReachableItem("Road");
            ReachableItem item3 = new ReachableItem("Bart");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.makePersistent(p3);
                pm.makePersistent(item1);
                pm.makePersistent(item2);
                pm.makePersistent(item3);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(ReachableItem.class,
                        "this.name == pA.firstName && pA.firstName == theName && pA.age == 28");
                q.declareParameters("String theName");
                q.declareVariables(Person.class.getName() + " pA");
                Collection c = (Collection) q.execute("Road");
                assertEquals(1,c.size());
                assertEquals(((ReachableItem)c.iterator().next()).getName(), "Road");
                tx.commit();

                tx.begin();
                q = pm.newQuery(ReachableItem.class);
                q.setFilter("this == item && (item.name.equals(\"Bugs\") || item.name.equals(\"Road\"))");
                q.declareVariables(ReachableItem.class.getName() + " item");
                q.setOrdering("name ascending");
                List l = new ArrayList((Collection) q.execute());
                assertEquals(2,l.size());
                assertEquals(((ReachableItem)l.get(0)).getName(), "Bugs");
                assertEquals(((ReachableItem)l.get(1)).getName(), "Road");
                tx.commit();

                tx.begin();
                q = pm.newQuery(ReachableItem.class);
                q.setFilter("this != item && item.name.equals(\"Bugs\")");
                q.declareVariables(ReachableItem.class.getName() + " item");
                q.setOrdering("name ascending");
                l = new ArrayList((Collection) q.execute());
                assertEquals(2, l.size());
                assertEquals(((ReachableItem)l.get(0)).getName(), "Bart");
                assertEquals(((ReachableItem)l.get(1)).getName(), "Road");
                tx.commit();
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
            clean(Person.class);
            clean(ReachableItem.class);
        }
    }

    public void testStringEquals()
    {
        try
        {
            Person p1 = new Person(1, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");
            p1.setAge(27);
            Person p2 = new Person(2, "Road", "Runner", "road.runner@warnerbros.com");
            p2.setAge(28);
            Person p3 = new Person(3, "Bart", "Simpson", "bart.simpson@simpsons.com");
            p3.setAge(28);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.makePersistent(p3);
                tx.commit();

                tx.begin();

                // field.equals(param)
                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() + " WHERE firstName.equals(:name)");
                Collection c1 = (Collection) q1.execute("Road");
                assertEquals(1, c1.size());
                assertEquals("Road", ((Person)c1.iterator().next()).getFirstName());

                c1 = (Collection) q1.execute("Bugs");
                assertEquals(1, c1.size());
                assertEquals("Bugs", ((Person)c1.iterator().next()).getFirstName());

                // param.equals(field)
                Query q2 = pm.newQuery("SELECT FROM " + Person.class.getName() + " WHERE :name.equals(firstName)");
                Collection c2 = (Collection) q2.execute("Road");
                assertEquals(1, c2.size());
                assertEquals("Road", ((Person)c2.iterator().next()).getFirstName());

                c2 = (Collection) q2.execute("Bugs");
                assertEquals(1, c2.size());
                assertEquals("Bugs", ((Person)c2.iterator().next()).getFirstName());

                tx.commit();
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
            clean(Person.class);
        }
    }

    public void testStringEqualsIgnoreCase()
    {
        try
        {
            Person p1 = new Person(1, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");
            p1.setAge(27);
            Person p2 = new Person(2, "Road", "Runner", "road.runner@warnerbros.com");
            p2.setAge(28);
            Person p3 = new Person(3, "Bart", "Simpson", "bart.simpson@simpsons.com");
            p3.setAge(28);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.makePersistent(p3);
                tx.commit();

                tx.begin();

                // field.equalsIgnoreCase(param)
                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() + " WHERE firstName.equalsIgnoreCase(:name)");
                Collection c1 = (Collection) q1.execute("Road");
                assertEquals(1, c1.size());
                assertEquals("Road", ((Person)c1.iterator().next()).getFirstName());

                c1 = (Collection) q1.execute("Bugs");
                assertEquals(1, c1.size());
                assertEquals("Bugs", ((Person)c1.iterator().next()).getFirstName());

                // param.equalsIgnoreCase(field)
                Query q2 = pm.newQuery("SELECT FROM " + Person.class.getName() + " WHERE :name.equalsIgnoreCase(firstName)");
                Collection c2 = (Collection) q2.execute("Road");
                assertEquals(1, c2.size());
                assertEquals("Road", ((Person)c2.iterator().next()).getFirstName());

                c2 = (Collection) q2.execute("Bugs");
                assertEquals(1, c2.size());
                assertEquals("Bugs", ((Person)c2.iterator().next()).getFirstName());

                tx.commit();
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
            clean(Person.class);
        }
    }

    public void testQueryUnboundVariables()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Office o1 = new Office(1, "Green", "Big spacious office");
                Office o2 = new Office(2, "Blue", "Pokey office at the back of the building");
                Office o3 = new Office(1, "Yellow", "Massive open plan office");
                Department d1 = new Department("Finance");
                Department d2 = new Department("Customer Support");
                Department d3 = new Department("Sales");
                Department d4 = new Department("IT");
                o1.addDepartment(d1);
                o1.addDepartment(d3);
                o2.addDepartment(d2);
                o3.addDepartment(d4);
                o3.addDepartment(d3);
                pm.makePersistent(o1);
                pm.makePersistent(o2);
                pm.makePersistent(o3);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
                fail("Error persisting sample data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery(Office.class);
                q.setFilter("dept.name.equals(\"Finance\") && departments.contains(dept)");
                q.declareVariables("Department dept");
                q.declareImports("import org.jpox.samples.models.company.Department");
                List l = new ArrayList((Collection) q.execute());
                assertEquals(1,l.size());

                q = pm.newQuery(Office.class);
                q.setFilter("dept.name.equals(\"Sales\") && departments.contains(dept)");
                q.declareVariables("Department dept");
                q.declareImports("import org.jpox.samples.models.company.Department");
                q.setOrdering("roomName ascending");
                l = new ArrayList((Collection) q.execute());
                assertEquals(2,l.size());
                assertEquals(((Office)l.get(0)).getRoomName(),"Green");
                assertEquals(((Office)l.get(1)).getRoomName(),"Yellow");

                tx.commit();            
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    public void testQueryUnboundVariablesInheritance1()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            bart.addSubordinate(boss);
            bart.addSubordinate(boss2);
            homer.addSubordinate(boss4);
            Department deptA = new Department("DeptA");
            Department deptB = new Department("DeptB");
            bart.addDepartment(deptB);     

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(deptA);
                pm.makePersistent(deptB);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter("emp.lastName == \"WakesUp2\" && this.subordinates.contains(emp)");
                q.declareVariables("Employee emp");
                q.declareImports("import org.jpox.samples.models.company.Employee");
                Collection c = (Collection) q.execute();
                assertEquals(1,c.size());
                assertEquals(((Manager)c.iterator().next()).getFirstName(),"Bart");
                tx.commit();
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    public void testQueryUnboundVariablesInheritance2()
    {
        try
        {
            ModeratedUserGroup grp1 = new ModeratedUserGroup(1, "JDO Expert Group");
            ModeratedUserGroup grp2 = new ModeratedUserGroup(1, "JPA Expert Group");
            ModeratedUserGroup grp3 = new ModeratedUserGroup(1, "J2EE Expert Group");
            ExpertGroupMember mem1 = new ExpertGroupMember(1, "Craig Russell");
            ExpertGroupMember mem2 = new ExpertGroupMember(1, "Linda De Michiel");
            ExpertGroupMember mem3 = new ExpertGroupMember(1, "Bill Shannon");

            grp1.getMembers().add(mem1);
            grp2.getMembers().add(mem2);
            grp3.getMembers().add(mem2);
            grp3.getMembers().add(mem3);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(grp1);
                pm.makePersistent(grp2);
                pm.makePersistent(grp3);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(pm.getExtent(UserGroup.class, true));
                q.setFilter("this.members.contains(mem) && mem.name == \"Craig Russell\"");
                q.declareVariables("ExpertGroupMember mem");
                q.declareImports("import org.jpox.samples.one_many.unidir_2.ExpertGroupMember");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals(((UserGroup)c.iterator().next()).getName(), "JDO Expert Group");
                tx.commit();
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
            clean(ModeratedUserGroup.class);
            clean(ExpertGroupMember.class);
        }
    }

    public void testQueryUnboundVariablesInheritanceRightHandDeclared1()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            bart.addSubordinate(boss);
            bart.addSubordinate(boss2);
            homer.addSubordinate(boss4);
            Department deptA = new Department("DeptA");
            Department deptB = new Department("DeptB");
            bart.addDepartment(deptB);     

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(deptA);
                pm.makePersistent(deptB);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter("this.subordinates.contains(emp) && emp.lastName == \"WakesUp2\" ");
                q.declareVariables("Employee emp");
                q.declareImports("import org.jpox.samples.models.company.Employee");
                Collection c = (Collection) q.execute();
                assertEquals(1,c.size());
                assertEquals(((Manager)c.iterator().next()).getFirstName(),"Bart");
                tx.commit();
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    public void testQueryUnboundVariablesInheritanceRightHandDeclared2()
    {
        try
        {
            ModeratedUserGroup grp1 = new ModeratedUserGroup(1, "JDO Expert Group");
            ModeratedUserGroup grp2 = new ModeratedUserGroup(1, "JPA Expert Group");
            ModeratedUserGroup grp3 = new ModeratedUserGroup(1, "J2EE Expert Group");
            ExpertGroupMember mem1 = new ExpertGroupMember(1, "Craig Russell");
            ExpertGroupMember mem2 = new ExpertGroupMember(1, "Linda De Michiel");
            ExpertGroupMember mem3 = new ExpertGroupMember(1, "Bill Shannon");

            grp1.getMembers().add(mem1);
            grp2.getMembers().add(mem2);
            grp3.getMembers().add(mem2);
            grp3.getMembers().add(mem3);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(grp1);
                pm.makePersistent(grp2);
                pm.makePersistent(grp3);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(pm.getExtent(UserGroup.class,true));
                q.setFilter("(mem.name == \"Craig Russell\") && this.members.contains(mem)");
                q.declareVariables("ExpertGroupMember mem");
                q.declareImports("import org.jpox.samples.one_many.unidir_2.ExpertGroupMember");
                Collection c = (Collection) q.execute();
                assertEquals(1,c.size());
                assertEquals(((UserGroup)c.iterator().next()).getName(), "JDO Expert Group");
                tx.commit();
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
            clean(ModeratedUserGroup.class);
            clean(ExpertGroupMember.class);
        }
    }

    /**
     * Tests the concatenation of strings and numbers
     */
    public void testConcatStringAndNumbers()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField(i+1);
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for (int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            tx.begin();
            Query q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == \"1-1\" ");
            Collection c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == \"\" + 1 + \"-\" + 1");
            c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == \"\" + 1 + \"-\" + 1 + \"-\"");
            c = (Collection) q.execute();
            Assert.assertEquals(0,c.size());

            q = pm.newQuery(BasicTypeHolder.class,"this.longField + \"-\" + this.longField == 1 + \"-\" + 1");
            c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"this.longField + \"-\" + this.longField == 1 + \"-\" + 1 + \"-\"");
            c = (Collection) q.execute();
            Assert.assertEquals(0,c.size());

            //test using parameters
            q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == str ");
            q.declareParameters("java.lang.String str");
            c = (Collection) q.execute("1-1");
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == str + \"-\" + str");
            q.declareParameters("java.lang.String str");
            c = (Collection) q.execute("1");
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == str + \"-\" + str");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(new Integer(1));
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));
            
            q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == \"\" + str + \"-\" + str + \"-\"");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(new Integer(1));
            Assert.assertEquals(0,c.size());

            q = pm.newQuery(BasicTypeHolder.class,"this.longField + \"-\" + this.longField == str + \"-\" + str");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(new Integer(1));
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"this.longField + \"-\" + this.longField == str + \"-\" + str + \"-\"");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(new Integer(1));
            Assert.assertEquals(0,c.size());

            //todo null
            q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == str ");
            q.declareParameters("java.lang.String str");
            c = (Collection) q.execute(null);
            Assert.assertEquals(0,c.size());

            q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == str + \"-\" + str");
            q.declareParameters("java.lang.String str");
            c = (Collection) q.execute(null);
            Assert.assertEquals(0,c.size());

            q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == str + \"-\" + str");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(null);
            Assert.assertEquals(0,c.size());
            
            q = pm.newQuery(BasicTypeHolder.class,"\"\" + this.longField + \"-\" + this.longField == \"\" + str + \"-\" + str + \"-\"");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(null);
            Assert.assertEquals(0,c.size());

            q = pm.newQuery(BasicTypeHolder.class,"this.longField + \"-\" + this.longField == str + \"-\" + str");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(new Integer(1));
            c = (Collection) q.execute(null);
            Assert.assertEquals(0,c.size());

            q = pm.newQuery(BasicTypeHolder.class,"this.longField + \"-\" + this.longField == str + \"-\" + str + \"-\"");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(null);
            Assert.assertEquals(0,c.size());
            
            q = pm.newQuery(BasicTypeHolder.class,"null == str + \"-\" + str + \"-\" && this.longField == 1");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(null);
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[0],JDOHelper.getObjectId(c.iterator().next()));
            
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }

    public void testNullEqualsNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField(i+1);
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for (int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            tx.begin();            
            Query q = pm.newQuery(BasicTypeHolder.class,"null == null");
            q.declareParameters("java.lang.Integer str");
            Collection c = (Collection) q.execute(null);
            Assert.assertEquals(5,c.size());
            
            q = pm.newQuery(BasicTypeHolder.class,"null != null");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(null);
            Assert.assertEquals(0,c.size());

            q = pm.newQuery(BasicTypeHolder.class,"null == null || null != null");
            q.declareParameters("java.lang.Integer str");
            c = (Collection) q.execute(null);
            Assert.assertEquals(5,c.size());

            tx.commit();

        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }
    
    /**
     * Tests use of parentheses inside a JDOQL
     */
    public void testUseOfParentheses()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField(i+1);
                basics[i].setShortField((short) (i+11));
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();
            Object ids[] = new Object[5];
            for (int i=0; i<basics.length; i++)
            {
                ids[i] = pm.getObjectId(basics[i]);
            }

            tx.begin();
            Query q = pm.newQuery(BasicTypeHolder.class,"(this.longField == 1 && this.shortField == 5) || (this.longField == 3 && this.shortField == 5)");
            Collection c = (Collection) q.execute();
            Assert.assertEquals(0,c.size());

            q = pm.newQuery(BasicTypeHolder.class,"(this.longField == 1 && this.shortField == 5) || (this.longField == 3 && this.shortField == 13)");
            c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[2],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"(this.longField == 4 && this.shortField == 14) || (this.longField == 3 && this.shortField == 5)");
            c = (Collection) q.execute();
            Assert.assertEquals(1,c.size());
            Assert.assertEquals(ids[3],JDOHelper.getObjectId(c.iterator().next()));

            q = pm.newQuery(BasicTypeHolder.class,"(this.longField == 4 && this.shortField == 14) || (this.longField == 3 && this.shortField == 13)");
            q.setOrdering("longField ascending");
            c = (Collection) q.execute();
            Assert.assertEquals(2,c.size());
            Iterator it = c.iterator();
            Assert.assertEquals(ids[2],JDOHelper.getObjectId(it.next()));
            Assert.assertEquals(ids[3],JDOHelper.getObjectId(it.next()));
           
            q = pm.newQuery(BasicTypeHolder.class,"(this.longField == 4 && this.shortField == 14) || (this.longField != 5 && this.shortField == 13)");
            q.setOrdering("longField ascending");
            c = (Collection) q.execute();
            Assert.assertEquals(2,c.size());
            it = c.iterator();
            Assert.assertEquals(ids[2],JDOHelper.getObjectId(it.next()));
            Assert.assertEquals(ids[3],JDOHelper.getObjectId(it.next()));
            
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }

    public void testQueryCheckValueDiffFields()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            DoubleLink tom = new DoubleLink(1, "Tom");
            DoubleLink dick = new DoubleLink(2, "Dick");
            DoubleLink harry = new DoubleLink(3, "Harry");
            tom.setFront(dick);
            dick.setBack(tom);
            dick.setFront(harry);
            harry.setBack(dick);
            pm.makePersistent(tom);
            pm.flush();

            Query q = pm.newQuery(DoubleLink.class, "front.id == 3 && back.id == 1");
            List results = (List) q.execute();
            assertEquals(1, results.size());
            DoubleLink link = (DoubleLink)results.get(0);
            assertTrue("Incorrect DoubleLink object returned by query", link.getName().equals("Dick"));

            tx.rollback(); // Dont commit our data
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
     * Test query executed outside of a transaction that will cause another object
     * to be fetched during the query (fetching that object must not close the
     * query's connection) 
     */
    public void testNonTx() 
    {
        try 
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"testNonTx 1");
            Employee bart = new Employee(2, "Bart", "Simpson","bart@simpson.com", 1, "testNonTx 2");
            bart.setManager(homer);
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
            
            // use a fresh PM so caches are empty
            pm = pmf.getPersistenceManager();
            // important for this test is that Employee.manager is in the default fetch group
            Query q = pm.newQuery(Employee.class);
            q.setFilter("personNum==2");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
        }
        finally 
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    public void testCast()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            bart.addSubordinate(boss);
            bart.addSubordinate(boss2);
            Department deptB = new Department("DeptB");
            bart.addDepartment(deptB);
            Qualification q1 = new Qualification("q1");
            q1.setPerson(boss);
            Qualification q2 = new Qualification("q2");
            q2.setPerson(boss2);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(boss);
                pm.makePersistent(boss2);
                pm.makePersistent(q1);
                pm.makePersistent(q2);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Qualification.class);
                q.setFilter("((Employee)person).serialNo == \"serial 3\"");
                q.declareImports("import org.jpox.samples.models.company.Employee");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals("q1", ((Qualification) c.iterator().next()).getName());
                q = pm.newQuery(Qualification.class);
                q.setFilter("((Manager)person).serialNo == \"serial 4\"");
                q.declareImports("import org.jpox.samples.models.company.Employee");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals("q2", ((Qualification) c.iterator().next()).getName());

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for the close of a Query and the availability of results.
     */
    public void testKeepResultsQueryAfterQueryClose()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
                pm.makePersistent(homer);
                pm.flush();

                Query q = pm.newQuery(Manager.class);
                List results = (List) q.execute();
                assertFalse(results.isEmpty());
                results.iterator().next();
                q.closeAll();
                assertFalse(results.iterator().hasNext());
                try
                {
                    results.iterator().next();
                    fail("expected NoSuchElementException");
                }
                catch(NoSuchElementException ex)
                {
                    //expected
                }

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
                fail("Exception thrown when trying to access QueryResult after closing the Query : " + e.getMessage());
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
            clean(Manager.class);
        }
    }

    /**
     * Test for the end of a transaction and the effect on query results.
     */
    public void testKeepResultsQueryAfterTxClose()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
                Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",1,"serial 2");
                pm.makePersistent(homer);
                pm.makePersistent(bart);
                pm.flush();

                Query q = pm.newQuery(Manager.class);
                List results = (List) q.execute();
                assertFalse(results.isEmpty());
                results.iterator().next();

                tx.rollback();

                results.iterator().next();
            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
                fail("Exception thrown when trying to access QueryResult after closing the Txn : " + e.getMessage());
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
            clean(Manager.class);
        }
    }

    /**
     * Test for the closure of a PM and the effect on query results.
     */
    public void testKeepResultsQueryAfterPMClose()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
                pm.makePersistent(homer);
                pm.flush();

                Query q = pm.newQuery(Manager.class);
                List results = (List) q.execute();
                assertFalse(results.isEmpty());
                Manager m1 = (Manager)results.iterator().next();

                tx.rollback();
                pm.close();
                Manager m3 = (Manager)results.iterator().next();
                assertEquals(m1,m3);
            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
                fail("Exception thrown when trying to access QueryResult after closing the PM : " + e.getMessage());
            }
            finally
            {
                if (!pm.isClosed())
                {
                    if (tx.isActive())
                    {
                        tx.rollback();
                    }
                    pm.close();
                }
            }
        }
        finally
        {
            clean(Manager.class);
        }
    }

    /**
     * test query with "field != null"
     */
    public void testQueryWithNonNullFieldCondition()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            bart.setManager(homer);
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(homer);
                pm.makePersistent(bart);
                pm.makePersistent(boss);
                Object id = pm.getObjectId(bart);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter("this.manager != null");
                Collection c = (Collection) q.execute();
                assertEquals(1,c.size());
                assertEquals(id,pm.getObjectId(c.iterator().next()));

                // check again with null
                q = pm.newQuery(Manager.class);
                q.setFilter("this.manager == null");
                c = (Collection) q.execute();
                assertEquals(2,c.size());

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test query with missing parameter value.
     */
    public void testQueryWithUnsetParameter()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(homer);
                tx.commit();

                tx.begin();
                try
                {
                    Query q = pm.newQuery(Manager.class);
                    q.setFilter("firstName != :myName");
                    q.execute();
                    fail("Should have thrown an exception for missing parameter but didn't");
                }
                catch (JDOUserException ue)
                {
                    // Expected
                }

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }
    
    public void testBoolean()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(homer);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter("1 == 1");
                Collection c = (Collection) q.execute();
                assertEquals(1,c.size());

                q = pm.newQuery(Manager.class);
                q.setFilter("1 == 0");
                c = (Collection) q.execute();
                assertEquals(0,c.size());

                q = pm.newQuery(Manager.class);
                q.setFilter("true == true");
                c = (Collection) q.execute();
                assertEquals(1,c.size());

                q = pm.newQuery(Manager.class);
                q.setFilter("true == false");
                c = (Collection) q.execute();
                assertEquals(0,c.size());

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    public void testQueryWithDetachedObjects()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1",new Integer(10));
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setNontransactionalRead(true);

            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(boss);
                pm.makePersistent(bart);
                tx.commit();

                // non transactional read
                Collection c = (Collection) pm.newQuery(Employee.class).execute();
                Employee detachedEmployee = (Employee)pm.detachCopy(c.iterator().next());
                pm.close();
                pm = pmf.getPersistenceManager();
                tx = pm.currentTransaction();
                tx.begin();
                Query q = pm.newQuery(Employee.class,"this == p");
                q.declareParameters("Employee p");
                c = (Collection) q.execute(detachedEmployee);
                assertEquals(JDOHelper.getObjectId(detachedEmployee),JDOHelper.getObjectId(c.iterator().next()));
                tx.commit();            

            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
                fail(e.toString());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for modulo operator in a Query.
     */
    public void testModuloOperator()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField(i + 1);
                basics[i].setShortField((short) (i+11));
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();

            // Run the test
            tx.begin();

            Query q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false), "longField % 2 == 1");
            List c = (List) q.execute();
            assertTrue("Number of items returned from modulo query was incorrect : should have been 3 but was " + c.size(),
                c.size() == 3);

            for (int i=0;i<c.size();i++)
            {
                long value = ((BasicTypeHolder)c.get(i)).getLongField();
                assertTrue("Long value of returned object is incorrect : should have been 1, 3 or 5 but was " + value,
                    value == 1 || value == 3 || value == 5);
            }

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(BasicTypeHolder.class);
        }
    }

    /**
     * Test use of cast on case with inheritance using a discriminator.
     */
    public void testInheritanceCastWithDiscriminator()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            PBase base = new PBase();
            PSub1 sub1 = new PSub1();
            PSub2 sub2 = new PSub2();
            pm.makePersistent(base);
            pm.makePersistent(sub1);
            pm.makePersistent(sub2);
            tx.commit();

            // Run the test
            tx.begin();
            Query q = pm.newQuery(PBase.class,
                "this instanceof " + PSub1.class.getName() + " || this instanceof " + PSub2.class.getName());
            List c = (List) q.execute();
            assertEquals("Number of items returned from instanceof+discriminator query was incorrect", 2, c.size());
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception during test", e);
            fail("Exception thrown during test " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(PSub1.class);
            clean(PSub2.class);
            clean(PBase.class);
        }
    }

    /**
     * Test for deletion by query.
     */
    public void testDeleteByQuery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some sample data (to delete)
            tx.begin();
            BasicTypeHolder basics[] = new BasicTypeHolder[5];
            for (int i=0; i<basics.length; i++)
            {
                basics[i] = new BasicTypeHolder();
                basics[i].setLongField(i + 1);
                basics[i].setShortField((short) (i+11));
                basics[i].setCharField('0');
            }
            pm.makePersistentAll(basics);
            tx.commit();

            // Run the test
            tx.begin();

            Query q = pm.newQuery(BasicTypeHolder.class);
            long numberDeleted = q.deletePersistentAll();
            assertEquals("Number of BasicTypeHolder objects deleted by query was incorrect", 5, numberDeleted);

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(BasicTypeHolder.class);
        }
    }

    public void testClose()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            try
            {
                tx.begin();
                BasicTypeHolder basics[] = new BasicTypeHolder[5];
                for (int i=0; i<basics.length; i++)
                {
                    basics[i] = new BasicTypeHolder();
                    basics[i].setLongField(i+1);
                    basics[i].setShortField((short) (i+11));
                    basics[i].setCharField('0');
                }
                pm.makePersistentAll(basics);
                tx.commit();            
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                Collection c = (Collection) q.execute(new Long(1));

                Assert.assertEquals(1, c.size());

                q.closeAll();

                tx.commit();
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            boolean success = false;

            try
            {
                success = false;
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                Collection c = (Collection) q.execute(new Long(1));
                q.closeAll();
                c.size();
                tx.commit();
            }
            catch (JDOUserException e)
            {
                success = true;
            }
            finally
            {
                Assert.assertTrue("Query result has been closed and exception was expected", success);
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                Collection c = (Collection) q.execute(new Long(1));
                Iterator iterator = c.iterator();
                q.closeAll();
                Assert.assertFalse("Query result has been closed and iterator should be closed", iterator.hasNext());
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                success = false;
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                Collection c = (Collection) q.execute(new Long(1));
                Iterator iterator = c.iterator();
                q.closeAll();
                iterator.next();
                tx.commit();
            }
            catch (NoSuchElementException e)
            {
                success = true;
            }
            finally
            {
                Assert.assertTrue("Query result has been closed and exception was expected", success);
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                Collection c = (Collection) q.execute(new Long(1));
                Iterator iterator = c.iterator();
                q.close(c);
                Assert.assertFalse("Query result has been closed and iterator should be closed", iterator.hasNext());
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                success = false;
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                Collection c = (Collection) q.execute(new Long(1));
                Iterator iterator = c.iterator();
                q.close(c);
                iterator.next();
                tx.commit();
            }
            catch (NoSuchElementException e)
            {
                success = true;
            }
            finally
            {
                Assert.assertTrue("Query result has been closed and exception was expected", success);
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                Collection c = (Collection) q.execute(new Long(1));
                Iterator iterator = c.iterator();
                q.close(new HashSet());
                Assert.assertTrue("Query result has not been closed and iterator should not be closed", iterator.hasNext());
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                Collection c = (Collection) q.execute(new Long(1));
                Iterator iterator = c.iterator();
                q.close(new HashSet());
                iterator.next();
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                Collection c = (Collection) q.executeWithArray(new Object[] {new Long(1)});
                Iterator iterator = c.iterator();
                q.close(c);
                Assert.assertFalse("Query result has been closed and iterator should be closed", iterator.hasNext());
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                success = false;
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                Collection c = (Collection) q.executeWithArray(new Object[] {new Long(1)});
                Iterator iterator = c.iterator();
                q.close(c);
                iterator.next();
                tx.commit();
            }
            catch (NoSuchElementException e)
            {
                success = true;
            }
            finally
            {
                Assert.assertTrue("Query result has been closed and exception was expected", success);
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                q.setCandidates(new HashSet());
                Collection c = (Collection) q.executeWithArray(new Object[] {new Long(1)});
                Iterator iterator = c.iterator();
                q.close(c);
                Assert.assertFalse("Query result has been closed and iterator should be closed", iterator.hasNext());
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                success = false;
                tx.begin();
                Query q = pm.newQuery(BasicTypeHolder.class, "(this.longField == longVar)");
                q.declareParameters("java.lang.Long longVar");
                q.setCandidates(new HashSet());
                Collection c = (Collection) q.executeWithArray(new Object[] {new Long(1)});
                Iterator iterator = c.iterator();
                q.close(c);
                iterator.next();
                tx.commit();
            }
            catch (NoSuchElementException e)
            {
                success = true;
            }
            finally
            {
                Assert.assertTrue("Query result has been closed and exception was expected", success);
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
        }
        finally
        {
            pm.close();

            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }

    /**
     * test query with use of parameter field comparison with field
     */
    public void testQueryWithParameterFieldComparison()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object p1Id = null;
            try
            {
                tx.begin();
                Person p1 = new Person(1, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");
                Person p2 = new Person(2, "Road", "Runner", "road.runner@warnerbros.com");
                Person p3 = new Person(3, "Bart", "Simpson", "bart.simpson@simpsons.com");
                p1.setBestFriend(p2);
                p2.setBestFriend(p3);

                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.makePersistent(p3);
                p1Id = pm.getObjectId(p1);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = (Person)pm.getObjectById(p1Id);

                Query q = pm.newQuery("SELECT FROM " + Person.class.getName() +
                " WHERE this.firstName == :param.bestFriend.firstName");
                Collection c = (Collection) q.execute(p1);
                assertEquals(1, c.size());
                Person p = (Person)c.iterator().next();
                assertEquals("FirstName is incorrect", "Road", p.getFirstName());
                assertEquals("LastName is incorrect", "Runner", p.getLastName());

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test query with use of parameter for a 1-1 relation, input as null.
     * This tests the SQL generation of the query since, while there is a parameter, we don't want it
     * to appear in the SQL as "COL == ?", since the value of the paramerter is null, hence the SQL should
     * be "COL IS NULL".
     */
    public void testQueryWithOneToOneParameterInputAsNull()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(1, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");
                Person p2 = new Person(2, "Road", "Runner", "road.runner@warnerbros.com");
                Person p3 = new Person(3, "Bart", "Simpson", "bart.simpson@simpsons.com");
                p1.setBestFriend(p2);
                p2.setBestFriend(p3);

                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.makePersistent(p3);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery("SELECT FROM " + Person.class.getName() +
                " WHERE bestFriend == :param");
                Collection c = (Collection) q.execute(null);
                assertEquals(1, c.size());
                Person p = (Person)c.iterator().next();
                assertEquals("FirstName is incorrect", "Bart", p.getFirstName());
                assertEquals("LastName is incorrect", "Simpson", p.getLastName());

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for the querying of an interface (persistent).
     */
    public void testQueryOfInterface()
    {
        Mouse mouse1 = new Mouse();
        mouse1.setId(101);
        mouse1.setManufacturer("Logitech");
        mouse1.setModel("M305");
        Keyboard kb1 = new Keyboard();
        kb1.setId(102);
        kb1.setManufacturer("Logitech");
        kb1.setModel("K304");

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(mouse1);
            pm.makePersistent(kb1);
            tx.commit();
    
            tx.begin();
            try
            {
                Query q = pm.newQuery(ComputerPeripheral.class);
                List<ComputerPeripheral> results = (List<ComputerPeripheral>) q.execute();
                assertEquals("Number of results incorrect", 2, results.size());
                Iterator<ComputerPeripheral> resultsIter = results.iterator();
                boolean mousePresent = false;
                boolean kbPresent = false;
                while (resultsIter.hasNext())
                {
                    ComputerPeripheral peri = resultsIter.next();
                    if (peri instanceof Mouse && peri.getId() == 101 && peri.getManufacturer().equals("Logitech") &&
                        peri.getModel().equals("M305"))
                    {
                        mousePresent = true;
                    }
                    if (peri instanceof Keyboard && peri.getId() == 102 && peri.getManufacturer().equals("Logitech") &&
                        peri.getModel().equals("K304"))
                    {
                        kbPresent = true;
                    }
                }
                if (!mousePresent)
                {
                    fail("Mouse not present in results");
                }
                if (!kbPresent)
                {
                    fail("Keyboard not present in results");
                }

                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Mouse.class);
            clean(Keyboard.class);
        }
    }

    /**
     * test query with comparison of an equality clause with a boolean.
     * "(x == 3) == TRUE"
     */
    public void testQueryWithEqualityComparisonToBoolean()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(1, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");

                pm.makePersistent(p1);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery("SELECT FROM " + Person.class.getName() + " WHERE (personNum == 1) == true");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                Person p = (Person)c.iterator().next();
                assertEquals("FirstName is incorrect", "Bugs", p.getFirstName());
                assertEquals("LastName is incorrect", "Bunny", p.getLastName());

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test use of bulk UPDATE.
     */
    public void testBulkUpdate()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(1, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");
                p1.setAge(24);

                pm.makePersistent(p1);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_QUERY_JDOQL_ALLOWALL, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery("UPDATE " + Person.class.getName() + " SET this.age = 28 WHERE personNum == 1");
                Object result = q.execute();
                assertEquals(new Long(1), result);

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    public void testOrdering()
    {
        Person p1 = new Person(1, "Bugs", "Bunny", null);
        p1.setAge(27);
        Person p2 = new Person(2, "Road", "Runner", "road.runner@warnerbros.com");
        p2.setAge(28);
        Person p3 = new Person(3, "Bart", "Simpson", "bart.simpson@simpsons.com");
        p3.setAge(28);

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(p1);
            pm.makePersistent(p2);
            pm.makePersistent(p3);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery("SELECT FROM " + Person.class.getName() + " ORDER BY emailAddress ASC NULLS FIRST");
            Collection c = (Collection) q.execute();
            assertEquals(3,c.size());
            if (vendorID != null)
            {
                if (((RDBMSStoreManager)storeMgr).getDatastoreAdapter().supportsOption(DatastoreAdapter.ORDERBY_NULLS_DIRECTIVES))
                {
                    // RDBMS supports NULLS FIRST so check order
                    Iterator<Person> iter = c.iterator();
                    Person pr1 = iter.next();
                    Person pr2 = iter.next();
                    Person pr3 = iter.next();
                    assertEquals("Bugs", pr1.getFirstName());
                    assertEquals("Bart", pr2.getFirstName());
                    assertEquals("Road", pr3.getFirstName());
                }
            }
            LOG.info(">> order results=" + StringUtils.collectionToString(c));
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Person.class);
        }
    }

    public void testOrderingAndFunction2Parms()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Office o1 = new Office(1, "Green", "Big spacious office");
                Office o2 = new Office(2, "Blue", "Pokey office at the back of the building");
                Office o3 = new Office(1, "Yellow", "Massive open plan office");
                Department d1 = new Department("Finance");
                Department d2 = new Department("Customer Support");
                Department d3 = new Department("Sales");
                Department d4 = new Department("IT");
                o1.addDepartment(d1);
                o1.addDepartment(d3);
                o2.addDepartment(d2);
                o3.addDepartment(d4);
                o3.addDepartment(d3);
                pm.makePersistent(o1);
                pm.makePersistent(o2);
                pm.makePersistent(o3);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
                fail("Error persisting sample data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery(Office.class);
                q.setFilter("dept.name.equals(\"Finance\") && departments.contains(dept)");
                q.declareVariables("Department dept");
                q.declareImports("import org.jpox.samples.models.company.Department");
                List l = new ArrayList((Collection) q.execute());
                assertEquals(1,l.size());

                q = pm.newQuery(Office.class);
                q.setFilter("dept.name.equals(\"Sales\") && departments.contains(dept)");
                q.declareVariables("Department dept");
                q.declareImports("import org.jpox.samples.models.company.Department");
                q.setOrdering("roomName.substring(0,10) ascending, description ascending");
                l = new ArrayList((Collection) q.execute());
                assertEquals(2,l.size());
                assertEquals(((Office)l.get(0)).getRoomName(),"Green");
                assertEquals(((Office)l.get(1)).getRoomName(),"Yellow");

                tx.commit();            
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for multithreaded queries for candidate collections.
     */
    public void testMultipleActiveQueryCandidateCollection()
    {
        int THREAD_SIZE = 40;
        Thread[] threads = new Thread[THREAD_SIZE];
        MultithreadRunnerCandidateCollection[] runner = new MultithreadRunnerCandidateCollection[THREAD_SIZE];

        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            runPrepareTestCandidateCollection(pm);

            try
            {
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    runner[i] = new MultithreadRunnerCandidateCollection();
                    threads[i] = new Thread(runner[i]);
                    threads[i].start();
                }
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i].join();
                    if (runner[i].exception != null)
                    {
                        fail("Collection.contains thread runner failed; consult the log for details : " + runner[i].exception.getMessage());
                    }
                }
            }
            catch (RuntimeException e)
            {
                fail(e.getMessage());
            }
            catch (InterruptedException e)
            {   
                fail(e.getMessage());
            }
        }
        finally
        {
            pm.close();
            clean(Employee.class);
        }
    }

    private class MultithreadRunnerCandidateCollection implements Runnable
    {
        // Exception set when the thread fails internally so we can communicate
        public Exception exception = null;

        public MultithreadRunnerCandidateCollection()
        {
        }

        public void run()
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            try
            {
                runTestCandidateCollection(pm);
            }
            catch (Exception e)
            {
                // Just set the exception for handling later
                this.exception = e;
                return;
            }
            finally
            {
                pm.close();
            }
        }
    }

    /**
     * Test for multithreaded queries for Map.get().
     */
    public void testMultipleActiveQueryMapGet()
    {
        int THREAD_SIZE = 40;
        Thread[] threads = new Thread[THREAD_SIZE];
        MultithreadRunnerMapGet[] runner = new MultithreadRunnerMapGet[THREAD_SIZE];

        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            Object idHolder1 = null;
            Object idValue1 = null;
            Object idValue2 = null;
            Object idValue3 = null;
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                MapHolder holder1 = new MapHolder("First");
                MapHolder holder2 = new MapHolder("Second");
                MapValueItem item1 = new MapValueItem("Item 1", "First Item");
                MapValueItem item2 = new MapValueItem("Item 2", "Second Item");
                MapValueItem item3 = new MapValueItem("Item 3", "Third Item");
                holder1.getJoinMapNonPC().put(item1.getName(), item1);
                holder1.getJoinMapNonPC().put(item2.getName(), item2);
                holder1.getJoinMapNonPC().put(item3.getName(), item3);
                holder2.getJoinMapNonPC().put(item3.getName(), item3);
                pm.makePersistent(holder1);
                pm.makePersistent(holder2);
                tx.commit();

                idHolder1 = JDOHelper.getObjectId(holder1);
                idValue1 = JDOHelper.getObjectId(item1);
                idValue2 = JDOHelper.getObjectId(item2);
                idValue3 = JDOHelper.getObjectId(item3);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            try
            {
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    runner[i] = new MultithreadRunnerMapGet(idHolder1, idValue1, idValue2, idValue3);
                    threads[i] = new Thread(runner[i]);
                    threads[i].start();
                }
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i].join();
                    if (runner[i].exception != null)
                    {
                        fail("Map.get thread runner failed; consult the log for details : " + runner[i].exception.getMessage());
                    }
                }
            }
            catch (RuntimeException e)
            {
                fail(e.getMessage());
            }
            catch (InterruptedException e)
            {
                fail(e.getMessage());
            }
        }
        finally
        {
            clean(MapHolder.class);
            clean(MapValueItem.class);
        }
    }

    private class MultithreadRunnerMapGet implements Runnable
    {
        Object idHolder1 = null;
        Object idValue1 = null;
        Object idValue2 = null;
        Object idValue3 = null;

        // Exception set when the thread fails internally so we can communicate
        public Exception exception = null;

        public MultithreadRunnerMapGet(Object idHolder1,
                Object idValue1, Object idValue2, Object idValue3)
        {
            this.idHolder1 = idHolder1;
            this.idValue1 = idValue1;
            this.idValue2 = idValue2;
            this.idValue3 = idValue3;
        }

        public void run()
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                MapValueItem value1 = (MapValueItem)pm.getObjectById(idValue1);
                MapValueItem value2 = (MapValueItem)pm.getObjectById(idValue2);
                MapValueItem value3 = (MapValueItem)pm.getObjectById(idValue3);

                //check map.get -> object expression == object literal
                Query q = pm.newQuery(MapHolder.class,"this.joinMapNonPC.get(\"Item 1\") == w");
                q.declareParameters("org.jpox.samples.one_many.map.MapValueItem w");
                Collection c = (Collection) q.execute(value1);
                Assert.assertEquals(1, c.size());
                
                Assert.assertEquals(idHolder1, JDOHelper.getObjectId(c.iterator().next()));
                q = pm.newQuery(MapHolder.class,"this.joinMapNonPC.get(\"Item 3\") == w");
                q.declareParameters("org.jpox.samples.one_many.map.MapValueItem w");
                c = (Collection) q.execute(value3);
                Assert.assertEquals(2, c.size());
                
                q = pm.newQuery(MapHolder.class, "this.joinMapNonPC.get(\"Item 1\") == w");
                q.declareParameters("org.jpox.samples.one_many.map.MapValueItem w");
                c = (Collection) q.execute(value2);
                Assert.assertEquals(0, c.size());

                q = pm.newQuery(MapHolder.class, "this.joinMapNonPC.get(\"Item 4\") == w");
                q.declareParameters("org.jpox.samples.one_many.map.MapValueItem w");
                c = (Collection) q.execute(value3);
                Assert.assertEquals(0, c.size());

                //check object literal == map.get -> object expression
                q = pm.newQuery(MapHolder.class, "w == this.joinMapNonPC.get(\"Item 1\")");
                q.declareParameters("org.jpox.samples.one_many.map.MapValueItem w");
                c = (Collection) q.execute(value1);
                Assert.assertEquals(1, c.size());
                Assert.assertEquals(idHolder1, JDOHelper.getObjectId(c.iterator().next()));

                q = pm.newQuery(MapHolder.class, "w == this.joinMapNonPC.get(\"Item 3\")");
                q.declareParameters("org.jpox.samples.one_many.map.MapValueItem w");
                c = (Collection) q.execute(value3);
                Assert.assertEquals(2, c.size());

                q = pm.newQuery(MapHolder.class, "w == this.joinMapNonPC.get(\"Item 1\")");
                q.declareParameters("org.jpox.samples.one_many.map.MapValueItem w");
                c = (Collection) q.execute(value2);
                Assert.assertEquals(0, c.size());

                q = pm.newQuery(MapHolder.class, "w == this.joinMapNonPC.get(\"Item 4\")");
                q.declareParameters("org.jpox.samples.one_many.map.MapValueItem w");
                c = (Collection) q.execute(value3);
                Assert.assertEquals(0, c.size());

                //test map.get in map literals
                Map map1 = new HashMap();
                map1.put("Item 1", value1);

                q = pm.newQuery(MapValueItem.class, "this == map.get(\"Item 1\")");
                q.declareParameters("java.util.Map map");
                c = (Collection) q.execute(map1);
                Assert.assertEquals(1, c.size());

                Map map2 = new HashMap();
                q = pm.newQuery(MapValueItem.class, "this == map.get(\"Item 1\")");
                q.declareParameters("java.util.Map map");
                c = (Collection) q.execute(map2);
                Assert.assertEquals(0, c.size());

                Map map3 = new HashMap();
                map3.put("Item 1", value1);
                map3.put("Item 2", value2);
                q = pm.newQuery(MapValueItem.class, "this == map.get(\"Item 3\")");
                q.declareParameters("java.util.Map map");
                c = (Collection) q.execute(map3);
                Assert.assertEquals(0, c.size());

                tx.commit();
            }
            catch (Exception e)
            {
                // Just set the exception for handling later
                LOG.error("Exception thrown during test", e);
                this.exception = e;
                return;
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
}