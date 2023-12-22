package org.datanucleus.tests.nullablepk;

import org.datanucleus.samples.models.nullablepk.NullablePrimitivePK;
import org.datanucleus.store.types.converters.TypeConverter;

public class NullablePrimitiveTypeConverter implements TypeConverter<Long,Long>
{
    private static final long serialVersionUID = -6482445306816424567L;

    public NullablePrimitiveTypeConverter()
    {
    }

    @Override
    public Long toDatastoreType(Long memberValue)
    {
        if (memberValue==null || memberValue==NullablePrimitivePK.NULL_VALUE) {
            return null;
        }
        return memberValue;
    }

    @Override
    public Long toMemberType(Long datastoreValue)
    {
        if (datastoreValue==null) {
            return NullablePrimitivePK.NULL_VALUE;
        }
        return datastoreValue;
    }
}
