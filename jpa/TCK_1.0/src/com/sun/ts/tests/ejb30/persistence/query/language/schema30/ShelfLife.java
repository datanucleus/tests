/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)ShelfLife.java	1.8 06/02/14
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Basic;
import javax.persistence.Column;
import java.sql.Date;

/*
 * ShelfLife 
 */

@Embeddable
public class ShelfLife implements java.io.Serializable {

    // Instance variables
    private Date inceptionDate;
    private Date soldDate;

    public ShelfLife()
    {
    }


    public ShelfLife(Date d1, Date d2)
    {
	inceptionDate = d1;
	soldDate = d2;
    }

    @Basic
    public Date getInceptionDate()
    {
	return inceptionDate;
    }
    public void setInceptionDate(Date d1)
    {
	inceptionDate = d1;
    }

    @Basic
    public Date getSoldDate()
    {
	return soldDate;
    }
    public void setSoldDate(Date d2)
    {
	soldDate = d2;
    }

}
