package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.graph.Node
import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.ui.geom._
import org.graphstream.algorithm.Toolkit._

import scala.util.Random
import scala.collection.JavaConversions._

object TestStars2 {
	def main(args:Array[String]) {
	    System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
	    (new TestStars2).test
	}
}

class TestStars2 {
    def test() {
        val graph  = new SingleGraph("Stars !")
        val x0     = 0.0
        val x1     = 0.0
        val width  = 100.0
        val height = 20.0
        val n      = 500
        val random = new Random
        val minDis = 4.0
        val sizeMx = 10.0
        
        graph.addAttribute("ui.stylesheet", styleSheet)
        graph.addAttribute("ui.quality")
        graph.addAttribute("ui.antialias")
        
        val viewer = graph.display(false)
        val view   = viewer.getDefaultView
        
        view.resizeFrame(1000, (1200*(height/width)).toInt)
        
        (0 until n).foreach { i =>
            val node:Node = graph.addNode("%d".format(i))
            node.setAttribute("xyz", (random.nextDouble*width).asInstanceOf[AnyRef], (random.nextDouble*height).asInstanceOf[AnyRef], 0.asInstanceOf[AnyRef])
            node.setAttribute("ui.size", (random.nextDouble()*sizeMx).asInstanceOf[AnyRef])
        }
        
        graph.getEachNode.foreach { node:Node =>
            val pos = new Point3(nodePosition(node))
            
            graph.getEachNode.foreach { otherNode:Node =>
                if(otherNode ne node) {
                    val otherPos = new Point3(nodePosition(otherNode))
                    val dist     = otherPos.distance(pos)
                    
                    if(dist < minDis) {
                        if(! node.hasEdgeBetween(otherNode.getId)) {
                            graph.addEdge("%s--%s".format(node.getId, otherNode.getId), node.getId, otherNode.getId)
                        }
                    }
                }
            }
        }
        
        //graph.addAttribute("ui.screenshot", "stars.png")
    }
    
    val styleSheet = """
			graph {
				canvas-color: black;
 				fill-mode: gradient-vertical;
 				fill-color: black, #004;
 				padding: 20px;
 			} 
			node {
				shape: circle;
    			size-mode: dyn-size;
				size: 10px;
				fill-mode: gradient-radial;
				fill-color: #FFFC, #FFF0;
				stroke-mode: none; 
				shadow-mode: gradient-radial;
				shadow-color: #FFF5, #FFF0;
				shadow-width: 5px;
				shadow-offset: 0px, 0px;
			}
			node:clicked {
				fill-color: #F00A, #F000;
			}
			node:selected {
				fill-color: #00FA, #00F0;
			}
			edge {
				shape: L-square-line;
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
}