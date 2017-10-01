/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)ServiceLocator.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.common.helper;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;

public class ServiceLocator {
    
    private ServiceLocator() {
    }
    
    public static Object lookup(String name) throws NamingException {
        return lookup(name, null);
    }
    
    public static Object lookupByShortName(String shortName) throws NamingException {
        return lookup("java:comp/env/" + shortName, null);
    }
    
    public static Object lookup(Class type) throws NamingException {
        return lookup(null, type);
    }
    
    /**
     * Looks up a resource by its name or fully qualified type name.  If name is
     * not null, then use it to look up and type is ignored.  If name is null,
     * then try to use the fully qualified class name of type.
     * 
     */
    public static Object lookup(String name, Class type) throws NamingException {
        String nameToUse = null;
        if(name == null) {
            nameToUse = type.getName();
        } else {
            nameToUse = name;
        }
        Context context = new InitialContext();
        return context.lookup(nameToUse);
    }  
}
