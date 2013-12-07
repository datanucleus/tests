package org.jpox.samples.models.referenceMapping;

import javax.jdo.annotations.*;

@PersistenceCapable
@Inheritance(strategy= InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy= DiscriminatorStrategy.VALUE_MAP, value = "Customer")
public class Customer extends Person {
    public Customer(long id) {
        super(id);
    }
}
