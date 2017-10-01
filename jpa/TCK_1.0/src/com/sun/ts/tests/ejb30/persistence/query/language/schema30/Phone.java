/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


/*
 * @(#)Phone.java	1.5 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/*
 * Phone
 */

@Entity
@Table(name="PHONE_TABLE")
public class Phone implements java.io.Serializable {

    // Instance variables
    private String id;
    private String area;
    private String number;
    private Address address;

    public Phone()
    {
    }

    public Phone(String v1, String v2, String v3)
    {
	id = v1;
	area = v2;
	number = v3;
    }

    public Phone(String v1, String v2, String v3, Address v4)
    {
	id = v1;
	area = v2;
	number = v3;
	address = v4;
    }


    // getters and setters for state fields
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

    @Column(name="AREA")
    public String getArea()
    {
	return area;
    }
    public void setArea(String v)
    {
	area = v;
    }

    @Column(name="PHONE_NUMBER")
    public String getNumber()
    {
	return number;
    }
    public void setNumber(String v)
    {
	number = v;
    }
    // ===========================================================
    // getters and setters for Association fields

    // Manyx1

    @ManyToOne
    @JoinColumn(
	name="FK_FOR_ADDRESS")
    public Address getAddress()
    {
	return address;
    }
    public void setAddress(Address a)
    {
	address = a;
    }

}
