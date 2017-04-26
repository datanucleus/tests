package org.datanucleus.samples.store;

/**
 * Definition of a music Compact Disc. Extends basic Product class.
 *
 * @version $Revision: 1.1 $
 **/
public class CompactDisc extends Product
{
    String  artist=null;

    String  title=null;

    int     year=-1;

    String  producer=null;

    String  publisher=null;

    protected CompactDisc()
    {
        super();
    }

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

    public String   getArtist()
    {
        return artist;
    }

    public String   getTitle()
    {
        return title;
    }

    public int  getYear()
    {
        return year;
    }

    public String   getProducer()
    {
        return producer;
    }

    public String   getPublisher()
    {
        return publisher;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public void setProducer(String producer)
    {
        this.producer = producer;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    public String   toString()
    {
        return "CompactDisc : " + artist + " - " + title + " [" + id + "]";
    }
}
