package org.datanucleus.samples.widget;


public class InversePrimitive extends Primitive
{
    private CollectionFieldTester tester;

    /**
     * Default constructor required since this is a PersistenceCapable class.
     */
    protected InversePrimitive()
    {
    }

    public InversePrimitive(CollectionFieldTester tester)
    {
        this.tester = tester;
    }

    public CollectionFieldTester getTester()
    {
        return this.tester;
    }

    public void setTester(CollectionFieldTester tester)
    {
        this.tester = tester;
    }
}
