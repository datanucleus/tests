package org.datanucleus.enhancer.jdo;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.InvalidMetaDataException;

/**
 * Tests for ClassMetaData.
 */
public class TestA18_6 extends JDOTestBase
{
    public void testNameAttributeReqired()
    {
        try
        {
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_6_no_name.jdo");
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
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_6_has_name.jdo");
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