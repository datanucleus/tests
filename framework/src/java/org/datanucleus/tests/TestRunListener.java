package org.datanucleus.tests;

import java.io.File;
import java.util.Properties;

import javax.jdo.Constants;

import org.datanucleus.PersistenceNucleusContext;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.NucleusLogger;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

import com.googlecode.flyway.core.Flyway;

/**
 * Listener used in the test projects allowing to run tasks before and after all the tests are run.
 */
public class TestRunListener extends RunListener
{
    public static final NucleusLogger LOG = NucleusLogger.getLoggerInstance("DataNucleus.Test");

    @Override
    public void testRunStarted(Description description) throws Exception
    {
        super.testRunStarted(description);
        prepareDatastore();
    }

    /**
     * Remove all the objects from the datastore so the tests can run from scratch
     */
    private void prepareDatastore()
    {
        boolean skipDatastoreReset = Boolean.getBoolean("maven.datanucleus.test.skip.reset");

        if (!skipDatastoreReset)
        {
            JDOPersistenceManagerFactory pmf =
                    (JDOPersistenceManagerFactory) TestHelper.getPMF(1, null);

            PersistenceNucleusContext ctx = pmf.getNucleusContext();
            if (ctx.getStoreManager() instanceof RDBMSStoreManager)
            {
                cleanupRDBMSdatastore();
            }
            else
            {
                LOG.info("Datastore clean up not supported");
            }
        }
    }

    private void cleanupRDBMSdatastore()
    {
        LOG.info("Cleaning up datastore before running the tests");

        Properties properties = TestHelper.getPropertiesForDatastore(1);
        String url = properties.getProperty(Constants.PROPERTY_CONNECTION_URL);
        String user = properties.getProperty(Constants.PROPERTY_CONNECTION_USER_NAME);
        String password = properties.getProperty(Constants.PROPERTY_CONNECTION_PASSWORD);

        if (url.contains("sqlite"))
        {
            // Flyway currently doesn't support SQLite
            // https://github.com/flyway/flyway/issues/76
            cleanupSQLite(url);
        }
        else
        {
            // Use Flyway to clean up RDBMS instances
            Flyway flyway = new Flyway();
            flyway.setDataSource(url, user, password);
            flyway.clean();
        }
    }

    private void cleanupSQLite(String url)
    {
        // Workaround for SQLite
        String filename = url.substring(url.lastIndexOf(":") + 1);
        File file = new File(filename);
        file.delete();
    }
}
