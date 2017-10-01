 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.18 06/07/27
 */

package com.sun.ts.tests.ejb30.persistence.query.apitests;

import javax.naming.InitialContext;
import java.util.*;
import com.sun.javatest.Status;
import com.sun.ts.lib.util.*;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import java.sql.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Client extends PMClientBase {

    private static Employee empRef[] = new Employee[20];
    private static Department deptRef[] = new Department[10];
    private static Insurance insRef[] = new Insurance[5];
    private static Date d1 = getHireDate(2000, 2, 14);
    private static Date d2 = getHireDate(2001, 6, 27);
    private static Date d3 = getHireDate(2002, 7, 7);
    private static Date d4 = getHireDate(2003, 3, 3);
    private static Date d5 = getHireDate(2004, 4, 10);
    private static Date d6 = getHireDate(2005, 2, 18);
    private static Date d7 = getHireDate(2000, 9, 17);
    private static Date d8 = getHireDate(2001, 11, 14);
    private static Date d9 = getHireDate(2002, 10, 4);
    private static Date d10 = getHireDate(2003, 1, 25);

    public Client() {
    }
    
    public static void main(String[] args) {
        Client theTests = new Client();
        Status s=theTests.run(args, System.out, System.err);
        s.exit();
    }
    
  public void setup(String[] args, Properties p) throws Fault
    {
        TLogger.log("Setup");
        try {
	    super.setup(args, p);

            TLogger.log("Create Test Data");
            createTestData();
            TLogger.log("Done creating test data");

     }  catch (Exception e) {
            TLogger.log("Unexpected Exception caught in Setup: " + e.getMessage());
            e.printStackTrace();
            throw new Fault("Setup failed:", e);

        }
    }


    /* 
     *  BEGIN Test Cases
     */


    /*
     * @testName: queryAPITest1
     * @assertion_ids:  PERSISTENCE:SPEC:728; PERSISTENCE:SPEC:400;
     *			PERSISTENCE:SPEC:404
     * @test_Strategy: Verify results of setFirstResult using JOIN in the FROM
     *  		clause projecting on state_field in the select clause.
     *			Verify that number of rows skipped are 1-1 with specified 
     *			value for setFirstResult.
     *
     *			The elements of a query result whose SELECT clause consists
     *			of more than one value are of type Object[]. 
     */

    public void queryAPITest1() throws Fault
    {
      List q = null;
      boolean pass1 = true;
      boolean pass2 = false;
      Object[][] expectedResultSet = new Object[][]{ new Object[]{"Marketing","Nicole"},
                                                new Object[]{"Marketing", "Stephen"},
                                                new Object[]{"Sales", "Cheng"},
                                                new Object[]{"Sales", "Irene"},
                                                new Object[]{"Sales", "Mark"},
                                                new Object[]{"Sales", "Shelly"},
                                                new Object[]{"Training", "Kate"},
                                                new Object[]{"Training", "Katy"},
                                                new Object[]{"Training", "Stephen"},
                                                new Object[]{"Training", "Steven"} };

      try {
	  getEntityTransaction().begin();
          TLogger.log("Invoking query for queryAPITest1" );
          q = getEntityManager().createQuery(
	  "select d.name, e.firstName from Department d join d.employees e where d.id <= 5 "
		+ " order by d.name, e.firstName")
 	  .setFirstResult(10)
          .getResultList();

	  if ( q.size()  != 10 ) {
		TLogger.log("queryAPITest1:  Did not get expected results.  Expected: 10, "
			+ "got: " + q.size() );
	   	pass1 = false;
	   } else if (pass1) {
             TLogger.log("Expected size received, verify contents . . . ");
	     //each element of the list q should be a size-2 array,
             //for instance ["Marketing","Nicole"]
             for(int i = 0; i < q.size(); i++) {
                Object obj = q.get(i);
                Object[] departmentAndNameExpected = expectedResultSet[i];
                Object[] departmentAndName = null;
                if(obj instanceof Object[]) {
                  TLogger.log("The element in the result list is of type Object[], continue . . .");
                    //good, this element of type Object[]
                    departmentAndName = (Object[]) obj;
                    TLogger.log("The element in the result list is of type Object[], continue . . .");
                    pass2 = Arrays.equals(departmentAndNameExpected, departmentAndName);
                    if(!pass2) {
                        TLogger.log("ERROR: Expecting element value: "
			 + Arrays.asList(departmentAndNameExpected)
			 + ", actual element value: "
			 + Arrays.asList(departmentAndName));
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
	pass2 = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass2)
            throw new Fault( "queryAPITest1 failed");
    }

    /*
     * @testName: queryAPITest2
     * @assertion_ids:  PERSISTENCE:SPEC:731; PERSISTENCE:JAVADOC:179
     * @test_Strategy: setParameter(int position, Object value) which
     *			sets a positional parameter which is not used in the
     *			query string. An IllegalArgumentException should be thrown.
     */

    public void queryAPITest2() throws Fault
    {
      List result = null;
      boolean pass = true;

      try {
	  getEntityTransaction().begin();
          TLogger.log("Invoking query for queryAPITest2" );
          result = getEntityManager().createQuery (
                        "select e from Employee e where e.firstName = ?1")
                        .setParameter(1, "Kellie" )
                        .setParameter(2, "Lee" )
                        .getResultList();

        getEntityTransaction().commit();

      } catch (IllegalArgumentException iae) {
          TLogger.log("IllegalArgumentException Caught as Expected" );
          pass = true;
      } catch (Exception e) {
          pass = false;
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass)
        throw new Fault( "queryAPITest2 failed");

    }

    /*
     * @testName: queryAPITest3
     * @assertion_ids: PERSISTENCE:JAVADOC:172
     * @test_Strategy:  If the select clause selects an object, then the number of
     *			rows skipped with setFirstResult will correspond to the
     * 			number of objects specified by setFirstResult."
     */

    public void queryAPITest3() throws Fault
    {
      TLogger.log("Begin queryAPITest3");
      List q = null;
      boolean pass1 = true;
      boolean pass2 = true;
      int found = 0;
      Integer expectedResult[] = new Integer[] {2, 3, 3, 4, 4, 5};

      try {
	  getEntityTransaction().begin();
          TLogger.log("Invoking query for queryAPITest3" );
          q = getEntityManager().createQuery (
	  "select e.department from Employee e where e.id < 10 order by e.department.id")
          .setFirstResult(3)
          .getResultList();

          TLogger.log("Query returned " + q.size() + " results.");

	  if ( q.size()  != 6 ) {
		pass1 = false;
             }  else {
                  if (pass1) {
                  Iterator i = q.iterator();
                  while (i.hasNext()) {
                        TLogger.log("Check contents of result set");
                        Department d = (Department) i.next();
                        for(int l=0; l<6; l++) {
                        if (expectedResult[l].equals(new Integer(d.getId()))) {
                          TLogger.log("Found department of: " +d.getId());
                          found++;
                          break;
                          }
                        }
                   }
                 }
              }

           if ( found != 6 ) {
               TLogger.log("ERROR: Query did not return expected results");
               pass2 = false;
           } else {
             TLogger.log("Successfully returned expected results");
           }

        getEntityTransaction().commit();

      } catch (Exception e) {
	pass1 = false;
	pass2 = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass1 || !pass2 )
            throw new Fault( "queryAPITest3 failed");
    }

    /*
     * @testName: queryAPITest4
     * @assertion_ids: PERSISTENCE:SPEC:744
     * @test_Strategy: getSingleResult() is expected to return a single result.  If
     *			 the query does not return a result, an NoResultException
     *			 is thrown.
     */

    public void queryAPITest4() throws Fault
    {
      Object result = null;
      boolean pass = false;

      try {
	  getEntityTransaction().begin();
          TLogger.log("Invoking query for queryAPITest4" );
          result = getEntityManager().createQuery (
          		"select d.name from Department d where d.id = 20")
          		.getSingleResult();

        getEntityTransaction().commit();
      } catch (NoResultException nre) {
        TLogger.log("Exception Caught as Expected:" + nre );
	pass = true;
      } catch (Exception e) {
        pass = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass)
        throw new Fault( "queryAPITest4 failed");

    }


    /*
     * @testName: queryAPITest5
     * @assertion_ids: PERSISTENCE:SPEC:745; PERSISTENCE:JAVADOC:126
     * @test_Strategy: getSingleResult() is expected to return a single result.  If
     *			 the query returns more than one result, a
     *			 NonUniqueResultException is thrown.
     */

    public void queryAPITest5() throws Fault
    {
      Object result = null;
      boolean pass = false;

      try {
          TLogger.log("Invoking query for queryAPITest5" );
	  getEntityTransaction().begin();
          result = getEntityManager().createQuery (
          		"select d.name from Department d where d.id > 1")
          		.getSingleResult();

        getEntityTransaction().commit();
      } catch (NonUniqueResultException nure) {
          TLogger.log("Exception Caught as Expected:" + nure );
          pass = true;
     } catch (Exception e) {
        pass = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass)
            throw new Fault( "queryAPITest5 failed");
    }

    /*
     * @testName: queryAPITest6
     * @assertion_ids: PERSISTENCE:JAVADOC:180
     * @test_Strategy: setMaxResult() is expected to throw an
     *                  IllegalArgumentException if argument is negative.
     *
     */

    public void queryAPITest6() throws Fault
    {
      List result = null;
      boolean pass = false;

      try {
	  getEntityTransaction().begin();
          TLogger.log("Invoking query for queryAPITest6" );
          result = getEntityManager().createQuery (
          		"select d from Department d")
	  		.setMaxResults(-5)
          		.getResultList();

         getEntityTransaction().commit();

      } catch (IllegalArgumentException iae) {
          TLogger.log("IllegalArgumentException Caught as Expected" );
          pass = true;
      } catch (Exception e) {
        pass = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass)
            throw new Fault( "queryAPITest6 failed");
    }


    /*
     * @testName: queryAPITest8
     * @assertion_ids: PERSISTENCE:SPEC:732; PERSISTENCE:JAVADOC:176
     * @test_Strategy: setParameter(String name, Object value) containing an
     *			argument of an incorrect type should throw an
     *			IllegalArgumentException.
     */

    public void queryAPITest8() throws Fault {
        List result = null;
        boolean pass = false;
        
        try {
            TLogger.log("Invoking query for queryAPITest8" );
            getEntityTransaction().begin();
            Query query = null;
            try {
                query = getEntityManager().createQuery(
                        "select e from Employee e where e.firstName = :fName")
                        .setParameter("fName", new Float(5.0));
            } catch (IllegalArgumentException e) {
                TLogger.log("IllegalArgumentException Caught as Expected" );
                pass = true;
            }
            if(!pass) {
                try {
                    result = query.getResultList();
                } catch(RuntimeException e) {
                    TLogger.log("Didn't get expected IllegalArgumentException when " +
                            "setting an invalid parameter on a query, but got " +
                            "expected RuntimeException when executing the query: " + e);
                    pass = true;
                }
            }
        } catch (Exception e) {
            pass = false;
            TLogger.log("Unexpection Exception :" + e );
            e.printStackTrace();
        } finally {
            try {
                if ( getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
            } catch (Exception re) {
                TLogger.log("Unexpection Exception in while rolling back TX:" + re );
                re.printStackTrace();
            }
        }
        
        if (!pass)
            throw new Fault( "queryAPITest8 failed");
    }

    /*
     * @testName: queryAPITest9
     * @assertion_ids: PERSISTENCE:SPEC:730; PERSISTENCE:JAVADOC:177
     * @test_Strategy: setParameter(String name, Date value, TemporalType type) containing a
     *			parameter name that does not correspond to parameter in
     *			query string should throw an IllegalArgumentException.
     */

    public void queryAPITest9() throws Fault {
        List result = null;
        boolean pass = false;
        
        try {
            TLogger.log("Invoking query for queryAPITest9" );
            getEntityTransaction().begin();
            Query query = null;
            try {
                query = getEntityManager().createQuery(
                        "select d from Department d where :param > 1")
                        .setParameter("badName", d1, TemporalType.DATE);
            } catch (IllegalArgumentException iae) {
                TLogger.log("IllegalArgumentException Caught as Expected" );
                pass = true;
            }
            if(!pass) {
                try {
                    result = query.getResultList();
                } catch(RuntimeException e) {
                    TLogger.log("Didn't get expected IllegalArgumentException when " +
                            "setting an invalid parameter on a query, but got " +
                            "expected RuntimeException when executing the query: " + e);
                    pass = true;
                }
            }
        } catch (Exception e) {
            pass = false;
            TLogger.log("Unexpection Exception :" + e );
            e.printStackTrace();
        } finally {
            try {
                if ( getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
            } catch (Exception re) {
                TLogger.log("Unexpection Exception in while rolling back TX:" + re );
                re.printStackTrace();
            }
        }
        
        if (!pass)
            throw new Fault( "queryAPITest9 failed");
    }

    /*
     * @testName: queryAPITest10
     * @assertion_ids: PERSISTENCE:SPEC:730; PERSISTENCE:JAVADOC:178
     * @test_Strategy: setParameter(String name, Calendar value, TemporalType type) containing a
     *			parameter name that does not correspond to parameter in
     *			query string should throw an IllegalArgumentException.
     */

    public void queryAPITest10() throws Fault {
        List result = null;
        java.util.Calendar c = Calendar.getInstance();
        boolean pass = false;
        
        try {
            TLogger.log("Invoking query for queryAPITest10" );
            getEntityTransaction().begin();
            Query query = null;
            try {
                query = getEntityManager().createQuery(
                        "select d from Department d where :param > 1")
                        .setParameter("badName", c, TemporalType.TIMESTAMP);
            } catch (IllegalArgumentException iae) {
                TLogger.log("IllegalArgumentException Caught as Expected" );
                pass = true;
            }
            if(!pass) {
                try {
                    result = query.getResultList();
                } catch(RuntimeException e) {
                    TLogger.log("Didn't get expected IllegalArgumentException when " +
                            "setting an invalid parameter on a query, but got " +
                            "expected RuntimeException when executing the query: " + e);
                    pass = true;
                }
            }
        } catch (Exception e) {
            pass = false;
            TLogger.log("Unexpection Exception :" + e );
            e.printStackTrace();
        } finally {
            try {
                if ( getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
            } catch (Exception re) {
                TLogger.log("Unexpection Exception in while rolling back TX:" + re );
                re.printStackTrace();
            }
        }
        
        if (!pass)
            throw new Fault( "queryAPITest10 failed");
    }

    /*
     * @testName: queryAPITest11
     * @assertion_ids: PERSISTENCE:SPEC:729; PERSISTENCE:SPEC:731
     * @test_Strategy: setParameter(int position, Object value) which 
     *                  has a positional parameter value specified that does
     *			not correspond to a positional parameter in the query
     *			string. An IllegalArgumentException is thrown.
     */

    public void queryAPITest11() throws Fault {
        List result = null;
        boolean pass = false;
        
        try {
            TLogger.log("Invoking query for queryAPITest11" );
            getEntityTransaction().begin();
            Query query = null;
            try {
                query = getEntityManager().createQuery(
                        "select e from Employee e where e.firstName = ?1 and e.lastName = ?3")
                        .setParameter(1, "Kellie" )
                        .setParameter(2, "Lee");
            } catch (IllegalArgumentException iae) {
                TLogger.log("IllegalArgumentException Caught as Expected" );
                pass = true;
            }
            if(!pass) {
                try {
                    result = query.getResultList();
                } catch(RuntimeException e) {
                    TLogger.log("Didn't get expected IllegalArgumentException when " +
                            "setting an invalid parameter on a query, but got " +
                            "expected RuntimeException when executing the query: " + e);
                    pass = true;
                }
            }
        } catch (Exception e) {
            pass = false;
            TLogger.log("Unexpection Exception :" + e );
            e.printStackTrace();
        } finally {
            try {
                if ( getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
            } catch (Exception re) {
                TLogger.log("Unexpection Exception in while rolling back TX:" + re );
                re.printStackTrace();
            }
        }
        
        if (!pass)
            throw new Fault( "queryAPITest11 failed");
    }

    /*
     * @testName: queryAPITest12
     * @assertion_ids:  PERSISTENCE:SPEC:729; PERSISTENCE:SPEC:731
     * @test_Strategy: setParameter(int position, Object value) which 
     *                  defines a value of the incorrect type
     *			should throw an IllegalArgumentException.
     */

    public void queryAPITest12() throws Fault {
        List result = null;
        boolean pass = false;
        
        try {
            getEntityTransaction().begin();
            Query query = null;
            TLogger.log("Invoke query for queryAPITest12 ..." );
            try {
                query = getEntityManager().createQuery(
                        "select e from Employee e where e.firstName = ?1 and e.lastName = ?2")
                        .setParameter(1, "Kate" )
                        .setParameter(2, new Integer(10));
            } catch (IllegalArgumentException iae) {
                TLogger.log("IllegalArgumentException Caught as Expected" );
                pass = true;
            }
            if(!pass) {
                try {
                    result = query.getResultList();
                } catch(RuntimeException e) {
                    TLogger.log("Didn't get expected IllegalArgumentException when " +
                            "setting an invalid parameter on a query, but got " +
                            "expected RuntimeException when executing the query: " + e);
                    pass = true;
                }
            }
        } catch (Exception e) {
            pass = false;
            TLogger.log("Unexpection Exception :" + e );
            e.printStackTrace();
        } finally {
            try {
                if ( getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
            } catch (Exception re) {
                TLogger.log("Unexpection Exception in while rolling back TX:" + re );
                re.printStackTrace();
            }
        }
        
        if (!pass)
            throw new Fault( "queryAPITest12 failed");
    }

    /*
     * @testName: queryAPITest13
     * @assertion_ids:  PERSISTENCE:JAVADOC:172
     * @test_Strategy: setFirstResult(int startPosition) with 
     *                  a negative value for startPosition should throw
     *                  an IllegalArgumentException.
     */

    public void queryAPITest13() throws Fault
    {
      List result = null;
      boolean pass = false;

      try {
          TLogger.log("Invoke query for queryAPITest13" );
	  getEntityTransaction().begin();
          result = getEntityManager().createQuery (
                        "select d from Department d")
                        .setFirstResult(-3)
                        .getResultList();

        getEntityTransaction().commit();
      } catch (IllegalArgumentException iae) {
          TLogger.log("IllegalArgumentException Caught as Expected" );
          pass = true;
     } catch (Exception e) {
        pass = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass)
            throw new Fault( "queryAPITest13 failed");
    }

    /*
     * @testName: queryAPITest14
     * @assertion_ids:  PERSISTENCE:JAVADOC:175
     * @test_Strategy: Using setMaxResult() set the maximum number of
     *          	results to a value which exceeds number of expected
     *			 result and verify the result set.
     */

    public void queryAPITest14() throws Fault
    {
      List q = null;
      boolean pass1 = true;
      boolean pass2 = true;
      int found = 0;
      Integer expectedResult[] = new Integer[] {1, 2, 3, 4, 5};

      try {
          TLogger.log("Invoking query for queryAPITest14" );
	  getEntityTransaction().begin();
          q = getEntityManager().createQuery (
                        "select d from Department d order by d.id")
                        .setMaxResults(15)
                        .getResultList();

	      if (q.size() != 5) {
                TLogger.log("query returned size of " + q.size() + ", expected 5");
                pass1 = false;
              } else {
                  if (pass1) {
                  Iterator i = q.iterator();
                  while (i.hasNext()) {
                        TLogger.log("Check contents of result set");
			Department d = (Department) i.next();
                        for(int l=0; l<5; l++) {
                        if (expectedResult[l].equals(new Integer(d.getId()))) {
			  TLogger.log("Found department of: " +d.getId());
                          found++;
                          break;
                          }
                        }
                   }
                 }
              }

 	   if ( found != 5 ) {
               TLogger.log("ERROR: Query did not return expected results");
               pass2 = false;
           } else {
             TLogger.log("Successfully returned expected results");
           }
        getEntityTransaction().commit();
      } catch (Exception e) {
	  pass1 = false;
	  pass2 = false;
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass1 || !pass2)
            throw new Fault( "queryAPITest14 failed");
    }

    /*
     * @testName: queryAPITest15
     * @assertion_ids:  PERSISTENCE:JAVADOC:175
     * @test_Strategy: Using setMaxResult() set the maximum number of
     *          	results to a value which is less than that of the expected
     *			results and verify the result set returned is only contains
     *			the number of results requested to be retrieved.
     */

    public void queryAPITest15() throws Fault
    {
      List q = null;
      boolean pass1 = true;
      boolean pass2 = true;
      int found = 0;
      Integer expectedResult[] = new Integer[] {4, 1};

      try {
          TLogger.log("Invoking query for queryAPITest15" );
	  getEntityTransaction().begin();
          q = getEntityManager().createQuery (
                        "select d from Department d order by d.name")
                        .setMaxResults(2)
                        .getResultList();

              if (q.size() != 2) {
                TLogger.log("query returned size of " + q.size() + ", expected 2");
                pass1 = false;
              } else {
                  if (pass1) {
                  Iterator i = q.iterator();
                  while (i.hasNext()) {
                        TLogger.log("Check contents of result set");
                        Department d = (Department) i.next();
                        for(int l=0; l<2; l++) {
                        if (expectedResult[l].equals(new Integer(d.getId()))) {
                          TLogger.log("Found department of: " +d.getId());
                          found++;
                          break;
                          }
                        }
                   }
                 }
              }

           if ( found != 2 ) {
               TLogger.log("ERROR: Query did not return expected results");
               pass2 = false;
           } else {
             TLogger.log("Successfully returned expected results");
           }
        getEntityTransaction().commit();
      } catch (Exception e) {
	  pass1 = false;
	  pass2 = false;
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }


      if (!pass1 || !pass2)
            throw new Fault( "queryAPITest15 failed");
    }

    /*
     * @testName: queryAPITest16
     * @assertion_ids:  PERSISTENCE:JAVADOC:170
     * @test_Strategy: getResultList() should throw an IllegalStateException
     *			if called for an EJB QL Update statement.
     */

    public void queryAPITest16() throws Fault
    {
      Query q = null;
      List result = null;
      boolean pass = false;

      try {
          TLogger.log("Invoking query for queryAPITest16" );
	  getEntityTransaction().begin();
          q = getEntityManager().createQuery (
                        "UPDATE Employee e SET e.salary = e.salary * 10.0 where e.salary > :minsal")
		.setParameter("minsal", (float)50000.0);
          result = q.getResultList();

        getEntityTransaction().commit();
      } catch (IllegalStateException ise) {
          TLogger.log("IllegalStateException Caught as Expected" );
          pass = true;
     } catch (Exception e) {
        pass = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
     } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
    }

    if (!pass)
            throw new Fault( "queryAPITest16 failed");
    }

    /*
     * @testName: queryAPITest17
     * @assertion_ids:  PERSISTENCE:JAVADOC:170
     * @test_Strategy: getResultList() should throw an IllegalStateException
     *			if called for an EJB QL Delete statement.
     */

    public void queryAPITest17() throws Fault
    {
      Query q = null;
      List result = null;
      boolean pass = false;

      try {
          TLogger.log("Invoking query for queryAPITest17" );
	  getEntityTransaction().begin();
          q = getEntityManager().createQuery (
                        "DELETE FROM Employee e where e.salary > :minsal")
		.setParameter("minsal", (float)50000.0);
          result = q.getResultList();

        getEntityTransaction().commit();
      } catch (IllegalStateException ise) {
          TLogger.log("IllegalStateException Caught as Expected" );
          pass = true;
     } catch (Exception e) {
        pass = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
     } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass)
            throw new Fault( "queryAPITest17 failed");
    }

    /*
     * @testName: queryAPITest18
     * @assertion_ids:  PERSISTENCE:JAVADOC:171
     * @test_Strategy: getSingleResult() should throw an IllegalStateException
     *			if called for an EJB QL Update statement.
     */

    public void queryAPITest18() throws Fault
    {
      Query q = null;
      Object result = null;
      boolean pass = false;

      try {
          TLogger.log("Invoking query for queryAPITest18" );
	  getEntityTransaction().begin();
          q = getEntityManager().createQuery (
                        "UPDATE Employee e SET e.salary = e.salary + :bonus where e.salary > :minsal")
		.setParameter("bonus", (float)1000.0)
		.setParameter("minsal", (float)50000.0);
          result = q.getSingleResult();

        getEntityTransaction().commit();
      } catch (IllegalStateException ise) {
          TLogger.log("IllegalStateException Caught as Expected" );
          pass = true;
     } catch (Exception e) {
        pass = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass)
            throw new Fault( "queryAPITest18 failed");
    }

    /*
     * @testName: queryAPITest19
     * @assertion_ids:  PERSISTENCE:JAVADOC:171
     * @test_Strategy: getSingleResult() should throw an IllegalStateException
     *			if called for an EJB QL Delete statement.
     */

    public void queryAPITest19() throws Fault
    {
      Query q = null;
      Object result = null;
      boolean pass = false;

      try {
          TLogger.log("Invoking query for queryAPITest19" );
	  getEntityTransaction().begin();
          q = getEntityManager().createQuery (
                        "DELETE FROM Employee e where e.salary > :minsal")
		.setParameter("minsal", (float)50000.0);
          result = q.getSingleResult();

        getEntityTransaction().commit();
      } catch (IllegalStateException ise) {
          TLogger.log("IllegalStateException Caught as Expected" );
          pass = true;
     } catch (Exception e) {
        pass = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass)
            throw new Fault( "queryAPITest19 failed");
    }

    /*
     * @testName: queryAPITest20
     * @assertion_ids:  PERSISTENCE:JAVADOC:169
     * @test_Strategy: executeUpdate() should throw an IllegalStateException
     *			if called for an EJB QL Select statement.
     */

    public void queryAPITest20() throws Fault
    {
      Query q = null;
      int result = 0;
      boolean pass = false;

      try {
          TLogger.log("Invoking query for queryAPITest20" );
	  getEntityTransaction().begin();
          q = getEntityManager().createQuery (
			"select d.id from Department d");
          result = q.executeUpdate();

        getEntityTransaction().commit();

      } catch (IllegalStateException ise) {
          TLogger.log("IllegalStateException Caught as Expected" );
          pass = true;
     } catch (Exception e) {
        pass = false;
        TLogger.log("Unexpection Exception :" + e );
        e.printStackTrace();
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass)
            throw new Fault( "queryAPITest20 failed");
    }

    /*
     * @testName:  queryAPITest21
     * @assertion_ids: PERSISTENCE:JAVADOC:173
     * @test_Strategy: setFlushMode - AUTO
     *
     */

    public void queryAPITest21() throws Fault
    {
      boolean pass = false;
      List d = null;

      try
      {
          getEntityTransaction().begin();
          TLogger.log("Starting queryAPITest21");
          Department dept1 = getEntityManager().find(Department.class, 1);
          dept1.setName("Research and Development");
          d = getEntityManager().createQuery(
                "SELECT d FROM Department d WHERE d.name = 'Research and Development'")
                .setFlushMode(FlushModeType.AUTO)
                .getResultList();

                Department newDepartment = getEntityManager().find(Department.class, 1);
                if ( newDepartment.getName().equals("Research and Development") ) {
                        pass = true;
                	TLogger.log("Expected results received");
	        }

        getEntityTransaction().commit();
      } catch (Exception e) {
        TLogger.log("Caught exception queryAPITest21: " + e);
        throw new Fault( "queryAPITest21 failed", e);
      } finally {
          try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
          } catch (Exception re) {
              TLogger.log("Unexpection Exception in while rolling back TX:" + re );
              re.printStackTrace();
          }
      }

      if (!pass)
            throw new Fault( "queryAPITest21 failed");
      }


   public void cleanup() throws Fault
   {
    try {
          getEntityTransaction().begin();

          for (int i=1; i<21; i++ ) {
		Employee emp = getEntityManager().find(Employee.class, i);
		if (emp != null ) {
                    getEntityManager().remove(emp);
		    //doFlush();
                    TLogger.log("removed employee " + emp);
		}
          }

          for (int i=1; i<6; i++ ) {
		Department dept = getEntityManager().find(Department.class, i);
		if (dept != null ) {
                    getEntityManager().remove(dept);
		    //doFlush();
                    TLogger.log("removed department " + dept);
		}
          }

          for (int i=1; i<4; i++ ) {
		Insurance ins = getEntityManager().find(Insurance.class, i);
		if (ins != null ) {
                    getEntityManager().remove(ins);
		    //doFlush();
                    TLogger.log("removed insurance " + ins);
		}
          }

          getEntityTransaction().commit();

    } catch (Exception re) {
          TLogger.log("Unexpection Exception in cleanup:" + re );
          re.printStackTrace();
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
        TLogger.log("cleanup complete, calling super.cleanup");
	super.cleanup();
    }


    /* 
     *  Business Methods to set up data for Test Cases
     */


    private void doFlush() throws PersistenceException
    {
        TLogger.log("Entering doFlush method");
        try {
                getEntityManager().flush();
        } catch (PersistenceException pe) {
                throw new PersistenceException("Unexpected Exception caught while flushing: " + pe);
        }
    }

    private void createTestData() throws Exception
    {
        TLogger.log("createTestData");
        try {
	getEntityTransaction().begin();

        TLogger.log("Create 5 Departments");
        deptRef[0] = new Department(1, "Engineering");
        deptRef[1] = new Department(2, "Marketing");
        deptRef[2] = new Department(3, "Sales");
        deptRef[3] = new Department(4, "Accounting");
        deptRef[4] = new Department(5, "Training");

              TLogger.log("Start to persist departments ");
                for (int i=0; i<5; i++ ) {
                    getEntityManager().persist(deptRef[i]);
		    doFlush();
                    TLogger.log("persisted department " + deptRef[i]);
                }

        TLogger.log("Create 3 Insurance Carriers");
        insRef[0] = new Insurance(1, "Prudential");
        insRef[1] = new Insurance(2, "Cigna");
        insRef[2] = new Insurance(3, "Sentry");

              TLogger.log("Start to persist insurance ");
                for (int i=0; i<3; i++ ) {
                    getEntityManager().persist(insRef[i]);
		    doFlush();
                    TLogger.log("persisted insurance " + insRef[i]);
                }


        TLogger.log("Create 20 employees");
        empRef[0] = new Employee(1, "Alan", "Frechette", d1, (float)35000.0);
	empRef[0].setDepartment(deptRef[0]);
	empRef[0].setInsurance(insRef[0]);

        empRef[1] = new Employee(2, "Arthur", "Frechette", d2, (float)35000.0);
	empRef[1].setDepartment(deptRef[1]);
	empRef[1].setInsurance(insRef[1]);

        empRef[2] = new Employee(3, "Shelly", "McGowan", d3, (float)50000.0);
	empRef[2].setDepartment(deptRef[2]);
	empRef[2].setInsurance(insRef[2]);

        empRef[3] = new Employee(4, "Robert", "Bissett", d4, (float)55000.0);
	empRef[3].setDepartment(deptRef[3]);
	empRef[3].setInsurance(insRef[0]);

        empRef[4] = new Employee(5, "Stephen", "DMilla", d5, (float)25000.0);
	empRef[4].setDepartment(deptRef[4]);
	empRef[4].setInsurance(insRef[1]);

        empRef[5] = new Employee(6, "Karen", "Tegan", d6, (float)80000.0);
	empRef[5].setDepartment(deptRef[0]);
	empRef[5].setInsurance(insRef[2]);

        empRef[6] = new Employee(7, "Stephen", "Cruise", d7, (float)90000.0);
	empRef[6].setDepartment(deptRef[1]);
	empRef[6].setInsurance(insRef[0]);

        empRef[7] = new Employee(8, "Irene", "Caruso", d8, (float)20000.0);
	empRef[7].setDepartment(deptRef[2]);
	empRef[7].setInsurance(insRef[1]);

        empRef[8] = new Employee(9, "William", "Keaton", d9, (float)35000.0);
	empRef[8].setDepartment(deptRef[3]);
	empRef[8].setInsurance(insRef[2]);

        empRef[9] = new Employee(10, "Kate", "Hudson", d10, (float)20000.0);
	empRef[9].setDepartment(deptRef[4]);
	empRef[9].setInsurance(insRef[0]);

        empRef[10] = new Employee(11, "Jonathan", "Smith", d10, (float)40000.0);
	empRef[10].setDepartment(deptRef[0]);
	empRef[10].setInsurance(insRef[1]);

        empRef[11] = new Employee(12, "Mary", "Macy", d9, (float)40000.0);
	empRef[11].setDepartment(deptRef[1]);
	empRef[11].setInsurance(insRef[2]);

        empRef[12] = new Employee(13, "Cheng", "Fang", d8, (float)40000.0);
	empRef[12].setDepartment(deptRef[2]);
	empRef[12].setInsurance(insRef[0]);

        empRef[13] = new Employee(14, "Julie", "OClaire", d7, (float)60000.0);
	empRef[13].setDepartment(deptRef[3]);
	empRef[13].setInsurance(insRef[1]);

        empRef[14] = new Employee(15, "Steven", "Rich", d6, (float)60000.0);
	empRef[14].setDepartment(deptRef[4]);
	empRef[14].setInsurance(insRef[2]);

        empRef[15] = new Employee(16, "Kellie", "Lee", d5, (float)60000.0);
	empRef[15].setDepartment(deptRef[0]);
	empRef[15].setInsurance(insRef[0]);

        empRef[16] = new Employee(17, "Nicole", "Martin", d4, (float)60000.0);
	empRef[16].setDepartment(deptRef[1]);
	empRef[16].setInsurance(insRef[1]);

        empRef[17] = new Employee(18, "Mark", "Francis", d3, (float)60000.0);
	empRef[17].setDepartment(deptRef[2]);
	empRef[17].setInsurance(insRef[2]);

        empRef[18] = new Employee(19, "Will", "Forrest", d2, (float)60000.0);
	empRef[18].setDepartment(deptRef[3]);
	empRef[18].setInsurance(insRef[0]);

        empRef[19] = new Employee(20, "Katy", "Hughes", d1, (float)60000.0);
	empRef[19].setDepartment(deptRef[4]);
	empRef[19].setInsurance(insRef[1]);

                TLogger.log("Start to persist employees ");
        	for (int i=0; i<20; i++ ) {
                    getEntityManager().persist(empRef[i]);
		    doFlush();
                    TLogger.log("persisted employee " + empRef[i]);
                }
	getEntityTransaction().commit();

        } catch (Exception re) {
          TLogger.log("Unexpection Exception in createTestData:" + re );
          re.printStackTrace();
       } finally {
         try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
       } catch (Exception re) {
         TLogger.log("Unexpection Exception in createTestData while rolling back TX:" + re );
         re.printStackTrace();
       }
     }

    }

    private static Date getHireDate(int yy, int mm, int dd)
    {
        Calendar newCal = Calendar.getInstance();
        newCal.clear();
        newCal.set(yy,mm,dd);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String sDate = sdf.format(newCal.getTime());
	TLogger.log("returning date:" + java.sql.Date.valueOf(sDate) );
        return java.sql.Date.valueOf(sDate);
    }

}

