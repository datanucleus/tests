package org.datanucleus.enhancer.jdo;

import org.datanucleus.metadata.InvalidMetaDataException;

/**
 */
public class TestA18_4_6 extends JDOTestBase 
{
    public void testTransactionalAndDefaultFetchGroup()
    {
        try
        {
            getEnhancedClassesFromFile(
                "org/datanucleus/enhancer/samples/A18_4_6_transactional_and_default_fetch_group.jdo");
            fail("must throw InvalidMetaDataException with key 044109");
        }
        catch (InvalidMetaDataException e)
        {
            assertEquals("044109", e.getMessageKey());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testTransactionalAndPrimaryKey()
    {
        try
        {
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_4_6_transactional_and_primary_key.jdo");
            fail("must throw InvalidMetaDataException with key 044109");
        }
        catch (InvalidMetaDataException e)
        {
            assertEquals("044109", e.getMessageKey());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testNoneAndDefaultFetchGroup()
    {
        try
        {
            getEnhancedClassesFromFile(
                "org/datanucleus/enhancer/samples/A18_4_6_transactional_and_default_fetch_group.jdo");
            fail("must throw InvalidMetaDataException with key 044109");
        }
        catch (InvalidMetaDataException e)
        {
            assertEquals("044109", e.getMessageKey());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testNoneAndPrimaryKey()
    {
        try
        {
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_4_6_transactional_and_primary_key.jdo");
            fail("must throw InvalidMetaDataException with key 044109");
        }
        catch (InvalidMetaDataException e)
        {
            assertEquals("044109", e.getMessageKey());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}