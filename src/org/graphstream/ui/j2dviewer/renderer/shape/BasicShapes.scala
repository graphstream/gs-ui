package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Image, Color, Graphics2D}
import java.awt.geom.{Ellipse2D, Line2D, Rectangle2D, RoundRectangle2D, RectangularShape}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui2.graphicGraph.stylesheet.Style
import org.graphstream.ui.j2dviewer.util.{GraphMetrics, Camera}


/**
 * Base for shapes centered around one point.
 */
trait AreaShape extends Shape with Area with Fillable with Strokable with Shadowable with Decorable {
}

/**
 * Base for shapes rendered between two points.
 */
trait ConnectorShape extends Shape with Connector with Fillable with Strokable with Shadowable with Decorable {
}


trait RectangularAreaShape extends AreaShape {
	val theShape:RectangularShape
  
 	def configure( g:Graphics2D, style:Style, camera:Camera ) {
 	  	configureFillable( style, camera )
 	  	configureShadowable( style, camera )
 	  	configureStrokable( style, camera )
 	  	configureDecorable( style, camera )
 	}
 
 	protected def make( forShadow:Boolean, camera:Camera ) {
		var x = theCenter.x
		var y = theCenter.y
		var w = theSize.x
		var h = theSize.y
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
			w += theShadowWidth.x * 2
			h += theShadowWidth.y * 2
		}
 	  	
		theShape.setFrame( x-w/2, y-h/2, w, h )
 	}
  
 	def renderShadow( g:Graphics2D, camera:Camera ) {
 		make( true, camera )
 		cast( g, theShape )
 	}
  
 	def render( g:Graphics2D, camera:Camera ) {
 		make( false, camera )
 		fill( g, theShape )
 		stroke( g, theShape )
 		decor( g, camera, theShape )
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
 
	override def make( forShadow:Boolean, camera:Camera ) {
		var x = theCenter.x
		var y = theCenter.y
		var w = theSize.x
		var h = theSize.y
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
			w += theShadowWidth.x * 2
			h += theShadowWidth.y * 2
		}
 
		var r = if( h/8 > w/8 ) w/8 else h/8 
		
  
		theShape.setRoundRect( x-w/2, y-h/2, w, h, r, r )
	}
}

class LineShape extends ConnectorShape {
	protected var theShape = new Line2D.Float
 
// Command
 
 	def configure( g:Graphics2D, style:Style, camera:Camera ) {
 	  
 	}
  
	protected def make( forShadow:Boolean, camera:Camera ) {
	  
	}
 
	def renderShadow( g:Graphics2D, camera:Camera ) {
	  
	}
 
	def render( g:Graphics2D, camera:Camera ) {
	  
	}
}