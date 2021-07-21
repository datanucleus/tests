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
package org.datanucleus.samples.types.locale;

import java.util.Locale;

/**
 * Holder of Locale field(s) for testing persistence of that type.
 */
public class LocaleHolder
{
    private Locale locale;

    public LocaleHolder()
    {
        super();
    }

    /**
     * @return Returns the locale.
     */
    public Locale getLocale()
    {
        return locale;
    }
    /**
     * @param locale The locale to set.
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * Indicates whether some other object is "equal to" this one.  By comparing
     * against an original copy of the object, <code>compareTo()</code> can be
     * used to verify that the object has been written to a database and read
     * back correctly.
     *
     * @param obj the reference object with which to compare
     * @return <code>true</code> if this object is equal to the obj argument; <code>false</code> otherwise.
     */
    public boolean compareTo(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof LocaleHolder))
        {
            return false;
        }

        LocaleHolder w = (LocaleHolder)obj;
        return locale == null ? w.locale == null : locale.equals(w.locale);
    }

    /**
     * Returns a string representation for this object.  All of the field
     * values are included in the string for debugging purposes.
     *
     * @return  a string representation for this object.
     */
    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());
        s.append("  locale = ").append(locale);

        return s.toString();
    }
}