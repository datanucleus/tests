/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.typeconversion;

import java.util.StringTokenizer;

import javax.persistence.AttributeConverter;

/**
 * Simple converter to String for a ComplicatedType.
 */
public class ComplicatedTypeConverter implements AttributeConverter<ComplicatedType, String>
{
    /* (non-Javadoc)
     * @see javax.persistence.jpa21.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    public String convertToDatabaseColumn(ComplicatedType comp)
    {
        return comp.getName1() + ":" + comp.getName2();
    }

    /* (non-Javadoc)
     * @see javax.persistence.jpa21.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    public ComplicatedType convertToEntityAttribute(String str)
    {
        StringTokenizer tokeniser = new StringTokenizer(str, "::");
        String name1 = tokeniser.nextToken();
        String name2 = tokeniser.nextToken();
        return new ComplicatedType(name1, name2);
    }
}
