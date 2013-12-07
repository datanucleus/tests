/**********************************************************************
Copyright (c) 2002 Kelly Grizzle (TJDO) and others. All rights reserved.
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
2005 Andy Jefferson - added close test
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jdo.JDOException;
import javax.jdo.JDOFatalUserException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.TestHelper;
import org.datanucleus.transaction.TransactionUtils;
import org.jpox.samples.models.company.Person;

/**
 * Tests the basic functionality of creating PersistenceManagerFactorys and setting properties on them.
 */
public class PersistenceManagerFactoryTest extends JDOPersistenceTestCase
{
    public PersistenceManagerFactoryTest(String name)
    {
        super(name);
    }

    /**
     * This class allows us to create a Properties to pass to
     * JDOHelper.getPersistenceManagerFactory() easily.
     */
    public class PMFProperties extends Properties
    {
        private boolean optimistic = false;
        private boolean retainValues = false;
        private boolean restoreValues = false;
        private boolean ignoreCache = false;
        private boolean nontransactionalRead = false;
        private boolean nontransactionalWrite = false;
        private boolean multithreaded = false;

        private String driverName = null;
        private String url = null;
        private String userName = null;
        private String factoryName = null;
        private String factory2Name = null;

        private boolean validateTables = false;
        private boolean validateConstraints = false;
        private boolean autoCreateTables = false;
        private boolean autoCreateConstraints = false;
        private int     isolationLevel = Connection.TRANSACTION_SERIALIZABLE;
      
        /**
         * Constructor.  This sets the "javax.jdo.option.PersistenceManagerFactoryClass"
         * for us.
         */
        public PMFProperties()
        {
            super();
            this.setProperty("javax.jdo.PersistenceManagerFactoryClass",
                             "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
            this.setProperty("datanucleus.autoStartMechanism", "None");
        }

        public void setOptimistic(boolean b)
        {
            this.optimistic = b;
            this.setProperty("javax.jdo.option.Optimistic", new Boolean(b).toString());
        }

        public void setRetainValues(boolean b)
        {
            this.retainValues = b;
            this.setProperty("javax.jdo.option.RetainValues", new Boolean(b).toString());
        }

        public void setRestoreValues(boolean b)
        {
            this.restoreValues = b;
            this.setProperty("javax.jdo.option.RestoreValues", new Boolean(b).toString());
        }

        public void setIgnoreCache(boolean b)
        {
            this.ignoreCache = b;
            this.setProperty("javax.jdo.option.IgnoreCache", new Boolean(b).toString());
        }

        public void setNontransactionalRead(boolean b)
        {
            this.nontransactionalRead = b;
            this.setProperty("javax.jdo.option.NontransactionalRead", new Boolean(b).toString());
        }

        public void setNontransactionalWrite(boolean b)
        {
            this.nontransactionalWrite = b;
            this.setProperty("javax.jdo.option.NontransactionalWrite", new Boolean(b).toString());
        }

        public void setMultithreaded(boolean b)
        {
            this.multithreaded = b;
            this.setProperty("javax.jdo.option.Multithreaded", new Boolean(b).toString());
        }

        public void setDriverName(String s)
        {
            this.driverName = s;
            this.setProperty("javax.jdo.option.ConnectionDriverName", s);
        }

        public void setUserName(String s)
        {
            this.userName = s;
            this.setProperty("javax.jdo.option.ConnectionUserName", s);
        }

        public void setPassword(String s)
        {
            this.setProperty("javax.jdo.option.ConnectionPassword", s);
        }

        public void setURL(String s)
        {
            this.url = s;
            this.setProperty("javax.jdo.option.ConnectionURL", s);
        }

        public void setFactoryName(String s)
        {
            this.factoryName = s;
            this.setProperty("javax.jdo.option.ConnectionFactoryName", s);
        }

        public void setFactory2Name(String s)
        {
            this.factory2Name = s;
            this.setProperty("javax.jdo.option.ConnectionFactory2Name", s);
        }

        public void setValidateTables(boolean b)
        {
            this.validateTables = b;
            this.setProperty("datanucleus.validateTables", new Boolean(b).toString());
        }

        public void setValidateConstraints(boolean b)
        {
            this.validateConstraints = b;
            this.setProperty("datanucleus.validateConstraints", new Boolean(b).toString());
        }

        public void setAutoCreateTables(boolean b)
        {
            this.autoCreateTables = b;
            this.setProperty("datanucleus.autoCreateTables", new Boolean(b).toString());
        }

        public void setAutoCreateConstraints(boolean b)
        {
            this.autoCreateConstraints = b;
            this.setProperty("datanucleus.autoCreateConstraints", new Boolean(b).toString());
        }

        public void setTransactionIsolation(int i)
        {
            this.isolationLevel = i;
            this.setProperty("datanucleus.transactionIsolation", 
                TransactionUtils.getNameForTransactionIsolationLevel(isolationLevel));
        }

        public boolean matchesPMF(PersistenceManagerFactory pmf)
        {
            if (optimistic != pmf.getOptimistic()) return false;
            if (retainValues != pmf.getRetainValues()) return false;
            if (restoreValues != pmf.getRestoreValues()) return false;
            if (ignoreCache != pmf.getIgnoreCache()) return false;
            if (nontransactionalRead != pmf.getNontransactionalRead()) return false;
            if (nontransactionalWrite != pmf.getNontransactionalWrite()) return false;
            if (multithreaded != pmf.getMultithreaded()) return false;

            if (driverName == null) { if (pmf.getConnectionDriverName() != null) return false; }
            else if (!driverName.equals(pmf.getConnectionDriverName())) return false;

            if (url == null) { if (pmf.getConnectionURL() != null) return false; }
            else if (!url.equals(pmf.getConnectionURL())) return false;
            
            if (userName == null) { if (pmf.getConnectionUserName() != null) return false; }
            else if (!userName.equals(pmf.getConnectionUserName())) return false;

            if (factoryName == null) { if (pmf.getConnectionFactoryName() != null) return false; }
            else if (!factoryName.equals(pmf.getConnectionFactoryName())) return false;

            if (factory2Name == null) { if (pmf.getConnectionFactory2Name() != null) return false; }
            else if (!factory2Name.equals(pmf.getConnectionFactory2Name())) return false;

            JDOPersistenceManagerFactory myPMF = (JDOPersistenceManagerFactory)pmf;
            if (validateTables != getConfigurationForPMF(myPMF).getBooleanProperty("datanucleus.validateTables")) return false;
            if (validateConstraints != getConfigurationForPMF(myPMF).getBooleanProperty("datanucleus.validateConstraints")) return false;
            if (autoCreateTables != getConfigurationForPMF(myPMF).getBooleanProperty("datanucleus.autoCreateTables")) return false;
            if (autoCreateConstraints != getConfigurationForPMF(myPMF).getBooleanProperty("datanucleus.autoCreateConstraints")) return false;
            int pmfIsolLevel = TransactionUtils.getTransactionIsolationLevelForName(getConfigurationForPMF(myPMF).getStringProperty("datanucleus.transactionIsolation"));
            if (isolationLevel != pmfIsolLevel) return false;

            return true;
        }
    }

    /**
     * Test instantiating a <code>PersistenceManagerFactory</code> via
     * <code>JDOHelper.getPersistenceManagerFactory(Properties)</code>.
     */
    public void testJDOHelperInstantiation()
    {
        /*
         * Things to consider:
         *  - Unknown properties should do nothing.
         *  - Setting Optimistic or RetainValues to true sets NontransactionalRead
         *     to true.
         *  - TransactionIsolation has valid values of Connection.TRANSACTION_*;
         *     anything else will throw an Exception.
         *  - A PersistenceManagerFactory obtained via JDOHelper should be
         *     nonconfigurable.
         */

        /*
         * 1) Test setting all propers to valid values.
         */
        boolean optimistic = true;
        boolean retainValues = true;
        boolean restoreValues = true;
        boolean ignoreCache = true;
        boolean nontransactionalRead = true;
        boolean nontransactionalWrite = false;
        boolean multithreaded = true;

        Properties dbProps = TestHelper.getPropertiesForDatastore(1);
        String  driverName = dbProps.getProperty("javax.jdo.option.ConnectionDriverName");
        String  url = dbProps.getProperty("javax.jdo.option.ConnectionURL");
        String  userName = dbProps.getProperty("javax.jdo.option.ConnectionUserName");
        String  password = dbProps.getProperty("javax.jdo.option.ConnectionPassword");

        boolean validateTables = true;
        boolean validateConstraints = true;
        boolean autoCreateTables = true;
        boolean autoCreateConstraints = true;
        int     transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;

        PMFProperties props = new PMFProperties();
        props.setOptimistic(optimistic);
        props.setRetainValues(retainValues);
        props.setRestoreValues(restoreValues);  // This throws an exception.  Test later.
        props.setIgnoreCache(ignoreCache);
        props.setNontransactionalRead(nontransactionalRead);
        props.setNontransactionalWrite(nontransactionalWrite);
        props.setMultithreaded(multithreaded);
        if (driverName != null)
        {
            props.setDriverName(driverName);
        }
        if (userName != null)
        {
            props.setUserName(userName);
        }
        if (password != null)
        {
            props.setPassword(password);
        }
        props.setURL(url);
        //props.setFactoryName(factory1);  // Setting this uses jndi which blows up w/out
        //props.setFactory2Name(factory2); // a managed environment (ie - no JNDI).
        props.setValidateTables(validateTables);
        props.setValidateConstraints(validateConstraints);
        props.setAutoCreateTables(autoCreateTables);
        props.setAutoCreateConstraints(autoCreateConstraints);
        props.setTransactionIsolation(transactionIsolation);

        PersistenceManagerFactory pmf =
            JDOHelper.getPersistenceManagerFactory(props);
        assertTrue("PMF should be org.datanucleus.api.jdo.JDOPersistenceManagerFactory.",
                   (pmf instanceof JDOPersistenceManagerFactory));
        assertTrue("PMF from JDOHelper doesn't match expected properties (" +
                   props.toString() + ").", props.matchesPMF(pmf));

        // Test whether NonTransactionalWrite was correctly handled 
        props.setNontransactionalWrite(true);
        try
        {
            pmf = JDOHelper.getPersistenceManagerFactory(props);
        }
        catch (Exception e)
        {
            fail("Setting of nontransactionalWrite shouldnt have caused an Exception but did : " + e.getMessage());
        }
        props.setNontransactionalWrite(false);

        /*
         * Test whether NonTransactionalRead was correctly set 
         */
        props.setNontransactionalRead(false);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("NontransactionalRead should be false",pmf.getNontransactionalRead());
        pmf.close();

        props.setNontransactionalRead(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertTrue("NontransactionalRead should be true",pmf.getNontransactionalRead());
        pmf.close();

        props.setNontransactionalRead(false);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("NontransactionalRead should be false",pmf.getNontransactionalRead());
        pmf.close();

        props.setNontransactionalRead(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertTrue("NontransactionalRead should be true",pmf.getNontransactionalRead());
        pmf.close();

        /*
         * Test whether RetainValues was correctly set 
         */
        props.setRetainValues(false);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("RetainValues should be false",pmf.getRetainValues());
        pmf.close();

        props.setRetainValues(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertTrue("RetainValues should be true",pmf.getRetainValues());
        pmf.close();

        props.setRetainValues(false);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("RetainValues should be false",pmf.getRetainValues());
        pmf.close();

        props.setRetainValues(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertTrue("RetainValues should be true",pmf.getRetainValues());
        pmf.close();
        
        /*
         * Test whether RestoreValues was correctly set 
         */
        props.setRestoreValues(false);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("RestoreValues should be false",pmf.getRestoreValues());
        pmf.close();

        props.setRestoreValues(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertTrue("RestoreValues should be true",pmf.getRestoreValues());
        pmf.close();

        props.setRestoreValues(false);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("RestoreValues should be false",pmf.getRestoreValues());
        pmf.close();

        props.setRestoreValues(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertTrue("RestoreValues should be true",pmf.getRestoreValues());
        pmf.close();
        
        /*
         * Test whether Optimistic was correctly set 
         */
        props.setOptimistic(false);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("Optimistic should be false",pmf.getOptimistic());
        pmf.close();

        props.setOptimistic(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertTrue("Optimistic should be true",pmf.getOptimistic());
        pmf.close();

        props.setOptimistic(false);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("Optimistic should be false",pmf.getOptimistic());
        pmf.close();

        props.setOptimistic(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertTrue("Optimistic should be true",pmf.getOptimistic());
        pmf.close();     

        /*
         * Test changing one property does not change other property JDO 2 ED
         * spec 5.6 NontransactionalRead, NontransactionalWrite, Optimistic, and
         * RetainValues are independent options. A JDO implementation must not
         * automatically change the values of these properties as a side effect
         * of the user changing other properties.
         */
        
        props.setNontransactionalRead(false);
        props.setRetainValues(false);
        props.setOptimistic(false);
        props.setRestoreValues(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("NontransactionalRead should be false",pmf.getNontransactionalRead());
        assertFalse("RetainValues should be false",pmf.getRetainValues());
        assertFalse("Optimistic should be false",pmf.getOptimistic());
        assertTrue("RestoreValues should be true",pmf.getRestoreValues());
        pmf.close();
        
        props.setRestoreValues(false);
        props.setNontransactionalRead(false);
        props.setRetainValues(false);
        props.setOptimistic(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("RestoreValues should be false",pmf.getRestoreValues());
        assertFalse("NontransactionalRead should be false",pmf.getNontransactionalRead());
        assertFalse("RetainValues should be false",pmf.getRetainValues());
        assertTrue("Optimistic should be true",pmf.getOptimistic());
        pmf.close();

        props.setOptimistic(false);
        props.setRestoreValues(false);
        props.setNontransactionalRead(false);
        props.setRetainValues(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("Optimistic should be false",pmf.getOptimistic());
        assertFalse("RestoreValues should be false",pmf.getRestoreValues());
        assertFalse("NontransactionalRead should be false",pmf.getNontransactionalRead());
        assertTrue("RetainValues should be true",pmf.getRetainValues());
        pmf.close();
        
        props.setRetainValues(false);
        props.setOptimistic(false);
        props.setRestoreValues(false);
        props.setNontransactionalRead(true);
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertFalse("RetainValues should be false",pmf.getRetainValues());
        assertFalse("Optimistic should be false",pmf.getOptimistic());
        assertFalse("RestoreValues should be false",pmf.getRestoreValues());
        assertTrue("NontransactionalRead should be true",pmf.getNontransactionalRead());
        pmf.close();
        
        /*
         * 2) Test that an invalid driver name throws an exception
         */
        if (vendorID != null)
        {
            props.setDriverName("my.test.driver.should.not.exist");
            try
            {
                pmf = JDOHelper.getPersistenceManagerFactory(props);
                assertTrue("Invalid driver name should have thrown exception.", false);
                pmf.close();
            }
            catch (Exception e) { /* Ignore */ }
        }

        /*
         * 3) Test that setting an invalid transaction isolation throws an exception.
         */
        if (vendorID != null)
        {
            props.setDriverName(driverName);
            props.setTransactionIsolation(8738);
            try
            {
                pmf = JDOHelper.getPersistenceManagerFactory(props);
                assertTrue("Setting invalid transaction isolation should have thrown exception.", false);
                pmf.close();
            }
            catch (Exception e) { /* Ignore */ }
        }

        /*
         * 4) Test that PMF is not configurable after getting an instance.
         */
        try
        {
            if (vendorID != null)
            {
                props.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            }
            pmf = JDOHelper.getPersistenceManagerFactory(props);
            assertTrue("PMF from JDOHelper doesn't match expected properties (" +
                       props.toString() + ").", props.matchesPMF(pmf));

            pmf.setConnectionUserName("bobjones");
            assertTrue("Setting properties after frozen should throw exception.", false);
            pmf.close();
        }
        catch (JDOUserException e) { /* Ignore */ }


        /*
         * 5) Test return data from getProperties
         */
        Properties pmf_props=pmf.getProperties();
        String vendor=pmf_props.getProperty("VendorName");
        if (vendor == null)
        {
            assertTrue("PMF doesnt return VendorName property",false);
        }
        String version=pmf_props.getProperty("VersionNumber");
        if (version == null)
        {
            assertTrue("PMF doesnt return VersionNumber property",false);
        }

        /*
         * 6) Test that setting an unknown property does nothing weird.
         */
        props.setProperty("datanucleus.MyTestUnknownProperty", "unknown");
        pmf = JDOHelper.getPersistenceManagerFactory(props);
        assertTrue("PMF from JDOHelper doesn't match expected properties (" +
                   props.toString() + ").", props.matchesPMF(pmf));
        pmf.close();
    }

    /**
     * Test of the closure behaviour of the PMF.
     */
    public void testClose()
    {
        // Create 10 to give a reasonable chance that the active tx ones aren't "closed" first internally
        PersistenceManager[] pms = new PersistenceManager[10];
        for (int i = 0; i < 10; ++i)
        {
            pms[i] = pmf.getPersistenceManager(); 
        }

        // Open and close a transaction on an arbitrary PM
        pms[3].currentTransaction().begin(); 
        pms[3].currentTransaction().commit(); 

        // Open (and leave active) a transaction on 2 arbitrary PMs
        pms[5].currentTransaction().begin();
        pms[7].currentTransaction().begin(); 

        // try to close PMF while has open tx
        boolean success = false;
        try
        {
            pmf.close();
        }
        catch (JDOException ex)
        {
            Throwable[] nested = ex.getNestedExceptions();
            assertNotNull("Expected nested exceptions", nested);
            assertEquals("Expected exactly 2 nested exceptions", 2, nested.length);

            for (PersistenceManager pm : pms)
            {
                assertFalse("None of the PMs should have been closed", pm.isClosed());
            }

            success = true;
            // Clean up the txns, PMs, and PMF
            pms[5].currentTransaction().commit();
            pms[7].currentTransaction().commit();
            pmf.close();
        }
        finally
        {
            getPMF();
        }

        pmf.getPersistenceManager();
        PersistenceManager pm3 = pmf.getPersistenceManager();
        pm3.currentTransaction().begin();
        pm3.currentTransaction().commit();
        success = false;
        try
        {
            pmf.close();
            success = true;
        }
        finally
        {
            getPMF();
        }        
        assertTrue("should not have raised an exception", success);

        // test close twice
        pmf.close();
        try
        {
            pmf.close();
        }
        catch(JDOUserException e)
        {
            // JDO2.3 spec 11.4 allows redundant calls to close().
            fail("should not have raised an exception when closing a closed PMF");
        }
        finally
        {
            getPMF();
        }

        // Try to access a PM from the closed PMF
        pmf.close();
        try
        {
            pmf.getPersistenceManager();
            fail("should have raised an exception when accessing PM from a closed PMF");
        }
        catch (JDOUserException ue)
        {
            // Expected, as per section 11.4 of the JDO2 spec
        }
        catch (Exception e)
        {
            LOG.error("Incorrect exception : expected JDOUserException ", e);
            fail("wrong type of exception thrown when accessing PM from closed PMF " + e.getClass().getName());
        }
        finally
        {
            getPMF();
        }
    }

    /**
     * Test for serverTimeZoneID setting.
     */
    public void testServerTimeZoneID()
    {
        PersistenceManagerFactory pmf = null;
        // Try setting the serverTimeZoneID to an invalid value
        try
        {
            Properties userProps = new Properties();
            userProps.setProperty("javax.jdo.option.ServerTimeZoneID", "JPOX_CENTRAL_TIMEZONE");
            pmf = TestHelper.getPMF(1, userProps);
            fail("Expected a JDOUserException when setting the ServerTimeZoneID to an invalid value but worked!");
        }
        catch (JDOFatalUserException jdoe)
        {
            // Expected
        }
        finally
        {
            if (pmf != null && !pmf.isClosed())
            {
                pmf.close();
            }
        }

        // Try setting it to a valid value and make sure it is retained
        try
        {
            Properties userProps = new Properties();
            userProps.setProperty("javax.jdo.option.ServerTimeZoneID", "UTC");
            pmf = TestHelper.getPMF(1, userProps);
            assertEquals("ServerTimeZoneID was not set correctly", ((JDOPersistenceManagerFactory)pmf).getServerTimeZoneID(), "UTC");
        }
        catch (Exception e)
        {
            fail("Exception thrown when setting the ServerTimeZoneID to UTC");
        }
        finally
        {
            if (pmf != null && !pmf.isClosed())
            {
                pmf.close();
            }
        }

        // Try leaving it unset and check the value
        try
        {
            pmf = TestHelper.getPMF(1, null);
            assertEquals("ServerTimeZoneID was not set correctly", ((JDOPersistenceManagerFactory)pmf).getServerTimeZoneID(), null);
        }
        catch (Exception e)
        {
            fail("Exception thrown when setting the ServerTimeZoneID to UTC");
        }
        finally
        {
            if (pmf != null && !pmf.isClosed())
            {
                pmf.close();
            }
        }
    }
    
    /**
     * Test setting a null for for a property value but string was expected 
     */
    public void testNullProperties()
    {
        Properties props = TestHelper.getPropertiesForDatastore(1);
        Map map = new HashMap();
        map.putAll(props);
        map.put("javax.jdo.option.Mapping", null);
        assertNotNull(JDOHelper.getPersistenceManagerFactory(map));
    }

    /**
     * Test setting an object for a property value but string was expected 
     */
    public void testObjectForStringProperty()
    {
        Properties props = TestHelper.getPropertiesForDatastore(1);
        Map map = new HashMap();
        map.putAll(props);
        map.put("javax.jdo.option.Mapping", new Object());
        try
        {
            JDOHelper.getPersistenceManagerFactory(map);
            fail("Expected JDOFatalInternalException");
        }
        catch (JDOFatalUserException ex)
        {
            assertEquals(IllegalArgumentException.class, ex.getNestedExceptions()[0].getCause().getClass());
        }
        catch (Exception e)
        {
            fail("Incorrect exception thrown : expected JDOFatalUserException but got " + e.getClass().getName());
        }
    }

    public void testSerialize()
    {
        try
        {
            Object id = null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                pm.makePersistent(p);
                tx.commit();
                id = pm.getObjectId(p);
            }
            catch (Exception e)
            {
                LOG.error("Exception persisting object", e);
                fail("Exception persisting object : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Serialize the current PMF
            byte[] bytes = null;
            try
            {
                bytes = TestHelper.serializeObject(pmf);
            }
            catch (RuntimeException re)
            {
                LOG.error("Error serializing PMF", re);
                fail("Error in serialization : " + re.getMessage());
            }

            // Deserialise the PMF
            JDOPersistenceManagerFactory pmf = null;
            try
            {
                pmf = (JDOPersistenceManagerFactory) TestHelper.deserializeObject(bytes);
            }
            catch (RuntimeException re)
            {
                LOG.error("Error deserializing PMF", re);
                fail("Error in deserialization : " + re.getMessage());
            }

            assertNotNull(pmf);
            assertNotNull(pmf.getNucleusContext());

            // Retrieve the object using the deserialised PM(F)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p = (Person)pm.getObjectById(id);
                assertNotNull(p);
                assertEquals("Fred", p.getFirstName());
                assertEquals("Flintstone", p.getLastName());
                assertEquals("fred.flintstone@warnerbros.com", p.getEmailAddress());

                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@warnerbros.com");
                pm.makePersistent(p2);
                pm.flush();

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception retrieving object with deserialised PMF", e);
                fail("Exception retrieving object with deserialised PMF: " + e.getMessage());
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
}