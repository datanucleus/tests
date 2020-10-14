package org.datanucleus.enhancer.jdo;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.InvalidMetaDataException;

/**
 * Test for package MetaData.
 */
public class TestA18_2 extends JDOTestBase
{
    public void testNameAttributeReqired()
    {
        try
        {
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_2_no_name.jdo");
        }
        catch (InvalidMetaDataException e)
        {
            assertEquals("MetaData.Class.NameNotSpecified.Error", e.getMessageKey());
        }
        catch (NucleusException e)
        {
            //some parsers will raise exception before we invalidate the metadata raising InvalidMetaDataException
            assertTrue("Type of exception is wrong " + e.getClass().getName(),
                NucleusException.class.isAssignableFrom(e.getClass()));
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