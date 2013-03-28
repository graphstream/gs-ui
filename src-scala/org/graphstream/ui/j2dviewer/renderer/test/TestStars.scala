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

import org.graphstream.graph._
import org.graphstream.graph.implementations._
import org.graphstream.algorithm.Toolkit._

import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._
import org.graphstream.ui.swingViewer._
import org.graphstream.ui.spriteManager._
import org.graphstream.ui.j2dviewer._

//import org.graphstream.ScalaGS._

object TestStars { def main( args:Array[String] ) { (new TestStars).run } }

class TestStars extends ViewerListener {
	var loop = true
	
	def run() = {
		val graph  = new MultiGraph( "Stars" )
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

		graph.addEdge( "AB", "A", "B" )
		graph.addEdge( "BC", "B", "C" )
		graph.addEdge( "CD", "C", "D" )
		graph.addEdge( "DA", "D", "A" )
		graph.addEdge( "DE", "D", "E" )
		graph.addEdge( "EB", "E", "B" )
		
		A.setAttribute("xyz", Array[Double]( 0, 1, 0 ))
		B.setAttribute("xyz", Array[Double]( 1.5, 1, 0 ))
		C.setAttribute("xyz", Array[Double]( 1, 0, 0 ))
		D.setAttribute("xyz", Array[Double]( 0, 0, 0 ))
		E.setAttribute("xyz", Array[Double]( 0.4, 0.6, 0 ))
		
		val sman = new SpriteManager( graph )
 
		sman.setSpriteFactory( new MySpriteFactory )
		
		val s1 = sman.addSprite( "S1" ).asInstanceOf[MySprite]
		val s2 = sman.addSprite( "S2" ).asInstanceOf[MySprite]
		val s3 = sman.addSprite( "S3" ).asInstanceOf[MySprite]
			
		s1.attachToEdge( "AB" )
		s2.attachToEdge( "CD" )
		s3.attachToEdge( "DA" )
		
		while( loop ) {
			pipeIn.pump
			s1.move
			s2.move
			s3.move
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
 	}
  
 	def buttonReleased( id:String ) {} 
 
// Data
 	
	val styleSheet = """
			graph {
				canvas-color: black;
 				fill-mode: gradient-vertical;
 				fill-color: black, #004;
 				padding: 60px;
 			} 
			node {
				shape: circle;
				size: 14px;
				fill-mode: gradient-radial;
				fill-color: #FFFA, #FFF0;
				stroke-mode: none; 
				shadow-mode: gradient-radial;
				shadow-color: #FFF9, #FFF0;
				shadow-width: 10px;
				shadow-offset: 0px, 0px;
			}
			node:clicked {
				fill-color: #F00A, #F000;
			}
			node:selected {
				fill-color: #00FA, #00F0;
			}
			edge {
				shape: line;
				size: 1px;
				fill-color: #FFF3;
				fill-mode: plain;
				arrow-shape: none;
			}
			sprite {
				shape: circle;
				fill-mode: gradient-radial;
				fill-color: #FFF8, #FFF0;
			}
			"""

	class MySpriteFactory extends SpriteFactory {
		override def newSprite( identifier:String, manager:SpriteManager, position:Values ):Sprite = {
			if( position != null )
				return new MySprite( identifier, manager, position );
		
			return new MySprite( identifier, manager );
		}
	}
	
	class MySprite( identifier:String, manager:SpriteManager, pos:Values ) extends Sprite( identifier, manager, pos ) {
		def this( identifier:String, manager:SpriteManager ) {
			this( identifier, manager, new Values( StyleConstants.Units.GU, 0, 0, 0 ) )
		}
		
		val SPEED = 0.005f
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
}