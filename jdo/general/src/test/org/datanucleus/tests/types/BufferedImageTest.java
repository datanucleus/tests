/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.jdo.JDOHelper;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.bufferedimage.BufferedImageHolder;

public class BufferedImageTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public BufferedImageTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]{BufferedImageHolder.class});
            initialised = true;
        }
    }

    public void testBasicPersistence() throws Exception
    {
        try
        {
            URL imageURL = getClass().getClassLoader().getResource("org/jpox/samples/types/bufferedimage/DataNucleus_80.jpg");
            BufferedImage refImage = ImageIO.read(imageURL);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
            ImageIO.write(refImage, "jpg", baos);
            byte[] buffer = baos.toByteArray();
            BufferedImage ref = ImageIO.read(new ByteArrayInputStream(buffer));

            BufferedImageHolder myImage = new BufferedImageHolder(null);
            myImage.setImage(refImage);
            Object id = null;
            javax.jdo.PersistenceManager pm = pmf.getPersistenceManager();
            javax.jdo.Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(myImage);
                tx.commit();
                id = JDOHelper.getObjectId(myImage);

                tx.begin();
                BufferedImageHolder myImage2 = (BufferedImageHolder) pm.getObjectById(id, true);
                assertEquals(ref.getRaster().toString(), myImage2.getImage().getRaster().toString());
                assertEquals(ref.getType(), myImage2.getImage().getType());
                pm.refresh(myImage2);
                assertEquals(ref.getRaster().toString(), myImage2.getImage().getRaster().toString());
                assertEquals(ref.getType(), myImage2.getImage().getType());
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                BufferedImageHolder myImage2 = (BufferedImageHolder) pm.getObjectById(id, true);
                assertEquals(ref.getRaster().toString(), myImage2.getImage().getRaster().toString());
                assertEquals(ref.getType(), myImage2.getImage().getType());
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
        }
        finally
        {
            clean(BufferedImageHolder.class);
        }
    }
}