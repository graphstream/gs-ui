package org.graphstream.ui.hobgobelin.test

import org.graphstream.graph.implementations.SingleGraph

object TestBasic {
	def main(args:Array[String]):Unit = {
		(new TestBasic()).test
	}
}

class TestBasic {
	def test {
		System.getProperties.setProperty("ui.viewer.view", "org.graphstream.ui.hobgobelin.JoglView")
		System.getProperties.setProperty("ui.viewer.renderer", "org.graphstream.ui.hobgobelin.HobgobelinRenderer")
		
		val graph = new SingleGraph("test")
		
		graph.display
		graph.addNode("A")
		graph.addNode("B")
		graph.addEdge("AB", "A", "B")
	}
}