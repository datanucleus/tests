package org.datanucleus.tests.enlistedobjectcache;

import org.datanucleus.cache.EnlistedSMCacheFactory;
import org.datanucleus.state.DNStateManager;

import java.util.Map;

public class EnlistedObjectCacheTestFactory implements EnlistedSMCacheFactory
{
    @Override
    public Map<Object, DNStateManager> createEnlistedSMCache()
    {
        return new EnlistedObjectCacheTestSMCache();
    }
}
