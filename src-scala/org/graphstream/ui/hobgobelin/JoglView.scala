/*
 * Copyright 2006 - 2012
 *      Stefan Balev       <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	<yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
package org.graphstream.ui.hobgobelin

import org.graphstream.ui.swingViewer.BaseView
import org.graphstream.ui.swingViewer.Viewer
import org.graphstream.ui.swingViewer.GraphRenderer
import javax.media.opengl.GLProfile
import javax.media.opengl.GLCapabilities
import javax.media.opengl.awt.GLCanvas
import javax.media.opengl.GLEventListener
import javax.media.opengl.GLAutoDrawable
import org.graphstream.ui.graphicGraph.GraphicGraph
import org.graphstream.ui.swingViewer.util.MouseManager
import java.awt.event.WindowListener
import javax.swing.JFrame
import java.awt.event.WindowEvent

/** A replacement of the DefaultView that provides an OpenGL context for rendering.
  * 
  * The JoglView provides a standard JOGL GLCanvas. It does not use NEWT since this is
  * not compatible with Swing. */
class JoglView extends BaseView with GLEventListener with WindowListener {
	/** The OpenGL profile. */
	protected var profile:GLProfile = null
		
	/** The (optional) frame. */
	protected var frame:JFrame = null;

	/** True if the window is iconified, we can stop rendering. */
	protected var isIconified:Boolean = false;
	
	/** The rendering surface. */
	protected var canvas:GLCanvas = null
	
	override def open(identifier:String, viewer:Viewer, renderer:GraphRenderer) {
		sys.props += "sun.java2d.opengl" -> "false"
		sys.props += "sun.java2d.noddraw" -> "true"

		if(!(renderer.isInstanceOf[JoglGraphRenderer])) {
			throw new RuntimeException("Cannot create a JoglView with a graph renderer that does not implement JoglGraphRenderer (%s)".format(renderer.getClass.getName))
		}
		
		this.profile = GLProfile.getGL2ES2	// Try to be android/iPhone compatible.   
		val caps = new GLCapabilities(profile)

		caps.setDoubleBuffered(true)
		caps.setRedBits(8)
		caps.setGreenBits(8)
		caps.setBlueBits(8)
		caps.setAlphaBits(8)
		
		this.canvas = new GLCanvas(caps)

		canvas.addGLEventListener(this)
		super.open(identifier, viewer, renderer)
	}
	
	override def display(graph:GraphicGraph, graphChanged:Boolean) {
		canvas.repaint//display
	}
	
	override def resizeFrame(width:Int, height:Int) {
		if(frame ne null) {
			frame.setSize(width, height)
		}
	}

	override def openInAFrame(on:Boolean) {
		if (on) {
			if (frame eq null) {
				frame = new JFrame("GraphStream")
				frame.setLayout(new java.awt.BorderLayout)
				frame.add(canvas, java.awt.BorderLayout.CENTER);
				frame.setSize(800, 600);
				frame.setVisible(true);
				frame.addWindowListener(this);
				if(shortcuts != null)
					shortcuts.installedInAWTComponent(frame);
				checkInitialAttributes();
			} else {
				frame.setVisible(true);
			}
		} else {
			if (frame ne null) {
				frame.removeWindowListener(this);
				if(shortcuts != null)
					shortcuts.removedFromAWTComponent(frame);
				frame.remove(canvas);
				frame.setVisible(false);
				frame.dispose();
				frame = null;
			}
		}
	}
	
	override def isAWT() = true
	
	override def getAWTComponent():java.awt.Component = canvas
	
	override def getGUIComponent():AnyRef = canvas
	
	//-------------------------------------------------------------------------
	// GLEventListener

	protected var glInited = false
	
	def reshape(drawable:GLAutoDrawable, x:Int, y:Int, width:Int, height:Int) {
		renderer.asInstanceOf[JoglGraphRenderer].reshape(x, y, width, height)
	}

	def init(drawable:GLAutoDrawable) {
		glInited = true
		renderer.asInstanceOf[JoglGraphRenderer].init(drawable)
	}

	def dispose(drawable:GLAutoDrawable) {
		glInited = false
	}

	def display(drawable:GLAutoDrawable) {
		if(glInited && (!isIconified)) {
			renderer.render(null, canvas.getX, canvas.getY, canvas.getWidth, canvas.getHeight)
			drawable.swapBuffers
		}
	}
	
	//--------------------------------------------------------------------------
	// WindowListener

	override def windowActivated(e:WindowEvent) {}

	override def windowClosed(e:WindowEvent) {}

	override def windowClosing(e:WindowEvent) {
		graph.addAttribute("ui.viewClosed", getId)

		viewer.getCloseFramePolicy match {
			case Viewer.CloseFramePolicy.CLOSE_VIEWER => viewer.removeView(getId)
			case Viewer.CloseFramePolicy.HIDE_ONLY    => if (frame ne null) frame.setVisible(false)
			case Viewer.CloseFramePolicy.EXIT         => sys.exit(0)
			case _                                    => throw new RuntimeException(
					"The %s view is not up to date, do not know %s CloseFramePolicy.".format(
							getClass.getName, viewer.getCloseFramePolicy))
		}
	}

	override def windowDeactivated(e:WindowEvent) {}

	override def windowDeiconified(e:WindowEvent) { isIconified = false }

	override def windowIconified(e:WindowEvent) { isIconified = true }

	override def windowOpened(e:WindowEvent) { graph.removeAttribute("ui.viewClosed") }

	//----------------------------------------------------------------------------
	// Methods deferred to the renderer

	override def setFrameTitle(title:String) {
		if(frame ne null)
			frame.setTitle(title)
	}
}