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

Contributors :
    ...
***********************************************************************/
package org.datanucleus.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.inheritance.ABase;
import org.jpox.samples.inheritance.ASub1;
import org.jpox.samples.models.company.Qualification;
import org.jpox.samples.one_one.unidir.Login;
import org.jpox.samples.one_one.unidir.LoginAccount;
import org.neodatis.odb.OdbConfiguration;

/**
 * Tests for basic persistence to NeoDatis using datastore identity.
 **/
public class DatastoreIdPersistenceTest extends JDOPersistenceTestCase
{
    public DatastoreIdPersistenceTest(String name)
    {
        super(name);
        OdbConfiguration.setLogServerStartupAndShutdown(false);
    }

    /**
     * Test of persistence of basic object.
     */
    public void testPersistBasic()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.YEAR, 2008);
            cal.set(Calendar.MONTH, 10);
            cal.set(Calendar.DAY_OF_MONTH, 15);
            cal.set(Calendar.HOUR_OF_DAY, 14);
            cal.set(Calendar.MINUTE, 30);
            cal.set(Calendar.SECOND, 0);
            Date date = cal.getTime();
            try
            {
                tx.begin();
                Qualification q1 = new Qualification("Cycling Proficiency");
                q1.setDate(date);
                pm.makePersistent(q1);
                tx.commit();
                id = pm.getObjectId(q1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Check the persistence using the same PM
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Qualification q = (Qualification)pm.getObjectById(id);
                assertNotNull("Qualification is null! when provided id=" + id, q);
                assertEquals("Name of Qualification retrieved is incorrect", "Cycling Proficiency", q.getName());
                assertEquals("Date of Qualification retrieved is incorrect", date, q.getDate());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            // Clean out data
            clean(Qualification.class);
        }
    }

    /**
     * Test of persistence of basic object.
     */
    public void testPersistBasicNewPM()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.YEAR, 2008);
            cal.set(Calendar.MONTH, 10);
            cal.set(Calendar.DAY_OF_MONTH, 15);
            cal.set(Calendar.HOUR_OF_DAY, 14);
            cal.set(Calendar.MINUTE, 30);
            cal.set(Calendar.SECOND, 0);
            Date date = cal.getTime();
            try
            {
                tx.begin();
                Qualification q1 = new Qualification("Cycling Proficiency");
                q1.setDate(date);
                pm.makePersistent(q1);
                tx.commit();
                id = pm.getObjectId(q1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Check the persistence
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Qualification q = (Qualification)pm.getObjectById(id);
                assertNotNull("Qualification is null! when provided id=" + id, q);
                assertEquals("Name of Qualification retrieved is incorrect", "Cycling Proficiency", q.getName());
                assertEquals("Date of Qualification retrieved is incorrect", date, q.getDate());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            // Clean out data
            clean(Qualification.class);
        }
    }

    /**
     * Test of retrieval of basic object using an Extent.
     */
    public void testRetrieveByExtentBasic()
    {
        try
        {
            // Persist object
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ASub1 sub1 = new ASub1();
                sub1.setName("Second");
                sub1.setRevision(2);
                pm.makePersistent(sub1);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve object by id
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Extent ex = pm.getExtent(ASub1.class);
                int number = 0;
                Iterator iter = ex.iterator();
                ASub1 sub = null;
                while (iter.hasNext())
                {
                    sub = (ASub1)iter.next();
                    number++;
                }
                assertEquals("Number of pbjects in Extent was incorrect", 1, number);

                assertNotNull("ASub1 object was not found by getExtent", sub);
                assertEquals("Name of ASub1 is wrong", "Second", sub.getName());
                assertEquals("Revision of ASub1 is wrong", 2, sub.getRevision());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            clean(ASub1.class);
        }
    }

    /**
     * Test of retrieval of basic object using an Extent.
     */
    public void testRetrieveByExtentBasicWithInheritance()
    {
        try
        {
            // Persist object
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ABase base1 = new ABase();
                base1.setName("First");
                base1.setRevision(1);
                ASub1 sub1 = new ASub1();
                sub1.setName("Second");
                sub1.setRevision(2);
                pm.makePersistent(base1);
                pm.makePersistent(sub1);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve object by Extent including subclasses
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Extent ex = pm.getExtent(ABase.class, true);
                int number = 0;
                Iterator iter = ex.iterator();
                ASub1 sub = null;
                ABase base = null;
                while (iter.hasNext())
                {
                    Object obj = iter.next();
                    if (obj instanceof ASub1)
                    {
                        sub = (ASub1)obj;
                    }
                    else
                    {
                        base = (ABase)obj;
                    }
                    number++;
                }
                assertEquals("Number of objects in Extent was incorrect", 2, number);

                assertNotNull("ASub1 object was not found by getExtent", sub);
                assertEquals("Name of ASub1 is wrong", "Second", sub.getName());
                assertEquals("Revision of ASub1 is wrong", 2, sub.getRevision());

                assertNotNull("ABase object was not found by getExtent", base);
                assertEquals("Name of ABase is wrong", "First", base.getName());
                assertEquals("Revision of ABase is wrong", 1, base.getRevision());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve object by Extent excluding subclasses
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Extent ex = pm.getExtent(ABase.class, false);
                int number = 0;
                Iterator iter = ex.iterator();
                ABase base = null;
                while (iter.hasNext())
                {
                    base = (ABase)iter.next();
                    number++;
                }
                assertEquals("Number of objects in Extent was incorrect", 1, number);

                assertNotNull("ABase object was not found by getExtent", base);
                assertEquals("Name of ABase is wrong", "First", base.getName());
                assertEquals("Revision of ABase is wrong", 1, base.getRevision());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            clean(ASub1.class);
            clean(ABase.class);
        }
    }

    /**
     * Test of update of basic object,  using the same PM for persist and update.
     */
    public void testUpdateBasicSamePM()
    {
        Object id = null;

        try
        {
            // Persist the object
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.YEAR, 2008);
            cal.set(Calendar.MONTH, 10);
            cal.set(Calendar.DAY_OF_MONTH, 15);
            cal.set(Calendar.HOUR_OF_DAY, 14);
            cal.set(Calendar.MINUTE, 30);
            cal.set(Calendar.SECOND, 0);
            Date date = cal.getTime();
            try
            {
                tx.begin();
                Qualification q1 = new Qualification("Cycling Proficiency");
                q1.setDate(date);
                pm.makePersistent(q1);
                tx.commit();
                id = pm.getObjectId(q1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Update the object
            try
            {
                tx.begin();

                // Retrieve and change the object
                Qualification q = (Qualification)pm.getObjectById(id);
                q.setName("50m Swimming");

                tx.commit();
                id = pm.getObjectId(q);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown updating data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve and check the object
                Qualification q = (Qualification)pm.getObjectById(id);
                assertEquals("Name is incorrect after update", "50m Swimming", q.getName());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown updating data " + e.getMessage());
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
            clean(Qualification.class);
        }
    }

    /**
     * Test of update of basic object, using a different PM for persist and update.
     */
    public void testUpdateBasicNewPM()
    {
        Object id = null;

        try
        {
            // Persist the object
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.YEAR, 2008);
            cal.set(Calendar.MONTH, 10);
            cal.set(Calendar.DAY_OF_MONTH, 15);
            cal.set(Calendar.HOUR_OF_DAY, 14);
            cal.set(Calendar.MINUTE, 30);
            cal.set(Calendar.SECOND, 0);
            Date date = cal.getTime();
            try
            {
                tx.begin();
                Qualification q1 = new Qualification("Cycling Proficiency");
                q1.setDate(date);
                pm.makePersistent(q1);
                tx.commit();
                id = pm.getObjectId(q1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
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

            // Update the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve and change the object
                Qualification q = (Qualification)pm.getObjectById(id);
                q.setName("50m Swimming");

                tx.commit();
                id = pm.getObjectId(q);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown updating data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve and check the object
                Qualification q = (Qualification)pm.getObjectById(id);
                assertEquals("Name is incorrect after update", "50m Swimming", q.getName());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown updating data " + e.getMessage());
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
            clean(Qualification.class);
        }
    }

    /**
     * Test of persist of 1-1 UNIDIR relation (PC field).
     */
    public void testPersistOneToOneUni()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object acctId = null;
            Object loginId = null;
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount("Mickey", "Mouse", "mickeym", "minnie");
                acct.setId(1);
                Login login = acct.getLogin();
                login.setId(1);
                pm.makePersistent(acct);

                tx.commit();
                acctId = pm.getObjectId(acct);
                loginId = pm.getObjectId(login);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LoginAccount acct = (LoginAccount)pm.getObjectById(acctId);
                Login login = (Login)pm.getObjectById(loginId);
                assertEquals("Acct first name retrieved is incorrect", "Mickey", acct.getFirstName());
                assertEquals("Acct last name retrieved is incorrect", "Mouse", acct.getLastName());
                assertEquals("Login username retrieved is incorrect", "mickeym", login.getUserName());
                assertEquals("Login password retrieved is incorrect", "minnie", login.getPassword());
                assertEquals("Login of LoginAccount retrieved is incorrect", login, acct.getLogin());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            // Clean out data
            clean(LoginAccount.class);
            clean(Login.class);
        }
    }

    /**
     * Test of update of 1-1 relation (PC field).
     */
    public void testUpdateOneToOne()
    {
        try
        {
            // Persist data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object acctId = null;
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount("Mickey", "Mouse", "mickeym", "minnie");
                acct.setId(1);
                Login login = acct.getLogin();
                login.setId(1);
                pm.makePersistent(acct);

                tx.commit();
                acctId = pm.getObjectId(acct);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
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

            // Retrieve/Update data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LoginAccount acct = (LoginAccount)pm.getObjectById(acctId);
                assertEquals("Acct first name retrieved is incorrect", "Mickey", acct.getFirstName());
                assertEquals("Acct last name retrieved is incorrect", "Mouse", acct.getLastName());

                Login login2 = new Login("mmouse", "minnie");
                acct.setLogin(login2);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving/updating data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LoginAccount acct = (LoginAccount)pm.getObjectById(acctId);
                Login login = acct.getLogin();
                assertEquals("Acct first name retrieved is incorrect", "Mickey", acct.getFirstName());
                assertEquals("Acct last name retrieved is incorrect", "Mouse", acct.getLastName());
                assertEquals("Login username retrieved is incorrect", "mmouse", login.getUserName());
                assertEquals("Login password retrieved is incorrect", "minnie", login.getPassword());
                assertEquals("Login of LoginAccount retrieved is incorrect", login, acct.getLogin());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            clean(LoginAccount.class);
            clean(Login.class);
        }
    }

    /**
     * Test of delete of 1-1 UNIDIR relation (PC field).
     * Should cascade delete the related object.
     */
    public void testDeleteOneToOneUni()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object acctId = null;
            Object loginId = null;
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount("Mickey", "Mouse", "mickeym", "minnie");
                acct.setId(1);
                Login login = acct.getLogin();
                login.setId(1);
                pm.makePersistent(acct);

                tx.commit();
                acctId = pm.getObjectId(acct);
                loginId = pm.getObjectId(login);
            }
            catch (Exception e)
            {
                LOG.info("Exception persisting objects", e);
                fail("Exception thrown persisting data " + e.getMessage());
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

            // Check data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LoginAccount acct = (LoginAccount)pm.getObjectById(acctId);
                Login login = (Login)pm.getObjectById(loginId);
                assertEquals("Acct first name retrieved is incorrect", "Mickey", acct.getFirstName());
                assertEquals("Acct last name retrieved is incorrect", "Mouse", acct.getLastName());
                assertEquals("Login username retrieved is incorrect", "mickeym", login.getUserName());
                assertEquals("Login password retrieved is incorrect", "minnie", login.getPassword());
                assertEquals("Login of LoginAccount retrieved is incorrect", login, acct.getLogin());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.info("Exception retrieving to check results", e);
                fail("Exception thrown retrieving data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // delete the LoginAccount
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LoginAccount acct = (LoginAccount)pm.getObjectById(acctId);
                pm.deletePersistent(acct);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.info("Exception deleting objects", e);
                fail("Exception thrown retrieving data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                try
                {
                    pm.getObjectById(acctId);
                    fail("LoginAccount object was retrieved, but should have been deleted");
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    // Expected
                }
                try
                {
                    pm.getObjectById(loginId);
                    fail("Login object was retrieved, but should have been deleted");
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    // Expected
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.info("Exception retrieving to check results", e);
                fail("Exception thrown checking data " + e.getMessage());
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
            // Clean out data
            clean(LoginAccount.class);
            clean(Login.class);
        }
    }
}