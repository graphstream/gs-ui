package org.graphstream.ui.j2dviewer.renderer.test

import scala.collection.JavaConversions._

import org.graphstream.graph.Graph
import org.graphstream.scalags.graph.MultiGraph

import org.graphstream.ui.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.SpringBox;

import org.graphstream.algorithm.generator.{DorogovtsevMendesGenerator, Generator}

import org.graphstream.ScalaGS._

object LayoutTest {
	def main( args:Array[String] ) {
	  	System.setProperty( "gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer" )
		val test = new ATest
		test.run( args )
	}
	
private class ATest extends ViewerListener {
	private[this] var loop = true
  
	def run( args:Array[String] ) {
		val graph  = new MultiGraph( "g1" )
		val viewer = graph.display( true )
		val pipeIn = viewer.newViewerPipe
		val gen    = new DorogovtsevMendesGenerator
  
		pipeIn.addAttributeSink( graph )
		pipeIn.addViewerListener( this )
		pipeIn.pump
  
		graph.addAttribute( "ui.default.title", "Layout Test" );
		graph.addAttribute( "ui.antialias" )
		graph.addAttribute( "ui.stylesheet", styleSheet )
		
		gen.addSink( graph )
		gen.setDirectedEdges( true, true )
		gen.begin
		var i = 0
		while ( i < 100 ) { gen.nextElement; i += 1 }
		gen.end
  
		graph.foreach { _.addAttribute( "ui.label", "truc" ) }

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
 				padding: 60px;
 			} 
			node {
				shape: circle;
				size: 10px;
				fill-mode: gradient-vertical;
				fill-color: white, rgb(200,200,200);
				stroke-mode: plain; 
				stroke-color: rgba(255,255,0,255);
				stroke-width: 2px;
				shadow-mode: plain;
				shadow-width: 0px;
				shadow-offset: 3px, -3px;
				shadow-color: rgba(0,0,0,100);
				text-visibility-mode: zoom-range;
				text-visibility: 0, 0.9;
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
			edge {
				size: 1px;
				shape: cubic-curve;
				fill-color: rgb(128,128,128);
				fill-mode: plain;
				stroke-mode: plain;
				stroke-color: rgb(80,80,80);
				stroke-width: 1px;
				shadow-mode: none;
				shadow-color: rgba(0,0,0,50);
				shadow-offset: 3px, -3px;
				shadow-width: 0px;
				arrow-shape: diamond;
				arrow-size: 14px, 7px;
			}
		""";
   
	private val oldStyleSheet:String = """
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
				text-visibility-mode: zoom-range;
				text-visibility: 0, 0.9;
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
			edge {
				size: 2px;
				shape: blob;
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