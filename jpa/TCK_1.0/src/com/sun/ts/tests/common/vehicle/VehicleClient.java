/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * %W%   %E%
 */


package  com.sun.ts.tests.common.vehicle;

import  com.sun.javatest.Status;
import  com.sun.ts.lib.util.*;
import  com.sun.ts.lib.harness.ServiceEETest;
/**
 * Class used as a client of all vehicle tests.
 */
public class VehicleClient extends ServiceEETest
{
    String[] sVehicles;
    private static Object theSharedObject = null;
 
    /* Run test in standalone mode */
    public static void main(String[] args) {
      VehicleClient client = new VehicleClient();
      Status s = client.run(args, System.out, System.err);
      s.exit();
    }

    /*
     * Set shared object
     */
    public static void setClientSharedObject(Object o) {
     theSharedObject = o;
    }

    /*
     * Get shared object
     */
    public static Object getClientSharedObject() {
      return theSharedObject;
    }  

}
