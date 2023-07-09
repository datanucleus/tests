package org.datanucleus.tests;

import org.datanucleus.samples.models.transportation.OwnerPhone;
import org.datanucleus.samples.models.transportation.Transportation;
import org.datanucleus.samples.models.transportation.Vehicle;
import org.datanucleus.samples.models.transportation.VehicleOwner;
import org.datanucleus.store.rdbms.RDBMSPropertyNames;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class ColumnReuseTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ColumnReuseTest(String name)
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
            VehicleOwner owner1 = new VehicleOwner(++idseq);
            owner1.setName("Jens Ole");

            VehicleOwner owner2 = new VehicleOwner(++idseq);
            owner2.setName("Harry Potter");
            car2.setOwner(owner1);
            car3.setOwner(owner2);
            car3.setPreviousOwner(owner1);
            pm.makePersistentAll(owner1, owner2);

            // create phomes
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
            clean(pmfWithColumnReuse, VehicleOwner.class);
        }
    }

    private String getTransportationString(Transportation transportation)
    {
        return transportation.getClass().getSimpleName() + "{" +
                "; name=" + transportation.getName() +
                (transportation instanceof Vehicle ?
                        "; owner=" + (getTransporationName(((Vehicle) transportation).getOwner())) +
                                "; prevOwner=" + (getTransporationName(((Vehicle) transportation).getPreviousOwner()))
                        :
                        ""
                ) +
                "}";
    }

    private String getTransporationName(Transportation transportation)
    {
        if (transportation != null)
        {
            return transportation.getName();
        }
        return "<null>";
    }
}
