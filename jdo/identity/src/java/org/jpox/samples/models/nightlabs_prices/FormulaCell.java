/**********************************************************************
Copyright (c) 2005 Marco Schulze (NightLabs) and others.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.



Contributions
    ...
 ***********************************************************************/
package org.jpox.samples.models.nightlabs_prices;

public class FormulaCell
{
    // primary key - begin
    private String organisationID;
    private long priceConfigID;
    private long formulaID;
    // primary key - end

    /**
     * The {@link FormulaPriceConfig} to which this instance of FormulaCell belongs. This is never <code>null</code>.
     */
    private FormulaPriceConfig priceConfig;

    /**
     * If this <code>FormulaCell</code> is used in the collection {@link FormulaPriceConfig#getFormulaCells()},
     * this field will point back to the {@link FormulaPriceConfig} - otherwise it's <code>null</code>.
     */
    private FormulaPriceConfig collectionOwner;

    // Default constructor only used by JDO - normal code will use the alternative constructor below.
    protected FormulaCell()
    {
    }

    public FormulaCell(FormulaPriceConfig priceConfig, long formulaID)
    {
        this.priceConfig = priceConfig;
        this.organisationID = priceConfig.getOrganisationID();
        this.priceConfigID = priceConfig.getPriceConfigID();
        this.formulaID = formulaID;
    }

    public String getOrganisationID() {
        return organisationID;
    }
    public long getPriceConfigID() {
        return priceConfigID;
    }
    public long getFormulaID() {
        return formulaID;
    }

    public FormulaPriceConfig getPriceConfig() {
        return priceConfig;
    }
    public FormulaPriceConfig getCollectionOwner() {
        return collectionOwner;
    }
    public void setCollectionOwner(FormulaPriceConfig collectionOwner) {
        this.collectionOwner = collectionOwner;
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
        final FormulaCell other = (FormulaCell) obj;

        if (organisationID == null) {
            if (other.organisationID != null)
                return false;
        } else if (!organisationID.equals(other.organisationID))
            return false;

        if (formulaID != other.formulaID)
            return false;

        if (priceConfigID != other.priceConfigID)
            return false;

        return true;
    }
}
