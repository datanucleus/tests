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

Contributors:
    ...
**********************************************************************/
package org.datanucleus.enhancer.jdo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.RegisterClassEvent;
import javax.jdo.spi.RegisterClassListener;

import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.metadata.JDOMetaDataManager;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.FileMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.PackageMetaData;
import org.datanucleus.metadata.xml.MetaDataParser;

import junit.framework.TestCase;

/**
 * Base class for unit tests for JDO enhancement.
 */
public abstract class JDOTestBase extends TestCase implements RegisterClassListener
{
	protected Hashtable<Class, RegisterClassEvent> pcClasses = new Hashtable<Class, RegisterClassEvent>();

    /**
	 * 
	 */
	public JDOTestBase()
    {
        // All classes registered with JDOImplHelper are registered here
		JDOImplHelper.getInstance().addRegisterClassListener(this);
	}

	/**
     * Constructor.
	 * @param arg0
	 */
	public JDOTestBase(String name)
    {
		super(name);
		JDOImplHelper.getInstance().addRegisterClassListener(this);
	}

	public Class findClass(final Class c[], final String name) 
    {
		if ((c == null) || (c.length == 0))
        {
			return null;
		}
		for (int i = 0; i < c.length; i++) 
        {
			if (c[i].getName().equals(name)) 
            {
				return c[i];
			}
		}
		return null;
	}

	/**
     * Method called by JDOImplHelper when an enhanced class is instantiated.
     * @param event Event containing the registered class
	 */
	public void registerClass(RegisterClassEvent event) 
    {
		this.pcClasses.put(event.getRegisteredClass(), event);
	}

    /**
     * Tear down, called after each test.
     * @throws Exception
     */
	protected void tearDown() throws Exception 
    {
        // Deregister this class as a listener for newly enhanced classes
		JDOImplHelper.getInstance().removeRegisterClassListener(this);

		super.tearDown();
	}

    /**
     * Accessor for a ClassEnhancer to use in enhancing.
     * @param cmd ClassMetaData.
     * @return The ClassEnhancer for this class
     */
    public ClassEnhancer getClassEnhancer(ClassMetaData cmd, MetaDataManager mmgr)
    {
        return new JDOClassEnhancer(cmd, new ClassLoaderResolverImpl(), mmgr);
    }

    /**
     * Method to return a set of enhanced classes for all of those found in the specified JDO MetaData file.
     * @param resourceName Name of the MetaData file (relative to the CLASSPATH).
     * @return Set of enhanced classes
     * @throws IllegalArgumentException if an error occurs reading the file resource
     */
    public Class[] getEnhancedClassesFromFile(String resourceName)
    {
        InputStream in = JDOTestBase.class.getClassLoader().getResourceAsStream(resourceName);
        if (in == null)
        {
            throw new IllegalArgumentException("Cannot load resource :" + resourceName);
        }

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(in));
            String buf = null;
            while ((buf = br.readLine()) != null) 
            {
                sb.append(buf);
            }
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Error reading MetaData file " + resourceName + ": " + e.getMessage(), e);
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }

        String jdoXmlContents = sb.toString();
        if (jdoXmlContents == null)
        {
            throw new IllegalArgumentException("Contents of file is null");
        }

        NucleusContext context = new NucleusContext("JDO", NucleusContext.ContextType.ENHANCEMENT, null);
        MetaDataManager mgr = new JDOMetaDataManager(context);
        MetaDataParser parser = new MetaDataParser(mgr, context.getPluginManager(), true);
        ClassLoaderResolverImpl clr = new ClassLoaderResolverImpl();

        // Parse the MetaData
        FileMetaData filemd = (FileMetaData)parser.parseMetaDataStream(
            new ByteArrayInputStream(jdoXmlContents.getBytes()), null, "JDO");
        if (filemd == null)
        {
            return null;
        }
        mgr.registerFile("EnhancerTestXMLFile", filemd, clr);

        // Populate/Initialise the MetaData for the actual classes.
        for (int i=0;i<filemd.getNoOfPackages();i++)
        {
            PackageMetaData pmd = filemd.getPackage(i);
            for (int j=0; j<pmd.getNoOfClasses(); j++)
            {
                ClassMetaData cmd = pmd.getClass(j);
                cmd.populate(clr, null, mgr);
                cmd.initialise(clr, mgr);
            }
        }

        // Enhance the classes
        TestClassLoader cl = new TestClassLoader();
        Class result[];
        ArrayList<Class> resultList = new ArrayList<Class>();
        for (int i=0; i<filemd.getNoOfPackages(); i++)
        {
            PackageMetaData pmd = filemd.getPackage(i);
            for (int j=0; j<pmd.getNoOfClasses(); j++)
            {
                ClassMetaData cmd = (ClassMetaData)pmd.getClass(j);

                // Enhance the class using the MetaData
                ClassEnhancer enhancer = getClassEnhancer(cmd, mgr);
                enhancer.enhance();

                // Save the enhanced class
                resultList.add(cl.getClass(cmd.getFullClassName(), enhancer.getClassBytes()));
            }
        }

        result = (Class[])resultList.toArray(new Class[resultList.size()]);
        return result;
    }

    /**
     * Simple class loader for loading the enhanced class into.
     */
    static class TestClassLoader extends ClassLoader
    {
        /**
         * @param parent
         */
        protected TestClassLoader()
        {
            super(TestClassLoader.class.getClassLoader());
        }

        public Class getClass(String className, byte b[])
        {
            return defineClass(className, b, 0, b.length);
        }
    }
}