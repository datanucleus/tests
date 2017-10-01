/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)PartProduct.java	1.9 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.callback.inheritance;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.DiscriminatorValue;

import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusImpl;

@Entity
@DiscriminatorValue("PartProduct")
@EntityListeners(com.sun.ts.tests.ejb30.persistence.callback.inheritance.PartProductListener.class)
public class PartProduct extends Product 
        implements java.io.Serializable, CallbackStatusIF {
    private long partNumber;
    
    public PartProduct() {
        super();
    }
    
    @Column(name="PNUM")
    public long getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(long v) {
        this.partNumber = v;
    }
}
