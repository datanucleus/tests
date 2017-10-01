 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.9 06/06/26
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.persist.manyXone;

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
     * @testName: persistMX1Test1
     * @assertion_ids: PERSISTENCE:SPEC:1090; PERSISTENCE:SPEC:1091;
     *			PERSISTENCE:SPEC:1070; PERSISTENCE:SPEC:1071;
     *			PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:622
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *                  The perist operation is cascaded to entities referenced by X, if
     *                  the relationship from X to these other entities is annotated with
     *                  cascade=PERSIST annotation member.
     *
     *                  Invoke persist on a ManyToOne relationship from X annotated
     *			with cascade=PERSIST and ensure the persist operation is cascaded.
     *
     */

     public void persistMX1Test1() throws Fault
     {
     TLogger.log("Begin persistMX1Test1");
     boolean pass = false;
     A aRef = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        aRef = new A("1", "bean1", 1);
        B b1 = new B("1", "b1", 1, aRef);
	getEntityManager().persist(b1);
        B b2 = new B("2", "b2", 1, aRef);
	getEntityManager().persist(b2);
        B b3 = new B("3", "b3", 1, aRef);
	getEntityManager().persist(b3);
        B b4 = new B("4", "b4", 1, aRef);
	getEntityManager().persist(b4);
	getEntityManager().flush();

	A newA1 = b1.getA1Info();
	A newA2 = b2.getA1Info();
	A newA3 = b3.getA1Info();
	A newA4 = b4.getA1Info();

        if ( (newA1 != null)  && (newA2 != null) &&
                (newA3 != null) && (newA4 != null) ){
                pass = true;
        } else {
          TLogger.log("ERROR: Wrong results received");
          pass = false;
        }
	getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
        e.printStackTrace();
	pass = false;
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
            throw new Fault( "persistMX1Test1 failed");
     }

    /*
     * @testName: persistMX1Test2
     * @assertion_ids:	PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:624
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *
     *                  If X is a removed entity, it becomes managed.
     *
     *                  Create an entity, persist it, remove it, and invoke persist again.
     *                  Check that it is managed and is accessible.
     *
     */

    public void persistMX1Test2() throws Fault
    {
     TLogger.log("Begin persistMX1Test2");
     boolean pass = false;
     A aRef = null;
     A newA = null;

     try {
	getEntityTransaction().begin();
        aRef = new A("2", "bean2", 2);
        B b1 = new B("2", "b2", 2, aRef);
	getEntityManager().persist(b1);
	getEntityManager().flush();

	A newA1 = b1.getA1Info();

        if (newA1 != null ) {
               try {
 	 	 TLogger.log("Remove b1 ");
                 getEntityManager().remove(newA1);
                 getEntityManager().remove(findB("2"));
                 getEntityManager().flush();

                 TLogger.log("Persist a removed entity ");
		 B newB = findB("2");
		   if ( null == newB) {
                 	getEntityManager().persist(b1);
			getEntityManager().flush();
                 	pass = ( (getEntityManager().contains(b1)) && (b1.getA1() != null) );
	            } else {
		       TLogger.log("Entity B not removed");
		       pass = false;
  		  }
		 getEntityTransaction().commit();
               } catch (Exception ee) {
                 TLogger.log("Unexpected exception trying to persist a removed entity", ee);
                 pass = false;
               }
        } else {
          TLogger.log("Instance is not already persisted. Test Fails.");
          pass = false;
        }

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
            throw new Fault( "persistMX1Test2 failed");
    }


    /*
     * @testName: persistMX1Test3
     * @assertion_ids:	PERSISTENCE:SPEC:667; PERSISTENCE:SPEC:668
     * @test_Strategy: The contains method [used to determine whether an
     *			 entity instance is in the managed state]
     *                   returns true:
     *
     *                 If the entity has been retrieved from the database
     *			 and has not been removed or detached.
     */

   public void persistMX1Test3() throws Fault
   {
     TLogger.log("Begin persistMX1Test3");
     boolean pass = false;
     A aRef = null;

     try {
        getEntityTransaction().begin();
        TLogger.log("New instances");
        aRef = new A("4", "bean4", 4);
	getEntityManager().persist(aRef);
        B b1 = new B("1", "b1", 4);
        getEntityManager().persist(b1);
        B b2 = new B("2", "b2", 4);
        getEntityManager().persist(b2);
        B b3 = new B("3", "b3", 4);
        getEntityManager().persist(b3);
        B b4 = new B("4", "b4", 4);
        getEntityManager().persist(b4);

	b1.setA1(aRef);
	b2.setA1(aRef);
	b3.setA1(aRef);
	b4.setA1(aRef);

	getEntityManager().flush();

	A newA1 = b1.getA1Info();
	A newA2 = b2.getA1Info();
	A newA3 = b3.getA1Info();
	A newA4 = b4.getA1Info();

        if ( ( (newA1 != null) && (getEntityManager().contains(newA1) ) )
	  && ( (newA2 != null) && (getEntityManager().contains(newA2) ) )
	  && ( (newA3 != null) && (getEntityManager().contains(newA3) ) )
	  && ( (newA4 != null) && (getEntityManager().contains(newA4) ) ) )
	{
 	  pass = true;
	}
	getEntityTransaction().commit();
	} catch (Exception e) {
	  pass = false;
          TLogger.log("ERROR: Unexpected exception caught: " + e );
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
           throw new Fault( "persistMX1Test3 failed");
     }


   /*
     * @testName: persistMX1Test4
     * @assertion_ids:	PERSISTENCE:SPEC:667; PERSISTENCE:SPEC:669;
     *			PERSISTENCE:SPEC:677
     * @test_Strategy: The contains method [used to determine whether an entity
     * 			 instance is in the managed state] returns true:
     *                 If the entity instance is new and the persist method has
     *			 been called on the entity.
     *			The effect of cascading persist is immediately visible
     *			visible to the contains method.
     */

     public void persistMX1Test4() throws Fault
     {
     TLogger.log("Begin persistMX1Test4");
     boolean pass = false;
     A aRef = null;
     Collection newCol = null;

     try {
        getEntityTransaction().begin();
        TLogger.log("New instances");
        aRef = new A("5", "bean5", 5);
        B b1 = new B("1", "b1", 5, aRef);
        getEntityManager().persist(b1);
        B b2 = new B("2", "b2", 5, aRef);
        getEntityManager().persist(b2);
        B b3 = new B("3", "b3", 5, aRef);
        getEntityManager().persist(b3);
        B b4 = new B("4", "b4", 5, aRef);
        getEntityManager().persist(b4);

        pass = getInstanceStatus(aRef);

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
            throw new Fault( "persistMX1Test4 failed");
     }

    /*
     * @testName: persistMX1Test5
     * @assertion_ids:	PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:676
     * @test_Strategy: The contains method [used to determine whether an
     *			 entity instance is in the managed state]
     *                   returns false:
     *
     *                 If the entity instance is new and the persist operation
     *			 has not been cascaded to it.
     *
     */
     public void persistMX1Test5() throws Fault
     {
     boolean pass = false;
     A aRef = null;

     try {
        getEntityTransaction().begin();
        TLogger.log("New instances");
        aRef = new A("6", "bean6", 6);
        B b1 = new B("1", "b1", 6, aRef);
        B b2 = new B("2", "b2", 6, aRef);
        B b3 = new B("3", "b3", 6, aRef);
        B b4 = new B("4", "b4", 6, aRef);

        pass = ( ( ! getInstanceStatus(aRef) ) && ( ! getInstanceStatus(b1) ) &&
			( ! getInstanceStatus(b2) ) && ( ! getInstanceStatus(b3) ) &&
			( ! getInstanceStatus(b4) ) );

	getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
        e.printStackTrace();
	pass = false;
      }

      if (! pass)
            throw new Fault( "persistMX1Test5 failed");
     }


    /*
     * @testName: persistMX1Test6
     * @assertion_ids:	PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:675
     * @test_Strategy: The contains method [used to determine whether an entity
     *			 instance is in the managed state]
     *                   returns false:
     *
     *                 If the entity instance is new and the persist operation
     *			 not been called on it.
     */

     public void persistMX1Test6() throws Fault
     {
     boolean pass = false;

     A a1 = null;
     B bRef = null;
     try {
	getEntityTransaction().begin();
        a1 = new A("7", "b7", 7);
        bRef = new B("7", "bean7", 7, a1);


        pass = ( ( !getEntityManager().contains(bRef))  &&
                         ( ! getEntityManager().contains(a1)) );

	getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
        e.printStackTrace();
      }

      if (!pass)
            throw new Fault( "persistMX1Test6 failed");
     }


    /*
     * @testName: persistMX1Test7
     * @assertion_ids:	PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:622
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *                  The perist operation is cascaded to entities referenced by X, if
     *                  the relationship from X to these other entities is annotated with
     *                  cascade=PERSIST annotation member.
     *
     *                  Invoke persist on a ManyToOne relationship from X
     *			annotated with cascade=PERSIST and ensure the
     *                  persist operation is cascaded.
     *
     *			If X is a pre-existing managed entity, it is ignored by the persist
     *			operation.  However, the persist operation is cascaded to entities 
     *			referenced by X, if the relationships from X to these other entities is
     *			annotated with cascade=PERSIST annotation member value.
     *
     */

     public void persistMX1Test7() throws Fault
     {
      boolean pass = false;
      A a1 = null;
      A a2 = null;
      B bRef = null;
      B bRef1 = null;

      try {
	getEntityTransaction().begin();
        a1 = new A("8", "a8", 8);
        bRef = new B("8", "bean8", 8);
        getEntityManager().persist(bRef);

        if ( getEntityManager().contains(bRef) ) {
             bRef1 = findB("8"); 
             bRef1.setA1(a1);
             getEntityManager().persist(bRef1);
             pass = getEntityManager().contains(a1);
             TLogger.log("Try to find A");
             a2 = findA("8");
             if (null != a2 ) {
                TLogger.log("b2 is not null");
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

      if (!pass)
            throw new Fault( "persistMX1Test7 failed");
     }

    /*
     * @testName: persistMX1Test8
     * @assertion_ids:	PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:642
     * @test_Strategy:  The flush method can be used for force synchronization. 
     *			The semantics of the flush operation applied to an entity
     *			X is as follows:
     *
     *                  If X is a managed entity, it is synchronized to the database.
     *
     */

     public void persistMX1Test8() throws Fault
     {
     boolean pass = false;
     B bRef = null;
     B b2 = null;
     A a1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("9", "A9", 9);
        bRef = new B("9", "bean9", 9);
        getEntityManager().persist(bRef);

	if (null == bRef.getA1() ) {
	   bRef.setA1(a1);
	   getEntityManager().flush();
	} 

        if ( (null != a1 ) && (getEntityManager().contains(a1)) ) {
            A result = bRef.getA1Info();
            pass = true;
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

      if (!pass)
            throw new Fault( "persistMX1Test8 failed");
      }

    /*
     * @testName: persistMX1Test9
     * @assertion_ids:	PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:643
     * @test_Strategy:  The flush method can be used for force synchronization.
     *			The semantics of the flush operation applied to an entity
     *			X is as follows:
     *
     *                  For all entities Y referenced by a relationship from X, if the
     *                  relationship to Y has been annotated with the cascade member value
     *                  cascade=PERSIST, the persist operation is applied to Y.
     *
     */

     public void persistMX1Test9() throws Fault
     {
     boolean pass = false;

     B bRef = null;
     A a1 = null;

     try {
        TLogger.log("New instances");
	getEntityTransaction().begin();
        a1 = new A("10", "a10", 10);
        bRef = new B("10", "bean10", 10, a1);

	getEntityManager().persist(bRef);
        getEntityManager().flush();
        pass = getEntityManager().contains(a1);
	getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("UnExpected Exception :" + e );
        pass = false;
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
             throw new Fault( "persistMX1Test9 failed");
     }


    /*
     * @testName: persistMX1Test10
     * @assertion_ids:	PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:646
     * @test_Strategy:  The flush method can be used for force synchronization.
     *			The semantics of the flush operation applied to an entity
     *			X is as follows:
     *
     *                  For any detached entity Y referenced by a relationship from X,
     *			where the relationship to Y has not been annotated with the
     *			cascade member value cascade=PERSIST the semantics depend
     *			upon the ownership of the relationship.  If X owns the
     *			relationship, any changes to the relationship are synchronized
     *			with the database, otherwise, if Y owns the relationship, the
     *			behavior is undefined.
     *
     */

     public void persistMX1Test10() throws Fault
     {
     A a1 = null;
     B bRef = null;
     B b2 = null;
     boolean pass = false;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("11", "a11", 11);
        bRef = new B("11", "bean11", 11, a1);
        getEntityManager().persist(bRef);
	getEntityTransaction().commit();

	getEntityTransaction().begin();
	b2 = findB("11");
	A newA = b2.getA1();
	newA.setAName("newA");
	getEntityManager().flush();
        if ( (b2.isA() ) && (newA.getAName().equals("newA")) ) {
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
            throw new Fault( "persistMX1Test10 failed");
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

   private List findByName(String name)
   {
      TLogger.log("Entered findByName method");
      return getEntityManager().createQuery(
	"select a from A a where a.name = :name")
        .setParameter("name", name)
        .getResultList();
   }

   private boolean getInstanceStatus(Object o) {
     TLogger.log("Entered getInstanceStatus method");
     return getEntityManager().contains(o);
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


