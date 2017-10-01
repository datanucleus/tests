/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * @(#)Helper.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.common.helper;

import com.sun.ts.lib.harness.EETest.Fault;
import java.util.Arrays;
import java.util.List;

public class Helper {
    
    private Helper() {
        super();
    }
    
    public static void compareResultList(List expected, List actual)
    throws Fault {
        String reason = null;
        if(expected.equals(actual)) {
            reason = "Got expected result list: " + expected;
            TLogger.log(reason);
        } else {
            reason = "Expecting result list: " + expected +
                    ", but actual: " + actual;
            throw new Fault(reason);
        }
    }
    
    public static void compareResultList(String[] expected, List actual)
    throws Fault {
        compareResultList(Arrays.asList(expected), actual);
    }
    
}
