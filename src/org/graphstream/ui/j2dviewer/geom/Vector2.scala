package org.graphstream.ui.j2dviewer.geom

import org.graphstream.ui.geom.Point2

class Vector2( x:Float, y:Float ) extends org.graphstream.ui.geom.Vector2( x, y ) {

	def this( from:Point2, to:Point2 ) {
		this( to.x - from.x, to.y - from.y )
	}

	def this() { this( 0, 0 ) }
 
	def apply( component:Int ):Float = data( component )

	def update( component:Int, value:Float ) { data(component) = value }
 
	def x:Float = data(0)
	
	def y:Float = data(1)
}

object Vector2 {
	def apply( x:Float, y:Float ):Vector2 = new Vector2( x, y )

	def apply( p0:Point2, p1:Point2 ):Vector2 = new Vector2( p0, p1 )
 
	def apply( p0:Point2, p1:Point2, p2:Point2 ):Vector2 = new Vector2(
			( p1.x - p0.x ) + ( p2.x - p1.x ),
			( p1.y - p0.y ) + ( p2.y - p1.y )
		)

	def apply( p0:Point2, p1:Point2, p2:Point2, p3:Point2 ):Vector2 = new Vector2(
			( p1.x - p0.x ) + ( p2.x - p1.x ) + ( p3.x - p2.x ),
			( p1.y - p0.y ) + ( p2.y - p1.y ) + ( p3.y - p2.y )
		)
}
