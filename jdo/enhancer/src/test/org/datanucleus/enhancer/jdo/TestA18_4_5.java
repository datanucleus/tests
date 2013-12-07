package org.datanucleus.enhancer.jdo;

import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.RegisterClassEvent;

/**
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
                        PersistenceCapable.CHECK_READ,
                        fieldFlags[i] & PersistenceCapable.CHECK_READ);
                    assertEquals(
                        "field " + i + " test",
                        PersistenceCapable.CHECK_WRITE,
                        fieldFlags[i] & PersistenceCapable.CHECK_WRITE);
                }
                else
                {
                    assertEquals(
                        "field " + i + " test",
                        PersistenceCapable.MEDIATE_READ,
                        fieldFlags[i] & PersistenceCapable.MEDIATE_READ);
                    assertEquals(
                        "field " + i + " test",
                        PersistenceCapable.MEDIATE_WRITE,
                        fieldFlags[i] & PersistenceCapable.MEDIATE_WRITE);
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
                    PersistenceCapable.CHECK_READ,
                    fieldFlags[i] & PersistenceCapable.CHECK_READ);
                assertEquals(
                    "field " + i + " CHECK_WRITE",
                    PersistenceCapable.CHECK_WRITE,
                    fieldFlags[i] & PersistenceCapable.CHECK_WRITE);
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
                    PersistenceCapable.MEDIATE_READ,
                    fieldFlags[i] & PersistenceCapable.MEDIATE_READ);
                assertEquals(
                    "field " + i + " MEDIATE_WRITE",
                    PersistenceCapable.MEDIATE_WRITE,
                    fieldFlags[i] & PersistenceCapable.MEDIATE_WRITE);
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
                    PersistenceCapable.MEDIATE_WRITE,
                    fieldFlags[i] & PersistenceCapable.MEDIATE_WRITE);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}