 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.10 06/07/12
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.detach.oneXone;

import com.sun.javatest.Status;
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
        TLogger.log("Entering Setup");
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
     * @testName: detach1X1Test1
     * @assertion_ids: PERSISTENCE:SPEC:625; PERSISTENCE:SPEC:742
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *
     * 			If X is a detached object and the persist
     *                  method is invoked on it, an IllegalArgumentException is thrown
     *			or an EntityExistsException, or the transaction commit will fail. 
     *                  Invoke persist on a detached entity.
     */

    public void detach1X1Test1() throws Fault
    {

      TLogger.log("Begin detach1X1Test1");
      boolean pass = false;
      A aRef = new A("1", "a1", 1);

      try {

      TLogger.log("Persist Instance");
      createA(aRef);

      getEntityManager().clear();

      getEntityTransaction().begin();
      TLogger.log("Call contains to determine if the instance is detached");

        if ( getEntityManager().contains(aRef)) {
	     TLogger.log("Entity is not detached, cannot proceed with test.");
	     pass = false;
	} else {
                try {
                  TLogger.log("Status is false as expected, try perist()");
                        getEntityManager().persist(aRef);
                } catch (IllegalArgumentException iae) {
                  TLogger.log("IllegalArgumentException thrown trying to persist" +
				" a detached entity", iae);
                  pass = true;
                } catch (EntityExistsException eee) {
                  TLogger.log("EntityExistsException thrown trying to persist " +
				"an existing entity", eee);
                  pass = true;
 		}
        }

	getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("OR, Transaction commit will fail."
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
            throw new Fault( "detach1X1Test1 failed");
    }


    /*
     * @testName: detach1X1Test2
     * @assertion_ids: PERSISTENCE:SPEC:635
     * @test_Strategy: If X is a detached entity, invoking the remove method on it will
     *                  cause an IllegalArgumentException to be thrown or the transaction
     *                  commit will fail.
     *                  Invoke remove on a detached entity.
     *
     */

    public void detach1X1Test2() throws Fault
    {
      TLogger.log("Begin detach1X1Test2");
      boolean pass = false;
      A a1 = new A("2", "a2", 2);
      B bRef = new B("2", "a2", 2, a1);

      try {
        TLogger.log("Persist B");
        createB(bRef);
	getEntityManager().clear();

        getEntityTransaction().begin();
        TLogger.log("Get Instance Status ");

      	if ( getEntityManager().contains(bRef)) {
             TLogger.log("contains method returned true; unexpected, test fails.");
             pass = false;
        } else {

          try {
             TLogger.log("Status is false as expected, try remove");
             getEntityManager().remove(bRef);
          } catch (IllegalArgumentException iae) {
            TLogger.log("IllegalArgumentException caught as expected "
			+ " trying to remove a detached entity", iae);
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
          throw new Fault( "detach1X1Test2 failed");
    }



   /* 
    *  Business Methods to set up data for Test Cases
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

   private Object findByName(String name)
   {
      TLogger.log("Entered findByName method");
      return getEntityManager().createQuery(
	"select a from A a where a.name = :name")
        .setParameter("name", name)
        .getSingleResult();
   }

   private boolean getInstanceStatus(Object o) {
     TLogger.log("Entered getInstanceStatus method");
     return getEntityManager().contains(o);
   }

   public void cleanup()  throws Fault
   {
       try {
       getEntityTransaction().begin();

          for (int i=1; i<6; i++ ) {
          B newB = getEntityManager().find(B.class, Integer.toString(i));
                if (newB != null ) {
                    getEntityManager().remove(newB);
                    TLogger.log("removed entity B:  " + newB);
                }
          }

          for (int i=1; i<6; i++ ) {
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


