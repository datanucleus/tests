package org.datanucleus.tests.findobject;

import org.datanucleus.ExecutionContext;
import org.datanucleus.samples.models.transportation.Address;
import org.datanucleus.state.DNStateManager;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.ValidatingStorePersistenceHandler;
import org.datanucleus.store.rdbms.RDBMSPersistenceHandler;

public class FindObjectTestPersistenceHandler extends RDBMSPersistenceHandler implements ValidatingStorePersistenceHandler
{
    private String nextFindObjectAddressLine;
    private int callCount=0;
    /**
     * Constructor.
     *
     * @param storeMgr StoreManager
     */
    public FindObjectTestPersistenceHandler(StoreManager storeMgr)
    {
        super(storeMgr);
    }

    @Override
    public Object findObject(ExecutionContext ec, Object id)
    {
        callCount++;
        if (nextFindObjectAddressLine != null) 
        {
            final DNStateManager<Address> sm = storeMgr.getNucleusContext().getStateManagerFactory().newForHollow(ec, Address.class, id);
            final int addressLineFieldNo = sm.getClassMetaData().getAbsolutePositionOfMember("addressLine");
            sm.replaceField(addressLineFieldNo, nextFindObjectAddressLine);
            sm.isLoaded(addressLineFieldNo);
            nextFindObjectAddressLine = null;
            return sm.getObject();
        }
        return super.findObject(ec, id);
    }

    public int getCallCount()
    {
        return callCount;
    }

    public void setNextFindObject(Address o)
    {
        nextFindObjectAddressLine = o.getAddressLine();
    }

    @Override
    public void validate(DNStateManager sm, boolean readFromPersistenceHandler)
    {
        if (readFromPersistenceHandler)
        {
            return;
        }
        sm.validate();
    }
}
