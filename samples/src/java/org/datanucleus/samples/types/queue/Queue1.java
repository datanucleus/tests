package org.datanucleus.samples.types.queue;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Sample object having a Queue using a join table.
 */
public class Queue1
{
    String name;
    Queue<Queue1Item> queue = new PriorityQueue<>();

    public Queue1(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int getQueueSize()
    {
        return queue.size();
    }

    public void offer(Queue1Item item)
    {
        queue.offer(item);
    }

    public Queue1Item peek()
    {
        return (Queue1Item)queue.peek();
    }

    public Queue1Item poll()
    {
        return (Queue1Item)queue.poll();
    }
}