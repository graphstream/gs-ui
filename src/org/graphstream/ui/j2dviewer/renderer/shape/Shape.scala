package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Color, Graphics2D, Image, Paint, Stroke}
import java.awt.geom.RectangularShape

import org.graphstream.ui.j2dviewer.Camera
import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.util._
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.sgeom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

/** Base for all shapes. */
trait Shape {
// Command
    
 	/**
     * Configure as much as possible the graphics before painting several version of this shape
     * at different positions.
     * @param g The Java2D graphics.
     */
 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement )
 
 	/**
     * Must create the shape from informations given earlier, that is, resize it if needed and
     * position it, and do all the things that are specific to each element, and cannot be done
     * for the group of elements.
     * All the settings for position, size, shadow, etc. must have been made. Usually all the
     * "static" settings are already set in configure, therefore most often this method is only in
     * charge of changing  the shape position (and computing size if fitting it to the contents).
 	 */
 	protected def make( g:Graphics2D, camera:Camera )
 	
 	/**
 	 * Same as {@link #make(Camera)} for the shadow shape. The shadow shape may be moved and
 	 * resized compared to the original shape. 
 	 */
  	protected def makeShadow( g:Graphics2D, camera:Camera )
  
  	/**
     * Render the shape.
     */
  	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo )
   
   	/**
     * Render the shape shadow. The shadow is rendered in a different pas than usual rendering,
     * therefore it is a separate method.
     */
   	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo )
}

/**
 * Trait for shapes that can be filled.
 */
trait Fillable {
	/** The fill paint. */
	var fillPaint:ShapePaint = null
 
	/** Value in [0..1] for dyn-colors. */
	var theFillPercent = 0f;

    /**
     * Fill the shape.
     * @param g The Java2D graphics.
     * @param dynColor The value between 0 and 1 allowing to know the dynamic plain color, if any.
     * @param shape The awt shape to fill.
     */
	def fill( g:Graphics2D, dynColor:Float, shape:java.awt.Shape, camera:Camera ) {
		fillPaint match {
		  case p:ShapeAreaPaint  => g.setPaint( p.paint( shape, camera.metrics.ratioPx2Gu ) );    g.fill( shape )
		  case p:ShapeColorPaint => g.setPaint( p.paint( dynColor ) ); g.fill( shape )
		  case _                 => null; // No fill. // printf( "no fill !!!%n" ) 
		}
	}
 
    /**
     * Fill the shape.
     * @param g The Java2D graphics.
     * @param shape The awt shape to fill.
     */
 	def fill( g:Graphics2D, shape:java.awt.Shape, camera:Camera ) { fill( g, theFillPercent, shape, camera ) }

    /**
     *  Configure all static parts needed to fill the shape.
     */
  	protected def configureFillable( style:Style, camera:Camera, element:GraphicElement ) {
  	  	if( style.getFillMode == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
  	  		element.getAttribute( "ui.color" ) match {
  	  			case x:Number => theFillPercent = x.floatValue
  	  			case _        => theFillPercent = 0f
  	  		}
  	  	} else {
  	  		theFillPercent = 0
  	  	}

 		fillPaint = ShapePaint( style )
  	}
}

/**
 * Shape that cannot be filled, but must be stroked.
 */
trait FillableLine {
	var fillColors:Array[Color] = null
	var fillStroke:ShapeStroke = null
	var theFillPercent = 0f;
  
	def fill( g:Graphics2D, width:Float, dynColor:Float, shape:java.awt.Shape ) {
		if( fillStroke != null ) {
			val stroke = fillStroke.stroke( width )
   
			g.setColor( fillColors(0) )
			g.setStroke( stroke )
			g.draw( shape )
		}
	}
 
	def fill( g:Graphics2D, width:Float, shape:java.awt.Shape ) { fill( g, width, theFillPercent, shape ) }
 
	protected def configureFillableConnector( style:Style, camera:Camera, element:GraphicElement ) {
  	  	theFillPercent = 0
  	  	if( style.getFillMode == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
  	  		element.getAttribute( "ui.color" ) match {
  	  			case x:Number => theFillPercent = x.floatValue
  	  			case _ => theFillPercent = 0f
  	  		}
       
  	  		fillColors = ShapePaint.createColors( style, style.getFillColorCount, style.getFillColors )
  	  		fillColors(0) = ShapePaint.interpolateColor( fillColors, theFillPercent )
  	  	}
  	  	else
        {
  	  		if( fillColors == null || fillColors.length < 1 )
  	  			fillColors = new Array[Color]( 1 )
       
  	  		fillColors(0) = style.getFillColor( 0 )
        }

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
	var theStrokeWidth = 0f

 	/** Paint the stroke of the shape. */
	def stroke( g:Graphics2D, shape:java.awt.Shape ) {
		if( theStroke != null ) {
			g.setStroke( theStroke.stroke( theStrokeWidth ) )
			g.setColor( strokeColor )
			g.draw( shape )
		}	  
	}
     
 	/** Configure all the static parts needed to stroke the shape. */
 	protected def configureStrokable( style:Style, camera:Camera ) {
		theStrokeWidth = camera.metrics.lengthToGu( style.getStrokeWidth )
		/*if( strokeColor == null )*/ strokeColor = ShapeStroke.strokeColor( style )
		/*if( theStroke   == null )*/ theStroke   = ShapeStroke.strokeForArea( style )
 	}
}

trait StrokableLine extends Strokable {
 	protected override def configureStrokable( style:Style, camera:Camera ) {
		theStrokeWidth = camera.metrics.lengthToGu( style.getStrokeWidth ) + camera.metrics.lengthToGu( style.getSize, 0 )
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
   			case p:ShapeAreaPaint  => g.setPaint( p.paint( shape, 1 ) ); g.fill( shape )
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

trait ShadowableLine {
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
 	def decor( g:Graphics2D, camera:Camera, iconAndText:IconAndText, element:GraphicElement, shape:java.awt.Shape ) {
 	  	var visible = true
 	  	if( element != null ) visible = camera.isTextVisible( element )
 	  	if( theDecor != null && visible ) {
 	  		val bounds = shape.getBounds2D
 	  		theDecor.render( g, camera, iconAndText, bounds.getMinX.toFloat, bounds.getMinY.toFloat, bounds.getMaxX.toFloat, bounds.getMaxY.toFloat )
 	  	}
 	}
  
  	/** Configure all the static parts needed to decor the shape. */
  	protected def configureDecorable( style:Style, camera:Camera ) {
		/*if( theDecor == null )*/ theDecor = ShapeDecor( style )
  	}
  	
  	/** Setup the parts of the decor specific to each element. */
  	def setupContents( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
  		if( info != null ) {
  			val style = element.getStyle
  			
  			if( info.iconAndText == null )
  				info.iconAndText = ShapeDecor.iconAndText( style, camera, element )

  			if( style.getIcon != null && style.getIcon.equals( "dynamic" ) && element.hasAttribute( "ui.icon" ) ) {
  				val url = element.getLabel("ui.icon").toString
  				info.iconAndText.setIcon( g, url )
// Console.err.printf( "changing icon %s%n", url )
  			}
// else Console.err.print( "NOT changing icon... %b %s %b%n".format( style.getIcon != null, style.getIcon, element.hasAttribute( "ui.icon" ) ) )
  			
  			info.iconAndText.setText( g, element.label )
  		}
  	}
}

trait Orientable {
	var orientation:StyleConstants.SpriteOrientation = null
	
	var target = new Point2
	
	protected def configureOrientable( style:Style, camera:Camera ) {
		orientation = style.getSpriteOrientation
	}
	
	def setupOrientation( camera:Camera, sprite:GraphicSprite ) {
		sprite.getAttachment match {
			case gn:GraphicNode => {
				sprite.getStyle.getSpriteOrientation match {
					case SpriteOrientation.NONE       => { target.set( 0, 0 ) }
					case SpriteOrientation.FROM       => { target.set( gn.getX, gn.getY ) }
					case SpriteOrientation.TO         => { target.set( gn.getX, gn.getY ) }
					case SpriteOrientation.PROJECTION => { target.set( gn.getX, gn.getY ) }
				}
			}
			case ge:GraphicEdge => {
				sprite.getStyle.getSpriteOrientation match {
					case SpriteOrientation.NONE       => { target.set( 0, 0 ) }
					case SpriteOrientation.FROM       => { target.set( ge.from.getX, ge.from.getY ) }
					case SpriteOrientation.TO         => { target.set( ge.to.getX, ge.to.getY ) }
					case SpriteOrientation.PROJECTION => {
						val ei = ge.getAttribute( ElementInfo.attributeName ).asInstanceOf[EdgeInfo]
						
						if( ei != null )
						      setTargetOnEdgeInfo( ei, camera, sprite, ge )
						else setTargetOnLineEdge( camera, sprite, ge ) 
					}
				}
			}
			case _ => { orientation = SpriteOrientation.NONE }
		}
	}
	
	protected def setTargetOnEdgeInfo( ei:EdgeInfo, camera:Camera, sprite:GraphicSprite, ge:GraphicEdge ) {
		if( ei.isCurve  ) {
			CubicCurve.eval( ei.points(0), ei.points(1), ei.points(2), ei.points(3), sprite.getX, target )
		} else {
			setTargetOnLineEdge( camera, sprite, ge )
		}
	}
	
	protected def setTargetOnLineEdge( camera:Camera, sprite:GraphicSprite, ge:GraphicEdge ) {
		val dir = Vector2( ge.to.getX-ge.from.getX, ge.to.getY-ge.from.getY )
		dir.scalarMult( sprite.getX )
		target.set( ge.from.getX + dir.x, ge.from.getY + dir.y )
	}
}

/**
 * Trait for elements painted inside an area.
 */
trait Area {
	protected val theCenter = new Point2
	protected val theSize = new Point2
	protected var fit = false
	
	def size( width:Float, height:Float ) { theSize.set( width, height ) }
	
	def size( style:Style, camera:Camera ) { 
		val w = camera.metrics.lengthToGu( style.getSize, 0 )
		val h = if( style.getSize.size > 1 ) camera.metrics.lengthToGu( style.getSize, 1 ) else w
  
		theSize.set( w, h )
		
		fit = ( style.getSizeMode == StyleConstants.SizeMode.FIT )
	}
	
	def dynSize( style:Style, camera:Camera, element:GraphicElement ) {
		var w = camera.metrics.lengthToGu( style.getSize, 0 )
		var h = if( style.getSize.size > 1 ) camera.metrics.lengthToGu( style.getSize, 1 ) else w

		if( element.hasAttribute( "ui.size" ) ) {
			w = camera.metrics.lengthToGu( StyleConstants.convertValue( element.getAttribute( "ui.size" ) ) )
			h = w;
		}
  
		theSize.set( w, h )
	}
	
	def positionAndFit( g:Graphics2D, camera:Camera, info:NodeInfo, element:GraphicElement, x:Float, y:Float ) {
		if( info != null ) {
			info.theSize.copy( theSize )
		}

		theCenter.set( x, y )
	}
}

/**
 * Trait for elements painted between two points.
 */
trait Connector {
// Attribute
	
	var info:EdgeInfo = null
	
	/** Width of the connector. */
	protected var theSize:Float = 0
	
	/** Size of the element at the end of the connector. */
//	protected var theSourceInfo:NodeInfo = null
	
	/** Size of the element at the origin of the connector. */
//	protected var theTargetInfo:NodeInfo = null
	
	protected var theTargetSizeX = 0f
	protected var theTargetSizeY = 0f
	protected var theSourceSizeX = 0f
	protected var theSourceSizeY = 0f
	
	/** Is the connector directed ? */
	var isDirected = false
	
// Command
	
	/** Origin point of the connector. */
	def fromPos:Point2 = info.points(0)
	
	/** First control point. */
	def byPos1:Point2 = info.points(1)
	
	/** Second control point. */
	def byPos2:Point2 = info.points(2)
	
	/** Destination of the connector. */
	def toPos:Point2 = info.points(3)
	
	/** Set the size (`width`) of the connector. */
	def size( width:Float ) { theSize = width }
	
	/** Set the size of the connector using a predefined style. */
	def size( style:Style, camera:Camera ) { size( camera.metrics.lengthToGu( style.getSize, 0 ) ) }
	
	def dynSize( style:Style, camera:Camera, element:GraphicElement ) {
		var w = camera.metrics.lengthToGu( style.getSize, 0 )
		
		if( element.hasAttribute( "ui.size" ) ) {
			w = camera.metrics.lengthToGu( StyleConstants.convertValue( element.getAttribute( "ui.size" ) ) )
		}
		
		size( w )
	}
	
	/** Define the two end points sizes using the fit size stored in the nodes. */
	def endPoints( from:GraphicNode, to:GraphicNode, directed:Boolean, camera:Camera ) {
		val fromInfo = from.getAttribute( ElementInfo.attributeName ).asInstanceOf[NodeInfo]
		val toInfo   = to.getAttribute( ElementInfo.attributeName ).asInstanceOf[NodeInfo]
		
		if( fromInfo != null && toInfo != null ) {
//Console.err.printf( "Using the dynamic size%n" )
			isDirected     = directed
			theSourceSizeX = fromInfo.theSize.x
			theSourceSizeY = fromInfo.theSize.y
			theTargetSizeX = toInfo.theSize.x
			theTargetSizeY = toInfo.theSize.y
		} else {
//Console.err.printf( "NOT using the dynamic size :-(%n" )
			endPoints( from.getStyle, to.getStyle, directed, camera )
		}
	}
	
	/** Define the two end points sizes (does not use the style nor the fit size). */
	def endPoints( sourceWidth:Float, targetWidth:Float, directed:Boolean ) {
		theSourceSizeX = sourceWidth
		theSourceSizeY = sourceWidth
		theTargetSizeX = targetWidth
		theTargetSizeY = targetWidth
		isDirected = directed
	}
	
	/** Define the two end points sizes (does not use the style nor the fit size). */
	def endPoints( sourceWidth:Float, sourceHeight:Float, targetWidth:Float, targetHeight:Float, directed:Boolean ) {
		theSourceSizeX = sourceWidth
		theSourceSizeY = sourceHeight
		theTargetSizeX = targetWidth
		theTargetSizeY = targetHeight
		isDirected = directed
	}
	
	/** Compute the two end points sizes using the style (may not use the fit size). */
	def endPoints( sourceStyle:Style, targetStyle:Style, directed:Boolean, camera:Camera ) {
		theSourceSizeX = camera.metrics.lengthToGu( sourceStyle.getSize, 0 )
		
		if( sourceStyle.getSize.size > 1 )
		      theSourceSizeY = camera.metrics.lengthToGu( sourceStyle.getSize, 1 )
		else theSourceSizeY = theSourceSizeX
		
		theTargetSizeX = camera.metrics.lengthToGu( targetStyle.getSize, 0 )
		
		if( targetStyle.getSize.size > 1 )
		      theTargetSizeY = camera.metrics.lengthToGu( targetStyle.getSize, 1 )
		else theTargetSizeY = theTargetSizeX
		
		isDirected = directed
	}
	
	/** Give the position of the origin and destination points. */
	def position( info:EdgeInfo, style:Style, xFrom:Float, yFrom:Float, xTo:Float, yTo:Float ) { position( info, style, xFrom, yFrom, xTo, yTo, 0, null ) }
	
	/**
	 * Give the position of the origin and destination points, for multi edges.
	 * <p>
	 * This only sets the isCurve/isLoop and ctrl1/ctrl2 for multi-edges/edge-loops, if the shape of the
	 * edge given by the style is also a curve, the make() methods must set these fields (we cannot do it here
	 * since we do not know the curves). This is important since arrows and sprites can be attached to edges.
	 * </p>
	 */
	def position( info:EdgeInfo, style:Style, xFrom:Float, yFrom:Float, xTo:Float, yTo:Float, multi:Int, group:GraphicEdge#EdgeGroup ) {
		this.info = info
		info.points(0).set( xFrom, yFrom )
		info.points(3).set( xTo, yTo )
		if( group != null ) {
			info.isMulti = group.getCount
			info.isCurve = true
			if( xFrom == xTo && yFrom == yTo ) {
				info.isLoop = true
				positionEdgeLoop( xFrom, yFrom, multi )
			} else {
				info.isLoop = false
				positionMultiEdge( xFrom, yFrom, xTo, yTo, multi, group )
			}
		} else {
			if( xFrom == xTo && yFrom == yTo ) {
				info.isMulti = 0
				info.isLoop  = true
				info.isCurve = true
				positionEdgeLoop( xFrom, yFrom, 0 )
			} else {
				info.isMulti = 0
				info.isLoop  = false
				info.isCurve = false
				// This does not mean the edge is not a curve, this means
				// that with what we know actually it is not a curve.
				// The style mays indicate a curve.
			}		  
		}
	}
	
	/** Define the control points to make the edge a loop. */
	protected def positionEdgeLoop( x:Float, y:Float, multi:Int ) {
		var m = 1f + multi * 0.2f
		val s = ( theTargetSizeX + theTargetSizeY ) / 2
		var d = s / 2 * m + 4 * s * m

		info.points(1).set( x + d, y )
		info.points(2).set( x, y + d )
//		to.set( x, y + theTargetNodeSize/2*m )
	}
	
	/** Define the control points to make this edge a part of a multi-edge. */
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
  
		info.points(1).set( x1 + vx, y1 + vy )
		info.points(2).set( x2 - vx, y2 - vy )
  
		val m = multi + ( if( edge.from eq main.from ) 0 else 1 )
  
		if( m % 2 == 0 ) {
			info.points(1).x += ( vx2 + ox )
			info.points(1).y += ( vy2 + oy )
			info.points(2).x += ( vx2 + ox )
			info.points(2).y += ( vy2 + oy )
		} else {
			info.points(1).x -= ( vx2 - ox )
			info.points(1).y -= ( vy2 - oy )
			info.points(2).x -= ( vx2 - ox ) 
			info.points(2).y -= ( vy2 - oy )		  
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
		if( connector.info.isCurve ) {
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