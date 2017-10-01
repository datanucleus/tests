/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)JPASigTest.java	1.2 06/04/20
 */

package com.sun.ts.tests.signaturetest.jpa;

import java.util.List;
import java.util.LinkedList;
import java.util.Properties;
import java.io.PrintWriter;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.javatest.Status;
import com.sun.ts.tests.signaturetest.*;

/*
 * This class is a simple example of a signature test that extends the
 * SigTest framework class.  This signature test is run outside of the
 * Java EE containers.  This class also contains the boilerplate
 * code necessary to create a signature test using the test framework.
 * To see a complete TCK example see the javaee directory for the Java EE
 * TCK signature test class.
 */
public class JPASigTest extends SigTest {

    /***** Abstract Method Implementation *****/

    /**
     * Returns a list of strings where each string represents a
     * package name.  Each package name will have it's signature
     * tested by the signature test framework.
     * 
     * @return String[] The names of the packages whose signatures
     *                  should be verified.
     */
    protected String[] getPackages() {
        return new String[] { "javax.persistence",
                              "javax.persistence.spi"};
    }

    /***** Boilerplate Code *****/

    /**
     * Entry point for different-VM execution.  It should delegate to method
     * run(String[], PrintWriter, PrintWriter), and this method should not
     * contain any test configuration.
     */
    public static void main(String[] args) {
        JPASigTest theTests = new JPASigTest();
        Status s = theTests.run(args, new PrintWriter(System.out), 
                   new PrintWriter(System.err));
        s.exit();
    }

    /**
     * Entry point for same-VM execution. In different-VM execution, the 
     * main method delegates to this method.
     */
    public Status run(String args[], PrintWriter out, PrintWriter err) {


        return super.run(args, out, err);
    }

    /*
     * The following comments are specified in the base class that
     * defines the signature tests.  This is done so the test finders
     * will find the right class to run.  The implementation of these
     * methods is inherited from the super class which is part of the
     * signature test framework.
     */

    // NOTE: If the API under test is not part of your testing runtime 
    // environment, you may use the property sigTestClasspath to specify 
    // where the API under test lives.  This should almost never be used. 
    // Normally the API under test should be specified in the classpath 
    // of the VM running the signature tests.  Use either the first 
    // comment or the one below it depending on which properties your 
    // signature tests need.  Please do not use both comments.

    /*
     *   @class.setup_props: ts_home, The base path of this TCK;
     *                       sigTestClasspath, path required for static checking;
     */
 
    /*
     * @testName:        signatureTest
     * @assertion:       An implementation of the Java Persistence API
     *			 must implement the required classes and APIs
     *			 defined in the specification and JavaDoc.
     * @test_Strategy:    Using reflection, gather the implementation specific
     *                    classes and APIs.  Compare these results with the 
     *                    expected (required) classes and APIs.
     *
     */


    /*
     * Call the parent class's cleanup method.
     */


} // end class JPASigTest
