package org.datanucleus.samples.multitenancy;


import org.datanucleus.api.jpa.annotations.MultiTenant;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "TenantedObject")
@MultiTenant(columnLength = 40)
public class TenantedObject {
	@Id
	long id;
	String name;

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
