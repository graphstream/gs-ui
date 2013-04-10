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

import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.graph.Node
import org.graphstream.graph.Edge

object TestPolys {
	def main(args:Array[String]) {
	    System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
	    (new TestPolys).test
	}
}

class TestPolys {
    def test() {
        val graph = new SingleGraph("Polys")
        
        val A:Node = graph.addNode("A")
        val B:Node = graph.addNode("B")
        val C:Node = graph.addNode("C")
        val D:Node = graph.addNode("D")
        
        A.addAttribute("xyz", Array[Double](  1,  1, 0))
        B.addAttribute("xyz", Array[Double](  1, -1, 0))
        C.addAttribute("xyz", Array[Double]( -1, -1, 0))
        D.addAttribute("xyz", Array[Double]( -1,  1, 0))
        
        A.addAttribute("ui.label", "A")
        B.addAttribute("ui.label", "B")
        C.addAttribute("ui.label", "C")
        D.addAttribute("ui.label", "D")
        
        val AB:Edge = graph.addEdge("AB", "A", "B")
        val BC:Edge = graph.addEdge("BC", "B", "C")
        val CD:Edge = graph.addEdge("CD", "C", "D")
        val DA:Edge = graph.addEdge("DA", "D", "A")
        
        AB.addAttribute("ui.points", Array[Double](1, 1, 0,
                                                   1.25, 0.5, 0,
                                                   0.75, -0.5, 0,
                                                   1, -1, 0))
        BC.addAttribute("ui.points", Array[Double](1, -1, 0,
                                                   0.5, -0.5, 0,
                                                   -0.5, -0.25, 0,
                                                   -1, -1, 0))
        CD.addAttribute("ui.points", Array[Double](-1, -1, 0,
                                                   -0.40, -0.5, 0,
                                                   -1.70, 0.5, 0,
                                                   -1, 1, 0))
//        DA.addAttribute("ui.points", Array[Double](-1, 1, 0,
//                                                   -0.5, 0.75, 0,
//                                                   0.5, 0.25, 0,
//                                                   1, 1, 0))
        
        graph.addAttribute("ui.stylesheet", styleSheet)
        graph.addAttribute("ui.antialias")
        graph.display(false)
    }
    
    val styleSheet = """
        edge { shape: cubic-curve; }
        """
}