/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
import org.graphstream.ui.swingViewer.Viewer
//import org.graphstream.ScalaGS._
import javax.swing._
import java.awt._

object AllSwingTest {
	def main( args:Array[String] ):Unit = {
		val test = new AllSwingTest
		test.run
	}
}

class AllSwingTest extends JFrame {
	def run {
		val graph  = new MultiGraph( "mg" )
		val viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD )
		
		graph.addNode("A")
		graph.addNode("B")
		graph.addNode("C")
		graph.addEdge("AB", "A", "B")
		graph.addEdge("BC", "B", "C")
		graph.addEdge("CA", "C", "A")
//		graph.addNodes( "A", "B", "C" )
//		graph.addEdges( "A", "B", "C", "A" )
		graph.addAttribute( "ui.antialias" )
		graph.addAttribute( "ui.quality" )
		graph.addAttribute( "ui.default.title", "All In Swing Test" )
		graph.addAttribute( "ui.stylesheet", styleSheet )
   
		graph.getNode[Node]("A").setAttribute("xyz", Array[Double]( -1, 0, 0 ))
		graph.getNode[Node]("B").setAttribute("xyz", Array[Double](  1, 0, 0 ))
  		graph.getNode[Node]("C").setAttribute("xyz", Array[Double](  0, 1, 0 ))
   
		val view = viewer.addDefaultView(false)
		
		add( view, BorderLayout.CENTER )
		
		view.getCamera.setViewPercent(0.5)
		view.getCamera.setViewCenter(-1, 0, 0)
		
		setSize( 800, 600 )
		setVisible( true )
	}
  
	protected val styleSheet = """
			graph {
				padding: 60px;
			}
		""" 
}