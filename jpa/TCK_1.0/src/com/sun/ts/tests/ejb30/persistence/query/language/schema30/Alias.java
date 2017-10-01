/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Alias.java	1.8 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.CascadeType;
import java.util.Collection;


/*
 * Alias
 */

@Entity
@Table(name="ALIAS_TABLE")
public class Alias implements java.io.Serializable
{


    // Instance variables
    private String id;
    private String alias;
    private Customer customerNoop;
    private Collection<Customer> customersNoop = new java.util.ArrayList<Customer>();
    private Collection<Customer> customers = new java.util.ArrayList<Customer>();

    public Alias() {
    }

    public Alias(String id, String alias)
    {
      this.id = id;
      this.alias = alias;
    }

    // ===========================================================
    // getters and setters for persistent fields
 
    @Id
    @Column(name="ID")
    public String getId() {
	return id;
    }
    public void setId(String id) {
	this.id = id;
    }
 
    @Column(name="ALIAS")
    public String getAlias() {
	return alias;
    }
    public void setAlias(String alias) {
	this.alias = alias;
    }
 
    // ===========================================================
    // getters and setters for relationship fields
 
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(
	name="FK1_FOR_CUSTOMER_TABLE", insertable=false, updatable=false)
    public Customer getCustomerNoop() {
	return customerNoop;
    }
    public void setCustomerNoop(Customer customerNoop) {
	this.customerNoop = customerNoop;
    }
 
    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="FKS_ANOOP_CNOOP",
    	joinColumns=
    		@JoinColumn(
			name="FK2_FOR_ALIAS_TABLE", referencedColumnName="ID"),
    	inverseJoinColumns=
    		@JoinColumn(
			name="FK8_FOR_CUSTOMER_TABLE", referencedColumnName="ID")
    )
    public Collection<Customer> getCustomersNoop() {
	return customersNoop;
    }
    public void setCustomersNoop(Collection<Customer> customersNoop) {
	this.customersNoop= customersNoop;
 
    }

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="FKS_ALIAS_CUSTOMER",
   	joinColumns=
    		@JoinColumn(
			name="FK_FOR_ALIAS_TABLE", referencedColumnName="ID"),
   	inverseJoinColumns=
    		@JoinColumn(
			name="FK_FOR_CUSTOMER_TABLE", referencedColumnName="ID")
    )
    public Collection<Customer> getCustomers() {
	return customers;
    }
    public void setCustomers(Collection<Customer> customers) {
	this.customers = customers;
    }


}
