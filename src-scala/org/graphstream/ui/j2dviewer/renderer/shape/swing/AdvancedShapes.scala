/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.ui.j2dviewer.renderer.shape.swing

import java.awt._
import java.awt.geom._

import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.util._
import org.graphstream.ui.geom._
import org.graphstream.ui.j2dviewer.renderer.shape.Decorable

import scala.collection.JavaConversions._
import scala.math._

/** Utility trait to display cubics Bézier curves control polygons. */
abstract trait ShowCubics {
    protected var showControlPolygon = false

    /** Show the control polygons. */
    protected def showCtrlPoints(g: Graphics2D, camera: Camera, skel:ConnectorSkeleton) {
        if (showControlPolygon && skel.isCurve) {
            val from = skel.from
            val ctrl1 = skel(1)
            val ctrl2 = skel(2)
            val to = skel.to
            val oval = new Ellipse2D.Double
            val color = g.getColor
            val stroke = g.getStroke
            val px6 = camera.metrics.px1 * 6;
            val px3 = camera.metrics.px1 * 3

            g.setColor(Color.RED)
            oval.setFrame(from.x - px3, from.y - px3, px6, px6)
            g.fill(oval)

            if (ctrl1 != null) {
                oval.setFrame(ctrl1.x - px3, ctrl1.y - px3, px6, px6)
                g.fill(oval)
                oval.setFrame(ctrl2.x - px3, ctrl2.y - px3, px6, px6)
                g.fill(oval)
                val line = new Line2D.Double
                line.setLine(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y)
                g.setStroke(new java.awt.BasicStroke(camera.metrics.px1.toFloat))
                g.draw(line)
                line.setLine(from.x, from.y, ctrl1.x, ctrl1.y)
                g.draw(line)
                line.setLine(ctrl2.x, ctrl2.y, to.x, to.y)
                g.draw(line)
            }

            oval.setFrame(to.x - px3, to.y - px3, px6, px6)
            g.fill(oval)
            g.setColor(color)
            g.setStroke(stroke)
        }
    }
}

/** A blob-like shape. */
class BlobShape extends AreaConnectorShape with ShowCubics {
    protected var theShape = new Path2D.Double

    // Command

    protected def make(bck: Backend, camera: Camera) {
        make(camera, 0, 0, 0, 0)
    }

    protected def make(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.isCurve)
            makeOnCurve(camera, sox, soy, swx, swy)
        else if (skel.isPoly)
            makeOnPolyline(camera, sox, soy, swx, swy)
        else makeOnLine(camera, sox, soy, swx, swy)
    }

    protected def makeOnLine(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel.from.x + sox
        val fromy = skel.from.y + soy
        val tox = skel.to.x + sox
        val toy = skel.to.y + soy
        val dir = new Vector2(tox - fromx, toy - fromy)
        val perp1 = new Vector2(dir.y, -dir.x); perp1.normalize // 1/2 perp vector to the from point.
        val perp2 = new Vector2(perp1.x, perp1.y) // 1/2 perp vector to the to point.
        val perpm = new Vector2(perp1.x, perp1.y) // 1/2 perp vector to the middle point on the edge.
        val srcsz = min(theSourceSize.x, theSourceSize.y)
        val trgsz = min(theTargetSize.x, theTargetSize.y)

        perp1.scalarMult((srcsz + swx) / 2f)
        perpm.scalarMult((theSize + swx) / 2f)

        if (isDirected)
            perp2.scalarMult((theSize + swx) / 2f)
        else perp2.scalarMult((trgsz + swx) / 2f)

        val t1 = 5f
        val t2 = 2.3f
        val m = 1f
        theShape.reset
        theShape.moveTo(fromx + perp1.x, fromy + perp1.y)
        theShape.quadTo(fromx + dir.x / t1 + perpm.x * m, fromy + dir.y / t1 + perpm.y * m,
            fromx + dir.x / t2 + perpm.x, fromy + dir.y / t2 + perpm.y)
        theShape.lineTo(tox - dir.x / t2 + perpm.x, toy - dir.y / t2 + perpm.y)
        theShape.quadTo(tox - dir.x / t1 + perpm.x * m, toy - dir.y / t1 + perpm.y * m,
            tox + perp2.x, toy + perp2.y)
        theShape.lineTo(tox - perp2.x, toy - perp2.y)
        theShape.quadTo(tox - dir.x / t1 - perpm.x * m, toy - dir.y / t1 - perpm.y * m,
            tox - dir.x / t2 - perpm.x, toy - dir.y / t2 - perpm.y)
        theShape.lineTo(fromx + dir.x / t2 - perpm.x, fromy + dir.y / t2 - perpm.y)
        theShape.quadTo(fromx + dir.x / t1 - perpm.x * m, fromy + dir.y / t1 - perpm.y * m,
            fromx - perp1.x, fromy - perp1.y)
        theShape.closePath
    }

    protected def makeOnPolyline(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        // TODO
        makeOnLine(camera, sox, soy, swx, swy)
    }

    protected def makeOnCurve(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.isLoop)
            makeLoop(camera, sox, soy, swx, swy)
        else makeMulti(camera, sox, soy, swx, swy)
    }

    protected def makeMulti(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy
        val srcsz = min(theSourceSize.x, theSourceSize.y)
        val trgsz = min(theTargetSize.x, theTargetSize.y)

        val maindir = new Vector2(c2x - c1x, c2y - c1y)
        val perp1 = new Vector2(maindir.y, -maindir.x); perp1.normalize // 1/2 perp vector to the from point.
        val perp2 = new Vector2(perp1.x, perp1.y) // 1/2 perp vector to the to point.
        val perpm = new Vector2(perp1.x, perp1.y) // 1/2 perp vector to the middle point on the edge.

        val t = 5f

        perp1.scalarMult((srcsz + swx) / 2f)
        perpm.scalarMult((theSize + swx) / 2f)

        //   ctrl1           ctrl2
        //     x---t-------t---x
        //    /                 \
        //   /                   \
        //  X                     X
        // from                  to

        if (isDirected)
            perp2.scalarMult((theSize + swx) / 2f)
        else perp2.scalarMult((trgsz + swx) / 2f)

        theShape.reset
        theShape.moveTo(fromx + perp1.x, fromy + perp1.y)

        theShape.quadTo(c1x + perpm.x, c1y + perpm.y,
            c1x + maindir.x / t + perpm.x, c1y + maindir.y / t + perpm.y)
        theShape.lineTo(c2x - maindir.x / t + perpm.x, c2y - maindir.y / t + perpm.y)
        theShape.quadTo(c2x + perpm.x, c2y + perpm.y, tox + perp2.x, toy + perp2.y)

        theShape.lineTo(tox - perp2.x, toy - perp2.y)

        theShape.quadTo(c2x - perpm.x, c2y - perpm.y,
            c2x - maindir.x / t - perpm.x, c2y - maindir.y / t - perpm.y)
        theShape.lineTo(c1x + maindir.x / t - perpm.x, c1y + maindir.y / t - perpm.y)
        theShape.quadTo(c1x - perpm.x, c1y - perpm.y, fromx - perp1.x, fromy - perp1.y)

        theShape.closePath
    }

    protected def makeLoop(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy
        val srcsz = min(theSourceSize.x, theSourceSize.y)
        //		val trgsz = min( theTargetSizeX, theTargetSizeY )

        val dirFrom = new Vector2(c1x - fromx, c1y - fromy);
        val dirTo = new Vector2(tox - c2x, toy - c2y);
        val mainDir = new Vector2(c2x - c1x, c2y - c1y)

        val perpFrom = new Vector2(dirFrom.y, -dirFrom.x); perpFrom.normalize
        val mid1 = new Vector2(dirFrom); mid1.sub(mainDir); mid1.normalize
        val mid2 = new Vector2(mainDir); mid2.sub(dirTo); mid2.normalize

        perpFrom.scalarMult((srcsz + swx) * 0.3f)

        if (isDirected) {
            mid1.scalarMult((theSize + swx) * 4f)
            mid2.scalarMult((theSize + swx) * 2f)
        } else {
            mid1.scalarMult((theSize + swx) * 4f)
            mid2.scalarMult((theSize + swx) * 4f)
        }

        theShape.reset
        theShape.moveTo(fromx + perpFrom.x, fromy + perpFrom.y)
        if (isDirected) {
            theShape.curveTo(c1x + mid1.x, c1y + mid1.y, c2x + mid2.x, c2y + mid2.y, tox, toy)
            theShape.curveTo(c2x - mid2.x, c2y - mid2.y, c1x - mid1.x, c1y - mid1.y, fromx - perpFrom.x, fromy - perpFrom.y)
        } else {
            var perpTo = new Vector2(dirTo.y, -dirTo.x); perpTo.normalize; perpTo.scalarMult((srcsz + swx) * 0.3f)
            theShape.curveTo(c1x + mid1.x, c1y + mid1.y, c2x + mid2.x, c2y + mid2.y, tox + perpTo.x, toy + perpTo.y)
            theShape.lineTo(tox - perpTo.x, toy - perpTo.y)
            theShape.curveTo(c2x - mid2.x, c2y - mid2.y, c1x - mid1.x, c1y - mid1.y, fromx - perpFrom.x, fromy - perpFrom.y)
        }
        theShape.closePath
    }

    protected def makeShadow(bck: Backend, camera: Camera) {
        make(camera, theShadowOff.x, theShadowOff.y, theShadowWidth.x, theShadowWidth.y)
    }

    def renderShadow(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        makeShadow(bck, camera)
        cast(bck.graphics2D, theShape)
    }

    def render(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        val g = bck.graphics2D
        make(bck, camera)
        stroke(g, theShape)
        fill(g, theShape, camera)
        decorConnector(bck, camera, skel.iconAndText, element, theShape)

        if (showControlPolygon) {
            val c = g.getColor();
            val s = g.getStroke();
            g.setStroke(new java.awt.BasicStroke(camera.metrics.px1.toFloat))
            g.setColor(Color.red);
            g.draw(theShape);
            g.setStroke(s);
            g.setColor(c);
            showCtrlPoints(g, camera, skel.asInstanceOf[ConnectorSkeleton])
        }
    }
}

/** An angular shape. */
class AngleShape extends AreaConnectorShape {
    protected var theShape = new Path2D.Double

    // Command

    protected def make(bck: Backend, camera: Camera) {
        make(camera, 0, 0, 0, 0)
    }

    protected def make(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.isCurve)
            makeOnCurve(camera, sox, soy, swx, swy)
        else if (skel.isPoly)
            makeOnPolyline(camera, sox, soy, swx, swy)
        else makeOnLine(camera, sox, soy, swx, swy)
    }

    protected def makeOnLine(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel.from.x + sox
        val fromy = skel.from.y + soy
        val tox = skel.to.x + sox
        val toy = skel.to.y + soy
        val dir = new Vector2(tox - fromx, toy - fromy)
        val perp = new Vector2(dir.y, -dir.x); perp.normalize // 1/2 perp vector to the from point.

        perp.scalarMult((theSize + swx) / 2f)

        theShape.reset
        theShape.moveTo(fromx + perp.x, fromy + perp.y)
        if (isDirected) {
            theShape.lineTo(tox, toy)
        } else {
            theShape.lineTo(tox + perp.x, toy + perp.y)
            theShape.lineTo(tox - perp.x, toy - perp.y)
        }
        theShape.lineTo(fromx - perp.x, fromy - perp.y)
        theShape.closePath
    }

    protected def makeOnPolyline(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        // TODO
        makeOnLine(camera, sox, soy, swx, swy)
    }

    protected def makeOnCurve(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.isLoop)
            makeLoop(camera, sox, soy, swx, swy)
        else makeMulti(camera, sox, soy, swx, swy)
    }

    protected def makeMulti(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy
        val maindir = new Vector2(c2x - c1x, c2y - c1y)
        val perp = new Vector2(maindir.y, -maindir.x); perp.normalize // 1/2 perp vector to the from point.
        val perp1 = new Vector2(perp.x, perp.y) // 1/2 perp vector to the first control point.
        val perp2 = new Vector2(perp.x, perp.y) // 1/2 perp vector to the second control point.

        perp.scalarMult((theSize + swx) * 0.5f)

        if (isDirected) {
            perp1.scalarMult((theSize + swx) * 0.4f)
            perp2.scalarMult((theSize + swx) * 0.2f)
        } else {
            perp1.scalarMult((theSize + swx) * 0.5f)
            perp2.scalarMult((theSize + swx) * 0.5f)
        }

        //   ctrl1           ctrl2
        //     x---t-------t---x
        //    /                 \
        //   /                   \
        //  X                     X
        // from                  to

        theShape.reset
        theShape.moveTo(fromx + perp.x, fromy + perp.y)
        if (isDirected) {
            theShape.curveTo(c1x + perp1.x, c1y + perp1.y,
                c2x + perp2.x, c2y + perp2.y,
                tox, toy)
            theShape.curveTo(c2x - perp2.x, c2y - perp2.y,
                c1x - perp1.x, c1y - perp1.y,
                fromx - perp.x, fromy - perp.y)
        } else {
            theShape.curveTo(c1x + perp.x, c1y + perp.y,
                c2x + perp.x, c2y + perp.y,
                tox + perp.x, toy + perp.y)
            theShape.lineTo(tox - perp.x, toy - perp.y)
            theShape.curveTo(c2x - perp.x, c2y - perp.y,
                c1x - perp.x, c1y - perp.y,
                fromx - perp.x, fromy - perp.y)
        }
        theShape.closePath
    }

    protected def makeLoop(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy

        val dirFrom = new Vector2(c1x - fromx, c1y - fromy);
        val dirTo = new Vector2(tox - c2x, toy - c2y);
        val mainDir = new Vector2(c2x - c1x, c2y - c1y)

        val perpFrom = new Vector2(dirFrom.y, -dirFrom.x); perpFrom.normalize
        val mid1 = new Vector2(dirFrom); mid1.sub(mainDir); mid1.normalize
        val mid2 = new Vector2(mainDir); mid2.sub(dirTo); mid2.normalize

        perpFrom.scalarMult(theSize * 0.5f)

        if (isDirected) {
            mid1.scalarMult(theSize * 0.8f)
            mid2.scalarMult(theSize * 0.6f)
        } else {
            mid1.scalarMult(theSize * 0.99f)
            mid2.scalarMult(theSize * 0.99f)
        }

        theShape.reset
        theShape.moveTo(fromx + perpFrom.x, fromy + perpFrom.y)
        if (isDirected) {
            theShape.curveTo(c1x + mid1.x, c1y + mid1.y, c2x + mid2.x, c2y + mid2.y, tox, toy)
            theShape.curveTo(c2x - mid2.x, c2y - mid2.y, c1x - mid1.x, c1y - mid1.y, fromx - perpFrom.x, fromy - perpFrom.y)
        } else {
            var perpTo = new Vector2(dirTo.y, -dirTo.x); perpTo.normalize; perpTo.scalarMult(theSize * 0.5f)
            theShape.curveTo(c1x + mid1.x, c1y + mid1.y, c2x + mid2.x, c2y + mid2.y, tox + perpTo.x, toy + perpTo.y)
            theShape.lineTo(tox - perpTo.x, toy - perpTo.y)
            theShape.curveTo(c2x - mid2.x, c2y - mid2.y, c1x - mid1.x, c1y - mid1.y, fromx - perpFrom.x, fromy - perpFrom.y)
        }
        theShape.closePath
    }

    protected def makeShadow(bck: Backend, camera: Camera) {
        if (skel.isCurve)
            makeOnCurve(camera, theShadowOff.x, theShadowOff.y, theShadowWidth.x, theShadowWidth.y)
        else makeOnLine(camera, theShadowOff.x, theShadowOff.y, theShadowWidth.x, theShadowWidth.y)
    }

    def renderShadow(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        makeShadow(bck, camera)
        cast(bck.graphics2D, theShape)
    }

    def render(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        val g = bck.graphics2D
        make(bck, camera)
        stroke(g, theShape)
        // 		fill( g, theSize, theShape, camera )
        fill(g, theShape, camera)
        decorConnector(bck, camera, skel.iconAndText, element, theShape)
    }
}

/** A cubic curve shape. */
class CubicCurveShape extends LineConnectorShape with ShowCubics {
    protected var theShape = new Path2D.Double

    // Command

    protected def make(bck: Backend, camera: Camera) {
        make(camera, 0, 0, 0, 0)
    }

    protected def make(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.multi > 1 || skel.isLoop) // is a loop or a multi edge
             makeMultiOrLoop(camera, sox, soy, swx, swy)
        else if(skel.isPoly && skel.size == 4)
             makeFromPoints(camera, sox, soy, swx, swy) // has points positions
        else makeSingle(camera, sox, soy, swx, swy) // is a single edge.
    }

    protected def makeSingle(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel.from.x + sox
        val fromy = skel.from.y + soy
        val tox = skel.to.x + sox
        val toy = skel.to.y + soy
        val mainDir = new Vector2(skel.from, skel.to)
        val length = mainDir.length
        val angle = mainDir.y / length
        var c1x = 0.0
        var c1y = 0.0
        var c2x = 0.0
        var c2y = 0.0

        if (angle > 0.707107f || angle < -0.707107f) {
            // North or south.
            c1x = fromx + mainDir.x / 2
            c2x = c1x
            c1y = fromy
            c2y = toy
        } else {
            // East or west.
            c1x = fromx
            c2x = tox
            c1y = fromy + mainDir.y / 2
            c2y = c1y
        }

        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)

        // Set the connector as a curve.

        if (sox == 0 && soy == 0) {
            skel.setCurve(
                fromx, fromy, 0,
                c1x, c1y, 0,
                c2x, c2y, 0,
                tox, toy, 0)
        }
    }
    
    protected def makeFromPoints(camera:Camera, sox:Double, soy:Double, swx:Double, swy:Double) {
        val fromx = skel.from.x + sox
        val fromy = skel.from.y + soy
        val tox = skel.to.x + sox
        val toy = skel.to.y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy
        
        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)

        if (sox == 0 && soy == 0) {	// Inform the system this is a curve, not a polyline.
            skel.setCurve(
                fromx, fromy, 0,
                c1x, c1y, 0,
                c2x, c2y, 0,
                tox, toy, 0)
        }
    }

    protected def makeMultiOrLoop(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.isLoop)
            makeLoop(camera, sox, soy, swx, swy)
        else makeMulti(camera, sox, soy, swx, swy)
    }

    protected def makeMulti(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy

        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)
    }

    protected def makeLoop(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy

        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)
    }

    protected def makeShadow(bck: Backend, camera: Camera) {
        if (skel.isCurve)
            makeMultiOrLoop(camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth)
        else makeSingle(camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth)
    }

    def renderShadow(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        makeShadow(bck, camera)
        cast(bck.graphics2D, theShape)
    }

    def render(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        val g = bck.graphics2D
        make(bck, camera)
        stroke(g, theShape)
        fill(g, theSize, theShape)
        decorConnector(bck, camera, skel.iconAndText, element, theShape)
        // 		showControlPolygon = true
        // 		if( showControlPolygon ) {
        //	 		val c = g.getColor();
        //	 		val s = g.getStroke();
        //	 		g.setStroke( new java.awt.BasicStroke( camera.metrics.px1 ) )
        //	 		g.setColor( Color.red );
        //	 		g.draw( theShape );
        //	 		g.setStroke( s );
        //	 		g.setColor( c );
        //	 		showCtrlPoints( g, camera )
        // 		}
    }
}

/** A cubic curve shape that mimics freeplane edges. */
class FreePlaneEdgeShape extends LineConnectorShape {
    protected var theShape = new Path2D.Double

    // Command

    protected def make(bck: Backend, camera: Camera) {
        make(camera, 0, 0, 0, 0)
    }

    protected def make(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {    	
        if (skel.multi > 1 || skel.isLoop) // is a loop or a multi edge
            makeMultiOrLoop(camera, sox, soy, swx, swy)
        else makeSingle(camera, sox, soy, swx, swy) // is a single edge.
    }

    protected def makeSingle(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
try {
        var fromx = skel.from.x + sox
        val fromy = skel.from.y + soy - theSourceSize.y / 2
        var tox = skel.to.x + sox
        val toy = skel.to.y + soy - theTargetSize.y / 2
        val length = abs(skel.to.x - skel.from.x)
        var c1x = 0.0
        var c1y = 0.0
        var c2x = 0.0
        var c2y = 0.0

        if (skel.from.x < skel.to.x) {
            // At right.
            fromx += theSourceSize.x / 2
            tox -= theTargetSize.x / 2
            c1x = fromx + length / 3
            c2x = tox - length / 3
            c1y = fromy
            c2y = toy
        } else {
            // At left.
            fromx -= theSourceSize.x / 2
            tox += theTargetSize.x / 2
            c1x = fromx - length / 3
            c2x = tox + length / 3
            c1y = fromy
            c2y = toy
        }

        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)

        // Set the connector as a curve.

        if (sox == 0 && soy == 0) {
            skel.setCurve(
                fromx, fromy, 0,
                c1x, c1y, 0,
                c2x, c2y, 0,
                tox, toy, 0)
        }
} catch {
	case e:Exception => { Console.err.println("FOUND") }
}
    }

    protected def makeMultiOrLoop(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.isLoop)
            makeLoop(camera, sox, soy, swx, swy)
        else makeMulti(camera, sox, soy, swx, swy)
    }

    protected def makeMulti(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy

        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)
    }

    protected def makeLoop(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy

        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)
    }

    protected def makeShadow(bck: Backend, camera: Camera) {
        if (skel.isCurve)
            makeMultiOrLoop(camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth)
        else makeSingle(camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth)
    }

    def renderShadow(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        makeShadow(bck, camera)
        cast(bck.graphics2D, theShape)
    }

    def render(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        val g = bck.graphics2D
        make(bck, camera)
        stroke(g, theShape)
        fill(g, theSize, theShape)
        decorConnector(bck, camera, skel.iconAndText, element, theShape)
    }
}

class HorizontalSquareEdgeShape extends LineConnectorShape {
    protected var theShape = new Path2D.Double

    // Command

    protected def make(bck: Backend, camera: Camera) {
        make(camera, 0, 0, 0, 0)
    }

    protected def make(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.multi > 1 || skel.isLoop) // is a loop or a multi edge
            makeMultiOrLoop(camera, sox, soy, swx, swy)
        else makeSingle(camera, sox, soy, swx, swy) // is a single edge.
    }

    protected def makeSingle(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val from = new Point3(skel.from.x + sox, skel.from.y + soy, 0)
        val to = new Point3(skel.to.x + sox, skel.to.y + soy, 0)
        val size = (theSourceSize.x + theTargetSize.x)
        var inter1: Point3 = null
        var inter2: Point3 = null
        var inter3: Point3 = null
        var inter4: Point3 = null

        if (to.x > from.x) {
            val len = to.x - from.x

            if (len < size) {
                inter1 = new Point3(from.x + theSourceSize.x, from.y, 0)
                inter2 = new Point3(to.x - theTargetSize.x, to.y, 0)

                inter3 = new Point3(inter1.x, inter1.y + (to.y - from.y) / 2, 0)
                inter4 = new Point3(inter2.x, inter3.y, 0)

                if (sox == 0 && soy == 0)
                    skel.setPoly(from, inter1, inter3, inter4, inter2, to)

            } else {
                val middle = (to.x - from.x) / 2
                inter1 = new Point3(from.x + middle, from.y, 0)
                inter2 = new Point3(to.x - middle, to.y, 0)

                if (sox == 0 && soy == 0)
                    skel.setPoly(from, inter1, inter2, to)
            }
        } else {
            val len = from.x - to.x

            if (len < size) {
                inter1 = new Point3(from.x - theSourceSize.x, from.y, 0)
                inter2 = new Point3(to.x + theTargetSize.x, to.y, 0)

                inter3 = new Point3(inter1.x, inter1.y + (to.y - from.y) / 2, 0)
                inter4 = new Point3(inter2.x, inter3.y, 0)

                if (sox == 0 && soy == 0)
                    skel.setPoly(from, inter1, inter3, inter4, inter2, to)

            } else {
                val middle = (to.x - from.x) / 2
                inter1 = new Point3(from.x + middle, from.y, 0)
                inter2 = new Point3(to.x - middle, to.y, 0)

                if (sox == 0 && soy == 0)
                    skel.setPoly(from, inter1, inter2, to)
            }
        }

        theShape.reset
        theShape.moveTo(from.x, from.y)
        theShape.lineTo(inter1.x, inter1.y)
        if ((inter3 ne null) && (inter4 ne null)) {
            theShape.lineTo(inter3.x, inter3.y)
            theShape.lineTo(inter4.x, inter4.y)
        }
        theShape.lineTo(inter2.x, inter2.y)
        theShape.lineTo(to.x, to.y)
    }

    protected def makeMultiOrLoop(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.isLoop)
            makeLoop(camera, sox, soy, swx, swy)
        else makeMulti(camera, sox, soy, swx, swy)
    }

    protected def makeMulti(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy

        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)
    }

    protected def makeLoop(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy

        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)
    }

    protected def makeShadow(bck: Backend, camera: Camera) {
        if (skel.isCurve)
            makeMultiOrLoop(camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth)
        else makeSingle(camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth)
    }

    def renderShadow(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        makeShadow(bck, camera)
        cast(bck.graphics2D, theShape)
    }

    def render(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        val g = bck.graphics2D
        make(bck, camera)
        stroke(g, theShape)
        fill(g, theSize, theShape)
        decorConnector(bck, camera, skel.iconAndText, element, theShape)
    }
}

class LSquareEdgeShape extends LineConnectorShape {
    protected var theShape = new Path2D.Double

    // Command

    protected def make(bck: Backend, camera: Camera) {
        make(camera, 0, 0, 0, 0)
    }

    protected def make(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.multi > 1 || skel.isLoop) // is a loop or a multi edge
            makeMultiOrLoop(camera, sox, soy, swx, swy)
        else makeSingle(camera, sox, soy, swx, swy) // is a single edge.
    }

    protected def makeSingle(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val from = new Point3(skel.from.x + sox, skel.from.y + soy, 0)
        val to = new Point3(skel.to.x + sox, skel.to.y + soy, 0)
        val mainDir = new Vector2(from, to)
        val length = mainDir.length
        val angle = mainDir.y / length
        var inter: Point3 = null

        if (angle > 0.707107f || angle < -0.707107f) {
            // North or south.
            inter = new Point3(from.x, to.y, 0)
        } else {
            // East or west.
            inter = new Point3(to.x, from.y, 0)
        }

        if (sox == 0 && soy == 0)
            skel.setPoly(from, inter, to)

        theShape.reset
        theShape.moveTo(from.x, from.y)
        theShape.lineTo(inter.x, inter.y)
        theShape.lineTo(to.x, to.y)
    }

    protected def makeMultiOrLoop(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        if (skel.isLoop)
            makeLoop(camera, sox, soy, swx, swy)
        else makeMulti(camera, sox, soy, swx, swy)
    }

    protected def makeMulti(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy

        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)
    }

    protected def makeLoop(camera: Camera, sox: Double, soy: Double, swx: Double, swy: Double) {
        val fromx = skel(0).x + sox
        val fromy = skel(0).y + soy
        val tox = skel(3).x + sox
        val toy = skel(3).y + soy
        val c1x = skel(1).x + sox
        val c1y = skel(1).y + soy
        val c2x = skel(2).x + sox
        val c2y = skel(2).y + soy

        theShape.reset
        theShape.moveTo(fromx, fromy)
        theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy)
    }

    protected def makeShadow(bck: Backend, camera: Camera) {
        if (skel.isCurve)
            makeMultiOrLoop(camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth)
        else makeSingle(camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth)
    }

    def renderShadow(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        makeShadow(bck, camera)
        cast(bck.graphics2D, theShape)
    }

    def render(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        val g = bck.graphics2D
        make(bck, camera)
        stroke(g, theShape)
        fill(g, theSize, theShape)
        decorConnector(bck, camera, skel.iconAndText, element, theShape)
    }
}
object PieChartShape {
    /** Some predefined colors. */
    val colors = Array[Color](Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA,
        Color.CYAN, Color.ORANGE, Color.PINK)
}

class PieChartShape
    extends org.graphstream.ui.j2dviewer.renderer.shape.Shape
    with org.graphstream.ui.j2dviewer.renderer.shape.Area
    with FillableMulticolored
    with Strokable
    with Shadowable
    with Decorable
    with AttributeUtils {

    val theShape = new Ellipse2D.Double

    var theValues: Array[Double] = null
    var valuesRef: AnyRef = null

    def configureForGroup(bck: Backend, style: Style, camera: Camera) {
        configureAreaForGroup(style, camera)
        configureFillableMultiColoredForGroup(style, camera)
        configureStrokableForGroup(style, camera)
        configureShadowableForGroup(style, camera)
        configureDecorableForGroup(style, camera)
    }

    def configureForElement(bck: Backend, element: GraphicElement, skel:Skeleton, camera: Camera) {
        val g = bck.graphics2D
        configureDecorableForElement(bck, camera, element, skel)
        configureAreaForElement(bck, camera, skel.asInstanceOf[AreaSkeleton], element, theDecor)
    }

    override def make(bck: Backend, camera: Camera) {
        //Console.err.println("drawing pie %s [%f %f]".format(theCenter, theCenter.x+theSize.x/2, theCenter.y+theSize.y/2));
        theShape.setFrameFromCenter(theCenter.x, theCenter.y, theCenter.x + theSize.x / 2, theCenter.y + theSize.y / 2)
    }

    override def makeShadow(bck: Backend, camera: Camera) {
        theShape.setFrameFromCenter(theCenter.x + theShadowOff.x, theCenter.y + theShadowOff.y,
            theCenter.x + (theSize.x + theShadowWidth.x) / 2, theCenter.y + (theSize.y + theShadowWidth.y) / 2)
    }

    override def renderShadow(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        makeShadow(bck, camera)
        cast(bck.graphics2D, theShape)
    }

    override def render(bck: Backend, camera: Camera, element: GraphicElement, skel:Skeleton) {
        val g = bck.graphics2D
        make(bck, camera)
        checkValues(element)
        fillPies(g, element)
        //fill(g, theSize, theShape)
        stroke(g, theShape)
        decorArea(bck, camera, skel.iconAndText, element, theShape)
    }

    protected def checkValues(element: GraphicElement) {
        val pieValues: AnyRef = element.getAttribute("ui.pie-values")

        if (pieValues ne null) {
            val oldRef = valuesRef
            valuesRef = pieValues
            // We use valueRef to avoid
            // recreating the values array for nothing.
            //if ((theValues == null) || (oldRef ne valuesRef)) {	// Cannot do this : the array reference can be the same and the values changed.
                theValues = getDoubles(valuesRef)
            //}
        }
    }

    protected def fillPies(g: Graphics2D, element: GraphicElement) {
        if (theValues != null) {
            // we assume the pies values sum up to one. And we wont check it, its a mater of speed ;-).
            val arc = new Arc2D.Double
            var beg = 0.0
            var end = 0.0
            var col = 0
            var sum = 0.0

            theValues.foreach { value =>
                end = beg + value
                arc.setArcByCenter(theCenter.x, theCenter.y, theSize.x / 2, beg * 360, value * 360, Arc2D.PIE)
                g.setColor(fillColors(col % fillColors.length))
                g.fill(arc)
                beg = end
                sum += value
                col += 1
            }

            if (sum > 1.01f)
                Console.err.print("[Sprite %s] The sum of values for ui.pie-value should eval to 1 at max (actually %f)%n".format(element.getId, sum))
        } else {
            // Draw a red empty circle to indicate "no value".
            g.setColor(Color.red)
            g.draw(theShape)
        }
    }
}