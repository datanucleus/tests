/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


/*
 * @(#)Order.java	1.12 06/06/07
 */

package com.sun.ts.tests.ejb30.persistence.callback.method;

import java.util.*;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;
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
@Table(name="ORDER_TABLE")
public class Order extends CallbackStatusImpl
        implements java.io.Serializable, CallbackStatusIF {
    private String id;
    private double totalPrice;
    private LineItem sampleLineItem;
    private Collection<LineItem> lineItems = new java.util.ArrayList<LineItem>();
    private GenerictListenerImpl callbackImpl = new GenerictListenerImpl();
    
    public Order() {
        super();
    }
    
    public Order(String id, double totalPrice) {
        this.id = id;
        this.totalPrice = totalPrice;
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
    
    @Column(name="TOTALPRICE")
    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double price) {
        this.totalPrice = price;
    }
    
    // 1x1
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(
        name="FK0_FOR_LINEITEM_TABLE")
    public LineItem getSampleLineItem() {
        return sampleLineItem;
    }
    public void setSampleLineItem(LineItem l) {
        this.sampleLineItem = l;
    }

    @OneToMany(mappedBy="order", cascade = CascadeType.ALL)
    public Collection<LineItem> getLineItems() {
        return lineItems;
    }
    public void setLineItems(Collection<LineItem> c) {
        this.lineItems = c;
    }
    
    public void addLineItem(LineItem p) {
        getLineItems().add(p);
    }
}
