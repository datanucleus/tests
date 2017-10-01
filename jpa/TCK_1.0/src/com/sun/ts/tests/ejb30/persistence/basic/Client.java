 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)Client.java	1.8 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.basic;

import javax.naming.InitialContext;
import java.util.Properties;
import com.sun.javatest.Status;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper;


public class Client extends PMClientBase implements java.io.Serializable  {
    public Client() {
    }
    
    public static void main(String[] args) {
        Client theTests = new Client();
        Status s=theTests.run(args, System.out, System.err);
        s.exit();
    }

    public void setup(String[] args, Properties p) throws Fault
    {
        TLogger.log("setup");
        try {

           super.setup(args, p);

        }  catch (Exception e) {
            TLogger.log("Exception: " + e.getMessage());
            throw new Fault("Setup failed:", e);
        }
    }

    
    /*
     * @testName: updateOrderTest
     * @assertion_ids:  PERSISTENCE:SPEC:500; PERSISTENCE:SPEC:501;
     *			PERSISTENCE:SPEC:503; PERSISTENCE:SPEC:504;
     *			PERSISTENCE:SPEC:505; PERSISTENCE:SPEC:506;
     *			PERSISTENCE:SPEC:507; PERSISTENCE:SPEC:508;
     *			PERSISTENCE:SPEC:932; PERSISTENCE:SPEC:936;
     *			PERSISTENCE:SPEC:939; PERSISTENCE:SPEC:943;
     *			PERSISTENCE:SPEC:946; PERSISTENCE:SPEC:930;
     *			PERSISTENCE:SPEC:1018; PERSISTENCE:SPEC:1019;
     *			PERSISTENCE:SPEC:1020; PERSISTENCE:SPEC:1021;
     *			PERSISTENCE:SPEC:1023; PERSISTENCE:SPEC:1025;
     *			PERSISTENCE:SPEC:848; PERSISTENCE:SPEC:856;
     *			PERSISTENCE:SPEC:908; PERSISTENCE:SPEC:909;
     *			PERSISTENCE:SPEC:914; PERSISTENCE:SPEC:915;
     *			PERSISTENCE:SPEC:925; PERSISTENCE:SPEC:918;
     *			PERSISTENCE:SPEC:928; PERSISTENCE:SPEC:929;
     *			PERSISTENCE:JAVADOC:149; PERSISTENCE:JAVADOC:152;
     *			PERSISTENCE:JAVADOC:163; PERSISTENCE:SPEC:846
     * @test_Strategy:  With basic entity requirements, persist/remove
     *			an entity.
     */
    public void updateOrderTest() throws Fault {
        int count = 5;
        Order[] orders = new Order[count];
	getEntityTransaction().begin();
        for(int i = 1; i < count; i++) {
            orders[i] = new Order(i, 100*i);
            getEntityManager().persist(orders[i]);
            TLogger.log("persisted order " + orders[i]);
        }
        for(int i = 1; i < count; i++) {
            getEntityManager().remove(orders[i]);
            TLogger.log("removed order " + orders[i]);
        }
        getEntityTransaction().commit();
    }

  public void cleanup() throws Fault {

        TLogger.log("In cleanup");
        try {
              if ( getEntityTransaction().isActive() ) {
                   getEntityTransaction().rollback();
              }
        } catch (Exception fe) {
          TLogger.log("ERROR: Unexpected exception rolling back TX:" + fe );
          fe.printStackTrace();
        }
        TLogger.log("Done cleanup, calling super.cleanup");
	super.cleanup();
   }

}

