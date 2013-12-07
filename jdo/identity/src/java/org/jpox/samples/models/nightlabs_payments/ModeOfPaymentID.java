/**********************************************************************
Copyright (c) 2006 Marco Schulze and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributions
    ...
 ***********************************************************************/
package org.jpox.samples.models.nightlabs_payments;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.StringTokenizer;

/**
 * This JDO objectid-class has been created by XDoclet for {@link org.nightlabs.test.ModeOfPayment}.
 */
public class ModeOfPaymentID implements Serializable
{
    public static final String ENCODING = "UTF-8";
    protected static final String JDO_PREFIX = "jdo";
    protected static final char JDO_PREFIX_SEPARATOR = '/';
    protected static final char CLASS_SEPARATOR = '?';
    protected static final String EMPTYSTRING = "";
    protected static final String SEPARATORS_FOR_TOKENIZER = "/?=&";
    protected static final String SEPARATOR_KEY_VALUE = "=";
    protected static final String SEPARATOR_ENTRY = "&";
    protected static final Byte NULLBYTE = new Byte((byte) 0);
    protected static final Character NULLCHAR = new Character((char) 0);

    public java.lang.String modeOfPaymentID;
    public java.lang.String organisationID;

    public ModeOfPaymentID()
    {
    }

    /**
     * This constructor creates a new instance of ModeOfPaymentID by parsing the <code>keyStr</code> that has been
     * created by {@link #toString()} and setting all fields to the values from the string.
     * <p>
     * This means, the following code will create a copy of this class:<br/><br/>
     * <code>ModeOfPaymentID newModeOfPaymentID = new ModeOfPaymentID(oldModeOfPaymentID.toString());</code>
     * @param keyStr A String formatted as "jdo/{className}?{field0}={value0}&{field1}={value1}...&{fieldN}={valueN}"
     * where all values are url encoded using {@link #ENCODING}.
     */
    public ModeOfPaymentID(String keyStr) throws ParseException, SecurityException, NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException
    {
        Class clazz = this.getClass();

        StringTokenizer st = new StringTokenizer(keyStr, SEPARATORS_FOR_TOKENIZER, true);
        String jdoPrefix = st.nextToken();
        if (!JDO_PREFIX.equals(jdoPrefix))
            throw new ParseException("keyStr \"" + keyStr + "\" does not start with jdo prefix \"" + JDO_PREFIX + "\"!", 0);
        if (!st.hasMoreTokens() || st.nextToken().charAt(0) != JDO_PREFIX_SEPARATOR)
            throw new ParseException("keyStr \"" + keyStr + "\" is missing separator \"" + JDO_PREFIX_SEPARATOR + "\" after jdo prefix!", 0);

        String className = st.nextToken();
        if (!className.equals(clazz.getName()))
            throw new ParseException("keyStr defines class \"" + className + "\", but this is an instance of \"" + clazz.getName() + "\"!",
                    0);

        if (!st.hasMoreTokens() || st.nextToken().charAt(0) != CLASS_SEPARATOR)
            throw new ParseException("keyStr \"" + keyStr + "\" is missing separator \"" + CLASS_SEPARATOR + "\" after class!", 0);

        while (st.hasMoreTokens())
        {
            String key = st.nextToken();
            String valStr = EMPTYSTRING;
            if (st.hasMoreTokens())
            {
                String sep = st.nextToken();
                if (!SEPARATOR_KEY_VALUE.equals(sep))
                    throw new ParseException("Expected \"" + SEPARATOR_KEY_VALUE + "\", but found \"" + sep + "\"!", 0);

                if (st.hasMoreTokens())
                {
                    valStr = st.nextToken();
                    if (SEPARATOR_ENTRY.equals(valStr))
                    {
                        sep = valStr;
                        valStr = EMPTYSTRING;
                    }
                    else
                        try
                        {
                            valStr = URLDecoder.decode(valStr, ENCODING);
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                }
                if (!SEPARATOR_ENTRY.equals(sep))
                {
                    if (st.hasMoreTokens())
                    {
                        sep = st.nextToken();
                        if (!SEPARATOR_ENTRY.equals(sep))
                            throw new ParseException("Expected \"" + SEPARATOR_ENTRY + "\", but found \"" + sep + "\"!", 0);
                    }
                } // if (!SEPARATOR_ENTRY.equals(val)) {
            } // if (st.hasMoreTokens()) {
            Field field = clazz.getField(key);
            Class fieldType = field.getType();
            if (valStr == null)
            {
                if (!fieldType.isPrimitive())
                    field.set(this, null);
                else
                {
                    if (boolean.class.isAssignableFrom(fieldType))
                        field.set(this, Boolean.FALSE);
                    if (char.class.isAssignableFrom(fieldType))
                        field.set(this, NULLCHAR);
                    if (byte.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType) || float.class
                            .isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType))
                        field.set(this, NULLBYTE);
                }
            }
            else
            {
                Object val = null;
                if (String.class.isAssignableFrom(fieldType))
                {
                    val = valStr;
                }
                else if (Number.class.isAssignableFrom(fieldType))
                {
                    Constructor c = fieldType.getConstructor(new Class[]{String.class});
                    val = c.newInstance(new Object[]{valStr});
                }
                else if (boolean.class.isAssignableFrom(fieldType))
                {
                    val = new Boolean(valStr);
                }
                else if (char.class.isAssignableFrom(fieldType))
                {
                    val = new Character(valStr.charAt(0));
                }
                else if (byte.class.isAssignableFrom(fieldType))
                {
                    val = new Byte(valStr);
                }
                else if (int.class.isAssignableFrom(fieldType))
                {
                    val = new Integer(valStr);
                }
                else if (long.class.isAssignableFrom(fieldType))
                {
                    val = new Long(valStr);
                }
                else
                    throw new IllegalArgumentException("Type " + fieldType.getName() + " of member " + key + " is not unsupported!");
                field.set(this, val);
            }
        }
    }

    /**
     * JDO expects the result of this method to be compatible with the constructor {@link #ModeOfPaymentID(String)}.
     * This method takes all the primary-key-fields and encodes them with their name and their value.
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer(JDO_PREFIX);
        sb.append(JDO_PREFIX_SEPARATOR);
        sb.append(this.getClass().getName());
        sb.append(CLASS_SEPARATOR);

        try
        {
            sb.append("modeOfPaymentID=");
            sb.append(URLEncoder.encode(String.valueOf(modeOfPaymentID), ENCODING));
            sb.append('&');
            sb.append("organisationID=");
            sb.append(URLEncoder.encode(String.valueOf(organisationID), ENCODING));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Why the hell goes encoding with " + ENCODING + " wrong?!", e);
        }
        return sb.toString();
    }

    /**
     * This method compares all primary key fields (according to the JDO spec).
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof ModeOfPaymentID))
            return false;

        ModeOfPaymentID other = (ModeOfPaymentID) obj;

        return ((this.modeOfPaymentID == other.modeOfPaymentID) || (this.modeOfPaymentID != null && this.modeOfPaymentID
                .equals(other.modeOfPaymentID))) && ((this.organisationID == other.organisationID) || (this.organisationID != null && this.organisationID
                .equals(other.organisationID)));
    }

    protected static int primitiveHashCode(boolean val)
    {
        return val ? 1231 : 1237;
    }

    protected static int primitiveHashCode(byte val)
    {
        return val;
    }

    protected static int primitiveHashCode(char val)
    {
        return val;
    }

    protected static int primitiveHashCode(double val)
    {
        long bits = Double.doubleToLongBits(val);
        return (int) (bits ^ (bits >>> 32));
    }

    protected static int primitiveHashCode(float val)
    {
        return Float.floatToIntBits(val);
    }

    protected static int primitiveHashCode(int val)
    {
        return val;
    }

    protected static int primitiveHashCode(long val)
    {
        return (int) (val ^ (val >>> 32));
    }

    protected static int primitiveHashCode(short val)
    {
        return val;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return (modeOfPaymentID == null ? 0 : modeOfPaymentID.hashCode()) ^ (organisationID == null ? 0 : organisationID.hashCode());
    }

    /**
     * @return Returns a newly created instance of <code>ModeOfPaymentID</code>
     *		with the primary-key fields set to the given parameters.
     */
    public static ModeOfPaymentID create(java.lang.String modeOfPaymentID, java.lang.String organisationID)
    {
        ModeOfPaymentID n = new ModeOfPaymentID();
        n.modeOfPaymentID = modeOfPaymentID;
        n.organisationID = organisationID;
        return n;
    }
}