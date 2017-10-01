 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)Client.java	1.16 06/04/12
 */

package com.sun.ts.tests.ejb30.persistence.callback.listener;

import com.sun.ts.tests.ejb30.common.helper.Helper;
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
    private Product product;
    private Order order;
    private LineItem lineItem;
    private Iterator iterator;
    
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

        TLogger.log("Find All Products");
        List p = getEntityManager().createQuery(
                "Select DISTINCT p From Product p where p.id LIKE '%Test%'")
                .setMaxResults(50)
                .getResultList();
         
        TLogger.log("Find All Orders");
        List o = getEntityManager().createQuery(
                "Select DISTINCT Object(o) From Order o where o.id LIKE '%Test%'")
                .setMaxResults(35)
                .getResultList();
         
        TLogger.log("Find All LineItems");
        List l = getEntityManager().createQuery(
                "Select DISTINCT Object(l) From LineItem l where l.id LIKE '%Test%'")
                .setMaxResults(70)
                .getResultList();

	 if ( p.size() != 0 ) {
             TLogger.log("Products found: cleaning up ");
             iterator = p.iterator();
             while(iterator.hasNext()) {
             Product pRef = (Product) iterator.next();
             Product newP = getEntityManager().find(Product.class, (String)pRef.getId() );
                if (newP != null ) {
                    removeEntity(newP);
                }
             }
         } 

        if ( l.size() != 0 ) {
            TLogger.log("LineItems found: cleaning up ");
            iterator = l.iterator();
            while(iterator.hasNext()) {
            LineItem lRef = (LineItem) iterator.next();
            LineItem newL = getEntityManager().find(LineItem.class, (String)lRef.getId() );
                if (newL != null ) {
                        removeEntity(newL);
                }
            } 
         } 

 	 if ( o.size() != 0 ) {
            TLogger.log("Orders found: cleaning up ");
            iterator = o.iterator();
            while(iterator.hasNext()) {
            Order oRef = (Order)iterator.next();
            Order newO = getEntityManager().find(Order.class, (String)oRef.getId() );
               if (newO != null ) {
                   removeEntity(newO);
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
        }
        TLogger.log("Cleanup done, calling super.cleanup");
	super.cleanup();
    }
    
    /*
     * @testName: prePersistTest
     * @assertion_ids:  PERSISTENCE:SPEC:695; PERSISTENCE:SPEC:708;
     *			PERSISTENCE:SPEC:701; PERSISTENCE:JAVADOC:34
     * @test_Strategy:
     */
    public void prePersistTest() throws Fault {
      boolean status = false;
      String reason = null;
      String id = prePersistTest;
      try {
                getEntityTransaction().begin();
                product = newProduct(id);
                getEntityManager().persist(product);
		getEntityManager().flush();

                if(product.isPrePersistCalled()) {
                        reason = "Product: prePersist was called.";
                        TLogger.log(reason);
                } else {
                        reason = "Product: prePersist was not called.";
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
     * @testName: prePersistMultiTest
     * @assertion_ids:  PERSISTENCE:SPEC:694; PERSISTENCE:SPEC:697;
     *			PERSISTENCE:SPEC:722
     * @test_Strategy:
     */
    public void prePersistMultiTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = prePersistMultiTest;
	try {
		getEntityTransaction().begin();
        	product = newProduct(id);
       	 	getEntityManager().persist(product);

                List actual = product.getPrePersistCalls();
        	Helper.compareResultList(LISTENER_ABC, actual);
		getEntityTransaction().commit();	
	} catch (Exception e) {
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
     * @testName: prePersistCascadeTest
     * @assertion_ids:  PERSISTENCE:SPEC:695; PERSISTENCE:SPEC:708
     * @test_Strategy:
     */
    public void prePersistCascadeTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = prePersistCascadeTest;
        try {
                getEntityTransaction().begin();
                order = newOrder(id);
                product = newProduct(id);
                lineItem = newLineItem(id);
                lineItem.setOrder(order);
                lineItem.setProduct(product);
                order.addLineItem(lineItem);
                getEntityManager().persist(product);
                getEntityManager().persist(order);
		getEntityManager().flush();

                if(order.isPrePersistCalled()) {
                        reason = "Order: prePersist was called.";
                        TLogger.log(reason);
                } else {
                        reason = "Order: prePersist was not called.";
                        throw new Fault(reason);
                }

                if(lineItem.isPrePersistCalled()) {
                        reason = "LineItem: prePersist was called.";
                        TLogger.log(reason);
                } else {
                        reason = "LineItem: prePersist was not called.";
                        throw new Fault(reason);
                }
                getEntityTransaction().commit();

        } catch (Exception e) {
          TLogger.log("Exception caught during prePersistCascadeTest", e);
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
     * @testName: prePersistMultiCascadeTest
     * @assertion_ids:  PERSISTENCE:SPEC:694; PERSISTENCE:SPEC:697;
     *			PERSISTENCE:SPEC:708; PERSISTENCE:JAVADOC:34;
     *			PERSISTENCE:SPEC:723; PERSISTENCE:SPEC:724;
     * @test_Strategy:
     */
    public void prePersistMultiCascadeTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = prePersistMultiCascadeTest;
	try {
		getEntityTransaction().begin();
        	order = newOrder(id);
        	product = newProduct(id);
        	lineItem = newLineItem(id);
        	lineItem.setOrder(order);
        	lineItem.setProduct(product);
        	order.addLineItem(lineItem);
        	getEntityManager().persist(product);
        	getEntityManager().persist(order);
	
                List actual = order.getPrePersistCalls();
        	Helper.compareResultList(LISTENER_ABC, actual);
                
                actual = lineItem.getPrePersistCalls();
        	Helper.compareResultList(LISTENER_BC, actual);

	  	getEntityTransaction().commit();

	} catch (Exception e) {
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
     * @assertion_ids:  PERSISTENCE:SPEC:695; PERSISTENCE:SPEC:708
     * @test_Strategy:
     */
    public void preRemoveTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = preRemoveTest;
        try {
                getEntityTransaction().begin();
                product = newProduct(id);
                getEntityManager().persist(product);
                getEntityManager().remove(product);

                if(product.isPreRemoveCalled()) {
                        reason = "Product: preRemove was called.";
                        TLogger.log(reason);
                } else {
                        reason = "Product: preRemove was not called.";
                        throw new Fault(reason);
                }
                product = null;
                getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Exception caught during preRemoveTest", e);
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
     * @testName: preRemoveMultiTest
     * @assertion_ids:  PERSISTENCE:SPEC:694; PERSISTENCE:SPEC:709;
     *			PERSISTENCE:SPEC:722
     * @test_Strategy:
     */
    public void preRemoveMultiTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = preRemoveMultiTest;
	try {
		getEntityTransaction().begin();
        	product = newProduct(id);
        	getEntityManager().persist(product);
        	getEntityManager().remove(product);

                List actual = product.getPreRemoveCalls();
                Helper.compareResultList(LISTENER_ABC, actual);
                
        	product = null;
		getEntityTransaction().commit();
	} catch (Exception e) {
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
     * @testName: preRemoveCascadeTest
     * @assertion_ids:  PERSISTENCE:SPEC:695; PERSISTENCE:SPEC:708
     * @test_Strategy:
     */
    public void preRemoveCascadeTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = preRemoveCascadeTest;
        try {
                getEntityTransaction().begin();
                order = newOrder(id);
                product = newProduct(id);
                lineItem = newLineItem(id);
                lineItem.setOrder(order);
                lineItem.setProduct(product);
                order.addLineItem(lineItem);
                getEntityManager().persist(product);
                getEntityManager().persist(order);
                getEntityManager().remove(order);
                boolean b = order.isPreRemoveCalled();
                order = null;

                if(b) {
                        reason = "Order: preRemove was called.";
                        TLogger.log(reason);
                } else {
                        reason = "Order: preRemove was not called.";
                        throw new Fault(reason);
                }

                if(lineItem.isPrePersistCalled()) {
                        reason = "LineItem: preRemove was called.";
                        TLogger.log(reason);
                } else {
                        reason = "LineItem: preRemove was not called.";
                        throw new Fault(reason);
                }

                getEntityTransaction().commit();
        } catch (Exception e) {
          TLogger.log("Exception caught during preRemoveCascadeTest", e);
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
     * @testName: preRemoveMultiCascadeTest
     * @assertion_ids:  PERSISTENCE:SPEC:694; PERSISTENCE:SPEC:708;
     *			PERSISTENCE:SPEC:722; PERSISTENCE:SPEC:726;
     *			PERSISTENCE:SPEC:727
     * @test_Strategy:
     */
    public void preRemoveMultiCascadeTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = preRemoveMultiCascadeTest;
	try {
		getEntityTransaction().begin();
        	order = newOrder(id);
       		product = newProduct(id);
        	lineItem = newLineItem(id);
        	lineItem.setOrder(order);
        	lineItem.setProduct(product);
        	order.addLineItem(lineItem);
        	getEntityManager().persist(product);
        	getEntityManager().persist(order);
        	getEntityManager().remove(order);
        	boolean b = order.isPreRemoveCalled();

                List actual = order.getPreRemoveCalls();
                Helper.compareResultList(LISTENER_ABC, actual);
                
                actual = lineItem.getPreRemoveCalls();
                Helper.compareResultList(LISTENER_BC, actual);
                
        	order = null;
		getEntityTransaction().commit();
	} catch (Exception e) {
	  TLogger.log("Exception caught during preRemoveMultiCascadeTest", e);
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
     * @assertion_ids:  PERSISTENCE:SPEC:695; PERSISTENCE:SPEC:716
     * @test_Strategy:
     */
    public void preUpdateTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = preUpdateTest;
        try {
                getEntityTransaction().begin();
                product = newProduct(id);
                getEntityManager().persist(product);
                product.setPrice((double) 2);
                getEntityManager().persist(product);
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
     * @testName: postLoadTest
     * @assertion_ids:  PERSISTENCE:SPEC:695; PERSISTENCE:SPEC:719;
     *			PERSISTENCE:SPEC:720
     * @test_Strategy:
     */
    public void postLoadTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = postLoadTest;
        try {
                getEntityTransaction().begin();
                product = newProduct(id);
                getEntityManager().persist(product);
		getEntityManager().flush();
		getEntityManager().refresh(product);
                Query q = getEntityManager().createQuery("select distinct p from Product p");
                java.util.List results = q.getResultList();
//              for(int i = 0, n = results.size(); i < n; i++) {
//
//              }
                TLogger.log(results.toString());

                if(product.isPostLoadCalled()) {
                        reason = "Product: postLoad was called after the query result was returned.";
                        TLogger.log(reason);
                } else {
                  reason = "Product: postLoad was not called even after the query result was returned.";
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
     * @testName: postLoadMultiTest
     * @assertion_ids:  PERSISTENCE:SPEC:694; PERSISTENCE:SPEC:719; PERSISTENCE:SPEC:722
     * @test_Strategy:
     */
    public void postLoadMultiTest() throws Fault {
        boolean status = false;
        String reason = null;
        String id = postLoadMultiTest;
	try {
		getEntityTransaction().begin();
        	product = newProduct(id);
        	getEntityManager().persist(product);
        	getEntityManager().flush();
		getEntityManager().refresh(product);
        	Query q = getEntityManager().createQuery("select distinct p from Product p");
        	java.util.List results = q.getResultList();
        	TLogger.log(results.toString());

                List actual = product.getPostLoadCalls();
                Helper.compareResultList(LISTENER_ABC, actual);
                
		getEntityTransaction().commit();
	} catch (Exception e) {
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
     * @assertion_ids:  PERSISTENCE:SPEC:695; PERSISTENCE:SPEC:704
     * @test_Strategy:
     */
    public void prePersistRuntimeExceptionTest() throws Fault {
        String id = prePersistRuntimeExceptionTest;
        try {
                getEntityTransaction().begin();
                product = newProduct(id);
                txShouldRollback(product, id);
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
    
    private Product newProduct(String id) {
        Product product = new Product();
        product.setTestName(id);
        product.setId(id);
        product.setName(id);
        product.setPartNumber((long) 1);
        product.setPrice((double) 1);
        product.setQuantity(1);
        return product;
    }
    
    private Order newOrder(String id) {
        Order order = new Order(id, (double) 1);
        order.setTestName(id);
        return order;
    }

    private LineItem newLineItem(String id) {
        LineItem lineItem = new LineItem();
        lineItem.setTestName(id);
        lineItem.setId(id);
        lineItem.setQuantity(1);
        return lineItem;
    }
    
}

