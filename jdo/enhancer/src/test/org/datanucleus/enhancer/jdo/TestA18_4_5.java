package org.datanucleus.enhancer.jdo;

import org.datanucleus.enhancer.EnhancementHelper.RegisterClassEvent;
import org.datanucleus.enhancement.Persistable;

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
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            byte fieldFlags[] = ev.getFieldFlags();
            for (int i = 0; i < fieldFlags.length; i++)
            {
                if (i < 22) // Was 21 if use strict JDO default persistent flags
                {
                    assertEquals(
                        "field " + i + " test",
                        Persistable.CHECK_READ,
                        fieldFlags[i] & Persistable.CHECK_READ);
                    assertEquals(
                        "field " + i + " test",
                        Persistable.CHECK_WRITE,
                        fieldFlags[i] & Persistable.CHECK_WRITE);
                }
                else
                {
                    assertEquals(
                        "field " + i + " test",
                        Persistable.MEDIATE_READ,
                        fieldFlags[i] & Persistable.MEDIATE_READ);
                    assertEquals(
                        "field " + i + " test",
                        Persistable.MEDIATE_WRITE,
                        fieldFlags[i] & Persistable.MEDIATE_WRITE);
                }
            }
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
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            byte fieldFlags[] = ev.getFieldFlags();
            for (int i = 0; i < fieldFlags.length; i++)
            {
                assertEquals(
                    "field " + i + " CHECK_READ",
                    Persistable.CHECK_READ,
                    fieldFlags[i] & Persistable.CHECK_READ);
                assertEquals(
                    "field " + i + " CHECK_WRITE",
                    Persistable.CHECK_WRITE,
                    fieldFlags[i] & Persistable.CHECK_WRITE);
            }
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
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            byte fieldFlags[] = ev.getFieldFlags();
            for (int i = 0; i < fieldFlags.length; i++)
            {
                assertEquals(
                    "field " + i + " MEDIATE_READ",
                    Persistable.MEDIATE_READ,
                    fieldFlags[i] & Persistable.MEDIATE_READ);
                assertEquals(
                    "field " + i + " MEDIATE_WRITE",
                    Persistable.MEDIATE_WRITE,
                    fieldFlags[i] & Persistable.MEDIATE_WRITE);
            }
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
            RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
            byte fieldFlags[] = ev.getFieldFlags();
            for (int i = 1; i < 2; i++)
            {
                assertEquals("field " + i + " MEDIATE_WRITE",
                    Persistable.MEDIATE_WRITE,
                    fieldFlags[i] & Persistable.MEDIATE_WRITE);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}