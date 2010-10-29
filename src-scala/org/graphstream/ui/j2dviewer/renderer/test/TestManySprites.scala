package org.graphstream.ui.j2dviewer.renderer.test

import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap

import org.graphstream.graph.{Graph, Node, Edge}
import org.graphstream.scalags.graph.MultiGraph

import org.graphstream.ui.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}
import org.graphstream.ui.spriteManager._
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.SpringBox;

import org.graphstream.algorithm.generator.{DorogovtsevMendesGenerator, Generator}
import org.graphstream.ui.graphicGraph.stylesheet.Values

import org.graphstream.ScalaGS._

import org.graphstream.algorithm.Toolkit._

object TestManySprites {
	def main( args:Array[String] ) {
		System.setProperty( "gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer" )
		(new TestManySprites).run( args )
	}
}

class TestManySprites extends ViewerListener {
	
	/** The application runs while this is true. */
	var loop = true
	
	/** The graph at hand. */
	var graph:Graph = null
	
	/** The set of sprites. */
	var sprites:SpriteManager = null 
	
	val NODE_COUNT = 1000
	val SPRITE_COUNT = 500
	
	/** Main application loop. */
	def run( args:Array[String] ) = {
		graph  = new MultiGraph( "TestSprites" )
		val viewer = graph.display( true )
		val pipeIn = viewer.newViewerPipe
		val gen    = new DorogovtsevMendesGenerator
  
		pipeIn.addAttributeSink( graph )
		pipeIn.addViewerListener( this )
		pipeIn.pump
		
		printf( "%d nodes, %d sprites%n", NODE_COUNT, SPRITE_COUNT )
  
		graph.addAttribute( "ui.default.title", "Layout Test" );
//		graph.addAttribute( "ui.antialias" )
		graph.addAttribute( "ui.stylesheet", styleSheet )
		
		gen.addSink( graph )
//		gen.setDirectedEdges( true, true )
		gen.begin
		var i = 0
		while ( i < NODE_COUNT ) { gen.nextEvents; i += 1 }
		gen.end
  
		sleep( 1000 )
		addSprites
		
		while( loop ) {
			pipeIn.pump
			moveSprites
			sleep( 10 )
		}
		
		printf( "bye bye" )
		exit
	}
	
	protected def sleep( ms:Long ) = Thread.sleep( ms )
	
	protected def addSprites() {
		sprites = new SpriteManager( graph )
		
		sprites.setSpriteFactory( new TestSpriteFactory )
		
		for( i <- 1 to SPRITE_COUNT ) {
			sprites.addSprite( i.toString )
		}
		
		sprites.foreach { s:Sprite =>
			s.attachToEdge( randomEdge( graph ).getId )
		}
	}
	
	protected def moveSprites() { sprites.foreach { s:Sprite => s.asInstanceOf[TestSprite].move } }

// Viewer Listener Interface
 
	def viewClosed( id:String ) { loop = false }
 
 	def buttonPushed( id:String ) {}
  
 	def buttonReleased( id:String ) {} 
 
// Data

	private val styleSheet = """
			graph {
 				fill-mode: plain;
 				fill-color: white, gray;
 				padding: 60px;
 			} 
			node {
				shape: circle;
				size: 4px;
				fill-mode: plain;
				fill-color: grey;
				stroke-mode: none; 
				text-visibility-mode: zoom-range;
				text-visibility: 0, 0.9;
			}
			edge {
				size: 1px;
				shape: line;
				fill-color: grey;
				fill-mode: plain;
				stroke-mode: none;
			}
			sprite {
				shape: circle;
				size: 6px;
				fill-mode: plain;
				fill-color: red;
				stroke-mode: none;
			}
		""";
}

protected class TestSpriteFactory extends SpriteFactory {
	override def newSprite( identifier:String, manager:SpriteManager, position:Values ):Sprite = {
		new TestSprite( identifier, manager );
	}
}

protected class TestSprite( identifier:String, manager:SpriteManager ) extends Sprite( identifier, manager ) {
	var dir = 0.01f

	def move() {
		var p = getX
		
		p += dir
			
		if( p < 0 || p > 1 )
		     chooseNextEdge
		else setPosition( p )
	}
	
	def chooseNextEdge() {
		val edge = getAttachment.asInstanceOf[Edge]
		val node:Node = if( dir > 0 ) edge.getTargetNode[Node] else edge.getSourceNode[Node]
		val next:Edge = randomEdge( node )
		var pos  = 0
		
		if( node == next.getSourceNode )
			 { dir =  0.01f; pos = 0; } 
		else { dir = -0.01f; pos = 1; }
		
		attachToEdge( next.getId )
		setPosition( pos )
	}
}