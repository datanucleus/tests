package org.datanucleus.samples.store;

/**
 * Definition of a Book. Extends basic Product class.
 *
 * @version $Revision: 1.1 $
 **/
public class Book extends Product
{
    /**
     * ISBN number of the book.
     **/
    String  isbn=null;

    /**
     * Author of the Book.
     **/
    String  author=null;

    /**
     * Title of the Book.
     **/
    String  title=null;

    /**
     * Edition of the Book.
     **/
    int     editionNo=-1;

    /**
     * Publisher of the Book.
     **/
    String  publisher=null;

    /**
     * Default Constructor. 
     **/
    protected Book()
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
     * @param   isbn        ISBN number of the book
     * @param   author      Author of the book
     * @param   title       Title of the book
     * @param   edition_no  Edition number of the book
     * @param   publisher   Name of publisher of the book 
     **/
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

    // ------------------------------- Accessors -------------------------------
    /**
     * Accessor for the isbn of the book.
     * @return  ISBN of the book.
     **/
    public String   getIsbn()
    {
        return isbn;
    }

    /**
     * Accessor for the author of the book.
     * @return  Author of the book.
     **/
    public String   getAuthor()
    {
        return author;
    }

    /**
     * Accessor for the title of the book.
     * @return  Title of the book.
     **/
    public String   getTitle()
    {
        return title;
    }

    /**
     * Accessor for the edition no of the book.
     * @return  Edition no of the book.
     **/
    public int  getEditionNo()
    {
        return editionNo;
    }

    /**
     * Accessor for the publisher of the book.
     * @return  Publisher of the book.
     **/
    public String   getPublisher()
    {
        return publisher;
    }

    // ------------------------------- Mutators --------------------------------
    /**
     * Mutator for the ISBN of the book.
     * @param   isbn        ISBN of the book.
     **/
    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }

    /**
     * Mutator for the author of the book.
     * @param   author  Author of the book.
     **/
    public void setAuthor(String author)
    {
        this.author = author;
    }

    /**
     * Mutator for the title of the book.
     * @param   title   Title of the book.
     **/
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Mutator for the edition no of the book.
     * @param   edition_no  Edition no of the book.
     **/
    public void setEditionNo(int edition_no)
    {
        this.editionNo = edition_no;
    }

    /**
     * Mutator for the publisher of the book.
     * @param   publisher   Publisher of the book.
     **/
    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    /**
     * Utility to return the object as a string.
     * @return  Stringified version of this Book. 
     **/
    public String   toString()
    {
        return "Book : " + author + " - " + title + " [" + id + "]";
    }
}
