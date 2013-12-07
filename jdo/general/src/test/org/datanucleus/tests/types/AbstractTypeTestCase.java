package org.datanucleus.tests.types;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Abstract base class for tests on types.
 */
public abstract class AbstractTypeTestCase extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    protected AbstractTypeTestCase(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    getSimpleClass()
                }
            );
            initialised = true;
        }
    }

    protected abstract Class getSimpleClass();
    
    protected abstract Object getOneObject();
    
    protected abstract void assertCorrectValues(Object obj);    

    protected abstract void changeObject(Object obj);    

    protected abstract void replaceObject(Object obj);    

    protected abstract int getNumberOfMutabilityChecks();    

    /**
     * Test of the basic persistence and retrieval of the type holder being tested.
     */
    public void testBasicPersistence()
    throws Exception
    {
        Class type = null;
        try
        {
            Object obj1 = getOneObject();
            type = obj1.getClass();
            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(obj1);
                id = JDOHelper.getObjectId(obj1);
                Object obj2 = pm.getObjectById(id,true);
                pm.refresh(obj2);

                assertCorrectValues(obj2);
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

            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Object obj3 = pm.getObjectById(id,true);
                assertCorrectValues(obj3);
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

            for( int i=0; i<getNumberOfMutabilityChecks(); i++)
            {
                // Check the mutability
                pm = pmf.getPersistenceManager();
                tx = pm.currentTransaction();
                try
                {
                    tx.begin();
                    Object obj4 = pm.getObjectById(id,true);
                    assertCorrectValues(obj4);

                    changeObject(obj4);

                    tx.commit();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    LOG.error(e);
                    fail("Error updating the object : " + e.getMessage());
                }
                finally
                {
                    if (tx.isActive())
                    {
                        tx.rollback();
                    }
                    pm.close();
                }

                // Check the mutability
                pm = pmf.getPersistenceManager();
                tx = pm.currentTransaction();
                try
                {
                    tx.begin();
                    Object obj4 = pm.getObjectById(id,true);
                    assertCorrectValues(obj4);
                    tx.commit();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    LOG.error(e);
                    fail("Error updating the object : " + e.getMessage());
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
        finally
        {
            clean(type);
        }
    }

    /**
     * Test of the attach/detach process for a holder of the type being tested.
     */
    public void testDetachAttach()
    throws Exception
    {
        Class type = null;
        try
        {
            Object obj1 = getOneObject();
            type = obj1.getClass();
            Object detached = null;
            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("group");
            try
            {
                tx.begin();
                pm.makePersistent(obj1);
                detached = pm.detachCopy(obj1);

                tx.commit();
                id = pm.getObjectId(obj1);

            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }        

            assertCorrectValues(detached);

            changeObject(detached);

            // Attach it
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("group");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                pm.makePersistent(detached);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst attaching object : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();

            }

            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Object obj3 = pm.getObjectById(id,true);
                assertCorrectValues(obj3);
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

            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("group");
            try
            {
                tx.begin();
                Object obj4 = pm.getObjectById(id,true);
                detached = pm.detachCopy(obj4);

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

            replaceObject(detached);

            // Attach it
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("group");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                pm.makePersistent(detached);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst attaching object : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();

            }

            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Object obj3 = pm.getObjectById(id,true);
                assertCorrectValues(obj3);
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
            clean(type);
        }
    }
}