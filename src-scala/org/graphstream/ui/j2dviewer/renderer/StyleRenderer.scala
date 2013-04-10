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

import org.graphstream.graph.Element
import org.graphstream.ui.graphicGraph.{GraphicElement, GraphicNode, GraphicEdge, GraphicSprite, GraphicGraph, StyleGroup}
import org.graphstream.ui.graphicGraph.GraphicElement.SwingElementRenderer
import org.graphstream.ui.graphicGraph.StyleGroup.ElementEvents

import org.graphstream.ui.j2dviewer.{Backend, Camera, J2DGraphRenderer}

import scala.collection.JavaConversions._

/** Style renderer companion object, acts as a factory for renderers.  */
object StyleRenderer {
	import org.graphstream.ui.graphicGraph.stylesheet.Selector.Type._
	def apply(style:StyleGroup, mainRenderer:J2DGraphRenderer):StyleRenderer = {
		style.getType match {
		  case NODE   => NodeRenderer(style, mainRenderer) 
		  case EDGE   => EdgeRenderer(style, mainRenderer)
		  case SPRITE => SpriteRenderer(style, mainRenderer)
		  case GRAPH  => printf("we got a graph%n"); null
		  case _      => throw new RuntimeException("WTF?")
		}
	}
}

/** Base class for style renderers. 
  * 
  * The main rendering loop is defined here.
  */
abstract class StyleRenderer(val group:StyleGroup) extends GraphicElement.SwingElementRenderer {
// Attribute
	
    /** True if some event is taking place actually. */
	protected var hadEvents:Boolean = false;
	
// Command
	
 	/** Render the shadow of all (visible) elements of the group. */
	def renderShadow(bck:Backend, camera:Camera) { render(bck, camera, true, renderShadow _) }
 
	/** Render all the (visible) elements of the group. */
	def render(bck:Backend, camera:Camera) { render(bck, camera, false, renderElement _) }
 
	/** Main rendering method.
      * 
      * This works in three phases:
      * - draw all "bulk" elements using renderElement()
      * - draw all "dynamic" elements using renderElement().
      * - draw all "event" elements using renderElement().
      * 
      * Before drawing, the setupRenderingPass() and pushStyle() methods are called. The phase 1 is
      * run. Then for each dynamic element in phase 2, before calling renderElement, for each element
      * the pushDynStyle() method is called.
      * Then for each element modified by an event, in phase 3, the before drawing the element, the
      * event is enabled, then pushStyle() is called, then the element is drawn, and finally the
      * event is disabled.
      * 
      * This rendering pass is made both for shadows and for regular drawing. The shadow and render
      * arguments allow to specify that we are rendering for shadow, and what element rendering
      * method to use (renderElement() or renderShadow()). */
	protected def render(bck:Backend, camera:Camera, shadow:Boolean, render:(Backend,Camera,GraphicElement)=>Unit ) {
		setupRenderingPass(bck, camera, shadow)
		pushStyle(bck, camera, shadow)

//var T1 = System.currentTimeMillis
		group.bulkElements.foreach { e =>
			val ge = e.asInstanceOf[GraphicElement]
	
			if(camera.isVisible(ge))
			     render(bck, camera, ge)
			else elementInvisible(bck, camera, ge);
		}
//var T2 = System.currentTimeMillis
			
		if(group.hasDynamicElements) {
			group.dynamicElements.foreach { e =>
				val ge = e.asInstanceOf[GraphicElement]

				if(camera.isVisible(ge)) {
					if(! group.elementHasEvents(ge)) {
						pushDynStyle(bck, camera, ge)
						render(bck, camera, ge)
					}
				} else {
					elementInvisible(bck, camera, ge)
				}
			}
		}

		if(group.hasEventElements) {
			group.elementsEvents.foreach { event =>
				val ge = event.getElement.asInstanceOf[GraphicElement]
				
				if(camera.isVisible(ge)) {
					event.activate
					pushStyle(bck, camera, shadow)
					render(bck, camera, ge)
					event.deactivate
				} else {
					elementInvisible(bck, camera, ge)
				}
			}
			
			hadEvents = true;
		}
		else
		{
			hadEvents = false;
		}
  
		endRenderingPass(bck, camera, shadow)
//System.err.println("[%s] %d".format(group.getId, (T2-T1)));
    }

// Methods to implement in each renderer
 
	/** Called before the whole rendering pass for all elements.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param forShadow true if we are in the shadow rendering pass. */
	protected def setupRenderingPass(bck:Backend, camera:Camera, forShadow:Boolean)
	
	/** Called before the rendering of bulk and event elements.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param forShadow true if we are in the shadow rendering pass. */
	protected def pushStyle(bck:Backend, camera:Camera, forShadow:Boolean)
	
	/** Called before the rendering of elements on dynamic styles. This must only change the style
	  * properties that can change dynamically.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param element The graphic element concerned by the dynamic style change. */
	protected def pushDynStyle(bck:Backend, camera:Camera, element:GraphicElement)
	
	/** Render a single element knowing the style is already prepared. Elements that are not visible
	  * are not drawn.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param element The element to render. */
	protected def renderElement(bck:Backend, camera:Camera, element:GraphicElement)
	
 	/** Render the shadow of the element.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param element The element to render. */
	protected def renderShadow(bck:Backend, camera:Camera, element:GraphicElement)
 
	/** Called during rendering in place of {@link #renderElement(Graphics2D, Camera, GraphicElement)}
	  * to signal that the given element is not inside the view. The renderElement() method will be
	  * called as soon as the element becomes visible anew.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param element The element to render. */
	protected def elementInvisible(bck:Backend, camera:Camera, element:GraphicElement)
 
	/** Called at the end of the rendering pass. 
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param forShadow true if we are in the shadow rendering pass. */
	protected def endRenderingPass(bck:Backend, camera:Camera, forShadow:Boolean) {
		// NOP by default.
	}
}