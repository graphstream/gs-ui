package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt._
import java.awt.geom._

import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.j2dviewer.renderer._

import org.graphstream.ui.util._
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
	
	def make( g:Graphics2D, camera:Camera ) {
		if( edgeInfo != null ) {
			var P0  = if( reverse ) edgeInfo.points(3) else edgeInfo.points(0)
			var P3  = if( reverse ) edgeInfo.points(0) else edgeInfo.points(3)
			val dir = Vector2( P3.x-P0.x, P3.y-P0.y )
			val per = Vector2( dir.y, -dir.x )
			
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
	
	def makeShadow( g:Graphics2D, camera:Camera ) {
//		val x   = theCenter.x + theShadowOff.x
//		val y   = theCenter.y + theShadowOff.y
//		val dir = Vector2( target.x - x, target.y - y ); dir.normalize
//		var per = Vector2( dir.y, -dir.x )
//		
//		dir.scalarMult( theSize.x + theShadowWidth.x )
//		per.scalarMult( ( theSize.y + theShadowWidth.y ) / 2 )
//
//		theShape.reset
//		theShape.moveTo( x + per.x, y + per.y )
//		theShape.lineTo( x + dir.x, y + dir.y )
//		theShape.lineTo( x - per.x, y - per.y )
//		theShape.closePath
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
 			decor( g, camera, info.iconAndText, element, theShape )
 		}
 	}
}

/*
class SpriteFlowShape extends LineConnectorShape {
	protected var theShape = new Line2D.Float 
	
	protected def make( g:Graphics2D, camera:Camera ) {
		make( camera, 0, 0, 0, 0 )
	}
  
	protected def make( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		if( info.isMulti > 1 || info.isLoop )	// is a loop or a multi edge
		     makeMultiOrLoop( camera, sox, soy, swx, swy )
		else makeSingle( camera, sox, soy, swx, swy )	// is a single edge.
	}
 
	protected def makeSingle( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		val fromx   = info.points(0).x + sox
		val fromy   = info.points(0).y + soy
		val tox     = info.points(3).x + sox
		val toy     = info.points(3).y + soy
		val mainDir = new Vector2( info.points(0), info.points(3) )
		val length  = mainDir.length
		val angle   = mainDir.y / length
		var c1x     = 0f
		var c1y     = 0f
		var c2x     = 0f
		var c2y     = 0f
		
		if( angle > 0.707107f || angle < -0.707107f ) {
			// North or south.
			c1x = fromx + mainDir.x / 2
			c2x = c1x
			c1y = fromy
			c2y = toy
		} else {
			// East or west.
			c1x = fromx
			c2x = tox
			c1y = fromy + mainDir.y / 2
			c2y = c1y
		}

		theShape.reset
		theShape.moveTo( fromx, fromy )
		theShape.curveTo( c1x, c1y, c2x, c2y, tox, toy )

		// Set the connector as a curve.
		
		if( sox == 0 && soy == 0 ) {
			info.isCurve = true
			info.points(0).set( fromx, fromy )
			info.points(1).set( c1x, c1y )
			info.points(2).set( c2x, c2y )
			info.points(3).set( tox, toy )
		}
	}
 
	protected def makeMultiOrLoop( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		if( info.isLoop )
			 makeLoop( camera, sox, soy, swx, swy )
		else makeMulti( camera, sox, soy, swx, swy )
	}
	
	protected def makeMulti( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		val fromx   = info.points(0).x + sox
		val fromy   = info.points(0).y + soy
		val tox     = info.points(3).x + sox
		val toy     = info.points(3).y + soy
		val c1x     = info.points(1).x + sox
		val c1y     = info.points(1).y + soy
		val c2x     = info.points(2).x + sox
		val c2y     = info.points(2).y + soy

		theShape.reset
		theShape.moveTo( fromx, fromy )
		theShape.curveTo( c1x, c1y, c2x, c2y, tox, toy )
	}

	protected def makeLoop( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
	  	val fromx = info.points(0).x + sox
		val fromy = info.points(0).y + soy
		val tox   = info.points(3).x + sox
		val toy   = info.points(3).y + soy
		val c1x   = info.points(1).x + sox
		val c1y   = info.points(1).y + soy
		val c2x   = info.points(2).x + sox
		val c2y   = info.points(2).y + soy

		theShape.reset
		theShape.moveTo( fromx, fromy )
		theShape.curveTo( c1x, c1y, c2x, c2y, tox, toy )
	}

	protected def makeShadow( g:Graphics2D, camera:Camera ) {
		if( info.isCurve )
		     makeMultiOrLoop( camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth )
		else makeSingle( camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth )
	}
	
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		makeShadow( g, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( g, camera )
 		stroke( g, theShape )
 		fill( g, theSize, theShape )
 		decor( g, camera, info.iconAndText, element, theShape )
// 		showControlPolygon = true
// 		if( showControlPolygon ) {
//	 		val c = g.getColor();
//	 		val s = g.getStroke();
//	 		g.setStroke( new java.awt.BasicStroke( camera.metrics.px1 ) )
//	 		g.setColor( Color.red );
//	 		g.draw( theShape );
//	 		g.setStroke( s );
//	 		g.setColor( c );
//	 		showCtrlPoints( g, camera )
// 		}
	}
}
*/