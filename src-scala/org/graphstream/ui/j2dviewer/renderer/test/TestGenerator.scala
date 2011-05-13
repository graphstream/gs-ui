package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.algorithm.generator.WattsStrogatzGenerator
import org.graphstream.graph.Graph
import org.graphstream.graph.implementations.DefaultGraph
import org.graphstream.algorithm.generator.LobsterGenerator
import org.graphstream.algorithm.Toolkit._

object TestGenerator {
	def main(args:Array[String]):Unit = {
	    System.setProperty( "gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer" )
	    (new TestGenerator()).test2
	}
}


class TestGenerator {
	def test() {
		val graph = new DefaultGraph("g")
        val gen   = new LobsterGenerator()
        val size  = 1000
        
        graph.addAttribute("ui.antialias")
        graph.addAttribute("ui.stylesheet", "edge { fill-color: grey; } node { size: 5px; fill-color: #222; }")
        gen.addSink(graph)
        graph.display(true)
        
        gen.begin
        for(i <- 0 until size) {
                gen.nextEvents
                Thread.sleep(25)
        }
        gen.end
	}
	
	def test2() {
		val graph = new DefaultGraph("g")
        val gen   = new WattsStrogatzGenerator(40, 4, 0.2);
        val size  = 1000
        
        graph.addAttribute("ui.antialias")
        graph.addAttribute("ui.stylesheet", "edge { fill-color: grey; } node { size: 5px; fill-color: #222; }")
        gen.addSink(graph)
        graph.display(false)
        
        gen.begin
        while(gen.nextEvents) {
            //Thread.sleep(250)
        }
        gen.end
        
        println("Finished diameter = %f".format(diameter(graph)))
	}
}