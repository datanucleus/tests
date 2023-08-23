package org.datanucleus.tests.transportation;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.cache.Level2Cache;
import org.datanucleus.samples.models.transportation.Driver;
import org.datanucleus.samples.models.transportation.FemaleDriver;
import org.datanucleus.samples.models.transportation.HumanDriver;
import org.datanucleus.samples.models.transportation.MaleDriver;
import org.datanucleus.samples.models.transportation.OwnerPhone;
import org.datanucleus.samples.models.transportation.PersonVehicleOwner;
import org.datanucleus.samples.models.transportation.RobotDriver;
import org.datanucleus.samples.models.transportation.Transportation;
import org.datanucleus.samples.models.transportation.Vehicle;
import org.datanucleus.samples.models.transportation.VehicleOwner;
import org.datanucleus.store.rdbms.RDBMSPropertyNames;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.TestHelper;
import org.junit.Assume;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class TransportationTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public TransportationTest(String name)
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
     * Test SQL query (and JDO query) works when having double mapped columns in model.
     * To allow double mapped columns we set datanucleus.rdbms.allowColumnReuse=true.
     */
    public void testDoubleMappedColumns()
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

            // create vehicles
            Vehicle car1 = new Vehicle(++idseq);
            car1.setName("Land Rover Defender");

            Vehicle car2 = new Vehicle(++idseq);
            car2.setName("Toyota Land Cruiser");

            Vehicle car3 = new Vehicle(++idseq);
            car3.setName("Jeep Wrangler");

            Vehicle rocket = new Vehicle(++idseq);
            rocket.setName("Space Shuttle");
            pm.makePersistentAll(car1, car2, car3, rocket);

            // create owners
            VehicleOwner owner1 = new PersonVehicleOwner(++idseq);
            owner1.setName("Jens Ole");

            VehicleOwner owner2 = new PersonVehicleOwner(++idseq);
            owner2.setName("Harry Potter");
            car2.setOwner(owner1);
            car3.setOwner(owner2);
            car3.setPreviousOwner(owner1);
            pm.makePersistentAll(owner1, owner2);

            // create phones
            final OwnerPhone phone1 = new OwnerPhone(owner1, "123456");
            final OwnerPhone phone2 = new OwnerPhone(owner1, "987654");
            final OwnerPhone phone3 = new OwnerPhone(owner2, "+12345");
            pm.makePersistentAll(phone1, phone2, phone3);

            tx.commit();

            String owner1String = getTransportationString(owner1);
            String owner2String = getTransportationString(owner2);

            String car3String = getTransportationString(car3);

            // Query for a basic object, including the PK field(s) and a comment
            tx = pm.currentTransaction();
            tx.begin();

            { // test SQL query find all
                String queryStr = "SELECT OWNERTYPE,OWNERID,PHONENUMBER FROM OWNERPHONE WHERE OWNERID=? AND OWNERTYPE=?";

                Query query = pm.newQuery("javax.jdo.query.SQL", queryStr);

                query.setClass(OwnerPhone.class);
                List<OwnerPhone> results = (List<OwnerPhone>) query.executeWithArray(owner2.getId(), owner2.getObjectType());
                assertNotNull(results);
                Set<String> foundPhoneNumbers = results.stream()
                        .map(OwnerPhone::getPhoneNumber)
                        .collect(Collectors.toSet());
                assertEquals("Found phone numbers in SQL incorrect:",
                        Set.of("+12345"),
                        foundPhoneNumbers);
            }

            { // test SQL query find some
                String queryStr = "SELECT OWNERTYPE,OWNERID,PREVIOUSOWNERID,THENAME,OBJECTTYPE,ID FROM TRANSPORTATION where ID=?";

                Query query = pm.newQuery("javax.jdo.query.SQL", queryStr);

                query.setClass(Vehicle.class);
                List<Vehicle> results = (List<Vehicle>) query.executeWithArray(car3.getId());
                assertNotNull(results);
                Set<String> foundVehicles = results.stream()
                        .map(this::getTransportationString)
                        .collect(Collectors.toSet());
                assertEquals("Found vehicles in SQL incorrect:",
                        Set.of(car3String),
                        foundVehicles);
            }

            { // test JDO query owners
                Set<String> foundCars = pm.newQuery(VehicleOwner.class)
                        .executeList()
                        .stream()
                        .map(this::getTransportationString)
                        .collect(Collectors.toSet());
                assertEquals("Found cars incorrect:",
                        Set.of(owner1String, owner2String),
                        foundCars);
            }

            // test update
            car2.setOwner(owner2);
            car2.setPreviousOwner(null);
            car3.setOwner(owner2);
            car3.setPreviousOwner(null);
            rocket.setOwner(owner1);
            rocket.setPreviousOwner(owner2);
            tx.commit();
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

            clean(pmfWithColumnReuse, OwnerPhone.class);
            clean(pmfWithColumnReuse, Vehicle.class);
            clean(pmfWithColumnReuse, PersonVehicleOwner.class);
        }
    }

    /**
     * Test SQL query (and JDO query) works when having double mapped columns in model.
     * To allow double mapped columns we set datanucleus.rdbms.allowColumnReuse=true.
     */
    public void testCustomDiscriminatorInRDBMSStoreManager()
    {
        Properties userProps = new Properties();
        final int testNumber = 1;
        final Properties factoryProperties = TestHelper.getFactoryProperties(testNumber, userProps);
        String dnConnectionurl = CustomDiscriminatorRDBMSStoreManager.DN_CONNECTIONURL;
        Object url = factoryProperties.get(dnConnectionurl);
        if (url == null)
        {
            dnConnectionurl = dnConnectionurl.toLowerCase();
            factoryProperties.get(dnConnectionurl);
        }
        Assume.assumeTrue(url instanceof String && ((String)url).startsWith("jdbc"));
        userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_ALLOW_COLUMN_REUSE, "true");
        userProps.setProperty(dnConnectionurl,
                CustomDiscriminatorRDBMSStoreManager.SUB_STOREMANAGER_PREFIX+url);
        PersistenceManagerFactory pmfWithCustomRDBMSStoreManager = getPMF(testNumber, userProps);

        PersistenceManager pm = pmfWithCustomRDBMSStoreManager.getPersistenceManager();
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

            clean(pmfWithCustomRDBMSStoreManager, Driver.class);
        }
    }

    private void clearCaches(PersistenceManager pm)
    {
        pm.evictAll();
        final Level2Cache level2Cache = ((JDOPersistenceManager) pm).getExecutionContext().getNucleusContext().getLevel2Cache();
        if (level2Cache!=null) {
            level2Cache.evictAll();
        }
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
