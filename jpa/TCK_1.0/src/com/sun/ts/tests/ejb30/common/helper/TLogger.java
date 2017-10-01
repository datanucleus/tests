/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)TLogger.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.common.helper;

/**
 * A convenience class to use different logging, e.g., cts TestUtil.log, or
 * System.out.println.
 */
public class TLogger {
    public static final String NL = System.getProperty("line.separator");
    private TLogger() {}
    
    public static void log(String... args) {
        String msg = null;
        if(args.length == 0) {
            return;
        } else if(args.length == 1) {
            msg = args[0];
            if(msg == null) {
                return;
            }
        } else {
            StringBuffer sb = new StringBuffer();
            for(String s : args) {
                sb.append(s).append(' ');
            }
            msg = sb.toString();
        }
        System.out.println(msg);
    }
    
    
    
    public static void log(String arg, Throwable thr) {
        if(arg != null) {
            System.out.println(arg);
        }
        if(thr != null) {
            thr.printStackTrace();
        }
    }
    
    public static void log(boolean status, String arg) {
        if(arg != null) {
            String s = status ? "PASSED: " : "FAILED: ";
            System.out.println(s + arg);
        }
    }
    
    public static void logMsg(String s) {
        log(s);
    }
    
    public static void logErr(String s) {
        log(s);
    }
    
    public static void logTrace(String s) {
        log(s);
    }
    
    public static void printStackTrace(Throwable th) {
        th.printStackTrace();
    }
    
    public static void main(String[] args) {
        TLogger.log("####");
    }
}

