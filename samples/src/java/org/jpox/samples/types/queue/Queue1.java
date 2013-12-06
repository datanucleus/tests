/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
This program and the accompanying materials are made available under 
the terms of the JPOX License v1.0 which accompanies this distribution.

Contributors:
    ...
**********************************************************************/
package org.jpox.samples.types.queue;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Sample object having a Queue using a join table.
 * 
 * @version $Revision: 1.1 $
 */
public class Queue1
{
    String name;
    Queue queue = new PriorityQueue();

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