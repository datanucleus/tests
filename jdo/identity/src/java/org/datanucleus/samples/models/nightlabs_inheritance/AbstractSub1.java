package org.datanucleus.samples.models.nightlabs_inheritance;

import javax.jdo.annotations.*;

@PersistenceCapable(detachable="true", table="NL_INF_SUB")
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public abstract class AbstractSub1 extends Base
{
    @Persistent
    private String clientType;

    @Persistent
    private String useCaseName;

    public AbstractSub1(String organisationID, String configModuleClassName,
            String configModuleInitialiserID, String useCaseName, String clientType)
    {
        super(organisationID, configModuleClassName, configModuleInitialiserID);
        assert useCaseName != null && useCaseName.trim().length() > 0;
        this.clientType = clientType;
        this.useCaseName = useCaseName;
    }

    public AbstractSub1(String organisationID, String configModuleClassName,
            String configModuleInitialiserID, Integer priority, String useCaseName, String clientType)
    {
        super(organisationID, configModuleClassName, configModuleInitialiserID,
                priority);
        assert useCaseName != null && useCaseName.trim().length() > 0;
        this.clientType = clientType;
        this.useCaseName = useCaseName;
    }

    public String getClientType()
    {
        return clientType;
    }

    public String getUseCaseName()
    {
        return useCaseName;
    }
}