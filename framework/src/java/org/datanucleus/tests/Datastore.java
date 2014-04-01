package org.datanucleus.tests;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Allow tests to be run for a specific datastore or RDBMS vendor
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface Datastore {

    public enum DatastoreKey {
        rdbms,
        // Vendors
        h2, mysql, postgresql, oracle,
        // Others
        mongodb,
        ldap,
        xml
    }

    DatastoreKey[] value();
}
