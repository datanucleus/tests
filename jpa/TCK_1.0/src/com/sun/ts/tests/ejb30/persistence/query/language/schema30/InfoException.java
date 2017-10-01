/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)InfoException.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.query.language.schema30;

public class InfoException extends Exception {
    public String reason = null;

    public InfoException() {
	super();
    }

    public InfoException(String msg) {
	super(msg);
	reason = msg;
    }
}
