 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.13 06/04/19
 */

package com.sun.ts.tests.ejb30.persistence.exceptions;

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
import javax.persistence.EntityTransaction;
import javax.persistence.TransactionRequiredException;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

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
           throw new Fault("Setup failed:", e);
        }
    }


    /*
     * @testName: exceptionTest1
     * @assertion_ids: PERSISTENCE:SPEC:606; PERSISTENCE:JAVADOC:220;
     *                 JavaEE:SPEC:10006
     * @test_Strategy: persist throws a TransactionRequiredException if 
     *                  invoked on a container-managed entity manager of
     *			type PersistenceContextType.TRANSACTION and 
     *			there is no transaction
     *
     *			When an EntityManager with an extended persistence
     *			context is used (as in Java SE environments) the
     *			persist operation may be called regardless whether
     *			a transaction is active.
     */

    public void exceptionTest1() throws Fault
    {
      boolean pass = false;
      Coffee newCoffee = new Coffee(new Integer(1), "hazelnut", (float)1.0);

      try {
	TLogger.log("Invoked persist without an active transaction");
        getEntityManager().persist(newCoffee);

      } catch (javax.persistence.TransactionRequiredException tre) {
          pass = true;
          TLogger.log("In JavaEE, Exception Caught as Expected: " + tre );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      }


      if ( (! pass) && (isStandAloneMode() ) ) {
        TLogger.log("In JavaSE, Exception Not Thrown as Expected");
	pass = true;
      }

      if (!pass)
        throw new Fault( "exceptionTest1 failed");
    }


    /*
     * @testName: exceptionTest2
     * @assertion_ids: PERSISTENCE:SPEC:606; PERSISTENCE:SPEC:738; PERSISTENCE:SPEC:740
     *                 JavaEE:SPEC:10006
     * @test_Strategy: flush() throws a javax.persistence.TransactionRequiredException 
     *                  if there is no transaction
     */

    public void exceptionTest2() throws Fault
    {

      TLogger.log("Begin exceptionTest2");
      Coffee newCoffee = new Coffee(new Integer(2), "french roast", (float)9.0);
      boolean pass = false;

      try {
	getEntityTransaction().begin();
        getEntityManager().persist(newCoffee);
	getEntityTransaction().commit();

        TLogger.log("try flush");
        getEntityManager().flush();

      } catch (javax.persistence.TransactionRequiredException tre) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + tre );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
	try {
	  if ( getEntityTransaction().isActive() ) {
	       getEntityTransaction().rollback();
	  }
 	} catch (Exception re) {
          TLogger.log("Unexpection Exception :" + re );
          re.printStackTrace();
        }
		
      }

      if (!pass)
        throw new Fault( "exceptionTest2 failed");

    }

    /*
     * @testName: exceptionTest3
     * @assertion_ids: PERSISTENCE:SPEC:606; JavaEE:SPEC:10006
     * @test_Strategy: refresh throws a TransactionRequiredException if 
     *                  invoked on a container-managed entity manager of
     *                  type PersistenceContextType.TRANSACTION and
     *                  there is no transaction
     *
     *                  When an EntityManager with an extended persistence
     *                  context is used (as in Java SE environments) the
     *                  refresh operation may be called regardless whether
     *                  a transaction is active.
     */

    public void exceptionTest3() throws Fault
    {

     TLogger.log("Begin exceptionTest3");
     boolean pass = false;
     Coffee newCoffee = new Coffee(new Integer(3), "french roast", (float)9.0);

     try {

        getEntityTransaction().begin();
        TLogger.log("Persist Coffee ");
        getEntityManager().persist(newCoffee);
        getEntityTransaction().commit();

        TLogger.log("Call refresh without an active transaction");
	getEntityManager().refresh(newCoffee);

      } catch (TransactionRequiredException tre) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + tre );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
	  try {
	       if (getEntityTransaction().isActive() ) {
		   getEntityTransaction().rollback();
		}

          } catch (Exception fe) {
            TLogger.log("Unexpection Exception Caught rolling back TX:" + fe );
            fe.printStackTrace();
          } 
      }

      if ( (! pass) && (isStandAloneMode() ) ) {
        TLogger.log("In JavaSE, Exception Not Thrown as Expected");
        pass = true;
      }

      if (!pass)
            throw new Fault( "exceptionTest3 failed");
     }


    /*
     * @testName: exceptionTest4
     * @assertion_ids: PERSISTENCE:SPEC:606; JavaEE:SPEC:10006
     * @test_Strategy: remove throws a TransactionRequiredException if there
     *                  invoked on a container-managed entity manager of
     *                  type PersistenceContextType.TRANSACTION and
     *                  there is no transaction
     *
     *                  When an EntityManager with an extended persistence
     *                  context is used (as in Java SE environments) the
     *                  remove operation may be called regardless whether
     *                  a transaction is active.

     */

    public void exceptionTest4() throws Fault
    {

      TLogger.log("Begin exceptionTest4");
      Coffee newCoffee = new Coffee(new Integer(5), "breakfast blend", (float)3.0);
      boolean pass = false;

      try {
        getEntityTransaction().begin();
        getEntityManager().persist(newCoffee);
        getEntityTransaction().commit();

        TLogger.log("Call remove without an active transaction");
        getEntityManager().remove(newCoffee);

      } catch (javax.persistence.TransactionRequiredException tre) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + tre );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
	try {
	     if (getEntityTransaction().isActive() ) {
	  	 getEntityTransaction().rollback();
	     }

        } catch (Exception fe) {
          TLogger.log("Unexpection Exception Caught rolling back TX:" + fe );
          fe.printStackTrace();
        } 

      }

     if ( (! pass) && (isStandAloneMode() ) ) {
        TLogger.log("In JavaSE, Exception Not Thrown as Expected");
        pass = true;
      }


      if (!pass)
        throw new Fault( "exceptionTest4 failed");
      }


    /*
     * @testName: exceptionTest5
     * @assertion_ids: PERSISTENCE:SPEC:606; PERSISTENCE:JAVADOC:36; JavaEE:SPEC:10006
     * @test_Strategy:  close throws an IllegalStateException will be thrown if
     *                  the EntityManager is container-managed.
     */

    public void exceptionTest5() throws Fault
    {

      TLogger.log("Begin exceptionTest5");
      boolean pass = false;

      try {
        getEntityTransaction().begin();
        getEntityManager().close();
        getEntityTransaction().commit();

      } catch (IllegalStateException ise) {
          pass = true;
          TLogger.log("Exception Caught as Expected: " + ise );
      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
        try {
             if (getEntityTransaction().isActive() ) {
                 getEntityTransaction().rollback();
             }

        } catch (Exception fe) {
          TLogger.log("Unexpection Exception Caught rolling back TX:" + fe );
          fe.printStackTrace();
        }

      }


      if ( (! pass) && (isStandAloneMode() ) ) {
        TLogger.log("In JavaSE, Exception Not Thrown as Expected");
        pass = true;
      }

      if (!pass)
            throw new Fault( "exceptionTest5 failed");
    }

    /*
     * @testName: exceptionTest6
     * @assertion_ids: PERSISTENCE:SPEC:606; JavaEE:SPEC:10006
     * @test_Strategy: refresh throws an IllegalArgumentException if the
     *                  entity is not managed
     */

    public void exceptionTest6() throws Fault
    {

      TLogger.log("Begin exceptionTest6");
      boolean pass = false;
      Coffee newCoffee = new Coffee(new Integer(7), "cinnamon", (float)7.0);

      try {
        getEntityTransaction().begin();
	getEntityManager().persist(newCoffee);
	getEntityManager().clear();

        if (! getEntityManager().contains(newCoffee) ) {
              getEntityManager().refresh(newCoffee);
        }  else {
           TLogger.log("Entity is managed, cannot proceed with test");
        }
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
        throw new Fault( "exceptionTest6 failed");
    }


    public void cleanup()  throws Fault
    {
      try {
	  getEntityTransaction().begin();
          for (int i=1; i<8; i++ ) {
          Coffee newcoffee = getEntityManager().find(Coffee.class, new Integer(i));
		if (newcoffee != null ) {
                    getEntityManager().remove(newcoffee);
                    TLogger.log("removed coffee " + newcoffee);
		}
          }
	getEntityTransaction().commit();
      } catch (Exception e) {
          TLogger.log("Unexpection Exception caught in cleanup:" + e );
          e.printStackTrace();
      } finally {
        try {
             if (getEntityTransaction().isActive() ) {
                 getEntityTransaction().rollback();
             }

        } catch (Exception fe) {
          TLogger.log("Unexpection Exception Caught rolling back TX:" + fe );
          fe.printStackTrace();
        }

      }

        TLogger.log("cleanup complete, calling super.cleanup");
	super.cleanup();
    }

}

