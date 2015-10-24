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
package org.datanucleus.samples.types.converters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jdo.AttributeConverter;

/**
 * Converter to convert a Collection<String> to String.
 */
public class CollectionToStringConverter implements AttributeConverter<Collection<String>, String>
{
    /* (non-Javadoc)
     * @see javax.jdo.AttributeConverter#convertToDatastore(java.lang.Object)
     */
    @Override
    public String convertToDatastore(Collection<String> attribute)
    {
        if (attribute == null)
        {
            return null;
        }

        StringBuilder str = new StringBuilder();
        for (String elem : attribute)
        {
            if (str.length() > 0)
            {
                str.append('#');
            }
            str.append(elem);
        }
        return str.toString();
    }

    /* (non-Javadoc)
     * @see javax.jdo.AttributeConverter#convertToAttribute(java.lang.Object)
     */
    @Override
    public Collection<String> convertToAttribute(String columnValue)
    {
        if (columnValue == null)
        {
            return null;
        }

        Set<String> set = new HashSet<String>();
        StringTokenizer tokeniser1 = new StringTokenizer(columnValue, "#");
        while (tokeniser1.hasMoreTokens())
        {
            String token = tokeniser1.nextToken();
            set.add(token);
        }

        return set;
    }
}
