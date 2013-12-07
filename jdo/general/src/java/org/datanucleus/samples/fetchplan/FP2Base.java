/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others.
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
package org.datanucleus.samples.fetchplan;

import java.io.Serializable;

/**
 *
 * @version $Revision: 1.1 $
 */
public class FP2Base
{
    private int piece1;
    private int piece2;
    private int piece3;
    private int piece4;
    private int piece5;
    private int piece6;
    private int piece7;
    private int piece8;
    private int piece9;
    private int piece10;
    private Object piece11;
    private int piece12;
    private int piece13;
    private int piece14;
    

    public final int getPiece1()
    {
        return piece1;
    }

    public final void setPiece1(int piece1)
    {
        this.piece1 = piece1;
    }

    public final int getPiece10()
    {
        return piece10;
    }

    public final void setPiece10(int piece10)
    {
        this.piece10 = piece10;
    }

    public final Object getPiece11()
    {
        return piece11;
    }

    public final void setPiece11(Object piece11)
    {
        this.piece11 = piece11;
    }

    public final int getPiece12()
    {
        return piece12;
    }

    public final void setPiece12(int piece12)
    {
        this.piece12 = piece12;
    }

    public final int getPiece2()
    {
        return piece2;
    }

    public final void setPiece2(int piece2)
    {
        this.piece2 = piece2;
    }

    public final int getPiece3()
    {
        return piece3;
    }

    public final void setPiece3(int piece3)
    {
        this.piece3 = piece3;
    }

    public final int getPiece4()
    {
        return piece4;
    }

    public final void setPiece4(int piece4)
    {
        this.piece4 = piece4;
    }

    public final int getPiece5()
    {
        return piece5;
    }

    public final void setPiece5(int piece5)
    {
        this.piece5 = piece5;
    }

    public final int getPiece6()
    {
        return piece6;
    }

    public final void setPiece6(int piece6)
    {
        this.piece6 = piece6;
    }

    public final int getPiece7()
    {
        return piece7;
    }

    public final void setPiece7(int piece7)
    {
        this.piece7 = piece7;
    }

    public final int getPiece8()
    {
        return piece8;
    }

    public final void setPiece8(int piece8)
    {
        this.piece8 = piece8;
    }

    public final int getPiece9()
    {
        return piece9;
    }

    public final void setPiece9(int piece9)
    {
        this.piece9 = piece9;
    }

    public int getPiece13()
    {
        return piece13;
    }

    public void setPiece13(int piece13)
    {
        this.piece13 = piece13;
    }

    public int getPiece14()
    {
        return piece14;
    }

    public void setPiece14(int piece14)
    {
        this.piece14 = piece14;
    }

    public static class Id implements Serializable
    {
        public int piece1;

        public Id()
        {
        }

        public Id(String s)
        {
            this.piece1 = Integer.valueOf(s).intValue();
        }

        public String toString()
        {
            return "" + piece1;
        }

        public int hashCode()
        {
            return piece1;
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Id))
            {
                Id k = (Id)other;
                return k.piece1 == this.piece1;
            }
            return false;
        }
    }
}