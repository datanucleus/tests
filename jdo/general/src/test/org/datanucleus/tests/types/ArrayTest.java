/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.types;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.spi.PersistenceCapable;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.array.ArrayElement;
import org.jpox.samples.array.ArrayHolderInterface;
import org.jpox.samples.array.BigDecimalArray;
import org.jpox.samples.array.BigIntegerArray;
import org.jpox.samples.array.BooleanArray;
import org.jpox.samples.array.BooleanObjectArray;
import org.jpox.samples.array.ByteArray;
import org.jpox.samples.array.ByteObjectArray;
import org.jpox.samples.array.CharArray;
import org.jpox.samples.array.CharObjectArray;
import org.jpox.samples.array.DateArray;
import org.jpox.samples.array.DoubleArray;
import org.jpox.samples.array.DoubleObjectArray;
import org.jpox.samples.array.FloatArray;
import org.jpox.samples.array.FloatObjectArray;
import org.jpox.samples.array.IntArray;
import org.jpox.samples.array.IntObjectArray;
import org.jpox.samples.array.InterfaceArray;
import org.jpox.samples.array.LocaleArray;
import org.jpox.samples.array.LongArray;
import org.jpox.samples.array.LongObjectArray;
import org.jpox.samples.array.ObjectArray;
import org.jpox.samples.array.PersistableArray;
import org.jpox.samples.array.ShortArray;
import org.jpox.samples.array.ShortObjectArray;
import org.jpox.samples.array.StringArray;
import org.jpox.samples.interfaces.Rectangle;
import org.jpox.samples.interfaces.Shape;

/**
 * Test case to test use of Arrays.
 * @version $Revision: 1.5 $
 **/
public class ArrayTest extends JDOPersistenceTestCase
{
    protected Transaction tx;
    protected PersistenceManager pm;

    public ArrayTest(String name)
    {
        super(name);
    }

    // ---------------------------- Serialised tests --------------------------------------

    /**
     * Test for boolean[] array, stored serialised or in a join table.
     */
    public void testBooleanArray()
    {
        boolean[] elements = new boolean[] {true, false, true, false};
        BooleanArray holder = new BooleanArray(elements, elements);
        performArrayTest(holder, boolean[].class, elements, elements, 0.0);

        holder = new BooleanArray(null, null);
        performArrayTest(holder, boolean[].class, null, null, 0.0);
    }

    /**
     * Test for byte[] array, stored serialised or in a join table.
     */
    public void testByteArray()
    {
        byte[] elements = new byte[] {1, 0, 1, 1};
        ByteArray holder = new ByteArray(elements, elements);
        performArrayTest(holder, byte[].class, elements, elements, 0.0);

        holder = new ByteArray(null, null);
        performArrayTest(holder, byte[].class, null, null, 0.0);
    }

    /**
     * Test for char[] array, stored serialised or in a join table.
     */
    public void testCharArray()
    {
        char[] elements = new char[] {'A', 'B', 'C'};
        CharArray holder = new CharArray(elements, elements);
        performArrayTest(holder, char[].class, elements, elements, 0.0);

        holder = new CharArray(null, null);
        performArrayTest(holder, char[].class, null, null, 0.0);
    }

    /**
     * Test for double[] array, stored serialised or in a join table.
     */
    public void testDoubleArray()
    {
        double[] elements = new double[] {12.34567, 23.45678, 1.00, -299.89};
        DoubleArray holder = new DoubleArray(elements, elements);
        performArrayTest(holder, double[].class, elements, elements, 0.00001);

        holder = new DoubleArray(null, null);
        performArrayTest(holder, double[].class, null, null, 0.0);
    }

    /**
     * Test for float[] array, stored serialised or in a join table.
     */
    public void testFloatArray()
    {
        float[] elements = new float[] {(float)12.34, (float)34.5};
        FloatArray holder = new FloatArray(elements, elements);
        performArrayTest(holder, float[].class, elements, elements, 0.00001);

        holder = new FloatArray(null, null);
        performArrayTest(holder, float[].class, null, null, 0.0);
    }

    /**
     * Test for int[] array, stored serialised or in a join table.
     */
    public void testIntArray()
    {
        int[] elements = new int[] {2001, 4001, 6004, 4000};
        IntArray holder = new IntArray(elements, elements);
        performArrayTest(holder, int[].class, elements, elements, 0.0);

        holder = new IntArray(null, null);
        performArrayTest(holder, int[].class, null, null, 0.0);
    }

    /**
     * Test for long[] array, stored serialised or in a join table.
     */
    public void testLongArray()
    {
        long[] elements = new long[] {123456789, 432156789, 1};
        LongArray holder = new LongArray(elements, elements);
        performArrayTest(holder, long[].class, elements, elements, 0.0);

        holder = new LongArray(null, null);
        performArrayTest(holder, long[].class, null, null, 0.0);
    }

    /**
     * Test for short[] array, stored serialised or in a join table.
     */
    public void testShortArray()
    {
        short[] elements = new short[] {123, 24, 1};
        ShortArray holder = new ShortArray(elements, elements);
        performArrayTest(holder, short[].class, elements, elements, 0.0);

        holder = new ShortArray(null, null);
        performArrayTest(holder, short[].class, null, null, 0.0);
    }

    /**
     * Test for Boolean[] array, stored serialised or in a join table.
     */
    public void testBooleanObjectArray()
    {
        Boolean[] elements = new Boolean[] {new Boolean(true), new Boolean(false), new Boolean(true), new Boolean(false)};
        BooleanObjectArray holder = new BooleanObjectArray(elements, elements);
        performArrayTest(holder, Boolean[].class, elements, elements, 0.0);

        holder = new BooleanObjectArray(null, null);
        performArrayTest(holder, Boolean[].class, null, null, 0.0);
    }

    /**
     * Test for Byte[] array, stored serialised or in a join table.
     */
    public void testByteObjectArray()
    {
        Byte[] elements = new Byte[] {new Byte("1"), new Byte("0"), new Byte("1"), new Byte("1")};
        ByteObjectArray holder = new ByteObjectArray(elements, elements);
        performArrayTest(holder, Byte[].class, elements, elements, 0.0);

        holder = new ByteObjectArray(null, null);
        performArrayTest(holder, Byte[].class, null, null, 0.0);
    }

    /**
     * Test for Character[] array, stored serialised or in a join table.
     */
    public void testCharObjectArray()
    {
        Character[] elements = new Character[] {new Character('A'), new Character('B'), new Character('C')};
        CharObjectArray holder = new CharObjectArray(elements, elements);
        performArrayTest(holder, Character[].class, elements, elements, 0.0);

        holder = new CharObjectArray(null, null);
        performArrayTest(holder, Character[].class, null, null, 0.0);
    }

    /**
     * Test for Double[] array, stored serialised or in a join table.
     */
    public void testDoubleObjectArray()
    {
        Double[] elements = new Double[] {new Double(12.34567), new Double(23.45678), new Double(1.00), new Double(-299.89)};
        DoubleObjectArray holder = new DoubleObjectArray(elements, elements);
        performArrayTest(holder, Double[].class, elements, elements, 0.00001);

        holder = new DoubleObjectArray(null, null);
        performArrayTest(holder, Double[].class, null, null, 0.0);
    }

    /**
     * Test for Float[] array, stored serialised or in a join table.
     */
    public void testFloatObjectArray()
    {
        Float[] elements = new Float[] {new Float(12.34), new Float(34.5)};
        FloatObjectArray holder = new FloatObjectArray(elements, elements);
        performArrayTest(holder, Float[].class, elements, elements, 0.00001);

        holder = new FloatObjectArray(null, null);
        performArrayTest(holder, Float[].class, null, null, 0.0);
    }

    /**
     * Test for Integer[] array, stored serialised or in a join table.
     */
    public void testIntObjectArray()
    {
        Integer[] elements = new Integer[] {new Integer(2001), new Integer(4001), new Integer(6004), new Integer(4000)};
        IntObjectArray holder = new IntObjectArray(elements, elements);
        performArrayTest(holder, Integer[].class, elements, elements, 0.0);

        holder = new IntObjectArray(null, null);
        performArrayTest(holder, Integer[].class, null, null, 0.0);
    }

    /**
     * Test for Long[] array, stored serialised or in a join table.
     */
    public void testLongObjectArray()
    {
        Long[] elements = new Long[] {new Long(123456789), new Long(432156789), new Long(1)};
        LongObjectArray holder = new LongObjectArray(elements, elements);
        performArrayTest(holder, Long[].class, elements, elements, 0.0);

        holder = new LongObjectArray(null, null);
        performArrayTest(holder, Long[].class, null, null, 0.0);
    }

    /**
     * Test for Short[] array, stored serialised or in a join table.
     */
    public void testShortObjectArray()
    {
        Short[] elements = new Short[] {new Short("123"), new Short("24"), new Short("1")};
        ShortObjectArray holder = new ShortObjectArray(elements, elements);
        performArrayTest(holder, Short[].class, elements, elements, 0.0);

        holder = new ShortObjectArray(null, null);
        performArrayTest(holder, Short[].class, null, null, 0.0);
    }

    /**
     * Test for BigDecimal[] array, stored serialised or in a join table.
     */
    public void testBigDecimalArray()
    {
        BigDecimal[] elements = new BigDecimal[] {new BigDecimal(12.34567), new BigDecimal(23.45678), 
                new BigDecimal(1.00), new BigDecimal(-299.89)};
        BigDecimalArray holder = new BigDecimalArray(elements, elements);
        performArrayTest(holder, BigDecimal[].class, elements, elements, 0.00001);

        holder = new BigDecimalArray(null, null);
        performArrayTest(holder, BigDecimal[].class, null, null, 0.0);
    }

    /**
     * Test for BigInteger[] array, stored serialised or in a join table.
     */
    public void testBigIntegerArray()
    {
        BigInteger[] elements = new BigInteger[] {new BigInteger("12"), new BigInteger("23"), new BigInteger("1"), new BigInteger("-299")};
        BigIntegerArray holder = new BigIntegerArray(elements, elements);
        performArrayTest(holder, BigInteger[].class, elements, elements, 0.0);

        holder = new BigIntegerArray(null, null);
        performArrayTest(holder, BigInteger[].class, null, null, 0.0);
    }

    /**
     * Test for Date[] array, stored serialised or in a join table.
     */
    public void testDateArray()
    {
        Date[] elements = new Date[] {new Date(0), new Date(10000000), new Date(20000000)};
        DateArray holder = new DateArray(elements, elements);
        performArrayTest(holder, Date[].class, elements, elements, 0.0);

        holder = new DateArray(null, null);
        performArrayTest(holder, Date[].class, null, null, 0.0);
    }

    /**
     * Test for Locale[] array, stored serialised or in a join table.
     */
    public void testLocaleArray()
    {
        Locale[] elements = new Locale[] {Locale.ENGLISH, Locale.JAPANESE, Locale.GERMAN};
        LocaleArray holder = new LocaleArray(elements, elements);
        performArrayTest(holder, Locale[].class, elements, elements, 0.0);

        holder = new LocaleArray(null, null);
        performArrayTest(holder, Locale[].class, null, null, 0.0);
    }

    /**
     * Test for String[] array, stored serialised or in a join table.
     */
    public void testStringArray()
    {
        String[] elements = new String[] {"First string", "Second string", "Third string that is much longer"};
        StringArray holder = new StringArray(elements, elements);
        performArrayTest(holder, String[].class, elements, elements, 0.0);

        holder = new StringArray(null, null);
        performArrayTest(holder, String[].class, null, null, 0.0);
    }

    /**
     * Test for String[] array, stored serialised or in a join table, including some nulls.
     */
    public void testStringArrayWithNulls()
    {
        String[] elements = new String[] {"First string", null, "Third string that is much longer"};
        StringArray holder = new StringArray(elements, elements);
        performArrayTest(holder, String[].class, elements, elements, 0.0);

        holder = new StringArray(null, null);
        performArrayTest(holder, String[].class, null, null, 0.0);
    }

    /**
     * Test for a field of type Interface[] stored serialised or in a join table.
     */
    public void testInterfaceArray()
    {
        Shape[] shapes = new Shape[2];
        shapes[0] = new Rectangle(1, 25.0, 20.0);
        shapes[1] = new Rectangle(2, 35.0, 10.0);
        InterfaceArray holder = new InterfaceArray(shapes, shapes);
        Shape[] expectedShapes = new Shape[2];
        expectedShapes[0] = (Shape)((Rectangle)shapes[0]).clone();
        expectedShapes[1] = (Shape)((Rectangle)shapes[1]).clone();

        performArrayTest(holder, Shape[].class, expectedShapes, expectedShapes, 0.0);

        holder = new InterfaceArray(null, null);
        performArrayTest(holder, Shape[].class, null, null, 0.0);
        
        clean(Rectangle.class);
    }

    /**
     * Test for a field of type PC[] stored serialised or in a join table.
     */
    public void testPersistableArray()
    {
        ArrayElement[] elements = new ArrayElement[3];
        elements[0] = new ArrayElement("101", "First element");
        elements[1] = new ArrayElement("102", "Second element");
        elements[2] = new ArrayElement("103", "Third element");
        PersistableArray holder = new PersistableArray(elements, elements);
        ArrayElement[] expectedElements = new ArrayElement[3];
        expectedElements[0] = (ArrayElement)elements[0].clone();
        expectedElements[1] = (ArrayElement)elements[1].clone();
        expectedElements[2] = (ArrayElement)elements[2].clone();
        performArrayTest(holder, ArrayElement[].class, expectedElements, expectedElements, 0.0);

        holder = new PersistableArray(null, null);
        performArrayTest(holder, ArrayElement[].class, null, null, 0.0);
    }

    /**
     * Test for a field of type PC[] stored in a join table, and having nulls.
     */
    public void testPersistableArrayWithNulls()
    {
        ArrayElement[] elements = new ArrayElement[3];
        elements[0] = new ArrayElement("101", "First element");
        elements[1] = null;
        elements[2] = new ArrayElement("103", "Third element");
        PersistableArray holder = new PersistableArray(elements, elements);
        ArrayElement[] expectedElements = new ArrayElement[3];
        expectedElements[0] = (ArrayElement)elements[0].clone();
        expectedElements[1] = null;
        expectedElements[2] = (ArrayElement)elements[2].clone();
        performArrayTest(holder, ArrayElement[].class, expectedElements, expectedElements, 0.0);

        holder = new PersistableArray(null, null);
        performArrayTest(holder, ArrayElement[].class, null, null, 0.0);
    }

    /**
     * Test for a field of type Object[] (as PCs) stored.
     */
    public void testObjectArray()
    {
        ArrayElement[] elements = new ArrayElement[3];
        elements[0] = new ArrayElement("101", "First element");
        elements[1] = new ArrayElement("102", "Second element");
        elements[2] = new ArrayElement("103", "Third element");
        ObjectArray holder = new ObjectArray(elements, elements);
        ArrayElement[] expectedElements = new ArrayElement[3];
        expectedElements[0] = (ArrayElement)elements[0].clone();
        expectedElements[1] = (ArrayElement)elements[1].clone();
        expectedElements[2] = (ArrayElement)elements[2].clone();
        performArrayTest(holder, ArrayElement[].class, expectedElements, expectedElements, 0.0);

        holder = new ObjectArray(null, null);
        performArrayTest(holder, ArrayElement[].class, null, null, 0.0);
    }

    /**
     * Test for a field of type PC[] stored using a foreign-key.
     */
    /*public void testForeignKeyPCArray()
    {
        org.jpox.samples.array.foreignkey.Product[] products = new org.jpox.samples.array.foreignkey.Product[3];
        products[0] = new org.jpox.samples.array.foreignkey.Product("100", "Toaster", "New toaster", "http://www.jpox.org", "GBP", 17.5, 17.5, 17.5, 0.0, 1);
        products[1] = new org.jpox.samples.array.foreignkey.Product("101", "Kettle", "Kettle", "http://www.jpox.org", "GBP", 10.0, 10.0, 10.0, 0.0, 1);
        products[2] = new org.jpox.samples.array.foreignkey.Product("102", "Microwave", "Microwave oven", "http://www.jpox.org", "GBP", 65.0, 65.0, 65.0, 0.0, 1);
        MyPCArrayFK holder = new MyPCArrayFK(products);
        org.jpox.samples.array.foreignkey.Product[] expectedProducts = new org.jpox.samples.array.foreignkey.Product[3];
        expectedProducts[0] = (org.jpox.samples.array.foreignkey.Product)products[0].clone();
        expectedProducts[1] = (org.jpox.samples.array.foreignkey.Product)products[1].clone();
        expectedProducts[2] = (org.jpox.samples.array.foreignkey.Product)products[2].clone();

        performArrayTest(holder, org.jpox.samples.array.foreignkey.Product[].class, expectedProducts);

        holder = new MyPCArrayFK(null);
        performArrayTest(holder, org.jpox.samples.array.foreignkey.Product[].class, null);
    }*/

    // ---------------------------- Array querying tests --------------------------------------

    /**
     * Test for a field of type int[] stored in a join table.
     */
    public void testJoinTableIntQueryArray()
    {
        int[] elements = new int[] {2001, 4001, 6004, 4000};
        IntArray holder = new IntArray(elements, elements);
        performArrayQueryTest(holder, int[].class, elements);
        performArrayTest(holder, int[].class, null, null, 0.0);
    }

    /**
     * Test for a field of type String[] stored in a join table.
     */
    public void testJoinTableStringQueryArray()
    {
        String[] elements = new String[] {"First string", "Second string", "Third string that is much longer"};
        StringArray holder = new StringArray(elements, elements);
        performArrayQueryTest(holder, String[].class, elements);
    }

    /**
     * Test for a field of type PC[] stored using a join table.
     */
    public void testJoinTablePCQueryArray()
    {
        ArrayElement[] elements = new ArrayElement[3];
        elements[0] = new ArrayElement("100", "First");
        elements[1] = new ArrayElement("101", "Second");
        elements[2] = new ArrayElement("102", "Third");
        PersistableArray holder = new PersistableArray(elements, elements);
        ArrayElement[] expectedElements = new ArrayElement[3];
        expectedElements[0] = (ArrayElement)elements[0].clone();
        expectedElements[1] = (ArrayElement)elements[1].clone();
        expectedElements[2] = (ArrayElement)elements[2].clone();
        performArrayQueryTest(holder, ArrayElement[].class, expectedElements);
    }

    /**
     * Test for a field of type Interface[] stored using a join table.
     */
    public void testJoinTableInterfaceQueryArray()
    {
        Shape[] shapes = new Shape[2];
        shapes[0] = new Rectangle(1, 25.0, 20.0);
        shapes[1] = new Rectangle(2, 35.0, 10.0);
        InterfaceArray holder = new InterfaceArray(shapes, shapes);
        Shape[] expectedShapes = new Shape[2];
        expectedShapes[0] = (Shape)((Rectangle)shapes[0]).clone();
        expectedShapes[1] = (Shape)((Rectangle)shapes[1]).clone();
        performArrayQueryTest(holder, Shape[].class, expectedShapes);

        clean(Rectangle.class);
    }

    /**
     * Test for a field of type PC[] stored using a foreign-key.
     */
    /*public void testForeignKeyPCQueryArray()
    {
        org.jpox.samples.array.foreignkey.Product[] products = new org.jpox.samples.array.foreignkey.Product[3];
        products[0] = new org.jpox.samples.array.foreignkey.Product("100", "Toaster", "New toaster", "http://www.jpox.org", "GBP", 17.5, 17.5, 17.5, 0.0, 1);
        products[1] = new org.jpox.samples.array.foreignkey.Product("101", "Kettle", "Kettle", "http://www.jpox.org", "GBP", 10.0, 10.0, 10.0, 0.0, 1);
        products[2] = new org.jpox.samples.array.foreignkey.Product("102", "Microwave", "Microwave oven", "http://www.jpox.org", "GBP", 65.0, 65.0, 65.0, 0.0, 1);
        MyPCArrayFK holder = new MyPCArrayFK(products);
        org.jpox.samples.array.foreignkey.Product[] expectedProducts = new org.jpox.samples.array.foreignkey.Product[3];
        expectedProducts[0] = (org.jpox.samples.array.foreignkey.Product)products[0].clone();
        expectedProducts[1] = (org.jpox.samples.array.foreignkey.Product)products[1].clone();
        expectedProducts[2] = (org.jpox.samples.array.foreignkey.Product)products[2].clone();

        performArrayQueryTest(holder, org.jpox.samples.array.foreignkey.Product[].class, expectedProducts);

        holder = new MyPCArrayFK(null);
        performArrayTest(holder, org.jpox.samples.array.foreignkey.Product[].class, null);
    }*/

    // -------------------------------- Utilities ------------------------------------------

    /**
     * Method to perform a test of an array type.
     * @param arrayHolder The container object holding the array
     * @param arrayType The type of the array
     * @param expectedArray1 The array elements that we expect for array 1.
     * @param expectedArray2 The array elements that we expect for array 2.
     * @param rounding TODO
     */
    public void performArrayTest(Object arrayHolder, Class arrayType, 
            Object expectedArray1, Object expectedArray2, double rounding)
    {
        try
        {
            // Persist the container
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(arrayHolder);
                tx.commit();
            }
            catch (JDOUserException e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while creating object with array of type " + arrayType + " : " + e.getMessage());
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
                
                // Retrieve the holder
                Query  q = pm.newQuery(pm.getExtent(arrayHolder.getClass(), true));
                List c = (List)q.execute();
                assertEquals("Number of " + arrayHolder.getClass().getName() + " objects retrieved was incorrect", 1, c.size());

                Iterator iter = c.iterator();
                while (iter.hasNext())
                {
                    ArrayHolderInterface theArrayHolder = (ArrayHolderInterface)iter.next();
                    Object theArray1 = null;
                    Object theArray2 = null;
                    try
                    {
                        Class[] argClasses = new Class[] {};
                        Object[] argParams = new Object[] {};
                        Method getArray1Method = ArrayHolderInterface.class.getMethod("getArray1", argClasses); 
                        theArray1 = getArray1Method.invoke(theArrayHolder, argParams);
                        Method getArray2Method = ArrayHolderInterface.class.getMethod("getArray2", argClasses); 
                        theArray2 = getArray2Method.invoke(theArrayHolder, argParams);
                    }
                    catch (Exception e)
                    {
                        LOG.error("Exception thrown in test", e);
                        fail("Failed to get the array(s) from the holder " + arrayHolder.getClass().getName());
                    }

                    // Compare the array elements - 1
                    if (theArray1 != null && expectedArray1 != null)
                    {
                        assertEquals("Number of items in the " + arrayType.getName() + " (1) was incorrect", 
                            Array.getLength(expectedArray1), Array.getLength(theArray1));
                        for (int i=0;i<Array.getLength(expectedArray1);i++)
                        {
                            Object expValue = Array.get(expectedArray1, i);
                            Object actValue = Array.get(theArray1, i);
                            String expType = (expValue != null ? expValue.getClass().getName() : null);
                            String actType = (actValue != null ? actValue.getClass().getName() : null);
                            assertEquals("Item " + i + " in " + arrayType.getName() + " (1) is of wrong type", 
                                expType, actType);
                            assertEquals("Item " + i + " in " + arrayType.getName() + " (1) was wrong", 
                                expValue, actValue);
                        }
                    }
                    else if (theArray1 == null && expectedArray1 != null)
                    {
                        fail("No array items retrieved for (1) yet should have had " + Array.getLength(expectedArray1));
                    }
                    else if (theArray1 == null && expectedArray1 == null)
                    {
                        // Success
                    }
                    else if (theArray1 != null && expectedArray1 == null)
                    {
                        fail("Array items returned for (1) yet should have been null");
                    }

                    // Compare the array elements - 2
                    if (theArray2 != null && expectedArray2 != null)
                    {
                        assertEquals("Number of items in the " + arrayType.getName() + " (2) was incorrect", 
                            Array.getLength(expectedArray2), Array.getLength(theArray2));
                        for (int i=0;i<Array.getLength(expectedArray2);i++)
                        {
                            Object expValue = Array.get(expectedArray2, i);
                            Object actValue = Array.get(theArray2, i);
                            String expType = (expValue != null ? expValue.getClass().getName() : null);
                            String actType = (actValue != null ? actValue.getClass().getName() : null);
                            assertEquals("Item " + i + " in " + arrayType.getName() + " (2) is of wrong type", 
                                expType, actType);
                            if (expValue instanceof Float || expValue instanceof Double ||
                                expValue instanceof BigDecimal)
                            {
                                double expected=0;
                                double actual=0;
                                if (expValue instanceof Float)
                                {
                                    expected = ((Float)expValue).doubleValue();
                                    actual = ((Float)actValue).doubleValue();
                                }
                                else if (expValue instanceof Double)
                                {
                                    expected = ((Double)expValue).doubleValue();
                                    actual = ((Double)actValue).doubleValue();
                                }
                                else if (expValue instanceof BigDecimal)
                                {
                                    expected = ((BigDecimal)expValue).doubleValue();
                                    actual = ((BigDecimal)actValue).doubleValue();
                                }
                                assertTrue("Item " + i + " in " + arrayType.getName() + " (2) was wrong (" + 
                                    Array.get(expectedArray2, i) +")",
                                    (expected <= (actual+rounding)) && (expected >= (actual-rounding))
                                    );
                            }
                            else
                            {
                                assertEquals("Item " + i + " in " + arrayType.getName() + " (2) was wrong", 
                                    expValue, actValue);
                            }
                        }
                    }
                    else if (theArray2 == null && expectedArray2 != null)
                    {
                        fail("No array items retrieved for (2) yet should have had " + Array.getLength(expectedArray2));
                    }
                    else if (theArray2 == null && expectedArray2 == null)
                    {
                        // Success
                    }
                    else if (theArray2 != null && expectedArray2 == null)
                    {
                        fail("Array items returned for (2) yet should have been null");
                    }

                    // Detach the holder with its array(s) - test of detaching
                    pm.getFetchPlan().addGroup(FetchPlan.ALL);
                    pm.detachCopy(theArrayHolder);
                }

                tx.commit();
            }
            catch (JDOUserException e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while querying objects with array of type " + arrayType.getName() + " : " + e.getMessage());
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
            clean(arrayHolder.getClass());

            Class arrayElementType = arrayType.getComponentType();
            if (PersistenceCapable.class.isAssignableFrom(arrayElementType))
            {
                clean(arrayElementType);
            }
        }
    }

    /**
     * Method to perform a test of an array type.
     * @param arrayHolder The container object holding the array
     * @param arrayType The type of the array
     * @param expectedArray The array elements that we expect.
     */
    public void performArrayQueryTest(Object arrayHolder, Class arrayType, Object expectedArray)
    {
        try
        {
            // Persist the container
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(arrayHolder);
                pm.flush();

                // Makes use of "array2" in ArrayHolderInterface as the array that is queryable (join table storage)

                Query  q = pm.newQuery(pm.getExtent(arrayHolder.getClass(), true));
                q.setFilter("this.array2.size() == :p");
                Collection c = (Collection) q.execute(new Integer(Array.getLength(expectedArray)));
                assertEquals("Number of " + arrayHolder.getClass().getName() + " objects retrieved was incorrect", 1, c.size());

                q = pm.newQuery(pm.getExtent(arrayHolder.getClass(), true));
                q.setFilter("this.array2.length == :p");
                c = (Collection) q.execute(new Integer(Array.getLength(expectedArray)));
                assertEquals("Number of " + arrayHolder.getClass().getName() + " objects retrieved was incorrect", 1, c.size());

                q = pm.newQuery(pm.getExtent(arrayHolder.getClass(), true));
                q.setFilter("this.array2.size() == :p");
                c = (Collection) q.execute(new Integer(Array.getLength(expectedArray)));
                assertEquals("Number of " + arrayHolder.getClass().getName() + " objects retrieved was incorrect", 1, c.size());

                q = pm.newQuery(pm.getExtent(arrayHolder.getClass(), true));
                q.setFilter("this.array2.contains(:p)");
                c = (Collection) q.execute(Array.get(((ArrayHolderInterface)arrayHolder).getArray2(),1));
                assertEquals("Number of " + arrayHolder.getClass().getName() + " objects retrieved was incorrect", 1, c.size());
                
                q = pm.newQuery(pm.getExtent(arrayHolder.getClass(), true));
                q.setFilter("this.array2.size() == :p");
                c = (Collection) q.execute(new Integer(0));
                assertEquals(c.size(), 0);
                tx.commit();
            }
            catch (JDOUserException e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while creating object with array of type " + arrayType + " : " + e.getMessage());
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
            clean(arrayHolder.getClass());

            Class arrayElementType = arrayType.getComponentType();
            if (PersistenceCapable.class.isAssignableFrom(arrayElementType))
            {
                clean(arrayElementType);
            }
        }
    }
}