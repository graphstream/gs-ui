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
package org.graphstream.ui.gobelin.java2d

import org.graphstream.ui.gobelin.Backend
import java.awt.Container
import java.awt.Graphics2D
import scala.collection.mutable.ArrayStack
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.RenderingHints
import org.graphstream.ui.geom.Point3

/** A full Java-2D rendering back-end. */
class BackendJava2D extends Backend {

    protected var surface:Container = null
    
	protected var g2:Graphics2D = null
    
    protected val matrixStack = new ArrayStack[AffineTransform]()
    
    protected var Tx:AffineTransform = null
    
    protected var xT:AffineTransform = null
    
    protected val dummyPoint = new Point2D.Double()
    
    protected var antialiasOn = false
    
    protected var qualityOn = false
    
    def engine:AnyRef = g2
    
	def graphics2D:Graphics2D = g2

	def open(drawingSurface:Container) {
        surface = drawingSurface
    }
    
    def close() {
        surface = null
    }
	
    override def startFrame(engine:AnyRef) {
	    this.g2 = engine.asInstanceOf[Graphics2D]
        Tx = g2.getTransform
        matrixStack.clear
        matrixStack.push(Tx)
    }

	def beginTransform() {
	}
	
    def endTransform() {
        g2.setTransform(Tx)
        computeInverse
    }

    protected def computeInverse() {
        try {
        	xT = new AffineTransform(Tx)
        	xT.invert
        } catch {
            case _ => Console.err.println("Cannot inverse matrix")
        }
    }
    
    def transform(x:Double, y:Double, z:Double):Point3 = {
        dummyPoint.setLocation(x,y)
        Tx.transform(dummyPoint, dummyPoint)
        new Point3(dummyPoint.x, dummyPoint.y, 0)
    }
    
    def inverseTransform(x:Double, y:Double, z:Double):Point3 = {
        dummyPoint.setLocation(x, y)
        xT.transform(dummyPoint, dummyPoint)
        new Point3(dummyPoint.x, dummyPoint.y, 0)
    }
    
    def transform(p:Point3):Point3 = {
        dummyPoint.setLocation(p.x, p.y)
        Tx.transform(dummyPoint, dummyPoint)
        p.set(dummyPoint.x, dummyPoint.y, 0)
        p
    }
    
    def inverseTransform(p:Point3):Point3 = {
        dummyPoint.setLocation(p.x, p.y)
        xT.transform(dummyPoint, dummyPoint)
        p.set(dummyPoint.x, dummyPoint.y, 0)
        p
    }
    
    /** Push the actual transformation on the matrix stack, installing
      * a copy of it on the top of the stack. */
    def pushTransform() {
        val newTx = new AffineTransform(Tx)
        matrixStack.push(newTx)
        g2.setTransform(newTx)
        Tx = newTx
        // xT not changed, since newTx is a copy of Tx
    }
    
    /** Pop the actual transformation of the matrix stack, installing
      * the previous one in the stack. */
    def popTransform() {
        assert(!matrixStack.isEmpty)
        matrixStack.pop
        //Tx = matrixStack.top
        g2.setTransform(matrixStack.top)
        //computeInverse
    }
    
    def setIdentity() = Tx.setToIdentity
    
    def translate(tx:Double, ty:Double, tz:Double) = Tx.translate(tx, ty)
    
    def rotate(angle:Double, ax:Double, ay:Double, az:Double) = Tx.rotate(angle)
    
    def scale(sx:Double, sy:Double, sz:Double) = Tx.scale(sx, sy)
    
    def antialias:Boolean = antialiasOn
    
    def quality:Boolean = qualityOn
    
    def antialias_=(on:Boolean) {
       import RenderingHints._
       antialiasOn = on
	   if(on) {
		   g2.setRenderingHint(KEY_STROKE_CONTROL,    VALUE_STROKE_PURE)
		   g2.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON)
		   g2.setRenderingHint(KEY_ANTIALIASING,      VALUE_ANTIALIAS_ON)
	   } else {
		   g2.setRenderingHint(KEY_STROKE_CONTROL,    VALUE_STROKE_DEFAULT)
		   g2.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_OFF)
		   g2.setRenderingHint(KEY_ANTIALIASING,      VALUE_ANTIALIAS_OFF)
	   }
    }
    
    def qality_=(on:Boolean) {
       import RenderingHints._
       qualityOn = on
       if(on) {
		   g2.setRenderingHint(KEY_RENDERING,           VALUE_RENDER_QUALITY)
		   g2.setRenderingHint(KEY_INTERPOLATION,       VALUE_INTERPOLATION_BICUBIC)
		   g2.setRenderingHint(KEY_COLOR_RENDERING,     VALUE_COLOR_RENDER_QUALITY)
		   g2.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY)
	   } else {
		   g2.setRenderingHint(KEY_RENDERING,           VALUE_RENDER_SPEED)
		   g2.setRenderingHint(KEY_INTERPOLATION,       VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
		   g2.setRenderingHint(KEY_COLOR_RENDERING,     VALUE_COLOR_RENDER_SPEED)
		   g2.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_SPEED)
	   }
    }
       
    def drawingSurface():Container = surface
}