/**********************************************************************
Copyright (c) 2017 Andy Jefferson and others. All rights reserved.
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
 **********************************************************************/
package org.datanucleus.api.jakarta;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import jakarta.persistence.spi.Transformer;

import javax.sql.DataSource;

/**
 * Simple implementation of a PersistenceUnitInfo provided by a JavaEE container.
 */
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo
{
    String providerClassName;

    String name;

    URL rootURL;

    PersistenceUnitTransactionType transactionType;
    
    DataSource jtaDataSource;

    DataSource nonJtaDataSource;

    List<String> mappingFileNames = new ArrayList<>();

    List<String> managedClassNames = new ArrayList<>();
    boolean excludeUnlistedClasses = false;

    List<URL> jarFileUrls = new ArrayList<>();

    Properties properties = new Properties();

    public PersistenceUnitInfoImpl(String providerClassName, String unitName, PersistenceUnitTransactionType txnType, URL rootURL)
    {
        this.providerClassName = providerClassName;
        this.name = unitName;
        this.transactionType = txnType;
        this.rootURL = rootURL;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getPersistenceUnitName()
     */
    @Override
    public String getPersistenceUnitName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getPersistenceProviderClassName()
     */
    @Override
    public String getPersistenceProviderClassName()
    {
        return providerClassName;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getTransactionType()
     */
    @Override
    public PersistenceUnitTransactionType getTransactionType()
    {
        return transactionType;
    }

    public void setJtaDataSource(DataSource ds)
    {
        this.jtaDataSource = ds;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getJtaDataSource()
     */
    @Override
    public DataSource getJtaDataSource()
    {
        return jtaDataSource;
    }

    public void setNonJtaDataSource(DataSource ds)
    {
        this.nonJtaDataSource = ds;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getNonJtaDataSource()
     */
    @Override
    public DataSource getNonJtaDataSource()
    {
        return nonJtaDataSource;
    }

    public void addMappingFileName(String name)
    {
        this.mappingFileNames.add(name);
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getMappingFileNames()
     */
    @Override
    public List<String> getMappingFileNames()
    {
        return mappingFileNames;
    }

    public void addJarFileUrl(URL url)
    {
        this.jarFileUrls.add(url);
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getJarFileUrls()
     */
    @Override
    public List<URL> getJarFileUrls()
    {
        return jarFileUrls;
    }

    public void setPersistenceUnitRootUrl(URL url)
    {
        this.rootURL = url;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getPersistenceUnitRootUrl()
     */
    @Override
    public URL getPersistenceUnitRootUrl()
    {
        return rootURL;
    }

    public void addManagedClassName(String name)
    {
        this.managedClassNames.add(name);
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getManagedClassNames()
     */
    @Override
    public List<String> getManagedClassNames()
    {
        return managedClassNames;
    }

    public void setExcludeUnlistedClasses(boolean flag)
    {
        this.excludeUnlistedClasses = flag;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#excludeUnlistedClasses()
     */
    @Override
    public boolean excludeUnlistedClasses()
    {
        return excludeUnlistedClasses;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getSharedCacheMode()
     */
    @Override
    public SharedCacheMode getSharedCacheMode()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getValidationMode()
     */
    @Override
    public ValidationMode getValidationMode()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getProperties()
     */
    @Override
    public Properties getProperties()
    {
        return properties;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getPersistenceXMLSchemaVersion()
     */
    @Override
    public String getPersistenceXMLSchemaVersion()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getClassLoader()
     */
    @Override
    public ClassLoader getClassLoader()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#addTransformer(jakarta.persistence.spi.ClassTransformer)
     */
    @Override
    public void addTransformer(Transformer transformer)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()
     */
    @Override
    public ClassLoader getNewTempClassLoader()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
