/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)PricedPartProductListener.java	1.6 06/07/27
 */

package com.sun.ts.tests.ejb30.persistence.callback.inheritance;

import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;
import com.sun.ts.tests.ejb30.persistence.callback.common.GenerictListenerImpl;
import com.sun.ts.tests.ejb30.persistence.callback.common.GenerictListener;

public class PricedPartProductListener extends GenerictListener {
    
    public PricedPartProductListener() {
        super();
    }
    
    public void prePersist(CallbackStatusIF b) {
        GenerictListenerImpl.logTrace("In PricedPartProductListener.prePersist.", b);
        throw new IllegalStateException("This is not a callback and should never be called.");
    }
}
