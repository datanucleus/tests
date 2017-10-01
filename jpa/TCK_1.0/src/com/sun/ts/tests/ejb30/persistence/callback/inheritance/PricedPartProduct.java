/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)PricedPartProduct.java	1.10 06/07/27
 */

package com.sun.ts.tests.ejb30.persistence.callback.inheritance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.DiscriminatorValue;

import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;

@Entity
@DiscriminatorValue("PricedPartProduct")
@EntityListeners(com.sun.ts.tests.ejb30.persistence.callback.common.GenerictListener.class)
public class PricedPartProduct extends PartProduct 
        implements java.io.Serializable, CallbackStatusIF {
    private double price;
    
    public PricedPartProduct() {
        super();
    }
    
    @Column(name="PRICE")
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
