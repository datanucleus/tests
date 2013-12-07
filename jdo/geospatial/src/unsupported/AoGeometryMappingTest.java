package org.datanucleus.tests;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.samples.aogeometry.*;

import com.esri.arcgis.geometry.Envelope;
import com.esri.arcgis.geometry.GeometryBag;
import com.esri.arcgis.geometry.Line;
import com.esri.arcgis.geometry.Multipoint;
import com.esri.arcgis.geometry.Path;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.Polygon;
import com.esri.arcgis.geometry.Polyline;
import com.esri.arcgis.geometry.Ring;

public class AoGeometryMappingTest extends JDOPersistenceTestCase
{
    public AoGeometryMappingTest(String name)
    {
        super(name);
    }

    protected void tearDown() throws Exception
    {
        if (runTestsForDatastore())
        {
            clean(SamplePoint.class);
            clean(SampleMultipoint.class);
            clean(SampleLine.class);
            clean(SampleRing.class);
            clean(SamplePath.class);
            clean(SamplePolyline.class);
            clean(SamplePolygon.class);
            clean(SampleEnvelope.class);
            clean(SampleGeometryBag.class);
        }
        super.tearDown();
    }

    boolean runTestsForDatastore()
    {
        return (vendorID.equalsIgnoreCase("WHO KNOWS WHAT!"));
    }

    public void testPointMapping()
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = new Point();
        point.putCoords(10.0, 10.0);

        SamplePoint samplePoint;
        SamplePoint samplePoint_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePoint = new SamplePoint(1001, "Point 1", point);
            pm.makePersistent(samplePoint);
            id = JDOHelper.getObjectId(samplePoint);
            samplePoint = (SamplePoint) pm.detachCopy(samplePoint);
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
            samplePoint_read = (SamplePoint) pm.getObjectById(id, true);
            assertEquals(samplePoint, samplePoint_read);
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

    public void testMultipointMapping()
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Multipoint multipoint = new Multipoint();
        Point p1 = new Point();
        Point p2 = new Point();
        Point p3 = new Point();
        p1.putCoords(10.0, 10.0);
        p2.putCoords(20.0, 20.0);
        p3.putCoords(30.0, 30.0);
        multipoint.addGeometry(p1, null, null);
        multipoint.addGeometry(p2, null, null);
        multipoint.addGeometry(p3, null, null);

        SampleMultipoint sampleMultipoint;
        SampleMultipoint sampleMultipoint_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleMultipoint = new SampleMultipoint(2001, "Multipoint 1", multipoint);
            pm.makePersistent(sampleMultipoint);
            id = JDOHelper.getObjectId(sampleMultipoint);
            sampleMultipoint = (SampleMultipoint) pm.detachCopy(sampleMultipoint);
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
            sampleMultipoint_read = (SampleMultipoint) pm.getObjectById(id, true);
            assertEquals(sampleMultipoint, sampleMultipoint_read);
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

    public void testLineMapping()
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Line line = new Line();
        Point startPoint = new Point();
        Point endPoint = new Point();
        startPoint.putCoords(10.0, 10.0);
        startPoint.putCoords(20.0, 20.0);
        line.putCoords(startPoint, endPoint);

        SampleLine sampleLine;
        SampleLine sampleLine_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleLine = new SampleLine(3001, "Line 1", line);
            pm.makePersistent(sampleLine);
            id = JDOHelper.getObjectId(sampleLine);
            sampleLine = (SampleLine) pm.detachCopy(sampleLine);
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
            sampleLine_read = (SampleLine) pm.getObjectById(id, true);
            assertEquals(sampleLine, sampleLine_read);
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

    public void testPathMapping()
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Path path = new Path();
        Point p1 = new Point();
        Point p2 = new Point();
        Point p3 = new Point();
        p1.putCoords(10.0, 10.0);
        p2.putCoords(20.0, 20.0);
        p3.putCoords(30.0, 30.0);
        path.addPoint(p1, null, null);
        path.addPoint(p2, null, null);
        path.addPoint(p3, null, null);

        SamplePath samplePath;
        SamplePath samplePath_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePath = new SamplePath(4001, "Path 1", path);
            pm.makePersistent(samplePath);
            id = JDOHelper.getObjectId(samplePath);
            samplePath = (SamplePath) pm.detachCopy(samplePath);
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
            samplePath_read = (SamplePath) pm.getObjectById(id, true);
            assertEquals(samplePath, samplePath_read);
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

    public void testRingMapping()
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Ring ring = new Ring();
        Point p1 = new Point();
        Point p2 = new Point();
        Point p3 = new Point();
        Point p4 = new Point();
        p1.putCoords(10.0, 10.0);
        p2.putCoords(40.0, 60.0);
        p3.putCoords(80.0, 50.0);
        p4.putCoords(20.0, 5.0);
        ring.addPoint(p1, null, null);
        ring.addPoint(p2, null, null);
        ring.addPoint(p3, null, null);
        ring.addPoint(p4, null, null);
        ring.addPoint(p1, null, null);

        SampleRing sampleRing;
        SampleRing sampleRing_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleRing = new SampleRing(5001, "Ring 1", ring);
            pm.makePersistent(sampleRing);
            id = JDOHelper.getObjectId(sampleRing);
            sampleRing = (SampleRing) pm.detachCopy(sampleRing);
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
            sampleRing_read = (SampleRing) pm.getObjectById(id, true);
            assertEquals(sampleRing, sampleRing_read);
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

    public void testPolylineMapping()
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Polyline polyline = new Polyline();
        Point p1 = new Point();
        Point p2 = new Point();
        Point p3 = new Point();
        Point p4 = new Point();
        Point p5 = new Point();
        Point p6 = new Point();
        p1.putCoords(10.0, 15.0);
        p2.putCoords(20.0, 25.0);
        p3.putCoords(30.0, 35.0);
        p4.putCoords(40.0, 45.0);
        p5.putCoords(50.0, 55.0);
        p6.putCoords(60.0, 65.0);
        Path path1 = new Path();
        Path path2 = new Path();
        path1.addPoint(p1, null, null);
        path1.addPoint(p2, null, null);
        path1.addPoint(p3, null, null);
        path2.addPoint(p4, null, null);
        path2.addPoint(p5, null, null);
        path2.addPoint(p6, null, null);
        polyline.addGeometry(path1, null, null);
        polyline.addGeometry(path2, null, null);

        SamplePolyline samplePolyline;
        SamplePolyline samplePolyline_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePolyline = new SamplePolyline(6001, "Polyline 1", polyline);
            pm.makePersistent(samplePolyline);
            id = JDOHelper.getObjectId(samplePolyline);
            samplePolyline = (SamplePolyline) pm.detachCopy(samplePolyline);
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
            samplePolyline_read = (SamplePolyline) pm.getObjectById(id, true);
            assertEquals(samplePolyline, samplePolyline_read);
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

    public void testPolygonMapping()
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Polygon polygon = new Polygon();
        Ring ring = new Ring();
        Point p1 = new Point();
        Point p2 = new Point();
        Point p3 = new Point();
        Point p4 = new Point();
        p1.putCoords(10.0, 10.0);
        p2.putCoords(40.0, 60.0);
        p3.putCoords(80.0, 50.0);
        p4.putCoords(20.0, 5.0);
        ring.addPoint(p1, null, null);
        ring.addPoint(p2, null, null);
        ring.addPoint(p3, null, null);
        ring.addPoint(p4, null, null);
        ring.addPoint(p1, null, null);
        polygon.addGeometry(ring, null, null);

        SamplePolygon samplePolygon;
        SamplePolygon samplePolygon_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePolygon = new SamplePolygon(7001, "Polygon 1", polygon);
            pm.makePersistent(samplePolygon);
            id = JDOHelper.getObjectId(samplePolygon);
            samplePolygon = (SamplePolygon) pm.detachCopy(samplePolygon);
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
            samplePolygon_read = (SamplePolygon) pm.getObjectById(id, true);
            assertEquals(samplePolygon, samplePolygon_read);
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

    public void testEnvelopeMapping()
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Envelope envelope = new Envelope();
        Point lowerLeft = new Point();
        lowerLeft.putCoords(10.0, 10.0);
        Point lowerRight = new Point();
        lowerRight.putCoords(60.0, 10.0);
        Point upperLeft = new Point();
        upperLeft.putCoords(10.0, 40.0);
        Point upperRight = new Point();
        upperRight.putCoords(60.0, 40.0);
        envelope.setLowerLeft(lowerLeft);
        envelope.setLowerRight(lowerRight);
        envelope.setUpperLeft(upperLeft);
        envelope.setUpperRight(upperRight);

        SampleEnvelope sampleEnvelope;
        SampleEnvelope sampleEnvelope_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleEnvelope = new SampleEnvelope(8001, "Envelope 1", envelope);
            pm.makePersistent(sampleEnvelope);
            id = JDOHelper.getObjectId(sampleEnvelope);
            sampleEnvelope = (SampleEnvelope) pm.detachCopy(sampleEnvelope);
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
            sampleEnvelope_read = (SampleEnvelope) pm.getObjectById(id, true);
            assertEquals(sampleEnvelope, sampleEnvelope_read);
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

    public void testGeometryBagMapping()
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        GeometryBag geometryBag = new GeometryBag();
        Point singlePoint = new Point();
        singlePoint.putCoords(10.0, 10.0);
        Point startPoint = new Point();
        Point endPoint = new Point();
        startPoint.putCoords(20.0, 20.0);
        endPoint.putCoords(40.0, 40.0);
        Line line = new Line();
        line.putCoords(startPoint, endPoint);
        geometryBag.addGeometry(singlePoint, null, null);
        geometryBag.addGeometry(line, null, null);

        SampleGeometryBag sampleGeometryBag;
        SampleGeometryBag sampleGeometryBag_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleGeometryBag = new SampleGeometryBag(9001, "GeometryBag 1", geometryBag);
            pm.makePersistent(sampleGeometryBag);
            id = JDOHelper.getObjectId(sampleGeometryBag);
            sampleGeometryBag = (SampleGeometryBag) pm.detachCopy(sampleGeometryBag);
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
            sampleGeometryBag_read = (SampleGeometryBag) pm.getObjectById(id, true);
            assertEquals(sampleGeometryBag, sampleGeometryBag_read);
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
}