/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)SigTestDriver.java	1.8 11/29/06
 */

package com.sun.ts.tests.signaturetest;

import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sun.javatest.Status;
import com.sun.ts.lib.util.TestUtil;

/**
 * <p>Wrapper for the <code>Sig Test</code> framework.</p>
 */
public class SigTestDriver extends SignatureTestDriver {

    private static final String CLASSPATH_FLAG   = "-classpath";
    private static final String FILENAME_FLAG    = "-FileName";
    private static final String PACKAGE_FLAG     = "-package";
    private static final String API_VERSION_FLAG = "-apiVersion";
    private static final String EXCLUDE_FLAG     = "-exclude";
    private static final String STATIC_FLAG      = "-static";
    private static final String CHECKVALUE_FLAG  = "-CheckValue";  // only valid w/ -static
    private static final String NO_CHECKVALUE_FLAG  = "-NoCheckValue";  
    private static final String SMODE_FLAG       = "-mode";  // requires arg of bin or src
    private static final String DEBUG_FLAG       = "-debug";



    // ---------------------------------------- Methods from SignatureTestDriver

    protected String normalizeFileName(File f) {
	String sURL = null;
        try {
	    sURL = f.toURL().toExternalForm();
        } catch (Exception e){
	    throw new RuntimeException(e);
        }
        return sURL;
    }


    protected String[] createTestArguments(String packageListFile,
                                           String mapFile,
                                           String signatureRepositoryDir,
                                           String packageOrClassUnderTest,
                                           String classpath,
                                           boolean bStaticMode)
    throws Exception {

        SignatureFileInfo info = getSigFileInfo(packageOrClassUnderTest,
                                                mapFile,
                                                signatureRepositoryDir);

        PackageList packageList = new PackageList(packageListFile);
        String[] subPackages =
            packageList.getSubPackages(packageOrClassUnderTest);

        List command = new ArrayList();

        if (bStaticMode) {
            // static mode allows finer level of constants checking 
            // -CheckValue says to check the actual const values
            TestUtil.logTrace("Setting static mode flag to allow constant checking.");
            command.add(STATIC_FLAG);
            command.add(CHECKVALUE_FLAG);

            // specifying "-mode src" allows stricter 2 way verification of constant vals
            // (note that using "-mode bin" mode is less strict)
            command.add(SMODE_FLAG);
            // command.add("bin");  
            command.add("src"); 
        } else {
            TestUtil.logTrace("Not Setting static mode flag to allow constant checking.");
        }

        // command.add(DEBUG_FLAG);
        command.add("-verbose");

        command.add(FILENAME_FLAG);
        command.add(info.getFile());

        command.add(CLASSPATH_FLAG);
        command.add(classpath);

        command.add(PACKAGE_FLAG);
        command.add(packageOrClassUnderTest);

        for (int i = 0; i < subPackages.length; i++) {
            command.add(EXCLUDE_FLAG);
            command.add(subPackages[i]);
        }

        command.add(API_VERSION_FLAG);
        command.add(info.getVersion());

        return ((String[]) command.toArray(new String[command.size()]));

    } // END createTestArguments


    protected boolean runSignatureTest(String packageOrClassName,
                                       String[] testArguments)
    throws Exception {

        Class sigTestClass =
            Class.forName("com.sun.tdk.signaturetest.SignatureTest");
        Object sigTestInstance = sigTestClass.newInstance();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        // do some logging to help with troubleshooting
        TestUtil.logTrace("\nCalling:  com.sun.tdk.signaturetest.SignatureTest() with following args:");
        for (int ii=0; ii < testArguments.length; ii++) {
            TestUtil.logTrace("   testArguments[" +ii+ "] = " + testArguments[ii]);
        }

        Method runMethod =
            sigTestClass.getDeclaredMethod("run",
                                           new Class[] { String[].class,
                                                         PrintWriter.class,
                                                         PrintWriter.class });
        runMethod.invoke(sigTestInstance,
                         new Object[] { testArguments, 
                                        new PrintWriter(output, true), 
                                        null });
        
        String rawMessages = output.toString();        
        if (rawMessages.indexOf("Added") > -1 ||
            rawMessages.indexOf("Removed") > -1 ||
            rawMessages.indexOf("Missing") > -1) {
            TestUtil.logMsg("********** Error Report '"
                            + packageOrClassName
                            + "' **********\n");            
            TestUtil.logMsg(rawMessages);            
        }        

        return Status.parse(sigTestInstance.toString().substring(7)).isPassed();

    } // END runSignatureTest

}
