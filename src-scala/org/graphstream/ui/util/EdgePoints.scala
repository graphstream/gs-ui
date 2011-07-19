package org.graphstream.ui.j2dviewer.util

import org.graphstream.ui.geom._

/** A set of points defining the topology of an edge. */
class EdgePoints(n:Int) {
	protected var points = new Array[Point3]( n )
	
	def size:Int = points.size
	
	def copy(newPoints:Array[Point3]) { points = newPoints.map { p => new Point3(p) } }
//	def copy(newPoints:Array[org.graphstream.ui.geom.Point3]) {
//	    points = newPoints.map { p => new Point3(p.x, p.y, p.z) }
//	}
	
	for(i <- 0 until size)
		points(i) = new Point3
	
	/** Set the `i`-th point of the set to be (`x`,`y`). */
	def set(i:Int, x:Double, y:Double, z:Double) = points(i).set( x, y, z)
	
	/** The `i`-th point of the set. */
	def get(i:Int):Point3 = points(i)
	
	def apply(i:Int):Point3 = points(i)
	
	def update(i:Int, coos:Point3) = points(i) = new Point3(coos.x, coos.y, coos.z) 
}