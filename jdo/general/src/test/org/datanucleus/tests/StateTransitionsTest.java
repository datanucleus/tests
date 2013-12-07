/**********************************************************************
Copyright (c) 2003 Erik Bengtson and others. All rights reserved.
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.Person;

/**
 * Test for basic state transitions.
 */
public class StateTransitionsTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    // Person data
    private static String EMAIL[] = {"jon.doe@msn.com", "jane.smith@msn.com", "tom.jones@aol.com"};
    private static String FIRSTNAME[] = {"Jon", "Jane", "Tom"};
    private static String LASTNAME[] = {"Doe", "Smith", "Jones"};

    public StateTransitionsTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(new Class[] { Person.class  });
            initialised = true;
        }
    }

    /**
     * Test for the majority of possible state transitions.
     * @throws Exception
     */
    public void testStateTransitions() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = null;
            Object id = null;

            try
            {
                Person x = new Person(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);
                tx = pm.currentTransaction();

                tx.setNontransactionalRead(false);
                tx.begin();

                // 1. transient to persistent-new
                assertTransient(x);
                pm.makePersistent(x);
                assertPersistentNew(x);

                // 15. persistent-new to transient
                tx.rollback();
                assertTransient(x);

                tx.begin();
                pm.makePersistent(x);
                assertPersistentNew(x);

                // 16. persistent-new to persistent-new-deleted
                pm.deletePersistent(x);
                assertPersistentNewDeleted(x);

                // 18. persistent-new-deleted to transient
                tx.commit();
                assertTransient(x);

                // 2. persistent-new to hollow
                x = new Person(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);
                tx.begin();
                pm.makePersistent(x);
                assertPersistentNew(x);
                id = pm.getObjectId(x);
                tx.commit();
                assertHollow(x);

                if (!tx.getOptimistic())
                {
                    // ?. hollow
                    tx.begin();
                    x = (Person) pm.getObjectById(id, true);
                    assertHollowOrPersistentClean(x);
                    tx.setNontransactionalRead(false);
                    tx.commit();
                    assertHollow(x);
                    boolean success = false;
                    try
                    {
                        x.getFirstName();
                    }
                    catch (JDOUserException ex)
                    {
                        success = true;
                    }
                    if (!success)
                    {
                        fail("Expected exception while trying to read a field in a Hollow pc with 'NonTransactionalRead=false'.");
                    }
                }

                // 6. persistent-clean to hollow
                tx.begin();
                x = (Person) pm.getObjectById(id, false);
                assertHollow(x);
                x.getFirstName();
                if (tx.getOptimistic())
                {
                    assertPersistentNontransactional(x);
                }
                else
                {
                    assertPersistentClean(x);
                }
                tx.commit();
                assertHollow(x);

                // 3. hollow to persistent-clean
                tx.begin();
                assertHollow(x);
                x.getFirstName();
                if (tx.getOptimistic())
                {
                    assertPersistentNontransactional(x);
                }
                else
                {
                    assertPersistentClean(x);
                }
                tx.commit();

                // 11. hollow to persistent-dirty
                tx.begin();
                assertHollow(x);
                x.setLastName(LASTNAME[1]);
                assertPersistentDirty(x);
                // 5. persistent-dirty to hollow via rollback
                tx.rollback();
                assertHollow(x);

                tx.begin();
                // 19. hollow to persistent-deleted
                assertHollow(x);
                pm.deletePersistent(x);
                assertPersistentDeleted(x);
                // 21. persistent-deleted to hollow
                tx.rollback();
                assertHollow(x);

                tx.begin();
                x.getFirstName();
                // 4. persistent-clean to persistent-dirty
                if (tx.getOptimistic())
                {
                    assertPersistentNontransactional(x);
                }
                else
                {
                    assertPersistentClean(x);
                }
                x.setLastName(LASTNAME[1]);
                assertPersistentDirty(x);

                // 19. persistent-dirty to persistent-deleted
                pm.deletePersistent(x);
                assertPersistentDeleted(x);
                // 21. persistent-deleted to hollow
                tx.rollback();
                assertHollow(x);

                tx.begin();
                // 5. persistent-dirty to hollow via commit
                x.setLastName(LASTNAME[1]);
                assertPersistentDirty(x);
                tx.commit();
                assertHollow(x);

                tx.begin();
                x.setLastName(LASTNAME[2]);
                assertPersistentDirty(x);
                // 19. persistent-dirty to persistent-deleted
                pm.deletePersistent(x);
                assertPersistentDeleted(x);
                // 20. persistent-deleted to transient
                tx.commit();
                assertTransient(x);

                x = new Person(1, FIRSTNAME[1], LASTNAME[1], EMAIL[1]);
                tx.begin();
                pm.makePersistent(x);
                pm.deletePersistent(x);
                assertPersistentNewDeleted(x);
                // 18. persistent-new-deleted to transient via commit
                tx.commit();
                assertTransient(x);

                x = new Person(2, FIRSTNAME[2], LASTNAME[2], EMAIL[2]);
                tx.begin();
                pm.makePersistent(x);
                pm.deletePersistent(x);
                assertPersistentNewDeleted(x);
                // 17. persistent-new-deleted to transient via rollback
                tx.rollback();
                assertTransient(x);

                x = new Person(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);
                tx.begin();
                pm.makePersistent(x);
                id = pm.getObjectId(x);
                tx.commit();
                assertHollow(x);
                tx.begin();
                x.getFirstName();
                if (tx.getOptimistic())
                {
                    assertPersistentNontransactional(x);
                }
                else
                {
                    assertPersistentClean(x);
                }
                // 12. persistent-clean to persistent-nontransactional
                pm.makeNontransactional(x);
                assertPersistentNontransactional(x);
                tx.commit();

                tx.begin();
                // 13. persistent-nontransactional to persistent-clean
                pm.makeTransactional(x);
                assertPersistentClean(x);
                tx.commit();

                tx.begin();
                pm.makeNontransactional(x);
                assertPersistentNontransactional(x);
                // 14. persistent-nontransactional to persistent-dirty
                x.setLastName(LASTNAME[1]);
                assertPersistentDirty(x);
                tx.rollback();

                tx.setNontransactionalRead(true);
                assertHollow(x);
                // 22. hollow to persistent-nontransactional
                x.getFirstName();
                assertPersistentNontransactional(x);

                // ?. hollow exception
                tx.begin();
                x = (Person) pm.getObjectById(id, false);
                assertHollow(x);
                tx.setNontransactionalRead(false);
                tx.commit();
                assertHollow(x);
                boolean success = false;
                try
                {
                    x.getFirstName();
                }
                catch (JDOUserException ex)
                {
                    success = true;
                }
                if (!success)
                {
                    fail("Expected exception while trying to read a field in a Hollow pc with 'NonTransactionalRead=false'.");
                }

                tx.setNontransactionalRead(false);
                // ?. hollow exception
                tx.begin();
                x = new Person(3, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);
                pm.makePersistent(x);
                tx.commit();
                success = false;
                try
                {
                    x.getFirstName();
                }
                catch (JDOUserException ex)
                {
                    success = true;
                }
                if (!success)
                {
                    fail("Expected exception while trying to read a field in a Hollow pc with 'NonTransactionalRead=false'.");
                }
                // ?. transient to transient-clean
                Person y = new Person(1, FIRSTNAME[1], LASTNAME[1], EMAIL[1]);
                tx.begin();
                assertTransient(y);            
                pm.makeTransactional(y);
                assertTransientClean(y);
                tx.commit();
                assertTransientClean(y);
                // ?. transient-clean to transient            
                pm.makeNontransactional(y);
                assertTransient(y); 
                // ?. transient to transient-clean         
                assertTransient(y);            
                pm.makeTransactional(y);    
                assertTransientClean(y);            
                // ?. transient-clean to transient-dirty         
                tx.begin();
                assertTransientClean(y);
                y.setLastName(EMAIL[2]);
                assertTransientDirty(y);            
                tx.commit();
                assertTransientClean(y);            

                // ?. transient-clean to transient-dirty to transient-clean           
                tx.begin();
                assertTransientClean(y);
                y.setLastName(EMAIL[2] + "a");
                assertTransientDirty(y);            
                tx.rollback();
                assertTransientClean(y);  
                tx.begin();
                y.setLastName(EMAIL[2]);         
                tx.rollback();
                assertTransientClean(y);            
                // ?. transient-clean to transient-dirty to transient-clean           
                tx.begin();
                assertTransientClean(y);
                y.setLastName(EMAIL[2] + "a");
                assertTransientDirty(y);            
                tx.commit();
                assertTransientClean(y);  
                // ?. transient to transient-dirty to transient-clean  
                Person z = new Person(2, FIRSTNAME[2], LASTNAME[2], EMAIL[2]);            
                tx.begin();
                pm.makeTransactional(z);            
                z.setLastName(EMAIL[0]);
                assertTransientDirty(z);            
                tx.rollback();
                assertTransientClean(z);             

                // ?. persistent-new to detached
                x = new Person(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);
                pm.setDetachAllOnCommit(true);
                tx.begin();
                pm.makePersistent(x);
                tx.commit();
                assertDetached(x);
                pm.setDetachAllOnCommit(false);

                // ?. pc non transactional to pc non transactional dirty
                tx.begin();
                x = (Person) pm.getObjectById(id, false);
                assertPersistent(x); // Either HOLLOW, or P_CLEAN (if cached)
                x.getFirstName();
                tx.setRetainValues(true);
                tx.commit();
                tx.setNontransactionalWrite(true);
                x.setFirstName("xx");
                assertPersistentNontransactional(x); // Would be Dirty without atomic nontx updates

                // ?. pc non transactional dirty to transient
                byte[] serialised = serialise(x);
                Object deserialised = deserialise(serialised);
                assertDetached(deserialised);

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

    // ------------------------------------- Convenience methods -------------------------------------

    private void assertTransient(Object o)
    {
        assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(o));
    }

    private void assertDetached(Object o)
    {
        assertEquals(ObjectState.DETACHED_CLEAN, JDOHelper.getObjectState(o));
    }
    
    private void assertTransientClean(Object o)
    {
        assertEquals(ObjectState.TRANSIENT_CLEAN, JDOHelper.getObjectState(o));
    }    
    
    private void assertTransientDirty(Object o)
    {
        assertEquals(ObjectState.TRANSIENT_DIRTY, JDOHelper.getObjectState(o));
    }    
    
    private void assertPersistentNew(Object o)
    {
        assertEquals(ObjectState.PERSISTENT_NEW, JDOHelper.getObjectState(o));
    }

    private void assertPersistentNontransactional(Object o)
    {
        assertEquals(ObjectState.HOLLOW_PERSISTENT_NONTRANSACTIONAL, JDOHelper.getObjectState(o));
    }

/*    private void assertPersistentNontransactionalDirty(Object o)
    {
        assertEquals(ObjectState.PERSISTENT_NONTRANSACTIONAL_DIRTY, JDOHelper.getObjectState(o));
    }*/

    private void assertPersistentClean(Object o)
    {
        assertEquals(ObjectState.PERSISTENT_CLEAN, JDOHelper.getObjectState(o));
    }

    private void assertHollowOrPersistentClean(Object o)
    {
        if (JDOHelper.getObjectState(o) != ObjectState.PERSISTENT_CLEAN && JDOHelper.getObjectState(o) != ObjectState.HOLLOW_PERSISTENT_NONTRANSACTIONAL)
        {
            throw new AssertionError("State is not P_CLEAN/HOLLOW; is " + JDOHelper.getObjectState(o));
        }
    }

    private void assertPersistentDirty(Object o)
    {
        assertEquals(ObjectState.PERSISTENT_DIRTY, JDOHelper.getObjectState(o));
    }

    private void assertHollow(Object o)
    {
        assertEquals(ObjectState.HOLLOW_PERSISTENT_NONTRANSACTIONAL, JDOHelper.getObjectState(o));
    }

    private void assertPersistent(Object o)
    {
        assertTrue("Object expected to be in a persistent state, but was " + JDOHelper.getObjectState(o),
            JDOHelper.isPersistent(o));
    }

    private void assertPersistentDeleted(Object o)
    {
        assertEquals(ObjectState.PERSISTENT_DELETED, JDOHelper.getObjectState(o));
    }

    private void assertPersistentNewDeleted(Object o)
    {
        assertEquals(ObjectState.PERSISTENT_NEW_DELETED, JDOHelper.getObjectState(o));
    }

    // ------------------------------------------- Utilities -------------------------------------------------

    public static byte[] serialise(Object obj) throws IOException
    {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(new BufferedOutputStream(byteOutStream));

        outStream.writeObject(obj);

        outStream.close();

        byte[] byteArray = byteOutStream.toByteArray();
        byteOutStream.close();
        return byteArray;
    }

    public static Object deserialise(byte[] byteArray) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(byteArray);
        ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(byteInStream));

        Object obj = inputStream.readObject();
        byteInStream.close();
        inputStream.close();
        return obj;
    }
}