/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


/*
 * @(#)Order.java	1.2 06/06/07
 */

package com.sun.ts.tests.ejb30.persistence.callback.listeneroverride;

import com.sun.ts.tests.ejb30.persistence.callback.common.ListenerC;
import java.util.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.EntityListeners;
import javax.persistence.CascadeType;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusImpl;
import com.sun.ts.tests.ejb30.persistence.callback.common.ListenerB;

@Entity
@Table(name="ORDER_TABLE")
//@EntityListeners({ListenerB.class, ListenerC.class})
@EntityListeners({ListenerC.class, ListenerB.class})
public class Order extends CallbackStatusImpl
        implements java.io.Serializable, CallbackStatusIF {
    private String id;
    private double totalPrice;
    private LineItem sampleLineItem;
    private Collection<LineItem> lineItems = new java.util.ArrayList<LineItem>();
    
    public Order() {
    }
    
    public Order(String id, double totalPrice) {
        this.id = id;
        this.totalPrice = totalPrice;
    }
    
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
    @JoinColumn(name="FK0_FOR_LINEITEM_TABLE")
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
