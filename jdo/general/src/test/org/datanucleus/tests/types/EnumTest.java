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
package org.datanucleus.tests.types;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.enums.AlternativeColour;
import org.jpox.samples.types.enums.AlternativePalette;
import org.jpox.samples.types.enums.Colour;
import org.jpox.samples.types.enums.Palette;

/**
 * Tests for mapping enums with JDO.
 */
public class EnumTest extends JDOPersistenceTestCase
{
    /**
     * @param name
     */
    public EnumTest(String name)
    {
        super(name);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        clean(Palette.class);
    }

    /**
     * Test persistence of an enum as a String.
     */
    public void testStringEnum()
    {
        Palette p;
        Object id = null;
        // ---------------------
        // RED
        // ---------------------
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            p = new Palette();
            p.setAmount(100);
            p.setColour(Colour.RED);
            p.setColourOrdinal(Colour.GREEN);
            p.setColourSerialized(Colour.BLUE);
            pm.makePersistent(p);
            id = JDOHelper.getObjectId(p);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            p = (Palette) pm.getObjectById(id, true);
            assertEquals(100, p.getAmount());
            assertEquals(Colour.RED, p.getColour());
            assertEquals(Colour.GREEN, p.getColourOrdinal());
            assertEquals(Colour.BLUE, p.getColourSerialized());
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
        // ---------------------
        // null
        // ---------------------
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            p = new Palette();
            p.setAmount(101);
            p.setColour(null);
            p.setColourOrdinal(null);
            p.setColourSerialized(null);
            pm.makePersistent(p);
            id = JDOHelper.getObjectId(p);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            p = (Palette) pm.getObjectById(id, true);
            assertEquals(101, p.getAmount());
            assertNull(p.getColour());
            assertNull(p.getColourOrdinal());
            assertNull(p.getColourSerialized());
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
        // ---------------------
        // GREEN
        // ---------------------
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            p = new Palette();
            p.setAmount(102);
            p.setColour(Colour.GREEN);
            p.setColourOrdinal(Colour.GREEN);
            p.setColourSerialized(Colour.GREEN);
            pm.makePersistent(p);
            id = JDOHelper.getObjectId(p);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            p = (Palette) pm.getObjectById(id, true);
            assertEquals(102, p.getAmount());
            assertEquals(Colour.GREEN, p.getColour());
            assertEquals(Colour.GREEN, p.getColourOrdinal());
            assertEquals(Colour.GREEN, p.getColourSerialized());
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

    /**
     * Test use of JDOQL matches with enums stored as Strings.
     */
    public void testQueryEnumToStringMatches()
    {
        Palette p[];
        Object id[];
        // ---------------------
        // RED
        // ---------------------
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        p = new Palette[5];
        id = new Object[5];
        try
        {
            tx.begin();
            p[0] = new Palette();
            p[0].setAmount(100);
            p[0].setColour(Colour.RED);
            p[0].setColourOrdinal(Colour.RED);
            p[1] = new Palette();
            p[1].setAmount(101);
            p[1].setColour(null);
            p[2] = new Palette();
            p[2].setAmount(102);
            p[2].setColour(Colour.GREEN);
            p[2].setColourOrdinal(Colour.GREEN);
            p[3] = new Palette();
            p[3].setAmount(103);
            p[3].setColour(Colour.BLUE);
            p[3].setColourOrdinal(Colour.BLUE);
            p[4] = new Palette();
            p[4].setAmount(104);
            p[4].setColour(Colour.RED);
            p[4].setColourOrdinal(Colour.RED);
            pm.makePersistentAll(p);
            id[0] = JDOHelper.getObjectId(p[0]);
            id[1] = JDOHelper.getObjectId(p[1]);
            id[2] = JDOHelper.getObjectId(p[2]);
            id[3] = JDOHelper.getObjectId(p[3]);
            id[4] = JDOHelper.getObjectId(p[4]);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Collection c = (Collection) pm.newQuery(Palette.class, "colour.toString().matches('BLUE')").execute();
            assertEquals(1, c.size());
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

    /**
     * Test use of JDOQL and enums for all types.
     */
    public void testQueryStringEnumAll()
    {
        Palette p[];
        Object id[];
        // ---------------------
        // RED
        // ---------------------
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        p = new Palette[5];
        id = new Object[5];
        try
        {
            tx.begin();
            p[0] = new Palette();
            p[0].setAmount(100);
            p[0].setColour(Colour.RED);
            p[0].setColourOrdinal(Colour.RED);
            p[1] = new Palette();
            p[1].setAmount(101);
            p[1].setColour(null);
            p[2] = new Palette();
            p[2].setAmount(102);
            p[2].setColour(Colour.GREEN);
            p[2].setColourOrdinal(Colour.GREEN);
            p[3] = new Palette();
            p[3].setAmount(103);
            p[3].setColour(Colour.BLUE);
            p[3].setColourOrdinal(Colour.BLUE);
            p[4] = new Palette();
            p[4].setAmount(104);
            p[4].setColour(Colour.RED);
            p[4].setColourOrdinal(Colour.RED);
            pm.makePersistentAll(p);
            id[0] = JDOHelper.getObjectId(p[0]);
            id[1] = JDOHelper.getObjectId(p[1]);
            id[2] = JDOHelper.getObjectId(p[2]);
            id[3] = JDOHelper.getObjectId(p[3]);
            id[4] = JDOHelper.getObjectId(p[4]);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Collection c = (Collection) pm.newQuery(Palette.class, "colourOrdinal == 2 && colour == 'BLUE'").execute();
            assertEquals(1, c.size());
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

    /**
     * Test use of JDOQL and enums stored as ints.
     */
    public void testQueryStringEnumOrdinal()
    {
        Palette p[];
        Object id[];
        // ---------------------
        // RED
        // ---------------------
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        p = new Palette[5];
        id = new Object[5];
        try
        {
            tx.begin();
            p[0] = new Palette();
            p[0].setAmount(100);
            p[0].setColour(Colour.RED);
            p[0].setColourOrdinal(Colour.RED);
            p[1] = new Palette();
            p[1].setAmount(101);
            p[1].setColour(null);
            p[2] = new Palette();
            p[2].setAmount(102);
            p[2].setColour(Colour.GREEN);
            p[2].setColourOrdinal(Colour.GREEN);
            p[3] = new Palette();
            p[3].setAmount(103);
            p[3].setColour(Colour.BLUE);
            p[3].setColourOrdinal(Colour.BLUE);
            p[4] = new Palette();
            p[4].setAmount(104);
            p[4].setColour(Colour.RED);
            p[4].setColourOrdinal(Colour.RED);
            pm.makePersistentAll(p);
            id[0] = JDOHelper.getObjectId(p[0]);
            id[1] = JDOHelper.getObjectId(p[1]);
            id[2] = JDOHelper.getObjectId(p[2]);
            id[3] = JDOHelper.getObjectId(p[3]);
            id[4] = JDOHelper.getObjectId(p[4]);
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

        // Query using the ordinal value
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Collection c = (Collection) pm.newQuery(Palette.class, "colourOrdinal == 0").execute();
            assertEquals(2, c.size());
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown execting Enum ordinal query", e);
            fail("Exception thrown executing Enum ordinal query : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Query using the Enum
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Collection c = (Collection) pm.newQuery(Palette.class, "colourOrdinal == :param").execute(Colour.RED);
            assertEquals(2, c.size());
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown execting Enum query", e);
            fail("Exception thrown executing Enum query : " + e.getMessage());
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

    /**
     * Test use of JDOQL with enums stored as Strings.
     */
    public void testQueryStringEnum()
    {
        Palette p[];
        Object id[];
        // ---------------------
        // RED
        // ---------------------
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        p = new Palette[5];
        id = new Object[5];
        try
        {
            tx.begin();
            p[0] = new Palette();
            p[0].setAmount(100);
            p[0].setColour(Colour.RED);
            p[0].setColourOrdinal(Colour.RED);
            p[1] = new Palette();
            p[1].setAmount(101);
            p[1].setColour(null);
            p[2] = new Palette();
            p[2].setAmount(102);
            p[2].setColour(Colour.GREEN);
            p[2].setColourOrdinal(Colour.GREEN);
            p[3] = new Palette();
            p[3].setAmount(103);
            p[3].setColour(Colour.BLUE);
            p[3].setColourOrdinal(Colour.BLUE);
            p[4] = new Palette();
            p[4].setAmount(104);
            p[4].setColour(Colour.RED);
            p[4].setColourOrdinal(Colour.RED);
            pm.makePersistentAll(p);
            id[0] = JDOHelper.getObjectId(p[0]);
            id[1] = JDOHelper.getObjectId(p[1]);
            id[2] = JDOHelper.getObjectId(p[2]);
            id[3] = JDOHelper.getObjectId(p[3]);
            id[4] = JDOHelper.getObjectId(p[4]);
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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Collection c = (Collection) pm.newQuery(Palette.class, "colour == 'RED'").execute();
            assertEquals(2, c.size());
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

    /**
     * Test use of JDOQL for Collection<Enum>.contains(enum).
     */
    public void testQueryEnumCollectionContainsEnum()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Palette[] p = new Palette[2];
        Object[] id = new Object[2];
        try
        {
            tx.begin();

            p[0] = new Palette();
            p[0].addColour(Colour.RED);
            p[0].addColour(Colour.BLUE);
            p[0].setAmount(100);
            p[0].setColour(Colour.RED);

            p[1] = new Palette();
            p[1].addColour(Colour.GREEN);
            p[1].addColour(Colour.BLUE);
            p[1].setAmount(100);
            p[1].setColour(Colour.BLUE);

            pm.makePersistentAll(p);
            id[0] = JDOHelper.getObjectId(p[0]);
            id[1] = JDOHelper.getObjectId(p[1]);

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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Collection enumColl = new HashSet();
            enumColl.add(Colour.RED);
            enumColl.add(Colour.GREEN);
            Collection c = (Collection) pm.newQuery(Palette.class, ":param.contains(colour)").execute(enumColl);
            assertEquals(1, c.size());
            Palette pr = (Palette) c.iterator().next();
            assertEquals(100, pr.getAmount());
            HashSet<Colour> colours = pr.getColours();
            assertEquals(2, colours.size());
            assertTrue(colours.contains(Colour.BLUE));
            assertTrue(colours.contains(Colour.RED));

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

    /**
     * Test persistence of an enum that uses its own internal value.
     */
    public void testEnumWithOwnValue()
    {
        AlternativePalette p;
        Object id = null;

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            p = new AlternativePalette();
            p.setAmount(100);
            p.setColour(AlternativeColour.GREEN);
            pm.makePersistent(p);
            id = JDOHelper.getObjectId(p);
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
        pmf.getDataStoreCache().evictAll();

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            p = (AlternativePalette) pm.getObjectById(id, true);
            assertEquals(100, p.getAmount());
            assertEquals(AlternativeColour.GREEN, p.getColour());
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