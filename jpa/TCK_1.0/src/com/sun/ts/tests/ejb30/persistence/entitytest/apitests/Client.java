 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.19 07/01/12
 */

package com.sun.ts.tests.ejb30.persistence.entitytest.apitests;

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
import javax.persistence.EntityNotFoundException;
import javax.persistence.TransactionRequiredException;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Iterator;
import java.util.Properties;

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
     * @testName: entityAPITest1
     * @assertion_ids: PERSISTENCE:JAVADOC:53
     * @test_Strategy: persist throws an IllegalArgumentException if the argument
     *                  is not an entity
     */

   public void entityAPITest1() throws Fault
   {

     TLogger.log("Begin entityAPITest1");
     Foo notAnEntity = new Foo();
     boolean pass = false;

     try {
       getEntityTransaction().begin();
       getEntityManager().persist(notAnEntity);
       getEntityTransaction().commit();

     } catch (IllegalArgumentException e) {
         pass = true;
         TLogger.log("Exception Caught as Expected: " + e );
     } catch (Exception e) {
         TLogger.log("Unexpection Exception :" + e );
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
            throw new Fault( "entityAPITest1 failed");
    }


    /*
     * @testName: entityAPITest2
     * @assertion_ids: PERSISTENCE:JAVADOC:43
     * @test_Strategy: find(Class entityClass, Object PK) returns null
     *                  if the entity does not exist.
     */

    public void entityAPITest2() throws Fault
    {

      TLogger.log("Begin entityAPITest2");
      boolean pass = false;

      try {

	Coffee doesNotExist = getEntityManager().find(Coffee.class, 55);

	if ( null == doesNotExist ) {
		TLogger.log("find returned null as expected");
		pass = true;
        }

     } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
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
            throw new Fault( "entityAPITest2 failed");
    }


    /*
     * @testName: entityAPITest3
     * @assertion_ids: PERSISTENCE:JAVADOC:43
     * @test_Strategy: find(Class entityClass, Object PK) throws an
     *                  IllegalArgumentException if the first argument
     *                  does not denote an entity type
     */

    public void entityAPITest3() throws Fault
    {
      TLogger.log("Begin entityAPITest3");
      boolean pass = false;

      try {

	getEntityManager().find(Foo.class, 1);

      } catch (IllegalArgumentException iae) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + iae );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
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
            throw new Fault( "entityAPITest3 failed");
     }


    /*
     * @testName: entityAPITest4
     * @assertion_ids: PERSISTENCE:JAVADOC:43
     * @test_Strategy: find(Class entityClass, Object PK) throws an
     *                  IllegalArgumentException if the second argument
     *                  is not a valid type for that entity's primary key
     */

    public void entityAPITest4() throws Fault
    {

      TLogger.log("Begin entityAPITest4");
      long longId = (long)55;
      boolean pass = false;

      try {

	Coffee coffee = getEntityManager().find(Coffee.class, longId);

        if (coffee == null ) {
		TLogger.log("coffee is null");
        }

      } catch (IllegalArgumentException iae) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + iae );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
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
            throw new Fault( "entityAPITest4 failed");

    }

    /*
     * @testName: entityAPITest5
     * @assertion_ids: PERSISTENCE:JAVADOC:47; PERSISTENCE:JAVADOC:62;
     *			PERSISTENCE:SPEC:743
     * @test_Strategy: getReference(Class entityClass, Object PK) throws an
     *                  EntityNotFoundException when the instance state is
     *                  first accessed.
     *
     *                  Because the given entity does not exist, its state (other than
     *                  the PK used when getReference was called) cannot be accessed.
     *
     *                  Throwing the EntityNotFoundException in this situation is
     *                  acceptable.  However, while the specification states that an
     *                  application may not access the state of a detached object
     *                  that has not been made available (section 3.2.4), it does not
     *                  specify the exception to throw in the case where the object
     *                  really does exist (i.e., in the database) and is detached.
     *
     *                  ...  In the meantime,
     *                  a vendor could argue that a PersistenceException
     *                  or other runtime exception (e.g., IllegalStateException) should be
     *                  acceptable for this test case.
     *
     *                  Returning null is not correct: since the entity does not exist,
     *                  the value of the field cannot be null.
     */

    public void entityAPITest5() throws Fault
    {

      TLogger.log("Begin entityAPITest5");
      boolean pass = false;
      String reason = null;
      Coffee newCoffee = new Coffee(new Integer(99), "french roast", (float)9.0);

      try {
        Coffee coffeeReference = getEntityManager().getReference(Coffee.class, new Integer(99));
        if(coffeeReference == null) {
            reason = "EntityManager.getReference(Coffee.class, 99) returned null";
        } else {
	    String thisBrand = coffeeReference.getBrandName();
            reason = "Did not get expected EntityNotFoundException, or other RuntimeException." +
                     " coffeeReference.getBrandName() returned " + thisBrand;
        }
      } catch (RuntimeException e) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + e );
      } catch (Exception e) {
          reason = "Expecting RuntimeException, but got " + e;
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
            throw new Fault( "entityAPITest5 failed: " + reason);
    }


    /*
     * @testName: entityAPITest6
     * @assertion_ids: PERSISTENCE:JAVADOC:47
     * @test_Strategy: getReference(Class entityClass, Object PK) throws an
     *                  IllegalArgumentException if the first argument
     *                  does not denote an entity type
     */

    public void entityAPITest6() throws Fault
    {
      TLogger.log("Begin entityAPITest6");
      boolean pass = false;

      try {

	getEntityTransaction().begin();
        TLogger.log("call getReference");

	Foo newFoo = getEntityManager().getReference(Foo.class, 1);

        TLogger.log("Check results");
	if (newFoo == null) {
           TLogger.log("newFoo is null");
	}

      } catch (IllegalArgumentException iae) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + iae );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
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
            throw new Fault( "entityAPITest6 failed");
    }


    /*
     * @testName: entityAPITest7
     * @assertion_ids: PERSISTENCE:JAVADOC:47
     * @test_Strategy: getReference(Class entityClass, Object PK) throws an
     *                  IllegalArgumentException if the second argument
     *                  is not a valid type for that entity's primary key
     */

    public void entityAPITest7() throws Fault
    {

      TLogger.log("Begin entityAPITest7");
      boolean pass = false;

      try {

	getEntityTransaction().begin();
	Coffee thisCoffee = getEntityManager().getReference(Coffee.class, new String("55"));
	getEntityTransaction().commit();

      } catch (IllegalArgumentException iae) {
        pass = true;
        TLogger.log("Exception Caught as Expected: " + iae );
      } catch (Exception e) {
        TLogger.log("Unexpection Exception :" + e );
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
            throw new Fault( "entityAPITest7 failed");

    }


    /*
     * @testName: entityAPITest8
     * @assertion_ids: PERSISTENCE:JAVADOC:54
     * @test_Strategy: refresh throws an IllegalArgumentException if the argument
     *                  is not an entity
     */

    public void entityAPITest8() throws Fault
    {

     TLogger.log("Begin entityAPITest8");
     Foo notAnEntity = new Foo();
     boolean pass = false;

      try {

        getEntityTransaction().begin();
        getEntityManager().refresh(notAnEntity);
        getEntityTransaction().commit();

      } catch (IllegalArgumentException iae) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + iae );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
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
        throw new Fault( "entityAPITest8 failed");

    }

    /*
     * @testName: entityAPITest10
     * @assertion_ids: PERSISTENCE:JAVADOC:37
     * @test_Strategy: contains throws an IllegalArgumentException if the
     *                  argument is not an entity
     */

    public void entityAPITest10() throws Fault
    {

     TLogger.log("Begin entityAPITest10");
     Foo notAnEntity = new Foo();
     boolean pass = false;

     try {

        getEntityTransaction().begin();
        getEntityManager().contains(notAnEntity);
        getEntityTransaction().commit();

     } catch (IllegalArgumentException iae) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + iae );
     } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
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
            throw new Fault( "entityAPITest10 failed");

    }


    /*
     * @testName: entityAPITest11
     * @assertion_ids: PERSISTENCE:JAVADOC:42; PERSISTENCE:SPEC:609
     * @test_Strategy: createQuery throws an IllegalArgumentException if the 
     *			Java Persistence QL query string is not valid
     */

    public void entityAPITest11() throws Fault
    {

      TLogger.log("Begin entityAPITest11");
      List result = null;
      boolean pass = false;

      try {
      	getEntityTransaction().begin();

      	result = getEntityManager().createQuery(
                "from Coffee AS c WHERE c.name LIKE 'vanilla'")
                .setMaxResults(99)
                .getResultList();

      	getEntityTransaction().commit();

      } catch (IllegalArgumentException iae) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + iae );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
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
            throw new Fault( "entityAPITest11 failed");
    }

    /*
     * @testName: entityAPITest12
     * @assertion_ids: PERSISTENCE:JAVADOC:38; PERSISTENCE:JAVADOC:119;
     *			PERSISTENCE:JAVADOC:117; PERSISTENCE:JAVADOC:118;
     *			PERSISTENCE:JAVADOC:123; PERSISTENCE:JAVADOC:124;
     *			PERSISTENCE:JAVADOC:121; PERSISTENCE:SPEC:1004
     * @test_Strategy: createNamedQuery creates an instance of Query in JPQL.
     */

    public void entityAPITest12() throws Fault
    {
      TLogger.log("Begin entityAPITest12");
      List result = null;
      boolean pass = true;

      try {
        getEntityTransaction().begin();

        result = getEntityManager().createNamedQuery("findAllCoffees")
                .getResultList();

        getEntityTransaction().commit();

	if ( ! ( result.size() == 5 ) ) {
                TLogger.log("Did not get expected results.  Expected 5 coffees, " + 
			"got: " + result.size());
		pass = false;
	}

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
	  pass = false;
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
            throw new Fault( "entityAPITest12 failed");
    }

    /*
     * @testName: entityAPITest13
     * @assertion_ids: PERSISTENCE:JAVADOC:38; PERSISTENCE:SPEC:1007;
     *			PERSISTENCE:SPEC:1009; PERSISTENCE:SPEC:1005;
     *			PERSISTENCE:SPEC:1008
     * @test_Strategy: createNamedQuery creates an instance of Query in SQL.
     *			Use the resultSetMapping to name the result set
     */

    public void entityAPITest13() throws Fault
    {

      TLogger.log("Begin entityAPITest13");
      List result = null;
      boolean pass = false;

      try {
        getEntityTransaction().begin();

	TLogger.log("Invoke query method, findallSQLCoffees");
        result = getEntityManager().createNamedQuery("findAllSQLCoffees")
                .getResultList();


	TLogger.log("Check results returned from findAllSQLCoffees");

        if ( result.size() == 5  ) {
             TLogger.log("Expected results received");
             pass = true;
        } else {
          TLogger.log("Did not get expected results.  Expected 5 coffees, " +
                        "got: " + result.size());
          pass = false;
	}

	getEntityTransaction().commit();
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
	  pass = false;
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
            throw new Fault( "entityAPITest13 failed");
    }


    /*
     * @testName: entityAPITest14
     * @assertion_ids: PERSISTENCE:JAVADOC:38; PERSISTENCE:SPEC:372.5
     * @test_Strategy: Execute a named query that uses a Constructor
     *			expression to return a collection of Java Instances.
     */


    public void entityAPITest14() throws Fault
    {

      TLogger.log("Begin entityAPITest14");
      List result = null;
      boolean pass = false;

      try {
        getEntityTransaction().begin();
        TLogger.log("Invoke query method, findAllNewCoffees");
        result = getEntityManager().createNamedQuery("findAllNewCoffees")
                .getResultList();

        TLogger.log("Check results returned from findAllNewCoffees");

        if ( result.size() == 5  ) {
             TLogger.log("Expected results received");
             pass = true;
        } else {
          TLogger.log("Did not get expected results.  Expected 5 coffees, " +
                        "got: " + result.size());
          pass = false;
        }

        getEntityTransaction().commit();
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          pass = false;
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
            throw new Fault( "entityAPITest14 failed");
      }

    /*
     * @testName: entityAPITest15
     * @assertion_ids: PERSISTENCE:JAVADOC:42; PERSISTENCE:SPEC:372.5;
     *			PERSISTENCE:SPEC:820
     * @test_Strategy: Execute a Java Persistence QL query that uses a Constructor
     *			expression to return a collection of Java Instances.
     */


    public void entityAPITest15() throws Fault
    {

      TLogger.log("Begin entityAPITest15");
      List result = null;
      boolean pass1 = true;
      boolean pass2 = false;
      int notManaged = 0;

      try {
        getEntityTransaction().begin();
        TLogger.log("Try same constructor expression query using createQuery");
	result = getEntityManager().createQuery(
		"Select NEW com.sun.ts.tests.ejb30.persistence.entitytest.apitests.Coffee(c.id, c.brandName, c.price)"
			+ " from Coffee c where c.price <> 0")
		.getResultList();

        TLogger.log("Check results returned from findAllNewCoffees");

        if ( result.size() != 5  ) {
                TLogger.log("Did not get expected results.  Expected 5 coffees, " +
                        "got: " + result.size());
                pass1 = false; 
        } else if (pass1) {

          Iterator i1 = result.iterator();
          TLogger.log("Check Collection to be sure entities are not managed");
          while (i1.hasNext()) {
                for(int l=1; l<6; l++) {
                	if ( ! getEntityManager().contains(i1.next()) ) {
                        TLogger.log("Coffee entity is not managed"  );
		    	notManaged++;
                        break;
                        }
                }
              }

        }

	if ( notManaged == 5) {
		pass2 = true;
	}

        getEntityTransaction().commit();
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          pass2 = false;
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

      if (!pass1 || !pass2 )
            throw new Fault( "entityAPITest15 failed");
      }




    /*
     * @testName: entityAPITest16
     * @assertion_ids: PERSISTENCE:JAVADOC:42; PERSISTENCE:SPEC:372.5;
     *			PERSISTENCE:SPEC:819
     * @test_Strategy: A constructor expression is not required to be an entity or
     *			mapped to the database.  Invoked a query with a Constructor
     *			expression using a non-entity class.
     */

    public void entityAPITest16() throws Fault
    {

      TLogger.log("Begin entityAPITest16");
      List result = null;
      boolean pass1 = true;
      boolean pass2 = false;
      int notManaged = 0;

      try {
        getEntityTransaction().begin();

        TLogger.log("Execute query in entityAPITest16");
        result = getEntityManager().createQuery(
		"Select NEW com.sun.ts.tests.ejb30.persistence.entitytest.apitests.Bar(c.id, c.brandName, c.price)"
			+ " from Coffee c where c.brandName = 'mocha'")
                .getResultList();


        TLogger.log("Check query results in entityAPITest16");

          if ( result.size() != 1  ) {
                TLogger.log("Did not get expected results.  Expected 1 Bar, " +
                        "got: " + result.size());
                pass1 = false;
          } else if (pass1) {

            Iterator i1 = result.iterator();
            TLogger.log("Check Collection to be sure entities are not managed");
            while (i1.hasNext()) {
		try {
                  getEntityManager().contains(i1.next());
                } catch (IllegalArgumentException iae ) {
		  TLogger.log("IllegalArgumentException expected as Bar is not an entity.");
		  pass2 = true;
                }

            }
	}

	if (! pass2) {
            getEntityTransaction().commit();
	}
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          pass2 = false;
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

      if (!pass1 || !pass2)
            throw new Fault( "entityAPITest16 failed");
      }


    /*
     * @testName: entityAPITest17
     * @assertion_ids: PERSISTENCE:JAVADOC:55
     * @test_Strategy: remove throws an IllegalArgumentException if
     *                  the instance is not an entity
     */

    public void entityAPITest17() throws Fault
    {

      TLogger.log("Begin entityAPITest17");
      Foo notAnEntity = new Foo();
      boolean pass = false;

      try {
        getEntityTransaction().begin();
        getEntityManager().remove(notAnEntity);
        getEntityTransaction().commit();

      } catch (IllegalArgumentException e) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + e );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
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
            throw new Fault( "entityAPITest17 failed");
    }


   /*
     * @testName: entityAPITest18
     * @assertion_ids: PERSISTENCE:JAVADOC:38; PERSISTENCE:SPEC:1006;
     *			PERSISTENCE:JAVADOC:120; PERSISTENCE:JAVADOC:115
     * @test_Strategy: createNamedQuery creates an instance of Query in SQL.
     *			using resultClass for the resulting instances
     */

    public void entityAPITest18() throws Fault
    {

      TLogger.log("Begin entityAPITest18");
      List result = null;
      boolean pass = false;

      try {
        getEntityTransaction().begin();

	TLogger.log("Invoke query method, findAllSQLCoffees2");
        result = getEntityManager().createNamedQuery("findAllSQLCoffees2")
                .getResultList();

	TLogger.log("Check results returned from findAllSQLCoffees2");

        if ( result.size() == 5  ) {
             TLogger.log("Expected results received");
             pass = true;
        } else {
          TLogger.log("Did not get expected results.  Expected 5 coffees, " +
                        "got: " + result.size());
          pass = false;
	}

        getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          pass = false;
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
            throw new Fault( "entityAPITest18 failed");
    }


    public void cleanup()  throws Fault
    {
	try {
	  getEntityTransaction().begin();
          for (int i=1; i<7; i++ ) {
          Coffee newcoffee = getEntityManager().find(Coffee.class, new Integer(i));
		if (newcoffee != null ) {
                    getEntityManager().remove(newcoffee);
		    getEntityManager().flush();
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
        TLogger.log("cleanup complete, calling super.cleanup");
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
        cRef[0] = new Coffee(new Integer(1), "hazelnut", (float)1.0);
        cRef[1] = new Coffee(new Integer(2), "vanilla creme", (float)2.0);
        cRef[2] = new Coffee(new Integer(3), "decaf", (float)3.0);
        cRef[3] = new Coffee(new Integer(4), "breakfast blend", (float)4.0);
        cRef[4] = new Coffee(new Integer(5), "mocha", (float)5.0);

 	TLogger.log("Start to persist coffees ");
                for (int i=0; i<5; i++ ) {
                    getEntityManager().persist(cRef[i]);
                    TLogger.log("persisted coffee " + cRef[i]);
                }
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

