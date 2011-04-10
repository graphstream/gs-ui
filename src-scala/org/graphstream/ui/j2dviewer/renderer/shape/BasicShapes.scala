/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
package org.graphstream.ui.j2dviewer.renderer.shape

import org.graphstream.ui.geom.Point3
import java.awt.{Image, Color, Graphics2D}
import java.awt.geom.{Ellipse2D, Line2D, Path2D, CubicCurve2D, Rectangle2D, RoundRectangle2D, RectangularShape}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._
import org.graphstream.ui.j2dviewer.Camera
import org.graphstream.ui.util.GraphMetrics
import org.graphstream.ui.j2dviewer.renderer.{ElementInfo, NodeInfo, EdgeInfo}

class CircleShape extends RectangularAreaShape {
	val theShape = new Ellipse2D.Float
}

class SquareShape extends RectangularAreaShape {
	val theShape = new Rectangle2D.Float
}

class RoundedSquareShape extends RectangularAreaShape {
	val theShape = new RoundRectangle2D.Float
 
	override def make( g:Graphics2D, camera:Camera ) {
		var w = theSize.x
		var h = theSize.y
		var r = if( h/8 > w/8 ) w/8 else h/8 
  
		theShape.setRoundRect( theCenter.x-w/2, theCenter.y-h/2, w, h, r, r )
	}
	override def makeShadow( g:Graphics2D, camera:Camera ) {
		var x = theCenter.x + theShadowOff.x
		var y = theCenter.y + theShadowOff.y
		var w = theSize.x + theShadowWidth.x * 2
		var h = theSize.y + theShadowWidth.y * 2
		var r = if( h/8 > w/8 ) w/8 else h/8 
		
		theShape.setRoundRect( x-w/2, y-h/2, w, h, r, r )
	}
}

class DiamondShape extends PolygonalShape {
	def make( g:Graphics2D, camera:Camera ) {
		val x  = theCenter.x
		val y  = theCenter.y
		val w2 = theSize.x / 2
		val h2 = theSize.y / 2

		theShape.reset
		theShape.moveTo( x - w2, y )
		theShape.lineTo( x,      y - h2 )
		theShape.lineTo( x + w2, y )
		theShape.lineTo( x,      y + h2 )
		theShape.closePath
	}
	
	def makeShadow( g:Graphics2D, camera:Camera ) {

		val x  = theCenter.x + theShadowOff.x
		val y  = theCenter.y + theShadowOff.y
		val w2 = ( theSize.x + theShadowWidth.x ) / 2
		val h2 = ( theSize.y + theShadowWidth.y ) / 2
		
		theShape.reset
		theShape.moveTo( x - w2, y )
		theShape.lineTo( x,      y - h2 )
		theShape.lineTo( x + w2, y )
		theShape.lineTo( x,      y + h2 )
		theShape.closePath
	}
}

class TriangleShape extends PolygonalShape {
	def make( g:Graphics2D, camera:Camera ) {
		val x  = theCenter.x
		val y  = theCenter.y
		val w2 = theSize.x / 2
		val h2 = theSize.y / 2
		
		theShape.reset
		theShape.moveTo( x,      y + h2 )
		theShape.lineTo( x + w2, y - h2 )
		theShape.lineTo( x - w2, y - h2 )
		theShape.closePath
	}
	
	def makeShadow( g:Graphics2D, camera:Camera ) {
		val x  = theCenter.x + theShadowOff.x
		val y  = theCenter.y + theShadowOff.y
		val w2 = ( theSize.x + theShadowWidth.x ) / 2
		val h2 = ( theSize.y + theShadowWidth.y ) / 2
		
		theShape.reset
		theShape.moveTo( x,      y + h2 )
		theShape.lineTo( x + w2, y - h2 )
		theShape.lineTo( x - w2, y - h2 )
		theShape.closePath
	}
}

class CrossShape extends PolygonalShape {
	def make( g:Graphics2D, camera:Camera ) {
		val x  = theCenter.x
		val y  = theCenter.y
		val h2 = theSize.x / 2
		val w2 = theSize.y / 2
		val w1 = theSize.x * 0.2f
		val h1 = theSize.y * 0.2f
		val w4 = theSize.x * 0.3f
		val h4 = theSize.y * 0.3f
		
		theShape.reset
		theShape.moveTo( x - w2, y + h4 )
		theShape.lineTo( x - w4, y + h2 )
		theShape.lineTo( x,      y + h1 )
		theShape.lineTo( x + w4, y + h2 )
		theShape.lineTo( x + w2, y + h4 )
		theShape.lineTo( x + w1, y )
		theShape.lineTo( x + w2, y - h4 )
		theShape.lineTo( x + w4, y - h2 )
		theShape.lineTo( x,      y - h1 )
		theShape.lineTo( x - w4, y - h2 )
		theShape.lineTo( x - w2, y - h4 )
		theShape.lineTo( x - w1, y )
		theShape.closePath
	}
	
	def makeShadow( g:Graphics2D, camera:Camera ) {
		val x  = theCenter.x + theShadowOff.x
		val y  = theCenter.y + theShadowOff.y
		val h2 = ( theSize.x + theShadowWidth.x ) / 2
		val w2 = ( theSize.y + theShadowWidth.y ) / 2
		val w1 = ( theSize.x + theShadowWidth.x ) * 0.2f
		val h1 = ( theSize.y + theShadowWidth.y ) * 0.2f
		val w4 = ( theSize.x + theShadowWidth.x ) * 0.3f
		val h4 = ( theSize.y + theShadowWidth.y ) * 0.3f
		
		theShape.reset
		theShape.moveTo( x - w2, y + h4 )
		theShape.lineTo( x - w4, y + h2 )
		theShape.lineTo( x,      y + h1 )
		theShape.lineTo( x + w4, y + h2 )
		theShape.lineTo( x + w2, y + h4 )
		theShape.lineTo( x + w1, y )
		theShape.lineTo( x + w2, y - h4 )
		theShape.lineTo( x + w4, y - h2 )
		theShape.lineTo( x,      y - h1 )
		theShape.lineTo( x - w4, y - h2 )
		theShape.lineTo( x - w2, y - h4 )
		theShape.lineTo( x - w1, y )
		theShape.closePath
	}
}

class PolygonShape extends PolygonalShape {
    var theValues:Array[Point3] = null
    var minPoint:Point3 = null
    var maxPoint:Point3 = null
	var valuesRef:AnyRef = null
    
    override def configureForElement( g:Graphics2D, element:GraphicElement, info:ElementInfo, camera:Camera ) {
        super.configureForElement(g, element, info, camera)
        
        if(element.hasAttribute( "ui.points" )) {
			val oldRef = valuesRef
			valuesRef = element.getAttribute("ui.points")
			// We use valueRef to avoid
			// recreating the values array for nothing.
			if( ( theValues == null ) || ( oldRef ne valuesRef ) ) {
				theValues = getPoints(valuesRef)
				
				if(info.isInstanceOf[NodeInfo]) {
				    val (min, max) = sizeOfPoints(theValues)

				    minPoint = min
				    maxPoint = max
				}
			}
		
			val ninfo = info.asInstanceOf[NodeInfo]
			ninfo.theSize.set(maxPoint.x-minPoint.x, maxPoint.y-minPoint.y)
			theSize.copy(ninfo.theSize)
		}
    }
    
    def make(g:Graphics2D, camera:Camera) {
        val x = theCenter.x
        val y = theCenter.y
        val n = theValues.size
        
        theShape.reset
        
        if(n > 0) {
        	theShape.moveTo(x+theValues(0).x, y+theValues(0).y)
        	for(i <- 1 until n) {
        	    theShape.lineTo(x+theValues(i).x, y+theValues(i).y)
        	}
        	theShape.closePath
        }
    }
    
    def makeShadow(g:Graphics2D, camera:Camera) {
        val n = theValues.size
        val x  = theCenter.x + theShadowOff.x
		val y  = theCenter.y + theShadowOff.y

        theShape.reset
        
        if(n > 0) {
        	theShape.moveTo(x+theValues(0).x, y+theValues(0).y)
        	for(i <- 1 until n) {
        	    theShape.lineTo(x+theValues(i).x, y+theValues(i).y)
        	}
        	theShape.closePath
        }
    }
    
// utilities
 	
    protected def sizeOfPoints(points:Array[Point3]):(Point3, Point3) = {
        var minx = Float.MaxValue
        var miny = Float.MaxValue
        var maxx = Float.MinValue
        var maxy = Float.MinValue
        
        points.foreach { p =>
        	minx = if(p.x<minx) p.x else minx
        	miny = if(p.y<miny) p.y else miny
        	maxx = if(p.x>maxx) p.x else maxx
        	maxy = if(p.y>maxy) p.y else maxy
        }
        
        (new Point3(minx, miny), new Point3(maxx, maxy))
    }
    
 	/** Try to extract an array of float values from various sources. */
 	protected def getPoints(values:AnyRef):Array[Point3] = {
 	    values match {
 			case b:Array[Point3]  => { b }
 			case b:Array[AnyRef] => {
 			    if(b.size>0) {
 			        if(b(0).isInstanceOf[Point3]) {
 			            val res = new Array[Point3](b.size)
 			            for(i<- 0 until b.size) {
 			                res(i) = b(i).asInstanceOf[Point3]
 			            }
 			            res
 			        } else if(b(0).isInstanceOf[Number]) {
 			        	val size = b.length/3
 			        	val res  = new Array[Point3](size)
 			    
 			        	for(i <- 0 until size) {
 			        		res(i) = new Point3(
 			        		        b(i*3).asInstanceOf[Number].floatValue,
 			        		        b(i*3+1).asInstanceOf[Number].floatValue,
 			        		        b(i*3+2).asInstanceOf[Number].floatValue)
 			        	}
 			        	res
 			        } else {
 			            Console.err.println("Cannot interpret ui.points elements type %s".format(b(0).getClass.getName))
 			            new Array[Point3](0)
 			        }
 			    } else {
 			        Console.err.println("ui.points array size is zero !!")
 			        new Array[Point3](0)
 			    }
 			}
 			case x => {
 			    Console.err.println("Cannot interpret ui.points contents (%s)".format(x.getClass.getName))
 			    new Array[Point3](0)
 			}
 		}
 	}
}

class FreePlaneNodeShape extends RectangularAreaShape {
	val theShape = new Rectangle2D.Float
	val theLineShape = new Line2D.Float 
 
	override def make( g:Graphics2D, camera:Camera ) {
		var w = theSize.x
		val h = theSize.y
		val x = theCenter.x
		val y = theCenter.y

		theShape.setRect( x-w/2, y-h/2, w, h )
		
		w -= theStrokeWidth
		
		theLineShape.setLine( x-w/2, y-h/2, x+w/2, y-h/2 )
	}

	override def makeShadow( g:Graphics2D, camera:Camera ) {
		var x = theCenter.x + theShadowOff.x
		var y = theCenter.y + theShadowOff.y
		var w = theSize.x + theShadowWidth.x * 2
		var h = theSize.y + theShadowWidth.y * 2
		
		theShape.setRect( x-w/2, y-h/2, w, h )
		theLineShape.setLine( x-w/2, y-h/2, x+w/2, y-h/2 )
	}

	override def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( g, camera )
 		fill( g, theShape, camera )
 		stroke( g, theLineShape )
 		decorArea( g, camera, info.iconAndText, element, theShape )
 	}
}