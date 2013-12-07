package org.datanucleus.enhancer.jdo;

import java.lang.reflect.Method;

import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.RegisterClassEvent;

/**
 */
public class TestA18_4_4 extends JDOTestBase 
{
    public void testJdoManagedFieldNumDefault()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullDefaultClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullDefaultClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            assertEquals("jdo field num is 55", 55, ev.getFieldFlags().length);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testJdoManagedFieldNameDefault() 
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullDefaultClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullDefaultClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            String fieldNames[] = ev.getFieldNames();
            for (int i = 0; i < fieldNames.length; i++) {
                String expectedValue = (i < 10) ? "field0" + i : "field" + i;
                assertEquals(
                    "check field name \"" + expectedValue + "\"",
                    expectedValue,
                    fieldNames[i]);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testJdoManagedFieldFlagDefault()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullDefaultClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullDefaultClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent) pcClasses.get(targetClass);
            byte fieldFlags[] = ev.getFieldFlags();
            int expectedValue;
            int serializeSupport = PersistenceCapable.SERIALIZABLE;
            for (int i = 0; i < fieldFlags.length; i++)
            {
                if (i < 22) // Was 21 if use strict JDO default persistent flags
                {
                    expectedValue = (PersistenceCapable.CHECK_READ | PersistenceCapable.CHECK_WRITE | serializeSupport);
                }
                else if ((i > 29) && (i < 35))
                {
                    expectedValue = (PersistenceCapable.MEDIATE_READ | PersistenceCapable.MEDIATE_WRITE);
                }
                else
                {
                    expectedValue = (PersistenceCapable.MEDIATE_READ | PersistenceCapable.MEDIATE_WRITE | serializeSupport);
                }
                assertEquals("field " + i + " test", expectedValue, fieldFlags[i]);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testAccessorAndMutatorDefault()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullDefaultClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullDefaultClass");
            targetClass.newInstance();
            boolean setter[] = new boolean[55];
            boolean getter[] = new boolean[55];
            Method methods[] = targetClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++)
            {
                String name = methods[i].getName();
                if (name.startsWith("jdoSet"))
                {
                    String sufix = name.substring(name.length() - 2, name.length());
                    try
                    {
                        setter[Integer.parseInt(sufix)] = true;
                    }
                    catch (NumberFormatException nfe)
                    {
                        //do nothing
                    }
                }
                else if ((name.startsWith("jdoGet")) ||
                         (name.startsWith("jdoIs")))
                {
                    String sufix = name.substring(name.length() - 2, name.length());
                    try
                    {
                        getter[Integer.parseInt(sufix)] = true;
                    }
                    catch (NumberFormatException nfe)
                    {
                        //do nothing
                    }
                }
            }
            for (int i = 0; i < 55; i++)
            {
                assertEquals("must have accessor for field " + i, true, getter[i]);
                assertEquals("must have mutator for field " + i, true, setter[i]);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // protected
    public void testJdoManagedFieldNumProtected()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullProtectedClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            assertEquals("jdo field num is 55", 55, ev.getFieldFlags().length);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testJdoManagedFieldNameProtected()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullProtectedClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            String fieldNames[] = ev.getFieldNames();
            for (int i = 0; i < fieldNames.length; i++)
            {
                String expectedValue = (i < 10) ? "field0" + i : "field" + i;
                assertEquals(
                    "check field name \"" + expectedValue + "\"",
                    expectedValue,
                    fieldNames[i]);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testJdoManagedFieldFlagProtected()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullProtectedClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent) pcClasses.get(targetClass);
            byte fieldFlags[] = ev.getFieldFlags();
            int expectedValue;
            int serializeSupport = PersistenceCapable.SERIALIZABLE;
            for (int i = 0; i < fieldFlags.length; i++)
            {
                if (i < 22) // Was 21 if use strict JDO default persistent flags
                {
                    expectedValue = (PersistenceCapable.CHECK_READ | PersistenceCapable.CHECK_WRITE | serializeSupport);
                }
                else if ((i > 29) && (i < 35))
                {
                    expectedValue = (PersistenceCapable.MEDIATE_READ | PersistenceCapable.MEDIATE_WRITE);
                }
                else
                {
                    expectedValue = (PersistenceCapable.MEDIATE_READ | PersistenceCapable.MEDIATE_WRITE | serializeSupport);
                }
                assertEquals("field " + i + " test", expectedValue, fieldFlags[i]);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testAccessorAndMutatorProtected()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullProtectedClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
            targetClass.newInstance();
            boolean setter[] = new boolean[55];
            boolean getter[] = new boolean[55];
            Method methods[] = targetClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++)
            {
                String name = methods[i].getName();
                if (name.startsWith("jdoSet"))
                {
                    String sufix = name.substring(name.length() - 2, name.length());
                    try
                    {
                        setter[Integer.parseInt(sufix)] = true;
                    }
                    catch (NumberFormatException nfe)
                    {
                        //do nothing
                    }
                }
                else if ((name.startsWith("jdoGet")) ||
                         (name.startsWith("jdoIs")))
                {
                    String sufix = name.substring(name.length() - 2, name.length());
                    try
                    {
                        getter[Integer.parseInt(sufix)] = true;
                    }
                    catch (NumberFormatException nfe)
                    {
                        //do nothing
                    }
                }
            }
            for (int i = 0; i < 55; i++)
            {
                assertEquals("must have accessor for field " + i, true, getter[i]);
                assertEquals("must have mutator for field " + i, true, setter[i]);
            }
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
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            assertEquals("jdo field num is 55", 55, ev.getFieldFlags().length);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testJdoManagedFieldNamePrivate()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            String fieldNames[] = ev.getFieldNames();
            for (int i = 0; i < fieldNames.length; i++)
            {
                String expectedValue = (i < 10) ? "field0" + i : "field" + i;
                assertEquals(
                    "check field name \"" + expectedValue + "\"",
                    expectedValue,
                    fieldNames[i]);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testJdoManagedFieldFlagPrivate()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent) pcClasses.get(targetClass);
            byte fieldFlags[] = ev.getFieldFlags();
            int expectedValue;
            int serializeSupport = PersistenceCapable.SERIALIZABLE;
            for (int i = 0; i < fieldFlags.length; i++)
            {
                if (i < 22) // Was 21 if use strict JDO default persistent flags
                {
                    expectedValue = (PersistenceCapable.CHECK_READ | PersistenceCapable.CHECK_WRITE | serializeSupport);
                }
                else if ((i > 29) && (i < 35))
                {
                    expectedValue = (PersistenceCapable.MEDIATE_READ | PersistenceCapable.MEDIATE_WRITE);
                }
                else
                {
                    expectedValue = (PersistenceCapable.MEDIATE_READ | PersistenceCapable.MEDIATE_WRITE | serializeSupport);
                }
                assertEquals("field " + i + " test", expectedValue, fieldFlags[i]);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testAccessorAndMutatorPrivate() 
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateClass");
            targetClass.newInstance();
            boolean setter[] = new boolean[55];
            boolean getter[] = new boolean[55];
            Method methods[] = targetClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++)
            {
                String name = methods[i].getName();
                if (name.startsWith("jdoSet"))
                {
                    String sufix = name.substring(name.length() - 2, name.length());
                    try
                    {
                        setter[Integer.parseInt(sufix)] = true;
                    }
                    catch (NumberFormatException nfe)
                    {
                        //do nothing
                    }
                }
                else if ((name.startsWith("jdoGet")) ||
                         (name.startsWith("jdoIs")))
                {
                    String sufix = name.substring(name.length() - 2, name.length());
                    try
                    {
                        getter[Integer.parseInt(sufix)] = true;
                    }
                    catch (NumberFormatException nfe)
                    {
                        //do nothing
                    }
                }
            }
            for (int i = 0; i < 55; i++)
            {
                assertEquals("must have accessor for field " + i, true, getter[i]);
                assertEquals("must have mutator for field " + i, true, setter[i]);
            }
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
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPublicClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            assertEquals("jdo field num is 55", 55, ev.getFieldFlags().length);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testJdoManagedFieldNamePublic()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPublicClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            String fieldNames[] = ev.getFieldNames();
            for (int i = 0; i < fieldNames.length; i++)
            {
                String expectedValue = (i < 10) ? "field0" + i : "field" + i;
                assertEquals(
                    "check field name \"" + expectedValue + "\"",
                    expectedValue,
                    fieldNames[i]);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testJdoManagedFieldFlagPublic()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPublicClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent) pcClasses.get(targetClass);
            byte fieldFlags[] = ev.getFieldFlags();
            int expectedValue;
            int serializeSupport = PersistenceCapable.SERIALIZABLE;
            for (int i = 0; i < fieldFlags.length; i++)
            {
                if (i < 22) // Was 21 if use strict JDO default persistent flags
                {
                    expectedValue = (PersistenceCapable.CHECK_READ | PersistenceCapable.CHECK_WRITE | serializeSupport);
                }
                else if ((i > 29) && (i < 35))
                {
                    expectedValue = (PersistenceCapable.MEDIATE_READ | PersistenceCapable.MEDIATE_WRITE);
                }
                else
                {
                    expectedValue = (PersistenceCapable.MEDIATE_READ | PersistenceCapable.MEDIATE_WRITE | serializeSupport);
                }
                assertEquals("field " + i + " test", expectedValue, fieldFlags[i]);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testAccessorAndMutatorPublic() 
    {
        try 
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPublicClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicClass");
            targetClass.newInstance();
            boolean setter[] = new boolean[55];
            boolean getter[] = new boolean[55];
            Method methods[] = targetClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++)
            {
                String name = methods[i].getName();
                if (name.startsWith("jdoSet")) 
                {
                    String sufix = name.substring(name.length() - 2, name.length());
                    try
                    {
                        setter[Integer.parseInt(sufix)] = true;
                    }
                    catch (NumberFormatException nfe)
                    {
                        //do nothing
                    }
                }
                else if ((name.startsWith("jdoGet")) ||
                         (name.startsWith("jdoIs"))) 
                {
                    String sufix = name.substring(name.length() - 2, name.length());
                    try
                    {
                        getter[Integer.parseInt(sufix)] = true;
                    }
                    catch (NumberFormatException nfe)
                    {
                        //do nothing
                    }
                }
            }
            for (int i = 0; i < 55; i++)
            {
                assertEquals("must have accessor for field " + i, true, getter[i]);
                assertEquals("must have mutator for field " + i, true, setter[i]);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}