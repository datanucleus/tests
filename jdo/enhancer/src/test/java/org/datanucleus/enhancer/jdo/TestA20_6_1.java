package org.datanucleus.enhancer.jdo;

import java.io.ObjectStreamClass;
import java.lang.reflect.Field;

import org.datanucleus.enhancer.samples.ImpledSerializableClass;
import org.datanucleus.enhancer.samples.SerializableClass;

/**
 */
public class TestA20_6_1 extends JDOTestBase
{
    public void testHasSerialVersionUID()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/SerializableClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.SerializableClass");
            Field fields[] = targetClass.getDeclaredFields();
            if (fields == null)
            {
                fail("SerializableClass has no fields.");
            }
            else
            {
                boolean hit = false;
                for (int i = 0; i < fields.length; i++)
                {
                    if (fields[i].getName().equals("serialVersionUID"))
                    {
                        hit = true;
                    }
                }
                if (!hit)
                {
                    fail("enhancer must genarate serialVersionUID field for Serializable persistent capable class");
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testHasSerialVersionUIDValue()
    {
        try
        {
            Class origClass = SerializableClass.class;
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/SerializableClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.SerializableClass");
            long origId = ObjectStreamClass.lookup(origClass).getSerialVersionUID();
            long newId = ObjectStreamClass.lookup(targetClass).getSerialVersionUID();
            assertEquals(origId, newId);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testHasSerialVersionUIDImpled()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/ImpledSerializableClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.ImpledSerializableClass");
            Field fields[] = targetClass.getDeclaredFields();
            if (fields == null)
            {
                fail("ImpledSerializableClass has no fields.");
            }
            else
            {
                boolean hit = false;
                for (int i = 0; i < fields.length; i++)
                {
                    if (fields[i].getName().equals("serialVersionUID"))
                    {
                        hit = true;
                    }
                }
                if (!hit)
                {
                    fail("enhancer must genarate serialVersionUID field for Serializable persistent capable class");
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testHasSerialVersionUIDValueImpled()
    {
        try
        {
            Class origClass = ImpledSerializableClass.class;
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/ImpledSerializableClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.ImpledSerializableClass");
            long origId = ObjectStreamClass.lookup(origClass).getSerialVersionUID();
            long newId = ObjectStreamClass.lookup(targetClass).getSerialVersionUID();
            assertEquals(origId, newId);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

}
