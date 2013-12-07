/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.models.fitness;

import java.util.Iterator;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.datanucleus.tests.TestHelper;

/**
 * Convenience methods for the "fitness" sample.
 */
public class FitnessHelper
{
    /**
     * Convenience method to disconnect relations between Gym sample objects.
     * Doesn't do all possible relations but covers main ones.
     */
    public static void cleanFitnessData(PersistenceManagerFactory pmf)
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.setOptimistic(false);
        try
        {
            tx.begin();
            javax.jdo.Extent ex1 = pm.getExtent(Gym.class);
            Iterator iter1 = ex1.iterator();
            while (iter1.hasNext())
            {
                Gym gym = (Gym)iter1.next();
                gym.getWardrobes().clear();
                pm.flush();
                gym.getWardrobes2().clear();
                pm.flush();
                gym.getEquipments().clear();
                pm.flush();
                gym.getEquipments2().clear();
                pm.flush();
                gym.getEquipmentsInverse().clear();
                pm.flush();
                gym.getEquipmentsInverse2().clear();
                pm.flush();
                gym.getPartners().clear();
                pm.flush();
                gym.getPartners2().clear();
                pm.flush();
                gym.getPartnersInverse().clear();
                gym.setGym(null);
                pm.flush();
                gym.getPartnersInverse2().clear();
                gym.setGym2(null);
                pm.flush();
            }

            javax.jdo.Extent ex2 = pm.getExtent(Wardrobe.class);
            Iterator iter2 = ex2.iterator();
            while (iter2.hasNext())
            {
                Wardrobe w = (Wardrobe)iter2.next();
                w.setClothes(null);
            }
            tx.commit();

            tx.begin();
            TestHelper.clean(pmf, Gym.class);
            TestHelper.clean(pmf, GymEquipment.class);
            TestHelper.clean(pmf, Wardrobe.class);
            TestHelper.clean(pmf, Cloth.class);
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