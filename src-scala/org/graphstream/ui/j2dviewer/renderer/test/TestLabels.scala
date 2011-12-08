package org.graphstream.ui.j2dviewer.renderer.test
import org.graphstream.graph.Node
import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.graph.Edge

object TestLabels {
	def main(args:Array[String]) {
	    System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
	    (new TestLabels).test
	}
}

class TestLabels {
    def test() = {
        val graph = new SingleGraph("test labels")
        
        graph.addAttribute("ui.stylesheet", styleSheet)
        graph.display
        
        val A:Node = graph.addNode("A")
        val B:Node = graph.addNode("B")
        val C:Node = graph.addNode("C")

        val AB:Edge = graph.addEdge("AB", "A", "B")
        val BC:Edge = graph.addEdge("BC", "B", "C")
        val CA:Edge = graph.addEdge("CA", "C", "A")
        
        AB.addAttribute("ui.label", "AB")
        BC.addAttribute("ui.label", "BC")
        CA.addAttribute("ui.label", "CA")
        
        var foo = true
        
        while(true) {
            if(foo) {
                AB.addAttribute("ui.class", "foo")
            } else {
                AB.removeAttribute("ui.class")
            }
            
            foo = !foo
            sleep(1000)
            Console.err.println("foo = %b".format(foo))
        }
    }
    
    protected def sleep(ms:Long) = {
    	Thread.sleep(ms)
    }
    
    protected val styleSheet =
        """
        	edge {
        		text-color: red;
        		text-background-mode: rounded-box;
        		text-padding: 5px;
    		}
        	edge.foo {
        		text-background-color: yellow;
        text-color: blue;
    		}
        """
}