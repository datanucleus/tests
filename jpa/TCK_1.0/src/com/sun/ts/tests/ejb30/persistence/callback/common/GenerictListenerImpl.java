/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)GenerictListenerImpl.java	1.2 06/04/07
 */

package com.sun.ts.tests.ejb30.persistence.callback.common;

import com.sun.ts.tests.ejb30.common.helper.TLogger;

/**
 * An annotation-free class that holds the logics for recording entity lifecycle
 * calls.  This class is used by entities as a generic helper class, and also
 * extended with annotations to form a callback listener class.
 */
public class GenerictListenerImpl implements Constants {
    
    public GenerictListenerImpl() {
        super();
    }
    
    public static void logTrace(String s, CallbackStatusIF b) {
        String ss = b.getEntityName() + ": " + s;
        TLogger.log(ss);
    }
    
    public void prePersist(CallbackStatusIF b) {
        logTrace("In prePersist in class " + this, b);
        b.setPrePersistCalled(true);
        b.addPrePersistCall(b.getEntityName());
        String testName = b.getTestName();
        if(prePersistRuntimeExceptionTest.equals(testName)) {
            throw new ArithmeticException("RuntimeException from PrePersist.");
        }
    }

    public void postPersist(Object b) {
        CallbackStatusIF p = (CallbackStatusIF) b;
        logTrace("In postPersist.", p);
        if(!p.isPrePersistCalled()) {
           TLogger.log("DEBUG: When calling postPersist, prePersist has not been called.");
            throw new IllegalStateException("When calling postPersist, prePersist has not been called.");
        }
        p.setPostPersistCalled(true);
        p.addPostPersistCall(p.getEntityName());
    }
    
    
    public void preRemove(CallbackStatusIF b) {
        logTrace("In preRemove.", b);
        b.setPreRemoveCalled(true);
        b.addPreRemoveCall(b.getEntityName());
    }

    public void postRemove(Object b) {
        CallbackStatusIF p = (CallbackStatusIF) b;
        logTrace("In postRemove.", p);
        if(!p.isPreRemoveCalled()) {
           TLogger.log("DEBUG: When calling postRemove, preRemove has not been called.");
            throw new IllegalStateException("When calling postRemove, preRemove has not been called.");
        }
        p.setPostRemoveCalled(true);
        p.addPostRemoveCall(p.getEntityName());
    }
    
    
    public void preUpdate(CallbackStatusIF b) {
        logTrace("In preUpdate.", b);
        b.setPreUpdateCalled(true);
        b.addPreUpdateCall(b.getEntityName());
    }

    public void postUpdate(Object b) {
        CallbackStatusIF p = (CallbackStatusIF) b;
        logTrace("In postUpdate.", p);
        if(!p.isPreUpdateCalled()) {
            TLogger.log("When calling postUpdate, preUpdate has not been called.");
            throw new IllegalStateException("When calling postUpdate, preUpdate has not been called.");
        }
        p.setPostUpdateCalled(true);
        p.addPostUpdateCall(p.getEntityName());
    }
    
    public void postLoad(CallbackStatusIF b) {
        logTrace("In postLoad.", b);
        b.setPostLoadCalled(true);
        b.addPostLoadCall(b.getEntityName());
    }
    
}
