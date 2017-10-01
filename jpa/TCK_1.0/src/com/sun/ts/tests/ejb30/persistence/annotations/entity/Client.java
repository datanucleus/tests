 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.9 06/04/17
 */

package com.sun.ts.tests.ejb30.persistence.annotations.entity;

import com.sun.javatest.Status;
import com.sun.ts.lib.util.*;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Properties;
import java.util.Iterator;
import java.util.Arrays;

public class Client extends PMClientBase {

    private static Coffee cRef[] = new Coffee[5];

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

            TLogger.log("Create Test data");
            createTestData();
            TLogger.log("Done creating test data");

     }  catch (Exception e) {
            TLogger.log("Exception: " + e.getMessage());
            throw new Fault("Setup failed:", e);
        }
    }


    /*
     * @testName: annotationEntityTest1
     * @assertion_ids: PERSISTENCE:SPEC:993; PERSISTENCE:SPEC:995;
     *			PERSISTENCE:JAVADOC:29; PERSISTENCE:SPEC:762
     * @test_Strategy: The name annotation element defaults to the unqualified
     *			name of the entity class.  This name is used to refer to
     *			the entities in queries.  
     *	
     *			Name the entity using a lower case name and ensure the
     *			query can be executed with the lower case entity
     *			name as the abstract schema name.
     *			
     */

   public void annotationEntityTest1() throws Fault
   {

     TLogger.log("Begin annotationEntityTest1");
     boolean pass = true;
     List c = null;

     try {
       getEntityTransaction().begin();
       String[] expectedBrands = new String[] {"vanilla creme",
			 "mocha", "hazelnut", "decaf", "breakfast blend"};

            TLogger.log("Find coffees by brand name");
            c = getEntityManager().createQuery(
                "Select c.brandName from cof c ORDER BY c.brandName DESC")
                .setMaxResults(10)
                .getResultList();

            String[] result = (String[])(c.toArray(new String[c.size()]));
            TLogger.log("Compare results of Coffee Brand Names");
            pass = Arrays.equals(expectedBrands, result);

    		if ( ! pass ) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 5 Coffees : "
                        + "vanilla creme, mocha, hazelnut, decaf, breakfast blend. " 
			+ " Received: " + c.size() );
                        Iterator it = c.iterator();
                        while (it.hasNext()) {
                 	  TLogger.log(" Coffee Brand Name: " +it.next() );
			}
                } else {
		  TLogger.log("annotationEntityTest1: Expected results received");
            	}

       getEntityTransaction().commit();

     } catch (Exception e) {
         TLogger.log("Unexpection Exception :" + e );
         e.printStackTrace();
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

      if (! pass )
            throw new Fault( "annotationEntityTest1 failed");
    }


    /*
     * @testName: annotationEntityTest2
     * @assertion_ids: PERSISTENCE:SPEC:993; PERSISTENCE:SPEC:995;
     *			PERSISTENCE:JAVADOC:29
     * @test_Strategy: The name annotation element defaults to the unqualified
     *			name of the entity class.  This name is used to refer to
     *			the entities in queries.  
     *	
     *			Name the entity using a different name than the entity
     *			class name and ensure the query can be executed
     *			with the lower case entity name as the abstract schema name
     *			selecting teh 
     *			
     */

   public void annotationEntityTest2() throws Fault
   {

     TLogger.log("Begin annotationEntityTest2");
     boolean pass1 = true;
     boolean pass2 = false;
     List c = null;

     try {
       getEntityTransaction().begin();
       Integer[] expectedPKs = new Integer[] {21, 22, 23, 24, 25};

            TLogger.log("Find all coffees");
            c = getEntityManager().createQuery(
                "Select c from cof c")
                .setMaxResults(10)
                .getResultList();

            if(c.size() != 5) {
                TLogger.log(
                "ERROR:  Did not get expected results.  Expected 5 references, got: "
                                + c.size());
              pass1 = false;
            } else if (pass1) {
              TLogger.log("Expected size received, verify contents . . . ");
              Iterator i = c.iterator();
              int foundCof = 0;
              while (i.hasNext()) {
                        TLogger.log("Check List for expected coffees");
                        Coffee o = (Coffee)i.next();
                        for(int l=0; l<5; l++) {
                        if (expectedPKs[l].equals(o.getId()) ) {
			  TLogger.log("Found coffee with PK: " + (Integer)o.getId() );
                          foundCof++;
                          break;
                          }
                      }
           }
           if( foundCof != 5 ) {
                      TLogger.log("anotationEntityTest2: Did not get expected results");
                      pass2 = false;
           } else {
                  TLogger.log(
                    "anotationEntityTest2: Expected results received");
		  pass2 = true;
                  }
          }

       getEntityTransaction().commit();

     } catch (Exception e) {
         TLogger.log("Unexpection Exception :" + e );
         e.printStackTrace();
	 pass2 = false;
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

      if (!pass1 || !pass2 )
            throw new Fault( "annotationEntityTest1 failed");
    }


    public void cleanup()  throws Fault
    {
	try {
	  getEntityTransaction().begin();
          for (int i=21; i<26; i++ ) {
          Coffee newcoffee = getEntityManager().find(Coffee.class, new Integer(i));
		if (newcoffee != null ) {
                    getEntityManager().remove(newcoffee);
		    //getEntityManager().flush();
                    TLogger.log("removed coffee " + newcoffee);
		}
          }
	getEntityTransaction().commit();
  	} catch (Exception e) {
	  TLogger.log("Unexpected Exception caught while cleaning up:" + e);
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
	TLogger.log("cleanup finished, call super.cleanup");
	super.cleanup();
    }


    /* 

     *  Business Methods to set up data for Test Cases
     */

    private void createTestData() throws Exception
    {
	try {

        TLogger.log("createTestData");

	getEntityTransaction().begin();
        TLogger.log("Create 5 Coffees");
        cRef[0] = new Coffee(new Integer(21), "hazelnut", (float)1.0);
        cRef[1] = new Coffee(new Integer(22), "vanilla creme", (float)2.0);
        cRef[2] = new Coffee(new Integer(23), "decaf", (float)3.0);
        cRef[3] = new Coffee(new Integer(24), "breakfast blend", (float)4.0);
        cRef[4] = new Coffee(new Integer(25), "mocha", (float)5.0);

 	TLogger.log("Start to persist coffees ");
                for (int i=0; i<5; i++ ) {
                    getEntityManager().persist(cRef[i]);
                    TLogger.log("persisted coffee " + cRef[i]);
                }
	getEntityManager().flush();
 	getEntityTransaction().commit();
     }  catch (Exception e) {
        TLogger.log("Unexpection while creating test data:" + e );
        e.printStackTrace();
     }  finally {
        try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
          re.printStackTrace();
        }
      }
   }

}

