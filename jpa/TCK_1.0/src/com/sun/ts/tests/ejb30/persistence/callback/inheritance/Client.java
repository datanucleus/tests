 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)Client.java	1.12 06/07/27
 */

package com.sun.ts.tests.ejb30.persistence.callback.inheritance;

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
import javax.persistence.Query;
import com.sun.ts.tests.ejb30.persistence.callback.common.Constants;
import com.sun.ts.tests.ejb30.persistence.callback.common.EntityCallbackClientBase;
import java.util.List;
import java.util.Iterator;

public class Client extends EntityCallbackClientBase 
        implements java.io.Serializable, Constants  {
    private PricedPartProduct p1;
    private PricedPartProduct_2 p2;
    
    public Client() {
        super();
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

        }  catch (Exception e) {
            TLogger.log("Exception: " + e.getMessage());
            throw new Fault("Setup failed:", e);
        }
    }

    public void cleanup() throws Fault {
    try {

        getEntityTransaction().begin();

        TLogger.log("Find All PricedPartProducts");
        List p = getEntityManager().createQuery(
                "Select DISTINCT p From PricedPartProduct p where p.id LIKE '%Test%'")
                .setMaxResults(20)
                .getResultList();

        TLogger.log("Find All PricedPartProduct_2s");
        List p2 = getEntityManager().createQuery(
                "Select DISTINCT p2 From PricedPartProduct_2 p2 where p2.id LIKE '%Test%'")
                .setMaxResults(20)
                .getResultList();

  	if ( p.size() != 0 ) {
             TLogger.log("PricedPartProducts found: cleaning up ");
             Iterator iterator = p.iterator();
             while(iterator.hasNext()) {
             PricedPartProduct pRef = (PricedPartProduct) iterator.next();
             PricedPartProduct newP = getEntityManager().find(PricedPartProduct.class,
						(String)pRef.getId() );
	     TLogger.log("DEBUG: Found PricedPartProduct: " + pRef.getId() );
                if (newP != null ) {
		    removeEntity(newP);
                }
             }
         }

  	if ( p2.size() != 0 ) {
             TLogger.log("PricedPartProduct_2 found: cleaning up ");
             Iterator iterator = p2.iterator();
             while(iterator.hasNext()) {
             PricedPartProduct_2 p2Ref = (PricedPartProduct_2) iterator.next();
             PricedPartProduct_2 newP2 =
			 getEntityManager().find(PricedPartProduct_2.class, (String)p2Ref.getId() );
	     TLogger.log("DEBUG: Found PricedPartProduct_2: " + p2Ref.getId() );
                if (newP2 != null ) {
		    removeEntity(newP2);
                }
             }
         }
        getEntityTransaction().commit();

        } catch (Exception e) {
          TLogger.log("Exception caught in clean up removing entities", e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
            TLogger.log("cleanup complete, calling super.cleanup");
	    super.cleanup();
        }
    }
     
    private PricedPartProduct_2 newPricedPartProduct_2(String id) {
        PricedPartProduct_2 product = new PricedPartProduct_2();
        product.setTestName(id);
        product.setId(id);
        product.setName(id);
        product.setPartNumber((long) 1);
        product.setPrice((double) 1);
        product.setQuantity(1);
        return product;
    }
    
    private PricedPartProduct newPricedPartProduct(String id) {
        PricedPartProduct product = new PricedPartProduct();
        product.setTestName(id);
        product.setId(id);
        product.setName(id);
        product.setPartNumber((long) 1);
        product.setPrice((double) 1);
        product.setQuantity(1);
        return product;
    }
    
    /////////////////////////////////////////////////////////////////////////
    
    /*
     * @testName: prePersistTest
     * @assertion_ids:  PERSISTENCE:SPEC:700; PERSISTENCE:SPEC:708;
     *			PERSISTENCE:SPEC:1067; PERSISTENCE:SPEC:997;
     *			PERSISTENCE:SPEC:999
     * @test_Strategy:
     */
    public void prePersistTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = prePersistTest;
	try {
	  getEntityTransaction().begin();
        	p1 = newPricedPartProduct(id);
        	getEntityManager().persist(p1);
	
        	if(p1.isPrePersistCalled()) {
            		reason = "PricedPartProduct: prePersist was called.";
            		TLogger.log(reason);
        	} else {
            		reason = "PricedPartProduct: prePersist was not called.";
            		throw new Fault(reason);
        	}
          getEntityTransaction().rollback();
        } catch (Exception e) {
          TLogger.log("Exception caught during prePersistTest", e);
	  throw new Fault(e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
        }
    }
    
    /*
     * @testName: prePersistTest2
     * @assertion_ids:  PERSISTENCE:SPEC:700; PERSISTENCE:SPEC:708
     * @test_Strategy:
     */
    public void prePersistTest2() throws Fault {
        boolean status = false;
        String reason = null;
        String id = prePersistTest2;
	try {
	  getEntityTransaction().begin();
        	p2 = newPricedPartProduct_2(id);
        	getEntityManager().persist(p2);

        	if(p2.isPrePersistCalled()) {
            		reason = "PricedPartProduct_2: prePersist was called.";
            		TLogger.log(reason);
        	} else {
            		reason = "PricedPartProduct_2: prePersist was not called.";
            		throw new Fault(reason);
        	}
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Exception caught during prePersistTest", e);
	  throw new Fault(e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
        }
    }
   
    
    /*
     * @testName: preRemoveTest
     * @assertion_ids:  PERSISTENCE:SPEC:700; PERSISTENCE:SPEC:708
     * @test_Strategy:
     */
    public void preRemoveTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = preRemoveTest;
	try {
	   getEntityTransaction().begin();
        	p1 = newPricedPartProduct(id);
        	getEntityManager().persist(p1);
        	getEntityManager().remove(p1);

        	if(p1.isPreRemoveCalled()) {
            		reason = "PricedPartProduct: preRemove was called.";
            		TLogger.log(reason);
        	} else {
            		reason = "PricedPartProduct: preRemove was not called.";
            		throw new Fault(reason);
        	}
        	p1 = null;
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Exception caught during prePersistTest", e);
	  throw new Fault(e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
        }
    }
    
    /*
     * @testName: preRemoveTest2
     * @assertion_ids:  PERSISTENCE:SPEC:700; PERSISTENCE:SPEC:708
     * @test_Strategy:
     */
    public void preRemoveTest2() throws Fault {
        boolean status = false;
        String reason = null;
        String id = preRemoveTest2;
	try {
	   getEntityTransaction().begin();
        	p2 = newPricedPartProduct_2(id);
        	getEntityManager().persist(p2);
        	getEntityManager().remove(p2);

        	if(p2.isPreRemoveCalled()) {
            		reason = "PricedPartProduct: preRemove was called.";
            		TLogger.log(reason);
        	} else {
            		reason = "PricedPartProduct: preRemove was not called.";
            		throw new Fault(reason);
        	}
        	p2 = null;
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Exception caught during prePersistTest", e);
	  throw new Fault(e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
        }
    }
    
    /*
     * @testName: preUpdateTest
     * @assertion_ids:  PERSISTENCE:SPEC:700; PERSISTENCE:SPEC:716
     * @test_Strategy:
     */
    public void preUpdateTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = preUpdateTest;
	try {
	   getEntityTransaction().begin();
        	p1 = newPricedPartProduct(id);
        	getEntityManager().persist(p1);
		getEntityManager().flush();
        	p1.setPrice((double) 2);
        	getEntityManager().persist(p1);
          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Exception caught during preUpdateTest", e);
	  throw new Fault(e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
        }
    }
    
    /*
     * @testName: preUpdateTest2
     * @assertion_ids:  PERSISTENCE:SPEC:700; PERSISTENCE:SPEC:716
     * @test_Strategy:
     */
    public void preUpdateTest2() throws Fault {
        boolean status = false;
        String reason = null;
        String id = preUpdateTest2;
	try {
	   getEntityTransaction().begin();
        	p2 = newPricedPartProduct_2(id);
        	getEntityManager().persist(p2);
		getEntityManager().flush();
        	p2.setPrice((double) 2);
        	getEntityManager().persist(p2);
		getEntityManager().flush();

          getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Exception caught during preUpdateTest2", e);
	  throw new Fault(e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
        }
    }
    
    
    /*
     * @testName: postLoadTest
     * @assertion_ids:  PERSISTENCE:SPEC:700; PERSISTENCE:SPEC:719
     * @test_Strategy:
     */
    public void postLoadTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = postLoadTest;
        try {
          getEntityTransaction().begin();
        	p1 = newPricedPartProduct(id);
        	getEntityManager().persist(p1);
		getEntityManager().flush();
		getEntityManager().refresh(p1);
        	Query q = getEntityManager().createQuery("select distinct p from PricedPartProduct p");
        	java.util.List results = q.getResultList();
//        	for(int i = 0, n = results.size(); i < n; i++) {
//            
//        	}
        	TLogger.log(results.toString());

        	if(p1.isPostLoadCalled()) {
            		reason = "PricedPartProduct: postLoad was called after the query result was returned.";
            		TLogger.log(reason);
        	} else {
            		reason = "PricedPartProduct: postLoad was not called even after the query result was returned.";
            		throw new Fault(reason);
        	}
          getEntityTransaction().commit();

        } catch (Exception e) {
          TLogger.log("Exception caught during postLoadTest", e);
	  throw new Fault(e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
        }
    }
    
    /*
     * @testName: postLoadTest2
     * @assertion_ids:  PERSISTENCE:SPEC:700; PERSISTENCE:SPEC:719
     * @test_Strategy:
     */
    public void postLoadTest2() throws Fault {
        boolean status = false;
        String reason = null;
        String id = postLoadTest2;
        try {
          getEntityTransaction().begin();
        	p2 = newPricedPartProduct_2(id);
        	getEntityManager().persist(p2);
		getEntityManager().flush();
		getEntityManager().refresh(p2);
        	Query q = getEntityManager().createQuery("select p.id from PricedPartProduct_2 p");
        	java.util.List results = q.getResultList();
//        	for(int i = 0, n = results.size(); i < n; i++) {
//            
//        	}
        	TLogger.log(results.toString());

        	if(p2.isPostLoadCalled()) {
            		reason = "PricedPartProduct_2: postLoad was called after the query result was returned.";
            		TLogger.log(reason);
        	} else {
            		reason = "PricedPartProduct_2: postLoad was not called even after the query result was returned.";
            		throw new Fault(reason);
        	}
          getEntityTransaction().commit();

        } catch (Exception e) {
          TLogger.log("Exception caught during postLoadTest", e);
	  throw new Fault(e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
        }
    }
    
    
    /*
     * @testName: prePersistRuntimeExceptionTest
     * @assertion_ids:  PERSISTENCE:SPEC:700; PERSISTENCE:SPEC:704
     * @test_Strategy:
     */
    public void prePersistRuntimeExceptionTest() throws Fault {
        String id = prePersistRuntimeExceptionTest;
	try {
	  getEntityTransaction().begin();
        	p1 = newPricedPartProduct(id);
        	txShouldRollback(p1, id);
        } catch (Exception e) {
          TLogger.log("Exception caught during prePersistRuntimeExceptionTest", e);
	  throw new Fault(e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
        }
    }
    
    /*
     * @testName: prePersistRuntimeExceptionTest2
     * @assertion_ids:  PERSISTENCE:SPEC:700; PERSISTENCE:SPEC:704
     * @test_Strategy:
     */
    public void prePersistRuntimeExceptionTest2() throws Fault {
        String id = prePersistRuntimeExceptionTest2;
	try {
	  getEntityTransaction().begin();
        	p2 = newPricedPartProduct_2(id);
        	txShouldRollback(p2, id);
        } catch (Exception e) {
          TLogger.log("Exception caught during prePersistRuntimeExceptionTest2", e);
	  throw new Fault(e);
        } finally {
          try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
          } catch (Exception re) {
            TLogger.log("Exception caught while rolling back TX", re);
          }
        }
    }
    
    /*
     * @testName: findProductTest
     * @assertion_ids:  PERSISTENCE:SPEC:735
     * @test_Strategy:
     */
    public void findProductTest() throws Fault {
    String id = "findProductTest";
    try {
  	getEntityTransaction().begin();
        p1 = newPricedPartProduct(id);
        getEntityManager().persist(p1);
	getEntityManager().flush();
        Object o = getEntityManager().find(Product.class, id);
        TLogger.log("finding object using Product.class and id '" + id + "'");

        if(o instanceof PricedPartProduct) {
         	TLogger.log("found object of type " + PricedPartProduct.class);
        } else if(o instanceof Product) {
            	TLogger.log("found object of type " + Product.class);
        } else {
          TLogger.log("the object found is neither " + 
          	PricedPartProduct.class + ", nor " +
          	Product.class);
        }
        
        Object oo = getEntityManager().find(PricedPartProduct.class, id);
        TLogger.log("finding object using PricedPartProduct.class and id '" + id + "'");
        if(oo instanceof PricedPartProduct) {
            TLogger.log("found object of type " + PricedPartProduct.class);
        } else if(oo instanceof Product) {
            TLogger.log("found object of type " + Product.class);
        } else {
            TLogger.log("the object found is neither " + 
                    PricedPartProduct.class + ", nor " +
                    Product.class);
        }
        
        if(o == oo) {
            TLogger.log("The two entities are identical");
        } else if(o.equals(oo)) {
            TLogger.log("The two entities are equal");
        }
               getEntityTransaction().commit();
    } catch (Exception e) {
      TLogger.log("Exception caught during findProductTest", e);
      throw new Fault(e);
    } finally {
      try {
                if (getEntityTransaction().isActive() ) {
                    getEntityTransaction().rollback();
                }
      } catch (Exception re) {
        TLogger.log("Exception caught while rolling back TX", re);
      }
    }
  }
   
}

