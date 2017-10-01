/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)LineItem.java	1.8 06/03/27
 */

package com.sun.ts.tests.ejb30.persistence.callback.method;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
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
@Table(name="LINEITEM_TABLE")
public class LineItem extends CallbackStatusImpl
        implements java.io.Serializable, CallbackStatusIF {
    private String id;
    private int quantity;
    private Order order;
    private Product product;
    private GenerictListenerImpl callbackImpl = new GenerictListenerImpl();
    
    public LineItem() {
        super();
    }
    
    public LineItem(String v1, int v2, Order v3, Product v4) {
        id = v1;
        quantity = v2;
        order = v3;
        product = v4;
    }
    
    public LineItem(String v1, int v2) {
        id = v1;
        quantity = v2;
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
    public void setId(String v) {
        id = v;
    }
    
    @Column(name="QUANTITY")
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int v) {
        quantity = v;
    }
    
    @ManyToOne
    @JoinColumn(name="FK1_FOR_ORDER_TABLE")
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order v) {
        order = v;
    }
    
    @ManyToOne
    @JoinColumn(name="FK_FOR_PRODUCT_TABLE")
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product v) {
        product = v;
    }
}
