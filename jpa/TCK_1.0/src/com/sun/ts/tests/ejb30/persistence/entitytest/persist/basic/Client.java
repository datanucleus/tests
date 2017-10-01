 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.5 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.persist.basic;

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
     * @testName: persistBasicTest1
     * @assertion_ids: PERSISTENCE:SPEC:613; PERSISTENCE:SPEC:614;
     *			PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:675
     * @test_Strategy:  A new entity bean instance has no persistent identity and
     *                  is not yet associated to a persistent context.
     *
     *			The contains method [used to determine whether an entity
     *			instance is in the managed state in the current persistence
     *			context ] returns false:
     *
     *			If the instance is new and the persist method has not been 
     *			on the entity.
     *
     *                  Instantiate an entity and verify the contains method returns false.
     */

    public void persistBasicTest1() throws Fault
    {

      TLogger.log("Begin persistBasicTest1");

      boolean pass = false;
      A aRef = null;
      try {

      aRef = new A("1", "A1", 1);

      getEntityTransaction().begin();
      if ( ! getInstanceStatus(aRef) ) {
         pass = true;
      }
      getEntityTransaction().commit();

      } catch (Exception e) {
        TLogger.log("Unexpected Exception:" + e );
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
        throw new Fault( "persistBasicTest1 failed");

    }

    /*
     * @testName: persistBasicTest2
     * @assertion_ids: PERSISTENCE:SPEC:613; PERSISTENCE:SPEC:615;
     *			PERSISTENCE:SPEC:619; PERSISTENCE:SPEC:667;
     *			PERSISTENCE:SPEC:669
     * @test_Strategy:  The new entity bean instance becomes both managed and
     *                  persistent by invoking the persist method on it. The
     *			semantics of the persist operation as applied to entity
     *			X is as follows:
     *
     *                  If X is a new entity, it becomes managed.
     *
     *                  Invoke persist on the new entity.
     *                  Find the entity instance and ensure it is managed by
     *                  calling contains() verifying it returns true.
     */

    public void persistBasicTest2() throws Fault
    {

      TLogger.log("Begin persistBasicTest2");

      boolean pass = false;
      A aRef = null;

      try {
      TLogger.log("DEBUG: new A");
      aRef = new A("2", "a2", 2);
      createA(aRef);

      A newA = findA("2");

	if ( newA != null ) {
        	TLogger.log("DEBUG: A IS NOT NULL");
	}

      getEntityTransaction().begin();
      pass = getInstanceStatus(findA("2"));
      getEntityTransaction().commit();

      } catch (Exception e) {
        TLogger.log("Unexpected Exception:" + e );
        e.printStackTrace();
      } finally {
        try {
	      if (getEntityTransaction().isActive() ) {
                  getEntityTransaction().rollback();
	      }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
        throw new Fault( "persistBasicTest2 failed");

    }


    /*
     * @testName: persistBasicTest3
     * @assertion_ids: PERSISTENCE:SPEC:613; PERSISTENCE:SPEC:618;
     *			PERSISTENCE:SPEC:620
     * @test_Strategy:  The new entity bean instance becomes both managed and
     *                  persistent by invoking the persist method on it. The
     *			semantics of the persist operation as applied to entity
     *			X is as follows:
     *
     *                  The entity X will be entered into the database at or before
     *                  transaction commit or as a result of the flush operation.
     *
     *                  Create a new entity instance, invoke flush(), then attempt to
     *                  access the entity by find and invoking a query on it.
     *
     */

    public void persistBasicTest3() throws Fault
    {

      TLogger.log("Begin persistBasicTest3");

      String aName = null;
      boolean pass = false;
      Object result = null;
      A a1 = null;

      try {
	getEntityTransaction().begin();
        a1 = new A("3", "a3", 3);
        TLogger.log("Persist Instance");
	getEntityManager().persist(a1);	
	getEntityManager().flush();

		 TLogger.log("find By Name");	
                  result =  (A)findByName("a3");

		 TLogger.log("Check to see that the entities are identical");	
                  if ( result == a1 ) {
                        pass = true;
                  }
	getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
        e.printStackTrace();
      } finally {
        try {
	      if (getEntityTransaction().isActive() ) {
                  getEntityTransaction().rollback();
	      }
        } catch (Exception fe) {
          TLogger.log("ERROR: Unexpected exception rolling back transaction");
          fe.printStackTrace();
        }
      }

      if (!pass)
        throw new Fault( "persistBasicTest3 failed");

     }


    /*
     * @testName: persistBasicTest4
     * @assertion_ids: PERSISTENCE:SPEC:613; PERSISTENCE:SPEC:618;
     *			PERSISTENCE:SPEC:621
     * @test_Strategy:  The new entity bean instance becomes both managed and
     *                  persistent by invoking the persist method on it. The
     *			semantics of the persist operation as applied to entity
     *			X is as follows:
     *
     *                  If X is preexisting managed entity, it is ignored by
     *			the persist operation.
     *
     *                  Invoke persist on an already managed instance.  Ensure
     *			no exception is thrown and that the entity is still
     *			persisted and managed.
     *
     */

    public void persistBasicTest4() throws Fault
    {

      TLogger.log("Begin persistBasicTest4");
      A aRef = new A("4", "a4", 4);

      boolean pass = false;
      try {

      TLogger.log("Persist Instance");
      createA(aRef);

	getEntityTransaction().begin();
        TLogger.log("Get Instance Status ");
        if ( getInstanceStatus(findA("4")) ) {
                try {
                  TLogger.log("Entity is managed, try to persist again ");
		  A newA = findA("4");
                  getEntityManager().persist(newA);
                  TLogger.log("Persist ignored on an already persisted entity as expected");
                  pass = true;
                } catch (Exception ee) {
                  TLogger.log("Unexpected exception trying to persist an" +
				" already persisted entity", ee);
                  pass = false;
                }

        } else {
          TLogger.log("Instance is not managed. Test Fails.");
          pass = false;
        }

	getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
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

      if (!pass)
        throw new Fault( "persistBasicTest4 failed");
    }



    /*
     * @testName: persistBasicTest5
     * @assertion_ids: PERSISTENCE:SPEC:613; PERSISTENCE:SPEC:618;
     *			PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:642
     * @test_Strategy:  The flush method can be used for force synchronization.  The semantics
     *                  of the flush operation applied to an entity X is as follows:
     *
     *                  If X is a managed entity, it is synchronized to the database.
     *
     *			Execute flush on a managed entity and ensure the database reflects
     *			the change.
     *
     */


    public void persistBasicTest5() throws Fault
    {

      TLogger.log("Begin persistBasicTest5");
      A aRef = new A("5", "a5", 5);
      A a2 = null;

      boolean pass = false;
      try {

      TLogger.log("Persist Instance");
      createA(aRef);

	getEntityTransaction().begin();
        TLogger.log("Get Instance Status ");
        if ( getInstanceStatus(findA("5")) ) {
                try {
                  TLogger.log("Entity is managed, try to change name and flush ");
		  a2 = findA("5");
		  a2.setAName("a2");
                  getEntityManager().flush();
		  	if (a2.getAName().equals("a2")) { 
                        TLogger.log("Sync to database successful");
                  	pass = true;
		 	}
                } catch (Exception ee) {
                  TLogger.log("Unexpected exception trying to flush a" +
                                "persisted entity", ee);
                  pass = false;
                }

        } else {
          TLogger.log("Instance is not already persisted. Test Fails.");
          pass = false;
        }
	getEntityTransaction().commit();

      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
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

      if (!pass)
        throw new Fault( "persistBasicTest5 failed");
    }


   /* 
    *  Business Methods for Test Cases
    */


    private void createA(A a)
    {
      TLogger.log("Entered createA method");
      getEntityTransaction().begin();
      getEntityManager().persist(a);
      // WORKAROUND
      getEntityManager().flush();
      getEntityTransaction().commit();
    }

   private A findA(String id)
   {
      TLogger.log("Entered findA method");
      return getEntityManager().find(A.class, id);
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
          for (int i=1; i<7; i++ ) {
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


