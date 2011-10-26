package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.graph.Graph
import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.ui.spriteManager.Sprite
import org.graphstream.ui.spriteManager.SpriteManager
import scala.collection.JavaConversions._
import org.graphstream.graph.Edge

object TestPies {
	def main(args:Array[String]):Unit = (new TestPie).test
}

class TestPie {
	def test() {
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")

		val g = new SingleGraph("test")
		g.addNode("A")
		g.addNode("B")
		g.addEdge("AB", "A", "B")

		val sm = new SpriteManager(g)
		val pie = sm.addSprite("pie")

		g.addAttribute("ui.antialias")
		pie.addAttribute("ui.style", "shape: pie-chart; fill-color: #F00, #0F0, #00F; size: 30px;")
//		g.addAttribute("ui.stylesheet", "sprite { shape: pie-chart; fill-color: #F00, #0F0, #00F; size: 30px; } node {fill-color: red; }")
		val values = new Array[Any](3);
		values(0) = 0.3333
		values(1) = 0.3333
		values(2) = 0.3333
		pie.addAttribute("ui.pie-values", values)
		pie.attachToEdge("AB")
		pie.setPosition(0.5)
		
		g.display()

		val values2 = new Array[Any](3);
		values2(0) = 0.1
		values2(1) = 0.3
		values2(2) = 0.6
		var on = true
		
		while(true) {
		    Thread.sleep(2000)
		    if(on) {
		        values(0) = 0.1
		        values(1) = 0.3
		        values(2) = 0.6
		    } else {
		        values(0) = 0.3
		        values(1) = 0.3
		        values(2) = 0.3
		    }
		    pie.addAttribute("ui.pie-values", values)
		    
		    //pie.addAttribute("ui.pie-values", if(on) values else values2)
		    on = ! on
		}
	}
}