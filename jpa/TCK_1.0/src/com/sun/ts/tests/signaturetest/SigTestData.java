/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)SigTestData.java	1.8 06/02/11
 */

package com.sun.ts.tests.signaturetest;

import java.util.Properties;

/**
 * This class holds the data passed to a signature test invocation during
 * the setup phase.  This allows us to keep the passed data separate and
 * reuse the data between the signature test framework base classes.
 */
public class SigTestData {

    private Properties props;

    public SigTestData(Properties props) {
        this.props = props;
    }

    public String getVehicle() {
        return props.getProperty("vehicle", "");
    }

    public String getTSHome() {
        return props.getProperty("ts_home", "");
    }

    public String getTestClasspath() {
        return props.getProperty("sigTestClasspath", "");
    }
 
    public String getProperty(String prop) {
        return props.getProperty(prop);
    }

} // end class SigTestData
