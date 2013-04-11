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
package org.graphstream.ui.j2dviewer.renderer

import org.graphstream.ui.util.AttributeUtils
import org.graphstream.ui.geom.Vector3
import org.graphstream.ui.swingViewer.util.CubicCurve
import org.graphstream.ui.geom.Point3
import java.awt.Graphics2D
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph.{GraphicElement, GraphicNode, StyleGroup}
import org.graphstream.ui.j2dviewer.{Camera, J2DGraphRenderer, Backend}
import org.graphstream.ui.j2dviewer.renderer.shape.swing.IconAndText
import org.graphstream.ui.util.EdgePoints

object Skeleton {
	def attributeName = "j2dsk"
}

/** Elements of rendering that, contrary to the shapes, are specific to the element, not the style
  * group and define the basic geometry of the shape. */
class Skeleton {
    /** The contents of the element. Allows to extract metrics of the contents. */
	var iconAndText:IconAndText = null
}

/** Skeleton for nodes and sprites. */
class AreaSkeleton extends Skeleton {
    var theCenter = new Point2(0, 0)
	var theSize = new Point2(0, 0)
}

/** Skeleton for edges.
  * Data stored on the edge to retrieve the edge basic geometry and various shared data between
  * parts of the renderer.
  * 
  * XXX TODO
  * This part needs much work. The skeleton geometry of an edge can be various things:
  *  - An automatically computed shape (for multi-graphs and loop edges).
  *  - An user specified shape:
  *     - A polyline (points are in absolute coordinates).
  *     - A polycurve (in absolute coordinates).
  *     - A vector representation (points are relative to an origin and the whole may be rotated).  
  */
class ConnectorSkeleton extends Skeleton with AttributeUtils {
    object EdgeShapeKind extends Enumeration {
        type EdgeShapeKind = Value
        val LINE = Value
        val CURVE = Value
        val POLYLINE = Value
    }
    
    import EdgeShapeKind._
    
	private[this] var points = new EdgePoints( 2 )	// we assume a line.
	private[this] var lengths:Array[Double] = null
	private[this] var lengthsSum:Double = -1
	private[this] var kind = LINE
	private[this] var isACurve = false
	private[this] var aMulti = 1
	private[this] var isALoop = false
	private[this] var ptsRef:AnyRef = null	// used to avoid recomputing the point set
	
	override def toString():String = "CtorSkel(%s, {%s})".format(kindString, points.toString)
	
	def kindString():String = {
    	kind match {
    		case EdgeShapeKind.POLYLINE => "polyline"
    		case EdgeShapeKind.CURVE => "curve"
    		case _ => "line"
    	}
    }
	
	/** If true the edge shape is a polyline made of size points. */
	def isPoly = kind == EdgeShapeKind.POLYLINE
	
	/** If true the edge shape is a loop defined by four points. */
	def isCurve = kind == EdgeShapeKind.CURVE
	
	/** If larger than one there are several edges between the two nodes of this edge. */
	def multi:Int = aMulti
	
	/** This is only set when the edge is a curve, if true the starting and
	 * ending nodes of the edge are the same node. */
	def isLoop = isALoop
	
	def setPoly(aSetOfPoints:AnyRef) {
        if((ptsRef ne aSetOfPoints) || (kind ne POLYLINE)) {
        	kind = POLYLINE;
        	val thePoints = getPoints(aSetOfPoints)
        	points = new EdgePoints(thePoints.size)
        	points.copy(thePoints)
        	lengths = null
        }
    }
    
    def setPoly(aSetOfPoints:Point3*) {
        if((points eq null) || (points.size != aSetOfPoints.size)) {
            points = new EdgePoints(aSetOfPoints.size)
        }
        
        kind = POLYLINE
        var i=0
        
        aSetOfPoints.foreach { point =>
        	points.set(i, point.x, point.y, point.z)
        	i += 1
        }
    }
	
	def setCurve(
	        x0:Double, y0:Double, z0:Double,
	        x1:Double, y1:Double, z1:Double,
	        x2:Double, y2:Double, z2:Double,
	        x3:Double, y3:Double, z3:Double ) {
	    kind   = CURVE
	    points = if(points.size!=4) new EdgePoints(4) else points
	    ptsRef = null
	    points(0) = new Point3(x0, y0, z0)
	    points(1) = new Point3(x1, y1, z1)
	    points(2) = new Point3(x2, y2, z2)
	    points(3) = new Point3(x3, y3, z3)
	}
	
	def setLine(
	        x0:Double, y0:Double, z0:Double,
	        x1:Double, y1:Double, z1:Double) {
	    kind      = LINE
	    points    = if(points.size!=2) new EdgePoints(2) else points
	    ptsRef    = null
	    points(0) = new Point3(x0, y0, z0)
	    points(1) = new Point3(x1, y1, z1)
	}
	
	def setMulti(i:Int) { aMulti = i }
	
	def isMulti:Boolean = multi > 1
	
	def setLoop(
	        x0:Double, y0:Double, z0:Double,
	        x1:Double, y1:Double, z1:Double,
	        x2:Double, y2:Double, z2:Double ) {
	    kind      = CURVE
	    points    = if(points.size!=4) new EdgePoints(4) else points
	    ptsRef    = null
	    isALoop   = true
	    points(0) = new Point3(x0, y0, z0)
	    points(1) = new Point3(x1, y1, z1)
	    points(2) = new Point3(x2, y2, z2)
	    points(3) = new Point3(x0, y0, z0)
	}
	
//	def setNotLoop() { isALoop = false }
 	
	/** The number of points in the edge shape. */
	def size:Int = points.size
	
	/** The i-th point of the edge shape. */
	def apply(i:Int):Point3 = points(i)
	
	/** Change the i-th point in the set of points making up the shape of this edge. */
	def update(i:Int, p:Point3) {
	    points(i) = p 
	    lengths = null
	}
	
	/** The last point of the edge shape. */
	def to:Point3 = points(points.size-1)

	/** The first point of the edge shape. */
	def from:Point3 = points(0)
	
	/**
	 * Total length of the polyline defined by the points.
	 */
	def length:Double = {
	    if(lengths==null)
	        segmentsLengths
	        
	    lengthsSum
	}
	
	/** Compute the length of each segment between the points making up this edge. This is mostly
	  * only useful for polylines. The results of this method is cached. It is only recomputed when
	  * a points changes in the shape. There are size-1 segments if the are size points. The segment
	  * 0 is between points 0 and 1. */
	def segmentsLengths():Array[Double] = {
	    if(lengths eq null) {
	        if(isPoly) {
			    val n = points.size
			    lengthsSum = 0
			    if(n > 0) {
			    	lengths = new Array[Double](points.size - 1)
			    	var prev = points(0)
			    	var next:Point3 = null
			    	
			    	for(i <- 1 until n) {
			    	    next = points(i)
			    	    lengths(i-1) = next.distance(prev)
			    	    lengthsSum += lengths(i-1)
			    	    prev = next
			    	}
			    } else {
			        lengths = new Array[Double](0)
			    }
	        } else if(isCurve) {
	            throw new RuntimeException("segmentsLengths for curve ....")
	        } else {
	            lengths = new Array[Double](1)
	            lengths(0) = points(0).distance(points(3))
	            lengthsSum = lengths(0)
	        }
	    }
	    
	    lengths
	}
	
	/** Length of the i-th segment. There are size-1 segments if there are size points. The segment
	 * 0 is between points 0 and 1. */
	def segmentLength(i:Int):Double = segmentsLengths()(i)
	
	/** Compute a point at the given percent on the shape and return it.
	 * The percent must be a number between 0 and 1. */
	def pointOnShape(percent:Double):Point3 = pointOnShape(percent, new Point3)
	
	/** Compute a point at a given percent on the shape and store it in the target,
	 * also returning it. The percent must be a number between 0 and 1. */
	def pointOnShape(percent:Double, target:Point3):Point3 = {
		var at = if(percent > 1) 1 else percent
		at = if(at < 0) 0 else at
		
		if(isCurve ) {
			CubicCurve.eval(points(0), points(1), points(2), points(3), at, target )
		} else if( isPoly ) {
		    var (i, sum, ps) = wichSegment(at)
		    val dir = new Vector3(points(i+1).x-points(i).x, points(i+1).y-points(i).y, 0)
		    dir.scalarMult(ps)
		    target.set(points(i).x+dir.data(0), points(i).y+dir.data(1), points(i).z)
		} else {
			val dir = new Vector3(to.x-from.x, to.y-from.y, 0)
			dir.scalarMult(at)
			target.set(from.x + dir.data(0), from.y + dir.data(1))
		}
		
		target
	}
	
	/** Compute a point at a given percent on the shape and push it from the shape perpendicular
	 * to it at a given distance in GU. The percent must be a number between 0 and 1. The resulting
	 * points is returned. */
	def pointOnShapeAndPerpendicular(percent:Double, perpendicular:Double):Point3 =
	    pointOnShapeAndPerpendicular(percent, perpendicular, new Point3)

	/** Compute a point at a given percent on the shape and push it from the shape perpendicular
	 * to it at a given distance in GU. The percent must be a number between 0 and 1. The result
	 * is stored in target and also returned. */
	def pointOnShapeAndPerpendicular(percent:Double, perpendicular:Double, target:Point3):Point3 = {
		var at = if(percent > 1) 1 else percent
		at = if(at < 0) 0 else at

	    if(isCurve) {
	        val p0   = points(0)
  			val p1   = points(1)
  			val p2   = points(2)
  			val p3   = points(3)
  			val perp = CubicCurve.perpendicular(p0, p1, p2, p3, at)
  			
  			perp.normalize
  			perp.scalarMult(perpendicular)
  			
  			target.x = CubicCurve.eval(p0.x, p1.x, p2.x, p3.x, at) - perp.data(0)
  			target.y = CubicCurve.eval(p0.y, p1.y, p2.y, p3.y, at) - perp.data(1)
  			target.z = 0
	    } else if(isPoly) {
		    var (i, sum, ps) = wichSegment(at)
		    val dir  = new Vector3(points(i+1).x-points(i).x, points(i+1).y-points(i).y, 0)
		    val perp = new Vector3(dir.data(1), -dir.data(0), 0)
		    
		    perp.normalize
		    perp.scalarMult(perpendicular)
		    dir.scalarMult(ps)
		    target.set(points(i).x+dir.data(0)+perp.data(0), points(i).y+dir.data(1)+perp.data(1), points(i).z)
	    } else {
			val dir  = new Vector3(to.x-from.x, to.y-from.y, 0)
			val perp = new Vector3(dir.data(1), -dir.data(0), 0)

			perp.normalize
			perp.scalarMult(perpendicular)
			dir.scalarMult(at)
			target.set(from.x + dir.data(0) + perp.data(0), from.y + dir.data(1) + perp.data(1), from.z)
	    }

	    target
	}

	/** On which segment of the line shape is the value at. The value at must be between 0 and 1
	 * and expresses a percentage on the shape. There are size-1 segments if size is the number
	 * of points of the shape. The segment 0 is between points 0 and 1. This method both compute
	 * the index of the segment, but also the sum of the previous segments lengths (not including
	 * the i-th segment), as well as the percent on the segment (a number in 0..1). */
	def wichSegment(at:Double):(Int, Double, Double) = {
	    val n   = size-1		// n-1 segments, for n points
		val pos = length * at	// Length is the sum of all segments lengths
		var sum = lengths(0)
		var i   = 0
	
		while(pos > sum) {
		    i   += 1
			sum += lengths(i)
		}

		assert(i>=0 && i<n)
	    
	    sum -= lengths(i)
	    
		(i, sum, (pos-sum)/lengths(i))
	}
}