package org.datanucleus.samples.types.queue;

/**
 * Sample item stored in a join table queue
 * 
 * @version $Revision: 1.1 $
 */
public class Queue1Item implements Comparable
{
    String value;

    public Queue1Item(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public int compareTo(Object o)
    {
        if (o == null)
        {
            return -1;
        }
        else if (!(o instanceof Queue1Item))
        {
            return 1;
        }
        return value.compareTo(((Queue1Item)o).value);
    }
}