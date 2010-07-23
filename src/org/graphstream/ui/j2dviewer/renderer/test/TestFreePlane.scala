package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.graph.Graph
import org.graphstream.scalags.graph.MultiGraph

import org.graphstream.ui.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.spriteManager._
import org.graphstream.ui.j2dviewer._

import org.graphstream.ScalaGS._

object TestFreePlane {
	def main( args:Array[String] ) {
		val test = new TestFreePlane
		test.run( args )
	}
}

private class TestFreePlane extends ViewerListener {
	private[this] var loop = true
  
	def run( args:Array[String] ) = {
		val graph  = new MultiGraph( "g1" )
		val viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD )
		val pipeIn = viewer.newViewerPipe
		val view   = viewer.addView( "view1", new J2DGraphRenderer )
  
		pipeIn.addAttributeSink( graph )
		pipeIn.addViewerListener( this )
		pipeIn.pump

		graph.addAttribute( "ui.stylesheet", styleSheet )
		graph.addAttribute( "ui.antialias" )
		graph.addAttribute( "ui.quality" )
		
		val root = graph.addNode( "root" )
		val A    = graph.addNode( "A" )
		val B    = graph.addNode( "B" )
		val C    = graph.addNode( "C" )
		val D    = graph.addNode( "D" )
		val E    = graph.addNode( "E" )
		val F    = graph.addNode( "F" )
		val G    = graph.addNode( "G" )
		val H    = graph.addNode( "H" )

		graph.addEdge( "rA", "root", "A" )
		graph.addEdge( "rB", "root", "B" )
		graph.addEdge( "rC", "root", "C" )
		graph.addEdge( "rD", "root", "D" )
		graph.addEdge( "rE", "root", "E" )
		graph.addEdge( "AF", "A", "F" )
		graph.addEdge( "CG", "C", "G" )
		graph.addEdge( "DH", "D", "H" )
		
		root("xyz") = ( 0, 0, 0 )
		A("xyz")    = ( 1, 1, 0 )
		B("xyz")    = ( 1, 0, 0 )
		C("xyz")    = (-1, 1, 0 )
		D("xyz")    = (-1, 0, 0 )
		E("xyz")    = (-1,-1, 0 )
		F("xyz")    = ( 2, 1.2, 0 )
		G("xyz")    = (-2, 1.2, 0 )
		H("xyz")    = (-2,-.5, 0 )
		
		root("label") = "Idea"
		A("label")    = "Topic1"
		B("label")    = "Topic2"
		C("label")    = "Topic3"
		D("label")    = "Topic4"
		E("label")    = "Topic5"
		F("label")    = "SubTopic1"
		G("label")    = "SubTopic2"
		H("label")    = "SubTopic3"
		
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
 
 	def buttonPushed( id:String ) {
 		if( id == "quit" )
 			loop = false
 		else if( id == "A" )
 			print( "Button A pushed%n".format() )
 	}
  
 	def buttonReleased( id:String ) {} 
 
// Data
 	
	val styleSheet = """
			graph {
				canvas-color: white;
 				fill-mode: gradient-radial;
 				fill-color: white, #EEEEEE;
 				padding: 60px;
 			} 
			node {
				shape: freeplane;
				size: 90px, 25px;
				fill-mode: none;
				stroke-mode: plain; 
				stroke-color: grey;
				stroke-width: 3px;
				shadow-mode: none;
				icon-mode: at-left;
				text-style: normal;
				text-font: 'Droid Sans';
				icon: url('file:///home/antoine/Documents/Perso/Art/Icons/GSLogo11d24.png');
			}
			node:clicked {
				stroke-mode: plain;
				stroke-color: red;
			}
			node:selected {
				stroke-mode: plain;
				stroke-color: blue;
			}
			edge {
				shape: freeplane;
				size: 3px;
				fill-color: grey;
				fill-mode: plain;
				shadow-mode: none;
				shadow-color: rgba(0,0,0,100);
				shadow-offset: 3px, -3px;
				shadow-width: 0px;
				arrow-shape: arrow;
				arrow-size: 20px, 6px;
			}
			"""
}