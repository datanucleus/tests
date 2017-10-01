/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)AnnualReview.java	1.5 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.relationship.defaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;

/*
 * AnnualReview
 */

@Entity
public class AnnualReview implements java.io.Serializable {

    // Instance variables
    private Integer  id;
    private Integer  service;

    public AnnualReview() {
    }

    public AnnualReview(Integer id, Integer service) {
        this.id = id;
        this.service = service;
    }


    // ===========================================================
    // getters and setters for the state fields

    @Id
    public Integer getAid() {
        return id;
    }
    public void setAid(Integer id) {
        this.id = id;
    }

    public Integer getService() {
        return service;
    }
    public void setService(Integer service) {
        this.service = service;
    }

}

