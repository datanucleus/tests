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
package org.datanucleus.tests;

import javax.jdo.PersistenceManager;
import javax.jdo.spi.PersistenceCapable;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.persistentinterfaces.Country;
import org.jpox.samples.persistentinterfaces.ICity;
import org.jpox.samples.persistentinterfaces.ILocation;

/**
 * Series of tests for persistent interfaces.
 * Note that these are separate from the tests in PersistentInterfaces1Test due to adding other implementations
 * of the persistent interfaces and so would affect the other tests.
 */
public class PersistentInterfaces3Test extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public PersistentInterfaces3Test(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]{});
            initialised = true;
        }
    }

    /**
     * test of newInstance()
     * Test basic creation of concrete classes
     */
    public void testNewInstancePCClass() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Country france = (Country) pm.newInstance(Country.class);
        france.setName("France");
        assertEquals("France",france.getName());
    }

    /**
     * test of newInstance()
     * Test basic creation of interfaces
     */
    public void testNewInstance() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        ICity paris = (ICity) pm.newInstance(ICity.class);
        paris.setPosition(1001);
        paris.setName("Paris");
        assertEquals("Paris", paris.getName());
        paris.setCountry(new Country("France"));
    }

    /**
     * test of newInstance()
     * Test basic creation of interfaces with persistent super interfaces
     */
    public void testNewInstance3() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        ILocation lochNess = (ILocation) pm.newInstance(ILocation.class);
        lochNess.setName("Loch Ness");
        assertEquals("Loch Ness", lochNess.getName());
        lochNess.setPosition(100);
        assertEquals(100, lochNess.getPosition());
        assertTrue(PersistenceCapable.class.isAssignableFrom(lochNess.getClass()));
    }
}