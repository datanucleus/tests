package org.datanucleus.enhancer.jdo;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.InvalidPrimaryKeyException;

/**
 */
public class TestA05_4_4 extends JDOTestBase
{
	public void testAllOfNonStaticFieldMustBeSerializableOkApp() 
    {
		try 
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_3_app_ok.jdo");
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

	public void testAllOfNonStaticFieldMustBeSerializableOkDataStore() 
    {
		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_3_ds_ok.jdo");
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

	public void testAllOfNonStaticFieldMustBeSerializableApp()
    {
		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_4_app_no_have_unserialized_non_static_field.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			fail("must throw InvalidPrimaryKeyException with key 019009");
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
            assertEquals("019009", ipke.getMessageKey());
        }
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testAllOfNonStaticFieldMustBeSerializableDataStore()
    {
		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_4_ds_no_have_unserialized_non_static_field.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			fail("must throw InvalidPrimaryKeyException with key 019009");
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
            assertEquals("019009", ipke.getMessageKey());
        }
        catch (Throwable e) 
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}