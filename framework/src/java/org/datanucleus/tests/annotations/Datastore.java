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
package org.datanucleus.tests.annotations;

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
public @interface Datastore 
{
    // The values matches the key returned by StoreManager#getStoreManagerKey converted to upper case 
    public enum DatastoreKey 
    {
        RDBMS,
        // Vendors - Matching keys from Datastore
        H2,
        MYSQL, 
        POSTGRESQL, 
        ORACLE,
        HSQL,
        DERBY,
        SQLSERVER,
        SQLITE,
        CLOUDSPANNER,
        // Others
        CASSANDRA,
        EXCEL,
        HBASE,
        JSON,
        LDAP,
        MONGODB,
        NEO4J,
        NEODATIS,
        ODF,
        XML,
    }

    DatastoreKey[] value();
}
