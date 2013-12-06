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
 * A simple class containing characters from ISO8859-1 (Latin-1).
 * This test is really for the non-English characters ... those with accents
 * etc often found in Spanish, French etc.
 * @version $Revision: 1.1 $
 */
public class ISO8859_1
{
    /**
     * lowercase field name.
     **/
    protected String lowercase=null;

    /**
     * UPPERCASE field name.
     **/
    protected String UPPERCASE=null;

    /**
     * Numeral field name.
     **/
    protected int numeral_12345=0;

    /**
     * Accented field name.
     **/
    protected String accented_·…Ì”˙Ò=null;

    /**
     * Accented field name.
     **/
    protected String Ì”˙Ò_accented_prefix=null;
    
    /**
     * Default constructor - for JDO.
     **/
    protected ISO8859_1()
    {
    }

    /**
     * Constructor.
     **/
    public ISO8859_1(String lower,
                     String upper,
                     int numeral,
                     String accented)
    {
        lowercase = lower;
        UPPERCASE = upper;
        numeral_12345 = numeral;
        accented_·…Ì”˙Ò = accented;
        Ì”˙Ò_accented_prefix = accented;
    }

    public String getLowercase()
    {
        return lowercase;
    }

    public String getUPPERCASE()
    {
        return UPPERCASE;
    }

    public int getNumeral_12345()
    {
        return numeral_12345;
    }

    public String getAccented_·…Ì”˙Ò()
    {
        return accented_·…Ì”˙Ò;
    }

    public String getÕ”˙Ò_accented_prefix()
    {
        return Ì”˙Ò_accented_prefix;
    }
    
    public String toString()
    {
        StringBuffer str=new StringBuffer();

        str.append("lowercase=" + lowercase + "\nUPPERCASE=" + UPPERCASE + "\nnumeral_12345=" + numeral_12345 + "\naccented_·…Ì”˙Ò=" + accented_·…Ì”˙Ò);
        return str.toString();
    }
}