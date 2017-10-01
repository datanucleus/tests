/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Country.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Basic;


@Embeddable
public class Country implements java.io.Serializable {

    // Instance variables
    private String country;
    private String code;

    public Country() {
    }
	

    public Country(String v1, String v2)
    {
	country = v1;
	code = v2;
    }

    @Basic
    public String getCountry()
    {
	return country;
    }
    public void setCountry(String v)
    {
	country = v;
    }

    @Basic
    public String getCode()
    {
	return code;
    }
    public void setCode(String v)
    {
	code = v;
    }
}
