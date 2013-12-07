package org.datanucleus.enhancer.jdo;

import javax.jdo.spi.RegisterClassEvent;

/**
 */
public class TestA18_4_1 extends JDOTestBase
{
	public void testJdoManagedFieldNumProtected()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullProtectedStaticClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedStaticClass");
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

    public void testJdoManagedFieldNumPublic()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPublicStaticClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicStaticClass");
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
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateStaticClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateStaticClass");
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
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullDefaultStaticClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullDefaultStaticClass");
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