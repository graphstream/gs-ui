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
package org.graphstream.ui.j2dviewer.renderer.shape

// This file contains the two most important traits : Area (for nodes and sprites) and Connector
// (for edges). Shapes will either merge one or the other of these traits.

import java.awt._
import java.awt.geom._

import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.util._
import org.graphstream.ui.geom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._
import org.graphstream.ui.j2dviewer.renderer.shape.swing.{ShapeDecor, IconAndText}

/** Trait for elements painted inside an area (most nodes and sprites).
  * 
  * This trait manages the size of the area (the size is rectangular, although the area may not
  * be), its position, and the automatic fit to the contents, if needed.
  * 
  *  As this trait computes the position and size of the shape, it should
  *  probably be configured first when assembling the configureForGroup
  *  and configureForElement methods. */
trait Area {
    /** The shape position. */
	protected val theCenter = new Point2
	/** The shape size. */
	protected val theSize = new Point2
	/** Fit the shape size to its contents? */
	protected var fit = false

	/** Select the general size for the group. */
	protected def configureAreaForGroup(style:Style, camera:Camera) = sizeForGroup(style, camera)
	
	/** Select the general size and position of the shape.
	  * This is done according to:
	  *   - The style,
	  *   - Eventually the element specific size attribute,
	  *   - Eventually the element contents (decor). */
	protected def configureAreaForElement(backend:Backend, camera:Camera, skel:AreaSkeleton, element:GraphicElement, decor:ShapeDecor) {
		var pos = camera.getNodeOrSpritePositionGU(element, null)
		
		if(fit) {
			val decorSize = decor.size(backend, camera, skel.iconAndText)
			if(decorSize._1 == 0 || decorSize._2 == 0)
				sizeForElement(element.getStyle, camera, element)
			positionAndFit(camera, skel, element, pos.x, pos.y, decorSize._1, decorSize._2)
		} else {
			sizeForElement(element.getStyle, camera, element)
			positionAndFit(camera, skel, element, pos.x, pos.y, 0, 0)
		}
	}
	
	/** Set the general size of the area according to the style.
	  * Also look if the style SizeMode says if the element must fit to its contents.
	  * If so, the configureAreaForElement() method will recompute the size for each
	  * element according to the contents (shape decoration). */
	private[this] def sizeForGroup(style:Style, camera:Camera) { 
		val w = camera.metrics.lengthToGu( style.getSize, 0 )
		val h = if( style.getSize.size > 1 ) camera.metrics.lengthToGu( style.getSize, 1 ) else w
  
		theSize.set(w, h)
		
		fit = (style.getSizeMode == StyleConstants.SizeMode.FIT)
	}
	
	/** Try to compute the size of this area according to the given element. */
	private[this] def sizeForElement(style:Style, camera:Camera, element:GraphicElement) {
		var w = camera.metrics.lengthToGu(style.getSize, 0)
		var h = if(style.getSize.size > 1) camera.metrics.lengthToGu(style.getSize, 1) else w
		
		if(style.getSizeMode == StyleConstants.SizeMode.DYN_SIZE) {
			var s:AnyRef = element.getAttribute("ui.size")
		
			if(s ne null) {
				w = camera.metrics.lengthToGu(StyleConstants.convertValue(s))
				h = w;
			}
		}
  
		theSize.set(w, h)
	}
	
	/** Assign a position to the shape according to the element, set the size of the element,
	  * and update the skeleton of the element. */
	private[this] def positionAndFit(camera:Camera, skel:AreaSkeleton, element:GraphicElement, x:Double, y:Double, contentOverallWidth:Double, contentOverallHeight:Double) {
		if(skel != null) {
			if(contentOverallWidth > 0 && contentOverallHeight > 0)
				theSize.set(contentOverallWidth, contentOverallHeight)
			
			skel.theSize.copy(theSize)
		}

		theCenter.set(x, y)
		skel.theCenter.copy(theCenter)
	}
}

/**
 * Trait for elements painted between two points.
 * 
 * The purpose of this class is to retrieve and store in the skeleton the lines coordinates of an
 * edge. This connector can be made of only two points, 4 points when this is a bezier cubic curve
 * or more if this is a polyline or a polycurve or a vectorial description.
 * The coordinates of these points are stored in a ConnectorSkeleton attribute directly on the edge
 * element  since several parts of the rendering need to access it (for example, sprites retrieve it
 * to follow the correct path when attached to this edge).
 */
trait Connector {
// Attribute
	/** We will use it often, better store it. */
	var skel:ConnectorSkeleton = null

	/** The edge, we will also need it often. */
	var theEdge:GraphicEdge = null
	
	/** Width of the connector. */
	protected var theSize:Double = 0
	
	/** Overall size of the area at the end of the connector. */
	protected var theTargetSize = new Point2(0, 0)

	/** Overall sizes of the area at the end of the connector. */
	protected var theSourceSize = new Point2(0, 0)

	/** Is the connector directed ? */
	var isDirected = false
	
// Command
	
	/** Origin point of the connector. */
	def fromPos:Point3 = skel.from
	
	/** First control point. Works only for curves. */
	def byPos1:Point3 = if(skel.isCurve) skel(1) else null
	
	/** Second control point. Works only for curves. */
	def byPos2:Point3 = if(skel.isCurve) skel(2) else null
	
	/** Destination of the connector. */
	def toPos:Point3 = skel.to
	
	def configureConnectorForGroup(style:Style, camera:Camera) = sizeForGroup(style, camera)
	
	def configureConnectorForElement(camera:Camera, element:GraphicEdge, skel:ConnectorSkeleton) {
	    this.skel = skel
	    this.theEdge = element
	    
		sizeForElement(element.getStyle, camera, element)
		endPoints(element.from, element.to, element.isDirected, camera)
		
		if(element.getGroup != null) {
	        skel.setMulti(element.getGroup.getCount)
	    }
		
// XXX TODO there are a lot of cases where we do not need this information.
// It would be good to compute it lazily, only when needed;
// Furthermore, it would be good to be able to update it, only when really
// Changed.
// There is lots of work to be done here, in order to extend the way we get
// the points of the skeleton. Probably a PointVector class that can tell
// when some of its parts changed.
		if(element.hasAttribute("ui.points")) {
		    skel.setPoly(element.getAttribute[AnyRef]("ui.points"))
		} else {
			positionForLinesAndCurves( skel, element.from.getStyle, element.from, 
				element.to, element.multi, element.getGroup )
		}
	}
	
	/** Set the size of the connector using the predefined style. */
	private[this] def sizeForGroup(style:Style, camera:Camera) { theSize = camera.metrics.lengthToGu( style.getSize, 0 ) }
	
	/** Set the size of the connector for this particular `element`. */
	private[this] def sizeForElement(style:Style, camera:Camera, element:GraphicElement) {
		if(style.getSizeMode == StyleConstants.SizeMode.DYN_SIZE && element.hasAttribute( "ui.size")) {
			theSize = camera.metrics.lengthToGu(StyleConstants.convertValue(element.getAttribute("ui.size")))
		}
	}
	
	/** Define the two end points sizes using the fit size stored in the nodes. */
	private[this] def endPoints(from:GraphicNode, to:GraphicNode, directed:Boolean, camera:Camera) {
		val fromInfo = from.getAttribute( Skeleton.attributeName ).asInstanceOf[AreaSkeleton]
		val toInfo   = to.getAttribute( Skeleton.attributeName ).asInstanceOf[AreaSkeleton]
		
		if(fromInfo != null && toInfo != null) {
			isDirected     = directed
			theSourceSize.copy(fromInfo.theSize)
			theTargetSize.copy(toInfo.theSize)
		} else {
			endPoints(from.getStyle, to.getStyle, directed, camera)
		}
	}
	
	/** Define the two end points sizes (does not use the style nor the fit size). */
	private[this] def endPoints(sourceWidth:Double, targetWidth:Double, directed:Boolean) {
	    theSourceSize.set(sourceWidth, sourceWidth)
	    theTargetSize.set(targetWidth, targetWidth)
		isDirected = directed
	}
	
	/** Define the two end points sizes (does not use the style nor the fit size). */
	private[this] def endPoints(sourceWidth:Double, sourceHeight:Double, targetWidth:Double, targetHeight:Double, directed:Boolean) {
	    theSourceSize.set(sourceWidth, sourceHeight)
	    theTargetSize.set(targetWidth, targetHeight)
		isDirected = directed
	}
	
	/** Compute the two end points sizes using the style (may not use the fit size). */
	private[this] def endPoints(sourceStyle:Style, targetStyle:Style, directed:Boolean, camera:Camera) {
		val srcx = camera.metrics.lengthToGu(sourceStyle.getSize, 0)
		val srcy = if(sourceStyle.getSize.size > 1) camera.metrics.lengthToGu(sourceStyle.getSize, 1) else srcx
		val trgx = camera.metrics.lengthToGu(targetStyle.getSize, 0)
		val trgy = if(targetStyle.getSize.size > 1) camera.metrics.lengthToGu(targetStyle.getSize, 1) else trgx
		
		theSourceSize.set(srcx, srcy)
		theTargetSize.set(trgx, trgy)
		isDirected = directed
	}
	
	/** Give the position of the origin and destination points. */
	private[this] def positionForLinesAndCurves( skel:ConnectorSkeleton, style:Style, from:GraphicNode, to:GraphicNode) {
	    positionForLinesAndCurves( skel, style, from, to, 0, null ) }
	
	/**
	 * Give the position of the origin and destination points, for multi edges.
	 * <p>
	 * This only sets the isCurve/isLoop and ctrl1/ctrl2 for multi-edges/edge-loops, if the shape of the
	 * edge given by the style is also a curve, the make() methods must set these fields (we cannot do it here
	 * since we do not know the curves). This is important since arrows and sprites can be attached to edges.
	 * </p>
	 */
	private[this] def positionForLinesAndCurves( skel:ConnectorSkeleton, style:Style, from:GraphicNode, to:GraphicNode, multi:Int, group:GraphicEdge#EdgeGroup ) {
	    
		//skel.points(0).set( xFrom, yFrom )
		//skel.points(3).set( xTo, yTo )
		if( group != null ) {
			if( from == to ) {
				positionEdgeLoop(skel, from.getX, from.getY, multi)
			} else {
				positionMultiEdge(skel, from.getX, from.getY, to.getX, to.getY, multi, group)
			}
		} else {
			if( from == to) {
				positionEdgeLoop(skel, from.getX, from.getY, 0)
			} else {
				// This does not mean the edge is not a curve, this means
				// that with what we know actually it is not a curve.
				// The style mays indicate a curve.
			    skel.setLine(from.getX, from.getY, 0, to.getX, to.getY, 0)
			    // XXX we will have to mutate the skel into a curve later.
			}		  
		}
	}
	
	/** Define the control points to make the edge a loop. */
	private[this] def positionEdgeLoop(skel:ConnectorSkeleton, x:Double, y:Double, multi:Int) {
		var m = 1f + multi * 0.2f
		val s = ( theTargetSize.x + theTargetSize.y ) / 2
		var d = s / 2 * m + 4 * s * m

		skel.setLoop(
				x, y, 0,
				x+d, y, 0,
				x, y+d, 0 )
	}
	
	/** Define the control points to make this edge a part of a multi-edge. */
	private[this] def positionMultiEdge(skel:ConnectorSkeleton, x1:Double, y1:Double, x2:Double, y2:Double, multi:Int, group:GraphicEdge#EdgeGroup) {
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
		
		skel.setCurve(
		        x1, y1, 0,
		        xx1, yy1, 0,
		        xx2, yy2, 0,
		        x2, y2, 0 )
	}
}

/** Some areas are attached to a connector (sprites). */
trait AreaOnConnector extends Area {
    /** The connector we are attached to. */
	protected var theConnector:Connector = null
	/** The edge represented by the connector.. */
	protected var theEdge:GraphicEdge = null

	/** XXX must call this method explicitly in the renderer !!! bad !!! XXX */
	def theConnectorYoureAttachedTo(connector:Connector) { theConnector = connector }
	
	protected def configureAreaOnConnectorForGroup(style:Style, camera:Camera) {
		sizeForEdgeArrow(style, camera)
	}
	
	protected def configureAreaOnConnectorForElement(edge:GraphicEdge, style:Style, camera:Camera) {
		connector(edge)
		theCenter.set(edge.to.getX, edge.to.getY)
	}
	
	private def connector(edge:GraphicEdge) { theEdge = edge }
 
	private def sizeForEdgeArrow(style:Style, camera:Camera) {
		val w = camera.metrics.lengthToGu(style.getArrowSize, 0)
		val h = if(style.getArrowSize.size > 1) camera.metrics.lengthToGu(style.getArrowSize, 1) else w
  
		theSize.set(w, h)
	}
}

/** Trait for all shapes that points at a direction. */
trait Orientable {
    /** The shape orientation. */
	var orientation:StyleConstants.SpriteOrientation = null
	
	/** The shape target. */
	var target = new Point3
	
	/** Configure the orientation mode for the group according to the style. */
	protected def configureOrientableForGroup(style:Style, camera:Camera) { orientation = style.getSpriteOrientation }
	
	/** Compute the orientation vector for the given element according to the orientation mode. */
	protected def configureOrientableForElement(camera:Camera, sprite:GraphicSprite) {
		sprite.getAttachment match {
			case gn:GraphicNode => {
				sprite.getStyle.getSpriteOrientation match {
					case SpriteOrientation.NONE       => { target.set(0, 0) }
					case SpriteOrientation.FROM       => { target.set(gn.getX, gn.getY) }
					case SpriteOrientation.TO         => { target.set(gn.getX, gn.getY) }
					case SpriteOrientation.PROJECTION => { target.set(gn.getX, gn.getY) }
				}
			}
			case ge:GraphicEdge => {
				sprite.getStyle.getSpriteOrientation match {
					case SpriteOrientation.NONE       => { target.set(0, 0) }
					case SpriteOrientation.FROM       => { target.set(ge.from.getX, ge.from.getY) }
					case SpriteOrientation.TO         => { target.set(ge.to.getX, ge.to.getY) }
					case SpriteOrientation.PROJECTION => {
						val ei = ge.getAttribute[ConnectorSkeleton](Skeleton.attributeName)
						
						if(ei != null)
						     ei.pointOnShape(sprite.getX, target)
						else setTargetOnLineEdge(camera, sprite, ge) 
					}
				}
			}
			case _ => { orientation = SpriteOrientation.NONE }
		}
	}
	
	private[this] def setTargetOnLineEdge(camera:Camera, sprite:GraphicSprite, ge:GraphicEdge) {
		val dir = new Vector2(ge.to.getX-ge.from.getX, ge.to.getY-ge.from.getY)
		dir.scalarMult(sprite.getX)
		target.set(ge.from.getX + dir.x, ge.from.getY + dir.y)
	}
}
/** Trait for shapes that can be decorated by an icon and/or a text. */
trait Decorable {
    /** The string of text of the contents. */
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
  			
  			//if( skel.iconAndText == null )
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