package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt._
import java.awt.geom._

import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._

import org.graphstream.ui.sgeom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

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

//class SpriteFlowShape extends Shape
//	with FillableLine
//	with StrokableLine
//	with ShadowableLine
//	with Decorable {
//
// 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
// 	  	configureFillableConnector( style, camera, element )
// 	  	configureShadowableConnector( style, camera )
// 	  	configureStrokableConnector( style, camera )
// 	  	configureDecorable( style, camera )
// 	}
//  
//	protected def make( g:Graphics2D, camera:Camera ) {
//		val from = info.points(0)
//		val to   = info.points(3)
//		if( info.isCurve ) {
//			val ctrl1 = info.points(1)
//			val ctrl2 = info.points(2)
//			val curve = new CubicCurve2D.Float
//			theShape = curve
//			curve.setCurve( from.x, from.y, ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, to.x, to.y )
//		} else {
//			val line = new Line2D.Float
//			theShape = line
//			line.setLine( from.x, from.y, to.x, to.y )
//		} 
//	}
//	protected def makeShadow( g:Graphics2D, camera:Camera ) {
//		var x0 = info.points(0).x + theShadowOff.x
//		var y0 = info.points(0).y + theShadowOff.y
//		var x1 = info.points(3).x + theShadowOff.x
//		var y1 = info.points(3).y + theShadowOff.y
//		
//		if( info.isCurve ) {
//			var ctrlx0 = info.points(1).x + theShadowOff.x
//			var ctrly0 = info.points(1).y + theShadowOff.y
//			var ctrlx1 = info.points(2).x + theShadowOff.x
//			var ctrly1 = info.points(2).y + theShadowOff.y
//			
//			val curve = new CubicCurve2D.Float
//			theShape = curve
//			curve.setCurve( x0, y0, ctrlx0, ctrly0, ctrlx1, ctrly1, x1, y1 )
//		} else {
//			val line = new Line2D.Float
//			theShape = line
//			line.setLine( x0, y0, x1, y1 )
//		} 
//	}
// 
//	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
// 		makeShadow( g, camera )
// 		cast( g, theShape )
//	}
// 
//	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
// 		make( g, camera )
// 		stroke( g, theShape )
// 		fill( g, theSize, theShape )
// 		decor( g, camera, info.iconAndText, element, theShape )
//	}	
//}