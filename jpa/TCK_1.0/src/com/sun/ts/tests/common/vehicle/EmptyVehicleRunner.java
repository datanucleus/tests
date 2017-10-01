/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * %W%   %E%
 */

package com.sun.ts.tests.common.vehicle;

import java.util.*;
import com.sun.ts.lib.harness.*;
import com.sun.ts.lib.util.TestUtil;
import com.sun.javatest.*;

public class EmptyVehicleRunner implements VehicleRunnable {

    public Status run (String[] argv, Properties p) {
	
        ServiceEETest theTestClient;
        Status sTestStatus = Status.passed("");
        
        //create an instance of the test client and run here
        try
        {
            Class c =
            Class.forName(p.getProperty("test_classname"));
            theTestClient = (ServiceEETest) c.newInstance();
            theTestClient.setSharedObject(VehicleClient.getClientSharedObject());
            sTestStatus = theTestClient.run(argv, p);
        }
        catch(ClassNotFoundException cnfe)
        {
            TestUtil.logErr("Failed to create the EETest instance", cnfe);
            sTestStatus = Status.failed("Failed to create the EETest instance");
        }
        catch(InstantiationException ie)
        {
            TestUtil.logErr("Failed to create the EETest instance", ie);
            sTestStatus = Status.failed("Failed to create the EETest instance");
        }
        catch(Exception e)
        {
            TestUtil.logErr("Failed running in a client side vehicle", e);
            sTestStatus = Status.failed("Failed running in a client side vehicle");
        }       
        
        return sTestStatus;
    }
}
