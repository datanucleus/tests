/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others.
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

/**
 * @version $Revision: 1.1 $
 */
public class FP2Sub extends FP2Base
{
    private int piece20;
    private int piece21;
    private Object piece22;
    private FP3Base piece23;

    public FP3Base getPiece23()
    {
        return piece23;
    }

    public void setPiece23(FP3Base piece23)
    {
        this.piece23 = piece23;
    }

    public int getPiece20()
    {
        return piece20;
    }

    public void setPiece20(int piece20)
    {
        this.piece20 = piece20;
    }

    public int getPiece21()
    {
        return piece21;
    }

    public void setPiece21(int piece21)
    {
        this.piece21 = piece21;
    }

    public Object getPiece22()
    {
        return piece22;
    }

    public void setPiece22(Object piece22)
    {
        this.piece22 = piece22;
    }
}