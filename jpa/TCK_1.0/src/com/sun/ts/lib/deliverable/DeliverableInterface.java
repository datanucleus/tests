/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)DeliverableInterface.java	1.11 06/04/07
 */

package com.sun.ts.lib.deliverable;

import java.util.Map;
import java.util.Properties;
import com.sun.ts.lib.porting.DeploymentInfo;
import com.sun.javatest.TestEnvironment;

/**
 * This interface serves as a place to retrieve deliverable specific information.
 * Some example deliverables would be standalone TCKs, the CTS, the JDBC test
 * suite, etc.
 *
 * @author	Kyle Grucci
 */
public interface DeliverableInterface
{
	/**
	 * This method is called to retrieve a reference to this Deliverable's
	 * PropertyManager.  If the PropertyManager instance does not yet exist,
	 * one will be created from the TestEnvironment object.
	 *
	 * @param	te	TestEnvironment specific to the currently running test
	 *
	 * @return		PropertyManagerInterface impl.
	 */
	 public PropertyManagerInterface createPropertyManager(TestEnvironment te) throws Exception;

	/**
	 * This method is called to retrieve a reference to this Deliverable's
	 * PropertyManager.  If the PropertyManager instance does not yet exist,
	 * one will be created from the Properties object.
	 *
	 * @param	p	Properties specific to the currently running test
	 *
	 * @return		PropertyManagerInterface impl.
	 */
	 public PropertyManagerInterface createPropertyManager(Properties p) throws Exception;

	/**
	 * This method is called to retrieve a reference to this Deliverable's
	 * PropertyManager.
	 *
	 * @return		PropertyManagerInterface impl.
	 */
	 public PropertyManagerInterface getPropertyManager() throws Exception;

	/**
	 * This method is called to determine the vehicles to use for given test
	 * directories.
	 *
	 * @return		A mapping between test directories and vehicles.
	 */
	 public Map getValidVehicles();

	/**
	 * This method is called to determine the the common apps that must be
	 * deployed for given test directories.
	 *
	 * @return		A mapping between test directories and common app dirs.
	 */
	 public Map getCommonApps();

	/**
	 * This method is called to determine the direction to run any interop tests
	 * The default for any tests if forward.
	 *
	 * @return		A mapping between test directories and directions.
	 */
	 public Map getInteropDirections();

	/**
	 * This method is called by the SuiteSynchronizer class to determine
	 * whether autodeployment is supported by this deliverable.
	 *
	 * @return		True if supported, else false.
	 */
	 public boolean supportsAutoDeployment();

	/**
	 * This method is called by the SuiteSynchronizer class to determine
	 * whether interop tests are supported by this deliverable.
	 *
	 * @return		True if supported, else false.
	 */
	 public boolean supportsInterop();

	/**
	 * This method is called by the SuiteSynchronizer class to determine
	 * whether JMS topic/queue/factory admin is supported by this deliverable.
	 *
	 * @return		True if supported, else false.
	 */
	 public boolean supportsAutoJMSAdmin();

        /**
         * This method is called by the TSScript class, which appends the returned 
         * string to the classpath specified in the testExecute commandline.
         *
         * @param   distDir The dist directory for the currently running test.
         *
         * @return  Returns a String which will be appended to the classpath
         *          specified in the testExecute commandline.   
         */
         public String getAdditionalClasspath(String distDir);

	/**
	 * This method is called by the SuiteSynchronizer class to get
	 * a DeploymentInfo Object.  Other deliverables may use this method to
	 * subclass the standard DeploymentInfo class.
	 *
	 * @param	earFile	The archive being deployed
	 * @param	sValidRuntimeInfoFilesArray	    An array of Sun RI runtime xml
	 *
	 * @return		This method should return a DeploymentInfo object
	 */
	 public DeploymentInfo getDeploymentInfo(String earFile, String [] sValidRuntimeInfoFilesArray);
}
