package org.datanucleus.samples.models.nullablepk;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.Objects;

@PersistenceCapable(table = "nullablepk", objectIdClass = NullableObjectPK.ID.class)
public class NullableObjectPK implements NullablePK
{
    @Column(name = "dummy")
    String dummyNonPKFirst;

    @PrimaryKey
    @Column(name = "mypktypedb")
    long mypktype;

    @PrimaryKey
    @Column(name = "mypk1db", allowsNull = "true")
    Long mypk1;

    @PrimaryKey
    @Column(name = "mypk2db", allowsNull = "true")
    Long mypk2;

    @Column(name = "val")
    String value;

    @Column(name = "unit", allowsNull = "true")
    Long unit;

    public NullableObjectPK(long mypktype, long mypk)
    {
        this.mypktype = mypktype;
        if (mypktype==0)
        {
            this.mypk1 = mypk;
            this.mypk2 = null;
        }
        else
        {
            this.mypk1 = null;
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

    public Long getMypk1()
    {
        return mypk1;
    }

    public void setMypk1(Long mypk1)
    {
        this.mypk1 = mypk1;
    }

    public Long getMypk2()
    {
        return mypk2;
    }

    public void setMypk2(Long mypk2)
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

    public Long getUnit()
    {
        return unit;
    }

    public void setUnit(Long unit)
    {
        this.unit = unit;
    }



    public static class ID implements Serializable
    {
        private static final long serialVersionUID = 258140270465562475L;
        public long mypktype;
        public Long mypk1;
        public Long mypk2;

        public ID()
        {
        }

        public ID(long mypktype, Long mypk1, Long mypk2)
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
            return mypktype == id.mypktype && Objects.equals(mypk1, id.mypk1) && Objects.equals(mypk2, id.mypk2);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(mypktype, mypk1, mypk2);
        }
    }
}
