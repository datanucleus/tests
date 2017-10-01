 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.7 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.remove.basic;

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


public class Client extends PMClientBase {

    private static A a[] = new A[10];

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
     * @testName: removeBasicTest1
     * @assertion_ids: PERSISTENCE:SPEC:617; PERSISTENCE:SPEC:628;
     *			PERSISTENCE:SPEC:629
     * @test_Strategy: A managed entity instance becomes removed by invoking the remove 
     *			method on it or by cascading the remove operation.  The semantics 
     *			of the remove operation, applied to an entity X are as follows:
     *
     *			If X is a new entity, it is ignored by the remove operation.
     *
     *                  Invoke remove on a new entity.
     *
     */

    public void removeBasicTest1() throws Fault
    {
      TLogger.log("Begin removeBasicTest1");
      boolean pass = false;
      A a1 = new A("1", "a1", 1);

      try {
          getEntityTransaction().begin();
          getEntityManager().remove(a1);
       	  pass = true;
          getEntityTransaction().commit();
      } catch (Exception fe) {
        TLogger.log("ERROR: Unexpected Exception during remove operation. " +
				"  Should have been ignored.");
	pass = false;
      	fe.printStackTrace();
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
            throw new Fault( "removeBasicTest1 failed"); }

    /*
     * @testName: removeBasicTest2
     * @assertion_ids: PERSISTENCE:SPEC:617; PERSISTENCE:SPEC:632
     * @test_Strategy: If X is a managed entity, the remove operation causes it to 
     *			transition to the removed state.
     *                  Invoke remove on a managed entity.
     *
     */

    public void removeBasicTest2() throws Fault
    {
      TLogger.log("Begin removeBasicTest2");
      boolean pass = false;
      A a1 = new A("2", "a2", 2);
      createA(a1);
      
      getEntityTransaction().begin();
      try {
	A newA = findA("2");
        getEntityManager().remove(newA);
	TLogger.log("Call contains after remove()");
	pass = ( ! getEntityManager().contains(newA) );
        getEntityTransaction().commit();
      } catch (Exception e) {
	  TLogger.log("Unexpected Exception :" + e );
	  pass = false;
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
            throw new Fault( "removeBasicTest2 failed");
    }


    /*
     * @testName: removeBasicTest3
     * @assertion_ids: PERSISTENCE:SPEC:617; PERSISTENCE:SPEC:636
     * @test_Strategy: If X is a removed entity, invoking the remove method on it will
     *                  be ignored.
     *
     *                  Invoke remove on a removed entity.
     *
     */

     public void removeBasicTest3() throws Fault
     {
      A a1 = new A("4", "a4", 4);
      boolean pass = false;
      boolean status = false;

      try {
	getEntityTransaction().begin();
        TLogger.log("Persist Instance");
        getEntityManager().persist(a1);

        if ( getEntityManager().contains(a1) ) {
             try {
		getEntityManager().remove(a1);
		getEntityManager().flush();

		A stillExists = findA("4");

                if ( stillExists == null ) {
                     getEntityManager().remove(a1);
                     pass = true;
                  }
              } catch (Exception e) {
                TLogger.log("Unexpected exception caught trying to remove" +
				" a removed entity, should have been ignored", e);
                pass = false;
              }
        } else {
          TLogger.log("Entity not managed, unexpected, test fails.");
          pass = false;
        }
        getEntityTransaction().commit();

      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
	e.printStackTrace();
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
            throw new Fault( "removeBasicTest3 failed");
     }


    /*
     * @testName: removeBasicTest4
     * @assertion_ids: PERSISTENCE:SPEC:617; PERSISTENCE:SPEC:637;
     *			PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:648
     * @test_Strategy: A removed entity will be removed from the database at or before
     *			transaction commit or as a result of a flush operation.
     *
     *                  Remove an entity.  Verify the entity is removed from the
     *			database at as a result of the flush operation.
     *
     *                  The flush method can be used for force synchronization.  The semantics
     *                  of the flush operation applied to an entity X is as follows:
     *
     *                  If X is a removed entity, it is removed from the database.
     *
     */

     public void removeBasicTest4() throws Fault
     {
      TLogger.log("Begin removeBasicTest4");
      boolean pass = false;
      boolean status = false;
      A a1 = new A("5", "a5", 5);
      getEntityTransaction().begin();
      getEntityManager().persist(a1);

           try {
	     A newA = findA("5");
	     if (null != newA ) {
		TLogger.log("Found newA, try Remove");
             	getEntityManager().remove(newA);
	     	getEntityManager().flush();

		TLogger.log("Removed, try to find and verify the entity has been removed");
	         newA = findA("5");
	         if (null == newA ) {
			TLogger.log("newA is Null as expected");
		  	pass = true;
	         }
       	     } else {
               TLogger.log("ERROR: Could not find persisted entity.");
               pass = false;
             }

	   getEntityTransaction().commit();
           } catch (Exception e) {
	     pass = false;
             TLogger.log("Unexpected Exception attempting to find removed entity:"
				 + e );
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

        if ( ! pass )
                throw new Fault( "removeBasicTest4 failed");
     }

    /*
     * @testName: removeBasicTest5
     * @assertion_ids: PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:673
     * @test_Strategy: The contains method [used to determine whether an entity
     *		       instance is in the managed state] returns false:
     *
     *  	       If the remove method has been called on the entity.
     *
     */

     public void removeBasicTest5() throws Fault
     {
      TLogger.log("Begin removeBasicTest5");
      boolean pass = false;
      boolean status = false;
      A a1 = new A("6", "a6", 6);
      createA(a1);

      try {
	getEntityTransaction().begin();

	A a2 = findA("6");
        getEntityManager().remove(a2);
	
	if ( ! getEntityManager().contains(a2) ) {
		pass = true;
	}

	getEntityTransaction().commit();
       } catch (Exception e) {
	 pass = false;
         TLogger.log("Unexpected Exception caught:" + e );
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
            throw new Fault( "removeBasicTest5 failed");
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

   private boolean getInstanceStatus(Object o) {
     TLogger.log("Entered getInstanceStatus method");
     return getEntityManager().contains(o);
   }


    public void cleanup()  throws Fault
    {
        TLogger.log("Cleanup existing entity data, if any");
	try {
          getEntityTransaction().begin();
          for (int i=1; i<10; i++ ) {
	  A aExists = getEntityManager().find(A.class, Integer.toString(i));
                if (aExists != null ) {
                   getEntityManager().remove(aExists);
		   TLogger.log("removed entity" +aExists);
		   getEntityManager().flush();
                }
          }
          getEntityTransaction().commit();
	} catch (Exception e) {
	  TLogger.log("Exception caught in cleanup while removing entities");
	  e.printStackTrace();
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
	TLogger.log("Cleanup complete, calling super.cleanup");
	super.cleanup();
    }

}


