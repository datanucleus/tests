/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)AddressException.java	1.2 06/02/11
 */


package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

public class AddressException extends Exception {
    public String reason = null;

    public AddressException() {
	super();
    }

    public AddressException(String msg) {
	super(msg);
	reason = msg;
    }
}
