package org.graphstream.ui.j2dviewer

import org.graphstream.graph._
import org.graphstream.scalags.graph.MultiGraph
import org.graphstream.ui.swingViewer.Viewer
import org.graphstream.ScalaGS._
import javax.swing._
import java.awt._

object AllSwingTest {
	def main( args:Array[String] ):Unit = {
		val test = new AllInSwing
		test.run
	}
}

class AllInSwing extends JFrame {
	def run {
		val graph  = new MultiGraph( "mg" )
		val viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD )
   
		graph.addNodes( "A", "B", "C" )
		graph.addEdges( "A", "B", "C", "A" )
		graph.addAttribute( "ui.antialias" )
		graph.addAttribute( "ui.quality" )
		graph.addAttribute( "ui.default.title", "All In Swing Test" )
		graph.addAttribute( "ui.stylesheet", styleSheet )
   
		graph.getNode[Node]("A")("xyz") = ( -1, 0, 0 )
		graph.getNode[Node]("B")("xyz") = (  1, 0, 0 )
  		graph.getNode[Node]("C")("xyz") = (  0, 1, 0 )
   
		add( viewer.addDefaultView( false ), BorderLayout.CENTER )
		setSize( 800, 600 )
		setVisible( true )
	}
  
	protected val styleSheet = """
			graph {
				padding: 60px;
			}
		""" 
}
