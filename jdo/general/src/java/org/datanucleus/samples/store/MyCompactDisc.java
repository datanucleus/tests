package org.datanucleus.samples.store;

/**
 * Extension of CompactDisc to add on its location.
 *
 * @version $Revision: 1.1 $
 **/
public class MyCompactDisc extends CompactDisc
{
    /**
     * Location of the Compact Disc
     **/
    String  location=null;

    /**
     * Default constructor. */
    protected MyCompactDisc()
    {
        super();
    }

    /**
     * Constructor.
     * @param   id          id of product
     * @param   name        name of product
     * @param   description description of product
     * @param   image_url   URL of image of product
     * @param   currency    Currency of product price
     * @param   price       Price of product
     * @param   old_price   Old price of product
     * @param   list_price  List price of product
     * @param   tax_percent Tax percentage on this product
     * @param   status_id   Status id of this product
     * @param   artist      Artist name of the Compact Disc
     * @param   title       Title of the Compact Disc
     * @param   year        Year of the Compact Disc
     * @param   producer    Producer of the Compact Disc
     * @param   publisher   Publisher of the Compact Disc 
     * @param   location    Location of the disc
     **/
    public MyCompactDisc(String id,
                       String name,
                       String description,
                       String image_url,
                       String currency,
                       double price,
                       double old_price,
                       double list_price,
                       double tax_percent,
                       int status_id,
                       String artist,
                       String title,
                       int year,
                       String producer,
                       String publisher,
                       String location)
    {
        super(id,name,description,image_url,currency,price,old_price,list_price,tax_percent,status_id,artist,title,year,producer,publisher);
        this.location = location;
    }

    // ------------------------------- Accessors -------------------------------
 
    /**
     * Accessor for the location of the Compact Disc
     * @return  Location of the Compact Disc
     **/
    public String   getLocation()
    {
        return location;
    }

    // ------------------------------- Mutators --------------------------------
 
    /**
     * Mutator for the location of the Compact Disc
     * @param   location  Location of the Compact Disc 
     **/
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * Utility to return the object as a string.
     * @return  Stringified version of this Compact Disc. 
     **/
    public String   toString()
    {
        return "MyCompactDisc : " + artist + " - " + title + " : " + location + " [" + id + "]";
    }
}
