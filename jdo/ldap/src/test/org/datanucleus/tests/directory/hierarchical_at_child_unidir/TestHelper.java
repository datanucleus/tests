package org.datanucleus.tests.directory.hierarchical_at_child_unidir;

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

            OrgUnit ar = new OrgUnit("AR");
            OrgUnit br = new OrgUnit("BR");

            Person bugs = new Person("Bugs", "Bunny", "Bugs Bunny", ar);
            Person ana = new Person("Ana", "Hicks", "Ana Hicks", ar);
            Person lami = new Person("Lami", "Puxa", "Lami Puxa", br);

            Account bbunny = new Account("bbunny", "secret1", bugs);
            Account ahicks = new Account("ahicks", "secret2", ana);
            Account lpuxa = new Account("lpuxa", "secret3", lami);

            pm.makePersistent(bbunny);
            pm.makePersistent(ahicks);
            pm.makePersistent(lpuxa);

            ids.add(pm.getObjectId(pm.getObjectById(Account.class, "bbunny")));
            ids.add(pm.getObjectId(pm.getObjectById(Account.class, "ahicks")));
            ids.add(pm.getObjectId(pm.getObjectById(Account.class, "lpuxa")));
            ids.add(pm.getObjectId(pm.getObjectById(Person.class, "Bugs Bunny")));
            ids.add(pm.getObjectId(pm.getObjectById(Person.class, "Ana Hicks")));
            ids.add(pm.getObjectId(pm.getObjectById(Person.class, "Lami Puxa")));
            ids.add(pm.getObjectId(pm.getObjectById(OrgUnit.class, "AR")));
            ids.add(pm.getObjectId(pm.getObjectById(OrgUnit.class, "BR")));

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
