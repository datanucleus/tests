 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)EntityTransactionWrapper.java	1.3 06/03/03
 */

package com.sun.ts.tests.common.vehicle.ejb3share;

import javax.persistence.EntityTransaction;

final public class EntityTransactionWrapper implements EntityTransaction {
    private EntityTransaction delegate;
    
    public EntityTransactionWrapper() {}
    
    public EntityTransactionWrapper(EntityTransaction delegate) {
        this.delegate = delegate;
    }
    
    public void setDelegate(EntityTransaction delegate) {
        this.delegate = delegate;
    }
    
    public void rollback() {
        delegate.rollback();
    }

    public boolean isActive() {
        return delegate.isActive();
    }

    public void commit() {
        delegate.commit();
    }

    public void begin() {
        delegate.begin();
    }

    public void setRollbackOnly() {
        delegate.setRollbackOnly();
    }

    public boolean getRollbackOnly() {
        return delegate.getRollbackOnly();
    }
}
