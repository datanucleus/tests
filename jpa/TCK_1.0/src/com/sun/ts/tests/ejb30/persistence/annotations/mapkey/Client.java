 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.6 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.annotations.mapkey;

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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Iterator;
import java.util.Arrays;

public class Client extends PMClientBase {


    public Client() {
    }
    
    private static Employee empRef[] = new Employee[10];
    private static Department deptRef[] = new Department[5];

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
     * @testName: annotationMapKeyTest1
     * @assertion_ids: PERSISTENCE:JAVADOC:114; PERSISTENCE:SPEC:1100;
     *			PERSISTENCE:SPEC:1101; PERSISTENCE:SPEC:518
     * @test_Strategy: The MapKey annotation is used to specify the map key for
     *			associations of type java.util.Map.
     *
     *			The name element designates the name of the persistence
     *			property or field of the associated entity that is used
     *			as the map key.
     *	
     *			Execute a query returning Employees objects.
     *			
     */

   public void annotationMapKeyTest1() throws Fault
   {

     TLogger.log("Begin annotationMapKeyTest1");
     boolean pass = true;
     List e = null;

     try {
       getEntityTransaction().begin();

            TLogger.log("Find Employees belonging to Department: Marketing");
            e = getEntityManager().createQuery(
                "Select e from Employee e where e.department.name = 'Marketing'")
                .setMaxResults(10)
                .getResultList();


		if ( e.size() != 3 ) {
		  TLogger.log("annotationMapKeyTest1: ERROR:  Did not get expected results"
			+ "Expected 3 Employees, Received: " + e.size() );
		  pass = false;
                } else {
		  TLogger.log("annotationMapKeyTest1: Expected results received. "
			+ "Expected 3 Employees, Received: " + e.size() );
            	}

       getEntityTransaction().commit();

     } catch (Exception ex) {
         TLogger.log("Unexpection Exception :" + ex );
         ex.printStackTrace();
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
            throw new Fault( "annotationMapKeyTest1 failed");
    }



    /*
     * @testName: annotationMapKeyTest2
     * @assertion_ids: PERSISTENCE:JAVADOC:114; PERSISTENCE:SPEC:1100;
     *			PERSISTENCE:SPEC:1101
     * @test_Strategy: The MapKey annotation is used to specify the map key for
     *                  associations of type java.util.Map.
     *
     *                  The name element designates the name of the persistence
     *                  property or field of the associated entity that is used
     *                  as the map key.
     *
     *			Execute a query returning Employee IDs.
     */

   public void annotationMapKeyTest2() throws Fault
   {

     TLogger.log("Begin annotationMapKeyTest2");
     boolean pass = true;
     List e = null;

     try {
       getEntityTransaction().begin();
       Integer[] expectedEmps = new Integer[] {4, 2};


            TLogger.log("Find Employees belonging to Department: Marketing");
            e = getEntityManager().createQuery(
                "Select e.id from Employee e where e.department.name = 'Administration' ORDER BY e.id DESC")
                .setMaxResults(10)
                .getResultList();

 	    Integer[] result = (Integer[])(e.toArray(new Integer[e.size()]));
            TLogger.log("Compare results of Employee Ids ");
            pass = Arrays.equals(expectedEmps, result);

                if ( ! pass ) {
                TLogger.log("ERROR:  Did not get expected results.  Expected 2 Employees : "
                        + " Received: " + e.size() );
                        Iterator it = e.iterator();
                        while (it.hasNext()) {
                          TLogger.log(" Employee PK : " +it.next() );
                        }
                } else {
                  TLogger.log("annotationMapKeyTest2: Expected results received");
                }

       getEntityTransaction().commit();

     } catch (Exception ex) {
         TLogger.log("Unexpection Exception :" + ex );
         ex.printStackTrace();
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
           throw new Fault( "annotationMapKeyTest2 failed");
    }


    public void cleanup()  throws Fault
    {
	try {
	getEntityTransaction().begin();

         for (int i=1; i<6; i++ ) {
                Employee emp = getEntityManager().find(Employee.class, i);
                if (emp != null ) {
                    getEntityManager().remove(emp);
                    TLogger.log("removed employee " + emp);
                }
          }

          for (int i=1; i<3; i++ ) {
                Department dept = getEntityManager().find(Department.class, i);
                if (dept != null ) {
                    getEntityManager().remove(dept);
                    TLogger.log("removed department " + dept);
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
        TLogger.log("cleanup complete, call super.cleanup");
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
	TLogger.log("Create 2 Departments");
        deptRef[0] = new Department(1, "Marketing");
        deptRef[1] = new Department(2, "Administration");

              TLogger.log("Start to persist departments ");
                for (int i=0; i<2; i++ ) {
                    getEntityManager().persist(deptRef[i]);
                    TLogger.log("persisted department " + deptRef[i]);
                }

	TLogger.log("Create 5 employees");
        empRef[0] = new Employee(1, "Alan", "Frechette");
        empRef[0].setDepartment(deptRef[0]);

        empRef[1] = new Employee(2, "Arthur", "Frechette");
        empRef[1].setDepartment(deptRef[1]);

        empRef[2] = new Employee(3, "Shelly", "McGowan");
        empRef[2].setDepartment(deptRef[0]);

        empRef[3] = new Employee(4, "Robert", "Bissett");
        empRef[3].setDepartment(deptRef[1]);

        empRef[4] = new Employee(5, "Stephen", "DMilla");
        empRef[4].setDepartment(deptRef[0]);

        Map<String, Employee> link = new HashMap<String, Employee>();
        link.put(empRef[0].getLastName(), empRef[0]);
        link.put(empRef[2].getLastName(), empRef[2]);
        link.put(empRef[4].getLastName(), empRef[4]);
        deptRef[0].setLastNameEmployees(link);

        Map<String, Employee> link1 = new HashMap<String, Employee>();
        link1.put(empRef[1].getLastName(), empRef[1]);
        link1.put(empRef[3].getLastName(), empRef[3]);
        deptRef[1].setLastNameEmployees(link1);

                TLogger.log("Start to persist employees ");
                for (int i=0; i<5; i++ ) {
                    getEntityManager().persist(empRef[i]);
                    TLogger.log("persisted employee " + empRef[i]);
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

