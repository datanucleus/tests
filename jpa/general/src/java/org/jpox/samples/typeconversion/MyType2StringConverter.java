/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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
import javax.persistence.Converter;

@Converter
public class MyType2StringConverter implements AttributeConverter<MyType2, String>
{
    /* (non-Javadoc)
     * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(MyType2 attribute)
    {
        if (attribute == null)
        {
            return null;
        }
        return attribute.getName1() + ":" + attribute.getVal2();
    }

    /* (non-Javadoc)
     * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public MyType2 convertToEntityAttribute(String column)
    {
        if (column == null)
        {
            return null;
        }
        StringTokenizer tokeniser = new StringTokenizer(column, ":");
        String name1 = tokeniser.nextToken();
        String name2 = (tokeniser.hasMoreTokens() ? tokeniser.nextToken() : null);
        long val2 = 0;
        try
        {
            val2 = Long.valueOf(name2);
        }
        catch (NumberFormatException nfe)
        {
        }
        return new MyType2(name1, val2);
    }
}