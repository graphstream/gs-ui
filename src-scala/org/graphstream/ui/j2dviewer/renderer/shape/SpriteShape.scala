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
	
	override def configureForGroup( g:Graphics2D, style:Style, camera:Camera ) {
		super.configureForGroup( g, style, camera )
		configureOrientableForGroup( style, camera )
	}
 
	override def configureForElement( g:Graphics2D, element:GraphicElement, info:ElementInfo, camera:Camera ) {
		super.configureForElement( g, element, info, camera )
		configureOrientableForElement( camera, element.asInstanceOf[GraphicSprite] /* Check This XXX TODO !*/ );
	}

	def make( g:Graphics2D, camera:Camera ) {
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
	
	def makeShadow( g:Graphics2D, camera:Camera ) {
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
	val theShape:RectangularShape = new Rectangle2D.Float
}
/*
class OrientableRoundedSquareShape extends OrientableRectangularAreaShape {
	var theShape = new RoundRectangle2D.Float
}
*/
class SpriteFlowShape
	extends Shape
	with FillableLine
	with StrokableLine
	with ShadowableLine
	with Decorable {
	
	var theSize = 0f
	var along = 0f
	var offset = 0f
	var edgeInfo:EdgeInfo = null
	var theShape = new GeneralPath
	var reverse = false
	
	def configureForGroup( g:Graphics2D, style:Style, camera:Camera ) {
		configureFillableLineForGroup( style, camera )
		configureStrokableForGroup( style, camera )
		configureShadowableLineForGroup( style, camera )
		configureDecorableForGroup( style, camera )

		theSize = camera.metrics .lengthToGu( style.getSize, 0 )
		reverse = ( style.getSpriteOrientation == SpriteOrientation.FROM )
	}
	
	def configureForElement( g:Graphics2D, element:GraphicElement, info:ElementInfo, camera:Camera ) {
		val sprite = element.asInstanceOf[GraphicSprite]
		
		if( sprite.isAttachedToEdge ) {
			val edge = sprite.getEdgeAttachment
			
			configureFillableLineForElement( element.getStyle, camera, element )
			configureDecorableForElement( g, camera, element, info )
		
			if( element.hasAttribute( "ui.size" ) )
				theSize = camera.metrics.lengthToGu( StyleConstants.convertValue( element.getAttribute( "ui.size" ) ) )
			
			along    = element.getX
			offset   = camera.metrics.lengthToGu( element.getY, sprite.getUnits )
			edgeInfo = edge.getAttribute( ElementInfo.attributeName ).asInstanceOf[EdgeInfo]
		} else {
			edgeInfo = null
		}
	}
	
	def make( g:Graphics2D, camera:Camera ) { make( g, camera, 0, 0 ) }
	
	def makeShadow( g:Graphics2D, camera:Camera ) { make( g, camera, theShadowOff.x, theShadowOff.y ) }
		
	def make( g:Graphics2D, camera:Camera, shx:Float, shy:Float ) {
		if( edgeInfo != null ) {
			var P0  = if( reverse ) edgeInfo.points(3) else edgeInfo.points(0)
			var P3  = if( reverse ) edgeInfo.points(0) else edgeInfo.points(3)
			val dir = Vector2( P3.x-P0.x, P3.y-P0.y )
			val per = Vector2( dir.y + shx, -dir.x + shy )
			
			per.normalize
			per.scalarMult( offset )
			theShape.reset
			theShape.moveTo( P0.x + per.x, P0.y + per.y  )
			
			if( edgeInfo.isCurve ) {
				val P1  = if( reverse ) edgeInfo.points(2) else edgeInfo.points(1)
				val P2  = if( reverse ) edgeInfo.points(1) else edgeInfo.points(2)
				val inc = 0.01f
				var t   = 0f
				
				while( t <= along ) {
					theShape.lineTo(
						CubicCurve.eval( P0.x + per.x, P1.x + per.x, P2.x + per.x, P3.x + per.x, t ),
						CubicCurve.eval( P0.y + per.y, P1.y + per.y, P2.y + per.y, P3.y + per.y, t )
					)
					
					t += inc
				}
			} else {
				val dir = Vector2( P3.x-P0.x, P3.y-P0.y )
				dir.scalarMult( along )
				theShape.lineTo( P0.x + dir.x + per.x, P0.y + dir.y + per.y )
			}
		}
	}
	
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
		if( edgeInfo != null ) {
			makeShadow( g, camera )
			cast( g, theShape )
		}
 	}
  
 	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		if( edgeInfo != null ) {
 			make( g, camera )
 			stroke( g, theShape )
 			fill( g, theSize, theShape )
 			decorConnector( g, camera, info.iconAndText, element, theShape )
 		}
 	}
}

object SpritePieChartShape {
	/** Some predefined colors. */
	val colors = Array[Color]( Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA,
			Color.CYAN, Color.ORANGE, Color.PINK )
}

class SpritePieChartShape
	extends Shape
	with Area
	with FillableMulticolored
	with Strokable 
	with Shadowable 
	with Decorable {
	
	val theShape = new Ellipse2D.Float
	
	var theValues:Array[Float] = null
	var valuesRef:AnyRef = null

	def configureForGroup( g:Graphics2D, style:Style, camera:Camera ) {
		configureAreaForGroup( style, camera )
		configureFillableMultiColoredForGroup( style, camera )
		configureStrokableForGroup( style, camera )
		configureShadowableForGroup( style, camera )
		configureDecorableForGroup( style, camera )
	}
	
	def configureForElement( g:Graphics2D, element:GraphicElement, info:ElementInfo, camera:Camera ) {
		configureDecorableForElement( g, camera, element, info )
		configureAreaForElement( g, camera, info.asInstanceOf[NodeInfo], element, theDecor )

		if( element.hasAttribute( "ui.pie-values" ) ) {
			val oldRef = valuesRef
			valuesRef = element.getAttribute( "ui.pie-values" )
			// We use valueRef to avoid
			// recreating the values array for nothing.
			if( ( theValues == null ) || ( oldRef ne valuesRef ) ) {
				theValues = getPieValues( valuesRef )
			}
		}
	}
	
	override def make( g:Graphics2D, camera:Camera ) {
		theShape.setFrameFromCenter( theCenter.x, theCenter.y, theCenter.x+theSize.x/2, theCenter.y+theSize.y/2 )
	}
	
	override def makeShadow( g:Graphics2D, camera:Camera ) {
		theShape.setFrameFromCenter( theCenter.x+theShadowOff.x, theCenter.y+theShadowOff.y,
				theCenter.x+(theSize.x+theShadowWidth.x)/2, theCenter.y+(theSize.y+theShadowWidth.y)/2 )
	}
	
	override def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
		makeShadow( g, camera )
		cast( g, theShape )
 	}
  
 	override def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( g, camera )
 		fillPies( g, element )
 		//fill( g, theSize, theShape )
 		stroke( g, theShape )
 		decorArea( g, camera, info.iconAndText, element, theShape )
 	}
 	
 	protected def fillPies( g:Graphics2D, element:GraphicElement ) {
 		if( theValues != null ) {
	 		// we assume the pies values sum up to one. And we wont check it, its a mater of speed ;-).
	 		val arc = new Arc2D.Float
	 		var beg = 0f
	 		var end = 0f
	 		var col = 0
	 		var sum = 0f
	 		
	 		theValues.foreach { value =>
	 			end = beg + value
	 			arc.setArcByCenter( theCenter.x, theCenter.y, theSize.x/2, beg*360, value*360, Arc2D.PIE )
	 			g.setColor( fillColors( col % fillColors.length ) )
	 			g.fill( arc )
	 			beg = end
	 			sum += value
	 			col += 1
	 		}
	 		
	 		if( sum > 1.01f )
	 			Console.err.print( "[Sprite %s] The sum of values for ui.pie-value should eval to 1 at max (actually %f)%n".format( element.getId, sum ) )
 		}
 	}
 	
 // utilities
 	
 	/** Try to extract an array of float values from various sources. */
 	protected def getPieValues( values:AnyRef ):Array[Float] = {
 		values match {
 			case a:Array[AnyRef] => { 
 				val result = new Array[Float]( a.length )
 				a.map( { _ match {
 						case n:Number => n.floatValue
 						case s:String => s.toFloat
 						case _        => 0f
 					}
 				} )
 			}
 			case b:Array[Float]  => { b }
 			case c:String          => { c.split(',').map { _.toFloat } }
 			case _                 => { Array[Float]( 0 ) }
 		}
 	}
}