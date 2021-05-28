package org.datanucleus.enhancer.jdo;

/**
 */
public class TestA18_4_3 extends JDOTestBase
{
	public void testJdoManagedFieldNumProtected()
    {
		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullProtectedFinalClass.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedFinalClass");
			targetClass.getDeclaredConstructor().newInstance();
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testJdoManagedFieldNumPublic()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPublicFinalClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicFinalClass");
            targetClass.getDeclaredConstructor().newInstance();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testJdoManagedFieldNumPrivate()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateFinalClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateFinalClass");
            targetClass.getDeclaredConstructor().newInstance();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testJdoManagedFieldNumDefault()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullDefaultFinalClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullDefaultFinalClass");
            targetClass.getDeclaredConstructor().newInstance();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}