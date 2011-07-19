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
  
import java.awt.{Container, Graphics2D}//, RenderingHints}
import java.util.ArrayList
import java.io.{File, IOException}
import java.awt.image.BufferedImage

import scala.collection.JavaConversions._

import org.graphstream.ui.geom.Point3

import org.graphstream.graph.Element

import org.graphstream.ui.swingViewer.{GraphRenderer, LayerRenderer}
import org.graphstream.ui.graphicGraph.{GraphicGraph, GraphicElement, StyleGroup, StyleGroupListener}
import org.graphstream.ui.graphicGraph.stylesheet.Selector

import org.graphstream.ui.util.Selection
import org.graphstream.ui.j2dviewer.renderer._
//import org.graphstream.ScalaGS._

import javax.imageio.ImageIO

object J2DGraphRenderer {
	val DEFAULT_RENDERER = "j2d_def_rndr";
}

/**
 * 2D renderer using Swing and Java2D to render the graph.
 * 
 * <p>
 * The role of this class is to equip each style group with a specific renderer and
 * to call these renderer to redraw the graph when needed.
 * </p>
 * 
 * <p>
 * A render pass begins by using the camera instance to set up the projection (allows
 * to pass from graph units to pixels, make a rotation a zoom or a translation) and
 * render each style group once for the shadows, and once for the real rendering
 * in Z order.
 * </p>
 * 
 * <p>
 * This class also handles a "selection" object that represents the current selection
 * and renders it.
 * </p>
 */
class J2DGraphRenderer extends GraphRenderer with StyleGroupListener {
// Attribute	
 
	/** Set the view on the view port defined by the metrics. */
	protected val camera = new Camera

	/** The graph to render. */
	protected var graph:GraphicGraph = null
 
	/** The drawing surface. */
	protected var surface:Container = null
 
	/** The current selection. */
	protected val selection = new Selection
 
	/** The layer renderer for the background (under the graph), can be null. */
	protected var backRenderer:LayerRenderer = null
	
	/** The layer renderer for the foreground (above the graph), can be null. */
	protected var foreRenderer:LayerRenderer = null
	
	/** The rendering backend. */
	protected var backend:Backend = null
	
// Construction
  
  	def open(graph:GraphicGraph, drawingSurface:Container) {
	  	if( this.graph == null ) {
		  	this.graph   = graph
		  	this.surface = drawingSurface
		  	graph.getStyleGroups.addListener(this)
	  	} else {
	  		throw new RuntimeException("renderer already open, use close() first")
	  	}
  	}
	
  	def close() {
  		if(graph != null) {
  			graph.getStyleGroups.removeListener(this)
  			graph   = null
  			surface = null
  		}
  	}
	
// Access

  	def getViewCenter():Point3 = camera.viewCenter

  	def getViewPercent():Double = camera.viewPercent

  	def getViewRotation():Double = camera.viewRotation

  	def getGraphDimension():Double = camera.metrics.diagonal

  	def findNodeOrSpriteAt(x:Double, y:Double):GraphicElement =
  	    camera.findNodeOrSpriteAt(graph, x, y)
 
  	def allNodesOrSpritesIn(x1:Double, y1:Double, x2:Double, y2:Double):ArrayList[GraphicElement] =
  	    camera.allNodesOrSpritesIn(graph, x1, y1, x2, y2)
  
  	/** The rendering surface this renderer uses. */
   	def renderingSurface:Container = surface
	
// Access -- Renderer bindings
   	
    protected def getStyleRenderer(graph:GraphicGraph):GraphBackgroundRenderer = {
  		if(graph.getStyle.getRenderer("dr") == null)
  			graph.getStyle.addRenderer("dr", new GraphBackgroundRenderer(graph, graph.getStyle))
  		
  		graph.getStyle.getRenderer("dr").asInstanceOf[GraphBackgroundRenderer]
    }
    
    protected def getStyleRenderer(style:StyleGroup):StyleRenderer = {
  		if( style.getRenderer("dr") == null)
  			style.addRenderer("dr", StyleRenderer(style, this))
    
  		style.getRenderer("dr").asInstanceOf[StyleRenderer]
    }
    
    protected def getStyleRenderer(element:GraphicElement):StyleRenderer = {
  		getStyleRenderer(element.getStyle)
    }
    
// Command

	def setBounds(minx:Double, miny:Double, minz:Double, maxx:Double, maxy:Double, maxz:Double) {
	    camera.setBounds(minx, miny, minz, maxx, maxy, maxz)
	}
 
  	def resetView() {
  		camera.setAutoFitView(true)
  		camera.viewRotation = 0
  	}
 
  	def setViewCenter(x:Double, y:Double, z:Double) {
  		camera.setAutoFitView(false)
  		camera.setViewCenter(x, y)
  	}
  	
  	def setGraphViewport(minx:Double, miny:Double, maxx:Double, maxy:Double) {
		camera.setAutoFitView(false)
		camera.setViewCenter(minx + (maxx - minx), miny + (maxy - miny))
		camera.setGraphViewport(minx, miny, maxx, maxy)
		camera.viewPercent = 1
	}
  	
  	def removeGraphViewport() {
  		camera.removeGraphViewport
  		resetView();
  	}

  	def setViewPercent(percent:Double) {
  		camera.setAutoFitView(false)
  		camera.viewPercent = percent
  	}

  	def setViewRotation(theta:Double) { camera.viewRotation = theta }

  	def beginSelectionAt(x:Double, y:Double) {
  		selection.active = true
  		selection.begins(x, y)
  	}

  	def selectionGrowsAt(x:Double, y:Double) {
  		selection.grows(x, y)
  	}

  	def endSelectionAt(x:Double, y:Double) {
  		selection.grows(x, y)
  		selection.active = false
  	}

  	def moveElementAtPx(element:GraphicElement, x:Double, y:Double) {
  		val p = camera.inverseTransform(x, y)
  		element.move(p.x, p.y, element.getZ)
  	}

// Commands -- Rendering
  
  	def render(g:Graphics2D, width:Int, height:Int) {
  		if(graph != null) {
  		    // Verify this view is not closed, the Swing repaint mechanism may trigger 1 or 2
  		    // calls to this after being closed.

  		    if(backend eq null)
  		        backend = new BackendJ2D // TODO choose it according to some setting ...
  		    
  		    backend.prepareNewFrame(g)
  		    camera.setBackend(backend)
  		        
  			val sgs = graph.getStyleGroups
  			
  			setupGraphics
  			graph.computeBounds
  			camera.setBounds(graph)
  			camera.setViewport(width, height)
  			getStyleRenderer(graph).render(backend, camera, width, height)
  			renderBackLayer
  
  			camera.pushView(graph)
  			sgs.shadows.foreach {
		  		getStyleRenderer(_).renderShadow(backend, camera)
  			}
  			sgs.zIndex.foreach { groups =>
  				groups.foreach { group =>
		  	  		if(group.getType != Selector.Type.GRAPH) {
		  	  			getStyleRenderer(group).render(backend, camera)
		  	  		}
  				}
  			}
 
  			camera.popView
  			renderForeLayer
  
  			if( selection.renderer == null ) selection.renderer = new SelectionRenderer( selection, graph )
  			selection.renderer.render(backend, camera, width, height )
  		}
  	}
   	
	protected def renderBackLayer() { if(backRenderer ne null) renderLayer(backRenderer) }
	
	protected def renderForeLayer() { if(foreRenderer ne null) renderLayer(foreRenderer) }
	
	protected def renderLayer(renderer:LayerRenderer) {
		val metrics = camera.metrics
		
		renderer.render(backend.graphics2D, graph, metrics.ratioPx2Gu,
			metrics.viewport.data(0).toInt,
			metrics.viewport.data(1).toInt,
			metrics.loVisible.x,
			metrics.loVisible.y,
			metrics.hiVisible.x,
			metrics.hiVisible.y)
	}

   protected def setupGraphics() {
       backend.setAntialias(graph.hasAttribute("ui.antialias"))
       backend.setQuality(graph.hasAttribute("ui.quality"))
   }

   def screenshot(filename:String, width:Int, height:Int) {
	   	if(filename.endsWith("png") || filename.endsWith("PNG")) {
			val img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
			render(img.createGraphics, width, height)
			val file = new File(filename)
			ImageIO.write(img, "png", file)

	   	} else if(filename.endsWith("bmp") || filename.endsWith("BMP")) {
			
	   	    val img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
			render(img.createGraphics, width, height)
			val file = new File(filename)
			ImageIO.write(img, "bmp", file)
			
		} else if(filename.endsWith("jpg")  || filename.endsWith("JPG")
		       || filename.endsWith("jpeg") || filename.endsWith("JPEG")) {
			
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
   
	def setBackLayerRenderer(renderer:LayerRenderer) { backRenderer = renderer }

	def setForeLayoutRenderer(renderer:LayerRenderer) { foreRenderer = renderer }
   
// Commands -- Style group listener
  
    def elementStyleChanged(element:Element, oldStyle:StyleGroup, style:StyleGroup) {
    	// XXX The element renderer should be the listener, not this. ... XXX

    	if(oldStyle ne null) {
    		
    	} else if(oldStyle ne null) {
    		val renderer = oldStyle.getRenderer(J2DGraphRenderer.DEFAULT_RENDERER)

	    	if((renderer ne null ) && renderer.isInstanceOf[JComponentRenderer])
	    		renderer.asInstanceOf[JComponentRenderer].unequipElement(element.asInstanceOf[GraphicElement])
    	}
    }
}