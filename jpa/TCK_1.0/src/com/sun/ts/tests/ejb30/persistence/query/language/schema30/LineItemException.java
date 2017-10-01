/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)LineItemException.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

public class LineItemException extends Exception {
    public String reason = null;

    public LineItemException() {
	super();
    }

    public LineItemException(String msg) {
	super(msg);
	reason = msg;
    }
}
