 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)CallbackStatusIF.java	1.3 06/02/26
 */

package com.sun.ts.tests.ejb30.persistence.callback.common;

import java.util.List;

public interface CallbackStatusIF {
    String getTestName();
    void setTestName(String s);
    String getEntityName();
    
    boolean isPrePersistCalled();
    List getPrePersistCalls();
    void setPrePersistCalled(boolean b);
    void addPrePersistCall(String shortName);
    
    boolean isPostPersistCalled();
    List getPostPersistCalls();
    void setPostPersistCalled(boolean b);
    void addPostPersistCall(String shortName);
    
    boolean isPreRemoveCalled();
    List getPreRemoveCalls();
    void setPreRemoveCalled(boolean b);
    void addPreRemoveCall(String shortName);
    
    boolean isPostRemoveCalled();
    List getPostRemoveCalls();
    void setPostRemoveCalled(boolean b);
    void addPostRemoveCall(String shortName);
    
    boolean isPreUpdateCalled();
    List getPreUpdateCalls();
    void setPreUpdateCalled(boolean b);
    void addPreUpdateCall(String shortName);
    
    boolean isPostUpdateCalled();
    List getPostUpdateCalls();
    void setPostUpdateCalled(boolean b);
    void addPostUpdateCall(String shortName);
    
    boolean isPostLoadCalled();
    List getPostLoadCalls();
    void setPostLoadCalled(boolean b);
    void addPostLoadCall(String shortName);
}
