/******************************************************************
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

Contributors:
    ...
 *****************************************************************/
package org.datanucleus.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.StringUtils;

/**
 * Utility class for assisting in the process of generating PMFs for the datastores used
 * by the unit tests suite. A PMF can be created at any time by calling <B>getPMF</B>
 * with the number of the datastore (corresponds to the System property
 * "org.datanucleus.test.properties.{number}") and any "user properties" to be used on that PMF.
 * This means that a user can create a PMF with the base properties for the datastore
 * (e.g number 1) and add on their own fine tuning properties, e.g turning on L2 cache.
 */
public class TestHelper
{
    /** Log for unit testing. */
    public static final NucleusLogger LOG = NucleusLogger.getLoggerInstance("DataNucleus.Test");

    /**
     * Method to return a PMF for the specified datastore number, adding on the user-provided
     * properties. The returned factory can be further configured.
     * @param number Number of the datastore (equates to a property file in the CLASSPATH)
     * @param userProps The user properties (null if not required)
     * @return The PMF
     */
    public static PersistenceManagerFactory getConfigurablePMF(int number, Properties userProps)
    {
        return new JDOPersistenceManagerFactory(getFactoryProperties(number, userProps));
    }

    /**
     * Method to return a PMF for the specified datastore number, adding on the user-provided
     * properties
     * @param number Number of the datastore (equates to a property file in the CLASSPATH)
     * @param userProps The user properties (null if not required)
     * @return The PMF
     */
    public static PersistenceManagerFactory getPMF(int number, Properties userProps)
    {
        return JDOHelper.getPersistenceManagerFactory(getFactoryProperties(number, userProps));
    }

    /**
     * Method to return a EMF for the specified datastore number, adding on the user-provided
     * properties
     * @param number Number of the datastore (equates to a property file in the CLASSPATH)
     * @param unitName Name of the persistence-unit to use (if any - defaults to TEST)
     * @param userProps The user properties (null if not required)
     * @return The EMF
     */
    public static EntityManagerFactory getEMF(int number, String unitName, Properties userProps)
    {
        if (unitName == null)
        {
            unitName = "TEST";
        }
        return javax.persistence.Persistence.createEntityManagerFactory(unitName,
            getFactoryProperties(number, userProps));
    }

    /**
     * Method to freeze the PMF so that it can't be changed.
     * @param pmf The PMF
     */
    public static void freezePMF(PersistenceManagerFactory pmf)
    {
        pmf.getDataStoreCache(); // Freezes the PMF configuration
    }

    /**
     * Method to return the properties for a particular datastore. Tries the following
     * in this order
     * <ul>
     * <li>File "datanucleus.test{num}.override.properties"</li>
     * <li>System property "datanucleus.test{num}.properties" defining a file</li>
     * <li>System property "datanucleus.test{num}.classpath.properties" defining a CLASSPATH location</li>
     * <li>"$HOME/datanucleus.properties"</li>
     * </ul>
     * @param number Number of the datastore (1, 2, ...)
     * @return Properties for this datastore
     */
    public static Properties getPropertiesForDatastore(int number)
    {
        // Try "datanucleus.test{num}.override.properties"
        String filename = "datanucleus.test" + number + ".override.properties";
        InputStream in = TestHelper.class.getClassLoader().getResourceAsStream(filename);
        if (in == null)
        {
            LOG.debug("Could not obtain PMF/EMF properties using " + filename);

            // Try "datanucleus.test{num}.properties"
            filename = System.getProperty("datanucleus.test" + number + ".properties");
            if (filename != null)
            {
                in = getResourceAsStream(filename);
            }
            if (in == null)
            {
                // Try "datanucleus.test{num}.classpath.properties"
                filename = System.getProperty("datanucleus.test" + number + ".classpath.properties");
                if (filename != null)
                {
                    in = getResourceAsStream(filename);
                }
                if (in == null)
                {
                    // Try ${home}/datanucleus.properties (for IDE tests)
                    filename = System.getProperty("user.home") + "/datanucleus.properties";
                    File file = new File(filename);
                    if (file.exists())
                    {
                        try
                        {
                            in = new FileInputStream(file);
                            LOG.info("PMF/EMF properties obtained using " + filename);
                        }
                        catch (FileNotFoundException e)
                        {
                        }
                    }
                    if (in == null)
                    {
                        LOG.debug("PMF/EMF properties couldn't be obtained using " + filename);
                        System.out.println("PMF could not be created using supported property file locations");
                        filename = "datanucleus-hsql." + number + ".properties";
                        in = getResourceAsStream(filename);
                        if (in != null)
                        {
                            System.out.println("Defaulting to " + filename);
                        }
                        else
                        {
                            System.out.println("PMF could not be created using default property file location " + filename);
                            System.exit(2);
                        }
                    }
                }
            }
        }
        else
        {
            LOG.info("PMF/EMF properties obtained using " + filename);
        }

        Properties props = new Properties();
        try
        {
            // Read all properties for the file
            props.load(in);
        }
        catch (IOException ioe)
        {
            LOG.error("Exception thrown reading properties file : " + ioe.getMessage());
            System.out.println("Exception thrown reading properties file : " + ioe.getMessage());
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException ioe)
                {
                }
            }
        }
        return props;
    }

    private static InputStream getResourceAsStream(String filename)
    {
        InputStream in;
        in = TestHelper.class.getClassLoader().getResourceAsStream(filename);
        if (in != null)
        {
            LOG.info("PMF/EMF created using " + filename);
        }
        else
        {
            LOG.debug("PMF/EMF couldn't be created using " + filename);
        }
        return in;
    }

    /**
     * Convenience method to remove all objects of the passed class in JDO.
     * @param pmf PersistenceManagerFactory
     * @param cls The class
     */
    @SuppressWarnings("unchecked")
    public static void clean(PersistenceManagerFactory pmf, Class cls)
    {
        javax.jdo.PersistenceManager pm = pmf.getPersistenceManager();
        javax.jdo.Transaction tx = pm.currentTransaction();
        tx.setOptimistic(false);

        try
        {
            // delete all objects of this class (and subclasses)
            tx.begin();

            Extent ex = pm.getExtent(cls, true);
            Iterator iter = ex.iterator();
            Collection coll = new HashSet();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                LOG.info("Cleanup object to delete=" + StringUtils.toJVMIDString(obj) +
                        " state=" + JDOHelper.getObjectState(obj));
                coll.add(obj);
            }
            LOG.debug("Cleanup : Number of objects of type " + cls.getName() + " to delete is " + coll.size());
            pm.deletePersistentAll(coll);
            LOG.debug("Cleanup : Number of objects deleted is " + coll.size());

            tx.commit();
        }
        catch (RuntimeException e)
        {
            LOG.error("Exception in clean", e);
            throw e;
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

    /**
     * Convenience method to remove all objects of the passed class in JPA.
     * @param emf EntityManagerFactory
     * @param cls The class
     */
    public static void clean(EntityManagerFactory emf, Class cls)
    {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            List result = em.createQuery("SELECT Object(T) FROM " + cls.getName() + " T").getResultList();
            LOG.debug("Cleanup : Number of objects of type " + cls.getName() + " to delete is " + result.size());
            for (int i = 0; i < result.size(); i++)
            {
                em.remove(result.get(i));
            }
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Utility to return the JDBC vendor for the datastore number specified.
     * This is the name after "jdbc:" in the URL. e.g "mysql"
     * @param number Number of datastore
     * @return The JDBC subprotocol
     */
    public static String getJDBCSubprotocolForDatastore(int number)
    {
        Properties props = getPropertiesForDatastore(number);
        String url = props.getProperty("javax.jdo.option.ConnectionURL");
        if (url == null)
        {
            return null;
        }
        StringTokenizer tokeniser = new StringTokenizer(url, ":");
        String pluginProtocol = tokeniser.nextToken();
        if (!pluginProtocol.equals("jdbc"))
        {
            return null;
        }
        return tokeniser.nextToken();
    }

    public static String getDatastorePluginProtocol(int number)
    {
        Properties props = getPropertiesForDatastore(number);
        String url = props.getProperty("javax.jdo.option.ConnectionURL");
        if (url == null)
        {
            return null;
        }
        StringTokenizer tokeniser = new StringTokenizer(url, ":");
        return tokeniser.nextToken();
    }

    private static Properties getFactoryProperties(int number, Properties userProps)
    {
        // Retrieve the default properties for this datastore
        Properties props = getPropertiesForDatastore(number);

        // Merge additional configuration
        Properties configProps = getConfigProperties(System.getProperty("datanucleus.test.config"));

        if (configProps != null)
        {
            props.putAll(configProps);
        }

        if (userProps != null)
        {
            // Add on the user properties to the default props for this datastore number
            Iterator userPropsIter = userProps.entrySet().iterator();
            while (userPropsIter.hasNext())
            {
                Map.Entry entry = (Map.Entry) userPropsIter.next();
                props.put(entry.getKey(), entry.getValue());
            }
        }
        return props;
    }

    private static Properties getConfigProperties(String config)
    {
        Properties props = new Properties();

        String filename = config + "-conf.properties";
        if (config != null)
        {
            InputStream in = getResourceAsStream(filename);

            if (in == null)
            {
                throw new RuntimeException("Could not find file " + filename);
            }

            try
            {
                // Read all properties for the file
                props.load(in);
            }
            catch (IOException ioe)
            {
                LOG.error("Exception thrown reading properties file : " + ioe.getMessage());
                System.out.println("Exception thrown reading properties file : " + ioe.getMessage());
            }
        }

        return props;
    }

    public static byte[] serializeObject(Object obj)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return baos.toByteArray();
        }
        catch (Throwable t)
        {
            throw new RuntimeException("serializeObject threw " + t, t);
        }
    }

    public static Object deserializeObject(byte[] bytes)
    {
        ObjectInputStream ois = null;
        try
        {
            ByteArrayInputStream bios = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bios);
            return ois.readObject();
        }
        catch (Throwable t)
        {
            throw new RuntimeException("deserializeObject threw " + t, t);
        }
    }
}