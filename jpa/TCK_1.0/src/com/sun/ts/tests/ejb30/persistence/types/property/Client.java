 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.9 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.types.property;

import javax.naming.InitialContext;
import java.util.Properties;
import com.sun.javatest.Status;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import com.sun.ts.tests.ejb30.persistence.types.common.Grade;
import java.util.Calendar;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Client extends PMClientBase implements java.io.Serializable  {


    private DataTypes d1;
    private DataTypes2 d2;
    java.util.Date dateId = getPKDate(2006, 04, 15);

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
     * @testName: propertyTypeTest1
     * @assertion_ids: PERSISTENCE:SPEC:514; PERSISTENCE:SPEC:515;
     *		       PERSISTENCE:SPEC:516; PERSISTENCE:SPEC:524;
     *                 PERSISTENCE:SPEC:527; PERSISTENCE:SPEC:534
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *                  wrappers of the primitive types: Character
     */

    public void propertyTypeTest1() throws Fault
    {

      TLogger.log("Begin propertyTypeTest1");
      boolean pass = false;
      Character newChar = new Character((char)'b');

      try {
        getEntityTransaction().begin();
         d1 = getEntityManager().find(DataTypes.class, new Integer(1));

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
	  pass = false;
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
            throw new Fault( "propertyTypeTest1 failed");
    }

   /*
     * @testName: propertyTypeTest2
     * @assertion_ids: PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:527;
     *                 PERSISTENCE:SPEC:533; PERSISTENCE:SPEC:534
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *                  wrappers of the primitive types: Short
     */

    public void propertyTypeTest2() throws Fault
    {

      TLogger.log("Begin propertyTypeTest2");
      boolean pass = false;
      Short newShort = new Short((short)101);

      try {
        getEntityTransaction().begin();
         d1 = getEntityManager().find(DataTypes.class, new Integer(1));

         if  ( (null != d1) && (d1.getShortData().equals((short)100)) ) {
                 d1.setShortData(newShort);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.getShortData().equals(newShort) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "propertyTypeTest2 failed");
    }

   /*
     * @testName: propertyTypeTest3
     * @assertion_ids: PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:527
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *                  wrappers of the primitive types: Integer
     */

    public void propertyTypeTest3() throws Fault
    {

      TLogger.log("Begin propertyTypeTest3");
      boolean pass = false;
      Integer newInt = new Integer(500);

      try {
        getEntityTransaction().begin();
         d1 = getEntityManager().find(DataTypes.class, new Integer(1));

         if  ( (null != d1) && (d1.getIntegerData().equals(new Integer(500))) ) {
                 d1.setIntegerData(newInt);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.getIntegerData().equals(newInt) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "propertyTypeTest3 failed");
    }

    /*
     * @testName: propertyTypeTest4
     * @assertion_ids: PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:527
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *                  wrappers of the primitive types: Long
     */

    public void propertyTypeTest4() throws Fault
    {

      TLogger.log("Begin propertyTypeTest4");
      boolean pass = false;
      Long newLong = 600L;

      try {
         getEntityTransaction().begin();
         d1 = getEntityManager().find(DataTypes.class, new Integer(1));

         if  ( (null != d1) && (d1.getLongData().equals(300L)) ) {
                 d1.setLongData(newLong);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.getLongData().equals(newLong) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "propertyTypeTest4 failed");
    }


    /*
     * @testName: propertyTypeTest5
     * @assertion_ids: PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:527
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *                  wrappers of the primitive types: Double
     */

    public void propertyTypeTest5() throws Fault
    {

      TLogger.log("Begin propertyTypeTest5");
      boolean pass = false;
      Double newDbl = 80D;

      try {
         getEntityTransaction().begin();
         d1 = getEntityManager().find(DataTypes.class, new Integer(1));

         if  ( (null != d1) && (d1.getDoubleData().equals(50D)) ) {
                 d1.setDoubleData(newDbl);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.getDoubleData().equals(newDbl) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (!pass)
            throw new Fault( "propertyTypeTest5 failed");

    }

    /*
     * @testName: propertyTypeTest6
     * @assertion_ids: PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:527
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *                  wrappers of the primitive types: Float
     */

    public void propertyTypeTest6() throws Fault
    {

      TLogger.log("Begin propertyTypeTest6");
      boolean pass = false;
      Float newFloat = 3.0F;

      try {
         getEntityTransaction().begin();
         d1 = getEntityManager().find(DataTypes.class, new Integer(1));

         if  ( (null != d1) && ( d1.getFloatData().equals(1.0F)) ) {
                 d1.setFloatData(newFloat);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.getFloatData().equals(newFloat) ) {
                pass = true;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }
 
      if (! pass)
            throw new Fault( "propertyTypeTest6 failed");

    }


    /*
     * @testName: propertyTypeTest7
     * @assertion_ids: PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:529;
     *			PERSISTENCE:SPEC:1090.1; PERSISTENCE:SPEC:1090.2;
     *			PERSISTENCE:JAVADOC:72; PERSISTENCE:JAVADOC:73
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *                  enums
     *
     *			Using the Enumerated annotation, with EnumType.ORDINAL.
     */

    public void propertyTypeTest7() throws Fault
    {

      TLogger.log("Begin propertyTypeTest7");
      boolean pass = false;

      try {
         getEntityTransaction().begin();
         TLogger.log("DEBUG:  FIND D1 IN propertyTypeTest7");
         d1 = getEntityManager().find(DataTypes.class, new Integer(1));

         if (null != d1) {
             TLogger.log("DataType Entity is not null, setting enumData ");
             d1.setEnumData(Grade.C);
         }

        getEntityManager().merge(d1);
        getEntityManager().flush();

        TLogger.log("propertyTypeTest7:  Check results");
        if ( ( null != d1) && (d1.getEnumData().equals(Grade.C)) ) {
        	TLogger.log("Expected Grade of:"  + d1.getEnumData() + "received");
                pass = true;
        } else {
        	TLogger.log("ERROR:  Did not get expected results.  Expected C, got: "
			+ d1.getEnumData() );
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (! pass)
            throw new Fault( "propertyTypeTest7 failed");
      }

    /*
     * @testName: propertyTypeTest8
     * @assertion_ids:  PERSISTENCE:SPEC:536; PERSISTENCE:SPEC:540;
     *                  PERSISTENCE:SPEC:550; PERSISTENCE:SPEC:1090.0;
     *                  PERSISTENCE:SPEC:1079
     * @test_Strategy:  The primary key should be one of the following types:
     *                  java.util.Date
     *			
     *			The application must not change the value of the primary key.
     *			The behavior is undefined if this occurs.
     *
     *			Temporal.TemporalType.DATE
     */

    public void propertyTypeTest8() throws Fault
    {

      TLogger.log("Begin propertyTypeTest8");
      boolean pass = false;

      try {
         getEntityTransaction().begin();
         TLogger.log("DEBUG:  FIND D2 IN propertyTypeTest8");
         d2 = getEntityManager().find(DataTypes2.class, dateId);

        TLogger.log("propertyTypeTest8:  Check results");
        if ( ( null != d2) && (d2.getId().equals(dateId)) ) {
        	TLogger.log("Got expected PK of:"  + d2.getId() + "received");
                pass = true;
        } else {
        	TLogger.log("ERROR:  Did not get expected results. "
			+ "Expected " + dateId + ", got: " + d2.getId() );
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (! pass)
            throw new Fault( "propertyTypeTest8 failed");
      }


    /*
     * @testName: propertyTypeTest9
     * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:528
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *			 Byte[]
     *
     */

    public void propertyTypeTest9() throws Fault
    {

      TLogger.log("Begin propertyTypeTest9");
      boolean pass = false;
      Byte[] b = { 31, 32, 33, 63, 64, 65 };
      Byte bv = 5;
      Byte[] a = null;


      try {
         getEntityTransaction().begin();
         TLogger.log("DEBUG:  FIND D2 IN propertyTypeTest9");
         d2 = getEntityManager().find(DataTypes2.class, dateId);

         if (null != d2) {
             TLogger.log("DataType Entity is not null, setting byteData ");
             d2.setByteData(b);
	     a = d2.getByteData();
             a[0] = (byte)(a[0] + bv);
             d2.setByteData(b);
         }

        getEntityManager().merge(d2);
        getEntityManager().flush();

        TLogger.log("propertyTypeTest9:  Check results");
        if ( ( null != d2) && (Arrays.equals(d2.getByteData(),a) ) ) {
        	TLogger.log("propertyTypeTest9: Expected results received");
                pass = true;
        } else {
        	TLogger.log("ERROR: Unexpected result in array comparison."); 
                for (int i=0; i<a.length; i++) {
                TLogger.log("Array a in propertyTest9 equals: " +a[i]);
                }
                for (int j=0; j<b.length; j++) {
                TLogger.log("Array b in propertyTest9 equals: " +b[j]);
                }
		pass = false;
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (! pass)
            throw new Fault( "propertyTypeTest9 failed");
      }

    /*
     * @testName: propertyTypeTest10
     * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:528
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *			 Character[]
     *
     */

    public void propertyTypeTest10() throws Fault
    {

      TLogger.log("Begin propertyTypeTest10");
      boolean pass = false;

      try {
         getEntityTransaction().begin();
	 Character[] charData = new Character[]{(char)'C', (char)'T', (char)'S'};
         TLogger.log("DEBUG:  FIND D1 IN propertyTypeTest10");
         d2 = getEntityManager().find(DataTypes2.class, dateId);

         if (null != d2) {
             TLogger.log("DataType Entity is not null, setting CharacterData ");
             d2.setCharData(charData);
         }

        getEntityManager().merge(d2);
        getEntityManager().flush();

        TLogger.log("propertyTypeTest10:  Check results");
        if ( ( null != d2) && (Arrays.equals(d2.getCharData(), charData) )) {
        	TLogger.log("propertyTypeTest10: Expected Results Received");
                pass = true;
        } else {
        	TLogger.log("ERROR:  Did not get expected results. "
			+ "Expected " + charData + ", got: " + d2.getCharData() );
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (! pass)
            throw new Fault( "propertyTypeTest10 failed");
      }

    /*
     * @testName: propertyTypeTest11
     * @assertion_ids: PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:527
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *			java.sql.Time
     */

    public void propertyTypeTest11() throws Fault
    {

      TLogger.log("Begin propertyTypeTest11");
      boolean pass = false;
      java.sql.Time timeValue = getTimeData(18, 30, 15);

      try {
         getEntityTransaction().begin();
         TLogger.log("DEBUG:  FIND D2 IN propertyTypeTest11");
         d2 = getEntityManager().find(DataTypes2.class, dateId);

         if (null != d2) {
             TLogger.log("DataType Entity is not null, setting TimeData ");
             d2.setTimeData(timeValue);
         }

        getEntityManager().merge(d2);
        getEntityManager().flush();

        TLogger.log("propertyTypeTest11:  Check results");
        if ( ( null != d2) && (d2.getTimeData().equals(timeValue)) ) {
        	TLogger.log("propertyTypeTest11: Expected Time Received");
                pass = true;
        } else {
        	TLogger.log("ERROR:  Did not get expected results. "
			+" Expected " + timeValue + " , got: "
			+ d2.getTimeData() );
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (! pass)
            throw new Fault( "propertyTypeTest11 failed");
      }

    /*
     * @testName: propertyTypeTest12
     * @assertion_ids: PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:527
     * @test_Strategy:  The persistent property of an entity may be of the following type:
     *			java.sql.Timestamp
     */

    public void propertyTypeTest12() throws Fault
    {

      TLogger.log("Begin propertyTypeTest12");
      boolean pass = false;
      java.sql.Timestamp tsValue = getTimestampData(2006, 02, 11);

      try {
         getEntityTransaction().begin();
         TLogger.log("DEBUG:  FIND D2 IN propertyTypeTest12");
         d2 = getEntityManager().find(DataTypes2.class, dateId);

         if (null != d2) {
             TLogger.log("DataType Entity is not null, setting TimestampData ");
             d2.setTsData(tsValue);
         }

        getEntityManager().merge(d2);
        getEntityManager().flush();

        TLogger.log("propertyTypeTest12:  Check results");
        if ( ( null != d2) && (d2.getTsData().equals(tsValue)) ) {
        	TLogger.log("propertyTypeTest12: Expected Timestamp Received");
                pass = true;
        } else {
        	TLogger.log("ERROR:  Did not get expected results. "
			+" Expected " + tsValue + " , got: "
			+ d2.getTsData() );
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
          TLogger.log("Unexpection Exception during Rollback:" + re );
          re.printStackTrace();
        }
      }

      if (! pass)
            throw new Fault( "propertyTypeTest12 failed");
      }


    // Methods used for Tests

    public void createTestData()
    {
    try {
   	getEntityTransaction().begin();

        d1 = new DataTypes(new Integer(1), new Character('a'), new Short((short)100),
				new Integer(500), new Long(300L), new Double(50D),
				new Float(1.0F));

	d2 = new DataTypes2(dateId);

        getEntityManager().persist(d1);
        getEntityManager().persist(d2);

        getEntityManager().flush();

   	getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception in createTestData:" + e );
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
         d1 = getEntityManager().find(DataTypes.class, new Integer(1));

         if (d1 != null ) {
            getEntityManager().remove(d1);
	    getEntityManager().flush();
          }

         d2 = getEntityManager().find(DataTypes2.class, dateId);
         if (d2 != null ) {
            getEntityManager().remove(d2);
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

    private static Date getPKDate(int yy, int mm, int dd)
    {
        Calendar newCal = Calendar.getInstance();
        newCal.clear();
        newCal.set(yy,mm,dd);
        TLogger.log("getPKDate: returning date:" + newCal.getTime() );
        return newCal.getTime();
    }

    private static Time getTimeData(int hh, int mm, int ss)
    {
        Calendar newCal = Calendar.getInstance();
        newCal.clear();
        newCal.set(hh,mm,ss);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String sDate = sdf.format(newCal.getTime());
        TLogger.log("getTimeData: returning Time:"
		 + java.sql.Time.valueOf(sDate) );
        return java.sql.Time.valueOf(sDate);
    }

    private static Timestamp getTimestampData(int yy, int mm, int dd)
    {
        TLogger.log("ENTERING getTimestampData");
        Calendar newCal = Calendar.getInstance();
        newCal.clear();
        newCal.set(yy,mm,dd);
        SimpleDateFormat sdf =
		 new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sDate = sdf.format(newCal.getTime());
        TLogger.log("getTimestampData: returning TimeStamp:"
		 + java.sql.Timestamp.valueOf(sDate) );
        return java.sql.Timestamp.valueOf(sDate);
    }
}

