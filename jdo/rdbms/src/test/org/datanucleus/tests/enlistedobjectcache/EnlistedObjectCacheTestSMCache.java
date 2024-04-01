package org.datanucleus.tests.enlistedobjectcache;

import org.datanucleus.state.DNStateManager;
import org.datanucleus.util.ConcurrentReferenceHashMap;

import java.util.Collection;

public class EnlistedObjectCacheTestSMCache extends ConcurrentReferenceHashMap<Object, DNStateManager>
{
    private static final long serialVersionUID = -1182501231359038018L;
    static int getValuesCount = 0;
    public EnlistedObjectCacheTestSMCache()
    {
        super(1, ConcurrentReferenceHashMap.ReferenceType.STRONG, ConcurrentReferenceHashMap.ReferenceType.WEAK);
    }

    @Override
    public Collection<DNStateManager> values()
    {
        getValuesCount++;
        return super.values();
    }
}
