/******************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved. 
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
 *****************************************************************/
package org.datanucleus.tests;

import java.sql.SQLException;

import javax.jdo.JDOException;
import javax.jdo.JDOFatalDataStoreException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.transaction.Synchronization;

import org.jpox.samples.models.company.Person;
import org.jpox.samples.one_one.unidir.Login;
import org.jpox.samples.one_one.unidir.LoginAccount;

/**
 * Series of tests for Transaction behaviour.
 * @version $Revision: 1.10 $
 */
public class TransactionTest extends JDOPersistenceTestCase
{
    public TransactionTest(String name)
    {
        super(name);
    }

    /**
     * Test for proper functioning of automatic rollback upon failed commit
     */
    public void testAutomaticRollback()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(true);

            LoginAccount acct = new LoginAccount("Fred", "Flintstone", "fred", "yabbadabbadoo");
            Login login = acct.getLogin();
            try
            {
                tx.begin();
                pm.makePersistent(acct);
                tx.commit();

                // provoke FK violation 
                tx.begin();
                pm.deletePersistent(login);
                boolean exceptionCaught = false;
                try
                {
                    tx.commit();
                    assertTrue("Should have caught exception during commit due to FK violation", false);                
                }
                catch (JDOException e)
                {
                    // Expected
                    exceptionCaught = true;
                }
                assertTrue("No exception was thrown during commit so couldnt test autoRollback", exceptionCaught);

                // now verify that we can still commit, i.e. tx was rolled back properly and is not active anymore
                tx.begin();
                LoginAccount acct2 = new LoginAccount("Wilma", "Flintstone", "wilma", "pebbles");
                pm.makePersistent(acct2);
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
            clean(LoginAccount.class);
        }
    }
    
    /**
     * Test that upon exception during commit, any SQLException that was the cause of the problem
     * is provided as nested exception
     */
    public void testSqlExceptionIsAccessible()
    {
        if (vendorID == null)
        {
            return; // Only applicable to RDBMS
        }

        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(true);

            LoginAccount acct = new LoginAccount("Fred", "Flintstone", "fred", "yabbadabbadoo");
            Login login = acct.getLogin();
            try
            {
                tx.begin();
                pm.makePersistent(acct);
                tx.commit();

                // provoke FK violation 
                tx.begin();
                pm.deletePersistent(login);
                boolean exceptionCaught = false;
                try
                {
                    tx.commit();
                    assertTrue("Should have caught exception during commit due to FK violation", false);                
                }
                catch (JDOException e)
                {
                    Throwable nested = e.getCause();
                    if (nested != null && (nested instanceof SQLException)) 
                    {
                        exceptionCaught = true;
                    }
                }
                assertTrue("Did not catch JDOException with nested SQLException, although expected", exceptionCaught);

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
            clean(LoginAccount.class);
        }
    }

    /**
     * Test for the "rollback-only" functionality of a transaction.
     */
    public void testRollbackOnly()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            tx.setRollbackOnly();
            assertTrue("Calling setRollbackOnly before starting the transaction should have had no effect, " +
                "but it set the rollbackOnly to true!", tx.getRollbackOnly() == false);
            try
            {
                tx.begin();

                Person pers = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                pm.makePersistent(pers);
                tx.setRollbackOnly();

                try
                {
                    tx.commit();
                    assertTrue("Calling commit() when setRollbackOnly is set should throw an exception, but didnt", false);
                }
                catch (JDOFatalDataStoreException fde)
                {
                    // Should come through here (outside J2EE container)
                }
                catch (JDOUserException fue)
                {
                    // Should come through here (inside J2EE container)
                }
                
                try
                {
                    tx.commit();
                    assertTrue("Calling commit() when setRollbackOnly is set should throw an exception, but didnt", false);
                }
                catch (JDOFatalDataStoreException fde)
                {
                    // Should come through here
                }
                catch (JDOUserException fue)
                {
                    // Should come through here (inside J2EE container)
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
        finally
        {
            clean(Person.class);
        }
    }

    /** Used by testUpdateDuringBeforeCompletion. */
    private Person person;

    /** Used by testUpdateDuringBeforeCompletion. */
    private String emailValueBeforeCompletion;

    /**
     * JDO2 $13.4.3 Synchronization: "During the user's beforeCompletion method, fields in persistent and 
     * transactional instances might be changed, persistent instances might be deleted, and instances might be 
     * made persistent. These changes will be reflected in the current transaction."
     */
    public void testUpdateDuringBeforeCompletion()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setNontransactionalRead(true);
            person = null;
            tx.setSynchronization(new Synchronization()
            {
                public void beforeCompletion()
                {
                    person.setEmailAddress(emailValueBeforeCompletion);
                }

                public void afterCompletion(int arg0)
                {
                }
            });

            try
            {
                // test on P_NEW object
                tx.begin();
                emailValueBeforeCompletion = "fred@jpox.org";
                person = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                pm.makePersistent(person);
                tx.commit();
                verifyStringSetDuringBeforeCompletion(pm, person);

                // test on HOLLOW object
                emailValueBeforeCompletion = "fred.flintstone@aol.com";
                tx.begin();
                tx.commit();
                verifyStringSetDuringBeforeCompletion(pm, person);
            }
            finally
            {
                tx.setSynchronization(null);
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

    private void verifyStringSetDuringBeforeCompletion(PersistenceManager pm, Person pers)
    {
        // verify that description got updated in beforeCompletion()
        assertEquals(emailValueBeforeCompletion, pers.getEmailAddress());
        Object id = JDOHelper.getObjectId(pers);

        // verify that email got updated in DB
        pm.evict(pers);
        pers = (Person) pm.getObjectById(id);
        assertEquals(emailValueBeforeCompletion, pers.getEmailAddress());
    }
}