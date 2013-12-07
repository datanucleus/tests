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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FormulaPriceConfig
extends TariffPriceConfig
{
    private Set formulaCells = new HashSet();

    private FormulaCell fallbackFormulaCell;

    private Map packagingResultPriceConfigs = new HashMap();

    public void setPackagingResultPriceConfig(String innerProductTypePK, String packageProductTypePK, PriceConfig resultPriceConfig)
    {
        packagingResultPriceConfigs.put(innerProductTypePK+'-'+packageProductTypePK, resultPriceConfig);
    }
    public PriceConfig getPackagingResultPriceConfig(String innerProductTypePK, String packageProductTypePK, boolean throwExceptionIfNotExistent)
    {
        PriceConfig res = (PriceConfig) packagingResultPriceConfigs.get(innerProductTypePK+'-'+packageProductTypePK);
        if (throwExceptionIfNotExistent && res == null)
            throw new IllegalArgumentException("There is no PriceConfig registered as the result of the combination of the innerProductType \""+innerProductTypePK+"\" packaged in the packageProductType \""+packageProductTypePK+"\"!");
        return res;
    }

    protected FormulaPriceConfig()
    {
    }

    /**
     * @param organisationID
     * @param priceConfigID
     */
    public FormulaPriceConfig(String organisationID, long priceConfigID)
    {
        super(organisationID, priceConfigID);
    }

    public FormulaCell getFallbackFormulaCell() {
        return fallbackFormulaCell;
    }
    public void setFallbackFormulaCell(FormulaCell fallbackFormulaCell) {
        this.fallbackFormulaCell = fallbackFormulaCell;
    }

    public Set getFormulaCells() {
        return formulaCells;
    }
}
