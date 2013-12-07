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
**********************************************************************/
package org.datanucleus.tests.application;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.identity.application.ComposedIntIDBase;
import org.jpox.samples.identity.application.ComposedMixedIDBase;
import org.jpox.samples.identity.application.ComposedStringIDBase;

/**
 * Test convenience functions in IdentityUtils.
 */
public class IdentityUtilsTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public IdentityUtilsTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[] 
                {
                    ComposedIntIDBase.class,
                    ComposedStringIDBase.class,
                    ComposedMixedIDBase.class
                });
            initialised = true;
        }
    }

    public void testGetValueForMemberInId()
    throws Exception
    {
        MetaDataManager mmgr = storeMgr.getNucleusContext().getMetaDataManager();
        ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);

        // a). composed of two ints
        {
            org.jpox.samples.identity.application.ComposedIntIDBase.Key key = 
                new org.jpox.samples.identity.application.ComposedIntIDBase.Key();
            key.code = 123;
            key.composed = 4567;

            AbstractClassMetaData cmd = mmgr.getMetaDataForClass(ComposedIntIDBase.class, clr);
            int[] pkNums = cmd.getPKMemberPositions();

            // Check code
            AbstractMemberMetaData pkMmd0 = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[0]);
            assertEquals("code", pkMmd0.getName());
            Object pkVal0 = IdentityUtils.getValueForMemberInId(key, pkMmd0);
            assertEquals(123, pkVal0);

            // Check composed
            AbstractMemberMetaData pkMmd1 = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[1]);
            assertEquals("composed", pkMmd1.getName());
            Object pkVal1 = IdentityUtils.getValueForMemberInId(key, pkMmd1);
            assertEquals(4567, pkVal1);
        }

        // b). composed of two Strings
        {
            org.jpox.samples.identity.application.ComposedStringIDBase.Key key =
                new org.jpox.samples.identity.application.ComposedStringIDBase.Key();
            key.code = "123";
            key.composed = "4567";

            AbstractClassMetaData cmd = mmgr.getMetaDataForClass(ComposedStringIDBase.class, clr);
            int[] pkNums = cmd.getPKMemberPositions();

            // Check code
            AbstractMemberMetaData pkMmd0 = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[0]);
            assertEquals("code", pkMmd0.getName());
            Object pkVal0 = IdentityUtils.getValueForMemberInId(key, pkMmd0);
            assertEquals("123", pkVal0);

            // Check composed
            AbstractMemberMetaData pkMmd1 = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[1]);
            assertEquals("composed", pkMmd1.getName());
            Object pkVal1 = IdentityUtils.getValueForMemberInId(key, pkMmd1);
            assertEquals("4567", pkVal1);
        }

        // b). composed of int+String+Double
        {
            org.jpox.samples.identity.application.ComposedMixedIDBase.Key key =
                new org.jpox.samples.identity.application.ComposedMixedIDBase.Key();
            key.code = 123;
            key.composed = "4567";
            key.doubleObjField = 5.0;

            AbstractClassMetaData cmd = mmgr.getMetaDataForClass(ComposedMixedIDBase.class, clr);
            int[] pkNums = cmd.getPKMemberPositions();

            // Check code
            AbstractMemberMetaData pkMmd0 = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[0]);
            assertEquals("code", pkMmd0.getName());
            Object pkVal0 = IdentityUtils.getValueForMemberInId(key, pkMmd0);
            assertEquals(123, pkVal0);

            // Check composed
            AbstractMemberMetaData pkMmd1 = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[1]);
            assertEquals("composed", pkMmd1.getName());
            Object pkVal1 = IdentityUtils.getValueForMemberInId(key, pkMmd1);
            assertEquals("4567", pkVal1);

            // Check composed
            AbstractMemberMetaData pkMmd2 = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[2]);
            assertEquals("doubleObjField", pkMmd2.getName());
            Object pkVal2 = IdentityUtils.getValueForMemberInId(key, pkMmd2);
            assertEquals(5.0, pkVal2);
        }
    }
}