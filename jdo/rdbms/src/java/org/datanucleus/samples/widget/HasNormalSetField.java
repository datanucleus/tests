package org.datanucleus.samples.widget;

import java.util.Set;


public interface HasNormalSetField
{
    Set getNormalSet();

    int getNumWidgets();

    void fillRandom(int numWidgets);

    void fillRandom(int numWidgets, boolean includeSetWidgets);
}
