/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)PricedPartProduct_2.java	1.8 06/06/26
 */

package com.sun.ts.tests.ejb30.persistence.callback.inheritance;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Transient;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusIF;
import com.sun.ts.tests.ejb30.persistence.callback.common.CallbackStatusImpl;


/**
 * All callbacks are defined in entity superclass.  The super class of this 
 * entity is not an entity.
 */

@Entity
@Table(name="PRICED_PRODUCT_TABLE")
public class PricedPartProduct_2 extends PricedPartProductCallback
        implements java.io.Serializable, CallbackStatusIF {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private long partNumber;
    private CallbackStatusImpl callbackStatus = new CallbackStatusImpl();
    
    public PricedPartProduct_2() {
        super();
    }
    
    @Id
    @Column(name="ID")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    @Column(name="NAME")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name="PRICE")
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    
    @Column(name="QUANTITY")
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int v) {
        this.quantity = v;
    }
    
    @Column(name="PNUM")
    public long getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(long v) {
        this.partNumber = v;
    }

    @Transient
    public CallbackStatusImpl getCallbackStatus() {
        return callbackStatus;
    }
    public void setCallbackStatus(CallbackStatusImpl callbackStatus) {
        this.callbackStatus = callbackStatus;
    }
    /////////////////////////////////////////////////////////////////////////
    public void setPreUpdateCalled(boolean b) {
        getCallbackStatus().setPreUpdateCalled(b);
    }

    public void setPreRemoveCalled(boolean b) {
        getCallbackStatus().setPreRemoveCalled(b);
    }

    public void setPrePersistCalled(boolean b) {
        getCallbackStatus().setPrePersistCalled(b);
    }

    public void setPostLoadCalled(boolean b) {
        getCallbackStatus().setPostLoadCalled(b);
    }

    public void setPostPersistCalled(boolean b) {
        getCallbackStatus().setPostPersistCalled(b);
    }

    public void setPostRemoveCalled(boolean b) {
        getCallbackStatus().setPostRemoveCalled(b);
    }

    public void setPostUpdateCalled(boolean b) {
        getCallbackStatus().setPostUpdateCalled(b);
    }
    
    public void setTestName(String s) {
        getCallbackStatus().setTestName(s);
    }

    @Transient
    public String getEntityName() {
        return "PricedPartProduct_2";
    }

    @Transient
    public String getTestName() {
        return getCallbackStatus().getTestName();
    }

    @Transient
    public boolean isPostLoadCalled() {
        return getCallbackStatus().isPostLoadCalled();
    }

    @Transient
    public boolean isPostPersistCalled() {
        return getCallbackStatus().isPostPersistCalled();
    }

    @Transient
    public boolean isPostRemoveCalled() {
        return getCallbackStatus().isPostRemoveCalled();
    }

    @Transient
    public boolean isPostUpdateCalled() {
        return getCallbackStatus().isPostUpdateCalled();
    }

    @Transient
    public boolean isPrePersistCalled() {
        return getCallbackStatus().isPrePersistCalled();
    }

    @Transient
    public boolean isPreRemoveCalled() {
        return getCallbackStatus().isPreRemoveCalled();
    }

    @Transient
    public boolean isPreUpdateCalled() {
        return getCallbackStatus().isPreUpdateCalled();
    }
}
