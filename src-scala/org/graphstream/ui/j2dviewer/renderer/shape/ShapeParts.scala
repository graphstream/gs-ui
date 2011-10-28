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
import org.graphstream.ui.j2dviewer.renderer.shape.swing.ShapeDecor

/** Trait for elements painted inside an area.
  * This trait manages the size of the area (rectangular), its position, and the automatic fit to
  * the contents, if needed. */
trait Area {
	protected val theCenter = new Point2
	protected val theSize = new Point2
	protected var fit = false

	/** Select the general size for the group. */
	protected def configureAreaForGroup(style:Style, camera:Camera) = size(style, camera)
	
	protected def configureAreaForElement(backend:Backend, camera:Camera, skel:AreaSkeleton, element:GraphicElement, decor:ShapeDecor) {
		var pos = camera.getNodeOrSpritePositionGU(element, null)
		
		if(fit) {
			val decorSize = decor.size(backend, camera, skel.iconAndText)
		
			configureAreaForElement(camera, skel.asInstanceOf[AreaSkeleton], element, pos.x, pos.y, decorSize._1, decorSize._2 )
		} else {
			configureAreaForElement(camera, skel.asInstanceOf[AreaSkeleton], element, pos.x, pos.y )
		}
	}
	
	protected[this] def configureAreaForElement(camera:Camera, skel:AreaSkeleton, element:GraphicElement, x:Double, y:Double ) {
		dynSize(element.getStyle, camera, element)
		positionAndFit(camera, skel, element, x, y, 0, 0)
	}
	
	protected[this] def configureAreaForElement(camera:Camera, skel:AreaSkeleton, element:GraphicElement, x:Double, y:Double, contentOverallWidth:Double, contentOverallHeight:Double ) {
		dynSize(element.getStyle, camera, element)
		positionAndFit(camera, skel, element, x, y, contentOverallWidth, contentOverallHeight)
	}

	/** Set the size for the group of this (rectangular) area without considering the style. */
	private def size(width:Double, height:Double) { theSize.set( width, height ) }
	
	/** Set the general size of the area according to the style.
	  * Also look if the style SizeMode says if the element must fit to its contents.
	  * If so, the configureAreaForElement() method will recompute the size for each
	  * element according to the contents (shape decoration). */
	private def size(style:Style, camera:Camera) { 
		val w = camera.metrics.lengthToGu( style.getSize, 0 )
		val h = if( style.getSize.size > 1 ) camera.metrics.lengthToGu( style.getSize, 1 ) else w
  
		theSize.set(w, h)
		
		fit = (style.getSizeMode == StyleConstants.SizeMode.FIT)
	}
	
	/** Try to compute the size of this area according to the given element. */
	private def dynSize(style:Style, camera:Camera, element:GraphicElement) {
		var w = camera.metrics.lengthToGu(style.getSize, 0)
		var h = if(style.getSize.size > 1) camera.metrics.lengthToGu(style.getSize, 1) else w

		if(element.hasAttribute("ui.size")) {
			w = camera.metrics.lengthToGu(StyleConstants.convertValue(element.getAttribute("ui.size")))
			h = w;
		}
  
		theSize.set(w, h)
	}
	
	/** Assign a position to the shape according to the element, set the size of the element  */
	protected def positionAndFit(camera:Camera, skel:AreaSkeleton, element:GraphicElement, x:Double, y:Double, contentOverallWidth:Double, contentOverallHeight:Double) {
		if(skel != null) {
			if(contentOverallWidth > 0 && contentOverallHeight > 0)
				theSize.set(contentOverallWidth, contentOverallHeight)
			
			skel.theSize.copy(theSize)
		}

		theCenter.set(x, y)
	}
}

/**
 * Trait for elements painted between two points.
 * 
 * The purpose of this class is to store the lines coordinates of an edge. This connector can
 * be made of only two points, 4 points when this is a bezier curve or more if this is a polyline.
 * The coordinates of these points are stored in a ConnectorSkeleton attribute directly on the edge element
 * since several parts of the rendering need to access it (for example, sprites retrieve it
 * to follow the correct path when attached to this edge).
 */
trait Connector {
// Attribute
	
	var skel:ConnectorSkeleton = null
	
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
	def fromPos:Point3 = skel.from
	
	/** First control point. Works only for curves. */
	def byPos1:Point3 = if(skel.isCurve) skel(1) else null
	
	/** Second control point. Works only for curves. */
	def byPos2:Point3 = if(skel.isCurve) skel(2) else null
	
	/** Destination of the connector. */
	def toPos:Point3 = skel.to
	
	def configureConnectorForGroup(style:Style, camera:Camera) {
		size( style, camera )
	}
	
	def configureConnectorForElement(camera:Camera, element:GraphicEdge, skel:ConnectorSkeleton) {
	    this.skel = skel
	    
		dynSize( element.getStyle, camera, element )
		endPoints( element.from, element.to, element.isDirected, camera )
		
		if(element.getGroup != null) {
	        skel.setMulti(element.getGroup.getCount)
	    }
		
		if(element.hasAttribute("ui.points")) {
		    skel.setPoly(element.getAttribute("ui.points").asInstanceOf[AnyRef])
		} else {
			positionForLinesAndCurves( skel, element.from.getStyle, element.from.getX, element.from.getY,
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
		val fromInfo = from.getAttribute( Skeleton.attributeName ).asInstanceOf[AreaSkeleton]
		val toInfo   = to.getAttribute( Skeleton.attributeName ).asInstanceOf[AreaSkeleton]
		
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
	private def positionForLinesAndCurves( skel:ConnectorSkeleton, style:Style, xFrom:Double, yFrom:Double, xTo:Double, yTo:Double ) {
	    positionForLinesAndCurves( skel, style, xFrom, yFrom, xTo, yTo, 0, null ) }
	
	/**
	 * Give the position of the origin and destination points, for multi edges.
	 * <p>
	 * This only sets the isCurve/isLoop and ctrl1/ctrl2 for multi-edges/edge-loops, if the shape of the
	 * edge given by the style is also a curve, the make() methods must set these fields (we cannot do it here
	 * since we do not know the curves). This is important since arrows and sprites can be attached to edges.
	 * </p>
	 */
	private def positionForLinesAndCurves( skel:ConnectorSkeleton, style:Style, xFrom:Double, yFrom:Double, xTo:Double, yTo:Double, multi:Int, group:GraphicEdge#EdgeGroup ) {
	    
		//skel.points(0).set( xFrom, yFrom )
		//skel.points(3).set( xTo, yTo )
		if( group != null ) {
			if( xFrom == xTo && yFrom == yTo ) {
				positionEdgeLoop(skel, xFrom, yFrom, multi)
			} else {
				positionMultiEdge(skel, xFrom, yFrom, xTo, yTo, multi, group)
			}
		} else {
			if( xFrom == xTo && yFrom == yTo ) {
				positionEdgeLoop(skel, xFrom, yFrom, 0)
			} else {
				// This does not mean the edge is not a curve, this means
				// that with what we know actually it is not a curve.
				// The style mays indicate a curve.
			    skel.setLine(xFrom, yFrom, 0, xTo, yTo, 0)
			    
			    // XXX we will have to mutate the skel into a curve later.
			}		  
		}
	}
	
	/** Define the control points to make the edge a loop. */
	private def positionEdgeLoop(skel:ConnectorSkeleton, x:Double, y:Double, multi:Int) {
		var m = 1f + multi * 0.2f
		val s = ( theTargetSizeX + theTargetSizeY ) / 2
		var d = s / 2 * m + 4 * s * m

		skel.setLoop(
				x, y, 0,
				x+d, y, 0,
				x, y+d, 0 )
	}
	
	/** Define the control points to make this edge a part of a multi-edge. */
	private def positionMultiEdge(skel:ConnectorSkeleton, x1:Double, y1:Double, x2:Double, y2:Double, multi:Int, group:GraphicEdge#EdgeGroup) {
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
						val ei = ge.getAttribute[ConnectorSkeleton]( Skeleton.attributeName )
						
						if(ei != null)
						     ei.pointOnShape(sprite.getX, target)
						else setTargetOnLineEdge(camera, sprite, ge) 
					}
				}
			}
			case _ => { orientation = SpriteOrientation.NONE }
		}
	}
	
	private[this] def setTargetOnLineEdge( camera:Camera, sprite:GraphicSprite, ge:GraphicEdge ) {
		val dir = new Vector2( ge.to.getX-ge.from.getX, ge.to.getY-ge.from.getY )
		dir.scalarMult( sprite.getX )
		target.set( ge.from.getX + dir.x, ge.from.getY + dir.y )
	}
}