/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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

import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.rdbms.sqlfunction.SQLFunction;

/**
 * Tests that use SQL functions during the persistence process.
 * For example UPPER() on a field at insert.
 *
 * @version $Revision: 1.4 $
 */
public class SQLFunctionPersistenceTest extends JDOPersistenceTestCase
{
    /**
     * @param name
     */
    public SQLFunctionPersistenceTest(String name)
    {
        super(name);
    }

    public void testSelectWithSQLFunction()
    {
        try
        {
            SQLFunction function;
            Object id= null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                function = new SQLFunction();
                function.setText("upper");
                pm.makePersistent(function);
                id = JDOHelper.getObjectId(function);
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
            pmf.getDataStoreCache().evictAll(false, SQLFunction.class); // Avoid L2 cache interference

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                function = (SQLFunction) pm.getObjectById(id,true);
                assertEquals("text String retrieved is wrong","UPPER",function.getText());
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
            pmf.getDataStoreCache().evictAll(false, SQLFunction.class); // Avoid L2 cache interference

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.setIgnoreCache(true);
                function = (SQLFunction) ((Collection)pm.newQuery(SQLFunction.class).execute()).iterator().next();
                assertEquals("text String retrieved is wrong","UPPER",function.getText());
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
            clean(SQLFunction.class);
        }
    }

    public void testInsertWithSQLFunction()
    {
        try
        {
            SQLFunction function;
            Object id= null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                function = new SQLFunction();
                function.setText("upper");
                function.setText1("t1");
                function.setText2("t2");
                function.setText3("t3");
                pm.makePersistent(function);
                id = JDOHelper.getObjectId(function);
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
            pmf.getDataStoreCache().evictAll(false, SQLFunction.class); // Avoid L2 cache interference

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                function = (SQLFunction) pm.getObjectById(id,true);
                assertEquals("text String retrieved is wrong","UPPER",function.getText());
                assertEquals("text String retrieved is wrong","T1",function.getText1());
                assertEquals("text String retrieved is wrong","valuei",function.getText2());
                assertEquals("text String retrieved is wrong","T3",function.getText3());
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
            clean(SQLFunction.class);
        }
    }

    public void testUpdateWithSQLFunction()
    {
        try
        {
            SQLFunction function;
            Object id= null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                function = new SQLFunction();
                function.setText("upper");
                function.setText1("t1");
                function.setText2("t2");
                function.setText3("t3");
                pm.makePersistent(function);
                id = JDOHelper.getObjectId(function);
                tx.commit();
                tx.begin();
                function.setText2("t2-1");
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
            pmf.getDataStoreCache().evictAll(false, SQLFunction.class); // Avoid L2 cache interference

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                function = (SQLFunction) pm.getObjectById(id,true);
                assertEquals("text String retrieved is wrong","UPPER",function.getText());
                assertEquals("text String retrieved is wrong","T1",function.getText1());
                assertEquals("text String retrieved is wrong","valueu",function.getText2());
                assertEquals("text String retrieved is wrong","T3",function.getText3());
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
            clean(SQLFunction.class);
        }
    }
}