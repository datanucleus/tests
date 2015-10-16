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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SetMyType1StringConverter implements AttributeConverter<Set<MyType1>, String>
{
    /* (non-Javadoc)
     * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(Set<MyType1> attribute)
    {
        if (attribute == null)
        {
            return null;
        }

        StringBuilder str = new StringBuilder();
        for (MyType1 type1 : attribute)
        {
            if (str.length() > 0)
            {
                str.append('#');
            }
            str.append(type1.getName1()).append(':').append(type1.getName2());
        }
        return str.toString();
    }

    /* (non-Javadoc)
     * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public Set<MyType1> convertToEntityAttribute(String column)
    {
        if (column == null)
        {
            return null;
        }

        Set<MyType1> set = new HashSet<MyType1>();
        StringTokenizer tokeniser1 = new StringTokenizer(column, "#");
        while (tokeniser1.hasMoreTokens())
        {
            String token = tokeniser1.nextToken();

            StringTokenizer tokeniser2 = new StringTokenizer(token, ":");
            String name1 = tokeniser2.nextToken();
            String name2 = (tokeniser2.hasMoreTokens() ? tokeniser2.nextToken() : null);
            set.add(new MyType1(name1, name2));
        }

        return set;
    }
}