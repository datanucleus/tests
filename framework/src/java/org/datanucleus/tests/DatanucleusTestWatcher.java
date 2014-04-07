package org.datanucleus.tests;

import java.util.stream.Stream;

import org.datanucleus.tests.Datastore.DatastoreKey;
import org.junit.Assume;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Provides support for custom annotations such as {@link Datastore}.
 */
public class DatanucleusTestWatcher extends TestWatcher
{

    private Description description;
    
    public String getTestName() {
        return description.getMethodName();
    }

    @Override
    protected void starting(Description description)
    {
        this.description = description;
        
        Class<?> testClass = description.getTestClass();

        if (JDOPersistenceTestCase.class.isAssignableFrom(testClass))
        {
            filterDatastores(testClass);
        }
    }

    private void filterDatastores(Class<?> testClass)
    {
        // Use class level annotation as default if present
        final DatastoreKey[] defaultDatastores = testClass.isAnnotationPresent(Datastore.class)
                ? testClass.getAnnotation(Datastore.class).value()
                : null;

        // Use method level annotation if present otherwise fallback to the default.
        DatastoreKey[] filterDatastores = description.getAnnotation(Datastore.class) == null
                ? defaultDatastores
                : description.getAnnotation(Datastore.class).value();

        // Any datastore to filter?
        if (filterDatastores != null)
        {
            String datastoreKey = JDOPersistenceTestCase.storeMgr.getStoreManagerKey();
            DatastoreKey currentDatastore = DatastoreKey.valueOf(datastoreKey);

            DatastoreKey vendorIdDatastore = JDOPersistenceTestCase.vendorID == null
                    ? null : DatastoreKey.valueOf(JDOPersistenceTestCase.vendorID);

            Assume.assumeTrue(
                    Stream.of(filterDatastores)
                            .anyMatch(datastore -> datastore.equals(currentDatastore) || datastore.equals(vendorIdDatastore)));
        }
    }
}
