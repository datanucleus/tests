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
package org.datanucleus.samples.annotations.query;

/**
 * Simple query result class holding fields of different types for use in queries.
 */
public class QueryTypeResult
{
    double doubleValue = 0.0;
    
    long longValue = 0;
    
    String stringValue = null;

    public QueryTypeResult(double dbl, long lng, String str)
    {
        this.longValue = lng;
        this.doubleValue = dbl;
        this.stringValue = str;
    }

    public double getDoubleValue()
    {
        return doubleValue;
    }
    public long getLongValue()
    {
        return longValue;
    }
    public String getStringValue()
    {
        return stringValue;
    }
}