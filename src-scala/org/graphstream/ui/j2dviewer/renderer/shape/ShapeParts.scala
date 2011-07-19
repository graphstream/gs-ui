/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt._
import java.awt.geom._

import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.util._
import org.graphstream.ui.geom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

/**
 * Trait for shapes that can be filled.
 */
trait Fillable {
	/** The fill paint. */
	var fillPaint:ShapePaint = null
 
	/** Value in [0..1] for dyn-colors. */
	var theFillPercent = 0.0;

    /**
     * Fill the shape.
     * @param g The Java2D graphics.
     * @param dynColor The value between 0 and 1 allowing to know the dynamic plain color, if any.
     * @param shape The awt shape to fill.
     */
	def fill( g:Graphics2D, dynColor:Double, shape:java.awt.Shape, camera:Camera ) {
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
 	protected def configureFillableForGroup( style:Style, camera:Camera ) {
 		fillPaint = ShapePaint( style )
 	}
 	
    /**
     *  Configure the dynamic parts needed to fill the shape.
     */
  	protected def configureFillableForElement( style:Style, camera:Camera, element:GraphicElement ) {
  	  	if( style.getFillMode == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
  	  		element.getAttribute[AnyRef]( "ui.color" ) match {
  	  			case x:Number => theFillPercent = x.floatValue
  	  			case _        => theFillPercent = 0f
  	  		}
  	  	} else {
  	  		theFillPercent = 0
  	  	}
  	}
}

trait FillableMulticolored {
	var fillColors:Array[Color] = null
	
	protected def configureFillableMultiColoredForGroup( style:Style, camera:Camera ) {
		val count = style.getFillColorCount
		
		if( fillColors == null || fillColors.length != count ) {
			fillColors = new Array[Color]( count )
			
			for( i <- 0 until count )
				fillColors( i ) = style.getFillColor( i )
		}
	}
	
//	protected def configureFillableMulticoloredForElement( g:Graphics2D, element:GraphicElement, info:ElementInfo, camera:Camera ) {
//	}
}

/**
 * Shape that cannot be filled, but must be stroked.
 */
trait FillableLine {
	var fillColors:Array[Color] = null
	var fillStroke:ShapeStroke = null
	var theFillPercent = 0.0;
  
	def fill( g:Graphics2D, width:Double, dynColor:Double, shape:java.awt.Shape ) {
		if( fillStroke != null ) {
			val stroke = fillStroke.stroke( width )
   
			g.setColor( fillColors(0) )
			g.setStroke( stroke )
			g.draw( shape )
		}
	}
 
	def fill( g:Graphics2D, width:Double, shape:java.awt.Shape ) { fill( g, width, theFillPercent, shape ) }
 
	protected def configureFillableLineForGroup( style:Style, camera:Camera ) {
		fillStroke = ShapeStroke.strokeForConnectorFill( style )
	}

	protected def configureFillableLineForElement( style:Style, camera:Camera, element:GraphicElement ) {
  	  	// TODO look at this and try to create the fillColors at the in ForGroup configuration !!!
		theFillPercent = 0
  	  	if( style.getFillMode == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
  	  		element.getAttribute[AnyRef]( "ui.color" ) match {
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
	var theStrokeWidth = 0.0

 	/** Paint the stroke of the shape. */
	def stroke( g:Graphics2D, shape:java.awt.Shape ) {
		if( theStroke != null ) {
			g.setStroke( theStroke.stroke( theStrokeWidth ) )
			g.setColor( strokeColor )
			g.draw( shape )
		}	  
	}
     
 	/** Configure all the static parts needed to stroke the shape. */
 	protected def configureStrokableForGroup( style:Style, camera:Camera ) {
		theStrokeWidth = camera.metrics.lengthToGu( style.getStrokeWidth )
		/*if( strokeColor == null )*/ strokeColor = ShapeStroke.strokeColor( style )
		/*if( theStroke   == null )*/ theStroke   = ShapeStroke.strokeForArea( style )
 	}
}

/** Trait for strokable lines. */
trait StrokableLine extends Strokable {
 	protected override def configureStrokableForGroup( style:Style, camera:Camera ) {
		theStrokeWidth = camera.metrics.lengthToGu( style.getStrokeWidth ) + camera.metrics.lengthToGu( style.getSize, 0 )
		strokeColor    = ShapeStroke.strokeColor( style )
		theStroke      = ShapeStroke.strokeForArea( style )
 	}
 	protected def configureStrokableLineForGroup( style:Style, camera:Camera ) { configureStrokableForGroup( style, camera ) }
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
	def shadowWidth( width:Double, height:Double ) { theShadowWidth.set( width, height ) }
 
 	/** Set the shadow offset according to the shape. */ 
	def shadowOffset( xoff:Double, yoff:Double ) { theShadowOff.set( xoff, yoff ) }
 
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
 	protected def configureShadowableForGroup( style:Style, camera:Camera ) {
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
	protected var theShadowWidth = 0.0
 
	/** Offset of the shadow according to the shape center. */
	protected val theShadowOff = new Point2

	protected var theShadowColor:Color = null
 
	/** Sety the shadow width added to the shape width. */
	def shadowWidth( width:Double ) { theShadowWidth = width }
 
 	/** Set the shadow offset according to the shape. */ 
	def shadowOffset( xoff:Double, yoff:Double ) { theShadowOff.set( xoff, yoff ) }
  
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
 	protected def configureShadowableLineForGroup( style:Style, camera:Camera ) {
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
 	def decorArea( g:Graphics2D, camera:Camera, iconAndText:IconAndText, element:GraphicElement, shape:java.awt.Shape ) {
 	  	var visible = true
 	  	if( element != null ) visible = camera.isTextVisible( element )
 	  	if( theDecor != null && visible ) {
 	  		val bounds = shape.getBounds2D
 	  		theDecor.renderInside( g, camera, iconAndText, bounds.getMinX, bounds.getMinY, bounds.getMaxX, bounds.getMaxY )
 	  	}
 	}
	
	def decorConnector( g:Graphics2D, camera:Camera, iconAndText:IconAndText, element:GraphicElement, shape:java.awt.Shape ) {
 	  	var visible = true
 	  	if( element != null ) visible = camera.isTextVisible( element )
 	  	if( theDecor != null && visible ) {
 	  		element match {
 	  			case edge:GraphicEdge => {
 	  				theDecor.renderAlong( g, camera, iconAndText, edge.from.x, edge.from.y, edge.to.x, edge.to.y )
 	  			}
 	  			case _ => {
 	  				val bounds = shape.getBounds2D
 	  				theDecor.renderAlong( g, camera, iconAndText, bounds.getMinX, bounds.getMinY, bounds.getMaxX, bounds.getMaxY )
 	  			}
 	  		}
 	  	}
	}
  
  	/** Configure all the static parts needed to decor the shape. */
  	protected def configureDecorableForGroup( style:Style, camera:Camera ) {
		/*if( theDecor == null )*/ theDecor = ShapeDecor( style )
  	}
  	
  	/** Setup the parts of the decor specific to each element. */
  	protected def configureDecorableForElement( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
  		text = element.label
 
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
	
	var target = new Point3
	
	protected def configureOrientableForGroup( style:Style, camera:Camera ) {
		orientation = style.getSpriteOrientation
	}
	
	protected def configureOrientableForElement( camera:Camera, sprite:GraphicSprite ) {
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
						val ei = ge.getAttribute[EdgeInfo]( ElementInfo.attributeName )
						
						if( ei != null )
						     ei.pointOnShape(sprite.getX, target)//setTargetOnEdgeInfo( ei, camera, sprite, ge )
						else setTargetOnLineEdge( camera, sprite, ge ) 
					}
				}
			}
			case _ => { orientation = SpriteOrientation.NONE }
		}
	}
	
//	private def setTargetOnEdgeInfo( ei:EdgeInfo, camera:Camera, sprite:GraphicSprite, ge:GraphicEdge ) {
//	    ei.pointOnShape(sprite.getX, target)
//		if( ei.isCurve  ) {
//			CubicCurve.eval( ei(0), ei(1), ei(2), ei(3), sprite.getX, target )
//		} else if( ei.isPoly ) {
//		    
//		} else {
//			setTargetOnLineEdge( camera, sprite, ge )
//		}
//	}
	
	private def setTargetOnLineEdge( camera:Camera, sprite:GraphicSprite, ge:GraphicEdge ) {
		val dir = new Vector2( ge.to.getX-ge.from.getX, ge.to.getY-ge.from.getY )
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
	
	protected def configureAreaForGroup( style:Style, camera:Camera ) {
		size( style, camera )
	}
	
	protected def configureAreaForElement( g:Graphics2D, camera:Camera, info:NodeInfo, element:GraphicElement, x:Double, y:Double ) {
		dynSize( element.getStyle, camera, element )
		positionAndFit( g, camera, info, element, x, y, 0, 0 )
	}
	
	protected def configureAreaForElement( g:Graphics2D, camera:Camera, info:NodeInfo, element:GraphicElement, x:Double, y:Double, contentOverallWidth:Double, contentOverallHeight:Double ) {
		dynSize( element.getStyle, camera, element )
		positionAndFit( g, camera, info, element, x, y, contentOverallWidth, contentOverallHeight )
	}
	
	protected def configureAreaForElement( g:Graphics2D, camera:Camera, info:NodeInfo, element:GraphicElement, decor:ShapeDecor ) {
		var pos = camera.getNodeOrSpritePositionGU( element, null )
		
		if( fit ) {
			val decorSize = decor.size( g, camera, info.iconAndText )
		
			configureAreaForElement( g, camera, info.asInstanceOf[NodeInfo], element, pos.x, pos.y, decorSize._1, decorSize._2 )
		} else {
			configureAreaForElement( g, camera, info.asInstanceOf[NodeInfo], element, pos.x, pos.y )
		}
	}
	
	private def size( width:Double, height:Double ) { theSize.set( width, height ) }
	
	private def size( style:Style, camera:Camera ) { 
		val w = camera.metrics.lengthToGu( style.getSize, 0 )
		val h = if( style.getSize.size > 1 ) camera.metrics.lengthToGu( style.getSize, 1 ) else w
  
		theSize.set( w, h )
		
		fit = ( style.getSizeMode == StyleConstants.SizeMode.FIT )
	}
	
	private def dynSize( style:Style, camera:Camera, element:GraphicElement ) {
		var w = camera.metrics.lengthToGu( style.getSize, 0 )
		var h = if( style.getSize.size > 1 ) camera.metrics.lengthToGu( style.getSize, 1 ) else w

		if( element.hasAttribute( "ui.size" ) ) {
			w = camera.metrics.lengthToGu( StyleConstants.convertValue( element.getAttribute( "ui.size" ) ) )
			h = w;
		}
  
		theSize.set( w, h )
	}
	
	protected def positionAndFit( g:Graphics2D, camera:Camera, info:NodeInfo, element:GraphicElement, x:Double, y:Double, contentOverallWidth:Double, contentOverallHeight:Double ) {
		if( info != null ) {
			if( contentOverallWidth > 0 && contentOverallHeight > 0 )
				theSize.set( contentOverallWidth, contentOverallHeight )
			
			info.theSize.copy( theSize )
		}

		theCenter.set( x, y )
	}
}

/**
 * Trait for elements painted between two points.
 * 
 * The purpose of this class is to store the lines coordinates of an edge. This connector can
 * be made of only two points, 4 points when this is a bezier curve or more if this is a polyline.
 * The coordinates of these points are stored in a EdgeInfo attribute directly on the edge element
 * since several parts of the rendering need to access it (for example, sprites retrieve it
 * to follow the correct path when attached to this edge).
 */
trait Connector {
// Attribute
	
	var info:EdgeInfo = null
	
	/** Width of the connector. */
	protected var theSize:Double = 0
	
	protected var theTargetSizeX = 0.0
	protected var theTargetSizeY = 0.0
	protected var theSourceSizeX = 0.0
	protected var theSourceSizeY = 0.0
	
	/** Is the connector directed ? */
	var isDirected = false
	
// Command
	
	/** Origin point of the connector. */
	def fromPos:Point3 = info.from
	
	/** First control point. Works only for curves. */
	def byPos1:Point3 = if(info.isCurve) info(1) else null
	
	/** Second control point. Works only for curves. */
	def byPos2:Point3 = if(info.isCurve) info(2) else null
	
	/** Destination of the connector. */
	def toPos:Point3 = info.to
	
	def configureConnectorForGroup( style:Style, camera:Camera ) {
		size( style, camera )
	}
	
	def configureConnectorForElement( g2:Graphics2D, camera:Camera, element:GraphicEdge, info:EdgeInfo ) {
	    this.info = info
	    
		dynSize( element.getStyle, camera, element )
		endPoints( element.from, element.to, element.isDirected, camera )
		
		if(element.getGroup != null) {
	        info.setMulti(element.getGroup.getCount)
	    }
		
		if(element.hasAttribute("ui.points")) {
		    info.setPoly(element.getAttribute("ui.points").asInstanceOf[AnyRef])
		} else {
			positionForLinesAndCurves( info, element.from.getStyle, element.from.getX, element.from.getY,
				element.to.getX, element.to.getY, element.multi, element.getGroup )
		}
	}
	
	/** Set the size (`width`) of the connector. */
	private def size( width:Double ) { theSize = width }
	
	/** Set the size of the connector using a predefined style. */
	private def size( style:Style, camera:Camera ) { size( camera.metrics.lengthToGu( style.getSize, 0 ) ) }
	
	private def dynSize( style:Style, camera:Camera, element:GraphicElement ) {
		var w = theSize  // already set by the configureForGroup() //camera.metrics.lengthToGu( style.getSize, 0 )
		
		if( element.hasAttribute( "ui.size" ) ) {
			w = camera.metrics.lengthToGu( StyleConstants.convertValue( element.getAttribute( "ui.size" ) ) )
		}
		
		size( w )
	}
	
	/** Define the two end points sizes using the fit size stored in the nodes. */
	private def endPoints( from:GraphicNode, to:GraphicNode, directed:Boolean, camera:Camera ) {
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
	private def endPoints( sourceWidth:Double, targetWidth:Double, directed:Boolean ) {
		theSourceSizeX = sourceWidth
		theSourceSizeY = sourceWidth
		theTargetSizeX = targetWidth
		theTargetSizeY = targetWidth
		isDirected = directed
	}
	
	/** Define the two end points sizes (does not use the style nor the fit size). */
	private def endPoints( sourceWidth:Double, sourceHeight:Double, targetWidth:Double, targetHeight:Double, directed:Boolean ) {
		theSourceSizeX = sourceWidth
		theSourceSizeY = sourceHeight
		theTargetSizeX = targetWidth
		theTargetSizeY = targetHeight
		isDirected = directed
	}
	
	/** Compute the two end points sizes using the style (may not use the fit size). */
	private def endPoints( sourceStyle:Style, targetStyle:Style, directed:Boolean, camera:Camera ) {
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
	private def positionForLinesAndCurves( info:EdgeInfo, style:Style, xFrom:Double, yFrom:Double, xTo:Double, yTo:Double ) {
	    positionForLinesAndCurves( info, style, xFrom, yFrom, xTo, yTo, 0, null ) }
	
	/**
	 * Give the position of the origin and destination points, for multi edges.
	 * <p>
	 * This only sets the isCurve/isLoop and ctrl1/ctrl2 for multi-edges/edge-loops, if the shape of the
	 * edge given by the style is also a curve, the make() methods must set these fields (we cannot do it here
	 * since we do not know the curves). This is important since arrows and sprites can be attached to edges.
	 * </p>
	 */
	private def positionForLinesAndCurves( info:EdgeInfo, style:Style, xFrom:Double, yFrom:Double, xTo:Double, yTo:Double, multi:Int, group:GraphicEdge#EdgeGroup ) {
	    
		//info.points(0).set( xFrom, yFrom )
		//info.points(3).set( xTo, yTo )
		if( group != null ) {
			if( xFrom == xTo && yFrom == yTo ) {
				positionEdgeLoop(info, xFrom, yFrom, multi)
			} else {
				positionMultiEdge(info, xFrom, yFrom, xTo, yTo, multi, group)
			}
		} else {
			if( xFrom == xTo && yFrom == yTo ) {
				positionEdgeLoop(info, xFrom, yFrom, 0)
			} else {
				// This does not mean the edge is not a curve, this means
				// that with what we know actually it is not a curve.
				// The style mays indicate a curve.
			    info.setLine(xFrom, yFrom, 0, xTo, yTo, 0)
			    
			    // XXX we will have to mutate the info into a curve later.
			}		  
		}
	}
	
	/** Define the control points to make the edge a loop. */
	private def positionEdgeLoop(info:EdgeInfo, x:Double, y:Double, multi:Int) {
		var m = 1f + multi * 0.2f
		val s = ( theTargetSizeX + theTargetSizeY ) / 2
		var d = s / 2 * m + 4 * s * m

		info.setLoop(
				x, y, 0,
				x+d, y, 0,
				x, y+d, 0 )
	}
	
	/** Define the control points to make this edge a part of a multi-edge. */
	private def positionMultiEdge(info:EdgeInfo, x1:Double, y1:Double, x2:Double, y2:Double, multi:Int, group:GraphicEdge#EdgeGroup) {
		var vx  = (  x2 - x1 )
		var vy  = (  y2 - y1 )
		var vx2 = (  vy ) * 0.6
		var vy2 = ( -vx ) * 0.6
		val gap = 0.2
		var ox  = 0.0
		var oy  = 0.0
		val f   = ( ( 1 + multi ) / 2 ) * gap // (1+multi)/2 must be done on integers.
  
		vx *= 0.2
		vy *= 0.2
  
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
  
		var xx1 = x1 + vx
		var yy1 = y1 + vy
		var xx2 = x2 - vx
		var yy2 = y2 - vy
  
		val m = multi + ( if( edge.from eq main.from ) 0 else 1 )
  
		if( m % 2 == 0 ) {
			xx1 += ( vx2 + ox )
			yy1 += ( vy2 + oy )
			xx2 += ( vx2 + ox )
			yy2 += ( vy2 + oy )
		} else {
			xx1 -= ( vx2 - ox )
			yy1 -= ( vy2 - oy )
			xx2 -= ( vx2 - ox ) 
			yy2 -= ( vy2 - oy )		  
		}
		
		info.setCurve(
		        x1, y1, 0,
		        xx1, yy1, 0,
		        xx2, yy2, 0,
		        x2, y2, 0 )
	}
}

trait AreaOnConnector extends Area {
	protected var theConnector:Connector = null
	protected var theEdge:GraphicEdge = null

	/** XXX must call this method explicitly in the renderer !!! bad !!! XXX */
	def theConnectorYoureAttachedTo( connector:Connector ) { theConnector = connector }
	
	protected def configureAreaOnConnectorForGroup( style:Style, camera:Camera ) {
		sizeForEdgeArrow( style, camera )
	}
	
	protected def configureAreaOnConnectorForElement( edge:GraphicEdge, style:Style, camera:Camera ) {
		connector( edge )
		theCenter.set( edge.to.getX, edge.to.getY )
	}
	
	private def connector( edge:GraphicEdge ) { theEdge = edge }
 
	private def sizeForEdgeArrow( style:Style, camera:Camera ) {
		val w = camera.metrics.lengthToGu( style.getArrowSize, 0 )
		val h = if( style.getArrowSize.size > 1 ) camera.metrics.lengthToGu( style.getArrowSize, 1 ) else w
  
		theSize.set( w, h )
	}
}