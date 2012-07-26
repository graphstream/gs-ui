package org.graphstream.ui.hobgobelin.test

import javax.swing.JPanel
import javax.media.opengl.GLProfile
import javax.media.opengl.GLCapabilities
import javax.media.opengl.awt.GLCanvas
import javax.swing.JFrame
import javax.media.opengl.GLEventListener
import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GL._
import java.awt.BorderLayout

object TestSwingAndJogl {
	def main(args:Array[String]):Unit = (new TestSwingAndJogl).test
}

class TestSwingAndJogl extends JFrame with GLEventListener {
	def test {
		val prof = GLProfile.getGL2ES2
		val caps = new GLCapabilities(prof)
		
		caps.setDoubleBuffered(true)
		
		val canvas = new GLCanvas(caps)
		
		canvas.addGLEventListener(this)
		add(canvas, BorderLayout.CENTER)
		setSize(800, 600)
		setVisible(true)
	}
	
	def init(win:GLAutoDrawable) {
        val gl = win.getGL.getGL2ES2; import gl._;
        
        glClearColor(0.7f, 0f, 0.9f, 0f)
        glClearDepth(1f)
        glEnable(GL_DEPTH_TEST)
        Console.err.printf("Inited%n");
    }
    
    def reshape(win:GLAutoDrawable, x:Int, y:Int, width:Int, height:Int) {
    	Console.err.printf("Reshape begins !%n")
        val gl = win.getGL.getGL2ES2; import gl._;
        
        glViewport(0, 0, width, height)
        Console.err.printf("Reshaped %d %d%n".format(width, height))
    }
    
    def display(win:GLAutoDrawable) {
        Console.err.printf("Display begins!%n")
        val gl = win.getGL.getGL2ES2; import gl._;
    
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
        
        // Your drawing code here.
        
        //win.swapBuffers
        Console.err.printf("Display%n")
    }
    
    def dispose(win:GLAutoDrawable) {
        val gl = win.getGL.getGL2ES2; import gl._;
        sys.exit
    }
}