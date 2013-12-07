package org.jpox.samples.models.referenceMapping;

import javax.jdo.annotations.*;

@PersistenceCapable(identityType= IdentityType.APPLICATION)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy= DiscriminatorStrategy.VALUE_MAP, value = "Dossier")
public class Folder {

    @Persistent(primaryKey = "true")
    private long id;

    @Persistent(types=Customer.class)
    @Extensions(@Extension(vendorName = "datanucleus", key = "mapping-strategy", value = "xcalia"))
    private Object customer;

    public Folder(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Object getCustomer() {
        return customer;
    }

    public void setCustomer(Object customer) {
        this.customer = customer;
    }
}
