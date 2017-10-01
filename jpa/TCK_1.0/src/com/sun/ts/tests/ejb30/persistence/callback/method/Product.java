/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Product.java	1.8 06/03/27
 */

package com.sun.ts.tests.ejb30.persistence.callback.method;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.persistence.PrePersist;
import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.PostRemove;
import javax.persistence.PreUpdate;
import javax.persistence.PostUpdate;
import javax.persistence.PostLoad;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusImpl;
import com.sun.ts.tests.ejb30.persistence.callback.common.GenerictListenerImpl;

@Entity
@Table(name="PRODUCT_TABLE")
public class Product extends CallbackStatusImpl 
        implements java.io.Serializable, CallbackStatusIF {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private long partNumber;
    private GenerictListenerImpl callbackImpl = new GenerictListenerImpl();
    
    public Product() {
        super();
    }
    
    @Transient
    public GenerictListenerImpl getCallbackImpl() {
        return callbackImpl;
    }
    public void setCallbackImpl(GenerictListenerImpl callbackImpl) {
        this.callbackImpl = callbackImpl;
    }

    /////////////////////////////////////////////////////////////////////////
    @PrePersist
    private void prePersist() {
        getCallbackImpl().prePersist(this);
    }

    @PostPersist
    private void postPersist() {
        getCallbackImpl().postPersist(this);
    }
    
    
    @PreRemove
    private void preRemove() {
       getCallbackImpl().preRemove(this);
    }
    @PostRemove
    private void postRemove() {
        getCallbackImpl().postRemove(this);
    }
    
    
    @PreUpdate
    private void preUpdate() {
        getCallbackImpl().preUpdate(this);
    }
    @PostUpdate
    private void postUpdate() {
        getCallbackImpl().postUpdate(this);
    }
    
    @PostLoad
    private void postLoad() {
        getCallbackImpl().postLoad(this);
    }
    
    /////////////////////////////////////////////////////////////////////////
   
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
