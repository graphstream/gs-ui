package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.graph.implementations.MultiGraph

object TestStrokeMode {
    def main(args:Array[String]) {
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
        (new TestStrokeMode).test
    }

    private val styleSheet = """
    		node {
    			fill-color: white;
    			fill-mode: plain;
    			stroke-mode: dots;
    			stroke-width: 1px;
    			stroke-color: red;
    			size: 20px;
    		}
    		edge {
    			fill-mode: none;
    			size: 0px;
    			stroke-mode: dots;
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
	}
}