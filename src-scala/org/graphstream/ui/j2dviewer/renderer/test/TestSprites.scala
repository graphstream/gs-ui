/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.ui.geom.Point3
import org.graphstream.graph._
import org.graphstream.graph.implementations.MultiGraph

import org.graphstream.algorithm.Toolkit._

import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.swingViewer._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._
import org.graphstream.ui.spriteManager._
import org.graphstream.ui.j2dviewer._

//import org.graphstream.ScalaGS._

object TestSprites {
	def main( args:Array[String] ):Unit = {
		(new TestSprites).run
	}
}

class TestSprites extends ViewerListener {
	var loop = true
	
	def run() = {
		val graph  = new MultiGraph( "TestSprites" )
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
		val E:Node = graph.addNode( "E" )

		C.addAttribute("ui.points",
		        new Point3(-0.05f, -0.05f, 0f),
		        new Point3( 0f,    -0.02f, 0f),
		        new Point3( 0.05f, -0.05f, 0f),
		        new Point3( 0f,     0.05f, 0f))
		
		graph.addEdge( "AB1", "A", "B", true )
		graph.addEdge( "AB2", "B", "A", true )
		graph.addEdge( "BC", "B", "C" )
		graph.addEdge( "CD", "C", "D", true )
		graph.addEdge( "DA", "D", "A" )
		graph.addEdge( "DE", "D", "E", true )
		graph.addEdge( "EB", "E", "B", true )
		graph.addEdge( "BB", "B", "B", true )
		
		graph.getEdge("CD").asInstanceOf[Edge].addAttribute("ui.points", 
		        new Point3(1, 0, 0),
		        new Point3(0.6f, 0.1f, 0f),
		        new Point3(0.3f,-0.1f, 0f),
		        new Point3(0, 0, 0))
		
		A.setAttribute("xyz", Array[Double]( 0, 1, 0 ))
		B.setAttribute("xyz", Array[Double]( 1.5, 1, 0 ))
		C.setAttribute("xyz", Array[Double]( 1, 0, 0 ))
		D.setAttribute("xyz", Array[Double]( 0, 0, 0 ))
		E.setAttribute("xyz", Array[Double]( 0.4, 0.6, 0 ))
		
		A.setAttribute("label", "A")
		B.setAttribute("label", "B")
		C.setAttribute("label", "C")
		D.setAttribute("label", "D")
		E.setAttribute("label", "E")
		
		val sman = new SpriteManager( graph )
 
//		sman.setSpriteFactory( new MySpriteFactory )
		
		val s1 = sman.addSprite("S1", classOf[MovingEdgeSprite])
		val s2 = sman.addSprite("S2", classOf[MovingEdgeSprite])
		val s3 = sman.addSprite("S3", classOf[MovingEdgeSprite])
		val s4 = sman.addSprite("S4", classOf[MovingEdgeSprite])
		val s5 = sman.addSprite("S5", classOf[DataSprite])
		val s6 = sman.addSprite("S6", classOf[MovingNodeSprite])
		val s7 = sman.addSprite("S7", classOf[MovingEdgeSprite])
		//val s8 = sman.addSprite("S8")
		val s8 = sman.addSprite("S8", classOf[MovingEdgeSprite])
			
		s1.attachToEdge("AB1")
		s2.attachToEdge("CD")
		s3.attachToEdge("DA")
		s4.attachToEdge("EB")
		s5.attachToNode("A")
		s6.attachToNode("D")
		s7.attachToEdge("AB2")
		s8.attachToEdge("EB")
		
		s2.setOffsetPx(20)
		s3.setOffsetPx(15)
		s4.setOffsetPx(4)
		s5.setPosition(Units.PX, 30, 0, 90)
		s5.setData(0.3f, 0.5f, 0.2f)
		s6.setOffsetPx(20)
		s8.setPosition(0.5f, 0.5f, 0f)

		s1.addAttribute("ui.label", "FooBar1")
		s2.addAttribute("ui.label", "FooBar2")
		s4.addAttribute("ui.label", "FooBar4")
		s7.addAttribute("ui.label", "FooBar7")
		
		s8.addAttribute("ui.points",
		        new Point3(-0.02f, -0.02f, 0f),
		        new Point3( 0f,    -0.01f, 0f),
		        new Point3( 0.02f, -0.02f, 0f),
		        new Point3( 0f,     0.02f, 0f))

		E.setAttribute("ui.pie-values",
		        (0.2f).asInstanceOf[AnyRef],
		        (0.3f).asInstanceOf[AnyRef],
		        (0.4f).asInstanceOf[AnyRef],
		        (0.1f).asInstanceOf[AnyRef])
		
//		graph.write("randomOutEdge.dgs")
		        
		while( loop ) {
			pipeIn.pump
			s1.move
			s2.move
			s3.move
			s4.move
			s6.move
			s7.move
			s8.move
			sleep( 4 )
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
				size: 14px;
				fill-mode: plain;
				fill-color: white;
				stroke-mode: plain; 
				stroke-color: grey;
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
			node#C {
				shape: polygon;
			}
			node#E {
				shape: pie-chart;
				fill-color: red, green, blue, yellow, magenta;
				size: 30px;
				stroke-mode: plain;
				stroke-width: 1px;
				stroke-color: black;
			}
			edge {
				shape: line;
				size: 1px;
				fill-color: grey;
				fill-mode: plain;
				arrow-shape: arrow;
				arrow-size: 10px, 3px;
			}
			edge#BC {
				shape: cubic-curve;
			}
			edge#EB {
				shape: cubic-curve;
			}
			edge#CD {
				shape: polyline;
			}
			sprite {
				shape: circle;
				fill-color: #944;
				z-index: -1;
			}
			sprite#S2 {
				shape: arrow;
				sprite-orientation: projection;
				size: 20px, 10px;
				fill-color: #449;
			}
			sprite#S3 {
				shape: arrow;
				sprite-orientation: to;
				size: 10px, 5px;
				fill-color: #494;
			}
			sprite#S4 {
				shape: flow;
				size: 8px;
				fill-color: #99A9;
				sprite-orientation: to;
			}
			sprite#S5 {
				shape: pie-chart;
				size: 40px;
				fill-color: cyan, magenta, yellow, red, green, blue;
				stroke-mode: plain;
				stroke-width: 1px;
				stroke-color: black;
			}
			sprite#S6 {
				shape: circle;
				size: 30px;
				fill-color: #55C;
			}
			sprite#S7 {
				shape: box;
				size: 100px, 58px;
				sprite-orientation: projection;
				fill-mode: image-scaled;
				fill-image: url('data/container_small.png');
				fill-color: red;
				stroke-mode: none;
			}
			sprite#S8 {
				shape: polygon;
				fill-color: yellow;
				stroke-mode: plain;
				stroke-width: 1px;
				stroke-color: red;
				shadow-mode: plain;
				shadow-color: #707070;
				shadow-offset: 3px, -3px;
			}
			"""
}

class MovingEdgeSprite extends Sprite {
	val SPEED = 0.001f
	var speed = SPEED
	var off = 0f
	var units = Units.PX
	
	def setOffsetPx( offset:Float ) { off = offset; units = Units.PX }
	
	def move() {
		var p = getX
		
		p += speed
		
		if( p < 0 || p > 1 ) {
			val edge = getAttachment.asInstanceOf[Edge]
			
			if( edge != null ) {
				val node = if( p > 1 ) edge.getTargetNode[Node] else edge.getSourceNode[Node]
				var other = randomOutEdge( node )
				
				if(other==null) {
				    Console.err.println("node %s out=%d null !!".format(node.getId, node.getOutDegree))
				}
				
				if( node.getOutDegree > 1 ) { while( other eq edge ) other = randomOutEdge( node ) }
				
				attachToEdge( other.getId )
				if( node eq other.getSourceNode ) {
					setPosition( units, 0, off, 0 )
					speed = SPEED
				} else {
					setPosition( units, 1, off, 0 )
					speed = -SPEED
				}
			}
		} else {
			setPosition( units, p, off, 0 )
		}
	}
}

class MovingNodeSprite extends Sprite {
	val SPEED = 1f
	var speed = SPEED
	var off = 0f
	var units = Units.PX
	
	def setOffsetPx( offset:Float ) { off = offset; units = Units.PX }
	
	def move() {
		var p = getZ + speed
		
		if( p < 0 || p > 360 ) { p = 0f }
		
		val node = getAttachment.asInstanceOf[Node]
			
		if( node != null ) {
			setPosition( units, off, 0, p )
		}
	}
}

class DataSprite extends Sprite {
	def setData( values:Float* ) {
		val data = new Array[Float]( values.length )
		var i    = 0

		values.foreach { value => data(i) = value; i += 1 }
		addAttribute( "ui.pie-values", data )
	}
}