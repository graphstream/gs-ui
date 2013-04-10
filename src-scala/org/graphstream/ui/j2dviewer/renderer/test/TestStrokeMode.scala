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
import scala.collection.JavaConversions._
import org.graphstream.graph.Node

object TestStrokeMode {
    def main(args:Array[String]) {
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
        (new TestStrokeMode).test
    }

    private val styleSheet = """
    		node {
    			fill-color: white;
    			fill-mode: plain;
    			stroke-mode: dashes;
    			stroke-width: 1px;
    			stroke-color: red;
    			size: 20px;
    		}
    		node#C {
    			stroke-mode: double;
    		}
    		edge {
    			fill-mode: none;
    			size: 0px;
    			stroke-mode: dashes;
    			stroke-width: 1px;
    			stroke-color: red;
    		}
    	"""
}

class TestStrokeMode {
	def test() {
	    val graph = new MultiGraph("stroke")

	    graph.addAttribute("ui.quality")
	    graph.addAttribute("ui.antialias")
	    graph.addAttribute("ui.stylesheet", TestStrokeMode.styleSheet)
	    graph.display
	    graph.addNode("A")
	    graph.addNode("B")
	    graph.addNode("C")
	    graph.addEdge("AB", "A", "B")
	    graph.addEdge("BC", "B", "C")
	    graph.addEdge("CA", "C", "A")
	    graph.foreach { node:Node =>
	        node.setAttribute("ui.label", node.getId)
	    }
	}
}