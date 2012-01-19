/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
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
package org.graphstream.ui.gobelin
  
import java.awt.{Container, Graphics2D}
import java.util.ArrayList
import java.io.{File, IOException}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import org.graphstream.ui.geom.Point3
import org.graphstream.graph.Element

import org.graphstream.ui.graphicGraph.{GraphicGraph, GraphicElement, GraphicNode, GraphicEdge, GraphicSprite, StyleGroup, StyleGroupListener}
import org.graphstream.ui.graphicGraph.stylesheet.Selector

import org.graphstream.ui.swingViewer.GraphRendererBase
import org.graphstream.ui.swingViewer.util.FPSLogger
import org.graphstream.ui.swingViewer.{GraphRenderer, LayerRenderer}

import org.graphstream.ui.gobelin.java2d.BackendJava2D

import scala.collection.JavaConversions._

/** Gobelin renderer.
  * 
  * The role of this class is to equip each style group with a specific renderer and to call these
  * renderers to redraw the graph when needed. At the contrary of the basic renderer where it exists
  * only three style renderers (one for nodes, one for edges and one for sprites), the Gobelin
  * renderer assigns a style to each group. This allows to configure the style renderer once for
  * each style.
  * 
  * The renderers then provides a skeleton factory for each node, edge and sprite, that defines
  * skeletons giving the main geometry of elements and register changes in the element, then selects
  * a skin according to the group style. The skin will be "applied" to each element to draw in the
  * group. The skin will be moved and scaled according to the skeleton.
  * 
  * A render pass begins by using the camera instance to set up the projection (allows to pass from
  * graph units to pixels, make a rotation a zoom or a translation) and render each style group once
  * for the shadows, and once for the real rendering in Z order.
  * 
  * This class also handles a "selection" object that represents the current selection and renders
  * it.
  * 
  * The renderer uses a back end so that it can adapt to multiple rendering targets (here Swing and
  * OpenGL). As the skin are finally responsible for drawing the graph, the back end is also
  * responsible for the skin creation. */
class GobelinGraphRenderer extends GraphRendererBase {
	/** Set the view on the view port defined by the metrics. */
	protected var camera:Camera = null

	/** The layer renderer for the background (under the graph), can be null. */
	protected var backRenderer:LayerRenderer = null
	
	/** The layer renderer for the foreground (above the graph), can be null. */
	protected var foreRenderer:LayerRenderer = null
	
	/** The rendering back end. */
	protected var backend:Backend = null
	
	/** Optional output for frame-per-second statistics. */
	protected var fpsLogger:FPSLogger = null
	
// Construction
	
  	def open(graph:GraphicGraph, drawingSurface:Container) {
		this.backend   = chooseBackend
		this.selection = new Selection
	    this.camera    = backend.chooseCamera
	
		selection.asInstanceOf[Selection].renderer = backend.chooseSelectionRenderer
  			
		backend.open(drawingSurface)
	    super.open(graph, backend.drawingSurface)
		graph.setSkeletonFactory(backend)
  	}
	
  	def close() {
  		if(graph != null) {
  		    if(fpsLogger ne null) {
  		        fpsLogger.close
  		        fpsLogger = null
  		    }
  		    
  		    graph.setSkeletonFactory(null)
  		    super.close
  		    removeRenderers  		    
  		    backend.close

  			backend   = null
  			selection = null
  			camera    = null
  		}
  	}
	
// Access
  	
  	def getCamera() = camera

//  	def findNodeOrSpriteAt(x:Double, y:Double):GraphicElement = camera.findNodeOrSpriteAt(graph, x, y)
 
//  	def allNodesOrSpritesIn(x1:Double, y1:Double, x2:Double, y2:Double):ArrayList[GraphicElement] = camera.allNodesOrSpritesIn(graph, x1, y1, x2, y2)
  
   	def renderingSurface:Container = backend.drawingSurface
	
// Access -- Renderer bindings
   	
   	/** Get (and assign if needed) a style renderer to the graphic graph. The renderer will be reused then. */
    protected def styleRenderer(graph:GraphicGraph):GraphStyleRenderer = {
  		if(graph.getStyle.getRenderer("dr") eq null)
  			graph.getStyle.addRenderer("dr", backend.chooseGraphStyleRenderer(graph))
  		
  		graph.getStyle.getRenderer("dr").asInstanceOf[GraphStyleRenderer]
    }
    
  	/** Get (and assign if needed) a style renderer to a style group. The renderer will be reused then. */
    protected def styleRenderer(style:StyleGroup):StyleRenderer = {
  		if( style.getRenderer("dr") eq null)
  			style.addRenderer("dr", backend.chooseStyleRenderer(style))
    
  		style.getRenderer("dr").asInstanceOf[StyleRenderer]
    }
    
    /** Get (and assign if needed) the style renderer associated with the style group of the element. */
    protected def styleRenderer(element:GraphicElement):StyleRenderer = {
  		styleRenderer(element.getStyle)
    }
    
    /** Remove all the registered renderers from the graphic graph. */
    protected def removeRenderers() {
        graph.getStyle.removeRenderer("dr")
        graph.getNodeIterator.foreach   { node:GraphicNode     => node.getStyle.removeRenderer("dr") }
        graph.getEdgeIterator.foreach   { edge:GraphicEdge     => edge.getStyle.removeRenderer("dr") }
        graph.getSpriteIterator.foreach { sprite:GraphicSprite => sprite.getStyle.removeRenderer("dr") }
    }
    
// Commands -- Rendering
  
  	def render(g:Graphics2D, width:Int, height:Int) {
  	    if(graph ne null) {
  	        startFrame
  			setupGraphics
  		    backend.startFrame(g)
  			graph.computeBounds
  			camera.setBounds(graph)
  			camera.setSurfaceSize(width, height)
  			renderGraph
  			renderBackLayer
  			camera.pushView(graph)
  			renderShadows
  			renderElements
  			camera.popView
  			renderForeLayer
  			selection.asInstanceOf[Selection].renderer.render(camera)
  	    	endFrame
  	    }
  	}

  	/** Render the graph background using the graph style renderer. */
  	protected def renderGraph() {
  	    val metrics = camera.getMetrics
  	    styleRenderer(graph).render(camera, metrics.surfaceSize.x.toInt, metrics.surfaceSize.y.toInt)   
  	}
  	
  	/** Render the shadow of each element that has one and is visible. */
  	protected def renderShadows() {
  		graph.getStyleGroups.shadows.foreach { group =>
		  	styleRenderer(group).renderShadow(camera)
  		}
  	}
  	
  	/** Render each element that is visible. */
  	protected def renderElements() {
  		graph.getStyleGroups.zIndex.foreach { groups =>
  			groups.foreach { group =>
		 	  	if(group.getType != Selector.Type.GRAPH) {
		  	  		styleRenderer(group).render(camera)
		  	  	}
  			}
  		}
  	}

  	/** Start a new frame. Actually only used to measure FPS statistics. */
  	protected def startFrame() {
  	    if((fpsLogger eq null) && graph.hasLabel("ui.log")) {
  	        fpsLogger = new FPSLogger(graph.getLabel("ui.log").toString)
  	    }
  	    
  	    if(! (fpsLogger eq null))
  	    	fpsLogger.beginFrame
  	}
  	
  	/** End the last frame. Actually only used to log FPS statistics. */
  	protected def endFrame() {
  	    if(! (fpsLogger eq null))
  	        fpsLogger.endFrame
  	}
   	
  	/** Render the user layer at the background. */
	protected def renderBackLayer() = if(backRenderer ne null) renderLayer(backRenderer)
	
	/** Render the user layer at the foreground. */
	protected def renderForeLayer() = if(foreRenderer ne null) renderLayer(foreRenderer)
	
	/** Render a back or front layer. */ 
	protected def renderLayer(renderer:LayerRenderer) {
		val metrics = camera.metrics
		
		renderer.render(backend.engine.asInstanceOf[Graphics2D], graph, metrics.ratioPx2Gu,
			metrics.surfaceSize.data(0).toInt,
			metrics.surfaceSize.data(1).toInt,
			metrics.loVisible.x,
			metrics.loVisible.y,
			metrics.hiVisible.x,
			metrics.hiVisible.y)
	}

	/** Setup the back-end before drawing. */
	protected def setupGraphics() {
       backend.antialias = graph.hasAttribute("ui.antialias")
       backend.quality   = graph.hasAttribute("ui.quality")
	}
	
	/** Take a screen shot of the actual graph state and store it in a file. */
	def screenshot(filename:String, width:Int, height:Int) {
	   	if(filename.toLowerCase.endsWith("png")) {
			val img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
			render(img.createGraphics, width, height)
			val file = new File(filename)
			ImageIO.write(img, "png", file)
	   	} else if(filename.toLowerCase.endsWith("bmp")) {
			// Who, in the world, is still using BMP ???
	   	    val img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
			render(img.createGraphics, width, height)
			val file = new File(filename)
			ImageIO.write(img, "bmp", file)
		} else if(filename.toLowerCase.endsWith("jpg") || filename.toLowerCase.endsWith("jpeg")) {
		    val img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
			render(img.createGraphics, width, height)
			val file = new File(filename)
			ImageIO.write(img, "jpg", file)
		} else {
		    System.err.println("unknown screenshot filename extension %s, saving to jpeg".format(filename))
		    val img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
			render(img.createGraphics, width, height)
			val file = new File(filename+".jpg")
			ImageIO.write(img, "jpg", file)
		}
	}
   
	/** Change the renderer for the user background layer. */
	def setBackLayerRenderer(renderer:LayerRenderer) { backRenderer = renderer }

	/** Change the renderer for the user foreground layer. */
	def setForeLayoutRenderer(renderer:LayerRenderer) { foreRenderer = renderer }

	/** Choose the appropriate back-end according to the system property "gs.ui.backend". */
	protected def chooseBackend():Backend = {
	    val backend = System.getProperty("gs.ui.backend")
	    if(backend ne null) {
	        backend.toLowerCase match {
	            case "opengl" => throw new RuntimeException
	            case "gl"     => throw new RuntimeException
	            case "java2d" => new BackendJava2D
	            case "j2d"    => new BackendJava2D
	            case _        => new BackendJava2D
	        }
		} else {
			new BackendJava2D 
		}
	}
}