package org.jpox.samples.models.nightlabs_product;

import java.util.HashSet;
import java.util.Set;

public class ProductTransfer
extends Transfer
{
    private Set products = new HashSet();

    protected ProductTransfer() { }

    public ProductTransfer(String organisationID, long transferID) {
        super(organisationID, "ProductTransfer", transferID);
    }

    public Set getProducts() {
        return products;
    }
}
