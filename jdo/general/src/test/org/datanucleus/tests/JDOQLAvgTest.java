/**********************************************************************
Copyright (c) 2014 Renato Garcia and others. All rights reserved.
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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.beans.Introspector;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.types.basic.BasicTypeHolder;
import org.jpox.samples.types.basic.DecimalHolder;
import org.jpox.samples.types.basic.FloatHolder;
import org.junit.Test;

public class JDOQLAvgTest extends JDOPersistenceTestCase
{
    @Test(expected = JDOException.class)
    public void testNoArg()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            Query query = pm.newQuery(BasicTypeHolder.class);
            query.setResult("AVG()");

            tx.begin();
            query.execute();
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
    
    @Test(expected = JDOException.class)
    public void testMultipleArgs()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            Query query = pm.newQuery(BasicTypeHolder.class);
            query.setResult("AVG(shortField,intField)");

            tx.begin();
            query.execute();
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

    public void testAvgOnIntegralTypes() throws Exception
    {
        testAvgTypes(this::newBasicTypeHolderWith, 7.5, 10, 5);
    }

    public void testAvgOnFloatingPointTypes() throws Exception
    {
        testAvgTypes(this::newFloatHolderWith, 7.5, 10, 5);
    }

    public void testAvgOnArbritaryPrecisionTypes() throws Exception
    {
        testAvgTypes(this::newDecimalHolderWith, 7.5, 10, 5);
    }

    public void testAvgTypesSubquery() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        // Create sample holders for each value that will avg to a decimal
        BasicTypeHolder holder1 = newBasicTypeHolderWith(10);
        BasicTypeHolder holder2 = newBasicTypeHolderWith(5);
        holder1.setCharField('a');
        holder2.setCharField('a');

        // Additional entry that to be filtered by the where clause
        BasicTypeHolder holder3 = newBasicTypeHolderWith(3);
        holder3.setCharField('b');

        try
        {
            tx.begin();
            pm.makePersistentAll(holder1, holder2);
            tx.commit();

            // Do a query for each numeric field to check all the types
            for (String field : extractNumericFields(holder1))
            {
                Query query = pm.newQuery(
                        "SELECT charField FROM org.jpox.samples.types.basic.BasicTypeHolder WHERE avg("
                                + field
                                + ") == 7.5 GROUP BY charField ");

                tx.begin();
                @SuppressWarnings("unchecked")
                List<Character> result = (List<Character>) query.execute();

                assertThat(result)
                        .as("Should filter based on AVG decimal value")
                        .containsExactly('a');

                tx.commit();
            }
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();

            clean(BasicTypeHolder.class);
        }
    }

    private <T extends TestObject> void testAvgTypes(Function<Integer, T> holderFactory, double expectedResult, Integer... values)
        throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        // Create sample holders for each value
        List<T> holders = Stream.of(values).map(holderFactory).collect(toList());
        T sampleHolder = holders.get(0);
        Class<?> holderClass = sampleHolder.getClass();

        try
        {
            tx.begin();
            pm.makePersistentAll(holders);
            tx.commit();

            Set<String> fields = extractNumericFields(sampleHolder);

            // Create the result query: avg(fieldName1),avg(fieldName2)...
            String avgResultQuery = fields.stream()
                    .map(fieldName -> "AVG(" + fieldName + ")")
                    .collect(joining(","));

            tx.begin();

            // Query the avg for the given fields on the holderClass
            Query query = pm.newQuery(holderClass);
            query.setResult(avgResultQuery);
            Object[] result = (Object[]) query.execute();

            assertThat(result)
                    .as("Avg return type for numeric values should be Double")
                    .hasSameSizeAs(fields)
                    .containsOnly(expectedResult)
                    .extracting("class")
                    .containsOnly(Double.class);

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();

            // Clean out our data
            clean(holderClass);
        }
    }

    private <T extends TestObject> Set<String> extractNumericFields(T holder) throws Exception
    {
        return Stream.of(Introspector.getBeanInfo(holder.getClass()).getPropertyDescriptors())
                .filter(pd -> pd.getName().matches("(?i).*(short|int|long|float|double|integer|decimal).*"))
                .map(pd -> pd.getName())
                .collect(toSet());
    }

    private DecimalHolder newDecimalHolderWith(int value)
    {
        DecimalHolder decimalHolder = new DecimalHolder();
        decimalHolder.setBigIntegerField(BigInteger.valueOf(value));
        decimalHolder.setBigDecimalField(new BigDecimal(value));

        return decimalHolder;
    }

    private FloatHolder newFloatHolderWith(int value)
    {
        FloatHolder floatHolder = new FloatHolder();
        floatHolder.setFloatField(value);
        floatHolder.setFloatObjField(new Float(value));
        floatHolder.setDoubleField(value);
        floatHolder.setDoubleObjField((double) value);

        return floatHolder;
    }

    private BasicTypeHolder newBasicTypeHolderWith(int value)
    {
        short shortValue = (short) value;

        BasicTypeHolder typeHolder = new BasicTypeHolder();
        typeHolder.setCharField((char)value);
        typeHolder.setShortField(shortValue);
        typeHolder.setShortObjField(shortValue);
        typeHolder.setIntField(value);
        typeHolder.setIntObjField(value);
        typeHolder.setLongField(value);
        typeHolder.setLongObjField((long) value);

        return typeHolder;
    }
}
