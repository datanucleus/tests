/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
     ...
***********************************************************************/
package org.datanucleus.test;

import org.junit.*;
import javax.jdo.*;
import java.util.ArrayList;
import java.util.List;

import mydomain.model.*;

import org.datanucleus.util.NucleusLogger;

public class RelationTest
{
    /**
     * Test for the persistence of related objects.
     * This creates 100000 Student, + 100000 Credit, + 100000 Thesis objects
     * @throws Exception If an error occurs
     */
    @Test
    public void testMakePersistent()
    throws Exception
    {
        performPersist(100000);
    }

    public void performPersist(final int numObjects)
    throws Exception
    {
        NucleusLogger.GENERAL.info(">> test START");
        // TODO Obtain PMF using standard test mechanism
        final PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("MyTest");

        long start = System.currentTimeMillis();
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            NucleusLogger.GENERAL.info(">> tx.begin");
            for (int x = 0; x < numObjects; x++)
            {
                Student student = new Student();
                Thesis thesis = new Thesis();
                thesis.setComplete(true);
                student.setThesis(thesis);
                List<Credit> credits = new ArrayList<Credit>();
                Credit credit = new Credit();
                credits.add(credit);
                student.setCredits(credits);

                pm.makePersistent(student);

                if ((x % 10000) * 10000 == 0 && x != 0)
                {
                    NucleusLogger.GENERAL.info(">> pm.flush " + x);
                    pm.flush();
                }
            }
            NucleusLogger.GENERAL.info(">> tx.commit");
            tx.commit();
            NucleusLogger.GENERAL.info(">> tx ended");
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        System.out.println("makePersistent(objs=" + numObjects + " Student+Thesis+Credit) time(ms)=" + (System.currentTimeMillis() - start));

        pmf.close();
    }
}
