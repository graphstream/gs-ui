package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D

import org.graphstream.graph.Element
import org.graphstream.ui.graphicGraph.{GraphicElement, GraphicNode, GraphicEdge, GraphicSprite, GraphicGraph, StyleGroup}
import org.graphstream.ui.graphicGraph.GraphicElement.SwingElementRenderer
import org.graphstream.ui.graphicGraph.StyleGroup.ElementEvents

import org.graphstream.ui.j2dviewer.{Camera, J2DGraphRenderer}
import org.graphstream.ScalaGS._

import scala.collection.JavaConversions._

abstract class StyleRenderer( val group:StyleGroup ) extends GraphicElement.SwingElementRenderer {
// Attribute
	
	protected var hadEvents:Boolean = false;
	
// Command
	
 	/**
 	 * Render the shadow of all (visible) elements of the group.  
     */
   def renderShadow( g:Graphics2D, camera:Camera ) {
     	render( g, camera, true, renderShadow _ )
   }
 
	/**
	 * Render all the (visible) elements of the group.
	 */
	def render( g:Graphics2D, camera:Camera ) {
		render( g, camera, false, renderElement _ )
	}
 
	/**
     * Main rendering method.
     * 
     * <p>
     * This works in three phases:
     * <ol>
     * 		<li>draw all "bulk" elements using renderElement()</li>
     * 		<li>draw all "dynamic" elments using renderElement().</li>
     * 		<li>draw all "event" elements using renderElement().</li>
     * </ol>
     * Before drawing, the setupRenderingPass() and pushStyle() methods are called. The phase 1 is
     * run. Then for each dynamic element in phase 2, before calling renderElement, for each element
     * the pushDynStyle() method is called.
     * Then for each element modified by an event, in phase 3, the before drawing the element, the
     * event is activated, then pushStyle() is called, then the element is drawn, and finally the
     * event is deactivated.
     * </p>
     * 
     * <p>
     * This rendering pass is made both for shadows and for regular drawing. The shadow and render
     * arguments allow to specify that we are rendering for shadow, and what element rendering
     * method to use (renderElement() or renderShadow()).
     * </p>
	 */
	protected def render( g:Graphics2D, camera:Camera, shadow:Boolean, render:(Graphics2D,Camera,GraphicElement)=>Unit ) {
		setupRenderingPass( g, camera, shadow )
		pushStyle( g, camera, shadow )

		group.bulkElements.foreach { e =>
			val ge = e.asInstanceOf[GraphicElement]
	
			if( camera.isVisible( ge ) )
			     render( g, camera, ge )
			else elementInvisible( g, camera, ge );
		}
			
		if( group.hasDynamicElements ) {
			group.dynamicElements.foreach { e =>
				val ge = e.asInstanceOf[GraphicElement];
					
				if( camera.isVisible( ge ) ) {
					if( ! group.elementHasEvents( ge ) ) {
						pushDynStyle( g, camera, ge )
						render( g, camera, ge )
					}
				} else {
					elementInvisible( g, camera, ge )
				}
			}
		}
			
		if( group.hasEventElements() ) {
			group.elementsEvents.foreach { event =>
				val ge = event.getElement.asInstanceOf[GraphicElement]
				
				if( camera.isVisible( ge ) ) {
					event.activate()
					pushStyle( g, camera, shadow )
					render( g, camera, ge )
					event.deactivate()
				} else {
					elementInvisible( g, camera, ge )
				}
			}
			
			hadEvents = true;
		}
		else
		{
			hadEvents = false;
		}
  
		endRenderingPass( g, camera, shadow )
    }

// Methods to implement in each renderer
 
	/**
	 * Called before the whole rendering pass for all elements.
	 * @param g The Swing graphics.
	 * @param camera The camera.
	 */
	protected def setupRenderingPass( g:Graphics2D, camera:Camera, forShadow:Boolean )
	
	/**
	 * Called before the rendering of bulk and event elements.
	 * @param g The Swing graphics.
	 * @param camera The camera.
	 */
	protected def pushStyle( g:Graphics2D, camera:Camera, forShadow:Boolean )
	
	/**
	 * Called before the rendering of elements on dynamic styles. This must only change the style
	 * properties that can change dynamically.
	 * @param g The Swing graphics.
	 * @param camera The camera.
	 * @param element The graphic element concerned by the dynamic style change.
	 */
	protected def pushDynStyle( g:Graphics2D, camera:Camera, element:GraphicElement )
	
	/**
	 * Render a single element knowing the style is already prepared. Elements that are not visible
	 * are not drawn.
	 * @param g The Swing graphics.
	 * @param camera The camera.
	 * @param element The element to render.
	 */
	protected def renderElement( g:Graphics2D, camera:Camera, element:GraphicElement )
	
 	/**
     * Render the shadow of the element. 
	 * @param g The Swing graphics.
	 * @param camera The camera.
	 * @param element The element to render.
     */
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement )
 
	/**
	 * Called during rendering in place of {@link #renderElement(Graphics2D, Camera, GraphicElement)}
	 * to signal that the given element is not inside the view. The renderElement() method will be
	 * called as soon as the element becomes visible anew.
	 * @param g The Swing graphics.
	 * @param camera The camera.
	 * @param element The element to render.
	 */
	protected def elementInvisible( g:Graphics2D, camera:Camera, element:GraphicElement )
 
	/**
	 * Called at the end of the rendering pass. 
	 * @param g The Swing graphics.
	 * @param camera The camera.
     */
	protected def endRenderingPass( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
		// NOP by default.
	}
}

/**
 * Style renderer companion object, acts as a factory for renderers. 
 */
object StyleRenderer {
	import org.graphstream.ui.graphicGraph.stylesheet.Selector.Type._
	def apply( style:StyleGroup, mainRenderer:J2DGraphRenderer ):StyleRenderer = {
		style.getType match {
		  case NODE   => NodeRenderer( style, mainRenderer ) 
		  case EDGE   => EdgeRenderer( style, mainRenderer )
		  case SPRITE => SpriteRenderer( style, mainRenderer )
		  case GRAPH  => printf( "we got a graph%n" ); null
		  case _      => throw new RuntimeException( "WTF?" )
		}
	}
}