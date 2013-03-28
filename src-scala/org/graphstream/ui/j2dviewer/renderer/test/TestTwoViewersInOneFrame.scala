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

import java.awt.GridLayout
import javax.swing.JFrame

import _root_.org.graphstream.graph._
import _root_.org.graphstream.graph.implementations._
import _root_.org.graphstream.stream.thread._
import _root_.org.graphstream.ui.swingViewer._
import _root_.org.graphstream.ui.swingViewer.Viewer.ThreadingModel
import _root_.org.graphstream.algorithm.generator._

object TestTwoViewersInOneFrame {
	def main(args:Array[String]):Unit = {
		System.setProperty( "gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer" );
		(new TestTwoViewersInOneFrame).test
	}
}

class TestTwoViewersInOneFrame extends JFrame {

	def test() {
		val graph1 = new MultiGraph("g1")
		val graph2 = new MultiGraph("g2")
		val viewer1 = new Viewer(new ThreadProxyPipe(graph1))
		val viewer2 = new Viewer(new ThreadProxyPipe(graph2))

		graph1.addAttribute("ui.stylesheet", styleSheet1)
		graph2.addAttribute("ui.stylesheet", styleSheet2)
		viewer1.addView(new DefaultView(viewer1, "view1", Viewer.newGraphRenderer))
		viewer2.addView(new DefaultView(viewer2, "view2", Viewer.newGraphRenderer))
		viewer1.enableAutoLayout
		viewer2.enableAutoLayout

		val gen = new DorogovtsevMendesGenerator

		gen.addSink(graph1)
		gen.addSink(graph2)
		gen.begin
		for(i <- 0 until 100)
			gen.nextEvents
		gen.end

		gen.removeSink(graph1)
		gen.removeSink(graph2)
//		graph1.addNode("A")
//		graph1.addNode("B")
//		graph1.addNode("C")
//		graph1.addEdge("AB", "A", "B", true)
//		graph1.addEdge("BC", "B", "C", true)
//		graph1.addEdge("CA", "C", "A", true)
//		graph2.addNode("A")
//		graph2.addNode("B")
//		graph2.addNode("C")
//		graph2.addEdge("AB", "A", "B", true)
//		graph2.addEdge("BC", "B", "C", true)
//		graph2.addEdge("CA", "C", "A", true)
		
		setLayout(new GridLayout(1, 2))
		//add(new JButton("Button"))
		add(viewer1.getView("view1"))
		add(viewer2.getView("view2"))
		setSize(800, 600)
		setVisible(true)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
	}

	protected val styleSheet1 =
		"graph { padding: 40px; }" +
		"node { fill-color: red; stroke-mode: plain; stroke-color: black; }";
	
	protected val styleSheet2 =
		"graph { padding: 40px; }" +
		"node { fill-color: blue; stroke-mode: plain; stroke-color: black; }";
}