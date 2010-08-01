package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Image, Color, Graphics2D}
import java.awt.geom.{Ellipse2D, Line2D, Path2D, CubicCurve2D, Rectangle2D, RoundRectangle2D, RectangularShape}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._
import org.graphstream.ui.j2dviewer.Camera
import org.graphstream.ui.util.GraphMetrics
import org.graphstream.ui.j2dviewer.renderer.{ElementInfo, NodeInfo, EdgeInfo}

class CircleShape extends RectangularAreaShape {
	val theShape = new Ellipse2D.Float
}

class SquareShape extends RectangularAreaShape {
	val theShape = new Rectangle2D.Float
}

class RoundedSquareShape extends RectangularAreaShape {
	val theShape = new RoundRectangle2D.Float
 
	override def make( g:Graphics2D, camera:Camera ) {
		var w = theSize.x
		var h = theSize.y
		var r = if( h/8 > w/8 ) w/8 else h/8 
  
		theShape.setRoundRect( theCenter.x-w/2, theCenter.y-h/2, w, h, r, r )
	}
	override def makeShadow( g:Graphics2D, camera:Camera ) {
		var x = theCenter.x + theShadowOff.x
		var y = theCenter.y + theShadowOff.y
		var w = theSize.x + theShadowWidth.x * 2
		var h = theSize.y + theShadowWidth.y * 2
		var r = if( h/8 > w/8 ) w/8 else h/8 
		
		theShape.setRoundRect( x-w/2, y-h/2, w, h, r, r )
	}
}

class DiamondShape extends PolygonalShape {
	def make( g:Graphics2D, camera:Camera ) {
		val x  = theCenter.x
		val y  = theCenter.y
		val w2 = theSize.x / 2
		val h2 = theSize.y / 2

		theShape.reset
		theShape.moveTo( x - w2, y )
		theShape.lineTo( x,      y - h2 )
		theShape.lineTo( x + w2, y )
		theShape.lineTo( x,      y + h2 )
		theShape.closePath
	}
	
	def makeShadow( g:Graphics2D, camera:Camera ) {

		val x  = theCenter.x + theShadowOff.x
		val y  = theCenter.y + theShadowOff.y
		val w2 = ( theSize.x + theShadowWidth.x ) / 2
		val h2 = ( theSize.y + theShadowWidth.y ) / 2
		
		theShape.reset
		theShape.moveTo( x - w2, y )
		theShape.lineTo( x,      y - h2 )
		theShape.lineTo( x + w2, y )
		theShape.lineTo( x,      y + h2 )
		theShape.closePath
	}
}

class TriangleShape extends PolygonalShape {
	def make( g:Graphics2D, camera:Camera ) {
		val x  = theCenter.x
		val y  = theCenter.y
		val w2 = theSize.x / 2
		val h2 = theSize.y / 2
		
		theShape.reset
		theShape.moveTo( x,      y + h2 )
		theShape.lineTo( x + w2, y - h2 )
		theShape.lineTo( x - w2, y - h2 )
		theShape.closePath
	}
	
	def makeShadow( g:Graphics2D, camera:Camera ) {
		val x  = theCenter.x + theShadowOff.x
		val y  = theCenter.y + theShadowOff.y
		val w2 = ( theSize.x + theShadowWidth.x ) / 2
		val h2 = ( theSize.y + theShadowWidth.y ) / 2
		
		theShape.reset
		theShape.moveTo( x,      y + h2 )
		theShape.lineTo( x + w2, y - h2 )
		theShape.lineTo( x - w2, y - h2 )
		theShape.closePath
	}
}

class CrossShape extends PolygonalShape {
	def make( g:Graphics2D, camera:Camera ) {
		val x  = theCenter.x
		val y  = theCenter.y
		val h2 = theSize.x / 2
		val w2 = theSize.y / 2
		val w1 = theSize.x * 0.2f
		val h1 = theSize.y * 0.2f
		val w4 = theSize.x * 0.3f
		val h4 = theSize.y * 0.3f
		
		theShape.reset
		theShape.moveTo( x - w2, y + h4 )
		theShape.lineTo( x - w4, y + h2 )
		theShape.lineTo( x,      y + h1 )
		theShape.lineTo( x + w4, y + h2 )
		theShape.lineTo( x + w2, y + h4 )
		theShape.lineTo( x + w1, y )
		theShape.lineTo( x + w2, y - h4 )
		theShape.lineTo( x + w4, y - h2 )
		theShape.lineTo( x,      y - h1 )
		theShape.lineTo( x - w4, y - h2 )
		theShape.lineTo( x - w2, y - h4 )
		theShape.lineTo( x - w1, y )
		theShape.closePath
	}
	
	def makeShadow( g:Graphics2D, camera:Camera ) {
		val x  = theCenter.x + theShadowOff.x
		val y  = theCenter.y + theShadowOff.y
		val h2 = ( theSize.x + theShadowWidth.x ) / 2
		val w2 = ( theSize.y + theShadowWidth.y ) / 2
		val w1 = ( theSize.x + theShadowWidth.x ) * 0.2f
		val h1 = ( theSize.y + theShadowWidth.y ) * 0.2f
		val w4 = ( theSize.x + theShadowWidth.x ) * 0.3f
		val h4 = ( theSize.y + theShadowWidth.y ) * 0.3f
		
		theShape.reset
		theShape.moveTo( x - w2, y + h4 )
		theShape.lineTo( x - w4, y + h2 )
		theShape.lineTo( x,      y + h1 )
		theShape.lineTo( x + w4, y + h2 )
		theShape.lineTo( x + w2, y + h4 )
		theShape.lineTo( x + w1, y )
		theShape.lineTo( x + w2, y - h4 )
		theShape.lineTo( x + w4, y - h2 )
		theShape.lineTo( x,      y - h1 )
		theShape.lineTo( x - w4, y - h2 )
		theShape.lineTo( x - w2, y - h4 )
		theShape.lineTo( x - w1, y )
		theShape.closePath
	}
}

class FreePlaneNodeShape extends RectangularAreaShape {
	val theShape = new Rectangle2D.Float
	val theLineShape = new Line2D.Float 
 
	override def make( g:Graphics2D, camera:Camera ) {
		var w = theSize.x
		val h = theSize.y
		val x = theCenter.x
		val y = theCenter.y

		theShape.setRect( x-w/2, y-h/2, w, h )
		
		w -= theStrokeWidth
		
		theLineShape.setLine( x-w/2, y-h/2, x+w/2, y-h/2 )
	}

	override def makeShadow( g:Graphics2D, camera:Camera ) {
		var x = theCenter.x + theShadowOff.x
		var y = theCenter.y + theShadowOff.y
		var w = theSize.x + theShadowWidth.x * 2
		var h = theSize.y + theShadowWidth.y * 2
		
		theShape.setRect( x-w/2, y-h/2, w, h )
		theLineShape.setLine( x-w/2, y-h/2, x+w/2, y-h/2 )
	}

	override def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( g, camera )
 		fill( g, theShape, camera )
 		stroke( g, theLineShape )
 		decor( g, camera, info.iconAndText, element, theShape )
 	}
}