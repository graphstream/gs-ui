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
package org.graphstream.ui.j2dviewer.renderer.shape.swing

import org.graphstream.ui.util.AttributeUtils
import org.graphstream.ui.geom.Point3
import java.awt.{Image, Color, Graphics2D}
import java.awt.geom.{Ellipse2D, Line2D, Path2D, CubicCurve2D, Rectangle2D, RoundRectangle2D, RectangularShape}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._
import org.graphstream.ui.j2dviewer.{Camera, Backend}
import org.graphstream.ui.j2dviewer.renderer.{Skeleton, AreaSkeleton, ConnectorSkeleton}

class CircleShape extends RectangularAreaShape {
	val theShape = new Ellipse2D.Double
}

class SquareShape extends RectangularAreaShape {
	val theShape = new Rectangle2D.Double
}

class RoundedSquareShape extends RectangularAreaShape {
	val theShape = new RoundRectangle2D.Double
 
	override def make(bck:Backend, camera:Camera ) {
		var w = theSize.x
		var h = theSize.y
		var r = if( h/8 > w/8 ) w/8 else h/8 
  
		theShape.setRoundRect( theCenter.x-w/2, theCenter.y-h/2, w, h, r, r )
	}
	override def makeShadow(bck:Backend, camera:Camera ) {
		var x = theCenter.x + theShadowOff.x
		var y = theCenter.y + theShadowOff.y
		var w = theSize.x + theShadowWidth.x * 2
		var h = theSize.y + theShadowWidth.y * 2
		var r = if( h/8 > w/8 ) w/8 else h/8 
		
		theShape.setRoundRect( x-w/2, y-h/2, w, h, r, r )
	}
}

class DiamondShape extends PolygonalShape {
	def make(bck:Backend, camera:Camera ) {
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
	
	def makeShadow(bck:Backend, camera:Camera ) {

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
	def make(bck:Backend, camera:Camera ) {
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
	
	def makeShadow(bck:Backend, camera:Camera ) {
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
	def make(bck:Backend, camera:Camera ) {
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
	
	def makeShadow(bck:Backend, camera:Camera ) {
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

class PolygonShape extends PolygonalShape with AttributeUtils {
    var theValues:Array[Point3] = null
    var minPoint:Point3 = null
    var maxPoint:Point3 = null
	var valuesRef:AnyRef = null
    
    override def configureForElement(bck:Backend, element:GraphicElement, skel:Skeleton, camera:Camera ) {
        super.configureForElement(bck, element, skel, camera)
        
        if(element.hasAttribute( "ui.points" )) {
			val oldRef = valuesRef
			valuesRef = element.getAttribute("ui.points")
			// We use valueRef to avoid
			// recreating the values array for nothing.
			if( ( theValues == null ) || ( oldRef ne valuesRef ) ) {
				theValues = getPoints(valuesRef)
				
				if(skel.isInstanceOf[AreaSkeleton]) {
				    val (min, max) = boundingBoxOfPoints(theValues)

				    minPoint = min
				    maxPoint = max
				}
			}
		
			val ninfo = skel.asInstanceOf[AreaSkeleton]
			ninfo.theSize.set(maxPoint.x-minPoint.x, maxPoint.y-minPoint.y)
			theSize.copy(ninfo.theSize)
		}
    }
    
    def make(bck:Backend, camera:Camera) {
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
    
    def makeShadow(bck:Backend, camera:Camera) {
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
}

class FreePlaneNodeShape extends RectangularAreaShape {
	val theShape = new Rectangle2D.Double
	val theLineShape = new Line2D.Double 
 
	override def make(bck:Backend, camera:Camera ) {
		var w = theSize.x
		val h = theSize.y
		val x = theCenter.x
		val y = theCenter.y

		theShape.setRect( x-w/2, y-h/2, w, h )
		
		w -= theStrokeWidth
		
		theLineShape.setLine( x-w/2, y-h/2, x+w/2, y-h/2 )
	}

	override def makeShadow(bck:Backend, camera:Camera ) {
		var x = theCenter.x + theShadowOff.x
		var y = theCenter.y + theShadowOff.y
		var w = theSize.x + theShadowWidth.x * 2
		var h = theSize.y + theShadowWidth.y * 2
		
		theShape.setRect( x-w/2, y-h/2, w, h )
		theLineShape.setLine( x-w/2, y-h/2, x+w/2, y-h/2 )
	}

	override def render(bck:Backend, camera:Camera, element:GraphicElement, skel:Skeleton) {
	    val g = bck.graphics2D
 		make(bck, camera)
 		fill(g, theShape, camera)
 		stroke(g, theLineShape)
 		decorArea(bck, camera, skel.iconAndText, element, theShape)
 	}
}