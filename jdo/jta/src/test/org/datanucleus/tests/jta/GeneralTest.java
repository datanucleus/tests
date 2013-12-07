/**********************************************************************
Copyright (c) 2007 Guido Anzuoni and others. All rights reserved.
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
package org.datanucleus.tests.jta;

import java.util.Collection;
import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import junit.framework.Assert;

import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.TestHelper;
import org.datanucleus.tests.jta.util.PersistenceManagerDisposer;
import org.jpox.samples.models.company.Account;
import org.jpox.samples.one_one.unidir.Login;
import org.jpox.samples.one_one.unidir.LoginAccount;

/**
 * Series of general tests for JTA.
 * Use of JTA here assumes 2 basic situations
 * <ul>
 * <li>UserTransaction obtained by the user and transactions demarcated using that by the user
 * **before** the JDO transaction is touched. In this case you don't use the JDO txn methods.</li>
 * <li>UserTransaction not obtained, and instead the user demarcates their transaction using the
 * JDO transaction methods. This should automatically start the UserTransaction, and commit it
 * when jdoTx.commit is called.</li>
 * </ul>
 */
public class GeneralTest extends JDOPersistenceTestCase
{
    /**
     * When run from testrunner.j2ee, we are given the kind of appserver we are running in,
     * so we can adapt the tests' expected behaviour to certain appserver peculiarities
     */
    private String cargoContainerId = System.getProperty("cargo.container.id");

    public GeneralTest(String name)
    {
        super(name);
    }

    protected void tearDown() throws Exception
    {
        try 
        {
            UserTransaction ut = getUserTransaction();
            ut.rollback();
        }
        catch (Throwable t)
        {}
        
        PersistenceManagerFactory lpmf = TestHelper.getPMF(2, null);
        clean(lpmf, Account.class);
        super.tearDown();
    }

    public void testEmptyJTA() throws Exception
    {
        // What does this test ?
    }

    /**
     * Test that uses JTA but starts the transaction using the JDO transaction methods.
     * See JDO2 16.1.3 for a brief description of the type of situation
     */
    public void testBasicJTAViaJDOTransaction() throws Exception
    {
        UserTransaction ut = getUserTransaction();
        ut.setTransactionTimeout(300);

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.setOptimistic(true);
        assertEquals("Transaction type is not JTA!", "JTA", pmf.getTransactionType());

        int totals = 0;
        try
        {
            // Demarcate the txn using JDO txn
            tx.begin();
            try
            {
                // Try to start the UserTransaction now (should have been started by JDO txn)
                ut.begin();
                fail("Attempted call to UserTransaction.begin after starting JDO txn directly worked!!");
            }
            catch (Exception e)
            {
                // Expected since the JDO txn started the user transaction
            }
            assertTrue("Transaction is not active after starting UserTransaction", tx.isActive());
            Query q = pm.newQuery(Account.class);
            Collection c = (Collection) q.execute();
            totals = c.size();
            q.closeAll();
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.info(">> Exception thrown ", e);
            fail("Exception thrown during use of JTA via JDO Transaction");
        }
        finally
        {
            pm.close();
        }

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            tx.begin();
            Account accnt = new Account();
            accnt.setUsername("jpox");
            pm.makePersistent(accnt);
            tx.commit();
        }
        finally
        {
            pm.close();
        }

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            tx.begin();
            Query q = pm.newQuery(Account.class);
            Collection c = (Collection) q.execute();
            try
            {
                Assert.assertTrue(c.size() == (totals + 1));
            }
            finally
            {
                q.closeAll();
                tx.commit();
            }
        }
        finally
        {
            pm.close();
        }
    }

    public void testBasicJTA() throws Exception
    {
        UserTransaction ut = getUserTransaction();
        ut.setTransactionTimeout(300);

        int totals = 0;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            ut.begin();
            assertTrue("Transaction is not active after starting UserTransaction", tx.isActive());
            Query q = pm.newQuery(Account.class);
            Collection c = (Collection) q.execute();
            totals = c.size();
            q.closeAll();
            ut.commit();
        }
        finally
        {
            pm.close();
        }

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            ut.begin();
            Account accnt = new Account();
            accnt.setUsername("jpox");
            pm.makePersistent(accnt);
            ut.commit();
        }
        finally
        {
            pm.close();
        }

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            ut.begin();
            Query q = pm.newQuery(Account.class);
            Collection c = (Collection) q.execute();
            try
            {
                Assert.assertTrue(c.size() == (totals + 1));
            }
            finally
            {
                q.closeAll();
                ut.commit();
            }
        }
        finally
        {
            pm.close();
        }
    }

    public void testMultiTxnJTA() throws Exception
    {
        UserTransaction ut = getUserTransaction();
        ut.setTransactionTimeout(300);

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            ut.begin();
            Query q = pm.newQuery(Account.class);
            Collection c = (Collection) q.execute();
            try
            {
                Assert.assertEquals("should have no elements in db",0,c.size());
            }
            finally
            {
                q.closeAll();
                ut.commit();
            }
        }
        finally
        {
            pm.close();
        }

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            ut.begin();
            Account accnt = new Account();
            accnt.setUsername("jpox");
            pm.makePersistent(accnt);
            ut.commit();
        }
        finally
        {
            pm.close();
        }
        //db has now 1 element
        
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            ut.begin();
            Account accnt = new Account();
            accnt.setUsername("jpox2");
            pm.makePersistent(accnt);
            pm.flush();
            ut.rollback();
        }
        finally
        {
            pm.close();
        }
        //db should still have 1 element
        
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            ut.begin();
            Query q = pm.newQuery(Account.class);
            Collection c = (Collection) q.execute();
            try
            {
                Assert.assertEquals(1,c.size());
            }
            finally
            {
                q.closeAll();
                ut.commit();
            }
        }
        finally
        {
            pm.close();
        }
    }

    public void testDelayedFlushExceptionJTA() throws Exception
    {
        UserTransaction ut = getUserTransaction();
        ut.setTransactionTimeout(300);

        int totals = 0;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            ut.begin();
            Query q = pm.newQuery(Account.class);
            Collection c = (Collection) q.execute();
            totals = c.size();
            q.closeAll();
            ut.commit();
        }
        finally
        {
            pm.close();
        }

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(true);
        boolean rbk = false;
        try
        {
            ut.begin();
            Account accnt = new Account();
            accnt.setUsername(null);
            pm.makePersistent(accnt);
            ut.commit();
        }
        // e.g. JBoss wraps the NucleusDataStoreException
        catch (Exception e)
        {
            // Expected
            if (isRollbackDueToDatastoreException(e))
            {
                rbk = true;
            }
        }
        finally
        {
            Assert.assertTrue("Not Rolledback", rbk);
            Assert.assertTrue("UserTransaction should not be active anymore?!", ut.getStatus()==Status.STATUS_NO_TRANSACTION);
            if (pm.currentTransaction().isActive() && ut.getClass().getName().startsWith("org.objectweb.jotm"))
            {
                // see http://www.jpox.org/servlet/jira/browse/NUCCORE-224
                fail("JOTM bug: when an exception is thrown during Synchronization.beforeCompletion(), the UserTransaction's status is " +
                    "STATUS_NO_TRANSCTION, but there was no callback to Synchronization.afterCompletion()");
            }
            pm.close();
        }

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            ut.begin();
            Query q = pm.newQuery(Account.class);
            Collection c = (Collection) q.execute();
            try
            {
                Assert.assertTrue(c.size() == (totals));
            }
            finally
            {
                q.closeAll();
                ut.commit();
            }
        }
        finally
        {
            pm.close();
        }
    }

	private boolean isRollbackDueToDatastoreException(Exception e) {
		return (e instanceof NucleusDataStoreException)
		        // JBoss 4.0.3SP1: some JBoss-Exception that has our original Exception's msg in its message
		        || (e.getMessage()!=null && e.getMessage().contains(NucleusDataStoreException.class.getSimpleName()))
		        // JBoss 4.2.3.GA: javax.transaction.RollbackException with the following message
		        // "javax.transaction.RollbackException: [com.arjuna.ats.internal.jta.transaction.arjunacore.commitwhenaborted] [com.arjuna.ats.internal.jta.transaction.arjunacore.commitwhenaborted] Can't commit because the transaction is in aborted state"
		        // JOTM 2.1.4: RollbackException without any message
		        || (e instanceof RollbackException);
	}
	
    public void testAddDeleteJTA() throws Exception
    {
        boolean opt = false;
        UserTransaction ut = getUserTransaction();
        ut.setTransactionTimeout(300);

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.setOptimistic(opt);
        ut.begin();
        Query q = pm.newQuery(Account.class);
        Collection c = (Collection) q.execute();
        c.size();
        q.closeAll();
        ut.commit();
        pm.close();

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(opt);
        ut.begin();
        Account accnt = new Account();
        accnt.setUsername("jpox");
        pm.makePersistent(accnt);
        Object oid = pm.getObjectId(accnt);
        ut.commit();
        pm.close();

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(opt);
        ut.begin();
        accnt = (Account) pm.getObjectById(oid);
        pm.deletePersistent(accnt);
        ut.commit();
        pm.close();

        pm = pmf.getPersistenceManager();
        pm.currentTransaction().setOptimistic(opt);
        ut.begin();
        try
        {
            accnt = (Account) pm.getObjectById(oid);
            System.err.println(accnt);
            Assert.assertTrue("accnt still in db:"+pm.getObjectId(accnt), false);
        }
        catch (javax.jdo.JDOObjectNotFoundException ex)
        {
        }
        finally
        {
            ut.commit();
            pm.close();
        }
    }

    public void testAddDeleteJTANoBatch() throws Exception
    {
        boolean opt = false;
        UserTransaction ut = getUserTransaction();
        ut.setTransactionTimeout(300);

        Properties props = new Properties();
        props.put("datanucleus.rdbms.statementBatchLimit", "0");
        PersistenceManagerFactory lpmf = TestHelper.getPMF(1, props);
        PersistenceManager pm = null;
        Transaction tx;

        pm = lpmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(opt);
        ut.begin();
        Query q = pm.newQuery(Account.class);
        Collection c = (Collection) q.execute();
        c.size();
        q.closeAll();
        ut.commit();
        pm.close();

        pm = lpmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(opt);
        ut.begin();
        Account accnt = new Account();
        accnt.setUsername("jpox");
        pm.makePersistent(accnt);
        Object oid = pm.getObjectId(accnt);
        ut.commit();
        pm.close();

        pm = lpmf.getPersistenceManager();
        tx = pm.currentTransaction();
        tx.setOptimistic(opt);
        ut.begin();
        accnt = (Account) pm.getObjectById(oid);
        pm.deletePersistent(accnt);
        ut.commit();
        pm.close();

        pm = lpmf.getPersistenceManager();
        pm.currentTransaction().setOptimistic(opt);
        ut.begin();
        try
        {
            accnt = (Account) pm.getObjectById(oid);
            System.err.println(accnt);
            Assert.assertTrue("accnt still in db:"+pm.getObjectId(accnt), false);
        }
        catch (javax.jdo.JDOObjectNotFoundException ex)
        {
        }
        finally
        {
            ut.commit();
            pm.close();
        }
    }

    /**
     * Test that pm.currentTransaction.isActive() is correct when marked for rollback before 
     * JTATransactionImpl had a chance to join
     */
    public void testEmptyTxTimeout()
        throws NamingException,
        SystemException,
        NotSupportedException,
        SecurityException,
        IllegalStateException,
        RollbackException,
        HeuristicMixedException,
        HeuristicRollbackException
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            try
            {
                UserTransaction ut = getUserTransaction();
                ut.setTransactionTimeout(1);
                ut.begin();
                synchronized (this)
                {
                    // provoke timeout
                    try
                    {
                        wait(1200);
                    }
                    catch (InterruptedException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                // make sure we were marked for rollback (JOTM + JBoss 4.0.3SP1), or rolled back already (JBoss 4.2.3)
                assertTrue("Expected UserTransaction.getStatus() to be either STATUS_MARKED_ROLLBACK or STATUS_ROLLEDBACK, but was " + ut.getStatus(), ut.getStatus() == Status.STATUS_MARKED_ROLLBACK || ut.getStatus() == Status.STATUS_ROLLEDBACK);

                if (ut.getStatus()==Status.STATUS_MARKED_ROLLBACK) // JOTM + JBoss 4.0.3SP1
                {
                    // by definition should be active even if marked for rollback
                    assertTrue(pm.currentTransaction().isActive());
                    ut.rollback();
                }
                else if (ut.getStatus()==Status.STATUS_ROLLEDBACK) // JBoss 4.2.3
                {
                    // by definition should be active (anything but NO_TRANSACTION means active)
                    assertTrue(pm.currentTransaction().isActive());
                    // workaround for JBoss 4.2.3 bug https://jira.jboss.org/jira/browse/JBAS-6663
                    if (cargoContainerId.equals("jboss42x"))
                    {
                        try
                        {
                            ut.rollback();
                        }
                        catch (IllegalStateException e)
                        {
                            // expected, make sure transaction has been disassociated from thread
                            assertTrue("Workaround for https://jira.jboss.org/jira/browse/JBAS-6663 has failed?!", ut.getStatus()==Status.STATUS_NO_TRANSACTION);
                        }
                    } else // whatever appserver this may be 
                    {
                        ut.rollback();
                    }
                }
                assertFalse(pm.currentTransaction().isActive());

                // reset timeout to default
                ut.setTransactionTimeout(0);

                // now verify that we can still commit, i.e. tx was rolled back properly and is not active anymore
                ut.begin();
                LoginAccount acct2 = new LoginAccount("Wilma", "Flintstone", "wilma", "pebbles");
                pm.makePersistent(acct2);
                ut.commit();
                assertFalse(pm.currentTransaction().isActive());
            }
            finally
            {
                try 
                {
                    pm.close();
                }
                catch (Exception e) 
                {
                    // eat exception so test will fail with the underlying exception
                    TestHelper.LOG.error("pm.close() failed", e);
                }
            }
        }
        finally
        {
            try 
            {
                clean(LoginAccount.class);
            }
            catch (Exception e) 
            {
                // eat exception so test will fail with the underlying exception
                TestHelper.LOG.error("clean(LoginAccount.class) failed", e);
            }
        }
    }

    /**
     * Test expected behaviour upon failed commit with JTA
     * (copied from test.jdo.general/org.datanucleus.tests.TransactionTest.java)
     * @throws NamingException 
     * @throws SystemException 
     * @throws NotSupportedException 
     * @throws HeuristicRollbackException 
     * @throws HeuristicMixedException 
     * @throws RollbackException 
     * @throws IllegalStateException 
     * @throws SecurityException 
     */
    public void testFailedCommit() throws NamingException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(true);
            UserTransaction ut = getUserTransaction();

            LoginAccount acct = new LoginAccount("Fred", "Flintstone", "fred", "yabbadabbadoo");
            Login login = acct.getLogin();
            try
            {
                ut.begin();
                pm.makePersistent(acct);
                ut.commit();

                // provoke FK violation 
                ut.begin();
                pm.deletePersistent(login);
                boolean exceptionCaught = false;
                try
                {
                    ut.commit();
                    assertTrue("Should have caught exception during commit due to FK violation", false);                
                }
                // e.g. JBoss wraps the NucleusDataStoreException
                catch (Exception e)
                {
                    // Expected
                    if (isRollbackDueToDatastoreException(e))
                    {
                        exceptionCaught = true;
                    }
                }
                assertTrue("No exception was thrown during commit so couldnt test autoRollback", exceptionCaught);
                // receiving a RollbackException in commmit() by definition means the UT was rolled back
                if (ut.getStatus()==Status.STATUS_NO_TRANSACTION && pm.currentTransaction().isActive() && ut.getClass().getName().startsWith("org.objectweb.jotm"))
                {
                    // see http://www.jpox.org/servlet/jira/browse/NUCCORE-224
                    fail("JOTM bug: when an exception is thrown during Synchronization.beforeCompletion(), the UserTransaction's status is " +
                        "STATUS_NO_TRANSCTION, but there was no callback to Synchronization.afterCompletion()");
                }
                assertFalse(pm.currentTransaction().isActive());
                
                // now verify that we can still commit, i.e. tx was rolled back properly and is not active anymore
                ut.begin();
                LoginAccount acct2 = new LoginAccount("Wilma", "Flintstone", "wilma", "pebbles");
                pm.makePersistent(acct2);
                ut.commit();
                assertFalse(pm.currentTransaction().isActive());
            }
            finally
            {
                try 
                {
                    if (ut.getStatus()!=Status.STATUS_NO_TRANSACTION)
                    {
                        ut.rollback();
                    }
                    pm.close();
                }
                catch (Exception e) 
                {
                    // eat exception so test will fail with the underlying exception
                    TestHelper.LOG.error("failure during finally block", e);
                }
            }
        }
        finally
        {
            try 
            {
                clean(LoginAccount.class);
            }
            catch (Exception e) 
            {
                // eat exception so test will fail with the underlying exception
                TestHelper.LOG.error("clean(LoginAccount.class) failed", e);
            }
        }
    }

    public void testCloseOnTxnCompletion()
    throws Exception
    {
        PersistenceManager pm = null;
        pm = pmf.getPersistenceManager();
        new PersistenceManagerDisposer(pm);
        pm.currentTransaction().begin();
        Account accnt = new Account();
        accnt.setUsername("jpox");
        pm.makePersistent(accnt);
        Object oid = pm.getObjectId(accnt);
        pm.currentTransaction().commit();
        assertTrue("The PersistenceManager is still open", pm.isClosed());

        
        pm = pmf.getPersistenceManager();
        new PersistenceManagerDisposer(pm);
        pm.currentTransaction().begin();
        accnt = (Account) pm.getObjectById(oid);
        pm.deletePersistent(accnt);
        pm.currentTransaction().commit();
        assertTrue("The PersistenceManager is still open", pm.isClosed());

        
        pm = pmf.getPersistenceManager();
        new PersistenceManagerDisposer(pm);
        pm.currentTransaction().begin();
        accnt = new Account();
        accnt.setUsername("jpox");
        pm.makePersistent(accnt);
        pm.currentTransaction().rollback();
        assertTrue("The PersistenceManager is still open", pm.isClosed());

        UserTransaction ut = getUserTransaction();
        ut.begin();
        pm = pmf.getPersistenceManager();
        new PersistenceManagerDisposer(pm);
        accnt = new Account();
        accnt.setUsername("jpox");
        pm.makePersistent(accnt);
        oid = pm.getObjectId(accnt);
        ut.commit();
        assertTrue("The PersistenceManager is still open", pm.isClosed());


        ut.begin();
        pm = pmf.getPersistenceManager();
        new PersistenceManagerDisposer(pm);
        accnt = (Account) pm.getObjectById(oid);
        pm.deletePersistent(accnt);
        ut.commit();
        assertTrue("The PersistenceManager is still open", pm.isClosed());

        
        ut.begin();
        pm = pmf.getPersistenceManager();
        new PersistenceManagerDisposer(pm);
        accnt = new Account();
        accnt.setUsername("jpox");
        pm.makePersistent(accnt);
        oid = pm.getObjectId(accnt);
        ut.rollback();
        assertTrue("The PersistenceManager is still open", pm.isClosed());
    }

    /**
     * Verify that any exceptions thrown during JTATransactionImpl.beforeCompletion() are propagated properly, either
     * caused by flushing, or by invoking user code in a user-provided Synchronization.beforeCompletion() callback.
     */
    public void testExceptionDuringBeforeCompletion()
        throws NotSupportedException,
        SystemException,
        NamingException,
        RollbackException,
        HeuristicMixedException,
        HeuristicRollbackException
    {
        pm = pmf.getPersistenceManager();
        new PersistenceManagerDisposer(pm);
        final String msg = "This was expected.";
        pm.currentTransaction().setSynchronization(new Synchronization()
        {

            public void beforeCompletion()
            {
                // throw the exception that we want to see during ut.commit()
                throw new RuntimeException(msg);

            }

            public void afterCompletion(int arg0)
            {
            }
        });

        boolean caughtExpectedException = false;
        UserTransaction ut = getUserTransaction();
        ut.begin();
        try
        {
            // access the currentTransaction so it joins UserTransaction
            pm.currentTransaction().isActive();
            ut.commit();
        }
        catch (Exception e)
        {
            
        	if (e instanceof RollbackException)
            {
                // JBoss 4.2.3 and JOTM 2.1.4 throw a RollbackException that has no clue about our original exception
                caughtExpectedException = true;
            }
            else if (cargoContainerId!=null && cargoContainerId.equals("jboss4x"))
            {
                if (!e.getMessage().contains(msg))
                {
                    e.printStackTrace();
                    throw new RuntimeException("With jboss4.0.3 we expect the exception caught to contain the message of our thrown exception", e);
                }
                else 
                {
                    caughtExpectedException = true;
                }
            }
        }
        assertTrue("Exception thrown during beforeCompletion() wasn't propagated properly", caughtExpectedException);
    }
 
//    /**
//     * Verify that JOTM properly calls Synchronization.afterCompletion() if there had been an exception
//     * during Synchronization.beforeCompletion()
//     */
//    public void testExceptionDuringBeforeCompletionSupportedByAppserver()
//        throws NotSupportedException,
//        SystemException,
//        NamingException,
//        RollbackException,
//        HeuristicMixedException,
//        HeuristicRollbackException, ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
//    {
//    	Class<?> jotmCurrentClass = this.getClass().getClassLoader().loadClass("org.objectweb.jotm.Current");
//    	TransactionManager tm = (TransactionManager) jotmCurrentClass.getMethod("getTransactionManager").invoke(null);
//        final String msg = "This was expected.";
//        boolean caughtExpectedException = false;
//        final boolean[] afterCompletionCalled = new boolean[1];
//        afterCompletionCalled[0] = false;
//        
//        UserTransaction ut = getUserTransaction();
//        ut.begin();
//        tm.getTransaction().registerSynchronization(new Synchronization()
//        {
//
//            public void beforeCompletion()
//            {
//                // throw the exception that we want to see during ut.commit()
//                throw new RuntimeException(msg);
//
//            }
//
//            public void afterCompletion(int arg0)
//            {
//            	afterCompletionCalled[0] = true;
//            }
//        });
//        assertTrue(ut.getStatus()!=Status.STATUS_NO_TRANSACTION);
//        
//        try
//        {
//            ut.commit();
//        }
//        catch (RollbackException e)
//        {
//            caughtExpectedException = true;
//        }
//        assertTrue("Exception thrown during beforeCompletion() wasn't propagated properly", caughtExpectedException);
//        if (ut.getStatus()==Status.STATUS_NO_TRANSACTION)
//        {
//        	assertTrue("The UserTransaction's status is STATUS_NO_TRANSACTION but afterCompletion() wasn't called", afterCompletionCalled[0]==true);
//        }
//    }    
    
    private UserTransaction getUserTransaction() 
    throws NamingException
    {
        return (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
    }
    
    /**
     * Verify accessibility of a previously acquired DB connection during Synchronization.afterCompletion()
     * (e.g. for application code that performs invalidation of custom caches in afterCompletion(),
     * such as cached results of computations based on persistent values)
     */
    public void testConnectionAccessDuringAfterCompletion() throws NamingException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException 
    {
        final PersistenceManager pm = pmf.getPersistenceManager();
        
        // prepare a persistent object to be found later on
        pm.currentTransaction().begin();
        Account accnt = new Account();
        accnt.setUsername("jpox");
        pm.makePersistent(accnt);
        pm.currentTransaction().commit();
        final boolean[] success = new boolean[1];

        // register a Synchronization object that accesses the DB during afterCompletion()
        pm.currentTransaction().setSynchronization(new Synchronization()
        {

            public void beforeCompletion()
            {

            }

            public void afterCompletion(int arg0)
            {
                // access a DB connection acquired previously during the transaction
                dbConnectionAccess(pm);
                success[0] = true;
            }
        });

        UserTransaction ut = getUserTransaction();
        ut.begin();
        // acquire a db connection for the ongoing transaction
        dbConnectionAccess(pm);
        ut.commit();
        assertTrue("accessing the DB connection during afterCompletion() wasn't successful", success[0]);
    }

    /**
     * Do something that will require a database connection
     * @param pm
     */
    private void dbConnectionAccess(final PersistenceManager pm)
    {
        Query query = pm.newQuery(Account.class, "username.equals('jpox')");
        /*Collection result = (Collection)*/ query.execute();
    }
}