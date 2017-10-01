/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Info.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import java.util.*;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;


/*
 * Info
 */

@Entity
@Table(name="INFO_TABLE")
public class Info implements java.io.Serializable {

    // Instance variables
    private String id;
    private String street;
    private String city;
    private String state;
    private String zip;
    private Spouse spouse;

    public Info() {
    }

    public Info(String v1, String v2, String v3, String v4, String v5) 
    {
	id = v1;
	street = v2;
	city = v3;
	state = v4;
	zip = v5;
    }

    public Info(String v1, String v2, String v3, String v4,
		String v5, Spouse v6)
    {
	id = v1;
	street = v2;
	city = v3;
	state = v4;
	zip = v5;
	spouse = v6;
    }

    // ===========================================================
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

    @Column(name="INFOSTREET")
    public String getStreet()
    {
	return street;
    }
    public void setStreet(String v)
    {
	street = v;
    }

    @Column(name="INFOSTATE")
    public String getState()
    {
        return state;
    }
    public void setState(String v)
    {
        state = v;
    }

    @Column(name="INFOCITY")
    public String getCity()
    {
	return city;
    }
    public void setCity(String v)
    {
	city = v;
    }

    @Column(name="INFOZIP")
    public String getZip()
    {
        return zip;
    }  
    public void setZip(String v)
    {
        zip = v;
    }

    // ===========================================================
    // getters and setters for association fields

    //ONEXONE
    @OneToOne(mappedBy="info")
    public Spouse getSpouse()
    {
	return spouse;
    }
    public void setSpouse(Spouse v)
    {
	this.spouse = v;
    }

}
