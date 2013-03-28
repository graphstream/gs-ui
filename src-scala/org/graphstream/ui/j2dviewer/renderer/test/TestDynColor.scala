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
package org.graphstream.ui.j2dviewer.renderer.test
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.graph.Node
import java.awt.Color
import org.graphstream.graph.Edge

object TestDynColor {
	def main(args:Array[String]):Unit = {
	    System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
	    (new TestDynColor).test
	}
}

class TestDynColor {
	def test() {
	    val g = new MultiGraph("foo")
	    
	    g.addAttribute("ui.stylesheet", "node { fill-mode: dyn-plain; stroke-mode: plain; stroke-width: 1px; } edge { fill-mode: dyn-plain; }")
	    g.addNode("A"); g.addNode("B"); g.addNode("C")
	    g.addEdge("AB", "A", "B"); g.addEdge("BC", "B", "C"); g.addEdge("CA", "C", "A")
	    g.display()
	    g.getNode[Node]("A").addAttribute("ui.color", Color.RED)
	    g.getNode[Node]("B").addAttribute("ui.color", Color.GREEN)
	    g.getNode[Node]("C").addAttribute("ui.color", Color.BLUE)
	    g.getEdge[Edge]("AB").addAttribute("ui.color", Color.YELLOW)
	    g.getEdge[Edge]("BC").addAttribute("ui.color", Color.MAGENTA)
	    g.getEdge[Edge]("CA").addAttribute("ui.color", Color.CYAN)
	}
}