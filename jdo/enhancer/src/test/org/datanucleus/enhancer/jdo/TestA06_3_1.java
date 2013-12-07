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

import javax.jdo.spi.PersistenceCapable;

import org.datanucleus.metadata.InvalidMetaDataException;

/**
 * Test of enhancement of inner classes.
 */
public class TestA06_3_1 extends JDOTestBase 
{
	/**
	 * Can't enhance an inner class that isn't static because the state of its fields
	 * depends on the state of the enclosing class
	 */
    public void testInnerClassMustBeStatic()
    {
		try
        {
			getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/ClassInnerClassButNotStatic.jdo");
			fail("must throw InvalidMetaDataException with key 044063");
		}
        catch (InvalidMetaDataException e) 
        {
			assertEquals("044063", e.getMessageKey());
		}
        catch (Throwable e) 
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

    /**
     * Test enhancement of public static inner classes with persistent owning class
     */
    public void testInnerClassSuperClassPersistent()
    {
        Class[] classes = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/ClassInnerClassOwningClassPersistent.jdo");
        assertEquals(2, classes.length);
        assertTrue(PersistenceCapable.class.isAssignableFrom(classes[0]));
        assertTrue(PersistenceCapable.class.isAssignableFrom(classes[1]));
    }

    /**
     * Test enhancement of public static inner classes with non persistent Owning class
     */
    public void testInnerClassSuperClassNotPersistent()
    {
        Class[] classes = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/ClassInnerClassOwningClassNotPersistent.jdo");
        assertEquals(1, classes.length);
        assertTrue(PersistenceCapable.class.isAssignableFrom(classes[0]));
    }

    /**
     * Test enhancement of public static inner classes with non persistent owning class. One of the two
     * is a persistent class 
     */
    public void testDualInnerClassOwningClassNotPersistent()
    {
        Class[] classes = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/ClassDualInnerClassOwningClassNotPersistent.jdo");
        assertEquals(1, classes.length);
        assertTrue(PersistenceCapable.class.isAssignableFrom(classes[0]));
    }
}