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


Contributors:
    ...
**********************************************************************/
package org.datanucleus.enhancer.jdo;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.InvalidMetaDataException;

/**
 * Test of FieldMetaData oddities
 */
public class TestA18_13 extends JDOTestBase
{
    /**
     * Field MetaData defines some mutually exclusive combinations.
     */
    public void testMutuallyExclusiveAttributes()
    {
        try
        {
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/MutuallyExclusiveJDOTags1.jdo");
            fail("Should have thrown an InvalidMetaDataException with key 044109");
        }
        catch (InvalidMetaDataException e)
        {
            assertEquals("044109", e.getMessageKey());
        }

        try
        {
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/MutuallyExclusiveJDOTags2.jdo");
            fail("Should have thrown an InvalidMetaDataException with key 044109");
        }
        catch (InvalidMetaDataException e)
        {
            assertEquals("044109", e.getMessageKey());
        }
    }

    public void testNameAttributeReqired()
    {
        try
        {
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_13_no_name.jdo");
        }
        catch (InvalidMetaDataException e)
        {
            assertEquals("044061", e.getMessageKey());
        }
        catch (RuntimeException e)
        {
            assertTrue(NucleusUserException.class.isAssignableFrom(e.getClass()));
        }
        catch (Throwable e) 
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }

        try
        {
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_2_has_name.jdo");
        }
        catch (InvalidMetaDataException e)
        {
            fail(e.getMessageKey());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}