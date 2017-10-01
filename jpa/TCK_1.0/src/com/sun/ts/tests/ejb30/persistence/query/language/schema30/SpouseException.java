/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)SpouseException.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

public class SpouseException extends Exception {
    public String reason = null;

    public SpouseException() {
	super();
    }

    public SpouseException(String msg) {
	super(msg);
	reason = msg;
    }
}
