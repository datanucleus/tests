package org.jpox.samples.models.inheritance_mapped_collection;

import java.io.Serializable;

public class ContainerInheritanceRoot
implements Serializable
{
    private static final long serialVersionUID = 7850057443828138155L;
    private String organisationID;
    private String bbbID;

	protected ContainerInheritanceRoot() { }

	public ContainerInheritanceRoot(String organisationID, String bbbID) {
		this.organisationID = organisationID;
		this.bbbID = bbbID;
	}

	public String getOrganisationID() {
		return organisationID;
	}

	public String getBbbID() {
		return bbbID;
	}

}
