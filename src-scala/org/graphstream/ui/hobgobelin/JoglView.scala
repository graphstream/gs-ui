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

/** A replacement of the DefaultView that provides an OpenGL context for renderering.
  * 
  * The JoglView provides a standard JOGL GLCanvas. It does not use NEWT since this is
  * not compatible with Swing. */
class JoglView extends DefaultView with GLEventListener {
	protected var profile:GLProfile = null
	
	protected var canvas:GLCanvas = null
	
	override def open(identifier:String, viewer:Viewer, renderer:GraphRenderer) {
		System.getProperties.setProperty("sun.java2d.opengl", "false")
		System.getProperties.setProperty("sun.java2d.noddraw", "true")
		
		this.id       = identifier;
		this.viewer   = viewer;
		this.renderer = renderer;
		this.graph    = viewer.getGraphicGraph
		this.profile  = GLProfile.get(GLProfile.GL2ES2)	// Try to be android/iPhone compatible.   
		
		val caps = new GLCapabilities(profile)
		
		caps.setDoubleBuffered(true)
		caps.setRedBits(8)
		caps.setGreenBits(8)
		caps.setBlueBits(8)
		caps.setAlphaBits(8)
		
		this.canvas = new GLCanvas(caps)

		canvas.addGLEventListener(this)
		add(canvas)
		
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
		// NOP here, all is done in the display(GLAutoDrawable) method
		// called automatically by the system or by display(GraphicGraph,Boolean)
		// or paintComponent(Graphics).
	}
	
	override def getComponent():java.awt.Component = canvas
	
	//-------------------------------------------------------------------------
	// GLEventListener

	def reshape(drawable:GLAutoDrawable, x:Int, y:Int, width:Int, height:Int) {
	}

	def init(drawable:GLAutoDrawable) {
	}

	def dispose(drawable:GLAutoDrawable) {
	}

	def display(drawable:GLAutoDrawable) {
	}
}