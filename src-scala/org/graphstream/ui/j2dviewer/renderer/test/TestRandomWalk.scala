package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.algorithm.randomWalk.RandomWalk
import scala.collection.JavaConversions._
import org.graphstream.graph.Edge
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator
import org.graphstream.graph.Graph

object TestRandomWalk {
	def main(args:Array[String]) {
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
    	    if(i%100==0) Console.err.println("step %d".format(i))
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