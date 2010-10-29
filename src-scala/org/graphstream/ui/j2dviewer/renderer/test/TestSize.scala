package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.graph._
import org.graphstream.scalags.graph.MultiGraph

import org.graphstream.algorithm.Toolkit._

import org.graphstream.ui.graphicGraph.stylesheet.{Values, StyleConstants}
import org.graphstream.ui.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.spriteManager._
import org.graphstream.ui.j2dviewer._

import org.graphstream.ScalaGS._

object TestSize {
	def main( args:Array[String] ) {
		//System.setProperty( "gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer" )
		(new TestSize).run
	} 
}

class TestSize extends ViewerListener {
	var loop = true
	
	def run() = {
		val graph  = new MultiGraph( "TestSize" )
		val viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD )
		val pipeIn = viewer.newViewerPipe
		val view   = viewer.addView( "view1", new J2DGraphRenderer )
//		val view   = viewer.addDefaultView( true )
  
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

		val AB:Edge = graph.addEdge( "AB", "A", "B" )
		val BC:Edge = graph.addEdge( "BC", "B", "C" )
		val CD:Edge = graph.addEdge( "CD", "C", "D" )
		val DA:Edge = graph.addEdge( "DA", "D", "A" )
		val BB:Edge = graph.addEdge( "BB", "B", "B" )
		
		A("xyz") = ( 0, 1, 0 )
		B("xyz") = ( 1, 1, 0 )
		C("xyz") = ( 1, 0, 0 )
		D("xyz") = ( 0, 0, 0 )
		
		A("label") = "A"
		B("label") = "B"
		C("label") = "C"
		D("label") = "A long label ..."
			
		AB("label") = "AB"
		BC("label") = "A long label ..."
		CD("label") = "CD"
		BB("label") = "BB"
		
		val sm = new SpriteManager( graph )
		val S1 = sm.addSprite( "S1" )
		
		S1.attachToNode( "C" )
		S1.setPosition( StyleConstants.Units.PX, 40, 45, 0 )
				
		var size = 20f
		var sizeInc = 1f
			
		while( loop ) {
			pipeIn.pump
			sleep( 40 )
			A.setAttribute( "ui.size", size.asInstanceOf[AnyRef] )
//			A.setAttribute( "ui.size", "%spx".format( size ) )
			BC.setAttribute( "ui.size", size.asInstanceOf[AnyRef] )
			S1.setAttribute( "ui.size", size.asInstanceOf[AnyRef] )
			
			size += sizeInc
			
			if( size > 50 ) {
				sizeInc = -1f; size = 50f
			} else if( size < 20 ) {
				sizeInc = 1f; size = 20f
			}
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
				shape: circle;
				size: 20px;
				fill-mode: plain;
				fill-color: #CCC;
				stroke-mode: plain; 
				stroke-color: black;
				stroke-width: 1px;
			}
			node:clicked {
				stroke-mode: plain;
				stroke-color: red;
			}
			node:selected {
				stroke-mode: plain;
				stroke-color: blue;
			}
			node#A {
				size-mode: dyn-size;
				size: 10px;
			}
			node#D {
				shape: box;
				size-mode: fit;
				padding: 5px;
			}
			edge {
				shape: blob;
				size: 1px;
				fill-color: grey;
				fill-mode: plain;
				arrow-shape: arrow;
				arrow-size: 10px, 3px;
			}
			edge#BC {
				size-mode: dyn-size;
				size: 1px;
			}
			sprite {
				shape: circle;
				fill-color: #FCC;
				stroke-mode: plain;
				stroke-color: black;
			}
			sprite:selected {
				stroke-color: red;
			}
			sprite#S1 {
				size-mode: dyn-size;
			}
			"""
}