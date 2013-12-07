package org.datanucleus.samples.store;

/**
 * Definition of a music Compact Disc. Extends basic Product class.
 *
 * @version $Revision: 1.1 $
 **/
public class CompactDisc extends Product
{
    /**
     * Artist of the Compact Disc
     **/
    String  artist=null;

    /**
     * Title of the Compact Disc
     **/
    String  title=null;

    /**
     * Year of the Compact Disc
     **/
    int     year=-1;

    /**
     * Producer of the Compact Disc
     **/
    String  producer=null;

    /**
     * Publisher of the Compact Disc
     **/
    String  publisher=null;

    /**
     * Default constructor. 
     **/
    protected CompactDisc()
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
     **/
    public CompactDisc(String id,
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
                       String publisher)
    {
        super(id,name,description,image_url,currency,price,old_price,list_price,tax_percent,status_id);

        this.artist     = artist;
        this.title      = title;
        this.year       = year;
        this.producer   = producer;
        this.publisher  = publisher;
    }

    // ------------------------------- Accessors -------------------------------
 
    /**
     * Accessor for the artist of the Compact Disc
     * @return  Artist of the Compact Disc 
     **/
    public String   getArtist()
    {
        return artist;
    }

    /**
     * Accessor for the title of the Compact Disc
     * @return  Title of the Compact Disc 
     **/
    public String   getTitle()
    {
        return title;
    }

    /**
     * Accessor for the year of the Compact Disc
     * @return  Year of the Compact Disc 
     **/
    public int  getYear()
    {
        return year;
    }

    /**
     * Accessor for the producer of the Compact Disc
     * @return  Producer of the Compact Disc 
     **/
    public String   getProducer()
    {
        return producer;
    }

    /**
     * Accessor for the publisher of the Compact Disc
     * @return  Publisher of the Compact Disc 
     **/
    public String   getPublisher()
    {
        return publisher;
    }

    // ------------------------------- Mutators --------------------------------
    /**
     * Mutator for the artist of the Compact Disc
     * @param   artist  Artist of the Compact Disc 
     **/
    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    /**
     * Mutator for the title of the Compact Disc.
     * @param   title   Title of the Compact Disc. 
     **/
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Mutator for the year of the Compact Disc.
     * @param   year    Year of the Compact Disc. 
     **/
    public void setYear(int year)
    {
        this.year = year;
    }

    /**
     * Mutator for the producer of the Compact Disc.
     * @param   producer    Producer of the Compact Disc. 
     **/
    public void setProducer(String producer)
    {
        this.producer = producer;
    }

    /**
     * Mutator for the publisher of the Compact Disc.
     * @param   publisher   Publisher of the Compact Disc.
     **/
    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    /**
     * Utility to return the object as a string.
     * @return  Stringified version of this Compact Disc.
     **/
    public String   toString()
    {
        return "CompactDisc : " + artist + " - " + title + " [" + id + "]";
    }
}
