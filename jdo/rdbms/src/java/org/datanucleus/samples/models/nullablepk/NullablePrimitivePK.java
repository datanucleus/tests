package org.datanucleus.samples.models.nullablepk;

import org.datanucleus.metadata.MetaData;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.Objects;

@PersistenceCapable(table = "nullablepk", objectIdClass = NullablePrimitivePK.ID.class)
public class NullablePrimitivePK implements NullablePK
{
    public static final long NULL_VALUE = -33L;

    @Column(name = "dummy")
    String dummyNonPKFirst;

    @PrimaryKey
    @Column(name = "mypktypedb")
    long mypktype;

    @PrimaryKey
    @Column(name = "mypk1db", allowsNull = "true")
    @Extension(
            vendorName = MetaData.VENDOR_NAME,
            key = "type-converter-name",
            value = "org.datanucleus.tests.nullablepk.NullablePrimitiveTypeConverter")
    long mypk1;

    @PrimaryKey
    @Column(name = "mypk2db", allowsNull = "true")
    @Extension(
            vendorName = MetaData.VENDOR_NAME,
            key = "type-converter-name",
            value = "org.datanucleus.tests.nullablepk.NullablePrimitiveTypeConverter")
    long mypk2;

    @Column(name = "val")
    String value;

    @Column(name = "unit", allowsNull = "true")
    @Extension(
            vendorName = MetaData.VENDOR_NAME,
            key = "type-converter-name",
            value = "org.datanucleus.tests.nullablepk.NullablePrimitiveTypeConverter")
    long unit = NULL_VALUE;

    public NullablePrimitivePK(long mypktype, long mypk)
    {
        this.mypktype = mypktype;
        if (mypktype==0)
        {
            this.mypk1 = mypk;
            this.mypk2 = NULL_VALUE;
        }
        else
        {
            this.mypk1 = NULL_VALUE;
            this.mypk2 = mypk;
        }
    }

    public String getDummyNonPKFirst()
    {
        return dummyNonPKFirst;
    }

    public void setDummyNonPKFirst(String dummyNonPKFirst)
    {
        this.dummyNonPKFirst = dummyNonPKFirst;
    }

    public long getMypktype()
    {
        return mypktype;
    }

    public void setMypktype(long mypktype)
    {
        this.mypktype = mypktype;
    }

    public long getMypk1()
    {
        return mypk1;
    }

    public void setMypk1(long mypk1)
    {
        this.mypk1 = mypk1;
    }

    public long getMypk2()
    {
        return mypk2;
    }

    public void setMypk2(long mypk2)
    {
        this.mypk2 = mypk2;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public long getUnit()
    {
        return unit;
    }

    public void setUnit(long unit)
    {
        this.unit = unit;
    }



    public static class ID implements Serializable
    {
        private static final long serialVersionUID = -5179033215838447479L;
        public long mypktype;
        public long mypk1;
        public long mypk2;

        public ID()
        {
        }

        public ID(long mypktype, long mypk1, long mypk2)
        {
            this.mypktype = mypktype;
            this.mypk1 = mypk1;
            this.mypk2 = mypk2;
        }

        @Override
        public String toString()
        {
            return "ID{" +
                    "mypktype=" + mypktype +
                    ", mypk1=" + mypk1 +
                    ", mypk2=" + mypk2 +
                    '}';
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ID id = (ID) o;
            return mypktype == id.mypktype && mypk1 == id.mypk1 && mypk2 == id.mypk2;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(mypktype, mypk1, mypk2);
        }
    }
}
