/*
 * Copyright 2006 - 2011 
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