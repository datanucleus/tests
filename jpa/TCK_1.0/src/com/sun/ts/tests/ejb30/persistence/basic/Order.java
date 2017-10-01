 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)Order.java	1.3 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.basic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PURCHASE_ORDER")
public class Order implements java.io.Serializable {
    private int id;
    private int total;
    
    public Order() {
    }
    
    public Order(int total) {
        this.total = total;
    }
    
    public Order(int id, int total) {
        this.id = id;
        this.total = total;
    }
    
    @Id
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public String toString() {
        return "Order id=" + getId() + ", total=" + getTotal();
    }
}
