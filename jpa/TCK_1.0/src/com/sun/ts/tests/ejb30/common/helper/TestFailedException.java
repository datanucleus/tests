/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * @(#)TestFailedException.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.common.helper;

import java.io.Serializable;

/**
 * An application exception for reporting test failure status and reason
 * back to test client.
 */
public class TestFailedException extends Exception implements Serializable {

    public TestFailedException() {
        super();
    }

    /**
     * @param message
     */
    public TestFailedException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public TestFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public TestFailedException(Throwable cause) {
        super(cause);
    }

}
