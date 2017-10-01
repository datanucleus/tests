/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)XAnnualReview.java	1.2 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.relationship.descriptors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/*
 * XAnnualReview
 */

public class XAnnualReview implements java.io.Serializable {

    // Instance variables
    private Integer xAid;
    private Integer xService;

    public XAnnualReview() {
    }

    public XAnnualReview(Integer xAid, Integer xService) {
        this.xAid = xAid;
        this.xService = xService;
    }


    // ===========================================================
    // getters and setters for the state fields

    public Integer getXAid() {
        return xAid;
    }
    public void setXAid(Integer xAid) {
        this.xAid = xAid;
    }

    public Integer getXService() {
        return xService;
    }
    public void setXService(Integer xService) {
        this.xService = xService;
    }

}

