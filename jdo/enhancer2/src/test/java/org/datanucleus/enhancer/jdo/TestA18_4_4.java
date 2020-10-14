package org.datanucleus.enhancer.jdo;

import java.lang.reflect.Method;

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
                if (name.startsWith("dnSet"))
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
                else if ((name.startsWith("dnGet")) ||
                         (name.startsWith("dnIs")))
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
                if (name.startsWith("dnSet"))
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
                else if ((name.startsWith("dnGet")) ||
                         (name.startsWith("dnIs")))
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
                if (name.startsWith("dnSet"))
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
                else if ((name.startsWith("dnGet")) ||
                         (name.startsWith("dnIs")))
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
                if (name.startsWith("dnSet")) 
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
                else if ((name.startsWith("dnGet")) ||
                         (name.startsWith("dnIs"))) 
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