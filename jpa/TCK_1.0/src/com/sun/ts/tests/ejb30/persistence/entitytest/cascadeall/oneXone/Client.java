 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.6 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.cascadeall.oneXone;

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
     * @testName: cascadeAll1X1Test1
     * @assertion_ids:  PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:623;
     *			PERSISTENCE:JAVADOC:133; PERSISTENCE:JAVADOC:137
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *			The perist operation is cascaded to entities referenced by X, if 
     *			the relationship from X to these other entities is annotated with
     *			cascade=ALL annotation member.
     *
     *                  Invoke persist on a OneToOne relationship from X annotated
     *			with cascade=ALL and ensure the persist operation is cascaded.
     *			Entity B is annotated with ALL so call persist from there.
     *
     */

     public void cascadeAll1X1Test1() throws Fault
     {
     TLogger.log("Begin cascadeAll1X1Test1");
     boolean pass = false;
     A a1 = null;
     A a2 = null;
     B bRef = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
     	a1 = new A("1", "a1", 1);
     	bRef = new B("1", "bean1", 1, a1);
    	getEntityManager().persist(bRef);

	TLogger.log("getA1");
        a2 = bRef.getA1();

        if ( (a1 == a2) && (getEntityManager().contains(bRef)) ) {
             pass = true;
        } else {
             TLogger.log("ERROR:  Unexpected results - test fails.");
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

      if (!pass)
            throw new Fault( "cascadeAll1X1Test1 failed");
     }


    /*
     * @testName: cascadeAll1X1Test2
     * @assertion_ids:  PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:624
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *
     *			If X is a removed entity, it becomes managed.
     *
     *			Create an entity, persist it, remove it, and invoke persist again.
     *			Check that is is managed and is accessible.
     *
     */

    public void cascadeAll1X1Test2() throws Fault
    {
      TLogger.log("Begin cascadeAll1X1Test2");
      B bRef = null;
      A a1 = null;

      boolean pass = false;

      try {
	 getEntityTransaction().begin();
         a1 = new A("2", "a2", 2);
         bRef = new B("2", "b2", 2, a1);
         TLogger.log("Persist Instance");
         getEntityManager().persist(bRef);

         TLogger.log("Get Instance Status ");
         if (getInstanceStatus(findB("2")) ) {
            try {
              TLogger.log("Entity is managed, remove it ");
              getEntityManager().remove(findB("2"));
	      getEntityManager().flush();

              TLogger.log("Persist a removed entity");
              getEntityManager().persist(bRef);
              pass = getInstanceStatus(bRef); 
           } catch (Exception ee) {
             TLogger.log("Unexpected exception trying to persist a removed entity", ee);
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
	      if ( getEntityTransaction().isActive() ) {
	      	   getEntityTransaction().rollback();
	      }
 	} catch (Exception fe) {
          TLogger.log("ERROR: Unexpected exception rolling back TX:" + fe );
          fe.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "cascadeAll1X1Test2 failed");
    }

    /*
     *
     * @testName: cascadeAll1X1Test3
     * @assertion_ids:  PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:627
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *
     *			For all entities Y referenced by a relationship from X, if the
     *			relationship to Y has been annotated with the cascade member
     *			value cascade=ALL, the persist operation is applied to Y.
     *
     *                  Invoke persist on a OneToOne relationship from X where Y is
     *			annotated with cascade=ALL and ensure the persist operation
     *			is cascaded.
     *
     */

     public void cascadeAll1X1Test3() throws Fault
     {

     TLogger.log("Begin cascadeAll1X1Test3");
     boolean pass = false;
     B bRef = null;
     A a1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("3", "a3", 3);
        bRef = new B("3", "bean3", 3, a1);
        getEntityManager().persist(bRef);

        A a2 = bRef.getA1();

         if ( (a1 == a2) && (getEntityManager().contains(a2)) )  {
              pass = true;
         }
         else {
              TLogger.log("ERROR:  Unexpected results received - test failed");
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

      if (!pass)
            throw new Fault( "cascadeAll1X1Test3 failed");
     }


    /*
     * @testName: cascadeAll1X1Test4
     * @assertion_ids: PERSISTENCE:SPEC:667; PERSISTENCE:SPEC:670
     * @test_Strategy: The contains method [used to determine whether an entity instance
     *		       is in the managed state] returns true:
     *  	       If the entity instance is new and the persist operation has been
     *		       cascaded to it.
     *
     */

     public void cascadeAll1X1Test4() throws Fault
     {
     TLogger.log("Begin cascadeAll1X1Test4");
     boolean pass = false;
     B bRef = null;
     A a1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("4", "a4", 4);
        bRef = new B("4", "bean4", 4, a1);
        getEntityManager().persist(bRef);

	TLogger.log("bref created, try find");
	A newA = findA("4");
	if (null != newA ) {
        	pass = getEntityManager().contains(newA);
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

      if (! pass)
            throw new Fault( "cascadeAll1X1Test4 failed");
     }


    /*
     * @testName: cascadeAll1X1Test5
     * @assertion_ids: PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:676
     * @test_Strategy: The contains method [used to determine whether an entity instance
     *		       is in the managed state] returns false:
     *                 If the entity instance is new and the persist operation has not
     *		       been cascaded to it.
     *
     */

     public void cascadeAll1X1Test5() throws Fault
     {

     TLogger.log("Begin cascadeAll1X1Test5");
     boolean pass = true;
     A aRef = null;
     B b1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        b1 = new B("5", "b5", 5);
        aRef = new A("5", "bean5", 5, b1);

        pass = getEntityManager().contains(b1);

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

     if (pass)
           throw new Fault( "cascadeAll1X1Test5 failed");
     }

    /*
     * @testName: cascadeAll1X1Test6
     * @assertion_ids:  PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:627
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *
     *                  If X is a pre-existing managed entity, it is ignored by the persist
     *                  operation.  However, the persist operation is cascaded to entities
     *                  referenced by X, if the relationships from X to these other entities is
     *                  annotated with cascade=ALL annotation member value.
     *
     * 			The flush method can be used for force synchronization.  The semantics
     *                  of the flush operation applied to an entity X is as follows:
     *
     *                  For all entities Y referenced by a relationship from X, if the
     *                  relationship to Y has been annotated with the cascade member value
     *                  cascade=ALL the persist operation is applied to Y.
     *
     */

     public void cascadeAll1X1Test6() throws Fault
     {
     TLogger.log("Begin cascadeAll1X1Test6");
     boolean pass = false;

     B bRef = null;
     B bRef1 = null;
     A a1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("6", "a6", 6);
        bRef = new B("6", "bean6", 6);
	getEntityManager().persist(bRef);

        if ( getEntityManager().contains(bRef) ) {
	     bRef1 = findB("6");  
	     bRef1.setA1(a1);
	     getEntityManager().persist(bRef1);
	     getEntityManager().flush();

	     TLogger.log("Try to find A");
	     A a2 = findA("6");

  	     if (null != a2 ) {
		TLogger.log("a2 is not null");
                pass = getEntityManager().contains(a2);
             }

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

     if (! pass)
            throw new Fault( "cascadeAll1X1Test6 failed");
     }

    /*
     * @testName: cascadeAll1X1Test7
     * @assertion_ids: PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:647
     * @test_Strategy:  The flush method can be used for force synchronization.  The
     *                  semantics of the flush operation applied to an entity X is as follows:
     *
     *                  For any entity Y referenced by a relationship from X, where 
     *                  the relationship to Y has not been annotated with the cascade 
     *                  element value cascade=ALL.
     *
     *			If Y is new or removed, an IllegalStateException will be thrown by the
     *			flush operation or the transaction commit will fail.
     *
     */

     public void cascadeAll1X1Test7() throws Fault
     {

     TLogger.log("Begin cascadeAll1X1Test7");
     boolean pass = false;

     A aRef = null;
     B b1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        b1 = new B("7", "b7", 7);
        aRef = new A("7", "bean7", 7, b1);
        getEntityManager().persist(aRef);
        getEntityManager().flush();
	getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("IllegalArgumentException caught as expected:" + e );
	pass = true;
     } finally {
        try {
              if ( getEntityTransaction().isActive() ) {
                   getEntityTransaction().rollback();
              }
        } catch (IllegalStateException fe) {
          TLogger.log("ERROR: Unexpected exception rolling back TX:" + fe );
          fe.printStackTrace();
        }
     }

     if (! pass)
            throw new Fault( "cascadeAll1X1Test7 failed");
     }

    /*
     * @testName: cascadeAll1X1Test8
     * @assertion_ids: PERSISTENCE:SPEC:631
     * @test_Strategy: A managed entity instance becomes removed by
     *			invoking the remove method on it or by
     *			cascading the remove operation.  The semantics of the 
     *                  remove operation, applied to an entity X are as follows:
     *
     *                  The remove operation is cascaded to entities referenced by X,
     *                  if the relationship from X to these other entities is annotated
     *                  with cascade=ALL annotation member.
     *
     *                  The cascade=ALL specification should only be applied to associations
     *                  that are specified as OneToOne or OneToMany.
     *
     *                  Invoke remove on a OneToOne relationship from X annotated
     *                  with cascade=ALL and ensure the remove operation is cascaded.
     *
     */

     public void cascadeAll1X1Test8() throws Fault
     {
      TLogger.log("Begin cascadeAll1X1Test8");
      boolean pass = false;

      try {
      getEntityTransaction().begin();
      A a1 = new A("8", "a8", 8);
      B bRef = new B("8", "a8", 8, a1);
      getEntityManager().persist(bRef);

      A a2 = bRef.getA1();

      if ( ( getInstanceStatus(bRef))  &&  (a2 == a1) ) {
                try {
                  TLogger.log("Status is true as expected, try remove()");
                        getEntityManager().remove(findB("8"));
                  TLogger.log("Remove is immediately visible to the contains method");
                  if ( (! getEntityManager().contains(a2)) &&
                                 ( ! getEntityManager().contains(bRef)) ) {
                        getEntityManager().flush();
                  }

        	A stillExists = findA("8");
        	if ( stillExists == null ) {
             		pass = true;
        	}
		  
                } catch (Exception onfe) {
                      TLogger.log("Expected Exception :" + onfe );
                }
        } else {
          TLogger.log("Instance is not managed- Unexpected");
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

     if ( ! pass )
             throw new Fault( "cascadeAll1X1Test8 failed");
     }


  /*
     * @testName: cascadeAll1X1Test9
     * @assertion_ids: PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:674
     * @test_Strategy: The contains method [used to determine whether an entity
     *                  instance is in the managed state] returns false:
     *
     *                 If the remove operation has been cascaded to it.
     *
     */

     public void cascadeAll1X1Test9() throws Fault
     {
      TLogger.log("Begin cascadeAll1X1Test9");
      boolean pass = false;

      try {
      getEntityTransaction().begin();
      A a1 = new A("9", "a9", 9);
      B bRef = new B("9", "a9", 9, a1);
      getEntityManager().persist(bRef);

        TLogger.log("Get Instance ");
        A a2 = bRef.getA1();

        if ( (getInstanceStatus(bRef))  &&  (a2 == a1) ) {
                try {
                  TLogger.log("Status is true as expected, try remove()");
  		  B newB = findB("9");
                  getEntityManager().remove(newB);

                  if (! getEntityManager().contains(a2) ) {
                     pass = true;
                  }
                } catch (Exception onfe) {
                        TLogger.log("Unexpected Exception :" + onfe );
                }
        } else {
          TLogger.log("Instance is not managed- Unexpected");
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

     if (!pass)
               throw new Fault( "cascadeAll1X1Test9 failed");
     }


   /*
     * @testName: cascadeAll1X1Test10
     * @assertion_ids: PERSISTENCE:SPEC:631
     * @test_Strategy: A managed entity instance becomes removed by invoking 
     *                  the remove method on it or by cascading the remove
     *			operation. The semantics of the remove
     *                  operation, applied to an entity X are as follows:
     *
     *                  If X is a new entity, it is ignored by the remove
     *                  operation.  However, the remove operation is cascaded to entities
     *                  referenced by X, if the relationships from X to these other
     *                  entities is annotated with cascade=ALL annotation member value.
     *
     */

    public void cascadeAll1X1Test10() throws Fault
    {
      TLogger.log("Begin cascadeAll1X1Test10");
      boolean pass = false;
      boolean status = true;

      try {
        getEntityTransaction().begin();
        A a1 = new A("10", "a10", 10);
        getEntityManager().persist(a1);

        B bRef = new B("10", "b10", 10, a1);

        if ( getInstanceStatus(a1) ) {
        try {
          TLogger.log("Entity is new, remove ignored on bRef, a1 should be removed");
          getEntityManager().remove(bRef);
          status = getEntityManager().contains(a1);
	  getEntityManager().flush();
        } catch (Exception e) {
          TLogger.log("Unexpected Exception :" + e );
          pass = false;
          }
        } else {
          TLogger.log("Instance is not managed- Unexpected");
          pass = false;
        }

        A stillExists = findA("10");
        if ( ( ! status )  && ( stillExists == null ) ) {
             pass = true;
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

        if (!pass)
              throw new Fault( "cascadeAll1X1Test10 failed");
    }

   /* 
    *  Business Methods to set up data for Test Cases
    */

    private void createA(A a)
    {
      TLogger.log("Entered createA method");
      getEntityTransaction().begin();
      getEntityManager().persist(a);
      getEntityManager().flush();
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
      getEntityManager().flush();
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

       for (int i=1; i<14; i++ ) {
          B newB = findB(Integer.toString(i));
                if (newB != null ) {
                    getEntityManager().remove(newB);
                    TLogger.log("removed entity B:  " + newB);
                }
          }

          for (int i=1; i<14; i++ ) {
          A newA = findA(Integer.toString(i));
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


