package org.datanucleus.enhancer.jdo;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.InvalidPrimaryKeyException;

/**
 */
public class TestA05_4_3 extends JDOTestBase
{
	public void testObjectidClassMustImplementSerializableOkApp()
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

	public void testObjectidClassMustHavePublicDefaultConstructorOkApp() 
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

	public void testObjectidClassNoPublicDefaultConstructorApp()
    {
		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_3_app_no_public_constructor.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			fail();
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
            assertEquals("019004", ipke.getMessageKey());
        }
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testObjectidClassMustImplementSerializableFailApp() 
    {
		try
        {
			getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_3_app_no_implement_serializable.jdo");
			fail("must throw InvalidPrimaryKeyException with key 019002");
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
            assertEquals("019002", ipke.getMessageKey());
        }
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testObjectidClassMustImplementSerializableOkDataStore()
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

	public void testObjectidClassMustHavePublicDefaultConstructorOkDataStore()
    {
		try 
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_3_ds_ok.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testObjectidClassNoPublicDefaultConstructorDataStore() 
    {
		try 
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_3_ds_no_public_constructor.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			fail();
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
            assertEquals("019004", ipke.getMessageKey());
        }
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testObjectidClassMustImplementSerializableFailDataStore()
    {
		try
        {
			getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_3_ds_no_implement_serializable.jdo");
			fail("must throw InvalidPrimaryKeyException with key 019002");
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
            assertEquals("019002", ipke.getMessageKey());
        }
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testObjectidClassNoOverrideToStringDataStore() 
    {
		try 
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_3_ds_no_override_tostring.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			fail("must throw InvalidPrimaryKeyException with key 019006");
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
            assertEquals("019006", ipke.getMessageKey());
        }
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testObjectidClassNoOverrideToStringDataApp() 
    {
		try 
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_3_app_no_override_tostring.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			fail("must throw InvalidPrimaryKeyException with key 019006");
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
            assertEquals("019006", ipke.getMessageKey());
        }
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}