 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.14 06/09/11
 */

package com.sun.ts.tests.ejb30.persistence.annotations.nativequery;

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
import java.util.List;
import java.util.Arrays;
import java.util.Properties;
import java.util.Iterator;

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
        } catch (Exception e) {
          TLogger.log("Exception: " + e.getMessage());
          throw new Fault("Setup failed:", e);
        }
    }

    /*
     * @testName: nativeQueryTest1
     * @assertion_ids: PERSISTENCE:SPEC:1009; PERSISTENCE:JAVADOC:199;
     *			PERSISTENCE:JAVADOC:64; PERSISTENCE:JAVADOC:41
     * @test_Strategy:  When multiple entities are returned by a SQL query, the entities must be
     *			mapped to the column results of the SQL statement in a SqlResultSetMapping
     *			metadata definition.
     *
     *			The query with the associated SqlResultSetMapping metadata
     *			returns multiple entities and uses default metadata and column name
     *			defaults.
     */

   public void nativeQueryTest1() throws Fault
   {

     TLogger.log("Begin nativeQueryTest1");
     boolean pass1 = true;
     boolean pass2 = false;
     int foundOrder1 = 0;
     int foundItem1 = 0;
     List q = null;

     try {
       getEntityTransaction().begin();

   	    TLogger.log("nativeQueryTest1: Create Items");
            Item i1 = new Item("4", "WaterShoes");
            Item i2 = new Item("5", "FlipFlops");
            Item i3 = new Item("6", "Sandals");

            TLogger.log("nativeQueryTest1: Create Order1s");
            Order1 o4 = new Order1("4", (double)25.0);
            o4.setItem(i1);
            getEntityManager().persist(o4);
            Order1 o5 = new Order1("5", (double)125.0);
            o5.setItem(i2);
            getEntityManager().persist(o5);
            Order1 o6 = new Order1("6", (double)150.0);
            o6.setItem(i3);
            getEntityManager().persist(o6);

            TLogger.log("nativeQueryTest1 - Execute Query ");
            q = getEntityManager().createNativeQuery(
                "Select o.ID, o.TOTALPRICE, " +
                " o.FK1_FOR_ITEM, i.ID, i.ITEMNAME from ORDER1 o, ITEM i " +
                "WHERE (o.TOTALPRICE > 100) AND (o.FK1_FOR_ITEM = i.ID)",  "Order1ItemResults")
                .getResultList();

          if ( q.size()  != 2 ) {
                TLogger.log("nativeQueryTest1:  Did not get expected results.  Expected: 2, "
                        + "got: " + q.size() );
                pass1 = false;
           } else if (pass1) {
             TLogger.log("nativeQueryTest1: Expected size received, verify contents . . . ");
             for(int i = 0; i < q.size(); i++) {
                Object obj = q.get(i);
                Object[] orderAndItem = null;

                //each element in the query result list should be an Object[], which
                //has exactly 2 elements. The first element is of type Order1, and the
                //second of type Item.
                if(obj instanceof Object[]) {
                    Object[] objectArray = (Object[]) obj;

                    //make sure this object array has exactly 2 elements.
                    if(objectArray.length != 2) {
                        pass2 = false;
                        TLogger.log("Expecting the object array have 2 elements, " +
						"but the object array is " + objectArray);
                        break;
                    }

                    Object object1 = objectArray[0];
                    Object object2 = objectArray[1];
                    Order1 orderReturned = null;
                    Item itemReturned  = null;

                    if(object1 instanceof Order1) {
                        orderReturned = (Order1) object1;

		 	if ( orderReturned == o5 || orderReturned == o6 ) {
				foundOrder1++;
			}

                    } else {
                        //unexpected
                        pass2 = false;
                        TLogger.log("Expecting this element to be of type Order1, but actual " +
                            object1);
                        break;
                   }

                   if(object2 instanceof Item) {
                        itemReturned = (Item) object2;
		 	if ( itemReturned == i2 || itemReturned == i3 ) {
				foundItem1++;
			}

                    } else {
                        //unexpected
                        pass2 = false;
                        TLogger.log("Expecting this element to be of type Item, but actual " +
                            object2);
                        break;
                    }
                } else {
                  pass2 = false;
                  TLogger.log("nativeQueryTest1: ERROR: The element in the result list is " +
					"not of type Object[]:" + obj);
                  break;
                }
            }
         }

         if ((foundOrder1 != 2) || (foundItem1 != 2)) {
                  TLogger.log("ERROR: nativeQueryTest1: Did not get expected results");
                      pass2 = false;
         } else {
            	TLogger.log("nativeQueryTest1: Expected results received");
            	pass2 = true;
         }

       getEntityTransaction().commit();

     } catch (Exception e) {
         TLogger.log("Unexpection Exception :" + e );
         pass1 = false;
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

     if (!pass1 || !pass2 )
            throw new Fault( "nativeQueryTest1 failed");
     }


    /*
     * @testName: nativeQueryTest2
     * @assertion_ids: PERSISTENCE:SPEC:1010; PERSISTENCE:SPEC:1011;
     *			PERSISTENCE:JAVADOC:198; PERSISTENCE:JAVADOC:64;
     *			PERSISTENCE:JAVADOC:77; PERSISTENCE:JAVADOC:78
     * @test_Strategy:  When multiple entities are returned by a SQL query, the entities must be
     *			mapped to the column results of the SQL statement in a SqlResultSetMapping
     *			metadata definition.
     *
     *			Scalar result types can be included in the query result by
     *			specifying the ColumnResult annotation in the metadata.

     *
     */

   public void nativeQueryTest2() throws Fault
   {

     TLogger.log("Begin nativeQueryTest2");
     boolean pass1 = true;
     boolean pass2 = true;
     List q = null;
     int foundOrder2 = 0;
     int foundItem2 = 0;

     try {
       getEntityTransaction().begin();

            TLogger.log("nativeQueryTest2: Create Items");
            Item i1 = new Item("7", "Loafers");
            Item i2 = new Item("8", "High Heels");
            Item i3 = new Item("9", "Socks");

            TLogger.log("nativeQueryTest2: Create Orders");

	    Order1 o7 = new Order1("7", (double)25.0);
            o7.setItem(i1);
            getEntityManager().persist(o7);
            Order1 o8 = new Order1("8", (double)125.0);
            o8.setItem(i2);
            getEntityManager().persist(o8);
            Order1 o9 = new Order1("9", (double)150.0);
            o9.setItem(i3);
            getEntityManager().persist(o9);


            TLogger.log("nativeQueryTest2 - Execute Query ");
            q = getEntityManager().createNativeQuery(
                "Select o.ID AS OID, o.TOTALPRICE AS OPRICE, " +
   		 "o.FK1_FOR_ITEM AS OITEM, i.ITEMNAME AS INAME from ORDER1 o, ITEM i " +
                "WHERE (o.TOTALPRICE < 100) AND (o.FK1_FOR_ITEM = i.ID)", "Order2ItemResults")
               .getResultList();

            if(q.size() != 1) {
                TLogger.log(
                "ERROR:  Did not get expected results.  Expected 2 references, got: "
                                + q.size());
              pass1 = false;
            } else if (pass1) {
              TLogger.log("nativeQueryTest2: Expected size received, verify contents . . . ");
              for(int i = 0; i < q.size(); i++) {
                Object obj = q.get(i);
                Object[] orderAndItem = null;

                //each element in the query result list should be an Object[], which
                //has exactly 2 elements. The first element is of type Order1, and the
                //second of type String.
                if(obj instanceof Object[]) {
                    Object[] objectArray = (Object[]) obj;

                    //make sure this object array has exactly 2 elements.
                    if(objectArray.length != 2) {
                        pass2 = false;
                        TLogger.log("Expecting the object array have 2 elements, " +
                                                "but the object array is " + objectArray);
                        break;
                    }

		    Object object1 = objectArray[0];
                    Object object2 = objectArray[1];
                    Order1 orderReturned = null;
                    String itemReturned  = null;

                    if(object1 instanceof Order1) {
                        orderReturned = (Order1) object1;
                        if ( orderReturned == o7 ) {
                                foundOrder2++;
			}
                    } else {
                        //unexpected
                        pass2 = false;
                        TLogger.log("Expecting this element to be of type Order1, but actual " +
                            object1);
                        break;
                    }
                    if(object2 instanceof String) {
                        itemReturned = (String) object2;
                        if ( itemReturned.equals("Loafers")) {
                                foundItem2++;
                        }
                    } else {
                        //unexpected
                        pass2 = false;
                        TLogger.log("Expecting this element to be Loafers, but actual " +
                            object2);
                        break;
                    }
                } else {
                  pass2 = false;
                  TLogger.log("nativeQueryTest2: ERROR: The element in the result list is " +
                                        "not of type Object[]:" + obj);
                  break;
                }
            }
         }


         if ((foundOrder2 != 1) || (foundItem2 != 1)) {
                  TLogger.log("ERROR: nativeQueryTest2: Did not get expected results");
                      pass2 = false;
         } else {
                TLogger.log("nativeQueryTest2: Expected results received");
                pass2 = true;
         }


       getEntityTransaction().commit();

     } catch (Exception e) {
         TLogger.log("Unexpection Exception :" + e );
         pass1 = false;
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

      if (!pass1 || !pass2 )
            throw new Fault( "nativeQueryTest2 failed");
     }


    /*
     * @testName: nativeQueryTest3
     * @assertion_ids: PERSISTENCE:SPEC:1012; PERSISTENCE:JAVADOC:200;
     *			PERSISTENCE:SPEC:1014; PERSISTENCE:SPEC:1015;
     *			PERSISTENCE:SPEC:1016; PERSISTENCE:JAVADOC:201
     * @test_Strategy:  When multiple entities are returned by a SQL query, the entities must be
     *			mapped to the column results of the SQL statement in a SqlResultSetMapping
     *			metadata definition.
     *
     *			When multiple entity types that include aliases in the SQL
     *			statement are used, it is required that the column names 
     *			be explicitly mapped to the entity fields.  The FieldResult
     *			annotation is used for this purpose.
     *	
     */

   public void nativeQueryTest3() throws Fault
   {
     TLogger.log("Begin nativeQueryTest3");
     boolean pass1 = true;
     boolean pass2 = false;
     List q = null;
     int foundOrder3 = 0;
     int foundItem3 = 0;

     try {
       getEntityTransaction().begin();

            TLogger.log("Create Items");
	    Item i1 = new Item("1", "Boots");
	    Item i2 = new Item("2", "Sneakers");
	    Item i3 = new Item("3", "Slippers");

            TLogger.log("Create Orders");
	    Order1 o1 = new Order1("1", (double)25.0);
	    o1.setItem(i1);
	    getEntityManager().persist(o1);
	    Order1 o2 = new Order1("2", (double)125.0);
	    o2.setItem(i2);
	    getEntityManager().persist(o2);
	    Order1 o3 = new Order1("3", (double)150.0);
	    o3.setItem(i3);
	    getEntityManager().persist(o3);

            TLogger.log("nativeQueryTest3 - Execute Query ");
  	    q = getEntityManager().createNativeQuery(
                "Select o.ID AS THISID, o.TOTALPRICE AS THISPRICE, " +
                "o.FK1_FOR_ITEM AS THISITEM, i.ID, i.ITEMNAME from ORDER1 o, ITEM i " +
                "WHERE (o.TOTALPRICE > 100) AND (o.FK1_FOR_ITEM = i.ID)", "Order3ItemResults")
                .getResultList();

          if ( q.size()  != 2 ) {
                TLogger.log("nativeQueryTest3:  Did not get expected results.  Expected: 2, "
                        + "got: " + q.size() );
                pass1 = false;
           } else if (pass1) {
             TLogger.log("nativeQueryTest3: Expected size received, verify contents . . . ");
             for(int i = 0; i < q.size(); i++) {
                Object obj = q.get(i);
                Object[] orderAndItem = null;

                //each element in the query result list should be an Object[], which
                //has exactly 2 elements. The first element is of type Order1, and the
                //second of type Item.
                if(obj instanceof Object[]) {
                    Object[] objectArray = (Object[]) obj;

                    //make sure this object array has exactly 2 elements.
                    if(objectArray.length != 2) {
                        pass2 = false;
                        TLogger.log("Expecting the object array have 2 elements, " +
                                                "but the object array is " + objectArray);
                        break;
                    }

                    Object object1 = objectArray[0];
                    Object object2 = objectArray[1];
                    Order1 orderReturned = null;
                    Item itemReturned  = null;

                    if(object1 instanceof Order1) {
                        orderReturned = (Order1) object1;
                        if ( orderReturned == o2 || orderReturned == o3 ) {
                                foundOrder3++;
			}
                    } else {
                        //unexpected
                        pass2 = false;
                        TLogger.log("Expecting this element to be of type Order1, but actual " +
                            object1);
                        break;
                    }
                    if(object2 instanceof Item) {
                        itemReturned = (Item) object2;
                        if ( itemReturned == i2 || itemReturned == i3 ) {
                                foundItem3++;
                        }
                    } else {
                        //unexpected
                        pass2 = false;
                        TLogger.log("Expecting this element to be of type Item, but actual " +
                            object2);
                        break;
                    }
                } else {
                  pass2 = false;
                  TLogger.log("nativeQueryTest3: ERROR: The element in the result list is " +
                                        "not of type Object[]:" + obj);
                  break;
                }
            }

         if ((foundOrder3 != 2) || (foundItem3 != 2)) {
                  TLogger.log("ERROR: nativeQueryTest1: Did not get expected results");
                      pass2 = false;
         } else {
                TLogger.log("nativeQueryTest3: Expected results received");
                pass2 = true;
           }
         }

	getEntityTransaction().commit();

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

     if ( !pass2 )
            throw new Fault( "nativeQueryTest3 failed");
     }

    public void cleanup()  throws Fault
    {
	try {
	  TLogger.log("Entering cleanup . . ." );
	  getEntityTransaction().begin();

          for (int i=1; i<10; i++ ) {
          Order1 neworder1 = getEntityManager().find(Order1.class, Integer.toString(i));
		if (neworder1 != null ) {
                    getEntityManager().remove(neworder1);
                    TLogger.log("removed order " + neworder1);
		}
          }
          for (int i=1; i<10; i++ ) {
          Item newItem = getEntityManager().find(Item.class, Integer.toString(i));
		if (newItem != null ) {
                    getEntityManager().remove(newItem);
                    TLogger.log("removed item " + newItem);
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


}

