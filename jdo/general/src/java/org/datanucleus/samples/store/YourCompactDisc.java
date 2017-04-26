package org.datanucleus.samples.store;

/**
 * Extension of CompactDisc to add on its location.
 **/
public class YourCompactDisc extends CompactDisc
{
    protected YourCompactDisc()
    {
        super();
    }

    public YourCompactDisc(String id,
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
        super(id,name,description,image_url,currency,price,old_price,list_price,tax_percent,status_id,artist,title,year,producer,publisher);
    }

    public String   toString()
    {
        return "YourCompactDisc : " + artist + " - " + title + " : [" + id + "]";
    }
}
