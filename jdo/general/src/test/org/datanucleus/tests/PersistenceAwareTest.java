/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.persistenceaware.AccessPublicFields;
import org.datanucleus.samples.persistenceaware.PublicFields;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests of persistence-aware capabilities.
 *
 * @version $Revision: 1.4 $
 */
public class PersistenceAwareTest extends JDOPersistenceTestCase
{
    /**
     * @param name
     */
    public PersistenceAwareTest(String name)
    {
        super(name);
    }

    /**
     * Tests wether persistent public fields accessed from another
     * class than the owning class are managed, that is if the
     * accessing class is enhanced correctly.
     */
    public void testPersistenceAware()
    {
        Object idAlpha = null;
        Object idBeta = null;
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PublicFields pfAlpha = new PublicFields();
                PublicFields pfBeta = new PublicFields();
                AccessPublicFields.setStringField(pfAlpha, "alpha");
                AccessPublicFields.setIntField(pfAlpha, 1);
                AccessPublicFields.setObjectField(pfAlpha, pfBeta);
                AccessPublicFields.setStringField(pfBeta, "beta");
                AccessPublicFields.setIntField(pfBeta, 2);
                pm.makePersistent(pfAlpha);
                idAlpha = JDOHelper.getObjectId(pfAlpha);
                idBeta = JDOHelper.getObjectId(pfBeta);
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
                PublicFields checkAlpha = (PublicFields)pm.getObjectById(idAlpha, true);
                PublicFields checkBeta = (PublicFields)pm.getObjectById(idBeta, true);
                assertEquals("alpha", AccessPublicFields.getStringField(checkAlpha));
                assertEquals(1, AccessPublicFields.getIntField(checkAlpha));
                assertTrue(checkBeta.equals(AccessPublicFields.getObjectField(checkAlpha)));
                assertEquals("beta", AccessPublicFields.getStringField(checkBeta));
                assertEquals(2, AccessPublicFields.getIntField(checkBeta));
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
            // Disconnect the objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PublicFields alpha = (PublicFields)pm.getObjectById(idAlpha);
                AccessPublicFields.setObjectField(alpha, null);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            clean(PublicFields.class);
        }
    }
}