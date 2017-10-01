/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)HardwareProduct.java	1.7 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/*
 * HardwareProduct
 */

@Entity
@DiscriminatorValue("HW")
public class HardwareProduct extends Product
		implements java.io.Serializable
{

    // Instance variables
    private int modelNumber;

    public HardwareProduct()
    {
	super();
    }


   // ===========================================================
   // getters and setters for the state fields

    @Column(name="MODEL", nullable=true)
    public int getModelNumber() {
        return modelNumber;
    }
    public void setModelNumber(int modelNumber) {
        this.modelNumber = modelNumber;
    }

}
