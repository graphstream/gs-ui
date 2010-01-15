package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Color, Graphics2D, Image, Paint, Stroke}
import java.awt.geom.RectangularShape

import org.graphstream.ui.j2dviewer.util.{GraphMetrics, Camera}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.j2dviewer.geom.Vector2
import org.graphstream.ui2.graphicGraph.GraphicEdge
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
 	protected def make( camera:Camera )
  	protected def makeShadow( camera:Camera )
  
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
 * Shape that cannot be filled, but must be stroked.
 */
trait FillableConnector {
	var fillColor:Color = null
	var fillStroke:ShapeStroke = null
  
	def fill( g:Graphics2D, width:Float, dynColor:Float, shape:java.awt.Shape ) {
		if( fillStroke != null ) {
			val stroke = fillStroke.stroke( width )
   
			g.setColor( fillColor )
			g.setStroke( stroke )
			g.draw( shape )
		}
	}
 
	def fill( g:Graphics2D, width:Float, shape:java.awt.Shape ) { fill( g, width, 0, shape ) }
 
	protected def configureFillableConnector( style:Style, camera:Camera ) {
		fillColor  = style.getFillColor( 0 )
		fillStroke = ShapeStroke.strokeForConnectorFill( style )
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

trait StrokableConnector extends Strokable {
 	protected override def configureStrokable( style:Style, camera:Camera ) {
		strokeWidth = camera.metrics.lengthToGu( style.getStrokeWidth ) + camera.metrics.lengthToGu( style.getSize, 0 )
		strokeColor = ShapeStroke.strokeColor( style )
		theStroke   = ShapeStroke.strokeForArea( style )
 	}
 	protected def configureStrokableConnector( style:Style, camera:Camera ) { configureStrokable( style, camera ) }
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

trait ShadowableConnector {
	/** The shadow paint. */
	var shadowStroke:ShapeStroke = null

	/** Additional width of a shadow (added to the shape size). */
	protected var theShadowWidth = 0f
 
	/** Offset of the shadow according to the shape center. */
	protected val theShadowOff = new Point2

	protected var theShadowColor:Color = null
 
	/** Sety the shadow width added to the shape width. */
	def shadowWidth( width:Float ) { theShadowWidth = width }
 
 	/** Set the shadow offset according to the shape. */ 
	def shadowOffset( xoff:Float, yoff:Float ) { theShadowOff.set( xoff, yoff ) }
  
 	/**
     * Render the shadow.
     * @param g The Java2D graphics.
     */
   	def cast( g:Graphics2D, shape:java.awt.Shape ) {
   	  	g.setColor( theShadowColor )
   	  	g.setStroke( shadowStroke.stroke( theShadowWidth ) )
   	  	g.draw( shape )
   	}
 
    /** Configure all the static parts needed to cast the shadow of the shape. */
 	protected def configureShadowableConnector( style:Style, camera:Camera ) {
 		theShadowWidth = camera.metrics.lengthToGu( style.getSize, 0 ) +
 			camera.metrics.lengthToGu( style.getShadowWidth ) +
 			camera.metrics.lengthToGu( style.getStrokeWidth )
 		theShadowOff.x = camera.metrics.lengthToGu( style.getShadowOffset, 0 )
 		theShadowOff.y = if( style.getShadowOffset.size > 1 ) camera.metrics.lengthToGu( style.getShadowOffset, 1 ) else theShadowOff.x
  	  	theShadowColor = style.getShadowColor( 0 )
 		shadowStroke   = ShapeStroke.strokeForConnectorFill( style )
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
 	  		theDecor.render( g, camera, text, bounds.getMinX.toFloat, bounds.getMinY.toFloat, bounds.getMaxX.toFloat, bounds.getMaxY.toFloat )
 	  	}
 	}
  
  	/** Configure all the static parts needed to decor the shape. */
  	protected def configureDecorable( style:Style, camera:Camera ) {
		/*if( theDecor == null )*/ theDecor = ShapeDecor( style )
  	}
}

/**
 * Trait for elements painted inside an area.
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
  
	protected val from = new Point2
	protected val to = new Point2
	protected var ctrl1:Point2 = null
	protected var ctrl2:Point2 = null
	protected var theSize:Float = 0
	protected var theTargetNodeSize:Float = 0
	var isCurve = false
	var isLoop  = false
	def fromPos:Point2 = from
	def byPos1:Point2 = ctrl1
	def byPos2:Point2 = ctrl2
	def toPos:Point2 = to
	def size( width:Float ) { theSize = width }
	def size( style:Style, camera:Camera ) { size( camera.metrics.lengthToGu( style.getSize, 0 ) ) }
	def targetNodeSize( width:Float ) { theTargetNodeSize = width }
	def targetNodeSize( nodeStyle:Style, camera:Camera ) {
		var s = camera.metrics.lengthToGu( nodeStyle.getSize, 0 )
		if( nodeStyle.getSize.size > 1 ) {
			s += camera.metrics.lengthToGu( nodeStyle.getSize, 1 )
			s /= 2
		}
		targetNodeSize( s )
	}
	def position( xFrom:Float, yFrom:Float, xTo:Float, yTo:Float ) { position( xFrom, yFrom, xTo, yTo, 0, null ) }
	def position( xFrom:Float, yFrom:Float, xTo:Float, yTo:Float, multi:Int, group:GraphicEdge#EdgeGroup ) {
		from.set( xFrom, yFrom )
		to.set( xTo, yTo )
		if( group != null ) {
			isCurve = true
			ctrl1   = new Point2
			ctrl2   = new Point2
			if( xFrom == xTo && yFrom == yTo ) {
				isLoop = true
				positionEdgeLoop( xFrom, yFrom, multi )
			} else {
				isLoop = false
				positionMultiEdge( xFrom, yFrom, xTo, yTo, multi, group )
			}
		} else {
			if( xFrom == xTo && yFrom == yTo ) {
				isLoop  = true
				isCurve = true
				ctrl1   = new Point2
				ctrl2   = new Point2
				positionEdgeLoop( xFrom, yFrom, 0 )
			} else {
				isLoop  = false
				isCurve = false
				ctrl1 = null
				ctrl2 = null
			}		  
		}
	}
	protected def positionEdgeLoop( x:Float, y:Float, multi:Int ) {
		var m = 1f + multi * 0.2f
		var d = theTargetNodeSize/2*m + 4*theTargetNodeSize*m

		ctrl1.set( x + d, y )
		ctrl2.set( x, y + d )
//		to.set( x, y + theTargetNodeSize/2*m )
	}
	protected def positionMultiEdge( x1:Float, y1:Float, x2:Float, y2:Float, multi:Int, group:GraphicEdge#EdgeGroup ) {
		var vx  = (  x2 - x1 )
		var vy  = (  y2 - y1 )
		var vx2 = (  vy ) * 0.6f
		var vy2 = ( -vx ) * 0.6f
		val gap = 0.2f
		var ox  = 0f
		var oy  = 0f
		val f   = ( ( 1 + multi ) / 2 ).toFloat * gap // (1+multi)/2 must be done on integers.
  
		vx *= 0.2f
		vy *= 0.2f
  
		val main = group.getEdge( 0 )
		val edge = group.getEdge( multi )
 
		if( group.getCount %2 == 0 ) {
			ox = vx2 * (gap/2)
			oy = vy2 * (gap/2)
			if( edge.from ne main.from ) {	// Edges are in the same direction.
				ox = - ox
				oy = - oy
			}
		}
  
		vx2 *= f
		vy2 *= f
  
		ctrl1.set( x1 + vx, y1 + vy )
		ctrl2.set( x2 - vx, y2 - vy )
  
		val m = multi + ( if( edge.from eq main.from ) 0 else 1 )
  
		if( m % 2 == 0 ) {
			ctrl1.x += ( vx2 + ox )
			ctrl1.y += ( vy2 + oy )
			ctrl2.x += ( vx2 + ox )
			ctrl2.y += ( vy2 + oy )
		} else {
			ctrl1.x -= ( vx2 - ox )
			ctrl1.y -= ( vy2 - oy )
			ctrl2.x -= ( vx2 - ox ) 
			ctrl2.y -= ( vy2 - oy )		  
		}
	}
 
	def setEdgeCtrlPoints( edge:GraphicEdge ) {
		if( ctrl1 != null && ctrl2 != null ) {
			if( edge.ctrl == null ) edge.ctrl = new Array[Float]( 4 )
			
			edge.ctrl(0) = ctrl1.x
			edge.ctrl(1) = ctrl1.y
			edge.ctrl(2) = ctrl2.x
			edge.ctrl(3) = ctrl2.y
		}
	}
}

/**
 * Trait for elements painted inside an area with an orientation.
 */
trait OrientedArea extends Area {
	protected val theDirection = new Vector2

	def direction( dx:Float, dy:Float ) { theDirection.set( dx, dy ) }
 
	def direction( edge:GraphicEdge ) {
		theDirection.set(
			edge.to.getX - edge.from.getX,
			edge.to.getY - edge.from.getY )
	}
 
	def direction( connector:Connector ) {
		if( connector.isCurve ) {
			theDirection.set( connector.toPos.x - connector.byPos2.x, connector.toPos.y - connector.byPos2.y )
		} else {
			theDirection.set( connector.toPos.x - connector.fromPos.x, connector.toPos.y - connector.fromPos.y )
		}
	}
 
	def sizeForEdgeArrow( style:Style, camera:Camera ) {
		val w = camera.metrics.lengthToGu( style.getArrowSize, 0 )
		val h = if( style.getArrowSize.size > 1 ) camera.metrics.lengthToGu( style.getArrowSize, 1 ) else w
  
		theSize.set( w, h )
	}
}

trait AreaOnConnector extends Area {
	protected var theConnector:Connector = null
	protected var theEdge:GraphicEdge = null

	def connector( edge:GraphicEdge ) { theEdge = edge }
 
	def direction( connector:Connector ) { theConnector = connector }
 
	def sizeForEdgeArrow( style:Style, camera:Camera ) {
		val w = camera.metrics.lengthToGu( style.getArrowSize, 0 )
		val h = if( style.getArrowSize.size > 1 ) camera.metrics.lengthToGu( style.getArrowSize, 1 ) else w
  
		theSize.set( w, h )
	}
}