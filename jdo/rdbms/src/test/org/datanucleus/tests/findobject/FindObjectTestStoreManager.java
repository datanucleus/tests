package org.datanucleus.tests.findobject;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.PersistenceNucleusContext;
import org.datanucleus.store.rdbms.RDBMSPersistenceHandler;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

import java.util.Map;

public class FindObjectTestStoreManager extends RDBMSStoreManager
{
    public static final String DN_CONNECTIONURL = "datanucleus.ConnectionURL";
    public static final String STOREMANAGER_PREFIX = "findobject_rdbms";

    /**
     * Constructs a new RDBMSManager.
     * On successful return the new RDBMSManager will have successfully connected to the database with the given
     * credentials and determined the schema name, but will not have inspected the schema contents any further.
     * The contents (tables, views, etc.) will be subsequently created and/or validated on-demand as the application
     * accesses persistent classes.
     *
     * @param clr   the ClassLoaderResolver
     * @param ctx   The corresponding Context. This factory's non-tx data source will be used to get database connections as needed to perform management functions.
     * @param props Properties for the datastore
     * @throws NucleusDataStoreException If the database could not be accessed or the name of the schema could not be determined.
     */
    public FindObjectTestStoreManager(ClassLoaderResolver clr, PersistenceNucleusContext ctx, Map<String, Object> props)
    {
        super(clr, ctx, fixProps(props));
    }

    private static Map<String, Object> fixProps(Map<String, Object> props)
    {
        String dnConnectionurl = DN_CONNECTIONURL;
        String url = (String) props.get(dnConnectionurl);
        if (url == null)
        {
            dnConnectionurl = dnConnectionurl.toLowerCase();
            url = (String) props.get(dnConnectionurl);
        }
        props.put(dnConnectionurl, url.substring(STOREMANAGER_PREFIX.length()));
        return props;
    }

    @Override
    protected RDBMSPersistenceHandler createPersistenceHandler()
    {
        return new FindObjectTestPersistenceHandler(this);
    }
}
