/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
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
package org.graphstream.ui.j2dviewer.renderer

import org.graphstream.ui.util.AttributeUtils
import org.graphstream.ui.geom.Vector3
import org.graphstream.ui.swingViewer.util.CubicCurve
import org.graphstream.ui.geom.Point3
import java.awt.Graphics2D
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph.{GraphicElement, GraphicNode, StyleGroup}
import org.graphstream.ui.j2dviewer.{Camera, J2DGraphRenderer, Backend}
import org.graphstream.ui.j2dviewer.renderer.shape._
import org.graphstream.ui.util.EdgePoints

/** Renderer for nodes.
  * This is a completely generic part, all is done in the various shapes. */
class NodeRenderer(styleGroup:StyleGroup) extends StyleRenderer(styleGroup) {
	protected var shape:Shape = null
 
	protected def setupRenderingPass(bck:Backend, camera:Camera, forShadow:Boolean) {
		shape = bck.chooseNodeShape(shape, group)
	}
	
	protected def pushStyle(bck:Backend, camera:Camera, forShadow:Boolean) {
		shape.configureForGroup(bck, group, camera)
	}
	
	protected def pushDynStyle(bck:Backend, camera:Camera, element:GraphicElement) {
	}
	
	protected def renderElement(bck:Backend, camera:Camera, element:GraphicElement) {
		val skel = getOrSetAreaSkeleton(element)
		shape.configureForElement(bck, element, skel, camera)
		shape.render(bck, camera, element, skel)
	}
	
	protected def renderShadow(bck:Backend, camera:Camera, element:GraphicElement) {
		val skel = getOrSetAreaSkeleton(element)
		shape.configureForElement(bck, element, skel, camera)
		shape.renderShadow(bck, camera, element, skel)
	}
 
	protected def elementInvisible(bck:Backend, camera:Camera, element:GraphicElement) {
		// NOP
	}	
		
	/** Retrieve the area shared informations stored on the given node element.
	  * If such information is not yet present, add it to the element. 
	  * @param element The element to look for.
	  * @return The node information.
	  * @throws RuntimeException if the element is not a node. */
	protected def getOrSetAreaSkeleton(element:GraphicElement):AreaSkeleton = {
		if(element.isInstanceOf[GraphicNode]) {
			var skel = element.getAttribute(Skeleton.attributeName).asInstanceOf[AreaSkeleton]
			
			if(skel eq null) {
				skel = new AreaSkeleton
				element.setAttribute(Skeleton.attributeName, skel)
			}
			
			skel
		} else {
			throw new RuntimeException("Trying to get AreaSkeleton on non-area (node or sprite) ...")
		}
	}
}

object NodeRenderer {
	def apply(style:StyleGroup, mainRenderer:J2DGraphRenderer):StyleRenderer = {
		if(style.getShape == org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape.JCOMPONENT)
		     new JComponentRenderer(style, mainRenderer)
		else new NodeRenderer(style)
	}
}