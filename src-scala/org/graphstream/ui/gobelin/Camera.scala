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

import org.graphstream.ui.geom.Point3
import org.graphstream.ui.swingViewer.util.GraphMetrics
import org.graphstream.ui.graphicGraph.GraphicGraph
import org.graphstream.ui.graphicGraph.GraphicNode
import org.graphstream.ui.graphicGraph.GraphicSprite
import org.graphstream.ui.graphicGraph.GraphicElement

object Camera {
}

/** Implementation of the camera for the Gobelin renderer.
  *
  * In this renderer, the camera contains a back-end that is in charge of changing the view 
  * for the graphic engine in use.
  * 
  * It is also in charge of the node and sprite visibility, and uses their skeletons to test it.
  */
abstract class Camera(val backend:Backend) extends org.graphstream.ui.swingViewer.BaseCamera {
    /** Save the state of the rendering back-end. */
    def pushView(graph:GraphicGraph)
    
    /** Setup the view so that the whole graph is visible. */ 
    def autoFitView()
    
    /** Setup the view according to the camera settings, as specified by the user. */
    def userView()
    
    /** Restore the state of the rendering back-end. */
    def popView()
    
    /** Is the given node visible in the specified area ? */
    protected def isNodeVisibleIn(node:GraphicNode, X1:Double, Y1:Double, X2:Double, Y2:Double):Boolean
    
    /** Is the given sprite visible in the specified area ? */
    protected def isSpriteVisibleIn(sprite:GraphicSprite, X1:Double, Y1:Double, X2:Double, Y2:Double):Boolean
    
    /** Is the given point inside the area of the given node ? */
    protected def nodeContains(elt:GraphicElement, x:Double, y:Double):Boolean
    
    /** Is the given point inside the area of the given sprite ? */
    protected def spriteContains(elt:GraphicElement, x:Double, y:Double):Boolean
}