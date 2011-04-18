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

import java.awt._
import java.awt.geom._
import java.awt.image._

import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._

import org.graphstream.ui.util._
import org.graphstream.ui.sgeom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

import scala.math._

class SpriteArrowShape extends PolygonalShape with Orientable {
	
	override def configureForGroup(bck:Backend, style:Style, camera:Camera ) {
		super.configureForGroup(bck, style, camera )
		configureOrientableForGroup( style, camera )
	}
 
	override def configureForElement(bck:Backend, element:GraphicElement, info:ElementInfo, camera:Camera ) {
		super.configureForElement(bck, element, info, camera )
		configureOrientableForElement( camera, element.asInstanceOf[GraphicSprite] /* Check This XXX TODO !*/ );
	}

	def make(bck:Backend, camera:Camera ) {
		var x   = theCenter.x
		var y   = theCenter.y
		val dir = Vector2( target.x - x, target.y - y ); dir.normalize
		var per = Vector2( dir.y, -dir.x )
		
		dir.scalarMult( theSize.x )
		per.scalarMult( theSize.y / 2 )

		theShape.reset
		theShape.moveTo( x + per.x, y + per.y )
		theShape.lineTo( x + dir.x, y + dir.y )
		theShape.lineTo( x - per.x, y - per.y )
		theShape.closePath
	}
	
	def makeShadow(bck:Backend, camera:Camera ) {
		val x   = theCenter.x + theShadowOff.x
		val y   = theCenter.y + theShadowOff.y
		val dir = Vector2( target.x - x, target.y - y ); dir.normalize
		var per = Vector2( dir.y, -dir.x )
		
		dir.scalarMult( theSize.x + theShadowWidth.x )
		per.scalarMult( ( theSize.y + theShadowWidth.y ) / 2 )

		theShape.reset
		theShape.moveTo( x + per.x, y + per.y )
		theShape.lineTo( x + dir.x, y + dir.y )
		theShape.lineTo( x - per.x, y - per.y )
		theShape.closePath
	}
}

class OrientableSquareShape extends OrientableRectangularAreaShape {
	val theShape:RectangularShape = new Rectangle2D.Double
}
/*
class OrientableRoundedSquareShape extends OrientableRectangularAreaShape {
	var theShape = new RoundRectangle2D.Double
}
*/
class SpriteFlowShape
	extends Shape
	with FillableLine
	with StrokableLine
	with ShadowableLine
	with Decorable {
	
	var theSize = 0.0
	var along = 0.0
	var offset = 0.0
	var edgeInfo:EdgeInfo = null
	var theShape = new GeneralPath
	var reverse = false
	
	def configureForGroup(bck:Backend, style:Style, camera:Camera ) {
		configureFillableLineForGroup( style, camera )
		configureStrokableForGroup( style, camera )
		configureShadowableLineForGroup( style, camera )
		configureDecorableForGroup( style, camera )

		theSize = camera.metrics .lengthToGu( style.getSize, 0 )
		reverse = ( style.getSpriteOrientation == SpriteOrientation.FROM )
	}
	
	def configureForElement(bck:Backend, element:GraphicElement, info:ElementInfo, camera:Camera ) {
		val sprite = element.asInstanceOf[GraphicSprite]
		
		if( sprite.isAttachedToEdge ) {
			val edge = sprite.getEdgeAttachment
			
			configureFillableLineForElement( element.getStyle, camera, element )
			configureDecorableForElement( bck.graphics2D, camera, element, info )
		
			if( element.hasAttribute( "ui.size" ) )
				theSize = camera.metrics.lengthToGu( StyleConstants.convertValue( element.getAttribute( "ui.size" ) ) )
			
			along    = element.getX
			offset   = camera.metrics.lengthToGu( element.getY, sprite.getUnits )
			edgeInfo = edge.getAttribute( ElementInfo.attributeName ).asInstanceOf[EdgeInfo]
		} else {
			edgeInfo = null
		}
	}
	
	def make(bck:Backend, camera:Camera ) { make(bck.graphics2D, camera, 0, 0 ) }
	
	def makeShadow(bck:Backend, camera:Camera ) { make(bck.graphics2D, camera, theShadowOff.x, theShadowOff.y ) }
		
	def make( g:Graphics2D, camera:Camera, shx:Double, shy:Double ) {
		// EdgeInfo contains a way to compute points perpendicular to the shape, however here
	    // we only need to compute the perpendicular vector once, hence this code.
	    
	    if(edgeInfo ne null) {
	        if(edgeInfo.isCurve) {
				var P0  = if(reverse) edgeInfo(3) else edgeInfo(0)
				val P1  = if(reverse) edgeInfo(2) else edgeInfo(1)
				val P2  = if(reverse) edgeInfo(1) else edgeInfo(2)
				var P3  = if(reverse) edgeInfo(0) else edgeInfo(3)
				val inc = 0.01
				var t   = 0.0
				val dir = Vector2(P3.x-P0.x, P3.y-P0.y)
				val per = Vector2(dir.y + shx, -dir.x + shy)
				
				per.normalize
				per.scalarMult(offset)
				theShape.reset
				theShape.moveTo(P0.x + per.x, P0.y + per.y)
				
				while(t <= along) {
					theShape.lineTo(
						CubicCurve.eval(P0.x + per.x, P1.x + per.x, P2.x + per.x, P3.x + per.x, t),
						CubicCurve.eval(P0.y + per.y, P1.y + per.y, P2.y + per.y, P3.y + per.y, t)
					)
					
					t += inc
				}
	        } else if(edgeInfo.isPoly) {
	            val P0           = if(reverse) edgeInfo.to   else edgeInfo.from
	            val P1           = if(reverse) edgeInfo.from else edgeInfo.to
	            var a            = if(reverse) 1-along else along
	            var (i, sum, ps) = edgeInfo.wichSegment(a)
				val dir          = Vector2(P1.x-P0.x, P1.y-P0.y)
				val per          = Vector2(dir.y + shx, -dir.x + shy)
	            
				per.normalize
				per.scalarMult(offset)
				
	            theShape.reset
				if(reverse) {
Console.err.println("reverse")
				    val n = edgeInfo.size
	                sum = edgeInfo.length - sum
	                ps  = 1-ps
	                theShape.moveTo(P1.x+per.x, P1.y+per.y)
	                for(j <- n-2 until i by -1) {
	                	theShape.lineTo(edgeInfo(j).x + per.x, edgeInfo(j).y + per.y)
	                }
	                val PX = edgeInfo.pointOnShape(along)
	                theShape.lineTo(PX.x+per.x, PX.y+per.y)
	            } else {
	                theShape.moveTo(P0.x+per.x, P0.y+per.y)
	                for(j <- 1 to i) {
	                	theShape.lineTo(edgeInfo(j).x + per.x, edgeInfo(j).y + per.y)
	                }
	                val PX = edgeInfo.pointOnShape(along)
	                theShape.lineTo(PX.x+per.x, PX.y+per.y)
	            }
	        } else {
	            val P0  = if(reverse) edgeInfo.to   else edgeInfo.from
	            val P1  = if(reverse) edgeInfo.from else edgeInfo.to
				val dir = Vector2(P1.x-P0.x, P1.y-P0.y)
				val per = Vector2(dir.y + shx, -dir.x + shy)

				per.normalize
				per.scalarMult(offset)
				dir.scalarMult(along)

				theShape.reset
				theShape.moveTo(P0.x + per.x, P0.y + per.y)
				theShape.lineTo(P0.x + dir.x + per.x, P0.y + dir.y + per.y)
	        }
	    }
	    
//		if( edgeInfo != null ) {
//			var P0  = if( reverse ) edgeInfo.to else edgeInfo.from
//			var P3  = if( reverse ) edgeInfo.from else edgeInfo.to
//			val dir = Vector2( P3.x-P0.x, P3.y-P0.y )
//			val per = Vector2( dir.y + shx, -dir.x + shy )
//			
//			per.normalize
//			per.scalarMult( offset )
//			theShape.reset
//			theShape.moveTo( P0.x + per.x, P0.y + per.y  )
//			
//			if( edgeInfo.isCurve ) {
//				val P1  = if( reverse ) edgeInfo(2) else edgeInfo(1)
//				val P2  = if( reverse ) edgeInfo(1) else edgeInfo(2)
//				val inc = 0.01f
//				var t   = 0f
//				
//				while( t <= along ) {
//					theShape.lineTo(
//						CubicCurve.eval( P0.x + per.x, P1.x + per.x, P2.x + per.x, P3.x + per.x, t ),
//						CubicCurve.eval( P0.y + per.y, P1.y + per.y, P2.y + per.y, P3.y + per.y, t )
//					)
//					
//					t += inc
//				}
//			} else {
//				val dir = Vector2( P3.x-P0.x, P3.y-P0.y )
//				dir.scalarMult( along )
//				theShape.lineTo( P0.x + dir.x + per.x, P0.y + dir.y + per.y )
//			}
//		}
	}
	
	def renderShadow(bck:Backend, camera:Camera, element:GraphicElement, info:ElementInfo ) {
		if( edgeInfo != null ) {
			makeShadow(bck, camera )
			cast(bck.graphics2D, theShape )
		}
 	}
  
 	def render(bck:Backend, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		if( edgeInfo != null ) {
 		    val g = bck.graphics2D
 			make(bck, camera )
 			stroke( g, theShape )
 			fill( g, theSize, theShape )
 			decorConnector( g, camera, info.iconAndText, element, theShape )
 		}
 	}
}