/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
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

import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.validation.ConstraintViolationException;

import org.datanucleus.samples.validation.ValidatedPerson;
import org.datanucleus.samples.validation.ValidatedPerson2;
import org.datanucleus.samples.validation.ValidatedPerson3;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.TestHelper;

/**
 * Tests for javax.validation with JDO.
 */
public class ValidationTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor.
     * @param name Name of the test (not used)
     */
    public ValidationTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    ValidatedPerson.class
                });
            initialised = true;
        }
    }

    /**
     * Test for not null annotation.
     */
    public void testNotNull()
    {
        Properties userProps = new Properties();
        userProps.setProperty("datanucleus.validation.mode", "auto");
        PersistenceManagerFactory validationPMF = TestHelper.getPMF(1, userProps);

        try
        {
            PersistenceManager pm = validationPMF.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                ValidatedPerson p1 = new ValidatedPerson("John", null);
                pm.makePersistent(p1);
                tx.commit();
                fail("Should have thrown validation exception on persist but didnt");
            }
            catch (ConstraintViolationException cve)
            {
                // expected
                LOG.info("Exception correctly thrown : " + cve.getMessage());
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
        }
    }

    /**
     * Test for @Size annotation.
     */
    public void testSize()
    {
        Properties userProps = new Properties();
        userProps.setProperty("datanucleus.validation.mode", "auto");
        PersistenceManagerFactory validationPMF = TestHelper.getPMF(1, userProps);

        try
        {
            PersistenceManager pm = validationPMF.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                ValidatedPerson p1 = new ValidatedPerson("John", "Smith");
                p1.setLogin("12345678901");
                LOG.info(">> Calling persist");
                pm.makePersistent(p1);
                tx.commit();
                fail("Should have thrown validation exception on persist but didnt");
            }
            catch (ConstraintViolationException cve)
            {
                // expected
                LOG.info("Exception correctly thrown : " + cve.getMessage());
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
        }
    }

    /**
     * Test for not null annotation on a non-persistent field.
     */
    public void testNonPersistentNotNull()
    {
        Properties userProps = new Properties();
        userProps.setProperty("datanucleus.validation.mode", "auto");
        PersistenceManagerFactory validationPMF = TestHelper.getPMF(1, userProps);

        try
        {
            PersistenceManager pm = validationPMF.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist with null password
                tx.begin();
                ValidatedPerson2 p1 = new ValidatedPerson2("John", "Smith");
                pm.makePersistent(p1);
                tx.commit();
                fail("Exception should have been thrown, but persisted with null non-persistent field");
            }
            catch (ConstraintViolationException cve)
            {
                LOG.error("Exception thrown as expected", cve);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = validationPMF.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                ValidatedPerson2 p1 = new ValidatedPerson2("John", "Smith");
                p1.setPassword("secret");
                pm.makePersistent(p1);
                tx.commit();
            }
            catch (ConstraintViolationException cve)
            {
                LOG.error("Exception thrown", cve);
                fail("Exception thrown but shouldn't have been");
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
            clean(ValidatedPerson2.class);
        }
    }

    /**
     * Test for not null annotation on a transactional field.
     */
    public void testTransactionalNotNull()
    {
        Properties userProps = new Properties();
        userProps.setProperty("datanucleus.validation.mode", "auto");
        PersistenceManagerFactory validationPMF = TestHelper.getPMF(1, userProps);

        try
        {
            PersistenceManager pm = validationPMF.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist with null password
                tx.begin();
                ValidatedPerson3 p1 = new ValidatedPerson3("John", "Smith");
                pm.makePersistent(p1);
                tx.commit();
                fail("Exception should have been thrown, but persisted with null transactional field");
            }
            catch (ConstraintViolationException cve)
            {
                LOG.error("Exception thrown as expected", cve);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = validationPMF.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                ValidatedPerson3 p1 = new ValidatedPerson3("John", "Smith");
                p1.setPassword("secret");
                pm.makePersistent(p1);
                tx.commit();
            }
            catch (ConstraintViolationException cve)
            {
                LOG.error("Exception thrown", cve);
                fail("Exception thrown but shouldn't have been");
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
            clean(ValidatedPerson3.class);
        }
    }
}