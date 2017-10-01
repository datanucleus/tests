/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)PartProductListener.java	1.7 06/04/07
 */

package com.sun.ts.tests.ejb30.persistence.callback.inheritance;

import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;
import com.sun.ts.tests.ejb30.persistence.callback.common.Constants;
import com.sun.ts.tests.ejb30.persistence.callback.common.GenerictListenerImpl;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;

import javax.persistence.PrePersist;
import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.PostRemove;
import javax.persistence.PreUpdate;
import javax.persistence.PostUpdate;
import javax.persistence.PostLoad;


/**
 * All callback methods except PrePersist in this class throw RuntimeException, 
 * since they must not be called.  This listener is intended to be used by
 * PartProduct entity, whose callback methods take precedence.
 */
public class PartProductListener implements Constants {
    
    public PartProductListener() {
        super();
    }
    
    private void fail(String callbackName) {
        String reason = "This callback " + callbackName + 
                " in PartProductListener should not be invoked. The one in the entity subclass should be invoked";
        TLogger.log(reason);
        //throw new IllegalStateException(reason);
    }
    
    @PrePersist
    public void prePersist(CallbackStatusIF b) {
        GenerictListenerImpl.logTrace("In PartProductListener.prePersist.", b);
        b.setPrePersistCalled(true);
        String testName = b.getTestName();
        if(prePersistRuntimeExceptionTest.equals(testName)) {
            throw new ArithmeticException("RuntimeException from PrePersist.");
        }
    }

    @PostPersist
    public void postPersist(Object b) {
        fail("PostPersist");
    }
    
    
    @PreRemove
    public void preRemove(CallbackStatusIF b) {
        fail("PreRemove");
    }
    @PostRemove
    public void postRemove(Object b) {
        fail("PostRemove");
    }
    
    
    @PreUpdate
    public void preUpdate(CallbackStatusIF b) {
        fail("PreUpdate");
    }
    @PostUpdate
    public void postUpdate(Object b) {
        fail("PostUpdate");
    }
    
    @PostLoad
    public void postLoad(CallbackStatusIF b) {
       fail("PostLoad");
    }
    
}
