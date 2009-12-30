package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Color, Graphics2D, Image, Paint, Stroke}
import java.awt.geom.RectangularShape

import org.graphstream.ui.geom.Point2
import org.graphstream.ui2.graphicGraph.stylesheet.Style

/**
 * Base for all shapes.
 */
trait Shape {
// Setting

	/** The fill paint. */
	var fillPaint:ShapePaint = null

 // Command
    
 	/**
     * Configure as much as possible the graphics before painting several version of this shape
     * at different positions.
     * @param g The Java2D graphics.
     */
 	def configure( style:Style, g:Graphics2D )
 
 	/**
     * Create the shape from informations given earlier.
     * All the settings for position, size, shadow, etc. must have been made. The shape is built
     * differently if it is for a shadow, so the boolean argument allows to distinguish the two
     * cases.
     * @param forShadow true if we prepare to draw a shadow.
 	 */
 	def make( forShadow:Boolean )
    
    /**
     * Fill the shape.
     * @param g The Java2D graphics.
     * @param dynColor The value between 0 and 1 allowing to know the dynamic plain color, if any.
     */
	def fill( g:Graphics2D, dynColor:Float )
 	def fill( g:Graphics2D ) { fill( g, Float.NaN ) }
}

/**
 * Trait for elements painted inside a rectangular area.
 */
trait Area {
	protected val theCenter = new Point2
	protected val theSize = new Point2
	def size( width:Float, height:Float ) { theSize.set( width, height ) }
	def position( x:Float, y:Float ) { theCenter.set( x, y ) }
}

trait Connector {
	val from = new Point2
	val to = new Point2
	def position( xFrom:Float, yFrom:Float, xTo:Float, yTo:Float ) {
		from.set( xFrom, yFrom )
		to.set( xTo, yTo )
	}
}

trait Strokable {
    /** The stroke color. */
	var strokeColor:Color = Color.BLACK

	/** The stroke. */
	var theStroke:ShapeStroke = null
 	
	/** The stroke width. */
	var strokeWidth = 0f

 	/** Paint the stroke of the shape. */
	def stroke( g:Graphics2D )
}

trait CastShadow {
	/** The shadow paint. */
	var shadowPaint:ShapePaint = null

	protected val theShadowWidth = new Point2
	protected val theShadowOff   = new Point2

	/**
     * The shadow width added to the shape width.
     */
	def shadowWidth( width:Float, height:Float ) { theShadowWidth.set( width, height ) }
 
 	/**
     * The shadow offset according to the shape. 
     */
	def shadowOffset( xoff:Float, yoff:Float ) { theShadowOff.set( xoff, yoff ) }
 
 	/**
     * Render the shadow.
     * @param g The Java2D graphics.
     */
   	def cast( g:Graphics2D )
}

trait Decored {
	var text:String = null
	var icon:Image = null
	var theDecor:ShapeDecor = null
  
 	/** Paint the decorations (text and icon). */
 	def decor( g:Graphics2D )
}

/**
 * Base for shapes centered around one point.
 */
trait AreaShape extends Shape with Area with Strokable with CastShadow with Decored {
}

/**
 * Base for shapes rendered between two points.
 */
trait ConnectorShape extends Shape with Connector with Strokable with CastShadow with Decored {
}