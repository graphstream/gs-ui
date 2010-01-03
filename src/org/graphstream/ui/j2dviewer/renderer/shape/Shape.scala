package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Color, Graphics2D, Image, Paint, Stroke}
import java.awt.geom.RectangularShape

import org.graphstream.ui.j2dviewer.util.{GraphMetrics, Camera}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui2.graphicGraph.stylesheet.Style

/**
 * Base for all shapes.
 */
trait Shape {
// Command
    
 	/**
     * Configure as much as possible the graphics before painting several version of this shape
     * at different positions.
     * @param g The Java2D graphics.
     */
 	def configure( g:Graphics2D, style:Style, camera:Camera )
 
 	/**
     * Must create the shape from informations given earlier, that is, resize it if needed and
     * position it.
     * All the settings for position, size, shadow, etc. must have been made. Usually all the
     * "static" settings are already set in configure, therefore mist often this method is only in
     * charge of changing  the shape position. The shape is built
     * differently if it is for a shadow, so the boolean argument allows to distinguish the two
     * cases.
     * @param forShadow true if we prepare to draw a shadow.
 	 */
 	protected def make( forShadow:Boolean, camera:Camera )
  
  	/**
     * Render the shape.
     */
  	def render( g:Graphics2D, camera:Camera )
   
   	/**
     * Render the shape shadow. The shadow is rendered in a different pas than usual rendering,
     * therefore it is a separate method.
     */
   	def renderShadow( g:Graphics2D, camera:Camera )
}

/**
 * Trait for elements painted inside a rectangular area.
 */
trait Area {
	protected val theCenter = new Point2
	protected val theSize = new Point2
	def size( width:Float, height:Float ) { theSize.set( width, height ) }
	def size( style:Style, camera:Camera ) { 
		val w = camera.metrics.lengthToGu( style.getSize, 0 )
		val h = if( style.getSize.size > 1 ) camera.metrics.lengthToGu( style.getSize, 1 ) else w
  
		theSize.set( w, h )
	}
	def position( x:Float, y:Float ) { theCenter.set( x, y ) }
}

/**
 * Trait for elements painted between two points.
 */
trait Connector {
	val from = new Point2
	val to = new Point2
	def position( xFrom:Float, yFrom:Float, xTo:Float, yTo:Float ) {
		from.set( xFrom, yFrom )
		to.set( xTo, yTo )
	}
}

/**
 * Trait for shapes that can be filled.
 */
trait Fillable {
	/** The fill paint. */
	var fillPaint:ShapePaint = null

    /**
     * Fill the shape.
     * @param g The Java2D graphics.
     * @param dynColor The value between 0 and 1 allowing to know the dynamic plain color, if any.
     * @param shape The awt shape to fill.
     */
	def fill( g:Graphics2D, dynColor:Float, shape:java.awt.Shape ) {
		fillPaint match {
		  case p:ShapeAreaPaint  => g.setPaint( p.paint( shape ) );    g.fill( shape )
		  case p:ShapeColorPaint => g.setPaint( p.paint( dynColor ) ); g.fill( shape )
		  case _                 => null; printf( "no fill !!!%n" ) 
		}
	}
 
    /**
     * Fill the shape.
     * @param g The Java2D graphics.
     * @param shape The awt shape to fill.
     */
 	def fill( g:Graphics2D, shape:java.awt.Shape ) { fill( g, 0, shape ) }

    /** Configure all static parts needed to fill the shape. */
  	protected def configureFillable( style:Style, camera:Camera ) {
 		/*if( fillPaint == null )*/ fillPaint = ShapePaint( style )
  	}
}

/**
 * Trait for shapes that can be stroked.
 */
trait Strokable {
    /** The stroke color. */
	var strokeColor:Color = null

	/** The stroke. */
	var theStroke:ShapeStroke = null
 	
	/** The stroke width. */
	var strokeWidth = 0f

 	/** Paint the stroke of the shape. */
	def stroke( g:Graphics2D, shape:java.awt.Shape ) {
		if( theStroke != null ) {
			g.setStroke( theStroke.stroke( strokeWidth ) )
			g.setColor( strokeColor )
			g.draw( shape )
		}	  
	}
     
 	/** Configure all the static parts needed to stroke the shape. */
 	protected def configureStrokable( style:Style, camera:Camera ) {
		strokeWidth = camera.metrics.lengthToGu( style.getStrokeWidth )
		/*if( strokeColor == null )*/ strokeColor = ShapeStroke.strokeColor( style )
		/*if( theStroke   == null )*/ theStroke   = ShapeStroke.strokeForArea( style )
 	}
}

/**
 * Trait for shapes that can cast a shadow.
 */
trait Shadowable {
	/** The shadow paint. */
	var shadowPaint:ShapePaint = null

	/** Additional width of a shadow (added to the shape size). */
	protected val theShadowWidth = new Point2
 
	/** Offset of the shadow according to the shape center. */
	protected val theShadowOff = new Point2

	/** Sety the shadow width added to the shape width. */
	def shadowWidth( width:Float, height:Float ) { theShadowWidth.set( width, height ) }
 
 	/** Set the shadow offset according to the shape. */ 
	def shadowOffset( xoff:Float, yoff:Float ) { theShadowOff.set( xoff, yoff ) }
 
 	/**
     * Render the shadow.
     * @param g The Java2D graphics.
     */
   	def cast( g:Graphics2D, shape:java.awt.Shape ) {
   		shadowPaint match {
   			case p:ShapeAreaPaint  => g.setPaint( p.paint( shape ) ); g.fill( shape )
   			case p:ShapeColorPaint => g.setPaint( p.paint( 0 ) );     g.fill( shape )
   			case _                 => null; printf( "no shadow !!!%n" )
   		}
   	}
 
    /** Configure all the static parts needed to cast the shadow of the shape. */
 	protected def configureShadowable( style:Style, camera:Camera ) {
 		theShadowWidth.x = camera.metrics.lengthToGu( style.getShadowWidth )
 		theShadowWidth.y = theShadowWidth.x
 		theShadowOff.x   = camera.metrics.lengthToGu( style.getShadowOffset, 0 )
 		theShadowOff.y   = if( style.getShadowOffset.size > 1 ) camera.metrics.lengthToGu( style.getShadowOffset, 1 ) else theShadowOff.x
 	  
  	  	/*if( shadowPaint == null )*/ shadowPaint = ShapePaint( style, true )
 	}
}

/**
 * Trait for shapes that can be decorated by an icon and/or a text.
 */
trait Decorable {
	var text:String = null
 
	/** The text and icon. */
	var theDecor:ShapeDecor = null
  
 	/** Paint the decorations (text and icon). */
 	def decor( g:Graphics2D, camera:Camera, shape:java.awt.Shape ) {
 	  	if( theDecor != null ) {
 	  		val bounds = shape.getBounds2D
Console.err.printf( "rendering text %s :%n", text )
 	  		theDecor.render( g, camera, text, bounds.getMinX.toFloat, bounds.getMinY.toFloat, bounds.getMaxX.toFloat, bounds.getMaxY.toFloat )
 	  	}
 	}
  
  	/** Configure all the static parts needed to decor the shape. */
  	protected def configureDecorable( style:Style, camera:Camera ) {
		/*if( theDecor == null )*/ theDecor = ShapeDecor( style )
  	}
}