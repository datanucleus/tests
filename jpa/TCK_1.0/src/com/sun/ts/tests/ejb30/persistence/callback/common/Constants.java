 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)Constants.java	1.5 06/03/27
 */

package com.sun.ts.tests.ejb30.persistence.callback.common;

public interface Constants {
    public static final String prePersistRuntimeExceptionTest = "prePersistRuntimeExceptionTest";
    public static final String prePersistTest = "prePersistTest";
    public static final String prePersistMultiTest = "prePersistMultiTest";
    public static final String preRemoveTest = "preRemoveTest";
    public static final String preRemoveMultiTest = "preRemoveMultiTest";
    public static final String preUpdateTest = "preUpdateTest";
    public static final String preUpdateMultiTest = "preUpdateMultiTest";
    public static final String postLoadTest = "postLoadTest";
    public static final String postLoadMultiTest = "postLoadMultiTest";
    public static final String prePersistCascadeTest = "prePersistCascadeTest";
    public static final String prePersistMultiCascadeTest = "prePersistMultiCascadeTest";
    public static final String preRemoveCascadeTest = "preRemoveCascadeTest";
    public static final String preRemoveMultiCascadeTest = "preRemoveMultiCascadeTest";
    
    public static final String prePersistRuntimeExceptionTest2 = "prePersistRuntimeExceptionTest2";
    public static final String prePersistTest2 = "prePersistTest2";
    public static final String preRemoveTest2 = "preRemoveTest2";
    public static final String preUpdateTest2 = "preUpdateTest2";
    public static final String postLoadTest2 = "postLoadTest2";
    public static final String prePersistCascadeTest2 = "prePersistCascadeTest2";
    public static final String preRemoveCascadeTest2 = "preRemoveCascadeTest2";
    
    public static final String LISTENER_A = "ListenerA";
    public static final String LISTENER_B = "ListenerB";
    public static final String LISTENER_C = "ListenerC";
    public static final String[] LISTENER_ABC = new String[]{LISTENER_A, LISTENER_B, LISTENER_C}; 
    public static final String[] LISTENER_BC = new String[]{LISTENER_B, LISTENER_C}; 
    public static final String GENERIC_LISTENER = "GenerictListener";
    public static final String PRODUCT = "Product";
    public static final String ORDER = "Order";
    public static final String LINE_ITEM = "LineItem";
    public static final String PART_PRODUCT = "PartProduct";
    public static final String PRICED_PART_PRODUCT = "PricedPartProduct";
    public static final String PRICED_PART_PRODUCT_2 = "PricedPartProduct_2";
}
