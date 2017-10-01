 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)Client.java	1.9 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.inheritance.mappedsc.annotation;

import javax.naming.InitialContext;
import java.util.Properties;
import com.sun.javatest.Status;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper;
import java.sql.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class Client extends PMClientBase implements java.io.Serializable  {


    private static FullTimeEmployee ftRef[] = new FullTimeEmployee[5];
    private static PartTimeEmployee ptRef[] = new PartTimeEmployee[5];

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
     * @testName: test1
     * @assertion_ids: PERSISTENCE:SPEC:593; PERSISTENCE:SPEC:595;
     *                 PERSISTENCE:SPEC:596; PERSISTENCE:SPEC:597;
     *                 PERSISTENCE:SPEC:598; PERSISTENCE:SPEC:599;
     *                 PERSISTENCE:SPEC:1130; PERSISTENCE:SPEC:1131;
     *                 PERSISTENCE:SPEC:1132; PERSISTENCE:SPEC:1133;
     *                 PERSISTENCE:SPEC:1061; PERSISTENCE:SPEC:1062;
     *                 PERSISTENCE:JAVADOC:4; PERSISTENCE:JAVADOC:5;
     *                 PERSISTENCE:JAVADOC:6
     * @test_Strategy: An entity may have a mapped superclass which provides 
     *			persistent entity state and mapping information but
     *			is not an entity itself.
     */

    public void test1() throws Fault
    {

      TLogger.log("Begin test1");
      boolean pass = false;

      try {
	FullTimeEmployee ftEmp1 = getEntityManager().find(FullTimeEmployee.class, 1);
	
	if ( ftEmp1.getFullTimeRep().equals("Mabel Murray") ) {
		pass = true;
	}

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      }

      if (!pass)
            throw new Fault( "test1 failed");
    }

    /*
     * @testName: test2
     * @assertion_ids: PERSISTENCE:SPEC:593; PERSISTENCE:SPEC:595;
     *                 PERSISTENCE:SPEC:596; PERSISTENCE:SPEC:597;
     *                 PERSISTENCE:SPEC:598; PERSISTENCE:SPEC:599;
     *                 PERSISTENCE:SPEC:1130; PERSISTENCE:SPEC:1131;
     *                 PERSISTENCE:SPEC:1132; PERSISTENCE:SPEC:1133;
     *                 PERSISTENCE:SPEC:1061; PERSISTENCE:SPEC:1062;
     *                 PERSISTENCE:JAVADOC:4; PERSISTENCE:JAVADOC:5;
     *                 PERSISTENCE:JAVADOC:6
     * @test_Strategy: An entity may have a mapped superclass which provides 
     *			persistent entity state and mapping information but
     *			is not an entity itself.
     */

    public void test2() throws Fault
    {

      TLogger.log("Begin test2");
      boolean pass = false;

      try {
        PartTimeEmployee ptEmp1 = getEntityManager().find(PartTimeEmployee.class, 6);

        if ( ptEmp1.getPartTimeRep().equals("John Cleveland") ) {
                pass = true;
        }

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      }

      if (!pass)
            throw new Fault( "test2 failed");
    }


   public void createTestData() {

	try {
	
	getEntityTransaction().begin();
        ftRef[0] = new FullTimeEmployee(1, "Jonathan", "Smith", d10, (float)40000.0);
        ftRef[1] = new FullTimeEmployee(2, "Mary", "Macy", d9, (float)40000.0);
        ftRef[2] = new FullTimeEmployee(3, "Sid", "Nee", d8, (float)40000.0);
        ftRef[3] = new FullTimeEmployee(4, "Julie", "OClaire", d7, (float)60000.0);
        ftRef[4] = new FullTimeEmployee(5, "Steven", "Rich", d6, (float)60000.0);

                TLogger.log("Persist full time employees ");
                for (int i=0; i<5; i++ ) {
                    getEntityManager().persist(ftRef[i]);
                    TLogger.log("persisted employee " + ftRef[i]);
                }

        ptRef[0] = new PartTimeEmployee(6, "Kellie", "Lee", d5, (float)60000.0);
        ptRef[1] = new PartTimeEmployee(7, "Nicole", "Martin", d4, (float)60000.0);
        ptRef[2] = new PartTimeEmployee(8, "Mark", "Francis", d3, (float)60000.0);
        ptRef[3] = new PartTimeEmployee(9, "Will", "Forrest", d2, (float)60000.0);
        ptRef[4] = new PartTimeEmployee(10, "Katy", "Hughes", d1, (float)60000.0);

                TLogger.log("Persist part time employees ");
                for (int i=0; i<5; i++ ) {
                    getEntityManager().persist(ptRef[i]);
                    TLogger.log("persisted employee " + ptRef[i]);
                }
   	 getEntityTransaction().commit();
      } catch (Exception re) {
        TLogger.log("Unexpection Exception creating test data:" + re );
        re.printStackTrace();
      } finally {
        try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
      } catch (Exception re) {
        TLogger.log("Unexpection Exception rolling back TX:" + re );
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

    public void cleanup()  throws Fault
    {
      try {
        getEntityTransaction().begin();
	TLogger.log("Remove full time employees ");
        for (int i=1; i<6; i++ ) {
        FullTimeEmployee ftemp = getEntityManager().find(FullTimeEmployee.class, i);
          if ( ftemp != null ) {
                getEntityManager().remove(ftemp);
                TLogger.log("removed employee " + ftemp);
          }
         }

        TLogger.log("Remove part time employees ");
        for (int i=6; i<11; i++ ) {
        PartTimeEmployee ptemp = getEntityManager().find(PartTimeEmployee.class, i);
          if ( ptemp != null ) {
                getEntityManager().remove(ptemp);
                TLogger.log("removed employee " + ptemp);
          }
         }

        getEntityTransaction().commit();
      } catch (Exception re) {
        TLogger.log("Unexpection Exception removing entities in cleanup:" + re );
        re.printStackTrace();
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

