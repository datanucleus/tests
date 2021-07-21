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
package org.datanucleus.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.types.basic.DateHolder;

/**
 * Tests for query tests using particular types.
 *
 * @version $Revision: 1.5 $
 */
public class TypeQueryTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public TypeQueryTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    DateHolder.class,
                });
            initialised = true;
        }
    }

    /**
     * Test for persistence of a java.util.Date with a jdbc-type of "DATE" and queried against
     * an input date parameter.
     */
    public void testQueryWithDateJdbcTypeChange()
    {
        try
        {
            // Persist some objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Calendar cal = Calendar.getInstance();
            cal.set(2005, 5, 20);
            Date cutOffDate = cal.getTime();
            try
            {
                tx.begin();

                DateHolder holder1 = new DateHolder();
                Calendar cal1 = Calendar.getInstance();
                cal1.set(2003, 6, 1);
                holder1.setDateField(cal1.getTime());
                DateHolder holder2 = new DateHolder();
                Calendar cal2 = Calendar.getInstance();
                cal2.set(2006, 6, 1);
                holder2.setDateField(cal2.getTime());

                pm.makePersistent(holder1);
                pm.makePersistent(holder2);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error("Exception thrown persisting object with date field", e);
                fail("Exception thrown persisting objects : " + e.getMessage());
                return;
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Query using an input parameter
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query q = pm.newQuery(DateHolder.class, "dateField < :myDate");
                List results = (List)q.execute(cutOffDate);
                assertEquals("Number of objects returned from Query of date is wrong", 1, results.size());
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error("Exception thrown querying object using date parameter", e);
                fail("Exception thrown querying object using date parameter with changed jdbc-type : " + e.getMessage());
                return;
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
            // Clean out our data
            clean(DateHolder.class);
        }
    }
}