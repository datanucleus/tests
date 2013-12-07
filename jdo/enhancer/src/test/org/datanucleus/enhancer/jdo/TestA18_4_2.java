package org.datanucleus.enhancer.jdo;

import java.lang.reflect.Method;

import javax.jdo.spi.RegisterClassEvent;

/**
 * With these tests, only field34 should be given a get/set/is method.
 */
public class TestA18_4_2 extends JDOTestBase
{
    public void testJdoManagedFieldNumProtected()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullProtectedTransientClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedTransientClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);

            // Should be no persistent fields since they are all transient and
            // none are overridden in the MetaData
            assertEquals("jdo field num is 0", 0, ev.getFieldFlags().length);
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
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullProtectedTransientClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedTransientClass");
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
                // Section 18 of the spec says that accessors/mutators should
                // be created for transient fields. It doesnt say whether these
                // should be added when there are no accessors in the class.
                // TODO Verify this 
                assertEquals("must not have accessor for field " + i, false, getter[i]);
                assertEquals("must not have mutator for field " + i, false, setter[i]);
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
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPublicTransientClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicTransientClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);

            // Should be no persistent fields since they are all transient and
            // none are overridden in the MetaData
            assertEquals("jdo field num is 0", 0, ev.getFieldFlags().length);
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
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPublicTransientClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPublicTransientClass");
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
                // Section 18 of the spec says that accessors/mutators should
                // be created for transient fields. It doesnt say whether these
                // should be added when there are no accessors in the class.
                // TODO Verify this 
                assertEquals("must not have accessor for field " + i, false, getter[i]);
                assertEquals("must not have mutator for field " + i, false, setter[i]);
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
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateTransientClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateTransientClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);

            // Should be no persistent fields since they are all transient and
            // none are overridden in the MetaData
            assertEquals("jdo field num is 0", 0, ev.getFieldFlags().length);
        }
        catch (Throwable e)
        {
            System.out.println(e);
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testAccessorAndMutatorPrivate()
    {
        try
        {
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateTransientClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateTransientClass");
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
                // Section 18 of the spec says that accessors/mutators should
                // be created for transient fields. It doesnt say whether these
                // should be added when there are no accessors in the class.
                // TODO Verify this 
                assertEquals("must not have accessor for field " + i, false, getter[i]);
                assertEquals("must not have mutator for field " + i, false, setter[i]);
            }
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
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullDefaultTransientClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullDefaultTransientClass");
            targetClass.newInstance();
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);

            // Should be no persistent fields since they are all transient and
            // none are overridden in the MetaData
            assertEquals("jdo field num is 0", 0, ev.getFieldFlags().length);
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
            Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullDefaultTransientClass.jdo");
            Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullDefaultTransientClass");
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
                else if ((name.startsWith("jdoGet")) || (name.startsWith("jdoIs")))
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
                // Section 18 of the spec says that accessors/mutators should
                // be created for transient fields. It doesnt say whether these
                // should be added when there are no accessors in the class.
                // TODO Verify this 
                assertEquals("must not have accessor for field " + i, false, getter[i]);
                assertEquals("must not have mutator for field " + i, false, setter[i]);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}