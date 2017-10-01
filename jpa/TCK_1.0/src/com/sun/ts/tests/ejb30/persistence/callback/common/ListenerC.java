/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)ListenerC.java	1.3 06/03/16
 */

package com.sun.ts.tests.ejb30.persistence.callback.common;


import javax.persistence.PrePersist;
import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.PostRemove;
import javax.persistence.PreUpdate;
import javax.persistence.PostUpdate;
import javax.persistence.PostLoad;


public class ListenerC extends ListenerBase implements Constants {
    
    public ListenerC() {
        super();
    }
    
    @Override
    @PrePersist
    public void prePersist(CallbackStatusIF b) {
        super.prePersist(b);
    }

    @Override
    @PostPersist
    public void postPersist(Object b) {
        super.postPersist(b);
    }
    
    @Override
    @PreRemove
    public void preRemove(CallbackStatusIF b) {
        super.preRemove(b);
    }
    
    @Override
    @PostRemove
    public void postRemove(Object b) {
        super.postRemove(b);
    }
    
    @Override
    @PreUpdate
    public void preUpdate(CallbackStatusIF b) {
        super.preUpdate(b);
    }
    
    @Override
    @PostUpdate
    public void postUpdate(Object b) {
        super.postUpdate(b);
    }
    
    @PostLoad
    @Override
    public void postLoad(CallbackStatusIF b) {
        super.postLoad(b);
    }
    
}
