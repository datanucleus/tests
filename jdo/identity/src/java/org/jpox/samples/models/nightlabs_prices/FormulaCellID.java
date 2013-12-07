package org.jpox.samples.models.nightlabs_prices;

import java.io.Serializable;

public class FormulaCellID
implements Serializable
{
    // primary key - begin
    public String organisationID;
    public long priceConfigID;
    public long formulaID;
    // primary key - end

    public FormulaCellID() { }

    public FormulaCellID(String key) {
        String[] parts = key.split("/");
        if (parts.length != 3)
            throw new IllegalArgumentException("key \"" + key + "\" is malformed!");

        this.organisationID = parts[0];
        this.priceConfigID = Long.parseLong(parts[1]);
        this.formulaID = Long.parseLong(parts[2]);
    }

    public String toString() {
        return organisationID + '/' + priceConfigID + '/' + formulaID;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((organisationID == null) ? 0 : organisationID.hashCode());
        result = prime * result + (int) (priceConfigID ^ (priceConfigID >>> 32));
        result = prime * result + (int) (formulaID ^ (formulaID >>> 32));
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final FormulaCellID other = (FormulaCellID) obj;
        if (organisationID == null) {
            if (other.organisationID != null)
                return false;
        } else if (!organisationID.equals(other.organisationID))
            return false;
        if (priceConfigID != other.priceConfigID)
            return false;
        if (formulaID != other.formulaID)
            return false;
        return true;
    }
}
