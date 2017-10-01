/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Product.java	1.1 06/03/27
 */

package com.sun.ts.tests.ejb30.persistence.callback.listeneroverride;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusImpl;
import com.sun.ts.tests.ejb30.persistence.callback.common.ListenerB;
import com.sun.ts.tests.ejb30.persistence.callback.common.ListenerC;

@Entity
@Table(name="PRODUCT_TABLE")
//@EntityListeners({ListenerB.class, ListenerC.class})
@EntityListeners({ListenerC.class, ListenerB.class})
public class Product extends CallbackStatusImpl 
        implements java.io.Serializable, CallbackStatusIF {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private long partNumber;
    
    public Product() {
        super();
    }
    
    public Product(String id, String name, double price, int quantity, long partNumber) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.partNumber = partNumber;
    }
    
    @Id
    @Column(name="ID")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    @Column(name="NAME")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name="PRICE")
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    
    @Column(name="QUANTITY")
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int v) {
        this.quantity = v;
    }
    
    @Column(name="PNUM")
    public long getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(long v) {
        this.partNumber = v;
    }
}
