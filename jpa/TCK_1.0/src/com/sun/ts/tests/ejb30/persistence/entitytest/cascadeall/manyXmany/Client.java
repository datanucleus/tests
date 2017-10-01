 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.9 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.cascadeall.manyXmany;

import com.sun.javatest.Status;
import com.sun.ts.lib.util.*;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
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
            e.printStackTrace();
            throw new Fault("Setup failed:", e);

        }
    }



    /* 
     *  BEGIN Test Cases
     */



    /*
     * @testName: cascadeAllMXMTest1
     * @assertion_ids: PERSISTENCE:SPEC:1093; PERSISTENCE:SPEC:618;
     *			 PERSISTENCE:SPEC:623; PERSISTENCE:JAVADOC:108;
     *			 PERSISTENCE:JAVADOC:109
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *                  The perist operation is cascaded to entities referenced by X, if
     *                  the relationship from X to these other entities is annotated with
     *                  cascade=ALL annotation member.
     *
     *                  Invoke persist on a ManyToMany relationship from X annotated with
     *			cascade=ALL and ensure the persist operation is cascaded.
     *
     */

     public void cascadeAllMXMTest1() throws Fault
     {

     TLogger.log("Begin cascadeAllMXMTest1");
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
         }
         else {
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
                throw new Fault( "cascadeAllMXMTest1 failed");
     }


    /*
     * @testName: cascadeAllMXMTest2
     * @assertion_ids:  PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:624
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

    public void cascadeAllMXMTest2() throws Fault
    {
     TLogger.log("Begin cascadeAllMXMTest2");
     boolean pass = false;
     A aRef = null;
     Collection bCol = null;
     Collection newCol = null;

     try {
	getEntityTransaction().begin();
        TLogger.log("New instances");
        B b1 = new B("1", "b1", 2);
        B b2 = new B("2", "b2", 2);
        B b3 = new B("3", "b3", 2);
        B b4 = new B("4", "b4", 2);
        Vector v1  = new Vector();
        v1.add(b1);
        v1.add(b2);
        v1.add(b3);
        v1.add(b4);
        aRef = new A("2", "bean2", 2, v1);
        getEntityManager().persist(aRef);

        newCol = aRef.getBCol();

        dumpCollectionDataB(newCol);

 	if (newCol.contains(b1) && newCol.contains(b2) &&
               newCol.contains(b3) && newCol.contains(b4)) {
               try {
		A newA = findA("2");
 		TLogger.log("Remove newA ");
                getEntityManager().remove(newA);
                getEntityManager().flush();
                TLogger.log("Persist a removed entity ");
                getEntityManager().persist(newA);
                pass = getInstanceStatus(newA);
                } catch (Exception ee) {
                  TLogger.log("Unexpected exception trying to persist a " +
				"removed entity", ee);
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
        throw new Fault( "cascadeAllMXMTest2 failed");
    }



    /*
     * @testName: cascadeAllMXMTest3
     * @assertion_ids: PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:636
     * @test_Strategy: The flush method can be used for force synchronization.
     *			 The semantics of the flush operation applied to an
     *			 entity X is as follows:
     *
     *                  If X is a managed entity, it is synchronized to the database.
     *
     */

     public void cascadeAllMXMTest3() throws Fault
     {
     boolean pass = false;
     A aRef = null;
     A a2 = null;
     B b1 = null;

     try {
        TLogger.log("New instances");
        b1 = new B("4", "b4", 4);
        Vector v1 = new Vector();
        v1.add(b1);
        aRef = new A("4", "bean4", 4, v1);
        createA(aRef);

        getEntityTransaction().begin();
        a2 = findA("4");

        if (getEntityManager().contains(a2) ) {
            a2.setAName("newBean4");
            getEntityManager().flush();
         TLogger.log("getAName returns: " + a2.getAName() );
            if (a2.getAName().equals("newBean4")) {
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
             throw new Fault("cascadeAllMXMTest3 failed");
     }




    /*
     * @testName: cascadeAllMXMTest4
     * @assertion_ids: PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:642
     * @test_Strategy:  A managed entity instance becomes removed by
     *			invoking the remove method on it or by cascading
     *			the remove operation.  The semantics of the remove
     *                  operation, applied to an entity X are as follows:
     *
     *                  Test the remove semantics of a ManyToMany relationship when the related
     *                  entity has NOT been annotated with REMOVE.
     *
     */

     public void cascadeAllMXMTest4() throws Fault
     {

     TLogger.log("Begin cascadeAllMXMTest4");
     boolean pass = false;
     A a1 = null;
     A a2 = null;
     Collection newCol = null;
     try {
	B b1 = new B("1", "b1", 5);
	B b2 = new B("2", "b2", 5);
        B b3 = new B("3", "b3", 5);
        B b4 = new B("4", "b4", 5);
        Vector v1  = new Vector();
        v1.add(b1);
        v1.add(b2);
        v1.add(b3);
        v1.add(b4);

	getEntityTransaction().begin();
        TLogger.log("New A instance");
        a1 = new A("5", "bean5", 5, v1);
        getEntityManager().persist(a1);

	newCol = a1.getBCol();
	
	dumpCollectionDataB(newCol);

	if (newCol.contains(b1) && newCol.contains(b2) &&
               newCol.contains(b3) && newCol.contains(b4)) {
         try {
             TLogger.log("Remove instances");
	      getEntityManager().remove(findB("1"));
	      getEntityManager().remove(findB("2"));
	      getEntityManager().remove(findB("3"));
	      getEntityManager().remove(findB("4"));
              getEntityManager().remove(a1);
		if ( ( ! getEntityManager().contains(a1)) ) {
              		pass = true;
	   	}

	getEntityTransaction().commit();
        } catch (Exception fe) {
          TLogger.log("ERROR: Unexpected exception caught trying to remove entity instance :" + fe );
          fe.printStackTrace();
        }
         } else {
             TLogger.log("Test failed");
             pass = false;
         }

      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
        e.printStackTrace();
      }

      if (!pass)
            throw new Fault( "cascadeAllMXMTest4 failed");
     }

    /*
     * @testName: cascadeAllMXMTest5
     * @assertion_ids: PERSISTENCE:SPEC:667; PERSISTENCE:SPEC:668
     * @test_Strategy: The contains method [used to determine whether an entity
     *			instance is in the managed state] returns true:
     *                 If the entity has been retrieved from the database and has
     *			not been removed or detached.
     *
     */

     public void cascadeAllMXMTest5() throws Fault
     {

     TLogger.log("Begin cascadeAllMXMTest5");
     boolean pass = false;
     A a1 = null;
     A a2 = null;
     Collection newCol = null;
     try {
	getEntityTransaction().begin();
        TLogger.log("New B instances");
        B b1 = new B("1", "b1", 6);
        B b2 = new B("2", "b2", 6);
        B b3 = new B("3", "b3", 6);
        B b4 = new B("4", "b4", 6);
        Vector v1  = new Vector();
        v1.add(b1);
        v1.add(b2);
        v1.add(b3);
        v1.add(b4);

        TLogger.log("New A instance");
        a1 = new A("6", "bean6", 6, v1);
        getEntityManager().persist(a1);
	getEntityManager().flush();

	a2 = findA("6");
  	newCol = a2.getBCol();

        dumpCollectionDataB(newCol);

        if (newCol.contains(b1) && newCol.contains(b2) &&
               newCol.contains(b3) && newCol.contains(b4)) {
               try {
 		pass = getInstanceStatus(a2);
      		} catch (Exception e) {
        	  TLogger.log("Unexpected Exception :" + e );
		}
	}

       getEntityTransaction().commit();

       } catch (Exception fe) {
         TLogger.log("ERROR: Unexpected exception caught trying to remove " +
			"entity instance :" + fe );
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
          throw new Fault( "cascadeAllMXMTest5 failed");
     }


    /*
     * @testName: cascadeAllMXMTest6
     * @assertion_ids: PERSISTENCE:SPEC:667; PERSISTENCE:SPEC:669
     * @test_Strategy: The contains method [used to determine whether an entity
     *			 instance is in the managed state] returns true:
     *                 If the entity instance is new and the persist method has
     *			 been called on the entity.
     *
     */

     public void cascadeAllMXMTest6() throws Fault
     {
     TLogger.log("Begin cascadeAllMXMTest6");
     boolean pass = false;
     A a1 = null;
     A a2 = null;
     Collection newCol = null;
     try {
	getEntityTransaction().begin();
        TLogger.log("New B instances");
        B b1 = new B("1", "b1", 7);
        B b2 = new B("2", "b2", 7);
        B b3 = new B("3", "b3", 7);
        B b4 = new B("4", "b4", 7);
        Vector v1  = new Vector();
        v1.add(b1);
        v1.add(b2);
        v1.add(b3);
        v1.add(b4);

        TLogger.log("New A instance");
        a1 = new A("7", "bean7", 7, v1);
        getEntityManager().persist(a1);
        pass = getInstanceStatus(a1);

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
            throw new Fault( "cascadeAllMXMTest6 failed");
     }


    /*
     * @testName: cascadeAllMXMTest7
     * @assertion_ids: PERSISTENCE:SPEC:667; PERSISTENCE:SPEC:670
     * @test_Strategy: The contains method [used to determine whether an entity
     *			instance is in the managed state] returns true:
     *                 If the entity instance is new and the persist operation
     *			has been cascaded to it.
     *
     */

     public void cascadeAllMXMTest7() throws Fault
     {

     TLogger.log("Begin cascadeAllMXMTest7");
     boolean pass = false;
     A a1 = null;
     A a2 = null;
     Collection newCol = null;
     try {
	getEntityTransaction().begin();
        TLogger.log("New A instance");
        B b1 = new B("1", "b1", 8);
        B b2 = new B("2", "b2", 8);
        B b3 = new B("3", "b3", 8);
        B b4 = new B("4", "b4", 8);
        Vector v1  = new Vector();
        v1.add(b1);
        v1.add(b2);
        v1.add(b3);
        v1.add(b4);

        TLogger.log("New B instances");
        a1 = new A("8", "bean8", 8, v1);
        getEntityManager().persist(a1);

        pass = ( getEntityManager().contains(b1) &&
		 getEntityManager().contains(b2) &&
		 getEntityManager().contains(b3) &&
		 getEntityManager().contains(b4) );
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
           throw new Fault( "cascadeAllMXMTest7 failed");
    }


    /*
     * @testName: cascadeAllMXMTest8
     * @assertion_ids: PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:675 
     * @test_Strategy: The contains method [used to determine whether an entity
     *			instance is in the managed state] returns false:
     *                 If the entity instance is new and the persist operation
     *			has not been called on it.
     *
     */

     public void cascadeAllMXMTest8() throws Fault
     {

     TLogger.log("Begin cascadeAllMXMTest8");
     boolean pass = true;
     A a1 = null;
     A a2 = null;
     Collection newCol = null;
     try {
	getEntityTransaction().begin();
        B b1 = new B("1", "b1", 9);
        B b2 = new B("2", "b2", 9);
        B b3 = new B("3", "b3", 9);
        B b4 = new B("4", "b4", 9);
        Vector v1  = new Vector();
        v1.add(b1);
        v1.add(b2);
        v1.add(b3);
        v1.add(b4);

        TLogger.log("New A instance");
        a1 = new A("9", "bean9", 9, v1);

        pass = getInstanceStatus(a1);
	getEntityTransaction().commit();

      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
        e.printStackTrace();
      }

        if (pass)
                throw new Fault( "cascadeAllMXMTest8 failed");
     }


    /*
     * @testName: cascadeAllMXMTest9
     * @assertion_ids: PERSISTENCE:SPEC:671; PERSISTENCE:SPEC:676 
     * @test_Strategy: The contains method [used to determine whether an entity
     *			instance is in the managed state] returns false:
     *                 If the entity instance is new and the persist operation has
     *			not been cascaded to it.
     *
     */

     public void cascadeAllMXMTest9() throws Fault
     {

     TLogger.log("Begin cascadeAllMXMTest9");
     boolean pass = false;
     A a1 = null;
     A a2 = null;
     Collection newCol = null;
     try {
        B b1 = new B("1", "b1", 10);
        B b2 = new B("2", "b2", 10);
        B b3 = new B("3", "b3", 10);
        B b4 = new B("4", "b4", 10);
        Vector v1  = new Vector();
        v1.add(b1);
        v1.add(b2);
        v1.add(b3);
        v1.add(b4);

	getEntityTransaction().begin();
        TLogger.log("New A instance");
        a1 = new A("10", "bean10", 10, v1);

        pass = ( ! getEntityManager().contains(b1) &&
                 ! getEntityManager().contains(b2) &&
                 ! getEntityManager().contains(b3) &&
                 ! getEntityManager().contains(b4) );

      getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
        e.printStackTrace();
      }

      if (!pass)
            throw new Fault( "cascadeAllMXMTest9 failed");
     }


    /*
     * @testName: cascadeAllMXMTest10
     * @assertion_ids:  PERSISTENCE:SPEC:618; PERSISTENCE:SPEC:622
     * @test_Strategy:  The new entity bean instance becomes both managed and persistent
     *                  by invoking the persist method on it. The semantics of the persist
     *                  operation as applied to entity X is as follows:
     *                  The perist operation is cascaded to entities referenced by X, if
     *                  the relationship from X to these other entities is annotated with
     *                  cascade=ALL annotation member.
     *
     *			If X is a pre-existing managed entity, it is ignored by the persist
     *			operation.  However, the persist operation is cascaded to entities 
     *			referenced by X, if the relationships from X to these other entities is
     *			annotated with cascade=ALL annotation member value.
     *
     */

     public void cascadeAllMXMTest10() throws Fault
     {
      boolean pass = false;
      A a1 = null;
      B bRef = null;
      B bRef1 = null;

      try {
	getEntityTransaction().begin();
        a1 = new A("11", "a11", 11);
        bRef = new B("11", "bean11", 11);
        getEntityManager().persist(bRef);

        if ( getEntityManager().contains(bRef) ) {
             bRef1 = findB("11");
	     Vector v1 = new Vector();
	     v1.add(a1);
             bRef1.setACol(v1);
             getEntityManager().persist(bRef1);
             getEntityManager().flush();
             pass = getEntityManager().contains(a1);
             TLogger.log("Try to find A");
             A a2 = findA("11");
             if (null != a2 ) {
                TLogger.log("a2 is not null");
             }
        }

        Vector nullCol = new Vector();
        bRef1.setACol(nullCol);
	getEntityManager().merge(bRef);
	a1.setBCol(nullCol);
	getEntityManager().merge(a1);

	getEntityTransaction().commit();

      } catch (Exception e) {
        TLogger.log("Unexpected Exception :" + e );
        e.printStackTrace();
      }

      if (! pass)
	     throw new Fault("cascadeAllMXMTest10 failed");
      }

    /*
     * @testName: cascadeAllMXMTest11
     * @assertion_ids:  PERSISTENCE:SPEC:641; PERSISTENCE:SPEC:644
     * @test_Strategy:  The flush method can be used for force synchronization.  The semantics
     *                  of the flush operation applied to an entity X is as follows:
     *
     *                  For all entities Y referenced by a relationship from X, if the
     *                  relationship to Y has been annotated with the cascade member value
     *                  cascade=ALL the persist operation is applied
     *			to Y.
     *
     */


     public void cascadeAllMXMTest11() throws Fault
     {
     boolean pass = false;
     B bRef = null;
     A a1 = null;

     try {
        TLogger.log("New instances");
        getEntityTransaction().begin();
        B b1 = new B("1", "b1", 12);
        B b2 = new B("2", "b2", 12);
        B b3 = new B("3", "b3", 12);
        B b4 = new B("4", "b4", 12);
        Vector v1  = new Vector();
        v1.add(b1);
        v1.add(b2);
        v1.add(b3);
        v1.add(b4);

        TLogger.log("New A instance");
        a1 = new A("12", "bean12", 12, v1);

        getEntityManager().persist(a1);
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

     if (! pass)
	     throw new Fault("cascadeAllMXMTest11 failed");
     }


   /*
    *
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
      try {
       Vector v1 = new Vector();
       getEntityTransaction().begin();
	
	
          for (int i=1; i<14; i++ ) {
          A newA = findA(Integer.toString(i));
                if (newA != null ) {
		    newA.setBCol(v1);
		    getEntityManager().merge(newA);
                    getEntityManager().remove(newA);
                    TLogger.log("removed entity A:  " + newA);
                }
          }

          for (int i=1; i<14; i++ ) {
          B newB = findB(Integer.toString(i));
                if (newB != null ) {
		    newB.setACol(v1);
		    getEntityManager().merge(newB);
                    getEntityManager().remove(newB);
                    TLogger.log("removed entity B:  " + newB);
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


