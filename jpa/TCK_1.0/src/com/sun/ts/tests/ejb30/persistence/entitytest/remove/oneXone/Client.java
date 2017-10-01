 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.10 06/09/13
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.remove.oneXone;

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
    private static B b[] = new B[10];


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
     * @testName: remove1X1Test1
     * @assertion_ids: PERSISTENCE:SPEC:628; PERSISTENCE:SPEC:629
     * @test_Strategy: A managed entity instance becomes removed by invoking the remove method
     *			on it or by cascading the remove operation.  The semantics of the remove
     *			operation, applied to an entity X are as follows:
     *
     *			If X is a new entity, it is ignored by the remove operation.
     *                  Invoke remove on a new entity.
     */

    public void remove1X1Test1() throws Fault
    {
      TLogger.log("Begin remove1X1Test1");
      boolean pass = false;
      A a1 = new A("1", "a1", 1);
      B bRef = new B("1", "b1", 1, a1);

      try {

      getEntityTransaction().begin();
      boolean result = getInstanceStatus(bRef);

      if ( ! result) {
	    TLogger.log("Instance state is not managed as expected. "+
		       "Try invoking remove on it.");
       	    getEntityManager().remove(bRef);
       	    pass = true;
      } else {
	    TLogger.log("ERROR:  Instance state is managed. " +
			       " Unexpected as this is NEW instance.");
	    pass = false;
        }
	getEntityTransaction().commit();
      } catch (Exception fe) {
        TLogger.log("ERROR: Unexpected exception during remove operation. " +
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
            throw new Fault( "remove1X1Test1 failed");
    }

    /*
     * @testName: remove1X1Test2
     * @assertion_ids: PERSISTENCE:SPEC:628; PERSISTENCE:SPEC:632
     * @test_Strategy: If X is a managed entity, the remove operation causes it to 
     *			transition to the removed state.
     *                  Invoke remove on a managed entity.
     *
     */

    public void remove1X1Test2() throws Fault
    {
      TLogger.log("Begin remove1X1Test2");
      boolean pass = true;
      A a1 = new A("2", "a2", 2);
      B bRef = new B("2", "b2", 2, a1);

      try {
      	getEntityTransaction().begin();
      	getEntityManager().persist(a1);
      	getEntityManager().persist(bRef);

      	TLogger.log("Get Instance Status ");
        if ( getInstanceStatus(bRef) ) {
		  TLogger.log("Status is true as expected, try remove()");
		  getEntityManager().remove(bRef);

		  TLogger.log("Call contains after remove()");
		  pass = getEntityManager().contains(bRef);
        } else {
          TLogger.log("ERROR:  Instance is not managed, cannot proceed with test");
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
        } catch (Exception fe) {
          TLogger.log("ERROR: Unexpected exception rolling back TX:" + fe );
          fe.printStackTrace();
        }
      }

      if ( pass)
             throw new Fault( "remove1X1Test2 failed");
    }

    /*
     * @testName: remove1X1Test3
     * @assertion_ids: PERSISTENCE:SPEC:628; PERSISTENCE:SPEC:633
     * @test_Strategy: A managed entity instance becomes removed by invoking the remove method
     *                  on it or by cascading the remove operation.  The semantics of the remove
     *                  operation, applied to an entity X are as follows:
     *
     *                  The remove operation is cascaded to entities referenced by X,
     *                  if the relationship from X to these other entities is annotated
     *                  with cascade=REMOVE annotation member.
     *
     *                  The cascade=REMOVE specification should only be applied to associations
     *                  that are specified as OneToOne or OneToMany.
     *
     *                  Invoke remove on a OneToOne relationship from X annotated
     *			with cascade=REMOVE and ensure the remove operation is cascaded.
     *
     */

     public void remove1X1Test3() throws Fault
     {
      TLogger.log("Begin remove1X1Test3");
      boolean pass = false;
      boolean status = false;

      try {
	
	getEntityTransaction().begin();
      	A a1 = new A("3", "a3", 3);
        getEntityManager().persist(a1);

        B bRef = new B("3", "a3", 3, a1);
        getEntityManager().persist(bRef);

      	TLogger.log("Get Instance Status ");
        A a2 = bRef.getA1();
        status = getInstanceStatus(bRef);

        if ( (status)  &&  (a2 == a1) ) {
              TLogger.log("Status is true as expected, try remove()");
               getEntityManager().remove(bRef);
               TLogger.log("Remove is immediately visible to the contains method");
		 if ( (! getEntityManager().contains(a2)) &&
				 ( ! getEntityManager().contains(bRef)) ) {
			pass = true;
		  }
        } else {
          TLogger.log("Instance is not managed- Unexpected");
          pass = false;
        }

	getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Expected Exception :" + e );
	  pass = false;
          e.printStackTrace();
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

        if ( ! pass )
                throw new Fault( "remove1X1Test3 failed");
     }

    /*
     * @testName: remove1X1Test4
     * @assertion_ids: PERSISTENCE:SPEC:628; PERSISTENCE:SPEC:636
     * @test_Strategy: If X is a removed entity, invoking the remove method on it will
     *                  be ignored.
     *                  Invoke remove on a removed entity.
     *
     */

     public void remove1X1Test4() throws Fault
     {
      boolean pass = false;
      boolean status = false;
      A a1 = new A("4", "a4", 4);
      B bRef = new B("4", "b4", 4, a1);

      try {
        getEntityTransaction().begin();
        getEntityManager().persist(a1);
        getEntityManager().persist(bRef);
        TLogger.log("Get Instance Status ");
        status = getEntityManager().contains(bRef);

        if ( status ) {
               TLogger.log("Entity is managed, remove");
               getEntityManager().remove(bRef);
	       getEntityManager().flush();

               B stillExists = findB("4");
               if ( stillExists == null ) {
                    getEntityManager().remove(bRef);
                    pass = true;
	       }
        } else {
          TLogger.log("Entity not managed, unexpected, test fails.");
          pass = false;
        }
	getEntityTransaction().commit();
      } catch (Exception e) {
	pass = false;
        TLogger.log("Unexpected Exception :" + e );
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
            throw new Fault( "remove1X1Test4 failed");
     }

    /*
     * @testName: remove1X1Test5
     * @assertion_ids: PERSISTENCE:SPEC:637; PERSISTENCE:SPEC:648
     * @test_Strategy: A removed entity will be removed from the database at or before
     *			transaction commit or as a result of a flush operation.
     *			Accessing an entity in the removed state is undefined.
     *
     *                  Remove an entity.  Verify the entity is removed from the
     *                  database at transaction commit.
     *
     */

     public void remove1X1Test5() throws Fault
     {
      TLogger.log("Begin remove1X1Test5");
      boolean pass = false;
      boolean status = false;
      A a1 = new A("5", "a5", 5);
      B bRef = new B("5", "a5", 5, a1);

      try {
	getEntityTransaction().begin();
        getEntityManager().persist(a1);
        getEntityManager().persist(bRef);

        TLogger.log("Get Instance ");
        A a2 = bRef.getA1();
        TLogger.log("Get Instance Status ");
        status = getInstanceStatus(bRef);

        if ( (status)  &&  (a2 == a1) ) {
                  getEntityManager().remove(bRef);
		  getEntityManager().flush();

		  B newB = findB("5");
                  if ( ( newB == null ) && ( ! getEntityManager().contains(bRef) ) &&
				(! getEntityManager().contains(a1) ) ) {
                        pass = true;
                  }
        } else {
          TLogger.log("Instance is not managed- Unexpected");
          pass = false;
        }
	getEntityTransaction().commit();
      } catch (Exception e) {
	pass = false;
        TLogger.log("Unexpected Exception :" + e );
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

      if ( ! pass )
              throw new Fault( "remove1X1Test5 failed");
     }

    /*
     * @testName: remove1X1Test6
     * @assertion_ids: PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:648
     * @test_Strategy: A removed entity will be removed from the database at or before
     *			transaction commit or as a result of a flush operation.
     *			Accessing an entity in the removed state is undefined.
     *
     *                  Remove an entity and force the removal using flush().  Verify
     *			the entity is removed from the database.
     *
     *  		The flush method can be used for force synchronization.  The semantics
     *                  of the flush operation applied to an entity X is as follows:
     *
     *                  If X is a removed entity, it is removed from the database.
     *
     */


     public void remove1X1Test6() throws Fault
     {
      TLogger.log("Begin remove1X1Test6");
      boolean pass = false;
      boolean status = false;

      getEntityTransaction().begin();
      A a1 = new A("6", "a6", 6);
      getEntityManager().persist(a1);
      B bRef = new B("6", "a6", 6, a1);
      getEntityManager().persist(bRef);

      TLogger.log("Get Instance ");
      A a2 = bRef.getA1();
      TLogger.log("Get Instance Status ");
      status = getInstanceStatus(bRef);

        if ( (status)  &&  (a2 == a1) ) {
                try {
                  TLogger.log("Status is true as expected, try remove()");
                  getEntityManager().remove(findB("6"));
                  getEntityManager().flush();
                  TLogger.log("A removed entity is removed from the database " +
                                "as a result of the flush operation");
                  B newB = findB("6");
                  if ( ( newB == null ) && ( ! getEntityManager().contains(bRef) ) &&
                                (! getEntityManager().contains(a1) ) ) {
                         pass = true;
                  }
                  getEntityTransaction().commit();

                } catch (Exception onfe) {
                  TLogger.log("Unexpected Exception :" + onfe );
                }
        } else {
          TLogger.log("Instance is not managed- Unexpected");
          pass = false;
        }

        if ( ! pass )
                throw new Fault( "remove1X1Test6 failed");
     }


    /*
     * @testName: remove1X1Test7
     * @assertion_ids: PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:673
     * @test_Strategy: The contains method [used to determine whether an entity
     *		       instance is in the managed state] returns false:
     *
     *  	       If the remove method has been called on the entity.
     *
     */

     public void remove1X1Test7() throws Fault
     {
      TLogger.log("Begin remove1X1Test7");
      boolean pass = false;
      boolean status = false;

      try {
      getEntityTransaction().begin();
      A a1 = new A("7", "a7", 7);
      getEntityManager().persist(a1);
      B bRef = new B("7", "a7", 7, a1);
      getEntityManager().persist(bRef);

      TLogger.log("Get Instance ");
      A a2 = bRef.getA1();
      TLogger.log("Get Instance Status ");
      status = getEntityManager().contains(bRef);

        if ( (status)  &&  (a2 == a1) ) {
                  TLogger.log("Status is true as expected, try remove()");
                  getEntityManager().remove(bRef);

		  if (! getEntityManager().contains(bRef)) {
		     pass = true;
	  	  }
         } else {
           TLogger.log("Instance is not managed- Unexpected");
           pass = false;
         }
	getEntityTransaction().commit();
      } catch (Exception e) {
	pass = false;
        TLogger.log("Unexpected Exception :" + e );
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
              throw new Fault( "remove1X1Test7 failed");
     }


    /*
     * @testName: remove1X1Test8
     * @assertion_ids: PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:674
     * @test_Strategy: The contains method [used to determine whether an entity
     *		 	instance is in the managed state] returns false:
     *
     *  	       If the remove operation has been cascaded to it.
     *
     */

     public void remove1X1Test8() throws Fault
     {
      TLogger.log("Begin remove1X1Test8");
      boolean pass = false;
      boolean status = false;
      try {
	getEntityTransaction().begin();
        A a1 = new A("8", "a8", 8);
        getEntityManager().persist(a1);
        B bRef = new B("8", "a8", 8, a1);
        getEntityManager().persist(bRef);

        TLogger.log("Get Instance ");
        A a2 = bRef.getA1();
        TLogger.log("Get Instance Status ");
        status = getInstanceStatus(bRef);

        if ( (status)  &&  (a2 == a1) ) {
              getEntityManager().remove(bRef);

              if (! getEntityManager().contains(a2) ) {
                     pass = true;
              }
        } else {
          TLogger.log("Instance is not managed- Unexpected");
          pass = false;
        }
	getEntityTransaction().commit();
      } catch (Exception e) {
	pass = false;
        TLogger.log("Unexpected Exception :" + e );
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
               throw new Fault( "remove1X1Test8 failed");
     }


    /*
     * @testName: remove1X1Test9
     * @assertion_ids:  PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:630
     * @test_Strategy: A managed entity instance becomes removed by invoking the remove method
     *                  on it or by cascading the remove operation.  The semantics of the remove
     *                  operation, applied to an entity X are as follows:
     *
     *                  If X is a new entity, it is ignored by the remove
     *                  operation.  However, the remove operation is cascaded to entities
     *                  referenced by X, if the relationships from X to these other entities is
     *                  annotated with cascade=REMOVE annotation member value.
     *
     */

    public void remove1X1Test9() throws Fault
    {
      TLogger.log("Begin remove1X1Test9");
      boolean pass = false;
      boolean status = false;

      try {
      getEntityTransaction().begin();
      A a1 = new A("9", "a9", 9);
      getEntityManager().persist(a1);
      B bRef = new B("9", "b9", 9, a1);

      TLogger.log("Get Instance Status ");
      status = getEntityManager().contains(a1);

      if ( status ) {
          TLogger.log("A Entity is persisted, bRef is new, remove should be ignored on bRef, a1 should be removed");
          getEntityManager().remove(bRef);
	  getEntityManager().flush();
          TLogger.log("Call contains after remove()");
          status = getEntityManager().contains(a1);

          TLogger.log("Call contains after remove()");
	  A stillExists = findA("9");
          if ( ( ! status )  && ( stillExists == null ) ) {
             pass = true;
          }
        } else {
          TLogger.log("Instance is not managed- Unexpected");
          pass = false;
        }

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
              throw new Fault( "remove1X1Test9 failed");
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

   private boolean getInstanceStatus(Object o) {
     TLogger.log("Entered getInstanceStatus method");
     return getEntityManager().contains(o);
   }


   public void cleanup()  throws Fault
   {
     TLogger.log("Cleanup existing entity data");
     try {
          getEntityTransaction().begin();
          for (int i=1; i<10; i++ ) {
   	  B newB = findB(Integer.toString(i));
                if (newB != null ) {
                   getEntityManager().remove(newB);
                   TLogger.log("removed entity" +newB);
                }
          }

          for (int i=1; i<10; i++ ) {
   	  A newA = findA(Integer.toString(i));
                if (newA != null ) {
                   getEntityManager().remove(newA);
                   TLogger.log("removed entity" +newA);
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


