 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

 /*
  * @(#)Coffee.java	1.6 06/02/11
  */

package com.sun.ts.tests.ejb30.persistence.entitytest.apitests;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedNativeQueries;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.EntityResult;

@NamedNativeQueries({
	@NamedNativeQuery(name="findAllSQLCoffees2",
        	query="select * from COFFEE",
		resultClass=com.sun.ts.tests.ejb30.persistence.entitytest.apitests.Coffee.class),
	@NamedNativeQuery(name="findAllSQLCoffees",
        	query="select * from COFFEE", resultSetMapping="CoffeeResult")
})
@SqlResultSetMapping(name="CoffeeResult",
	entities=@EntityResult(entityClass=com.sun.ts.tests.ejb30.persistence.entitytest.apitests.Coffee.class))
@NamedQueries({
	@NamedQuery(name="findAllCoffees",
		 query="Select Distinct c from Coffee c"),
	@NamedQuery(name="findAllNewCoffees",
		query="Select NEW com.sun.ts.tests.ejb30.persistence.entitytest.apitests.Coffee(c.id, c.brandName, c.price) from Coffee c where c.price <> 0")
})
@Entity
@Table(name = "COFFEE")
public class Coffee implements java.io.Serializable {

    private Integer id;
    private String brandName;
    private float price;
    
    public Coffee() {
    }
    
    public Coffee(Integer id, String brandName, float price) {
	this.id = id;
	this.brandName = brandName;
        this.price = price;
    }
    
    @Id
    @Column(name="ID")
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Column(name="BRANDNAME")
    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String bName) {
        this.brandName = bName;
    }
    
    @Column(name="PRICE")
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }


    public String toString() {
        return "Coffee id=" + getId() + ", brandName=" + getBrandName() + ", price=" + getPrice();
    }
}
