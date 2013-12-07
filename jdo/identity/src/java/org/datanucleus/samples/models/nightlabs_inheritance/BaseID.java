package org.datanucleus.samples.models.nightlabs_inheritance;

import java.io.Serializable;
import java.text.ParseException;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;

public class BaseID implements Serializable
{
    private static final long serialVersionUID = -9062695677416098225L;
    public static final String ENCODING = "UTF-8";
    private static final String JDO_PREFIX = "jdo";
    private static final char JDO_PREFIX_SEPARATOR = '/';
    private static final char CLASS_SEPARATOR = '?';
    private static final String SEPARATORS_FOR_TOKENIZER = "/?=&";
    private static final String SEPARATOR_KEY_VALUE = "=";
    private static final String SEPARATOR_ENTRY = "&";
    private static final int RADIX = 36;

    public java.lang.String organisationID;
    public java.lang.String configModuleClassName;
    public java.lang.String configModuleInitialiserID;
    
    public BaseID()
    {
    }

    public BaseID(final String keyStr)
    throws ParseException, SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException,
            InstantiationException, InvocationTargetException
    {
        Class<? extends BaseID> clazz = this.getClass();

        StringTokenizer st = new StringTokenizer(keyStr, SEPARATORS_FOR_TOKENIZER, true);
        String jdoPrefix = st.nextToken();
        if (!JDO_PREFIX.equals(jdoPrefix))
            throw new ParseException(
                    "keyStr \""+ //$NON-NLS-1$
                    keyStr+
                    "\" does not start with jdo prefix \""+ //$NON-NLS-1$
                    JDO_PREFIX+
                    "\"!", 0); //$NON-NLS-1$
        if (!st.hasMoreTokens() || st.nextToken().charAt(0) != JDO_PREFIX_SEPARATOR)
            throw new ParseException(
                    "keyStr \""+ //$NON-NLS-1$
                    keyStr+
                    "\" is missing separator \""+ //$NON-NLS-1$
                    JDO_PREFIX_SEPARATOR+
                    "\" after jdo prefix!", 0); //$NON-NLS-1$

        String className = st.nextToken();
        if (!className.equals(clazz.getName()))
            throw new ParseException(
                    "keyStr defines class \""+ //$NON-NLS-1$
                    className+
                    "\", but this is an instance of \""+ //$NON-NLS-1$
                    clazz.getName()+
                    "\"!", 0); //$NON-NLS-1$

        if (!st.hasMoreTokens() || st.nextToken().charAt(0) != CLASS_SEPARATOR)
            throw new ParseException(
                    "keyStr \""+ //$NON-NLS-1$
                    keyStr+
                    "\" is missing separator \""+ //$NON-NLS-1$
                    CLASS_SEPARATOR+
                    "\" after class!", 0); //$NON-NLS-1$

        while (st.hasMoreTokens()) {
            String key = st.nextToken();
            String valStr = ""; //$NON-NLS-1$
            if (st.hasMoreTokens()) {
                String sep = st.nextToken();
                if (!SEPARATOR_KEY_VALUE.equals(sep))
                    throw new ParseException(
                            "Expected \""+ //$NON-NLS-1$
                            SEPARATOR_KEY_VALUE+
                            "\", but found \""+ //$NON-NLS-1$
                            sep+
                            "\"!", 0); //$NON-NLS-1$

                if (st.hasMoreTokens()) {
                    valStr = st.nextToken();
                    if (SEPARATOR_ENTRY.equals(valStr)) {
                        sep = valStr;
                        valStr = ""; //$NON-NLS-1$
                    }
                    else
                        try {                   
                            valStr = URLDecoder.decode(valStr, ENCODING);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                }
                if (!SEPARATOR_ENTRY.equals(sep)) {
                    if (st.hasMoreTokens()) {
                        sep = st.nextToken();
                        if (!SEPARATOR_ENTRY.equals(sep))
                            throw new ParseException(
                                    "Expected \""+ //$NON-NLS-1$
                                    SEPARATOR_ENTRY+
                                    "\", but found \""+ //$NON-NLS-1$
                                    sep+"\"!", 0); //$NON-NLS-1$
                    }
                } // if (!SEPARATOR_ENTRY.equals(val)) {
            } // if (st.hasMoreTokens()) {
            Field field = clazz.getField(key);
            Class<?> fieldType = field.getType();
            if (valStr == null) {
                if (!fieldType.isPrimitive())
                    field.set(this, null);
                else {
                    if (boolean.class.isAssignableFrom(fieldType))
                        field.set(this, Boolean.FALSE);
                    else if (char.class.isAssignableFrom(fieldType))
                        field.set(this, Character.valueOf((char)0));
                    else // for all other primitives - i.e. byte, short, int, long, float, double
                        field.set(this, Byte.valueOf((byte)0));                 
                }
            }
            else {
                Object val = null;
                if (String.class.isAssignableFrom(fieldType))
                    val = valStr;
                else if (boolean.class.isAssignableFrom(fieldType))
                    val = Boolean.valueOf(valStr);
                else if (char.class.isAssignableFrom(fieldType))
                    val = Character.valueOf(valStr.charAt(0));
                else if (byte.class.isAssignableFrom(fieldType))
                    val = Byte.valueOf(valStr, RADIX);
                else if (short.class.isAssignableFrom(fieldType))
                    val = Short.valueOf(valStr, RADIX);
                else if (int.class.isAssignableFrom(fieldType))
                    val = Integer.valueOf(valStr, RADIX);
                else if (long.class.isAssignableFrom(fieldType))
                    val = Long.valueOf(valStr, RADIX);
                else
                    throw new IllegalArgumentException(
                            "Type "+ //$NON-NLS-1$
                            fieldType.getName()+
                            " of member "+ //$NON-NLS-1$
                            key+
                            " is not unsupported!"); //$NON-NLS-1$
                field.set(this, val);
            }
        }
    }

    public String toString() 
    {
        StringBuffer sb = new StringBuffer(JDO_PREFIX);
        sb.append(JDO_PREFIX_SEPARATOR);
        sb.append(this.getClass().getName());
        sb.append(CLASS_SEPARATOR);

        try {
            sb.append("organisationID="); //$NON-NLS-1$
            sb.append(URLEncoder.encode(String.valueOf(organisationID), ENCODING));
            sb.append('&');
            sb.append("configModuleClassName="); //$NON-NLS-1$
            sb.append(URLEncoder.encode(String.valueOf(configModuleClassName), ENCODING));
            sb.append('&');
            sb.append("configModuleInitialiserID="); //$NON-NLS-1$
            sb.append(URLEncoder.encode(String.valueOf(configModuleInitialiserID), ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(
                    "Encoding failed with encoding " + //$NON-NLS-1$ 
                    ENCODING, e);
        }
        return sb.toString();
    }

    /**
     * Compare all primary key fields (according to the JDO spec).
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if all primary key fields are equal - <code>false</code> otherwise.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        BaseID other = (BaseID) obj;
        if(organisationID == null) {
            if(other.organisationID != null)
                return false;
        } else if(!organisationID.equals(other.organisationID))
            return false;
        if(configModuleClassName == null) {
            if(other.configModuleClassName != null)
                return false;
        } else if(!configModuleClassName.equals(other.configModuleClassName))
            return false;
        if(configModuleInitialiserID == null) {
            if(other.configModuleInitialiserID != null)
                return false;
        } else if(!configModuleInitialiserID.equals(other.configModuleInitialiserID))
            return false;
        return true;
    }

    /**
     * Returns a hash code for this object id. The hash code for a
     * object id object is computed by combining the hash codes of
     * all primary key fields.
     * @return a hash code for this object.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((organisationID == null) ? 0 : organisationID.hashCode());
        result = prime * result + ((configModuleClassName == null) ? 0 : configModuleClassName.hashCode());
        result = prime * result + ((configModuleInitialiserID == null) ? 0 : configModuleInitialiserID.hashCode());
        return result;
    }

    public static BaseID create(String organisationID, String configModuleClassName, String configModuleInitialiserID)
    {
        BaseID n = new BaseID();
        n.organisationID = organisationID;
        n.configModuleClassName = configModuleClassName;
        n.configModuleInitialiserID = configModuleInitialiserID;
        return n;
    }
}
