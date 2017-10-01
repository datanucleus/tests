/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Client.java	1.8 06/05/03
 */

package com.sun.ts.tests.ejb30.persistence.types.primarykey.compound;

import javax.naming.InitialContext;
import java.util.Properties;
import com.sun.javatest.Status;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper;


public class Client extends PMClientBase {

    private Properties props = null;
    CompoundPK refPK1 =
                new CompoundPK(1, "cof0001", (float)1.0);
    CompoundPK refPK2 =
                new CompoundPK(2, "cof0002", (float)2.0);
    CompoundPK2 refPK3 =
                new CompoundPK2(3, "cof0003", (float)3.0);
    CompoundPK2 refPK4 =
                new CompoundPK2(4, "cof0004", (float)4.0);
    CompoundPK3 refPK5 =
                new CompoundPK3(5, "cof0005", (float)5.0);
    CompoundPK3 refPK6 =
                new CompoundPK3(6, "cof0006", (float)6.0);

    public Client() {
    }

    public static void main(String[] args) {
        Client theTests = new Client();
        Status s = theTests.run(args, System.out, System.err);
        s.exit();
    }

    public void setup(String[] args, Properties p) throws Fault {
      
	TLogger.log("Entering setup");
	super.setup(args, p);
    } 

 
    /**
     * @testName: testCompoundPK1
     * @assertion_ids: PERSISTENCE:SPEC:1063; PERSISTENCE:SPEC:1064;
     *			PERSISTENCE:SPEC:533; PERSISTENCE:SPEC:547;
     *			PERSISTENCE:SPEC:1127; PERSISTENCE:SPEC:535;
     *			PERSISTENCE:SPEC:544; PERSISTENCE:SPEC:545;
     *			PERSISTENCE:SPEC:546
     * @test_Strategy:
     *     Define a 3.0 Entity bean with a compound primary key
     *
     *	   A composite primary key may either be represented and mapped as an
     *	   embeddable class or may be represented and mapped to multiple fields
     *	   or properties of the entity class.
     *
     *     Using an @Embeddable composite primary key class mapped as a @EmbeddedId,
     *     Check that you can:
     *         - Create bean instances
     *         - Discover these instances with EntityManager.find(EntityClass, primaryKey)
     *         - Remove the beans using EntityManager.remove(Object entity) 
     *
     *	   There should be only one EmbeddedId annotation and no Id annotations when
     *	   the EmbeddedId annotation is used.
     *
     */
    public void testCompoundPK1() throws Fault {

        TestBean bean1 = null;
        TestBean bean2 = null;
        TestBean bean3;
        TestBean bean4;
        CompoundPK valPK1;
        CompoundPK valPK2;
        boolean pass = false;

        try {
	    getEntityTransaction().begin();
            TLogger.log("[testCompoundPK1] Creating bean1 and bean2 instance...");
            bean1 = new TestBean(refPK1, "Arabica", 10);
            bean2 = new TestBean(refPK2, "Java", 12);
	    getEntityManager().persist(bean1);
	    getEntityManager().persist(bean2);
	    getEntityManager().flush();

            TLogger.log("[Client] Locate beans using primary keys...");
            bean3 = getEntityManager().find(TestBean.class, refPK1);
            bean4 = getEntityManager().find(TestBean.class, refPK2);

            TLogger.log("[testCompoundPK1] Check we can call the beans...");
            bean1.ping();
            bean2.ping();
            bean3.ping();
            bean4.ping();

            TLogger.log("[testCompoundPK1] Check beans are identical...");
            if ( ( bean1 == bean3) ) {
		TLogger.log("[testCompoundPK1] bean1 and bean3 are equal");
		pass = true;
	    }
	    if (! pass) {
		throw new Exception("testCompoundPK1: bean1 and bean3 should be identical!");
	    }

            if ( ( bean2 == bean4) ) {
		TLogger.log("[testCompoundPK1] bean2 and bean4 are equal");
		pass = true;
    	    }
	    if (! pass) {
		throw new Exception("[testCompoundPK1] bean2 and bean4 should be identical!");
	    }

            TLogger.log("[testCompoundPK1] Comparing primary keys...");
	    valPK1 = (CompoundPK) bean3.getCompoundPK();
	    valPK2 = (CompoundPK) bean4.getCompoundPK();
	    if ( valPK1.equals(refPK1) && refPK1.equals(valPK1) ) {
		TLogger.log("testCompoundPK1: valPK1 equals refPK1");
		pass = true;
	    }
	    if (! pass) {
		throw new Exception("testCompoundPK1: bean1 and bean3 PK should match!");
	    }
            if ( valPK2.equals(refPK2) && refPK2.equals(valPK2) ) {
		TLogger.log("testCompoundPK1: valPK2 equals refPK2");
		pass = true;
	    }
	    if (! pass) {
		throw new Exception("[testCompoundPK1] bean2 and bean4 PK should match!");
	    }

        } catch (Exception e) {
            TLogger.log("[testCompoundPK1] Caught exception: " + e);
            	throw new Fault("testCompoundPK1 test failed: " + e, e);
        } finally {
            try {
                if (getEntityTransaction().isActive() ) {
		    getEntityTransaction().rollback();
		}
            } catch (Exception e) {
	        TLogger.log("[testCompoundPK1] Exception caught while rolling back TX" + e);
		e.printStackTrace();
	    }
        }

        /* testCompoundPK1 pass */
    }


   /**
     * @testName: testCompoundPK2
     * @assertion_ids: PERSISTENCE:SPEC:1065; PERSISTENCE:SPEC:1066;
     *			PERSISTENCE:JAVADOC:85; PERSISTENCE:SPEC:548;
     *			PERSISTENCE:SPEC:549; PERSISTENCE:SPEC:535;
     *			PERSISTENCE:SPEC:544; PERSISTENCE:SPEC:545;
     *			PERSISTENCE:SPEC:546; PERSISTENCE:SPEC:1102
     *
     * @test_Strategy:
     *     Define a 3.0 Entity bean with a compound primary key
     *
     *	   A composite primary key may either be represented and mapped as an
     *	   embeddable class or may be represented and mapped to multiple fields
     *	   or properties of the entity class.
     *
     *     Using the @IdClass annotation to define a composite primary key class where the
     *	   primary keys are mapped to multiple properties of the entity class,
     *     Check that you can:
     *         - Create bean instances
     *         - Discover these instances with EntityManager.find(EntityClass, primaryKey)
     *         - Remove the beans using EntityManager.remove(Object entity)
     *
     *	  When using the @IdClass annotion, the @Id annotation must also be applied to
     *    such field or properties.
     *	  This entity uses the @Id annotations on the primary key properties.
     *
     */
    public void testCompoundPK2() throws Fault {

        TestBean2 bean1 = null;
        TestBean2 bean2 = null;
        TestBean2 bean3;
        TestBean2 bean4;
        boolean pass = false;

        try {
            getEntityTransaction().begin();
            TLogger.log("[testCompoundPK2] Creating bean1 and bean2 instance...");
            bean1 = new TestBean2(3, "cof0003", (float)3.0, "Vanilla", 10);
            bean2 = new TestBean2(4, "cof0004", (float)4.0, "Mocha Java", 12);
            getEntityManager().persist(bean1);
            getEntityManager().persist(bean2);
            getEntityManager().flush();

            TLogger.log("[testCompoundPK2] Locate beans using primary keys...");
            bean3 = getEntityManager().find(TestBean2.class, refPK3);
            bean4 = getEntityManager().find(TestBean2.class, refPK4);

            TLogger.log("[testCompoundPK2] Check we can call the beans...");
            bean1.ping();
            bean2.ping();
            bean3.ping();
            bean4.ping();

            TLogger.log("[testCompoundPK2] Check beans are identical...");
            if ( ( bean1 == bean3) ) {
                TLogger.log("[testCompoundPK2] bean1 and bean3 are equal");
                pass = true;
            }
            if (! pass) {
                throw new Exception("[testCompoundPK2] bean1 and bean3 should be identical!");
            }

            if ( ( bean2 == bean4) ) {
                TLogger.log("[testCompoundPK2] bean2 and bean4 are equal");
                pass = true;
            }
            if (! pass) {
                throw new Exception("[testCompoundPK2] bean2 and bean4 PK should match!");
            }

        } catch (Exception e) {
            TLogger.log("[testCompoundPK2] Caught exception: " + e);
                throw new Fault("testCompoundPK2 test failed: " + e, e);
        } finally {
            try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
            } catch (Exception e) {
                TLogger.log("[testCompoundPK2] Exception caught while rolling back TX" + e);
                e.printStackTrace();
            }
        }

        /* testCompoundPK2 pass */
    }


   /**
     * @testName: testCompoundPK3
     * @assertion_ids: PERSISTENCE:SPEC:1065; PERSISTENCE:SPEC:1066;
     *			PERSISTENCE:JAVADOC:85; PERSISTENCE:SPEC:548;
     *			PERSISTENCE:SPEC:549; PERSISTENCE:SPEC:535;
     *			PERSISTENCE:SPEC:544; PERSISTENCE:SPEC:545
     * @test_Strategy:
     *     Define a 3.0 Entity bean with a compound primary key
     *
     *     A composite primary key may either be represented and mapped as an
     *     embeddable class or may be represented and mapped to multiple fields
     *     or properties of the entity class.
     *
     *     Using the @IdClass annotation to define a composite primary key class where the
     *     primary keys are mapped to multiple fields of the entity class,
     *     Check that you can:
     *         - Create bean instances
     *         - Discover these instances with EntityManager.find(EntityClass, primaryKey)
     *         - Remove the beans using EntityManager.remove(Object entity)
     *
     *	  When using the @IdClass annotion, the @Id annotation must also be applied to
     *    such fields.
     */
    public void testCompoundPK3() throws Fault {

        TestBean3 bean1 = null;
        TestBean3 bean2 = null;
        TestBean3 bean3;
        TestBean3 bean4;
        boolean pass = false;

        try {
            getEntityTransaction().begin();
            TLogger.log("[testCompoundPK3] Creating bean1 and bean2 instance...");
            bean1 = new TestBean3(5, "cof0005", (float)5.0, "Cinnamon", 11);
            bean2 = new TestBean3(6, "cof0006", (float)6.0, "Hazelnut", 12);
            getEntityManager().persist(bean1);
            getEntityManager().persist(bean2);
            getEntityManager().flush();

            TLogger.log("[testCompoundPK3] Locate beans using primary keys...");
            bean3 = getEntityManager().find(TestBean3.class, refPK5);
            bean4 = getEntityManager().find(TestBean3.class, refPK6);

            TLogger.log("[testCompoundPK3] Check we can call the beans...");
            bean1.ping();
            bean2.ping();
            bean3.ping();
            bean4.ping();

            TLogger.log("[testCompoundPK3] Check beans are identical...");
            if ( ( bean1 == bean3) ) {
                TLogger.log("[testCompoundPK3] bean1 and bean3 are equal");
                pass = true;
            }
            if (! pass) {
                throw new Exception("[testCompoundPK3] bean1 and bean3 should be identical!");
            }

            if ( ( bean2 == bean4) ) {
                TLogger.log("[testCompoundPK3] bean2 and bean4 are equal");
                pass = true;
            }
            if (! pass) {
                throw new Exception("[testCompoundPK3] bean2 and bean4 PK should match!");
            }

        } catch (Exception e) {
            TLogger.log("[testCompoundPK3] Caught exception: " + e);
                throw new Fault("testCompoundPK3 test failed: " + e, e);
        } finally {
            try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
            } catch (Exception e) {
                TLogger.log("[testCompoundPK3] Exception caught while rolling back TX" + e);
                e.printStackTrace();
            }
        }

        /* testCompoundPK3 pass */
    }


    public void cleanup()  throws Fault {
    try {

        getEntityTransaction().begin();

        TestBean b1 = getEntityManager().find(TestBean.class, refPK1);
        TestBean b2 = getEntityManager().find(TestBean.class, refPK2);

        TestBean2 b3 = getEntityManager().find(TestBean2.class, refPK3);
        TestBean2 b4 = getEntityManager().find(TestBean2.class, refPK4);

        TestBean3 b5 = getEntityManager().find(TestBean3.class, refPK5);
        TestBean3 b6 = getEntityManager().find(TestBean3.class, refPK6);

         if (b1 != null ) {
            getEntityManager().remove(b1);
	    TLogger.log("removed TestBean, " + refPK1);
         }

         if (b2 != null ) {
            getEntityManager().remove(b2);
	    TLogger.log("removed TestBean, " + refPK2);
         }

         if (b3 != null ) {
            getEntityManager().remove(b3);
	    TLogger.log("removed TestBean2, " + refPK3);
         }

         if (b4 != null ) {
            getEntityManager().remove(b4);
	    TLogger.log("removed TestBean2, " + refPK4);
         }

         if (b5 != null ) {
            getEntityManager().remove(b5);
	    TLogger.log("removed TestBean3, " + refPK5);
         }

         if (b6 != null ) {
            getEntityManager().remove(b6);
	    TLogger.log("removed TestBean3, " + refPK6);
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

