package org.datanucleus.enhancer.jdo;

/**
 * 
 */
public class TestA18_4_5 extends JDOTestBase
{
    public void testDefaultFetchGroupDefault()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPublicClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicClass");
            targetClass.newInstance();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testDefaultFetchGroupTrue()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_4_5_true.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicClass");
            targetClass.newInstance();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testDefaultFetchGroupFalse()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_4_5_false.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicClass");
            targetClass.newInstance();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testDefaultFetchGroupPk()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_4_5_pk.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicClass");
            targetClass.newInstance();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}