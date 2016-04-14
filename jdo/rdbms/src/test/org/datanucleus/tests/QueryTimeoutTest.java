package org.datanucleus.tests;

import org.datanucleus.PropertyNames;
import org.jpox.samples.models.company.Office;

import javax.jdo.*;
import java.util.List;

/**
 * Created by pica on 02/03/16.
 */
public class QueryTimeoutTest extends JDOPersistenceTestCase{

    // pseudo unique description for our data
    private static final String DESC_PFX = "QueryTimeout-";
    private static boolean initialised = false;

    public QueryTimeoutTest(String name) {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]{Office.class,});
            initialised = true;
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        LOG.info("starting QueryTimeoutTest setUp");
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
        final Transaction tx = pm.currentTransaction();
        try {

            tx.begin();
            Office r;
            r = new Office(0, "-x", DESC_PFX + "foo");
            pm.makePersistent(r);
            r = new Office(1, "-y", DESC_PFX + "bar");
            pm.makePersistent(r);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
        LOG.info("ended setUp");
    }

    public void testJdoQLTimeout() {

        Runnable sleeper = new SerializedSleepyLister(pmf, false, 2000);
        RunnerThrowable searcher = new ImpatientSearcher(pmf, false, 1000);

        Thread t1 = new Thread(sleeper);
        Thread t2 = new Thread(searcher);

        t1.start();
        LOG.info(String.format("t1 started at %d", System.nanoTime()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info(String.format("t2 started at %d", System.nanoTime()));
        t2.start();

        joinBoth(t1, t2);

        assertNotNull("ImpatientSearcher did not get a timeout", searcher.getThrowable());
        assertEquals(JDOException.class, searcher.getThrowable().getClass());
        // TODO
//        assertEquals(QueryTimeoutException.class, searcher.getThrowable().getCause().getClass());
    }

    public void testSQLTimeout() {

        Runnable sleeper = new SerializedSleepyLister(pmf, true, 2000);
        RunnerThrowable searcher = new ImpatientSearcher(pmf, true, 1000);

        Thread t1 = new Thread(sleeper);
        Thread t2 = new Thread(searcher);

        t1.start();
        LOG.info(String.format("t1 started at %d", System.nanoTime()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info(String.format("t2 started at %d", System.nanoTime()));
        t2.start();

        joinBoth(t1, t2);
        assertNotNull("ImpatientSearcher did not get a timeout", searcher.getThrowable());
        assertEquals(JDODataStoreException.class, searcher.getThrowable().getClass());
        // TODO
//        assertEquals(QueryTimeoutException.class, searcher.getThrowable().getCause().getClass());
    }

    private void joinBoth(Thread t1, Thread t2) {
        while (t1.isAlive() || t2.isAlive()) {
            if (t1.isAlive()) {
                try {
                    t1.join(100);
                } catch (InterruptedException e) {
                    LOG.info("Still waiting for sleepy");
                }
            }
            if (t2.isAlive()) {
                try {
                    t2.join(100);
                } catch (InterruptedException e) {
                    LOG.info("Still waiting for impatient");
                }
            }
        }
    }


    protected void tearDown() throws Exception {
        clean(Office.class);
        super.tearDown();
    }

    interface Thrower {
        Throwable getThrowable();
    }


    interface RunnerThrowable extends Runnable, Thrower {
    }

    /**
     * actor that expects an answer
     */
    static class ImpatientSearcher implements RunnerThrowable {
        final PersistenceManager pm;
        private final boolean sql;
        private final int wait;
        private Throwable throwable;

        ImpatientSearcher(PersistenceManagerFactory pmf, boolean sql, int wait) {
            this.pm = pmf.getPersistenceManager();
            this.pm.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
            this.sql = sql;
            this.wait = wait;
        }


        @Override
        public void run() {
            LOG.debug(String.format("Running %s at %d", ImpatientSearcher.class, System.nanoTime()));

            final Transaction tx = pm.currentTransaction();
            tx.begin();
            try {
                Query q;
                if (sql) {
                    q = pm.newQuery("javax.jdo.query.SQL", "SELECT * FROM office WHERE description = '" + DESC_PFX + "foo' FOR UPDATE ");
                } else {
                    q = pm.newQuery("SELECT FROM " + Office.class.getName() + " WHERE description == '" + DESC_PFX + "foo'");
                    q.setSerializeRead(true);
                }
                q.setDatastoreReadTimeoutMillis(wait);
                q.setUnique(true);
                final long t0 = System.nanoTime();
                final Office r = (Office) q.execute();
                LOG.info(String.format("got %s after %d", r, (System.nanoTime() - t0) / 1000000));

                tx.commit();
            } catch (Throwable t) {
                throwable = t;
                throw t;
            } finally {
                if (tx.isActive()) {
                    tx.rollback();
                }
                pm.close();
            }
        }

        @Override
        public Throwable getThrowable() {
            return throwable;
        }

    }

    /**
     * actor that locks rows and sleeps for a while
     */
    static class SerializedSleepyLister implements Runnable {

        final PersistenceManager pm;
        final long sleep;
        final boolean sql;

        SerializedSleepyLister(PersistenceManagerFactory pmf, boolean sql, long sleep) {
            this.pm = pmf.getPersistenceManager();
            this.pm.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
            this.sleep = sleep;
            this.sql = sql;
        }

        @Override
        public void run() {
            LOG.debug(String.format("Running %s at %d", SerializedSleepyLister.class, System.nanoTime()));
            final Transaction tx = pm.currentTransaction();
            tx.begin();
            try {
                Query q;
                if (sql) {
                    q = pm.newQuery("javax.jdo.query.SQL", "SELECT * FROM office FOR UPDATE");
                } else {
                    q = pm.newQuery("SELECT FROM " + Office.class.getName());
                    q.setSerializeRead(true);
                }
                final List<Office> rs = (List<Office>) q.execute();
                LOG.info(String.format("Got %d rows at %d", rs.size(), System.nanoTime()));
                final long t0 = System.currentTimeMillis();
                do {
                    final long t = System.currentTimeMillis();
                    try {
                        final long millis = sleep - (t - t0);
                        LOG.debug(String.format("About to sleep %s", millis));
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (System.currentTimeMillis() - t0 < sleep);
                LOG.debug(String.format("Done sleeping at %s", System.nanoTime()));
                tx.commit();
            } finally {
                if (tx.isActive()) {
                    tx.rollback();
                }
                pm.close();
            }
        }
    }

}
