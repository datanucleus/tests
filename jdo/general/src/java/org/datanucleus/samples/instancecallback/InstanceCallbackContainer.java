package org.datanucleus.samples.instancecallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InstanceCallbackContainer
{
    private Map icTestersByPersistentValue;
    private Set icTesters;
    
    public InstanceCallbackContainer()
    {
        icTesters = new HashSet();
        icTestersByPersistentValue = new HashMap();
    }
    
    /**
     * @return Returns the icTesters.
     */
    public Set getIcTesters()
    {
        return icTesters;
    }

    public void addIcTesterToSet(InstanceCallbackTester tester)
    {
        this.icTesters.add(tester);
    }

    public void addIcTesterToMap(InstanceCallbackTester tester)
    {
        this.icTestersByPersistentValue.put(tester.getPersistentValue(), tester);
    }

    /**
     * @return Returns the icTestersByPersistentValue.
     */
    public Map getIcTestersByPersistentValue()
    {
        return icTestersByPersistentValue;
    }
}
