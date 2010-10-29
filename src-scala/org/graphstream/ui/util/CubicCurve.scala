package org.graphstream.ui.util

import org.graphstream.ui.geom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.j2dviewer.renderer.shape._

import java.awt.geom._

/** Utility methods to deal with Bézier cubic curves. */
object CubicCurve {
	/** Evaluate a cubic Bézier curve according to control points `x0`, `x1`, `x2` and `x3` and 
	 * return the position at parametric position `t` of the curve.
	 * @return The coordinate at parametric position `t` on the curve. */
	def eval( x0:Float, x1:Float, x2:Float, x3:Float, t:Float ):Float = {
		val tt = ( 1f - t )
		
		x0 * (tt*tt*tt) + 3f * x1 * t * (tt*tt) + 3f * x2 * (t*t) * tt + x3 * (t*t*t)
	}

	/** Evaluate a cubic Bézier curve according to control points `p0`, `p1`, `p2` and `p3` and 
	 * return the position at parametric position `t` of the curve.
	 * @return The point at parametric position `t` on the curve. */
	def eval( p0:Point2, p1:Point2, p2:Point2, p3:Point2, t:Float ):Point2 = {
		new Point2( eval( p0.x, p1.x, p2.x, p3.x, t ),
		            eval( p0.y, p1.y, p2.y, p3.y, t ) )
	}

	/** Evaluate a cubic Bézier curve according to control points `p0`, `p1`, `p2` and `p3` and 
	 * return the position at parametric position `t` of the curve.
	 * @return The point at parametric position `t` on the curve. */
	def eval( p0:Point2D.Float, p1:Point2D.Float, p2:Point2D.Float, p3:Point2D.Float, t:Float ):Point2D.Float = {
		new Point2D.Float( eval( p0.x, p1.x, p2.x, p3.x, t ),
		                    eval( p0.y, p1.y, p2.y, p3.y, t ) )
	}
	
	/** Evaluate a cubic Bézier curve according to control points `p0`, `p1`, `p2` and `p3` and 
	 * store the position at parametric position `t` of the curve in `result`.
	 * @return the given reference to `result`. */
	def eval( p0:Point2, p1:Point2, p2:Point2, p3:Point2, t:Float, result:Point2 ):Point2 = {
		result.set( eval( p0.x, p1.x, p2.x, p3.x, t ),
		            eval( p0.y, p1.y, p2.y, p3.y, t ) )
		result
	}
	
	/** Derivative of a cubic Bézier curve according to control points `x0`, `x1`, `x2` and `x3` 
	 * at parametric position `t` of the curve.
	 * @return The derivative at parametric position `t` on the curve. */
	def derivative( x0:Float, x1:Float, x2:Float, x3:Float, t:Float ):Float = {
//A = x3 - 3 * x2 + 3 * x1 - x0
//B = 3 * x2 - 6 * x1 + 3 * x0
//C = 3 * x1 - 3 * x0
//D = x0
//Vx = 3At2 + 2Bt + C 
		3 * ( x3 - 3 * x2 + 3 * x1 - x0 ) * t*t +
		2 * ( 3 * x2 - 6 * x1 + 3 * x0 ) * t +
		( 3 * x1 - 3 * x0 )
	}
	
	/** Derivative point of a cubic Bézier curve according to control points `x0`, `x1`, `x2` and
	 * `x3` at parametric position `t` of the curve.
	 * @return The derivative point at parametric position `t` on the curve. */
	def derivative( p0:Point2, p1:Point2, p2:Point2, p3:Point3, t:Float ):Point2 = {
		new Point2( derivative( p0.x, p1.x, p2.x, p3.x, t ), derivative( p0.y, p1.y, p2.y, p3.y, t ) )
	}

	/** Store in `result` the derivative point of a cubic Bézier curve according to control points
	 * `x0`, `x1`, `x2` and `x3` at parametric position `t` of the curve.
	 * @return the given reference to `result`. */
	def derivative( p0:Point2, p1:Point2, p2:Point2, p3:Point3, t:Float, result:Point2 ):Point2 = {
		result.set( derivative( p0.x, p1.x, p2.x, p3.x, t ), derivative( p0.y, p1.y, p2.y, p3.y, t ) )
		result
	}

	/** The perpendicular vector to the curve defined by control points `p0`, `p1`, `p2` and `p3`
	 * at parametric position `t`.
	 * @return A vector perpendicular to the curve at position `t`. */
	def perpendicular( p0:Point2, p1:Point2, p2:Point2, p3:Point2, t:Float ):Vector2 = {
		new Vector2( derivative( p0.y, p1.y, p2.y, p3.y, t ), -derivative( p0.x, p1.x, p2.x, p3.x, t ) )
	}

	/** Store in `result` the perpendicular vector to the curve defined by control points `p0`,
	 * `p1`, `p2` and `p3`  at parametric position `t`.
	 * @return the given reference to `result`. */
	def perpendicular( p0:Point2, p1:Point2, p2:Point2, p3:Point2, t:Float, result:Vector2 ):Vector2 = {
		result.set( derivative( p0.y, p1.y, p2.y, p3.y, t ), -derivative( p0.x, p1.x, p2.x, p3.x, t ) )
		result
	}
	
	/** The perpendicular vector to the curve defined by control points `p0`, `p1`, `p2` and `p3`
	 * at parametric position `t`.
	 * @return A vector perpendicular to the curve at position `t`. */
	def perpendicular( p0:Point2D.Float, p1:Point2D.Float, p2:Point2D.Float, p3:Point2D.Float, t:Float ):Point2D.Float = {
		new Point2D.Float( derivative( p0.y, p1.y, p2.y, p3.y, t ), -derivative( p0.x, p1.x, p2.x, p3.x, t ) )
	}
	
	/** A quick and dirty hack to evaluate the length of a cubic bezier curve. This method simply compute
	 * the length of the three segments of the enclosing polygon and scale them. This is fast but
	 * inaccurate. */
	def approxLengthOfCurveQuickAndDirty( c:Connector ):Float = {
		// Computing a curve real length is really heavy.
		// We approximate it using the length of the 3 line segments of the enclosing
		// control points.
		( c.fromPos.distance( c.byPos1 )*0.5f + c.byPos1.distance( c.byPos2 )*0.8f + c.byPos2.distance( c.toPos )*0.5f )
	}
	
	/** Evaluate the length of a Bézier curve by taking four points on the curve and summing the lengths of
	 * the five segments thus defined. */
	def approxLengthOfCurveQuick( c:Connector ):Float = {
		val ip0 = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, 0.1f )
		val ip1 = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, 0.3f )
		val ip2 = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, 0.7f )
		val ip3 = CubicCurve.eval( c.fromPos, c.byPos1, c.byPos2, c.toPos, 0.9f )
		
		( c.fromPos.distance( ip0 ) + ip0.distance( ip1 ) + ip1.distance( ip2 ) + ip2.distance( ip3 ) + ip3.distance( c.toPos ) )
	}
	
	/** Evaluate the length of a Bézier curve by taking n points on the curve and summing the lengths of
	 * the n+1 segments thus defined. */
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
	
	/** Return two points, one inside and the second outside of the shape of the destination node
	 * of the given `edge`, the points can be used to deduce a vector along the Bézier curve entering
	 * point in the shape. */
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
	
	/** Use a dichotomy method to evaluate the intersection between the `edge` destination node
	 * shape and the Bézier curve of the connector `c`. The returned values are the point of
	 * intersection as well as the parametric position of this point on the curve (a float).
	 * The maximal recursive depth of the dichotomy is fixed to 7 here.
	 * @return A 2-tuple made of the point of intersection and the associated parametric position.
	 */
	def approxIntersectionPointOnCurve( edge:GraphicEdge, c:Connector, camera:Camera ):(Point2,Float) = 
		approxIntersectionPointOnCurve( edge, c, camera, 7 )
		
	/** Use a dichotomy method to evaluate the intersection between the `edge` destination node
	 * shape and the Bézier curve of the connector `c`. The returned values are the point of
	 * intersection as well as the parametric position of this point on the curve (a float).
	 * The dichotomy can recurse at any level to increase precision, often 7 is sufficient, the
	 * `maxDepth` parameter allows to set this depth.
	 * @return A 2-tuple made of the point of intersection and the associated parametric position.
	 */
	def approxIntersectionPointOnCurve( edge:GraphicEdge, c:Connector, camera:Camera, maxDepth:Int ):(Point2,Float) = {
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
	
// =================================================================================================
// A simple test for the cubic curve eval, derivative and perpendicular methods.	
// =================================================================================================
	
	import javax.swing._
	import java.awt._
	
	def main( args:Array[String] ) {

		val frame = new JFrame("Test Beziers")
		val canvas = new MyCanvas()
		
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE )
		frame.add( canvas, BorderLayout.CENTER )
		frame.setSize( 400, 420 )
		frame.setVisible( true )
	}
	
	class MyCanvas extends JPanel {
		override def paint( gg:Graphics ) {
			val g  = gg.asInstanceOf[Graphics2D]
			val P0 = new Point2D.Float( 10, 390 )
			val P1 = new Point2D.Float( 50, 10 )
			val P2 = new Point2D.Float( 350, 390 )
			val P3 = new Point2D.Float( 390, 10 )
			
			val curve = new CubicCurve2D.Float
			val line  = new Line2D.Float
			curve.setCurve( P0, P1, P2, P3 )
			
			g.setColor( Color.BLUE )
			g.draw( curve )
			g.setColor( Color.RED )
			
			line.setLine( P0, P1 )
			g.draw( line )
			line.setLine( P1, P2 )
			g.draw( line )
			line.setLine( P2, P3 )
			g.draw( line )
			
			var t = 0f;
			
			g.setColor( Color.GREEN )
			while( t < 1 ) {
				val P = eval( P0, P1, P2, P3, t )
				val V = perpendicular( P0, P1, P2, P3, t )
				val T = new Point2D.Float( P.x+V.x, P.y+V.y )
				
				line.setLine( P, T )
				g.draw( line )
				
				t += 0.01f
			}
		}
	}
}