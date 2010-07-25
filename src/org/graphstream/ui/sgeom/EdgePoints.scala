package org.graphstream.ui.sgeom

import org.graphstream.ui.geom.Point2

/** A set of points defining the topology of an edge. */
class EdgePoints( size:Int ) {
	protected val points = new Array[Point2]( size )
	
	for( i <- 0 until size )
		points(i) = new Point2
	
	/** Set the `i`-th point of the set to be (`x`,`y`). */
	def set( i:Int, x:Float, y:Float ) = points(i).set( x, y )
	
	/** The `i`-th point of the set. */
	def get( i:Int ):Point2 = points(i)
	
	def apply( i:Int  ):Point2 = points(i)
	
	def update( i:Int, coos:Point2 ) = points(i) = new Point2( coos.x, coos.y ) 
}