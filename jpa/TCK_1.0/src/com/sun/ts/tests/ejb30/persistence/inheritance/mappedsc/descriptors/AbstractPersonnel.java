/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)AbstractPersonnel.java	1.1 06/02/21
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.mappedsc.descriptors;


/*
 * AbstractPersonnel
 */

public abstract class AbstractPersonnel {

	public String getFullTimeRep() {
		return "Mabel Murray";
	}

	public String getPartTimeRep() {
		return "John Cleveland";
	}
}
