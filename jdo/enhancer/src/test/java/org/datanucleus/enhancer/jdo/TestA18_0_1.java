package org.datanucleus.enhancer.jdo;

/**
 */
public class TestA18_0_1 extends JDOTestBase
{
	public void testJdoManagedFieldNum() 
    {
		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullDefaultClass.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullDefaultClass");
			targetClass.getDeclaredConstructor().newInstance();
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}

		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateClass.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateClass");
			targetClass.getDeclaredConstructor().newInstance();
		}
        catch (Throwable e) 
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}

		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_0_1.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.getDeclaredConstructor().newInstance();
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}

		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateClass.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateClass");
			targetClass.getDeclaredConstructor().newInstance();
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testJdoManagedFieldName()
    {
		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_0_1.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.getDeclaredConstructor().newInstance();
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testJdoManagedFieldFlag()
    {
		try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPublicClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicClass");
            targetClass.getDeclaredConstructor().newInstance();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}