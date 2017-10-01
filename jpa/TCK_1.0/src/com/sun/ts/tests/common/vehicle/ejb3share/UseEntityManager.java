 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)UseEntityManager.java	1.3 06/02/11
 */

package com.sun.ts.tests.common.vehicle.ejb3share;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public interface UseEntityManager {
    public void setEntityManager(EntityManager em);
    public EntityManager getEntityManager();
    
    public void setEntityTransaction(EntityTransaction entityTransaction);
    public EntityTransaction getEntityTransaction();
    
    public void setInContainer(boolean inContainer);
    public boolean isInContainer();
    
}
