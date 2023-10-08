package org.datanucleus.tests;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.cache.Level2Cache;
import org.datanucleus.samples.models.transportation.Address;
import org.datanucleus.samples.models.transportation.Driver;
import org.datanucleus.samples.models.transportation.RobotDriver;
import org.datanucleus.store.rdbms.RDBMSPropertyNames;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class ManyToOneUniWithJoinTableTest extends JDOPersistenceTestCase
{

    private static boolean initialised = false;

    public ManyToOneUniWithJoinTableTest(String name)
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
     * Test JDO query through a M-1 uni directional mapping using a join table
     */
    public void testManyToOneUniDirectionalWithJoinTable()
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

            pm.makePersistentAll(robot1, robot2);

            // create addresses
            Address address1 = new Address(++idseq);
            address1.setAddressLine("123 Robo Street");
            robot1.setHomeAddress(address1);

            Address address2 = new Address(++idseq);
            address2.setAddressLine("1313 Webfoot Walk");
            robot1.setAwayAddress(address2);

            pm.makePersistentAll(address1, address2);

            tx.commit();

            String robot1String = getDriverString(robot1);
            String robot2String = getDriverString(robot2);
            String address1String = getAddressString(address1);
            String address2String = getAddressString(address2);

            // Query for a basic object, including the PK field(s) and a comment
            tx = pm.currentTransaction();
            tx.begin();

            // test JDO query drivers - using implicit (DN generated) join columns
            // as specified on homeAddress
            {
                Object res = pm.newQuery(Driver.class)
                        .filter("homeAddress==:myaddress")
                        .executeWithArray(address1);
                Set<String> foundDrivers = ((List<Driver>)res)
                        .stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found drivers incorrect:",
                        Set.of(robot1String),
                        foundDrivers);
            }

            { // test JDO query find nothing
                Object res = pm.newQuery(Driver.class)
                        .filter("homeAddress==:myaddress")
                        .executeWithArray(address2);
                Set<String> foundDrivers = ((List<Driver>)res)
                        .stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found drivers incorrect:",
                        Set.of(),
                        foundDrivers);
            }

            // test JDO query drivers - explicit join columns specified
            // on awayAddress
            {
                Object res = pm.newQuery(Driver.class)
                        .filter("awayAddress==:myaddress")
                        .executeWithArray(address2);
                Set<String> foundDrivers = ((List<Driver>)res)
                        .stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found drivers incorrect:",
                        Set.of(robot1String),
                        foundDrivers);
            }


            { // test JDO query find nothing
                Object res = pm.newQuery(Driver.class)
                        .filter("awayAddress==:myaddress")
                        .executeWithArray(address1);
                Set<String> foundDrivers = ((List<Driver>)res)
                        .stream()
                        .map(this::getDriverString)
                        .collect(Collectors.toSet());
                assertEquals("Found drivers incorrect:",
                        Set.of(),
                        foundDrivers);
            }

            // test reading association
            clearCaches(pm);
            assertEquals("Home address not read correct", address1String, getAddressString(robot1.getHomeAddress()));
            assertEquals("Away address not read correct", address2String, getAddressString(robot1.getAwayAddress()));
            assertEquals("Empty home address not read correct", null, robot2.getHomeAddress());
            assertEquals("Empty away address not read correct", null, robot2.getAwayAddress());
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
            clean(pmfWithColumnReuse, Address.class);
        }
    }

    private void clearCaches(PersistenceManager pm) {
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
                "; homeAddress=" + getAddressString(driver.getHomeAddress()) +
                "; awayAddress=" + getAddressString(driver.getAwayAddress()) +
                "}";
    }

    private String getAddressString(Address address)
    {
        return address == null ? "<null>" :
                address.getClass().getSimpleName() + "{" +
                        "; addressLine=" + address.getAddressLine() +
                        "; type=" + address.getType() +
                        "}";
    }
}
