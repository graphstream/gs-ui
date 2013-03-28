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
import org.graphstream.ui.swingViewer.Viewer
import javax.swing._
import java.awt._
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator

object AllSwingTest {
	def main( args:Array[String] ):Unit = {
		val test = new AllSwingTest
		test.run
	}
}

class AllSwingTest extends JFrame {
	def run {
		val g = new MultiGraph("mg")
		val v = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD)
		val gen = new DorogovtsevMendesGenerator()

		g.addAttribute("ui.antialias")
		g.addAttribute("ui.quality")
		g.addAttribute("ui.stylesheet", styleSheet)

		v.enableAutoLayout
		add(v.addDefaultView(false), BorderLayout.CENTER)
		
		gen.addSink(g)
		gen.begin
		for(i <- 0 until 100) gen.nextEvents
		gen.end
		gen.removeSink(g)
		
		setSize( 800, 600 )
		setVisible( true )
	}
  
	protected val styleSheet = """
			graph {
				padding: 60px;
			}
		""" 
}