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
package org.graphstream.ui.j2dviewer

import org.graphstream.ui.sgeom.Point3

//import java.awt.Graphics2D
//import java.awt.geom.{AffineTransform, Point2D}
import java.util.ArrayList

import scala.collection.mutable.HashSet
import scala.collection.JavaConversions._
import scala.math._

import org.graphstream.graph.Node
import org.graphstream.ui.graphicGraph.stylesheet.Selector.Type._

import org.graphstream.ui.graphicGraph.{GraphicEdge, GraphicElement, GraphicGraph, GraphicNode, GraphicSprite}
import org.graphstream.ui.graphicGraph.stylesheet.{Style, Values}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

import org.graphstream.ui.util.{GraphMetrics, CubicCurve}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.sgeom.Vector2
import org.graphstream.ui.j2dviewer.renderer.{ElementInfo, EdgeInfo, NodeInfo}

import scala.math._
import org.graphstream.ScalaGS._


/**
 * Define how the graph is viewed.
 * 
 * <p>
 * The camera is in charge of projecting the graph elements in graph units (GU) into
 * user spaces (often in pixels). It defines the transformation (an affine matrix) to pass
 * from the first to the second. It also contains the graph metrics, a set of values that
 * give the overall dimensions of the graph in graph units, as well as the view port, the
 * area on the screen (or any rendering surface) that will receive the results in pixels
 * (or rendering units). The two mains methods for this operation are
 * {@link #pushView(Graphics2D,GraphicGraph)} and {@link #popView()}.
 * </p>
 * 
 * <p>
 * The user of the camera must set both the view port and the graph bounds in order for the
 * camera to correctly project the graph view. The camera also defines a centre at which it
 * always points. It can zoom on the graph, pan in any direction and rotate along two axes.
 * </p>
 * 
 * <p>
 * There are two modes : an "auto-fit" mode where the camera always show the whole graph even
 * if it changes in size, and a "user" mode where the camera centre (looked-at point), zoom and
 * panning are specified.
 * </p>
 * 
 * <p>
 * Knowing the transformation also allows to provide services like "what element is
 * visible ?" (in the camera view) or "on what element is the mouse cursor actually ?".
 * </p>
 * 
 * <p>
 * The camera is also able to compute sprite positions according to their attachment, as well as
 * maintaining a list of all elements out of the view, so that it is not needed to render them.
 * </p>
 */
class Camera {
// Attribute
	
  	/** Information on the graph overall dimension and position. */
  	val metrics = new GraphMetrics
	
  	/** Automatic centring of the view. */
  	protected var autoFit = true
	
  	/** The camera centre of view. */
  	protected val center = new Point3
	
  	/** The camera zoom. */
  	protected var zoom:Double = 1
	
  	/** The rendering back-end. */
  	protected var bck:Backend = null
  	
  	/** The graph-space -> pixel-space transformation. */
//  	protected var Tx = new AffineTransform

  	/** The inverse transform of Tx. */
//  	protected var xT:AffineTransform = null
	
  	/** The previous affine transform. */
//  	protected var oldTx:AffineTransform = null
	
  	/** The rotation angle. */
  	protected var rotation:Double = 0
	
  	/** Padding around the graph. */
  	protected var padding = new Values(Units.GU, 0, 0, 0);
	
  	/**
  	 * Which node is visible. This allows to mark invisible nodes to fasten visibility tests for
  	 * nodes, attached sprites and edges. The visibility test is heavy, and we often need to test
  	 * for nodes visibility. This allows to do it only once per rendering step. Hence the storage
  	 * of the invisible nodes here.
  	 */
  	protected val nodeInvisible = new HashSet[String]
  	
  	/**
  	 * The graph view port, if any. The graph view port is a view inside the graph space. It allows
  	 * to compute the view according to a specified area of the graph space instead of the graph
  	 * dimensions.
  	 */
  	protected var gviewport:Array[Double] = null
  		
// Access
	
  	/**
  	 * The view centre (a point in graph units).
  	 * @return The view centre.
  	 */
  	def viewCenter:Point3 = center
	
  	/**
  	 * The visible portion of the graph.
  	 * @return A real for which value 1 means the graph is fully visible and uses the whole
  	 * view port.
  	 */
  	def viewPercent:Double = zoom
	
  	/**
  	 * The rotation angle in degrees.
  	 * @return The rotation angle in degrees.
  	 */
  	def viewRotation:Double = rotation
	
  	override def toString():String = {
  		var builder = new StringBuilder( "Camera :%n".format() )
		
  		builder.append( "    autoFit  = %b%n".format( autoFit ) )
  		builder.append( "    center   = %s%n".format( center ) )
  		builder.append( "    rotation = %f%n".format( rotation ) )
  		builder.append( "    zoom     = %f%n".format( zoom ) )
  		builder.append( "    padding  = %s%n".format( padding ) )
  		builder.append( "    metrics  = %s%n".format( metrics ) )
		
  		builder.toString
  	}
	
  	/**
  	 * True if the element would be visible on screen. The method used is to transform the centre
  	 * of the element (which is always in graph units) using the camera actual transformation to
  	 * put it in pixel units. Then to look in the style sheet the size of the element and to test
  	 * if its enclosing rectangle intersects the view port. For edges, its two nodes are used. 
  	 * @param element The element to test.
  	 * @return True if the element is visible and therefore must be rendered.
  	 */
  	def isVisible(element:GraphicElement):Boolean = {
  		if( styleVisible( element ) ) element.getSelectorType match {
  			case NODE   => ! nodeInvisible.contains(element.getId)
  			case EDGE   => isEdgeVisible(element.asInstanceOf[GraphicEdge])
  			case SPRITE => isSpriteVisible(element.asInstanceOf[GraphicSprite])
  			case _      => false
  		} else false
    }

  	/**
  	 * Return the given point in pixels converted in graph units (GU) using the inverse
  	 * transformation of the current projection matrix. The inverse matrix is computed only
  	 * once each time a new projection matrix is created.
  	 * @param x The source point abscissa in pixels.
  	 * @param y The source point ordinate in pixels.
  	 * @return The resulting points in graph units.
  	 */
  	def inverseTransform(x:Double, y:Double):Point3 = bck.inverseTransform(x, y, 0)
	
  	/**
  	 * Transform a point in graph units into pixels.
  	 * @return The transformed point.
  	 */
  	def transform(x:Double, y:Double):Point3 = bck.transform(x, y, 0)
	
  	/**
  	 * Search for the first node or sprite (in that order) that contains the point at coordinates
  	 * (x, y).
  	 * @param graph The graph to search for.
  	 * @param x The point abscissa.
  	 * @param y The point ordinate.
  	 * @return The first node or sprite at the given coordinates or null if nothing found. 
  	 */
  	def findNodeOrSpriteAt(graph:GraphicGraph, x:Double, y:Double):GraphicElement = {
  		var ge:GraphicElement = null
  		
  		graph.getEachNode.foreach { n =>	
  			val node = n.asInstanceOf[GraphicNode]
			
  			if( nodeContains( node, x, y ) )
  				ge = node
  		}
	
  		graph.spriteSet.foreach { sprite =>
  			if( spriteContains( sprite, x, y ) )
  				ge = sprite
  		}
  		
 		ge
  	 }
	
  	/**
  	 * Search for all the nodes and sprites contained inside the rectangle (x1,y1)-(x2,y2).
  	 * @param graph The graph to search for.
  	 * @param x1 The rectangle lowest point abscissa.
  	 * @param y1 The rectangle lowest point ordinate.
  	 * @param x2 The rectangle highest point abscissa.
  	 * @param y2 The rectangle highest point ordinate.
  	 * @return The set of sprites and nodes in the given rectangle.
  	 */
  	def allNodesOrSpritesIn(graph:GraphicGraph, x1:Double, y1:Double, x2:Double, y2:Double):ArrayList[GraphicElement] = {
  		val elts = new ArrayList[GraphicElement]
	
        graph.getEachNode.foreach { node:Node =>	
  			if(isNodeIn(node.asInstanceOf[GraphicNode], x1, y1, x2, y2))
  				elts.add( node.asInstanceOf[GraphicNode])
  		}
		
  		graph.spriteSet.foreach { sprite:GraphicSprite =>
  			if(isSpriteIn(sprite, x1, y1, x2, y2))
  				elts.add(sprite)
  		}
		
  		elts
  	}

  	/**
  	 * Compute the real position of a sprite according to its eventual attachment in graph units.
  	 * @param sprite The sprite.
  	 * @param pos Receiver for the sprite 2D position, can be null. 
  	 * @param units The units in which the position must be computed (the sprite already contains units).
  	 * @return The same instance as the one given by parameter pos or a new one if pos was null,
  	 * containing the computed position in the given units.
  	 */
  	def getSpritePosition(sprite:GraphicSprite, pos:Point3, units:Units):Point3 = {
  		if(      sprite.isAttachedToNode() ) getSpritePositionNode(sprite, pos, units)
  		else if( sprite.isAttachedToEdge() ) getSpritePositionEdge(sprite, pos, units)
  		else                                 getSpritePositionFree(sprite, pos, units)
  	 }
  	
  	def graphViewport = gviewport
  	
// Command
  	
  	def setBackend(backend:Backend) { bck = backend }

  	def setGraphViewport(minx:Double, miny:Double, maxx:Double, maxy:Double) {
  	    gviewport = Array( minx, miny, maxx, maxy )
  	}
  	
  	def removeGraphViewport() { gviewport = null }

  	/**
  	 * Set the camera view in the given graphics and backup the previous transform of the graphics.
  	 * Call {@link #popView(Graphics2D)} to restore the saved transform. You can only push one time
  	 * the view.
  	 * @param g2 The Swing graphics to change.
  	 * @param graph The graphic graph (used to check element visibility).
  	 */
  	def pushView(graph:GraphicGraph) {
  		bck.pushTransform
  		setPadding(graph)
			
  		if(autoFit)
  		     autoFitView
  		else userView
			
  		checkVisibility(graph)
  	}
	
  	/**
  	 * Restore the transform that was used before {@link #pushView(Graphics2D)} is used.
     * @param g2 The Swing graphics to restore.
     */
    def popView() { bck.popTransform }
	
  	/**
  	 * Compute a transformation matrix that pass from graph units (user space) to pixel units
  	 * (device space) so that the whole graph is visible.
  	 * @param g2 The Swing graphics.
  	 * @param Tx The transformation to modify.
  	 * @return The transformation modified.
  	 */
  	protected def autoFitView() {
  		var sx = 0.0; var sy = 0.0
  		var tx = 0.0; var ty = 0.0
  		val padXgu = paddingXgu * 2
  		val padYgu = paddingYgu * 2
  		val padXpx = paddingXpx * 2
  		val padYpx = paddingYpx * 2
		
  		sx = (metrics.viewport(0) - padXpx) / (metrics.size(0) + padXgu)	// Ratio along X
  		sy = (metrics.viewport(1) - padYpx) / (metrics.size(1) + padYgu)	// Ratio along Y
  		tx = metrics.lo.x + (metrics.size(0) / 2)								// Centre of graph in X
  		ty = metrics.lo.y + (metrics.size(1) / 2)								// Centre of graph in Y
		
  		if(sx > sy)	// The least ratio.
  		     sx = sy
  		else sy = sx
		
  		bck.beginTransform
  		bck.setIdentity
  		bck.translate(metrics.viewport(0)/2,
  		              metrics.viewport(1)/2, 0)			// 4. Place the whole result at the centre of the view port.
  		if(rotation != 0)
  		    bck.rotate(rotation/(180.0/Pi), 0, 0, 1)	// 3. Eventually apply a Z axis rotation.
  		bck.scale(sx, -sy, 0)							// 2. Scale the graph to pixels. Scale -y since we reverse the view (top-left to bottom-keft).
  		bck.translate(-tx, -ty, 0)						// 1. Move the graph so that its real centre is at (0,0).
  		bck.endTransform
//  		Tx.setToIdentity
//  		Tx.translate( metrics.viewport.data(0) / 2,
//  		              metrics.viewport.data(1) / 2 )	// 4. Place the whole result at the centre of the view port.		
//  		if( rotation != 0 )
//  			Tx.rotate( rotation/(180/Pi) )				// 3. Eventually apply a rotation.
//  		Tx.scale( sx, -sy )								// 2. Scale the graph to pixels. Scale -y since we reverse the view (top-left to bottom-left).
//  		Tx.translate( -tx, -ty )						// 1. Move the graph so that its real centre is at (0,0).
//		
//  		xT = new AffineTransform( Tx )
//  		try { xT.invert } catch { case _ => System.err.printf( "cannot inverse gu2px matrix...%n" ) }
		
  		zoom = 1

  		center.set(tx, ty, 0)
  		metrics.ratioPx2Gu = sx
  		metrics.loVisible.copy(metrics.lo)
  		metrics.hiVisible.copy(metrics.hi)
		
//  		Tx
  	}

  	/**
  	 * Compute a transformation that pass from graph units (user space) to a pixel units (device
  	 * space) so that the view (zoom and centre) requested by the user is produced.
  	 * @param g2 The Swing graphics.
  	 * @param Tx The transformation to modify.
  	 * @return The transformation modified.
  	 */
  	protected def userView() {
  		var sx = 0.0; var sy = 0.0
  		var tx = 0.0; var ty = 0.0
  		val padXgu = paddingXgu * 2
  		val padYgu = paddingYgu * 2
  		val padXpx = paddingXpx * 2
  		val padYpx = paddingYpx * 2
  		val gw     = if(gviewport ne null) gviewport(2)-gviewport(0) else metrics.size(0)
  		val gh     = if(gviewport ne null) gviewport(3)-gviewport(1) else metrics.size(1)
//		val diag   = Math.max( metrics.size.data(0)+padXgu, metrics.size.data(1)+padYgu ) * zoom 
//		
//		sx = (metrics.viewport(0) - padXpx) / diag 
//		sy = (metrics.viewport(1) - padYpx) / diag
  		sx = (metrics.viewport(0) - padXpx) / ((gw + padXgu) * zoom) 
		sy = (metrics.viewport(1) - padYpx) / ((gh + padYgu) * zoom)
  		
		tx = center.x
		ty = center.y
		
		if(sx > sy)	// The least ratio.
		     sx = sy;
		else sy = sx;
	
  		bck.beginTransform
  		bck.setIdentity
  		bck.translate(metrics.viewport(0)/2,
  		              metrics.viewport(1)/2, 0)			// 4. Place the whole result at the centre of the view port.
  		if(rotation != 0)
  		    bck.rotate(rotation/(180.0/Pi), 0, 0, 1)	// 3. Eventually apply a rotation.
  		bck.scale(sx, -sy, 0)							// 2. Scale the graph to pixels. Scale -y since we reverse the view (top-left to bottom-left).
  		bck.translate(-tx, -ty, 0)						// 1. Move the graph so that the give centre is at (0,0).
  		bck.endTransform
//  		Tx.setToIdentity
//  		Tx.translate( metrics.viewport.data(0) / 2,
//		              metrics.viewport.data(1) / 2 )	// 4. Place the whole result at the centre of the view port.			
//		if( rotation != 0 )
//			Tx.rotate( rotation/(180/Pi) );				// 3. Eventually apply a rotation.
//		Tx.scale( sx, -sy )								// 2. Scale the graph to pixels. Scale -y since we reverse the view (top-left to bottom-left).
//		Tx.translate( -tx, -ty )						// 1. Move the graph so that the given centre is at (0,0).
//		
//		xT = new AffineTransform( Tx )
//		try { xT.invert } catch { case _ => System.err.printf( "cannot inverse gu2px matrix...%n" ) }
		
		metrics.ratioPx2Gu = sx

		val w2 = (metrics.viewport(0) / sx) / 2f
		val h2 = (metrics.viewport(1) / sx) / 2f
		
		metrics.loVisible.set(center.x-w2, center.y-h2)
		metrics.hiVisible.set(center.x+w2, center.y+h2)
		
//		Tx
  	}

  	/**
     * Enable or disable automatic adjustment of the view to see the entire graph.
     * @param on If true, automatic adjustment is enabled.
     */
    def setAutoFitView(on:Boolean) {
  		if(autoFit && (! on)) {
  			// We go from autoFit to user view, ensure the current centre is at the
  			// middle of the graph, and the zoom is at one.
			
  			zoom = 1
  			center.set(metrics.lo.x + (metrics.size(0) / 2),
  			           metrics.lo.y + (metrics.size(1) / 2), 0);
  		}

  		autoFit = on
  	}
	
  	/**
  	 * Set the centre of the view (the looked at point). As the viewer is only 2D, the z value is
  	 * not required.
  	 * @param x The new position abscissa.
  	 * @param y The new position ordinate.
  	 */
  	def setViewCenter(x:Double, y:Double) { center.set(x, y, 0) }
	
  	/**
     * Set the zoom (or percent of the graph visible), 1 means the graph is fully visible.
     * @param z The zoom.
     */
    def viewPercent_=(z:Double) { zoom = z }
	
  	/**
  	 * Set the rotation angle around the centre.
  	 * @param angle The rotation angle in degrees.
  	 */
  	def viewRotation_=(angle:Double) { rotation = angle }

  	/**
  	 * Set the output view port size in pixels. 
  	 * @param viewportWidth The width in pixels of the view port.
  	 * @param viewportHeight The width in pixels of the view port.
  	 */
  	def setViewport(viewportWidth:Double, viewportHeight:Double) { metrics.setViewport(viewportWidth, viewportHeight) }
	
  	/**
  	 * Set the graphic graph bounds (the lowest and highest points).
  	 * @param minx Lowest abscissa.
  	 * @param miny Lowest ordinate.
  	 * @param minz Lowest depth.
  	 * @param maxx Highest abscissa.
  	 * @param maxy Highest ordinate.
  	 * @param maxz Highest depth.
  	 */
  	def setBounds(minx:Double, miny:Double, minz:Double, maxx:Double, maxy:Double, maxz:Double) =
  	    metrics.setBounds(minx, miny, minz, maxx, maxy, maxz)
  
  	/** Set the graphic graph bounds from the graphic graph. */
  	def setBounds(graph:GraphicGraph) {
  	    setBounds(graph.getMinPos.x, graph.getMinPos.y, 0, graph.getMaxPos.x, graph.getMaxPos.y, 0)
  	}
   
// Utility
	
  	/**
  	 * Set the graph padding. Called in pushView.
  	 * @param graph The graphic graph.
  	 */
  	protected def setPadding(graph:GraphicGraph) { padding.copy(graph.getStyle.getPadding) }
  	
  	/**
  	 * Process each node to check if it is in the actual view port, and mark invisible nodes. This
  	 * method allows for fast node, sprite and edge visibility checking when drawing. This must be
  	 * called before each rendering (if the view port changed). Called in pushView.
  	 */
  	protected def checkVisibility(graph:GraphicGraph) {
  		val W:Double = metrics.viewport(0)
  		val H:Double = metrics.viewport(1)
		
  		nodeInvisible.clear
	
  		graph.getEachNode.foreach { node:Node =>
  			val visible = isNodeIn(node.asInstanceOf[GraphicNode], 0, 0, W, H) && (!node.asInstanceOf[GraphicNode].hidden) && node.asInstanceOf[GraphicNode].positionned;
			
  			if(! visible)
  				nodeInvisible += node.getId
  		}
  	}

  	protected def paddingXgu:Double = if(padding.units == Units.GU && padding.size > 0) padding.get( 0 ) else 0
  	protected def paddingYgu:Double = if(padding.units == Units.GU && padding.size > 1) padding.get( 1 ) else paddingXgu
  	protected def paddingXpx:Double = if(padding.units == Units.PX && padding.size > 0) padding.get( 0 ) else 0
  	protected def paddingYpx:Double = if(padding.units == Units.PX && padding.size > 1) padding.get( 1 ) else paddingXpx

  	/**
  	 * Check if a sprite is visible in the current view port.
  	 * @param sprite The sprite to check.
  	 * @return True if visible.
  	 */
  	protected def isSpriteVisible(sprite:GraphicSprite):Boolean =
  	    isSpriteIn(sprite, 0, 0, metrics.viewport(0), metrics.viewport(1))

  	/**
  	 * Check if an edge is visible in the current view port.
  	 * @param edge The edge to check.
  	 * @return True if visible.
  	 */
  	protected def isEdgeVisible(edge:GraphicEdge):Boolean = {
  	    if((!edge.getNode0[GraphicNode].positionned)
  	    || (!edge.getNode1[GraphicNode].positionned)) {
  	        false
  	    } else {
  	    	val node0Invis = nodeInvisible.contains(edge.getNode0[Node].getId)
  			val node1Invis = nodeInvisible.contains(edge.getNode1[Node].getId)
		
  			! (node0Invis && node1Invis)
  	    }
  	}

  	/**
  	 * Is the given node visible in the given area.
  	 * @param node The node to check.
  	 * @param X1 The min abscissa of the area.
  	 * @param Y1 The min ordinate of the area.
  	 * @param X2 The max abscissa of the area.
  	 * @param Y2 The max ordinate of the area.
  	 * @return True if the node lies in the given area.
  	 */
  	protected def isNodeIn(node:GraphicNode, X1:Double, Y1:Double, X2:Double, Y2:Double):Boolean = {
  		val size = getNodeOrSpriteSize(node)//node.getStyle.getSize
  		val w2   = metrics.lengthToPx(size, 0) / 2
  		val h2   = if(size.size > 1) metrics.lengthToPx(size, 1)/2 else w2
  		val src  = new Point3(node.getX, node.getY, 0)
		
  		bck.transform(src)
//  		Tx.transform( src, src )

  		val x1 = src.x - w2
  		val x2 = src.x + w2
  		val y1 = src.y - h2
  		val y2 = src.y + h2
		
  		if(     x2 < X1) false
  		else if(y2 < Y1) false
  		else if(x1 > X2) false
  		else if(y1 > Y2) false
  		else             true
  	}
	
  	/**
  	 * Is the given sprite visible in the given area.
  	 * @param sprite The sprite to check.
  	 * @param X1 The min abscissa of the area.
  	 * @param Y1 The min ordinate of the area.
  	 * @param X2 The max abscissa of the area.
  	 * @param Y2 The max ordinate of the area.
  	 * @return True if the node lies in the given area.
  	 */
  	protected def isSpriteIn( sprite:GraphicSprite, X1:Double, Y1:Double, X2:Double, Y2:Double ):Boolean = {
  		if( sprite.isAttachedToNode && ( nodeInvisible.contains( sprite.getNodeAttachment.getId ) ) ) {
  			false
  		} else if( sprite.isAttachedToEdge && ! isEdgeVisible( sprite.getEdgeAttachment ) ) {
  			false
  		} else {
  			val size = sprite.getStyle.getSize
  			val w2   = metrics.lengthToPx( size, 0 ) / 2
  			val h2   = if( size.size > 1 ) metrics.lengthToPx( size, 1 )/2 else w2
  			val src  = spritePositionPx( sprite )
	
  			val x1 = src.x - w2
  			val x2 = src.x + w2
  			val y1 = src.y - h2
  			val y2 = src.y + h2
		
  			if(      x2 < X1 ) false
  			else if( y2 < Y1 ) false
  			else if( x1 > X2 ) false
  			else if( y1 > Y2 ) false
  			else               true 
  		}
  	}
   
  	protected def spritePositionPx(sprite:GraphicSprite):Point3 = {
  		getSpritePosition(sprite, new Point3, Units.PX)
//  		sprite.getUnits match {
//  			case Units.PX       => { new Point2D.Double( sprite.getX, sprite.getY ) }
//  			case Units.GU       => { val pos = new Point2D.Double( sprite.getX, sprite.getY ); Tx.transform( pos, pos ).asInstanceOf[Point2D.Double] }
//  			case Units.PERCENTS => { new Point2D.Double( (sprite.getX/100f)*metrics.viewport.data(0), (sprite.getY/100f)*metrics.viewport.data(1) ) }
//  		}
  	}

  	/**
  	 * Check if a node contains the given point (x,y).
  	 * @param elt The node.
  	 * @param x The point abscissa.
  	 * @param y The point ordinate.
  	 * @return True if (x,y) is in the given element.
  	 */
  	protected def nodeContains(elt:GraphicElement, x:Double, y:Double):Boolean = {
  		val size = getNodeOrSpriteSize(elt)	//  elt.getStyle.getSize	// TODO use nodeinfo
  		val w2   = metrics.lengthToPx(size, 0) / 2
  		val h2   = if(size.size() > 1) metrics.lengthToPx(size, 1)/2 else w2
  		val dst  = bck.transform(elt.getX, elt.getY, 0)
		
  		val x1 = dst.x - w2
  		val x2 = dst.x + w2
  		val y1 = dst.y - h2
  		val y2 = dst.y + h2
		
  		if(     x < x1) false
  		else if(y < y1) false
  		else if(x > x2) false
  		else if(y > y2) false
  		else            true
  	}

  	/**
  	 * Check if a sprite contains the given point (x,y).
  	 * @param elt The sprite.
  	 * @param x The point abscissa.
  	 * @param y The point ordinate.
  	 * @return True if (x,y) is in the given element.
  	 */
  	protected def spriteContains(elt:GraphicElement, x:Double, y:Double):Boolean = {
  		val sprite = elt.asInstanceOf[GraphicSprite]
  		val size   = getNodeOrSpriteSize(elt) //sprite.getStyle.getSize // TODO use nodeinfo
  		val w2     = metrics.lengthToPx(size, 0) / 2
  		val h2     = if(size.size() > 1) metrics.lengthToPx(size, 1)/2 else w2
  		val dst    = spritePositionPx(sprite) // new Point2D.Double( sprite.getX(), sprite.getY() )
//  		val dst    = new Point2D.Double
//	
//  		Tx.transform( src, dst )

  		val x1 = dst.x - w2
  		val x2 = dst.x + w2
  		val y1 = dst.y - h2
  		val y2 = dst.y + h2
		
  		if(     x < x1) false
  		else if(y < y1) false
  		else if(x > x2) false
  		else if(y > y2) false
  		else            true	
  	}
  	
  	protected def getNodeOrSpriteSize(elt:GraphicElement):Values = {
  		val info = elt.getAttribute(ElementInfo.attributeName).asInstanceOf[NodeInfo]
  		
  		if(info ne null) {
  			new Values(Units.GU, info.theSize.x, info.theSize.y)
  		} else {
  		    elt.getStyle.getSize
  		}
  	}
   
  	protected def styleVisible(element:GraphicElement):Boolean = {
  		val visibility = element.getStyle.getVisibility
  		element.getStyle.getVisibilityMode match {
  			case VisibilityMode.HIDDEN     => false
  			case VisibilityMode.AT_ZOOM    => (zoom == visibility(0))
  			case VisibilityMode.UNDER_ZOOM => (zoom <= visibility(0))
  			case VisibilityMode.OVER_ZOOM  => (zoom >= visibility(0))
  			case VisibilityMode.ZOOM_RANGE => if(visibility.size > 1) (zoom >= visibility(0) && zoom <= visibility(1)) else true
  			case VisibilityMode.ZOOMS      => values.contains(visibility(0))
  			case _                         => true
  		}
  	}

  	def isTextVisible(element:GraphicElement):Boolean = {
  		val visibility = element.getStyle.getTextVisibility
  		element.getStyle.getTextVisibilityMode match {
  			case TextVisibilityMode.HIDDEN     => false
  			case TextVisibilityMode.AT_ZOOM    => (zoom == visibility(0))
  			case TextVisibilityMode.UNDER_ZOOM => (zoom <= visibility(0))
  			case TextVisibilityMode.OVER_ZOOM  => (zoom >= visibility(0))
  			case TextVisibilityMode.ZOOM_RANGE => if(visibility.size > 1) (zoom >= visibility(0) && zoom <= visibility(1)) else true
  			case TextVisibilityMode.ZOOMS      => values.contains(visibility(0))
  			case _                             => true
  		}
  	}
  	
  	def getNodeOrSpritePositionGU(elt:GraphicElement, pos:Point3):Point3 = {
  		var p = pos
  		if(p eq null) p = new Point3
  		elt match {
  			case node:GraphicNode     => { p.x = node.getX; p.y = node.getY; p }
  			case sprite:GraphicSprite => { getSpritePosition(sprite, p, Units.GU) }
  		}
  	}
   
  	/**
  	 * Compute the position of a sprite if it is not attached.
  	 * @param sprite The sprite.
  	 * @param position Where to stored the computed position, if null, the position is created.
  	 * @param units The units the computed position must be given into. 
  	 * @return The same instance as pos, or a new one if pos was null.
  	 */
  	protected def getSpritePositionFree(sprite:GraphicSprite, position:Point3, units:Units):Point3 = {
  		var pos = position
  
  		if(pos eq null)
  			pos = new Point3
		
  		if(sprite.getUnits == units) {
  			pos.x = sprite.getX
  			pos.y = sprite.getY
  		} else if(units == Units.GU && sprite.getUnits == Units.PX) {
  			pos.x = sprite.getX
  			pos.y = sprite.getY
  			bck.transform(pos)
  		} else if(units == Units.PX && sprite.getUnits == Units.GU) {
  			pos.x = sprite.getX
  			pos.y = sprite.getY
  			bck.transform(pos)
  		} else if(units == Units.GU && sprite.getUnits == Units.PERCENTS) {
  			pos.x = metrics.lo.x + (sprite.getX/100f) * metrics.graphWidthGU
  			pos.y = metrics.lo.y + (sprite.getY/100f) * metrics.graphHeightGU
  		} else if(units == Units.PX && sprite.getUnits == Units.PERCENTS) {
  			pos.x = (sprite.getX/100f) * metrics.viewport.data(0)
  			pos.y = (sprite.getY/100f) * metrics.viewport.data(1)
  		} else {
  			throw new RuntimeException("Unhandled yet sprite positioning convertion %s to %s.".format(sprite.getUnits, units))
  		}
		
  		pos
  	}

  	/**
     * Compute the position of a sprite if attached to a node.
     * @param sprite The sprite.
     * @param pos Where to stored the computed position, if null, the position is created.
     * @param units The units the computed position must be given into. 
     * @return The same instance as pos, or a new one if pos was null.
     */
    protected def getSpritePositionNode(sprite:GraphicSprite, position:Point3, units:Units):Point3 = {
    	var pos = position

    	if(pos eq null)
    		pos = new Point3
		
    	val node   = sprite.getNodeAttachment
    	val radius = metrics.lengthToGu( sprite.getX, sprite.getUnits )
    	val z      = sprite.getZ * (Pi / 180.0)
		
    	pos.x = node.x + (cos(z) * radius)
    	pos.y = node.y + (sin(z) * radius)

    	if(units == Units.PX)
    		bck.transform(pos)

    	pos
    }
	
  	/**
  	 * Compute the position of a sprite if attached to an edge.
  	 * @param sprite The sprite.
  	 * @param pos Where to store the computed position, if null, the position is created.
  	 * @param units The units the computed position must be given into. 
  	 * @return The same instance as pos, or a new one if pos was null.
  	 */
  	protected def getSpritePositionEdge(sprite:GraphicSprite, position:Point3, units:Units):Point3 = {
  		var pos = position

  		if(pos eq null)
  			pos = new Point3
		
  		val edge = sprite.getEdgeAttachment.asInstanceOf[GraphicEdge]
  		val info = edge.getAttribute(ElementInfo.attributeName).asInstanceOf[EdgeInfo]
  		
  		if(info ne null) {
  			val o  = metrics.lengthToGu(sprite.getY, sprite.getUnits)
  			if(o==0) {
  				val p = info.pointOnShape(sprite.getX)
  				pos.x = p.x
  				pos.y = p.y
  			} else {
  			    val p = info.pointOnShapeAndPerpendicular(sprite.getX, o)
  			    pos.x = p.x
  			    pos.y = p.y
  			}
  		} else {
  			var x  = 0.0
  			var y  = 0.0
  			var dx = 0.0
  			var dy = 0.0
  			var d  = sprite.getX				// Percent on the edge.
  			val o  = metrics.lengthToGu(sprite.getY, sprite.getUnits)	
  												// Offset from the position given by percent, perpendicular to the edge.
  			x  = edge.from.x
  			y  = edge.from.y
  			dx = edge.to.x - x
  			dy = edge.to.y - y
  			
  			d = if( d > 1 ) 1 else d
  			d = if( d < 0 ) 0 else d
			
  			x += dx * d
  			y += dy * d
			
  			if(o != 0) {
  				d   = sqrt( dx*dx + dy*dy )
  				dx /= d
  				dy /= d
			
  				x += -dy * o
  				y +=  dx * o
  			}
			
  			pos.x = x
  			pos.y = y
  		}
			
  		if(units == Units.PX)
  			bck.transform(pos)

  		pos
  	}
}