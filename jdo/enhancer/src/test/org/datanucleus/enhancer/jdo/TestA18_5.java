/**********************************************************************
Copyright (c) 2003 Andy Jefferson and others.
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

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.InvalidMetaDataException;

/**
 * Test for use of extension tags.
 */
public class TestA18_5 extends JDOTestBase 
{
    /**
     * Check that any extension tags have to have the "vendor" attr.
     */
	public void testExtensionNeedsVendor()
    {
		try
        {
			getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_5_1.jdo");
			fail("must throw InvalidMetaDataException with key 044160");
		}
        catch (InvalidMetaDataException e) 
        {
			assertEquals("044160", e.getMessageKey());
		}
        catch (NucleusException e)
        {
            //some parsers will raise exception before we invalidate the metadata raising InvalidMetaDataException
            //we nest the exception in JDOUserException
            assertTrue("Expected cause JDOUserException, but was "+e.getCause().getClass().getName()+"  : "+e.toString()+"  : "+e.getCause().toString(),
                NucleusUserException.class.isAssignableFrom(e.getCause().getClass()));
        }
        catch (Throwable e) 
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

    /**
     * Check that any extension for other than us can miss the value
     */
	public void testExtensionAlternative()
    {
		try
        {
			getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_5_1b.jdo");
		}
        catch (InvalidMetaDataException e) 
        {
            System.out.println(e);
			fail("must not throw any InvalidMetaDataException when non-JPOX vendor and missing value on extension tag");
		}
        catch (NucleusException e)
        {
            //some parsers will raise exception before we invalidate the metadata raising InvalidMetaDataException
            //we nest the exception in JDOUserException
            assertTrue("Expected cause JDOUserException, but was "+e.getCause().getClass().getName()+"  : "+e.toString()+"  : "+e.getCause().toString(),
                NucleusUserException.class.isAssignableFrom(e.getCause().getClass()));
        }        
        catch (Throwable e) 
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

    /**
     * Check that any extension has to have key AND value.
     */
	public void testExtension()
    {
		try
        {
			getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_5_1c.jdo");
			fail("must throw InvalidMetaDataException with key 044160");
		}
        catch (InvalidMetaDataException e) 
        {
			assertEquals("044160", e.getMessageKey());
		}
        catch (NucleusException e)
        {
            //some parsers will raise exception before JPOX invalidates the metadata raising InvalidMetaDataException
            //we nest the exception in JDOUserException
            assertTrue("Expected cause JDOUserException, but was "+e.getCause().getClass().getName()+"  : "+e.toString()+"  : "+e.getCause().toString(),
                NucleusUserException.class.isAssignableFrom(e.getCause().getClass()));
        }        
        catch (Throwable e) 
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}