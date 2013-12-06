/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 

Contributors:
    ...
**********************************************************************/
package org.jpox.samples.types.point;

/**
 * Object with a Point.
 *
 * @version $Revision: 1.1 $
 */
public class PointHolder
{
    private java.awt.Point point;

    /**
     * Constructor.
     * @param x Initial x coord
     * @param y Initial y coord
     */
    public PointHolder(int x, int y)
    {
        point = new java.awt.Point(x, y);
    }

    /**
     * @return Returns the point.
     */
    public java.awt.Point getPoint()
    {
        return point;
    }

    /**
     * Method to update the existing point's x coord
     * @param x New x coord
     */
    public void setPointX(int x)
    {
        point.setLocation(x, (int)point.getY());
    }

    /**
     * Method to update the existing point's y coord
     * @param y New y coord
     */
    public void setPointY(int y)
    {
        point.setLocation((int)point.getX(), y);
    }

    /**
     * @param point The point to set.
     */
    public void setPoint(java.awt.Point point)
    {
        this.point = point;
    }

    public void setLocation(int x, int y)
    {
        this.point.setLocation(x, y);
    }

    public void move(int x, int y)
    {
        this.point.move(x, y);
    }

    public void translate(int x, int y)
    {
        this.point.translate(x, y);
    }    
}