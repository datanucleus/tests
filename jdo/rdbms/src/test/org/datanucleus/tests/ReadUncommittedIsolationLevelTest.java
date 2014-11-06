package org.datanucleus.tests;

import javax.jdo.Constants;
import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.PropertyNames;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Office;

public class ReadUncommittedIsolationLevelTest extends JDOPersistenceTestCase {

	private static boolean initialised = false;
	private Object oid;
	private PersistenceManager pm1;
	private PersistenceManager pm2;
	private Transaction tx;
	private Transaction tx1;
	private Transaction tx2;
	private Office o1;
	private Office o2;
	private static final String ROOM = "room";

	public ReadUncommittedIsolationLevelTest(String name) {
		super(name);
		if (!initialised)
		{
			addClassesToSchema(new Class[]
					{
							Office.class,
					});
			initialised = true;
		}
	}


	public void testReadUncommited() {

		LOG.info("Start test " + Constants.TX_READ_UNCOMMITTED);
		System.out.println("Start test " + Constants.TX_READ_UNCOMMITTED);

		_init();

		String finalDescription = null;
		try {

			pm1 = pmf.getPersistenceManager();
			pm1.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
			tx1 = pm1.currentTransaction();
			tx1.setIsolationLevel(Constants.TX_READ_UNCOMMITTED);
			tx1.begin();

			pm2 = pmf.getPersistenceManager();
			pm2.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
			tx2 = pm2.currentTransaction();
			tx2.setIsolationLevel(Constants.TX_READ_UNCOMMITTED);
			tx2.begin();

			o1 = (Office) pm1.getObjectById(oid);
			System.out.println("within tx1 before modifying:" + o1.asString());
			LOG.info("within tx1 after modifying:" + o1.asString());
			finalDescription = o1.getDescription() + o1.getRoomName();
			o1.setDescription(finalDescription);

			// send UPDATE to database
			pm1.flush();
			System.out.println("within tx1 after modifying:" + o1.asString());
			LOG.info("within tx1 after modifying:" + o1.asString());

			o2 = (Office) pm2.getObjectById(oid);
			LOG.info("within tx2: " + o2.asString());
			System.out.println("within tx2: " + o2.asString());
			assertEquals("uncommited modification not seen", finalDescription,
					o2.getDescription());

		} catch (JDODataStoreException e) {
			assertFalse("Should be able to see description " + finalDescription
							+ " but " + e.getMessage(), true);
		} finally {
			tx2.commit();
			pm2.close();
			tx1.commit();
			pm1.close();
			clean(Office.class);
			clean(Department.class);
			closePMF();
		}

		LOG.info("End test " + Constants.TX_READ_UNCOMMITTED);
		System.out.println("End test " + Constants.TX_READ_UNCOMMITTED);
	}


	private void _init() {
		try {
			pm = pmf.getPersistenceManager();
			pm.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
			tx = pm.currentTransaction();
			tx.begin();

			// create sample data
			Office o = new Office(1L, ROOM, "desc");
			o = pm.makePersistent(o);
			oid = pm.getObjectId(o);

			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}

		o1 = null;
		o2 = null;
	}
}

