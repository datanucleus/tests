/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)GenerictListener.java	1.6 06/03/10
 */

package com.sun.ts.tests.ejb30.persistence.callback.common;


import javax.persistence.PrePersist;
import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.PostRemove;
import javax.persistence.PreUpdate;
import javax.persistence.PostUpdate;
import javax.persistence.PostLoad;


public class GenerictListener extends GenerictListenerImpl implements Constants {
    
    public GenerictListener() {
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
