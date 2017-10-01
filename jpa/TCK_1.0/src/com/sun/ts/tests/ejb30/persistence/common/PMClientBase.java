 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */
/*
 * @(#)PMClientBase.java	1.8 06/04/19
 */

package com.sun.ts.tests.ejb30.persistence.common;

import java.io.BufferedInputStream;
import java.util.Properties;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

abstract public class PMClientBase extends ServiceEETest
        implements com.sun.ts.tests.common.vehicle.ejb3share.UseEntityManager,
        java.io.Serializable {
    protected Properties props;
    
    transient private EntityManager em;
    transient private EntityTransaction et;
    transient private boolean inContainer;
    
    //The following are properties specific to standalone TCK,
    //not used when running tests in JavaEE environment
    
    transient private EntityManagerFactory emf;

    transient public static final String PROVIDER_PROP =
            "javax.persistence.provider";
    
    transient public static final String TRANSACTION_TYPE_PROP =
            "javax.persistence.transactionType";
    
    transient public static final String JTA_DATASOURCE_PROP =
            "javax.persistence.jtaDataSource";
    
    transient public static final String NON_JTA_DATASOURCE_PROP =
            "javax.persistence.nonJtaDataSource";
    
    transient public static final String RESOURCE_LOCAL = "RESOURCE_LOCAL";
    
    /**
     * Name of a property defined in ts.jte, to denote whether tests run in
     * JavaEE or Java SE mode.  This property must not be set when running tests
     * in JavaEE mode; and must be set to "standalone" when in Java SE mode.
     */
    transient public static final String MODE_PROP = "platform.mode";
    
    /**
     * Denotes that tests are running in Java SE mode. This is the only valid
     * non-null value for this property.
     */
    transient public static final String STANDALONE_MODE = "standalone";
    
    /**
     * Name of a property defined in ts.jte, to specify the name of the
     * persistence unit used in the testsuite.  It must be consistent with
     * the value in persistence.xml
     */
    transient public static final String PERSISTENCE_UNIT_NAME_PROP = "persistence.unit.name";
    
    /**
     * Name of the property in ts.jte that specifies an absolute path to the 
     * properties file that contains properties for initializing EntityManagerFactory,
     * including both standard and provider-specific properties.
     */
    transient public static final String PERSISTENCE_UNIT_PROPERTIES_FILE_PROP =
            "persistence.unit.properties.file.full.path";
    
    /**
     * The current test mode. The only valid non-null value is "standalone".
     */
    transient private String mode;
    
    /**
     * Persistence unit name, as defined in ts.jte.
     */
    private String persistenceUnitName;
    
    protected PMClientBase() {
        super();
    }
    
    protected  void removeEntity(Object o) {
        if(o != null) {
            try {
                getEntityManager().remove(o);
            } catch (Exception e) {
                TLogger.log("removeEntity: Exception caught when removing entity: "
                        + e.toString());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * If a subclass overrides this method, the overriding implementation must
     * call super.setup() at the beginning.
     */
    public void setup(String[] args, Properties p) throws Fault {
        props = p;
        mode = p.getProperty(MODE_PROP);
        persistenceUnitName = p.getProperty(PERSISTENCE_UNIT_NAME_PROP);
        if(mode == null) {
            TLogger.log(MODE_PROP + " is set to " + mode +
                    ", so tests are running in JavaEE environment.");
        } else if(STANDALONE_MODE.equalsIgnoreCase(mode)) {
            TLogger.log(MODE_PROP + " is set to " + mode +
                    ", so tests are running in J2SE environment standalone mode." +
                    PERSISTENCE_UNIT_NAME_PROP + " is set to "
                    + persistenceUnitName);
        } else {
            TLogger.log("WARNING: " + MODE_PROP + " is set to " + mode +
                    ", an invalid value.");
        }
    }
    
    /**
     * In JavaEE environment, does nothing.  In Java SE environment, closes the
     * EntityManager if its open, and closes the EntityManagerFactory if its open.
     * If a subclass overrides this method, the overriding implementation must
     * call super.cleanup() at the end.
     */
    public void  cleanup() throws Fault	{
        if(isStandAloneMode()) {
            if(em != null && em.isOpen()) {
                em.close();
            }
            if(emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
    
    public void setEntityManager(javax.persistence.EntityManager em) {
        this.em = em;
    }
    
    public EntityManager getEntityManager() {
        if (this.em != null) {
            return this.em;
        }
        if (isStandAloneMode() ) {
            initEntityManager();
            return this.em;
        }
        throw new IllegalStateException("The test is running in JavaEE environment, " +
                "but PMClientBase.em has not been initialized from the vehicle component.");
    }
    
    public void setEntityTransaction(EntityTransaction et) {
        this.et = et;
    }
    
    public EntityTransaction getEntityTransaction() {
        if (this.et != null) {
            return this.et;
        }
        if (isStandAloneMode() ) {
            initEntityTransaction();
            return this.et;
        }
        throw new IllegalStateException("The test is running in JavaEE environment, " +
                "but PMClientBase.et has not been initialized from the vehicle component.");
    }
    
    /**
     * Creates EntityManager in JavaSE environment.  In JavaEE environment,
     * EntityManager should already have been set from within the vehicle.
     */
    protected void initEntityManager() {
        Properties propsMap = getPersistenceUnitProperties();
        emf = Persistence.createEntityManagerFactory(persistenceUnitName, propsMap);
        this.em = emf.createEntityManager();
    }
    
    /**
     * Creates EntityTransaction in JavaSE environment.  In JavaEE environment,
     * EntityManager should already have been set from within the vehicle.
     */
    protected void initEntityTransaction() {
        EntityTransaction delegate = getEntityManager().getTransaction();
        this.et = delegate;
    }
    
    public boolean isInContainer() {
        return inContainer;
    }
    
    public void setInContainer(boolean inContainer) {
        this.inContainer = inContainer;
    }
    
   /*
    * Properties needed for Standalone TCK persistence.xml
    */
    protected Properties getPersistenceUnitProperties() {
        String emfPropFilePath = props.getProperty(PERSISTENCE_UNIT_PROPERTIES_FILE_PROP);
        Properties jpaProps = new Properties();
        File propFile = new File(emfPropFilePath);
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(propFile));
            jpaProps.load(bis);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read emf properties file: " +
                    emfPropFilePath, e);
        } finally {
            if(bis != null) {
                try {
                    bis.close();
                } catch (IOException ignore) {
                }
            }
        }
        checkPersistenceUnitProperties(jpaProps);
        return jpaProps;
    }
    
    public boolean isStandAloneMode() {
        if(mode == null) {
            return false;
        }
        return STANDALONE_MODE.equalsIgnoreCase(mode);
    }

    /**
     * Verifies certain properties that are not applicable in Java SE environment
     * are not filtered out, and not passed to Persistence.createEntityManagerFactory.
     */
    private void checkPersistenceUnitProperties(Properties jpaProps) {
        TLogger.log("persistence unit properites from user: " + jpaProps.toString());
        String provider = jpaProps.getProperty(PROVIDER_PROP);
        if(provider == null) {
            throw new IllegalStateException(PROVIDER_PROP + 
                    " not specified in persistence unit properties file");
        }
        String transactionType = jpaProps.getProperty(TRANSACTION_TYPE_PROP);
        if(transactionType != null && !RESOURCE_LOCAL.equals(transactionType)) {
            throw new IllegalStateException(TRANSACTION_TYPE_PROP +
                    " is set to an unsupported value: " + transactionType +
                    ".  The only portably supported type is " + RESOURCE_LOCAL +
                    ".  Please correct it in persistence unit properties file.");
        }
        String jtaDataSource = jpaProps.getProperty(JTA_DATASOURCE_PROP);
        if(jtaDataSource != null) {
            TLogger.log("WARNING: " + JTA_DATASOURCE_PROP + " is specified as " +
                    jtaDataSource + ", and it will be passed to the persistence " +
                    "provider.  However, this is in general not supported in " +
                    "Java SE environment");
//            jpaProps.remove(JTA_DATASOURCE_PROP);
        }
//        String nonJtaDataSource = jpaProps.getProperty(NON_JTA_DATASOURCE_PROP);
//        if(nonJtaDataSource == null) {
//            throw new IllegalStateException(NON_JTA_DATASOURCE_PROP + " is required " +
//                    "in Java SE environment.  It has not been specified.  Please " +
//                    "set it in persistence unit properties file.");
//        }
        TLogger.log("persistence unit properites verified: " + jpaProps.toString());
    }
}

