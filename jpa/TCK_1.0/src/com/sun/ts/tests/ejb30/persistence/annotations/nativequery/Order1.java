/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


/*
 * @(#)Order1.java	1.5 06/09/11
 */

package com.sun.ts.tests.ejb30.persistence.annotations.nativequery;

import java.util.*;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.ColumnResult;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;

/*
 * Order1
 */

@SqlResultSetMappings({
@SqlResultSetMapping(name="Order1ItemResults",
        entities={
	@EntityResult(
		entityClass=com.sun.ts.tests.ejb30.persistence.annotations.nativequery.Order1.class),
	 @EntityResult(
		entityClass=com.sun.ts.tests.ejb30.persistence.annotations.nativequery.Item.class) }),
@SqlResultSetMapping(name="Order2ItemResults",
        entities={
        @EntityResult(
                entityClass=com.sun.ts.tests.ejb30.persistence.annotations.nativequery.Order1.class,
                fields={
                        @FieldResult(name="id", column="OID"),
                        @FieldResult(name="totalPrice", column="OPRICE"),
                        @FieldResult(name="item", column="OITEM")})},
                columns={
                        @ColumnResult(name="INAME")}),
@SqlResultSetMapping(name="Order3ItemResults",
        entities={
        @EntityResult(
                entityClass=com.sun.ts.tests.ejb30.persistence.annotations.nativequery.Order1.class,
                fields={
                        @FieldResult(name="id", column="THISID"),
                        @FieldResult(name="totalPrice", column="THISPRICE"),
                        @FieldResult(name="item", column="THISITEM") }),
        @EntityResult(
                entityClass=com.sun.ts.tests.ejb30.persistence.annotations.nativequery.Item.class) })
})
@Entity
@Table(name="ORDER1")
public class Order1 implements java.io.Serializable
{


    // Instance variables
    private String id;
    private double totalPrice;
    private Item item;

    public Order1()
    {
    }

    public Order1 (String id, double totalPrice)
    {
        this.id = id;
        this.totalPrice = totalPrice;
    }

    public Order1 (String id)
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

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(
        name="FK1_FOR_ITEM")
    public Item getItem()
    {
        return item;
    }
    public void setItem(Item v)
    {
        item = v;
    }

}
