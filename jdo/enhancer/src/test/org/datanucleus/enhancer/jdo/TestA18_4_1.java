package org.datanucleus.enhancer.jdo;

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
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}