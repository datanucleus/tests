/******************************************************************
Copyright (c) 2014 Renato Garcia and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
    ...
 *****************************************************************/
package org.datanucleus.tests;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.datanucleus.tests.annotations.TransactionMode.Mode.OPTIMISTIC;
import static org.datanucleus.tests.annotations.TransactionMode.Mode.PESSIMISTIC;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

import org.datanucleus.tests.annotations.Datastore;
import org.datanucleus.tests.annotations.Datastore.DatastoreKey;
import org.datanucleus.tests.annotations.TransactionMode;
import org.datanucleus.tests.annotations.TransactionMode.Mode;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.StringUtils;
import org.junit.Assume;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import junit.framework.AssertionFailedError;

/**
 * Provides support for custom annotations such as {@link Datastore} which allows to skip tests for specific configurations
 */
public class DataNucleusTestWatcher extends TestWatcher
{
    /** Log for unit testing. */
    private static final NucleusLogger LOG = NucleusLogger.getLoggerInstance("DataNucleus.Test");
    
    private Description description;

    /**
     * Log the error to using DN log
     */
    @Override
    protected void failed(Throwable e, Description description)
    {
        if(!(e instanceof AssertionFailedError))
        {
            // JUnit assert errors are already logged by tests that are not using this feature of
            // having the errors automatically sent to the log by the runner.
            // New test will not catch the exception or should raise a java.lang.AssertionError 
            // using AssertJ
            LOG.error("Exception running " + description.getDisplayName(), e);
        }
        super.failed(e, description);
    }
    public String getTestName()
    {
        return description.getTestClass().getName() + " " + description.getMethodName();
    }

    @Override
    protected void starting(Description description)
    {
        this.description = description;

        Class<?> testClass = description.getTestClass();
        if (JDOPersistenceTestCase.class.isAssignableFrom(testClass))
        {
            NucleusLogger.GENERAL.debug(">> TestWatcher.starting " + testClass);
            filterDatastores(testClass);
            filterTransactionMode(testClass);
        }
    }

    // Filter based on the Optimistic and Pessimistic transaction configuration
    private void filterTransactionMode(Class<?> testClass)
    {
        List<Mode> filterModes = findAnnotationAtMethodOrClass(TransactionMode.class, description, testClass)
                .map(annotation -> asList(annotation.value()))
                .orElse(emptyList());
        if (!filterModes.isEmpty())
        {
            boolean optimistic = JDOPersistenceTestCase.pmf.getOptimistic();

            Assume.assumeTrue(
                    filterModes
                            .stream()
                            .anyMatch(filter ->
                                (filter.equals(OPTIMISTIC) && optimistic) || (filter.equals(PESSIMISTIC) && !optimistic)
                            ));
        }
    }

    private void filterDatastores(Class<?> testClass)
    {
        List<DatastoreKey> filterDatastores =
                findAnnotationAtMethodOrClass(Datastore.class, description, testClass)
                        .map(annotation -> asList(annotation.value()))
                        .orElse(emptyList());
        NucleusLogger.GENERAL.debug(">> filterDatastores=" + StringUtils.collectionToString(filterDatastores));

        if (!filterDatastores.isEmpty())
        {
            String datastoreKey = JDOPersistenceTestCase.storeMgr.getStoreManagerKey();
            DatastoreKey currentDatastore = DatastoreKey.valueOf(datastoreKey.toUpperCase());
            DatastoreKey vendorIdDatastore = JDOPersistenceTestCase.vendorID == null ?
                    null : DatastoreKey.valueOf(JDOPersistenceTestCase.vendorID.toUpperCase());
            NucleusLogger.GENERAL.debug(">> " + testClass + " current=" + currentDatastore + " vendor=" + vendorIdDatastore);

            boolean filtered = filterDatastores.stream()
                    .anyMatch(filter -> filter.equals(currentDatastore) || filter.equals(vendorIdDatastore));
            NucleusLogger.GENERAL.debug(">> filtered=" + filtered);
            // TODO With JUNIT 4.11+ this does NOT halt the test (unlike what the Javadoc says, and what it did in earlier versions)
            Assume.assumeTrue(filtered);
        }
    }

    private <T extends Annotation> Optional<T> findAnnotationAtMethodOrClass(Class<T> annotation, Description description, Class<?> testClass)
    {
        Annotation foundAnnotation = description.getAnnotation(annotation);
        if (foundAnnotation == null)
        {
            foundAnnotation = testClass.getAnnotation(annotation);
        }

        return Optional.ofNullable((T) foundAnnotation);
    }
}
