/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)SoftwareProduct.java	1.7 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;

/*
 * SoftwareProduct
 */


@Entity
@DiscriminatorValue("SW")
public class SoftwareProduct extends Product
		implements java.io.Serializable
{

    // Instance variables
    private double revisionNumber;

    public SoftwareProduct()
    {
	super();
    }


   // ===========================================================
   // getters and setters for the state fields

    @Column(name="REV", nullable=true)
    public double getRevisionNumber() {
        return revisionNumber;
    }
    public void setRevisionNumber(double revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

}
