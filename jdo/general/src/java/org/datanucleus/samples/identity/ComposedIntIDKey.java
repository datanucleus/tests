/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.identity;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.datanucleus.samples.identity.ComposedIntIDKey;

/**
 * Sample application identity PK composed of 2 ints.
 * @version $Revision: 1.1 $
 */
public class ComposedIntIDKey implements Serializable
{
    public int code;
    public int composed;

    /**
     *  Default constructor.
     */
    public ComposedIntIDKey ()
    {
    }

    /**
     *  String constructor.
     */
    public ComposedIntIDKey (String str) 
    {
        StringTokenizer toke = new StringTokenizer (str, "::");

        str = toke.nextToken ();
        this.code = Integer.parseInt (str);
        str = toke.nextToken ();
        this.composed = Integer.parseInt (str);
    }

    /**
     *  Implementation of equals method.
     */
    public boolean equals (Object ob)
    {
        if (this == ob)
        {
            return true;
        }
        if (!(ob instanceof ComposedIntIDKey))
        {
            return false;
        }

        ComposedIntIDKey other = (ComposedIntIDKey) ob;
        return ((this.code == other.code) && (this.composed == other.composed));
    }

    /**
     *  Implementation of hashCode method that supports the
     *  equals-hashCode contract.
     */
    public int hashCode ()
    {
        return this.code ^ this.composed;
    }

    /**
     *  Implementation of toString that outputs this object id's
     *  primary key values. 
     */
    public String toString ()
    {
        return String.valueOf (this.code)
            + "::" + String.valueOf (this.composed);
    }
}