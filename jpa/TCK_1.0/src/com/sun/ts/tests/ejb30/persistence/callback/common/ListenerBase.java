/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)ListenerBase.java	1.3 06/04/07
 */

package com.sun.ts.tests.ejb30.persistence.callback.common;

import com.sun.ts.tests.ejb30.common.helper.TLogger;

/**
 * An annotation-free class that holds the logics for recording entity lifecycle
 * calls.  This is extended to form entity callback listener classes.
 * This class must not be specified as a entity callback listener.
 */
abstract public class ListenerBase implements Constants {
    protected ListenerBase() {
        super();
    }
    
    public String getShortName() {
        String name = this.getClass().getName();
        name = name.substring(name.lastIndexOf('.') + 1);
        return name;
    }
    
    protected void prePersist(CallbackStatusIF b) {
        GenerictListenerImpl.logTrace("In prePersist in class " + this, b);
        b.setPrePersistCalled(true);
        b.addPrePersistCall(getShortName());
        String testName = b.getTestName();
        if(prePersistRuntimeExceptionTest.equals(testName)) {
            throw new ArithmeticException("RuntimeException from PrePersist.");
        }
    }

    protected void postPersist(Object b) {
        CallbackStatusIF p = (CallbackStatusIF) b;
        GenerictListenerImpl.logTrace("In postPersist." + this, p);
        if(!p.isPrePersistCalled()) {
           TLogger.log("DEBUG: When calling postPersist, prePersist has not been called.");
            throw new IllegalStateException("When calling postPersist, prePersist has not been called.");
        }
        p.setPostPersistCalled(true);
        p.addPostPersistCall(getShortName());
    }
    
    
    protected void preRemove(CallbackStatusIF b) {
        GenerictListenerImpl.logTrace("In preRemove." + this, b);
        b.setPreRemoveCalled(true);
        b.addPreRemoveCall(getShortName());
    }

    protected void postRemove(Object b) {
        CallbackStatusIF p = (CallbackStatusIF) b;
        GenerictListenerImpl.logTrace("In postRemove." + this, p);
        if(!p.isPreRemoveCalled()) {
           TLogger.log("DEBUG: When calling postRemove, preRemove has not been called.");
            throw new IllegalStateException("When calling postRemove, preRemove has not been called.");
        }
        p.setPostRemoveCalled(true);
        p.addPostRemoveCall(getShortName());
    }
    
    
    protected void preUpdate(CallbackStatusIF b) {
        GenerictListenerImpl.logTrace("In preUpdate." + this, b);
        b.setPreUpdateCalled(true);
        b.addPreUpdateCall(getShortName());
    }

    protected void postUpdate(Object b) {
        CallbackStatusIF p = (CallbackStatusIF) b;
        GenerictListenerImpl.logTrace("In postUpdate." + this, p);
        if(!p.isPreUpdateCalled()) {
            TLogger.log("When calling postUpdate, preUpdate has not been called.");
            throw new IllegalStateException("When calling postUpdate, preUpdate has not been called.");
        }
        p.setPostUpdateCalled(true);
        p.addPostUpdateCall(getShortName());
    }
    
    protected void postLoad(CallbackStatusIF b) {
        GenerictListenerImpl.logTrace("In postLoad." + this, b);
        b.setPostLoadCalled(true);
        b.addPostLoadCall(getShortName());
    }
    
}
