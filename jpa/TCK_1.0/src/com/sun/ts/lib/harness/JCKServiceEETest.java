/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)JCKServiceEETest.java	1.13 06/02/11
 */

package com.sun.ts.lib.harness;

import java.io.*;

/**
 * This abstract class must be extended by all API clients of tests of 
 * J2EE service apis; for example, JDBC, RMI-IIOP, JavaMail, JMS, 
 * etc.  This allows us to bundle a number of API tests into a single
 * J2EE component and reduces the number of such components that have
 * to be deployed.
 *  
 * @author	Vella Raman 
 */	
public abstract class JCKServiceEETest extends ServiceEETest implements Serializable
{
}
