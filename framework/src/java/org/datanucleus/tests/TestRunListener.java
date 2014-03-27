package org.datanucleus.tests;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.jdo.JDOFatalUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;
import javax.sql.DataSource;

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
        LOG.info("Preparing datastore for test run");

        boolean skipDatastoreReset = Boolean.getBoolean("maven.datanucleus.test.skip.reset");

        if (!skipDatastoreReset)
        {
            cleanupDatastore(1);
            
            try
            {
                // TODO Find a better solution to determine if it should cleanup the 2nd datastore
                cleanupDatastore(2);
            }
            catch (JDOFatalUserException e)
            {
                // Some datastores won't have the 2nd  
            }
        }
    }

    private void cleanupDatastore(int number)
    {
        String datastoreProtocol = TestHelper.getDatastorePluginProtocol(number);
        if (datastoreProtocol != null && !datastoreProtocol.equals("jdbc")) // Avoid creating PMF when clean up may not be supported
        {
            LOG.info("Datastore clean up not supported for datastore=" + datastoreProtocol);
            return;
        }

        JDOPersistenceManagerFactory pmf = (JDOPersistenceManagerFactory) TestHelper.getPMF(number, null);
        PersistenceNucleusContext ctx = pmf.getNucleusContext();
        if (ctx.getStoreManager() instanceof RDBMSStoreManager)
        {
            PersistenceManager pm = null;
            JDOConnection jdoConnection = null;
            Connection nativeConnection = null;

            try
            {
                pm = pmf.getPersistenceManager();
                jdoConnection = pm.getDataStoreConnection();

                // Obtain the connection via PM instead of using the PMF properties so we support DataSource too
                nativeConnection = (Connection) jdoConnection.getNativeConnection();

                cleanupRDBMSdatastore(nativeConnection);
            }
            catch (SQLException e)
            {
                LOG.error("Error during datastore clean up", e);
            }
            finally
            {
                if (jdoConnection != null)
                {
                    jdoConnection.close();
                }

                if (pm != null)
                {
                    pm.close();
                }
            }
        }
        else
        {
            // TODO Support cleanup of mongodb, Cassandra, Excel, ODF etc
            LOG.info("Datastore clean up not supported");
        }

        pmf.close();
    }

    private void cleanupRDBMSdatastore(Connection connection) throws SQLException
    {
        LOG.info("Cleaning up datastore before running the tests");

        String url = connection.getMetaData().getURL();

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
            flyway.setDataSource(new ConnectionWrapperDataSource(connection));
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

    /*
     * A connection wrapper so we can use the connection obtained via PM on Flyway.
     */
    public class ConnectionWrapperDataSource implements DataSource
    {
        public ConnectionWrapperDataSource(Connection connection)
        {
            super();
            this.connection = connection;
        }

        private final Connection connection;

        @Override
        public PrintWriter getLogWriter() throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLoginTimeout() throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Connection getConnection() throws SQLException
        {
            return connection;
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public Logger getParentLogger() throws SQLFeatureNotSupportedException
        {
            throw new UnsupportedOperationException();
        }
    }
}
