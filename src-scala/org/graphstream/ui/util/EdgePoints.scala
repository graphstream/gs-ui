package org.graphstream.ui.util

import org.graphstream.ui.geom._

/** A set of points defining the topology of an edge. */
class EdgePoints(n:Int) {
	protected var points = new Array[Point3](n)
	
	for(i <- 0 until size)
		points(i) = new Point3
	
	/** Number of points. */
	def size:Int = points.size

	/** Copy a set of points into this set (points are fully copied). */
	def copy(newPoints:Array[Point3]) { points = newPoints.map { p => new Point3(p) } }
	
	/** Set the `i`-th point of the set to be (`x`,`y`). */
	def set(i:Int, x:Double, y:Double, z:Double) = points(i).set( x, y, z)
	
	/** The `i`-th point of the set. */
	def get(i:Int):Point3 = points(i)
	
	def apply(i:Int):Point3 = points(i)
	
	def update(i:Int, coos:Point3) = points(i) = new Point3(coos.x, coos.y, coos.z) 
}