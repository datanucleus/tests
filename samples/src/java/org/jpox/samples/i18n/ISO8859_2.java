/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.i18n;

/**
 * A simple class containing characters from ISO8859-2.
 * @version $Revision: 1.1 $
 */
public class ISO8859_2
{
    /**
     * Lowercase field name.
     **/
    protected String lowercase_\u00e7\u00eb\u010f\u00e9\u00fa=null;

    /**
     * Uppercase field name.
     **/
    protected String UPPERCASE_\u00c7\u00cb\u0110\u010e=null;

    /**
     * Lowercase field name.
     **/
    protected String \u00e7\u00eb\u010f\u00e9\u00fa_lowercase_prefix=null;

    /**
     * Uppercase field name.
     **/
    protected String \u00c7\u00cb\u0110\u010e_UPPERCASE_prefix=null;
    
    /**
     * Default constructor - for JDO.
     **/
    protected ISO8859_2()
    {
    }

    /**
     * Constructor.
     **/
    public ISO8859_2(String lower, String upper)
    {
        lowercase_\u00e7\u00eb\u010f\u00e9\u00fa = lower;
        UPPERCASE_\u00c7\u00cb\u0110\u010e = upper;
        \u00e7\u00eb\u010f\u00e9\u00fa_lowercase_prefix = lower;
        \u00c7\u00cb\u0110\u010e_UPPERCASE_prefix = upper;
    }

    public String getLowercase_\u00e7\u00eb\u010f\u00e9\u00fa()
    {
        return lowercase_\u00e7\u00eb\u010f\u00e9\u00fa;
    }

    public String getUPPERCASE_\u00c7\u00cb\u0110\u010e()
    {
        return UPPERCASE_\u00c7\u00cb\u0110\u010e;
    }

    public String get\u00e7\u00eb\u010f\u00e9\u00fa_lowercase_prefix()
    {
        return \u00e7\u00eb\u010f\u00e9\u00fa_lowercase_prefix;
    }

    public String get\u00c7\u00cb\u0110\u010e_UPPERCASE_prefix()
    {
        return \u00c7\u00cb\u0110\u010e_UPPERCASE_prefix;
    }
    public String toString()
    {
        StringBuffer str=new StringBuffer();

        str.append("lowercase_\u00e7\u00eb\u010f\u00e9\u00fa=" + lowercase_\u00e7\u00eb\u010f\u00e9\u00fa + "\nUPPERCASE_\u00c7\u00cb\u0110\u010e=" + UPPERCASE_\u00c7\u00cb\u0110\u010e);
        return str.toString();
    }
}