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
		val D = graph.addNode( "D" )
		val E = graph.addNode( "E" )
		graph.addAttribute( "ui.stylesheet", styleSheet )
		graph.addAttribute( "ui.antialias" )
  
		A("xyz") = ( -1, 0, 0 )
		B("xyz") = (  1, 0, 0 )
		C("xyz") = (  0, 1, 0 )
		D("xyz") = (  0,0.5,0 )
		E("xyz") = (0.5, 0, 0 )
  
		A("label") = "A"
		B("label") = "B"
		C("label") = "C"
		D("label") = "D"
		E("label") = "E"
 
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
			graph {
 				fill-mode: gradient-radial;
 				fill-color: white, gray;
 				padding: 40px;
 			} 
			node {
				size: 60px, 25px;
				fill-mode: gradient-vertical;
				fill-color: white, rgb(200,200,200);
				stroke-mode: plain; 
				stroke-color: rgba(255,255,0,255);
				stroke-width: 2px;
				shadow-mode: plain;
				shadow-width: 0px;
				shadow-offset: 3px, -3px;
				shadow-color: rgba(0,0,0,100);
				icon-mode: at-left;
				icon: url('file:///home/antoine/GSLogo11d24.png');
			}
			node:clicked {
				stroke-mode: plain;
				stroke-color: red;
			}
			edge {
				fill-color:green;
				shadow-mode:plain;
			}
		""";
}