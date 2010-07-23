package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Image, Color, Graphics2D}
import java.awt.geom.{Ellipse2D, Line2D, Path2D, CubicCurve2D, Rectangle2D, RoundRectangle2D, RectangularShape}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.j2dviewer.Camera
import org.graphstream.ui.util.GraphMetrics


/**
 * Base for shapes centered around one point.
 */
trait AreaShape
	extends Shape
	with Area
	with Fillable 
	with Strokable 
	with Shadowable 
	with Decorable

trait OrientedAreaShape
	extends Shape
	with OrientedArea
	with Fillable
	with Strokable
	with Shadowable
 
trait AreaOnConnectorShape
	extends Shape
	with AreaOnConnector
	with Fillable
	with Strokable
	with Shadowable
 
/**
 * Base for shapes rendered between two points.
 */
trait ConnectorShape
	extends Shape
	with Connector
	with Decorable
 
trait LineConnectorShape
	extends ConnectorShape
	with FillableLine
	with StrokableLine
	with ShadowableLine

trait AreaConnectorShape
	extends ConnectorShape
	with Fillable
	with Strokable
	with Shadowable

trait RectangularAreaShape extends AreaShape {
	val theShape:RectangularShape
 
 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
 	  	configureFillable( style, camera, element )
 	  	configureShadowable( style, camera )
 	  	configureStrokable( style, camera )
 	  	configureDecorable( style, camera )
 	}
 
 	protected def make( camera:Camera ) {
		val w = theSize.x
		val h = theSize.y
		
		theShape.setFrame( theCenter.x-w/2, theCenter.y-h/2, w, h )
 	}
 
 	protected def makeShadow( camera:Camera ) {
		val x = theCenter.x + theShadowOff.x
		val y = theCenter.y + theShadowOff.y
		val w = theSize.x + theShadowWidth.x * 2
		val h = theSize.y + theShadowWidth.y * 2
		
		theShape.setFrame( x-w/2, y-h/2, w, h )
 	}
  
 	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
 		makeShadow( camera )
 		cast( g, theShape )
 	}
  
 	def render( g:Graphics2D, camera:Camera, element:GraphicElement ) {
 		make( camera )
 		fill( g, theShape, camera )
 		stroke( g, theShape )
 		decor( g, camera, element, theShape )
 	}
}

class CircleShape extends RectangularAreaShape {
	val theShape = new Ellipse2D.Float
}

class SquareShape extends RectangularAreaShape {
	val theShape = new Rectangle2D.Float
}

class RoundedSquareShape extends RectangularAreaShape {
	val theShape = new RoundRectangle2D.Float
 
	override def make( camera:Camera ) {
		var w = theSize.x
		var h = theSize.y
		var r = if( h/8 > w/8 ) w/8 else h/8 
  
		theShape.setRoundRect( theCenter.x-w/2, theCenter.y-h/2, w, h, r, r )
	}
	override def makeShadow( camera:Camera ) {
		var x = theCenter.x + theShadowOff.x
		var y = theCenter.y + theShadowOff.y
		var w = theSize.x + theShadowWidth.x * 2
		var h = theSize.y + theShadowWidth.y * 2
		var r = if( h/8 > w/8 ) w/8 else h/8 
		
		theShape.setRoundRect( x-w/2, y-h/2, w, h, r, r )
	}
}

abstract class PolygonalShape extends AreaShape {
	var theShape = new Path2D.Float
 
 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
 	  	configureFillable( style, camera, element )
 	  	configureShadowable( style, camera )
 	  	configureStrokable( style, camera )
 	  	configureDecorable( style, camera )
 	}
 
 	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
 		makeShadow( camera )
 		cast( g, theShape )
 	}
  
 	def render( g:Graphics2D, camera:Camera, element:GraphicElement ) {
 		make( camera )
 		fill( g, theShape, camera )
 		stroke( g, theShape )
 		decor( g, camera, element, theShape )
 	}	
}

class DiamondShape extends PolygonalShape {
	def make( camera:Camera ) {
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
	
	def makeShadow( camera:Camera ) {

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
	def make( camera:Camera ) {
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
	
	def makeShadow( camera:Camera ) {
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
	def make( camera:Camera ) {
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
	
	def makeShadow( camera:Camera ) {
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
 
	override def make( camera:Camera ) {
		var w = theSize.x
		val h = theSize.y
		val x = theCenter.x
		val y = theCenter.y

		theShape.setRect( x-w/2, y-h/2, w, h )
		
		w -= theStrokeWidth
		
		theLineShape.setLine( x-w/2, y-h/2, x+w/2, y-h/2 )
	}
	override def makeShadow( camera:Camera ) {
		var x = theCenter.x + theShadowOff.x
		var y = theCenter.y + theShadowOff.y
		var w = theSize.x + theShadowWidth.x * 2
		var h = theSize.y + theShadowWidth.y * 2
		
		theShape.setRect( x-w/2, y-h/2, w, h )
		theLineShape.setLine( x-w/2, y-h/2, x+w/2, y-h/2 )
	}
	override def render( g:Graphics2D, camera:Camera, element:GraphicElement ) {
 		make( camera )
 		fill( g, theShape, camera )
 		stroke( g, theLineShape )
 		decor( g, camera, element, theShape )
 	}
}

class LineShape extends LineConnectorShape {
	protected var theShape:java.awt.Shape = new Line2D.Float
 
// Command
 
 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
 	  	configureFillableConnector( style, camera, element )
 	  	configureShadowableConnector( style, camera )
 	  	configureStrokableConnector( style, camera )
 	  	configureDecorable( style, camera )
 	}
  
	protected def make( camera:Camera ) {
		val from = info.points(0)
		val to   = info.points(3)
		if( info.isCurve ) {
			val ctrl1 = info.points(1)
			val ctrl2 = info.points(2)
			val curve = new CubicCurve2D.Float
			theShape = curve
			curve.setCurve( from.x, from.y, ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, to.x, to.y )
		} else {
			val line = new Line2D.Float
			theShape = line
			line.setLine( from.x, from.y, to.x, to.y )
		} 
	}
	protected def makeShadow( camera:Camera ) {
		var x0 = info.points(0).x + theShadowOff.x
		var y0 = info.points(0).y + theShadowOff.y
		var x1 = info.points(3).x + theShadowOff.x
		var y1 = info.points(3).y + theShadowOff.y
		
		if( info.isCurve ) {
			var ctrlx0 = info.points(1).x + theShadowOff.x
			var ctrly0 = info.points(1).y + theShadowOff.y
			var ctrlx1 = info.points(2).x + theShadowOff.x
			var ctrly1 = info.points(2).y + theShadowOff.y
			
			val curve = new CubicCurve2D.Float
			theShape = curve
			curve.setCurve( x0, y0, ctrlx0, ctrly0, ctrlx1, ctrly1, x1, y1 )
		} else {
			val line = new Line2D.Float
			theShape = line
			line.setLine( x0, y0, x1, y1 )
		} 
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
 		makeShadow( camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement ) {
 		make( camera )
 		stroke( g, theShape )
 		fill( g, theSize, theShape )
 		decor( g, camera, element, theShape )
	}
}