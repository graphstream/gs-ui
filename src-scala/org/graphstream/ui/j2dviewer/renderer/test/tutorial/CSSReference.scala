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
package org.graphstream.ui.j2dviewer.renderer.test.tutorial

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units
import org.graphstream.ui.spriteManager.SpriteManager
import org.graphstream.graph.Edge
import java.io.PrintStream
import org.graphstream.graph._
import org.graphstream.graph.implementations._
import org.graphstream.ui.swingViewer.{Viewer, View}
//import org.graphstream.ScalaGS._
//import org.graphstream.scalags.graph._
import scala.collection.JavaConversions._

object CSSReference {
    def main(args:Array[String]):Unit = {
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
        (new CSSReference).runTests
    }
}

class CSSReference {
	def runTests() {
//	    genericTest("basic", "")

//	    genericTest("fill_mode_plain1",  "node { size: 20px; fill-color: red; }")
//	    genericTest("fill_mode_plain2",  "node { size: 5px; } graph { fill-color: #DCC; }")
//	    genericTest("fill_mode_radial1", "node { size: 20px; fill-color: rgb(0,0,0), rgba(0,0,0,0); fill-mode: gradient-radial; }")
//	    genericTest("fill_mode_radial2", "node { size: 5px; } graph { fill-color: #FFF, #BBB; fill-mode: gradient-radial; }")
//	    genericTest("fill_mode_horiz1",  "node { size: 20px; fill-color: yellow, orange; fill-mode: gradient-horizontal; }")
//	    genericTest("fill_mode_horiz2",  "node { size: 5px; } graph { fill-color: #DDD, #777; fill-mode: gradient-horizontal; }")
//	    genericTest("fill_mode_vert1",   "node { size: 20px; fill-color: yellow, orange; fill-mode: gradient-vertical; }")
//	    genericTest("fill_mode_vert2",   "node { size: 5px; } graph { fill-color: #DDD, #777; fill-mode: gradient-vertical; }")
//	    genericTest("fill_mode_diag1",   "node { size: 20px; fill-color: aquamarine, #254; fill-mode: gradient-diagonal1; }")
//	    genericTest("fill_mode_diag2",   "node { size: 5px; } graph { fill-color: #DDD, #777; fill-mode: gradient-diagonal1; }")
//	    genericTest("fill_mode_diag3",   "node { size: 20px; fill-color: aquamarine, #254; fill-mode: gradient-diagonal2; }")
//	    genericTest("fill_mode_diag4",   "node { size: 5px; } graph { fill-color: #DDD, #777; fill-mode: gradient-diagonal2; }")
//	    genericTest("fill_mode_tiled",   "graph { fill-mode: image-tiled; fill-image: url('data/Smiley_032.png'); }")
//		genericTest("fill_mode_scaled1", "node { size: 32px; fill-mode: image-scaled; fill-image: url('data/Smiley_032.png'); }")
//		genericTest("fill_mode_scaled2", "node { size: 32px; fill-mode: image-scaled; fill-image: url('data/Smiley_032.png'); shape: box; stroke-mode: plain; stroke-color: #999; } node#A { size: 16px, 32px; } node#C { size: 32px, 16px; }")
//		genericTest("fill_mode_scaled3", "graph { fill-mode: image-scaled; fill-image: url('data/Smiley_128.png'); }")
//		genericTest("fill_mode_scaled_max1", "node { size: 32px; fill-mode: image-scaled-ratio-max; fill-image: url('data/Smiley_032.png'); shape: box; stroke-mode: plain; stroke-color: #999; } node#A { size: 16px, 32px; } node#C { size: 32px, 16px; }")
//		genericTest("fill_mode_scaled_max2", "graph { fill-mode: image-scaled-ratio-max; fill-image: url('data/Smiley_128.png'); }")
//		genericTest("fill_mode_scaled_min1", "node { size: 32px; fill-mode: image-scaled-ratio-min; fill-image: url('data/Smiley_032.png'); shape: box; stroke-mode: plain; stroke-color: #999; } node#A { size: 16px, 32px; } node#C { size: 32px, 16px; }")
//		genericTest("fill_mode_scaled_min2", "graph { fill-mode: image-scaled-ratio-min; fill-image: url('data/Smiley_064.png'); }")
//	    genericTest("fill_mode_shade",   "graph { fill-mode: gradient-vertical; fill-color: gray, white, white, white, white, gray; }")
//	    genericTest("fill_mode_pride",   "graph { fill-mode: gradient-vertical; fill-color: purple, blue, green, yellow, orange, red; }")
//
//		genericTest("stroke_mode1",      "node { fill-color: #DEE; size: 20px; stroke-mode: plain; stroke-color: #555; } node#A { shape: box; } node#C { shape: triangle; }" )
//		genericTest("stroke_mode2",      "node { fill-color: #DED; size: 20px; stroke-mode: plain; stroke-color: #855; stroke-width: 0.5px; shape: cross; } node#A { shape: rounded-box; stroke-width: 6px; stroke-color: #585; } node#C { shape: diamond; stroke-width: 3px; stroke-color: #558; }" )
//
//	    genericTest("z_index1",          "node { size: 20px; z-index: 1; fill-color: #999; } edge { z-index: 0; fill-color: #333; size: 3px; }" )
//	    genericTest("z_index2",          "node { size: 20px; z-index: 0; fill-color: #999; } edge { z-index: 1; fill-color: #333; size: 3px; }" )
//	    
//	    genericTest("shadow_plain",      "node { size: 15px; fill-color: #D22; stroke-mode: plain; stroke-color: #999; shadow-mode: plain; shadow-width: 0px; shadow-color: #999; shadow-offset: 3px, -3px; }")
//	    genericTest("shadow_radial",     "node { size: 15px; fill-color: #D82; stroke-mode: plain; stroke-color: #999; shadow-mode: gradient-radial; shadow-width: 2px; shadow-color: #999, white; shadow-offset: 3px, -3px; }")
//	    genericTest("shadow_horiz",      "node { size: 15px; fill-color: #DC2; stroke-mode: plain; stroke-color: #999; shadow-mode: gradient-horizontal; shadow-width: 4px; shadow-color: #999, white; shadow-offset: 0px; }")
//	    genericTest("shadow_vert",       "node { size: 15px; fill-color: #8C2; stroke-mode: plain; stroke-color: #999; shadow-mode: gradient-vertical; shadow-width: 4px; shadow-color: #999, white; shadow-offset: 0px; }")
//	    genericTest("shadow_off1",       "node { size: 15px; fill-color: #CCC; stroke-mode: plain; stroke-color: #999; shadow-mode: plain; shadow-width: 3px; shadow-color: #FC0; shadow-offset: 0px; } edge { fill-color: #777; }")
//	    genericTest("shadow_off2",       "node { size: 15px; fill-color: #CCC; stroke-mode: plain; stroke-color: #999; shadow-mode: plain; shadow-width: 3px; shadow-color: #FC0; shadow-offset: 0px; } edge { fill-color: #777; shadow-mode: plain; shadow-width: 3px; shadow-color: #FC0; shadow-offset: 0px; }")
//	    genericTest("shadow_tron",       "graph { fill-color: black; } node { size: 15px; fill-color: #44F; stroke-mode: plain; stroke-width: 2px; stroke-color: #CCF; shadow-mode: gradient-radial; shadow-width: 10px; shadow-color: #EEF, #000; shadow-offset: 0px; } edge { fill-color: #CCF; size: 2px; }")
//	    
//	    //genericTest("padding_nodes1",    "node { shape: text-box; stroke-mode: plain; stroke-color: #AAA; fill-color: #EEE; }", addLabels)
//	    
//	    genericTest("padding_graph1",    "graph { padding:  0px; fill-color: #EEE; }")
//	    genericTest("padding_graph2",    "graph { padding: 20px; fill-color: #EEE; }")
//	
//	    genericTest("text_bg_mode1", "node { text-alignment: at-right; text-color: #222; } node#B { text-alignment: at-left; } node#C { text-alignment: under; }", addLabels)
//	    genericTest("text_bg_mode2", "node { text-alignment: at-right; text-color: #222; text-background-mode: plain; text-background-color: white; } node#B { text-alignment: at-left; } node#C { text-alignment: under; }", addLabels)
//	    genericTest("text_bg_mode3", "graph { padding: 40px; } node { text-alignment: at-right; text-background-mode: plain; text-background-color: #EB2; text-color: #222; }", addLabels)
//	    genericTest("text_bg_mode4", "graph { padding: 40px; } node { text-alignment: at-right; text-padding: 3px, 2px; text-background-mode: rounded-box; text-background-color: #EB2; text-color: #222; }", addLabels)
//
//	    genericTest("text_style1", "node { text-alignment: under; text-offset: 0px, 4px; text-color: #444; } node#A { text-style:italic; } node#B { text-style:bold; } node#C { text-style: bold-italic;  text-alignment: above; text-offset: 0px, -4px; }", addLabels)
//	    
//	    genericTest("text_bg_pad1", "node { text-alignment: under; text-color: white; text-style: bold; text-background-mode: rounded-box; text-background-color: #222C; text-padding: 1px; text-offset: 0px, 2px; } node#C {text-alignment:above; text-offset: 0px, -2px;}", addLabels)
//	    genericTest("text_bg_pad2", "node { text-alignment: under; text-color: white; text-style: bold; text-background-mode: rounded-box; text-background-color: #222C; text-padding: 5px, 4px; text-offset: 0px, 5px; }node#C {text-alignment:above; text-offset: 0px, -5px;}", addLabels)
//    
//	    genericTest("text_bg_off1", "graph { padding: 45px; } node { text-alignment: at-right; text-padding: 3px, 2px; text-background-mode: rounded-box; text-background-color: #A7CC; text-color: white; text-style: bold-italic; text-color: #FFF; }", addLabels)
//	    genericTest("text_bg_off2", "graph { padding: 45px; } node { text-alignment: at-right; text-padding: 3px, 2px; text-background-mode: rounded-box; text-background-color: #A7CC; text-color: white; text-style: bold-italic; text-color: #FFF; text-offset: 5px, 0px; }", addLabels)
//    
//	    genericTest("icon1", "graph { padding: 50px; } node { text-alignment: at-right; text-padding: 3px, 2px; text-background-mode: rounded-box; text-background-color: #222; text-color: #DDD; icon-mode: at-left; icon: url('data/Smiley_016.png');} node#A {text-alignment:at-left; text-offset: -4px, 0px;}", addLabels)
//	    
//	    genericTest("size_mode1", "node { size-mode: fit; shape: rounded-box; fill-color: white; stroke-mode: plain; padding: 3px, 2px; }", addLabels)
//	    genericTest("size_mode2", "graph { padding: 50px; } node { size-mode: fit; shape: rounded-box; fill-color: white; stroke-mode: plain; padding: 5px, 4px; icon-mode: at-left; icon: url('data/Smiley_032.png'); }", addLabels, enlarge)
//	    
//	    genericTest("size1", "node { shape: circle; stroke-mode: plain; stroke-color: #777; } node#A { size: 20px; fill-color: #FB2; } node#B { size: 40px, 20px; fill-color: #2BF; } node#C { size: 20px, 40px; fill-color: #B2F; }")
	    
//	    genericTest("node_shape1", "node { shape: circle; fill-color: #EEE; stroke-mode: plain; stroke-color: #333; size:30px; } node#A { size: 20px, 30px; } node#B { size: 30px, 20px; } edge { fill-color: #333; }")
//	    genericTest("node_shape2", "node { shape: box; fill-color: #EEE; stroke-mode: plain; stroke-color: #333; size:30px; } node#A { size: 20px, 30px; } node#B { size: 30px, 20px; } edge { fill-color: #333; }")
//	    genericTest("node_shape3", "node { shape: rounded-box; fill-color: #EEE; stroke-mode: plain; stroke-color: #333; size:30px; } node#A { size: 20px, 30px; } node#B { size: 30px, 20px; } edge { fill-color: #333; }")
//	    genericTest("node_shape4", "node { shape: diamond; fill-color: #EEE; stroke-mode: plain; stroke-color: #333; size:30px; } node#A { size: 20px, 30px; } node#B { size: 30px, 20px; } edge { fill-color: #333; }")
//	    genericTest("node_shape5", "node { shape: cross; fill-color: #EEE; stroke-mode: plain; stroke-color: #333; size:30px; } node#A { size: 20px, 30px; } node#B { size: 30px, 20px; } edge { fill-color: #333; }")
//	    genericTest("node_shape6", "node { shape: freeplane; fill-color: white; stroke-mode: plain; size-mode: fit; } edge { shape: freeplane; }", addLabels, move)
//	    genericTest("node_shape7", "node { shape: freeplane; fill-color: white; stroke-mode: plain; size-mode: fit; } edge { shape: freeplane; }", addLabels, enlarge, freeplane)
//	    genericTest("node_shape8", "node { shape: freeplane; fill-color: white; stroke-mode: plain; size-mode: fit; stroke-width: 3px; stroke-color: #333; } edge { shape: freeplane; size: 3px; fill-color: #444; } node#A { stroke-color: red; icon-mode: at-left; icon: url('data/Smiley_016.png'); } node#B { stroke-color: red; } edge#\"A--B\" { fill-color: red; }", addLabels, enlarge, freeplane)
	    
//	    genericTest("edge_shape1", "edge { shape: angle; arrow-shape: none; size: 10px; } edge#\"A--B\"{ fill-color: #92F; } edge#\"B--C\"{ fill-color: #C2D; } edge#\"C--A\"{ fill-color: #74F; }", addDirection )
//	    genericTest("edge_shape2", "edge { shape: angle; arrow-shape: none; size: 15px; fill-color: #444; } node { size: 20px; fill-color: #777; stroke-mode: plain; stroke-color: #333; }", addDirection )
//	    genericTest("edge_shape3", "edge { shape: cubic-curve; }", move)
//	    genericTest("edge_shape4", "edge { shape: blob; size: 3px; fill-color: #444; } node { size: 20px; fill-color: #444; }", move, enlarge)
//	    genericTest("edge_shape5", "edge { shape: blob; size: 3px; arrow-shape: none; } node { size: 20px; }", move, addDirection, enlarge)
	    
//	    genericTest("arrow_shape1", "edge { arrow-shape: arrow; } node#A { fill-color: red; } node#B { fill-color: #FA0; } node#C { fill-color: #04F; }", addDirection)
//	    genericTest("arrow_shape1b", "edge { arrow-shape: arrow; arrow-size: 20px, 4px; } node#A { fill-color: red; } node#B { fill-color: #FA0; } node#C { fill-color: #04F; }", addDirection)
//	    genericTest("arrow_shape1c", "edge { arrow-shape: arrow; arrow-size: 10px, 10px; } node#A { fill-color: red; } node#B { fill-color: #FA0; } node#C { fill-color: #04F; }", addDirection)
//	    genericTest("arrow_shape2", "edge { arrow-shape: circle; fill-color: #444; arrow-size: 8px; } edge#\"C--A\" {fill-color: #F00;} edge#\"B--C\" {fill-color: #FA0;} edge#\"A--B\" {fill-color: #555;} node { stroke-mode: plain; stroke-color: #333; fill-color: #CCC; }", addDirection)
//	    genericTest("arrow_shape3", "edge { arrow-shape: diamond; fill-color: #444; arrow-size: 8px, 4px; } edge#\"C--A\" {fill-color: #0AF;} edge#\"B--C\" {fill-color: #555;} edge#\"A--B\" {fill-color: #FA0;} node { stroke-mode: plain; stroke-color: #333; fill-color: #CCC; }", addDirection)
//	    genericTest("arrow_shape4", "node { size: 25px; stroke-mode: plain; stroke-color: #444; fill-color: #777; } edge { size: 2px; fill-color: #444; arrow-shape: image; arrow-image: url('data/Smiley_016.png'); }", addDirection)
	    
//	    genericTest("sprite_shape1", "sprite { shape: pie-chart; fill-color: #FC0, #F00, #03F, #A0F; size: 20px; }", addSprites)
//	    genericTest("sprite_shape2", "sprite { shape: flow; size: 5px; z-index: 0; } sprite#S1 { fill-color: #373; } sprite#S2 { fill-color: #393; } sprite#S3 { fill-color: #3B3; }", addSprites)
//	    genericTest("sprite_shape3", "sprite { shape: flow; size: 5px; z-index: 0; } sprite#S1 { fill-color: #DA3; } sprite#S4 { fill-color: #FF3; } sprite#S2 { fill-color: #5FA; } sprite#S5 { fill-color: #3FF; } sprite#S3 { fill-color: #57F; } sprite#S6 { fill-color: #93F; }", addSprites, moveSprites, addMoreSprites)
//	    genericTest("sprite_shape4", "sprite { shape: arrow; sprite-orientation: projection; fill-color: #C816; size: 20px; stroke-mode: plain; stroke-color: #333; } sprite#S4 { fill-color: #922; sprite-orientation: projection; size: 16px, 8px; } sprite#S5 { fill-color: #922; size: 16px, 8px; sprite-orientation: to; } sprite#S6 { fill-color: #922; size: 16px, 8px; sprite-orientation: from; }", addSprites, addMoreSprites, moveMoreSprites)
	    
	    genericTest("firstStyleSheet", "node#A { shape: box; size: 15px, 20px; fill-mode: plain; fill-color: red; stroke-mode: plain; stroke-color: blue; text-alignment: center; }", addSimpleLabels)
	}
	
	type MoreActions = (Graph,Viewer,View)=>Unit
	
	def addLabels(g:Graph, vv:Viewer, v:View) {
	     g.getNode[Node]("A").setAttribute("ui.label", "Node A")
	     g.getNode[Node]("B").setAttribute("ui.label", "Node B")
	     g.getNode[Node]("C").setAttribute("ui.label", "Node C")
	}
	
	def addSimpleLabels(g:Graph, vv:Viewer, v:View) {
	     g.getNode[Node]("A").setAttribute("ui.label", "A")
	     g.getNode[Node]("B").setAttribute("ui.label", "B")
	     g.getNode[Node]("C").setAttribute("ui.label", "C")
	}
	
	def enlarge(g:Graph, vv:Viewer, v:View) {
	    v.resizeFrame(300, 250)
	    Thread.sleep(200)
	}
	
	def move(g:Graph, vv:Viewer, v:View) {
	    g.getNode[Node]("B").setAttribute("xyz", Array[Double](1, 0.5, 0))
	}
	
	def addDirection(g:Graph, vv:Viewer, v:View) {
	    g.removeEdge("A--B")
	    g.removeEdge("B--C")
	    g.removeEdge("C--A")
	    g.addEdge("A--B", "A", "B", true)
	    g.addEdge("B--C", "B", "C", true)
	    g.addEdge("C--A", "C", "A", true)
	}
	
	def addSprites(g:Graph, vv:Viewer, v:View) {
		val sm = new SpriteManager(g)
		val s1 = sm.addSprite("S1")
		val s2 = sm.addSprite("S2")
		val s3 = sm.addSprite("S3")
		
		s1.attachToEdge("A--B")
		s2.attachToEdge("B--C")
		s3.attachToEdge("C--A")
		s1.setPosition(0.5)
		s2.setPosition(0.5)
		s3.setPosition(0.5)
		s1.addAttribute("ui.pie-values", 0.2.asInstanceOf[AnyRef], 0.3.asInstanceOf[AnyRef], 0.4.asInstanceOf[AnyRef], 0.1.asInstanceOf[AnyRef])
		s2.addAttribute("ui.pie-values", 0.5.asInstanceOf[AnyRef], 0.2.asInstanceOf[AnyRef], 0.1.asInstanceOf[AnyRef], 0.2.asInstanceOf[AnyRef])
		s3.addAttribute("ui.pie-values", 0.3.asInstanceOf[AnyRef], 0.3.asInstanceOf[AnyRef], 0.3.asInstanceOf[AnyRef], 0.1.asInstanceOf[AnyRef])
	}
	
	def moveSprites(g:Graph, vv:Viewer, v:View) {
	    val sm = new SpriteManager(g)
	    val s1 = sm.getSprite("S1")
	    val s2 = sm.getSprite("S2")
	    val s3 = sm.getSprite("S3")
	    
	    s1.setPosition(0.8)
	    s3.setPosition(0.3)
	}
	
	def addMoreSprites(g:Graph, vv:Viewer, v:View) {
	    val sm = new SpriteManager(g)
	    val s1 = sm.getSprite("S1")
	    val s2 = sm.getSprite("S2")
	    val s3 = sm.getSprite("S3")
		val s4 = sm.addSprite("S4")
		val s5 = sm.addSprite("S5")
		val s6 = sm.addSprite("S6")
		
		s4.attachToEdge("A--B")
		s5.attachToEdge("B--C")
		s6.attachToEdge("C--A")
		s1.setPosition(Units.PX, 0.8, 2, 0)
		s2.setPosition(Units.PX, 0.5, 2, 0)
		s3.setPosition(Units.PX, 0.3, 2, 0)
		s4.setPosition(Units.PX, 0.5, -2, 0)
		s5.setPosition(Units.PX, 0.5, -2, 0)
		s6.setPosition(Units.PX, 0.5, -2, 0)
	}
	
	def moveMoreSprites(g:Graph, vv:Viewer, v:View) {
	    val sm = new SpriteManager(g)
	    val s1 = sm.getSprite("S1")
	    val s2 = sm.getSprite("S2")
	    val s3 = sm.getSprite("S3")
		val s4 = sm.getSprite("S4")
		val s5 = sm.getSprite("S5")
		val s6 = sm.getSprite("S6")
	    
		s1.setPosition(Units.PX, 0.5, 0, 0)
		s2.setPosition(Units.PX, 0.5, 0, 0)
		s3.setPosition(Units.PX, 0.5, 0, 0)
		s4.setPosition(Units.PX, 0.8, 16, 0)
		s5.setPosition(Units.PX, 0.2, 16, 0)
		s6.setPosition(Units.PX, 0.4, 16, 0)
	}
	
	def freeplane(g:Graph, vv:Viewer, v:View) {
	    g.removeEdge("C--A")
	    g.removeEdge("B--C")
//	    g.addNodes("D", "E", "F")
	    g.addNode("D"); g.addNode("E"); g.addNode("F")
	    g.addEdge("AC", "A", "C")
	    g.addEdge("AD", "A", "D")
	    g.addEdge("AE", "A", "E")
	    g.addEdge("AF", "A", "F")
	    g.getNode[Node]("A").setAttribute("xyz", Array[Double](0, 0, 0))
	    g.getNode[Node]("B").setAttribute("xyz", Array[Double](-2, 1, 0))
	    g.getNode[Node]("C").setAttribute("xyz", Array[Double](-2, 0, 0))
	    g.getNode[Node]("D").setAttribute("xyz", Array[Double](-2, -1, 0))
	    g.getNode[Node]("E").setAttribute("xyz", Array[Double](2, 0.7, 0))
		g.getNode[Node]("F").setAttribute("xyz", Array[Double](2, -0.7, 0))
		g.getNode[Node]("D").setAttribute("label", "Node D")
		g.getNode[Node]("E").setAttribute("label", "Node E")
		g.getNode[Node]("F").setAttribute("label", "Node F")
	}
	
	def genericTest(title:String, stylesheet:String, more:MoreActions*) {
	    val graph = new MultiGraph("ui")
	    val viewer = graph.display(false)
	    val view = viewer.getView(Viewer.DEFAULT_VIEW_ID)
	    view.resizeFrame(200, 150)
	    Thread.sleep(200)
	    graph.addAttribute("ui.title", title)
	    graph.addAttribute("ui.quality")
	    graph.addAttribute("ui.antialias")
	    graph.addAttribute("ui.stylesheet", stylesheet)
//	    graph.addNodes("A", "B", "C")
//	    graph.addEdges("A", "B", "C", "A")
	    graph.addNode("A"); graph.addNode("B"); graph.addNode("C")
	    graph.addEdge("AB", "A", "B"); graph.addEdge("BC", "B", "C"); graph.addEdge("CA", "C", "A")
	    graph.getNode[Node]("A").setAttribute("xyz", Array[Double](-1, 0, 0))
	    graph.getNode[Node]("B").setAttribute("xyz", Array[Double]( 1, 0, 0))
	    graph.getNode[Node]("C").setAttribute("xyz", Array[Double]( 0, 1, 0))
	    more.foreach { action => action(graph, viewer, view) }
	    graph.addAttribute("ui.screenshot", "%s.png".format(title))
	    val out = new PrintStream("%s.css".format(title))
	    out.print(stylesheet)
	    out.flush
	    out.close
	}
}