package org.datanucleus.samples.store;

/**
 * Definition of a Product
 * Represents a product, and contains the key aspects of the item.
 */
public class Product implements Cloneable
{
    protected String id=null;

    protected String name=null;

    protected String description=null;

    protected String imageUrl=null;

    protected String currency=null;

    protected double price=0.0;

    protected double oldPrice=0.0;

    protected double listPrice=0.0;

    protected double taxPercent=0.0;

    protected int statusId=-1;

    protected Guarantee guarantee;

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

    public String getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public String getDescription()
    {
        return description;
    }
    public String getImageUrl()
    {
        return imageUrl;
    }
    public String getCurrency()
    {
        return currency;
    }
    public double getPrice()
    {
        return price;
    }
    public double getOldPrice()
    {
        return oldPrice;
    }
    public double getListPrice()
    {
        return listPrice;
    }
    public double getTaxPercent()
    {
        return taxPercent;
    }
    public int getStatusId()
    {
        return statusId;
    }
    public Guarantee getGuarantee()
    {
        return guarantee;
    }

    public void setId(String id)
    {
        this.id = id;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public void setImageUrl(String image_url)
    {
        this.imageUrl = image_url;
    }
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }
    public void setPrice(double price)
    {
        this.price = price;
    }
    public void setOldPrice(double old_price)
    {
        this.oldPrice = old_price;
    }
    public void setListPrice(double list_price)
    {
        this.listPrice = list_price;
    }
    public void setTaxPercent(double tax_percent)
    {
        this.taxPercent = tax_percent;
    }
    public void setStatusId(int status_id)
    {
        this.statusId = status_id;
    }
    public void setGuarantee(Guarantee guar)
    {
        this.guarantee = guar;
    }

    public String toString()
    {
        return "Product : " + name + " [" + id + "]";
    }
}