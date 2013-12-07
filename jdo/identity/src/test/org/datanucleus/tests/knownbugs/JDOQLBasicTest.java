/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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
***********************************************************************/
package org.datanucleus.tests.knownbugs;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.inheritance.ABase;
import org.jpox.samples.inheritance.ASub1;
import org.jpox.samples.inheritance.ASub2;

/**
 * Tests for JDOQL basic operations.
 */
public class JDOQLBasicTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public JDOQLBasicTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    ABase.class,
                    ASub1.class,
                    ASub2.class,
                });
            initialised = true;
        }        
    }

    /**
     * Test use of cast on case with inheritance using a union.
     * See NUCRDBMS-325
     */
    public void testInheritanceCastWithUnion()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ABase base = new ABase();
            ASub1 sub1 = new ASub1();
            ASub2 sub2 = new ASub2();
            pm.makePersistent(base);
            pm.makePersistent(sub1);
            pm.makePersistent(sub2);
            tx.commit();

            // Run the test
            tx.begin();
            Query q = pm.newQuery(ABase.class,
                "this instanceof " + ASub1.class.getName() + " || this instanceof " + ASub2.class.getName());
            List c = (List) q.execute();
            assertEquals("Number of items returned from instanceof+union query was incorrect", 2, c.size());
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception during test", e);
            fail("Exception thrown during test " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(ASub1.class);
            clean(ASub2.class);
            clean(ABase.class);
        }
    }
}