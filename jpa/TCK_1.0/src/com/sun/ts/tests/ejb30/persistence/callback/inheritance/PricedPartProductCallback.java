/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)PricedPartProductCallback.java	1.9 06/04/07
 */

package com.sun.ts.tests.ejb30.persistence.callback.inheritance;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusImpl;
import com.sun.ts.tests.ejb30.persistence.callback.common.Constants;
import com.sun.ts.tests.ejb30.persistence.callback.common.GenerictListenerImpl;

import javax.persistence.PrePersist;
import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.PostRemove;
import javax.persistence.PreUpdate;
import javax.persistence.PostUpdate;
import javax.persistence.PostLoad;
import javax.persistence.MappedSuperclass;

/**
 * This class defines some entity callback methods for an 
 * entity class.  This class itself is not an entity.  These callback methods
 * are intended to be inherited by subclass entities.
 */

@MappedSuperclass
abstract public class PricedPartProductCallback extends CallbackStatusImpl
        implements Constants, CallbackStatusIF {

    //@todo: for multiple callback listeners/methods, need to add to each 
    //callback methods:
    //addPrePersistCalls(getEntityName());
    
    @PrePersist
    public void prePersist() {
        GenerictListenerImpl.logTrace("In prePersist.", this);
        this.setPrePersistCalled(true);
        String testName = this.getTestName();
        if(prePersistRuntimeExceptionTest2.equals(testName)) {
            throw new ArithmeticException("RuntimeException from PrePersist.");
        }
    }

    @PostPersist
    public void postPersist() {
        GenerictListenerImpl.logTrace("In postPersist.", this);
        if(!this.isPrePersistCalled()) {
            throw new IllegalStateException("When calling postPersist, prePersist has not been called.");
        }
        this.setPostPersistCalled(true);
    }
    
    
    @PreRemove
    public void preRemove() {
        GenerictListenerImpl.logTrace("In preRemove.", this);
        this.setPreRemoveCalled(true);
    }
    
    @PostRemove
    public void postRemove() {
        GenerictListenerImpl.logTrace("In postRemove.", this);
        if(!this.isPreRemoveCalled()) {
            throw new IllegalStateException("When calling postRemove, preRemove has not been called.");
        }
        this.setPostRemoveCalled(true);
    }
    
    
    @PreUpdate
    public void preUpdate() {
        GenerictListenerImpl.logTrace("In preUpdate.", this);
        this.setPreUpdateCalled(true);
    }
    
    @PostUpdate
    public void postUpdate() {
        GenerictListenerImpl.logTrace("In postUpdate.", this);
        if(!this.isPreUpdateCalled()) {
            throw new IllegalStateException("When calling postUpdate, preUpdate has not been called.");
        }
        this.setPostUpdateCalled(true);
    }
    
    @PostLoad
    public void postLoad() {
        GenerictListenerImpl.logTrace("In postLoad.", this);
        this.setPostLoadCalled(true);
    }
    
}
