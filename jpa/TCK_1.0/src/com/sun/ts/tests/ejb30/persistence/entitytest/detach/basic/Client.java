 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 *  @(#)Client.java	1.8 06/07/12
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.detach.basic;

import com.sun.javatest.Status;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import java.util.Properties;

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
            e.printStackTrace();
            throw new Fault("Setup failed:", e);

        }
    }


    /* 
     *  BEGIN Test Cases
     */


    /*
     * @testName: detachBasicTest1
     * @assertion_ids: PERSISTENCE:SPEC:635
     * @test_Strategy: If X is a detached entity, invoking the remove method on it will
     *                  cause an IllegalArgumentException to be thrown or the transaction
     *                  commit will fail.
     *                  Invoke remove on a detached entity.
     *
     */

    public void detachBasicTest1() throws Fault
    {
      TLogger.log("Begin detachBasicTest1");
      boolean pass = false;
      boolean status = false;
      A aRef = new A("1", "a1", 1);

      try {

        TLogger.log("Persist Instance");
        createA(aRef);

        getEntityManager().clear();

        getEntityTransaction().begin();
        TLogger.log("TX started, see if entity is detached");
        if ( getEntityManager().contains(aRef)) {
        	TLogger.log("contains method returned true; expected false"
			+ " (detached), test fails.");
          pass = false;
	} else {

          try {
                TLogger.log("Try remove");
                getEntityManager().remove(aRef);
          } catch (IllegalArgumentException iae) {
            TLogger.log("IllegalArgumentException caught as expected", iae);
            pass = true;
          }

        }

        TLogger.log("TX commit");
	getEntityTransaction().commit();

     } catch (Exception e) {
       TLogger.log("OR, Transaction commit will fail. "
			+" Test the commit failed by testing"
			+" the transaction is marked for rollback");
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
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "detachBasicTest1 failed");
    }



   /* 
    *  Business Methods for Test Cases
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

    public void cleanup()  throws Fault
    {
	try {
          getEntityTransaction().begin();
          for (int i=1; i<5; i++ ) {
          A newA = getEntityManager().find(A.class, Integer.toString(i));
                if (newA != null ) {
                    getEntityManager().remove(newA);
                    TLogger.log("removed entity A:  " + newA);
                }
          }
	 getEntityManager().flush();
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


