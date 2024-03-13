package org.datanucleus.tests.nullablepk;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.enhancement.Persistable;
import org.datanucleus.samples.models.nullablepk.NullableObjectPK;
import org.datanucleus.samples.models.nullablepk.NullablePK;
import org.datanucleus.samples.models.nullablepk.NullablePrimitivePK;
import org.datanucleus.state.DNStateManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.tests.JDOPersistenceTestCase;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Simple test for the case of application identity field(s) being nullable. 
 * This is to simulate an existing schema table without a PK, and one of the fields that is effectively part of the "PK" is nullable.
 */
public class NullablePKTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public NullablePKTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                    {
//                            NullablePrimitivePK.class,
                    });
            initialised = true;
        }
    }


    /**
     * Test having nulls in primary key field using Java non-primitive field
     */
    public void testNullableObjectPK()
    {
        checkNullablePK(false);
    }

    /**
     * Test having nulls in primary key field using Java primitive field with column mapping to/from null values in DB.
     */
    public void testNullablePrimitivePK()
    {
        checkNullablePK(true);
    }

    private void checkNullablePK(boolean usePrimitives)
    {
        if (!(storeMgr instanceof RDBMSStoreManager))
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        ((JDOPersistenceManager)pm).getExecutionContext().getNucleusContext().getTypeManager()
                .registerConverter(NullablePrimitiveTypeConverter.class.getName(), new NullablePrimitiveTypeConverter(), Long.class, Long.class, false, null);
        Transaction tx = pm.currentTransaction();
        try
        {
            // With normal auto-schema creation it is not possible to end up with nullable PK columns (since not in the SQL standard).
            // However, it can easily be the case when mapping to old legacy DB schema and schema-creation has been disabled.
            // In these cases the DB schema typically have a unique index including the nullable fields that uniquely identifies an object (instead of the DB PK directly).
            // In this test case we do not bother to create the unique-index as we are only testing this for a very small number of rows.
            // BUT, we do need to drop schema created from auto-schema-creation in DN and re-create DB table without PK.

            runStmt("DROP TABLE NULLABLEPK", false);
            String createStmt = "CREATE TABLE NULLABLEPK\n(\n"+
                    "DUMMY CHARACTER VARYING(255) NULL,\n"+
                    "MYPK1DB NUMERIC NULL,\n" +
                    "MYPK2DB NUMERIC NULL,\n" +
                    "MYPKTYPEDB NUMERIC NOT NULL,\n" +
                    "UNIT NUMERIC NULL,\n" +
                    "VAL CHARACTER VARYING(255) NULL\n"+
                    ")";

            runStmt(createStmt);

            // Create some basic data to query
            tx.begin();
            long idseq = 0;

            // Create POs (test InsertRequest)
            final NullablePK my0;
            final NullablePK my1;
            if (usePrimitives)
            {
                my0 = new NullablePrimitivePK(0, ++idseq);
                my1 = new NullablePrimitivePK(1, ++idseq);
                ((NullablePrimitivePK) my1).setUnit(7);
            }
            else
            {
                my0 = new NullableObjectPK(0, ++idseq);
                my1 = new NullableObjectPK(1, ++idseq);
                ((NullableObjectPK) my1).setUnit(7L);
            }
            my0.setValue("my0value");
            my1.setValue("my1value");

            pm.makePersistentAll(my0, my1);

            tx.commit();

            tx = pm.currentTransaction();
            tx.begin();

            // Do SQL query to verify objects are inserted
            {
                clearCaches(pm);
                String queryStr = "SELECT MYPKTYPEDB,MYPK1DB,MYPK2DB,VAL,UNIT FROM NULLABLEPK";

                Query<Map> query = pm.newQuery("javax.jdo.query.SQL", queryStr);

                Collection<Object[]> results = (Collection<Object[]>) query.execute();
                assertNotNull(results);
                Set<String> foundRows = new HashSet<>();
                results.stream().forEach(m->foundRows.add(getRow(m[0], m[1], m[2], m[3], m[4])));
                assertEquals("Found rows in SQL incorrect:",
                        Set.of(getRow(0, 1, null, "my0value", null),
                                getRow(1, null, 2, "my1value", 7)),
                        foundRows);
            }

            // Make update (test UpdateRequest)
            my0.setValue("new0value");
            my1.setValue("new1value");
            if (usePrimitives) {
                ((NullablePrimitivePK) my0).setUnit(9);
                ((NullablePrimitivePK) my1).setUnit(NullablePrimitivePK.NULL_VALUE);
            } else {
                ((NullableObjectPK) my0).setUnit(9L);
                ((NullableObjectPK)my1).setUnit(null);
            }
            tx.commit();

            // Do SQL query to verify objects are updated
            {
                clearCaches(pm);
                String queryStr = "SELECT MYPKTYPEDB,MYPK1DB,MYPK2DB,VAL,UNIT FROM NULLABLEPK";

                Query<Map> query = pm.newQuery("javax.jdo.query.SQL", queryStr);

                Collection<Object[]> results = (Collection<Object[]>) query.execute();
                assertNotNull(results);
                Set<String> foundRows = new HashSet<>();
                results.stream().forEach(m->foundRows.add(getRow(m[0], m[1], m[2], m[3], m[4])));
                assertEquals("Found rows in SQL incorrect:",
                        Set.of(getRow(0, 1, null, "new0value", 9),
                                getRow(1, null, 2, "new1value", null)),
                        foundRows);
            }

            // Test FetchRequest
            {
                clearCaches(pm);
                final String v0 = my0.getValue();
                assertEquals("Fetch obj 0", "new0value", v0);
                final String v1 = my1.getValue();
                assertEquals("Fetch obj 1", "new1value", v1);
            }

            // Test LocateRequest
            {
                ((DNStateManager)((Persistable)my0).dnGetStateManager()).locate();
                ((DNStateManager)((Persistable)my1).dnGetStateManager()).locate();
            }

            tx = pm.currentTransaction();
            tx.begin();

            // Test DeleteRequest
            {
                pm.deletePersistent(my0);
                pm.deletePersistent(my1);
                tx.commit();

                // check both rows are gone
                String queryStr = "SELECT MYPKTYPEDB,MYPK1DB,MYPK2DB,VAL,UNIT FROM NULLABLEPK";

                Query<Map> query = pm.newQuery("javax.jdo.query.SQL", queryStr);

                Collection<Object[]> results = (Collection<Object[]>) query.execute();
                assertNotNull(results);
                Set<String> foundRows = new HashSet<>();
                results.stream().forEach(m->foundRows.add(getRow(m[0], m[1], m[2], m[3], m[4])));
                assertEquals("Found rows in SQL incorrect:",
                        Set.of(),
                        foundRows);
            }

            tx = pm.currentTransaction();
            tx.begin();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error("Got exception", e);
            fail("Exception thrown while performing SQL query using candidate class : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            runStmt("DROP TABLE NULLABLEPK", false);
        }
    }

    private String getRow(Object pktypedb, Object pk1db, Object pk2db, Object val, Object unit)
    {
        return "NullablePK("+
                "pktypedb="+pktypedb+";"+
                "pk1db="+pk1db+";"+
                "pk2db="+pk2db+";"+
                "val="+val+";"+
                "unit="+unit+";"+
                ")";
    }

    private void runStmt(String stmt)
    {
        runStmt(stmt, true);
    }

    private void runStmt(String stmt, boolean failOnError)
    {
        Connection con = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            LOG.debug(stmt);
            pm.currentTransaction().begin();
            con = (Connection) storeMgr.getConnectionManager().getConnection(((JDOPersistenceManager)pm).getExecutionContext()).getConnection();
            con.prepareStatement(stmt).execute();
            pm.currentTransaction().commit();
        }
        catch (SQLException e)
        {
            LOG.error("Error running statement: "+stmt+"\n"+e);
            if (failOnError)
            {
                fail("Error running statement: "+stmt+"\n"+e);
            }
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    private void clearCaches(PersistenceManager pm)
    {
        pm.evictAll();
        pm.getPersistenceManagerFactory().getDataStoreCache().evictAll();
    }
}
