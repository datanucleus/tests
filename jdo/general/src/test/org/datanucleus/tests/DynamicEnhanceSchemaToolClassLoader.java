package org.datanucleus.tests;

public class DynamicEnhanceSchemaToolClassLoader extends ClassLoader
{
    public DynamicEnhanceSchemaToolClassLoader(ClassLoader parent)
    {
        super(parent);
    }

    /**
     * Define a class in this ClassLoader.
     * @param fullClassName the class name
     * @param bytes the bytes representation of the class
     * @param clr the ClassLoaderResolver to load linked classes
     */
    public synchronized void defineClass(String fullClassName, byte[] bytes)
    {
        try
        {
            defineClass(fullClassName, bytes, 0, bytes.length);
        }
        finally
        {
        }
    }
}
