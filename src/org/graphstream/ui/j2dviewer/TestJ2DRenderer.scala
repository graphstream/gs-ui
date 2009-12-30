package org.graphstream.ui.j2dviewer

import org.graphstream.graph.Graph
import org.graphstream.scalags.graph.MultiGraph

import org.graphstream.ui2.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}

import org.graphstream.ScalaGS._

object TestJ2DRenderer {
	def main( args:Array[String] ) {
		val test = new Test
		test.run( args )
	}
}

private class Test extends ViewerListener {
	private[this] var loop = true
  
	def run( args:Array[String] ) {
		val graph  = new MultiGraph( "g1" )
		val viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD )
		val pipeIn = viewer.newViewerPipe
		val view   = viewer.addView( "view1", new J2DGraphRenderer )
  
		pipeIn.addAttributeSink( graph )
		pipeIn.addViewerListener( this )
		pipeIn.pump
  
		val A = graph.addNode( "A" )
		val B = graph.addNode( "B" )
		val C = graph.addNode( "C" )
		graph.addEdges( "A", "B", "C", "A" )
		graph.addAttribute( "ui.stylesheet", styleSheet )
  
		A("xyz") = ( -1, 0, 0 )
		B("xyz") = (  1, 0, 0 )
		C("xyz") = (  0, 1, 0 )
 
		while( loop ) {
			pipeIn.pump
			sleep( 10 )
		}
		
		printf( "bye bye" )
		exit
	}
 
	protected def sleep( ms:Long ) { Thread.sleep( ms ) }

// Viewer Listener Interface
 
	def viewClosed( id:String ) { loop = false }
 
 	def buttonPushed( id:String ) {}
  
 	def buttonReleased( id:String ) {} 
 
// Data
   
	private val styleSheet:String = """
			graph { fill-mode: gradient-radial; fill-color: white, gray; } 
			node { fill-color:red; stroke-color:blue; shadow-mode:plain; }
			edge { fill-color:green; shadow-mode:plain; }
		""";
}