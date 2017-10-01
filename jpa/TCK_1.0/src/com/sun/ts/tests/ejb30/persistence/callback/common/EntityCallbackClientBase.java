 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)EntityCallbackClientBase.java	1.4 06/04/07
 */

package com.sun.ts.tests.ejb30.persistence.callback.common;

import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import javax.persistence.Query;


public abstract class EntityCallbackClientBase extends PMClientBase
        implements java.io.Serializable, Constants  {
    protected  EntityCallbackClientBase() {
        super();
    }
    
    protected void txShouldRollback(Object b, String id) throws Fault {
        String reason = "";
        try {
            getEntityManager().persist(b);
            getEntityTransaction().commit();
            reason = "Expecting ArithmeticException from callback method, but got none.";
            throw new Fault(reason);
        } catch (ArithmeticException e) {
            reason = "got expected exception: " + e.toString();
            TLogger.log(reason);
        } catch (Exception e) {
            reason = "Expecting ArithmeticException, but got unexpected exception: "
                    + e.toString();
            throw new Fault(reason, e);
        }
        
        Object p2 = getEntityManager().find(b.getClass(), id);
        if(p2 == null) {
            reason += "got expected result: entity with id " + id + " was not found.";
            TLogger.log(reason);
        } else {
            reason += "Unexpected result: found entity with id " + id;
            throw new Fault(reason);
        }
        
    }
}

