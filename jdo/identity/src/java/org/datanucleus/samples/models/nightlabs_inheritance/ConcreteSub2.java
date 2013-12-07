package org.datanucleus.samples.models.nightlabs_inheritance;

import javax.jdo.annotations.*;

@PersistenceCapable(detachable="true")
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class ConcreteSub2 extends Base
{
    public ConcreteSub2(String organisationID,
            String configModuleClassName, String configModuleInitialiserID)
    {
        super(organisationID, configModuleClassName, configModuleInitialiserID);
    }

    public ConcreteSub2(String organisationID,
            String configModuleClassName, String configModuleInitialiserID, 
            Integer priority)
    {
        super(organisationID, configModuleClassName, configModuleInitialiserID,
                priority);
    }
}
