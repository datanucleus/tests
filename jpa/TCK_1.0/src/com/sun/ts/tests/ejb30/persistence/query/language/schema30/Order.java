/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


/*
 * @(#)Order.java	1.7 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;
import java.util.Collection;

/*
 * Order
 */

@Entity
@Table(name="ORDER_TABLE")
public class Order implements java.io.Serializable
{


    // Instance variables
    private String id;
    private double totalPrice;
    private Customer customer;
    private CreditCard creditCard;
    private LineItem sampleLineItem;
    private Collection<LineItem> lineItems = new java.util.ArrayList<LineItem>();

    public Order()
    {
    }

    public Order (String id, double totalPrice)
    {
        this.id = id;
        this.totalPrice = totalPrice;
    }

    public Order (String id, Customer customer)
    {
        this.id = id;
        this.customer = customer;
    }

    public Order (String id)
    {
        this.id = id;
    }

    //====================================================================
    // getters and setters for State fields

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

    //==================================================================== 
    // getters and setters for Association fields

    // MANYx1
    @ManyToOne
    @JoinColumn(
	name="FK4_FOR_CUSTOMER_TABLE")
    public Customer getCustomer() {
	return customer;
    }
    public void setCustomer(Customer customer) {
	this.customer = customer;
    }

    //1x1
    @OneToOne(mappedBy="order")
    public CreditCard getCreditCard() {
	return creditCard;
    }
    public void setCreditCard(CreditCard cc) {
	this.creditCard = cc;
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

    //1xMANY
    @OneToMany(cascade=CascadeType.ALL, mappedBy="order")
    public Collection<LineItem> getLineItems() {
	return lineItems;
    }
    public void setLineItems(Collection<LineItem> c) {
	this.lineItems = c;
    }

    //====================================================================
    // Miscellaneous Business Methods

    public void addLineItem(LineItem p) throws LineItemException
    {
        getLineItems().add(p);
    }

    public void addSampleLineItem(LineItem p) throws LineItemException
    {
        setSampleLineItem(p);
    }

}
