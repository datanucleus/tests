package org.datanucleus.enhancer.jdo;

import java.lang.reflect.Method;

/**
 */
public class TestA20_6_2 extends JDOTestBase
{
    public void testHasWriteObjectMethod()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/SerializableClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.SerializableClass");
            Method methods[] = targetClass.getDeclaredMethods();
            if (methods == null)
            {
                fail("SerializableClass has no method.");
            }
            else
            {
                boolean hit = false;
                for (int i = 0; i < methods.length; i++)
                {
                    if (methods[i].getName().equals("writeObject"))
                    {
                        hit = true;
                    }
                }
                if (!hit)
                {
                    fail("enhancer must genarate writeObject for Serializable persistent capable class");
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testHasWriteObjectMethodImpled()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/ImpledSerializableClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.ImpledSerializableClass");
            Method methods[] = targetClass.getDeclaredMethods();
            if (methods == null)
            {
                fail("ImpledSerializableClass has no method.");
            }
            else
            {
                boolean hit = false;
                for (int i = 0; i < methods.length; i++)
                {
                    if (methods[i].getName().equals("writeObject"))
                    {
                        hit = true;
                    }
                }
                if (!hit)
                {
                    fail("enhancer must genarate writeObject for Serializable persistent capable class");
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}