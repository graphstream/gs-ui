package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.graph._
import org.graphstream.graph.implementations._

import org.graphstream.ScalaGS._

object TutorialDiagrams {
	def main(args:Array[String]) {
	    System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
	    (new TutorialDiagrams).diagrams
	}
}


class TutorialDiagrams {
	
    type Populator = (Graph)=>(Int,Int)
    
    def diagrams() {
        diagram("diagram1", styleSheet1, diagram1)
        diagram("diagram1b", styleSheet1, diagram1b)
        diagram("diagram2", styleSheet1, diagram2)
        diagram("diagram3", styleSheet3, diagram3)
    }
    
    def diagram(title:String, styleSheet:String, populate:Populator) {
        val graph = new MultiGraph(title)
        
        graph.addAttribute("ui.quality")
        graph.addAttribute("ui.antialias")
        graph.addAttribute("ui.stylesheet", styleSheet)
        
        val (width, height) = populate(graph)
        
        val viewer = graph.display(false);
        val view   = viewer.getDefaultView
        view.resizeFrame(width, height)
        graph.addAttribute("ui.screenshot", "%s.png".format(title))
    }

    def diagram1(graph:Graph) = {
    	val G:Node = graph.addNode("Graph")
        val V:Node = graph.addNode("Viewer")
        val E:Edge = graph.addEdge("G->V", "Graph", "Viewer", true)
        
        G("xyz") = (0, 0, 0)
        V("xyz") = (1, 0, 0)
        G("ui.label") = "Graph"
        V("ui.label") = "Viewer"
        
        (500, 250)
    }

    def diagram1b(graph:Graph) = {
    	val G:Node = graph.addNode("Graph")
        val V:Node = graph.addNode("Viewer")
        val B1:Node = graph.addNode("bidon1")
        val B2:Node = graph.addNode("bidon2")
        
        graph.addEdge("G->bidon1", "Graph", "bidon1", true)
        graph.addEdge("bidon1->V", "bidon1", "Viewer", true)
        graph.addEdge("V->bidon2", "Viewer", "bidon2", true)
        graph.addEdge("bidon2->G", "bidon2", "Graph", true)
        
        G("xyz") = (0, 0, 0)
        B1("xyz") = (0, 0.5, 0)
        V("xyz") = (1, 0.5, 0)
        B2("xyz") = (1, 0, 0)
        G("ui.label") = "Graph"
        V("ui.label") = "Viewer"
        B1("ui.class") = "invisible"
        B2("ui.class") = "invisible"
            
        (500, 370)
    }

    def diagram2(graph:Graph) = {
    	val G:Node = graph.addNode("Graph")
    	val P:Node = graph.addNode("Pipe")
        val V:Node = graph.addNode("Viewer")
        
        graph.addEdge("G->P", "Graph", "Pipe", true)
        graph.addEdge("P->V", "Pipe", "Viewer", true)
        
        G("xyz") = (0, 0, 0)
        P("xyz") = (1, 0, 0)
        V("xyz") = (2, 0, 0)
        G("ui.label") = "Graph"
        P("ui.label") = "Pipe"
        V("ui.label") = "Viewer"
        
        (500, 250)
    }
    
    def diagram3(graph:Graph) = {
    	val G:Node = graph.addNode("Graph")
        val V:Node = graph.addNode("Viewer")
        val P1:Node = graph.addNode("GtoV")
        val P2:Node = graph.addNode("VtoG")
        graph.addEdge("G->GtoV", "Graph", "GtoV", true)
        graph.addEdge("GtoV->V", "GtoV", "Viewer", true)
        graph.addEdge("VtoG<-V", "Viewer", "VtoG", true)
        graph.addEdge("G<-VtoG", "VtoG", "Graph", true)
        
        G("ui.label") = "Graph"
        P1("ui.label") = "Pipe"
        P2("ui.label") = "ViewerPipe"
        V("ui.label") = "Viewer"
            
        G("xyz")  = (-2,  0, 0)
        P1("xyz") = (-1,  1.4, 0)
        P2("xyz") = ( 1, -1.4, 0)
        V("xyz")  = ( 2,  0, 0)
        
        (800, 500)
    }
    
    val styleSheet1 = """
    		graph {
    			padding: 90px;
    		}
    		node {
    			size: 128px;
    			shape: box;
    			fill-mode: image-scaled;
    			fill-image: url('data/Source128.png');
    			text-alignment: under;
    			text-color: #DDD;
    			text-background-mode: rounded-box;
    			text-background-color: #333;
    			text-padding: 4px;
    		}
    		node#Pipe {
    			fill-image: url('data/Pipe128.png');
    		}
    		node#Viewer {
    			fill-image: url('data/Sink128.png');
    		}
    		node.invisible {
    			fill-mode: plain;
    			fill-color: #0000;
    		}
    		edge {
    			size: 4px;
    			fill-color: #979797;
    			arrow-shape: none;
    		}
    	"""
    val styleSheet3 = """
    		graph {
    			padding: 90px;
    		}
    		node {
    			size: 128px;
    			shape: box;
    			fill-mode: image-scaled;
    			fill-image: url('data/Pipe128.png');
    			text-alignment: under;
    			text-color: #DDD;
    			text-background-mode: rounded-box;
    			text-background-color: #333;
    			text-padding: 4px;
    		}
    		node#Graph {
    			fill-image: url('data/PipeUp128.png');
    		}
    		node#Viewer {
    			fill-image: url('data/PipeDown128.png');
    		}
    		node#VtoG {
    			fill-image: url('data/PipeLeft128.png');
    		}
    		edge {
    			size: 4px;
    			fill-color: #979797;
    			shape: L-square-line;
    			arrow-size: 25px, 10px;
    			arrow-shape: none;
    		}
    	"""
}