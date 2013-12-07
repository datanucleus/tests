package org.datanucleus.samples.models.nightlabs_inheritance;

import javax.jdo.annotations.*;

@PersistenceCapable(objectIdClass=BaseID.class, detachable="true", table="NL_INH_BASE")
@Version(strategy=VersionStrategy.VERSION_NUMBER)
@FetchGroups(@FetchGroup(fetchGroups={"default"}, name="ConfigModuleInitialiser.this", members={}))
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public abstract class Base
{
    public static final Integer PRIORITY_DEFAULT = 500;

    @PrimaryKey
    @Column(length=100)
    private String organisationID;

    @PrimaryKey
    private String configModuleClassName;

    @PrimaryKey
    protected String configModuleInitialiserID;

    @Persistent
    private Integer priority;

    public Base(String organisationID, String configModuleClassName,
            String configModuleInitialiserID)
    {
        super();
        this.organisationID = organisationID;
        this.configModuleInitialiserID = configModuleInitialiserID;
        this.configModuleClassName = configModuleClassName;
        this.priority = PRIORITY_DEFAULT;
    }

    public Base(String organisationID, String configModuleClassName,
            String configModuleInitialiserID, Integer priority)
    {
        super();
        this.organisationID = organisationID;
        this.configModuleInitialiserID = configModuleInitialiserID;
        this.configModuleClassName = configModuleClassName;
        this.priority = priority;
    }

    public String getConfigModuleClassName() {
        return configModuleClassName;
    }

    public String getConfigModuleInitialiserID() {
        return configModuleInitialiserID;
    }

    public String getOrganisationID() {
        return organisationID;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
