package org.datanucleus.samples.models.nightlabs_inheritance;

import javax.jdo.annotations.*;

@PersistenceCapable(detachable="true")
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class ConcreteSub1 extends AbstractSub1
{
    public ConcreteSub1(String organisationID, String configModuleClassName, String configModuleInitialiserID)
    {
        super(organisationID, configModuleClassName, configModuleInitialiserID, "LegalEntitySearch", null);
    }
}
