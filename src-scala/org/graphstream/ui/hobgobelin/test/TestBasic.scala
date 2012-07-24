package org.graphstream.ui.hobgobelin.test

import org.graphstream.graph.implementations.SingleGraph

object TestBasic {
	def main(args:Array[String]):Unit = {
		(new TestBasic()).test
	}
}

class TestBasic {
	def test {
		sys.props += "gs.ui.view" -> "org.graphstream.ui.hobgobelin.JoglView"
		sys.props += "gs.ui.renderer" -> "org.graphstream.ui.hobgobelin.HobgobelinGraphRenderer"
		
		val graph = new SingleGraph("test")
		
		graph.display(false)
		graph.addNode("A")
		graph.addNode("B")
		graph.addEdge("AB", "A", "B")
	}
}