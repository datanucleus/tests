/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.types.enums;

/**
 * Different representation of colour where we specify different ordinal values.
 */
public enum AlternativeColour 
{
    RED((short)1), GREEN((short)3), BLUE((short)5), YELLOW((short)8);

    private short value;

    private AlternativeColour(short value)
    {
            this.value = value;
    }

    public short getValue() 
    {
            return value;
    }

    public static AlternativeColour getEnumByValue(short value)
    {
        switch (value)
        {
            case 1:
                return RED;
            case 3:
                return GREEN;
            case 5:
                return BLUE;
            default:
                return YELLOW;
        }
    }
}