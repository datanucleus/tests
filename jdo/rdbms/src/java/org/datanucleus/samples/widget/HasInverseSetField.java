/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.widget;

import java.util.Set;


public interface HasInverseSetField extends HasNormalSetField
{
    Set getInverseSet();

    int getNumElementWidgets();
}
