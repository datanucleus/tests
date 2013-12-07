package org.jpox.samples.models.nightlabs_product;

public class ProductType {
    private String organisationID;
    private String productTypeID;

    protected ProductType() { }
    public ProductType(String organisationID, String productTypeID) {
        this.organisationID = organisationID;
        this.productTypeID = productTypeID;
    }

    public String getOrganisationID() {
        return organisationID;
    }
    public String getProductTypeID() {
        return productTypeID;
    }
}
