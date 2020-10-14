package org.datanucleus.enhancer.jdo;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.InvalidPrimaryKeyException;

/**
 */
public class TestA05_4_9 extends JDOTestBase
{
	public void testHaveSerializableProtectedField()
    {
		try
        {
			Class classes[] = getEnhancedClassesFromFile("org/datanucleus/enhancer/samples/A5_4_9.jdo");
			Class targetClass = findClass(classes, "org.datanucleus.enhancer.samples.FullProtectedClass");
			targetClass.newInstance();
			fail("must throw InvalidPrimaryKeyException with key 019010");
		}
        catch (NucleusUserException ue)
        {
            Throwable[] nested = ue.getNestedExceptions();
            assertEquals(nested.length, 1);
            assertEquals(nested[0].getClass(), InvalidPrimaryKeyException.class);
            InvalidPrimaryKeyException ipke = (InvalidPrimaryKeyException)nested[0];
            assertEquals("019010", ipke.getMessageKey());
        }
        catch (Throwable e)
        {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}