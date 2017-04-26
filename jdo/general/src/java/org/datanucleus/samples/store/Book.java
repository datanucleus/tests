package org.datanucleus.samples.store;

/**
 * Definition of a Book. Extends basic Product class.
 **/
public class Book extends Product
{
    String  isbn=null;

    String  author=null;

    String  title=null;

    int     editionNo=-1;

    String  publisher=null;

    protected Book()
    {
        super();
    }

    public Book(String id,
                String name,
                String description,
                String image_url,
                String currency,
                double price,
                double old_price,
                double list_price,
                double tax_percent,
                int status_id,
                String isbn,
                String author,
                String title,
                int edition_no,
                String publisher)
    {
        super(id,name,description,image_url,currency,price,old_price,list_price,tax_percent,status_id);

        this.isbn       = isbn;
        this.author     = author;
        this.title      = title;
        this.editionNo  = edition_no;
        this.publisher  = publisher;
    }

    public String   getIsbn()
    {
        return isbn;
    }

    public String   getAuthor()
    {
        return author;
    }

    public String   getTitle()
    {
        return title;
    }

    public int  getEditionNo()
    {
        return editionNo;
    }

    public String   getPublisher()
    {
        return publisher;
    }

    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setEditionNo(int edition_no)
    {
        this.editionNo = edition_no;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    public String   toString()
    {
        return "Book : " + author + " - " + title + " [" + id + "]";
    }
}
