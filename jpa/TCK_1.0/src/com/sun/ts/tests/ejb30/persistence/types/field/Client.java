 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)Client.java	1.14 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.types.field;

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
import java.util.List;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Time;
import java.sql.Timestamp;
import javax.persistence.Query;

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
     * @testName: fieldTypeTest1
     * @assertion_ids: PERSISTENCE:SPEC:511; PERSISTENCE:SPEC:524;
     *			PERSISTENCE:SPEC:534; PERSISTENCE:SPEC:512;
     *			PERSISTENCE:SPEC:525; PERSISTENCE:JAVADOC:14;
     *			PERSISTENCE:JAVADOC:203
     * @test_Strategy:  The persistent field of an entity may be of the following type:
     *			Java primitive types: boolean
     */

    public void fieldTypeTest1() throws Fault
    {

      TLogger.log("Begin fieldTypeTest1");
      boolean pass = false;

      try {
	getEntityTransaction().begin();
	 d1 = getEntityManager().find(DataTypes.class, 1);
         if  ( (null != d1) && ( ! d1.fetchBooleanData() )) {
		 d1.storeBooleanData(true);
	 }

	getEntityManager().merge(d1);
	getEntityManager().flush();

	if (d1.fetchBooleanData() ) {
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
            throw new Fault( "fieldTypeTest1 failed");
    }


   /*
     * @testName: fieldTypeTest2
     * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:525;
     *			PERSISTENCE:SPEC:513
     * @test_Strategy:  The persistent field of an entity may be of the following type:
     *			Java primitive types: byte
     */

    public void fieldTypeTest2() throws Fault
    {

      TLogger.log("Begin fieldTypeTest2");
      boolean pass = false;
      byte newByte = (byte)111;

      try {
	 getEntityTransaction().begin();
	 d1 = getEntityManager().find(DataTypes.class, 1);
         if  ( (null != d1) && (d1.fetchByteData() == (byte)100) ) { 
                 d1.storeByteData(newByte);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.fetchByteData() == newByte ) {
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
            throw new Fault( "fieldTypeTest2 failed");
    }

   /*
     * @testName: fieldTypeTest3
     * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:525
     * @test_Strategy:  The persistent field of an entity may be of the following type:
     *			Java primitive types: char
     */

    public void fieldTypeTest3() throws Fault
    {

      TLogger.log("Begin fieldTypeTest3");
      boolean pass = false;
      char newChar = 'b';

      try {
         getEntityTransaction().begin();
	 d1 = getEntityManager().find(DataTypes.class, 1);

         if  ( (null != d1) && ( d1.fetchCharacterData() == ('a') ) ) {
                 d1.storeCharacterData(newChar);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.fetchCharacterData() == newChar ) {
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
            throw new Fault( "fieldTypeTest3 failed");
    }

   /*
     * @testName: fieldTypeTest4
     * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:525
     * @test_Strategy:  The persistent field of an entity may be of the following type:
     *			Java primitive types: short
     */

    public void fieldTypeTest4() throws Fault
    {

      TLogger.log("Begin fieldTypeTest4");
      boolean pass = false;
      short newShort = (short)101;

      try {

         getEntityTransaction().begin();
	 d1 = getEntityManager().find(DataTypes.class, 1);
         if  ( (null != d1) && ( d1.fetchShortData() == (short)100) ) {
                 d1.storeShortData(newShort);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.fetchShortData() == newShort ) {
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
	  pass = false;
	}
      }

      if (!pass)
            throw new Fault( "fieldTypeTest4 failed");
    }

   /*
     * @testName: fieldTypeTest5
     * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:525
     * @test_Strategy:  The persistent field of an entity may be of the following type:
     *			Java primitive types: int
     */

    public void fieldTypeTest5() throws Fault
    {

      TLogger.log("Begin fieldTypeTest5");
      boolean pass = false;
      int newInt = 500;

      try {
        getEntityTransaction().begin();
	 d1 = getEntityManager().find(DataTypes.class, 1);
         if  ( (null != d1) && ( d1.fetchIntegerData() == 300) ) {
                 d1.storeIntegerData(newInt);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.fetchIntegerData() == newInt ) {
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
            throw new Fault( "fieldTypeTest5 failed");
    }

   /*
     * @testName: fieldTypeTest6
     * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:525
     * @test_Strategy:  The persistent field of an entity may be of the following type:
     *			Java primitive types: long
     */

    public void fieldTypeTest6() throws Fault
    {

      TLogger.log("Begin fieldTypeTest6");
      boolean pass = false;
      long newLong = (long)600;

      try {
         getEntityTransaction().begin();
	 d1 = getEntityManager().find(DataTypes.class, 1);
         if  ( (null != d1) && (d1.fetchLongData() == (long)600) ) {
                 d1.storeLongData(newLong);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.fetchLongData() == newLong ) {
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
            throw new Fault( "fieldTypeTest6 failed");
    }

   /*
     * @testName: fieldTypeTest7
     * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:525
     * @test_Strategy:  The persistent field of an entity may be of the following type:
     *			Java primitive types: double
     */

    public void fieldTypeTest7() throws Fault
    {

      TLogger.log("Begin fieldTypeTest7");
      boolean pass = false;
      double newDbl = (double)80;

      try {
        getEntityTransaction().begin();
	 d1 = getEntityManager().find(DataTypes.class, 1);
         if  ( (null != d1) && ( d1.fetchDoubleData() == ((double)50) ) ) {
                 d1.storeDoubleData(newDbl);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

        if ( d1.fetchDoubleData() == newDbl ) {
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
            throw new Fault( "fieldTypeTest7 failed");
    }

   /*
     * @testName: fieldTypeTest8
     * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:525
     * @test_Strategy:  The persistent field of an entity may be of the following type:
     *			Java primitive types: float
     */

    public void fieldTypeTest8() throws Fault
    {

      TLogger.log("Begin fieldTypeTest8");
      boolean pass = false;
      float expFloat = (float)1.0;
      float floatRange= (float)2.0;
      float newFloat = (float)6.0;
      float newfloatRange= (float)7.0;

      try {
         getEntityTransaction().begin();
	 d1 = getEntityManager().find(DataTypes.class, 1);

	 TLogger.log("float value is: " + d1.fetchFloatData() );

         if  ( (null != d1) && ( (d1.fetchFloatData() >= expFloat ) && ( d1.fetchFloatData() < floatRange )) ){
                 d1.storeFloatData(newFloat);
         }

        getEntityManager().merge(d1);
	getEntityManager().flush();

         if  ( (d1.fetchFloatData() >= newFloat ) && ( d1.fetchFloatData() < newfloatRange ) ){
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
            throw new Fault( "fieldTypeTest8 failed");
    }

    /*
     * @testName: fieldTypeTest9
     * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:529; PERSISTENCE:SPEC:556
     * @test_Strategy:  The persistent field of an entity may be of the following type:
     *                  enums
     *
     *			With the Enumerated annotation and EnumType.STRING.
     */

    public void fieldTypeTest9() throws Fault
    {

      TLogger.log("Begin fieldTypeTest9");
      boolean pass = false;

      try {
         getEntityTransaction().begin();
	 TLogger.log("find DataTypes entity in fieldTypeTest9");
         d1 = getEntityManager().find(DataTypes.class, new Integer(1));

         if (null != d1) {
	 TLogger.log("DataTypes is not null, setting enumData");
             d1.storeEnumData(Grade.B);
         }

        getEntityManager().merge(d1);
        getEntityManager().flush();

	TLogger.log("Update performed, check results");
        if ( (null != d1) && (d1.fetchEnumData().equals(Grade.B)) ) {
		TLogger.log("Expected results received");
                pass = true;
        } else {
	  TLogger.log("ERROR:  Did not get expected results received");
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
            throw new Fault( "fieldTypeTest9 failed");
    }

     /*
      * @testName: fieldTypeTest10
      * @assertion_ids:  PERSISTENCE:SPEC:536; PERSISTENCE:SPEC:540;
      *			 PERSISTENCE:SPEC:550; PERSISTENCE:SPEC:1090.0;
      *			 PERSISTENCE:JAVADOC:216; PERSISTENCE:JAVADOC:217
      * @test_Strategy:  The primary key should be one of the following types:
      *                  java.util.Date
      *			
      *			The application must not change the value of the primary key.
      *			The behavior is undefined if this occurs.
      *
      *			Temporal.TemporalType.DATE
      */

     public void fieldTypeTest10() throws Fault
     {
 
       TLogger.log("Begin fieldTypeTest10");
       boolean pass = false;
 
       try {
          getEntityTransaction().begin();
          TLogger.log("DEBUG:  FIND D2 IN fieldTypeTest10");
          d2 = getEntityManager().find(DataTypes2.class, dateId);
 
         TLogger.log("fieldTypeTest10:  Check results");
         if ( ( null != d2) && (d2.fetchId().equals(dateId)) ) {
         	TLogger.log("Got expected PK of:"  + d2.fetchId() + "received");
                 pass = true;
         } else {
         	TLogger.log("ERROR:  Did not get expected results. "
 			+ "Expected " + dateId + ", got: " + d2.fetchId() );
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
             throw new Fault( "fieldTypeTest10 failed");
       }
 
 
     /*
      * @testName: fieldTypeTest11
      * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:528
      * @test_Strategy:  The persistent property of an entity may be of the following type:
      *			 byte[]
      *
      */
 
     public void fieldTypeTest11() throws Fault
     {
 
       TLogger.log("Begin fieldTypeTest11");
       boolean pass = false;
       byte[] b = { 31, 32, 33, 63, 64, 65 };
       byte bv = 5;
       byte[] a = null;
 
       try {
          getEntityTransaction().begin();
          TLogger.log("DEBUG:  FIND D2 IN fieldTypeTest11");
          d2 = getEntityManager().find(DataTypes2.class, dateId);
 
          if (null != d2) {
              TLogger.log("DataType Entity is not null, setting byteData ");
              d2.storeByteData(b);
 	      a = d2.fetchByteData();
              a[0] = (byte)(a[0] + bv);
              d2.storeByteData(b);
          }
 
         getEntityManager().merge(d2);
         getEntityManager().flush();
 
         TLogger.log("fieldTypeTest11:  Check results");
         if ( ( null != d2) && (Arrays.equals(d2.fetchByteData(),a) ) ) {
         	TLogger.log("fieldTypeTest11: Expected results received");
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
             throw new Fault( "fieldTypeTest11 failed");
       }
 
     /*
      * @testName: fieldTypeTest12
      * @assertion_ids:  PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:528
      * @test_Strategy:  The persistent property of an entity may be of the following type:
      *			 char[]
      *
      */
 
     public void fieldTypeTest12() throws Fault
     {
 
       TLogger.log("Begin fieldTypeTest12");
       boolean pass = false;
 
       try {
          getEntityTransaction().begin();
 	 char[] charData = new char[]{(char)'c', (char)'t', (char)'s'};
          TLogger.log("DEBUG:  FIND D2 IN fieldTypeTest12");
          d2 = getEntityManager().find(DataTypes2.class, dateId);
 
          if (null != d2) {
              TLogger.log("DataType Entity is not null, setting charData ");
              d2.storeCharData(charData);
          }
 
         getEntityManager().merge(d2);
         getEntityManager().flush();
 
         TLogger.log("fieldTypeTest12:  Check results");
         if ( ( null != d2) && (Arrays.equals(d2.fetchCharData(), charData) )) {
         	TLogger.log("fieldTypeTest12: Expected Results Received");
                 pass = true;
         } else {
         	TLogger.log("ERROR:  Did not get expected results. "
 			+ "Expected " + charData + ", got: " + d2.fetchCharData() );
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
             throw new Fault( "fieldTypeTest12 failed");
       }
 
     /*
      * @testName: fieldTypeTest13
      * @assertion_ids: PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:527
      * @test_Strategy: The persistent property of an entity may be of the following type:
      *			java.sql.Time
      */
 
     public void fieldTypeTest13() throws Fault
     {
 
       TLogger.log("Begin fieldTypeTest13");
       boolean pass = false;
       java.sql.Time timeValue = getTimeData(18, 30, 15);
 
       try {
          getEntityTransaction().begin();
          TLogger.log("DEBUG:  FIND D2 IN fieldTypeTest13");
          d2 = getEntityManager().find(DataTypes2.class, dateId);
 
          if (null != d2) {
              TLogger.log("DataType Entity is not null, setting TimeData ");
              d2.storeTimeData(timeValue);
          }
 
         getEntityManager().merge(d2);
         getEntityManager().flush();
 
         TLogger.log("fieldTypeTest13:  Check results");
         if ( ( null != d2) && (d2.fetchTimeData().equals(timeValue)) ) {
         	TLogger.log("fieldTypeTest13: Expected Time Received");
                 pass = true;
         } else {
         	TLogger.log("ERROR:  Did not get expected results. "
 			+" Expected " + timeValue + " , got: "
 			+ d2.fetchTimeData() );
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
             throw new Fault( "fieldTypeTest13 failed");
       }
 
     /*
      * @testName: fieldTypeTest14
      * @assertion_ids: PERSISTENCE:SPEC:524; PERSISTENCE:SPEC:527
      * @test_Strategy: The persistent property of an entity may be of the following type:
      *			java.sql.Timestamp
      */
 
     public void fieldTypeTest14() throws Fault
     {
 
       TLogger.log("Begin fieldTypeTest14");
       boolean pass = false;
       java.sql.Timestamp tsValue = getTimestampData(2006, 02, 11);
 
       try {
          getEntityTransaction().begin();
          TLogger.log("DEBUG:  FIND D2 IN fieldTypeTest14");
          d2 = getEntityManager().find(DataTypes2.class, dateId);
 
          if (null != d2) {
              TLogger.log("DataType Entity is not null, setting TimestampData ");
              d2.storeTsData(tsValue);
          }
 
         getEntityManager().merge(d2);
         getEntityManager().flush();
 
         TLogger.log("fieldTypeTest14:  Check results");
         if ( ( null != d2) && (d2.fetchTsData().equals(tsValue)) ) {
         	TLogger.log("fieldTypeTest14: Expected Timestamp Received");
                 pass = true;
         } else {
         	TLogger.log("ERROR:  Did not get expected results. "
 			+" Expected " + tsValue + " , got: "
 			+ d2.fetchTsData() );
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
             throw new Fault( "fieldTypeTest14 failed");
       }

     /*
      * @testName: fieldTypeTest15
      * @assertion_ids: PERSISTENCE:SPEC:428; PERSISTENCE:SPEC:529;
      *			PERSISTENCE:SPEC:1090.1; PERSISTENCE:SPEC:1090.2
      * @test_Strategy:  enum_expression ::= enum_primary | (subquery)
      *			 enum_primary ::=
      *				statefield_path_expression |
      *				input parameter |
      *				enum_literal
      *
      *				statefield_path_expression
      */
 
     public void fieldTypeTest15() throws Fault
     {
 
       TLogger.log("Begin fieldTypeTest15");
       boolean pass = false;
       Object result = null;
       Query q = null;

       try {

       getEntityTransaction().begin();
       d1 = getEntityManager().find(DataTypes.class, 1);

         if (null != d1)  {
             d1.storeEnumData(Grade.A);
         }

        getEntityManager().merge(d1);
        getEntityManager().flush();

	q = getEntityManager().createQuery(
     		"SELECT dt FROM DataTypes dt WHERE dt.enumData = com.sun.ts.tests.ejb30.persistence.types.common.Grade.A");
 
	result = (DataTypes) q.getSingleResult();

           if ( d1 == result ) {
		pass = true;
                TLogger.log("fieldTypeTest15: Expected results received");
           } else {
             TLogger.log("ERROR:  fieldTypeTest15: Did not get expected results.");
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
             throw new Fault( "fieldTypeTest15 failed");
       }


     /*
      * @testName: fieldTypeTest16
      * @assertion_ids: PERSISTENCE:SPEC:428; PERSISTENCE:SPEC:529;
      *			PERSISTENCE:SPEC:1090.1; PERSISTENCE:SPEC:1090.2
      * @test_Strategy:  enum_expression ::= enum_primary | (subquery)
      *			 enum_primary ::=
      *				state_field_path_expression |
      *				input parameter |
      *				enum_literal
      *
      *				named parameter 
      */
 
     public void fieldTypeTest16() throws Fault
     {
 
       TLogger.log("Begin fieldTypeTest16");
       boolean pass = false;
       Object result = null;
       Query q = null;

       try {

       getEntityTransaction().begin();
       d1 = getEntityManager().find(DataTypes.class, 1);

         if (null != d1)  {
             d1.storeEnumData(Grade.A);
         }

        getEntityManager().merge(d1);
        getEntityManager().flush();

	q = getEntityManager().createQuery(
     		"SELECT dt FROM DataTypes dt WHERE dt.enumData = :grade")
       		.setParameter("grade", Grade.A);
 
	result = (DataTypes) q.getSingleResult();

           if ( d1 == result ) {
		pass = true;
                TLogger.log("fieldTypeTest16: Expected results received");
           } else {
             TLogger.log("ERROR:  fieldTypeTest16: Did not get expected results.");
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
             throw new Fault( "fieldTypeTest16 failed");
       }


     /*
      * @testName: fieldTypeTest17
      * @assertion_ids: PERSISTENCE:SPEC:428; PERSISTENCE:SPEC:529;
      *			PERSISTENCE:SPEC:1090.1; PERSISTENCE:SPEC:1090.2
      * @test_Strategy:  enum_expression ::= enum_primary | (subquery)
      *			 enum_primary ::=
      *				state_field_path_expression |
      *				input parameter |
      *				enum_literal
      *
      *				 positional parameters
      */
 
     public void fieldTypeTest17() throws Fault
     {
 
       TLogger.log("Begin fieldTypeTest17");
       boolean pass = false;
       Object result = null;
       Query q = null;
       Grade failingGrade = com.sun.ts.tests.ejb30.persistence.types.common.Grade.F; 
       Grade incompleteGrade = com.sun.ts.tests.ejb30.persistence.types.common.Grade.INCOMPLETE;

       try {

       getEntityTransaction().begin();
       d1 = getEntityManager().find(DataTypes.class, 1);

         if (null != d1)  {
             d1.storeEnumData(Grade.C);
         }

        getEntityManager().merge(d1);
        getEntityManager().flush();

	q = getEntityManager().createQuery(
     		"SELECT dt FROM DataTypes dt WHERE (dt.enumData <> ?1) OR (dt.enumData <> ?2) ")
       		.setParameter(1, failingGrade)
       		.setParameter(2, incompleteGrade);
 
	result = (DataTypes) q.getSingleResult();

           if ( d1 == result ) {
		pass = true;
                TLogger.log("fieldTypeTest17: Expected results received");
           } else {
             TLogger.log("ERROR:  fieldTypeTest17: Did not get expected results.");
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
             throw new Fault( "fieldTypeTest17 failed");
       }


    // Methods used for Tests

    public void createTestData()
    {
    try {
       getEntityTransaction().begin();
       d1 = new DataTypes(1, false, (byte)100, 'a', (short)100, 300, (long)600,
			(double)50, (float)1.0);

       TLogger.log("DEBUG:  dateId is: " + dateId);
       d2 = new DataTypes2(dateId);

   	getEntityManager().persist(d1);
   	getEntityManager().persist(d2);
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
         d1 = getEntityManager().find(DataTypes.class, 1);

         if ( null != d1 ) {
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
          TLogger.log("Unexpection Exception Removing Entities in Cleanup:" + e );
          e.printStackTrace();
      } finally { 
        try {
		if ( getEntityTransaction().isActive() ) {
		     getEntityTransaction().rollback();
		}
        } catch (Exception re) {
          TLogger.log("Unexpection Exception during Rollback in Cleanup:" + re );
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
