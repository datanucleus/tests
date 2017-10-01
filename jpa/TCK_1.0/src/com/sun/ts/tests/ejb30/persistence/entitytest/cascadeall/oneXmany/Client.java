 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.11 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.cascadeall.oneXmany;

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
     * @testName: cascadeAll1XMTest1
     * @assertion_ids:  PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:623;
     *			PERSISTENCE:JAVADOC:129; PERSISTENCE:JAVADOC:132;
     *			PERSISTENCE:SPEC:566
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *                  The perist operation is cascaded to entities referenced by X, if
     *                  the relationship from X to these other entities is annotated with
     *                  cascade=ALL annotation member.
     *
     *                  Invoke persist on a OneToMany relationship from X annotated
     *			with cascade=ALL and ensure the persist operation is cascaded.
     *
     */

     public void cascadeAll1XMTest1() throws Fault
     {
     TLogger.log("Begin cascadeAll1XMTest1");
     boolean pass = false;
     A aRef = null;
     Collection bCol = null;
     Collection newCol = null;

     try {
        TLogger.log("New instances");
        B b1 = new B("1", "b1", 1);
        B b2 = new B("2", "b2", 1);
        B b3 = new B("3", "b3", 1);
        B b4 = new B("4", "b4", 1);
        Vector v1  = new Vector();
        v1.add(b1);
        v1.add(b2);
        v1.add(b3);
        v1.add(b4);
        aRef = new A("1", "bean1", 1, v1);
        createA(aRef);

        newCol = aRef.getBCol();

        dumpCollectionDataB(newCol);

        if (newCol.contains(b1) && newCol.contains(b2) &&
                newCol.contains(b3) && newCol.contains(b4)) {
                pass = true;
        } else {
          TLogger.log("Test failed");
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
            throw new Fault( "cascadeAll1XMTest1 failed");
     }


    /*
     * @testName: cascadeAll1XMTest2
     * @assertion_ids:  PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:644
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *                  The perist operation is cascaded to entities referenced by X, if
     *                  the relationship from X to these other entities is annotated with
     *                  cascade=ALL annotation member.
     *
     *                  For all entities Y referenced by a relationship from X, if the
     *                  relationship to Y has been annotated with the cascade member
     *                  value cascade=ALL, the persist operation is applied to Y.
     *
     *                  Invoke persist a relationship from X annotated where Y IS NOT
     *                  annotated with cascade=ALL and ensure the persist operation
     *			is not cascaded.  An IllegalStateException should be thrown.
     *
     */

     public void cascadeAll1XMTest2() throws Fault
     {
     TLogger.log("Begin cascadeAll1XMTest2");
     boolean pass = false;
     B bRef = null;
     A a1 = null;
     A a2 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("2", "a2", 2);
        bRef = new B("2", "bean2", 2, a1);
	getEntityManager().persist(bRef);
	getEntityManager().flush();
	getEntityTransaction().commit();

      } catch (IllegalStateException e) {
	  TLogger.log("IllegalStateException caught as expected.  Validate the entity did " +
			"not get persisted");
	  pass = true;
/*
		B newB = findB("2");
         	a2 = newB.getA1Info();
	 	TLogger.log("A1Info is:" + a2.getAId() );

         	if (null == a2 )  {
             	    pass = true;
         	}
*/
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
            throw new Fault( "cascadeAll1XMTest2 failed");
     }

   /*
     * @testName: cascadeAll1XMTest3
     * @assertion_ids:  PERSISTENCE:SPEC:667; PERSISTENCE:SPEC:669;
     *                  PERSISTENCE:SPEC:677
     * @test_Strategy: The contains method [used to determine whether an entity
     * 			 instance is in the managed state] returns true:
     *                 If the entity instance is new and the persist method has
     *			 been called on the entity.
     *			The effect of cascading persist is immediately visible
     *			visible to the contains method.
     */

     public void cascadeAll1XMTest3() throws Fault
     {
     TLogger.log("Begin cascadeAll1XMTest3");
     boolean pass = false;
     A a1 = null;
     A a2 = null;
     Collection newCol = null;
     try {
	getEntityTransaction().begin();
        B b1 = new B("1", "b1", 3);
        B b2 = new B("2", "b2", 3);
        B b3 = new B("3", "b3", 3);
        B b4 = new B("4", "b4", 3);
        Vector v1  = new Vector();
        v1.add(b1);
        v1.add(b2);
        v1.add(b3);
        v1.add(b4);

        TLogger.log("New A instance");
        a1 = new A("3", "bean3", 3, v1);
        getEntityManager().persist(a1);
        newCol = a1.getBCol();

        dumpCollectionDataB(newCol);

   	if ( (newCol.size() != 0 ) && ( getEntityManager().contains(b1) ) &&
                        (  getEntityManager().contains(b2) ) &&
                        (  getEntityManager().contains(b3) ) &&
                        (  getEntityManager().contains(b4) )) {
                pass = getInstanceStatus(a1);
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
            throw new Fault( "cascadeAll1XMTest3 failed");
     }


    /*
     * @testName: cascadeAll1XMTest4
     * @assertion_ids: PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:676
     * @test_Strategy: The contains method [used to determine whether an entity
     *			 instance is in the managed state]
     *                   returns false:
     *
     *                 If the entity instance is new and the persist operation
     *			 has not been cascaded to it.
     */

     public void cascadeAll1XMTest4() throws Fault
     {
     boolean pass = false;

     A a1 = null;
     B bRef = null;
     try {
	getEntityTransaction().begin();
        a1 = new A("4", "b4", 4);
        bRef = new B("4", "bean4", 4, a1);

        pass = ( ( !getEntityManager().contains(bRef))  &&
                         ( ! getEntityManager().contains(a1)) );

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
            throw new Fault( "cascadeAll1XMTest4 failed");
     }


    /*
     * @testName: cascadeAll1XMTest5
     * @assertion_ids:  PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:622
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *                  The perist operation is cascaded to entities referenced by X, if
     *                  the relationship from X to these other entities is annotated with
     *                  cascade=ALL annotation member.
     *
     *                  Invoke persist on a OneToMany relationship from X
     *			annotated with cascade=ALL and ensure the
     *                  persist operation is cascaded.
     *
     *			If X is a pre-existing managed entity, it is ignored by the persist
     *			operation.  However, the persist operation is cascaded to entities 
     *			referenced by X, if the relationships from X to these other entities is
     *			annotated with cascade=ALL annotation member value.
     *
     */

     public void cascadeAll1XMTest5() throws Fault
     {
      boolean pass = false;
      A aRef = null;
      A aRef1 = null;
      B b1 = null;

      try {
	getEntityTransaction().begin();
        b1 = new B("5", "b5", 5);
	Vector v1 = new Vector();
	v1.add(b1);
        aRef = new A("5", "bean5", 5);
        getEntityManager().persist(aRef);

        if ( getEntityManager().contains(aRef) ) {
             aRef1 = findA("5"); 
             aRef1.setBCol(v1);
             getEntityManager().persist(aRef1);
             TLogger.log("Try to find B");
             B b2 = findB("5");
             if (null != b2 ) {
                TLogger.log("b2 is not null");
                pass = getEntityManager().contains(b2);
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
            throw new Fault( "cascadeAll1XMTest5 failed");
     }

    /*
     * @testName: cascadeAll1XMTest6
     * @assertion_ids:  PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:642
     * @test_Strategy:  The flush method can be used for force synchronization. 
     *			The semantics of the flush operation applied to an entity
     *			X is as follows:
     *
     *                  If X is a managed entity, it is synchronized to the database.
     *
     */

     public void cascadeAll1XMTest6() throws Fault
     {
     boolean pass = false;
     A aRef = null;
     A a2 = null;
     B b1 = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        b1 = new B("6", "b6", 6);
	Vector v1 = new Vector();
	v1.add(b1);
        aRef = new A("6", "bean6", 6, v1);
        getEntityManager().persist(aRef);

        a2 = findA("6");
        if ( (null != a2 ) && (getEntityManager().contains(a2)) ) {
            Collection result = a2.getBInfoFromA();
	    dumpCollectionDataB(result);
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
            throw new Fault( "cascadeAll1XMTest6 failed");
      }

    /*
     * @testName: cascadeAll1XMTest7
     * @assertion_ids:  PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:644
     * @test_Strategy:  The flush method can be used for force synchronization.
     *			The semantics of the flush operation applied to an entity
     *			X is as follows:
     *
     *                  For all entities Y referenced by a relationship from X, if the
     *                  relationship to Y has been annotated with the cascade member value
     *                  cascade=ALL, the persist operation is applied to Y.
     *
     */

     public void cascadeAll1XMTest7() throws Fault
     {
     boolean pass = false;

     A aRef = null;
     B b1 = null;

     try {
        TLogger.log("New instances");
	getEntityTransaction().begin();
        b1 = new B("7", "b7", 7);
	Vector v1 = new Vector();
	v1.add(b1);
        aRef = new A("7", "bean7", 7, v1);

	getEntityManager().persist(aRef);
        getEntityManager().flush();
        pass = getEntityManager().contains(b1);
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
             throw new Fault( "cascadeAll1XMTest7 failed");
     }


    /*
     * @testName: cascadeAll1XMTest8
     * @assertion_ids:  PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:645
     * @test_Strategy:  The flush method can be used for force synchronization.
     *			The semantics of the flush operation applied to an entity
     *			X is as follows:
     *
     *                  For any new entity Y referenced by a relationship from X, where the
     *                  relationship to Y has not been annotated with the cascade member value
     *                  cascade=ALL, an exception will be thrown by
     *                  the container or the transaction commit will fail.
     *
     */

     public void cascadeAll1XMTest8() throws Fault
     {
      boolean pass = false;
      B bRef = null;
      A a1 = null;

      try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("8", "a8", 8);
        bRef = new B("8", "bean8", 8, a1);

	getEntityManager().persist(bRef);
	getEntityManager().flush();
	getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("Exception caught as Expected:" + e );
        pass = true;
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
            throw new Fault( "cascadeAll1XMTest8 failed");
     }


    /*
     * @testName: cascadeAll1XMTest9
     * @assertion_ids: PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:647
     * @test_Strategy:  The flush method can be used for force synchronization.
     *			The semantics of the flush operation applied to an entity
     *			X is as follows:
     *
     *                  For any detached entity Y referenced by a relationship from X,
     *			where the relationship to Y has not been annotated with the
     *			cascade member value cascade=ALL the semantics depend
     *			upon the ownership of the relationship.  If X owns the
     *			relationship, any changes to the relationship are synchronized
     *			with the database, otherwise, if Y owns the relationship, the
     *			behavior is undefined.
     *
     */

     public void cascadeAll1XMTest9() throws Fault
     {
     A a1 = null;
     B bRef = null;
     B b2 = null;
     boolean pass = false;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        a1 = new A("9", "a9", 9);
        getEntityManager().persist(a1);
  	bRef = new B("9", "bean9", 9, a1);
        getEntityManager().persist(bRef);
	getEntityManager().flush();

	b2 = findB("9");
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
            throw new Fault( "cascadeAll1XMTest9 failed");
     }

    /*
     * @testName: cascadeAll1XMTest10
     * @assertion_ids: PERSISTENCE:SPEC:628; PERSISTENCE:SPEC:634;
     *			PERSISTENCE:SPEC:575
     * @test_Strategy: A managed entity instance becomes removed by invoking 
     *                  the remove method on it or by cascading the remove
     *			operation. The semantics of the remove
     *                  operation, applied to an entity X are as follows:
     *
     *                  The remove operation is cascaded to entities referenced by X,
     *                  if the relationship from X to these other entities is annotated
     *                  with cascade=ALL annotation member.
     *
     *                  The cascade=ALL specification should only be applied to associations
     *                  that are specified as OneToOne or OneToMany.
     *
     *                  Invoke remove on a OneToMany relationship from X annotated with
     *                  cascade=ALL and ensure the remove operation is cascaded.
     *
     */

     public void cascadeAll1XMTest10() throws Fault
     {
      TLogger.log("Begin cascadeAll1XMTest10");
      boolean pass = false;

      getEntityTransaction().begin();
      B b1 = new B("1", "b1", 10);
      getEntityManager().persist(b1);
      B b2 = new B("2", "b2", 10);
      getEntityManager().persist(b2);
      B b3 = new B("3", "b3", 10);
      getEntityManager().persist(b3);
      B b4 = new B("4", "b4", 10);
      getEntityManager().persist(b4);
      Vector v1  = new Vector();
      v1.add(b1);
      v1.add(b2);
      v1.add(b3);
      v1.add(b4);
      A aRef = new A("10", "bean10", 10, v1);
      getEntityManager().persist(aRef);

      Collection newCol = aRef.getBCol();

      dumpCollectionDataB(newCol);

      try {
        TLogger.log("Get Instance Status ");

        if ( (getInstanceStatus(aRef)) &&  (newCol.contains(b1)) && (newCol.contains(b2)) &&
               (newCol.contains(b3)) && (newCol.contains(b4)) ) {
                try {
                  TLogger.log("Status is true as expected, try remove()");
                        getEntityManager().remove(findA("10"));
                        TLogger.log("Remove is immediately visible to the contains method");
                        if ( (! getEntityManager().contains(aRef) ) &&
                             (! getEntityManager().contains(b1) ) &&
                             (! getEntityManager().contains(b2) ) &&
                             (! getEntityManager().contains(b3) ) &&
                             (! getEntityManager().contains(b4) ) ){
                             pass = true;
                        }

                } catch (Exception e) {
                        TLogger.log("Unexpected Exception :" + e );
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
                throw new Fault( "cascadeAll1XMTest10 failed");
     }


    /*
     * @testName: cascadeAll1XMTest11
     * @assertion_ids: PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:674
     * @test_Strategy: The contains method [used to determine whether an entity instance
     *                 is in the managed state] returns false:
     *                 If the remove operation has been cascaded to it.
     *
     */

     public void cascadeAll1XMTest11() throws Fault
     {
      TLogger.log("Begin cascadeAll1XMTest11");
      boolean pass = false;
      boolean status = false;

      getEntityTransaction().begin();
      B b1 = new B("1", "b1", 11);
      getEntityManager().persist(b1);
      B b2 = new B("2", "b2", 11);
      getEntityManager().persist(b2);
      B b3 = new B("3", "b3", 11);
      getEntityManager().persist(b3);
      B b4 = new B("4", "b4", 11);
      getEntityManager().persist(b4);
      Vector v1  = new Vector();
      v1.add(b1);
      v1.add(b2);
      v1.add(b3);
      v1.add(b4);
      A aRef = new A("11", "bean11", 11, v1);
      getEntityManager().persist(aRef);
	
      Collection newCol = aRef.getBCol();

      dumpCollectionDataB(newCol);

      try {
        TLogger.log("Get Instance Status ");

        if ( (getInstanceStatus(aRef)) &&  (newCol.contains(b1)) && (newCol.contains(b2)) &&
               (newCol.contains(b3)) && (newCol.contains(b4)) ) {
                try {
                  TLogger.log("Status is true as expected, try remove()");
                        getEntityManager().remove(findA("11"));
                  if ( (! getEntityManager().contains(aRef) ) &&
                             (! getEntityManager().contains(b1) ) &&
                             (! getEntityManager().contains(b2) ) &&
                             (! getEntityManager().contains(b3) ) &&
                             (! getEntityManager().contains(b4) ) ){
                             pass = true;
                  }
                } catch (Exception e) {
                  TLogger.log("Expected Exception :" + e );
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
           throw new Fault( "cascadeAll1XMTest11 failed");
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
          B newB = findB(Integer.toString(i));
                if (newB != null ) {
                    getEntityManager().remove(newB);
                    TLogger.log("removed entity B:  " + newB);
                }
          }
          for (int i=1; i<20; i++ ) {
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


