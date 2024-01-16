/**********************************************************************
 Copyright (c) 2018 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.lockmanager;

import org.datanucleus.PropertyNames;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.samples.annotations.models.company.Project;
import org.datanucleus.state.LockManager;
import org.datanucleus.state.LockManagerImpl;
import org.datanucleus.tests.JDOPersistenceTestCase;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import java.util.Properties;

public class LockManagerExtensionTest extends JDOPersistenceTestCase
{
    /**
     * Used by the JUnit framework to construct tests.
     *
     * @param name Name of the <i>TestCase</i>.
     */
    public LockManagerExtensionTest(String name)
    {
        super(name);
    }

    /**
     * Test getting default lock manager
     */
    public void testGetDefaultLockManager()
    {
        try (PersistenceManager pm = pmf.getPersistenceManager())
        {
            final LockManager lockManager = ((JDOPersistenceManager) pm).getExecutionContext().getLockManager();
            assertEquals("Correct default lock manager", LockManagerImpl.class.getName(), lockManager.getClass().getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing lock-manager test : " + e.getMessage());
        }
    }

    /**
     * Test getting custom lock manager
     */
    public void testGetCustomLockManager()
    {
        Properties userProps = new Properties();
        userProps.setProperty(PropertyNames.PROPERTY_LOCKMANAGER_TYPE, "testlockmanager");
        PersistenceManagerFactory pmfForTest = getPMF(1, userProps);

        try (PersistenceManager pm = pmfForTest.getPersistenceManager())
        {
            final LockManager lockManager = ((JDOPersistenceManager) pm).getExecutionContext().getLockManager();
            assertEquals("Correct default lock manager", TestLockManagerImpl.class.getName(), lockManager.getClass().getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing lock-manager test : " + e.getMessage());
        }
    }

    /**
     * Test getting not found custom lock manager name
     */
    public void testGetNotFoundLockManagerName()
    {
        Properties userProps = new Properties();
        userProps.setProperty(PropertyNames.PROPERTY_LOCKMANAGER_TYPE, "notfoundinpluginxml");
        PersistenceManagerFactory pmfForTest = getPMF(1, userProps);

        try (PersistenceManager pm = pmfForTest.getPersistenceManager())
        {
            final LockManager lockManager = ((JDOPersistenceManager) pm).getExecutionContext().getLockManager();
            fail("Should not be able to get to here - specified non existing named lock manager: "+lockManager);
        }
        catch (NucleusUserException e)
        {
            // This is the expected result
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing lock-manager test : " + e.getMessage());
        }
    }

    /**
     * Test getting not found custom lock manager class
     */
    public void testGetNotFoundLockManagerClass()
    {
        Properties userProps = new Properties();
        userProps.setProperty(PropertyNames.PROPERTY_LOCKMANAGER_TYPE, "notfoundtestlockmanager");
        PersistenceManagerFactory pmfForTest = getPMF(1, userProps);

        try (PersistenceManager pm = pmfForTest.getPersistenceManager())
        {
            final LockManager lockManager = ((JDOPersistenceManager) pm).getExecutionContext().getLockManager();
            fail("Should not be able to get to here - specified non existing lock manager class: "+lockManager);
        }
        catch (NucleusUserException e)
        {
            // This is the expected result
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing lock-manager test : " + e.getMessage());
        }
    }

    /**
     * Test max value on optimistic version meta-data when using version-number strategy
     */
    public void testMaxValueOnVersionMetaData()
    {
        Properties userProps = new Properties();
        userProps.setProperty(PropertyNames.PROPERTY_LOCKMANAGER_TYPE, "testlockmanager");
        userProps.put("javax.jdo.option.Optimistic", "true");
        PersistenceManagerFactory pmfForTest = getPMF(1, userProps);

        try
        {
            PersistenceManager pm = pmfForTest.getPersistenceManager();
            final Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                int no = 0;
                final Project project = new Project("AcmeXYZ", no);
                pm.makePersistent(project);
                tx.commit();
                tx.begin();

                for (; no < 10010; no++)
                {
                    if (no < 10000)
                    {
                        assertEquals("Version no not increased", no, ((Number) JDOHelper.getVersion(project)).longValue());
                    }

                    project.setBudget(no);
                    tx.commit();
                    tx.begin();
                }

                assertEquals("Version no wrapped as expected", 10, ((Number) JDOHelper.getVersion(project)).longValue());


                pm.deletePersistent(project);
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
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing lock-manager test : " + e.getMessage());
        }
        finally
        {
            // Clean out our data
            clean(pmfForTest, Project.class);
        }
    }
}
