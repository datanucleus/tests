 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.8 06/07/12
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.detach.manyXone;

import com.sun.javatest.Status;
import com.sun.ts.lib.util.*;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper;
import java.util.Properties;
import java.util.List;
import java.util.Collection;
import java.util.Vector;
import java.util.Iterator;

import javax.persistence.*;

public class Client extends PMClientBase {


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
            e.printStackTrace();
            	throw new Fault("Setup failed:", e);
        }
    }


    /* 
     *  BEGIN Test Cases
     */


   /*
     * @testName: detachMX1Test1
     * @assertion_ids:  PERSISTENCE:SPEC:625; PERSISTENCE:SPEC:742;
     *			PERSISTENCE:SPEC:1092; PERSISTENCE:SPEC:1093
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *
     *                  If X is a detached object and the persist
     *                  method is invoked on it, an IllegalArgumentException is thrown or
     *                  the commit() will fail.  Check for an IllegalArgumentException,
     *                  if not thrown, be sure the persist method was not successful 
     *                  by invoking find().
     *                  Invoke persist on a detached entity.
     *
     */

    public void detachMX1Test1() throws Fault
    {
      TLogger.log("Begin detachMX1Test1");
      boolean pass = false;
      A aRef = new A("3", "a3", 3);

      try {
      	TLogger.log("Persist Instance");
      	createA(aRef);
	getEntityManager().clear();

	getEntityTransaction().begin();
        TLogger.log("Call contains to determine if the instance is detached");

         if ( getEntityManager().contains(aRef) ) {
              TLogger.log("Entity is not detached, unexpected cannot proceed with test.");
              pass = false;
	 } else {

           try {
               TLogger.log("Status is false as expected, try perist()");
               getEntityManager().persist(aRef);
           } catch (IllegalArgumentException iae) {
               TLogger.log("IllegalArgumentException thrown trying to" +
			" persist a detached entity", iae);
               pass = true;
           } catch (EntityExistsException eee) {
               TLogger.log("IllegalArgumentException thrown trying to" +
			" persist an existing entity", eee);
               pass = true;
           }
        }

	 getEntityTransaction().commit();
       } catch (Exception e) {
         TLogger.log("OR, Transaction commit will fail.  "
			+ " Test the commit failed by testing"
			+ " the transaction is marked for rollback");

        if ( ( ! pass) &&
	    (e instanceof javax.transaction.TransactionRolledbackException ||
             e instanceof javax.persistence.PersistenceException)) {
             pass = true;
        }

      } finally {
        try {
              if ( getEntityTransaction().isActive() ) {
                   getEntityTransaction().rollback();
              }
        } catch (Exception fe) {
          TLogger.log("ERROR: Unexpected exception rolling back TX:" + fe );
          fe.printStackTrace();
        }

      }

      if (!pass)
        throw new Fault( "detachMX1Test1 failed");
    }

   /*
    *
    *  Business Methods to set up data for Test Cases
    *
    */

   private void createA(A a)
   {
     TLogger.log("Entered createA method");
     getEntityTransaction().begin();
     getEntityManager().persist(a);
     getEntityTransaction().commit();
   }

   private A findA(String id)
   {
      TLogger.log("Entered findA method");
      return getEntityManager().find(A.class, id);
   }

   private void createB(B b)
   {
      TLogger.log("Entered createB method");
      getEntityTransaction().begin();
      getEntityManager().persist(b);
      getEntityTransaction().commit();
   }

   private B findB(String id)
   {
      TLogger.log("Entered findB method");
      return getEntityManager().find(B.class, id);
   }

    private void dumpCollectionDataA(Collection c)
    {
        TLogger.log("Collection Data");
        TLogger.log("---------------");
        TLogger.log("- size=" + c.size());
        Iterator i = c.iterator();
        int elem = 1;
        while(i.hasNext()) {
            A v = (A)i.next();
            TLogger.log("- Element #" + elem++);
            TLogger.log("  id=" + v.getAId() +
                   ", name=" + v.getAName() +
                   ", value=" + v.getAValue());
        }
    }

    private void dumpCollectionDataB(Collection c)
    {
        TLogger.log("Collection Data");
        TLogger.log("---------------");
        TLogger.log("- size=" + c.size());
        Iterator i = c.iterator();
        int elem = 1;
        while(i.hasNext()) {
            B v = (B)i.next();
            TLogger.log("- Element #" + elem++);
            TLogger.log("  id=" + v.getBId() +
                   ", name=" + v.getBName() +
                   ", value=" + v.getBValue());
        }
    }


    public void cleanup()  throws Fault
    {
       try{
       getEntityTransaction().begin();
          for (int i=1; i<20; i++ ) {
          A newA = findA(Integer.toString(i));
                if (newA != null ) {
                    getEntityManager().remove(newA);
                    TLogger.log("removed entity A:  " + newA);
                }
          }

          for (int i=1; i<20; i++ ) {
          B newB = findB(Integer.toString(i));
                if (newB != null ) {
                    getEntityManager().remove(newB);
                    TLogger.log("removed entity B:  " + newB);
                }
          }
        getEntityTransaction().commit();

        } catch (Exception e) {
          TLogger.log("Exception caught cleaning up entities", e);
          e.printStackTrace();
        } finally {
          try {
              if (getEntityTransaction().isActive() ) {
                  getEntityTransaction().rollback();
              }
        } catch (Exception re) {
          TLogger.log("ERROR: Unexpected exception rolling back transaction");
          re.printStackTrace();
        }
       }

	TLogger.log("Cleanup OK, calling super.cleanup");
	super.cleanup();
    }


}


