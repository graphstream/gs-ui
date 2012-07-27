package org.graphstream.ui.hobgobelin

import org.graphstream.ui.swingViewer.GraphRenderer
import org.graphstream.ui.swingViewer.LayerRenderer
import org.graphstream.ui.swingViewer.util.FPSLogger
import java.awt.Component
import org.graphstream.ui.graphicGraph.GraphicGraph
import org.graphstream.ui.graphicGraph.GraphicElement.Skeleton
import org.graphstream.ui.swingViewer.GraphRendererBase
import org.graphstream.ui.swingViewer.Camera
import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.graph.Element
import org.graphstream.ui.graphicGraph.StyleGroup
import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GL._
import javax.media.opengl.GL3._
import org.sofa.opengl.SGL
import javax.media.opengl.glu.GLU
import scala.collection.JavaConversions._
import java.awt.Graphics2D

/** A thing that has some OpenGL properties. */
trait JoglGraphRenderer {
	/** The drawing surface is initialized and usable. */
	def init(canvas:GLAutoDrawable)
	/** The drawing surface has been resized. */
	def reshape(x:Int, y:Int, width:Int, height:Int)
}

/** A very simple OpenGL renderer for the graph that respect a thin subset of CSS.
  * 
  * This is a minimal implementation of a renderer that only supports a subset
  * of the CSS:
  *   - Fill
  *   - Size
  *   - Stroke
  *   - Text
  *   
  * It uses OpenGL for rendering and needs a JoglView.
  */
class HobgobelinGraphRenderer extends GraphRendererBase with JoglGraphRenderer {
	//------------------------------------------------------------------
	// Attributes

	/** The camera. */
	protected var camera:HobgobelinCamera = null
	
	/** Render each node. */
	protected var nodeRenderer:NodeRenderer = null
	
	/** Render each edge. */
	protected var edgeRenderer:EdgeRenderer = null
	
	/** Render each sprite. */
	protected var spriteRenderer:SpriteRenderer = null
	
	/** Render the background of the graph, before anything is drawn. */
	protected var backRenderer:LayerRenderer = null
	
	/** Render the foreground of the graph, after anything is drawn. */
	protected var foreRenderer:LayerRenderer = null
	
	/** Optional output log of the frame-per-second. */
	protected var fpsLog:FPSLogger = null
	
	//------------------------------------------------------------------
	// Creation
	
	override def open(graph:GraphicGraph, renderingSurface:Component) {
		camera = new HobgobelinCamera(graph)
		super.open(graph, renderingSurface)
		graph.setSkeletonFactory(new HobgobelinSkeletonFactory())
	}
	
	override def close() {
		if(fpsLog ne null) {
			fpsLog.close
			fpsLog = null
		}
		
		graph.setSkeletonFactory(null)
		super.close
		camera = null
	}
	
	//------------------------------------------------------------------
	// Access
	
	override def getCamera():Camera = camera
	
	//------------------------------------------------------------------
	// GLEventListener
	
	var canvas:GLAutoDrawable = null
	
	var sgl:SGL = null
	
	override def init(canvas:GLAutoDrawable) {
		this.canvas = canvas
		val gl = canvas.getGL.getGL2ES2; import gl._;
		sgl = new org.sofa.opengl.backend.SGLJogl2ES2(gl.getGL2ES2, GLU.createGLU)
		
		sgl.clearColor(0f, 0f, 0f, 0f)
		sgl.clearDepth(1f)
		sgl.enable(GL_DEPTH_TEST)
	}
	
	override def reshape(x:Int, y:Int, width:Int, height:Int) {
		sgl.viewport(0, 0, width, height)
	}

	//--------------------------------------------------------------------
	// Command
	
	override def moveElementAtPx(element:GraphicElement, x:Double, y:Double) {
		val p = camera.transformPxToGu(x, y)
		element.move(p.x, p.y, element.getCenter.z)
	}
	
	override def screenshot(filename:String, width:Int, height:Int) {
		// TODO
	}
	
	override def setBackLayerRenderer(renderer:LayerRenderer) {
		backRenderer = renderer
	}
	
	override def setForeLayerRenderer(renderer:LayerRenderer) {
		foreRenderer = renderer
	}
	
	override def elementStyleChanged(element:Element, oldStyle:StyleGroup, newStyle:StyleGroup) {
	}
	
	//------------------------------------------------------------------
	// Rendering
	
	override def render(g:java.awt.Graphics2D, x:Int, y:Int, width:Int, height:Int) {
		val camera = this.camera.asInstanceOf[HobgobelinCamera]
		renderGraphBackground
		camera.pushPXView(x, y, width, height)
		renderLayer(g, backRenderer, true)
		camera.pushGUView(x, y, width, height)
		renderLayer(g, backRenderer, false)
		renderGraphElements
		renderGraphForeground
		renderLayer(g, foreRenderer, false)
		camera.popGUView
		renderLayer(g, foreRenderer, true)
		camera.popPXView
	}
	
	protected def renderGraphBackground() {
		sgl.clearColor(graph.getStyle.getFillColor(0))
		sgl.clear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT)		
	}

	protected def renderGraphElements() {
		val sgs = graph.getStyleGroups
		
		// Render in Z-index order.
		
		if(sgs ne null) {
			sgs.zIndex.foreach { groups =>
				groups.foreach { group =>
					renderGroup(group)
				}
			}
		}
	}
	
	protected def renderGraphForeground() {
		
	}
	
	protected def renderGroup(group:StyleGroup) {
		
	}
	
	protected def renderLayer(g:Graphics2D, renderer:LayerRenderer, inPixels:Boolean) {
		if (renderer != null && renderer.rendersInPX == inPixels) {
			renderer.render(g, graph, camera)
		}
	}
}

/** Creates the skeletons for each element, we can reuse the basic renderer skeletons. */
class HobgobelinSkeletonFactory extends GraphicGraph.SkeletonFactory {
	def newNodeSkeleton():Skeleton = new org.graphstream.ui.swingViewer.basicRenderer.skeletons.NodeSkeleton()
	def newEdgeSkeleton():Skeleton = new org.graphstream.ui.swingViewer.basicRenderer.skeletons.EdgeSkeleton()
	def newSpriteSkeleton():Skeleton = new org.graphstream.ui.swingViewer.basicRenderer.skeletons.SpriteSkeleton()
}