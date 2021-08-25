package org.datanucleus.samples.multitenancy;

import org.datanucleus.api.jdo.annotations.MultiTenant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "TenantedIndexObject")
@Table(
	   indexes = {
			   @Index(name = "idx_tenant", columnList = "tenant"),
	   })
@MultiTenant(column = "tenant")
public class TenantedIndexObject {
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
