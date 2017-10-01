 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)Client.java	1.8 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.types.generator;

import javax.naming.InitialContext;
import java.util.Properties;
import com.sun.javatest.Status;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;

public class Client extends PMClientBase implements java.io.Serializable  {


    private DataTypes d0;
    private DataTypes d1;
    private DataTypes d2;
    private DataTypes d3;
    private DataTypes d4;
    private DataTypes d5;
    private DataTypes d6;

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
     * @testName: generatorTypeTest1
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1052;
     *                  PERSISTENCE:SPEC:1057; PERSISTENCE:JAVADOC:206;
     *                  PERSISTENCE:JAVADOC:209; PERSISTENCE:JAVADOC:210;
     *                  PERSISTENCE:JAVADOC:211; PERSISTENCE:JAVADOC:213;
     *                  PERSISTENCE:JAVADOC:215; PERSISTENCE:JAVADOC:81;
     *                  PERSISTENCE:SPEC:1136; PERSISTENCE:SPEC:1137
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.TABLE, 
     *                  indicates the persistence provider must assign
     *                  primary keys for the entity using an underlying
     *			database strategy table to ensure uniqueness.
     *
     *			Using GenerationType.TABLE, access a persisted entity and
     *			modify its' data.
     */

    public void generatorTypeTest1() throws Fault
    {

      TLogger.log("Begin generatorTypeTest1");
      boolean pass = false;
      Character newChar = new Character((char)'b');

      try {
        getEntityTransaction().begin();

         d1 = getEntityManager().find(DataTypes.class, d0.getId());

         if  ( (null != d1) && (d1.getCharacterData().equals((char)'a')) ) {
                 d1.setCharacterData(newChar);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.getCharacterData().equals(newChar) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "generatorTypeTest1 failed");
    }

   /*
     * @testName: generatorTypeTest2
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1052;
     *                  PERSISTENCE:SPEC:1057
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.TABLE,
     *                  indicates the persistence provider must assign
     *                  primary keys for the entity using an underlying
     *                  database strategy table to ensure uniqueness.
     *
     *                  Using GenerationType.TABLE, access a persisted entity and
     *                  modify its' data.
     */

    public void generatorTypeTest2() throws Fault
    {

      TLogger.log("Begin generatorTypeTest2");
      boolean pass = false;
      Short newShort = new Short((short)101);

      try {
        getEntityTransaction().begin();
         d2 = getEntityManager().find(DataTypes.class, d0.getId());

         if  ( (null != d2) && (d2.getShortData().equals((short)100)) ) {
                 d2.setShortData(newShort);
         }

        getEntityManager().merge(d2);
	getEntityManager().flush();

        if ( d2.getShortData().equals(newShort) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "generatorTypeTest2 failed");
    }

   /*
     * @testName: generatorTypeTest3
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1052;
     *                  PERSISTENCE:SPEC:1057
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.TABLE,
     *                  indicates the persistence provider must assign
     *                  primary keys for the entity using an underlying
     *                  database strategy table to ensure uniqueness.
     *
     *                  Using GenerationType.TABLE, access a persisted entity and
     *                  modify its' data.
     */

    public void generatorTypeTest3() throws Fault
    {

      TLogger.log("Begin generatorTypeTest3");
      boolean pass = false;
      Integer newInt = new Integer(500);

      try {
        getEntityTransaction().begin();
         d3 = getEntityManager().find(DataTypes.class, d0.getId());

         if  ( (null != d3) && (d3.getIntegerData().equals(new Integer(500))) ) {
                 d3.setIntegerData(newInt);
         }

        getEntityManager().merge(d3);
	getEntityManager().flush();

        if ( d3.getIntegerData().equals(newInt) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "generatorTypeTest3 failed");
    }

    /*
     * @testName: generatorTypeTest4
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1052;
     *                  PERSISTENCE:SPEC:1057
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.TABLE,
     *                  indicates the persistence provider must assign
     *                  primary keys for the entity using an underlying
     *                  database strategy table to ensure uniqueness.
     *
     *                  Using GenerationType.TABLE, access a persisted entity and
     *                  modify its' data.
     */

    public void generatorTypeTest4() throws Fault
    {

      TLogger.log("Begin generatorTypeTest4");
      boolean pass = false;
      Long newLong = 600L;

      try {
         getEntityTransaction().begin();
         d4 = getEntityManager().find(DataTypes.class, d0.getId());

         if  ( (null != d4) && (d4.getLongData().equals(300L)) ) {
                 d4.setLongData(newLong);
         }

        getEntityManager().merge(d4);
	getEntityManager().flush();

        if ( d4.getLongData().equals(newLong) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }


      if (!pass)
            throw new Fault( "generatorTypeTest4 failed");
    }


    /*
     * @testName: generatorTypeTest5
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1052;
     *                  PERSISTENCE:SPEC:1057
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.TABLE,
     *                  indicates the persistence provider must assign
     *                  primary keys for the entity using an underlying
     *                  database strategy table to ensure uniqueness.
     *
     *                  Using GenerationType.TABLE, access a persisted entity and
     *                  modify its' data.
     */

    public void generatorTypeTest5() throws Fault
    {

      TLogger.log("Begin generatorTypeTest5");
      boolean pass = false;
      Double newDbl = 80D;

      try {
         getEntityTransaction().begin();
         d5 = getEntityManager().find(DataTypes.class, d0.getId());

         if  ( (null != d5) && (d5.getDoubleData().equals(50D)) ) {
                 d5.setDoubleData(newDbl);
         }

        getEntityManager().merge(d5);
	getEntityManager().flush();

        if ( d5.getDoubleData().equals(newDbl) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "generatorTypeTest5 failed");
    }

    /*
     * @testName: generatorTypeTest6
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1052;
     *                  PERSISTENCE:SPEC:1057
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.TABLE,
     *                  indicates the persistence provider must assign
     *                  primary keys for the entity using an underlying
     *                  database strategy table to ensure uniqueness.
     *
     *                  Using GenerationType.TABLE, access a persisted entity and
     *			modify its' data.
     */

    public void generatorTypeTest6() throws Fault
    {

      TLogger.log("Begin generatorTypeTest6");
      boolean pass = false;
      Float newFloat = 3.0F;

      try {
         getEntityTransaction().begin();
         d6 = getEntityManager().find(DataTypes.class, d0.getId());

         if  ( (null != d6) && ( d6.getFloatData().equals(1.0F)) ) {
                 d6.setFloatData(newFloat);
         }

        getEntityManager().merge(d6);
	getEntityManager().flush();

        if ( d6.getFloatData().equals(newFloat) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "generatorTypeTest6 failed");

    }

    // Methods used for Tests

    public void createTestData()
    {
    try {
   	getEntityTransaction().begin();

        TLogger.log("In createTestData: new DataType" );
    	d0 = new DataTypes(new Character('a'), new Short((short)100), new Integer(500),
                                new Long(300L), new Double(50D), new Float(1.0F));

        TLogger.log("persist DataType" );
        getEntityManager().persist(d0);

        getEntityManager().flush();
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

    }

    public void cleanup()  throws Fault
    {
    try { 
	 getEntityTransaction().begin();
         DataTypes newD = getEntityManager().find(DataTypes.class, d0.getId());

         if (newD != null ) {
            getEntityManager().remove(newD);
	    getEntityManager().flush();
          }
	 getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception in Cleanup:" + e );
          e.printStackTrace();
     } finally {
        try {
                if ( getEntityTransaction().isActive() ) {
                     getEntityTransaction().rollback();
                }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }
        TLogger.log("cleanup complete, calling super.cleanup");
	super.cleanup();
    }

}

