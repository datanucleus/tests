/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)LineItem.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/*
 * LineItem
 */

@Entity
@Table(name="LINEITEM_TABLE")
public class LineItem implements java.io.Serializable {

    // Instance variables
    private String id;
    private int quantity;
    private Order order;
    private Product product;

    public LineItem()
    {
    }

    public LineItem(String v1, int v2, Order v3, Product v4)
    {
	id = v1;
	quantity = v2;
	order = v3;
	product = v4;
    }

    public LineItem(String v1, int v2)
    {
	id = v1;
	quantity = v2;
    }

    @Id
    @Column(name="ID")
    public String getId()
    {
	return id;
    }
    public void setId(String v)
    {
	id = v;
    }

    @Column(name="QUANTITY")
    public int getQuantity()
    {
	return quantity;
    }

    public void setQuantity(int v)
    {
	quantity = v;
    }

    @ManyToOne
    @JoinColumn(
	name="FK1_FOR_ORDER_TABLE")
    public Order getOrder()
    {
	return order;
    }
    public void setOrder(Order v)
    {
	order = v;
    }

    @ManyToOne
    @JoinColumn(
        name="FK_FOR_PRODUCT_TABLE")
    public Product getProduct()
    {
	return product;
    }
    public void setProduct(Product v)
    {
	product = v;
    }
}
