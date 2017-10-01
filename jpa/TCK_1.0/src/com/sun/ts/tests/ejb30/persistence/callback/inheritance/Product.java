/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Product.java	1.8 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.callback.inheritance;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;

import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusImpl;

@Entity
@Table(name="PRODUCT_TABLE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "PRODUCT_TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Product")
@EntityListeners(com.sun.ts.tests.ejb30.persistence.callback.inheritance.ProductListener.class)
public class Product extends CallbackStatusImpl 
        implements java.io.Serializable, CallbackStatusIF {
    private String id;
    private String name;
    private int quantity;
    
    public Product() {
        super();
    }
    
    public Product(String id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }
    
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
    
    @Column(name="QUANTITY")
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int v) {
        this.quantity = v;
    }
}
