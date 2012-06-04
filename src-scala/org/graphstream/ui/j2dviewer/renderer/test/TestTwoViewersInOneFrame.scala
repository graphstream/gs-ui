package org.graphstream.ui.j2dviewer.renderer.test

import java.awt.GridLayout
import javax.swing.JFrame

import _root_.org.graphstream.graph._
import _root_.org.graphstream.graph.implementations._
import _root_.org.graphstream.stream.thread._
import _root_.org.graphstream.ui.swingViewer._
import _root_.org.graphstream.ui.swingViewer.Viewer.ThreadingModel
import _root_.org.graphstream.algorithm.generator._

object TestTwoViewersInOneFrame {
	def main(args:Array[String]):Unit = {
		System.setProperty( "gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer" );
		(new TestTwoViewersInOneFrame).test
	}
}

class TestTwoViewersInOneFrame extends JFrame {

	def test() {
		val graph1 = new MultiGraph("g1")
		val graph2 = new MultiGraph("g2")
		val viewer1 = new Viewer(new ThreadProxyPipe(graph1))
		val viewer2 = new Viewer(new ThreadProxyPipe(graph2))

		graph1.addAttribute("ui.stylesheet", styleSheet1)
		graph2.addAttribute("ui.stylesheet", styleSheet2)
		viewer1.addView(new DefaultView(viewer1, "view1", Viewer.newGraphRenderer))
		viewer2.addView(new DefaultView(viewer2, "view2", Viewer.newGraphRenderer))
		viewer1.enableAutoLayout
		viewer2.enableAutoLayout

		val gen = new DorogovtsevMendesGenerator

		gen.addSink(graph1)
		gen.addSink(graph2)
		gen.begin
		for(i <- 0 until 100)
			gen.nextEvents
		gen.end

		gen.removeSink(graph1)
		gen.removeSink(graph2)
//		graph1.addNode("A")
//		graph1.addNode("B")
//		graph1.addNode("C")
//		graph1.addEdge("AB", "A", "B", true)
//		graph1.addEdge("BC", "B", "C", true)
//		graph1.addEdge("CA", "C", "A", true)
//		graph2.addNode("A")
//		graph2.addNode("B")
//		graph2.addNode("C")
//		graph2.addEdge("AB", "A", "B", true)
//		graph2.addEdge("BC", "B", "C", true)
//		graph2.addEdge("CA", "C", "A", true)
		
		setLayout(new GridLayout(1, 2))
		//add(new JButton("Button"))
		add(viewer1.getView("view1"))
		add(viewer2.getView("view2"))
		setSize(800, 600)
		setVisible(true)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
	}

	protected val styleSheet1 =
		"graph { padding: 40px; }" +
		"node { fill-color: red; stroke-mode: plain; stroke-color: black; }";
	
	protected val styleSheet2 =
		"graph { padding: 40px; }" +
		"node { fill-color: blue; stroke-mode: plain; stroke-color: black; }";
}