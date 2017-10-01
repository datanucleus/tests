/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)LineItemSuper.java	1.1 06/03/27
 */

package com.sun.ts.tests.ejb30.persistence.callback.listener;

import com.sun.ts.tests.ejb30.persistence.callback.common.ListenerA;
import com.sun.ts.tests.ejb30.persistence.callback.common.ListenerB;
import javax.persistence.MappedSuperclass;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusImpl;

@MappedSuperclass
@EntityListeners({ListenerA.class, ListenerB.class})
public class LineItemSuper extends CallbackStatusImpl
        implements java.io.Serializable, CallbackStatusIF {
    public int quantity;
    
    public LineItemSuper() {
    }
    
    @Column(name="QUANTITY")
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int v) {
        quantity = v;
    }
}
