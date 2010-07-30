package org.graphstream.ui.util

import org.graphstream.ui.geom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.j2dviewer.renderer.shape._

object CubicCurve {
	/**
	 * Evaluate a cubic curve according to control points (x) and return the position at
	 * a given "percent" (t) of the curve.
	 * @param x0 The first control point.
	 * @param x1 The second control point.
	 * @param x2 The third control point.
	 * @param x3 The fourth control point.
	 * @param t The percent on the curve (between 0 and 1 included).
	 * @return The coordinate at t percent on the curve.
	 */
	def eval( x0:Float, x1:Float, x2:Float, x3:Float, t:Float ):Float = {
		val tt = ( 1f - t )
		
		x0 * (tt*tt*tt) + 3f * x1 * t * (tt*tt) + 3f * x2 * (t*t) * tt + x3 * (t*t*t)
	}

	/**
	 * Evaluate a cubic curve according to control points (x) and return the position at
	 * a given "percent" (t) of the curve.
	 * @param p0 The first control point.
	 * @param p1 The second control point.
	 * @param p2 The third control point.
	 * @param p3 The fourth control point.
	 * @param t The percent on the curve (between 0 and 1 included).
	 * @return The coordinates at t percent on the curve.
	 */
	def eval( p0:Point2, p1:Point2, p2:Point2, p3:Point2, t:Float ):Point2 = {
		new Point2( eval( p0.x, p1.x, p2.x, p3.x, t ),
		            eval( p0.y, p1.y, p2.y, p3.y, t ) )
	}

	/** 
	 * A quick and dirty hack to evaluate the length of a cubic curve. This method simply compute
	 * the length of the three segments of the enclosing polygon and scale them. This is fast but
	 * inaccurate.
	 */
	def approxLengthOfCurveQuickAndDirty( c:Connector ):Float = {
		// Computing a curve real length is really heavy.
		// We approximate it using the length of the 3 line segments of the enclosing
		// control points.
		( c.fromPos.distance( c.byPos1 )*0.5f + c.byPos1.distance( c.byPos2 )*0.8f + c.byPos2.distance( c.toPos )*0.5f )
	}
	
	/**
	 * Evaluate the length of a curve by taking four points on the curve and summing the lengths of
	 * the five segments thus defined.
	 */
	def approxLengthOfCurveQuick( c:Connector ):Float = {
		val ip0 = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, 0.1f )
		val ip1 = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, 0.3f )
		val ip2 = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, 0.7f )
		val ip3 = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, 0.9f )
		
		( c.fromPos.distance( ip0 ) + ip0.distance( ip1 ) + ip1.distance( ip2 ) + ip2.distance( ip3 ) + ip3.distance( c.toPos ) )
	}
	
	/**
	 * Evaluate the length of a curve by taking n points on the curve and summing the lengths of
	 * the n+1 segments thus defined.
	 */
	def approxLengthOfCurve( c:Connector ):Float = {
		val inc = 0.1f
		var i   = inc
		var len = 0f
		var p0  = c.fromPos
		
		while( i < 1f ) {
			val p = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, i )
			i += inc
			len += p0.distance( p )
			p0 = p
		}
		
		len += p0.distance( c.toPos )
		
		len
	}
	
	/**
	 * Return two points, one inside and the second outside of the shape of the destination node
	 * of the given `edge`, the points can be used to deduce a vector along the curve entering
	 * point in the shape.
	 */
	def approxVectorEnteringCurve( edge:GraphicEdge, c:Connector, camera:Camera ):(Point2, Point2) = {
		val node = edge.to
		val info = node.getAttribute( ElementInfo.attributeName ).asInstanceOf[NodeInfo]
		var w    = 0f
		var h    = 0f
		
		if( info != null ) {
			w = info.theSize.x
			h = info.theSize.y
		} else {
			w = camera.metrics.lengthToGu( node.getStyle.getSize, 0 )
			h = if( node.getStyle.getSize.size > 1 ) camera.metrics.lengthToGu( node.getStyle.getSize, 1 ) else w
		}
		
		var searching = true
		var p0        = c.fromPos
		var p1        = c.toPos
		val inc       = 0.1f 
		var i         = inc
		
		while( searching ) {
			p1 = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, i )
			
			if( ShapeUtil.isPointIn( node, p1, w, h ) ) {
				searching = false
			} else {
				p0 = p1
			}
		}
		
		(p0, p1)
	}
	
	/**
	 * Use a dychotomy methode to evaluate the intersection between the `edge` destination node
	 * shape and the curve of the connector `c` given. The returned values are the point of
	 * intersection as well as the parametric position of this point on the curve (a float).
	 */
	def approxIntersectionPointOnCurve( edge:GraphicEdge, c:Connector, camera:Camera ):(Point2,Float) = {
		val node = edge.to
		val info = node.getAttribute( ElementInfo.attributeName ).asInstanceOf[NodeInfo]
		var w    = 0f
		var h    = 0f
		
		if( info != null ) {
			w = info.theSize.x
			h = info.theSize.y
		} else {
			w = camera.metrics.lengthToGu( node.getStyle.getSize, 0 )
			h = if( node.getStyle.getSize.size > 1 ) camera.metrics.lengthToGu( node.getStyle.getSize, 1 ) else w
		}
			
		val maxDepth  = 7
		var searching = true
		var p         = c.toPos//        = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, 0.5f )
		var tbeg      = 0f
		var tend      = 1f
		var t         = 0f
		var depth     = 0
		
		while( depth < maxDepth ) {
			t = tbeg + ( (tend - tbeg ) / 2 )
			p = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, t )
			
			if( ShapeUtil.isPointIn( node, p, w, h ) ) {
				tend= t
			} else {
				tbeg = t
			}
			
			depth += 1
		}
		
		(p, t)
	}
}