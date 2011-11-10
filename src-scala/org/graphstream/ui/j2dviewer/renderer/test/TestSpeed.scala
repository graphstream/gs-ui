package org.graphstream.ui.j2dviewer.renderer.test
import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.algorithm.generator.GridGenerator

object TestSpeed {
	def main(args:Array[String]) = (new TestSpeed).test
}

class TestSpeed {
    def test() {
        //System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
    
        for(i <- 1 until 60) {
            Console.err.println("%d".format(i))
            Thread.sleep(1000)
        }
        
        val graph = new SingleGraph("simple")
        var gridg = new GridGenerator(false, false, true)
        
        graph.addAttribute("ui.log", "fps.log")
        graph.display(false)
        gridg.addSink(graph)
        gridg.begin
        gridg.nextEvents
        Thread.sleep(1000)
        for(i <- 1 until 10) {
            gridg.nextEvents
            Thread.sleep(100)
        }
        for(i <- 1 until 100) {
            gridg.nextEvents
            Thread.sleep(10)
        }
        gridg.end
        gridg.removeSink(graph)
    }
}