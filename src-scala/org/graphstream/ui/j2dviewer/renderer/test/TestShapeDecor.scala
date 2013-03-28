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

import org.graphstream.algorithm.Toolkit._

import org.graphstream.ui.graphicGraph.stylesheet.{Values, StyleConstants}
import org.graphstream.ui.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.spriteManager._
import org.graphstream.ui.j2dviewer._

//import org.graphstream.ScalaGS._

object TestShapeDecor {
	def main( args:Array[String] ) { (new TestShapeDecor).run } 
}

class TestShapeDecor extends ViewerListener {
	var loop = true
	
	def run() = {
		val graph  = new MultiGraph( "TestSize" )
		val viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD )
		val pipeIn = viewer.newViewerPipe
		val view   = viewer.addView( "view1", new J2DGraphRenderer )
//		val view   = viewer.addDefaultView( true )
  
		view.resizeFrame(500, 430)
		
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
		val F:Node = graph.addNode( "F" )
		val G:Node = graph.addNode( "G" )
		
		val U:Node = graph.addNode( "U" )
		val V:Node = graph.addNode( "V" )
		val W:Node = graph.addNode( "W" )
		val X:Node = graph.addNode( "X" )
		val Y:Node = graph.addNode( "Y" )
		val Z:Node = graph.addNode( "Z" )
		val T:Node = graph.addNode( "T" )

		//graph.addNodes( "a", "b", "c", "d", "e", "f", "g",   "u", "v", "w", "x", "y", "z", "t",   "i", "j" )
		graph.addNode("a")
		graph.addNode("b")
		graph.addNode("c")
		graph.addNode("d")
		graph.addNode("e")
		graph.addNode("f")
		graph.addNode("g")

		graph.addNode("u")
		graph.addNode("v")
		graph.addNode("w")
		graph.addNode("x")
		graph.addNode("y")
		graph.addNode("z")
		graph.addNode("t")

		graph.addNode("i")
		graph.addNode("j")

		val au:Edge = graph.addEdge( "au", "a", "u" )
		val bv:Edge = graph.addEdge( "bv", "b", "v" )
		val cw:Edge = graph.addEdge( "cw", "c", "w" )
		val dx:Edge = graph.addEdge( "dx", "d", "x" )
		val ey:Edge = graph.addEdge( "ey", "e", "y" )
		val fz:Edge = graph.addEdge( "fz", "f", "z" )
		val gt:Edge = graph.addEdge( "gt", "g", "t" )
		val ij:Edge = graph.addEdge( "ij", "i", "j" )
		
		val AU:Edge = graph.addEdge( "AU", "A", "U" )
		val BV:Edge = graph.addEdge( "BV", "B", "V" )
		val CW:Edge = graph.addEdge( "CW", "C", "W" )
		val DX:Edge = graph.addEdge( "DX", "D", "X" )
		val EY:Edge = graph.addEdge( "EY", "E", "Y" )
		val FZ:Edge = graph.addEdge( "FZ", "F", "Z" )
		val GT:Edge = graph.addEdge( "GT", "G", "T" )
		
		A.setAttribute("xyz", Array[Double]( 0, 6, 0 ))
		B.setAttribute("xyz", Array[Double]( 0, 5, 0 ))
		C.setAttribute("xyz", Array[Double]( 0, 4, 0 ))
		D.setAttribute("xyz", Array[Double]( 0, 3, 0 ))
		E.setAttribute("xyz", Array[Double]( 0, 2, 0 ))
		F.setAttribute("xyz", Array[Double]( 0, 1, 0 ))
		G.setAttribute("xyz", Array[Double]( 0, 0, 0 ))
		
		U.setAttribute("xyz", Array[Double]( 3, 5, 0 ))
		V.setAttribute("xyz", Array[Double]( 3, 4, 0 ))
		W.setAttribute("xyz", Array[Double]( 3, 3, 0 ))
		X.setAttribute("xyz", Array[Double]( 3, 2, 0 ))
		Y.setAttribute("xyz", Array[Double]( 3, 1, 0 ))
		Z.setAttribute("xyz", Array[Double]( 3, 0, 0 ))
		T.setAttribute("xyz", Array[Double]( 3,-1, 0 ))
		
		graph.getNode[Node]("a").setAttribute("xyz", Array[Double]( 6, 5, 0 ))
		graph.getNode[Node]("b").setAttribute("xyz", Array[Double]( 6, 4, 0 ))
		graph.getNode[Node]("c").setAttribute("xyz", Array[Double]( 6, 3, 0 ))
		graph.getNode[Node]("d").setAttribute("xyz", Array[Double]( 6, 2, 0 ))
		graph.getNode[Node]("e").setAttribute("xyz", Array[Double]( 6, 1, 0 ))
		graph.getNode[Node]("f").setAttribute("xyz", Array[Double]( 6, 0, 0 ))
		graph.getNode[Node]("g").setAttribute("xyz", Array[Double]( 6,-1, 0 ))
		
		graph.getNode[Node]("u").setAttribute("xyz", Array[Double]( 9, 6, 0 ))
		graph.getNode[Node]("v").setAttribute("xyz", Array[Double]( 9, 5, 0 ))
		graph.getNode[Node]("w").setAttribute("xyz", Array[Double]( 9, 4, 0 ))
		graph.getNode[Node]("x").setAttribute("xyz", Array[Double]( 9, 3, 0 ))
		graph.getNode[Node]("y").setAttribute("xyz", Array[Double]( 9, 2, 0 ))
		graph.getNode[Node]("z").setAttribute("xyz", Array[Double]( 9, 1, 0 ))
		graph.getNode[Node]("t").setAttribute("xyz", Array[Double]( 9, 0, 0 ))
		
		graph.getNode[Node]("i").setAttribute("xyz", Array[Double]( 3, 7, 0 ))
		graph.getNode[Node]("j").setAttribute("xyz", Array[Double]( 6, 8, 0 ))
		
		A.setAttribute("label", "Center")
		B.setAttribute("label", "AtLeft")
		C.setAttribute("label", "AtRight")
		D.setAttribute("label", "Left")
		E.setAttribute("label", "Right")
		F.setAttribute("label", "Under")
		G.setAttribute("label", "Above")
		
		U.setAttribute("label", "Center")
		V.setAttribute("label", "AtLeft")
		W.setAttribute("label", "AtRight")
		X.setAttribute("label", "Left")
		Y.setAttribute("label", "Right")
		Z.setAttribute("label", "Under")
		T.setAttribute("label", "Above")
		
		au.setAttribute("label", "Center")
		bv.setAttribute("label", "AtLeft")
		cw.setAttribute("label", "AtRight")
		dx.setAttribute("label", "Left")
		ey.setAttribute("label", "Right")
		fz.setAttribute("label", "Under")
		gt.setAttribute("label", "Above")
		ij.setAttribute("label", "Along")

		graph.addAttribute("ui.screenshot", "text_align.png")
		    
		while( loop ) {
			pipeIn.pump
			sleep( 40 )
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
 				fill-mode: plain;
 				fill-color: white;
 				padding: 30px;
 			} 
			node {
				shape: circle;
				size: 10px;
				fill-mode: plain;
				fill-color: #0004;
			}
			node:clicked {
				fill-color: #F004;
			}
			node:selected {
				fill-color: #00F4;
			}
			node#A {
				text-alignment: center;
				text-color: #F00;
			}
			node#B {
				text-alignment: at-left;
				text-color: #0F0;
			}
			node#C {
				text-alignment: at-right;
				text-color: #00F;
			}
			node#D {
				text-alignment: left;
				text-color: #FA0;
			}
			node#E {
				text-alignment: right;
				text-color: #0FF;
			}
			node#F {
				text-alignment: under;
				text-color: #F0F;
			}
			node#G {
				text-alignment: above;
				text-color: #999;
			}
			node#U {
				text-alignment: center;
				text-color: #F00;
				icon-mode: at-left;
				icon: url('data/Smiley_016.png');
			}
			node#V {
				text-alignment: at-left;
				text-color: #0F0;
				icon-mode: at-left;
				icon: url('data/Smiley_016.png');
			}
			node#W {
				text-alignment: at-right;
				text-color: #00F;
				icon-mode: at-left;
				icon: url('data/Smiley_016.png');
			}
			node#X {
				text-alignment: left;
				text-color: #FA0;
				icon-mode: at-left;
				icon: url('data/Smiley_016.png');
			}
			node#Y {
				text-alignment: right;
				text-color: #0FF;
				icon-mode: at-left;
				icon: url('data/Smiley_016.png');
			}
			node#Z {
				text-alignment: under;
				text-color: #F0F;
				icon-mode: at-left;
				icon: url('data/Smiley_016.png');
			}
			node#K {
				text-alignment: above;
				text-color: #999;
				icon-mode: at-left;
				icon: url('data/Smiley_016.png');
			}
			edge {
				fill-color: #0004;
			}
			edge#au { text-alignment: center; }
			edge#bv { text-alignment: at-left; }
			edge#cw { text-alignment: at-right; }
			edge#dx { text-alignment: left; }
			edge#ey { text-alignment: right; }
			edge#fz { text-alignment: under; }
			edge#gt { text-alignment: above; }
			edge#ij { text-alignment: along; }
			"""
}