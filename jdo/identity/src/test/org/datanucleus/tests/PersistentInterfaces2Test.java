/**********************************************************************
 Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
 Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.tests;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.persistentinterfaces.Country;
import org.jpox.samples.persistentinterfaces.ICity;
import org.jpox.samples.persistentinterfaces.ILocation;

/**
 * Test for persistent interfaces.
 * This typically throws some exception from org.datanucleus.store.rdbms.query.PersistentClassROF.getObject
 * about not having the PK field. It sometimes has the cmd for ICityImpl (where the "position" field is number 3)
 * yet has selected "position" as field 1 (in superclass ILocation metadata).
 * Also this causes conflict with PersistentInterfaces1Test if run in a different order to the file
 */
public class PersistentInterfaces2Test extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public PersistentInterfaces2Test(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]{});
            initialised = true;
        }
    }

    /**
     * test objects with references to persistent interfaces
     */
    public void testObjectWithReferenceToPersistentInterface() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Create object with reference to a persistent interface object
            Country france = new Country("France");
            ICity paris = (ICity) pm.newInstance(ICity.class);
            france.setCapital(paris);
            paris.setPosition(1001);
            paris.setPopulation(2345000);
            assertEquals(1001, paris.getPosition());

            Object id = null;
            try
            {
                tx.begin();
                pm.makePersistent(france);
                tx.commit();
                id = pm.getObjectId(france);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                france = (Country) pm.getObjectById(id);
                assertEquals(1001, france.getCapital().getPosition());
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
        finally
        {
            clean(Country.class);
            clean(ICity.class);
            clean(ILocation.class);
        }
    }
}