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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.jdo.AttributeConverter;

/**
 * Converter to convert a Map<String,String> to String.
 * Saves the map as <pre>{key1:val1},{key2:val2},...</pre>
 */
public class MapToStringConverter implements AttributeConverter<Map<String,String>, String>
{
    /* (non-Javadoc)
     * @see javax.jdo.AttributeConverter#convertToDatastore(java.lang.Object)
     */
    @Override
    public String convertToDatastore(Map<String,String> attribute)
    {
        if (attribute == null)
        {
            return null;
        }

        StringBuilder str = new StringBuilder();
        Iterator<Map.Entry<String, String>> entryIter = attribute.entrySet().iterator();
        while (entryIter.hasNext())
        {
            Map.Entry<String, String> entry = entryIter.next();
            if (str.length() > 0)
            {
                str.append(',');
            }

            str.append('{').append(entry.getKey()).append(':').append(entry.getValue()).append('}');
        }
        return str.toString();
    }

    /* (non-Javadoc)
     * @see javax.jdo.AttributeConverter#convertToAttribute(java.lang.Object)
     */
    @Override
    public Map<String,String> convertToAttribute(String columnValue)
    {
        if (columnValue == null)
        {
            return null;
        }

        Map<String,String> map = new HashMap<String,String>();
        StringTokenizer tokeniser1 = new StringTokenizer(columnValue, ",");
        while (tokeniser1.hasMoreTokens())
        {
            // Extract the "{key1:val1}"
            String token = tokeniser1.nextToken();
            int openBrace = token.indexOf('{');
            int colon = token.indexOf(':');
            int closeBrace = token.indexOf('}');
            if (openBrace < 0 || closeBrace < 0 || colon < 0)
            {
                throw new RuntimeException("Invalid contents for converter : " + token);
            }
            String key = token.substring(openBrace+1,colon);
            String val = token.substring(colon+1,closeBrace);
            map.put(key, val);
        }

        return map;
    }
}
