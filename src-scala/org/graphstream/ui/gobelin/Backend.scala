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

import java.awt.Container
import org.graphstream.ui.graphicGraph.GraphicGraph.SkeletonFactory
import org.graphstream.ui.geom.Point3
import java.awt.Graphics2D

/**
 * The graphic driver.
 * 
 * The back-end can be for example Java2D or OpenGL.
 */
abstract class Backend extends SkeletonFactory {
    /** Called before any prior use of this back-end. */
    def open(drawingSurface:Container)
    
    /** Called after finished using this object. */
    def close()
    
    /** Setup the back-end for a new rendering session. */
    def startFrame(g2:Graphics2D)
    
    /** Transform a point in graph units into pixel units.
      * @return the transformed point. */
    def transform(x:Double, y:Double, z:Double):Point3
    
    /** Pass a point in transformed coordinates (pixels) into the reverse transform (into
      * graph units).
      * @return the transformed point. */
    def inverseTransform(x:Double, y:Double, z:Double):Point3
    
    /** Transform a point in graph units into pixel units, the given point is transformed in place
      * and also returned. */
    def transform(p:Point3):Point3
    
    /** Transform a point in pixel units into graph units, the given point is transformed in
      * place and also returned. */
    def inverseTransform(p:Point3):Point3
    
    /** Push the actual transformation on the matrix stack, installing
      * a copy of it on the top of the stack. */
    def pushTransform()
 
    /** Begin the work on the actual transformation matrix. */
    def beginTransform

    /** Make the top-most matrix as an identity matrix. */
    def setIdentity()
    
    /** Multiply the to-most matrix by a translation matrix. */
    def translate(tx:Double, ty:Double, tz:Double)
    
    /** Multiply the top-most matrix by a rotation matrix. */
    def rotate(angle:Double, ax:Double, ay:Double, az:Double)
    
    /** Multiply the top-most matrix by a scaling matrix. */
    def scale(sx:Double, sy:Double, sz:Double)
    
    /** End the work on the actual transformation matrix, installing it as the actual modelview
      * matrix. If you do not call this method, all the scaling, translation and rotation are
      * lost. */
    def endTransform
    
    /** Pop the actual transformation of the matrix stack, restoring
      * the previous one in the stack. */
    def popTransform()

    def antialias:Boolean
    
    def quality:Boolean
    
    /** Enable or disable anti-aliasing. */
    def antialias_=(on:Boolean)

    /** Enable or disable the hi-quality mode. */
    def quality_=(on:Boolean)

    /** The graphic engine, Java2D graphics, or the OpenGL context or any data related to the back-end. */
    def engine:AnyRef
    
    /** The drawing surface.
      * 
      * The drawing surface may be different than the one passed as
      * argument to open(), the back-end is free to create a new surface
      * as it sees fit. */
    def drawingSurface():Container
    
    /** Create a camera appropriate for this backend. */
    def chooseCamera():Camera
    
    /** Create a selection renderer appropriate for this back-end. */
    def chooseSelectionRenderer():SelectionRenderer
}