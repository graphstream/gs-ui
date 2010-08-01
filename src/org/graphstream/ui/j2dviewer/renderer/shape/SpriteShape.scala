package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt._
import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

import org.graphstream.ui.sgeom._

class SpriteArrowShape extends PolygonalShape with Orientable {
	def make( g:Graphics2D, camera:Camera ) {
		var x   = theCenter.x
		var y   = theCenter.y
		val dir = Vector2( target.x - x, target.y - y ); dir.normalize
		var per = Vector2( dir.y, -dir.x )
//		
//		x = target.x
//		y = target.y
		
		dir.scalarMult( theSize.x )
		per.scalarMult( theSize.y / 2 )

		theShape.reset
		theShape.moveTo( x + per.x, y + per.y )
		theShape.lineTo( x + dir.x, y + dir.y )
		theShape.lineTo( x - per.x, y - per.y )
		theShape.closePath
	}
	
	def makeShadow( g:Graphics2D, camera:Camera ) {
		val x   = theCenter.x + theShadowOff.x
		val y   = theCenter.y + theShadowOff.y
		val dir = Vector2( target.x - x, target.y - y ); dir.normalize
		var per = Vector2( dir.y, -dir.x )
		
		dir.scalarMult( theSize.x + theShadowWidth.x )
		per.scalarMult( ( theSize.y + theShadowWidth.y ) / 2 )

		theShape.reset
		theShape.moveTo( x + per.x, y + per.y )
		theShape.lineTo( x + dir.x, y + dir.y )
		theShape.lineTo( x - per.x, y - per.y )
		theShape.closePath
	}
}