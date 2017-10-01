 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)Client.java	1.5 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.types.auto;

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
    private DataTypes newD1;
    private DataTypes newD2;
    private DataTypes newD3;
    private DataTypes newD4;
    private DataTypes newD5;
    private DataTypes newD6;

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
     * @testName: autoTypeTest1
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *			PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1055;
     *			PERSISTENCE:SPEC:1057; PERSISTENCE:SPEC:1050
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *			specification of generation strategies for the
     *			values of primary keys. GenerationType.AUTO, 
     *			indicates the persistence provider should pick an 
     *			appropriate strategy for the particular database.
     *
     *			If no Column annotation is specified, the primary
     *			key column name is assumed to be the name of the
     *			identifier property or field.
     *
     *			Using GenerationType.AUTO and defaulting the column name 
     *			to ID, try to find the primary key and modify the
     *			data.
     *	
     */

    public void autoTypeTest1() throws Fault
    {

      TLogger.log("Begin autoTypeTest1");
      boolean pass = false;
      Character newChar = new Character((char)'b');

      try {
        getEntityTransaction().begin();

         newD1 = getEntityManager().find(DataTypes.class, new Integer(d0.getId()));

         if  ( (null != newD1) && (newD1.getCharacterData().equals((char)'a')) ) {
                 newD1.setCharacterData(newChar);
         }

        getEntityManager().merge(newD1);
	getEntityManager().flush();

        if ( newD1.getCharacterData().equals(newChar) ) {
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
            throw new Fault( "autoTypeTest1 failed");
    }

   /*
     * @testName: autoTypeTest2
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1055;
     *                  PERSISTENCE:SPEC:1057; PERSISTENCE:SPEC:1050
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.AUTO, 
     *                  indicates the persistence provider should pick an 
     *                  appropriate strategy for the particular database.
     *
     *                  If no Column annotation is specified, the primary
     *                  key column name is assumed to be the name of the
     *                  identifier property or field.
     *
     *                  Using GenerationType.AUTO and defaulting the column name
     *                  to ID, try to find the primary key and modify the
     *                  data.
     */

    public void autoTypeTest2() throws Fault
    {

      TLogger.log("Begin autoTypeTest2");
      boolean pass = false;
      Short newShort = new Short((short)101);

      try {
        getEntityTransaction().begin();
         newD2 = getEntityManager().find(DataTypes.class, new Integer(d1.getId()));

         if  ( (null != newD2) && (newD2.getShortData().equals((short)100)) ) {
                 newD2.setShortData(newShort);
         }

        getEntityManager().merge(newD2);
	getEntityManager().flush();

        if ( newD2.getShortData().equals(newShort) ) {
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
            throw new Fault( "autoTypeTest2 failed");
    }

   /*
     * @testName: autoTypeTest3
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1055;
     *                  PERSISTENCE:SPEC:1057; PERSISTENCE:SPEC:1050
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.AUTO, 
     *                  indicates the persistence provider should pick an 
     *                  appropriate strategy for the particular database.
     *
     *                  If no Column annotation is specified, the primary
     *                  key column name is assumed to be the name of the
     *                  identifier property or field.
     *
     *                  Using GenerationType.AUTO and defaulting the column name
     *                  to ID, try to find the primary key and modify the
     *                  data.
     */

    public void autoTypeTest3() throws Fault
    {

      TLogger.log("Begin autoTypeTest3");
      boolean pass = false;
      Integer newInt = new Integer(500);

      try {
        getEntityTransaction().begin();
         newD3 = getEntityManager().find(DataTypes.class, new Integer(d2.getId()));

         if  ( (null != newD3) && (newD3.getIntegerData().equals(new Integer(500))) ) {
                 newD3.setIntegerData(newInt);
         }

        getEntityManager().merge(newD3);
	getEntityManager().flush();

        if ( newD3.getIntegerData().equals(newInt) ) {
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
            throw new Fault( "autoTypeTest3 failed");
    }

    /*
     * @testName: autoTypeTest4
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1055;
     *                  PERSISTENCE:SPEC:1057; PERSISTENCE:SPEC:1050
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.AUTO, 
     *                  indicates the persistence provider should pick an 
     *                  appropriate strategy for the particular database.
     *
     *                  If no Column annotation is specified, the primary
     *                  key column name is assumed to be the name of the
     *                  identifier property or field.
     *
     *                  Using GenerationType.AUTO and defaulting the column name
     *                  to ID, try to find the primary key and modify the
     *                  data.
     */

    public void autoTypeTest4() throws Fault
    {

      TLogger.log("Begin autoTypeTest4");
      boolean pass = false;
      Long newLong = 600L;

      try {
         getEntityTransaction().begin();
         newD4 = getEntityManager().find(DataTypes.class, new Integer(d0.getId()));

         if  ( (null != newD4) && (newD4.getLongData().equals(300L)) ) {
                 newD4.setLongData(newLong);
         }

        getEntityManager().merge(newD4);
	getEntityManager().flush();

        if ( newD4.getLongData().equals(newLong) ) {
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
            throw new Fault( "autoTypeTest4 failed");
    }


    /*
     * @testName: autoTypeTest5
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1055;
     *                  PERSISTENCE:SPEC:1057; PERSISTENCE:SPEC:1050
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.AUTO, 
     *                  indicates the persistence provider should pick an 
     *                  appropriate strategy for the particular database.
     *
     *                  If no Column annotation is specified, the primary
     *                  key column name is assumed to be the name of the
     *                  identifier property or field.
     *
     *                  Using GenerationType.AUTO and defaulting the column name
     *                  to ID, try to find the primary key and modify the
     *                  data.
     */

    public void autoTypeTest5() throws Fault
    {

      TLogger.log("Begin autoTypeTest5");
      boolean pass = false;
      Double newDbl = 80D;

      try {
         getEntityTransaction().begin();
         newD5 = getEntityManager().find(DataTypes.class, new Integer(d1.getId()));

         if  ( (null != newD5) && (newD5.getDoubleData().equals(50D)) ) {
                 newD5.setDoubleData(newDbl);
         }

        getEntityManager().merge(newD5);
	getEntityManager().flush();

        if ( newD5.getDoubleData().equals(newDbl) ) {
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
            throw new Fault( "autoTypeTest5 failed");
    }

    /*
     * @testName: autoTypeTest6
     * @assertion_ids: PERSISTENCE:JAVADOC:82; PERSISTENCE:JAVADOC:83;
     *                  PERSISTENCE:SPEC:1051; PERSISTENCE:SPEC:1055;
     *                  PERSISTENCE:SPEC:1057; PERSISTENCE:SPEC:1050
     * @test_Strategy:  The GeneratedValue annotation provides for the
     *                  specification of generation strategies for the
     *                  values of primary keys. GenerationType.AUTO, 
     *                  indicates the persistence provider should pick an 
     *                  appropriate strategy for the particular database.
     *
     *                  If no Column annotation is specified, the primary
     *                  key column name is assumed to be the name of the
     *                  identifier property or field.
     *
     *                  Using GenerationType.AUTO and defaulting the column name
     *                  to ID, try to find the primary key and modify the
     *                  data.
     */

    public void autoTypeTest6() throws Fault
    {

      TLogger.log("Begin autoTypeTest6");
      boolean pass = false;
      Float newFloat = 3.0F;

      try {
         getEntityTransaction().begin();
         newD6 = getEntityManager().find(DataTypes.class, new Integer(d2.getId()));

         if  ( (null != newD6) && ( newD6.getFloatData().equals(1.0F)) ) {
                 newD6.setFloatData(newFloat);
         }

        getEntityManager().merge(newD6);
	getEntityManager().flush();

        if ( newD6.getFloatData().equals(newFloat) ) {
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
            throw new Fault( "autoTypeTest6 failed");

    }

    // Methods used for Tests

    public void createTestData()
    {
    try {
   	getEntityTransaction().begin();

        TLogger.log("In createTestData: new DataType" );
    	d0 = new DataTypes(new Character('a'), new Short((short)100), new Integer(500),
                                new Long(300L), new Double(50D), new Float(1.0F));
    	d1 = new DataTypes(new Character('a'), new Short((short)100), new Integer(500),
                                new Long(300L), new Double(50D), new Float(1.0F));
    	d2 = new DataTypes(new Character('a'), new Short((short)100), new Integer(500),
                                new Long(300L), new Double(50D), new Float(1.0F));

        TLogger.log("persist DataType" );
        getEntityManager().persist(d0);
        getEntityManager().persist(d1);
        getEntityManager().persist(d2);

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
         DataTypes existingD0 = getEntityManager().find(DataTypes.class, d0.getId());
         DataTypes existingD1 = getEntityManager().find(DataTypes.class, d1.getId());
         DataTypes existingD2 = getEntityManager().find(DataTypes.class, d2.getId());

         if (existingD0 != null ) {
            getEntityManager().remove(existingD0);
         }

         if (existingD1 != null ) {
            getEntityManager().remove(existingD1);
         }

         if (existingD2 != null ) {
            getEntityManager().remove(existingD2);
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

