package org.datanucleus.enhancer.jdo;

/**
 */
public class TestA20_7_1 extends JDOTestBase
{
    @SuppressWarnings("unchecked")
    public void testHasWriteObjectMethod() 
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A20_7_1.jdo");

            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.CloneableClass");
            Object o1 = targetClass.newInstance();
            Object o2 = targetClass.getMethod("clone", new Class[0]).invoke(o1, new Object[0]);
            if (o1 == o2)
            {
                fail();
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}