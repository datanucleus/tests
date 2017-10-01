 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.10 06/09/13
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.persist.oneXone;

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
     * @testName: persist1X1Test1
     * @assertion_ids:  PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:622
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *
     *			The persist operation is cascaded to entities referenced by X, if 
     *			the relationship from X to these other entities is annotated with
     *			cascade=PERSIST annotation member.
     *
     *                  Invoke persist on a OneToOne relationship from X
     *                  annotated with cascade=PERSIST and ensure the persist
     *			operation is cascaded.
     *			Entity B is annotated with PERSIST so call persist from there.
     *
     */

     public void persist1X1Test1() throws Fault
     {
     TLogger.log("Begin persist1X1Test1");
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
            throw new Fault( "persist1X1Test1 failed");
     }


    /*
     * @testName: persist1X1Test2
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

    public void persist1X1Test2() throws Fault
    {
      TLogger.log("Begin persist1X1Test2");
      B bRef = null;
      A a1 = null;

      boolean pass = false;
      boolean result = false;

      try {
	 getEntityTransaction().begin();
         a1 = new A("2", "a2", 2);
         bRef = new B("2", "b2", 2, a1);
         TLogger.log("Persist Instance");
         getEntityManager().persist(bRef);

         TLogger.log("Get Instance Status ");
         result = getInstanceStatus(findB("2"));

         if (result) {
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
            throw new Fault( "persist1X1Test2 failed");
    }

    /*
     * @testName: persist1X1Test3
     * @assertion_ids:  PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:647
     * @test_Strategy:  The flush method can be used for force synchronization.  The
     *                  semantics of the flush operation applied to an entity X is as follows:
     *
     *                  For any entity Y referenced by a relationship from X, where
     *                  the relationship to Y has not been annotated with the cascade
     *                  element value cascade=PERSIST:
     *
     *                  If Y is new or removed, an IllegalStateException will be thrown by the
     *                  flush operation or the transaction commit will fail.
     *
     */

     public void persist1X1Test3() throws Fault
     {

     TLogger.log("Begin persist1X1Test3");
     boolean pass = false;

     A aRef = null;
     B b1 = null;

     try {
        getEntityTransaction().begin();
        TLogger.log("New instances");
        b1 = new B("13", "b13", 13);
        aRef = new A("13", "bean13", 13, b1);
        getEntityManager().persist(aRef);
        getEntityManager().flush();
        getEntityTransaction().commit();
      } catch (IllegalStateException e) {
        TLogger.log("IllegalStateException caught as expected:" + e );
        pass = true;
        aRef = null;
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
            throw new Fault( "persist1X1Test3 failed");
     }



    /*
     *
     * @testName: persist1X1Test4
     * @assertion_ids:  PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:643
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *
     *			For all entities Y referenced by a relationship from X, if the
     *			relationship to Y has been annotated with the cascade member
     *			value cascade=PERSIST, the persist operation is applied to Y.
     *
     *                  Invoke persist on a OneToOne relationship from X where Y is
     *			annotated with cascade=PERSIST and ensure the persist operation
     *			is cascaded.
     *
     */

     public void persist1X1Test4() throws Fault
     {

     TLogger.log("Begin persist1X1Test4");
     boolean pass = false;
     B bRef = null;
     A a1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("4", "a4", 4);
        bRef = new B("4", "bean4", 4, a1);
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
            throw new Fault( "persist1X1Test4 failed");
     }


    /*
     * @testName: persist1X1Test5
     * @assertion_ids:  PERSISTENCE:SPEC:628; PERSISTENCE:SPEC:632
     * @test_Strategy:  A managed entity instance becomes removed by invoking 
     *                  the remove method on it or by cascading the remove
     *			operation. The semantics of the remove
     *                  operation, applied to an entity X are as follows:
     *
     *			Test the remove semantics of a OneToOne relationship when the related
     *			entity has NOT been annotated with REMOVE.
     *
     */

     public void persist1X1Test5() throws Fault
     {
     TLogger.log("Begin persist1X1Test5");
     boolean pass = false;
     B bRef = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New A instance");
        A a1 = new A("5", "a5", 5);
        TLogger.log("New B instance");
        bRef = new B("5", "bean5", 5, a1);
        getEntityManager().persist(bRef);

        TLogger.log("Get newly persisted A instance");
        A a2 = bRef.getA1();

         if (a1 == a2) {
               TLogger.log("Try to remove a2 instance");
               getEntityManager().remove(a2); 
               TLogger.log("Try to remove bRef instance");
               getEntityManager().remove(bRef); 
	       getEntityManager().flush();
	 }

	 A newA = findA("5");
	 B newB = findB("5");

	 if ( (null == newA ) && (null == newB ) ) {
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
            throw new Fault( "persist1X1Test5 failed");
     }

    /*
     * @testName: persist1X1Test6
     * @assertion_ids:  PERSISTENCE:SPEC:667; PERSISTENCE:SPEC:668
     * @test_Strategy: The contains method [used to determine whether an entity instance is in
     *		       the managed state] returns true:
     *
     *  	       If the entity has been retrieved from the database and has not been
     *		       removed or detached.
     *
     */

     public void persist1X1Test6() throws Fault
     {
     TLogger.log("Begin persist1X1Test6");
     boolean pass = false;
     B bRef = null;
     A a1 = null;
     A a2 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("6", "a6", 6);
        bRef = new B("6", "bean6", 6, a1);
        getEntityManager().persist(bRef);
	getEntityManager().flush();

    	pass = getEntityManager().contains(bRef.getA1());

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
            throw new Fault( "persist1X1Test6 failed");
     }


    /*
     * @testName: persist1X1Test7
     * @assertion_ids:  PERSISTENCE:SPEC:667; PERSISTENCE:SPEC:669
     * @test_Strategy: The contains method [used to determine whether an entity instance
     *		       is in the managed state] returns true:
     * 		       If the entity instance is new and the persist method has been
     *		       called on the entity.
     *
     */

     public void persist1X1Test7() throws Fault
     {
     TLogger.log("Begin persist1X1Test7");
     boolean pass = false;
     B bRef = null;
     A a1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("7", "a7", 7);
        bRef = new B("7", "bean7", 7, a1);
        getEntityManager().persist(bRef);

        pass = getEntityManager().contains(bRef);

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
            throw new Fault( "persist1X1Test7 failed");
     }

    /*
     * @testName: persist1X1Test8
     * @assertion_ids:  PERSISTENCE:SPEC:667; PERSISTENCE:SPEC:670
     * @test_Strategy: The contains method [used to determine whether an entity instance
     *		       is in the managed state] returns true:
     *  	       If the entity instance is new and the persist operation has been
     *		       cascaded to it.
     *
     */

     public void persist1X1Test8() throws Fault
     {
     TLogger.log("Begin persist1X1Test8");
     boolean pass = false;
     B bRef = null;
     A a1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("8", "a8", 8);
        bRef = new B("8", "bean8", 8, a1);
        getEntityManager().persist(bRef);

	TLogger.log("bref created, try find");
	A newA = findA("8");
        pass = getEntityManager().contains(newA);

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
            throw new Fault( "persist1X1Test8 failed");
     }

    /*
     * @testName: persist1X1Test9
     * @assertion_ids:  PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:675
     * @test_Strategy: The contains method [used to determine whether an entity instance
     *		       is in the managed state] returns false:
     *                 If the entity instance is new and the persist operation has not
     *		       been called on it.
     *
     */

     public void persist1X1Test9() throws Fault
     {
     TLogger.log("Begin persist1X1Test9");
     boolean pass = true;
     B bRef = null;
     A a1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("9", "a9", 9);
        bRef = new B("9", "bean9", 9, a1);

        pass = getEntityManager().contains(bRef);

        getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
        e.printStackTrace();
      }

      if (pass)
            throw new Fault( "persist1X1Test9 failed");
     }

    /*
     * @testName: persist1X1Test10
     * @assertion_ids:  PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:676
     * @test_Strategy: The contains method [used to determine whether an entity instance
     *		       is in the managed state] returns false:
     *                 If the entity instance is new and the persist operation has not
     *		       been cascaded to it.
     *
     */

     public void persist1X1Test10() throws Fault
     {

     TLogger.log("Begin persist1X1Test10");
     boolean pass = true;
     A aRef = null;
     B b1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        b1 = new B("10", "b10", 10);
        aRef = new A("10", "bean10", 10, b1);

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
            throw new Fault( "persist1X1Test10 failed");
     }

    /*
     * @testName: persist1X1Test11
     * @assertion_ids:  PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:622;
     * 			PERSISTENCE:SPEC:626
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *
     *                  If X is a pre-existing managed entity, it is ignored by the persist
     *                  operation.  However, the persist operation is cascaded to entities
     *                  referenced by X, if the relationships from X to these other entities is
     *                  annotated with cascade=PERSIST annotation member value.
     *
     * 			The flush method can be used for force synchronization.  The semantics
     *                  of the flush operation applied to an entity X is as follows:
     *
     *                  For all entities Y referenced by a relationship from X, if the
     *                  relationship to Y has been annotated with the cascade member value
     *                  cascade=PERSIST the persist operation is applied to Y.
     *
     */

     public void persist1X1Test11() throws Fault
     {
     TLogger.log("Begin persist1X1Test11");
     boolean pass = false;

     B bRef = null;
     B bRef1 = null;
     A a1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("11", "a11", 11);
        bRef = new B("11", "bean11", 11);
	getEntityManager().persist(bRef);

        if ( getEntityManager().contains(bRef) ) {
	     bRef1 = findB("11");  
	     bRef1.setA1(a1);
	     getEntityManager().persist(bRef1);
	     getEntityManager().flush();
	     pass = getEntityManager().contains(a1);
	     TLogger.log("Try to find A");
	     A a2 = findA("11");
	     if (null != a2 ) {
		TLogger.log("a2 is not null");
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
            throw new Fault( "persist1X1Test11 failed");
     }

    /*
     * @testName: persist1X1Test12
     * @assertion_ids:  PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:642
     * @test_Strategy:  The flush method can be used for force synchronization.  The semantics
     *                  of the flush operation applied to an entity X is as follows:
     *
     *                  If X is a managed entity, it is synchronized to the database.
     *
     */

     public void persist1X1Test12() throws Fault
     {

     TLogger.log("Begin persist1X1Test12");
     boolean pass = false;
     B bRef = null;
     A a1 = null;

     try {
        TLogger.log("New instances");
        a1 = new A("12", "a12", 12);
        bRef = new B("12", "bean12", 12, a1);
        createB(bRef);

	getEntityTransaction().begin();
	B b2 = findB("12");

	if (getEntityManager().contains(b2) ) {
	    b2.setBName("newBean12");
	    getEntityManager().flush();
	    TLogger.log("getBName returns: " + b2.getBName() );
	    if (b2.getBName().equals("newBean12")) {
	        pass = true;
            }
	} else {
	    TLogger.log("ERROR: Entity not managed - test fails.");
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
            throw new Fault( "persist1X1Test12 failed");
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

          for (int i=1; i<16; i++ ) {
          B newB = findB(Integer.toString(i));
                if (newB != null ) {
                    getEntityManager().remove(newB);
                    TLogger.log("removed entity B:  " + newB);
                }
          }

          for (int i=1; i<16; i++ ) {
          A newA = findA(Integer.toString(i));
                if (newA != null ) {
                    getEntityManager().remove(newA);
                    TLogger.log("removed entity A:  " + newA);
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


