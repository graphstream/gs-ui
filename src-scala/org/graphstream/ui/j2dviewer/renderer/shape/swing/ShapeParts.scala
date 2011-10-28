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

package org.graphstream.ui.j2dviewer.renderer.shape.swing

import java.awt._
import java.awt.geom._

import org.graphstream.ui.j2dviewer.renderer.shape._
import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.util._
import org.graphstream.ui.geom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

/** Trait for shapes that can be filled. 
  * 
  * Works only for Swing only.
  */
trait Fillable {
	/** The fill paint. */
	var fillPaint:ShapePaint = null
 
	/** Value in [0..1] for dyn-colors. */
	var theFillPercent = 0.0
	
	var theFillColor:Color = null

    /** Fill the shape.
      * @param g The Java2D graphics.
      * @param dynColor The value between 0 and 1 allowing to know the dynamic plain color, if any.
      * @param shape The awt shape to fill. */
	def fill(g:Graphics2D, dynColor:Double, optColor:Color, shape:java.awt.Shape, camera:Camera) {
		fillPaint match {
		  case p:ShapeAreaPaint  => g.setPaint( p.paint( shape, camera.metrics.ratioPx2Gu ) );    g.fill( shape )
		  case p:ShapeColorPaint => g.setPaint( p.paint( dynColor, optColor ) ); g.fill( shape )
		  case _                 => null; // No fill. // printf( "no fill !!!%n" ) 
		}
	}
 
    /** Fill the shape.
      * @param g The Java2D graphics.
      * @param shape The awt shape to fill. */
 	def fill(g:Graphics2D, shape:java.awt.Shape, camera:Camera) { fill( g, theFillPercent, theFillColor, shape, camera ) }

    /** Configure all static parts needed to fill the shape. */
 	protected def configureFillableForGroup( style:Style, camera:Camera ) {
 		fillPaint = ShapePaint( style )
 	}
 	
    /** Configure the dynamic parts needed to fill the shape. */
  	protected def configureFillableForElement( style:Style, camera:Camera, element:GraphicElement ) {
  	  	if( style.getFillMode == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
  	  		element.getAttribute[AnyRef]( "ui.color" ) match {
  	  			case x:Number => theFillPercent = x.floatValue; theFillColor = null
  	  			case x:Color  => theFillColor = x; theFillPercent = 0
  	  			case _        => theFillPercent = 0; theFillColor = null
  	  		}
  	  	} else {
  	  		theFillPercent = 0
  	  	}
  	}
}

trait FillableMulticolored {
	var fillColors:Array[Color] = null
	
	protected def configureFillableMultiColoredForGroup(style:Style, camera:Camera) {
		val count = style.getFillColorCount
		
		if(fillColors == null || fillColors.length != count) {
			fillColors = new Array[Color](count)
			
			for( i <- 0 until count )
				fillColors(i) = style.getFillColor(i)
		}
	}
}

/**
 * Shape that cannot be filled, but must be stroked.
 * 
 * Works for Swing only.
 */
trait FillableLine {
	//var fillColors:Array[Color] = null
	var fillStroke:ShapeStroke = null
	var theFillPercent = 0.0
	var theFillColor:Color = null
  
	def fill( g:Graphics2D, width:Double, dynColor:Double, shape:java.awt.Shape ) {
		if( fillStroke != null ) {
			val stroke = fillStroke.stroke( width )
   
//			g.setColor( fillColors(0) )
			g.setColor( theFillColor )
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
  	  			case x:Number => theFillPercent = x.floatValue; theFillColor = ShapePaint.interpolateColor( style.getFillColors, theFillPercent )
  	  			case x:Color => theFillColor = x; theFillPercent = 0
  	  			case _ => theFillPercent = 0f; theFillColor = style.getFillColor(0)
  	  		}
       
  	  		//fillColors = ShapePaint.createColors( style, style.getFillColorCount, style.getFillColors )
  	  		//fillColors(0) = if(theFillColor != null) theFillColor else ShapePaint.interpolateColor( fillColors, theFillPercent )
  	  	}
  	  	else
        {
//  	  		if( fillColors == null || fillColors.length < 1 )
//  	  			fillColors = new Array[Color]( 1 )
       
//  	  		fillColors(0) = style.getFillColor( 0 )
  	  	    theFillColor = style.getFillColor(0)
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
   			case p:ShapeColorPaint => g.setPaint( p.paint( 0, null ) );     g.fill( shape )
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
 	def decorArea(backend:Backend, camera:Camera, iconAndText:IconAndText, element:GraphicElement, shape:java.awt.Shape ) {
 	  	var visible = true
 	  	if( element != null ) visible = camera.isTextVisible( element )
 	  	if( theDecor != null && visible ) {
 	  		val bounds = shape.getBounds2D
 	  		theDecor.renderInside(backend, camera, iconAndText, bounds.getMinX, bounds.getMinY, bounds.getMaxX, bounds.getMaxY )
 	  	}
 	}
	
	def decorConnector(backend:Backend, camera:Camera, iconAndText:IconAndText, element:GraphicElement, shape:java.awt.Shape ) {
 	  	var visible = true
 	  	if( element != null ) visible = camera.isTextVisible( element )
 	  	if( theDecor != null && visible ) {
 	  		element match {
 	  			case edge:GraphicEdge => {
 	  				theDecor.renderAlong(backend, camera, iconAndText, edge.from.x, edge.from.y, edge.to.x, edge.to.y )
 	  			}
 	  			case _ => {
 	  				val bounds = shape.getBounds2D
 	  				theDecor.renderAlong(backend, camera, iconAndText, bounds.getMinX, bounds.getMinY, bounds.getMaxX, bounds.getMaxY )
 	  			}
 	  		}
 	  	}
	}
  
  	/** Configure all the static parts needed to decor the shape. */
  	protected def configureDecorableForGroup( style:Style, camera:Camera ) {
		/*if( theDecor == null )*/ theDecor = ShapeDecor( style )
  	}
  	
  	/** Setup the parts of the decor specific to each element. */
  	protected def configureDecorableForElement(backend:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
  		text = element.label
 
  		if( skel != null ) {
  			val style = element.getStyle
  			
  			if( skel.iconAndText == null )
  				skel.iconAndText = ShapeDecor.iconAndText( style, camera, element )

  			if( style.getIcon != null && style.getIcon.equals( "dynamic" ) && element.hasAttribute( "ui.icon" ) ) {
  				val url = element.getLabel("ui.icon").toString
  				skel.iconAndText.setIcon(backend, url)
// Console.err.printf( "changing icon %s%n", url )
  			}
// else Console.err.print( "NOT changing icon... %b %s %b%n".format( style.getIcon != null, style.getIcon, element.hasAttribute( "ui.icon" ) ) )
  			
  			skel.iconAndText.setText(backend, element.label)
  		}
  	}
}