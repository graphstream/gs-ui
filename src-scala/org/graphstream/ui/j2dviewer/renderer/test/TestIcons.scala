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
import org.graphstream.graph.implementations.MultiGraph

import org.graphstream.ui.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.spriteManager._
import org.graphstream.ui.j2dviewer._

//import org.graphstream.ScalaGS._

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
		
		A.setAttribute("xyz", Array[Double]( 0.0, 1.0, 0 ))
		B.setAttribute("xyz", Array[Double]( 3.2, 1.5, 0 ))
		C.setAttribute("xyz", Array[Double]( 0.2, 0.0, 0 ))
		D.setAttribute("xyz", Array[Double]( 3.0,-0.5, 0 ))
		
		A.setAttribute("label", "Topic1")
		B.setAttribute("label", "Topic2")
		C.setAttribute("label", "Topic3")
		D.setAttribute("label", "Topic4")
		
		A.setAttribute("ui.icon",  icon1)
		
		var i=0;
			
		while( loop ) {
			pipeIn.pump
			sleep( 60 )
			
			i += 1
			
			if( i > 26 ) i = 1
			
			if( A.getAttribute[AnyRef]("ui.icon") eq icon1 )
			     A.setAttribute("ui.icon", icon2)
			else A.setAttribute("ui.icon", icon1)
			
			B.setAttribute("ui.icon", "data/cube/3anidot5a_%d.png".format( i ))
			C.setAttribute("ui.icon", "data/cube/3anidot5a_%d.png".format( i ))
			D.setAttribute("ui.icon", "data/cube/3anidot5a_%d.png".format( i ))
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