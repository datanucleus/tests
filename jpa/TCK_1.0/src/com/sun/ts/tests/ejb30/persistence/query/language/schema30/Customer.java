/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Customer.java	1.10 06/07/10
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import java.util.Collection;


/*
 * Customer
 */

@Entity
@Table(name="CUSTOMER_TABLE")
public class Customer implements java.io.Serializable
{

    // Instance variables
    private String id;
    private String name;
    private Address home;
    private Address work;
    private Country country;
    private Spouse spouse;
    private Collection<CreditCard> creditCards = new java.util.ArrayList<CreditCard>();
    private Collection<Order> orders = new java.util.ArrayList<Order>();
    private Collection<Alias> aliases = new java.util.ArrayList<Alias>();
    private Collection<Alias> aliasesNoop = new java.util.ArrayList<Alias>();

    public Customer()
    {
    }

    public Customer (String id, String name)
    {
	this.id = id;
	this.name = name;
    }

    public Customer (String id, String name, Country country)
    {
	this.id = id;
	this.name = name;
	this.country = country;
    }

    public Customer (String id, String name, Address home,
                Address work, Country country)
    {
	this.id = id;
	this.name = name;
	this.home = home;
	this.work = work;
	this.country = country;
    }

    // ===========================================================
    // getters and setters for CMP fields
 
    @Id
    @Column(name="ID")
    public String getId() {
	return id;
    }
    public void setId(String v) {
	this.id = v;
    }
 
    @Column(name="NAME")
    public String getName() {
	return name;
    }
    public void setName(String v) {
	this.name = v;
    }
 
    @Embedded
    @Column(name="COUNTRY")
    public Country getCountry() {
	return country;
    }
    public void setCountry(Country v) {
	this.country = v;
    }
 
    // ===========================================================
    // getters and setters for State fields
 
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="FK6_FOR_CUSTOMER_TABLE")
    public Address getHome() {
	return home;
    }
    public void setHome(Address v) {
	this.home = v;
    }
 
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="FK5_FOR_CUSTOMER_TABLE")
    public Address getWork() {
	return work;
    }
    public void setWork(Address v) {
	this.work = v;
    }
 
    @OneToOne(mappedBy="customer")
    public Spouse getSpouse() {
	return spouse;
    }
    public void setSpouse(Spouse v) {
	this.spouse = v;
    }

    @OneToMany(cascade=CascadeType.ALL, mappedBy="customer")
    public Collection<CreditCard> getCreditCards() {
	return creditCards;
    }
    public void setCreditCards(Collection<CreditCard> v) {
	this.creditCards = v;
    }
 
    @OneToMany(cascade=CascadeType.ALL, mappedBy="customer")
    public Collection<Order> getOrders() {
	return orders;
    }
    public void setOrders(Collection<Order> v) {
	this.orders = v;
    }

    @ManyToMany(mappedBy="customers")
    public Collection<Alias> getAliases() {
	return aliases;
    }
    public void setAliases(Collection<Alias> v) {
	this.aliases = v;
    }

    @ManyToMany(mappedBy="customersNoop")
    public Collection<Alias> getAliasesNoop() {
	return aliasesNoop;
    }
    public void setAliasesNoop(Collection<Alias> v) {
	this.aliasesNoop = v;
    }

    public boolean equals(Object o) {
        Customer other;
        boolean same = true;

        if (! (o instanceof Customer)) {
            return false;
        }
        other = (Customer) o;

        same &= this.id.equals(other.id);

        return same;
    }


   public int hashCode() {
        int myHash;

        myHash = this.id.hashCode();

        return myHash;
   }

}
