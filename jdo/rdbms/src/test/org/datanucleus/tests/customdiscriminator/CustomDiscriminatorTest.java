package org.datanucleus.tests.customdiscriminator;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.samples.models.transportation.Driver;
import org.datanucleus.samples.models.transportation.FemaleDriver;
import org.datanucleus.samples.models.transportation.HumanDriver;
import org.datanucleus.samples.models.transportation.MaleDriver;
import org.datanucleus.samples.models.transportation.RobotDriver;
import org.datanucleus.samples.models.transportation.Transportation;
import org.datanucleus.samples.models.transportation.Vehicle;
import org.datanucleus.store.rdbms.RDBMSPropertyNames;
import org.datanucleus.tests.JDOPersistenceTestCase;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomDiscriminatorTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public CustomDiscriminatorTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                    {
//                            TransportationTest.class,
//                            OwnerPhone.class,
//                            Vehicle.class,
//                            VehicleOwner.class,
//                            PersonVehicleOwner.class,
//                            CompanyVehicleOwner.class,
                    });
            initialised = true;
        }
    }

    /**
     * Test custom discriminator in RDBMSStoreManager
     */
    public void testCustomDiscriminatorInRDBMSStoreManager()
    {
        Properties userProps = new Properties();
        userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_ALLOW_COLUMN_REUSE, "true");
        PersistenceManagerFactory pmfWithColumnReuse = getPMF(1, userProps);

        PersistenceManager pm = pmfWithColumnReuse.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some basic data to query
            tx.begin();
            long idseq = 0;

            // create drivers
            Driver robot1 = new RobotDriver(++idseq);
            robot1.setName("R2D2");

            Driver robot2 = new RobotDriver(++idseq);
            robot2.setName("C3PO");

            Driver female1 = new FemaleDriver(++idseq);
            female1.setName("Eva");

            Driver female2 = new FemaleDriver(++idseq);
            female2.setName("Margrethe");

            Driver male1 = new MaleDriver(++idseq);
            male1.setName("Adam");

            Driver male2 = new MaleDriver(++idseq);
            male2.setName("Pingo");

            pm.makePersistentAll(robot1, robot2, female1, female2, male1, male2);

            tx.commit();

            String robot1String = getDriverString(robot1);
            String robot2String = getDriverString(robot2);
            String female1String = getDriverString(female1);
            String female2String = getDriverString(female2);
            String male1String = getDriverString(male1);
            String male2String = getDriverString(male2);

            final Object male1Oid = JDOHelper.getObjectId(male1);
            final Object female1Oid = JDOHelper.getObjectId(female1);

            // Query for a basic object, including the PK field(s) and a comment
            tx = pm.currentTransaction();
            tx.begin();

            { // test SQL query find one
                clearCaches(pm);
                String queryStr = "SELECT NAME,OBJECTTYPE,SUBTYPE,ID FROM DRIVER WHERE ID=?";

                Query query = pm.newQuery("javax.jdo.query.SQL", queryStr);

                query.setClass(Driver.class);
                List<Driver> results = (List<Driver>) query.executeWithArray(robot2.getId());
                assertNotNull(results);
                Set<String> foundDrivers = results.stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found drivers in SQL incorrect:",
                        Set.of(robot2String),
                        foundDrivers);
            }

            { // test SQL query find some
                clearCaches(pm);
                String queryStr = "SELECT NAME,OBJECTTYPE,SUBTYPE,ID FROM DRIVER WHERE ID in (?,?,?)";

                Query query = pm.newQuery("javax.jdo.query.SQL", queryStr);

                query.setClass(Driver.class);
                List<Driver> results = (List<Driver>) query.executeWithArray(robot1.getId(), female2.getId(), male1.getId());
                assertNotNull(results);
                Set<String> foundDrivers = results.stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found drivers in SQL incorrect:",
                        Set.of(robot1String, female2String, male1String),
                        foundDrivers);
            }

            { // test JDO query robot drivers
                clearCaches(pm);
                Set<String> foundDrivers = pm.newQuery(RobotDriver.class)
                        .executeList()
                        .stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found robot drivers incorrect:",
                        Set.of(robot1String, robot2String),
                        foundDrivers);
            }

            { // test JDO query human drivers
                clearCaches(pm);
                Set<String> foundDrivers = pm.newQuery(HumanDriver.class)
                        .executeList()
                        .stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found female drivers incorrect:",
                        Set.of(female1String, female2String, male1String, male2String),
                        foundDrivers);
            }

            { // test JDO query female drivers
                clearCaches(pm);
                Set<String> foundDrivers = pm.newQuery(FemaleDriver.class)
                        .executeList()
                        .stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found female drivers incorrect:",
                        Set.of(female1String, female2String),
                        foundDrivers);
            }

            { // test JDO query female drivers
                clearCaches(pm);
                Set<String> foundDrivers = pm.newQuery(MaleDriver.class)
                        .executeList()
                        .stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found male drivers incorrect:",
                        Set.of(male1String, male2String),
                        foundDrivers);
            }

            { // test JDO query all drivers
                clearCaches(pm);
                Set<String> foundDrivers = pm.newQuery(Driver.class)
                        .executeList()
                        .stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found drivers incorrect:",
                        Set.of(robot1String, robot2String, female1String, female2String, male1String, male2String),
                        foundDrivers);
            }

            { // test ID lookup
                clearCaches(pm);
                final Object objectById = pm.getObjectById(male1Oid);
                assertEquals("Looked up wrong male driver", male1String, getDriverString((Driver) objectById));
            }

            { // test ID lookup
                clearCaches(pm);
                final Object objectById = pm.getObjectById(female1Oid);
                assertEquals("Looked up wrong female driver", female1String, getDriverString((Driver) objectById));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing SQL query using candidate class : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(pmfWithColumnReuse, Driver.class);
        }
    }

    private void clearCaches(PersistenceManager pm)
    {
        pm.evictAll();
        pmf.getDataStoreCache().evictAll();
    }

    private String getDriverString(Driver driver)
    {
        return driver.getClass().getSimpleName() + "{" +
                "; name=" + driver.getName() +
                "}";
    }

    private String getTransportationString(Transportation transportation)
    {
        return transportation.getClass().getSimpleName() + "{" +
                "; name=" + transportation.getName() +
                (transportation instanceof Vehicle ?
                        "; owner=" + (getTransportationName(((Vehicle) transportation).getOwner())) +
                                "; prevOwner=" + (getTransportationName(((Vehicle) transportation).getPreviousOwner()))
                        :
                        ""
                ) +
                "}";
    }

    private String getTransportationName(Transportation transportation)
    {
        if (transportation != null)
        {
            return transportation.getName();
        }
        return "<null>";
    }
}
