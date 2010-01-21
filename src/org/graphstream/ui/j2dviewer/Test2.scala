package org.graphstream.ui.j2dviewer

import org.graphstream.graph.Graph
import org.graphstream.scalags.graph.MultiGraph

import org.graphstream.ui.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.SpringBox;

import org.graphstream.algorithm.generator.{DorogovtsevMendesGenerator, Generator}

import org.graphstream.ScalaGS._

object Test2 {
	def main( args:Array[String] ) {
		val test = new ATest
		test.run( args )
	}
	
private class ATest extends ViewerListener {
	private[this] var loop = true
  
	def run( args:Array[String] ) {
		val graph  = new MultiGraph( "g1" )
		val viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD )
		val pipeIn = viewer.newViewerPipe
		val view   = viewer.addView( "view1", new J2DGraphRenderer )
		val layout = new SpringBox( false )
		val gen    = new DorogovtsevMendesGenerator
  
		pipeIn.addAttributeSink( graph )
		pipeIn.addViewerListener( this )
		pipeIn.pump
  
  		graph.addSink( layout )
		layout.addAttributeSink( graph )
		graph.addAttribute( "ui.antialias" )
		graph.addAttribute( "ui.stylesheet", styleSheet )
		
		gen.addSink( graph )
		gen.begin
		var i = 0
		while ( i < 1000 ) {
			gen.nextElement
			i += 1
		}
		gen.end

		while( loop ) {
			pipeIn.pump
			layout.compute
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
 				padding: 60px;
 			} 
			node {
				shape: box;
				size: 10px, 10px;
				fill-mode: gradient-vertical;
				fill-color: white, rgb(200,200,200);
				stroke-mode: plain; 
				stroke-color: rgba(255,255,0,255);
				stroke-width: 2px;
				shadow-mode: plain;
				shadow-width: 0px;
				shadow-offset: 3px, -3px;
				shadow-color: rgba(0,0,0,100);
				//icon-mode: at-left;
				//icon: url('file:///home/antoine/GSLogo11d24.png');
			}
			node:clicked {
				stroke-mode: plain;
				stroke-color: red;
			}
			node:selected {
				stroke-mode: plain;
				stroke-width: 4px;
				stroke-color: blue;
			}
			node#A {
				stroke-mode: plain;
				stroke-width: 2px;
				stroke-color: yellow;
				size: 80px, 30px;
				shape: jcomponent;
				jcomponent: button;
			}
			edge {
				size: 2px;
				fill-color: rgb(128,128,128);
				fill-mode: plain;
				stroke-mode: plain;
				stroke-color: rgb(80,80,80);
				stroke-width: 2px;
				shadow-mode: plain;
				shadow-color: rgba(0,0,0,50);
				shadow-offset: 3px, -3px;
				shadow-width: 0px;
				arrow-shape: arrow;
				arrow-size: 20px, 6px;
			}
		""";
}
}