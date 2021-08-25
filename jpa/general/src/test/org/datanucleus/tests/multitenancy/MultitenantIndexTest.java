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
import org.datanucleus.samples.multitenancy.TenantedIndexObject;
import org.datanucleus.tests.JPAPersistenceTestCase;

import javax.persistence.*;
import java.util.Properties;

/**
 * Tests for JPQL "SELECT" queries.
 */
public class MultitenantIndexTest extends JPAPersistenceTestCase {
	private static boolean initialised = false;

	public MultitenantIndexTest(String name) {
		super(name);
		if (!initialised) {
			addClassesToSchema(new Class[]{TenantedIndexObject.class});
		}
	}

	public void testCreate() {
		long id = System.currentTimeMillis();
		Properties ps1 = new Properties();
		ps1.put(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "T1");
		EntityManagerFactory emf1 = getEMF("JPATest", ps1);
		EntityManager em1 = emf1.createEntityManager();
		EntityTransaction tx1 = em1.getTransaction();
		try {
			tx1.begin();
			TenantedIndexObject p = new TenantedIndexObject();
			p.setId(id);
			em1.persist(p);
			tx1.commit();
		} catch (PersistenceException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			if (tx1.isActive()) {
				tx1.rollback();
			}
			em1.close();
		}
	}
}