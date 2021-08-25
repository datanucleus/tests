package org.datanucleus.tests.multitenancy;

import org.datanucleus.PropertyNames;
import org.datanucleus.samples.multitenancy.TenantedIndexObject;
import org.datanucleus.tests.JPAPersistenceTestCase;

import javax.persistence.*;
import java.util.Properties;

/**
 * Tests for Schema creation with MultiTenancy.
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