package org.datanucleus.enhancer.jdo;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.InvalidPrimaryKeyException;

/**
 */
public class TestA05_4_1 extends JDOTestBase 
{
    public void testObjectidClassMustBePublicOkApp()
    {
		try
        {
            Class[] classes = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_1_app_ok.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			assertEquals(true, true);
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testObjectidClassMustBePublicFailApp()
    {
		try
        {
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_1_app_fail.jdo");
			fail("must throw InvalidPrimaryKeyException with key 019001");
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
			assertEquals("019001", ipke.getMessageKey());
		}
        catch (Throwable e) 
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testObjectidClassMustBePublicOkDataStore()
    {
		try
        {
            Class[] classes = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_1_ds_ok.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			assertEquals(true, true);
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testObjectidClassMustBePublicFailDataStore() 
    {
		try
        {
            getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_1_ds_fail.jdo");
			fail("must throw InvalidPrimaryKeyException with key 019001");
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
            assertEquals("019001", ipke.getMessageKey());
        }
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}