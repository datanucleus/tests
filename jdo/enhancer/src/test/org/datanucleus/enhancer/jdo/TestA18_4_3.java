package org.datanucleus.enhancer.jdo;

import javax.jdo.spi.RegisterClassEvent;

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
			targetClass.newInstance();
			RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
			assertEquals("jdo field num is 0", 0, ev.getFieldFlags().length);
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
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent) pcClasses.get(targetClass);
            assertEquals("jdo field num is 0", 0, ev.getFieldFlags().length);
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
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent) pcClasses.get(targetClass);
            assertEquals("jdo field num is 0", 0, ev.getFieldFlags().length);
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
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent) pcClasses.get(targetClass);
            assertEquals("jdo field num is 0", 0, ev.getFieldFlags().length);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}