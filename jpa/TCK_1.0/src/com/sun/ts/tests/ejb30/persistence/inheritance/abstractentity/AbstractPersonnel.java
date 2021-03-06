/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)AbstractPersonnel.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.abstractentity;


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
