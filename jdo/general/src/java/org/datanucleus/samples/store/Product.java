package org.datanucleus.samples.store;

/**
 * Definition of a Product
 * Represents a product, and contains the key aspects of the item.
 *
 * @version $Revision: 1.1 $  
 **/
public class Product implements Cloneable
{
    /**
     * Id of the Product.
     **/
    protected String id=null;

    /**
     * Name of the Product.
     **/
    protected String name=null;

    /**
     * Description of the Product.
     **/
    protected String description=null;

    /**
     * Image URL of the Product.
     **/
    protected String imageUrl=null;

    /**
     * Currency of the Product.
     **/
    protected String currency=null;

    /**
     * Price of the Product.
     **/
    protected double price=0.0;

    /** 
     * Old Price of the Product.
     **/
    protected double oldPrice=0.0;

    /**
     * List Price of the Product.
     **/
    protected double listPrice=0.0;

    /**
     * Tax Percent of the Product.
     **/
    protected double taxPercent=0.0;

    /**
     * Status of the Product.
     **/
    protected int statusId=-1;

    /**
     * Default constructor. 
     **/
    protected Product()
    {
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
     **/
    public Product(String id,
                    String name,
                    String description,
                    String image_url,
                    String currency,
                    double price,
                    double old_price,
                    double list_price,
                    double tax_percent,
                    int status_id)
    {
        this.id             = id;
        this.name           = name;
        this.description    = description;
        this.imageUrl       = image_url;
        this.currency       = currency;
        this.price          = price;
        this.oldPrice       = old_price;
        this.listPrice      = list_price;
        this.taxPercent     = tax_percent;
        this.statusId       = status_id;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException cnse)
        {
            return null;
        }
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }
        Product other = (Product)obj;
        if (id.equals(other.id) &&
            name.equals(other.name) &&
            description.equals(other.description) &&
            price == other.price)
        {
            return true;
        }
        return false;
    }

    // ------------------------------- Accessors -------------------------------
    /**
     * Accessor for the id of the product.
     * @return  Id of the product.
     **/
    public String getId()
    {
        return id;
    }

    /**
     * Accessor for the name of the product.
     * @return  Name of the product.
     **/
    public String getName()
    {
        return name;
    }

    /**
     * Accessor for the description of the product.
     * @return  Description of the product.
     **/
    public String getDescription()
    {
        return description;
    }

    /**
     * Accessor for the image URL of the product.
     * @return  Image URL of the product.
     **/
    public String getImageUrl()
    {
        return imageUrl;
    }

    /**
     * Accessor for the currency of the product.
     * @return  Currency of the product.
     **/
    public String getCurrency()
    {
        return currency;
    }

    /**
     * Accessor for the price of the product.
     * @return  Price of the product.
     **/
    public double getPrice()
    {
        return price;
    }

    /**
     * Accessor for the old price of the product.
     * @return  Old price of the product.
     **/
    public double getOldPrice()
    {
        return oldPrice;
    }

    /**
     * Accessor for the list price of the product.
     * @return  List price of the product.
     **/
    public double getListPrice()
    {
        return listPrice;
    }

    /**
     * Accessor for the tax percent of the product.
     * @return  Tax percent of the product.
     **/
    public double getTaxPercent()
    {
        return taxPercent;
    }

    /**
     * Accessor for the status of the product.
     * @return  Status of the product.
     **/
    public int getStatusId()
    {
        return statusId;
    }

    // ------------------------------- Mutators --------------------------------
    /**
     * Mutator for the id of the product.
     * @param   id      Id of the product.
     **/
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Mutator for the name of the product.
     * @param   name    Name of the product.
     **/
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Mutator for the description of the product.
     * @param   description     Description of the product.
     **/
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Mutator for the image URL of the product.
     * @param   image_url   Image URL of the product.
     **/
    public void setImageUrl(String image_url)
    {
        this.imageUrl = image_url;
    }

    /**
     * Mutator for the currency of the product.
     * @param   currency    Currency of the product.
     **/
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    /**
     * Mutator for the price of the product.
     * @param   price   Price of the product.
     **/
    public void setPrice(double price)
    {
        this.price = price;
    }

    /**
     * Mutator for the old price of the product.
     * @param   old_price   Old price of the product.
     **/
    public void setOldPrice(double old_price)
    {
        this.oldPrice = old_price;
    }

    /**
     * Mutator for the list price of the product.
     * @param   list_price  list_price of the product.
     **/
    public void setListPrice(double list_price)
    {
        this.listPrice = list_price;
    }

    /**
     * Mutator for the tax percentage of the product.
     * @param   tax_percent Tax percent of the product.
     **/
    public void setTaxPercent(double tax_percent)
    {
        this.taxPercent = tax_percent;
    }

    /**
     * Mutator for the status id of the product.
     * @param   status_id   Status id of the product.
     **/
    public void setStatusId(int status_id)
    {
        this.statusId = status_id;
    }

    /**
     * Utility to return the object as a string.
     * @return  Stringified version of this Product. 
     **/
    public String toString()
    {
        return "Product : " + name + " [" + id + "]";
    }
}