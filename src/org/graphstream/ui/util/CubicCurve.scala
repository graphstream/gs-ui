package org.graphstream.ui.util

import org.graphstream.ui.geom.Point2

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
}