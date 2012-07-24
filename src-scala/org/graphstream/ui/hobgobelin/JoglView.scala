package org.graphstream.ui.hobgobelin

import org.graphstream.ui.swingViewer.DefaultView
import org.graphstream.ui.swingViewer.Viewer
import org.graphstream.ui.swingViewer.GraphRenderer
import javax.media.opengl.GLProfile
import javax.media.opengl.GLCapabilities
import javax.media.opengl.awt.GLCanvas
import javax.media.opengl.GLEventListener
import javax.media.opengl.GLAutoDrawable
import org.graphstream.ui.graphicGraph.GraphicGraph
import org.graphstream.ui.swingViewer.util.MouseManager

/** A replacement of the DefaultView that provides an OpenGL context for rendering.
  * 
  * The JoglView provides a standard JOGL GLCanvas. It does not use NEWT since this is
  * not compatible with Swing. */
class JoglView extends DefaultView with GLEventListener {
	protected var profile:GLProfile = null
	
	protected var canvas:GLCanvas = null
	
	override def open(identifier:String, viewer:Viewer, renderer:GraphRenderer) {
		sys.props += "sun.java2d.opengl" -> "false"
		sys.props += "sun.java2d.noddraw" -> "true"

		if(!(renderer.isInstanceOf[JoglGraphRenderer])) {
			throw new RuntimeException("Cannot create a JoglView with a graph renderer that does not implement JoglGraphRenderer (%s)".format(renderer.getClass.getName))
		}
		
		this.id       = identifier;
		this.viewer   = viewer;
		this.renderer = renderer;
		this.graph    = viewer.getGraphicGraph
		this.profile  = GLProfile.getGL2ES2//get(GLProfile.GL2ES2)	// Try to be android/iPhone compatible.   
		
		val caps = new GLCapabilities(profile)

		caps.setDoubleBuffered(true)
		caps.setRedBits(8)
		caps.setGreenBits(8)
		caps.setBlueBits(8)
		caps.setAlphaBits(8)
		
		this.canvas = new GLCanvas(caps)

		canvas.addGLEventListener(this)
		setLayout(new java.awt.BorderLayout)
		add(canvas, java.awt.BorderLayout.CENTER)

		setMouseManager(null)
		setShortcutManager(null)
		graph.addAttributeSink(this)
		checkInitialAttributes
		renderer.open(graph, canvas)
	}
	
	override def display(graph:GraphicGraph, graphChanged:Boolean) {
		canvas.display
	}
	
	override def paintComponent(g:java.awt.Graphics) {
		canvas.display
	}
	
	override def render(g:java.awt.Graphics2D) {
		// NOP here. We inherit this from the DefaultView, but will not use it.
		// all is done in the display(GLAutoDrawable) method
		// called automatically by the system or by display(GraphicGraph,Boolean)
		// or paintComponent(Graphics).
	}
	
	override def getComponent():java.awt.Component = canvas
	
	//-------------------------------------------------------------------------
	// GLEventListener

	protected var glInited = false
	
	def reshape(drawable:GLAutoDrawable, x:Int, y:Int, width:Int, height:Int) {
Console.err.printf("gl reshape")
		renderer.asInstanceOf[JoglGraphRenderer].reshape(x, y, width, height)
	}

	def init(drawable:GLAutoDrawable) {
Console.err.printf("gl init")
		glInited = true
		renderer.asInstanceOf[JoglGraphRenderer].init(drawable)
	}

	def dispose(drawable:GLAutoDrawable) {
Console.err.printf("gl dispose")
		glInited = false
	}

	def display(drawable:GLAutoDrawable) {
Console.err.printf("gl display%n")
		if(glInited && (!isIconified)) {
			renderer.render(null, canvas.getX, canvas.getY, canvas.getWidth, canvas.getHeight)
			drawable.swapBuffers
		}
	}
}