package org.datanucleus.tests.directory.hierarchical_at_child_bidir;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

public class TestHelper
{

    List<Object> ids = new ArrayList<Object>();

    public void setUp(PersistenceManagerFactory pmf) throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Company jdo = new Company("JDO Inc.");
            pm.makePersistent(jdo);

            Department engineering = new Department("Engineering", jdo);
            Department sales = new Department("Sales", jdo);
            pm.makePersistent(engineering);
            pm.makePersistent(sales);

            Account bbunny = new AccountWithPassword("bbunny", "secret", null);
            Account ahicks = new Account("ahicks", null);
            Account lpuxa = new AccountWithPassword("lpuxa", "secret", null);

            Address bbAddress = new Address("B-City", "B-Street", null);
            Address ahAddress = new Address("A-City", "A-Street", null);
            Address lpAddress = new Address("L-City", "L-Street", null);

            Person bugsBunny = new Person("Bugs", "Bunny", "Bugs Bunny", bbunny, bbAddress, engineering);
            bbunny.setPerson(bugsBunny);
            bbAddress.setPerson(bugsBunny);
            engineering.getPersons().add(bugsBunny);
            Person anaHicks = new Person("Ana", "Hicks", "Ana Hicks", ahicks, ahAddress, engineering);
            ahicks.setPerson(anaHicks);
            ahAddress.setPerson(anaHicks);
            engineering.getPersons().add(anaHicks);
            Person lamiPuxa = new Person("Lami", "Puxa", "Lami Puxa", lpuxa, lpAddress, sales);
            lpuxa.setPerson(lamiPuxa);
            lpAddress.setPerson(lamiPuxa);
            sales.getPersons().add(lamiPuxa);

            pm.makePersistent(bugsBunny);
            pm.makePersistent(anaHicks);
            pm.makePersistent(lamiPuxa);

            ids.add(pm.getObjectId(bugsBunny));
            ids.add(pm.getObjectId(anaHicks));
            ids.add(pm.getObjectId(lamiPuxa));
            ids.add(pm.getObjectId(engineering));
            ids.add(pm.getObjectId(sales));
            ids.add(pm.getObjectId(jdo));

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void tearDown(PersistenceManagerFactory pmf) throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            for (Object id : ids)
            {
                try
                {
                    pm.deletePersistent(pm.getObjectById(id));
                }
                catch (Exception e)
                {
                }
            }
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }
}
