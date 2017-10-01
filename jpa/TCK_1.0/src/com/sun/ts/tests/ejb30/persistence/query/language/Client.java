/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 *  @(#)Client.java	1.42 06/08/23
 */

package com.sun.ts.tests.ejb30.persistence.query.language;

import com.sun.javatest.Status;
import com.sun.ts.lib.harness.*;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper;
import com.sun.ts.tests.ejb30.persistence.query.language.schema30.*;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.persistence.PersistenceException;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class Client extends PMClientBase 
{

    public static final int CUSTOMERREF = 1;
    public static final int ORDERREF = 2;
    public static final int ALIASREF = 3;
    public static final int PRODUCTREF = 4;

    public static final int NUMOFCUSTOMERS = 20;
    public static final int NUMOFORDERS = 20;
    public static final int NUMOFALIASES = 30;
    public static final int NUMOFALLPRODUCTS = 38;
    public static final int NUMOFPRODUCTS = 18;
    public static final int NUMOFSWPRODUCTS = 10;
    public static final int NUMOFHWPRODUCTS = 10;

    public static final int NUMOFADDRESSES = 36;
    public static final int NUMOFHOMEADDRESSES = 18;
    public static final int NUMOFWORKADDRESSES = 18;
    public static final int NUMOFPHONES = 36;
    public static final int NUMOFCREDITCARDS = 24;
    public static final int NUMOFLINEITEMS = 56;
    public static final int NUMOFSPOUSES = 6;
    public static final int NUMOFINFODATA = 6;
    public static final int NUMOFCOUNTRIES = 20;

    public static Phone phone[] = new Phone[50];
    public static Address address[] = new Address[50];
    public static Country country[] = new Country[50];
    public static CreditCard creditCard[] = new CreditCard[50];
    public static LineItem lineItem[] = new LineItem[60];
    public static Spouse spouse[] = new Spouse[6];
    public static Info info[] = new Info[6];

    private static Customer customerRef[] = new Customer[50];
    private static Order orderRef[] = new Order[50];
    private static Alias aliasRef[] = new Alias[50];
    private static Product productRef[] = new Product[50];
    private static HardwareProduct hardwareRef[] = new HardwareProduct[20];
    private static SoftwareProduct softwareRef[] = new SoftwareProduct[20];
    private static ShelfLife shelfRef[] = new ShelfLife[20];

    public static final Date d1 = getShelfDate(2000, 2, 14);
    public static final Date d2 = getShelfDate(2001, 6, 27);
    public static final Date d3 = getShelfDate(2002, 7, 7);
    public static final Date d4 = getShelfDate(2003, 3, 3);
    public static final Date d5 = getShelfDate(2004, 4, 10);
    public static final Date d6 = getShelfDate(2005, 2, 18);
    public static final Date d7 = getShelfDate(2000, 9, 17);
    public static final Date d8 = getShelfDate(2001, 11, 14);
    public static final Date d9 = getShelfDate(2002, 10, 4);
    public static final Date d10 = getShelfDate(2003, 1, 25);

    private static Properties props = null;
    private static List aliasCol = null;
    private static List custCol = null;
    private static List orderCol = null;
    private static List prodCol = null;


    public Client() {
    }

    public static void main(String[] args)
    {
        Client theTests = new Client();
        Status s = theTests.run(args, System.out, System.err);
        s.exit();
    }


    /*  Test setup */


    public void setup(String[] args, Properties p) throws Fault
    {
        TLogger.log("Entering Setup");
        try {
	    super.setup(args, p);
	    schema30Setup(p);
     }  catch (Exception e) {
            TLogger.log("Exception caught in Setup: " + e.getMessage());
           	throw new Fault("Setup failed:", e);
        }
    }

    public void cleanup()  throws Fault
    {
      try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in cleanup:" + re );
          re.printStackTrace();
        }
	//schemaExists = true;
        TLogger.log("cleanup ok, calling super.cleanup");
	super.cleanup();
    }


    /* Run test */


    /*
     *   @testName:  queryTest1
     *   @assertion_ids: PERSISTENCE:SPEC:312; PERSISTENCE:SPEC:322;
     *			 PERSISTENCE:SPEC:602; PERSISTENCE:SPEC:603;
     *			 PERSISTENCE:JAVADOC:91; PERSISTENCE:SPEC:785
     *   @test_Strategy:   
     *                  This query is defined on a many-one relationship.
     *			Verify the results were accurately returned.
     *
     */

    public void queryTest1() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;

      try
        {
	  getEntityTransaction().begin();
          TLogger.log("Find All Orders for Customer: Robert E. Bissett");
          o = getEntityManager().createQuery(
                "Select Distinct o from Order AS o WHERE o.customer.name = :name")
                .setParameter("name", "Robert E. Bissett")
                .setMaxResults(NUMOFORDERS)
                .getResultList();
    
          expectedPKs = new String[2];
	  expectedPKs[0] ="4";
	  expectedPKs[1] ="9";
            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
              TLogger.log(
                    "ERROR:  Did not get expected results.  Expected 2 references, got: " 
				+ o.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest1: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest1 failed", e);
        }

      if (!pass)
      	    throw new Fault( "queryTest1 failed");
      }

    /*
     *   @testName:  queryTest2
     *   @assertion_ids: PERSISTENCE:SPEC:317.1; PERSISTENCE:SPEC:750;
     *			 PERSISTENCE:SPEC:764; PERSISTENCE:SPEC:746.1
     *   @test_Strategy: Find All Customers.
     *                   Verify the results were accurately returned.
     *   
     */  
 
    public void queryTest2() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      try
        {
	  getEntityTransaction().begin();
          TLogger.log("Execute findAllCustomers");
          List result = getEntityManager().createQuery(
                        "Select Distinct Object(c) FROM Customer AS c")
                        .setMaxResults(NUMOFCUSTOMERS)
                        .getResultList();

          expectedPKs = new String[NUMOFCUSTOMERS];
          for(int i=0; i<NUMOFCUSTOMERS; i++)
              expectedPKs[i] = Integer.toString(i+1);

          if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
              TLogger.log(
                    "ERROR:  Did not get expected results.  Expected "
				 + NUMOFCUSTOMERS + " references, got: " 
				 + result.size() );
              pass = false;
          } else {
              TLogger.log(
 		   "Expected results received");
		pass = true;
          }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest2: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest2 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest2 failed");
      }

    /* 
     *   @testName:  queryTest3
     *   @assertion_ids: PERSISTENCE:SPEC:321; PERSISTENCE:SPEC:317.2;
     *			 PERSISTENCE:SPEC:332; PERSISTENCE:SPEC:323;
     *			 PERSISTENCE:SPEC:517; PERSISTENCE:SPEC:518;
     *			 PERSISTENCE:SPEC:519; PERSISTENCE:JAVADOC:93;
     *			 PERSISTENCE:JAVADOC:94
     *   @test_Strategy:   
     *                   This query is defined on a many-many relationship.
     *                   Verify the results were accurately returned.
     */


    public void queryTest3() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	getEntityTransaction().begin();
        TLogger.log("Find All Customers with Alias: imc");
	c = getEntityManager().createQuery(
        "Select Distinct Object(c) FrOm Customer c, In(c.aliases) a WHERE a.alias = :aName")
        .setParameter("aName", "imc")
        .setMaxResults(NUMOFCUSTOMERS)
        .getResultList();

        expectedPKs = new String[1];
	expectedPKs[0] = "8";
            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
              TLogger.log(
                    "ERROR:  Did not get expected results.  Expected 1 reference, got: " 
                                + c.size());
              pass = false;
            } else {
                TLogger.log("Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest3: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest3 failed", e);
        }
        
      if (!pass)
            throw new Fault( "queryTest3 failed");
    }

    /*
     *   @testName:  queryTest4
     *   @assertion_ids: PERSISTENCE:SPEC:322; PERSISTENCE:SPEC:394;
     *			 PERSISTENCE:SPEC:751; PERSISTENCE:SPEC:753;
     *			 PERSISTENCE:SPEC:754; PERSISTENCE:SPEC:755
     *   @test_Strategy:   
     *                   This query is defined on a one-one relationship
     *			 and used conditional AND in query.
     *                   Verify the results were accurately returned.
     *
     */  

    public void queryTest4() throws Fault
    {
      boolean pass = true;
      Customer c = null;
      Query q = null;

      try
        {
	  getEntityTransaction().begin();
          Customer expected = getEntityManager().find(Customer.class, "3");
          TLogger.log("Find Customer with Home Address in Swansea");
	  q = getEntityManager().createQuery (
          "SELECT c from Customer c WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip")
          .setParameter("street", "125 Moxy Lane")
          .setParameter("city", "Swansea")
          .setParameter("state", "MA")
          .setParameter("zip", "11345");

	  c = (Customer)q.getSingleResult();

       	     if ( expected == c ) {
                  TLogger.log("Expected results received");
             } else {
               TLogger.log("ERROR:  Did not get expected results."); 
               pass = false;
             }

	getEntityTransaction().commit();
      } catch (Exception e)
        {
          TLogger.log("Caught exception queryTest4: " + e);
	  e.printStackTrace();
          throw new Fault( "queryTest4 failed", e);
        }

      if (!pass)
        throw new Fault( "queryTest4 failed");
    }

    /*
     *   @testName:  queryTest5
     *   @assertion_ids: PERSISTENCE:SPEC:323; PERSISTENCE:SPEC:760;
     *			 PERSISTENCE:SPEC:761
     *   @test_Strategy: Execute a query to
     *                   find customers with a certain credit card type.
     *                   This query is defined on a one-many relationship.
     *                   Verify the results were accurately returned.
     *   
     */  

    public void queryTest5() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try {
	getEntityTransaction().begin();
        TLogger.log("Find all Customers with AXP Credit Cards");
	c = getEntityManager().createQuery(
        "Select Distinct Object(c) fRoM Customer c, IN(c.creditCards) a where a.type = :ccard")
        .setParameter("ccard", "AXP")
        .setMaxResults(NUMOFCUSTOMERS)
        .getResultList();

        expectedPKs = new String[7];
	expectedPKs[0] = "1";
	expectedPKs[1] = "4";
	expectedPKs[2] = "5";
	expectedPKs[3] = "8";
	expectedPKs[4] = "9";
	expectedPKs[5] = "12";
	expectedPKs[6] = "15";

              if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
              	  TLogger.log(
                        "ERROR:  Did not get expected results.  Expected 7 references, got: " 
                                + c.size());
                 pass = false;
              } else {
                  TLogger.log(
			"Expected results received");
              }  
        getEntityTransaction().commit();
      } catch (Exception e) {  
        TLogger.log("Caught exception queryTest5: " + e);
	e.printStackTrace();
        throw new Fault( "queryTest5 failed", e);
      } 
 
      if (!pass)
            throw new Fault( "queryTest5 failed");
      }     

    /*
     *   @testName:  queryTest6
     *   @assertion_ids:  PERSISTENCE:SPEC:348.4; PERSISTENCE:SPEC:338; PERSISTENCE:SPEC:339
     *   @test_Strategy:   
     *                   This query is defined on a one-one relationship
     *                   using conditional OR in query.
     *                   Verify the results were accurately returned.
     *   
     */  

    public void queryTest6() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
         {
	  getEntityTransaction().begin();
          TLogger.log("Find Customers with Home Address Information");

	  c = getEntityManager().createQuery(
        "SELECT DISTINCT c from Customer c WHERE c.home.street = :street OR c.home.city = :city OR c.home.state = :state or c.home.zip = :zip")
        .setParameter("street", "47 Skyline Drive")
        .setParameter("city", "Chelmsford")
        .setParameter("state", "VT")
        .setParameter("zip", "02155")
        .setMaxResults(NUMOFCUSTOMERS)
        .getResultList();

          expectedPKs = new String[4];
          expectedPKs[0] = "1";
          expectedPKs[1] = "10";
          expectedPKs[2] = "11";
          expectedPKs[3] = "13";

              if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
                TLogger.log(
                    "ERROR:  Did not get expected results.  Expected 4 references, got: " 
                                + c.size());
              } else {
                  TLogger.log(
		      "Expected results received");
              }  
	getEntityTransaction().commit();
      } catch (Exception e)
        {
          TLogger.log("Caught exception queryTest6: " + e);
	  e.printStackTrace();
          throw new Fault( "queryTest6 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest6 failed");
    }

    /*
     *   @testName:  queryTest7
     *   @assertion_ids:  PERSISTENCE:SPEC:319; PERSISTENCE:SPEC:735;
     *			  PERSISTENCE:SPEC:784
     *   @test_Strategy:  Ensure identification variables can be interpreted
     *			  correctly regardless of case.
     *                    Verify the results were accurately returned.
     *
     */

    public void queryTest7() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List p = null;
      try
        {
	  getEntityTransaction().begin();
          TLogger.log("Find All Products");
	  p = getEntityManager().createQuery(
                "Select DISTINCT Object(P) From Product p")
                .setMaxResults(NUMOFALLPRODUCTS)
                .getResultList();

          expectedPKs = new String[NUMOFALLPRODUCTS];
          for(int i=0; i<NUMOFALLPRODUCTS; i++)
              expectedPKs[i] = Integer.toString(i+1);

          if(!Util.checkEJBs(p, PRODUCTREF, expectedPKs)) {
              TLogger.log(
                    "ERROR:  Did not get expected results.  Expected "
				+ NUMOFALLPRODUCTS +
			 	  "references, got: " + p.size());
              pass = false;
          } else {
              TLogger.log(
                  "Expected results received");
          }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest7: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest7 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest7 failed");
      }

    /*
     *   @testName:  queryTest8
     *   @assertion_ids: PERSISTENCE:SPEC:348.4; PERSISTENCE:SPEC:345
     *   @test_Strategy: Execute a query containing
     *                   a conditional expression composed with logical
     *                   operator NOT.
     *                   Verify the results were accurately returned.
     *
     */

    public void queryTest8() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;

      try
        {
	  getEntityTransaction().begin();
          TLogger.log("Find all orders where the total price is NOT less than $4500");
	  o = getEntityManager().createQuery(
                "Select Distinct Object(o) FROM Order o WHERE NOT o.totalPrice < 4500")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

          expectedPKs = new String[3];
          expectedPKs[0] = "5";
          expectedPKs[1] = "11";
          expectedPKs[2] = "16";
            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
                TLogger.log(
                    "ERROR:  Did not get expected results.  Expected 3 references, got: " 
                                + o.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest8: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest8 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest8 failed");
      }

    /*
     *   @testName:  queryTest9
     *   @assertion_ids:  PERSISTENCE:SPEC:348.4; PERSISTENCE:SPEC:345
     *   @test_Strategy:  Execute a query containing a
     *			a conditional expression composed with logical
     *                  operator OR.
     *                  Verify the results were accurately returned.
     *   
     */  

    public void queryTest9() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;
 
      try
        {
	  getEntityTransaction().begin();
          TLogger.log("Find all orders where the customer name is Karen R. Tegan" +
                		" OR the total price is less than $100");
          o = getEntityManager().createQuery(
                "SeLeCt DiStInCt oBjEcT(o) FROM Order AS o WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice < 100")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

          expectedPKs = new String[5];
          expectedPKs[0] = "6";
          expectedPKs[1] = "9";
          expectedPKs[2] = "10";
          expectedPKs[3] = "12";
          expectedPKs[4] = "13";
            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
                TLogger.log(
                    "ERROR:  Did not get expected results.  Expected 5 references, got: " 
                                + o.size());
                pass = false;
            } else {
                TLogger.log(
			"Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest9: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest9 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest9 failed");
      }

    /*
     *   @testName:  queryTest10
     *   @assertion_ids:  PERSISTENCE:SPEC:346; PERSISTENCE:SPEC:347; PERSISTENCE:SPEC:348.2; PERSISTENCE:SPEC:344
     *   @test_Strategy:  Execute a query containing a
     *                  conditional expression composed with AND and OR and using
     *                  standard bracketing () for ordering. The comparison
     *			operator < and arithmetic operations are also used in
     *			the query. 
     *                  Verify the results were accurately returned.
     *   
     */

    public void queryTest10() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;
 
      try
        {
	  getEntityTransaction().begin();
          TLogger.log("Find all orders where line item quantity is 1 AND the" +
                " order total less than 100 or customer name is Robert E. Bissett");

	  o = getEntityManager().createQuery(
                "select distinct Object(o) FROM Order AS o, in(o.lineItems) l WHERE (l.quantity < 2) AND ((o.totalPrice < (3 + 54 * 2 + -8)) OR (o.customer.name = 'Robert E. Bissett'))")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

          expectedPKs = new String[4];
          expectedPKs[0] = "4";
          expectedPKs[1] = "9";
          expectedPKs[2] = "12";
          expectedPKs[3] = "13";
            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
                TLogger.log(
                    "ERROR:  Did not get expected results.  Expected 4 references, got: " 
                                + o.size());
                pass = false;
            } else {
                TLogger.log(
		    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest10: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest10 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest10 failed");
      }

    /* 
     *   @testName:  queryTest11
     *   @assertion_ids: PERSISTENCE:SPEC:338; PERSISTENCE:SPEC:339; PERSISTENCE:SPEC:341
     *   @test_strategy: Execute the findOrdersByQuery9 method using 
     *                  conditional expression composed with AND with an
     *			input parameter as a conditional factor. The
     *			comparison operator < is also used in the query.
     *                  Verify the results were accurately returned.
     *			//CHANGE THIS TO INPUT/NAMED PARAMETER
     *   
     */

    public void queryTest11() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;
      try
        {
	  getEntityTransaction().begin();
          TLogger.log("Find all orders with line item quantity < 2" +
                " for customer Robert E. Bissett");
	  o = getEntityManager().createQuery(
                "SELECT DISTINCT Object(o) FROM Order o, in(o.lineItems) l WHERE l.quantity < 2 AND o.customer.name = 'Robert E. Bissett'")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

          expectedPKs = new String[2];
          expectedPKs[0] = "4";
          expectedPKs[1] = "9";
            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
            TLogger.log(
                    "ERROR:  Did not get expected results.  Expected 2 references, got: "
                                + o.size());
                pass = false;
            } else {
                TLogger.log(
		    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest11: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest11 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest11 failed");
    }

    /*
     *   @testName:  queryTest12
     *   @assertion_ids: PERSISTENCE:SPEC:349; PERSISTENCE:SPEC:348.3
     *   @test_Strategy:   
     *			Execute a query containing the comparison operator BETWEEN.
     *                  Verify the results were accurately returned.
     *   
     */

    public void queryTest12() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;

      try
        {
	  getEntityTransaction().begin();
          TLogger.log("Find all orders with a total price BETWEEN $1000 and $1200");
	  o = getEntityManager().createQuery(
                "SELECT DISTINCT Object(o) From Order o where o.totalPrice BETWEEN 1000 AND 1200")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

          expectedPKs = new String[5];
          expectedPKs[0] = "1";
          expectedPKs[1] = "3";
          expectedPKs[2] = "7";
          expectedPKs[3] = "8";
          expectedPKs[4] = "14";
            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
             TLogger.log(
                    "ERROR:  Did not get expected results.  Expected 5 references, got: "
                                + o.size());
                pass = false;
            } else {
                TLogger.log(
		    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest12: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest12 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest12 failed");
      }

    /*
     *   @testName:  queryTest13
     *   @assertion_ids: PERSISTENCE:SPEC:349
     *   @test_Strategy:   
     *			 Execute a query containing NOT BETWEEN.
     *                   Verify the results were accurately returned.
     */

    public void queryTest13() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;
 
      try
        {
	  getEntityTransaction().begin();
          TLogger.log("Find all orders with a total price NOT BETWEEN $1000 and $1200");
		o = getEntityManager().createQuery(
		"SELECT DISTINCT Object(o) From Order o where o.totalPrice NOT bETwEeN 1000 AND 1200")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

          expectedPKs = new String[15];
          expectedPKs[0] = "2";
          expectedPKs[1] = "4";
          expectedPKs[2] = "5";
          expectedPKs[3] = "6";
          expectedPKs[4] = "9";
          expectedPKs[5] = "10";
          expectedPKs[6] = "11";
          expectedPKs[7] = "12";
          expectedPKs[8] = "13";
          expectedPKs[9] = "15";
          expectedPKs[10] = "16";
          expectedPKs[11] = "17";
          expectedPKs[12] = "18";
          expectedPKs[13] = "19";
          expectedPKs[14] = "20";
            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
                TLogger.log(
                    "ERROR:  Did not get expected results.  Expected 15 references, got: "
                                + o.size());
                pass = false;
            } else {
                TLogger.log(
		    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest13: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest13 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest13 failed");
      }  

    /*   
     *   @testName:  queryTest14
     *   @assertion_ids:  PERSISTENCE:SPEC:345; PERSISTENCE:SPEC:334
     *   @test_Strategy:  Conditional expressions are composed of other conditional
     *                  expressions, comparison operators, logical operations,
     *                  path expressions that evaluate to boolean values and
     *                  boolean literals.
     *
     *                  Execute a query method that contains
     *			a conditional expression with a path expression
     *			that evaluates to a boolean literal.
     *                  Verify the results were accurately returned.
     */
 
    public void queryTest14() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;
 
      try
        {
	  getEntityTransaction().begin();
          TLogger.log("Find all orders that do not have approved Credit Cards");
	  o = getEntityManager().createQuery(
                "select distinct Object(o) From Order o WHERE o.creditCard.approved = FALSE")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

          expectedPKs = new String[6];
          expectedPKs[0] = "1";
          expectedPKs[1] = "7";
          expectedPKs[2] = "11";
          expectedPKs[3] = "13";
          expectedPKs[4] = "18";
          expectedPKs[5] = "20";
            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
             TLogger.log(
                    "ERROR:  Did not get expected results.  Expected 6 references, got: "
                                + o.size());
                pass = false;
            } else {
		TLogger.log(
		    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest14: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest14 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest14 failed");
      }

    /*   
     *   @testName:  queryTest15
     *   @assertion_ids: PERSISTENCE:SPEC:330; PERSISTENCE:SPEC:313.6; PERSISTENCE:SPEC:412.1
     *   @test_Strategy: Execute a query method with a string literal enclosed
     *			 in single quotes (the string includes a single quote)
     *                   in the conditional expression of the WHERE clause.
     *                   Verify the results were accurately returned.
     */

     public void queryTest15() throws Fault
     {
       boolean pass = true;
       Customer c = null;
       Query q = null;

       try
         {
	  getEntityTransaction().begin();
          Customer expected = getEntityManager().find(Customer.class, "5");
           TLogger.log("Find customer with name: Stephen S. D'Milla");
	   q =  getEntityManager().createQuery(
        	"sElEcT c FROM Customer c Where c.name = :cName")
		.setParameter("cName", "Stephen S. D'Milla");
	
           c = (Customer) q.getSingleResult();

   	   if ( expected == c ) {
                  TLogger.log("Expected results received");
           } else {
             TLogger.log("ERROR:  Did not get expected results.");
             pass = false;
           }

	getEntityTransaction().commit();
       } catch (Exception e)
         {
           TLogger.log("Caught exception queryTest15: " + e);
	    e.printStackTrace();
           throw new Fault( "queryTest15 failed", e);
         }
 
       if (!pass)
             throw new Fault( "queryTest15 failed");
     }

     
    /*
     *   @testName:  queryTest16
     *   @assertion_ids: PERSISTENCE:SPEC:352; PERSISTENCE:SPEC:348.3
     *   @test_Strategy: Execute a query method using
     *			 comparison operator IN in a comparison expression
     *			 within the WHERE clause. 
     *                   Verify the results were accurately returned.
     */

    public void queryTest16() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all customers IN home city: Lexington");
	c = getEntityManager().createQuery(
                "select distinct c FROM Customer c WHERE c.home.city IN ('Lexington')")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[1];
        expectedPKs[0] = "2";
            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
                TLogger.log(
                 "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + c.size());
                pass = false;
            } else {
                TLogger.log(
		    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest16: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest16 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest16 failed");
    }

    /* 
     *   @testName:  queryTest17
     *   @assertion_ids: PERSISTENCE:SPEC:352; PERSISTENCE:SPEC:353
     *   @test_Strategy: Execute a query using
     *                  comparison operator NOT IN in a comparison expression
     *                  within the WHERE clause. 
     *                  Verify the results were accurately returned.
     */

    public void queryTest17() throws Fault 
    { 
      boolean pass = true; 
      String expectedPKs[] = null; 
      List c = null; 
      try 
        { 
	  getEntityTransaction().begin();
        TLogger.log("Find all customers NOT IN home city: Swansea or Brookline");
	c = getEntityManager().createQuery(
                "SELECT DISTINCT Object(c) FROM Customer c Left Outer Join c.home h WHERE "
		+ " h.city Not iN ('Swansea', 'Brookline')")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[15];
        expectedPKs[0] = "1";
        expectedPKs[1] = "2";
        expectedPKs[2] = "5";
        expectedPKs[3] = "6";
        expectedPKs[4] = "7";
        expectedPKs[5] = "8";
        expectedPKs[6] = "10";
        expectedPKs[7] = "11";
        expectedPKs[8] = "12";
        expectedPKs[9] = "13";
        expectedPKs[10] = "14";
        expectedPKs[11] = "15";
        expectedPKs[12] = "16";
        expectedPKs[13] = "17";
        expectedPKs[14] = "18";

            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) { 
                TLogger.log(
                 "ERROR:  Did not get expected results.  Expected 15 references, got: "
                                + c.size());
                pass = false; 
            } else { 
                TLogger.log( 
		    "Expected results received");
            } 
	getEntityTransaction().commit();
      } catch (Exception e) 
        { 
            TLogger.log("Caught exception queryTest17: " + e); 
	    e.printStackTrace();
            throw new Fault( "queryTest17 failed", e); 
        } 
 
      if (!pass) 
            throw new Fault( "queryTest17 failed"); 
    }

    /*  
     *   @testName:  queryTest18
     *   @assertion_ids: PERSISTENCE:SPEC:358; PERSISTENCE:SPEC:348.3
     *   @test_Strategy: Execute a query using the
     *                  comparison operator LIKE in a comparison expression
     *			within the WHERE clause. 
     * 			The pattern-value includes a percent character. 
     *                  Verify the results were accurately returned.
     */

    public void queryTest18() throws Fault 
    { 
      boolean pass = true; 
      String expectedPKs[] = null;
      List c = null; 
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find All Customers with home ZIP CODE that ends in 77");
	c = getEntityManager().createQuery(
                "select distinct Object(c) FROM Customer c WHERE c.home.zip LIKE '%77'")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[1]; 
        expectedPKs[0] = "2"; 
            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
                TLogger.log(
                 "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + c.size());
                pass = false; 
            } else { 
                TLogger.log( 
		    "Expected results received");
            } 
	getEntityTransaction().commit();
      } catch (Exception e) 
        {
            TLogger.log("Caught exception queryTest18: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest18 failed", e);   
        }
 
      if (!pass) 
            throw new Fault( "queryTest18 failed");  
    }

    /*  
     *   @testName:  queryTest19
     *   @assertion_ids: PERSISTENCE:SPEC:358; PERSISTENCE:SPEC:348.3
     *   @test_Strategy: Execute a query using the
     *                  comparison operator NOT LIKE in a comparison expression
     *                  within the WHERE clause.
     *			The pattern-value includes a percent character and an underscore.
     *                  Verify the results were accurately returned.
     *
     */

   public void queryTest19() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all customers with a home zip code that does not contain" +
			" 44 in the third and fourth position");
	c = getEntityManager().createQuery(
                "Select Distinct Object(c) FROM Customer c WHERE c.home.zip not like '%44_'")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[15]; 
        expectedPKs[0] = "1";    
        expectedPKs[1] = "2";    
        expectedPKs[2] = "3";    
        expectedPKs[3] = "4";    
        expectedPKs[4] = "5";    
        expectedPKs[5] = "9";    
        expectedPKs[6] = "10";    
        expectedPKs[7] = "11";    
        expectedPKs[8] = "12";    
        expectedPKs[9] = "13";    
        expectedPKs[10] = "14";    
        expectedPKs[11] = "15";    
        expectedPKs[12] = "16";    
        expectedPKs[13] = "17";    
        expectedPKs[14] = "18";    
            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 15 references, got: "
                                + c.size());
                pass = false;
            } else {
                TLogger.log( 
		    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest19: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest19 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest19 failed");
    }

    /*
     *   @testName:  queryTest20
     *   @assertion_ids: PERSISTENCE:SPEC:361; PERSISTENCE:SPEC:348.3;
     *			 PERSISTENCE:SPEC:769
     *   @test_Strategy: Execute a query using the
     *                   comparison operator IS EMPTY.
     *                   Verify the results were accurately returned.

     *
     */  
 
    public void queryTest20() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all customers who do not have aliases");
	c = getEntityManager().createQuery(
                "Select Distinct Object(c) FROM Customer c WHERE c.aliases IS EMPTY")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[7];
        expectedPKs[0] = "6";
        expectedPKs[1] = "15";
        expectedPKs[2] = "16";
        expectedPKs[3] = "17";
        expectedPKs[4] = "18";
        expectedPKs[5] = "19";
        expectedPKs[6] = "20";

        if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 7 references, got: "
                                + c.size());
                pass = false;
         } else {
                TLogger.log( 
		    "Expected results received");
         }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest20: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest20 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest20 failed");
    }

    /*
     *   @testName:  queryTest21
     *   @assertion_ids: PERSISTENCE:SPEC:361; PERSISTENCE:SPEC:348.3
     *   @test_Strategy: Execute a query using
     *			the comparison operator IS NOT EMPTY. 
     *                  Verify the results were accurately returned.
     */  
 
    public void queryTest21() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all customers who have aliases");
	c = getEntityManager().createQuery(
                "Select Distinct Object(c) FROM Customer c WHERE c.aliases IS NOT EMPTY")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[13];
        expectedPKs[0] = "1";    
        expectedPKs[1] = "2";    
        expectedPKs[2] = "3";    
        expectedPKs[3] = "4";    
        expectedPKs[4] = "5";    
        expectedPKs[5] = "7";    
        expectedPKs[6] = "8";    
        expectedPKs[7] = "9";    
        expectedPKs[8] = "10";    
        expectedPKs[9] = "11";    
        expectedPKs[10] = "12";    
        expectedPKs[11] = "13";    
        expectedPKs[12] = "14";    

            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
                TLogger.log(
                "ERROR:  Did not get expected results.  Expected 15 reference, got: "
                                + c.size());
                pass = false;
            } else {
                TLogger.log( 
		    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest21: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest21 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest21 failed");
    }

    /*
     *   @testName:  queryTest22
     *   @assertion_ids: PERSISTENCE:SPEC:359; PERSISTENCE:SPEC:316
     *   @test_Strategy: Execute a query using the IS NULL comparison operator
     *                  in the WHERE clause. 
     *                  Verify the results were accurately returned.
     */  
        
    public void queryTest22() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find All Customers who have a null work zip code");
	c = getEntityManager().createQuery(
                "sELEct dIsTiNcT oBjEcT(c) FROM Customer c WHERE c.work.zip IS NULL")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

	expectedPKs = new String[1];
        expectedPKs[0] = "13";
            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + c.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest22: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest22 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest22 failed");
    }

    /* 
     *   @testName:  queryTest23 
     *   @assertion_ids: PERSISTENCE:SPEC:359
     *   @test_Strategy: Execute a query using
     *                  the IS NOT NULL comparison operator within the WHERE
     *                  clause. 
     *                  Verify the results were accurately returned.
     *                  (This query is executed against non-NULL data.  For NULL
     *			data, see test queryTest47)
     */  

    public void queryTest23() throws Fault 
    { 
      boolean pass = true; 
      String expectedPKs[] = null; 
      List c = null; 
      try 
        { 
	  getEntityTransaction().begin();
        TLogger.log("Find all customers who do not have null work zip code entry");
	c = getEntityManager().createQuery(
                "Select Distinct Object(c) FROM Customer c WHERE c.work.zip IS NOT NULL")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

	expectedPKs = new String[17];
        expectedPKs[0] = "1";
        expectedPKs[1] = "2";
        expectedPKs[2] = "3";
        expectedPKs[3] = "4";
        expectedPKs[4] = "5";
        expectedPKs[5] = "6";
        expectedPKs[6] = "7";
        expectedPKs[7] = "8";
        expectedPKs[8] = "9";
        expectedPKs[9] = "10";
        expectedPKs[10] = "11";
        expectedPKs[11] = "12";
        expectedPKs[12] = "14";
        expectedPKs[13] = "15";    
        expectedPKs[14] = "16";    
        expectedPKs[15] = "17";    
        expectedPKs[16] = "18";    

            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) { 
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 17 references, got: "
                                + c.size());
                pass = false; 
            } else { 
                TLogger.log(
                    "Expected results received");
            } 
	getEntityTransaction().commit();
      } catch (Exception e) 
        { 
            TLogger.log("Caught exception queryTest23: " + e); 
	    e.printStackTrace();
            throw new Fault( "queryTest23 failed", e); 
        } 
  
      if (!pass) 
            throw new Fault( "queryTest23 failed"); 
    }


    /*
     *   @testName:  queryTest24
     *   @assertion_ids: PERSISTENCE:SPEC:369.1
     *   @test_Strategy: Execute a query which
     *			includes the string function CONCAT in a 
     *			functional expression within the WHERE clause.
     *                  Verify the results were accurately returned.
     */  
 
    public void queryTest24() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List a = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all aliases who have match: stevie");
	a = getEntityManager().createQuery(
                "Select Distinct Object(a) From Alias a WHERE a.alias = CONCAT('ste', 'vie')")
                .setMaxResults(NUMOFALIASES)
                .getResultList();

        expectedPKs = new String[1];
        expectedPKs[0] = "14";
            if(!Util.checkEJBs(a, ALIASREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + a.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest24: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest24 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest24 failed");
    }

    /*
     *   @testName:  queryTest25
     *   @assertion_ids: PERSISTENCE:SPEC:369.2
     *   @test_Strategy: Execute a query which
     *                  includes the string function SUBSTRING in a  
     *                  functional expression within the WHERE clause. 
     *                  Verify the results were accurately returned.
     */  
 
    public void queryTest25() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List a = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all aliases containing the substring: iris");
	a = getEntityManager().createQuery(
                "Select Distinct Object(a) From Alias a WHERE a.alias = SUBSTRING(:string1, :int2, :int3)")
                .setParameter("string1", "iris")
                .setParameter("int2", new Integer(1))
                .setParameter("int3", new Integer(4))
                .setMaxResults(NUMOFALIASES)
                .getResultList();

        expectedPKs = new String[1];
        expectedPKs[0] = "20";
            if(!Util.checkEJBs(a, ALIASREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + a.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest25: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest25 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest25 failed");
    }

    /*
     *   @testName:  queryTest26
     *   @assertion_ids: PERSISTENCE:SPEC:369.4
     *   @test_Strategy: Execute a query which includes the string function LENGTH  
     *                  in a functional expression within the WHERE clause. 
     *                  Verify the results were accurately returned.
     */  

    public void queryTest26() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List a = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find aliases whose alias name is greater than 4 characters");
	a = getEntityManager().createQuery(
                "Select Distinct OBjeCt(a) From Alias a WHERE LENGTH(a.alias) > 4")
                .setMaxResults(NUMOFALIASES)
                .getResultList();

        expectedPKs = new String[7];
        expectedPKs[0] = "8";
        expectedPKs[1] = "10";
        expectedPKs[2] = "13";
        expectedPKs[3] = "14";
        expectedPKs[4] = "18";
        expectedPKs[5] = "28";
        expectedPKs[6] = "29";
            if(!Util.checkEJBs(a, ALIASREF, expectedPKs)) {
	    TLogger.log(
                "ERROR:  Did not get expected results.  Expected 7 references, got: "
                                + a.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest26: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest26 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest26 failed");
    }


    /*
     *   @testName:  queryTest27
     *   @assertion_ids: PERSISTENCE:SPEC:369.5
     *   @test_Strategy: Execute a query which
     *                  includes the arithmetic function ABS in a  
     *                  functional expression within the WHERE clause. 
     *                  Verify the results were accurately returned.
     */  
 
    public void queryTest27() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all Orders with a total price greater than 1180");
	o = getEntityManager().createQuery(
                "Select DISTINCT Object(o) From Order o WHERE o.totalPrice > ABS(:dbl)")
		.setParameter("dbl", new Double(1180D))
                .setMaxResults(NUMOFORDERS)
                .getResultList();

        expectedPKs = new String[9];
        expectedPKs[0] = "1";
        expectedPKs[1] = "2";
        expectedPKs[2] = "4";
        expectedPKs[3] = "5";
        expectedPKs[4] = "6";
        expectedPKs[5] = "11";
        expectedPKs[6] = "16";
        expectedPKs[7] = "17";
        expectedPKs[8] = "18";
            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 9 references, got: "
                                + o.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest27: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest27 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest27 failed");
    }

    /*
     *   @testName:  queryTest28
     *   @assertion_ids:  PERSISTENCE:SPEC:369.3
     *   @test_Strategy:   Execute a query which
     *                  includes the string function LOCATE in a  
     *                  functional expression within the WHERE clause. 
     *                  Verify the results were accurately returned.
     */  

    public void queryTest28() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List a = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all aliases who contain the string: ev in their alias name");
	a = getEntityManager().createQuery(
                "Select Distinct Object(a) from Alias a where LOCATE('ev', a.alias) = 3")
                .setMaxResults(NUMOFALIASES)
                .getResultList();

        expectedPKs = new String[3];
        expectedPKs[0] = "13";
        expectedPKs[1] = "14";
        expectedPKs[2] = "18";
            if(!Util.checkEJBs(a, ALIASREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 3 references, got: "
                                + a.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest28: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest28 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest28 failed");
    }

    /*
     *   @testName:  queryTest29
     *   @assertion_ids: PERSISTENCE:SPEC:363.1; PERSISTENCE:SPEC:365
     *   @test_Strategy: Execute a query using
     *			 the comparison operator MEMBER OF in a collection
     *			 member expression.
     *                   Verify the results were accurately returned.
     */
     
    public void queryTest29() throws Fault
    {  
      boolean pass = true;
      String expectedPKs[] = null;
      List a = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find aliases who are members of customersNoop");
	a = getEntityManager().createQuery(
                "Select Distinct Object(a) FROM Alias a WHERE a.customerNoop MEMBER OF a.customersNoop")
                .setMaxResults(NUMOFALIASES)
                .getResultList();

        expectedPKs = new String[0];
            if(!Util.checkEJBs(a, ALIASREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 0 references, got: "
                                + a.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest29: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest29 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest29 failed");
    }

   /*
     *   @testName:  queryTest30
     *   @assertion_ids: PERSISTENCE:SPEC:363; PERSISTENCE:SPEC:365
     *   @test_Strategy: Execute a query using
     *                  the comparison operator NOT MEMBER in a collection
     *                  member expression. 
     *                  Verify the results were accurately returned.
     */  
     
    public void queryTest30() throws Fault
    {  
      boolean pass = true;
      String expectedPKs[] = null;
      List a = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find aliases who are NOT members of collection");   
	a = getEntityManager().createQuery(
                "Select Distinct Object(a) FROM Alias a WHERE a.customerNoop NOT MEMBER OF a.customersNoop")
                .setMaxResults(NUMOFALIASES)
                .getResultList();

 	expectedPKs = new String[NUMOFALIASES];
        		for(int i=0; i<NUMOFALIASES; i++)
              		expectedPKs[i] = Integer.toString(i+1);
            if(!Util.checkEJBs(a, ALIASREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected " + NUMOFALIASES +
				 "references, got: " + a.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest30: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest30 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest30 failed");
    }

    /*
     *   @testName:  queryTest31
     *   @assertion_ids: PERSISTENCE:SPEC:358
     *   @test_Strategy: Execute a query using the
     *                  comparison operator LIKE in a comparison expression
     *                  within the WHERE clause.
     *			The optional ESCAPE syntax is used to escape the underscore.
     *			Verify the results were accurately returned.
     *
     */  

    public void queryTest31() throws Fault 
    {    
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null; 
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all customers with an alias LIKE: sh_ll");
	c = getEntityManager().createQuery(
                "select distinct Object(c) FROM Customer c, in(c.aliases) a WHERE a.alias LIKE 'sh\\_ll' escape '\\'")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[1]; 
        expectedPKs[0] = "3"; 
            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + c.size());
                pass = false; 
            } else { 
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest31: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest31 failed", e);   
        }
 
      if (!pass)
            throw new Fault( "queryTest31 failed"); 
    }

    /*
     *   @testName:  queryTest32
     *   @assertion_ids: PERSISTENCE:SPEC:363
     *   @test_Strategy: Execute a query using
     *                  the comparison operator MEMBER in a collection
     *                  member expression with an identification variable and
     *                  omitting the optional reserved word OF. 
     *                  Verify the results were accurately returned.
     */  

    public void queryTest32() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all orders where line items are members of the orders");
	o = getEntityManager().createQuery(
                "Select Distinct Object(o) FROM Order o, LineItem l WHERE l MEMBER o.lineItems")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

	expectedPKs = new String[NUMOFORDERS];
                        for(int i=0; i<NUMOFORDERS; i++)
                        expectedPKs[i] = Integer.toString(i+1);

            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected " + NUMOFORDERS + "references, got: "
                                + o.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest32: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest32 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest32 failed");
    }

    /*
     *   @testName:  queryTest33
     *   @assertion_ids: PERSISTENCE:SPEC:363.3; PERSISTENCE:SPEC:352.1
     *   @test_Strategy:   Execute a query using
     *                  the comparison operator NOT MEMBER in a collection
     *                  member expression with input parameter omitting
     *			the optional use of 'OF'. 
     *                  Verify the results were accurately returned.
     */

    public void queryTest33() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;
      LineItem liDvc = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find orders whose orders are do NOT contain the specified line items");
	liDvc = getEntityManager().find(LineItem.class, "30");
	o = getEntityManager().createQuery(
                "Select Distinct Object(o) FROM Order o WHERE :param NOT MEMBER o.lineItems")
                .setParameter("param", liDvc)
                .setMaxResults(NUMOFORDERS)
                .getResultList();

	expectedPKs = new String[19];
        expectedPKs[0] = "1";
        expectedPKs[1] = "2";
	expectedPKs[2] = "3";
	expectedPKs[3] = "4";
	expectedPKs[4] = "5";
	expectedPKs[5] = "7";
	expectedPKs[6] = "8";
	expectedPKs[7] = "9";
	expectedPKs[8] = "10";
        expectedPKs[9] = "11";
	expectedPKs[10] = "12";
	expectedPKs[11] = "13";
	expectedPKs[12] = "14";
	expectedPKs[13] = "15";
	expectedPKs[14] = "16";
	expectedPKs[15] = "17";
	expectedPKs[16] = "18";
	expectedPKs[17] = "19";
	expectedPKs[18] = "20";

	if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
           TLogger.log(
                "ERROR:  Did not get expected results.  Expected 19 references, got: "
                                + o.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");

            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest33: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest33 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest33 failed");
    }

    /*
     *   @testName:  queryTest34
     *   @assertion_ids:  PERSISTENCE:SPEC:363.1
     *   @test_Strategy:   Execute a query using
     *                  the comparison operator MEMBER OF in a collection
     *                  member expression using single_valued_association_path_expression.
     *                  Verify the results were accurately returned.
     */  

    public void queryTest34() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find orders who have Samples in their orders");
	o = getEntityManager().createQuery(
                "Select Distinct Object(o) FROM Order o WHERE o.sampleLineItem MEMBER OF o.lineItems")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

        expectedPKs = new String[2];
	expectedPKs[0] = "1";
	expectedPKs[1] = "6";
        if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 2 references, got: "
                                + o.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");

            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest34: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest34 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest34 failed");
    }


    /*   
     *   @testName:  queryTest35
     *   @assertion_ids:  PERSISTENCE:SPEC:352
     *   @test_Strategy:   Execute a query using
     *                  comparison operator NOT IN in a comparison expression
     *                  within the WHERE clause where the value for the 
     *			state_field_path_expression contains numeric values.
     *			Verify the results were accurately returned.
     */  
 
    public void queryTest35() throws Fault 
    { 
      boolean pass = true; 
      String expectedPKs[] = null;
      List o = null; 
      try 
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all orders which contain lineitems not of quantities 1 or 5");
	o = getEntityManager().createQuery(
                "Select Distinct Object(o) from Order o, in(o.lineItems) l where l.quantity NOT IN (1, 5)")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

        expectedPKs = new String[9]; 
        expectedPKs[0] = "10"; 
        expectedPKs[1] = "12"; 
        expectedPKs[2] = "14"; 
        expectedPKs[3] = "15"; 
        expectedPKs[4] = "16"; 
        expectedPKs[5] = "17"; 
        expectedPKs[6] = "18"; 
        expectedPKs[7] = "19"; 
        expectedPKs[8] = "20"; 
            if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) { 
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 9 references, got: "
                                + o.size());
                pass = false; 
            } else { 
                TLogger.log(
                    "Expected results received");
            } 
	getEntityTransaction().commit();
      } catch (Exception e) 
        {
            TLogger.log("Caught exception queryTest35: " + e); 
	    e.printStackTrace();
            throw new Fault( "queryTest35 failed", e); 
        }
 
      if (!pass) 
            throw new Fault( "queryTest35 failed"); 
    }

    /*   
     *   @testName:  queryTest36 
     *   @assertion_ids:  PERSISTENCE:SPEC:352 
     *   @test_Strategy:  Execute a query using
     *                  comparison operator IN in a conditional expression
     *                  within the WHERE clause where the value for the IN
     *                  expression is an input parameter.
     *                  Verify the results were accurately returned.
     */  
 
    public void queryTest36() throws Fault 
    {    
      boolean pass = true; 
      String expectedPKs[] = null;
      List c = null; 
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all customers who lives in city Attleboro");
	c = getEntityManager().createQuery(
                "SELECT c From Customer c where c.home.city IN(:city)")
                .setParameter("city", "Attleboro")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[1]; 
        expectedPKs[0] = "13"; 
            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) { 
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + c.size());
                pass = false; 
            } else { 
                TLogger.log(
                    "Expected results received");
            } 
	getEntityTransaction().commit();
      } catch (Exception e) 
        {
            TLogger.log("Caught exception queryTest36: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest36 failed", e);
        }
 
      if (!pass) 
            throw new Fault( "queryTest36 failed");
    }

    /*
     *   @testName:  queryTest37
     *   @assertion_ids:  PERSISTENCE:SPEC:354
     *   @test_Strategy:   Execute two methods using the comparison
     *                  operator IN in a comparison expression
     *                  within the WHERE clause and verify the results
     *		        of the two queries are equivalent regardless of
     *			the way the expression is composed.
     */  

    public void queryTest37() throws Fault
    {
      boolean pass1 = true;
      boolean pass2 = true;
      String expectedPKs[] = null;
      String expectedPKs2[] = null;
      List c1 = null;
      List c2 = null;

      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Execute two queries composed differently and verify results");
        c1 = getEntityManager().createQuery(
                "SELECT DISTINCT Object(c) from Customer c where c.home.state IN('NH', 'RI')")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

	expectedPKs = new String[5];
        expectedPKs[0] = "5";
        expectedPKs[1] = "6";
        expectedPKs[2] = "12";
        expectedPKs[3] = "14";
        expectedPKs[4] = "16";

	c2 = getEntityManager().createQuery(
                "SELECT DISTINCT Object(c) from Customer c WHERE (c.home.state = 'NH') OR (c.home.state = 'RI')")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

	expectedPKs2 = new String[5];
        expectedPKs2[0] = "5";   
        expectedPKs2[1] = "6";
        expectedPKs2[2] = "12";
        expectedPKs2[3] = "14";
        expectedPKs2[4] = "16";

            if(!Util.checkEJBs(c1, CUSTOMERREF, expectedPKs))
	    {
        	TLogger.log(
                "ERROR:  Did not get expected results for first query.  Expected 5 reference, got: "
                                + c1.size());
                pass1 = false;
            } else {
                TLogger.log(
                    "Expected results received for first query");
            } 

	    if(!Util.checkEJBs(c2, CUSTOMERREF, expectedPKs2))
            {
                TLogger.log(
                "ERROR:  Did not get expected results for second query.  Expected 5 reference, got: "
                                + c2.size());
                pass2 = false;
            } else {
                TLogger.log(
                    "Expected results received for second query");
            }      
 
	getEntityTransaction().commit();
     	} catch (Exception e)
          {
            TLogger.log("Caught exception queryTest37: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest37 failed", e);
          }

      if (!pass1 || !pass2)
            throw new Fault( "queryTest37 failed");
       }

    /*   
     *   @testName:  queryTest38 
     *   @assertion_ids: PERSISTENCE:SPEC:369.7
     *   @test_Strategy: Execute a query which
     *                  includes the arithmetic function MOD in a
     *                  functional expression within the WHERE clause.
     *                  Verify the results were accurately returned.
     */  
 
    public void queryTest38() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List p = null;
 
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find orders that have the quantity of 50 available");
	p = getEntityManager().createQuery(
                "Select DISTINCT Object(p) From Product p where MOD(550, 100) = p.quantity")
                .setMaxResults(NUMOFPRODUCTS)
                .getResultList();

        expectedPKs = new String[2];
        expectedPKs[0] = "5";   
        expectedPKs[1] = "20";   
            if(!Util.checkEJBs(p, PRODUCTREF, expectedPKs)) {
                TLogger.log(
                "ERROR:  Did not get expected results.  Expected 2 references, got: "
                                + p.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest38: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest38 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest38 failed");
    }


    /*
     *   @testName:  queryTest39
     *   @assertion_ids:  PERSISTENCE:SPEC:369.6;
     *			 PERSISTENCE:SPEC:814; PERSISTENCE:SPEC:816
     *   @test_Strategy:  Execute a query which
     *                  includes the arithmetic function SQRT in a
     *                  functional expression within the WHERE clause.
     *			Verify the results were accurately returned.   
     */
 
    public void queryTest39() throws Fault
    {   
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      double dbl = 50;

      try
      {
	  getEntityTransaction().begin();
        TLogger.log("Find customers with specific credit card balance");
	c = getEntityManager().createQuery(
                "Select Distinct OBJECT(c) from Customer c, IN(c.creditCards) b where SQRT(b.balance) = :dbl")
                .setParameter("dbl", new Double(50))
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[1];
        expectedPKs[0] = "3";
 
        if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
            TLogger.log(
                "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + c.size());
                pass = false;
              } else {
                TLogger.log(
                    "Expected results received");
              }  
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest39: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest39 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest39 failed");
    }   

   /*
     *   @testName:  queryTest40
     *   @assertion_ids:  PERSISTENCE:SPEC:350
     *   @test_Strategy:  Execute two methods using the comparison
     *                  operator BETWEEN in a comparison expression
     *                  within the WHERE clause and verify the results
     *                  of the two queries are equivalent regardless of
     *                  the way the expression is composed.
     */

    public void queryTest40() throws Fault
    {
      boolean pass1 = true;
      boolean pass2 = true;
      String expectedPKs[] = null;
      String expectedPKs2[] = null;
      List p1 = null;
      List p2 = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Execute two queries composed differently and verify results" +
         " Execute Query 1");
        p1 = getEntityManager().createQuery(
                "Select DISTINCT OBJECT(p) From Product p where p.quantity BETWEEN 10 AND 20")
                .setMaxResults(NUMOFPRODUCTS)
                .getResultList();
        expectedPKs = new String[7];
        expectedPKs[0] = "8";
        expectedPKs[1] = "9";
        expectedPKs[2] = "17";
        expectedPKs[3] = "27";
        expectedPKs[4] = "28";
        expectedPKs[5] = "31";
        expectedPKs[6] = "36";

        TLogger.log("Execute Query 2");
        p2 = getEntityManager().createQuery(
                "Select DISTINCT OBJECT(p) From Product p where (p.quantity >= 10) AND (p.quantity <= 20)")
                .setMaxResults(NUMOFPRODUCTS)
                .getResultList();

        expectedPKs2 = new String[7];
        expectedPKs2[0] = "8";
        expectedPKs2[1] = "9";
        expectedPKs2[2] = "17";
        expectedPKs2[3] = "27";
        expectedPKs2[4] = "28";
        expectedPKs2[5] = "31";
        expectedPKs2[6] = "36";

       if(!Util.checkEJBs(p1, PRODUCTREF, expectedPKs))
       {
                TLogger.log(
                "ERROR:  Did not get expected results for first query in queryTest40. " 
				+ "  Expected 7 references, got: "
                                + p1.size());
                pass1 = false;
            } else {
              TLogger.log(
                "Expected results received for first query in queryTest40.");
              }

        if(!Util.checkEJBs(p2, PRODUCTREF, expectedPKs2))
        {
                TLogger.log(
                  "ERROR:  Did not get expected results for second query in queryTest40. "
				+ "  Expected 7 references, got: "
                                + p2.size());
                pass2 = false;
            } else {
              TLogger.log(
                "Expected results received for second query in queryTest40.");
              }

	getEntityTransaction().commit();
        } catch (Exception e)
          {
            TLogger.log("Caught exception queryTest40: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest40 failed", e);
        }

        if (!pass1 || !pass2)
              throw new Fault( "queryTest40 failed");
    }

    /*
     *   @testName:  queryTest41
     *   @assertion_ids:  PERSISTENCE:SPEC:350
     *   @test_Strategy:  Execute two methods using the comparison
     *                  operator NOT BETWEEN in a comparison expression
     *                  within the WHERE clause and verify the results
     *                  of the two queries are equivalent regardless of
     *                  the way the expression is composed.
     */

    public void queryTest41() throws Fault
    {
      boolean pass1 = true;
      boolean pass2 = true;
      String expectedPKs[] = null;
      String expectedPKs2[] = null;
      List p1 = null;
      List p2 = null;

      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Execute two queries composed differently and verify results" +
        " Execute first query");
        p1 = getEntityManager().createQuery(
                "Select DISTINCT Object(p) From Product p where p.quantity NOT BETWEEN 10 AND 20")
                .setMaxResults(NUMOFALLPRODUCTS)
                .getResultList();

        expectedPKs = new String[31];
        expectedPKs[0] = "1";
        expectedPKs[1] = "2";
        expectedPKs[2] = "3";
        expectedPKs[3] = "4";
        expectedPKs[4] = "5";
        expectedPKs[5] = "6";
        expectedPKs[6] = "7";
        expectedPKs[7] = "10";
        expectedPKs[8] = "11";
        expectedPKs[9] = "12";
        expectedPKs[10] = "13";
        expectedPKs[11] = "14";
        expectedPKs[12] = "15";
        expectedPKs[13] = "16";
        expectedPKs[14] = "18";
        expectedPKs[15] = "19";
        expectedPKs[16] = "20";
        expectedPKs[17] = "21";
        expectedPKs[18] = "22";
        expectedPKs[19] = "23";
        expectedPKs[20] = "24";
        expectedPKs[21] = "25";
        expectedPKs[22] = "26";
        expectedPKs[23] = "29";
        expectedPKs[24] = "30";
        expectedPKs[25] = "32";
        expectedPKs[26] = "33";
        expectedPKs[27] = "34";
        expectedPKs[28] = "35";
        expectedPKs[29] = "37";
        expectedPKs[30] = "38";

        TLogger.log("Execute second query");
        p2 = getEntityManager().createQuery(
                "Select DISTINCT Object(p) From Product p where (p.quantity < 10) OR (p.quantity > 20)")
                .setMaxResults(NUMOFALLPRODUCTS)
                .getResultList();

        expectedPKs2 = new String[31];
        expectedPKs2[0] = "1";
        expectedPKs2[1] = "2";
        expectedPKs2[2] = "3";
        expectedPKs2[3] = "4";
        expectedPKs2[4] = "5";
        expectedPKs2[5] = "6";
        expectedPKs2[6] = "7";
        expectedPKs2[7] = "10";
        expectedPKs2[8] = "11";
        expectedPKs2[9] = "12";
        expectedPKs2[10] = "13";
        expectedPKs2[11] = "14";
        expectedPKs2[12] = "15";
        expectedPKs2[13] = "16";
        expectedPKs2[14] = "18";
        expectedPKs2[15] = "19";
        expectedPKs2[16] = "20";
        expectedPKs2[17] = "21";
        expectedPKs2[18] = "22";
        expectedPKs2[19] = "23";
        expectedPKs2[20] = "24";
        expectedPKs2[21] = "25";
        expectedPKs2[22] = "26";
        expectedPKs2[23] = "29";
        expectedPKs2[24] = "30";
        expectedPKs2[25] = "32";
        expectedPKs2[26] = "33";
        expectedPKs2[27] = "34";
        expectedPKs2[28] = "35";
        expectedPKs2[29] = "37";
        expectedPKs2[30] = "38";

        if(!Util.checkEJBs(p1, PRODUCTREF, expectedPKs))
        {
                TLogger.log(
                "ERROR:  Did not get expected results for first query.  Expected 31 references, got: "
                                + p1.size());
                pass1 = false;
            } else {
             TLogger.log(
                "Expected results received for first query");
              }

        if(!Util.checkEJBs(p2, PRODUCTREF, expectedPKs2))
        {
                TLogger.log(
                  "ERROR:  Did not get expected results for second query.  Expected 31 references, got: "
                                + p2.size());
                pass2 = false;
            } else {
              TLogger.log(
                "Expected results received for second query");
              }
	getEntityTransaction().commit();
        } catch (Exception e)
          {
            TLogger.log("Caught exception queryTest41: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest41 failed", e);
          }

      if (!pass1 || !pass2)
            throw new Fault( "queryTest41 failed");
    }

    /*
     *   @testName:  queryTest42
     *   @assertion_ids:  PERSISTENCE:SPEC:423
     *   @test_Strategy:  This tests that nulls are eliminated using a
     *			  single-valued_association_field with IS NOT NULL.
     *			  Verify results are accurately returned.
     *
     */

    public void queryTest42() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List o = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all orders where related customer name is not null");
	o = getEntityManager().createQuery(
                "Select Distinct Object(o) from Order o where o.customer.name IS NOT NULL")
                .setMaxResults(NUMOFORDERS)
                .getResultList();

        expectedPKs = new String[19];
        expectedPKs[0] = "1";
        expectedPKs[1] = "2";
        expectedPKs[2] = "3";
        expectedPKs[3] = "4";
        expectedPKs[4] = "5";
        expectedPKs[5] = "6";
        expectedPKs[6] = "7";
        expectedPKs[7] = "8";
        expectedPKs[8] = "9";
        expectedPKs[9] = "10";
        expectedPKs[10] = "11";
        expectedPKs[11] = "12";
        expectedPKs[12] = "14";
        expectedPKs[13] = "15";
        expectedPKs[14] = "16";
        expectedPKs[15] = "17";
        expectedPKs[16] = "18";
        expectedPKs[17] = "19";
        expectedPKs[18] = "20";


        if(!Util.checkEJBs(o, ORDERREF, expectedPKs)) {
                TLogger.log(
                  "ERROR:  Did not get expected results.  Expected 19 references, got: "
                                + o.size());
                pass = false;
              } else {
                TLogger.log(
                    "Expected results received");
              }  
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest42: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest42 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest42 failed");
    }

    /*
     *   @testName:  queryTest43
     *   @assertion_ids:  PERSISTENCE:SPEC:425
     *   @test_Strategy: Execute a query using Boolean operator AND
     *                  in a conditional test ( False AND False = False)
     *                  where the second condition is not NULL.
     *
     */

     public void queryTest43() throws Fault
     {
      boolean pass = true;
      String expectedPKs[] = null;
      List p = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Check results of AND operator: False AND False = False");
	p = getEntityManager().createQuery(
    	    "Select Distinct Object(p) from Product p where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)")
                .setParameter("int1", new Integer(100))
                .setMaxResults(NUMOFPRODUCTS)
                .getResultList();

        expectedPKs = new String[0];
        if(!Util.checkEJBs(p, PRODUCTREF, expectedPKs)) {
              TLogger.log(
                "ERROR:  Did not get expected results.  Expected 0 references, got: "
                                + p.size());
                pass = false;
              } else {
                TLogger.log(
                    "Expected results received");
              }  
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest43: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest43 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest43 failed");
    }

    /*
     *   @testName:  queryTest44
     *   @assertion_ids:  PERSISTENCE:SPEC:416
     *   @test_Strategy: If an input parameter is NULL, comparison operations
     *			involving the input parameter will return an unknown value.
     *
     */

    public void queryTest44() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List p = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Provide a null value for a comparison operation and verify the results");
	p = getEntityManager().createQuery(
                "Select Distinct Object(p) from Product p where p.name = ?1")
                .setParameter(1, null)
                .setMaxResults(NUMOFPRODUCTS)
                .getResultList();

        expectedPKs = new String[0];

        if(!Util.checkEJBs(p, PRODUCTREF, expectedPKs)) {
               TLogger.log(
                 "ERROR:  Did not get expected results.  Expected 0 references, got: "
                                + p.size());
                pass = false;
              } else {
                TLogger.log(
                    "Expected results received");
              }  
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest44: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest44 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest44 failed");
    }

    /*
     *   @testName:  queryTest45
     *   @assertion_ids: PERSISTENCE:SPEC:361
     *   @test_Strategy: Execute a query using IS NOT EMPTY
     *                  in a collection_valued_association_field where the field is EMPTY.
     */  

    public void queryTest45() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;

      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find customers whose id is greater than 1 " +
			"OR where the relationship is NOT EMPTY");
	c = getEntityManager().createQuery(
                "Select Object(c) from Customer c where c.aliasesNoop IS NOT EMPTY or c.id <> '1'")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[19];
        expectedPKs[0] = "2";
        expectedPKs[1] = "3";
        expectedPKs[2] = "4";
        expectedPKs[3] = "5";
        expectedPKs[4] = "6";
        expectedPKs[5] = "7";
        expectedPKs[6] = "8";
        expectedPKs[7] = "9";
        expectedPKs[8] = "10";
        expectedPKs[9] = "11";
        expectedPKs[10] = "12";
        expectedPKs[11] = "13";
        expectedPKs[12] = "14";
        expectedPKs[13] = "15";
        expectedPKs[14] = "16";
        expectedPKs[15] = "17";
        expectedPKs[16] = "18";
        expectedPKs[17] = "19";
        expectedPKs[18] = "20";
            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
               TLogger.log(
                 "ERROR:  Did not get expected results.  Expected 19 references, got: "
                                + c.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest45: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest45 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest45 failed");
    }

    /*
     *   @testName:  queryTest46
     *   @assertion_ids:  PERSISTENCE:SPEC:337; PERSISTENCE:SPEC:342; PERSISTENCE:SPEC:359
     *   @test_Strategy: Use the operator IS NULL in a null comparison expression 
     *                   within the WHERE clause where the named parameter is null.
     *			 Verify the results were accurately returned.
     *
     */

    public void queryTest46() throws Fault
    {
    boolean pass = false;
    String[] expectedResult1 = new String[] {null};
    String[] expectedResult2 = new String[] {null, null, null};
    List s = null;
    try
        {
	  getEntityTransaction().begin();
        TLogger.log("Execute query with input parameter and IS NULL");
	s = getEntityManager().createQuery(
                "SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1")
                .setParameter("param1", null)
                .setMaxResults(NUMOFALIASES)
                .getResultList();

          if (s.size() == 1) {
                TLogger.log("Checking results for alias names with size of " + s.size());
		String[] result = (String[])(s.toArray(new String[s.size()]));
		pass = Arrays.equals(expectedResult1, result);
               	TLogger.log("Expected results received with size :" + s.size());
           } else {
		if (s.size() == 3) {
  			String[] result = (String[])(s.toArray(new String[s.size()]));   
                	pass = Arrays.equals(expectedResult2, result);
               	        TLogger.log("Expected results received with size :" + s.size());
                 } else {
                        TLogger.log("ERROR: Did not received expected results");
		 }
	  }
	getEntityTransaction().commit();
        } catch (Exception e)
          {
            TLogger.log("Caught exception queryTest46: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest46 failed", e);
          }

      if (!pass)
            throw new Fault( "queryTest46 failed");
    }

    /*
     *   @testName:  queryTest47
     *   @assertion_ids:  PERSISTENCE:SPEC:376; PERSISTENCE:SPEC:401;
     *			  PERSISTENCE:SPEC:399.3; PERSISTENCE:SPEC:422;
     *			  PERSISTENCE:SPEC:752; PERSISTENCE:SPEC:753
     *   @test_Strategy: The IS NOT NULL construct can be used
     *                  to eliminate the null values from the result
     *                  set of the query.
     *			Verify the results are accurately returned.
     */

    public void queryTest47() throws Fault
    {
        boolean pass = false;
        List c = null;
        String[] expectedZips = new String[] {"00252", "00252", "00252",
                                                "00252", "00252", "00252", "00252",
                                                "00252", "00252", "00252", "00252",
                                                "00252", "00252", "00252", "00252",
                                                "00252", "11345"};
        try {  
	  getEntityTransaction().begin();
            TLogger.log("Find work zip codes that are not null");
	    c = getEntityManager().createQuery(
                "Select c.work.zip from Customer c where c.work.zip IS NOT NULL ORDER BY c.work.zip ASC")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

            String[] result = (String[])(c.toArray(new String[c.size()]));
            TLogger.log("Compare results of work zip codes");
            pass = Arrays.equals(expectedZips, result);
	getEntityTransaction().commit();
        } catch (Exception e) {
            TLogger.log("Caught exception queryTest47: " + e);
	    e.printStackTrace();
            throw new Fault("queryTest47 failed", e);
        }

        if (!pass)
              throw new Fault("queryTest47 failed");
    }

    /*
     *   @testName:  queryTest48
     *   @assertion_ids:  PERSISTENCE:SPEC:329; PERSISTENCE:SPEC:328;
     *			  PERSISTENCE:SPEC:348.1; PERSISTENCE:SPEC:399.1;
     *			  PERSISTENCE:SPEC:772
     *   @test_Strategy:  This query, which includes a null non-terminal association-field, 
     *			  verifies the null is not included in the result set.
     */

    public void queryTest48() throws Fault
    {
      boolean pass = false;
      List o = null;
      Double[] expectedBalances = new Double[] { new Double(400D),  new Double(500D),
						 new Double(750D),  new Double(1000D),
                                                 new Double(1400D), new Double(1500D),
						 new Double(2000D), new Double(2500D),
						 new Double(4400D), new Double(5000D),
						 new Double(5500D), new Double(7000D),
						 new Double(7400D), new Double(8000D),
						 new Double(9500D), new Double(13000D),
						 new Double(15000D), new Double(23000D) };
       try {
	  getEntityTransaction().begin();
            TLogger.log("Find all credit card balances");
	    o = getEntityManager().createQuery(
		"Select Distinct o.creditCard.balance from Order o ORDER BY o.creditCard.balance ASC")
		.setMaxResults(NUMOFORDERS)
		.getResultList();

            Double[] result = (Double[])(o.toArray(new Double[o.size()]));
	    Iterator i = o.iterator();
	    while ( i.hasNext() ) {
	    TLogger.log("Query results returned:  " + (Double)i.next() );
	    }
            TLogger.log("Compare expected results to query results");
            pass = Arrays.equals(expectedBalances, result);

	getEntityTransaction().commit();
        } catch (Exception e) {
            TLogger.log("Caught exception queryTest48: " + e);
	    e.printStackTrace();
            throw new Fault("queryTest48 failed", e);
        }

        if (!pass )
              throw new Fault("queryTest48 failed");
    }

    /*
     *   @testName:  queryTest49
     *   @assertion_ids:  PERSISTENCE:SPEC:359
     *   @test_Strategy: Use the operator IS NULL
     *                  in a null comparison expression using
     *                  a single_valued_path_expression.
     *                  Verify the results were accurately returned.
     */ 

    public void queryTest49() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find All Customers who have a null relationship");
	c = getEntityManager().createQuery(
                "Select Distinct Object(c) FROM Customer c, in(c.aliases) a WHERE a.customerNoop IS NULL")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

            expectedPKs = new String[13];
            expectedPKs[0] = "1";
            expectedPKs[1] = "2";
            expectedPKs[2] = "3";
            expectedPKs[3] = "4";
            expectedPKs[4] = "5";
            expectedPKs[5] = "7";
            expectedPKs[6] = "8";
            expectedPKs[7] = "9";
            expectedPKs[8] = "10";
            expectedPKs[9] = "11";
            expectedPKs[10] = "12";
            expectedPKs[11] = "13";
            expectedPKs[12] = "14";

            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
                TLogger.log(
                  "ERROR:  Did not get expected results.  Expected 13 references, got: "
                                + c.size());
                pass = false;
              } else {
                TLogger.log(
                    "Expected results received");
              }

	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest49: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest49 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest49 failed");
    }

    /*
     *   @testName:  queryTest50
     *   @assertion_ids: PERSISTENCE:SPEC:358
     *   @test_Strategy:   Execute a query using the
     *                  comparison operator LIKE in a comparison expression
     *                  within the WHERE clause using percent (%) to wild card any
     *			expression including the optional ESCAPE syntax.
     *                  Verify the results were accurately returned.
     *
     */

    public void queryTest50() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Find all customers with an alias that contains an underscore");
        c = getEntityManager().createQuery(
                "select distinct Object(c) FROM Customer c, in(c.aliases) a WHERE a.alias LIKE '%\\_%' escape '\\'")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[1];
        expectedPKs[0] = "3";
            if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
              TLogger.log(
                "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + c.size());
              pass = false;
            } else {
              TLogger.log(
                  "Expected results received");

            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest50: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest50 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest50 failed");
    }

    /* 
     *   @testName:  queryTest51 
     *   @assertion_ids:  PERSISTENCE:SPEC:359
     *   @test_Strategy: Use the operator IS NOT NULL 
     *                  in a null comparision expression within the WHERE 
     *                  clause where the single_valued_path_expression is NULL.
     *                  Verify the results were accurately returned.
     *   
     */
         
    public void queryTest51() throws Fault 
    { 
      boolean pass = true; 
      List c = null; 
      try 
        { 
	  getEntityTransaction().begin();
        TLogger.log("Find All Customers who do not have null relationship");
	c= getEntityManager().createQuery(
                "sElEcT Distinct oBJeCt(c) FROM Customer c, IN(c.aliases) a WHERE a.customerNoop IS NOT NULL")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

	    if(c.size() != 0) {
              TLogger.log(
                "ERROR:  Did not get expected results.  Expected 0 references, got: "
                                + c.size());
              pass = false; 
            } else { 
              TLogger.log(
                  "Expected results received");
            } 
	getEntityTransaction().commit();
      } catch (Exception e) 
        { 
            TLogger.log("Caught exception queryTest51: " + e); 
	    e.printStackTrace();
            throw new Fault( "queryTest51 failed", e); 
        } 
  
      if (!pass) 
            throw new Fault( "queryTest51 failed"); 
    }


    /*
     *   @testName:  queryTest52
     *   @assertion_ids:  PERSISTENCE:SPEC:424; PERSISTENCE:SPEC:789
     *   @test_Strategy: Define a query using Boolean operator AND 
     *                  in a conditional test ( True AND True = True)
     *			where the second condition is NULL.
     *                  Verify the results were accurately returned.
     */  
 
    public void queryTest52() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	getEntityTransaction().begin();
        TLogger.log("Determine if customer has a NULL relationship");
	c = getEntityManager().createQuery(
                "select Distinct Object(c) from Customer c, in(c.aliases) a where c.name = :cName AND a.customerNoop IS NULL")
                .setParameter("cName", "Shelly D. McGowan")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[1];
        expectedPKs[0] = "3";
        if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
          TLogger.log(
                "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + c.size());
                pass = false;
              } else {
              TLogger.log(
                  "Expected results received");
              }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest52: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest52 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest52 failed");
    }

    /*   
     *   @testName:  queryTest53
     *   @assertion_ids: PERSISTENCE:SPEC:425
     *   @test_Strategy: Define a query using Boolean operator OR
     *                  in a conditional test (True OR True = True)
     *			where the second condition is NULL.
     *                  Verify the results were accurately returned.
     */  
 
    public void queryTest53() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null; 
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Determine if customer has a NULL relationship");
	c = getEntityManager().createQuery(
                "select distinct object(c) fRoM Customer c, IN(c.aliases) a where c.name = :cName OR a.customerNoop IS NULL")
                .setParameter("cName", "Arthur D. Frechette")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[13];
        expectedPKs[0] = "1";
        expectedPKs[1] = "2";
        expectedPKs[2] = "3";
        expectedPKs[3] = "4";
        expectedPKs[4] = "5";
        expectedPKs[5] = "7";
        expectedPKs[6] = "8";
	expectedPKs[7] = "9";
        expectedPKs[8] = "10";
        expectedPKs[9] = "11";
        expectedPKs[10] = "12";
        expectedPKs[11] = "13";
        expectedPKs[12] = "14";

        if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
              TLogger.log(
                "ERROR:  Did not get expected results.  Expected 13 references, got: "
                                + c.size());
                pass = false; 
              } else {  
              TLogger.log(
                "Expected results received");
              }  
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest53: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest53 failed", e);
        }
 
      if (!pass) 
            throw new Fault( "queryTest53 failed");
    }

    /*
     *   @testName:  queryTest54
     *   @assertion_ids:  PERSISTENCE:SPEC:426
     *   @test_Strategy: Define a query using Boolean operator NOT
     *                  in a conditional test (NOT True = False)
     *			where the relationship is NULL. 
     *                  Verify the results were accurately returned.
     */  

    public void queryTest54() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Determine if customers have a NULL relationship");
	c = getEntityManager().createQuery(
                "SELECT DISTINCT Object(c) from Customer c, in(c.aliases) a where NOT a.customerNoop IS NULL")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        if(c.size() != 0) {
              TLogger.log(
                "ERROR:  Did not get expected results.  Expected 0 references, got: "
                                + c.size());
              pass = false;
            } else {
              TLogger.log(
                  "Expected results received");
            }
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest54: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest54 failed", e);
        }
 
      if (!pass)
            throw new Fault( "queryTest54 failed");
    }

    /*
     *   @testName:  queryTest55
     *   @assertion_ids:  PERSISTENCE:SPEC:358
     *   @test_Strategy:  The LIKE expression uses an input parameter for the condition.
     *                    Verify the results were accurately returned.
     *
     */

    public void queryTest55() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Determine which customers have an area code beginning with 9");
	c = getEntityManager().createQuery(
                "SELECT Distinct Object(c) From Customer c, IN(c.home.phones) p where p.area LIKE :area")
                .setParameter("area", "9%")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[3];
        expectedPKs[0] = "3";
        expectedPKs[1] = "12";
        expectedPKs[2] = "16";

        if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
      	      TLogger.log(
                "ERROR:  Did not get expected results.  Expected 3 references, got: "
                                + c.size());
                pass = false;
              } else {
                TLogger.log(
                  "Expected results received");
              }  
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest55: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest55 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest55 failed");
    }


    /*
     *   @testName:  queryTest56
     *   @assertion_ids:  PERSISTENCE:SPEC:375; PERSISTENCE:SPEC:410;
     *			  PERSISTENCE:SPEC:403; PERSISTENCE:SPEC:283;
     *			  PERSISTENCE:SPEC:814; PERSISTENCE:SPEC:816
     *   @test_Strategy:  This query returns a null single_valued_association_path_expression.
     *			  Verify the results were accurately returned.
     */

    public void queryTest56() throws Fault
    {

      boolean pass1 = true;
      boolean pass2 = true;
      List c = null;
      String[] expectedZips = new String[] {"00252", "00252", "00252",
                                                "00252", "00252", "00252", "00252",
                                                "00252", "00252", "00252", "00252",
                                                "00252", "00252", "00252", "00252",
                                                "00252", "11345"};

        try {
	  getEntityTransaction().begin();
            TLogger.log("Find all work zip codes");
	    c = getEntityManager().createQuery(
                "Select c.work.zip from Customer c")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();
 
            if(c.size() != 18) {
                TLogger.log(
                "ERROR:  Did not get expected results.  Expected 18 references, got: "
                                + c.size());
              pass1 = false;
            } else if (pass1) {
              Iterator i = c.iterator();
              int numOfNull = 0;
              int foundZip = 0;
              while (i.hasNext()) {
                        TLogger.log("Check contents of List for null");
                        Object o = i.next();
                        if (o == null) {
                        numOfNull++;
                        continue; }
 
                        TLogger.log("Check List for expected zip codes");
 
                        for(int l=0; l<17; l++) {
                        if (expectedZips[l].equals(o) ) {
                          foundZip++;
                          break;
                          }
                      }
           }
           if((numOfNull != 1) || (foundZip != 17)) {
                      TLogger.log("Did not get expected results");
                      pass2 = false;
           } else {
                  TLogger.log(
                    "Expected results received");
                  }
          }

	getEntityTransaction().commit();
        } catch (Exception e) {
            TLogger.log("Caught exception queryTest56: " + e);
	    e.printStackTrace();
            throw new Fault("queryTest56 failed", e);
        }

        if (!pass1 || !pass2 )
            throw new Fault("queryTest56 failed");
    }


    /*
     *   @testName:  queryTest57
     *   @assertion_ids:  PERSISTENCE:SPEC:409; PERSISTENCE:SPEC:270
     *	 @test_Strategy:  This query returns a null single_valued_association_field.
     *			  Verify the results are accurately returned.
     */

    public void queryTest57() throws Fault
    {
     boolean pass = false;
     Object c = null;
 
        try {
	  getEntityTransaction().begin();
            TLogger.log("Find customer spouse"); 
	    c = getEntityManager().createQuery(
                "Select s.customer from Spouse s where s.id = '6'")
                .getSingleResult();

	getEntityTransaction().commit();
        } catch (NoResultException e) {
            TLogger.log("Exception caught as expected queryTest57: " + e);
            pass = true;	
        } catch (Exception e) {
            TLogger.log("Unexcpected exception caught in queryTest57: " + e);
            throw new Fault("queryTest57 failed", e);
        }

        if (!pass)
              throw new Fault("queryTest57 failed");
    }

    /*
     *   @testName:  queryTest58
     *   @assertion_ids:  PERSISTENCE:SPEC:377; PERSISTENCE:SPEC:410;
     *			  PERSISTENCE:SPEC:280; PERSISTENCE:SPEC:767
     *   @test_Strategy:  This query returns a null single_valued_association_path_expression.
     *			  Verify the results are accurately returned.
     */

    public void queryTest58() throws Fault
    {
     boolean pass = true;
     Object s = null;

        try {
	  getEntityTransaction().begin();
            TLogger.log("Find home zip codes");
            s = getEntityManager().createQuery(
                "Select c.name from Customer c where c.home.street = '212 Edgewood Drive'")
                .getSingleResult();

            if (s != null) {
	         TLogger.log(
                   "ERROR:  Did not get expected results.  Expected null.");
                  pass = false;
                } else {
                  TLogger.log(
                    "Expected results received");
                       }
	getEntityTransaction().commit();
        } catch (Exception e) {
            TLogger.log("Caught exception queryTest58: " + e);
	    e.printStackTrace();
            throw new Fault("queryTest58 failed", e);
        }
        if (!pass)
              throw new Fault("queryTest58 failed");
    }

    /*
     *   @testName:  queryTest59
     *   @assertion_ids:  PERSISTENCE:SPEC:420; PERSISTENCE:SPEC:408
     *   @test_Strategy:  This tests a null single_valued_association_path_expression is
     *			  returned using IS NULL.
     *			  Verify the results are accurately returned.
     *
     */

    public void queryTest59() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
	  getEntityTransaction().begin();
        TLogger.log("Determine which customers have an null name");
	c = getEntityManager().createQuery(
                "Select Distinct Object(c) from Customer c where c.name is null")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[1];
        expectedPKs[0] = "12";

        if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
               TLogger.log(
                "ERROR:  Did not get expected results.  Expected 1 reference, got: "
                                + c.size());
                pass = false;
              } else {
                  TLogger.log(
                    "Expected results received");
              }  
	getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest59: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest59 failed", e);
        }

      if (!pass)
            throw new Fault( "queryTest59 failed");
    }

    /*
     *   @testName:  queryTest60
     *   @assertion_ids: PERSISTENCE:SPEC:775; PERSISTENCE:SPEC:771; PERSISTENCE:SPEC:773
     *   @test_Strategy:  This query contains an identification variable defined in a collection member 
     *			  declaration which is not used in the rest of the query
     *   		  however, a JOIN operation needs to be performed for the correct result set.
     *                    Verify the results are accurately returned.
     *
     */

    public void queryTest60() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
        getEntityTransaction().begin();
        TLogger.log("Find Customers with an Order");
        c = getEntityManager().createQuery(
   		"SELECT DISTINCT c FROM Customer c, IN(c.orders) o")
                .setMaxResults(NUMOFCUSTOMERS)
                .getResultList();

        expectedPKs = new String[18];
        expectedPKs[0] = "1";
        expectedPKs[1] = "2";
        expectedPKs[2] = "3";
        expectedPKs[3] = "4";
        expectedPKs[4] = "5";
        expectedPKs[5] = "6";
        expectedPKs[6] = "7";
        expectedPKs[7] = "8";
        expectedPKs[8] = "9";
        expectedPKs[9] = "10";
        expectedPKs[10] = "11";
        expectedPKs[11] = "12";
        expectedPKs[12] = "13";
        expectedPKs[13] = "14";
        expectedPKs[14] = "15";
        expectedPKs[15] = "16";
        expectedPKs[16] = "17";
        expectedPKs[17] = "18";


        if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
               TLogger.log(
                "ERROR:  Did not get expected results.  Expected 18 references, got: "
                                + c.size());
                pass = false;
              } else {
                  TLogger.log(
                    "Expected results received");
              } 
        getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest60: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest60 failed", e);
        }

      if (!pass)
           throw new Fault( "queryTest60 failed");
    }

    /*
     *   @testName:  queryTest61
     *   @assertion_ids: PERSISTENCE:SPEC:778;PERSISTENCE:SPEC:780
     *   @test_Strategy: Execute a query defining an identification variable for c.work
     *   		in an OUTER JOIN clause. The JOIN operation will include
     *                   customers without addresses. Verify the results are accurately returned.
     */

    public void queryTest61() throws Fault
    {
      boolean pass = true;
      String expectedPKs[] = null;
      List c = null;
      try
        {
        getEntityTransaction().begin();
 		c = getEntityManager().createQuery(
                	"select Distinct c FROM Customer c LEFT OUTER JOIN " 
				+ "c.work workAddress where workAddress.zip IS NULL")
                	.setMaxResults(NUMOFCUSTOMERS)
                	.getResultList();

        	expectedPKs = new String[3];
        	expectedPKs[0] = "13";
        	expectedPKs[1] = "19";
        	expectedPKs[2] = "20";

            	if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
            	TLogger.log(
                	"ERROR:  Did not get expected results.  Expected 3 references, got: "
                                + c.size());
                pass = false;
            	} else {
                  TLogger.log("Expected results received");
            	}

        getEntityTransaction().commit();
      } catch (Exception e)
        {
            TLogger.log("Caught exception queryTest61: " + e);
	    e.printStackTrace();
            throw new Fault( "queryTest61 failed", e);
        }

      if (!pass)
           throw new Fault( "queryTest61 failed");
    }


    /*
     *
     *   @testName:  queryTest62
     *   @assertion_ids: PERSISTENCE:SPEC:369.8
     *   @test_Strategy: Execute a query which includes the arithmetic function 
     *                  SIZE in a functional expression within the WHERE clause.
     *                  The SIZE function returns an integer value
     *                  the number of elements of the Collection.
     *                  Verify the results were accurately returned.
     */

     public void queryTest62() throws Fault
     {
       boolean pass = true;
       String expectedPKs[] = null;
       List c = null;

       try
       {
         getEntityTransaction().begin();

                c = getEntityManager().createQuery(
                "SELECT DISTINCT c FROM Customer c WHERE SIZE(c.orders) >= 2")
                .getResultList();

                 expectedPKs = new String[2];
                 expectedPKs[0] = "4";
                 expectedPKs[1] = "14";

                 if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
                 TLogger.log(
                         "ERROR:  Did not get expected results.  Expected 2 references, got: "
                                 + c.size());
                 pass = false;
                 } else {
                   TLogger.log("Expected results received");
                 }

         getEntityTransaction().commit();
       } catch (Exception e)
         {
             TLogger.log("Caught exception queryTest62: " + e);
            e.printStackTrace();
             throw new Fault( "queryTest62 failed", e);
         }

       if (!pass)
            throw new Fault( "queryTest62 failed");
     }


    /*
     *   @testName:  queryTest63
     *   @assertion_ids: PERSISTENCE:SPEC:369.8
     *   @test_Strategy: Execute a query which includes the arithmetic function
     *                  SIZE in a functional expression within the WHERE clause.
     *                  The SIZE function returns an integer value
     *                  the number of elements of the Collection.
     *
     *                  If the Collection is empty, the SIZE function
     *                  evaluates to zero.
     *
     *                  Verify the results were accurately returned.
     */

     public void queryTest63() throws Fault
     {
       boolean pass = true;
       String expectedPKs[] = null;
       List c = null;

       try
       {
         getEntityTransaction().begin();

                c = getEntityManager().createQuery(
                        "SELECT DISTINCT c FROM Customer c WHERE SIZE(c.orders) > 100")
                        .getResultList();

                expectedPKs = new String[0];

                 if(!Util.checkEJBs(c, CUSTOMERREF, expectedPKs)) {
                 TLogger.log(
                         "ERROR:  Did not get expected results.  Expected 0 references, got: "
                                 + c.size());
                 pass = false;
                 } else {
                   TLogger.log("Expected results received");
                 }

         getEntityTransaction().commit();
       } catch (Exception e)
         {
             TLogger.log("Caught exception queryTest63: " + e);
             e.printStackTrace();
             throw new Fault( "queryTest63 failed", e);
         }

       if (!pass)
            throw new Fault( "queryTest63 failed");
     }

    /*
     *   @testName:  queryTest64
     *   @assertion_ids: PERSISTENCE:SPEC:372.5;PERSISTENCE:SPEC:817;
     *			 PERSISTENCE:SPEC:395
     *   @test_Strategy: A constructor may be used in the SELECT list to
     *                  return a collection of Java instances.  The
     *                  specified class is not required to be an entity
     *                  or mapped to the database. The constructor name
     *                  must be fully qualified.
     *
     *                  Verify the results were accurately returned.
     */

     public void queryTest64() throws Fault
     {
       boolean pass = true;
       List c = null;

       try
       {
         getEntityTransaction().begin();
                c = getEntityManager().createQuery(
                        "SELECT NEW com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer "
                                + "(c.id, c.name) FROM Customer c where "
                                + " c.work.city = :workcity")
                        .setParameter("workcity", "Burlington")
                        .setMaxResults(NUMOFCUSTOMERS)
                        .getResultList();


                if( c.size() != 18 ) {
                TLogger.log(
                        "ERROR:  Did not get expected results.  Expected 18 , got: "
                                 + c.size());
                 pass = false;
                } else {
                   TLogger.log("Expected results received");
                }

         getEntityTransaction().commit();
       } catch (Exception e)
         {
             TLogger.log("Caught exception queryTest64: " + e);
            e.printStackTrace();
             throw new Fault( "queryTest64 failed", e);
         }

       if (!pass)
            throw new Fault( "queryTest64 failed");

       }


    /*
     *   @testName:  queryTest65
     *   @assertion_ids: PERSISTENCE:SPEC:381; PERSISTENCE:SPEC:313.3;
     *			 PERSISTENCE:SPEC:386; PERSISTENCE:SPEC:406;
     *			 PERSISTENCE:SPEC:825; PERSISTENCE:SPEC:822
     *   @test_Strategy: Execute a query which contains the aggregate function MIN.  
     *                  Verify the results are accurately returned.
     *
     */

    public void queryTest65() throws Fault
    {
        boolean pass = true;
        String s1 = new String("4");
	Query q = null;
        String s2 = null;

        try {
          TLogger.log("Find MINIMUM order id for Robert E. Bissett");
          q = getEntityManager().createQuery(
		"SELECT DISTINCT MIN(o.id) FROM Order o where o.customer.name = 'Robert E. Bissett' ");

	  s2 = (String) q.getSingleResult();

          if (s2.equals(s1)) {
                        TLogger.log("Successfully returned expected results");
                } else {
                        TLogger.log("queryTest65 returned " + s2 + "expected: " + s1);
                        pass = false;
                       }
        } catch (Exception e) {
            TLogger.log("Caught exception queryTest65: " + e);
            e.printStackTrace();
            throw new Fault("queryTest65 failed", e);
        }
        if (!pass)
            throw new Fault("queryTest65 failed");
    }

    /*
     *   @testName:  queryTest66
     *   @assertion_ids: PERSISTENCE:SPEC:382; PERSISTENCE:SPEC:387;
     *			 PERSISTENCE:SPEC:406; PERSISTENCE:SPEC:406.1;
     *			 PERSISTENCE:SPEC:825; PERSISTENCE:SPEC:822
     *   @test_Strategy: Execute a query which contains the aggregate function MAX.
     *			Verify the results are accurately returned.
     *
     */

    public void queryTest66() throws Fault
    {
        boolean pass = true;
        Integer i1 = new Integer(8);
	Query q = null;
        Integer i2 = 0;

        try {
          TLogger.log("Find MAXIMUM number of lineItem quantities available an order may have");
	  q = getEntityManager().createQuery("SELECT DISTINCT MAX(l.quantity) FROM LineItem l");
		
	  i2 = (Integer)q.getSingleResult();

          if ( i2.equals(i1) ) {
                        TLogger.log("Successfully returned expected results");
                } else {
                        TLogger.log("queryTest66 returned:"  + i2 + "expected: " + i1);
                        pass = false;
                       }
        } catch (Exception e) {
            TLogger.logErr("Caught exception queryTest66: " + e);
            e.printStackTrace();
            throw new Fault("queryTest66 failed", e);
        }
        if (!pass)
            throw new Fault("queryTest66 failed");
    }


    /*
     *   @testName:  queryTest67
     *   @assertion_ids: PERSISTENCE:SPEC:380; PERSISTENCE:SPEC:406;
     *			 PERSISTENCE:SPEC:406.1; PERSISTENCE:SPEC:385;
     *			 PERSISTENCE:SPEC:826; PERSISTENCE:SPEC:821;
     *			 PERSISTENCE:SPEC:814; PERSISTENCE:SPEC:818
     *   @test_Strategy: Execute a query using the aggregate function AVG.
     *			 Verify the results are accurately returned.
     *
     */

    public void queryTest67() throws Fault
    {
        boolean pass = true;
        Double d1 = 1487.29;
        Double d2 = 1487.30;
	Double d3 = null;
	Query q = null;

        try {
          TLogger.logMsg("Find AVERAGE price of all orders");
	  q = getEntityManager().createQuery("SELECT AVG(o.totalPrice) FROM Order o");

	  d3 = (Double)q.getSingleResult();

          if ( ( (d3 >= d1) && (d3 < d2) ) ) {
                     TLogger.log("queryTest67 returned expected results: " + d1);
             } else {
                TLogger.log("queryTest67 returned " + d3 + "expected: " + d1);
                pass = false;
                    }
        } catch (Exception e) {
            TLogger.log("Caught exception queryTest67: " + e);
            e.printStackTrace();
            throw new Fault("queryTest67 failed", e);
        }
        if (!pass)
            throw new Fault("queryTest67 failed");
    }

    /*
     *   @testName:  queryTest68
     *   @assertion_ids: PERSISTENCE:SPEC:383; PERSISTENCE:SPEC:406;
     *			 PERSISTENCE:SPEC:406.1; PERSISTENCE:SPEC:388
     *   @test_Strategy: Execute a query which contains the aggregate function SUM.
     *                   SUM returns Double when applied to state-fields of floating 
     *                   types.
     *                   Verify the results are accurately returned.
     *
     */

    public void queryTest68() throws Fault
    {
        boolean pass = true;
        Double d1 = new Double(33387.14);
        Double d2 = new Double(33387.15);
	Double d3 = null;
	Query q = null;

        try {
          TLogger.logMsg("Find SUM of all product prices");
	  q = getEntityManager().createQuery("SELECT Sum(p.price) FROM Product p");

	  d3 = (Double)q.getSingleResult();

          if ( ( (d3 >= d1) && (d3 < d2) ) ) {
                     TLogger.logMsg("queryTest68 returned expected results: " + d1);
             } else {
                TLogger.log("queryTest68 returned " + d3 + "expected: " + d1);
                pass = false;
                    }
        } catch (Exception e) {
            TLogger.logErr("Caught exception queryTest68: " + e);
            e.printStackTrace();
            throw new Fault("queryTest68 failed", e);
        }
        if (!pass)
            throw new Fault("queryTest68 failed");
    }


    /*
     *   @testName:  queryTest69
     *   @assertion_ids: PERSISTENCE:SPEC:384; PERSISTENCE:SPEC:389;
     *		         PERSISTENCE:SPEC:406; PERSISTENCE:SPEC:406.4;
     *			 PERSISTENCE:SPEC:824; PERSISTENCE:SPEC:392
     *   @test_Strategy:  This test verifies the same results of two queries using
     *                  the keyword DISTINCT or not using DISTINCT in the query with
     *                  the aggregate keyword COUNT to verity the NULL values are eliminated
     *                  before the aggregate is applied.
     *
     */

    public void queryTest69() throws Fault
    {
      boolean pass1 = false;
      boolean pass2 = false;
      Query q1 = null;
      Query q2 = null;
      Long expectedResult1 = new Long(17);
      Long expectedResult2 = new Long(16);

      try
        {
        TLogger.log("Execute two queries composed differently and verify results");

        q1 = getEntityManager().createQuery("Select Count(c.home.city) from Customer c");
	Long result1 = (Long) q1.getSingleResult();

        if ( ! (result1.equals(expectedResult1)) ) {
                TLogger.log("ERROR: Query1 in queryTest69 returned:"
				 + result1 + " expected: " + expectedResult1 );
                pass1 = false;
           } else {
                TLogger.log("PASS:  Query1 in queryTest69 returned expected results");
                pass1 = true;
                }

        q2 = getEntityManager().createQuery("Select Count(Distinct c.home.city) from Customer c");

	Long result2 = (Long) q2.getSingleResult();

        if ( ! (result2.equals(expectedResult2)) ) {
                TLogger.log("ERROR: Query 2 in queryTest69 returned:"
				 + result2 + " expected: " + expectedResult2 );
                pass2 = false;
           } else {
                TLogger.log("PASS:  Query 2 in queryTest69 returned expected results");
                pass2 = true;
           }

        } catch (Exception e)
          {
            TLogger.log("Caught exception queryTest69: " + e);
            e.printStackTrace();
            throw new Fault( "queryTest69 failed", e);
        }

        if (!pass1 || !pass2)
          throw new Fault( "queryTest69 failed");

	}

    /*
     *   @testName:  queryTest70
     *   @assertion_ids: PERSISTENCE:SPEC:383; PERSISTENCE:SPEC:406;
     *			 PERSISTENCE:SPEC:406.1; PERSISTENCE:SPEC:388;
     *			 PERSISTENCE:SPEC:827; PERSISTENCE:SPEC:821
     *   @test_Strategy: Execute a query which contains the aggregate function SUM.
     *                   SUM returns Long when applied to state-fields of integral
     *                   types.
     *                   Verify the results are accurately returned.
     *
     */

    public void queryTest70() throws Fault
    {
        boolean pass = true;
        Long expectedValue = new Long(3277);
        Long result;
        Query q = null;

        try {
          TLogger.logMsg("Find SUM of all product prices");
          q = getEntityManager().createQuery("SELECT Sum(p.quantity) FROM Product p");

          result = (Long)q.getSingleResult();

          if ( expectedValue.equals(result) ) {
                     TLogger.logMsg("queryTest70 returned expected results: " + result);
             } else {
                TLogger.log("queryTest70 returned " + result + "expected: " + expectedValue);
                pass = false;
                    }
        } catch (Exception e) {
            TLogger.logErr("Caught exception queryTest70: " + e);
            e.printStackTrace();
            throw new Fault("queryTest70 failed", e);
        }
        if (!pass)
            throw new Fault("queryTest70 failed");
    }

   /*
    *   @testName: test_leftouterjoin_1xM
    *   @assertion_ids: PERSISTENCE:SPEC:780
    *   @test_Strategy: LEFT OUTER JOIN for 1-M relationship.
    *                   Retrieve credit card information for a customer with
    *                   name like Caruso.
    *
    */

    public void test_leftouterjoin_1xM() throws Fault
    {
        List result = null;
        boolean pass = true;
        String expectedPKs[] = null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT c from Customer c LEFT OUTER JOIN c.creditCards cc where c.name LIKE '%Caruso'")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="7";
            expectedPKs[1] ="8";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
              TLogger.log(
                    "ERROR: Did not get expected results.  Expected 2 references, got: "
                                + result.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_leftouterjoin_1x1: " + e);
          throw new Fault( "test_leftouterjoin_1x1 failed", e);
        }

    if (!pass)
           throw new Fault( "test_leftouterjoin_1x1 failed");
    }

   /*
    *   @testName: test_leftouterjoin_Mx1
    *   @assertion_ids: PERSISTENCE:SPEC:780; PERSISTENCE:SPEC:399.1;
    *			PERSISTENCE:SPEC:399
    *   @test_Strategy: Left Outer Join for M-1 relationship.
    *                   Retrieve customer information from Order.
    *
    */

    public void test_leftouterjoin_Mx1() throws Fault
    {
      List q = null;
      boolean pass1 = true;
      boolean pass2 = false;
      Object[][] expectedResultSet = new Object[][]{ new Object[]{"15","14"},
                                                new Object[]{"16", "14"} };

        try {
            getEntityTransaction().begin();
            q = getEntityManager().createQuery(
            "SELECT o.id, cust.id from Order o LEFT OUTER JOIN o.customer cust"
                        + " where cust.name=?1 ORDER BY o.id")
            .setParameter(1, "Kellie A. Sanborn")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

          if ( q.size()  != 2 ) {
                TLogger.log("test_leftouterjoin_Mx1:  Did not get expected results. "
                        + "Expected 2,  got: " + q.size() );
                pass1 = false;
           } else if (pass1) {
             TLogger.log("Expected size received, verify contents . . . ");
             //each element of the list q should be a size-2 array
             for(int i = 0; i < q.size(); i++) {
                Object obj = q.get(i);
                Object[] orderAndCustomerExpected = expectedResultSet[i];
                Object[] orderAndCustomer = null;
                if(obj instanceof Object[]) {
                  TLogger.log("The element in the result list is of type Object[], continue . . .");
                    orderAndCustomer = (Object[]) obj;
                    pass2 = Arrays.equals(orderAndCustomerExpected, orderAndCustomer);
                    if(!pass2) {
                        TLogger.log("ERROR: Expecting element value: "
                         + Arrays.asList(orderAndCustomerExpected)
                         + ", actual element value: "
                         + Arrays.asList(orderAndCustomer));
                        break;
                    }
                } else {
                  pass2 = false;
                  TLogger.log("ERROR: The element in the result list is not of type Object[]:" + obj);
                  break;
                }
            }
         }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_leftouterjoin_Mx1: " + e);
          throw new Fault( "test_leftouterjoin_Mx1 failed", e);
        }

    if (!pass1 || !pass2)
           throw new Fault( "test_leftouterjoin_Mx1 failed");
    }

   /*
    *   @testName: test_leftouterjoin_MxM
    *   @assertion_ids: PERSISTENCE:SPEC:780; PERSISTENCE:SPEC:317;
    *			PERSISTENCE:SPEC:317.3; PERSISTENCE:SPEC:320;
    *			PERSISTENCE:SPEC:811
    *   @test_Strategy: Left Outer Join for M-M relationship.
    *                   Retrieve all aliases where customer name like Ste.
    *
    */

    public void test_leftouterjoin_MxM() throws Fault
    {

      List q = null;
      boolean pass1 = true;
      boolean pass2 = false;
      Object[][] expectedResultSet = new Object[][]{ new Object[]{"7", "sjc"},
                                                new Object[]{"5", "ssd"},
                                                new Object[]{"7", "stevec"},
                                                new Object[]{"5", "steved"},
                                                new Object[]{"5", "stevie"},
                                                new Object[]{"7", "stevie"} };
       try {
            getEntityTransaction().begin();
            q = getEntityManager().createQuery(
            "SELECT c.id, a.alias from Customer c LEFT OUTER JOIN c.aliases a "
                        + "where c.name LIKE 'Ste%' ORDER BY a.alias, c.id")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

          if ( q.size()  != 6 ) {
                TLogger.log("test_leftouterjoin_MxM:  Did not get expected results. "
                        + "Expected 6,  got: " + q.size() );
                pass1 = false;
           } else if (pass1) {
             TLogger.log("Expected size received, verify contents . . . ");
             //each element of the list q should be a size-2 array
             for(int i = 0; i < q.size(); i++) {
                Object obj = q.get(i);
                Object[] customerAndAliasExpected = expectedResultSet[i];
                Object[] customerAndAlias = null;
                if(obj instanceof Object[]) {
                  TLogger.log("The element in the result list is of type Object[], continue . . .");
                    customerAndAlias = (Object[]) obj;
                    pass2 = Arrays.equals(customerAndAliasExpected, customerAndAlias);
                    if(!pass2) {
                        TLogger.log("ERROR: Expecting element value: "
                         + Arrays.asList(customerAndAliasExpected)
                         + ", actual element value: "
                         + Arrays.asList(customerAndAlias));
                        break;
                    }
                } else {
                  pass2 = false;
                  TLogger.log("ERROR: The element in the result list is not of type Object[]:" + obj);
                  break;
                }
            }
         }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_leftouterjoin_MxM: " + e);
          throw new Fault( "test_leftouterjoin_MxM failed", e);
        }

    if (!pass1 || !pass2 )
           throw new Fault( "test_leftouterjoin_MxM failed");
    }

   /*
    *   @testName: test_upperStringExpression
    *   @assertion_ids: PERSISTENCE:SPEC:369.11
    *   @test_Strategy: Test for Upper expression in the Where Clause
    *                   Select the customer with alias name = UPPER(SJC)
    *
    */


    public void test_upperStringExpression() throws Fault
    {

        List result = null;
        boolean pass = true;
 	String expectedPKs[] = null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select Object(c) FROM Customer c JOIN c.aliases a where UPPER(a.alias)='SJC' ")
            .getResultList();

            expectedPKs = new String[1];
            expectedPKs[0] ="7";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
              TLogger.log(
                    "ERROR: Did not get expected results.  Expected 1 references, got: "
                                + result.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }


        } catch (Exception e) {
          TLogger.log("Unexpected exception caught exception in test_upperStringExpression: " + e);
          e.printStackTrace();
        }

    if (!pass)
           throw new Fault( "test_upperStringExpression failed");
    }

   /*
    *   @testName: test_lowerStringExpression
    *   @assertion_ids: PERSISTENCE:SPEC:369.10
    *   @test_Strategy: Test for Lower expression in the Where Clause
    *                   Select the customer with alias name = LOWER(sjc)
    *
    */


    public void test_lowerStringExpression() throws Fault
    {

        List result = null;
        boolean pass = true;
 	String expectedPKs[] = null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select Object(c) FROM Customer c JOIN c.aliases a where LOWER(a.alias)='sjc' ")
            .getResultList();

            expectedPKs = new String[1];
            expectedPKs[0] ="7";


            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
              TLogger.log(
                    "ERROR: Did not get expected results.  Expected 1 references, got: "
                                + result.size());
                pass = false;
            } else {
                TLogger.log(
                    "Expected results received");
            }

        } catch (Exception e) {
          TLogger.log("Unexpected exception caught exception in test_lowerStringExpression: " + e);
          e.printStackTrace();
        }

    if (!pass)
           throw new Fault( "test_lowerStringExpression failed");
    }


   /*
    *   @testName: test_groupBy
    *   @assertion_ids: PERSISTENCE:SPEC:810; PERSISTENCE:SPEC:756;
    *			PERSISTENCE:SPEC:770
    *   @test_Strategy: Test for Only Group By in a simple select statement.
    *			Country is an Embeddable entity.
    *
    */

    public void test_groupBy() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedCodes[]= new String[]{"CHA", "GBR", "IRE", "JPN", "USA"};

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select c.country.code FROM Customer c GROUP BY c.country.code")
            .setMaxResults(NUMOFCOUNTRIES)
            .getResultList();
           
   	    String[] output = (String[])(result.toArray(new String[result.size()]));
            Arrays.sort(output);

            pass = Arrays.equals(expectedCodes, output);

                if ( ! pass ) {
                 TLogger.log("ERROR:  Did not get expected results.  Expected 4 Country Codes: "
                        + "CHA, GBR, JPN, USA. Received: " + result.size() );
                        Iterator it = result.iterator();
                        while (it.hasNext()) {
                 	TLogger.log(" Credit Card Type: " +it.next().toString() );
			}
                }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_groupBy: " + e);
	  e.printStackTrace();
          throw new Fault( "test_groupBy failed", e);
        }

      if (!pass)
           throw new Fault( "test_groupBy failed");
    }


   /*
    *   @testName: test_groupBy_1
    *   @assertion_ids: PERSISTENCE:SPEC:810
    *   @test_Strategy: Test for Only Group By in a simple select statement
    *			without using an Embeddable Entity in the query.
    *
    */

    public void test_groupBy_1() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedTypes[]=  new String[] {"AXP", "MCARD", "VISA"};

        try {
	    getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select cc.type FROM CreditCard cc JOIN cc.customer cust GROUP BY cc.type")
            .setMaxResults(NUMOFCREDITCARDS)
            .getResultList();

            String[] output = (String[])(result.toArray(new String[result.size()]));
	    Arrays.sort(output);

	    pass = Arrays.equals(expectedTypes, output);

            	if ( ! pass ) {
               	 TLogger.log("ERROR:  Did not get expected results.  Expected 3 Credit Card Types: "
			+ "AXP, MCARD, VISA. Received: " + result.size() );
            		Iterator it = result.iterator();
            		while (it.hasNext()) {
               	 TLogger.log(" Credit Card Type: " +it.next().toString() );
            	}

            }

        } catch (Exception e) {
            TLogger.log("Caught exception test_groupBy_1: " + e);
            e.printStackTrace();
            throw new Fault( "test_groupBy failed", e);
        }

       if (!pass)
           throw new Fault( "test_groupBy_1 failed");
    }


   /*
    *   @testName: test_innerjoin_1x1
    *   @assertion_ids: PERSISTENCE:SPEC:779; PERSISTENCE:SPEC:372;
    *			PERSISTENCE:SPEC:372.2
    *   @test_Strategy: Inner Join for 1-1 relationship.
    *            	Select all customers with spouses.
    */

     public void test_innerjoin_1x1() throws Fault
     {
        List result = null;
        boolean pass=false;
        String expectedPKs[] = null;

        try {
            getEntityTransaction().begin();

            result = getEntityManager().createQuery(
            "SELECT c from Customer c INNER JOIN c.spouse s")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[5];
            expectedPKs[0] ="7";
            expectedPKs[1] ="10";
            expectedPKs[2] ="11";
            expectedPKs[3] ="12";
            expectedPKs[4] ="13";
            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 5 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
            TLogger.log("Caught exception test_innerjoin_1x1: " + e);
	    e.printStackTrace();
            throw new Fault( "test_innerjoin_1x1 failed", e);
        }

       if (!pass)
            throw new Fault( "test_innerjoin_1x1 failed");
      }


   /*
    *   @testName: test_innerjoin_1xM
    *   @assertion_ids: PERSISTENCE:SPEC:779
    *   @test_Strategy:  Inner Join for 1-M relationship.
    *                    Retrieve credit card information for all customers.
    */

    public void test_innerjoin_1xM() throws Fault
    {
        List result = null;
        boolean pass=false;
        String expectedPKs[] = null;

       try {
            getEntityTransaction().begin();

            result = getEntityManager().createQuery(
            "SELECT DISTINCT object(c) from Customer c INNER JOIN c.creditCards cc where cc.type='VISA' ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[8];
            expectedPKs[0] ="1";
            expectedPKs[1] ="2";
            expectedPKs[2] ="3";
            expectedPKs[3] ="6";
            expectedPKs[4] ="7";
            expectedPKs[5] ="10";
            expectedPKs[6] ="14";
            expectedPKs[7] ="17";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 8 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();

        } catch (Exception e) {
            TLogger.log("Caught exception test_innerjoin_1xM: " + e);
	    e.printStackTrace();
            throw new Fault( "test_innerjoin_1xM failed", e);
	}

       if (!pass)
            throw new Fault( "test_innerjoin_1xM failed");
      }


   /*
    *   @testName: test_innerjoin_Mx1
    *   @assertion_ids: PERSISTENCE:SPEC:779; PERSISTENCE:SPEC:373
    *   @test_Strategy: Inner Join for M-1 relationship.
    *                   Retrieve customer information from Order.
    *                   customer name = Kellie A. Sanborn
    */

    public void test_innerjoin_Mx1() throws Fault
    {
        List result = null;
        boolean pass=false;
        String expectedPKs[] = null;
        String name = "Kellie A. Sanborn";
        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
           "SELECT Object(o) from Order o INNER JOIN o.customer cust where cust.name = ?1")
            .setParameter(1, "Kellie A. Sanborn")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="15";
            expectedPKs[1] ="16";

            if(!Util.checkEJBs(result, ORDERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 2 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
            TLogger.log("Caught exception test_innerjoin_Mx1: " + e);
	    e.printStackTrace();
            throw new Fault( "test_innerjoin_Mx1 failed", e);
        }

       if (!pass)
            throw new Fault( "test_innerjoin_Mx1 failed");
       }


   /* 
    *   @testName: test_innerjoin_MxM
    *   @assertion_ids: PERSISTENCE:SPEC:779
    *   @test_Strategy: Inner Join for M-M relationship.
    *                   Retrieve aliases for alias name=fish.
    */

    public void test_innerjoin_MxM() throws Fault
    {
        List result = null;
        boolean pass=false;
        String expectedPKs[] = null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT Object(c) from Customer c INNER JOIN c.aliases a where a.alias = :aName ")
            .setParameter("aName", "fish")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="1";
            expectedPKs[1] ="2";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 2 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
            TLogger.log("Caught exception test_innerjoin_MxM: " + e);
	    e.printStackTrace();
            throw new Fault( "test_innerjoin_Mx1 failed", e);
        }

       if (!pass)
            throw new Fault( "test_innerjoin_MxM failed");
       }

   /*
    *   @testName: test_fetchjoin_1x1
    *   @assertion_ids: PERSISTENCE:SPEC:781; PERSISTENCE:SPEC:774; PERSISTENCE:SPEC:776
    *   @test_Strategy: JOIN FETCH for 1-1 relationship.
    *                   Prefetch the spouses for all Customers.
    */

    public void test_fetchjoin_1x1() throws Fault
    {
        List result = null;
        boolean pass=false;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT c from Customer c JOIN FETCH c.spouse ")
           .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[5];
            expectedPKs[0] ="7";
            expectedPKs[1] ="10";
            expectedPKs[2] ="11";
            expectedPKs[3] ="12";
            expectedPKs[4] ="13";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR: Did not get expected results. Expected 5 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_fetchjoin_1x1: " + e);
	  e.printStackTrace();
          throw new Fault("test_fetchjoin_1x1 failed", e);
        }

    if (!pass)
           throw new Fault( "test_fetchjoin_1x1 failed");
    }

   /*
    *   @testName: test_fetchjoin_1xM
    *   @assertion_ids: PERSISTENCE:SPEC:782; PERSISTENCE:SPEC:374;
    *			PERSISTENCE:SPEC:777; PERSISTENCE:SPEC:783
    *   @test_Strategy:  Fetch Join for 1-M relationship.
    *                    Retrieve customers from NY or RI who have orders.
    *
    */

    public void test_fetchjoin_1xM() throws Fault
    {
        List result = null;
        boolean pass=false;
        String expectedPKs[]=null;

        try {
 	    getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT c from Customer c LEFT JOIN FETCH c.orders where c.home.state IN('NY','RI')")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="14";
            expectedPKs[1] ="17";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 2 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
	  e.printStackTrace();
          throw new Fault( "test_fetchjoin_1xM failed", e);
        }

    if (!pass)
           throw new Fault( "test_fetchjoin_1xM failed");
    }

   /* 
    *   @testName: test_fetchjoin_Mx1
    *   @assertion_ids: PERSISTENCE:SPEC:781; PERSISTENCE:SPEC:654
    *   @test_Strategy:  Retrieve customer information from Order.
    *
    */

    public void test_fetchjoin_Mx1() throws Fault
    {
        List result = null;
        boolean pass=false;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select o from Order o LEFT JOIN FETCH o.customer where o.customer.home.city='Lawrence'")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="15";
            expectedPKs[1] ="16";

            if(!Util.checkEJBs(result, ORDERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 8 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_fetchjoin_Mx1: " + e);
	  e.printStackTrace();
          throw new Fault( "test_fetchjoin_Mx1 failed", e);
        }

    if (!pass)
           throw new Fault( "test_fetchjoin_Mx1 failed");
    }


   /*
    *   @testName: test_fetchjoin_Mx1_1
    *   @assertion_ids: PERSISTENCE:SPEC:781
    *   @test_Strategy:  Retrieve customer information from Order.
    *
    */

    public void test_fetchjoin_Mx1_1() throws Fault
    {
        List result = null;
        boolean pass=false;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
	    "select Object(o) from Order o LEFT JOIN FETCH o.customer where o.customer.name LIKE '%Caruso' ")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="7";
            expectedPKs[1] ="8";

            if(!Util.checkEJBs(result, ORDERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 2 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_fetchjoin_Mx1_1: " + e);
	  e.printStackTrace();
          throw new Fault( "test_fetchjoin_Mx1_1 failed", e);
        }

    if (!pass)
           throw new Fault( "test_fetchjoin_Mx1_1 failed");
    }



   /*
    *   @testName: test_fetchjoin_MxM
    *   @assertion_ids: PERSISTENCE:SPEC:781
    *   @test_Strategy:  Left Join Fetch for M-M relationship.
    *                    Retrieve customers with orders that live in NH.
    */

    public void test_fetchjoin_MxM() throws Fault
    {
        List result = null;
        boolean pass=false;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
	    TLogger.log("DEBUG: FETCHJOIN-MXM Executing Query");
            result = getEntityManager().createQuery(
            "SELECT DISTINCT a from Alias a LEFT JOIN FETCH a.customers where a.alias LIKE 'a%' ")
            .setMaxResults(NUMOFALIASES)
            .getResultList();

            expectedPKs = new String[4];
            expectedPKs[0] ="1";
            expectedPKs[1] ="2";
            expectedPKs[2] ="5";
            expectedPKs[3] ="6";

            if(!Util.checkEJBs(result, ALIASREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 4 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();

        } catch (Exception e) {
          TLogger.log("Caught exception test_fetchjoin_MxM: " + e);
	  e.printStackTrace();
          throw new Fault( "test_fetchjoin_MxM failed");
        }

    if (!pass)
           throw new Fault( "test_fetchjoin_MxM failed");
    }


   /*
    *   @testName: test_betweenDates
    *   @assertion_ids: PERSISTENCE:SPEC:349.2; PERSISTENCE:SPEC:553;
    *			PERSISTENCE:JAVADOC:15; PERSISTENCE:JAVADOC:166;
    *			PERSISTENCE:JAVADOC:189; PERSISTENCE:SPEC:1049;
    *			PERSISTENCE:SPEC:1059; PERSISTENCE:SPEC:1060
    *   @test_Strategy: Execute a query containing using the operator BETWEEN
    *                   with datetime_expression.
    *			Verify the results were accurately returned.
    *
    */

    public void test_betweenDates() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
	    Date date1 = getShelfDate(2000, 2, 14);
    	    Date date6 = getShelfDate(2005, 2, 18);
            TLogger.log("The dates used in test_betweenDates is : " + date1 + " and " + date6);
            result = getEntityManager().createQuery(
            "SELECT DISTINCT p From Product p where p.shelfLife.soldDate BETWEEN :date1 AND :date6")
	    .setParameter("date1", date1)
            .setParameter("date6", date6)
            .setMaxResults(NUMOFPRODUCTS)
            .getResultList();

            expectedPKs = new String[4];
            expectedPKs[0] ="31";
            expectedPKs[1] ="32";
            expectedPKs[2] ="33";
            expectedPKs[3] ="37";

            if(!Util.checkEJBs(result, PRODUCTREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 3 references, got: "
                + result.size());
            } else {

             TLogger.log("Expected results received");
              pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
	  e.printStackTrace();
          TLogger.log("Caught exception test_betweenDates: " + e);
	  e.printStackTrace();
          throw new Fault( "test_betweenDates failed");
        }

    if (!pass)
           throw new Fault( "test_betweenDates failed");
    }

   /*
    *   @testName: test_notBetweenArithmetic
    *   @assertion_ids: PERSISTENCE:SPEC:349; PERSISTENCE:SPEC:331
    *   @test_Strategy: Execute a query containing using the operator BETWEEN and NOT BETWEEN.
    *                   Verify the results were accurately returned.
    */

    public void test_notBetweenArithmetic() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT o From Order o where o.totalPrice NOT BETWEEN 1000 AND 1200")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            expectedPKs = new String[15];
            expectedPKs[0] ="2";
            expectedPKs[1] ="4";
            expectedPKs[2] ="5";
            expectedPKs[3] ="6";
            expectedPKs[4] ="9";
            expectedPKs[5] ="10";
            expectedPKs[6] ="11";
            expectedPKs[7] ="12";
            expectedPKs[8] ="13";
            expectedPKs[9] ="15";
            expectedPKs[10] ="16";
            expectedPKs[11] ="17";
            expectedPKs[12] ="18";
            expectedPKs[13] ="19";
            expectedPKs[14] ="20";

            if(!Util.checkEJBs(result, ORDERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 15 references, got: "
                        + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
	  e.printStackTrace();
          TLogger.log("Caught exception test_notBetweenArithmetic: " + e);
	  e.printStackTrace();
          throw new Fault( "test_notBetweenArithmetic failed");
        }

    if (!pass)
           throw new Fault( "test_notBetweenArithmetic failed");
    }

   /*
    *   @testName: test_notBetweenDates
    *   @assertion_ids: PERSISTENCE:SPEC:349.2; PERSISTENCE:SPEC:1129;
    *			PERSISTENCE:SPEC:600
    *   @test_Strategy: Execute a query containing using the operator NOT BETWEEN.
    *                   Verify the results were accurately returned.
    */

    public void test_notBetweenDates() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;
        Date date1 = getShelfDate(2000, 2, 14);
        Date newdate = getShelfDate(2005, 2, 17);
	TLogger.log("The dates used in test_betweenDates is : " + date1 + " and " + newdate);

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate")
            .setParameter("date1", date1)
            .setParameter("newdate", newdate)
            .setMaxResults(NUMOFPRODUCTS)
            .getResultList();

            expectedPKs = new String[1];
            expectedPKs[0] ="31";
            if(!Util.checkEJBs(result, PRODUCTREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 1 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_notBetweenDates: " + e);
	  e.printStackTrace();
          throw new Fault( "test_notBetweenDates failed");
        }

    if (!pass)
           throw new Fault( "test_notBetweenDates failed");
    }

   /*
    *   @testName: test_ANDconditionTT
    *   @assertion_ids: PERSISTENCE:SPEC:424; PERSISTENCE:SPEC:768
    *   @test_Strategy: Both the conditions in the WHERE Clause are True
    *  			and hence the result is also TRUE
    *                   Verify the results were accurately returned.
    */

    public void test_ANDconditionTT() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select Object(o) FROM Order AS o WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            expectedPKs = new String[1];
            expectedPKs[0] ="6";

            if(!Util.checkEJBs(result, ORDERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 1 reference, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
	  e.printStackTrace();
          TLogger.log("Caught exception test_ANDconditionTT: " + e);
          throw new Fault( "test_ANDconditionTT failed");
        }

    if (!pass)
           throw new Fault( "test_ANDconditionTT failed");
    }

   /*
    *   @testName: test_ANDconditionTF
    *   @assertion_ids: PERSISTENCE:SPEC:424
    *   @test_Strategy: First condition is True and Second is False
    *  			and hence the result is also False
    */

    public void test_ANDconditionTF() throws Fault
    {
        boolean pass=false;
        List result = null;

	try {
        getEntityTransaction().begin();
        result = getEntityManager().createQuery(
        "select Object(o) FROM Order AS o WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            if ( result.size() == 0) {
                TLogger.log("Expected results received");
                pass=true;
            } else {
                TLogger.log("ERROR:  Did not get expected results.  Expected 0 references, got: "
                + result.size());
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_ANDconditionTF: " + e);
	  e.printStackTrace();
          throw new Fault( "test_ANDconditionTF failed");
        }

    	if (!pass)
       		throw new Fault( "test_ANDconditionTF failed");
     }

   /*
    *   @testName: test_ANDconditionFT
    *   @assertion_ids: PERSISTENCE:SPEC:424
    *   @test_Strategy: First condition is FALSE and Second is TRUE
    *  			and hence the result is also False
    */

    public void test_ANDconditionFT() throws Fault
    {
        boolean pass=false;
        List result = null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select Object(o) FROM Order AS o WHERE o.customer.id = '1001' AND o.totalPrice < 1000 ")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            if ( result.size() == 0) {
                TLogger.log("Expected results received");
                pass=true;
            } else {
                TLogger.log("ERROR:  Did not get expected results.  Expected 0 references, got: "
                + result.size());
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_ANDconditionFT: " + e);
	  e.printStackTrace();
          throw new Fault( "test_ANDconditionFT failed");
        }

    	if (!pass)
       		throw new Fault( "test_ANDconditionFT failed");
     }

   /*
    *   @testName: test_ANDconditionFF
    *   @assertion_ids: PERSISTENCE:SPEC:424
    *   @test_Strategy: First condition is FALSE and Second is FALSE
    *  			and hence the result is also False
    */

    public void test_ANDconditionFF() throws Fault
    {
        boolean pass=false;
        List result = null;

        try {
            getEntityTransaction().begin();
  	    result = getEntityManager().createQuery(
            "select Object(o) FROM Order AS o WHERE o.customer.id = '1001' AND o.totalPrice > 10000")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            if ( result.size() == 0) {
                TLogger.log("Expected results received");
                pass=true;
            } else {
                TLogger.log("ERROR:  Did not get expected results.  Expected 0 references, got: "
                + result.size());
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_ANDconditionFF: " + e);
	  e.printStackTrace();
          throw new Fault( "test_ANDconditionFF failed");
        }

    	if (!pass)
       		throw new Fault( "test_ANDconditionFF failed");
     }


   /*
    *   @testName: test_ORconditionTT
    *   @assertion_ids: PERSISTENCE:SPEC:425
    *   @test_Strategy: First condition is TRUE OR Second is TRUE
    *  			and hence the result is also TRUE
    */

    public void test_ORconditionTT() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select DISTINCT o FROM Order AS o WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            expectedPKs = new String[3];
            expectedPKs[0] ="6";
            expectedPKs[1] ="11";
            expectedPKs[2] ="16";

            if(!Util.checkEJBs(result, ORDERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 3 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_ORconditionTT: " + e);
	  e.printStackTrace();
          throw new Fault( "test_ORconditionTT failed");
        }

    	if (!pass)
       		throw new Fault( "test_ORconditionTT failed");
     }

   /*
    *   @testName: test_ORconditionTF
    *   @assertion_ids: PERSISTENCE:SPEC:425
    *   @test_Strategy: First condition is TRUE OR Second is FALSE
    *  			and hence the result is also TRUE
    */

    public void test_ORconditionTF() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select Object(o) FROM Order AS o WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            expectedPKs = new String[1];
            expectedPKs[0] ="6";

            if(!Util.checkEJBs(result, ORDERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 1 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_ORconditionTF: " + e);
	  e.printStackTrace();
          throw new Fault( "test_ORconditionTF failed");
        }

    	if (!pass)
       		throw new Fault( "test_ORconditionTF failed");
     }


   /*
    *   @testName: test_ORconditionFT
    *   @assertion_ids: PERSISTENCE:SPEC:425
    *   @test_Strategy: First condition is FALSE OR Second is TRUE
    *  			and hence the result is also TRUE
    */

    public void test_ORconditionFT() throws Fault
    {
      boolean pass=false;
      List result = null;
      String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
	    "select Distinct Object(o) FROM Order AS o WHERE o.customer.id = '1001' OR o.totalPrice < 1000 ")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            expectedPKs = new String[7];
            expectedPKs[0] ="9";
            expectedPKs[1] ="10";
            expectedPKs[2] ="12";
            expectedPKs[3] ="13";
            expectedPKs[4] ="15";
            expectedPKs[5] ="19";
            expectedPKs[6] ="20";

            if(!Util.checkEJBs(result, ORDERREF, expectedPKs)) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 7 references, got: "
                + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_ORconditionFT: " + e);
	  e.printStackTrace();
          throw new Fault( "test_ORconditionFT failed");
        }

     if (!pass)
       	    throw new Fault( "test_ORconditionFT failed");
     }


   /*
    *   @testName: test_ORconditionFF
    *   @assertion_ids: PERSISTENCE:SPEC:425
    *   @test_Strategy: First condition is FALSE OR Second is FALSE
    *  			and hence the result is also FALSE
    */

    public void test_ORconditionFF() throws Fault
    {
        boolean pass=false;
        List result = null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select Object(o) FROM Order AS o WHERE o.customer.id = '1001' OR o.totalPrice > 10000")
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            if ( result.size() == 0) {
                TLogger.log("Expected results received");
                pass=true;
            } else {
                TLogger.log("ERROR:  Did not get expected results.  Expected 0 references, got: "
                + result.size());
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_ORconditionFF: " + e);
	  e.printStackTrace();
          throw new Fault( "test_ORconditionFF failed");
        }


     if (!pass)
       	    throw new Fault( "test_ORconditionFF failed");
     }

   /*
    *   @testName: test_groupByWhereClause
    *   @assertion_ids: PERSISTENCE:SPEC:808
    *   @test_Strategy: Test for Group By within a WHERE clause 
    */

    public void test_groupByWhereClause() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;
        String[] expectedCusts = new String[] {"Jonathan K. Smith", "Kellie A. Sanborn", "Robert E. Bissett"};

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select c.name FROM Customer c JOIN c.orders o WHERE o.totalPrice BETWEEN 90 AND 160 GROUP BY c.name")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            String[] output = (String[])(result.toArray(new String[result.size()]));

	    Arrays.sort(output);
            pass = Arrays.equals(expectedCusts, output);

	    if ( ! pass ) {
                 TLogger.log("ERROR:  Did not get expected results.  Expected 3 Customers : "
                        + "Jonathan K. Smith, Kellie A. Sanborn and Robert E. Bissett. Received: "
			+ result.size() );
                        Iterator it = result.iterator();
                        while (it.hasNext()) {
                 		TLogger.log(" Customer: " +it.next().toString() );
			}
             }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_groupByWhereClause: " + e);
	  e.printStackTrace();
          throw new Fault( "test_groupByWhereClause failed");
        }

     if (!pass)
       	    throw new Fault( "test_groupByWhereClause failed");
     }


   /*
    *   @testName: test_groupByHaving
    *   @assertion_ids: PERSISTENCE:SPEC:808; PERSISTENCE:SPEC:353;
    *			PERSISTENCE:SPEC:757; PERSISTENCE:SPEC:391
    *   @test_Strategy: Test for Group By and Having in a select statement
    * 			Select the count of customers in each country where Country
    *			is China, England
    */

    public void test_groupByHaving() throws Fault
    {
      boolean pass=false;
      List result = null;
      Long expectedGBR = new Long(2);
      Long expectedCHA = new Long(4);

      try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "select Count(c) FROM Customer c GROUP BY c.country.code "
		+ "HAVING c.country.code IN ('GBR', 'CHA') ")
	    .setMaxResults(NUMOFCOUNTRIES)
	    .getResultList();

	Iterator i = result.iterator();
        int numOfExpected = 0;
        while (i.hasNext()) {
           TLogger.log("Check result received . . . " );
           Long l = (Long)i.next();
           if ( (l.equals(expectedGBR)) || (l.equals(expectedCHA)) ) {
             	numOfExpected++;
           }
        }

	if (numOfExpected != 2 ) {
	   pass = false;
           TLogger.log("ERROR:  Did not get expected results.  Expected 2 Values returned : "
           + "2 with Country Code GBR and 4 with Country Code CHA. "
	   + "Received: " + result.size() );
           Iterator it = result.iterator();
           	while (it.hasNext()) {
           	TLogger.log("Count of Codes Returned: " +it.next() );
           	}
	 } else {
	   TLogger.log("Expected results received.");
	   pass = true;
	 }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_groupByHaving: " + e);
          e.printStackTrace();
          throw new Fault( "test_groupByHaving failed");

       }

     if (!pass)
       	    throw new Fault( "test_groupByHaving failed");
     }


   /*
    *   @testName: test_havingClause
    *   @assertion_ids: PERSISTENCE:SPEC:808; PERSISTENCE:SPEC:1069.0
    *   @test_Strategy: Select the Country name from Customer Country
    */

    public void test_havingClause() throws Fault
    {
	boolean pass1 = true;
	boolean pass2 = false;
        List result = null;
	List resultsList = null;
        String[] expectedCountries = new String[]{"China", "England", "Ireland",
						 "Japan", "United States"};

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
    	    "select c.country.country FROM Customer c GROUP BY c.country.country")
            .setMaxResults(NUMOFCOUNTRIES)
            .getResultList();

          if ( result.size()  != 5 ) {
                TLogger.log("ERROR:  Did not get expected results.  Expected: 5, "
                        + "got: " + result.size() );
                pass1 = false;
         } else if (pass1) {
		resultsList = new ArrayList();
		for(int i = 0; i < result.size(); i++) {
  			Object obj = result.get(i); 
		        String sObj = (String)obj;
			resultsList.add(sObj);
		}
	 }
           TLogger.log("Convert to Array . . . ");
           String[] result1 = (String[])(resultsList.toArray(new String[result.size()]));
	   Arrays.sort(result1);

           TLogger.log("Compare Arrays . . . ");
           pass2 = Arrays.equals(expectedCountries, result1);

	   if ( !pass2 ) {
           	TLogger.log("ERROR:  Did not get expected results.  Expected 5 Countries : "
		+ "China, England, Japan, Ireland, United States"
		+ "Received: " + result.size() );
           	Iterator it = result.iterator();
                	while (it.hasNext()) {
                	TLogger.log("Country Returned: " +it.next() );
                	}
           } else {
             TLogger.log("Expected results received.");
           }
          getEntityTransaction().commit();
        } catch (Exception e) {
	  pass2 = false;
          TLogger.log("Caught exception test_havingClause: " + e);
          e.printStackTrace();
          throw new Fault( "test_havingClause failed");
        }

     if (!pass2)
       	    throw new Fault( "test_havingClause failed");
     }


   /*
    *   @testName: test_substringHavingClause
    *   @assertion_ids: PERSISTENCE:SPEC:807
    *   @test_Strategy:Test for Functional Expression: substring in Having Clause
    *  			Select all customers with alias = fish
    */

    public void test_substringHavingClause() throws Fault
    {
        boolean pass=false;
	Query q = null;
        Object result = null;
        Long expectedCount = new Long(2);

        try {
            getEntityTransaction().begin();
	    TLogger.log("substringHavingClause: Executing Query");
            q = getEntityManager().createQuery(
            "select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias "
		+ "HAVING a.alias = SUBSTRING(:string1, :int1, :int2)")
            .setParameter("string1", "fish")
            .setParameter("int1", new Integer(1))
            .setParameter("int2", new Integer(4));

            result = (Long)q.getSingleResult();

            TLogger.log("Check results received .  .  .");
                if (expectedCount.equals(result)) {
                       TLogger.log("Expected results received");
                       pass=true;
                } else {
                  TLogger.log("ERROR: Did not get expected results. Expected Count of 2, got: "
		  + result);
                }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_substringHavingClause: " + e);
          e.printStackTrace();
          throw new Fault( "test_substringHavingClause failed");
        }

     if (!pass)
       	    throw new Fault( " test_substringHavingClause failed");
     }


   /*
    *   @testName: test_concatHavingClause
    *   @assertion_ids: PERSISTENCE:SPEC:807; PERSISTENCE:SPEC:803; PERSISTENCE:SPEC:804;
    *			PERSISTENCE:SPEC:805; PERSISTENCE:SPEC:806; PERSISTENCE:SPEC:734
    *   @test_Strategy:Test for Functional Expression: concat in Having Clause
    *  			Find customer Margaret Mills by firstname-lastname concatenation.
    */

   public void test_concatHavingClause() throws Fault
    {
        boolean pass=false;
        Query q = null;
        String result = null;
        String expectedCustomer = "Margaret Mills";

        try {
            getEntityTransaction().begin();
            q = getEntityManager().createQuery(
            "select c.name FROM Customer c Group By c.name HAVING c.name = concat(:fmname, :lname) ")
            .setParameter("fmname", "Margaret ")
            .setParameter("lname", "Mills");
            result = (String)q.getSingleResult();

	   if (result.equals(expectedCustomer)) {
                TLogger.log("Expected results received");
                pass=true;
            } else {
	        pass = false;
                TLogger.log("test_concatHavingClause:  Did not get expected results. " 
		+ "Expected: " + expectedCustomer + ", got: " + result);
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_concatHavingClause: " + e);
          e.printStackTrace();
          throw new Fault( "test_concatHavingClause failed");
        }

     if (!pass)
       	    throw new Fault("test_concatHavingClause failed");
     }

   /*
    *   @testName: test_lowerHavingClause
    *   @assertion_ids: PERSISTENCE:SPEC:807; PERSISTENCE:SPEC:369.10
    *   @test_Strategy:Test for Functional Expression: lower in Having Clause
    *  			Select all customers in country with code GBR
    */

    public void test_lowerHavingClause() throws Fault
    {
        boolean pass=false;
        List result = null;
        Long expectedCount = new Long(2);

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
	    "select count(c.country.code) FROM Customer c GROUP BY c.country.code "
		+ " HAVING LOWER(c.country.code) = 'gbr' ")
             .getResultList();

	    Iterator it = result.iterator();
            while (it.hasNext()) {
            Long l = (Long) it.next();
                if (l.equals(expectedCount)) {
                       pass=true;
                       TLogger.log("Expected results received");
                } else {
                  TLogger.log("ERROR: Did not get expected results. Expected 2 references, got: "
		  + result.size());
                }
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_lowerHavingClause: " + e);
          e.printStackTrace();
          throw new Fault( "test_lowerHavingClause failed");
        }

     if (!pass)
       	    throw new Fault( " test_lowerHavingClause failed");
     }

   /*
    *   @testName: test_upperHavingClause
    *   @assertion_ids: PERSISTENCE:SPEC:807; PERSISTENCE:SPEC:369.11
    *   @test_Strategy:Test for Functional Expression: upper in Having Clause
    *  			Select all customers in country ENGLAND
    */


    public void test_upperHavingClause() throws Fault
    {
        boolean pass=false;
        List result = null;
        Long expectedCount = new Long(2);

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
	    "select count(c.country.country) FROM Customer c GROUP BY c.country.country "
		+ "HAVING UPPER(c.country.country) = 'ENGLAND' ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            Iterator it = result.iterator();
            while (it.hasNext()) {
            Long l = (Long) it.next();
                if (l.equals(expectedCount)) {
                       pass=true;
                       TLogger.log("Expected results received");
                } else {
                  TLogger.log("ERROR: Did not get expected results. Expected 2 references, got: "
                  + result.size());
                }
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_upperHavingClause: " + e);
          e.printStackTrace();
          throw new Fault( "test_upperHavingClause failed");
        }

     if (!pass)
       	    throw new Fault("test_upperHavingClause failed");
     }

   /*
    *   @testName: test_lengthHavingClause
    *   @assertion_ids: PERSISTENCE:SPEC:807; PERSISTENCE:SPEC:369.4
    *   @test_Strategy:Test for Functional Expression: length in Having Clause
    *  		Select all customer names having the length of the city of the home address = 10
    */

    public void test_lengthHavingClause() throws Fault
    {
        boolean pass=false;
        List result = null;
	String[] expectedCities = new String[]{"Chelmsford", "Roslindale"};

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
 	    "select a.city  FROM Customer c JOIN c.home a GROUP BY a.city HAVING LENGTH(a.city) = 10 ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            String[] output = (String[])(result.toArray(new String[result.size()]));
            Arrays.sort(output);

            pass = Arrays.equals(expectedCities, output);

            if ( ! pass ) {
            TLogger.log("ERROR:  Did not get expected results.  Expected 2 Cities, got: "
                + result.size());
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_lengthHavingClause: " + e);
          e.printStackTrace();
          throw new Fault( "test_lengthHavingClause failed");
        }

     if (!pass)
       	    throw new Fault("test_lengthHavingClause failed");
     }

   /*
    *   @testName: test_locateHavingClause
    *   @assertion_ids:  PERSISTENCE:SPEC:807; PERSISTENCE:SPEC:369.3
    *   @test_Strategy: Test for LOCATE expression in the Having Clause
    *  		Select customer names if there the string "Frechette" is located in the customer name.
    */

   public void test_locateHavingClause() throws Fault
   {
        boolean pass=false;
        List result = null;
        String[] expectedCusts= new String[] {"Alan E. Frechette", "Arthur D. Frechette"};

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
	    "select c.name FROM Customer c GROUP BY c.name HAVING LOCATE('Frechette', c.name) > 0 ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            String[] output = (String[])(result.toArray(new String[result.size()]));
            Arrays.sort(output);

      	    pass = Arrays.equals(expectedCusts, output);

            if ( ! pass ) {
            TLogger.log("ERROR:  Did not get expected results.  Expected 2 Customers, got: "
                + result.size());
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_locateHavingClause: " + e);
          e.printStackTrace();
          throw new Fault( "test_locateHavingClause failed");
        }

   	if (!pass)
            throw new Fault( " test_locateHavingClause failed");
     }


   /*
    *   @testName: test_trimHavingClause_01
    *   @assertion_ids: PERSISTENCE:SPEC:369.9
    *   @test_Strategy: Test for TRIM BOTH characters (blank) in the Having Clause
    */

    public void test_trimHavingClause_01() throws Fault
    {
        boolean pass=false;
        Object result = null;
	Query q = null;
	String expected = " David R. Vincent ";

        try {
            getEntityTransaction().begin();
            q = getEntityManager().createQuery(
            "select c.name  FROM Customer c Group by c.name HAVING "
		+ "trim(BOTH from c.name) = 'David R. Vincent'");

            result = (String) q.getSingleResult();

            if (expected.equals(result) ) {
                pass = true;
                TLogger.log("Expected results received");
            } else {
                TLogger.log("ERROR test_trimHavingClause_01: Did not get expected results."
		+ "Expected David R. Vincent, got: " + result);
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_trimHavingClause_01: " + e);
          e.printStackTrace();
          throw new Fault( "test_trimHavingClause_01 failed");
        }

   	if (!pass)
            throw new Fault( " test_trimHavingClause_01 failed");
     }


   /*
    *   @testName: test_trimHavingClause_02
    *   @assertion_ids: PERSISTENCE:SPEC:369.9
    *   @test_Strategy: Test for TRIM LEADING characters (blank) in the Having Clause
    */

    public void test_trimHavingClause_02() throws Fault
    {
        boolean pass=false;
	Query q = null;
        Object result = null;
	String expected = " David R. Vincent ";

        try {
            getEntityTransaction().begin();
            q = getEntityManager().createQuery(
            "select c.name FROM  Customer c Group By c.name Having "
		+ "trim(LEADING from c.name) = 'David R. Vincent '  ");

            result = (String) q.getSingleResult();

            if (expected.equals(result) ) {
                pass = true;
                TLogger.log( "Expected results received");
            } else {
                TLogger.log("ERROR test_trimHavingClause_02: Did not get expected results."
		+ "Expected David R. Vincent, got: " + result);
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_trimHavingClause_02: " + e);
          e.printStackTrace();
          throw new Fault( "test_trimHavingClause_02 failed");
        }

   	if (!pass)
            throw new Fault("test_trimHavingClause_02 failed");
	}


   /*
    *   @testName: test_trimHavingClause_03
    *   @assertion_ids: PERSISTENCE:SPEC:369.9
    *   @test_Strategy: Test for TRIM TRAILING characters (blank) in the Having Clause
    */

    public void test_trimHavingClause_03() throws Fault
    {

        boolean pass=false;
        Object result = null;
	Query q = null;
	String expected = " David R. Vincent ";

        try {
            getEntityTransaction().begin();
            q = getEntityManager().createQuery(
            "select c.name  FROM Customer c Group By c.name HAVING "
		+ "trim(TRAILING from c.name) = ' David R. Vincent'");

            result = (String) q.getSingleResult();

            if (expected.equals(result) ) {
                pass = true;
                TLogger.log( "Expected results received");
            } else {
                TLogger.log("ERROR test_trimHavingClause_03: Did not get expected results."
		+ "Expected David R. Vincent, got: " + result);
            }

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_trimHavingClause_03: " + e);
          e.printStackTrace();
          throw new Fault( "test_trimHavingClause_03 failed");
        }

   	if (!pass)
            throw new Fault( "test_trimHavingClause_03 failed");
      }

   /*
    *   @testName: test_ABSHavingClause
    *   @assertion_ids: PERSISTENCE:SPEC:369.5
    *   @test_Strategy: Test for ABS expression in the Having Clause
    */

    public void test_ABSHavingClause() throws Fault
    {
        boolean pass=false;
	Query q = null;
        Object result = null;
	Double expectedPrice = new Double(10191.90);

        try {
            getEntityTransaction().begin();
            q = getEntityManager().createQuery(
	    "select sum(o.totalPrice) FROM Order o GROUP BY o.totalPrice HAVING ABS(o.totalPrice) = :doubleValue ")
            .setParameter("doubleValue", new Double(5095.95));

           result = (Double) q.getSingleResult();

           if (expectedPrice.equals(result) ) {
                pass = true;
                TLogger.log( "Expected results received");
            } else {
                TLogger.log("ERROR: test_ABSHavingClause:  Did not get expected results."
		+ "Expected 10190, got: " + (Long)result );
            }
        } catch (Exception e) {
          TLogger.log("Caught exception test_ABSHavingClause: " + e);
          e.printStackTrace();
          throw new Fault( "test_ABSHavingClause failed");
        }

   	if (!pass)
            throw new Fault( "test_ABSHavingClause failed");
    }


   /*
    *   @testName: test_SQRTWhereClause
    *   @assertion_ids: PERSISTENCE:SPEC:369.6
    *   @test_Strategy: Test for SQRT expression in the WHERE Clause
    */

    public void test_SQRTWhereClause() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
	    TLogger.log("DEBUG SQRT: Executing Query");
            result = getEntityManager().createQuery(
            "select object(o) FROM Order o Where SQRT(o.totalPrice) > :doubleValue ")
            .setParameter("doubleValue", new Double(70))
            .setMaxResults(NUMOFORDERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="11";
            expectedPKs[1] ="16";

             if(!Util.checkEJBs(result, ORDERREF, expectedPKs)) {
                TLogger.log("ERROR test_SQRTWhereClause:  Did not get expected results."
			+ "  Expected 2 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_SQRTWhereClause: " + e);
          e.printStackTrace();
          throw new Fault( "test_SQRTWhereClause failed");
        }

   	if (!pass)
            throw new Fault( " test_SQRTWhereClause failed");

    }


   /*
    *   @testName: test_subquery_exists_01
    *   @assertion_ids: PERSISTENCE:SPEC:791;PERSISTENCE:SPEC:792
    *   @test_Strategy: Test NOT EXISTS in the Where Clause for a correlated query.
    *  			Select the customers without orders.
    */

    public void test_subquery_exists_01() throws Fault
    {
       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT c FROM Customer c WHERE NOT EXISTS (SELECT o1 FROM c.orders o1) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="19";
            expectedPKs[1] ="20";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_exists_01:  Did not get expected results.  "
			+ "Expected 2 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_exists_01: " + e);
          e.printStackTrace();
          throw new Fault( "test_subquery_exists_01 failed");
        }

   	if (!pass)
            throw new Fault( " test_subquery_exists_01 failed");
    }


   /*
    *   @testName: test_subquery_exists_02
    *   @assertion_ids: PERSISTENCE:SPEC:791;PERSISTENCE:SPEC:792
    *   @test_Strategy: Test for EXISTS in the Where Clause for a correlated query.
    *			Select the customers with orders where total order > 1500.
    */

   public void test_subquery_exists_02() throws Fault
    {

       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT c FROM Customer c WHERE EXISTS (SELECT o FROM c.orders o where o.totalPrice > 1500 ) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[4];
            expectedPKs[0] ="5";
            expectedPKs[1] ="10";
            expectedPKs[2] ="14";
            expectedPKs[3] ="15";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_exists_02:  Did not get expected results. "
		+ " Expected 4 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_exists_02: " + e);
          e.printStackTrace();
          throw new Fault( "test_subquery_exists_02 failed");
        }

   	if (!pass)
            throw new Fault( " test_subquery_exists_02 failed");
    }


   /*
    *   @testName: test_subquery_like
    *   @assertion_ids: PERSISTENCE:SPEC:791;PERSISTENCE:SPEC:792;
    *			PERSISTENCE:SPEC:800;PERSISTENCE:SPEC:801;
    *			PERSISTENCE:SPEC:802
    *   @test_Strategy:  Use LIKE expression in a sub query.
    * 			 Select the customers with name like Caruso.
    *			 The name Caruso is derived in the subquery.
    */

    public void test_subquery_like() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "Select Object(o) from Order o WHERE EXISTS " 
		+ "(Select c From o.customer c WHERE c.name LIKE '%Caruso') " )
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="7";
            expectedPKs[1] ="8";

            if(!Util.checkEJBs(result, ORDERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_like:  Did not get expected "
			+ " results.  Expected 2 references, got: "
                	+ result.size());
           } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_like: " + e);
          e.printStackTrace();
          throw new Fault( "test_subquery_like failed");
        }

   	if (!pass)
            throw new Fault( " test_subquery_like failed");
    }

   /*
    *   @testName: test_subquery_in
    *   @assertion_ids: PERSISTENCE:SPEC:800;PERSISTENCE:SPEC:801;
    *			PERSISTENCE:SPEC:802; PERSISTENCE:SPEC:352.2
    *   @test_Strategy: Use IN expression in a sub query.
    */

   public void test_subquery_in() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "Select DISTINCT c from Customer c WHERE c.home.state IN"
		+ "(Select distinct w.state from c.work w where w.state = :state ) ")
	    .setParameter("state", "MA")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[10];
            expectedPKs[0] ="1";
            expectedPKs[1] ="2";
            expectedPKs[2] ="3";
            expectedPKs[3] ="4";
            expectedPKs[4] ="7";
            expectedPKs[5] ="8";
            expectedPKs[6] ="9";
            expectedPKs[7] ="11";
            expectedPKs[8] ="13";
            expectedPKs[9] ="15";
           if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR: test_subquery_in:  Did not get expected results. "
			+ " Expected 11 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) { 
          TLogger.log("Caught exception test_subquery_in: " + e);
          e.printStackTrace();
          throw new Fault( "test_subquery_in failed");
        }

   	if (!pass)
            throw new Fault("test_subquery_in failed");
    }

   /*
    *   @testName: test_subquery_between
    *   @assertion_ids: PERSISTENCE:SPEC:800;PERSISTENCE:SPEC:801;
    *			PERSISTENCE:SPEC:802
    *   @test_Strategy: Use BETWEEN expression in a sub query.
    *			Select the customers whose orders total price is between 1000 and 2000.
    */

    public void test_subquery_between() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            TLogger.log("Execute query for test_subquery_between");
            result = getEntityManager().createQuery(
            "SELECT DISTINCT c FROM Customer c WHERE EXISTS (SELECT o FROM c.orders o where o.totalPrice BETWEEN 1000 AND 1200)")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[5];
            expectedPKs[0] ="1";
            expectedPKs[1] ="3";
            expectedPKs[2] ="7";
            expectedPKs[3] ="8";
            expectedPKs[4] ="13";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_between:  Did not get expected "
		+ " results.  Expected 5 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_between: " + e);
          e.printStackTrace();
          throw new Fault( "test_subquery_between failed");
        }

   	if (!pass)
            throw new Fault( " test_subquery_between failed");

    }

   /*
    *   @testName: test_subquery_join
    *   @assertion_ids: PERSISTENCE:SPEC:800;PERSISTENCE:SPEC:801;
    *			PERSISTENCE:SPEC:802; PERSISTENCE:SPEC:765
    *   @test_Strategy: Use JOIN in a sub query.
    *  			Select the customers whose orders have line items of quantity > 2.
    */

    public void test_subquery_join() throws Fault
    {

        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT c FROM Customer c JOIN c.orders o WHERE EXISTS "
		+ "(SELECT o FROM o.lineItems l where l.quantity > 3 ) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[5];
            expectedPKs[0] ="6";
            expectedPKs[1] ="9";
            expectedPKs[2] ="11";
            expectedPKs[3] ="13";
            expectedPKs[4] ="16";

             if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_join:  Did not get expected results."
		+ "  Expected 5 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_join: " + e);
          e.printStackTrace();
          throw new Fault( "test_subquery_join failed");
        }

   	if (!pass)
            throw new Fault( " test_subquery_join failed");
    }

   /*
    *   @testName: test_subquery_ALL_GT
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:797;
    *			PERSISTENCE:SPEC:766; PERSISTENCE:SPEC:793;
    *			PERSISTENCE:SPEC:799
    *   @test_Strategy: Test for ALL in a subquery with the relational operator ">".
    * 	Select all customers where total price of orders is greater than ALL the
    *   values in the result set.
    */

    public void test_subquery_ALL_GT() throws Fault
    {
       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT c FROM Customer c, IN(c.orders) co WHERE co.totalPrice > "
		+ "ALL (Select o.totalPrice FROM Order o, in(o.lineItems) l WHERE l.quantity > 3) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[4];
            expectedPKs[0] ="5";
            expectedPKs[1] ="10";
            expectedPKs[2] ="14";
            expectedPKs[3] ="15";

             if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_ALL_GT:  Did not get expected results. "
		+ " Expected 4 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
	  pass = false;
          TLogger.log("Caught exception test_subquery_ALL_GT: " + e);
          e.printStackTrace();
          throw new Fault( "test_subquery_ALL_GT failed");
        }

   	if (!pass)
            throw new Fault( " test_subquery_ALL_GT failed");
    }

   /*
    *   @testName: test_subquery_ALL_LT
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:797
    *   @test_Strategy: Test for ALL in a subquery with the relational operator "<".
    * 	Select all customers where total price of orders is less than ALL the
    *	values in the result set.
    */

   public void test_subquery_ALL_LT() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT distinct object(C) FROM Customer C, IN(C.orders) co WHERE co.totalPrice < "
		+ "ALL (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity > 3) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[1];
            expectedPKs[0] ="12";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_ALL_LT:  Did not get expected results."
		+ "  Expected 1 reference, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_ALL_LT: " + e);
          e.printStackTrace();
          throw new Fault("test_subquery_ALL_LT failed");
        }

   	if (!pass)
            throw new Fault("test_subquery_ALL_LT failed");
    }

   /*
    *   @testName: test_subquery_ALL_EQ
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:797
    *   @test_Strategy: Test for ALL in a subquery with the relational operator "=".
    * 	Select all customers where total price of orders is = ALL the values in the result set.
    * 	The result set contains the min of total price of orders.
    */

    public void test_subquery_ALL_EQ() throws Fault
    {
        boolean pass=false;
        List result = null;
        String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT c FROM Customer c, IN(c.orders) co WHERE co.totalPrice = ALL"
		+ " (Select MIN(o.totalPrice) FROM Order o) ")
            .setMaxResults(NUMOFCUSTOMERS)
	    .getResultList();

	    expectedPKs = new String[1];
            expectedPKs[0] ="12";

             if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_ALL_EQ:  Did not get expected results. "
                + " Expected 1 reference, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }


          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_ALL_EQ: " + e);
          e.printStackTrace();
          throw new Fault( "test_subquery_ALL_EQ failed");
   	}

   	if (!pass)
            throw new Fault("test_subquery_ALL_EQ failed");

    }

   /*
    *   @testName: test_subquery_ALL_LTEQ
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:797
    *   @test_Strategy: Test for ALL in a subquery with the relational operator "<=".
    * 	Select all customers where total price of orders is <= ALL the values in the result set.
    * 	The result set contains the total price of orders where count of lineItems > 3.
    */


    public void test_subquery_ALL_LTEQ() throws Fault
    {
       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT c FROM Customer c, IN(c.orders) co WHERE co.totalPrice <= ALL"
		+ " (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity > 3) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="9";
            expectedPKs[1] ="12";

             if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_ALL_LTEQ:  Did not get expected results.  "
		+ "Expected 2 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_ALL_LTEQ: " + e);
          e.printStackTrace();
          throw new Fault( "test_subquery_ALL_LTEQ failed");
        }

   	if (!pass)
            throw new Fault("test_subquery_ALL_LTEQ failed");
    }

   /*
    *   @testName: test_subquery_ALL_GTEQ
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:797
    *   @test_Strategy: Test for ALL in a subquery with the relational operator ">=".
    * 	Select all customers where total price of orders is >= ALL the values in the result set.
    */

    public void test_subquery_ALL_GTEQ() throws Fault
    {
       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

       try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT object(c) FROM Customer C, IN(c.orders) co WHERE co.totalPrice >= ALL"
		+ " (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity >= 3) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="10";
            expectedPKs[1] ="14";

             if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_ALL_GTEQ:  Did not get expected results. "
		+ " Expected 2 references, got: " + result.size());
            } else {
              TLogger.log("Expected results received");
              pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
	  pass = false;
          TLogger.log("Caught exception test_subquery_ALL_GTEQ: " + e);
          e.printStackTrace();
          throw new Fault( "test_subquery_ALL_GTEQ failed");
        }

   	if (!pass)
            throw new Fault( "test_subquery_ALL_GTEQ failed");
    }

   /*
    *   @testName: test_subquery_ALL_NOTEQ
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:797;
    * 			PERSISTENCE:SPEC:798
    *   @test_Strategy: Test for ALL in a subquery with the relational operator "<>".
    * 	Select all customers where total price of orders is <> ALL the values in the result set.
    */

    public void test_subquery_ALL_NOTEQ() throws Fault
    {
       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT Distinct object(c) FROM Customer c, IN(c.orders) co WHERE co.totalPrice <> "
		+ "ALL (Select MIN(o.totalPrice) FROM Order o) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[17];
            expectedPKs[0] ="1";
            expectedPKs[1] ="2";
            expectedPKs[2] ="3";
            expectedPKs[3] ="4";
            expectedPKs[4] ="5";
            expectedPKs[5] ="6";
            expectedPKs[6] ="7";
            expectedPKs[7] ="8";
            expectedPKs[8] ="9";
            expectedPKs[9] ="10";
            expectedPKs[10] ="11";
            expectedPKs[11] ="13";
            expectedPKs[12] ="14";
            expectedPKs[13] ="15";
            expectedPKs[14] ="16";
            expectedPKs[15] ="17";
            expectedPKs[16] ="18";

             if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_ALL_NOTEQ:  Did not get expected results."
		+ "  Expected 17 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_ALL_NOTEQ: " + e);
          e.printStackTrace();
          throw new Fault("test_subquery_ALL_NOTEQ failed");
        }

   	if (!pass)
            throw new Fault("test_subquery_ALL_NOTEQ failed");
    }

   /*
    *   @testName: test_subquery_ANY_GT
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:797;
    * 			PERSISTENCE:SPEC:798
    *   @test_Strategy:  Test for ANY in a subquery with the relational operator ">".
    *	Select all customers  where total price of orders is greater than ANY of the values 
    * 	in the result.  The result set contains the total price of orders where count of
    *	lineItems = 3.
    */

   public void test_subquery_ANY_GT() throws Fault
    {
       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT c FROM Customer c, IN(c.orders) co WHERE co.totalPrice > ANY"
		+ " (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity = 3) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[16];
            expectedPKs[0] ="1";
            expectedPKs[1] ="2";
            expectedPKs[2] ="3";
            expectedPKs[3] ="4";
            expectedPKs[4] ="5";
            expectedPKs[5] ="6";
            expectedPKs[6] ="7";
            expectedPKs[7] ="8";
            expectedPKs[8] ="10";
            expectedPKs[9] ="11";
            expectedPKs[10] ="13";
            expectedPKs[11] ="14";
            expectedPKs[12] ="15";
            expectedPKs[13] ="16";
            expectedPKs[14] ="17";
            expectedPKs[15] ="18";

             if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_ANY_GT:  Did not get expected results. "
		+ "  Expected 16 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_ALL_GT: " + e);
          e.printStackTrace();
          throw new Fault("test_subquery_ALL_GT failed");
        }
   	if (!pass)
            throw new Fault("test_subquery_ANY_GT failed");
    }

   /*
    *   @testName: test_subquery_ANY_LT
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:797;
    * 			PERSISTENCE:SPEC:798
    *   @test_Strategy: Test for ANY in a subquery with the relational operator "<".
    *	Select all customers where total price of orders is less than ANY of the values
    *	in the result set. The result set contains the total price of orders where
    *	count of lineItems = 3.
    */

    public void test_subquery_ANY_LT() throws Fault
    {
       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

       try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT Distinct Object(c) FROM Customer c, IN(c.orders) co WHERE co.totalPrice < ANY"
		+ " (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity = 3)")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[17];
            expectedPKs[0] ="1";
            expectedPKs[1] ="2";
            expectedPKs[2] ="3";
            expectedPKs[3] ="4";
            expectedPKs[4] ="5";
            expectedPKs[5] ="6";
            expectedPKs[6] ="7";
            expectedPKs[7] ="8";
            expectedPKs[8] ="9";
            expectedPKs[9] ="11";
            expectedPKs[10] ="12";
            expectedPKs[11] ="13";
            expectedPKs[12] ="14";
            expectedPKs[13] ="15";
            expectedPKs[14] ="16";
            expectedPKs[15] ="17";
            expectedPKs[16] ="18";

             if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_ANY_LT:  Did not get expected results.  "
		+ "Expected 17 references, got: " + result.size());
            } else {
              TLogger.log("Expected results received");
              pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_ANY_LT: " + e);
          e.printStackTrace();
          throw new Fault("test_subquery_ANY_LT failed");
        }

   	if (!pass)
            throw new Fault("test_subquery_ANY_LT failed");
    }

   /*
    *   @testName: test_subquery_ANY_EQ
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:797;
    * 			PERSISTENCE:SPEC:798
    *   @test_Strategy: Test for ANY in a subquery with the relational operator "=".
    * 	Select all customers where total price of orders is = ANY the values in the result set.
    * 	The result set contains the min and avg of total price of orders.
    */

   public void test_subquery_ANY_EQ() throws Fault
   {
       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

       try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT Distinct object(c) FROM Customer c, IN(c.orders) co WHERE co.totalPrice = ANY"
		+ " (Select MAX(o.totalPrice) FROM Order o) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[2];
            expectedPKs[0] ="10";
            expectedPKs[1] ="14";

             if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_ANY_EQ:  Did not get expected results.  "
		+ "Expected 2 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;

            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_ANY_EQ:" + e);
          e.printStackTrace();
          throw new Fault("test_subquery_ANY_EQ failed");
        }

   	if (!pass)
            throw new Fault("test_subquery_ANY_EQ failed");
    }

   /*
    *   @testName: test_subquery_SOME_LTEQ
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:795;
    * 			PERSISTENCE:SPEC:797; PERSISTENCE:SPEC:798
    *   @test_Strategy: SOME with less than or equal to
    * 		        The result set contains the total price of orders where count of
    *			lineItems = 3.
    */

    public void test_subquery_SOME_LTEQ() throws Fault
    {

       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT DISTINCT object(c) FROM Customer c, IN(c.orders) co WHERE co.totalPrice <= SOME"
		+ " (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity = 3) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[18];
            expectedPKs[0] ="1";
            expectedPKs[1] ="2";
            expectedPKs[2] ="3";
            expectedPKs[3] ="4";
            expectedPKs[4] ="5";
            expectedPKs[5] ="6";
            expectedPKs[6] ="7";
            expectedPKs[7] ="8";
            expectedPKs[8] ="9";
            expectedPKs[9] ="10";
            expectedPKs[10] ="11";
            expectedPKs[11] ="12";
            expectedPKs[12] ="13";
            expectedPKs[13] ="14";
            expectedPKs[14] ="15";
            expectedPKs[15] ="16";
            expectedPKs[16] ="17";
            expectedPKs[17] ="18";

             if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_SOME_LTEQ:  Did not get expected results. "
		+ " Expected 18 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_SOME_LTEQ: " + e);
          e.printStackTrace();
          throw new Fault("test_subquery_SOME_LTEQ failed");
        }

   	if (!pass)
            throw new Fault( " test_subquery_SOME_LTEQ failed");
    }

   /*
    *   @testName: test_subquery_SOME_GTEQ
    *   @assertion_ids: PERSISTENCE:SPEC:794; PERSISTENCE:SPEC:795;
    * 			PERSISTENCE:SPEC:797; PERSISTENCE:SPEC:798
    *   @test_Strategy: Test for SOME in a subquery with the relational operator ">=".
    *	Select all customers where total price of orders is >= SOME the values in the result set.
    * 	The result set contains the total price of orders where count of lineItems = 3.
    */

    public void test_subquery_SOME_GTEQ() throws Fault
    {
      
       boolean pass=false;
       List result = null;
       String expectedPKs[]=null;

        try {
            getEntityTransaction().begin();
            result = getEntityManager().createQuery(
            "SELECT Distinct object(c) FROM Customer c, IN(c.orders) co WHERE co.totalPrice >= SOME"
		+ " (Select o.totalPrice FROM Order o, IN(o.lineItems) l WHERE l.quantity = 3) ")
            .setMaxResults(NUMOFCUSTOMERS)
            .getResultList();

            expectedPKs = new String[17];
            expectedPKs[0] ="1";
            expectedPKs[1] ="2";
            expectedPKs[2] ="3";
            expectedPKs[3] ="4";
            expectedPKs[4] ="5";
            expectedPKs[5] ="6";
            expectedPKs[6] ="7";
            expectedPKs[7] ="8";
            expectedPKs[8] ="9";
            expectedPKs[9] ="10";
            expectedPKs[10] ="11";
            expectedPKs[11] ="13";
            expectedPKs[12] ="14";
            expectedPKs[13] ="15";
            expectedPKs[14] ="16";
            expectedPKs[15] ="17";
            expectedPKs[16] ="18";

            if(!Util.checkEJBs(result, CUSTOMERREF, expectedPKs)) {
                TLogger.log("ERROR test_subquery_SOME_GTEQ:  Did not get expected results. "
		+ " Expected 17 references, got: " + result.size());
            } else {
                TLogger.log("Expected results received");
                pass=true;
            }
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Caught exception test_subquery_SOME_GTEQ: " + e);
          e.printStackTrace();
          throw new Fault("test_subquery_SOME_GTEQ failed");
        }

   	if (!pass)
            throw new Fault("test_subquery_SOME_GTEQ failed");
    }


    /*
     *
     *	Setup for Query Language Tests
     *
     */

    public void schema30Setup(Properties p) throws Exception
    {
        TLogger.log("Entering Schema 3.0 Setup");
        try {

            props = p;

            TLogger.log(
		"Check if Schema already exists in Persistent Storage");
	    if(SchemaAlreadyExists()) return;
	    //if(schemaExists) return;

            TLogger.log("Create Schema Data");
	    createSchemaData(p);

	    TLogger.log("Set Relationships");
	    createRelationships();

            TLogger.log("Done creating Schema in Persistent Storage");
	    //schemaExists = true;
	    TLogger.log("Exiting setup w/o Error");
     	} catch (Exception e) {
          if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
	  }
          RemoveSchemaData();
	  e.printStackTrace();
            throw new Exception("Exception occurred in Schema 3.0 setup: " + e);
        } finally {
       	  try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
		}
       	  } catch (Exception re) {
              TLogger.log("Unexpection Exception in schema30Setup:" + re );
              re.printStackTrace();
       	  }
        }
   }

    private boolean SchemaAlreadyExists() throws Exception
    {

	getEntityTransaction().begin();
        TLogger.log("SchemaAlreadyExists");

        boolean schemaExists = true;

        TLogger.log("Invoke findAllCustomers");
	custCol = getEntityManager().createQuery(
                	"Select DISTINCT Object(c) From Customer c")
                	.setMaxResults(NUMOFCUSTOMERS)
                	.getResultList();

        TLogger.log("Invoke findAllProducts");
	prodCol = getEntityManager().createQuery(
                	"Select DISTINCT Object(p) From Product p")
                	.setMaxResults(NUMOFALLPRODUCTS)
                	.getResultList();

        TLogger.log("Invoke findAllOrders");
	orderCol = getEntityManager().createQuery(
                	"Select DISTINCT Object(o) From Order o")
                	.setMaxResults(NUMOFORDERS)
                	.getResultList();

        TLogger.log("Invoke findAllAliases");
	aliasCol = getEntityManager().createQuery(
                	"Select DISTINCT Object(a) From Alias a")
                	.setMaxResults(NUMOFALIASES)
                	.getResultList();

	if(custCol.size() != NUMOFCUSTOMERS || 	
		prodCol.size() != NUMOFALLPRODUCTS ||
			orderCol.size() != NUMOFORDERS ||
				aliasCol.size() != NUMOFALIASES) {
	    TLogger.log("Number of customers found = " + custCol.size());
	    TLogger.log("Number of products found = " + prodCol.size());
	    TLogger.log("Number of orders found = " + orderCol.size());
	    TLogger.log("Number of aliases found = " + aliasCol.size());
	    schemaExists = false;
	}

        getEntityTransaction().commit();

	if(schemaExists) {
	    TLogger.log("Schema already exists in Persistent Storage");
	    return true;
	} else {
            TLogger.log("Schema does not exist in Persistent Storage");
	    RemoveSchemaData();
	    return false;
	}
    }


    private void RemoveSchemaData()
    {

        TLogger.log("RemoveSchemaData");
	// Determine if additional persistent data needs to be removed

	try {
	    getEntityTransaction().begin();

            for (int j=0; j<NUMOFALIASES; j++ ) {
                    Alias alias =
			getEntityManager().find(Alias.class, Integer.toString(j));
		    if (null != alias ) {
                        getEntityManager().remove(alias);
		        getEntityManager().flush();
                        TLogger.log("removed alias " + alias);
		    }
            }

            for (int i=0; i<NUMOFCUSTOMERS; i++ ) {
                    Customer cust =
			 getEntityManager().find(Customer.class, Integer.toString(i));
		    if (null != cust ) {
                        getEntityManager().remove(cust);
		        getEntityManager().flush();
                        TLogger.log("removed customer " + cust);
		    }
            }

   	     if (prodCol.size() != 0 ) {
               TLogger.log("Products found: cleaning up ");
               Iterator iterator = prodCol.iterator();
               while(iterator.hasNext()) {
                 Product pRef = (Product) iterator.next();
                 Product prod = getEntityManager().find(Product.class, (String)pRef.getId() );
	    	    if (null != prod ) {
                        getEntityManager().remove(prod);
		        getEntityManager().flush();
                        TLogger.log("removed product " + prod);
		    }
                 }
            }

            for (int l=0; l<NUMOFORDERS; l++ ) {
                    Order ord =
			getEntityManager().find(Order.class, Integer.toString(l));
		    if (null != ord ) {
                        getEntityManager().remove(ord);
		        getEntityManager().flush();
                        TLogger.log("removed order " + ord);
		    }
            }
	  getEntityTransaction().commit();
	} catch(Exception e) {
              TLogger.log("Exception encountered while removing entities:");
              e.printStackTrace();
        } finally {
	  try {
		if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
		}
           } catch (Exception re) {
    	     TLogger.log("Unexpection Exception in RemoveSchemaData:" + re );
             re.printStackTrace();
           }
        }
    }

    private static Date getShelfDate(int yy, int mm, int dd)
    {
        Calendar newCal = Calendar.getInstance();
        newCal.clear();
        newCal.set(yy,mm,dd);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdf.format(newCal.getTime());
        TLogger.log("returning date:" + java.sql.Date.valueOf(sDate) );
        return java.sql.Date.valueOf(sDate);
    }


    private void doFlush() throws PersistenceException
    {
	TLogger.log("Entering doFlush method");
	try {
		getEntityManager().flush();
	} catch (PersistenceException pe) {
		pe.printStackTrace();
	 	throw new PersistenceException("Unexpected Exception caught while flushing: " + pe);
        }
    }

    private void createSchemaData(Properties p) throws Exception
    {
        getEntityTransaction().begin();
        TLogger.log("Entered createSchemaData");

	TLogger.log("Create " + NUMOFCOUNTRIES + " countries");
	country[0] = new Country("United States", "USA");
	country[1] = new Country("United States", "USA");
	country[2] = new Country("United States", "USA");
	country[3] = new Country("United States", "USA");
	country[4] = new Country("United States", "USA");
	country[5] = new Country("United States", "USA");
	country[6] = new Country("United States", "USA");
	country[7] = new Country("United States", "USA");
	country[8] = new Country("United States", "USA");
	country[9] = new Country("United States", "USA");
	country[10] = new Country("England", "GBR");
	country[11] = new Country("Ireland", "IRE");
	country[12] = new Country("China", "CHA");
	country[13] = new Country("Japan", "JPN");
	country[14] = new Country("United States", "USA");
	country[15] = new Country("England", "GBR");
	country[16] = new Country("Ireland", "IRE");
	country[17] = new Country("China", "CHA");
	country[18] = new Country("China", "CHA");
	country[19] = new Country("China", "CHA");


	TLogger.log("Create " + NUMOFADDRESSES + " addresses");
	address[0] = new Address(
		"1", "1 Oak Road", "Bedford", "MA", "02155");
	address[1] = new Address(
		"2", "1 Network Drive", "Burlington", "MA", "00252");

	address[2] = new Address(
		"3", "10 Griffin Road", "Lexington", "MA", "02277");
	address[3] = new Address(
		"4", "1 Network Drive", "Burlington", "MA", "00252");

	address[4] = new Address(
		"5", "125 Moxy Lane", "Swansea", "MA", "11345");
	address[5] = new Address(
		"6", "1 Network Drive", "Burlington", "MA", "11345");

	address[6] = new Address(
		"7", "2654 Brookline Avenue", "Brookline", "MA", "11678");
	address[7] = new Address(
		"8", "1 Network Drive", "Burlington", "MA", "00252");

	address[8] = new Address(
		"9", "100 Forrest Drive", "Hudson", "NH", "78654");
	address[9] = new Address(
		"10", "1 Network Drive", "Burlington", "MA", "00252");

	address[10] = new Address(
		"11", "200 Elliot Road", "Nashua", "NH", "65447");
	address[11] = new Address(
		"12", "1 Network Drive", "Burlington", "MA", "00252");

	address[12] = new Address(
		"13", "634 Goldstar Road", "Peabody", "MA", "88444");
	address[13] = new Address(
		"14", "1 Network Drive", "Burlington", "MA", "00252");

	address[14] = new Address(
		"15", "100 Forrest Drive", "Peabody", "MA", "88444");
	address[15] = new Address(
		"16", "1 Network Drive", "Burlington", "MA", "00252");

        address[16] = new Address(
                "17", "18 Rosewood Avenue", null, "MA", "87653");
        address[17] = new Address(
                "18", "1 Network Drive", "Burlington", "MA", "00252");

        address[18] = new Address( 
                "19", null, "Belmont", "VT", "23083"); 
        address[19] = new Address( 
                "20", "1 Network Drive", "Burlington", "MA", "00252"); 

        address[20] = new Address( 
                "21", "3212 Boston Road", "Chelmsford", "MA", "01824"); 
        address[21] = new Address( 
                "22", "1 Network Drive", "Burlington", "MA", "00252"); 

        address[22] = new Address(
                "23", "212 Edgewood Drive", "Claremont", "NH", "58976");
        address[23] = new Address(
                "24", "1 Network Drive", "Burlington", null, "00252");

        address[24] = new Address(
                "25", "47 Skyline Drive", "Attleboro", "MA", "76656");
        address[25] = new Address(
                "26", "1 Network Drive", "Burlington", "MA", null);

        address[26] = new Address(
                "27", "4 Rangeway Road", "Lawrence", "RI", "53026");
        address[27] = new Address(
                "28", "1 Network Drive", "Burlington", "MA", "00252");

        address[28] = new Address(
                "29", "48 Sears Street", "Boston", "MA", "02110");
        address[29] = new Address(
                "30", "1 Network Drive", "Burlington", "MA", "00252");

        address[30] = new Address(
                "31", "1240 Davis Drive", "Northwood", "NH", "03260");
        address[31] = new Address(
                "32", "1 Network Drive", "Burlington", "MA", "00252");

        address[32] = new Address(
                "33", "455 James Avenue", "Roslindale", "NY", "57550");
        address[33] = new Address(
                "34", "1 Network Drive", "Burlington", "MA", "00252");

        address[34] = new Address(
                "35", "8 Beverly Lane", "HarwichPort", "PA", "45870");
        address[35] = new Address(
                "36", "1 Network Drive", "Burlington", "MA", "00252");

                for (int i=0; i<NUMOFADDRESSES; i++ ) {
                    getEntityManager().persist(address[i]);
                    TLogger.log("persisted address " + address[i]);
                }

        TLogger.log("Create " + NUMOFPHONES + " phone numbers");
        phone[0] = new Phone("1", "617", "664-8122", address[0]);
        phone[1] = new Phone("2", "781", "442-8122", address[1]);
        phone[2] = new Phone("3", "508", "662-7117", address[2]);
        phone[3] = new Phone("4", "781", "442-4488", address[3]);
        phone[4] = new Phone("5", "992", "223-8888", address[4]);
        phone[5] = new Phone("6", "781", "442-1134", address[5]);

        phone[6] = new Phone("7", "442", "883-1597", address[6]);
        phone[7] = new Phone("8", "781", "442-6699", address[7]);
        phone[8] = new Phone("9", "603", "777-7890", address[8]);
        phone[9] = new Phone("10", "781", "442-2323", address[9]);
        phone[10] = new Phone("11", "603", "889-2355", address[10]);

        phone[11] = new Phone("12", "781", "442-9876", address[11]);
        phone[12] = new Phone("13", "222", "767-3124", address[12]);
        phone[13] = new Phone("14", "781", "442-1111", address[13]);
        phone[14] = new Phone("15", "222", "767-8898", address[14]);
        phone[15] = new Phone("16", "781", "442-4444", address[15]);

        phone[16] = new Phone("17", null, "564-9087", address[16]);
        phone[17] = new Phone("18", "781", "442-5341", address[17]);
        phone[18] = new Phone("19", null, null, address[18]);
        phone[19] = new Phone("20", "781", "442-1585", address[19]);
        phone[20] = new Phone("21", "207", "532-6354", address[20]);

        phone[21] = new Phone("22", "781", "442-0845", address[21]);
        phone[22] = new Phone("23", "913", null, address[22]);
        phone[23] = new Phone("24", "781", "442-7465", address[23]);
        phone[24] = new Phone("25", "678", "663-6091", address[24]);
        phone[25] = new Phone("26", "781", "442-2139", address[25]);

        phone[26] = new Phone("27", "890", "670-9138", address[26]);
        phone[27] = new Phone("28", "781", "442-0230", address[27]);
        phone[28] = new Phone("29", "450", "876-9087", address[28]);
        phone[29] = new Phone("30", "781", "442-6766", address[29]);
        phone[30] = new Phone("31", "908", "458-0980", address[30]);

        phone[31] = new Phone("32", "781", "442-6251", address[31]);
        phone[32] = new Phone("33", "432", "435-0909", address[32]);
        phone[33] = new Phone("34", "781", "442-8790", address[33]);
        phone[34] = new Phone("35", "415", "355-9008", address[34]);
        phone[35] = new Phone("36", "781", "442-2879", address[35]);

                for (int i=0; i<NUMOFPHONES; i++ ) {
                    getEntityManager().persist(phone[i]);
                    TLogger.log("persisted phone " + phone[i]);
		    doFlush();
                }

	TLogger.log("Create " + NUMOFCREDITCARDS + " creditcards");
	creditCard[0] = new CreditCard(
		"1", "1234-2567-1222-9999", "VISA", "04/02", true, (double)5579);
	creditCard[1] = new CreditCard(
		"2", "3455-9876-1221-0060", "MCARD", "10/03", false, (double)15000);
	creditCard[2] = new CreditCard(
		"3", "1210-1449-2200-3254", "AXP", "11/02", true, (double)3000);
	creditCard[3] = new CreditCard(
		"4", "0002-1221-0078-0890", "VISA", "05/03", true, (double)8000);
	creditCard[4] = new CreditCard(
		"5", "1987-5555-8733-0011", "VISA", "05/03", true, (double)2500);
	creditCard[5] = new CreditCard(
		"6", "0000-0011-2200-3087", "MCARD", "11/02", true, (double)23000);
	creditCard[6] = new CreditCard(
		"7", "3341-7610-8880-9910", "AXP", "10/04", true, (double)13000);
	creditCard[7] = new CreditCard(
		"8", "2222-3333-4444-5555", "MCARD", "12/03", true, (double)2000);
	creditCard[8] = new CreditCard(
		"9", "8888-2222-0090-1348", "AXP", "01/02", true, (double)4500);
	creditCard[9] = new CreditCard(
		"10", "1762-5094-8769-3117", "VISA", "06/01", true, (double)14000);
	creditCard[10] = new CreditCard(
		"11", "1234-1234-1234-9999", "MCARD", "09/03", true, (double)7000);
	creditCard[11] = new CreditCard(
		"12", "9876-9876-1234-5678", "VISA", "04/04", false, (double)1000);
	creditCard[12] = new CreditCard(
		"13", "7777-8888-9999-0012", "MCARD", "01/02", true, (double)3500);
	creditCard[13] = new CreditCard(
		"14", "9099-8808-7718-4455", "AXP", "03/05", true, (double)4400);
	creditCard[14] = new CreditCard(
		"15", "7653-7901-2397-1768", "AXP", "02/04", true, (double)5000);
	creditCard[15] = new CreditCard(
		"16", "8760-8618-9263-3322", "VISA", "04/05", false, (double)750);
	creditCard[16] = new CreditCard(
		"17", "9870-2309-6754-3210", "MCARD", "03/03", true, (double)500);
	creditCard[17] = new CreditCard(
		"18", "8746-8754-9090-1234", "AXP", "08/04", false, (double)1500);
	creditCard[18] = new CreditCard(
		"19", "8736-0980-8765-4869", "MCARD", "09/02", true, (double)5500);
	creditCard[19] = new CreditCard(
		"20", "6745-0979-0970-2345", "VISA", "02/05", true, (double)1400);
	creditCard[20] = new CreditCard(
		"21", "8033-5896-9901-4566", "AXP", "09/07", true, (double)400);
	creditCard[21] = new CreditCard(
		"22", "4390-5671-4385-0091", "MCARD", "03/06", false, (double)7400);
	creditCard[22] = new CreditCard(
		"23", "3456-0909-3434-2134", "VISA", "04/08", true, (double)9500);
	creditCard[23] = new CreditCard(
		"24", "5643-2090-4569-2323", "MCARD", "01/06", false, (double)1000);

            	for (int i=0; i<NUMOFCREDITCARDS; i++ ) {
                    getEntityManager().persist(creditCard[i]);
                    TLogger.log("persisted creditCard " + creditCard[i]);
	   	    doFlush();
                }

	TLogger.log("Create  " + NUMOFLINEITEMS + " lineitems");
	lineItem[0] = new LineItem("1", 1);
	lineItem[1] = new LineItem("2", 1);
	lineItem[2] = new LineItem("3", 1);
	lineItem[3] = new LineItem("4", 1);
	lineItem[4] = new LineItem("5", 1);
	lineItem[5] = new LineItem("6", 1);
	lineItem[6] = new LineItem("7", 1);
	lineItem[7] = new LineItem("8", 1);
	lineItem[8] = new LineItem("9", 1);
	lineItem[9] = new LineItem("10", 1);
	lineItem[10] = new LineItem("11", 1);
	lineItem[11] = new LineItem("12", 1);
	lineItem[12] = new LineItem("13", 1);
	lineItem[13] = new LineItem("14", 1);
	lineItem[14] = new LineItem("15", 1);
	lineItem[15] = new LineItem("16", 1);
	lineItem[16] = new LineItem("17", 1);
	lineItem[17] = new LineItem("18", 1);
	lineItem[18] = new LineItem("19", 1);
	lineItem[19] = new LineItem("20", 1);
	lineItem[20] = new LineItem("21", 1);
	lineItem[21] = new LineItem("22", 1);
	lineItem[22] = new LineItem("23", 1);
	lineItem[23] = new LineItem("24", 1);
	lineItem[24] = new LineItem("25", 1);
	lineItem[25] = new LineItem("26", 1);
	lineItem[26] = new LineItem("27", 1);
	lineItem[27] = new LineItem("28", 1);
	lineItem[28] = new LineItem("29", 1);
	lineItem[29] = new LineItem("30", 5);
	lineItem[30] = new LineItem("31", 3);
	lineItem[31] = new LineItem("32", 8);
	lineItem[32] = new LineItem("33", 1);
	lineItem[33] = new LineItem("34", 1);
	lineItem[34] = new LineItem("35", 6);
	lineItem[35] = new LineItem("36", 1);
	lineItem[36] = new LineItem("37", 2);
	lineItem[37] = new LineItem("38", 3);
	lineItem[38] = new LineItem("39", 5);
	lineItem[39] = new LineItem("40", 3);
	lineItem[40] = new LineItem("41", 2);
	lineItem[41] = new LineItem("42", 1);
	lineItem[42] = new LineItem("43", 1);
	lineItem[43] = new LineItem("44", 3);
	lineItem[44] = new LineItem("45", 1);
	lineItem[45] = new LineItem("46", 2);
	lineItem[46] = new LineItem("47", 3);
	lineItem[47] = new LineItem("48", 3);
	lineItem[48] = new LineItem("49", 4);
	lineItem[49] = new LineItem("50", 5);
	lineItem[50] = new LineItem("51", 2);
	lineItem[51] = new LineItem("52", 1);
	lineItem[52] = new LineItem("53", 3);
	lineItem[53] = new LineItem("54", 1);
	lineItem[54] = new LineItem("55", 3);
	lineItem[55] = new LineItem("56", 1);

          	for (int i=0; i<NUMOFLINEITEMS; i++ ) {
                    getEntityManager().persist(lineItem[i]);
                    TLogger.log("persisted lineItem " + lineItem[i]);
		    doFlush();
                }

        TLogger.log("Create " + NUMOFCUSTOMERS + " customers");
        customerRef[0] = new Customer(
		"1", "Alan E. Frechette", 
			address[0], address[1], country[0]);
        customerRef[1] = new Customer(
		"2", "Arthur D. Frechette", 
			address[2], address[3], country[1]);
        customerRef[2] = new Customer(
		"3", "Shelly D. McGowan", 
			address[4], address[5], country[2]);
        customerRef[3] = new Customer(
		"4", "Robert E. Bissett", 
			address[6], address[7], country[3]);
        customerRef[4] = new Customer(
		"5", "Stephen S. D'Milla", 
			address[8], address[9], country[4]);
        customerRef[5] = new Customer(
		"6", "Karen R. Tegan", 
			address[10], address[11], country[5]);
        customerRef[6] = new Customer(
		"7", "Stephen J. Caruso", 
			address[12], address[13], country[6]);
        customerRef[7] = new Customer(
		"8", "Irene M. Caruso", 
			address[14], address[15], country[7]);
        customerRef[8] = new Customer(
		"9", "William P. Keaton", 
			address[16], address[17], country[8]);
        customerRef[9] = new Customer(
		"10", "Kate P. Hudson", 
			address[18], address[19], country[9]);
        customerRef[10] = new Customer(
		"11", "Jonathan K. Smith", 
			address[20], address[21], country[10]);
        customerRef[11] = new Customer(
		"12", null, 
			address[22], address[23], country[11]);
        customerRef[12] = new Customer(
		"13", "Douglas A. Donahue", 
			address[24], address[25], country[12]);
        customerRef[13] = new Customer(
		"14", "Kellie A. Sanborn", 
			address[26], address[27], country[13]);
        customerRef[14] = new Customer(
		"15", "Margaret Mills",
			address[28], address[29], country[14]);
        customerRef[15] = new Customer(
		"16", "Sonya C. Sanders",
			address[30], address[31], country[15]);
        customerRef[16] = new Customer(
		"17", "Jack B. Grace",
			address[32], address[33], country[16]);
        customerRef[17] = new Customer(
		"18", "Ron F. Bender",
			address[34], address[35], country[17]);
        customerRef[18] = new Customer(
		"19", "Lisa M. Presley", country[18]);
        customerRef[19] = new Customer(
		"20", " David R. Vincent ", country[19]);

          	for (int i=0; i<NUMOFCUSTOMERS; i++ ) {
                    getEntityManager().persist(customerRef[i]);
                    TLogger.log("persisted customer " + customerRef[i]);
		    doFlush();
                }

        TLogger.log("Create " + NUMOFINFODATA + " spouse info data");
        info[0] = new Info();
		info[0].setId("1");
		info[0].setStreet("634 Goldstar Road");
		info[0].setCity("Peabody");
		info[0].setState("MA");
		info[0].setZip("88444");
        info[1] = new Info();
		info[1].setId("2");
		info[1].setStreet("3212 Boston Road");
		info[1].setCity("Chelmsford");
		info[1].setState("MA");
		info[1].setZip("01824");
        info[2] = new Info();
		info[2].setId("3");
		info[2].setStreet("47 Skyline Drive");
		info[2].setCity("Attleboro");
		info[2].setState("MA");
		info[2].setZip("76656");
        info[3] = new Info();
		info[3].setId("4");
		info[3].setStreet(null);
		info[3].setCity("Belmont");
		info[3].setState("VT");
		info[3].setZip("23083");
        info[4] = new Info();
		info[4].setId("5");
		info[4].setStreet("212 Edgewood Drive");
		info[4].setCity("Claremont");
		info[4].setState("NH");
		info[4].setZip("58976");
        info[5] = new Info();
		info[5].setId("6");
		info[5].setStreet("11 Richmond Lane");
		info[5].setCity("Chatham");
		info[5].setState("NJ");
		info[5].setZip("65490");

        TLogger.log("Create " + NUMOFSPOUSES + " spouses");
        spouse[0] = new Spouse(
		"1", "Kathleen", "Jones", "Porter",
                         "034-58-0988", info[0], customerRef[6]);
        spouse[1] = new Spouse(
		"2", "Judith", "Connors", "McCall",
                         "074-22-6431", info[1], customerRef[10]);
        spouse[2] = new Spouse(
		"3", "Linda", "Kelly", "Morrison",
                         "501-22-5940", info[2], customerRef[12]);
        spouse[3] = new Spouse(
		"4", "Thomas", null, "Mullen",
                         "210-23-3456", info[3], customerRef[9]);
        spouse[4] = new Spouse(
		"5", "Mitchell", null, "Jackson",
                         "476-44-3349", info[4], customerRef[11]);
        spouse[5] = new Spouse(
		"6", "Cynthia", "White", "Allen",
                         "508-908-7765", info[5]);

           	for (int i=0; i<NUMOFSPOUSES; i++ ) {
                    getEntityManager().persist(spouse[i]);
                    TLogger.log("persisted spouse " + spouse[i]);
		    doFlush();
		}

        TLogger.log("Create " + NUMOFPRODUCTS + " products");
        productRef[0] = new Product(
		 "1", "Java 2 Unleashed Programming",
			 (double)54.95, 100, (long)987654321);
        productRef[1] = new Product(
		"2", "Java 2 Network Programming",
			 (double)37.95, 100, (long)876543219);
        productRef[2] = new Product(
		"3", "CORBA Programming",
			 (double)44.95, 55, (long)765432198);
        productRef[3] = new Product(
		"4", "WEB Programming with JSP's & Servlet's",
			 (double)33.95, 25, (long)654321987);
        productRef[4] = new Product(
		"5", "Dell Laptop PC",
			 (double)1095.95, 50, (long)543219876);
        productRef[5] = new Product(
		"6", "Compaq Laptop PC",
			 (double)995.95, 33, (long)432198765);
        productRef[6] = new Product(
		"7", "Toshiba Laptop PC",
			 (double)1210.95, 22, (long)321987654);
        productRef[7] = new Product(
		"8", "Gateway Laptop PC",
			 (double)1100.95, 11, (long)219876543);
        productRef[8] = new Product(
		"9", "Free Samples",
			 (double)0.00, 10, (long)000000000);
        productRef[9] = new Product(
		"10", "Designing Enterprise Applications",
			 (double)39.95, 500, (long)123456789);
        productRef[10] = new Product(
		"11", "Complete Guide to XML",
			 (double)38.85, 300, (long)234567891);
        productRef[11] = new Product(
		"12", "Programming for Dummies",
			 (double)24.95, 45, (long)345678912);
        productRef[12] = new Product(
		"13", "Introduction to Java",
			 (double)60.95, 95, (long)456789123);
        productRef[13] = new Product(
		"14", "Ultra System",
			 (double)5095.95, 250, (long)567891234);
        productRef[14] = new Product(
		"15", "Very Best Tutorial",
			 (double)25.99, 0, (long)678912345);
        productRef[15] = new Product(
		"16", "Home Grown Programming Examples",
			 (double)10.95, 25, (long)789123456);
        productRef[16] = new Product(
		"17", "Programming in ANSI C",
			 (double)23.95, 10, (long)891234567);
        productRef[17] = new Product(
		"18", "Trial Software",
			 (double)10.00, 75, (long)912345678);

         	for (int i=0; i<NUMOFPRODUCTS; i++ ) {
                    getEntityManager().persist(productRef[i]);
                    TLogger.log("persisted product " + productRef[i]);
		    doFlush();
                }

        TLogger.log("Create 10 ShelfLife Instances");
        shelfRef[0] = new ShelfLife(d1, null);
        shelfRef[1] = new ShelfLife(d2, null);
        shelfRef[2] = new ShelfLife(d3, null);
        shelfRef[3] = new ShelfLife(d4, null);
        shelfRef[4] = new ShelfLife(d5, null);
        shelfRef[5] = new ShelfLife(null, null);
        shelfRef[6] = new ShelfLife(null, d6);
        shelfRef[7] = new ShelfLife(null, d7);
        shelfRef[8] = new ShelfLife(d8, d9);
        shelfRef[9] = new ShelfLife(null, d10);

        TLogger.log("Create " + NUMOFHWPRODUCTS + " Hardware Products");
        hardwareRef[0] = new HardwareProduct();
		hardwareRef[0].setId("19");
		hardwareRef[0].setName("Gateway E Series");
		hardwareRef[0].setPrice((double)600.00);
		hardwareRef[0].setQuantity(25);
		hardwareRef[0].setPartNumber((long)238945678);
		hardwareRef[0].setShelfLife(shelfRef[0]);
		hardwareRef[0].setWareHouse("Columbia");
		hardwareRef[0].setModelNumber(2578);
	        getEntityManager().persist(hardwareRef[0]);
        hardwareRef[1] = new HardwareProduct();
		hardwareRef[1].setId("20");
		hardwareRef[1].setName("Java Desktop Systems");
		hardwareRef[1].setPrice((double)890.00);
		hardwareRef[1].setQuantity(50);
		hardwareRef[1].setPartNumber((long)304506708);
		hardwareRef[1].setModelNumber(10050);
		hardwareRef[1].setWareHouse("Lowell");
	        getEntityManager().persist(hardwareRef[1]);
        hardwareRef[2] = new HardwareProduct();
		hardwareRef[2].setId("21");
		hardwareRef[2].setName("Dell Inspiron");
		hardwareRef[2].setPrice((double)1100.00);
		hardwareRef[2].setQuantity(5);
		hardwareRef[2].setPartNumber((long)373767373);
		hardwareRef[2].setModelNumber(01100);
		hardwareRef[2].setWareHouse("Richmond");
		hardwareRef[2].setShelfLife(shelfRef[1]);
	        getEntityManager().persist(hardwareRef[2]);
        hardwareRef[3] = new HardwareProduct();
		hardwareRef[3].setId("22");
		hardwareRef[3].setName("Toshiba");
		hardwareRef[3].setPrice((double)250.00);
		hardwareRef[3].setQuantity(40);
		hardwareRef[3].setPartNumber((long)285764839);
		hardwareRef[3].setModelNumber(00720);
		hardwareRef[3].setWareHouse("Richmond");
	        getEntityManager().persist(hardwareRef[3]);
        hardwareRef[4] = new HardwareProduct();
		hardwareRef[4].setId("23");
		hardwareRef[4].setName("SunBlade");
		hardwareRef[4].setPrice((double)450.00);
		hardwareRef[4].setQuantity(80);
		hardwareRef[4].setPartNumber((long)987290102);
		hardwareRef[4].setModelNumber(00150);
	        getEntityManager().persist(hardwareRef[4]);
        hardwareRef[5] = new HardwareProduct();
		hardwareRef[5].setId("24");
		hardwareRef[5].setName("Opteron");
		hardwareRef[5].setPrice((double)800.00);
		hardwareRef[5].setQuantity(33);
		hardwareRef[5].setPartNumber((long)725109484);
		hardwareRef[5].setModelNumber(00050);
		hardwareRef[5].setWareHouse("Lowell");
		hardwareRef[5].setShelfLife(shelfRef[2]);
	        getEntityManager().persist(hardwareRef[5]);
        hardwareRef[6] = new HardwareProduct();
		hardwareRef[6].setId("25");
		hardwareRef[6].setName("Sun Enterprise");
		hardwareRef[6].setPrice((double)15000.00);
		hardwareRef[6].setQuantity(100);
		hardwareRef[6].setPartNumber((long)773620626);
		hardwareRef[6].setModelNumber(10000);
	        getEntityManager().persist(hardwareRef[6]);
        hardwareRef[7] = new HardwareProduct();
		hardwareRef[7].setId("26");
		hardwareRef[7].setName("Dell Dimension");
		hardwareRef[7].setPrice((double)950.00);
		hardwareRef[7].setQuantity(70);
		hardwareRef[7].setPartNumber((long)927262628);
		hardwareRef[7].setModelNumber(3000);
	        getEntityManager().persist(hardwareRef[7]);
        hardwareRef[8] = new HardwareProduct();
		hardwareRef[8].setId("27");
		hardwareRef[8].setName("Dell Dimension");
		hardwareRef[8].setPrice((double)795.00);
		hardwareRef[8].setQuantity(20);
		hardwareRef[8].setPartNumber((long)482726166);
		hardwareRef[8].setModelNumber(04500);
		hardwareRef[8].setShelfLife(shelfRef[3]);
		hardwareRef[8].setWareHouse("Columbia");
	        getEntityManager().persist(hardwareRef[8]);
        hardwareRef[9] = new HardwareProduct();
		hardwareRef[9].setId("28");
		hardwareRef[9].setName("SunBlade");
		hardwareRef[9].setPrice((double)1000.00);
		hardwareRef[9].setQuantity(20);
		hardwareRef[9].setPartNumber((long)312010108);
		hardwareRef[9].setModelNumber(00100);
		hardwareRef[9].setShelfLife(shelfRef[4]);
	        getEntityManager().persist(hardwareRef[9]);

		TLogger.log("Flush hardware products");
		doFlush();

        TLogger.log("Create " + NUMOFSWPRODUCTS + " Software Products");
        softwareRef[0] = new SoftwareProduct();
                softwareRef[0].setId("29");
                softwareRef[0].setName("SunOS 9");
                softwareRef[0].setPrice((double)500.00);
                softwareRef[0].setQuantity(500);
                softwareRef[0].setPartNumber((long)837373379);
                softwareRef[0].setRevisionNumber((double)1.0);
		softwareRef[0].setShelfLife(shelfRef[5]);
                getEntityManager().persist(softwareRef[0]);
        softwareRef[1] = new SoftwareProduct();
                softwareRef[1].setId("30");
                softwareRef[1].setName("Patch 590-009");
                softwareRef[1].setPrice((double)55.00);
                softwareRef[1].setQuantity(23);
                softwareRef[1].setPartNumber((long)285764891);
                softwareRef[1].setRevisionNumber((double)1.1);
                getEntityManager().persist(softwareRef[1]);
        softwareRef[2] = new SoftwareProduct();
                softwareRef[2].setId("31");
                softwareRef[2].setName("NetBeans");
                softwareRef[2].setPrice((double)35.00);
                softwareRef[2].setQuantity(15);
                softwareRef[2].setPartNumber((long)174983901);
                softwareRef[2].setRevisionNumber((double)4.0);
		softwareRef[2].setShelfLife(shelfRef[6]);
	 	softwareRef[2].setWareHouse("Lowell");
                getEntityManager().persist(softwareRef[2]);
        softwareRef[3] = new SoftwareProduct();
                softwareRef[3].setId("32");
                softwareRef[3].setName("J2SE");
		softwareRef[3].setPrice((double)150.00);
                softwareRef[3].setQuantity(100);
                softwareRef[3].setPartNumber((long)173479765);
                softwareRef[3].setRevisionNumber((double)5.0);
		softwareRef[3].setShelfLife(shelfRef[7]);
                getEntityManager().persist(softwareRef[3]);
       softwareRef[4] = new SoftwareProduct();
                softwareRef[4].setId("33");
                softwareRef[4].setName("Creator");
                softwareRef[4].setPrice((double)125.00);
                softwareRef[4].setQuantity(60);
                softwareRef[4].setPartNumber((long)847651234);
                softwareRef[4].setRevisionNumber((double)4.0);
		softwareRef[4].setShelfLife(shelfRef[8]);
                getEntityManager().persist(softwareRef[4]);
        softwareRef[5] = new SoftwareProduct();
                softwareRef[5].setId("34");
                softwareRef[5].setName("Java Programming Examples");
                softwareRef[5].setPrice((double)175.00);
                softwareRef[5].setQuantity(200);
                softwareRef[5].setPartNumber((long)376512908);
                softwareRef[5].setRevisionNumber((double)1.5);
                getEntityManager().persist(softwareRef[5]);
        softwareRef[6] = new SoftwareProduct();
                softwareRef[6].setId("35");
                softwareRef[6].setName("Tutorial");
                softwareRef[6].setPrice((double)250.00);
                softwareRef[6].setQuantity(35);
                softwareRef[6].setPartNumber((long)837462890);
                softwareRef[6].setRevisionNumber((double)1.4);
		softwareRef[6].setWareHouse(null);
                getEntityManager().persist(softwareRef[6]);
        softwareRef[7] = new SoftwareProduct();
                softwareRef[7].setId("36");
                softwareRef[7].setName("Testing Tools");
                softwareRef[7].setPrice((double)300.00);
                softwareRef[7].setQuantity(20);
                softwareRef[7].setPartNumber((long)372615467);
                softwareRef[7].setRevisionNumber((double)1.0);
                getEntityManager().persist(softwareRef[7]);
        softwareRef[8] = new SoftwareProduct();
                softwareRef[8].setId("37");
                softwareRef[8].setName("Patch 395-478");
                softwareRef[8].setPrice((double)55.00);
                softwareRef[8].setQuantity(25);
                softwareRef[8].setPartNumber((long)847628901);
                softwareRef[8].setRevisionNumber((double)1.1);
		softwareRef[8].setShelfLife(shelfRef[9]);
		softwareRef[8].setWareHouse("Lowell");
                getEntityManager().persist(softwareRef[8]);
        softwareRef[9] = new SoftwareProduct();
                softwareRef[9].setId("38");
                softwareRef[9].setName("Appserver 8");
                softwareRef[9].setPrice((double)0.00);
                softwareRef[9].setQuantity(150);
                softwareRef[9].setPartNumber((long)873657891);
                softwareRef[9].setRevisionNumber((double)1.1);
                getEntityManager().persist(softwareRef[9]);

		TLogger.log("Flush software products");
		doFlush();

        TLogger.log("Create " + NUMOFORDERS + " Orders");
        orderRef[0] = new Order("1", customerRef[0]);
        orderRef[1] = new Order("2", customerRef[1]);
        orderRef[2] = new Order("3", customerRef[2]);
        orderRef[3] = new Order("4", customerRef[3]);
        orderRef[4] = new Order("5", customerRef[4]);
        orderRef[5] = new Order("6", customerRef[5]);
        orderRef[6] = new Order("7", customerRef[6]);
        orderRef[7] = new Order("8", customerRef[7]);
        orderRef[8] = new Order("9", customerRef[3]);
        orderRef[9] = new Order("10", customerRef[8]);
        orderRef[10] = new Order("11", customerRef[9]);
        orderRef[11] = new Order("12", customerRef[10]);
        orderRef[12] = new Order("13", customerRef[11]);
        orderRef[13] = new Order("14", customerRef[12]);
        orderRef[14] = new Order("15", customerRef[13]);
        orderRef[15] = new Order("16", customerRef[13]);
        orderRef[16] = new Order("17", customerRef[14]);
        orderRef[17] = new Order("18", customerRef[15]);
        orderRef[18] = new Order("19");
        orderRef[19] = new Order("20");

           	for (int i=0; i<NUMOFORDERS; i++ ) {
                    getEntityManager().persist(orderRef[i]);
                    TLogger.log("persisted order " + orderRef[i]);
		    doFlush();
		}

        TLogger.log("Create " + NUMOFALIASES + " Aliases");
        aliasRef[0] = new Alias("1", "aef");
        aliasRef[1] = new Alias("2", "al");
        aliasRef[2] = new Alias("3", "fish");
        aliasRef[3] = new Alias("4", "twin");
        aliasRef[4] = new Alias("5", "adf");
        aliasRef[5] = new Alias("6", "art");
        aliasRef[6] = new Alias("7", "sdm");
        aliasRef[7] = new Alias("8", "sh_ll");
        aliasRef[8] = new Alias("9", "reb");
        aliasRef[9] = new Alias("10", "bobby");
        aliasRef[10] = new Alias("11", "bb");
        aliasRef[11] = new Alias("12", "ssd");
        aliasRef[12] = new Alias("13", "steved");
        aliasRef[13] = new Alias("14", "stevie");
        aliasRef[14] = new Alias("15", "");
        aliasRef[15] = new Alias("16", "");
        aliasRef[16] = new Alias("17", "sjc");
        aliasRef[17] = new Alias("18", "stevec");
        aliasRef[18] = new Alias("19", "imc");
        aliasRef[19] = new Alias("20", "iris");
        aliasRef[20] = new Alias("21", "bro");
        aliasRef[21] = new Alias("22", "sis");
        aliasRef[22] = new Alias("23", "kell");
        aliasRef[23] = new Alias("24", "bill");
        aliasRef[24] = new Alias("25", "suzy");
        aliasRef[25] = new Alias("26", "jon");
        aliasRef[26] = new Alias("27", "jk");
        aliasRef[27] = new Alias("28", "kellieann");
        aliasRef[28] = new Alias("29", "smitty");
        aliasRef[29] = new Alias("30", null);

        	for (int i=0; i<NUMOFALIASES; i++ ) {
                    getEntityManager().persist(aliasRef[i]);
                    TLogger.log("persisted alias " + aliasRef[i]);
		    doFlush();
                }


	getEntityTransaction().commit();
        TLogger.log("Done Creating Schema in Persistent Storage");

        }

 	private void createRelationships() throws Exception
        {
        double totalPrice;

        getEntityTransaction().begin();
	TLogger.log("Setting additional relationships for Order 1");
		lineItem[0].setProduct(productRef[0]);
		lineItem[0].setOrder(orderRef[0]);
		getEntityManager().merge(lineItem[0]);

		lineItem[1].setProduct(productRef[1]);
		lineItem[1].setOrder(orderRef[0]);
		getEntityManager().merge(lineItem[1]);

		lineItem[2].setProduct(productRef[7]);
		lineItem[2].setOrder(orderRef[0]);
		getEntityManager().merge(lineItem[2]);

		lineItem[28].setProduct(productRef[8]);
		lineItem[28].setOrder(orderRef[0]);
		getEntityManager().merge(lineItem[28]);

		orderRef[0].getLineItems().add(lineItem[0]);
		orderRef[0].getLineItems().add(lineItem[1]);
		orderRef[0].getLineItems().add(lineItem[2]);
        	orderRef[0].setSampleLineItem(lineItem[28]);
		totalPrice = productRef[0].getPrice() + 
			productRef[1].getPrice() + productRef[7].getPrice() +
			productRef[8].getPrice();
		orderRef[0].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[0]);

		creditCard[1].setOrder(orderRef[0]);
		creditCard[1].setCustomer(customerRef[0]);
		getEntityManager().merge(creditCard[1]);
		doFlush();
	TLogger.log("Done with Order 1 relationships");

	TLogger.log("Setting additional relationships for Order 2");
		lineItem[3].setProduct(productRef[0]);
		lineItem[3].setOrder(orderRef[1]);
		getEntityManager().merge(lineItem[3]);

		lineItem[4].setProduct(productRef[1]);
		lineItem[4].setOrder(orderRef[1]);
		getEntityManager().merge(lineItem[4]);

		lineItem[5].setProduct(productRef[2]);
		lineItem[5].setOrder(orderRef[1]);
		getEntityManager().merge(lineItem[5]);

		lineItem[6].setProduct(productRef[3]);
		lineItem[6].setOrder(orderRef[1]);
		getEntityManager().merge(lineItem[6]);

		lineItem[7].setProduct(productRef[4]);
		lineItem[7].setOrder(orderRef[1]);
		getEntityManager().merge(lineItem[7]);

		orderRef[1].getLineItems().add(lineItem[3]);
		orderRef[1].getLineItems().add(lineItem[4]);
		orderRef[1].getLineItems().add(lineItem[5]);
		orderRef[1].getLineItems().add(lineItem[6]);
		orderRef[1].getLineItems().add(lineItem[7]);
		totalPrice = productRef[0].getPrice() + 
	    		productRef[1].getPrice() + productRef[2].getPrice() +
			productRef[3].getPrice() + productRef[4].getPrice();
		orderRef[1].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[1]);

		creditCard[3].setOrder(orderRef[1]);
		creditCard[3].setCustomer(customerRef[1]);
		getEntityManager().merge(creditCard[3]);
		doFlush();
	TLogger.log("Done Setting relationships for Order 2"); 

	TLogger.log("Setting additional relationships for Order 3");
		lineItem[8].setProduct(productRef[2]);
		lineItem[8].setOrder(orderRef[2]);
		getEntityManager().merge(lineItem[8]);

		lineItem[9].setProduct(productRef[5]);
		lineItem[9].setOrder(orderRef[2]);
		getEntityManager().merge(lineItem[9]);

		orderRef[2].getLineItems().add(lineItem[8]);
		orderRef[2].getLineItems().add(lineItem[9]);
		totalPrice = productRef[2].getPrice() + productRef[5].getPrice();
		orderRef[2].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[2]);

		creditCard[4].setOrder(orderRef[2]);
		creditCard[4].setCustomer(customerRef[2]);
		getEntityManager().merge(creditCard[4]);
		doFlush();
	TLogger.log("Done Setting Relationships for Order 3");

	TLogger.log("Setting additional relationships for Order 4");
		lineItem[10].setProduct(productRef[6]);
		lineItem[10].setOrder(orderRef[3]);
		getEntityManager().merge(lineItem[10]);

		orderRef[3].getLineItems().add(lineItem[10]);
		totalPrice = productRef[6].getPrice();
		orderRef[3].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[3]);

		creditCard[5].setOrder(orderRef[3]);
		creditCard[5].setCustomer(customerRef[3]);
		getEntityManager().merge(creditCard[5]);
		doFlush();
	TLogger.log("Done Setting Relationships for Order 4");

	TLogger.log("Setting additional relationships for Order 5");
		lineItem[11].setProduct(productRef[0]);
		lineItem[11].setOrder(orderRef[4]);
		getEntityManager().merge(lineItem[11]);

		lineItem[12].setProduct(productRef[1]);
		lineItem[12].setOrder(orderRef[4]);
		getEntityManager().merge(lineItem[12]);

		lineItem[13].setProduct(productRef[2]);
		lineItem[13].setOrder(orderRef[4]);
		getEntityManager().merge(lineItem[13]);

		lineItem[14].setProduct(productRef[3]);
		lineItem[14].setOrder(orderRef[4]);
		getEntityManager().merge(lineItem[14]);

		lineItem[15].setProduct(productRef[4]);
		lineItem[15].setOrder(orderRef[4]);
		getEntityManager().merge(lineItem[15]);

		lineItem[16].setProduct(productRef[5]);
		lineItem[16].setOrder(orderRef[4]);
		getEntityManager().merge(lineItem[16]);

		lineItem[17].setProduct(productRef[6]);
		lineItem[17].setOrder(orderRef[4]);
		getEntityManager().merge(lineItem[17]);

		lineItem[18].setProduct(productRef[7]);
		lineItem[18].setOrder(orderRef[4]);
		getEntityManager().merge(lineItem[18]);

		orderRef[4].getLineItems().add(lineItem[11]);
		orderRef[4].getLineItems().add(lineItem[12]);
		orderRef[4].getLineItems().add(lineItem[13]);
		orderRef[4].getLineItems().add(lineItem[14]);
		orderRef[4].getLineItems().add(lineItem[15]);
		orderRef[4].getLineItems().add(lineItem[16]);
		orderRef[4].getLineItems().add(lineItem[17]);
		orderRef[4].getLineItems().add(lineItem[18]);
		totalPrice = productRef[0].getPrice() + 
			productRef[1].getPrice() + productRef[2].getPrice() +
			productRef[3].getPrice() + productRef[4].getPrice() +
			productRef[5].getPrice() + productRef[6].getPrice() +
			productRef[7].getPrice();
		orderRef[4].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[4]);

		creditCard[7].setOrder(orderRef[4]);
		creditCard[7].setCustomer(customerRef[4]);
		getEntityManager().merge(creditCard[7]);
		doFlush();
	TLogger.log("Done Setting Relationships for Order 5");

	TLogger.log("Setting additional relationships for Order 6");
		lineItem[19].setProduct(productRef[3]);
		lineItem[19].setOrder(orderRef[5]);
		getEntityManager().merge(lineItem[19]);

		lineItem[20].setProduct(productRef[6]);
		lineItem[20].setOrder(orderRef[5]);
		getEntityManager().merge(lineItem[20]);

        	lineItem[29].setProduct(productRef[8]);
		lineItem[29].setOrder(orderRef[5]);
		getEntityManager().merge(lineItem[29]);
	
		orderRef[5].getLineItems().add(lineItem[19]);
		orderRef[5].getLineItems().add(lineItem[20]);
		orderRef[5].setSampleLineItem(lineItem[29]);
		totalPrice = productRef[3].getPrice() + productRef[6].getPrice() +
			productRef[8].getPrice();
		orderRef[5].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[5]);

		creditCard[10].setOrder(orderRef[5]);
		creditCard[10].setCustomer(customerRef[5]);
		getEntityManager().merge(creditCard[10]);
		doFlush();
	TLogger.log("Done Setting Relationships for Order 6");

	TLogger.log("Setting additional relationships for Order 7");
		lineItem[21].setProduct(productRef[2]);
		lineItem[21].setOrder(orderRef[6]);
		getEntityManager().merge(lineItem[21]);

		lineItem[22].setProduct(productRef[3]);
		lineItem[22].setOrder(orderRef[6]);
		getEntityManager().merge(lineItem[22]);

		lineItem[23].setProduct(productRef[7]);
		lineItem[23].setOrder(orderRef[6]);
		getEntityManager().merge(lineItem[23]);

		orderRef[6].getLineItems().add(lineItem[21]);
		orderRef[6].getLineItems().add(lineItem[22]);
		orderRef[6].getLineItems().add(lineItem[23]);
		totalPrice = productRef[2].getPrice() + 
			productRef[3].getPrice() + productRef[7].getPrice();
		orderRef[6].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[6]);

		creditCard[11].setOrder(orderRef[6]);
		creditCard[11].setCustomer(customerRef[6]);
		getEntityManager().merge(creditCard[11]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 7");

	TLogger.log("Setting additional relationships for Order 8");
		lineItem[24].setProduct(productRef[0]);
		lineItem[24].setOrder(orderRef[7]);
		getEntityManager().merge(lineItem[24]);

		lineItem[25].setProduct(productRef[4]);
		lineItem[25].setOrder(orderRef[7]);
		getEntityManager().merge(lineItem[25]);

		orderRef[7].getLineItems().add(lineItem[24]);
		orderRef[7].getLineItems().add(lineItem[25]);
		totalPrice = productRef[0].getPrice() + productRef[4].getPrice();
		orderRef[7].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[7]);

		creditCard[13].setOrder(orderRef[7]);
		creditCard[13].setCustomer(customerRef[7]);
		getEntityManager().merge(creditCard[13]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 8");

	TLogger.log("Setting additional relationships for Order 9");
		lineItem[26].setProduct(productRef[0]);
		lineItem[26].setOrder(orderRef[8]);
		getEntityManager().merge(lineItem[26]);

		lineItem[27].setProduct(productRef[1]);
		lineItem[27].setOrder(orderRef[8]);
		getEntityManager().merge(lineItem[27]);

		orderRef[8].getLineItems().add(lineItem[26]);
		orderRef[8].getLineItems().add(lineItem[27]);
		totalPrice = productRef[0].getPrice() + productRef[1].getPrice();
		orderRef[8].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[8]);

		creditCard[6].setOrder(orderRef[8]);
		creditCard[6].setCustomer(customerRef[3]);
		getEntityManager().merge(creditCard[6]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 9");


	TLogger.log("Setting additional relationships for Order 10");
        	lineItem[30].setProduct(productRef[9]);
        	lineItem[30].setOrder(orderRef[9]);
		getEntityManager().merge(lineItem[30]);

        	lineItem[31].setProduct(productRef[16]);
        	lineItem[31].setOrder(orderRef[9]);
		getEntityManager().merge(lineItem[31]);

		orderRef[9].getLineItems().add(lineItem[30]);
		orderRef[9].getLineItems().add(lineItem[31]);
        	totalPrice = productRef[9].getPrice() + productRef[16].getPrice();
        	orderRef[9].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[9]);
	
        	creditCard[14].setOrder(orderRef[9]);
        	creditCard[14].setCustomer(customerRef[8]);
		getEntityManager().merge(creditCard[14]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 10");


	TLogger.log("Setting additional relationships for Order 11");
        	lineItem[32].setProduct(productRef[13]);
        	lineItem[32].setOrder(orderRef[10]);
		getEntityManager().merge(lineItem[32]);

        	orderRef[10].getLineItems().add(lineItem[32]);
        	totalPrice = productRef[13].getPrice();
        	orderRef[10].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[10]);

	        creditCard[15].setOrder(orderRef[10]);
       		creditCard[15].setCustomer(customerRef[9]);
		getEntityManager().merge(creditCard[15]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 11");

	TLogger.log("Setting additional relationships for Order 12");
	        lineItem[33].setProduct(productRef[10]);
       		lineItem[33].setOrder(orderRef[11]);
		getEntityManager().merge(lineItem[33]);

        	lineItem[34].setProduct(productRef[12]);
        	lineItem[34].setOrder(orderRef[11]);
		getEntityManager().merge(lineItem[34]);

		orderRef[11].getLineItems().add(lineItem[33]);
		orderRef[11].getLineItems().add(lineItem[34]);
        	totalPrice = productRef[10].getPrice() + productRef[12].getPrice();
        	orderRef[11].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[11]);

        	creditCard[16].setOrder(orderRef[11]);
        	creditCard[16].setCustomer(customerRef[10]);
		getEntityManager().merge(creditCard[16]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 12");


	TLogger.log("Setting additional relationships for Order 13");
        	lineItem[35].setProduct(productRef[17]);
        	lineItem[35].setOrder(orderRef[12]);
		getEntityManager().merge(lineItem[35]);

        	orderRef[12].getLineItems().add(lineItem[35]);
        	totalPrice = productRef[17].getPrice();
        	orderRef[12].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[12]);
	
        	creditCard[17].setOrder(orderRef[12]);
        	creditCard[17].setCustomer(customerRef[11]);
		getEntityManager().merge(creditCard[17]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 13");


	TLogger.log("Setting additional relationships for Order 14"); 
        	lineItem[36].setProduct(productRef[7]);
        	lineItem[36].setOrder(orderRef[13]); 
		getEntityManager().merge(lineItem[36]);

        	lineItem[37].setProduct(productRef[14]); 
        	lineItem[37].setOrder(orderRef[13]); 
		getEntityManager().merge(lineItem[37]);

        	lineItem[38].setProduct(productRef[15]); 
        	lineItem[38].setOrder(orderRef[13]); 
		getEntityManager().merge(lineItem[38]);

		orderRef[13].getLineItems().add(lineItem[36]);
		orderRef[13].getLineItems().add(lineItem[37]);
		orderRef[13].getLineItems().add(lineItem[38]);
        	totalPrice = productRef[7].getPrice() + productRef[14].getPrice() +
				productRef[15].getPrice(); 
        	orderRef[13].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[13]);

	        creditCard[18].setOrder(orderRef[13]); 
        	creditCard[18].setCustomer(customerRef[12]); 
		getEntityManager().merge(creditCard[18]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 14");  

	TLogger.log("Setting additional relationships for Order 15");  
        	lineItem[39].setProduct(productRef[1]); 
        	lineItem[39].setOrder(orderRef[14]); 
		getEntityManager().merge(lineItem[39]);

        	lineItem[40].setProduct(productRef[2]);  
        	lineItem[40].setOrder(orderRef[14]);  
		getEntityManager().merge(lineItem[40]);

        	lineItem[41].setProduct(productRef[12]);
        	lineItem[41].setOrder(orderRef[14]);
		getEntityManager().merge(lineItem[41]);

        	lineItem[42].setProduct(productRef[15]);
        	lineItem[42].setOrder(orderRef[14]);
		getEntityManager().merge(lineItem[42]);

		orderRef[14].getLineItems().add(lineItem[39]);
		orderRef[14].getLineItems().add(lineItem[40]);
		orderRef[14].getLineItems().add(lineItem[41]);
		orderRef[14].getLineItems().add(lineItem[42]);
        	totalPrice = productRef[1].getPrice() + productRef[2].getPrice() +
                        	productRef[12].getPrice() + productRef[15].getPrice();
        	orderRef[14].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[14]);

        	creditCard[19].setOrder(orderRef[14]);  
        	creditCard[19].setCustomer(customerRef[13]);  
		getEntityManager().merge(creditCard[19]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 15");  


	TLogger.log("Setting additional relationships for Order 16");
       		lineItem[43].setProduct(productRef[13]);
        	lineItem[43].setOrder(orderRef[15]);
		getEntityManager().merge(lineItem[43]);

        	orderRef[15].getLineItems().add(lineItem[43]);
        	totalPrice = productRef[13].getPrice();
        	orderRef[15].setTotalPrice((double)totalPrice);
		getEntityManager().merge(orderRef[15]);

        	creditCard[19].setOrder(orderRef[15]);
        	creditCard[19].setCustomer(customerRef[13]);
		getEntityManager().merge(creditCard[19]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 16");  

        TLogger.log("Setting additional relationships for Order 17");
        	lineItem[44].setProduct(hardwareRef[0]);
        	lineItem[44].setOrder(orderRef[16]);
        	getEntityManager().merge(lineItem[44]);

        	lineItem[45].setProduct(hardwareRef[1]);
        	lineItem[45].setOrder(orderRef[16]);
		getEntityManager().merge(lineItem[45]);

        	lineItem[46].setProduct(softwareRef[0]);
        	lineItem[46].setOrder(orderRef[16]);
		getEntityManager().merge(lineItem[46]);

        	orderRef[16].getLineItems().add(lineItem[44]);
        	orderRef[16].getLineItems().add(lineItem[45]);
        	orderRef[16].getLineItems().add(lineItem[46]);
        	totalPrice = hardwareRef[0].getPrice() + hardwareRef[1].getPrice() +
                        	softwareRef[0].getPrice();
        	orderRef[16].setTotalPrice((double)totalPrice);
        	getEntityManager().merge(orderRef[16]);

        	creditCard[20].setOrder(orderRef[16]);
        	creditCard[20].setCustomer(customerRef[14]);
        	getEntityManager().merge(creditCard[20]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 17");  

        TLogger.log("Setting additional relationships for Order 18");
        	lineItem[47].setProduct(hardwareRef[2]);
        	lineItem[47].setOrder(orderRef[17]);
        	getEntityManager().merge(lineItem[47]);

        	lineItem[48].setProduct(softwareRef[1]);
        	lineItem[48].setOrder(orderRef[17]);
        	getEntityManager().merge(lineItem[48]);

        	lineItem[49].setProduct(hardwareRef[3]);
        	lineItem[49].setOrder(orderRef[17]);
        	getEntityManager().merge(lineItem[49]);

        	lineItem[50].setProduct(softwareRef[2]);
        	lineItem[50].setOrder(orderRef[17]);
        	getEntityManager().merge(lineItem[50]);

        	orderRef[17].getLineItems().add(lineItem[47]);
        	orderRef[17].getLineItems().add(lineItem[48]);
        	orderRef[17].getLineItems().add(lineItem[49]);
        	orderRef[17].getLineItems().add(lineItem[50]);
        	totalPrice = hardwareRef[2].getPrice() + hardwareRef[3].getPrice() +
                        	softwareRef[1].getPrice() + softwareRef[2].getPrice();
        	orderRef[17].setTotalPrice((double)totalPrice);
        	getEntityManager().merge(orderRef[17]);

        	creditCard[21].setOrder(orderRef[17]);
        	creditCard[21].setCustomer(customerRef[15]);
        	getEntityManager().merge(creditCard[21]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 18");  


        TLogger.log("Setting additional relationships for Order 19");
        	lineItem[51].setProduct(hardwareRef[4]);
        	lineItem[51].setOrder(orderRef[18]);
        	getEntityManager().merge(lineItem[51]);

        	lineItem[52].setProduct(softwareRef[3]);
        	lineItem[52].setOrder(orderRef[18]);
        	getEntityManager().merge(lineItem[52]);

        	lineItem[53].setProduct(softwareRef[4]);
        	lineItem[53].setOrder(orderRef[18]);
        	getEntityManager().merge(lineItem[53]);

        	orderRef[18].getLineItems().add(lineItem[51]);
        	orderRef[18].getLineItems().add(lineItem[52]);
        	orderRef[18].getLineItems().add(lineItem[53]);
        	totalPrice = hardwareRef[4].getPrice() + softwareRef[3].getPrice() +
			 	softwareRef[4].getPrice();
        	orderRef[18].setTotalPrice((double)totalPrice);
        	getEntityManager().merge(orderRef[18]);

        	creditCard[22].setOrder(orderRef[18]);
        	creditCard[22].setCustomer(customerRef[16]);
        	getEntityManager().merge(creditCard[22]);
		doFlush();
	TLogger.log("Done Setting additional relationships for Order 19");  

        TLogger.log("Setting additional relationships for Order 20");
        	lineItem[54].setProduct(hardwareRef[5]);
        	lineItem[54].setOrder(orderRef[19]);
        	getEntityManager().merge(lineItem[54]);

        	lineItem[55].setProduct(softwareRef[5]);
        	lineItem[55].setOrder(orderRef[19]);
        	getEntityManager().merge(lineItem[55]);

        	orderRef[19].getLineItems().add(lineItem[54]);
        	orderRef[19].getLineItems().add(lineItem[55]);
        	totalPrice = hardwareRef[5].getPrice() + softwareRef[5].getPrice();
        	orderRef[19].setTotalPrice((double)totalPrice);
        	getEntityManager().merge(orderRef[19]);

        	creditCard[23].setOrder(orderRef[19]);
        	creditCard[23].setCustomer(customerRef[17]);
        	getEntityManager().merge(creditCard[23]);
		doFlush();
        TLogger.log("Done Setting additional relationships for Order 20");


	TLogger.log("Setting additional relationships for Customer 1");
        	aliasRef[0].getCustomers().add(customerRef[0]);
	        getEntityManager().merge(aliasRef[0]); 

        	aliasRef[1].getCustomers().add(customerRef[0]);
	        getEntityManager().merge(aliasRef[1]); 

        	aliasRef[2].getCustomers().add(customerRef[0]);
	        getEntityManager().merge(aliasRef[2]); 

        	aliasRef[3].getCustomers().add(customerRef[0]);
	        getEntityManager().merge(aliasRef[3]); 

		orderRef[0].setCustomer(customerRef[0]);
		getEntityManager().merge(orderRef[0]);

		creditCard[0].setCustomer(customerRef[0]);
		getEntityManager().merge(creditCard[0]);

		creditCard[1].setCustomer(customerRef[0]);
		getEntityManager().merge(creditCard[1]);

		creditCard[2].setCustomer(customerRef[0]);
		getEntityManager().merge(creditCard[2]);
		doFlush();

	TLogger.log("Setting additional relationships for Customer 2");
        	aliasRef[2].getCustomers().add(customerRef[1]);
		getEntityManager().merge(aliasRef[2]);

        	aliasRef[3].getCustomers().add(customerRef[1]);
		getEntityManager().merge(aliasRef[3]);

        	aliasRef[4].getCustomers().add(customerRef[1]);
		getEntityManager().merge(aliasRef[4]);

        	aliasRef[5].getCustomers().add(customerRef[1]);
		getEntityManager().merge(aliasRef[5]);

		orderRef[1].setCustomer(customerRef[1]);
	        getEntityManager().merge(orderRef[1]);

		creditCard[3].setCustomer(customerRef[1]);
	        getEntityManager().merge(creditCard[3]);
		doFlush();

	TLogger.log("Setting additional relationships for Customer 3");
        	aliasRef[6].getCustomers().add(customerRef[2]);
	 	getEntityManager().merge(aliasRef[6]);

        	aliasRef[7].getCustomers().add(customerRef[2]);
	 	getEntityManager().merge(aliasRef[7]);

		orderRef[2].setCustomer(customerRef[2]);
		getEntityManager().merge(orderRef[2]);

		creditCard[4].setCustomer(customerRef[2]);
		getEntityManager().merge(creditCard[4]);
		doFlush();

	TLogger.log("Setting additional relationships for Customer 4");
        	aliasRef[8].getCustomers().add(customerRef[3]);
		getEntityManager().merge(aliasRef[8]);

        	aliasRef[9].getCustomers().add(customerRef[3]);
		getEntityManager().merge(aliasRef[9]);

        	aliasRef[10].getCustomers().add(customerRef[3]);
		getEntityManager().merge(aliasRef[10]);

		orderRef[3].setCustomer(customerRef[3]);
		getEntityManager().merge(orderRef[3]);

		creditCard[5].setCustomer(customerRef[3]);
		getEntityManager().merge(creditCard[5]);

		creditCard[6].setCustomer(customerRef[3]);
		getEntityManager().merge(creditCard[6]);
		doFlush();

	TLogger.log("Setting additional relationships for Customer 5");
        	aliasRef[11].getCustomers().add(customerRef[4]);
		getEntityManager().merge(aliasRef[11]);

        	aliasRef[12].getCustomers().add(customerRef[4]);
		getEntityManager().merge(aliasRef[12]);

        	aliasRef[13].getCustomers().add(customerRef[4]);
		getEntityManager().merge(aliasRef[13]);

		orderRef[4].setCustomer(customerRef[4]);
		getEntityManager().merge(orderRef[4]);

		creditCard[7].setCustomer(customerRef[4]);
		getEntityManager().merge(creditCard[7]);

		creditCard[8].setCustomer(customerRef[4]);
		getEntityManager().merge(creditCard[8]);
		doFlush();

	TLogger.log("Setting additional relationships for Customer 6");
		// aliasRef[14] - Not Set
        	// aliasRef[15] - Not Set
		orderRef[5].setCustomer(customerRef[5]);
		getEntityManager().merge(orderRef[5]);

		creditCard[9].setCustomer(customerRef[5]);
		getEntityManager().merge(creditCard[9]);

		creditCard[10].setCustomer(customerRef[5]);
		getEntityManager().merge(creditCard[10]);
		doFlush();

	TLogger.log("Setting additional relationships for Customer 7");
        	aliasRef[13].getCustomers().add(customerRef[6]);
		getEntityManager().merge(aliasRef[13]);

        	aliasRef[16].getCustomers().add(customerRef[6]);
		getEntityManager().merge(aliasRef[16]);

        	aliasRef[17].getCustomers().add(customerRef[6]);
		getEntityManager().merge(aliasRef[17]);

		orderRef[6].setCustomer(customerRef[6]);
		getEntityManager().merge(orderRef[6]);

		creditCard[11].setCustomer(customerRef[6]);
		getEntityManager().merge(creditCard[11]);
		doFlush();
	TLogger.log("Setting additional relationships for Customer 8");
		aliasRef[18].getCustomers().add(customerRef[7]);
		getEntityManager().merge(aliasRef[18]);

        	aliasRef[19].getCustomers().add(customerRef[7]);
		getEntityManager().merge(aliasRef[19]);

		orderRef[7].setCustomer(customerRef[7]);
		getEntityManager().merge(orderRef[7]);

		creditCard[12].setCustomer(customerRef[7]);
		getEntityManager().merge(creditCard[12]);

		creditCard[13].setCustomer(customerRef[7]);
		getEntityManager().merge(creditCard[13]);
		doFlush();
	TLogger.log("Setting additional relationships for Customer 9");
        	aliasRef[23].getCustomers().add(customerRef[8]);
		getEntityManager().merge(aliasRef[23]);

        	orderRef[9].setCustomer(customerRef[8]);
		getEntityManager().merge(orderRef[9]);

        	creditCard[14].setCustomer(customerRef[8]);
		getEntityManager().merge(creditCard[14]);

		doFlush();
	TLogger.log("Setting additional relationships for Customer 10");
        	aliasRef[21].getCustomers().add(customerRef[9]);
		getEntityManager().merge(aliasRef[21]);

        	aliasRef[29].getCustomers().add(customerRef[9]);
		getEntityManager().merge(aliasRef[29]);

		orderRef[10].setCustomer(customerRef[9]);
		getEntityManager().merge(orderRef[10]);

		creditCard[15].setCustomer(customerRef[9]);
		getEntityManager().merge(creditCard[15]);
		doFlush();
	TLogger.log("Setting additional relationships for Customer 11");
        	aliasRef[25].getCustomers().add(customerRef[10]);
		getEntityManager().merge(aliasRef[25]);

        	aliasRef[26].getCustomers().add(customerRef[10]);
		getEntityManager().merge(aliasRef[26]);

        	aliasRef[28].getCustomers().add(customerRef[10]);
		getEntityManager().merge(aliasRef[28]);

        	orderRef[11].setCustomer(customerRef[10]);
		getEntityManager().merge(orderRef[11]);

        	creditCard[16].setCustomer(customerRef[10]);
		getEntityManager().merge(creditCard[16]);
		doFlush();
	TLogger.log("Setting additional relationships for Customer 12");
        	aliasRef[24].getCustomers().add(customerRef[11]);
		getEntityManager().merge(aliasRef[24]);

        	orderRef[12].setCustomer(customerRef[11]);
		getEntityManager().merge(orderRef[12]);

        	creditCard[17].setCustomer(customerRef[11]);
		getEntityManager().merge(creditCard[17]);
		doFlush();
	TLogger.log("Setting additional relationships for Customer 13");
        	aliasRef[20].getCustomers().add(customerRef[12]);
		getEntityManager().merge(aliasRef[20]);

        	orderRef[13].setCustomer(customerRef[12]);
		getEntityManager().merge(orderRef[13]);

        	creditCard[18].setCustomer(customerRef[12]);
		getEntityManager().merge(creditCard[18]);

		doFlush();
	TLogger.log("Setting additional relationships for Customer 14");
        	aliasRef[22].getCustomers().add(customerRef[13]);
		getEntityManager().merge(aliasRef[22]);

        	aliasRef[27].getCustomers().add(customerRef[13]);
		getEntityManager().merge(aliasRef[27]);

		orderRef[14].setCustomer(customerRef[13]);
		getEntityManager().merge(orderRef[14]);

		orderRef[15].setCustomer(customerRef[13]);
		getEntityManager().merge(orderRef[15]);

		creditCard[19].setCustomer(customerRef[13]);
		getEntityManager().merge(creditCard[19]);
		doFlush();
	TLogger.log("Setting additional relationships for Customer 15");
		// No Aliases
		orderRef[16].setCustomer(customerRef[14]);
		getEntityManager().merge(orderRef[16]);

		creditCard[20].setCustomer(customerRef[14]);
		getEntityManager().merge(creditCard[20]);
		doFlush();
	TLogger.log("Setting additional relationships for Customer 16");
		// No Aliases
		orderRef[17].setCustomer(customerRef[15]);
		getEntityManager().merge(orderRef[17]);
	
		creditCard[21].setCustomer(customerRef[15]);
		getEntityManager().merge(creditCard[21]);
		doFlush();
	TLogger.log("Setting additional relationships for Customer 17");
		// No Aliases
		orderRef[18].setCustomer(customerRef[16]);
		getEntityManager().merge(orderRef[18]);

		creditCard[22].setCustomer(customerRef[16]);
		getEntityManager().merge(creditCard[22]);
		doFlush();
	TLogger.log("Setting additional relationships for Customer 18");
		// No Aliases
		orderRef[19].setCustomer(customerRef[17]);
		getEntityManager().merge(orderRef[19]);

		creditCard[23].setCustomer(customerRef[17]);
		getEntityManager().merge(creditCard[23]);
		doFlush();
	TLogger.log("Done with createRelationships");

	getEntityTransaction().commit();
    }


}
