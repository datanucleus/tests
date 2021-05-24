/**********************************************************************
Copyright (c) 2017 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
     ...
 **********************************************************************/
package org.datanucleus.samples.annotations.cdi;

import javax.inject.Inject;
import jakarta.persistence.AttributeConverter;

/**
 * Converter using CDI to inject into it.
 */
public class EncryptorConverter implements AttributeConverter<String, String>
{
    @Inject
    Encryptor encryptor;

    /* (non-Javadoc)
     * @see jakarta.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(String attr)
    {
        if (encryptor != null)
        {
            return encryptor.getName() + "(" + attr + ")";
        }
        return attr;
    }

    /* (non-Javadoc)
     * @see jakarta.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public String convertToEntityAttribute(String data)
    {
        return data;
    }
}
