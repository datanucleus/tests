/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)ListenerA.java	1.4 06/03/27
 */

package com.sun.ts.tests.ejb30.persistence.callback.common;


import javax.persistence.PrePersist;
import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.PostRemove;
import javax.persistence.PreUpdate;
import javax.persistence.PostUpdate;
import javax.persistence.PostLoad;


public class ListenerA extends ListenerBase implements Constants {
    
    public ListenerA() {
        super();
    }
    
    @Override
    @PrePersist
    protected void prePersist(CallbackStatusIF b) {
        super.prePersist(b);
    }

    @Override
    @PostPersist
    protected void postPersist(Object b) {
        super.postPersist(b);
    }
    
    @Override
    @PreRemove
    protected void preRemove(CallbackStatusIF b) {
        super.preRemove(b);
    }
    
    @Override
    @PostRemove
    protected void postRemove(Object b) {
        super.postRemove(b);
    }
    
    @Override
    @PreUpdate
    protected void preUpdate(CallbackStatusIF b) {
        super.preUpdate(b);
    }
    
    @Override
    @PostUpdate
    protected void postUpdate(Object b) {
        super.postUpdate(b);
    }
    
    @PostLoad
    @Override
    protected void postLoad(CallbackStatusIF b) {
        super.postLoad(b);
    }
    
}
