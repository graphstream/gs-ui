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
package org.graphstream.ui.util

import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._
import org.graphstream.ui.geom._
import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._

import scala.math._

object ShapeUtil {
  	/** Try to evaluate the "radius" of the edge target node shape along the edge. In other words
	  * this method computes the intersection point between the edge and the node shape contour.
	  * The returned length is the length of a line going from the center of the shape toward
	  * the point of intersection between the target node shape contour and the edge.
	  * @param edge The edge (it contains its target node).
	  * @param camera the camera.
	  * @return The radius. */
 	def evalTargetRadius2D(edge:GraphicEdge, camera:Camera):Double = {
 	    val eskel = edge.getAttribute(Skeleton.attributeName).asInstanceOf[ConnectorSkeleton]
 	    evalTargetRadius2D(
 	    		edge.to.getStyle,
 	    		edge.to.getAttribute(Skeleton.attributeName).asInstanceOf[AreaSkeleton],
 	    		new Point3(eskel.from.x, eskel.from.y, eskel.from.z),
 	    		new Point3(eskel.to.x, eskel.to.y, eskel.to.z),
 	    		camera)
 	}
 	
 	/** Try to evaluate the "radius" of the given node considering an edge between points `from` and `to`.
 	  * In other words, this method computes the intersection point between the edge and the node
 	  * shape contour. The returned length is the length of a line going from the center of the shape
 	  * toward the point of intersection between the target node shape contour and the edge.
 	  * @param from The origin point of the edge.
 	  * @param to The target point of the edge.
 	  * @param node The target node shape.
 	  * @param the camera.
 	  * @return The radius. */
 	def evalTargetRadius2D(from:Point3, to:Point3, node:GraphicNode, camera:Camera):Double = {
 	    evalTargetRadius2D(node.getStyle, node.getAttribute(Skeleton.attributeName).asInstanceOf[AreaSkeleton],
 	            from, null, null, to, camera)
 	}
 
 	/** Try to evaluate the "radius" of the given area skeleton considering an edge between points `p0` and
 	  * point `p3` (the edge is considered a straight line). In other words, this method computes the intersection
 	  * point between the edge and the area geometry. The returned length of the line going from the center of
 	  * the skeleton geometry toward the point of intersection between the skeleton geometry and the edge.
 	  * @param style The style of the area skeleton.
 	  * @param skeleton The skeleton.
 	  * @param p0 The origin point of the edge.
 	  * @param p3 the target point of the edge.
 	  * @param camera the camera.
 	  * @return the radius. */
   	def evalTargetRadius2D(style:Style, skeleton:AreaSkeleton, p0:Point3, p3:Point3, camera:Camera):Double = evalTargetRadius2D(style, skeleton, p0, null, null, p3, camera)
  
 	/** Try to evaluate the "radius" of the given area skeleton considering a cubic curve edge between points `p0` and
 	  * point `p3` and curving to control points `p1`  and `p2`. In other words, this method computes the intersection
 	  * point between the edge and the area geometry. The returned length of the line going from the center of
 	  * the skeleton geometry toward the point of intersection between the skeleton geometry and the edge.
 	  * @param edge The edge.
 	  * @param p0 The origin point of the edge.
 	  * @param p3 the target point of the edge.
 	  * @param camera the camera.
 	  * @return the radius. */
  	def evalTargetRadius2D(edge:GraphicEdge, p0:Point3, p1:Point3, p2:Point3, p3:Point3, camera:Camera):Double = 
  		evalTargetRadius2D(edge.to.getStyle,
  			edge.to.getAttribute(Skeleton.attributeName).asInstanceOf[AreaSkeleton],
  			p0, p1, p2, p3, camera)
  		
 	/** Try to evaluate the "radius" of the given area skeleton considering a cubic curve edge between points `p0` and
 	  * point `p3` and curving to control points `p1`  and `p2`. In other words, this method computes the intersection
 	  * point between the edge and the area geometry. The returned length of the line going from the center of
 	  * the skeleton geometry toward the point of intersection between the skeleton geometry and the edge.
 	  * @param style The style of the area skeleton.
 	  * @param skeleton The skeleton.
 	  * @param p0 The origin point of the edge.
 	  * @param p3 the target point of the edge.
 	  * @param camera the camera.
 	  * @return the radius. */
  	def evalTargetRadius2D(style:Style, skeleton:AreaSkeleton, p0:Point3, p1:Point3, p2:Point3, p3:Point3, camera:Camera):Double = { 
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape._

  	  	var w = 0.0
  	  	var h = 0.0
  	  	val s = if(style.getStrokeMode != StrokeMode.NONE) camera.metrics.lengthToGu(style.getStrokeWidth) else 0f

  	  	if(skeleton != null) {
  	  		w = skeleton.theSize.x
  	  		h = skeleton.theSize.y
  	  	} else {
  	  		w = camera.metrics.lengthToGu(style.getSize, 0)
  	  		h = if(style.getSize.size > 1) camera.metrics.lengthToGu(style.getSize, 1) else w
  	  	}
  	  	
		style.getShape match {
			case CIRCLE       => evalEllipseRadius2D(p0, p1, p2, p3, w, h, s)
			case DIAMOND      => evalEllipseRadius2D(p0, p1, p2, p3, w, h, s)
			case CROSS        => evalEllipseRadius2D(p0, p1, p2, p3, w, h, s)
			case TRIANGLE     => evalEllipseRadius2D(p0, p1, p2, p3, w, h, s)
			case TEXT_CIRCLE  => evalEllipseRadius2D(p0, p1, p2, p3, w, h, s)
			case TEXT_DIAMOND => evalEllipseRadius2D(p0, p1, p2, p3, w, h, s)
			case BOX          => evalBoxRadius2D(p0, p1, p2, p3, w/2+s, h/2+s)
			case ROUNDED_BOX  => evalBoxRadius2D(p0, p1, p2, p3, w/2+s, h/2+s)
			case TEXT_BOX     => evalBoxRadius2D(p0, p1, p2, p3, w/2+s, h/2+s)
			case JCOMPONENT   => evalBoxRadius2D(p0, p1, p2, p3, w/2+s, h/2+s)
			case _            => evalBoxRadius2D(p0, p1, p2, p3, w/2+s, h/2+s)
		}
	}
  
   	/** Compute the length of a (eventually cubic curve) vector along the edge from the ellipse center toward the intersection
   	  * point with the ellipse that match the ellipse radius. If `p1` and `p2` are null, the edge is considered
   	  * a straight line, else a cubic curve.
   	  * @param p0 the origin point of the edge
   	  * @param p1 the first cubic-curve control point or null for straight edge.
   	  * @param p2 the second cubic-curve control point or null for straight edge.
   	  * @param p3 the target point of the edge.
   	  * @param w the width of the ellipse.
   	  * @param h the height of the ellipse.
   	  * @param s the width of the stroke of the ellipse shape.
   	  */
  	protected def evalEllipseRadius2D(p0:Point3, p1:Point3, p2:Point3, p3:Point3, w:Double, h:Double, s:Double):Double = {
  	  	if(w == h)
  	  	     w / 2 + s	// Welcome simplification for circles ...
  	  	else evalEllipseRadius2D(p0, p1, p2, p3, w/2 + s, h/2 + s)
  	}

	/** Compute the length of a vector along the edge from the ellipse center that match the
	  * ellipse radius.
	  * @param edge The edge representing the vector.
	  * @param w The ellipse first radius (width/2).
	  * @param h The ellipse second radius (height/2).
	  * @return The length of the radius along the edge vector. */
	protected def evalEllipseRadius2D(p0:Point3, p1:Point3, p2:Point3, p3:Point3, w:Double, h:Double):Double = {
  	  	if(w == h) {
  	  	     w / 2	// Welcome simplification for circles ...
  	  	} else {
			// Vector of the entering edge.
	
			var dx = 0.0
			var dy = 0.0
	
			if(p1 != null && p2 != null) {
				dx = p3.x - p2.x //( p2.x + ((p1.x-p2.x)/4) )	// Use the line going from the last control-point to target
				dy = p3.y - p2.y //( p2.y + ((p1.y-p2.y)/4) )	// center as the entering edge.
			} else {
				dx = p3.x - p0.x
				dy = p3.y - p0.y
			}
			
			// The entering edge must be deformed by the ellipse ratio to find the correct angle.
	
			dy *= w / h
	
			// Find the angle of the entering vector with (1,0).
	
			val d  = sqrt(dx*dx + dy*dy)
			var a  = dx / d
	
			// Compute the coordinates at which the entering vector and the ellipse cross.
	
			a  = acos(a)
			dx = cos(a) * w
			dy = sin(a) * h
	
			// The distance from the ellipse center to the crossing point of the ellipse and
			// vector. Yo !
	
			sqrt(dx*dx + dy*dy)
  	  	}
	}

 	/** Compute the length of a vector along the edge from the box center that match the box
	  * "radius".
	  * @param edge The edge representing the vector.
	  * @param w The box first radius (width/2).
	  * @param h The box second radius (height/2).
	  * @return The length of the radius along the edge vector. */
	def evalBoxRadius2D(p0:Point3, p1:Point3, p2:Point3, p3:Point3, w:Double, h:Double):Double = {
		// Pythagora : Angle at which we compute the intersection with the height or the width.
	
		var da = w / sqrt(w*w + h*h).toDouble
		
		da = if(da < 0) -da else da
		
		// Angle of the incident vector.
		var dx = 0.0
		var dy = 0.0

		if(p1 != null && p2 != null) {
			dx = p3.x - p2.x // ( p2.x + ((p1.x-p2.x)/4) )	// Use the line going from the last control-point to target
			dy = p3.y - p2.y //( p2.y + ((p1.y-p2.y)/4) )	// center as the entering edge.
		} else {
			dx = p3.x - p0.x
			dy = p3.y - p0.y
		}
  
		val d = sqrt(dx*dx + dy*dy).toDouble
		var a = dx/d
		
		a = if(a < 0) -a else a
	
		// Choose the side of the rectangle the incident edge vector crosses.
		
		if(da < a) {
			w / a
		} else {
			a = dy/d
			a = if(a < 0) -a else a
            h / a
		}
	}
	
	/** Compute if point `p` is inside of the shape of `elt` whose overall size is `w` x `h`. */
	def isPointIn(elt:GraphicElement, p:Point3, w:Double, h:Double):Boolean = {
		import ShapeKind._
		elt.getStyle.getShape.kind match {
			case RECTANGULAR => isPointIn2DBox( p, elt.getX, elt.getY, w, h )
			case ELLIPSOID   => isPointIn2DEllipse( p, elt.getX, elt.getY, w, h )
			case _ => false
		}
	}
	
	/** Compute if point `p` is inside of a rectangular shape of overall size `w` x `h`. */
	def isPointIn2DBox(p:Point3, x:Double, y:Double, w:Double, h:Double):Boolean = {
		val w2 = w/2
		val h2 = h/2
		( p.x > (x-w2) && p.x < (x+w2) && p.y > (y-h2) && p.y < (y+h2) )
	}
	
	/** Compute if point `p` is inside of a ellipsoid shape of overall size `w` x `h`. */
	def isPointIn2DEllipse(p:Point3, x:Double, y:Double, w:Double, h:Double):Boolean = {
		val xx = p.x - x
		val yy = p.y - y
		val w2 = w/2
		val h2 = h/2
		
		(((xx*xx)/(w2*w2)) + ((yy*yy)/(h2*h2)) < 1)
	}
}