 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.8 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.annotations.orderby;

import com.sun.javatest.Status;
import com.sun.ts.lib.util.*;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper;

import java.util.List;
import java.util.Properties;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import javax.persistence.Query;

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
	   createTestData();
     	}  catch (Exception e) {
           TLogger.log("Exception: " + e.getMessage());
           throw new Fault("Setup failed:", e);
        }
    }


    /*
     * @testName: orderByTest1
     * @assertion_ids: PERSISTENCE:SPEC:1103; PERSISTENCE:SPEC:1104;
     *			PERSISTENCE:SPEC:1105; PERSISTENCE:SPEC:1106;
     *			PERSISTENCE:JAVADOC:145; PERSISTENCE:SPEC:1111
     * @test_Strategy: The OrderBy annotation specifies the ordering of the
     *			elements of a collection valued association at the
     *			point when the association is retrieved.
     *	
     *			The property name must correspond to that of
     *			a persistenct property of the associated class.
     *
     *			The property used in the ordering must correspond
     *			to columns for which comparison operations are
     *			supported.
     *
     *			If DESC is specified, the elements will be ordered
     *			in descending order.
     *			
     *			Retrieve the Collection using getter property accessor. 
     */

   public void orderByTest1() throws Fault
   {

     TLogger.log("Begin orderByTest1");
     boolean pass1 = true;
     boolean pass2 = false;
     int foundEmpName = 0;
     List resultsList = new ArrayList();
     String[] expectedResult = new String[] {"Zoe", "Song", "Jie", "Ay"};

     try {
       getEntityTransaction().begin();

	Employee empChange = getEntityManager().find(Employee.class, 65);

	empChange.setFirstName("Ay");
	getEntityManager().merge(empChange);
	getEntityManager().flush();

	Insurance newIns = getEntityManager().find(Insurance.class, 60);
	getEntityManager().refresh(newIns);

        List insResult = newIns.getEmployees();

        if (insResult.size() != 4) {
          TLogger.log("orderByTest1:  Did not get expected results.  Expected: 4, "
                        + "got: " + insResult.size() );
	  pass1 = false;
        } else if (pass1) {
          Iterator i1 = insResult.iterator();
                TLogger.log("Check Employee Collection for expected first names");
                while (i1.hasNext()) {
                        Employee e1 = (Employee)i1.next();
		 	resultsList.add((String)e1.getFirstName());
                        TLogger.log("orderByTest1: got Employee FirstName:" +(String)e1.getFirstName());
		}

        TLogger.log("Compare first names received with expected first names ");
	String[] result = (String[])(resultsList.toArray(new String[resultsList.size()]));
        pass2 = Arrays.equals(expectedResult, result);

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

      if (! pass1 || !pass2 )
            throw new Fault( "orderByTest1 failed");
    }

    /*
     * @testName: orderByTest2
     * @assertion_ids: PERSISTENCE:SPEC:1103; PERSISTENCE:SPEC:1104;
     *			PERSISTENCE:SPEC:1105; PERSISTENCE:SPEC:1106;
     *			PERSISTENCE:SPEC:1109; PERSISTENCE:SPEC:1110;
     *			PERSISTENCE:JAVADOC:145; PERSISTENCE:SPEC:653
     * @test_Strategy: The OrderBy annotation specifies the ordering of the
     *			elements of a collection valued association at the
     *			point when the association is retrieved.
     *
     *			The property name must correspond to that of
     *			a persistenct property of the associated class.
     *
     *			The property used in the ordering must correspond
     *			to columns for which comparison operations are
     *			supported.
     *
     *			If ASC is specified, the elements will be ordered
     *			in ascending order.
     *			
     *			Retrieve the Collection using getter property accessor. 
     */

   public void orderByTest2() throws Fault
   {

     TLogger.log("Begin orderByTest2");
     boolean pass1 = true;
     boolean pass2 = false;
     int foundEmpName = 0;
     List resultsList = new ArrayList();
     String[] expectedResult = new String[] {"Jie", "Song", "Yay", "Zoe"};


     try {
       getEntityTransaction().begin();

        Employee emp2Change = getEntityManager().find(Employee.class, 65);

	emp2Change.setFirstName("Yay");
	getEntityManager().merge(emp2Change);
	getEntityManager().flush();

       Department newDept = getEntityManager().find(Department.class, 50);
       getEntityManager().refresh(newDept);

       List deptResult = newDept.getEmployees();

       if (deptResult.size() != 4) {
  	 TLogger.log("orderByTest2:  Did not get expected results.  Expected: 4, "
                        + "got: " + deptResult.size() );
	 pass1 = false;
       } else if (pass1) {
          Iterator i2 = deptResult.iterator();
                TLogger.log("Check Employee Collection for expected first names");
                while (i2.hasNext()) {
                        Employee e2 = (Employee)i2.next();
                        resultsList.add((String)e2.getFirstName());
                        TLogger.log("orderByTest2: got Employee FirstName:" +(String)e2.getFirstName());
                }

        TLogger.log("Compare first names received with expected first names ");
        String[] result = (String[])(resultsList.toArray(new String[resultsList.size()]));
        pass2 = Arrays.equals(expectedResult, result);

        }

       getEntityTransaction().commit();
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

     if (! pass1 || !pass2 )
            throw new Fault( "orderByTest2 failed");
     }

    /*
     * @testName: orderByTest3
     * @assertion_ids: PERSISTENCE:SPEC:1103; PERSISTENCE:SPEC:1104;
     *			PERSISTENCE:SPEC:1105; PERSISTENCE:SPEC:1106;
     *			PERSISTENCE:JAVADOC:145
     * @test_Strategy: The OrderBy annotation specifies the ordering of the
     *			elements of a collection valued association at the
     *			point when the association is retrieved.
     *
     *			If DESC is specified, the elements will be ordered
     *			in descending order.
     *	
     *			Add to the Collection then retrieve the updated
     *			Collection and ensure the list is ordered. 
     *			
     */

    public void orderByTest3() throws Fault
    {
     TLogger.log("Begin orderByTest3");
     boolean pass1 = true;
     boolean pass2 = false;
     List insResult  = null;
     List resultsList = new ArrayList();
     String[] expectedResult = new String[] {"Zoe", "Song", "Penelope", "May", "Jie"};


     try {
        getEntityTransaction().begin();

	Employee emp3Change = getEntityManager().find(Employee.class, 85);
	Insurance ins = getEntityManager().find(Insurance.class, 60);

	emp3Change.setInsurance(ins);
	getEntityManager().merge(emp3Change);
	ins.getEmployees().add(emp3Change);
	getEntityManager().merge(ins);
	getEntityManager().flush();

        getEntityManager().refresh(ins);

        insResult = ins.getEmployees();

        if (insResult.size() != 5) {
	    TLogger.log("ERROR: orderByTest3: Expected List Size of 5 " 
			+ "got: " +insResult.size());
            pass1 = false;
        } else if (pass1) {
          Iterator i3 = insResult.iterator();
                TLogger.log("Check Employee Collection for expected first names");
                while (i3.hasNext()) {
                        Employee e3 = (Employee)i3.next();
                        resultsList.add((String)e3.getFirstName());
                        TLogger.log("orderByTest3: got Employee FirstName:"
				+(String)e3.getFirstName());
                }

	    TLogger.log("orderByTest3: Expected size received, check ordering . . .");
            String[] result = (String[])(resultsList.toArray(new String[resultsList.size()]));
            pass2 = Arrays.equals(expectedResult, result);

	}

       getEntityTransaction().commit();

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

     if (! pass1 || !pass2 )
            throw new Fault( "orderByTest3 failed");
     }

    /*
     * @testName: orderByTest4
     * @assertion_ids: PERSISTENCE:SPEC:1103; PERSISTENCE:SPEC:1104;
     *			PERSISTENCE:SPEC:1105; PERSISTENCE:SPEC:1106;
     *			PERSISTENCE:JAVADOC:145
     * @test_Strategy: The OrderBy annotation specifies the ordering of the
     *			elements of a collection valued association at the
     *			point when the association is retrieved.
     *	
     *			If ASC is specified, the elements will be ordered
     *			in ascending order.
     *			
     *			Retrieve the Collection, add to the Collection and
     *			retrieve it again making sure the list is ordered . 
     *			
     */

    public void orderByTest4() throws Fault
    {
     TLogger.log("Begin orderByTest4");
     boolean pass1 = true;
     boolean pass2 = false;
     List resultsList = new ArrayList();
     String[] expectedResult = new String[] {"Jie", "May", "Penelope", "Song", "Zoe"};

     try {
        getEntityTransaction().begin();

        Employee emp4Change = getEntityManager().find(Employee.class, 85);
        Department dept = getEntityManager().find(Department.class, 50);

        emp4Change.setDepartment(dept);
        getEntityManager().merge(emp4Change);
        dept.getEmployees().add(emp4Change);
        getEntityManager().merge(dept);
        getEntityManager().flush();

        getEntityManager().refresh(dept);
        List deptResult = dept.getEmployees();

        if (deptResult.size() != 5) {
            TLogger.log("ERROR: orderByTest4: Expected Collection Size of 5 " 
                        + "got: " +deptResult.size());
            pass1 = false;
        } else if (pass1) {
          Iterator i4 = deptResult.iterator();
                TLogger.log("Check Employee Collection for expected first names");
                while (i4.hasNext()) {
                        Employee e4 = (Employee)i4.next();
                        resultsList.add((String)e4.getFirstName());
                        TLogger.log("orderByTest4: got Employee FirstName:"
				+(String)e4.getFirstName());
                }

	  TLogger.log("orderByTest4: Expected size received, check ordering . . .");
          String[] result = (String[])(resultsList.toArray(new String[resultsList.size()]));
          pass2 = Arrays.equals(expectedResult, result);
	}

       getEntityTransaction().commit();

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

      if (! pass1 || ! pass2 )
            throw new Fault( "orderByTest4 failed");
    }

    private void createTestData() throws Exception
    {
        try {
        TLogger.log("createTestData");
        getEntityTransaction().begin();
	float salary = (float)10000.00;

	Department d1 = new Department(50, "SJSAS Appserver");
        getEntityManager().persist(d1);

	Insurance s1 = new Insurance(60, "United");
        getEntityManager().persist(s1);

	Employee e1 = new Employee(70, "Jie", "Leng", salary, d1, s1);
	Employee e2 = new Employee(80, "Zoe", "Leng", salary, d1, s1);
	Employee e3 = new Employee(90, "Song", "Leng", salary, d1, s1);
	Employee e4 = new Employee(65, "May", "Leng", salary, d1, s1);
	Employee e5 = new Employee(85, "Penelope", "Leng", salary);
        getEntityManager().persist(e1);
        getEntityManager().persist(e2);
        getEntityManager().persist(e3);
        getEntityManager().persist(e4);
        getEntityManager().persist(e5);

 	List<Employee> link = new ArrayList<Employee>();
        link.add(e1);
        link.add(e2);
        link.add(e3);
        link.add(e4);

        d1.setEmployees(link);
        getEntityManager().merge(d1);

        s1.setEmployees(link);
        getEntityManager().merge(s1);

        TLogger.log("Persisted Entity Data");
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


    public void cleanup()  throws Fault
    {
	try {
	  getEntityTransaction().begin();

          if (getEntityManager().find(Employee.class, 65) != null ) {
              getEntityManager().remove(getEntityManager().find(Employee.class, 65));
              TLogger.log("removed Employee with PK 65");
          }
          if (getEntityManager().find(Employee.class, 70) != null ) {
              getEntityManager().remove(getEntityManager().find(Employee.class, 70));
              TLogger.log("removed Employee with PK 70");
          }
          if (getEntityManager().find(Employee.class, 80) != null ) {
              getEntityManager().remove(getEntityManager().find(Employee.class, 80));
              TLogger.log("removed Employee with PK 80");
          }
          if (getEntityManager().find(Employee.class, 85) != null ) {
              getEntityManager().remove(getEntityManager().find(Employee.class, 85));
              TLogger.log("removed Employee with PK 85");
          }
          if (getEntityManager().find(Employee.class, 90) != null ) {
              getEntityManager().remove(getEntityManager().find(Employee.class, 90));
              TLogger.log("removed Employee with PK 90");
          }

          if (getEntityManager().find(Department.class, 50) != null ) {
              getEntityManager().remove(getEntityManager().find(Department.class, 50));
              TLogger.log("removed Department with PK 50");
          }

          if (getEntityManager().find(Insurance.class, 60) != null ) {
              getEntityManager().remove(getEntityManager().find(Insurance.class, 60));
              TLogger.log("removed Insurance with PK 60");
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


}

