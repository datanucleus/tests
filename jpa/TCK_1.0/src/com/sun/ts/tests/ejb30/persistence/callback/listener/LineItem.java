/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)LineItem.java	1.9 06/03/27
 */

package com.sun.ts.tests.ejb30.persistence.callback.listener;

import com.sun.ts.tests.ejb30.persistence.callback.common.ListenerB;
import com.sun.ts.tests.ejb30.persistence.callback.common.ListenerC;
import javax.persistence.Entity;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.EntityListeners;
import javax.persistence.ExcludeDefaultListeners;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;

@Entity
@Table(name="LINEITEM_TABLE")
@EntityListeners({ListenerB.class, ListenerC.class})
@ExcludeDefaultListeners()
@ExcludeSuperclassListeners
public class LineItem extends LineItemSuper
        implements java.io.Serializable, CallbackStatusIF {
    private String id;
    
//    moved to LineItemSuper
//    private int quantity;
    
    private Order order;
    private Product product;
    
    public LineItem() {
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
    
    @Id
    @Column(name="ID")
    public String getId() {
        return id;
    }
    public void setId(String v) {
        id = v;
    }
    
//    moved to LineItemSuper
//    @Column(name="QUANTITY")
//    public int getQuantity() {
//        return quantity;
//    }
//    
//    public void setQuantity(int v) {
//        quantity = v;
//    }
    
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
