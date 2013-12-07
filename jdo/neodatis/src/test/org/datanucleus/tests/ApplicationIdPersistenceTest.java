/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.neodatis.appid.Sample1;
import org.jpox.samples.one_one.bidir.Boiler;
import org.jpox.samples.one_one.bidir.Timer;
import org.neodatis.odb.OdbConfiguration;

/**
 * Tests for basic persistence to NeoDatis using application identity.
 **/
public class ApplicationIdPersistenceTest extends JDOPersistenceTestCase
{
    public ApplicationIdPersistenceTest(String name)
    {
        super(name);
        OdbConfiguration.setLogServerStartupAndShutdown(false);
    }

    /**
     * Test of persistence of more than 1 app id objects with the same "id".
     */
    public void testPersistDuplicates()
    {
        try
        {
            // Persist an object with id "101"
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Sample1 s1 = new Sample1(101, "First Sample");
                pm.makePersistent(s1);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data to NeoDatis " + e.getMessage());
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

                Sample1 s2 = new Sample1(101, "Second Sample");
                pm.makePersistent(s2);

                tx.commit();
                fail("Was allowed to persist two application-identity objects with the same identity");
            }
            catch (Exception e)
            {
                // Expected
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
            clean(Sample1.class);
        }
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
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Baxi", "Superwarm");
                pm.makePersistent(boiler);

                tx.commit();
                id = pm.getObjectId(boiler);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data to NeoDatis " + e.getMessage());
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
                Boiler boiler = (Boiler)pm.getObjectById(id);
                assertEquals("Make of Boiler retrieved is incorrect", "Baxi", boiler.getMake());
                assertEquals("Model of Boiler retrieved is incorrect", "Superwarm", boiler.getModel());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data from NeoDatis " + e.getMessage());
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
            clean(Boiler.class);
        }
    }

    /**
     * Test of persist of 1-1 relation (PC field).
     */
    public void testPersistOneToOne()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object boilerId = null;
            Object timerId = null;
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Baxi", "Maxwarm");
                Timer timer = new Timer("Seiko", true, boiler);
                boiler.setTimer(timer);
                pm.makePersistent(boiler);

                tx.commit();
                boilerId = pm.getObjectId(boiler);
                timerId = pm.getObjectId(timer);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data to NeoDatis " + e.getMessage());
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
                Boiler boiler = (Boiler)pm.getObjectById(boilerId);
                Timer timer = (Timer)pm.getObjectById(timerId);
                assertEquals("Make of Boiler retrieved is incorrect", "Baxi", boiler.getMake());
                assertEquals("Model of Boiler retrieved is incorrect", "Maxwarm", boiler.getModel());
                assertEquals("Model of Timer retrieved is incorrect", "Seiko", timer.getMake());
                assertEquals("Digital flag of Timer retrieved is incorrect", true, timer.isDigital());
                assertEquals("Timer of Boiler retrieved is incorrect", timer, boiler.getTimer());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data from NeoDatis " + e.getMessage());
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
            clean(Boiler.class);
            clean(Timer.class);
        }
    }
}