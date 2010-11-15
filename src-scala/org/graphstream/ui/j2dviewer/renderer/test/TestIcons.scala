package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.graph._
import org.graphstream.scalags.graph.MultiGraph

import org.graphstream.ui.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.spriteManager._
import org.graphstream.ui.j2dviewer._

import org.graphstream.ScalaGS._

object TestIcons {
	def main( args:Array[String] ) { (new TestIcons).run( args ) }
}

private class TestIcons extends ViewerListener {
	private[this] var loop = true
  
	val icon1 = "data/surprise1.png"
	val icon2 = "data/surprise2.png"
	
	def run( args:Array[String] ) = {
		val graph  = new MultiGraph( "Icons ..." )
		val viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD )
		val pipeIn = viewer.newViewerPipe
		val view   = viewer.addView( "view1", new J2DGraphRenderer )
  
		pipeIn.addAttributeSink( graph )
		pipeIn.addViewerListener( this )
		pipeIn.pump

		graph.addAttribute( "ui.stylesheet", styleSheet )
		graph.addAttribute( "ui.antialias" )
		graph.addAttribute( "ui.quality" )
		
		val A:Node = graph.addNode( "A" )
		val B:Node = graph.addNode( "B" )
		val C:Node = graph.addNode( "C" )
		val D:Node = graph.addNode( "D" )

		graph.addEdge( "AB", "A", "B" )
		graph.addEdge( "BC", "B", "C" )
		graph.addEdge( "CD", "C", "D" )
		graph.addEdge( "DA", "D", "A" )
		
		A("xyz") = ( 0.0, 1.0, 0 )
		B("xyz") = ( 3.2, 1.5, 0 )
		C("xyz") = ( 0.2, 0.0, 0 )
		D("xyz") = ( 3.0,-0.5, 0 )
		
		A("label") = "Topic1"
		B("label") = "Topic2"
		C("label") = "Topic3"
		D("label") = "Topic4"
		
		A("ui.icon") = icon1
		
		var i=0;
			
		while( loop ) {
			pipeIn.pump
			sleep( 60 )
			
			i += 1
			
			if( i > 26 ) i = 1
			
			if( A("ui.icon") == icon1 )
			     A("ui.icon") = icon2
			else A("ui.icon") = icon1
			
			B("ui.icon") = "data/cube/3anidot5a_%d.png".format( i )
			C("ui.icon") = "data/cube/3anidot5a_%d.png".format( i )
			D("ui.icon") = "data/cube/3anidot5a_%d.png".format( i )
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
				size: 10px;
				size-mode: fit;
				fill-mode: none;
				stroke-mode: plain; 
				stroke-color: grey;
				stroke-width: 3px;
				padding: 5px, 1px;
				shadow-mode: none;
				icon-mode: at-left;
				text-style: normal;
				text-font: 'Droid Sans';
				icon: dyn-icon;
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