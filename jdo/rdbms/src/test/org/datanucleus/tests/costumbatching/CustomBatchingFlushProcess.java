package org.datanucleus.tests.costumbatching;

import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusOptimisticException;
import org.datanucleus.flush.FlushOrdered;
import org.datanucleus.flush.OperationQueue;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.samples.models.transportation.Address;
import org.datanucleus.samples.models.transportation.FemaleDriver;
import org.datanucleus.samples.models.transportation.MaleDriver;
import org.datanucleus.samples.models.transportation.RobotDriver;
import org.datanucleus.state.DNStateManager;
import org.datanucleus.store.rdbms.flush.BatchingFlushProcess;
import org.datanucleus.store.rdbms.query.StatementMappingIndex;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Really simple flush process to prove batching in tests.
 * Only handles the minimum required sorting of SMs to complete the tests.
 */
public class CustomBatchingFlushProcess implements BatchingFlushProcess
{
    private FlushOrdered delegate = new FlushOrdered();

    @Override
    public boolean batchInInsertRequest(boolean hasIdentityColumn, StatementMappingIndex[] externalFKStmtMappings, AbstractClassMetaData cmd, ExecutionContext ec)
    {
        return !hasIdentityColumn && externalFKStmtMappings == null;
    }

    @Override
    public boolean batchInUpdateRequest(boolean optimisticChecks, ExecutionContext ec)
    {
        return true;
    }

    @Override
    public boolean batchInDeleteRequest(boolean optimisticChecks, ExecutionContext ec)
    {
        return ec.getTransaction().isActive();
    }

    private static Map<Class, Integer> classSorting = Map.of
            (
                    Address.class, 1,
                    RobotDriver.class, 2,
                    MaleDriver.class, 3,
                    FemaleDriver.class, 4
            );
    @Override
    public List<NucleusOptimisticException> execute(ExecutionContext ec, Collection<DNStateManager> primarySMs, Collection<DNStateManager> secondarySMs, OperationQueue opQueue)
    {
        // This very simple test flush process simply orders primarySMs t
        Set<DNStateManager> inserted = new HashSet<>();
        Set<DNStateManager> deleted = new HashSet<>();
        primarySMs.forEach(sm->
        {
            if (sm.getLifecycleState().isNew() && !sm.isFlushedToDatastore() && !sm.isFlushedNew())
            {
                inserted.add(sm);
            }
            else if (sm.getLifecycleState().isDeleted() && !sm.isFlushedToDatastore())
            {
                deleted.add(sm);
            }
        });

        Comparator<DNStateManager<?>> sorter = Comparator.comparing((DNStateManager<?> o) -> classSorting.get(o.getObject().getClass()))
                .thenComparing(Object::hashCode);
        final TreeSet<DNStateManager> sortedPrimarySMs = new TreeSet<>(new Comparator<DNStateManager>()
        {
            @Override
            public int compare(DNStateManager o1, DNStateManager o2)
            {
                if (inserted.contains(o1))
                {
                    if (inserted.contains(o2))
                    {
                        return sorter.compare(o1,o2);
                    }
                    return -1;
                }
                else if (deleted.contains(o1))
                {
                    if (deleted.contains(o2))
                    {
                        return -sorter.compare(o1,o2);
                    }
                    return 1;

                }
                else
                {
                    if (inserted.contains(o2))
                    {
                        return 1;
                    }
                    else if (deleted.contains(o2))
                    {
                        return -1;
                    }
                    return sorter.compare(o1,o2);
                }
            }
        });
        sortedPrimarySMs.addAll(primarySMs);
        primarySMs.clear();
        return delegate.execute(ec, sortedPrimarySMs, secondarySMs, opQueue);
    }
}
