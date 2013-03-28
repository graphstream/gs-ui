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
import org.graphstream.algorithm.randomWalk.RandomWalk
import scala.collection.JavaConversions._
import org.graphstream.graph.Edge
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator
import org.graphstream.graph.Graph

object TestRandomWalk {
	def main(args:Array[String]) {
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
		(new TestRandomWalk).test
	}
}

class TestRandomWalk {
    def test() {
    	val graph = new MultiGraph("random walk")
    	val gen   = new DorogovtsevMendesGenerator
    	val rwalk = new RandomWalk
    	
    	gen.addSink(graph)
    	gen.begin
    	for(i <- 0 until 400) {
    		gen.nextEvents
    	}
    	gen.end
    	
    	graph.addAttribute("ui.stylesheet", styleSheet)
    	graph.addAttribute("ui.quality")
    	graph.addAttribute("ui.antialias")
    	graph.display()
    	
    	rwalk.setEntityCount(graph.getNodeCount*2)
    	rwalk.setEvaporation(0.97)
    	rwalk.setEntityMemory(40)
    	rwalk.init(graph)
    	for(i <- 0 until 3000) {
    	    rwalk.compute
    	    if(i%100==0){
    	        Console.err.println("step %d".format(i))
    	    	updateGraph(graph, rwalk)
    	    }
    	//    Thread.sleep(100)
    	}
    	rwalk.terminate
    	updateGraph(graph, rwalk)
    	graph.addAttribute("ui.screenshot", "randomWalk.png")
    }
    
    def updateGraph(graph:Graph, rwalk:RandomWalk) {
        var mine = Double.MaxValue
    	var maxe = Double.MinValue
    	
    	graph.getEachEdge.foreach { edge:Edge =>
    	    val passes = rwalk.getPasses(edge)
    	    if(passes>maxe) maxe = passes
    	    if(passes<mine) mine = passes
    	}
    	
    	graph.getEachEdge.foreach { edge:Edge =>
    	    val passes = rwalk.getPasses(edge)
    	    val color  = ((passes-mine)/(maxe-mine));
    		edge.setAttribute("ui.color", color.asInstanceOf[AnyRef])
    	}
    }
    
    val styleSheet = """
    		edge {
    			size: 2px;
    			fill-color: red, yellow, green, #444;
    			fill-mode: dyn-plain;
    		}
    		node {
    			size: 6px;
    			fill-color: #444;
    		}
    	"""
}