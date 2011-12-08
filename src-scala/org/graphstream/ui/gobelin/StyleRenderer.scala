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

import org.graphstream.ui.graphicGraph.StyleGroup
import org.graphstream.ui.graphicGraph.GraphicGraph
import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.swingViewer.GraphRenderer
import org.graphstream.ui.swingViewer.basicRenderer.ElementRenderer

import scala.collection.JavaConversions._

/** Base renderer for style groups.
  * 
  * Such renderers are associated with each style group, and therefore are able to be configured
  * for each group, they change only if the style change. */
abstract class StyleRenderer(val group:StyleGroup) extends GraphicElement.GraphicElementRenderer {
// Attribute
	
    /** True if some event is taking place actually. */
	protected var hadEvents:Boolean = false;
	
// Command
	
 	/** Render the shadow of all (visible) elements of the group. */
	def renderShadow(camera:Camera) { render(camera, true, renderShadow _) }
 
	/** Render all the (visible) elements of the group. */
	def render(camera:Camera) { render(camera, false, renderElement _) }
 
// Internal Tambouille
	
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
	protected def render(camera:Camera, shadow:Boolean, render:(Camera, GraphicElement)=>Unit) {
		setupRenderingPass(camera, shadow)
		pushStyle(camera, shadow)

		group.bulkElements.foreach { e =>
			val ge = e.asInstanceOf[GraphicElement]
	
			if(camera.isVisible(ge))
			     render(camera, ge)
			else elementInvisible(camera, ge);
		}
			
		if(group.hasDynamicElements) {
			group.dynamicElements.foreach { e =>
				val ge = e.asInstanceOf[GraphicElement]

				if(camera.isVisible(ge)) {
					if(! group.elementHasEvents(ge)) {
						pushDynStyle(camera, ge)
						render(camera, ge)
					}
				} else {
					elementInvisible(camera, ge)
				}
			}
		}

		if(group.hasEventElements) {
			group.elementsEvents.foreach { event =>
				val ge = event.getElement.asInstanceOf[GraphicElement]
				
				if(camera.isVisible(ge)) {
					event.activate
					pushStyle(camera, shadow)
					render(camera, ge)
					event.deactivate
				} else {
					elementInvisible(camera, ge)
				}
			}
			
			hadEvents = true;
		}
		else
		{
			hadEvents = false;
		}
  
		endRenderingPass(camera, shadow)
    }

// Methods to implement in each renderer
 
	/** Called before the whole rendering pass for all elements.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param forShadow true if we are in the shadow rendering pass. */
	protected def setupRenderingPass(camera:Camera, forShadow:Boolean)
	
	/** Called before the rendering of bulk and event elements.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param forShadow true if we are in the shadow rendering pass. */
	protected def pushStyle(camera:Camera, forShadow:Boolean)
	
	/** Called before the rendering of elements on dynamic styles. This must only change the style
	  * properties that can change dynamically.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param element The graphic element concerned by the dynamic style change. */
	protected def pushDynStyle(camera:Camera, element:GraphicElement)
	
	/** Render a single element knowing the style is already prepared. Elements that are not visible
	  * are not drawn.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param element The element to render. */
	protected def renderElement(camera:Camera, element:GraphicElement)
	
 	/** Render the shadow of the element.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param element The element to render. */
	protected def renderShadow(camera:Camera, element:GraphicElement)
 
	/** Called during rendering in place of {@link #renderElement(Graphics2D, Camera, GraphicElement)}
	  * to signal that the given element is not inside the view. The renderElement() method will be
	  * called as soon as the element becomes visible anew.
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param element The element to render. */
	protected def elementInvisible(camera:Camera, element:GraphicElement) {
	    // NOP by default.
	}
 
	/** Called at the end of the rendering pass. 
	  * @param bck The rendering back-end.
	  * @param camera The camera.
	  * @param forShadow true if we are in the shadow rendering pass. */
	protected def endRenderingPass(camera:Camera, forShadow:Boolean) {
		// NOP by default.
	}	
}

/** Renderer for the graph style, which is not an Element. */ 
abstract class GraphStyleRenderer(val graph:GraphicGraph) extends GraphicElement.GraphicElementRenderer {
    /** Render the graph style.
      * @param camera The view point and settings.
      * @param width The rendering surface in pixels.
      */
	def render(camera:Camera, width:Int, height:Int)
}

/** Renderer for the selection. */
abstract class SelectionRenderer(val selection:Selection) {
    def render(camera:Camera)
}