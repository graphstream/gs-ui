package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Image, Color, Graphics2D}
import java.awt.geom.{Ellipse2D, Line2D, Path2D, Rectangle2D, RoundRectangle2D, RectangularShape}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.sgeom.Vector2
import org.graphstream.ui.graphicGraph.{GraphicElement, GraphicEdge}
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.j2dviewer.Camera
import org.graphstream.ui.util.{GraphMetrics, CubicCurve}
import org.graphstream.ui.j2dviewer.renderer.ElementInfo

class ArrowOnEdge extends AreaOnConnectorShape {
	val theShape = new Path2D.Float()
 
// Command
 
 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
 	  	configureFillable( style, camera, element )
 	  	configureShadowable( style, camera )
 	  	configureStrokable( style, camera )
 	}
  
	protected def make( g:Graphics2D, camera:Camera ) { make( false, camera ) }
	protected def makeShadow( g:Graphics2D, camera:Camera ) { make( true, camera ) }
  
	protected def make( forShadow:Boolean, camera:Camera ) {
		if( theConnector.info.isCurve )
		     makeOnCurve( forShadow, camera )
		else makeOnLine(  forShadow, camera )
	}
 
	protected def makeOnLine( forShadow:Boolean, camera:Camera ) {
		var off = camera.evalTargetRadius( theEdge )
		val theDirection = new Vector2(
			theConnector.toPos.x - theConnector.fromPos.x,
			theConnector.toPos.y - theConnector.fromPos.y )
			
		theDirection.normalize
  
		var x    = theCenter.x - ( theDirection(0) * off )
		var y    = theCenter.y - ( theDirection(1) * off )
		val perp = new Vector2( theDirection(1), -theDirection(0) )
		
		perp.normalize
		theDirection.scalarMult( theSize.x )
		perp.scalarMult( theSize.y )
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
		}
  
		// Create a polygon.
		
		theShape.reset
		theShape.moveTo( x , y )
		theShape.lineTo( x - theDirection(0) + perp(0), y - theDirection(1) + perp(1) )		
		theShape.lineTo( x - theDirection(0) - perp(0), y - theDirection(1) - perp(1) )
		theShape.closePath
	}

	protected def makeOnCurve( forShadow:Boolean, camera:Camera ) {
		val off = camera.evalTargetRadius( theEdge.to.getStyle, theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos )
		var style = theEdge.getStyle
		
		// Express node width in percents of the edge length.

		var d  = lengthOfCurve( theConnector )
		var t1 = ( ( d - off ) / d )
			
		// Express arrow length in percents of the edge length.
			
		var t2 = ( ( d - ( off + theSize.x ) ) / d )

		// Now compute arrow coordinates and vector.
		
		var p1 = CubicCurve.eval( theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos, t1 )
		var p2 = CubicCurve.eval( theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos, t2 )
		var dir = Vector2( p1.x - p2.x, p1.y - p2.y )

		var per = Vector2( dir(1), -dir(0) )
		per.normalize
		per.scalarMult( theSize.y )
  
		// Create a polygon.
	
		theShape.reset
		theShape.moveTo( p1.x , p1.y )
		theShape.lineTo( p1.x - dir(0) + per(0), p1.y - dir(1) + per(1) )		
		theShape.lineTo( p1.x - dir(0) - per(0), p1.y - dir(1) - per(1) )
		theShape.closePath
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( true, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( false, camera )
 		stroke( g, theShape )
 		fill( g, theShape, camera )
	}
 
	protected def lengthOfCurve( c:Connector ):Float = {
		// Computing a curve real length is really heavy.
		// We approximate it using the length of the 3 line segments of the enclosing
		// control points.
		( c.fromPos.distance( c.byPos1 ) + c.byPos1.distance( c.byPos2 ) + c.byPos2.distance( c.toPos ) ) * 0.75f
	}
}

class ArrowOnEdge2 extends OrientedAreaShape {
	val theShape = new Path2D.Float()
	var theEdge:GraphicEdge = null
 
// Command
 
 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
 	  	configureFillable( style, camera, element )
 	  	configureShadowable( style, camera )
 	  	configureStrokable( style, camera )
 	}
  
	protected def make( g:Graphics2D, camera:Camera ) { make( false, camera ) }
	protected def makeShadow( g:Graphics2D, camera:Camera ) { make( true, camera ) }
  
	protected def make( forShadow:Boolean, camera:Camera ) {
		val off = camera.evalTargetRadius( theEdge )
  
		theDirection.normalize
  
		var x    = theCenter.x - ( theDirection(0) * off )
		var y    = theCenter.y - ( theDirection(1) * off )
  		val perp = new Vector2( theDirection(1), -theDirection(0) )
		
    	perp.normalize
    	theDirection.scalarMult( theSize.x )
		perp.scalarMult( theSize.y )
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
		}
  
		// Create a polygon.
		
		theShape.reset
		theShape.moveTo( x , y )
		theShape.lineTo( x - theDirection(0) + perp(0), y - theDirection(1) + perp(1) )		
		theShape.lineTo( x - theDirection(0) - perp(0), y - theDirection(1) - perp(1) )
		theShape.closePath
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( true, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( false, camera )
 		stroke( g, theShape )
 		fill( g, theShape, camera )
	}
}

class CircleOnEdge extends AreaOnConnectorShape {
	val theShape = new Ellipse2D.Float
 
// Command
 
 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
 	  	configureFillable( style, camera, element )
 	  	configureShadowable( style, camera )
 	  	configureStrokable( style, camera )
 	}
  
	protected def make( g:Graphics2D, camera:Camera )       { make( false, camera ) }
	protected def makeShadow( g:Graphics2D, camera:Camera ) { make( true, camera ) }
  
	protected def make( forShadow:Boolean, camera:Camera ) {
		if( theConnector.info.isCurve )
		     makeOnCurve( forShadow, camera )
		else makeOnLine(  forShadow, camera )
	}
 
	protected def makeOnLine( forShadow:Boolean, camera:Camera ) {
		val off = camera.evalTargetRadius( theEdge ) + ((theSize.x+theSize.y)/4)
		val theDirection = new Vector2(
			theConnector.toPos.x - theConnector.fromPos.x,
			theConnector.toPos.y - theConnector.fromPos.y )
			
		theDirection.normalize
  
		var x    = theCenter.x - ( theDirection(0) * off )
		var y    = theCenter.y - ( theDirection(1) * off )
		//val perp = new Vector2( theDirection(1), -theDirection(0) )
		
		//perp.normalize
		theDirection.scalarMult( theSize.x )
		//perp.scalarMult( theSize.y )
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
		}
  
		// Set the shape.
		
		theShape.setFrame( x-(theSize.x/2), y-(theSize.y/2), theSize.x, theSize.y )
	}

	protected def makeOnCurve( forShadow:Boolean, camera:Camera ) {
		val off   = camera.evalTargetRadius( theEdge.to.getStyle, theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos ) + ( ( theSize.x+theSize.y ) / 4 )
		var style = theEdge.getStyle

		// Express node width in percents of the edge length.

		val d  = lengthOfCurve( theConnector )
		val t1 = ( ( d - off ) / d )

		// Now compute arrow coordinates and vector.
		
		val p1 = CubicCurve.eval( theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos, t1 )

		// Create the shape.

		theShape.setFrame( p1.x-(theSize.x/2), p1.y-(theSize.y/2), theSize.x, theSize.y )
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( true, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( false, camera )
 		stroke( g, theShape )
 		fill( g, theShape, camera )
	}
 
	protected def lengthOfCurve( c:Connector ):Float = {
		// Computing a curve real length is really heavy.
		// We approximate it using the length of the 3 line segments of the enclosing
		// control points.
		( c.fromPos.distance( c.byPos1 ) + c.byPos1.distance( c.byPos2 ) + c.byPos2.distance( c.toPos ) ) * 0.75f
	}
}

class DiamondOnEdge extends AreaOnConnectorShape {
	val theShape = new Path2D.Float()
 
// Command
 
 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
 	  	configureFillable( style, camera, element )
 	  	configureShadowable( style, camera )
 	  	configureStrokable( style, camera )
 	}
  
	protected def make( g:Graphics2D, camera:Camera ) { make( false, camera ) }
	protected def makeShadow( g:Graphics2D, camera:Camera ) { make( true, camera ) }
  
	protected def make( forShadow:Boolean, camera:Camera ) {
		if( theConnector.info.isCurve )
		     makeOnCurve( forShadow, camera )
		else makeOnLine(  forShadow, camera )
	}
 
	protected def makeOnLine( forShadow:Boolean, camera:Camera ) {
		var off = camera.evalTargetRadius( theEdge )
		val theDirection = new Vector2(
			theConnector.toPos.x - theConnector.fromPos.x,
			theConnector.toPos.y - theConnector.fromPos.y )
			
		theDirection.normalize
  
		var x    = theCenter.x - ( theDirection(0) * off )
		var y    = theCenter.y - ( theDirection(1) * off )
		val perp = new Vector2( theDirection(1), -theDirection(0) )
		
		perp.normalize
		theDirection.scalarMult( theSize.x / 2 )
		perp.scalarMult( theSize.y )
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
		}
  
		// Create a polygon.
		
		theShape.reset
		theShape.moveTo( x , y )
		theShape.lineTo( x - theDirection(0) + perp(0), y - theDirection(1) + perp(1) )	
		theShape.lineTo( x - theDirection(0)*2, y - theDirection(1)*2 )
		theShape.lineTo( x - theDirection(0) - perp(0), y - theDirection(1) - perp(1) )
		theShape.closePath
	}

	protected def makeOnCurve( forShadow:Boolean, camera:Camera ) {
		val off = camera.evalTargetRadius( theEdge.to.getStyle, theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos )
		var style = theEdge.getStyle
		
		// Express node width in percents of the edge length.

		var d  = lengthOfCurve( theConnector )
		var t1 = ( ( d - off ) / d )
			
		// Express arrow length in percents of the edge length.
			
		var t2 = ( ( d - ( off + theSize.x ) ) / d )

		// Now compute arrow coordinates and vector.
		
		var p1 = CubicCurve.eval( theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos, t1 )
		var p2 = CubicCurve.eval( theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos, t2 )
		var dir = Vector2( ( p1.x - p2.x ) / 2, ( p1.y - p2.y ) / 2 )

		var per = Vector2( dir(1), -dir(0) )
		per.normalize
		per.scalarMult( theSize.y )
  
		// Create a polygon.
	
		theShape.reset
		theShape.moveTo( p1.x , p1.y )
		theShape.lineTo( p1.x - dir(0) + per(0), p1.y - dir(1) + per(1) )
		theShape.lineTo( p2.x, p2.y )
		theShape.lineTo( p1.x - dir(0) - per(0), p1.y - dir(1) - per(1) )
		theShape.closePath
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( true, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( false, camera )
 		stroke( g, theShape )
 		fill( g, theShape, camera )
	}
 
	protected def lengthOfCurve( c:Connector ):Float = {
		// Computing a curve real length is really heavy.
		// We approximate it using the length of the 3 line segments of the enclosing
		// control points.
		( c.fromPos.distance( c.byPos1 ) + c.byPos1.distance( c.byPos2 ) + c.byPos2.distance( c.toPos ) ) * 0.75f
	}
}