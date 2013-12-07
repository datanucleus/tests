package org.jpox.samples.models.nightlabs_product;

public class Transfer {
    private String organisationID;
    private String transferTypeID;
    private long transferID;

    public Transfer() { }
    public Transfer(String organisationID, String transferTypeID, long transferID) {
        this.organisationID = organisationID;
        this.transferTypeID = transferTypeID;
        this.transferID = transferID;
    }

    public String getOrganisationID() {
        return organisationID;
    }
    public String getTransferTypeID() {
        return transferTypeID;
    }
    public long getTransferID() {
        return transferID;
    }
}
