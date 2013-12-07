/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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

import java.awt.Color;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.color.ColorHolder;

/**
 * Tests for SCO mutable type java.awt.Color
 * 
 * @version $Revision: 1.1 $
 */
public class ColorTest  extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * @param name
     */
    public ColorTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    ColorHolder.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test of the basic persistence of java.awt.Color mutable SCO type.
     *
     */
    public void testBasicPersistence() throws Exception
    {
        try
        {
            ColorHolder myColor = new ColorHolder();
            myColor.setColorA(new Color(100, 90, 80));
            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(myColor);
                id = JDOHelper.getObjectId(myColor);
                ColorHolder myColor2 = (ColorHolder) pm.getObjectById(id, true);
                pm.refresh(myColor2);
                assertEquals(100, myColor2.getColorA().getRed());
                assertEquals(90, myColor2.getColorA().getGreen());
                assertEquals(80, myColor2.getColorA().getBlue());
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
                ColorHolder myColor2 = (ColorHolder) pm.getObjectById(id, true);
                assertEquals(100, myColor2.getColorA().getRed());
                assertEquals(90, myColor2.getColorA().getGreen());
                assertEquals(80, myColor2.getColorA().getBlue());
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
            clean(ColorHolder.class);
        }
    }
}