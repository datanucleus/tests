 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.5 06/04/20
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.detach.manyXmany;

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
        } catch (Exception e) {
          e.printStackTrace();
          	throw new Fault("Setup failed:", e);
        }
    }



    /* 
     *  BEGIN Test Cases
     */


    /*
     * @testName: detachMXMTest1
     * @assertion_ids:  PERSISTENCE:SPEC:659; PERSISTENCE:SPEC:662;
     *			PERSISTENCE:SPEC:1092; PERSISTENCE:SPEC:1093
     * @test_Strategy:  The merge operation allows for the propagation of state from
     *			detached entities onto persistence entities managed by
     *			the EntityManager.  The semantics of the merge operation
     *			applied to entity X are as follows:
     *
     *			If X is a detached entity, the state of X is copied onto
     *			a pre-existing managed entity instance X1 of the same identity
     *			or a new managed copy of X1 is created.
     *
     *			If X is a managed entity, it is ignored by the merge operation
     *			however, the merge operation is cascaded to entities referenced
     *			by relationships from X if these relationships have been
     *			annotated with the cascade element value
     *
     */

    public void detachMXMTest1() throws Fault
    {

      A aRef = new A("1", "a1", 1);
      B b1 = new B("1", "b1", 1);
      B b2 = new B("2", "b2", 2);
      int foundB = 0;
      String[] expectedResults = new String[] {"1", "2"};
      boolean pass1 = true;
      boolean pass2 = false;

      try {

      TLogger.log("Begin detachMXMTest1");
      createA(aRef);

      TLogger.log("Call clean to detach");
      getEntityManager().clear();

      getEntityTransaction().begin();

      if ( ! getEntityManager().contains(aRef) ) {
                  TLogger.log("Status is false as expected, try merge");
                  // AJ ### Changed this to use aRef2 since merge returns the
                  // attached object; cant add to the detached one
                  A aRef2 = getEntityManager().merge(aRef);
			aRef2.getBCol().add(b1);
                        TLogger.log("added b1 to bcol");
			aRef2.getBCol().add(b2);
                        TLogger.log("added b2 to bcol");
                        getEntityManager().merge(aRef2);
                        TLogger.log("merged updated a");

                TLogger.log("findA and getBCol");
        	A a1 = getEntityManager().find(A.class, "1");
		Collection newCol = a1.getBCol();

        	if (newCol.size() != 2)  {
                	TLogger.log("ERROR:  detachMXMTest1: Did not get expected results."
                                + "Expected Collection Size of 2 B entities, got: "
                                + newCol.size());
              	  pass1 = false;
        	} else if (pass1) {

           	Iterator i1 = newCol.iterator();
           	while (i1.hasNext()) {
               	      TLogger.log("Check Collection B entities");
               	      B c1 = (B)i1.next();
	
       	              for(int l=0; l<2; l++) {
       	              if (expectedResults[l].equals((String)c1.getBId()) ) {
       	                  TLogger.log("Found B Entity : "
       	                          	+ (String)c1.getBName() );
       	                   foundB++;
       	                   break;
       	                   }
       	               }
		}
       	     }
	
        } else {
          TLogger.log("Entity is not detached, cannot proceed with test.");
          pass1 = false;
          pass2 = false;
        }

        getEntityTransaction().commit();
	

      } catch (Exception e) {
          org.datanucleus.util.NucleusLogger.GENERAL.info("Exception thrown in commit ", e);
  	TLogger.log("Unexpected Exception caught during commit:" + e );
	pass1 = false;
	pass2 = false;
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

     if (foundB != 2) {
                TLogger.log("ERROR: detachMXMTest1: Did not get expected results");
                pass2 = false;
           } else {
                  TLogger.log(
                    "detachMXMTest1: Expected results received");
                  pass2 = true;
           }

      if (!pass1 || !pass2)
            throw new Fault( "detachMXMTest1 failed");
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
          for (int i=1; i<10; i++ ) {
          A newA = findA(Integer.toString(i));
                if (newA != null ) {
   		    newA.setBCol(v1);
                    getEntityManager().merge(newA);
                    getEntityManager().remove(newA);
                    TLogger.log("removed entity A:  " + newA);
                }
          }

          for (int i=1; i<10; i++ ) {
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


