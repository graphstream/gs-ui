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

import org.graphstream.algorithm.generator.WattsStrogatzGenerator
import org.graphstream.graph.Graph
import org.graphstream.graph.implementations.DefaultGraph
import org.graphstream.algorithm.generator.LobsterGenerator
import org.graphstream.algorithm.Toolkit._
import org.graphstream.algorithm.generator.BarabasiAlbertGenerator
import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.algorithm.BetweennessCentrality
import scala.collection.JavaConversions._
import org.graphstream.algorithm.generator.GridGenerator
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator
import org.graphstream.algorithm.generator.RandomGenerator
import org.graphstream.algorithm.generator.RandomEuclideanGenerator

object TestGenerator {
	def main(args:Array[String]):Unit = {
	    System.setProperty( "gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer" )
	    //(new TestGenerator()).testBetweeness(10)
	    //(new TestGenerator()).testBarabasiAlbert(1, 100)
	    //(new TestGenerator()).testBarabasiAlbert(2, 100)
	    //(new TestGenerator()).testBarabasiAlbert(3, 100)
	    //(new TestGenerator()).testBarabasiAlbert(6, 100)
	    //(new TestGenerator()).testGrid(1, 10, false, false, false)
	    //(new TestGenerator()).testGrid(2, 10, true, false, false)
	    //(new TestGenerator()).testGrid(3, 10, true, true, true)
	    //(new TestGenerator()).testGrid(4, 10, false, true, true)
	    //(new TestGenerator()).testDorogovtsevMendes(10)
	    //(new TestGenerator()).testDorogovtsevMendes(50)
	    //(new TestGenerator()).testDorogovtsevMendes(300)
	    //(new TestGenerator()).testWattsStrogatz(20, 4, 0.4)
	    (new TestGenerator()).testRandomEuclidean(1000)
	}
}


class TestGenerator {
    def testRandomEuclidean(size:Int) {
		val graph = new SingleGraph("random euclidean")
		val gen = new RandomEuclideanGenerator
		gen.addSink(graph)
		gen.begin
		for(i <- 0 until size) {
			gen.nextEvents
		}
		gen.end

    	graph.addAttribute("ui.stylesheet", reStyleSheet)
    	graph.addAttribute("ui.quality")
    	graph.addAttribute("ui.antialias")
    	val viewer = graph.display(false)
    	val view   = viewer.getDefaultView

    	view.resizeFrame(600, 600)
    	waitScreenShot(500, "randomEuclidean.png", graph)
    }
    
    val reStyleSheet = """
    		node {
    			size: 4px;
    			fill-color: #3338;
    		}
    		edge {
    			size: 0.5px;
    			fill-color: #5558;
    		}
    	"""
    
    def testRandom(size:Int, k:Int) {
        val graph = new SingleGraph("random")
        val gen = new RandomGenerator(k);
        
        gen.addSink(graph)
        gen.begin
        for(i <- 0 until size)
            gen.nextEvents
        gen.end
        
    	graph.addAttribute("ui.stylesheet", gridStyleSheet)
    	graph.addAttribute("ui.quality")
    	graph.addAttribute("ui.antialias")
    	val viewer = graph.display(true)
    	val view   = viewer.getDefaultView

    	view.resizeFrame(300, 300)
    	waitScreenShot(500, "random.png", graph)
    }
    
    def testWattsStrogatz(size:Int, k:Int, beta:Double) {
		val graph = new SingleGraph("This is a small world!")
		val gen = new WattsStrogatzGenerator(size, k, beta)
		
		gen.addSink(graph)
		gen.begin
		while(gen.nextEvents) {}
		gen.end
		
    	graph.addAttribute("ui.stylesheet", gridStyleSheet)
    	graph.addAttribute("ui.quality")
    	graph.addAttribute("ui.antialias")
    	val viewer = graph.display(false)
    	val view   = viewer.getDefaultView

    	view.resizeFrame(300, 300)
    	waitScreenShot(500, "wattsStrogatz.png", graph)
    }
    
    def testDorogovtsevMendes(size:Int) {
        val graph = new SingleGraph("DorogovtsevMendes")
        val gen = new DorogovtsevMendesGenerator
        
        gen.addSink(graph)
        gen.begin
        for(i <- 1 until size) gen.nextEvents
        gen.end
    	
    	graph.addAttribute("ui.stylesheet", gridStyleSheet)
    	graph.addAttribute("ui.quality")
    	graph.addAttribute("ui.antialias")
    	val viewer = graph.display(true)
    	val view   = viewer.getDefaultView

    	view.resizeFrame(300, 300)
    	waitScreenShot(4000, "dorogovtsevMendes%d.png".format(size), graph)
    }
    
    def testGrid(name:Int, size:Int, cross:Boolean, tore:Boolean, layout:Boolean) {
    	val graph = new SingleGraph("betweenness")
    	val gen   = new GridGenerator(cross, tore)
        
    	gen.addSink(graph)
    	gen.begin
    	for(i <- 1 until size) gen.nextEvents
    	gen.end
    	
    	graph.addAttribute("ui.stylesheet", gridStyleSheet)
    	graph.addAttribute("ui.quality")
    	graph.addAttribute("ui.antialias")
    	val viewer = graph.display(layout)
    	val view   = viewer.getDefaultView

    	view.resizeFrame(300, 300)
    	waitScreenShot(1500, "gridGenerator%d.png".format(name), graph)
    }
    
    val gridStyleSheet = """
    		node {
    			size: 4px;
    			fill-color: #333;
    		}
    		edge {
    			fill-color: #555;
    		}
    	"""
    
    def testBetweeness(size:Int) {
    	val graph = new SingleGraph("betweenness")
    	val gen   = new GridGenerator()
//    	val gen   = new DorogovtsevMendesGenerator
    	val betw  = new BetweennessCentrality
    	
    	gen.addSink(graph)
    	gen.begin
    	for(i <- 1 until size) gen.nextEvents
    	gen.end
    	
    	betw.init(graph)
    	betw.compute
    	
    	var min = Double.MaxValue
    	var max = Double.MinValue
    	
    	graph.foreach { node => 
    	    val cb = betw.centrality(node)
    	    if(cb>max) max = cb
    	    if(cb<min) min = cb
    	}
    	
    	graph.foreach { node =>
    	    val cb = betw.centrality(node)
    	    node.setAttribute("ui.color", ((cb-min)/(max-min)).asInstanceOf[AnyRef])
    	}
    	
    	graph.addAttribute("ui.stylesheet", bwtStyleSheet)
    	graph.addAttribute("ui.quality")
    	graph.addAttribute("ui.antialias")
    	val viewer = graph.display(false)
    	val view   = viewer.getDefaultView

    	view.resizeFrame(300, 300)
    	waitScreenShot(1500, "betweennessCentrality.png", graph)
    }

    val bwtStyleSheet =  """
    		node {
    			fill-color: green, yellow, red;
    			fill-mode: dyn-plain;
    		}
    	"""

    def testBarabasiAlbert(n:Int, size:Int) {
    	val graph = new SingleGraph("betweenness")
    	val gen   = new BarabasiAlbertGenerator(n)
    	val betw  = new BetweennessCentrality
    	
    	gen.addSink(graph)
    	gen.begin
    	for(i <- 1 until size) gen.nextEvents
    	gen.end
    	
    	betw.init(graph)
    	betw.compute
    	
    	var min = Double.MaxValue
    	var max = Double.MinValue
    	
    	graph.foreach { node => 
    	    val cb = betw.centrality(node)
    	    if(cb>max) max = cb
    	    if(cb<min) min = cb
    	}
    	
    	graph.foreach { node =>
    	    val cb = betw.centrality(node)
    	    node.setAttribute("ui.color", ((cb-min)/(max-min)).asInstanceOf[AnyRef])
    	}
    	
    	graph.addAttribute("ui.stylesheet", baStyleSheet)
    	graph.addAttribute("ui.quality")
    	graph.addAttribute("ui.antialias")
    	val viewer = graph.display(true)
    	val view   = viewer.getDefaultView

    	view.resizeFrame(300, 300)
    	waitScreenShot(3000, "barabasiAlber%d.png".format(n), graph)
    }

    val baStyleSheet =  """
    		node {
    			fill-color: #444, blue, red;
    			fill-mode: dyn-plain;
    		}
    	"""
    
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
	
	def waitScreenShot(time:Long, name:String, graph:Graph) {
	    Thread.sleep(time)
    	System.err.println("3")
    	Thread.sleep(1000)
    	System.err.println("2")
    	Thread.sleep(1000)
    	System.err.println("1")
    	Thread.sleep(1000)
    	System.err.println("Photo !")
    	graph.addAttribute("ui.screenshot", name)
	}
}