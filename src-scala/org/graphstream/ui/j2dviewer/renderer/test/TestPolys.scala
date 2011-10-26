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