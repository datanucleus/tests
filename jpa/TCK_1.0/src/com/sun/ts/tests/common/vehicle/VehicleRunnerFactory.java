/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)VehicleRunnerFactory.java	1.13 06/02/11
 */

package com.sun.ts.tests.common.vehicle;

import java.util.*;
import com.sun.javatest.*;
import com.sun.ts.tests.common.vehicle.*;
import com.sun.ts.lib.harness.*;
import com.sun.ts.lib.util.*;

public final class VehicleRunnerFactory {
    private static VehicleRunnable ejbRunner;
    private static VehicleRunnable servletRunner;
    private static VehicleRunnable jspRunner;
    private static VehicleRunnable emptyRunner;
    private static VehicleRunnable stateless3Runner;
    private static VehicleRunnable stateful3Runner;
    private static VehicleRunnable appmanagedRunner;
    private static VehicleRunnable appmanagedNoTxRunner;
    private static VehicleRunnable jbiRunner;
    private static VehicleRunnable wsejbRunner;
    private static VehicleRunnable wsservletRunner;
    private static VehicleRunnable pmservletRunner;
    private static VehicleRunnable puservletRunner;
    private static VehicleRunnable webRunner;

    private VehicleRunnerFactory() {}
    private static VehicleRunnable getEJBRunner() {
	if(ejbRunner == null) {
	    try {
	        Class c =
		Class.forName("com.sun.ts.tests.common.vehicle.ejb.EJBVehicleRunner");
	        ejbRunner = (VehicleRunnable) c.newInstance();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return ejbRunner;
    }

    private static VehicleRunnable getJBIRunner() {
	if(jbiRunner == null) {
	    try {
	        Class c =
		Class.forName("com.sun.ts.tests.common.vehicle.jbi.JBIVehicleRunner");
	        jbiRunner = (VehicleRunnable) c.newInstance();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return jbiRunner;
    }
    
    private static VehicleRunnable getServletRunner() {
	if(servletRunner == null) {
	    try {
	        Class c =
		    Class.forName("com.sun.ts.tests.common.vehicle.servlet.ServletVehicleRunner");
	        servletRunner = (VehicleRunnable) c.newInstance();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return servletRunner;
    }
    
    private static VehicleRunnable getJSPRunner() {
	if(jspRunner == null) {
	    try {
	        Class c =
		    Class.forName("com.sun.ts.tests.common.vehicle.jsp.JSPVehicleRunner");
	        jspRunner = (VehicleRunnable) c.newInstance();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return jspRunner;
    }
    
    private static VehicleRunnable getWebRunner() {
	if(webRunner == null) {
	    try {
	        Class c =
		    Class.forName("com.sun.ts.tests.common.vehicle.web.WebVehicleRunner");
	        webRunner = (VehicleRunnable) c.newInstance();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return webRunner;
    }
    
    private static VehicleRunnable getEmptyRunner() {
	if(emptyRunner == null) {
	    try {
	        Class c =
		    Class.forName("com.sun.ts.tests.common.vehicle.EmptyVehicleRunner");
	        emptyRunner = (VehicleRunnable) c.newInstance();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return emptyRunner;
    }
    private static VehicleRunnable getStateless3Runner() {
        if(stateless3Runner == null) {
	    try {
	        Class c =
		Class.forName("com.sun.ts.tests.common.vehicle.stateless3.Stateless3VehicleRunner");
	        stateless3Runner = (VehicleRunnable) c.newInstance();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return stateless3Runner;
    }
    private static VehicleRunnable getStateful3Runner() {
        if(stateful3Runner == null) {
	    try {
	        Class c =
		Class.forName("com.sun.ts.tests.common.vehicle.stateful3.Stateful3VehicleRunner");
	        stateful3Runner = (VehicleRunnable) c.newInstance();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return stateful3Runner;
    }
    private static VehicleRunnable getAppManagedRunner() {
        if(appmanagedRunner == null) {
            try {
                Class c =
                Class.forName("com.sun.ts.tests.common.vehicle.appmanaged.AppManagedVehicleRunner");
                appmanagedRunner = (VehicleRunnable) c.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return appmanagedRunner;
    }
    private static VehicleRunnable getAppManagedNoTxRunner() {
        if(appmanagedNoTxRunner == null) {
            try {
                Class c =
                Class.forName("com.sun.ts.tests.common.vehicle.appmanagedNoTx.AppManagedNoTxVehicleRunner");
                appmanagedNoTxRunner = (VehicleRunnable) c.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return appmanagedNoTxRunner;
    }
    private static VehicleRunnable getWSEJBRunner() {
	if(wsejbRunner == null) {
	    try {
	        Class c =
		Class.forName("com.sun.ts.tests.common.vehicle.wsejb.WSEJBVehicleRunner");
	        wsejbRunner = (VehicleRunnable) c.newInstance();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return wsejbRunner;
    }
    private static VehicleRunnable getWSServletRunner() {
	if(wsservletRunner == null) {
	    try {
	        Class c =
		Class.forName("com.sun.ts.tests.common.vehicle.wsservlet.WSServletVehicleRunner");
	        wsservletRunner = (VehicleRunnable) c.newInstance();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return wsservletRunner;
    }
    private static VehicleRunnable getPMServletRunner() {
        if(pmservletRunner == null) {
            try {
                Class c =
                Class.forName("com.sun.ts.tests.common.vehicle.pmservlet.PMServletVehicleRunner");
                pmservletRunner = (VehicleRunnable) c.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return pmservletRunner;
    }
    private static VehicleRunnable getPUServletRunner() {
        if(puservletRunner == null) {
            try {
                Class c =
                Class.forName("com.sun.ts.tests.common.vehicle.puservlet.PUServletVehicleRunner");
                puservletRunner = (VehicleRunnable) c.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return puservletRunner;
    }
    //runners are stateless and thus can be cached and reused.
    //But we cannot have reference to ejb vehicle directory in
    //order to compile this class in any tck's.
    public static VehicleRunnable getVehicleRunner(String vtype) {
	if(vtype.equalsIgnoreCase("ejb")) {
	    return getEJBRunner();
	} else if(vtype.equalsIgnoreCase("servlet")) {
	    return getServletRunner();
	} else if(vtype.equalsIgnoreCase("jsp")) {
	    return getJSPRunner();
        } else if(vtype.equalsIgnoreCase("web")) {
	    return getWebRunner();
        } else if(vtype.equalsIgnoreCase("stateless3")) {
            return getStateless3Runner();
        } else if(vtype.equalsIgnoreCase("stateful3")) {
            return getStateful3Runner();
        } else if(vtype.equalsIgnoreCase("appmanaged")) {
            return getAppManagedRunner();
        } else if(vtype.equalsIgnoreCase("appmanagedNoTx")) {
            return getAppManagedNoTxRunner();
	} else if(vtype.equalsIgnoreCase("jbi")) {
            return getJBIRunner();
	} else if(vtype.equalsIgnoreCase("wsejb")) {
            return getWSEJBRunner();
	} else if(vtype.equalsIgnoreCase("wsservlet")) {
            return getWSServletRunner();
	} else if(vtype.equalsIgnoreCase("pmservlet")) {
            return getPMServletRunner();
	} else if(vtype.equalsIgnoreCase("puservlet")) {
            return getPUServletRunner();
	} else {
	      if(!vtype.equalsIgnoreCase("appclient")
	       && !vtype.equalsIgnoreCase("wsappclient")
               && !vtype.equalsIgnoreCase("standalone")) {
		TestUtil.logMsg("Invalid vehicle " + vtype + ". Will run test directly.");
	    }
	    return getEmptyRunner();
	}
    }
}
