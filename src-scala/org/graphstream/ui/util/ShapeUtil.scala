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
package org.graphstream.ui.util

import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._
import org.graphstream.ui.geom._
import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._

import scala.math._

object ShapeUtil {
  	
  	/**
	 * Try to evaluate the "radius" of the edge target node shape along the edge. In other words
	 * this method computes the intersection point between the edge and the node shape contour.
	 * The returned length is the length of a line going from the centre of the shape toward
	 * the point of intersection between the target node shape contour and the edge.
	 * @param edge The edge (it contains its target node).
	 * @return The radius.
	 */
 	def evalTargetRadius( edge:GraphicEdge, camera:Camera ):Float = evalTargetRadius( edge.to.getStyle,
 		edge.to.getAttribute( ElementInfo.attributeName ).asInstanceOf[NodeInfo],
 			new Point2( edge.from.x, edge.from.y ), new Point2( edge.to.x, edge.to.y ), camera )
 
   	def evalTargetRadius( style:Style, info:NodeInfo, p0:Point2, p3:Point2, camera:Camera ):Float =
   		evalTargetRadius( style, info, p0, null, null, p3, camera )
  
  	def evalTargetRadius( edge:GraphicEdge, p0:Point2, p1:Point2, p2:Point2, p3:Point2, camera:Camera ):Float = 
  		evalTargetRadius( edge.to.getStyle,
  			edge.to.getAttribute(ElementInfo.attributeName).asInstanceOf[NodeInfo],
  				p0, p1, p2, p3, camera )
  		
  	def evalTargetRadius( style:Style, info:NodeInfo, p0:Point2, p1:Point2, p2:Point2, p3:Point2, camera:Camera ):Float = { 
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape._

  	  	var w = 0f 
  	  	var h = 0f
  	  	val s = if( style.getStrokeMode != StrokeMode.NONE ) camera.metrics.lengthToGu( style.getStrokeWidth ) else 0f

  	  	if( info != null ) {
  	  		w = info.theSize.x
  	  		h = info.theSize.y
  	  	} else {
  	  		w = camera.metrics.lengthToGu( style.getSize, 0 )
  	  		h = if( style.getSize.size > 1 ) camera.metrics.lengthToGu( style.getSize, 1 ) else w
  	  	}
  	  	
		style.getShape match {
			case CIRCLE       => evalEllipseRadius( p0, p1, p2, p3, w, h, s )
			case DIAMOND      => evalEllipseRadius( p0, p1, p2, p3, w, h, s )
			case CROSS        => evalEllipseRadius( p0, p1, p2, p3, w, h, s )
			case TRIANGLE     => evalEllipseRadius( p0, p1, p2, p3, w, h, s )
			case TEXT_CIRCLE  => evalEllipseRadius( p0, p1, p2, p3, w, h, s )
			case TEXT_DIAMOND => evalEllipseRadius( p0, p1, p2, p3, w, h, s )
			case BOX          => evalBoxRadius( p0, p1, p2, p3, w/2+s, h/2+s )
			case TEXT_BOX     => evalBoxRadius( p0, p1, p2, p3, w/2+s, h/2+s )
			case JCOMPONENT   => evalBoxRadius( p0, p1, p2, p3, w/2+s, h/2+s )
			case _            => 0
		}
	}
  
  	protected def evalEllipseRadius( p0:Point2, p1:Point2, p2:Point2, p3:Point2, w:Float, h:Float, s:Float ):Float = {
  	  	if( w == h )
  	  	     w / 2 + s	// Welcome simplification for circles ...
  	  	else evalEllipseRadius( p0, p1, p2, p3, w/2 + s, h/2 + s )
  	}

	/**
	 * Compute the length of a vector along the edge from the ellipse centre that match the
	 * ellipse radius.
	 * @param edge The edge representing the vector.
	 * @param w The ellipse first radius (width/2).
	 * @param h The ellipse second radius (height/2).
	 * @return The length of the radius along the edge vector.
	 */
	protected def evalEllipseRadius( p0:Point2, p1:Point2, p2:Point2, p3:Point2, w:Float, h:Float ):Float = {
		// Vector of the entering edge.

		var dx = 0f
		var dy = 0f

		if( p1 != null && p2 != null ) {
			dx = p3.x - p2.x //( p2.x + ((p1.x-p2.x)/4) )	// Use the line going from the last control-point to target
			dy = p3.y - p2.y //( p2.y + ((p1.y-p2.y)/4) )	// center as the entering edge.
		} else {
			dx = p3.x - p0.x
			dy = p3.y - p0.y
		}
		
		// The entering edge must be deformed by the ellipse ratio to find the correct angle.

		dy *= ( w / h )

		// Find the angle of the entering vector with (1,0).

		val d  = sqrt( dx*dx + dy*dy ).toFloat
		var a  = dx / d

		// Compute the coordinates at which the entering vector and the ellipse cross.

		a  = acos( a ).toFloat
		dx = ( cos( a ) * w ).toFloat
		dy = ( sin( a ) * h ).toFloat

		// The distance from the ellipse centre to the crossing point of the ellipse and
		// vector. Yo !

		sqrt( dx*dx + dy*dy ).toFloat
	}

 	/**
	 * Compute the length of a vector along the edge from the box centre that match the box
	 * "radius".
	 * @param edge The edge representing the vector.
	 * @param w The box first radius (width/2).
	 * @param h The box second radius (height/2).
	 * @return The length of the radius along the edge vector.
	 */
	def evalBoxRadius( p0:Point2, p1:Point2, p2:Point2, p3:Point2, w:Float, h:Float ):Float = {

		// Pythagora : Angle at which we compute the intersection with the height or the width.
	
		var da = w / ( sqrt( w*w + h*h ).toFloat )
		
		da = if( da < 0 ) -da else da
		
		// Angle of the incident vector.
		var dx = 0f
		var dy = 0f

		if( p1 != null && p2 != null ) {
			dx = p3.x - p2.x // ( p2.x + ((p1.x-p2.x)/4) )	// Use the line going from the last control-point to target
			dy = p3.y - p2.y //( p2.y + ((p1.y-p2.y)/4) )	// center as the entering edge.
		} else {
			dx = p3.x - p0.x
			dy = p3.y - p0.y
		}
  
		val d = sqrt( dx*dx + dy*dy ).toFloat
		var a = dx/d
		
		a = if( a < 0 ) -a else a
	
		// Choose the side of the rectangle the incident edge vector crosses.
		
		if( da < a ) {
			w / a
		} else {
			a = dy/d
			a = if( a < 0 ) -a else a
            h / a
		}
	}
	
	/** Compute if point `p`  is inside of the shape of `elt` whose overall size is `w` x `h`. */
	def isPointIn( elt:GraphicElement, p:Point2, w:Float, h:Float ):Boolean = {
		import ShapeKind._
		elt.getStyle.getShape.kind match {
			case RECTANGULAR => isPointInBox( p, elt.getX, elt.getY, w, h )
			case ELLIPSOID   => isPointInEllipse( p, elt.getX, elt.getY, w, h )
			case _ => false
		}
	}
	
	/** Compute if point `p`  is inside of a rectangular shape of overall size `w` x `h`. */
	def isPointInBox( p:Point2, x:Float, y:Float, w:Float, h:Float ):Boolean = {
		val w2 = w/2
		val h2 = h/2
		( p.x > (x-w2) && p.x < (x+w2) && p.y > (y-h2) && p.y < (y+h2) )
	}
	
	/** Compute if point `p`  is inside of a ellipsoid shape of overall size `w` x `h`. */
	def isPointInEllipse( p:Point2, x:Float, y:Float, w:Float, h:Float ):Boolean = {
		val xx = p.x - x
		val yy = p.y - y
		val w2 = w/2
		val h2 = h/2
		
		( ((xx*xx)/(w2*w2)) + ((yy*yy)/(h2*h2)) < 1 )
	}
}