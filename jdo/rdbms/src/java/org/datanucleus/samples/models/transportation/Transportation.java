package org.datanucleus.samples.models.transportation;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.Objects;

@PersistenceCapable(table = "transportation", objectIdClass = Transportation.ID.class)
@Discriminator(strategy = DiscriminatorStrategy.VALUE_MAP, column = "objectType")
public abstract class Transportation
{
    @PrimaryKey
    private long id;
    @PrimaryKey
    private String objectType;

    @Persistent(defaultFetchGroup = "true")
    @Column(name = "thename")
    private String name;


    protected Transportation()
    {
    }

    protected Transportation(long id, String objectType)
    {
        this.id = id;
        this.objectType = objectType;
    }


    public long getId()
    {
        return id;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static class ID implements Serializable
    {
        private static final long serialVersionUID = -404742716646861172L;
        public long id;
        public String objectType;

        public ID()
        {
        }

        public ID(long id, String objectType)
        {
            this.id = id;
            this.objectType = objectType;
        }

        @Override
        public String toString()
        {
            return "Transportation-ID{" +
                    "id=" + id +
                    ", objectType='" + objectType + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ID id1 = (ID) o;
            return id == id1.id && Objects.equals(objectType, id1.objectType);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(id, objectType);
        }
    }
}
