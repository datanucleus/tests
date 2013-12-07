/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
2003 Mike Martin (TJDO) - original JDOQLQueryTests
2004 Erik Bengtson - added many many tests
    ...
***********************************************************************/
package org.datanucleus.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import junit.framework.Assert;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.many_many.AccountCustomer;
import org.jpox.samples.many_many.GasSupplier;
import org.jpox.samples.many_many.OilSupplier;
import org.jpox.samples.many_many.OneOffCustomer;
import org.jpox.samples.many_many.PetroleumCustomer;
import org.jpox.samples.many_many.PetroleumSupplier;
import org.jpox.samples.models.company.CompanyHelper;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Identity1;
import org.jpox.samples.models.company.Identity2;
import org.jpox.samples.models.company.Identity3;
import org.jpox.samples.models.company.Identity4;
import org.jpox.samples.models.company.Identity5;
import org.jpox.samples.models.company.InsuranceDepartment;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.Office;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.PersonHolder;
import org.jpox.samples.one_many.bidir.Animal;
import org.jpox.samples.one_many.bidir.Cattle;
import org.jpox.samples.one_many.bidir.DairyFarm;
import org.jpox.samples.one_many.bidir.Farm;
import org.jpox.samples.one_many.bidir.Poultry;
import org.jpox.samples.types.basic.BasicTypeHolder;
import org.jpox.samples.types.basic.StringHolder;

/**
 * Test of the various ways of constraining the result of a JDOQL query.
 * Namely, setOrdering, setResult, setResultClass, setRange, setUnique.
 */
public class JDOQLResultTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the <tt>TestCase</tt>.
     */
    public JDOQLResultTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[] {
                    Person.class,
                    Employee.class,
                    Manager.class,
                    Office.class,
                    Department.class,
                    InsuranceDepartment.class,
                    BasicTypeHolder.class,
                    PetroleumCustomer.class,
                    PetroleumSupplier.class,
                    AccountCustomer.class,
                    OneOffCustomer.class,
                    GasSupplier.class,
                    OilSupplier.class,
                    Farm.class,
                    DairyFarm.class,
                    Animal.class,
                    Cattle.class,
                    Poultry.class
                    });
            initialised = true;
        }        
    }

    /**
     * Test case to use the JDO 2.0 setUnique() and setRange() methods to
     * control the number and type of objects returned from the query.
     */
    public void testRangeAndUnique()
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
                Query q=pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setFilter("firstName == \"Bugs\"");
                q.setUnique(true);
                Object results=q.execute();
                assertTrue("setUnique() test returned an object of an incorrect type ",results instanceof Employee);
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            boolean success = false;
            try
            {
                Query q=pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setUnique(true);
                q.execute();
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                success = true;
            }
            assertTrue("expected JdoUserException for unique == true and returned more than one instance",success);

            try
            {
                Query q=pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setRange(1,3);
                Collection results=(Collection)q.execute();
                assertTrue("setRange() test returned an incorrect number of results : should have been 2 but was " + results.size(),results.size() == 2);
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

    
    public void testUniqueResultAggregates()
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
                Query q=pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setResult("max(salary)");
                Object results=q.execute();
                assertTrue("test returned an object of an incorrect type : type is "+results.getClass(),results instanceof Float);
                q.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }

            try
            {
                Query q=pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                q.setResult("max (salary)");
                Object results=q.execute();
                assertTrue("test returned an object of an incorrect type : type is "+results.getClass(),results instanceof Float);
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
     * Test cartesian products
     */
    public void testSetResultCartesianProduct1()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager bart = new Manager(2, "Bart", "Simpson", "bart@simpson.com", 2, "serial 2");
                Manager boss[] = new Manager[5];
                boss[0] = new Manager(3, "Boss", "WakesUp", "boss@wakes.up", 4, "serial 3");
                boss[1] = new Manager(4, "Boss", "WakesUp2", "boss2@wakes.up", 5, "serial 4");
                boss[2] = new Manager(5, "Boss", "WakesUp3", "boss3@wakes.up", 6, "serial 5");
                boss[3] = new Manager(6, "Boss", "WakesUp4", "boss4@wakes.up", 7, "serial 6");
                boss[4] = new Manager(7, "Boss", "WakesUp5", "boss5@wakes.up", 8, "serial 7");
                Office office = new Office(5, "cubicle 1", "none");
                bart.addSubordinate(boss[0]);
                bart.addSubordinate(boss[1]);
                Department deptA = new Department("DeptA");
                Department deptB = new Department("DeptB");
                bart.getDepartments().add(deptA);
                boss[3].getDepartments().add(deptB);
                pm.makePersistent(bart);
                pm.makePersistentAll(boss);
                pm.makePersistent(office);
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
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Employee e");
                    List results = (List)q.execute();
                    assertEquals(12,results.size());
                    q.closeAll();
                    fail("JDOUserException expected");
                }
                catch (JDOUserException e)
                {
                    //expected;
                }

                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Employee e");
                    q.setResult("this");
                    List results = (List)q.execute();
                    assertEquals(12,results.size());
                    q.closeAll();
                    fail("JDOUserException expected");
                }
                catch (JDOUserException e)
                {
                    //expected;
                }

                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Employee e");
                    q.setResult("this, e");
                    List results = (List)q.execute();
                    assertEquals(12,results.size());
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }

                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Employee e; Office o");
                    q.setResult("this, e, o");
                    List results = (List)q.execute();
                    assertEquals(12,results.size());
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }

                Office office = new Office(6, "cubicle 2", "none");
                pm.makePersistent(office);
                pm.flush();

                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Employee e; Office o");
                    q.setResult("this, e, o");
                    List results = (List)q.execute();
                    assertEquals(24, results.size());
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }

                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Department d");
                    q.setResult("this, d");
                    List results = (List)q.execute();
                    assertEquals(4,results.size());
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Department d");
                    q.setResult("this, d");
                    q.setFilter("d==this");
                    List results = (List)q.execute();
                    assertEquals(2,results.size());
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Department d");
                    q.setResult("this, d");
                    q.setFilter("d!=this");
                    List results = (List)q.execute();
                    assertEquals(2,results.size());
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }                
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Manager e");
                    q.setResult("this, e");
                    List results = (List)q.execute();
                    assertEquals(12,results.size());

                    q.closeAll();
                }
                catch (RuntimeException e)
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
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test cartesian products
     */
    public void testSetResultCartesianProduct2()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PetroleumCustomer customer1 = new PetroleumCustomer("C1");
                PetroleumCustomer customer2 = new PetroleumCustomer("C2");
                PetroleumCustomer customer3 = new PetroleumCustomer("C3");
                PetroleumSupplier supplier1 = new PetroleumSupplier("S1");
                PetroleumSupplier supplier2 = new PetroleumSupplier("S2");
                PetroleumSupplier supplier3 = new PetroleumSupplier("S3");
                PetroleumSupplier supplier4 = new PetroleumSupplier("S4");
                PetroleumSupplier supplier5 = new PetroleumSupplier("S5");
                customer1.addSupplier(supplier1);
                customer1.addSupplier(supplier2);
                customer2.addSupplier(supplier1);
                customer2.addSupplier(supplier3);
                customer2.addSupplier(supplier5);
                customer3.addSupplier(supplier1);
                customer3.addSupplier(supplier2);
                customer3.addSupplier(supplier3);
                customer3.addSupplier(supplier4);
                pm.makePersistent(customer1);
                pm.makePersistent(customer2);
                pm.makePersistent(customer3);
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
                try
                {
                    Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                    q.declareVariables("PetroleumSupplier s");
                    q.setResult("this, s");
                    List results = (List)q.execute();
                    assertEquals(15,results.size());
                    q.closeAll();
                }
                catch (RuntimeException e)
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
            clean(PetroleumCustomer.class);
            clean(PetroleumSupplier.class);
        }
    }

    /**
     * Test cartesian products
     */
    public void testSetResultCartesianProductCollection()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager bart = new Manager(2, "Bart", "Simpson", "bart@simpson.com", 2, "serial 2");
                Manager boss[] = new Manager[5];
                boss[0] = new Manager(3, "Boss", "WakesUp", "boss@wakes.up", 4, "serial 3");
                boss[1] = new Manager(4, "Boss", "WakesUp2", "boss2@wakes.up", 5, "serial 4");
                boss[2] = new Manager(5, "Boss", "WakesUp3", "boss3@wakes.up", 6, "serial 5");
                boss[3] = new Manager(6, "Boss", "WakesUp4", "boss4@wakes.up", 7, "serial 6");
                boss[4] = new Manager(7, "Boss", "WakesUp5", "boss5@wakes.up", 8, "serial 7");
                Office office = new Office(5, "cubicle 1", "none");
                bart.addSubordinate(boss[0]);
                bart.addSubordinate(boss[1]);
                Department deptA = new Department("DeptA");
                Department deptB = new Department("DeptB");
                bart.getDepartments().add(deptA);
                boss[3].getDepartments().add(deptB);
                pm.makePersistent(bart);
                pm.makePersistentAll(boss);
                pm.makePersistent(office);
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
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Manager e");
                    q.setResult("this, e.departments");
                    q.execute();
                    q.closeAll();
                    fail("Expected user exception");
                }
                catch (JDOUserException e)
                {
                    //expected
                }
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Manager e");
                    q.setResult("this, e.departments");
                    q.compile();
                    q.closeAll();
                    fail("Expected user exception");
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
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test cartesian products
     */
    public void testSetResultCartesianProductContains1()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                try
                {
                    tx.begin();
                    Manager bart = new Manager(2, "Bart", "Simpson", "bart@simpson.com", 2, "serial 2");
                    Manager boss[] = new Manager[5];
                    boss[0] = new Manager(3, "Boss", "WakesUp", "boss@wakes.up", 4, "serial 3");
                    boss[1] = new Manager(4, "Boss", "WakesUp2", "boss2@wakes.up", 5, "serial 4");
                    boss[2] = new Manager(5, "Boss", "WakesUp3", "boss3@wakes.up", 6, "serial 5");
                    boss[3] = new Manager(6, "Boss", "WakesUp4", "boss4@wakes.up", 7, "serial 6");
                    boss[4] = new Manager(7, "Boss", "WakesUp5", "boss5@wakes.up", 8, "serial 7");
                    Office office = new Office(5, "cubicle 1", "none");
                    bart.addSubordinate(boss[0]);
                    bart.addSubordinate(boss[1]);
                    Department deptA = new Department("DeptA");
                    Department deptB = new Department("DeptB");
                    bart.getDepartments().add(deptA);
                    boss[3].getDepartments().add(deptB);
                    pm.makePersistent(bart);
                    pm.makePersistentAll(boss);
                    pm.makePersistent(office);
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
                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.declareVariables("Manager e");
                    q.setResult("this, e");
                    q.setFilter("e.departments.contains(this)");
                    List results = (List) q.execute();
                    assertEquals(2, results.size());
                    q.closeAll();
                    tx.commit();
                }
                catch (Exception e)
                {
                    LOG.error("Exception during test", e);
                    fail("Exception during test : " + e.getMessage());
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
                    try
                    {
                        Query q = pm.newQuery(pm.getExtent(Department.class, false));
                        q.declareVariables("Manager e");
                        q.setResult("e");
                        q.setFilter("e.departments.contains(this)");
                        List results = (List) q.execute();
                        assertEquals(2, results.size());
                        q.closeAll();
                    }
                    catch (RuntimeException e)
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
            finally
            {
                pm.close();
            }
        }
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test cartesian products
     */
    public void testSetResultCartesianProductContains2()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PetroleumCustomer customer1 = new PetroleumCustomer("C1");
                PetroleumCustomer customer2 = new PetroleumCustomer("C2");
                PetroleumCustomer customer3 = new PetroleumCustomer("C3");
                PetroleumCustomer customer4 = new PetroleumCustomer("C4");
                PetroleumSupplier supplier1 = new PetroleumSupplier("S1");
                PetroleumSupplier supplier2 = new PetroleumSupplier("S2");
                PetroleumSupplier supplier3 = new PetroleumSupplier("S3");
                PetroleumSupplier supplier4 = new PetroleumSupplier("S4");
                PetroleumSupplier supplier5 = new PetroleumSupplier("S5");
                customer1.addSupplier(supplier1);
                customer1.addSupplier(supplier2);
                customer2.addSupplier(supplier1);
                customer2.addSupplier(supplier3);
                customer2.addSupplier(supplier5);
                customer3.addSupplier(supplier1);
                customer3.addSupplier(supplier2);
                customer3.addSupplier(supplier3);
                pm.makePersistent(customer1);
                pm.makePersistent(customer2);
                pm.makePersistent(customer3);
                pm.makePersistent(supplier4);
                pm.makePersistent(customer4);
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
                try
                {
                    Query q = pm.newQuery(pm.getExtent(PetroleumSupplier.class, false));
                    q.declareVariables("PetroleumCustomer c");
                    q.setResult("this, c");
                    q.setFilter("c.suppliers.contains(this)");
                    List results = (List)q.execute();

                    assertEquals(8,results.size());

                    q.closeAll();
                }
                catch (RuntimeException e)
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
            try
            {
                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(PetroleumSupplier.class, false));
                    q.declareVariables("PetroleumCustomer c");
                    q.setResult("this");
                    q.setFilter("c.suppliers.contains(this)");
                    List results = (List)q.execute();

                    assertEquals(8,results.size());

                    q.closeAll();
                }
                catch (RuntimeException e)
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
            try
            {
                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(PetroleumSupplier.class, false));
                    q.declareVariables("PetroleumCustomer c");
                    q.setResult("distinct this");
                    q.setFilter("c.suppliers.contains(this)");
                    List results = (List)q.execute();

                    assertEquals(4,results.size());

                    q.closeAll();
                }
                catch (RuntimeException e)
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

            try
            {
                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                    q.declareVariables("PetroleumSupplier s");
                    q.setResult("this, s");
                    q.setFilter("suppliers.contains(s)");
                    List results = (List)q.execute();

                    assertEquals(8,results.size());

                    q.closeAll();
                }
                catch (RuntimeException e)
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
            try
            {
                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                    q.declareVariables("PetroleumSupplier s");
                    q.setResult("distinct this, s");
                    q.setFilter("suppliers.contains(s)");
                    List results = (List)q.execute();

                    assertEquals(8,results.size());

                    q.closeAll();
                }
                catch (RuntimeException e)
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
            try
            {
                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                    q.declareVariables("PetroleumSupplier s");
                    q.setResult("s");
                    q.setFilter("suppliers.contains(s)");
                    List results = (List)q.execute();
                    assertEquals(8,results.size());

                    q.closeAll();
                }
                catch (RuntimeException e)
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
            try
            {
                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                    q.declareVariables("PetroleumSupplier s");
                    q.setResult("distinct s");
                    q.setFilter("suppliers.contains(s)");
                    List results = (List)q.execute();
                    assertEquals(4,results.size());
                    q.closeAll();
                }
                catch (RuntimeException e)
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
            try
            {
                tx.begin();

                Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                q.declareVariables("PetroleumSupplier s");
                q.setResult("s");
                q.setFilter("suppliers.contains(s) && name=='C1'");
                List results = (List)q.execute();
                assertEquals(2,results.size());
                q.closeAll();
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
                Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                q.declareVariables("PetroleumSupplier s");
                q.setResult("COUNT(s)");
                q.setFilter("name=='C1' && suppliers.contains(s)");
                Long results = (Long)q.execute();
                assertEquals(2, results.longValue());
                q.closeAll();
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
                Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                q.declareVariables("PetroleumSupplier s");
                q.setResult("COUNT(s)");
                q.setFilter("suppliers.contains(s) && name=='C1'");
                Long results = (Long)q.execute();
                assertEquals(2, results.longValue());
                q.closeAll();
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

                Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                q.declareVariables("PetroleumSupplier s");
                q.setResult("COUNT(s), s.name");
                q.setFilter("suppliers.contains(s) && name=='C1'");
                q.setGrouping("s.name");
                List results = (List)q.execute();
                assertEquals(2, results.size());
                assertEquals(1, ((Long)((Object[])results.get(0))[0]).longValue());
                assertEquals(1, ((Long)((Object[])results.get(1))[0]).longValue());
                q.closeAll();
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
                Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                q.declareVariables("PetroleumSupplier s");
                q.setResult("COUNT(s)");
                q.setFilter("suppliers.contains(s)");
                Long results = (Long)q.execute();
                assertEquals(8, results.longValue());
                q.closeAll();
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

                Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                q.declareVariables("PetroleumSupplier s");
                q.setResult("COUNT(distinct s)");
                q.setFilter("suppliers.contains(s)");
                Long results = (Long)q.execute();
                assertEquals(4, results.longValue());
                q.closeAll();
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();             
        }
        finally
        {
            clean(PetroleumCustomer.class);
            clean(PetroleumSupplier.class);
        }
    }
    
    /**
     * Test cartesian products
     */
    public void testSetResultCartesianProduct1to1()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager bart = new Manager(2, "Bart", "Simpson", "bart@simpson.com", 2, "serial 2");
                Manager boss[] = new Manager[5];
                boss[0] = new Manager(3, "Boss", "WakesUp", "boss@wakes.up", 4, "serial 3");
                boss[1] = new Manager(4, "Boss", "WakesUp2", "boss2@wakes.up", 5, "serial 4");
                boss[2] = new Manager(5, "Boss", "WakesUp3", "boss3@wakes.up", 6, "serial 5");
                boss[3] = new Manager(6, "Boss", "WakesUp4", "boss4@wakes.up", 7, "serial 6");
                boss[4] = new Manager(7, "Boss", "WakesUp5", "boss5@wakes.up", 8, "serial 7");
                Department deptA = new Department("DeptA");
                Department deptB = new Department("DeptB");
                deptA.setManager(bart);
                deptB.setManager(boss[1]);
                pm.makePersistent(bart);
                pm.makePersistentAll(boss);
                pm.makePersistent(deptA);
                pm.makePersistent(deptB);
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

                    Query q = pm.newQuery(pm.getExtent(Department.class, false));
                    q.setResult("manager.firstName, manager.lastName");
                    q.setOrdering("name ascending");
                    List results = (List)q.execute();
                    assertEquals(2,results.size());
                    assertEquals("Bart",((Object[])results.get(0))[0]);
                    assertEquals("Simpson",((Object[])results.get(0))[1]);
                    assertEquals("Boss",((Object[])results.get(1))[0]);
                    assertEquals("WakesUp2",((Object[])results.get(1))[1]);
                    
                    q.closeAll();

                
                tx.commit();
            }            
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();
        }
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test cartesian products
     */
    public void testSetResultCartesianProductContainsVariableNoNavigation()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PetroleumCustomer customer1 = new PetroleumCustomer("C1");
                PetroleumCustomer customer2 = new PetroleumCustomer("C2");
                PetroleumCustomer customer3 = new PetroleumCustomer("C3");
                PetroleumCustomer customer4 = new PetroleumCustomer("C4");
                PetroleumSupplier supplier1 = new PetroleumSupplier("S1");
                PetroleumSupplier supplier2 = new PetroleumSupplier("S2");
                PetroleumSupplier supplier3 = new PetroleumSupplier("S3");
                PetroleumSupplier supplier4 = new PetroleumSupplier("S4");
                PetroleumSupplier supplier5 = new PetroleumSupplier("S5");
                customer1.addSupplier(supplier1);
                customer1.addSupplier(supplier2);
                customer2.addSupplier(supplier1);
                customer2.addSupplier(supplier3);
                customer2.addSupplier(supplier5);
                customer3.addSupplier(supplier1);
                customer3.addSupplier(supplier2);
                customer3.addSupplier(supplier3);
                pm.makePersistent(customer1);
                pm.makePersistent(customer2);
                pm.makePersistent(customer3);
                pm.makePersistent(supplier4);
                pm.makePersistent(customer4);
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
                try
                {
                    Query q = pm.newQuery(pm.getExtent(PetroleumCustomer.class, false));
                    q.declareVariables("PetroleumSupplier s");
                    q.setResult("s");
                    q.setFilter("suppliers.contains(s) && s.name=='S2'");
                    List results = (List)q.execute();
                    assertEquals(2,results.size());
                    q.closeAll();
                }
                catch (RuntimeException e)
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
            pm.close();
        }
        finally
        {
            clean(PetroleumCustomer.class);
            clean(PetroleumSupplier.class);
        }
    }

    /**
     * Test of the ordering clause of a JDOQL statement.
     */
    public void testOrdering()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Try some ordering using basic fields
                tx.begin();
                BasicTypeHolder basics[] = new BasicTypeHolder[5];
                for (int i=0; i<basics.length; i++)
                {
                    basics[i] = new BasicTypeHolder();
                    basics[i].setLongField(i+1);
                    basics[i].setShortField((short) (i+11));
                    basics[i].setIntField(20040101);
                    basics[i].setBooleanObjectField(new Boolean(i%2==0));
                    basics[i].setCharField('0');
                }
                pm.makePersistentAll(basics);

                StringHolder strings[] = new StringHolder[5];
                for (int i=0; i<strings.length; i++)
                {
                    strings[i] = new StringHolder();
                }
                strings[0].setNormalString("Aaa");
                strings[1].setNormalString("bBB");
                strings[2].setNormalString("ddd");
                strings[3].setNormalString("Ccc");
                strings[4].setNormalString("eEe");
                pm.makePersistentAll(strings);
                pm.flush();

                Query q = pm.newQuery(BasicTypeHolder.class);
                q.setOrdering("longField ascending");
                List l = (List) q.execute();
                Assert.assertEquals(5, l.size());

                q = pm.newQuery(BasicTypeHolder.class);
                q.setOrdering("longField - shortField ascending");
                l = (List) q.execute();
                Assert.assertEquals(5, l.size());

                q = pm.newQuery(BasicTypeHolder.class);
                q.setOrdering("(longField - shortField) ascending");
                l = (List) q.execute();
                Assert.assertEquals(5, l.size());

                q = pm.newQuery(BasicTypeHolder.class);
                q.setOrdering("Math.abs(-longField) ascending");
                l = (List) q.execute();
                Assert.assertEquals(5, l.size());
                Assert.assertEquals(1, ((BasicTypeHolder)l.get(0)).getLongField());
                Assert.assertEquals(2, ((BasicTypeHolder)l.get(1)).getLongField());
                Assert.assertEquals(3, ((BasicTypeHolder)l.get(2)).getLongField());
                Assert.assertEquals(4, ((BasicTypeHolder)l.get(3)).getLongField());
                Assert.assertEquals(5, ((BasicTypeHolder)l.get(4)).getLongField());

                // Query our 5 StringHolders and order by uppercase(normalString)
                q = pm.newQuery(StringHolder.class);
                q.setOrdering("normalString.toUpperCase() ascending");
                l = (List) q.execute();
                assertEquals("The number of returned elements is incorrect", 5, l.size());
                assertEquals("The first Primitive is incorrectly ordered",
                    "Aaa", ((StringHolder)l.get(0)).getNormalString());
                assertEquals("The second Primitive is incorrectly ordered",
                    "bBB", ((StringHolder)l.get(1)).getNormalString());
                assertEquals("The third Primitive is incorrectly ordered",
                    "Ccc", ((StringHolder)l.get(2)).getNormalString());
                assertEquals("The fourth Primitive is incorrectly ordered",
                    "ddd", ((StringHolder)l.get(3)).getNormalString());
                assertEquals("The fifth Primitive is incorrectly ordered",
                    "eEe", ((StringHolder)l.get(4)).getNormalString());

                // Composite ordering
                q = pm.newQuery(BasicTypeHolder.class);
                q.setOrdering("(longField - shortField) ascending , this.longField  ascending");
                l = (List) q.execute();
                Assert.assertEquals(5, l.size());

                q = pm.newQuery(BasicTypeHolder.class);
                q.setOrdering("this.booleanObjField ascending");
                l = (List) q.execute();
                Assert.assertEquals(5, l.size());

                tx.rollback(); // Don't actually create the instances

                // Try some ordering using self-referencing relations
                tx.begin();
                Person p1 = new Person(1, "Bart", "Simpson", "bart@simpsons.com");
                Person p2 = new Person(2, "Homer", "Simpson", "homer@simpsons.com");
                Person p3 = new Person(3, "Lisa", "Simpson", "lisa@simpsons.com");
                Person p4 = new Person(4, "Marge", "Simpson", "marge@simpsons.com");
                Person p5 = new Person(5, "Maggie", "Simpson", "maggie@simpsons.com");
                Person p6 = new Person(6, "Moe", "Bartender", "moe@simpsons.com");
                Person p7 = new Person(7, "Deputy", "Dawg", "deputy@cartoons.com");
                Person p8 = new Person(8, "Road", "Runner", "maggie@cartoons.com");
                Person p9 = new Person(9, "Donald", "Duck", "moe@cartoons.com");
                p4.setBestFriend(p2);
                p5.setBestFriend(p3);
                p6.setBestFriend(p1);
                pm.makePersistentAll(new Object[] { p4, p5, p6, p7, p8, p9});
                pm.flush();

                q = pm.newQuery(Person.class);
                q.setOrdering("bestFriend.firstName ascending");
                l = (List) q.execute();
                Assert.assertEquals(9, l.size()); // 9 Person objects so 9 results - 6 nulls, and then 3 results
                Iterator it = l.iterator();
                Person p = (Person)it.next();
                assertTrue(p.getBestFriend() == null);
                p = (Person)it.next();
                assertTrue(p.getBestFriend() == null);
                p = (Person)it.next();
                assertTrue(p.getBestFriend() == null);
                p = (Person)it.next();
                assertTrue(p.getBestFriend() == null);
                p = (Person)it.next();
                assertTrue(p.getBestFriend() == null);
                p = (Person)it.next();
                assertTrue(p.getBestFriend() == null);
                p = (Person)it.next();
                assertTrue(p.getBestFriend().getFirstName().equals("Bart"));
                assertTrue(p.getFirstName().equals("Moe"));
                p = (Person)it.next();
                assertTrue(p.getBestFriend().getFirstName().equals("Homer"));
                assertTrue(p.getFirstName().equals("Marge"));
                p = (Person)it.next();
                assertTrue(p.getBestFriend().getFirstName().equals("Lisa"));
                assertTrue(p.getFirstName().equals("Maggie"));

                tx.rollback(); // Don't actually create the instances
            }
            catch (Exception e)
            {
                LOG.error("Exception in query", e);
                fail("Exception thrown by query " + e.getMessage());
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
            clean(BasicTypeHolder.class);
        }
    }

    public void testOrderingUsingCollectionExpression()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager boss[] = new Manager[5];
                boss[0] = new Manager(3, "Boss", "WakesUp", "boss@wakes.up", 4, "serial 3");
                boss[1] = new Manager(4, "Boss", "WakesUp2", "boss2@wakes.up", 5, "serial 4");
                boss[2] = new Manager(5, "Boss", "WakesUp3", "boss3@wakes.up", 6, "serial 5");
                boss[3] = new Manager(6, "Boss", "WakesUp4", "boss4@wakes.up", 7, "serial 6");
                boss[4] = new Manager(7, "Boss", "WakesUp5", "boss5@wakes.up", 8, "serial 7");
                Department deptA = new Department("DeptA");
                Department deptB = new Department("DeptB");
                Department deptC = new Department("DeptC");
                boss[0].getDepartments().add(deptA);
                boss[0].getDepartments().add(deptB);
                boss[1].getDepartments().add(deptC);
                pm.makePersistentAll(boss);
                pm.flush();

                Query q = pm.newQuery(Manager.class);
                q.setOrdering("departments.size() ascending, lastName ascending");
                List l = (List) q.execute();
                Assert.assertEquals(5, l.size());
                Assert.assertEquals("WakesUp3", ((Manager)l.get(0)).getLastName());
                Assert.assertEquals("WakesUp4", ((Manager)l.get(1)).getLastName());
                Assert.assertEquals("WakesUp5", ((Manager)l.get(2)).getLastName());
                Assert.assertEquals("WakesUp2", ((Manager)l.get(3)).getLastName());
                Assert.assertEquals("WakesUp", ((Manager)l.get(4)).getLastName());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in query", e);
                fail("Exception thrown by query " + e.getMessage());
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
     * Test the use of setResult with the DISTINCT keyword.
     */
    public void testSetResultDistinct()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Populate some data
                tx.begin();
                BasicTypeHolder basics[] = new BasicTypeHolder[5];
                for( int i=0; i<basics.length; i++)
                {
                    basics[i] = new BasicTypeHolder();
                    basics[i].setLongField(i+1);
                    basics[i].setShortField((short) (i+11));
                    basics[i].setCharField('0');
                }
                pm.makePersistentAll(basics);
                tx.commit();

                // Query the data
                tx.begin();

                Query q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("longField == 2 || longField == 3");
                q.setResult("distinct this.longField");
                q.setOrdering("this.longField ascending");
                Collection c = (Collection) q.execute();
                Assert.assertEquals(2,c.size());

                try
                {
                    Iterator it = c.iterator();
                    assertEquals(2,((Long)it.next()).longValue());
                    assertEquals(3,((Long)it.next()).longValue());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
            clean(BasicTypeHolder.class);
        }
    }

    /**
     * Test the use of setResult().
     */
    public void testSetResult()
    {
        try
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
                Query q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("this.longField");
                q.setOrdering("this.longField ascending");
                Collection c = (Collection) q.execute();
                Assert.assertEquals(2,c.size());

                try
                {
                    Iterator it = c.iterator();
                    assertEquals(2,((Long)it.next()).longValue());
                    assertEquals(3,((Long)it.next()).longValue());
                }
                catch( Exception e )
                {
                    fail(e.getMessage());
                }
                q = pm.newQuery(BasicTypeHolder.class);
                q.setResult("this.longField, this.shortField");
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setOrdering("this.longField ascending");
                c = (Collection) q.execute();
                Assert.assertEquals(2,c.size());

                try
                {
                    Iterator it = c.iterator();
                    Object[] object = (Object[])it.next();
                    assertEquals(2,((Long)object[0]).longValue());
                    assertEquals(12,((Short)object[1]).shortValue());
                    object = (Object[])it.next();
                    assertEquals(3,((Long)object[0]).longValue());
                    assertEquals(13,((Short)object[1]).shortValue());
                }
                catch( Exception e )
                {
                    fail(e.getMessage());
                }

                q = pm.newQuery(BasicTypeHolder.class);
                q.setResult("this.longField, this.shortField, this");
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setOrdering("this.longField ascending");
                c = (Collection) q.execute();

                try
                {
                    Iterator it = c.iterator();
                    Object[] object = (Object[])it.next();
                    assertEquals(2,((Long)object[0]).longValue());
                    assertEquals(12,((Short)object[1]).shortValue());
                    assertEquals(ids[1],JDOHelper.getObjectId(object[2]));
                    object = (Object[])it.next();
                    assertEquals(3,((Long)object[0]).longValue());
                    assertEquals(13,((Short)object[1]).shortValue());
                    assertEquals(ids[2],JDOHelper.getObjectId(object[2]));
                }
                catch( Exception e )
                {
                    fail(e.getMessage());
                }

                q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("this.longField == 1 || this.longField == 2");
                q.setResult("JDOHelper.getObjectId(this)");
                c = (Collection) q.execute();
                Assert.assertEquals(2,c.size());

                Iterator it = c.iterator();
                assertEquals(ids[0],it.next());
                assertEquals(ids[1],it.next());

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
            clean(BasicTypeHolder.class);
        }
    }

    /**
     * Test the use of setResult().
     */
    public void testCandidateCollection()
    {
        try
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
                    basics[i].setShortField((short) (i+11));
                    basics[i].setCharField('0');
                }
                pm.makePersistentAll(basics);

                List candidates = new ArrayList();
                candidates.add(basics[0]);
                candidates.add(basics[1]);
                candidates.add(basics[2]);
                candidates.add(basics[3]);
                candidates.add(basics[4]);

                // Basic candidate test
                Query q = pm.newQuery(BasicTypeHolder.class);
                q.setCandidates(candidates);
                Collection c = (Collection) q.execute();
                assertEquals(5, c.size());

                // Add duplicate of one candidate (so should get dup in result)
                candidates.add(basics[4]);
                q.setCandidates(candidates);
                c = (Collection) q.execute();
                assertEquals(6, c.size());

                // Same thing but with result
                q.setResult("this");
                c = (Collection) q.execute();
                assertEquals(6, c.size());

                // Same thing but with distinct result so we eliminate the dup candidate
                q.setResult("distinct this");
                c = (Collection) q.execute();
                assertEquals(5, c.size());

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
            clean(BasicTypeHolder.class);
        }
    }
    
    /**
     * Test where the result class or result object instance is not of the same type as the candidates.
     */
    public void testCandidateCollectionWithResultNotSameTypeAsCandidates()
    {
        try
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

                List candidates = new ArrayList();
                candidates.add(basics[0]);
                candidates.add(basics[1]);
                candidates.add(basics[2]);
                candidates.add(basics[3]);
                candidates.add(basics[4]);
                candidates.add(basics[4]);

                // Duplicate candidates, but with different result type
                Query q = pm.newQuery(BasicTypeHolder.class);
                q.setCandidates(candidates);
                q.setResult("this, this");
                Collection c = (Collection) q.execute();
                // Not returning object of candidate type so ignore dups in candidates
                assertEquals(5, c.size());

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
            clean(BasicTypeHolder.class);
        }
    }

    public void testSetIllegalAttributeInResult()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                try
                {
                    Query q = pm.newQuery(BasicTypeHolder.class);
                    q.setResult("this.longFieldQQ");
                    q.execute();
                    fail("Expected JDOUserException");
                }
                catch(JDOUserException ex)
                {
                    //expected
                }
                try
                {
                    Query q = pm.newQuery(BasicTypeHolder.class);
                    q.setResult("qngFieldQQ");
                    q.execute();
                    fail("Expected JDOUserException");
                }
                catch(JDOUserException ex)
                {
                    //expected
                }

                try
                {
                    Query q = pm.newQuery(BasicTypeHolder.class);
                    q.setResult("this.longFieldQQ");
                    q.compile();
                    fail("Expected JDOUserException");
                }
                catch(JDOUserException ex)
                {
                    //expected
                }

                try
                {
                    Query q = pm.newQuery(BasicTypeHolder.class);
                    q.setResult("qngFieldQQ");
                    q.compile();
                    fail("Expected JDOUserException");
                }
                catch(JDOUserException ex)
                {
                    //expected
                }
                tx.rollback();
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
            clean(BasicTypeHolder.class);
        }
    }

    /**
     * Test the use of setResult() returning the parameter
     */
    public void testSetResultParameter()
    {
        try
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
                //obtain a primite persistent instance
                Query q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("this.longField == 1 || this.longField == 2");
                q.setResult("this");
                Collection c = (Collection) q.execute();

                Iterator it = c.iterator();
                BasicTypeHolder p = (BasicTypeHolder) it.next();

                //test return String
                q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("this.longField == 1 || this.longField == 2");
                q.setResult("s");
                q.declareParameters("String s");
                c = (Collection) q.execute("result");
                Assert.assertEquals(2,c.size());

                it = c.iterator();
                assertEquals("result",it.next());
                assertEquals("result",it.next());

                //test return Long
                q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("this.longField == 1 || this.longField == 2");
                q.setResult("l");
                q.declareParameters("Long l");
                c = (Collection) q.execute(new Long(10));
                Assert.assertEquals(2,c.size());

                it = c.iterator();
                assertEquals(new Long(10),it.next());
                assertEquals(new Long(10),it.next());

                //test return Primitive
                q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("this.longField == 1 || this.longField == 2");
                q.setResult("p");
                q.declareParameters("BasicTypeHolder p");
                c = (Collection) q.execute(p);
                Assert.assertEquals(2,c.size());

                it = c.iterator();
                assertEquals(p,it.next());
                assertEquals(p,it.next());

                //test return Primitive
                q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("this.longField == 1");
                q.setResult("p");
                q.declareParameters("BasicTypeHolder p");
                c = (Collection) q.execute(p);
                Assert.assertEquals(1,c.size());

                it = c.iterator();
                assertEquals(p,it.next());

                //test return null
                q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("this.longField == 1");
                q.setResult("p");
                q.declareParameters("BasicTypeHolder p");
                c = (Collection) q.execute(null);
                Assert.assertEquals(1,c.size());

                it = c.iterator();
                assertNull(it.next());

                //test return Primitive
                q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("this.longField == 1");
                q.setResult("this, p");
                q.declareParameters("BasicTypeHolder p");
                c = (Collection) q.execute(p);
                Assert.assertEquals(1,c.size());

                it = c.iterator();
                assertEquals(p,((Object[])it.next())[1]);

                //test return Long
                q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("this.longField == 1");
                q.setResult("p.longField");
                q.declareParameters("BasicTypeHolder p");
                c = (Collection) q.execute(p);
                Assert.assertEquals(1,c.size());

                it = c.iterator();
                assertEquals(1, p.getLongField());
                assertEquals(new Long(1), it.next());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in query", e);
                fail("Exception thrown by query " + e.getMessage());
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
            clean(BasicTypeHolder.class);
        }
    }

    public void testSetResultWithAggregationWithoutSubclasses()
    {
        try
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
                Query q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("longField == 2 || longField == 3");
                q.setResult("max(longField)");
                Object result = q.execute();
                assertEquals("Type of result object is incorrect", result.getClass().getName(), "java.lang.Long");
                assertEquals("Result is incorrect", 3, ((Long)result).longValue());
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
            clean(BasicTypeHolder.class);
        }
    }
    
    public void testSetResultWithAggregationWithSubclasses()
    {
        try
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
                Query q = pm.newQuery(BasicTypeHolder.class);
                q.setFilter("longField == 2 || longField == 3");
                q.setResult("max(longField)");
                Object result = q.execute();
                assertEquals("Type of result object is incorrect", result.getClass().getName(), "java.lang.Long");
                assertEquals("Result is incorrect", 3, ((Long)result).longValue());
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
            clean(BasicTypeHolder.class);
        }
    }

    public void testSetResultWithAggregation1()
    {
        try
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
                //-----------
                //MAX
                //-----------
                Query q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("max(this.longField)");
                Object result = q.execute();
                assertEquals("Type of result object is incorrect", result.getClass().getName(), "java.lang.Long");
                assertEquals("Result is incorrect", 3, ((Long)result).longValue());

                //-----------
                //MIN
                //-----------
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("min(this.longField)");
                result = q.execute();
                assertEquals("Type of result object is incorrect", result.getClass().getName(), "java.lang.Long");
                assertEquals("Result is incorrect", 2, ((Long)result).longValue());

                //-----------
                //SUM
                //-----------
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("sum(this.longField)");
                result = q.execute();
                assertEquals("Type of result object is incorrect", result.getClass().getName(), "java.lang.Long");
                assertEquals("Result is incorrect", 5, ((Long)result).longValue());

                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("count(this.longField)");
                result = q.execute();
                assertEquals("Type of result object is incorrect", result.getClass().getName(), "java.lang.Long");
                assertEquals("Result is incorrect", 2, ((Long)result).longValue());

                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("count(this.intField)");
                result = q.execute();
                assertEquals("Type of result object is incorrect", result.getClass().getName(), "java.lang.Long");
                assertEquals("Result is incorrect", 2, ((Long)result).longValue());

                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("count(this)");
                result = q.execute();
                assertEquals("Type of result object is incorrect", result.getClass().getName(), "java.lang.Long");
                assertEquals("Result is incorrect", 2, ((Long)result).longValue());

                //-----------
                //COUNT + non aggregate expression
                //-----------            
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("count(this.longField),this.longField");
                q.setGrouping("this.longField");
                q.setOrdering("this.longField ascending");
                Collection c = (Collection) q.execute();
                Assert.assertEquals(2,c.size());
                try
                {
                    Iterator it = c.iterator();
                    Object[] object = (Object[])it.next();
                    assertEquals(1,((Long)object[0]).longValue());
                    assertEquals(2,((Long)object[1]).longValue());
                    object = (Object[])it.next();
                    assertEquals(1,((Long)object[0]).longValue());
                    assertEquals(3,((Long)object[1]).longValue());
                }
                catch (Exception e)
                {
                    fail(e.getMessage());
                }

                //-----------
                //AVG
                //-----------            
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 4");
                q.setResult("avg(this.longField)");
                result = q.execute();
                assertEquals("Type of result object is incorrect", "java.lang.Double", result.getClass().getName());
                assertEquals("Result is incorrect", 3, ((Double)result).longValue());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in query", e);
                fail("Exception thrown by query " + e.getMessage());
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
            clean(BasicTypeHolder.class);
        }
    }

    public void testSetResultWithAggregation2()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Office o1 = new Office(1, "Turquoise", "Small office at the end of the long corridor");
                Office o2 = new Office(2, "Green", "Large fully-featured modern office");
                Office o3 = new Office(1, "Yellow", "Medium sized office");
                pm.makePersistent(o1);
                pm.makePersistent(o2);
                pm.makePersistent(o3);
                tx.commit();

                tx.begin();
                //-----------
                //COUNT
                //-----------
                Query q = pm.newQuery(pm.getExtent(Office.class, false));
                q.setResult("count(this)");
                Object result = q.execute();
                assertEquals("Type of result object is incorrect", result.getClass().getName(), "java.lang.Long");
                assertEquals("Result is incorrect", 3, ((Long)result).longValue());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in query", e);
                fail("Exception thrown by query " + e.getMessage());
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
            clean(Office.class);
        }
    }

    public void testSetResultWithAggregationAndNonAggregateExpression()
    {
        try
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
                Object ids[] = new Object[5];
                for (int i=0; i<basics.length; i++)
                {
                    ids[i] = pm.getObjectId(basics[i]);
                }
                pm.flush();

                //-----------
                //COUNT + non aggregate expression
                //-----------            
                Query q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("count(this.longField),this.longField");
                q.setGrouping("this.longField");
                q.setOrdering("this.longField ascending");
                Collection c = (Collection) q.execute();

                Assert.assertEquals(2,c.size());

                try
                {
                    Iterator it = c.iterator();
                    Object[] object = (Object[])it.next();
                    assertEquals(1,((Long)object[0]).longValue());
                    assertEquals(2,((Long)object[1]).longValue());
                    object = (Object[])it.next();
                    assertEquals(1,((Long)object[0]).longValue());
                    assertEquals(3,((Long)object[1]).longValue());
                }
                catch( Exception e )
                {
                    fail(e.getMessage());
                }              

                //-----------
                //COUNT + non aggregate expression
                //-----------            
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("this.longField,count(this.longField)");
                q.setGrouping("this.longField");
                q.setOrdering("this.longField ascending");
                c = (Collection) q.execute();

                Assert.assertEquals(2,c.size());

                try
                {
                    Iterator it = c.iterator();
                    Object[] object = (Object[])it.next();
                    assertEquals(1,((Long)object[1]).longValue());
                    assertEquals(2,((Long)object[0]).longValue());
                    object = (Object[])it.next();
                    assertEquals(1,((Long)object[1]).longValue());
                    assertEquals(3,((Long)object[0]).longValue());
                }
                catch( Exception e )
                {
                    fail(e.getMessage());
                }              

                //-----------
                //SUM + non aggregate expression
                //-----------            
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("this.longField,sum(this.longField)");
                q.setGrouping("this.longField");
                q.setOrdering("this.longField ascending");
                c = (Collection) q.execute();

                Assert.assertEquals(2,c.size());

                try
                {
                    Iterator it = c.iterator();
                    Object[] object = (Object[])it.next();
                    assertEquals(2,((Long)object[1]).longValue());
                    assertEquals(2,((Long)object[0]).longValue());
                    object = (Object[])it.next();
                    assertEquals(3,((Long)object[1]).longValue());
                    assertEquals(3,((Long)object[0]).longValue());
                }
                catch( Exception e )
                {
                    fail(e.getMessage());
                } 
                //-----------
                //COUNT + non aggregate expression
                //-----------            
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("count(this.shortField),this.longField");
                q.setGrouping("this.longField");
                q.setOrdering("this.longField ascending");
                c = (Collection) q.execute();

                Assert.assertEquals(2,c.size());

                try
                {
                    Iterator it = c.iterator();
                    Object[] object = (Object[])it.next();
                    assertEquals(1,((Long)object[0]).longValue());
                    assertEquals(2,((Long)object[1]).longValue());
                    object = (Object[])it.next();
                    assertEquals(1,((Long)object[0]).longValue());
                    assertEquals(3,((Long)object[1]).longValue());
                }
                catch( Exception e )
                {
                    fail(e.getMessage());
                }              

                //-----------
                //COUNT + non aggregate expression
                //-----------            
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("this.longField,count(this.shortField)");
                q.setGrouping("this.longField");
                q.setOrdering("this.longField ascending");
                c = (Collection) q.execute();

                Assert.assertEquals(2,c.size());

                try
                {
                    Iterator it = c.iterator();
                    Object[] object = (Object[])it.next();
                    assertEquals(1,((Long)object[1]).longValue());
                    assertEquals(2,((Long)object[0]).longValue());
                    object = (Object[])it.next();
                    assertEquals(1,((Long)object[1]).longValue());
                    assertEquals(3,((Long)object[0]).longValue());
                }
                catch( Exception e )
                {
                    fail(e.getMessage());
                }              

                //-----------
                //SUM + non aggregate expression
                //-----------            
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("this.longField, sum(this.shortField)");
                q.setGrouping("this.longField");
                q.setOrdering("this.longField ascending");
                c = (Collection) q.execute();

                Assert.assertEquals(2,c.size());

                Iterator it = c.iterator();
                Object[] object = (Object[])it.next();
                assertEquals(12,((Long)object[1]).longValue());
                assertEquals(2,((Long)object[0]).longValue());
                object = (Object[])it.next();
                assertEquals(13,((Long)object[1]).longValue());
                assertEquals(3,((Long)object[0]).longValue());

                //-----------
                //SUM(this.XXX) + non aggregate expression
                //-----------            
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("this.longField, sum(this.shortField)");
                q.setGrouping("this.longField");
                c = (Collection) q.execute();

                Assert.assertEquals(2,c.size());

                it = c.iterator();
                object = (Object[])it.next();
                assertEquals(12,((Long)object[1]).longValue());
                assertEquals(2,((Long)object[0]).longValue());
                object = (Object[])it.next();
                assertEquals(13,((Long)object[1]).longValue());
                assertEquals(3,((Long)object[0]).longValue());


                //-----------
                //SUM(XXX) + non aggregate expression
                //-----------            
                q = pm.newQuery(pm.getExtent(BasicTypeHolder.class,false));
                q.setFilter("this.longField == 2 || this.longField == 3");
                q.setResult("this.longField, sum(shortField)");
                q.setGrouping("this.longField");
                c = (Collection) q.execute();

                Assert.assertEquals(2,c.size());

                it = c.iterator();
                object = (Object[])it.next();
                assertEquals(12,((Long)object[1]).longValue());
                assertEquals(2,((Long)object[0]).longValue());
                object = (Object[])it.next();
                assertEquals(13,((Long)object[1]).longValue());
                assertEquals(3,((Long)object[0]).longValue());

                tx.rollback(); // Dont persist the objects
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
            clean(BasicTypeHolder.class);
        }
    }

    public void testSetResultWithAggregationAndNonAggregateExpression2()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Office o1 = new Office(1, "Turquoise", "Small office at the end of the long corridor");
                Office o2 = new Office(2, "Green", "Large fully-featured modern office");
                Office o3 = new Office(1, "Yellow", "Medium sized office");
                pm.makePersistent(o1);
                pm.makePersistent(o2);
                pm.makePersistent(o3);
                pm.flush();

                //-----------
                //COUNT
                //-----------
                Query q = pm.newQuery(pm.getExtent(Office.class, false));
                q.setResult("count(this), floor");
                q.setGrouping("floor");
                q.setOrdering("floor ascending");
                Collection c = (Collection) q.execute();
                Assert.assertEquals(2,c.size());
                Iterator it = c.iterator();
                Object[] object = (Object[])it.next();
                assertEquals(2,((Long)object[0]).longValue());
                object = (Object[])it.next();
                assertEquals(1,((Long)object[0]).longValue());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception in query", e);
                fail("Exception thrown by query " + e.getMessage());
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
            clean(Office.class);
        }
    }

    /**
     * Test for insensitive ResultSets. This tests a JPOX extension.
     */
    public void testResultSetInsensitive()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Employee bunny = new Employee(3,"Bugs","Bunny","bugs.bunny@warnerbros.com",12,"serial 3"); //Eh, what's up, doc?
            Employee roadrunner = new Employee(4,"Road","Runner","road.runner@warnerbros.com",11,"serial 4"); //Meep! Meep!

            getConfigurationForPMF(pmf).setProperty("datanucleus.rdbms.query.resultSetType", "scroll-insensitive");
            getConfigurationForPMF(pmf).setProperty("datanucleus.rdbms.query.fetchDirection", "forward");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Create some data
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                pm.makePersistent(bunny);
                pm.makePersistent(roadrunner);
                tx.commit();

                // Query the data
                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(org.jpox.samples.models.company.Employee.class, false));
                    List results = (List)q.execute();
                    assertEquals("Number of objects retrieved using insensitive ResultSet is incorrect", 4, results.size());
                    Object[] objects = new Object[results.size()];

                    ListIterator resultsIter = results.listIterator();

                    // Navigate forwards through the list
                    assertEquals("hasNext() returns incorrect value", true, resultsIter.hasNext());
                    assertEquals("hasPrevious() returns incorrect value", false, resultsIter.hasPrevious());
                    assertEquals("nextIndex() in listIterator is incorrect", 0, resultsIter.nextIndex());
                    assertEquals("previousIndex() in listIterator is incorrect", -1, resultsIter.previousIndex());
                    objects[0] = resultsIter.next();
                    assertEquals("previous() returns an incorrect object", objects[0], resultsIter.previous());
                    resultsIter.next();

                    assertEquals("hasNext() returns incorrect value", true, resultsIter.hasNext());
                    assertEquals("hasPrevious() returns incorrect value", true, resultsIter.hasPrevious());
                    assertEquals("nextIndex() in listIterator is incorrect", 1, resultsIter.nextIndex());
                    assertEquals("previousIndex() in listIterator is incorrect", 0, resultsIter.previousIndex());
                    objects[1] = resultsIter.next();
                    assertEquals("previous() returns an incorrect object", objects[1], resultsIter.previous());
                    resultsIter.next();

                    assertEquals("hasNext() returns incorrect value", true, resultsIter.hasNext());
                    assertEquals("hasPrevious() returns incorrect value", true, resultsIter.hasPrevious());
                    assertEquals("nextIndex() in listIterator is incorrect", 2, resultsIter.nextIndex());
                    assertEquals("previousIndex() in listIterator is incorrect", 1, resultsIter.previousIndex());
                    objects[2] = resultsIter.next();
                    assertEquals("previous() returns an incorrect object", objects[2], resultsIter.previous());
                    resultsIter.next();

                    assertEquals("hasNext() returns incorrect value", true, resultsIter.hasNext());
                    assertEquals("hasPrevious() returns incorrect value", true, resultsIter.hasPrevious());
                    assertEquals("nextIndex() in listIterator is incorrect", 3, resultsIter.nextIndex());
                    assertEquals("previousIndex() in listIterator is incorrect", 2, resultsIter.previousIndex());
                    objects[3] = resultsIter.next();
                    assertEquals("previous() returns an incorrect object", objects[3], resultsIter.previous());
                    resultsIter.next();

                    assertEquals("hasNext() returns incorrect value", false, resultsIter.hasNext());
                    assertEquals("hasPrevious() returns incorrect value", true, resultsIter.hasPrevious());
                    assertEquals("previousIndex() in listIterator is incorrect", 3, resultsIter.previousIndex());
                    assertEquals("nextIndex() in listIterator is incorrect", 4, resultsIter.nextIndex());

                    // Navigate backwards through the list
                    assertEquals("previous() returns an incorrect object", objects[3], resultsIter.previous());
                    assertEquals("hasNext() returns incorrect value", true, resultsIter.hasNext());
                    assertEquals("hasPrevious() returns incorrect value", true, resultsIter.hasPrevious());
                    assertEquals("previousIndex() returns incorrect value", 2, resultsIter.previousIndex());
                    assertEquals("nextIndex() returns incorrect value", 3, resultsIter.nextIndex());

                    assertEquals("previous() returns an incorrect object", objects[2], resultsIter.previous());
                    assertEquals("hasNext() returns incorrect value", true, resultsIter.hasNext());
                    assertEquals("hasPrevious() returns incorrect value", true, resultsIter.hasPrevious());
                    assertEquals("previousIndex() returns incorrect value", 1, resultsIter.previousIndex());
                    assertEquals("nextIndex() returns incorrect value", 2, resultsIter.nextIndex());

                    assertEquals("previous() returns an incorrect object", objects[1], resultsIter.previous());
                    assertEquals("hasNext() returns incorrect value", true, resultsIter.hasNext());
                    assertEquals("hasPrevious() returns incorrect value", true, resultsIter.hasPrevious());
                    assertEquals("previousIndex() returns incorrect value", 0, resultsIter.previousIndex());
                    assertEquals("nextIndex() returns incorrect value", 1, resultsIter.nextIndex());

                    assertEquals("previous() returns an incorrect object", objects[0], resultsIter.previous());
                    assertEquals("hasNext() returns incorrect value", true, resultsIter.hasNext());
                    assertEquals("hasPrevious() returns incorrect value", false, resultsIter.hasPrevious());
                    assertEquals("previousIndex() returns incorrect value", -1, resultsIter.previousIndex());
                    assertEquals("nextIndex() returns incorrect value", 0, resultsIter.nextIndex());

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
            clean(Employee.class);
        }
    }

    /**
     * Test of count(this) with inheritance using an Extent (inc subclasses).
     */
    public void testCountOnClassHierarchy()
    {
        try
        {
            // Create objects
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();

                // Create a base object
                Farm baseobject = new Farm("base farm");
                pm.makePersistent(baseobject);

                // Create a sub object
                DairyFarm subobject = new DairyFarm("dairy farm");
                pm.makePersistent(subobject);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown during create of base and subclass objects for \"new-table\" strategy inheritance tree : " + e.getMessage());
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
                Query q = pm.newQuery(pm.getExtent(Farm.class, true));
                q.setResult("count(this)");
                assertEquals(2, ((Long) q.execute()).longValue());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during query", e);
                fail("Exception thrown during count(this) query" + e.getMessage());
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
            clean(Farm.class);
            clean(DairyFarm.class);
        }
    }

    /**
     * Test case to use the JDO 2.0 setResultClass().
     */
    public void testNewObjectInResult()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(34);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@jpox.com");
                p2.setAge(37);
                pm.makePersistent(p1);
                pm.makePersistent(p2);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            
            // Query using PersonHolder result output
            try
            {
                tx.begin();
                //check with default constructor
                Query q = pm.newQuery(pm.getExtent(Person.class, false));
                q.setFilter("age > 35");
                q.setResult("new org.jpox.samples.models.company.PersonHolder()");
                List results = (List)q.execute();
                assertEquals(1, results.size());
                Iterator resultsIter = results.iterator();
                if (resultsIter.hasNext())
                {
                    Object obj = resultsIter.next();
                    assertTrue("ResultClass of query is incorrect. Should have been " + PersonHolder.class.getName() + " but is " + obj.getClass().getName(),
                        PersonHolder.class == obj.getClass());
                    PersonHolder holder = (PersonHolder)obj;
                    assertNull(holder.getPerson1());
                }
                q.closeAll();

                //check with simple argument
                q = pm.newQuery(pm.getExtent(Person.class, false));
                q.setFilter("age > 35");
                q.setResult("new org.jpox.samples.models.company.PersonHolder(this)");
                results = (List)q.execute();
                assertEquals(1, results.size());
                resultsIter = results.iterator();
                if (resultsIter.hasNext())
                {
                    Object obj = resultsIter.next();
                    assertTrue("ResultClass of query is incorrect. Should have been " + PersonHolder.class.getName() + " but is " + obj.getClass().getName(),
                        PersonHolder.class == obj.getClass());
                    PersonHolder holder = (PersonHolder)obj;
                    assertNotNull(holder.getPerson1());
                    assertNull(holder.getPerson2());
                }
                q.closeAll();

                //check multiple args and with a literal null
                q = pm.newQuery(pm.getExtent(Person.class, false));
                q.setFilter("age > 35");
                q.setResult("new org.jpox.samples.models.company.PersonHolder(this,null,this.firstName)");
                results = (List)q.execute();
                assertEquals(1, results.size());
                resultsIter = results.iterator();
                if (resultsIter.hasNext())
                {
                    Object obj = resultsIter.next();
                    assertTrue("ResultClass of query is incorrect. Should have been " + PersonHolder.class.getName() + " but is " + obj.getClass().getName(),
                        PersonHolder.class == obj.getClass());
                    PersonHolder holder = (PersonHolder)obj;
                    assertNotNull(holder.getPerson1());
                    assertNull(holder.getPerson2());
                    assertEquals("Barney", holder.getFirstName().trim());
                }
                q.closeAll();

                //add more literals
                q = pm.newQuery(pm.getExtent(Person.class, false));
                q.setFilter("age > 35");
                q.setResult("new org.jpox.samples.models.company.PersonHolder(this,null,this.firstName,'string',1)");
                results = (List)q.execute();
                assertEquals(1, results.size());
                resultsIter = results.iterator();
                if (resultsIter.hasNext())
                {
                    Object obj = resultsIter.next();
                    assertTrue("ResultClass of query is incorrect. Should have been " + PersonHolder.class.getName() + " but is " + obj.getClass().getName(),
                        PersonHolder.class == obj.getClass());
                    PersonHolder holder = (PersonHolder)obj;
                    assertNotNull(holder.getPerson1());
                    assertNull(holder.getPerson2());
                    assertEquals("Barney", holder.getFirstName().trim());
                    assertEquals("string", holder.getLiteralString());
                    assertEquals(1,holder.getLiteralInt());
                }
                q.closeAll();

                //make sure if objects after literal, they work
                q = pm.newQuery(pm.getExtent(Person.class, false));
                q.setFilter("age > 35");
                q.setResult("new org.jpox.samples.models.company.PersonHolder(this,null,this.firstName,'string',1,this)");
                results = (List)q.execute();
                assertEquals(1, results.size());
                resultsIter = results.iterator();
                if (resultsIter.hasNext())
                {
                    Object obj = resultsIter.next();
                    assertTrue("ResultClass of query is incorrect. Should have been " + PersonHolder.class.getName() + " but is " + obj.getClass().getName(),
                        PersonHolder.class == obj.getClass());
                    PersonHolder holder = (PersonHolder)obj;
                    assertNotNull(holder.getPerson1());
                    assertNull(holder.getPerson2());
                    assertEquals("Barney",holder.getFirstName().trim());
                    assertEquals("string",holder.getLiteralString());
                    assertEquals(1,holder.getLiteralInt());
                }
                q.closeAll();

                //test new Object(new Object())
                q = pm.newQuery(pm.getExtent(Person.class, false));
                q.setFilter("age > 35");
                q.setResult("new org.jpox.samples.models.company.PersonHolder(new org.jpox.samples.models.company.PersonHolder(new org.jpox.samples.models.company.PersonHolder(this),this),this)");
                results = (List)q.execute();
                assertEquals(1, results.size());
                resultsIter = results.iterator();
                if (resultsIter.hasNext())
                {
                    Object obj = resultsIter.next();
                    assertTrue("ResultClass of query is incorrect. Should have been " + PersonHolder.class.getName() + " but is " + obj.getClass().getName(),
                        PersonHolder.class == obj.getClass());
                    PersonHolder holder = (PersonHolder)obj;
                    assertNotNull(holder.getPerson1());
                    assertNotNull(holder.getPerson2());
                }
                q.closeAll();

                //test new Object(new Object())
                q = pm.newQuery(pm.getExtent(Person.class, false));
                q.setFilter("age > 35");
                q.setResult("this, 0, new org.jpox.samples.models.company.PersonHolder(new org.jpox.samples.models.company.PersonHolder(new org.jpox.samples.models.company.PersonHolder(this),this),this), 1, null");
                results = (List)q.execute();
                assertEquals(1, results.size());
                resultsIter = results.iterator();
                if (resultsIter.hasNext())
                {
                    Object[] obj = (Object[]) resultsIter.next();
                    assertTrue("ResultClass of query is incorrect. Should have been " + PersonHolder.class.getName() +
                        " but is " + obj[2].getClass().getName(), PersonHolder.class == obj[2].getClass());
                    PersonHolder holder = (PersonHolder)obj[2];
                    assertNotNull(holder.getPerson1());
                    assertNotNull(holder.getPerson2());
                    assertNotNull(obj[0]);
                    assertEquals(obj[0],holder.getPerson1());
                    assertEquals(0,((Long)obj[1]).intValue());
                    assertNotNull(obj[3]);
                    assertEquals(1,((Long)obj[3]).intValue());
                    assertNull(obj[4]);
                }
                q.closeAll();

                tx.commit();
            }
            catch (JDOException ex)
            {
                LOG.error("Exception in query", ex);
                fail(ex.getMessage());
            }
            catch (Exception e)
            {
                LOG.error("Exception caught", e);
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
            // Clean out our data
            clean(Person.class);
        }
    }

    /**
     * Test case to use the JDO 2.0 setResultClass().
     */
    public void testSetResultClass()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(34);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@jpox.com");
                p1.setAge(37);
                pm.makePersistent(p1);
                pm.makePersistent(p2);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Query using Object[] result output
            try
            {
                tx.begin();
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Person.class, false));
                    q.setFilter("age > 25");
                    q.setResultClass(Object[].class);
                    List results = (List)q.execute();
                    Iterator resultsIter = results.iterator();
                    while (resultsIter.hasNext())
                    {
                        Object obj = resultsIter.next();
                        assertEquals("ResultClass of query is incorrect.",
                            Object[].class.getName(), obj.getClass().getName());
                    }
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }
                
                // Query using user-defined result output : user class has constructor of right type
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Person.class, false));
                    q.setResult("firstName, lastName, age");
                    q.setResultClass(Identity1.class);
                    List results = (List)q.execute();
                    Iterator resultsIter = results.iterator();
                    while (resultsIter.hasNext())
                    {
                        Object obj = resultsIter.next();
                        assertEquals("ResultClass of query is incorrect.",
                            Identity1.class.getName(), obj.getClass().getName());
                    }
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }
                
                // Query using user-defined result output : user class has public fields
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Person.class, false));
                    q.setResult("firstName, lastName, age");
                    q.setResultClass(Identity2.class);
                    List results = (List)q.execute();
                    Iterator resultsIter = results.iterator();
                    while (resultsIter.hasNext())
                    {
                        Object obj = resultsIter.next();
                        assertEquals("ResultClass of query is incorrect.",
                            Identity2.class.getName(), obj.getClass().getName());
                    }
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }
                
                // Query using user-defined result output : user class has setters
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Person.class, false));
                    q.setResult("firstName, lastName, age");
                    q.setResultClass(Identity3.class);
                    List results = (List)q.execute();
                    Iterator resultsIter = results.iterator();
                    while (resultsIter.hasNext())
                    {
                        Object obj = resultsIter.next();
                        assertEquals("ResultClass of query is incorrect.",
                            Identity3.class.getName(), obj.getClass().getName());
                    }
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    fail(e.getMessage());
                }
                
                // Query using user-defined result output : user class has put method
                try
                {
                    Query q = pm.newQuery(pm.getExtent(Person.class, false));
                    q.setResult("firstName, lastName, age");
                    q.setResultClass(Identity4.class);
                    List results = (List)q.execute();
                    Iterator resultsIter = results.iterator();
                    while (resultsIter.hasNext())
                    {
                        Object obj = resultsIter.next();
                        assertEquals("ResultClass of query is incorrect.",
                            Identity4.class.getName(), obj.getClass().getName());
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
            }

            // Query using user-defined result output : invalid result class
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            Query q2 = null;
            try
            {
                tx.begin();
                q2 = pm.newQuery(pm.getExtent(Person.class, false));
                q2.setResult("firstName, lastName, age");
                q2.setResultClass(Identity5.class);
                List results = (List)q2.execute();
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    resultsIter.next();
                }

                fail("Query was executed but should have thrown an exception in the result extraction due to invalid ResultClass");
                tx.commit();
            }
            catch (JDOUserException e)
            {
                LOG.info("Exception thrown by query", e);
                // Success!
                q2.closeAll();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Query using simple type result output
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query q = pm.newQuery(pm.getExtent(Person.class, false));
                q.setResult("firstName"); // Field is of type String
                q.setResultClass(String.class);
                List results = (List)q.execute();
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    Object obj = resultsIter.next();
                    assertEquals("ResultClass of query is incorrect.",
                        String.class.getName(), obj.getClass().getName());
                }
                q.closeAll();
                tx.commit();
            }
            catch (JDOUserException e)
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
            
            // Query using simple type result output but with too many fields
            try
            {
                tx.begin();
                Query q = pm.newQuery(pm.getExtent(Person.class, false));
                q.setResult("firstName, age"); // Fields of type String, int
                q.setResultClass(String.class);
                q.execute();
                fail("ResultClass was specified as String yet 2 fields were returned. An exception should have been thrown");
                q.closeAll();
                tx.commit();
            }
            catch (JDOUserException e)
            {
                // Success. JPOX detected the erroneous resultClass spec.
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
        }
    }

    public void testSetResultWithAggregationGroupingOfAbs()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Office o1 = new Office(1, "Turquoise", "Small office at the end of the long corridor");
                Office o2 = new Office(2, "Green", "Large fully-featured modern office");
                Office o3 = new Office(1, "Yellow", "Medium sized office");
                pm.makePersistent(o1);
                pm.makePersistent(o2);
                pm.makePersistent(o3);
                pm.flush();

                Query q = pm.newQuery("SELECT abs(floor), count(this) FROM " + Office.class.getName() + " GROUP BY abs(floor)");
                Collection c = (Collection) q.execute();
                Assert.assertEquals(2, c.size());
                Iterator it = c.iterator();

                Object[] object = (Object[])it.next();
                assertEquals(1.0, ((Double)object[0]).doubleValue());
                assertEquals(2, ((Long)object[1]).longValue());

                object = (Object[])it.next();
                assertEquals(2.0, ((Double)object[0]).doubleValue());
                assertEquals(1, ((Long)object[1]).longValue());

                tx.rollback();

            }
            catch (Exception e)
            {
                LOG.error("Exception in query", e);
                fail("Exception thrown by query " + e.getMessage());
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
            clean(Office.class);
        }
    }

    /**
     * Tests the use of JDOHelper.getObjectId() as the result.
     */
    public void testSetResultJDOHelperGetObjectID()
    {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Persist some simple objects
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
            Query q = pm.newQuery("SELECT JDOHelper.getObjectId(this) FROM " + Office.class.getName());
            List results = (List)q.execute();
            assertEquals("Number of results is incorrect", 3, results.size());
            Iterator iter = results.iterator();
            boolean[] present = new boolean[]{false, false, false};
            while (iter.hasNext())
            {
                Object id = iter.next();
                if (id.equals(officeIds[0]))
                {
                    present[0] = true;
                }
                if (id.equals(officeIds[1]))
                {
                    present[1] = true;
                }
                if (id.equals(officeIds[2]))
                {
                    present[2] = true;
                }
            }
            for (int i=0;i<3;i++)
            {
                assertTrue("id " + i + " is not present", present[i]);
            }
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

            // Clean out our data
            clean(Office.class);
        }
    }
}