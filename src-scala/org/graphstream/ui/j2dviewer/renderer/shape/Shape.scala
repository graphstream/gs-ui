package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt._
import java.awt.geom._

import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.util._
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.sgeom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

/** Base for all shapes. */
abstract trait Shape {
// Command
    
 	/**
     * Configure as much as possible the graphics before painting several version of this shape
     * at different positions.
     * @param g The Java2D graphics.
     */
 	def configureForGroup( g:Graphics2D, style:Style, camera:Camera )
 
 	/**
 	 * Configure all the dynamic and per element settings.
 	 * Some configurations can only be done before painting the element, since they change for
 	 * each element.
 	 */
 	def configureForElement( g:Graphics2D, element:GraphicElement, info:ElementInfo, camera:Camera )
 	
 	/**
     * Must create the shape from informations given earlier, that is, resize it if needed and
     * position it, and do all the things that are specific to each element, and cannot be done
     * for the group of elements.
     * All the settings for position, size, shadow, etc. must have been made. Usually all the
     * "static" settings are already set in configure, therefore most often this method is only in
     * charge of changing  the shape position (and computing size if fitting it to the contents).
     * This method is made to be called inside the render() method, hence it is protected.
 	 */
 	protected def make( g:Graphics2D, camera:Camera )
 	
 	/**
 	 * Same as {@link #make(Camera)} for the shadow shape. The shadow shape may be moved and
 	 * resized compared to the original shape. This method is made to be called inside the
 	 * renderShadow() method, hence it is protected.
 	 */
  	protected def makeShadow( g:Graphics2D, camera:Camera )
  
  	/**
     * Render the shape for the given element.
     */
  	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo )
   
   	/**
     * Render the shape shadow for the given element. The shadow is rendered in a different pass
     * than usual rendering, therefore it is a separate method.
     */
   	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo )
}