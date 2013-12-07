package org.jpox.samples.models.nightlabs_product;

public class Product {
    private String organisationID;
    private long productID;

    private ProductType productType;

    public Product() { }

    public Product(String organisationID, long productID, ProductType productType) {
        this.organisationID = organisationID;
        this.productID = productID;
        this.productType = productType;
    }

    public String getOrganisationID() {
        return organisationID;
    }
    public long getProductID() {
        return productID;
    }

    public ProductType getProductType() {
        return productType;
    }
}
