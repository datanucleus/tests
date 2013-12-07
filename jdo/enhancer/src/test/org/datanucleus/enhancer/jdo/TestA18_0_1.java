package org.datanucleus.enhancer.jdo;

import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.RegisterClassEvent;

/**
 */
public class TestA18_0_1 extends JDOTestBase
{
	public void testJdoManagedFieldNum() 
    {
		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullDefaultClass.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullDefaultClass");
			targetClass.newInstance();
			RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
			assertEquals("default class of jdo field num is 55", 55, ev.getFieldFlags().length);
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}

		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateClass.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateClass");
			targetClass.newInstance();
			RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
			assertEquals("private class of jdo field num is 55", 55, ev.getFieldFlags().length);
		}
        catch (Throwable e) 
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}

		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_0_1.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
			assertEquals("protected class of jdo field num is 55", 55, ev.getFieldFlags().length);
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}

		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/FullPrivateClass.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullPrivateClass");
			targetClass.newInstance();
			RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
			assertEquals("private class of jdo field num is 55", 55, ev.getFieldFlags().length);
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testJdoManagedFieldName()
    {
		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A18_0_1.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			RegisterClassEvent ev = (RegisterClassEvent)pcClasses.get(targetClass);
			String fieldNames[] = ev.getFieldNames();
			for (int i = 0; i < fieldNames.length; i++) {
				String expectedValue = (i < 10) ? "field0" + i : "field" + i;
				assertEquals(expectedValue, fieldNames[i]);
			}
		}
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void testJdoManagedFieldFlag()
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
}