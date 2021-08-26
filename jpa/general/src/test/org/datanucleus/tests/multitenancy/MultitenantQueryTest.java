/**********************************************************************
 Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Contributors:
 2007 Andy Jefferson - rewritten to new test.framework/samples
 ...
 **********************************************************************/
package org.datanucleus.tests.multitenancy;

import org.datanucleus.PropertyNames;
import org.datanucleus.samples.multitenancy.TenantedObject;
import org.datanucleus.tests.JPAPersistenceTestCase;

import javax.persistence.*;
import java.util.Properties;

import static org.datanucleus.util.NucleusLogger.DATASTORE_NATIVE;

/**
 * Tests for JPQL "SELECT" queries.
 */
public class MultitenantQueryTest extends JPAPersistenceTestCase {
	private static boolean initialised = false;

	public MultitenantQueryTest(String name) {
		super(name);
		if (!initialised) {
			addClassesToSchema(new Class[]{TenantedObject.class});
		}
	}

	public void testCreateQuery() {
		try {
			long id = System.currentTimeMillis();
			Properties ps1 = new Properties();
			ps1.put(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "T1");

			EntityManagerFactory emf1 = getEMF("JPATest", ps1);
			EntityManager em1 = emf1.createEntityManager();
			EntityTransaction tx1 = em1.getTransaction();
			try {
				tx1.begin();
				TenantedObject p = new TenantedObject();
				p.setId(id);
				em1.persist(p);

			} catch (PersistenceException e) {
				e.printStackTrace();
				fail(e.getMessage());
			} finally {
				if (tx1.isActive()) {
					tx1.rollback();
				}
				em1.close();
			}
			em1 = emf1.createEntityManager();
			 tx1 = em1.getTransaction();
			try {
				tx1.begin();

				TypedQuery<TenantedObject> query = em1.createQuery("SELECT t FROM TenantedObject t where t.id=:i", TenantedObject.class);
				query.setParameter("i", id);
				TenantedObject singleResult = query.getSingleResult();
				assertNotNull(singleResult);
			} catch (PersistenceException e) {
				e.printStackTrace();
				fail(e.getMessage());
			} finally {
				if (tx1.isActive()) {
					tx1.rollback();
				}
				em1.close();
			}
			Properties ps2 = new Properties();
			ps2.put(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "T2");
			EntityManagerFactory emf2 = getEMF("JPATest", ps2);
			EntityManager em2 = emf2.createEntityManager();
			EntityTransaction tx2 = em2.getTransaction();
			try {
				tx2.begin();
				TypedQuery<TenantedObject> query = em2.createQuery("SELECT t FROM TenantedObject t where t.id=:i", TenantedObject.class);
				query.setParameter("i", id);
				TenantedObject singleResult = query.getSingleResult();
				assertNull(singleResult);
			} catch (PersistenceException e) {

				// expected, but would like to eventually have IllegalArgumentException
			} finally {
				if (tx2.isActive()) {
					tx2.rollback();
				}
				em2.close();
			}
		} finally {
		}
	}

	public void testCreateFind() {
		try {
			long id = System.currentTimeMillis();
			Properties ps1 = new Properties();
			ps1.put(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "T1");
			Properties ps2 = new Properties();
			ps2.put(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "T2");
			EntityManagerFactory emf1 = getEMF("JPATest", ps1);
			EntityManager em1 = emf1.createEntityManager();
			EntityTransaction tx1 = em1.getTransaction();
			try {
				tx1.begin();
				TenantedObject p = new TenantedObject();
				p.setName("TOTO "+id);
				p.setId(id);
				em1.persist(p);
				tx1.commit();
			} catch (Exception e) {e.printStackTrace();
				fail(e.getMessage());
				// expected, but would like to eventually have IllegalArgumentException
			} finally {
				if (tx1.isActive()) {
					tx1.rollback();
				}
				em1.close();
			}
			em1 = emf1.createEntityManager();
			tx1 = em1.getTransaction();
			try {
				tx1.begin();
				TenantedObject tenantedObject = em1.find(TenantedObject.class, id);
				assertNotNull(tenantedObject);
			} catch (PersistenceException e) {e.printStackTrace();
				fail(e.getMessage());
			} finally {
				if (tx1.isActive()) {
					tx1.rollback();
				}
				em1.close();
			}
			EntityManagerFactory emf2 = getEMF("JPATest", ps2);
			EntityManager em2 = emf2.createEntityManager();
			EntityTransaction tx2 = em2.getTransaction();
			try {
				tx2.begin();
				TenantedObject tenantedObject = em2.find(TenantedObject.class, id);
				assertNull(tenantedObject);
			} catch (PersistenceException e) {
				// expected, but would like to eventually have IllegalArgumentException
			} finally {
				if (tx2.isActive()) {
					tx2.rollback();
				}
				em2.close();
			}
		} finally {
		}
	}
}