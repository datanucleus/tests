/******************************************************************
 Copyright (c) 2006 Erik Bengtson and others. All rights reserved. 
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.jdo.spi.JDOImplHelper;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.exceptions.ClassNotPersistenceCapableException;
import org.datanucleus.api.jdo.exceptions.NoPersistenceInformationException;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.types.TypeManager;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.persistentinterfaces.ICity;

/**
 * Tests for ClassLoading behaviour. These tests requires derby, log4j, enhancer, bcel, and other 
 * basic dependencies to be executed
 */
public class ClassLoaderTest extends JDOPersistenceTestCase
{
    URL urlToTestClassFolder = getURLForTestClassFolder();
    URL dnCoreJarURL = getURLForCoreFolder();
    URL jdoJarURL = getURLForJDOJar();
    URL testFrameworkJarURL = getURLForTestFrameworkJar();
    URL enhancerJarURL = getURLForEnhancerJar();
    URL jUnitJarURL = getURLForJunitJar();
    URL log4jURL = getURLForLog4JJar();
    URL derby = getURLForDerby();
    URL asmURL = getURLForASM();

    static 
    {
        initOnCreate = false;
    }
    public ClassLoaderTest()
    {
        super("unset");
    }

    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the <tt>TestCase</tt>.
     */
    public ClassLoaderTest(String name)
    {
        super(name);
    }
    
    /**
     * Test classpath in context loader two classpaths here 
     * 1st classloader contains Test Case classes, DN, JDO, LOG4J
     * 2nd classloader contains org.jpox.sample classes, but not JDO files. Delegates to 1st classloader for JDO, DN and other libraries
     * @throws Throwable
     */
    public void testNewInstance() throws Throwable
    {
        if (!"derby".equals(vendorID))
        {
            return;
        }

        ClassLoader loader = new URLClassLoaderRestrictive(new URL[]{dnCoreJarURL, testFrameworkJarURL, 
                urlToTestClassFolder, jdoJarURL, jUnitJarURL, log4jURL, derby, enhancerJarURL, asmURL},null);

        ClassLoader l = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(loader);

        Class cls = Class.forName(ClassLoaderTest.class.getName(), true, loader);
        try
        {
            cls.getMethod("runNewInstance").invoke(cls.newInstance());
        }
        catch (InvocationTargetException e)
        {
            throw e;
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(l);
        }

    }
    
    /**
     * Test classpath in context loader two classpaths here 
     * 1st classloader contains Test Case classes, DN, JDO, LOG4J
     * 2nd classloader contains org.jpox.sample classes. Delegates to 1st classloader for JDO, DN and other libraries
     * @throws Throwable
     */
    public void testContextClassLoader() throws Throwable
    {
        if (!"derby".equals(vendorID))
        {
            return;
        }

        ClassLoader loader = new URLClassLoaderRestrictive(new URL[] {dnCoreJarURL, testFrameworkJarURL, 
                urlToTestClassFolder, jdoJarURL, jUnitJarURL, log4jURL, derby},null);
        ClassLoader l = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(loader);

        Class cls = Class.forName(ClassLoaderTest.class.getName(), true, loader);
        try
        {
            cls.getMethod("runPersistAndDeletePerson").invoke(cls.newInstance());
        }
        catch (InvocationTargetException e)
        {
            throw e.getTargetException();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(l);
        }

    }
    
    /**
     * Test classpath in context loader two classpaths here 
     * 1st classloader contains Test Case classes, DN, JDO, LOG4J
     * 2nd classloader contains org.jpox.sample classes, but not JDO files. Delegates to 1st classloader for JDO, DN and other libraries
     * @throws Throwable
     */
    public void testNegativeContextClassLoader() throws Throwable
    {
        if (!"derby".equals(vendorID))
        {
            return;
        }

        @SuppressWarnings("resource")
        ClassLoader loader = new URLClassLoaderRestrictive(new URL[]{dnCoreJarURL, testFrameworkJarURL, 
                urlToTestClassFolder, jdoJarURL, jUnitJarURL, log4jURL, derby},null).blockOnlyClass("org.jpox.samples.*jdo");

        ClassLoader l = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(loader);

        Class cls = Class.forName(ClassLoaderTest.class.getName(), true, loader);
        try
        {
            cls.getMethod("runPersistAndDeletePerson").invoke(cls.newInstance());
            fail("expected JDOFatalUserException");
        }
        catch (InvocationTargetException e)
        {
            // MetaData will not be found so expect a JDOFatalUserException
            if (!(e.getTargetException().getClass().getName().equals(NoPersistenceInformationException.class.getName())))
            {
                e.getTargetException().printStackTrace();
                fail("Classloader without persistence information (metadata/annotations) should have thrown exception relating to this but threw " +
                    e.getTargetException());
            }
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(l);
        }
    }

    /**
     * Test classpath in context loader two classpaths here 
     * 1st classloader contains JDO, LOG4J
     * 2nd classloader contains DN classes, Test Case classes and samples. Delegates to 1st classloader for JDO, and other libraries
     * @throws Throwable
     */
    public void testDNinClassloader2() throws Throwable
    {
        if (!"derby".equals(vendorID))
        {
            return;
        }

        @SuppressWarnings("resource")
        URLClassLoaderRestrictive excludeOrgJpoxJavaxJdo = new URLClassLoaderRestrictive(null).blockOnlyClass("org.jpox.*").blockOnlyClass("javax.jdo.*");
        URLClassLoader loader = new URLClassLoaderRestrictive(new URL[]
            {dnCoreJarURL, enhancerJarURL, testFrameworkJarURL, urlToTestClassFolder, jdoJarURL, jUnitJarURL, log4jURL, derby, asmURL},excludeOrgJpoxJavaxJdo);

        ClassLoader l = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(loader);

        Class cls = Class.forName(ClassLoaderTest.class.getName(), true, loader);
        try
        {
            cls.getMethod("runPersistAndDeletePerson").invoke(cls.newInstance());
        }
        catch (InvocationTargetException e)
        {
            throw e.getTargetException();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(l);
        }
    }

    /**
     * Test classpath in context loader two classpaths here 
     * 1st classloader contains JDO, LOG4J
     * 2nd classloader contains DN classes, Test Case classes and samples. Delegates to 1st classloader for JDO, and other libraries
     * @throws Throwable
     */
    public void testNewObjectInstance() throws Throwable
    {
        if (!"derby".equals(vendorID))
        {
            return;
        }

        @SuppressWarnings("resource")
        URLClassLoaderRestrictive excludeOrgJpoxJavaxJdo = new URLClassLoaderRestrictive(ClassLoaderTest.class.getClassLoader()).blockOnlyClass("org.jpox.*").blockOnlyClass("javax.jdo.*");
        URLClassLoader loader = new URLClassLoader(new URL[]
            {dnCoreJarURL, testFrameworkJarURL, urlToTestClassFolder, jdoJarURL}, excludeOrgJpoxJavaxJdo);
        ClassLoader l = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(loader);

        Class cls = Class.forName(ClassLoaderTest.class.getName(), true, loader);
        try
        {
            cls.getMethod("runNewObjectInstance").invoke(cls.newInstance());
        }
        catch (InvocationTargetException e)
        {
            throw e.getTargetException();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(l);
        }
    }

    /**
     * Test classpath in context loader two classpaths here 
     * 1st classloader contains JDO, LOG4J
     * 2nd classloader contains DN classes, Test Case classes and samples. Delegates to 1st classloader for JDO, and other libraries
     * @throws Throwable
     */
    public void testNewObjectInstance1() throws Throwable
    {
        if (!"derby".equals(vendorID))
        {
            return;
        }

        @SuppressWarnings("resource")
        URLClassLoaderRestrictive excludeOrgJpoxJavaxJdo = new URLClassLoaderRestrictive().blockOnlyClass("org.jpox.*").blockOnlyClass("javax.jdo.*");
        URLClassLoader loader = new URLClassLoader(new URL[]
            {dnCoreJarURL, derby, jUnitJarURL, log4jURL, testFrameworkJarURL, urlToTestClassFolder, jdoJarURL}, excludeOrgJpoxJavaxJdo);
        ClassLoader l = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(loader);

        Class cls = Class.forName(ClassLoaderTest.class.getName(), true, loader);
        try
        {
            cls.getMethod("runNewObjectInstance").invoke(cls.newInstance());
        }
        catch (InvocationTargetException e)
        {
            throw e.getTargetException();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(l);
        }
    }
    
    /**
     * Test classpath in context loader two classpaths here . running on the 2nd classloader
     * 1st classloader contains JDO, LOG4J
     * 2nd classloader contains DN classes, Test Case classes and samples. Delegates to 1st classloader for JDO, and other libraries
     * @throws Throwable
     */
    public void testNegativeDNinClassloader2() throws Throwable
    {
        if (!"derby".equals(vendorID))
        {
            return;
        }

        @SuppressWarnings("resource")
        ClassLoader blockOnlyOrgJpoxSamples = new URLClassLoaderRestrictive(new URL[]
             {dnCoreJarURL, testFrameworkJarURL, urlToTestClassFolder, jdoJarURL, jUnitJarURL, log4jURL, derby},null).blockOnlyClass("org.jpox.samples.*");

        ClassLoader l = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(blockOnlyOrgJpoxSamples);

        Class cls = Class.forName(ClassLoaderTest.class.getName(), true, blockOnlyOrgJpoxSamples);
        Person p = new Person(987322, "cl", "last", "mail");
        try
        {
            cls.getMethod("runPersistArgument", new Class[] {Object.class}).invoke(cls.newInstance(), new Object[] {p});
        }
        catch (InvocationTargetException e)
        {
            //class not persistenceCapable since interface loaded by different classloader
            assertEquals(e.getTargetException().toString(),ClassNotPersistenceCapableException.class.getName(),e.getTargetException().getClass().getName());
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(l);
        }
    }
    
    /**
     * Test assign class linked to jdo classes from classloader App and try to use within JDO classes loaded by classloader 2
     * App classloader the current classloader 
     * 1st classloader contains LOG4J, JUNIT
     * 2nd classloader contains JDO, DN classes, Test Case classes and samples. Delegates to 1st classloader
     * @throws Throwable
     */
    public void testClassNotPersistenceCapable() throws Throwable
    {
        if (!"derby".equals(vendorID))
        {
            return;
        }

        @SuppressWarnings("resource")
        URLClassLoaderRestrictive excludeOrgJpoxJavaxJdo = new URLClassLoaderRestrictive(ClassLoaderTest.class.getClassLoader()).blockOnlyClass("org.jpox.*").blockOnlyClass("javax.jdo.*");
        URLClassLoader loader = new URLClassLoaderSafe(new URL[]{urlToTestClassFolder, jdoJarURL, jUnitJarURL, log4jURL}, excludeOrgJpoxJavaxJdo);
        URLClassLoader loader2 = new URLClassLoaderSafe(new URL[]{dnCoreJarURL, enhancerJarURL, testFrameworkJarURL, urlToTestClassFolder}, loader);
        ClassLoader l = Thread.currentThread().getContextClassLoader();

        Person p = new Person(987322, "cl", "last", "mail");
        Thread.currentThread().setContextClassLoader(loader2);

        Class cls = Class.forName(ClassLoaderTest.class.getName(), true, loader2);
        try
        {
            cls.getMethod("runPersistArgument", new Class[] {Object.class}).invoke(cls .newInstance(), new Object[] {p});
        }
        catch (InvocationTargetException e)
        {
            //persistence capable not found
            assertEquals(e.getTargetException().toString(),ClassNotPersistenceCapableException.class.getName(),e.getTargetException().getClass().getName());
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(l);
        }
    }

    /**
     * Test classpath in context loader two classpaths here 
     * 1st classloader contains JDO, LOG4J
     * 2nd classloader contains DN classes, Test Case classes and samples. Delegates to 1st classloader for JDO, and other libraries
     * THIS TEST IS COMMENTED OUT SINCE IS INVALID. TypeManager does NOT have static methods getTypeManager(), getSupportedTypes() etc
     * @throws Throwable
     */
    /*public void testTypeManagerLoadingDNTypes() throws Throwable
    {
        if (!"derby".equals(vendorID))
        {
            return;
        }

        URL wrongURL = ClassLoaderTest.class.getResource("/org/jpox/persistence/ClassLoaderTest.class");
        URL urlToTestClassFolder = getURLForTestClassFolder();
        URL urlToClassFolder = getURLForClassFolder();
        URL jdoJarURL = getURLForJDOJar();

        URLClassLoader loader = new ExcludesSamplesClassLoader(new URL[]{urlToClassFolder,urlToTestClassFolder,jdoJarURL}, 
            new ExcludesOrgJpoxJavaxJdoClassLoader(new URL[]{wrongURL}, ClassLoaderTest.class.getClassLoader()));
        URLClassLoader loader2 = new URLClassLoader(new URL[]{wrongURL}, null );

        ClassLoader l = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(loader2);

        Class cls = Class.forName(TypeManager.class.getName(), true, loader);
        try
        {
            Object obj = cls.getMethod("getTypeManager", null).invoke(null,null);
            Set set = (Set) cls.getMethod("getSupportedTypes",null).invoke(obj,null);
            assertTrue(!set.isEmpty());
        }
        catch (InvocationTargetException e)
        {
            throw e;
        }

        Thread.currentThread().setContextClassLoader(l);
    }*/

    /**
     * Test classpath in context loader two classpaths here 
     * 1st classloader contains JDO, LOG4J
     * 2nd classloader contains DN classes, Test Case classes and samples. Delegates to 1st classloader for JDO, and other libraries
     * @throws Throwable
     */
    public void testNotInitilizedClass() throws Throwable
    {
        if (!"derby".equals(vendorID))
        {
            return;
        }

        ClassLoader l = Thread.currentThread().getContextClassLoader();

        @SuppressWarnings("resource")
        ClassLoader loader = new URLClassLoaderRestrictive(new URL[]{dnCoreJarURL, testFrameworkJarURL, urlToTestClassFolder, jdoJarURL, jUnitJarURL, log4jURL, derby},null).blockOnlyClass("org.jpox.samples.*^(jdo)");
        
        Thread.currentThread().setContextClassLoader(loader);
        Class cls = Class.forName(ClassLoaderTest.class.getName(), true, loader);
        try
        {
            cls.getMethod("runValidateRegisteredClass").invoke(cls.newInstance());
        }
        catch (InvocationTargetException e)
        {
            throw e.getTargetException();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(l);
        }

    }

    /**
     * Run the classloader test for ContextClassLoader
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void runNewInstance() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        getPMF(); //make sure we enfore reload the metadata 

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Object id = null;
        try
        {
            try
            {
                tx.begin();
                ICity p = (ICity) pm.newInstance(ICity.class);
                p.setPopulation(1001);
                pm.makePersistent(p);
                id = pm.getObjectId(p);
    
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
                ICity p = (ICity) pm.getObjectById(id);
                assertEquals(1001, p.getPopulation());
                pm.deletePersistent(p);
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
            try
            {
                clean(ICity.class);
            }
            finally
            {
                shutdownDatabase();
            }
        }
    }

    /**
     * Run the classloader test for ContextClassLoader
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void runPersistAndDeletePerson() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        getPMF(); //make sure we enfore reload the metadata 

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Object id = null;
        try
        {
            try
            {
                Person p = new Person(987322, "cl", "last", "mail");
                tx.begin();
                pm.makePersistent(p);
                id = pm.getObjectId(p);
    
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
                Person p = (Person) pm.getObjectById(id);
                assertEquals("cl", p.getFirstName());
                pm.deletePersistent(p);
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
            try
            {
                clean(Person.class);
            }
            finally
            {
                shutdownDatabase();
            }
        }
    }

    /**
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void runNewObjectInstance() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        assertEquals(0,JDOImplHelper.getInstance().getRegisteredClasses().size());
        getPMF(); //make sure we enfore reload the metadata 

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        assertEquals(0,JDOImplHelper.getInstance().getRegisteredClasses().size());
        try
        {
            tx.begin();
            Class cls = Class.forName("org.jpox.samples.identity.ComposedStringID", false, Thread.currentThread().getContextClassLoader());
            assertEquals(0,JDOImplHelper.getInstance().getRegisteredClasses().size());
            pm.newObjectIdInstance(cls, "abc::MyIdentity");

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
            shutdownDatabase();
        }
    }
    
    /**
     * Run the classloader test for ContextClassLoader
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void runValidateRegisteredClass() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        assertEquals(0,JDOImplHelper.getInstance().getRegisteredClasses().size());
        getPMF(); //make sure we enfore reload the metadata 

        PersistenceManager pm = pmf.getPersistenceManager();
        assertEquals(0,JDOImplHelper.getInstance().getRegisteredClasses().size());
        JDOImplHelper h = JDOImplHelper.getInstance(); 
        Collection c = h.getRegisteredClasses();

        MetaDataManager mmgr = ((JDOPersistenceManager)pm).getExecutionContext().getMetaDataManager();
        ClassLoaderResolver clr = ((JDOPersistenceManager)pm).getExecutionContext().getClassLoaderResolver();
        mmgr.getMetaDataForClass("org.jpox.samples.models.company.Employee", clr);

        assertFalse(c.contains(clr.classForName("org.jpox.samples.models.company.Employee",false)));
        Class cls = clr.classForName("org.jpox.samples.models.company.Employee",true);
        assertTrue(c.contains(cls));
        pm.close();
    }    
    /**
     * Run the classloader test for ContextClassLoader
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void runPersistArgument(Object o) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        getPMF(); //make sure we enfore reload the metadata 

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Object id = null;
        try
        {
            tx.begin();
            pm.makePersistent(o);
            id = pm.getObjectId(o);

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
            shutdownDatabase();
        }

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Object o1 = pm.getObjectById(id);
            pm.deletePersistent(o1);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
            shutdownDatabase();
        }
    }

    public static class TestMappingManager implements MappingManager
    {
        /* (non-Javadoc)
         * @see org.datanucleus.store.rdbms.mapping.MappingManager#getMapping(java.lang.Class)
         */
        public JavaTypeMapping getMapping(Class c)
        {
            return getMapping(c, false, false, (String)null);
        }

        public void loadDatastoreMapping(PluginManager mgr, ClassLoaderResolver clr, String vendorId)
        {
        }

        /* (non-Javadoc)
         * @see org.datanucleus.store.rdbms.mapping.MappingManager#getDatastoreMappingClass(java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.datanucleus.ClassLoaderResolver)
         */
        public Class getDatastoreMappingClass(String memberName, String javaType, String jdbcType, String sqlType, ClassLoaderResolver clr)
        {
            // TODO Auto-generated method stub
            return null;
        }

        public JavaTypeMapping getMapping(Class c, boolean serialised, boolean embedded, String fieldName)
        {
            return null;
        }
        public JavaTypeMapping getMappingWithDatastoreMapping(Class c, boolean serialised, boolean embedded, ClassLoaderResolver clr)
        {
            return null;
        }
        public JavaTypeMapping getMapping(Table table, AbstractMemberMetaData fmd, ClassLoaderResolver clr, int mappingFieldType)
        {
            return null;
        }
        public DatastoreMapping createDatastoreMapping(JavaTypeMapping mapping, AbstractMemberMetaData fmd, int index, Column column)
        {
            return null;
        }
        public DatastoreMapping createDatastoreMapping(JavaTypeMapping mapping, Column column, String javaType)
        {
            return null;
        }
        public void registerDatastoreMapping(String javaTypeName, Class datastoreMappingType, String jdbcType, 
                String sqlType, boolean dflt, boolean override)
        {
        }
        public Column createColumn(JavaTypeMapping mapping, String javaType, int datastoreFieldIndex)
        {
            return null;
        }
        public Column createColumn(JavaTypeMapping mapping, String javaType, ColumnMetaData colmd)
        {
            return null;
        }
        public Column createColumn(AbstractMemberMetaData fmd, Table table, JavaTypeMapping mapping, ColumnMetaData colmd, Column reference, ClassLoaderResolver clr)
        {
            return null;
        }

        public JavaTypeMapping getMapping(Class c, boolean serialised, boolean embedded, String fieldName, DatastoreAdapter dba, TypeManager typeMgr)
        {
            return null;
        }
    }
    public static class URLClassLoaderRestrictive extends URLClassLoader
    {
        List<String> allowOnly = new ArrayList<String>();
        List<String> blockOnly = new ArrayList<String>();
        
        
        URLClassLoaderRestrictive() throws Throwable
        {
            super(new URL[]{},null);
        }
        
        URLClassLoaderRestrictive(URL[] urls, ClassLoader parent)
        {
            super(urls,parent);
        }
        
        URLClassLoaderRestrictive(ClassLoader parent)
        {
            super(new URL[] {},parent);
        }
        
        public Class loadClass(String name) throws ClassNotFoundException
        {
            if( allowOnly.size() > 0 )
            {
                boolean load = false;
                for( int i=0; !load && i<allowOnly.size(); i++)
                {
                    load = name.matches((String)allowOnly.get(i));
                }
                if( load )
                {
                    return super.loadClass(name);
                }
                throw new ClassNotFoundException("Class "+name+" not found (not allowed to load).");
            }
            if( blockOnly.size() > 0 )
            {
                boolean load = true;
                for( int i=0; load && i<blockOnly.size(); i++)
                {
                    load = !name.matches((String)blockOnly.get(i));
                }
                if( load )
                {
                    return super.loadClass(name);
                }
                throw new ClassNotFoundException("Class "+name+" not found (loading blocked).");
            }
            return super.loadClass(name);
        }

        protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException
        {
            if( allowOnly.size() > 0 )
            {
                boolean load = false;
                for( int i=0; !load && i<allowOnly.size(); i++)
                {
                    load = name.matches((String)allowOnly.get(i));
                }
                if( load )
                {
                    return super.loadClass(name, resolve);
                }
                throw new ClassNotFoundException("Class "+name+" not found (not allowed to load).");
            }
            if( blockOnly.size() > 0 )
            {
                boolean load = true;
                for( int i=0; load && i<blockOnly.size(); i++)
                {
                    load = !name.matches((String)blockOnly.get(i));
                }
                if( load )
                {
                    return super.loadClass(name, resolve);
                }
                throw new ClassNotFoundException("Class "+name+" not found (loading blocked).");
            }
            return super.loadClass(name, resolve);
        }
        
        public URLClassLoaderRestrictive allowOnlyClass(String pattern)
        {
            allowOnly.add(pattern);
            return this;
        }

        public URLClassLoaderRestrictive blockOnlyClass(String pattern)
        {
            blockOnly.add(pattern);
            return this;
        }

        public Enumeration getResources(String name) throws IOException
        {
            if( allowOnly.size() > 0 )
            {
                boolean load = false;
                for( int i=0; !load && i<allowOnly.size(); i++)
                {
                    load = name.matches((String)allowOnly.get(i));
                }
                if( load )
                {
                    return super.getResources(name);
                }
                return new Enumeration(){
                    
                    public Object nextElement()
                    {
                        return null;
                    }
                
                    public boolean hasMoreElements()
                    {
                        return false;
                    }
                };
            }
            if( blockOnly.size() > 0 )
            {
                boolean load = true;
                for( int i=0; load && i<blockOnly.size(); i++)
                {
                    load = !name.matches((String)blockOnly.get(i));
                }
                if( load )
                {
                    return super.getResources(name);
                }
                return new Enumeration(){
                    
                    public Object nextElement()
                    {
                        return null;
                    }
                
                    public boolean hasMoreElements()
                    {
                        return false;
                    }
                };
            }
            return super.getResources(name);
        }

        public URL getResource(String name)
        {
            if( allowOnly.size() > 0 )
            {
                boolean load = false;
                for( int i=0; !load && i<allowOnly.size(); i++)
                {
                    load = name.matches((String)allowOnly.get(i));
                }
                if( load )
                {
                    return super.getResource(name);
                }
                return null;
            }
            if( blockOnly.size() > 0 )
            {
                boolean load = true;
                for( int i=0; load && i<blockOnly.size(); i++)
                {
                    load = !name.matches((String)blockOnly.get(i));
                }
                if( load )
                {
                    return super.getResource(name);
                }
                return null;
            }
            return super.getResource(name);
        }
    }

    public static class URLClassLoaderSafe extends URLClassLoader
    {
        public ClassLoader cl;
        public URLClassLoaderSafe(URL[] url, ClassLoader cl)
        {
            super(url);
            this.cl = cl;
        }
        
        public Class loadClass(String name) throws ClassNotFoundException
        {
            try
            {
                return cl.loadClass(name);
            }
            catch(Exception ex)
            {
                return super.loadClass(name);
            }
        }
        
        protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException
        {
            try
            {
                return cl.loadClass(name);
            }
            catch(Exception ex)
            {
                return super.loadClass(name, resolve);
            }
        }
        
        public Enumeration getResources(String name) throws IOException
        {
            Enumeration e = cl.getResources(name);
            if( e == null )
            {
                e = super.getResources(name);
            }
            return e;
        }
    }
    
    /**
     * Method to obtain the PMF to use.
     * Creates a new PMF on each call.
     * @return The PMF (also stored in the local "pmf" variable)
     */
    protected synchronized PersistenceManagerFactory getPMF()
    {
        Properties props = new Properties();
        try
        {
            props.load(ClassLoaderTest.class.getResourceAsStream("/org/jpox/persistence/ClassLoaderTest.properties"));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return getPMF(props);
    }

    /**
     * Shutdown the derby database
     */
    protected void shutdownDatabase()
    {
        // TODO This should be if we are actually using Derby!
        for( int i=0; i<10; i++)
        {
            try
            {
                DriverManager.getConnection("jdbc:derby:ClassLoaderTestDB;shutdown=true");
                break;
            }
            catch (SQLException e)
            {
                if( e.getSQLState().equals("08006") )
                {
                    break;
                }
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Convenience accessor for getting the directory / jar url.
     * @return The URL
     * @throws Throwable
     */
    private URL getURLForClass(String className)
    throws Throwable
    {
        String path = ClassLoaderTest.class.getResource(className).getPath();
        if (path.indexOf('!') >= 0) // if its a jar
        {
            return new URL(path.substring(0, path.indexOf('!')));
        }
        //else normal file directory
        File file = new File(path);
        int count = new StringTokenizer(className,"/").countTokens();
        for( int i=0; i<count; i++)
        {
            file = file.getParentFile();
        }
        return new URL(file.toURI().toURL().toString()+"/");
    }

    /**
     * Convenience accessor for the JDO jar URL.
     * @return The URL
     */
    private URL getURLForJDOJar()
    {
        try
        {
            return getURLForClass("/javax/jdo/JDOHelper.class");
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    /**
     * Convenience accessor for the Log4J jar URL.
     * @return The URL
     */
    private URL getURLForLog4JJar()
    {
        try
        {
            return getURLForClass("/org/apache/log4j/Category.class");
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    /**
     * Convenience accessor for the Junit jar URL.
     * @return The URL
     */
    private URL getURLForJunitJar()
    {
        try
        {
            return getURLForClass("/junit/framework/TestCase.class");
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    /**
     * Convenience accessor for the TestFramework JAR URL.
     * @return The URL
     */
    private URL getURLForTestFrameworkJar()
    {
        try
        {
            return getURLForClass("/org/datanucleus/tests/TestHelper.class");
        }
        catch (Throwable e)
        {
            return null;
        }
    }
    
    /**
     * Convenience accessor for the Derby JAR URL.
     * @return The URL
     */
    private URL getURLForDerby()
    {
        try
        {
            return getURLForClass("/org/apache/derby/jdbc/EmbeddedDriver.class");
        }
        catch (Throwable e)
        {
            return null;
        }
    }    

    /**
     * Convenience accessor for the ASM JAR URL.
     * @return The URL
     */
    private URL getURLForASM()
    {
        try
        {
            return getURLForClass("/org/objectweb/asm/Type.class");
        }
        catch (Throwable e)
        {
            return null;
        }
    }    
    
    /**
     * Convenience accessor for the Enhancer JAR URL.
     * @return The URL
     */
    private URL getURLForEnhancerJar()
    {
        try
        {
            return getURLForClass("/org/datanucleus/enhancer/DataNucleusEnhancer.class");
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    /**
     * Convenience accessor for the classes folder URL.
     * @return The URL
     */
    private URL getURLForCoreFolder()
    {
        try
        {
            return getURLForClass("/org/datanucleus/NucleusContext.class");
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    /**
     * Convenience accessor for the test-classes folder URL.
     * @return The URL
     */
    private URL getURLForTestClassFolder()
    {
        try
        {
            return getURLForClass("/org/jpox/persistence/ClassLoaderTest.class");
        }
        catch (Throwable e)
        {
            return null;
        }
    }
    
}