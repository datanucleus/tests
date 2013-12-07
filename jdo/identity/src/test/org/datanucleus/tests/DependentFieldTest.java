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
2004 Andy Jefferson - added table creation in constructor
2005 Andy Jefferson - added bidirectional dependent test
2005 Andy Jefferson - added interface dependent test
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.dependentfield.DepInterfaceImpl1;
import org.jpox.samples.dependentfield.DepInterfaceImpl2;
import org.jpox.samples.dependentfield.DependentElement;
import org.jpox.samples.dependentfield.DependentElement1;
import org.jpox.samples.dependentfield.DependentElement10;
import org.jpox.samples.dependentfield.DependentElement2;
import org.jpox.samples.dependentfield.DependentElement3;
import org.jpox.samples.dependentfield.DependentElement4;
import org.jpox.samples.dependentfield.DependentElement5;
import org.jpox.samples.dependentfield.DependentElement6;
import org.jpox.samples.dependentfield.DependentElement7;
import org.jpox.samples.dependentfield.DependentElement8;
import org.jpox.samples.dependentfield.DependentElement9;
import org.jpox.samples.dependentfield.DependentHolder;

/**
 * Tests for the metadata dependent
 * <ul>
 * <li>dependent</li>
 * <li>dependent-element</li>
 * <li>dependent-value</li>
 * <li>dependent-key</li>
 * </ul> 
 */
public class DependentFieldTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    private static int COLLECTION_SIZE = 2;
    private static int COLLECTION_ELEMENTS_FOR_FIELDS_DEPENDENT = 9;
    private static int COLLECTION_ELEMENTS_FOR_FIELDS_NONDEPENDENT = 9;
    // is the number of elements for collections
    private static int COLLECTION_ELEMENTS_FOR_ALL_FIELDS = COLLECTION_ELEMENTS_FOR_FIELDS_DEPENDENT + COLLECTION_ELEMENTS_FOR_FIELDS_NONDEPENDENT;
    
    private static int ELEMENTS_SIZE = (COLLECTION_SIZE * COLLECTION_ELEMENTS_FOR_ALL_FIELDS) + 1;

    /**
     * Constructor.
     * @param name Name of the test
     */
    public DependentFieldTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        DependentHolder.class,
                        DependentElement.class,
                        DependentElement1.class,
                        DependentElement2.class,
                        DependentElement3.class,
                        DependentElement4.class,
                        DependentElement5.class,
                        DependentElement6.class,
                        DependentElement7.class,
                        DependentElement8.class,
                        DependentElement9.class,
                        DependentElement10.class,
                        DepInterfaceImpl1.class,
                        DepInterfaceImpl2.class,
                    });
            initialised = true;
        }
    }

    /**
     * Test delete of objects with dependent fields using "deletePersistent".
     * Container object "DependentField" contains 
     * <ul>
     * <li>1-1 relation (dependent)</li>
     * <li>2 join-table sets (1 dependent, 1 non-dependent)</li>
     * <li>2 join-table lists (1 dependent, 1 non-dependent)</li>
     * <li>8 maps (all combos of dependent, non-dependent)</li>
     * <li>1 array (dependent)</li>
     * </ul>
     */
    public void testDependentFieldsDeletion()
    {
        try
        {
            // Populate the objects - add 2 elements to each set/list/map
            int nextElement = 0;
            DependentElement element[] = new DependentElement[ELEMENTS_SIZE];
            Object elementObjectId[] = new Object[ELEMENTS_SIZE];
            boolean expectedDelete[] = new boolean[ELEMENTS_SIZE];
            for (int i = 0; i < ELEMENTS_SIZE; i++)
            {
                element[i] = new DependentElement(i, "el" + i);
            }

            DependentHolder holder = new DependentHolder(0, "field0");

            // 1-1 dependent
            expectedDelete[nextElement] = true;
            holder.setElement(element[nextElement++]);

            int indexDependents = nextElement;

            // a). dependents
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = true;
                holder.getListDependent1().add(element[nextElement++]);
            }
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = true;
                expectedDelete[nextElement + 1] = true;
                holder.getMapDependent1().put(element[nextElement++], element[nextElement++]);
            }
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = true;
                expectedDelete[nextElement + 1] = false;
                holder.getMapDependentKeys1().put(element[nextElement++], element[nextElement++]);
            }
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = false;
                expectedDelete[nextElement + 1] = true;
                holder.getMapDependentValues1().put(element[nextElement++], element[nextElement++]);
            }
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = true;
                holder.getSetDependent1().add(element[nextElement++]);
            }
            DependentElement[] array = new DependentElement[COLLECTION_SIZE];
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = true;
                array[i] = element[nextElement++];
            }
            holder.setArrayDependent1(array);

            // b). non dependents
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = false;
                holder.getListNonDependent1().add(element[nextElement++]);
            }
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = false;
                expectedDelete[nextElement + 1] = false;
                holder.getMapNonDependent1().put(element[nextElement++], element[nextElement++]);
            }
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = false;
                expectedDelete[nextElement + 1] = false;
                holder.getMapNonDependentKeys1().put(element[nextElement++], element[nextElement++]);
            }
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = false;
                expectedDelete[nextElement + 1] = false;
                holder.getMapNonDependentValues1().put(element[nextElement++], element[nextElement++]);
            }
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = false;
                holder.getSetNonDependent1().add(element[nextElement++]);
            }
            DependentElement[] nonArray = new DependentElement[COLLECTION_SIZE];
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                expectedDelete[nextElement] = false;
                nonArray[i] = element[nextElement++];
            }
            holder.setArrayNonDependent1(nonArray);

            PersistenceManager pm = pmf.getPersistenceManager();
            Object holderId;
            DependentHolder loaded;
            try
            {
                // Persist the objects
                pm.currentTransaction().begin();
                pm.makePersistent(holder);
                pm.currentTransaction().commit();

                // Access the ids of the objects
                holderId = pm.getObjectId(holder);
                for (int i=0; i<ELEMENTS_SIZE; i++)
                {
                    elementObjectId[i] = pm.getObjectId(element[i]);
                }

                pm.currentTransaction().begin();
                loaded = (DependentHolder) pm.getObjectById(holderId, true);

                // make sure persistence is fine
                assertEquals(COLLECTION_SIZE,loaded.getListDependent1().size());
                assertEquals(COLLECTION_SIZE,loaded.getMapDependent1().size());
                assertEquals(COLLECTION_SIZE,loaded.getMapDependentKeys1().size());
                assertEquals(COLLECTION_SIZE,loaded.getMapDependentValues1().size());
                assertEquals(COLLECTION_SIZE,loaded.getSetDependent1().size());
                assertEquals(COLLECTION_SIZE,loaded.getArrayDependent1().length);
                assertEquals(COLLECTION_SIZE,loaded.getListNonDependent1().size());
                assertEquals(COLLECTION_SIZE,loaded.getMapNonDependent1().size());
                assertEquals(COLLECTION_SIZE,loaded.getMapNonDependentKeys1().size());
                assertEquals(COLLECTION_SIZE,loaded.getMapNonDependentValues1().size());
                assertEquals(COLLECTION_SIZE,loaded.getSetNonDependent1().size());
                assertNotNull(loaded.getElement());

                // Delete the container object
                pm.deletePersistent(loaded);
                pm.currentTransaction().commit();

                // ====================================================================
                // Check the existence of the various objects
                // Only the non-dependent ones should exist
                // ====================================================================
                // Container object
                pm.currentTransaction().begin();
                boolean success = false;
                try
                {
                    success = false;
                    loaded = (DependentHolder)pm.getObjectById(holderId, true);
                }
                catch (JDOObjectNotFoundException ex)
                {
                    success = true;
                }
                finally
                {
                    if (!success)
                    {
                        fail("field should have been deleted");
                    }
                }

                // 1-1 element
                try
                {
                    success = false;
                    pm.getObjectById(elementObjectId[0],true);
                }
                catch (JDOObjectNotFoundException ex)
                {
                    success = true;
                }
                finally
                {
                    if (!success)
                    {
                        fail("dependent field element should have been deleted.");
                    }
                }

                // collection/map elements
                for (int i = 0; i < COLLECTION_ELEMENTS_FOR_ALL_FIELDS; i++)
                {
                    try
                    {
                        success = false;
                        Object pc = pm.getObjectById(elementObjectId[i + indexDependents], true);
                        if (!expectedDelete[i + indexDependents] && !JDOHelper.isDeleted(pc))
                        {
                            success = true;
                        }
                    }
                    catch (JDOObjectNotFoundException ex)
                    {
                        if (expectedDelete[i + indexDependents])
                        {
                            success = true;
                        }
                    }
                    finally
                    {
                        if (!success && expectedDelete[i + indexDependents])
                        {
                            fail("dependent field " + (i + indexDependents) + " should have been deleted.");
                        }
                        else if (!success && !expectedDelete[i + indexDependents])
                        {
                            fail("dependent field " + (i + indexDependents) + " should have not been deleted.");
                        }
                    }
                }
                pm.currentTransaction().commit();
            }
            finally
            {
                if (pm.currentTransaction().isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            // Clean out our data
            clearDependentData(pmf);
        }
    }

    private static int TEST_LISTS = 0;
    private static int TEST_MAP = 1;
    private static int TEST_MAP_VALUES = 2;
    private static int TEST_MAP_KEYS = 3;
    private static int TEST_SET = 4;

    /**
     * test delete of ForeignKey Sets with dependent elements using deletePersistent().
     */
    public void testDependentFieldsInverseSetsDeletion()
    {
        runTestDependentFieldsInverse(TEST_SET);
    }

    /**
     * test delete of ForeignKey Lists with dependent elements using deletePersistent().
     */
    public void testDependentFieldsInverseListsDeletion()
    {
        runTestDependentFieldsInverse(TEST_LISTS);
    }

    /**
     * test delete of objects with dependent fields using deletePersistent().
     */
    public void testDependentFieldsInverseMapsDeletion()
    {
        runTestDependentFieldsInverse(TEST_MAP);
    }

    /**
     * test delete of Foreign-Key Maps with dependent values using deletePersistent().
     */
    public void testDependentFieldsInverseMapsValuesDeletion()
    {
        runTestDependentFieldsInverse(TEST_MAP_VALUES);
    }

    /**
     * test delete of Foreign-Key Maps with dependent keys using deletePersistent().
     */
    public void testDependentFieldsInverseMapsKeysDeletion()
    {
        runTestDependentFieldsInverse(TEST_MAP_KEYS);
    }

    public void runTestDependentFieldsInverse(int testType)
    {
        try
        {
            Object element[] = new Object[(COLLECTION_SIZE * 4)+1];
            Object elementObjectId[] = new Object[(COLLECTION_SIZE * 4)+1];
            boolean expectedDelete[] = new boolean[(COLLECTION_SIZE * 4)+1];
            initElementsForInverse(element, testType);

            PersistenceManager pm = pmf.getPersistenceManager();
            Object fieldObjectId;
            DependentHolder loaded;

            try
            {
                //---------------------------------------------------------------------
                // prepare data
                //---------------------------------------------------------------------
                DependentHolder holder = new DependentHolder(0,"field0");
                holder.setElement((DependentElement)element[0]);
                prepareDataForInverse(holder, element, expectedDelete, testType);
                pm.currentTransaction().begin();
                pm.makePersistent(holder);
                pm.currentTransaction().commit();

                fieldObjectId = pm.getObjectId(holder);
                for (int i = 0; i < elementObjectId.length; i++)
                {
                    elementObjectId[i] = pm.getObjectId(element[i]);
                }

                // ---------------------------------------------------------------------
                // verify data
                // ---------------------------------------------------------------------
                pm.currentTransaction().begin();
                loaded = (DependentHolder) pm.getObjectById(fieldObjectId, true);
                verifyDataForInverse(loaded, testType);

                // ---------------------------------------------------------------------
                // delete data
                // ---------------------------------------------------------------------
                pm.deletePersistent(loaded);
                pm.currentTransaction().commit();

                // ---------------------------------------------------------------------
                // tests: check owner is deleted
                // ---------------------------------------------------------------------
                pm.currentTransaction().begin();
                boolean success = false;
                try
                {
                    success = false;
                    loaded = (DependentHolder) pm.getObjectById(fieldObjectId, true);
                }
                catch (JDOObjectNotFoundException ex)
                {
                    success = true;
                }
                finally
                {
                    if (!success)
                    {
                        fail("field should have been deleted");
                    }
                }

                // ---------------------------------------------------------------------
                // tests: fields
                // ---------------------------------------------------------------------
                for (int i = 0; i < elementObjectId.length; i++)
                {
                    if (elementObjectId[i] != null)
                    {
                        try
                        {
                            success = false;
                            pm.getObjectById(elementObjectId[i], true);
                            if (!expectedDelete[i])
                            {
                                success = true;
                            }
                        }
                        catch (JDOObjectNotFoundException ex)
                        {
                            if (expectedDelete[i])
                            {
                                success = true;
                            }
                        }
                        finally
                        {
                            if (!success && expectedDelete[i])
                            {
                                fail("dependent field " + i + " should have been deleted.");
                            }
                            else if (!success && !expectedDelete[i])
                            {
                                fail("dependent field " + i + " should have not been deleted.");
                            }
                        }
                    }
                }
                pm.currentTransaction().commit();

            }
            finally
            {
                if (pm.currentTransaction().isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            // Clean out our data
            clearDependentData(pmf);
        }
    }

    private void initElementsForInverse(Object element[], int testType)
    {
        element[0] = new DependentElement(0, "el" + 0);
        int index = 1;
        if (testType == TEST_LISTS)
        {
            for (int i = 0; i < (COLLECTION_SIZE * 2); i++)
            {
                element[index] = new DependentElement3(index, "el" + index);
                index++;
            }
            for (int i = 0; i < (COLLECTION_SIZE * 2); i++)
            {
                element[index] = new DependentElement4(index, "el" + index);
                index++;
            }
        }
        else if (testType == TEST_MAP)
        {
            String desc = "key";
            for (int i = 0; i < (COLLECTION_SIZE * 2); i++)
            {
                element[index] = new DependentElement9(index, desc + index);
                desc = desc.equals("key") ? "value" : "key";
                index++;
            }
            desc = "key";
            for (int i = 0; i < (COLLECTION_SIZE * 2); i++)
            {
                element[index] = new DependentElement10(index, desc + index);
                desc = desc.equals("key") ? "value" : "key";
                index++;
            }
        }
        else if (testType == TEST_MAP_KEYS)
        {
            String desc = "key";
            for (int i = 0; i < (COLLECTION_SIZE * 2); i++)
            {
                element[index] = new DependentElement7(index, desc + index);
                desc = desc.equals("key") ? "value" : "key";
                index++;
            }
            desc = "key";
            for (int i = 0; i < (COLLECTION_SIZE * 2); i++)
            {
                element[index] = new DependentElement8(index, desc + index);
                desc = desc.equals("key") ? "value" : "key";
                index++;
            }
        }
        else if (testType == TEST_MAP_VALUES)
        {
            String desc = "key";
            for (int i = 0; i < (COLLECTION_SIZE * 2); i++)
            {
                element[index] = new DependentElement5(index, desc + index);
                desc = desc.equals("key") ? "value" : "key";
                index++;
            }
            desc = "key";
            for (int i = 0; i < (COLLECTION_SIZE * 2); i++)
            {
                element[index] = new DependentElement6(index, desc + index);
                desc = desc.equals("key") ? "value" : "key";
                index++;
            }
        }
        else if (testType == TEST_SET)
        {
            for (int i = 0; i < (COLLECTION_SIZE * 2); i++)
            {
                element[index] = new DependentElement1(index, "el" + index);
                index++;
            }
            for (int i = 0; i < (COLLECTION_SIZE * 2); i++)
            {
                element[index] = new DependentElement2(index, "el" + index);
                index++;
            }
        }
    }

    private void prepareDataForInverse(DependentHolder holder, Object element[], boolean expectedDelete[], int testType)
    {
        int nextElement = 1;
        expectedDelete[0] = true;
        if (testType == TEST_LISTS)
        {
            for (int i = 0; i < (COLLECTION_SIZE); i++)
            {
                expectedDelete[nextElement] = true;
                holder.getListDependent2().add(element[nextElement++]);
            }
            nextElement = ((element.length - 1) / 2) + 1;
            for (int i = 0; i < (COLLECTION_SIZE); i++)
            {
                expectedDelete[nextElement] = false;
                holder.getListNonDependent2().add(element[nextElement++]);
            }
        }
        else if (testType == TEST_MAP)
        {
            for (int i = 0; i < (COLLECTION_SIZE); i++)
            {
                expectedDelete[nextElement] = true;
                expectedDelete[nextElement + 1] = true;
                holder.getMapDependent2().put(element[nextElement++], element[nextElement++]);
            }
            nextElement = ((element.length - 1) / 2) + 1;
            for (int i = 0; i < (COLLECTION_SIZE); i++)
            {
                expectedDelete[nextElement] = false;
                expectedDelete[nextElement + 1] = false;
                holder.getMapNonDependent2().put(element[nextElement++], element[nextElement++]);
            }
        }
        else if (testType == TEST_MAP_VALUES)
        {
            for (int i = 0; i < (COLLECTION_SIZE); i++)
            {
                expectedDelete[nextElement] = false;
                expectedDelete[nextElement + 1] = true;
                holder.getMapDependentValues2().put(element[nextElement++], element[nextElement++]);
            }
            nextElement = ((element.length - 1) / 2) + 1;
            for (int i = 0; i < (COLLECTION_SIZE); i++)
            {
                expectedDelete[nextElement] = false;
                expectedDelete[nextElement + 1] = false;
                holder.getMapNonDependentValues2().put(element[nextElement++], element[nextElement++]);
            }
        }
        else if (testType == TEST_MAP_KEYS)
        {
            for (int i = 0; i < (COLLECTION_SIZE); i++)
            {
                expectedDelete[nextElement] = true;
                expectedDelete[nextElement + 1] = false;
                holder.getMapDependentKeys2().put(element[nextElement++], element[nextElement++]);
            }
            nextElement = ((element.length - 1) / 2) + 1;
            for (int i = 0; i < (COLLECTION_SIZE); i++)
            {
                expectedDelete[nextElement] = false;
                expectedDelete[nextElement + 1] = false;
                holder.getMapNonDependentKeys2().put(element[nextElement++], element[nextElement++]);
            }
        }
        else if (testType == TEST_SET)
        {
            for (int i = 0; i < (COLLECTION_SIZE); i++)
            {
                expectedDelete[nextElement] = true;
                holder.getSetDependent2().add(element[nextElement++]);
            }
            nextElement = ((element.length - 1) / 2) + 1;
            for (int i = 0; i < (COLLECTION_SIZE); i++)
            {
                expectedDelete[nextElement] = false;
                holder.getSetNonDependent2().add(element[nextElement++]);
            }
        }
    }

    private void verifyDataForInverse(DependentHolder holder, int testType)
    {
        if (testType == TEST_LISTS)
        {
            // make sure persistence is fine
            assertEquals(COLLECTION_SIZE, holder.getListDependent2().size());
            assertEquals(COLLECTION_SIZE, holder.getListNonDependent2().size());
            assertEquals(0, holder.getMapDependent2().size());
            assertEquals(0, holder.getMapNonDependent2().size());
            assertEquals(0, holder.getMapDependentValues2().size());
            assertEquals(0, holder.getMapNonDependentValues2().size());
            assertEquals(0, holder.getMapDependentKeys2().size());
            assertEquals(0, holder.getMapNonDependentKeys2().size());
            assertEquals(0, holder.getSetDependent2().size());
            assertEquals(0, holder.getSetNonDependent2().size());
        }
        else if (testType == TEST_MAP)
        {
            // make sure persistence is fine
            assertEquals(COLLECTION_SIZE, holder.getMapDependent2().size());
            assertEquals(COLLECTION_SIZE, holder.getMapNonDependent2().size());
            assertEquals(0, holder.getListDependent2().size());
            assertEquals(0, holder.getListNonDependent2().size());
            assertEquals(0, holder.getMapDependentValues2().size());
            assertEquals(0, holder.getMapNonDependentValues2().size());
            assertEquals(0, holder.getMapDependentKeys2().size());
            assertEquals(0, holder.getMapNonDependentKeys2().size());
            assertEquals(0, holder.getSetDependent2().size());
            assertEquals(0, holder.getSetNonDependent2().size());
        }
        else if (testType == TEST_MAP_VALUES)
        {
            // make sure persistence is fine
            assertEquals(COLLECTION_SIZE, holder.getMapDependentValues2().size());
            assertEquals(COLLECTION_SIZE, holder.getMapNonDependentValues2().size());
            assertEquals(0, holder.getMapDependent2().size());
            assertEquals(0, holder.getMapNonDependent2().size());
            assertEquals(0, holder.getListDependent2().size());
            assertEquals(0, holder.getListNonDependent2().size());
            assertEquals(0, holder.getMapDependentKeys2().size());
            assertEquals(0, holder.getMapNonDependentKeys2().size());
            assertEquals(0, holder.getSetDependent2().size());
            assertEquals(0, holder.getSetNonDependent2().size());
        }
        else if (testType == TEST_MAP_KEYS)
        {
            // make sure persistence is fine
            assertEquals(COLLECTION_SIZE, holder.getMapDependentKeys2().size());
            assertEquals(COLLECTION_SIZE, holder.getMapNonDependentKeys2().size());
            assertEquals(0, holder.getMapDependentValues2().size());
            assertEquals(0, holder.getMapNonDependentValues2().size());
            assertEquals(0, holder.getMapDependent2().size());
            assertEquals(0, holder.getMapNonDependent2().size());
            assertEquals(0, holder.getListDependent2().size());
            assertEquals(0, holder.getListNonDependent2().size());
            assertEquals(0, holder.getSetDependent2().size());
            assertEquals(0, holder.getSetNonDependent2().size());
        }
        else if (testType == TEST_SET)
        {
            // make sure persistence is fine
            assertEquals(COLLECTION_SIZE, holder.getSetDependent2().size());
            assertEquals(COLLECTION_SIZE, holder.getSetNonDependent2().size());
            assertEquals(0, holder.getMapDependentKeys2().size());
            assertEquals(0, holder.getMapNonDependentKeys2().size());
            assertEquals(0, holder.getMapDependentValues2().size());
            assertEquals(0, holder.getMapNonDependentValues2().size());
            assertEquals(0, holder.getMapDependent2().size());
            assertEquals(0, holder.getMapNonDependent2().size());
            assertEquals(0, holder.getListDependent2().size());
            assertEquals(0, holder.getListNonDependent2().size());
        }
        assertNotNull(holder.getElement());
    }

    /**
     * test removal of dependent element from a set/list using JoinTable.
     */
    public void testDependentElementsRemovalUsingJoinTable()
    {
        try
        {
            DependentHolder holder;
            Object fieldObjectId = null;

            DependentElement element[] = new DependentElement[COLLECTION_SIZE*4];
            Object[] elementObjectId = new Object[COLLECTION_SIZE*4];
            boolean expectedDelete[] = new boolean[COLLECTION_SIZE*4];

            holder = new DependentHolder(100, "field100");
            int elementNumber = 0;
            for (int i=0;i<COLLECTION_SIZE;i++)
            {
                element[elementNumber] = new DependentElement(100+elementNumber, "setDepElement" + i);
                holder.getSetDependent1().add(element[elementNumber]);
                expectedDelete[elementNumber] = true;
                elementNumber++;
            }
            for (int i=0;i<COLLECTION_SIZE;i++)
            {
                element[elementNumber] = new DependentElement(100+elementNumber, "setElement" + i);
                holder.getSetNonDependent1().add(element[elementNumber]);
                expectedDelete[elementNumber] = false;
                elementNumber++;
            }
            for (int i=0;i<COLLECTION_SIZE;i++)
            {
                element[elementNumber] = new DependentElement(100+elementNumber, "listDepElement" + i);
                holder.getListDependent1().add(element[elementNumber]);
                expectedDelete[elementNumber] = true;
                elementNumber++;
            }
            for (int i=0;i<COLLECTION_SIZE;i++)
            {
                element[elementNumber] = new DependentElement(100+elementNumber, "listElement" + i);
                holder.getListNonDependent1().add(element[elementNumber]);
                expectedDelete[elementNumber] = false;
                elementNumber++;
            }

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            DependentHolder loaded;
            try
            {
                // Persist the objects
                tx.begin();
                pm.makePersistent(holder);
                tx.commit();

                // Access the ids of the objects
                fieldObjectId = pm.getObjectId(holder);
                for (int i=0; i<COLLECTION_SIZE*4; i++)
                {
                    elementObjectId[i] = pm.getObjectId(element[i]);
                }

                tx.begin();
                loaded = (DependentHolder) pm.getObjectById(fieldObjectId,true);

                // make sure persistence is fine
                assertEquals(COLLECTION_SIZE, loaded.getSetDependent1().size());
                assertEquals(COLLECTION_SIZE, loaded.getSetNonDependent1().size());
                assertEquals(COLLECTION_SIZE, loaded.getListDependent1().size());
                assertEquals(COLLECTION_SIZE, loaded.getListNonDependent1().size());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
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
            tx= pm.currentTransaction();
            try
            {
                // Remove the elements from each Set/List
                // This operation should remove the dependent elements and leave the non-dependent elements
                tx.begin();

                loaded = (DependentHolder) pm.getObjectById(fieldObjectId,true);

                elementNumber = 0;
                for (int i=0;i<COLLECTION_SIZE;i++)
                {
                    DependentElement retrievedElement = (DependentElement)pm.getObjectById(elementObjectId[elementNumber]);
                    loaded.getSetDependent1().remove(retrievedElement);
                    elementNumber++;
                }
                for (int i=0;i<COLLECTION_SIZE;i++)
                {
                    DependentElement retrievedElement = (DependentElement)pm.getObjectById(elementObjectId[elementNumber]);
                    loaded.getSetNonDependent1().remove(retrievedElement);
                    elementNumber++;
                }
                for (int i=0;i<COLLECTION_SIZE;i++)
                {
                    DependentElement retrievedElement = (DependentElement)pm.getObjectById(elementObjectId[elementNumber]);
                    loaded.getListDependent1().remove(retrievedElement);
                    elementNumber++;
                }
                for (int i=0;i<COLLECTION_SIZE;i++)
                {
                    DependentElement retrievedElement = (DependentElement)pm.getObjectById(elementObjectId[elementNumber]);
                    loaded.getListNonDependent1().remove(retrievedElement);
                    elementNumber++;
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the results
            pm = pmf.getPersistenceManager();
            tx= pm.currentTransaction();
            try
            {
                tx.begin();

                // Check that the sets/lists are now empty
                loaded = (DependentHolder) pm.getObjectById(fieldObjectId,true);
                assertEquals(0, loaded.getSetDependent1().size());
                assertEquals(0, loaded.getSetNonDependent1().size());
                assertEquals(0, loaded.getListDependent1().size());
                assertEquals(0, loaded.getListNonDependent1().size());

                // Check that the dependent elements have been removed but not the non-dependent elements
                for (int i=0;i<COLLECTION_SIZE*4;i++)
                {
                    try
                    {
                        DependentElement retrievedElement = (DependentElement)pm.getObjectById(elementObjectId[i]);
                        if (retrievedElement != null && expectedDelete[i])
                        {
                            fail("Element " + i + " hasnt been deleted yet should have been since it is dependent and was removed from the JoinTable set/list");
                        }
                    }
                    catch (JDOObjectNotFoundException e)
                    {
                        if (!expectedDelete[i])
                        {
                            fail("Element " + i + " has been deleted yet should only have been nulled when being removed from the JoinTable set/list");
                        }
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
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
            clearDependentData(pmf);
        }
    }

    /**
     * test removal of dependent element from a set/list using ForeignKey.
     */
    public void testDependentElementsRemovalUsingForeignKey()
    {
        try
        {
            DependentHolder field;
            Object fieldObjectId = null;

            Object[] elements = new Object[COLLECTION_SIZE*4];
            Object[] elementObjectId = new Object[COLLECTION_SIZE*4];
            boolean expectedDelete[] = new boolean[COLLECTION_SIZE*4];

            field = new DependentHolder(200, "field200");
            int elementNumber = 0;
            for (int i=0;i<COLLECTION_SIZE;i++)
            {
                elements[elementNumber] = new DependentElement1(200+i, "setDepElement" + i);
                field.getSetDependent2().add(elements[elementNumber]);
                expectedDelete[elementNumber++] = true;
            }
            for (int i=0;i<COLLECTION_SIZE;i++)
            {
                elements[elementNumber] = new DependentElement2(200+i, "setElement" + i);
                field.getSetNonDependent2().add(elements[elementNumber]);
                expectedDelete[elementNumber++] = false;
            }
            for (int i=0;i<COLLECTION_SIZE;i++)
            {
                elements[elementNumber] = new DependentElement3(200+i, "listDepElement" + i);
                field.getListDependent2().add(elements[elementNumber]);
                expectedDelete[elementNumber++] = true;
            }
            for (int i=0;i<COLLECTION_SIZE;i++)
            {
                elements[elementNumber] = new DependentElement4(200+i, "listElement" + i);
                field.getListNonDependent2().add(elements[elementNumber]);
                expectedDelete[elementNumber++] = false;
            }

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            DependentHolder loaded;
            try
            {
                // Persist the objects
                tx.begin();
                pm.makePersistent(field);
                tx.commit();

                // Access the ids of the objects
                fieldObjectId = pm.getObjectId(field);
                for (int i=0; i<COLLECTION_SIZE*4; i++)
                {
                    elementObjectId[i] = pm.getObjectId(elements[i]);
                }

                tx.begin();
                loaded = (DependentHolder) pm.getObjectById(fieldObjectId,true);

                // make sure persistence is fine
                assertEquals(COLLECTION_SIZE, loaded.getSetDependent2().size());
                assertEquals(COLLECTION_SIZE, loaded.getSetNonDependent2().size());
                assertEquals(COLLECTION_SIZE, loaded.getListDependent2().size());
                assertEquals(COLLECTION_SIZE, loaded.getListNonDependent2().size());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data using ForeignKey : " + e.getMessage());
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
            tx= pm.currentTransaction();
            try
            {
                // Remove the elements from each Set/List
                // This operation should remove the dependent elements and leave the non-dependent elements
                tx.begin();

                loaded = (DependentHolder) pm.getObjectById(fieldObjectId,true);

                elementNumber = 0;
                for (int i=0;i<COLLECTION_SIZE;i++)
                {
                    Object retrievedElement = pm.getObjectById(elementObjectId[elementNumber]);
                    loaded.getSetDependent2().remove(retrievedElement);
                    elementNumber++;
                }
                for (int i=0;i<COLLECTION_SIZE;i++)
                {
                    Object retrievedElement = pm.getObjectById(elementObjectId[elementNumber]);
                    loaded.getSetNonDependent2().remove(retrievedElement);
                    elementNumber++;
                }
                for (int i=0;i<COLLECTION_SIZE;i++)
                {
                    Object retrievedElement = pm.getObjectById(elementObjectId[elementNumber]);
                    loaded.getListDependent2().remove(retrievedElement);
                    elementNumber++;
                }
                for (int i=0;i<COLLECTION_SIZE;i++)
                {
                    Object retrievedElement = pm.getObjectById(elementObjectId[elementNumber]);
                    loaded.getListNonDependent2().remove(retrievedElement);
                    elementNumber++;
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field ForeignKey set/list data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the results
            pm = pmf.getPersistenceManager();
            tx= pm.currentTransaction();
            try
            {
                tx.begin();

                // Check that the sets/lists are now empty
                loaded = (DependentHolder) pm.getObjectById(fieldObjectId,true);
                assertEquals(0, loaded.getSetDependent2().size());
                assertEquals(0, loaded.getSetNonDependent2().size());
                assertEquals(0, loaded.getListDependent2().size());
                assertEquals(0, loaded.getListNonDependent2().size());

                // Check that the dependent elements have been removed but not the non-dependent elements
                for (int i=0;i<COLLECTION_SIZE*4;i++)
                {
                    try
                    {
                        pm.getObjectById(elementObjectId[i]);
                        if (expectedDelete[i])
                        {
                            fail("Element " + i + " hasnt been deleted yet should have been since it is dependent and was removed from the ForeignKey set/list");
                        }
                    }
                    catch (JDOObjectNotFoundException e)
                    {
                        if (!expectedDelete[i])
                        {
                            fail("Element " + i + " has been deleted yet should only have been nulled when being removed from the ForeignKey set/list");
                        }
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data using ForeignKey : " + e.getMessage());
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
            clearDependentData(pmf);
        }
    }

    /**
     * test removal of dependent key from a map using JoinTable.
     */
    public void testDependentKeysRemovalUsingJoinTable()
    {
        try
        {
            DependentHolder holder;
            Object fieldObjectId = null;

            DependentElement keys[] = new DependentElement[COLLECTION_SIZE*2];
            DependentElement values[] = new DependentElement[COLLECTION_SIZE*2];
            Object[] keyObjectId = new Object[COLLECTION_SIZE*2];
            boolean expectedDelete[] = new boolean[COLLECTION_SIZE*2];

            // JoinTable map
            holder = new DependentHolder(300, "field300");
            int keyNumber = 0;
            for (int i=0;i<COLLECTION_SIZE;i++)
            {
                keys[keyNumber] = new DependentElement(300+keyNumber, "mapDepKey" + i);
                values[keyNumber] = new DependentElement(350+keyNumber, "mapDepValue" + i);
                holder.getMapDependentKeys1().put(keys[keyNumber], values[keyNumber]);
                expectedDelete[keyNumber] = true;
                keyNumber++;
            }
            for (int i=0;i<COLLECTION_SIZE;i++)
            {
                keys[keyNumber] = new DependentElement(300+keyNumber, "mapDepKey" + i);
                values[keyNumber] = new DependentElement(350+keyNumber, "mapDepValue" + i);
                holder.getMapNonDependentKeys1().put(keys[keyNumber], values[keyNumber]);
                expectedDelete[keyNumber] = false;
                keyNumber++;
            }

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            DependentHolder loaded;
            try
            {
                // Persist the objects
                tx.begin();
                pm.makePersistent(holder);
                tx.commit();

                // Access the ids of the objects
                fieldObjectId = pm.getObjectId(holder);
                for (int i=0; i<COLLECTION_SIZE*2; i++)
                {
                    keyObjectId[i] = pm.getObjectId(keys[i]);
                }

                tx.begin();
                loaded = (DependentHolder) pm.getObjectById(fieldObjectId,true);

                // make sure persistence is fine
                assertEquals(COLLECTION_SIZE, loaded.getMapDependentKeys1().size());
                assertEquals(COLLECTION_SIZE, loaded.getMapNonDependentKeys1().size());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
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
            tx= pm.currentTransaction();
            try
            {
                // Remove the elements from each Map
                // This operation should remove the dependent keys and leave the non-dependent keys
                tx.begin();

                loaded = (DependentHolder) pm.getObjectById(fieldObjectId,true);

                keyNumber = 0;
                for (int i=0;i<COLLECTION_SIZE;i++)
                {
                    DependentElement retrievedKey = (DependentElement)pm.getObjectById(keyObjectId[keyNumber]);
                    loaded.getMapDependentKeys1().remove(retrievedKey);
                    keyNumber++;
                }
                for (int i=0;i<COLLECTION_SIZE;i++)
                {
                    DependentElement retrievedKey = (DependentElement)pm.getObjectById(keyObjectId[keyNumber]);
                    loaded.getMapNonDependentKeys1().remove(retrievedKey);
                    keyNumber++;
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the results
            pm = pmf.getPersistenceManager();
            tx= pm.currentTransaction();
            try
            {
                tx.begin();

                // Check that the maps are now empty
                loaded = (DependentHolder) pm.getObjectById(fieldObjectId,true);
                assertEquals(0, loaded.getMapDependentKeys1().size());
                assertEquals(0, loaded.getMapNonDependentKeys1().size());

                // Check that the dependent keys have been removed but not the non-dependent keys
                for (int i=0;i<COLLECTION_SIZE*2;i++)
                {
                    try
                    {
                        pm.getObjectById(keyObjectId[i]);
                        if (expectedDelete[i])
                        {
                            fail("Key " + i + " hasnt been deleted yet should have been since it is dependent and was removed from the JoinTable map");
                        }
                    }
                    catch (JDOObjectNotFoundException e)
                    {
                        if (!expectedDelete[i])
                        {
                            fail("Key " + i + " has been deleted yet should only have been nulled when being removed from the JoinTable map");
                        }
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
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
            clearDependentData(pmf);
        }
    }

    /**
     * test removal of dependent key from a map using ForeignKey.
     */
    public void testDependentKeysRemovalUsingForeignKey()
    {
        try
        {
            DependentHolder field;
            Object fieldObjectId = null;

            Object keys[] = new Object[COLLECTION_SIZE * 2];
            Object values[] = new Object[COLLECTION_SIZE * 2];
            Object[] keyObjectId = new Object[COLLECTION_SIZE * 2];
            boolean expectedDelete[] = new boolean[COLLECTION_SIZE * 2];

            // JoinTable map
            field = new DependentHolder(400, "field400");
            int keyNumber = 0;
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                keys[keyNumber] = new DependentElement7(400+keyNumber, "mapDepKey" + i);
                values[keyNumber] = new DependentElement7(450 + keyNumber, "mapDepValue" + i);
                field.getMapDependentKeys2().put(keys[keyNumber], values[keyNumber]);
                expectedDelete[keyNumber] = true;
                keyNumber++;
            }
            for (int i = 0; i < COLLECTION_SIZE; i++)
            {
                keys[keyNumber] = new DependentElement8(400+keyNumber, "mapDepKey" + i);
                values[keyNumber] = new DependentElement8(450 + keyNumber, "mapDepValue" + i);
                field.getMapNonDependentKeys2().put(keys[keyNumber], values[keyNumber]);
                expectedDelete[keyNumber] = false;
                keyNumber++;
            }

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            DependentHolder loaded;
            try
            {
                // Persist the objects
                tx.begin();
                pm.makePersistent(field);
                tx.commit();

                // Access the ids of the objects
                fieldObjectId = pm.getObjectId(field);
                for (int i = 0; i < COLLECTION_SIZE * 2; i++)
                {
                    keyObjectId[i] = pm.getObjectId(keys[i]);
                }

                tx.begin();
                loaded = (DependentHolder) pm.getObjectById(fieldObjectId, true);

                // make sure persistence is fine
                assertEquals(COLLECTION_SIZE, loaded.getMapDependentKeys2().size());
                assertEquals(COLLECTION_SIZE, loaded.getMapNonDependentKeys2().size());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
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
                // Remove the elements from each Map
                // This operation should remove the dependent keys and leave the
                // non-dependent keys
                tx.begin();

                loaded = (DependentHolder) pm.getObjectById(fieldObjectId, true);

                keyNumber = 0;
                for (int i = 0; i < COLLECTION_SIZE; i++)
                {
                    Object retrievedKey = pm.getObjectById(keyObjectId[keyNumber]);
                    loaded.getMapDependentKeys2().remove(retrievedKey);
                    keyNumber++;
                }
                for (int i = 0; i < COLLECTION_SIZE; i++)
                {
                    Object retrievedKey = pm.getObjectById(keyObjectId[keyNumber]);
                    loaded.getMapNonDependentKeys2().remove(retrievedKey);
                    keyNumber++;
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the results
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Check that the maps are now empty
                loaded = (DependentHolder) pm.getObjectById(fieldObjectId, true);
                assertEquals(0, loaded.getMapDependentKeys2().size());
                assertEquals(0, loaded.getMapNonDependentKeys2().size());

                // Check that the dependent keys have been removed but not the
                // non-dependent keys
                for (int i = 0; i < COLLECTION_SIZE * 2; i++)
                {
                    try
                    {
                        pm.getObjectById(keyObjectId[i]);
                        if (expectedDelete[i])
                        {
                            fail("Key " + i + " hasnt been deleted yet should have been since it is dependent and was removed from the ForeignKey map");
                        }
                    }
                    catch (JDOObjectNotFoundException e)
                    {
                        if (!expectedDelete[i])
                        {
                            fail("Key " + i + " has been deleted yet should only have been nulled when being removed from the ForeignKey map");
                        }
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
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
            clearDependentData(pmf);
        }
    }

    /**
     * Test of an Inverse 1-N with dependent specified at both ends.
     */
    public void testBidirectionalDependentFields()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object containerId = null;
            Object element1Id = null;
            Object element2Id = null;
            try
            {
                tx.begin();
                DependentHolder holder = new DependentHolder(1,"Basic Container");
                DependentElement1 element1 = new DependentElement1(1, "Element 1");
                DependentElement1 element2 = new DependentElement1(2, "Element 2");
                holder.getSetDependent2().add(element1);
                holder.getSetDependent2().add(element2);
                pm.makePersistent(holder);
                tx.commit();
                containerId = pm.getObjectId(holder);
                element1Id = pm.getObjectId(element1);
                element2Id = pm.getObjectId(element2);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting bidirectional dependent objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Delete the container
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                DependentHolder holder = (DependentHolder)pm.getObjectById(containerId);
                pm.getObjectById(element1Id);
                pm.getObjectById(element2Id);
                assertEquals("Dependent container has incorrect number of dependent elements", 
                    2, holder.getSetDependent2().size());

                // Delete the container
                pm.deletePersistent(holder);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while deleting bidirectional dependent objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check that none of the objects is still present
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                try
                {
                    DependentHolder holder = (DependentHolder)pm.getObjectById(containerId);
                    if (holder != null)
                    {
                        fail("Bidirectional dependent container still exists after deletion!");
                    }
                }
                catch (Exception e)
                {
                }
                try
                {
                    DependentElement1 element1 = (DependentElement1)pm.getObjectById(element1Id);
                    if (element1 != null)
                    {
                        fail("Bidirectional dependent element 1 still exists after deletion!");
                    }
                }
                catch (Exception e)
                {
                }
                try
                {
                    DependentElement1 element2 = (DependentElement1)pm.getObjectById(element2Id);
                    if (element2 != null)
                    {
                        fail("Bidirectional dependent element 2 still exists after deletion!");
                    }
                }
                catch (Exception e)
                {
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while checking for deleted bidirectional dependent objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Recreate the container and elements
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                DependentHolder holder = new DependentHolder(11,"Basic Container 11");
                DependentElement1 element1 = new DependentElement1(11, "Element 11");
                DependentElement1 element2 = new DependentElement1(12, "Element 12");
                holder.getSetDependent2().add(element1);
                holder.getSetDependent2().add(element2);
                pm.makePersistent(holder);
                tx.commit();
                containerId = pm.getObjectId(holder);
                element1Id = pm.getObjectId(element1);
                element2Id = pm.getObjectId(element2);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting bidirectional dependent objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Remove an element
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                DependentHolder holder = (DependentHolder)pm.getObjectById(containerId);
                DependentElement1 element2 = (DependentElement1)pm.getObjectById(element2Id);

                assertEquals("Dependent container has incorrect number of dependent elements", 
                    2, holder.getSetDependent2().size());

                // Remove the second element from the container. This will empty the collection, and then
                // will delete it since it is dependent on the owner, and delete the container since that
                // is dependent on the elements.
                holder.getSetDependent2().remove(element2);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while removing element from container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check that none of the objects is still present
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                try
                {
                    DependentHolder holder = (DependentHolder)pm.getObjectById(containerId);
                    if (holder == null)
                    {
                        fail("Bidirectional dependent container doesn't exist after removal of element!");
                    }
                }
                catch (Exception e)
                {
                }
                try
                {
                    DependentElement1 element1 = (DependentElement1)pm.getObjectById(element1Id);
                    if (element1 == null)
                    {
                        fail("Bidirectional dependent element 1 doesn't exist after removal of element!");
                    }
                }
                catch (Exception e)
                {
                }
                try
                {
                    DependentElement1 element2 = (DependentElement1)pm.getObjectById(element2Id);
                    if (element2 != null)
                    {
                        fail("Bidirectional dependent element 2 still exists after its removal from container. Should have been deleted when removed from the container!");
                    }
                }
                catch (Exception e)
                {
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while checking for bidirectional dependent objects : " + e.getMessage());
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
            clearDependentData(pmf);
        }
    }

    /**
     * Test of dependent fields using interface 1-1 relationship.
     * @throws Exception
     */
    public void testInterfaceDependentFields()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                DependentHolder holder1 = new DependentHolder(101, "Holder 1");
                DepInterfaceImpl1 impl1 = new DepInterfaceImpl1("Impl1", 23);
                holder1.setIntf(impl1);

                DependentHolder holder2 = new DependentHolder(102, "Holder 2");
                DepInterfaceImpl2 impl2 = new DepInterfaceImpl2("Impl2", 56.7);
                holder2.setIntf(impl2);

                pm.makePersistent(holder1);
                pm.makePersistent(holder2);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while creating interface dependent objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the numbers of objects created
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery(DependentHolder.class);
                List results = (List)q.execute();
                assertEquals("Number of DepInterfaceHolder objects created is incorrect", results.size(), 2);

                q = pm.newQuery(DepInterfaceImpl1.class);
                results = (List)q.execute();
                assertEquals("Number of DepInterfaceImpl1 objects created is incorrect", results.size(), 1);

                q = pm.newQuery(DepInterfaceImpl2.class);
                results = (List)q.execute();
                assertEquals("Number of DepInterfaceImpl2 objects created is incorrect", results.size(), 1);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while checking interface dependent objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Try to delete the implementations (checks if any FKs are created)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery(DepInterfaceImpl2.class);
                List results = (List)q.execute();
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    DepInterfaceImpl2 sq = (DepInterfaceImpl2)resultsIter.next();
                    pm.deletePersistent(sq);
                }

                tx.commit();
                fail("JPOX managed to delete a DepInterfaceImpl2 object that was referenced by another object. " + 
                "This should not have happened due to FK constraints. Maybe this RDBMS doesnt manage FKs correctly");
            }
            catch (Exception e)
            {
                // Should come through here since it should throw an exception due to FK constraints
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Delete the holders. This *should* delete the Shapes as well
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery(DependentHolder.class);
                List results = (List)q.execute();
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    DependentHolder holder = (DependentHolder)resultsIter.next();
                    pm.deletePersistent(holder);
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while deleting interface dependent objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the numbers of objects persisted
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery(DependentHolder.class);
                List results = (List)q.execute();
                assertEquals("Number of DepInterfaceHolder objects persisted is incorrect", results.size(), 0);

                q = pm.newQuery(DepInterfaceImpl1.class);
                results = (List)q.execute();
                assertEquals("Number of DepInterfaceImpl1 objects persisted is incorrect", results.size(), 0);

                q = pm.newQuery(DepInterfaceImpl2.class);
                results = (List)q.execute();
                assertEquals("Number of DepInterfaceImpl2 objects persisted is incorrect", results.size(), 0);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while checking interface dependent objects : " + e.getMessage());
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
            clearDependentData(pmf);
        }
    }

    /**
     * Test the nullification of a 1-1 relation marked as dependent.
     */
    public void testNullify1to1Relation()
    {
        try
        {
            Object holderId = null;
            Object dependentObjectId = null;

            // Persist the objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                DependentHolder field = new DependentHolder(0, "field0");
                DependentElement dependent = new DependentElement(0, "el" + 0);
                field.setElement(dependent);

                // Persist the objects
                tx.begin();
                pm.makePersistent(field);
                tx.commit();

                holderId = pm.getObjectId(field);
                dependentObjectId = pm.getObjectId(dependent);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object and nullify the element
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                // This operation should remove the dependent element
                tx.begin();
                DependentHolder loaded = (DependentHolder) pm.getObjectById(holderId, true);
                loaded.setElement(null);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the results
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Check that the dependent field is empty
                DependentHolder loaded = (DependentHolder) pm.getObjectById(holderId, true);
                assertNull(loaded.getElement());

                // Check that the dependent element has been removed.
                try
                {
                    DependentElement retrievedElement = (DependentElement) pm.getObjectById(dependentObjectId);
                    if (retrievedElement != null)
                    {
                        fail("Dependent field hasnt been deleted yet should have been since it is dependent and was removed from the 1-1 Relation");
                    }
                }
                catch (JDOObjectNotFoundException e)
                {
                }
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
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
            clearDependentData(pmf);
        }
    }

    /**
     * Test the nullification of a 1-1 relation marked as dependent, when the value is a transient PC.
     */
    public void testNullifyNonPersistent1to1Relation()
    {
        try
        {
            Object holderId = null;
    
            // Persist the objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                DependentHolder field = new DependentHolder(0, "field0");
    
                // Persist the objects
                tx.begin();
                pm.makePersistent(field);
                tx.commit();
    
                holderId = pm.getObjectId(field);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
    
            // Retrieve the object, set a transient PC and nullify the element
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                // This operation should remove the dependent element
                tx.begin();
                DependentHolder loaded = (DependentHolder) pm.getObjectById(holderId, true);
                DependentElement dependent = new DependentElement(0, "el" + 0);
                loaded.setElement(dependent);
                
                loaded.setElement(null);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
    
            // Check the results
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
    
                // Check that the dependent field is empty
                DependentHolder loaded = (DependentHolder) pm.getObjectById(holderId, true);
                assertNull(loaded.getElement());
               
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
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
            clearDependentData(pmf);
        }
    }

    /**
     * Test of the nulling of a 1-1 relation whilst detached and then re-attaching the relation.
     * Should result in the deletion of the dependent element.
     */
    public void testRemoveDependentFieldOnDetached()
    {
        try
        {
            // Create the objects
            DependentHolder holder = new DependentHolder(0, "field0");
            DependentElement dependent = new DependentElement(0, "el" + 0);
            holder.setElement(dependent);

            Object holderId = null;
            Object elementId = null;
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist the objects
                tx.begin();
                pm.makePersistent(holder);
                tx.commit();
                holderId = pm.getObjectId(holder);
                elementId = pm.getObjectId(dependent);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
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
            tx= pm.currentTransaction();
            try
            {
                // Detach the field object
                tx.begin();
                DependentHolder loaded = (DependentHolder) pm.getObjectById(holderId, true);
                DependentHolder detached = (DependentHolder)pm.detachCopy(loaded);
                tx.commit();

                // Nullify the relation
                detached.setElement(null);

                // Attach the changes. This operation should remove the dependent element
                tx.begin();
                pm.makePersistent(detached);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check the results
            pm = pmf.getPersistenceManager();
            tx= pm.currentTransaction();
            try
            {
                tx.begin();
                DependentHolder loaded = (DependentHolder) pm.getObjectById(holderId,true);
                assertNull(loaded.getElement()); // Check that the dependent field is null

                // Check that the dependent element has been removed.
                try
                {
                    DependentElement retrievedElement = (DependentElement)pm.getObjectById(elementId);
                    if (retrievedElement != null)
                    {
                        fail("Dependent field hasnt been deleted yet should have been since it is dependent and was removed from the 1-1 Relation");
                    }
                }
                catch (JDOObjectNotFoundException e)
                {
                    // Expected result
                }
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting dependent field data : " + e.getMessage());
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
            clearDependentData(pmf);
        }
    }
    
    /**
     * Convenience method to clean out dependent data
     * @param pmf The pmf to use
     */
    private void clearDependentData(PersistenceManagerFactory pmf)
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            // disassociate all dependent fields
            tx.begin();
            Extent ext = pm.getExtent(DependentHolder.class, false);
            Iterator it = ext.iterator();
            while (it.hasNext())
            {
                DependentHolder holder = (DependentHolder) it.next();
                DependentElement element = holder.getElement();
                if (element != null)
                {
                    element.setOwner(null);
                    element.setKey(null);
                    pm.flush();
                }
                holder.setElement(null);
                holder.setListDependent1(null);
                holder.setListNonDependent1(null);
                holder.setSetDependent1(null);
                holder.setSetNonDependent1(null);
                holder.setMapDependent1(null);
                holder.setMapNonDependent1(null);
                holder.setMapDependentKeys1(null);
                holder.setMapDependentValues1(null);
                holder.setMapNonDependentKeys1(null);
                holder.setMapNonDependentValues1(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentHolder.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentHolder holder = (DependentHolder) it.next();
                DependentElement element = holder.getElement();
                if (element != null)
                {
                    element.setOwner(null);
                    element.setKey(null);
                    pm.flush();
                }
                holder.setElement(null);
                holder.setListDependent2(null);
                holder.setListNonDependent2(null);
                holder.setSetDependent2(null);
                holder.setSetNonDependent2(null);
                holder.setMapDependent2(null);
                holder.setMapNonDependent2(null);
                holder.setMapDependentKeys2(null);
                holder.setMapDependentValues2(null);
                holder.setMapNonDependentKeys2(null);
                holder.setMapNonDependentValues2(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentElement1.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentElement1 field = (DependentElement1) it.next();
                field.setKey(null);
                field.setOwner(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentElement2.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentElement2 field = (DependentElement2) it.next();
                field.setKey(null);
                field.setOwner(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentElement3.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentElement3 field = (DependentElement3) it.next();
                field.setKey(null);
                field.setOwner(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentElement4.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentElement4 field = (DependentElement4) it.next();
                field.setKey(null);
                field.setOwner(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentElement5.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentElement5 field = (DependentElement5) it.next();
                field.setKey(null);
                field.setOwner(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentElement6.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentElement6 field = (DependentElement6) it.next();
                field.setKey(null);
                field.setOwner(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentElement7.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentElement7 field = (DependentElement7) it.next();
                field.setKey(null);
                field.setOwner(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentElement8.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentElement8 field = (DependentElement8) it.next();
                field.setKey(null);
                field.setOwner(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentElement9.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentElement9 field = (DependentElement9) it.next();
                field.setKey(null);
                field.setOwner(null);
                pm.flush();
            }

            ext = pm.getExtent(DependentElement10.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                DependentElement10 field = (DependentElement10) it.next();
                field.setKey(null);
                field.setOwner(null);
                pm.flush();
            }

            tx.commit();

            TestHelper.clean(pmf, DependentHolder.class);
            TestHelper.clean(pmf, DependentElement1.class);
            TestHelper.clean(pmf, DependentElement2.class);
            TestHelper.clean(pmf, DependentElement3.class);
            TestHelper.clean(pmf, DependentElement4.class);
            TestHelper.clean(pmf, DependentElement5.class);
            TestHelper.clean(pmf, DependentElement6.class);
            TestHelper.clean(pmf, DependentElement7.class);
            TestHelper.clean(pmf, DependentElement8.class);
            TestHelper.clean(pmf, DependentElement9.class);
            TestHelper.clean(pmf, DependentElement10.class);
            TestHelper.clean(pmf, DependentHolder.class);
            TestHelper.clean(pmf, DepInterfaceImpl1.class);
            TestHelper.clean(pmf, DepInterfaceImpl2.class);
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