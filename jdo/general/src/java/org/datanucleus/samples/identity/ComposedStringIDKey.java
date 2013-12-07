/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.identity;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.datanucleus.samples.identity.ComposedStringIDKey;

/**
 * Sample application identity PK composed of 2 Strings.
 * @version $Revision: 1.1 $
 */
public class ComposedStringIDKey implements Serializable
{
    public String code;
    public String composed;

    /**
     *  Default constructor.
     */
    public ComposedStringIDKey ()
    {
    }

    /**
     *  String constructor.
     */
    public ComposedStringIDKey (String str) 
    {
        StringTokenizer toke = new StringTokenizer (str, "::");

        str = toke.nextToken ();
        this.code = str;
        str = toke.nextToken ();
        this.composed = str;
    }

    /**
     *  Implementation of equals method.
     */
    public boolean equals (Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof ComposedStringIDKey))
            return false;

        ComposedStringIDKey c = (ComposedStringIDKey)obj;

        return code.equals(c.code)
            && composed.equals(c.composed);
    }

    /**
     *  Implementation of hashCode method that supports the
     *  equals-hashCode contract.
     */
    public int hashCode ()
    {
        return this.code.hashCode() ^ this.composed.hashCode();
    }

    /**
     *  Implementation of toString that outputs this object id's
     *  primary key values. 
     */
    public String toString ()
    {
        return this.code + "::" + this.composed;
    }
}