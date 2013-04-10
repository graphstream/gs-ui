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

import java.awt.geom.Rectangle2D
import java.awt.{Graphics2D, Color, BasicStroke}
import org.graphstream.ui.graphicGraph.GraphicGraph
import org.graphstream.ui.j2dviewer.{Camera, Backend}
import org.graphstream.ui.util.Selection

class SelectionRenderer(val selection:Selection, val graph:GraphicGraph) {
	protected val shape        = new Rectangle2D.Double
	protected val linesColor   = new Color(240, 240, 240)
	protected val linesColorQ  = new Color(  0,   0,   0, 64)
	protected val fillColor    = new Color( 50,  50, 200, 32)
	protected val strokeColorQ = new Color( 50,  50, 200, 64)
	protected val strokeColor  = new Color(128, 128, 128)
 
	/** Render the selection (in pixel units). */
	def render(bck:Backend, camera:Camera, panelWidth:Int, panelHeight:Int) {
	    // XXX
	    // TODO make this an abstract class whose implementation are create by the back-end
	    // XXX
		if(selection.active && selection.x1 != selection.x2 && selection.y1 != selection.y2) {
		    val g       = bck.graphics2D
			val quality = (graph.hasAttribute("ui.quality") || graph.hasAttribute("ui.antialias"))
			var x1      = selection.x1
			var y1      = selection.y1
			var x2      = selection.x2
			var y2      = selection.y2
			var t       = 0.0
			
			if(x1 > x2) { t = x1; x1 = x2; x2 = t }
			if(y1 > y2) { t = y1; y1 = y2; y2 = t }
   
			if(quality)
			     g.setColor(linesColorQ)
			else g.setColor(linesColor) 

			g.setStroke(new BasicStroke(1))
   
			g.drawLine(0, y1.toInt, panelWidth, y1.toInt)
			g.drawLine(0, y2.toInt, panelWidth, y2.toInt)
			g.drawLine(x1.toInt, 0, x1.toInt, panelHeight)
			g.drawLine(x2.toInt, 0, x2.toInt, panelHeight)
	
			shape.setFrame(x1, y1, x2-x1, y2-y1)
			
			if(quality) {
				g.setColor(fillColor)
				g.fill(shape)
				g.setColor(strokeColorQ)
			} else {
				g.setColor(strokeColor)
			}
   
			g.draw(shape)
		}
	}
}