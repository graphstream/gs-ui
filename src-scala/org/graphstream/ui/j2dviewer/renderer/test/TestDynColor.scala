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