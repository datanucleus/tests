/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.widget;


public class FloatWidget extends Widget
{
    /**
     * The minimum accuracy required of the storage and retrieval of float
     * values.
     * 1e-6 indicates that values must retain at least six significant digits
     * of accuracy.
     */
    public static final float FLOAT_EPSILON = 1e-5f;

    /**
     * The minimum accuracy required of the storage and retrieval of double
     * values.
     * 1e-14 indicates that values must retain at least 14 significant digits
     * of accuracy.
     */
    public static final double DOUBLE_EPSILON = 1e-14d;

    /**
     * The minimum float value we will try to store in the DB.
     * This is set to roughly half of the Java limit.
     */
    public static final float MIN_FLOAT_VALUE = 1e-22f;

    /**
     * The maximum float value we will try to store in the DB.
     * This is set to roughly half of the Java limit.
     */
    public static final float MAX_FLOAT_VALUE = 1e17f;

    /**
     * The minimum double value we will try to store in the DB.
     * This is set to roughly half of the Java limit.
     */
    public static final double MIN_DOUBLE_VALUE = 1e-62d;

    /**
     * The maximum double value we will try to store in the DB.
     * This is set to roughly half of the Java limit.
     */
    public static final double MAX_DOUBLE_VALUE = 1e62d;


    private float floatField;
    private Float floatObjField;
    private double doubleField;
    private Double doubleObjField;


    public FloatWidget()
    {
        super();
    }


    public float getFloatField()
    {
        return floatField;
    }


    public Float getFloatObjField()
    {
        return floatObjField;
    }


    public double getDoubleField()
    {
        return doubleField;
    }


    public Double getDoubleObjField()
    {
        return doubleObjField;
    }


    private float nextFloat()
    {
        float f;

        do
        {
            f = Float.intBitsToFloat(r.nextInt());
        } while (Float.isNaN(f) || Float.isInfinite(f) || f < MIN_FLOAT_VALUE || f > MAX_FLOAT_VALUE);

        return f;
    }


    private double nextDouble()
    {
        double d;

        do
        {
            d = Double.longBitsToDouble(r.nextLong());
        } while (Double.isNaN(d) || Double.isInfinite(d) || d < MIN_DOUBLE_VALUE || d > MAX_DOUBLE_VALUE);

        return d;
    }


    /**
     * Fills all of the object's fields with random data values.  Any non-
     * primitive fields (with the exception of <code>id</code>) will also be
     * assigned <code>null</code> on a random basis.
     */

    public void fillRandom()
    {
        super.fillRandom();

        floatField = nextFloat();
        floatObjField = nextNull() ? null : new Float(nextFloat());
        doubleField = nextDouble();
        doubleObjField = nextNull() ? null : new Double(nextDouble());
    }


    /**
     * Indicates whether some other object is "equal to" this one.  By comparing
     * against an original copy of the object, <code>compareTo()</code> can be
     * used to verify that the object has been written to a database and read
     * back correctly.
     *
     * @param   obj     the reference object with which to compare
     *
     * @return  <code>true</code> if this object is equal to the obj argument;
     *          <code>false</code> otherwise.
     */

    public boolean compareTo(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof FloatWidget) || !super.compareTo(obj))
            return false;

        FloatWidget w = (FloatWidget)obj;

        if (floatObjField == null) { if (w.floatObjField != null) return false; }
        else if (!approximates(floatObjField.floatValue(), w.floatObjField.floatValue())) return false;

        if (doubleObjField == null) { if (w.doubleObjField != null) return false; }
        else if (!approximates(doubleObjField.doubleValue(), w.doubleObjField.doubleValue())) return false;

        return approximates(floatField, w.floatField)
            && approximates(doubleField, w.doubleField);
    }


    public static boolean approximates(float x, float y)
    {
        return Math.abs(x - y) / Math.max(Math.max(Math.abs(x), Math.abs(y)), Float.MIN_VALUE) < FLOAT_EPSILON;
    }


    public static boolean approximates(double x, double y)
    {
        return Math.abs(x - y) / Math.max(Math.max(Math.abs(x), Math.abs(y)), Double.MIN_VALUE) < DOUBLE_EPSILON;
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

        s.append("  floatField = ").append(floatField);
        s.append('\n');
        s.append("  floatObjField = ").append(floatObjField);
        s.append('\n');
        s.append("  doubleField = ").append(doubleField);
        s.append('\n');
        s.append("  doubleObjField = ").append(doubleObjField);
        s.append('\n');

        return s.toString();
    }
}
