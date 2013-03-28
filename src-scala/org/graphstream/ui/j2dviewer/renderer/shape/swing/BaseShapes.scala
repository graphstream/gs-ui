/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
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

//import java.awt._
//import java.awt.geom._

import org.graphstream.ui.geom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units
import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.util._
import org.graphstream.ui.j2dviewer.renderer.shape._

import scala.math._

/**
 * Base for shapes centered around one point.
 */
trait AreaShape
	extends Shape
	with Area
	with Fillable 
	with Strokable 
	with Shadowable 
	with Decorable {
 	
	def configureForGroup(bck:Backend, style:Style, camera:Camera) {
 	  	configureFillableForGroup(bck, style, camera)
 	  	configureStrokableForGroup(style, camera)
 	  	configureShadowableForGroup(style, camera)
 	  	configureDecorableForGroup(style, camera)
 	  	configureAreaForGroup(style, camera)
 	}
 
	def configureForElement(bck:Backend, element:GraphicElement, skel:Skeleton, camera:Camera) {
		configureFillableForElement(element.getStyle, camera, element)
		configureDecorableForElement(bck, camera, element, skel)
		configureAreaForElement(bck, camera, skel.asInstanceOf[AreaSkeleton], element, theDecor)
	}
}

trait AreaOnConnectorShape
	extends Shape
	with AreaOnConnector
	with Fillable
	with Strokable
	with Shadowable {
	
	def configureForGroup(bck:Backend, style:Style, camera:Camera) {
		configureFillableForGroup(bck, style, camera)
		configureStrokableForGroup(style, camera)
		configureShadowableForGroup(style, camera)
		configureAreaOnConnectorForGroup(style, camera)
	}
	
	def configureForElement(bck:Backend, element:GraphicElement, skel:Skeleton, camera:Camera) {
		configureFillableForElement(element.getStyle, camera, element)
		configureAreaOnConnectorForElement(element.asInstanceOf[GraphicEdge], element.getStyle, camera)
	}
}
 
/**
 * Base for shapes rendered between two points.
 */
trait ConnectorShape
	extends Shape
	with Connector
	with Decorable {
	
	def configureForGroup(bck:Backend, style:Style, camera:Camera) {
		configureDecorableForGroup(style, camera)
		configureConnectorForGroup(style, camera)
	}
	
	def configureForElement(bck:Backend, element:GraphicElement, skel:Skeleton, camera:Camera) {
		configureDecorableForElement(bck, camera, element, skel)
		configureConnectorForElement(camera, element.asInstanceOf[GraphicEdge], skel.asInstanceOf[ConnectorSkeleton] /* TODO check this ! */)
	}
}
 
trait LineConnectorShape
	extends ConnectorShape
	with FillableLine
	with StrokableLine
	with ShadowableLine {
	
	override def configureForGroup(bck:Backend, style:Style, camera:Camera) {
		super.configureForGroup(bck, style, camera)
		configureFillableLineForGroup(bck, style, camera, theSize)
		configureStrokableLineForGroup(style, camera)
		configureShadowableLineForGroup(style, camera)
	}
	
	override def configureForElement(bck:Backend, element:GraphicElement, skel:Skeleton, camera:Camera) {
		configureFillableLineForElement(element.getStyle, camera, element)
		super.configureForElement(bck, element, skel, camera)
	}
}

trait AreaConnectorShape
	extends ConnectorShape
	with Fillable
	with Strokable
	with Shadowable {
	
	override def configureForGroup(bck:Backend, style:Style, camera:Camera) {
		configureFillableForGroup(bck, style, camera)
		configureStrokableForGroup(style, camera)
		configureShadowableForGroup(style, camera)
		super.configureForGroup(bck, style, camera)
	}
	
	override def configureForElement(bck:Backend, element:GraphicElement, skel:Skeleton, camera:Camera) {
		configureFillableForElement(element.getStyle, camera, element)
		super.configureForElement(bck, element, skel, camera)
	}}

	
trait RectangularAreaShape extends AreaShape {
	val theShape:java.awt.geom.RectangularShape
 
 	protected def make(bck:Backend, camera:Camera) {
		val w = theSize.x
		val h = theSize.y
		
		theShape.setFrame(theCenter.x-w/2, theCenter.y-h/2, w, h)
 	}
 
 	protected def makeShadow(bck:Backend, camera:Camera) {
		val x = theCenter.x + theShadowOff.x
		val y = theCenter.y + theShadowOff.y
		val w = theSize.x + theShadowWidth.x * 2
		val h = theSize.y + theShadowWidth.y * 2
		
		theShape.setFrame(x-w/2, y-h/2, w, h)
 	}
  
 	def renderShadow(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
		makeShadow(bck, camera)
 		cast(bck.graphics2D, theShape)
 	}
  
 	def render(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton ) {
 		make(bck, camera )
 		fill(bck.graphics2D, theShape, camera)
 		stroke(bck.graphics2D, theShape)
 		decorArea(bck, camera, skel.iconAndText, element, theShape)
 	}
}

abstract class OrientableRectangularAreaShape extends RectangularAreaShape with Orientable {
	 
	var p:Point3 = null
	var angle = 0.0
	var w = 0.0
	var h = 0.0
	var oriented = false
	
	override def configureForGroup(bck:Backend, style:Style, camera:Camera) {
		super.configureForGroup(bck, style, camera)
		configureOrientableForGroup(style, camera)
		oriented = (style.getSpriteOrientation != StyleConstants.SpriteOrientation.NONE)
	}
 
	override def configureForElement(bck:Backend, element:GraphicElement, skel:Skeleton, camera:Camera) {
		super.configureForElement(bck, element, skel, camera)
		configureOrientableForElement(camera, element.asInstanceOf[GraphicSprite] /* Check This XXX TODO !*/);
	}
	
	protected override def make(bck:Backend, camera:Camera) { make(bck, false, camera) }
	protected override def makeShadow(bck:Backend, camera:Camera) { make(bck, true, camera) }

	protected def make(bck:Backend, forShadow:Boolean, camera:Camera) {
		if (oriented) {
			val theDirection = new Vector2(
					target.x - theCenter.x,
					target.y - theCenter.y )
			
			theDirection.normalize
		
			var x = theCenter.x
			var y = theCenter.y
		
			if( forShadow ) {
				x += theShadowOff.x
				y += theShadowOff.y
			}
		
			p     = camera.transformGuToPx(x, y, 0)	// Pass to pixels, the image will be drawn in pixels.
			angle = acos(theDirection.dotProduct( 1, 0 ))
		
			if( theDirection.y > 0 )			// The angle is always computed for acute angles
				angle = ( Pi - angle )
	
			w = camera.metrics.lengthToPx(theSize.x, Units.GU)
			h = camera.metrics.lengthToPx(theSize.y, Units.GU)
			theShape.setFrame(0, 0, w, h)
		} else {
			if (forShadow)
				super.makeShadow(bck, camera)
			else
				super.make(bck, camera)
		}
	}
	
 
	override def renderShadow(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
 		make(bck, true, camera)
 		
 		val g = bck.graphics2D
 		
 		if (oriented) {
	 		val Tx = g.getTransform
	 		val Tr = new java.awt.geom.AffineTransform
	 		Tr.translate( p.x, p.y )								// 3. Position the image at its position in the graph.
	 		Tr.rotate( angle )										// 2. Rotate the image from its center.
	 		Tr.translate( -w/2, -h/2 )								// 1. Position in center of the image.
	 		g.setTransform( Tr )									// An identity matrix.
 			cast(g, theShape)
	 		g.setTransform( Tx )									// Restore the original transform
 		} else {
 			super.renderShadow(bck, camera, element, skel)
 		}
	}
 
	override def render(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
 		make(bck, false, camera)
		
 		val g = bck.graphics2D
 		
 		if (oriented) {
	 		val Tx = g.getTransform
	 		val Tr = new java.awt.geom.AffineTransform
	 		Tr.translate( p.x, p.y )								// 3. Position the image at its position in the graph.
	 		Tr.rotate( angle )										// 2. Rotate the image from its center.
	 		Tr.translate( -w/2, -h/2 )								// 1. Position in center of the image.
	 		g.setTransform( Tr )									// An identity matrix.
	 		stroke(g, theShape)
	 		fill(g, theShape, camera)
	 		g.setTransform( Tx )									// Restore the original transform
	 		theShape.setFrame(theCenter.x-w/2, theCenter.y-h/2, w, h)
	 		decorArea(bck, camera, skel.iconAndText, element, theShape)
 		} else {
 			super.render(bck, camera, element, skel)
 		}
 	}
}

abstract class PolygonalShape extends AreaShape {
	var theShape = new java.awt.geom.Path2D.Double
 
 	def renderShadow(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
 		makeShadow(bck, camera)
 		cast(bck.graphics2D, theShape)
 	}
  
 	def render(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
 	    val g = bck.graphics2D
 		make(bck, camera)
 		fill(g, theShape, camera)
 		stroke(g, theShape)
 		decorArea(bck, camera, skel.iconAndText, element, theShape)
 	}
}

class LineShape extends LineConnectorShape {
	protected var theShapeL = new java.awt.geom.Line2D.Double
	protected var theShapeC = new java.awt.geom.CubicCurve2D.Double
	protected var theShape:java.awt.Shape = null
// Command
  
	protected def make(bck:Backend, camera:Camera) {
		val from = skel.from
		val to   = skel.to
		if( skel.isCurve ) {
			val ctrl1 = skel(1)
			val ctrl2 = skel(2)
			theShapeC.setCurve( from.x, from.y, ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, to.x, to.y )
			theShape = theShapeC
		} else {
			theShapeL.setLine( from.x, from.y, to.x, to.y )
			theShape = theShapeL
		} 
	}
	protected def makeShadow(bck:Backend, camera:Camera) {
		var x0 = skel.from.x + theShadowOff.x
		var y0 = skel.from.y + theShadowOff.y
		var x1 = skel.to.x + theShadowOff.x
		var y1 = skel.to.y + theShadowOff.y
		
		if( skel.isCurve ) {
			var ctrlx0 = skel(1).x + theShadowOff.x
			var ctrly0 = skel(1).y + theShadowOff.y
			var ctrlx1 = skel(2).x + theShadowOff.x
			var ctrly1 = skel(2).y + theShadowOff.y
			
			theShapeC.setCurve( x0, y0, ctrlx0, ctrly0, ctrlx1, ctrly1, x1, y1 )
			theShape = theShapeC
		} else {
			theShapeL.setLine( x0, y0, x1, y1 )
			theShape = theShapeL
		} 
	}
 
	def renderShadow(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
 		makeShadow(bck, camera)
 		cast(bck.graphics2D, theShape)
	}
 
	def render(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
	    val g = bck.graphics2D
 		make(bck, camera)
 		stroke(g, theShape )
 		fill(g, theSize, theShape)
 		decorConnector(bck, camera, skel.iconAndText, element, theShape)
	}
}

/**
 * A cubic curve shape.
 */
class PolylineEdgeShape extends LineConnectorShape with ShowCubics {
	protected var theShape = new java.awt.geom.Path2D.Double

// Command
 
	protected def make(bck:Backend, camera:Camera) {
		val n = skel.size
		
		theShape.reset
		theShape.moveTo(skel(0).x, skel(0).y)
		
		for(i <- 1 until n) {
			theShape.lineTo(skel(i).x, skel(i).y)
		}		
	}

	protected def makeShadow(bck:Backend, camera:Camera) {
	}
	
	def renderShadow(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
 		makeShadow(bck, camera)
 		cast(bck.graphics2D, theShape)
	}
 
	def render(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
	    val g = bck.graphics2D
 		make(bck, camera)
 		stroke(g, theShape)
 		fill(g, theSize, theShape)
 		decorConnector(bck, camera, skel.iconAndText, element, theShape)
	}
}