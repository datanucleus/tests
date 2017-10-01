/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Product.java	1.8 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import java.util.Collection;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.SecondaryTable;
import javax.persistence.Embedded;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.DiscriminatorType;


/*
 * 
 * Product
 *	  
 */


@Entity
@Table(name="PRODUCT_TABLE")
@SecondaryTable(name="PRODUCT_DETAILS", pkJoinColumns=@PrimaryKeyJoinColumn(name="ID"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "PRODUCT_TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Product")
public class Product implements java.io.Serializable
{

    // Instance variables
    private String id;
    private String name;
    private double price;
    private int quantity;
    private long partNumber;
    private String wareHouse;
    private ShelfLife shelfLife;


    public Product()
    {
    }

    public Product (String id, String name, double price, int quantity,
			long partNumber)
    {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.partNumber = partNumber;
    }

    // ===========================================================
    // getters and setters for State fields

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

    @Column(name="WHOUSE", nullable=true, table="PRODUCT_DETAILS")
    public String getWareHouse() {
	return wareHouse;
    }
    public void setWareHouse(String v) {
	this.wareHouse = v;
    }

    @Embedded
     @AttributeOverrides({
        @AttributeOverride(name="inceptionDate",
           column=@Column(name="INCEPTION", nullable=true)),
        @AttributeOverride(name="soldDate",
            column=@Column(name="SOLD", nullable=true))
      })
    public ShelfLife getShelfLife() {
	return shelfLife;
    }
    public void setShelfLife(ShelfLife v) {
	this.shelfLife = v;
    }

    public String toString() {
        return "Product id = " + getId() + ", Product Name = " + getName() + " , Product Quantity = " +
			getQuantity() + " , Price: " + getPrice() + ", Part Number: " + getPartNumber();
    }

}
