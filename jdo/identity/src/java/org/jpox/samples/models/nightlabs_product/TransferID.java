/*
 * This class has been auto-generated by XDoclet.
 * See http://xdoclet.sourceforge.net for details.
 *
 * Please DO NOT edit this file! Your changes will be lost
 * with the next run of XDoclet.
 */

package org.jpox.samples.models.nightlabs_product;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.StringTokenizer;

public class TransferID
implements Serializable
{
	private static final long serialVersionUID = -6212499158598058147L;
	/**
	 * The values of all fields are url encoded in UTF-8.
	 */
	public static final String ENCODING="UTF-8";

	protected static final String JDO_PREFIX = "jdo";
	protected static final char JDO_PREFIX_SEPARATOR = '/';
	protected static final char CLASS_SEPARATOR = '?';
	protected static final String EMPTYSTRING = "";
	protected static final String SEPARATORS_FOR_TOKENIZER = "/?=&";
	protected static final String SEPARATOR_KEY_VALUE = "=";
	protected static final String SEPARATOR_ENTRY = "&";	
	protected static final Byte NULLBYTE = Byte.valueOf((byte)0);
	protected static final Character NULLCHAR = Character.valueOf((char)0);

	/**
	 * The radix that is used for encoding/decoding field values of numeric IDs (byte, short, int, long).
	 */
	protected static final int RADIX = 36;

	/**
	 * Primary key field organisationID.
	 *
	 * See {@link org.nightlabs.jfire.transfer.Transfer#organisationID}.
	 */
	public java.lang.String organisationID;
	/**
	 * Primary key field transferTypeID.
	 *
	 * See {@link org.nightlabs.jfire.transfer.Transfer#transferTypeID}.
	 */
	public java.lang.String transferTypeID;
	/**
	 * Primary key field transferID.
	 *
	 * See {@link org.nightlabs.jfire.transfer.Transfer#transferID}.
	 */
	public long transferID;

	/**
	 * Create a new empty instance of TransferID.
	 */
	public TransferID()
	{
	}

	/**
	 * Create a new instance of TransferID.
	 * This is done by parsing the <code>keyStr</code> that has been created
	 * by {@link #toString()} and setting all fields to the values from the string.
	 * <p>
	 * This means, the following code will create a copy of this class:<br/><br/>
	 * <code>TransferID newTransferID = new TransferID(oldTransferID.toString());</code>
	 *
	 * @param keyStr A String formatted as "jdo/{className}?{field0}={value0}&amp;{field1}={value1}...&amp;{fieldN}={valueN}"
	 *	 where all values are url encoded using {@link #ENCODING}.
	 */
	public TransferID(String keyStr)
	throws ParseException, SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException,
			InstantiationException, InvocationTargetException
	{
		Class clazz = this.getClass();

		StringTokenizer st = new StringTokenizer(keyStr, SEPARATORS_FOR_TOKENIZER, true);
		String jdoPrefix = st.nextToken();
		if (!JDO_PREFIX.equals(jdoPrefix))
			throw new ParseException("keyStr \""+keyStr+"\" does not start with jdo prefix \""+JDO_PREFIX+"\"!", 0);
		if (!st.hasMoreTokens() || st.nextToken().charAt(0) != JDO_PREFIX_SEPARATOR)
			throw new ParseException("keyStr \""+keyStr+"\" is missing separator \""+JDO_PREFIX_SEPARATOR+"\" after jdo prefix!", 0);

		String className = st.nextToken();
		if (!className.equals(clazz.getName()))
			throw new ParseException("keyStr defines class \""+className+"\", but this is an instance of \""+clazz.getName()+"\"!", 0);

		if (!st.hasMoreTokens() || st.nextToken().charAt(0) != CLASS_SEPARATOR)
			throw new ParseException("keyStr \""+keyStr+"\" is missing separator \""+CLASS_SEPARATOR+"\" after class!", 0);

		while (st.hasMoreTokens()) {
			String key = st.nextToken();
			String valStr = EMPTYSTRING;
			if (st.hasMoreTokens()) {
				String sep = st.nextToken();
				if (!SEPARATOR_KEY_VALUE.equals(sep))
					throw new ParseException("Expected \""+SEPARATOR_KEY_VALUE+"\", but found \""+sep+"\"!", 0);

				if (st.hasMoreTokens()) {
					valStr = st.nextToken();
					if (SEPARATOR_ENTRY.equals(valStr)) {
						sep = valStr;
						valStr = EMPTYSTRING;
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
							throw new ParseException("Expected \""+SEPARATOR_ENTRY+"\", but found \""+sep+"\"!", 0);
					}
				} // if (!SEPARATOR_ENTRY.equals(val)) {
			} // if (st.hasMoreTokens()) {
			Field field = clazz.getField(key);
			Class fieldType = field.getType();
			if (valStr == null) {
				if (!fieldType.isPrimitive())
					field.set(this, null);
				else {
					if (boolean.class.isAssignableFrom(fieldType))
						field.set(this, Boolean.FALSE);
					if (char.class.isAssignableFrom(fieldType))
						field.set(this, NULLCHAR);
					if (byte.class.isAssignableFrom(fieldType) ||
							short.class.isAssignableFrom(fieldType) ||
							int.class.isAssignableFrom(fieldType) ||
							long.class.isAssignableFrom(fieldType) ||
							float.class.isAssignableFrom(fieldType) ||
							double.class.isAssignableFrom(fieldType))
						field.set(this, NULLBYTE);					
				}
			}
			else {
				Object val = null;
				if (String.class.isAssignableFrom(fieldType)) {
					val = valStr;
				}
				else if (boolean.class.isAssignableFrom(fieldType)) {
					val = Boolean.parseBoolean(valStr);
				}
				else if (char.class.isAssignableFrom(fieldType)) {
					val = Character.valueOf(valStr.charAt(0));
				}
				else if (byte.class.isAssignableFrom(fieldType)) {
					val = Byte.valueOf(Byte.parseByte(valStr, RADIX));
				}
				else if (short.class.isAssignableFrom(fieldType)) {
					val = Short.valueOf(Short.parseShort(valStr, RADIX));
				}
				else if (int.class.isAssignableFrom(fieldType)) {
					val = Integer.valueOf(Integer.parseInt(valStr, RADIX));
				}
				else if (long.class.isAssignableFrom(fieldType)) {
					val = Long.valueOf(Long.parseLong(valStr, RADIX));
				}
				else
					throw new IllegalArgumentException("Type "+fieldType.getName()+" of member "+key+" is not unsupported!");
				field.set(this, val);
			}
		}
	}

	protected static final String encodeFieldValue(Object object) throws UnsupportedEncodingException {
		return URLEncoder.encode(String.valueOf(object), ENCODING);
	}
	protected static final String encodeFieldValue(boolean val) throws UnsupportedEncodingException {
		return URLEncoder.encode(String.valueOf(val), ENCODING);
	}
	protected static final String encodeFieldValue(char val) throws UnsupportedEncodingException {
		return URLEncoder.encode(String.valueOf(val), ENCODING);
	}
	protected static final String encodeFieldValue(byte val) throws UnsupportedEncodingException {
		return URLEncoder.encode(Integer.toString(val, RADIX), ENCODING);
	}
	protected static final String encodeFieldValue(short val) throws UnsupportedEncodingException {
		return URLEncoder.encode(Integer.toString(val, RADIX), ENCODING);
	}
	protected static final String encodeFieldValue(int val) throws UnsupportedEncodingException {
		return URLEncoder.encode(Integer.toString(val, RADIX), ENCODING);
	}
	protected static final String encodeFieldValue(long val) throws UnsupportedEncodingException {
		return URLEncoder.encode(Long.toString(val, RADIX), ENCODING);
	}
	protected static final String encodeFieldValue(float val) throws UnsupportedEncodingException {
		return URLEncoder.encode(String.valueOf(val), ENCODING);
	}
	protected static final String encodeFieldValue(double val) throws UnsupportedEncodingException {
		return URLEncoder.encode(String.valueOf(val), ENCODING);
	}

	/**
	 * Create a string representation of this object id.
	 * <p>
	 * JDO expects the result of this method to be compatible with the constructor
	 * {@link #TransferID(String)}.
	 * This method takes all the primary-key-fields and encodes them with their name
	 * and their value.
	 *
	 * @return a string representation of this object id.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(JDO_PREFIX);
		sb.append(JDO_PREFIX_SEPARATOR);
		sb.append(this.getClass().getName());
		sb.append(CLASS_SEPARATOR);

		try {
		 sb.append("organisationID=");
		 sb.append(encodeFieldValue(organisationID));
		 sb.append('&');
		 sb.append("transferTypeID=");
		 sb.append(encodeFieldValue(transferTypeID));
		 sb.append('&');
		 sb.append("transferID=");
		 sb.append(encodeFieldValue(transferID));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Why the hell goes encoding with " + ENCODING + " wrong?!", e);
		}
		return sb.toString();
	}

	/**
	 * Compare all primary key fields (according to the JDO spec).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return <code>true</code> if all primary key fields are equal - <code>false</code> otherwise.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (!(obj instanceof TransferID))
			return false;

		TransferID other = (TransferID)obj;

		return
					(
						(this.organisationID == other.organisationID) ||
						(this.organisationID != null && this.organisationID.equals(other.organisationID))
					)
					&&
					(
						(this.transferTypeID == other.transferTypeID) ||
						(this.transferTypeID != null && this.transferTypeID.equals(other.transferTypeID))
					)
					&&
					this.transferID == other.transferID
			;
	}

	protected static final int primitiveHashCode(boolean val) {
		return val ? 1231 : 1237;
	}

	protected static final int primitiveHashCode(char val) {
		return val;
	}

	protected static final int primitiveHashCode(byte val) {
		return val;
	}

	protected static final int primitiveHashCode(short val) {
		return val;
	}

	protected static final int primitiveHashCode(int val) {
		return val;
	}

	protected static final int primitiveHashCode(long val) {
		return (int)(val ^ (val >>> 32));
	}

	protected static final int primitiveHashCode(float val) {
		return Float.floatToIntBits(val);
	}

	protected static final int primitiveHashCode(double val) {
		long bits = Double.doubleToLongBits(val);
		return (int)(bits ^ (bits >>> 32));
	}

	/**
	 * Returns a hash code for this object id. The hash code for a
	 * object id object is computed by generating hash codes for
	 * all primary key fields and XOR'ing them.
	 *
	 * @return  a hash code value for this object.
	 */
	public int hashCode()
	{
		return
					(organisationID == null ? 0 : organisationID.hashCode()) // Normally, it should never be null, but still it just happened to me! Marco.
					^
					(transferTypeID == null ? 0 : transferTypeID.hashCode()) // Normally, it should never be null, but still it just happened to me! Marco.
					^
					primitiveHashCode(transferID)
			;
	}

	/**
	 * Create a new object id instance.
	 *
	 * @param organisationID The primary key field organisationID. See {@link org.nightlabs.jfire.transfer.Transfer#organisationID}.
	 * @param transferTypeID The primary key field transferTypeID. See {@link org.nightlabs.jfire.transfer.Transfer#transferTypeID}.
	 * @param transferID The primary key field transferID. See {@link org.nightlabs.jfire.transfer.Transfer#transferID}.
	 * @return a newly created instance of <code>TransferID</code>
	 *		with the primary-key fields set to the given parameters.
	 */
	public static TransferID create(
				java.lang.String organisationID,
				java.lang.String transferTypeID,
				long transferID
		)
	{
		TransferID n = new TransferID();
		n.organisationID = organisationID;
		n.transferTypeID = transferTypeID;
		n.transferID = transferID;
		return n;
	}

}
