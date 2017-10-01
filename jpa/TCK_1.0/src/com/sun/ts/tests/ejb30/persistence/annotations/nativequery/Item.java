/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


/*
 * @(#)Item.java	1.4 06/03/28
 */

package com.sun.ts.tests.ejb30.persistence.annotations.nativequery;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.CascadeType;


/*
 * Item
 */

@Entity
@Table(name="ITEM")
public class Item implements java.io.Serializable
{


    // Instance variables
    private String id;
    private String itemName;
    private Order1 order1;

    public Item()
    {
    }

    public Item (String id, String itemName)
    {
        this.id = id;
        this.itemName = itemName;
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

    @Column(name="ITEMNAME")
    public String getItemName() {
	return itemName;
    }
    public void setItemName(String itemName) {
	this.itemName = itemName;
    }

    @OneToOne(mappedBy="item")
    public Order1 getOrder1()
    {
        return order1;
    }
    public void setOrder1(Order1 v)
    {
        order1 = v;
    }

}
