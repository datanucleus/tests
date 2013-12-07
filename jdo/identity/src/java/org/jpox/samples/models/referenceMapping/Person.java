package org.jpox.samples.models.referenceMapping;

import javax.jdo.annotations.*;

@PersistenceCapable
@Inheritance(strategy= InheritanceStrategy.SUBCLASS_TABLE)
@Discriminator(strategy= DiscriminatorStrategy.VALUE_MAP, value = "Person")
public abstract class Person {
    @Persistent(primaryKey = "true")
    private long id;

    protected Person(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
