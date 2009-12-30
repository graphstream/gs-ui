package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Image, Color, Graphics2D}
import java.awt.geom.{Ellipse2D, Line2D, RectangularShape}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui2.graphicGraph.stylesheet.Style


trait RectangularAreaShape extends AreaShape {
	val theShape:RectangularShape
  
 	def configure( style:Style, g:Graphics2D ) {
		if( shadowPaint == null ) shadowPaint = ShapePaint( style, true )
		if( fillPaint   == null ) fillPaint   = ShapePaint( style )
		if( strokeColor == null ) strokeColor = ShapeStroke.strokeColor( style )
		if( theStroke   == null ) theStroke   = ShapeStroke.strokeForArea( style )
		if( theDecor    == null ) theDecor    = ShapeDecor( style )
 	}
 
 	def make( forShadow:Boolean ) {
		var x = theCenter.x
		var y = theCenter.y
		var w = theSize.x
		var h = theSize.y
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
			w += theShadowWidth.x * 2
			h += theShadowWidth.y * 2
		}
 	  	
		theShape.setFrame( x-w/2, y-h/2, w, h )
 	}
 
   	def cast( g:Graphics2D ) {
   		shadowPaint match {
   			case p:ShapeAreaPaint  => g.setPaint( p.paint( theShape ) )
   			case p:ShapeColorPaint => g.setPaint( p.paint( 0 /* XXX  */ ) )
   			case _                 => null
   		}
     
   		g.fill( theShape )
   	}

	def fill( g:Graphics2D, dynColor:Float ) {
		fillPaint match {
		  case p:ShapeAreaPaint  => g.setPaint( p.paint( theShape ) )
		  case p:ShapeColorPaint => g.setPaint( p.paint( dynColor ) )
		  case _                 => null 
		}
  
		g.fill( theShape )
	}

	def stroke( g:Graphics2D ) {
		g.setStroke( theStroke.stroke( strokeWidth ) )
		g.setColor( strokeColor )
		g.draw( theShape )
	}
 
	def decor( g:Graphics2D ) {
	  
	}
}

class Circle extends RectangularAreaShape {
	val theShape = new Ellipse2D.Float
}

class Line extends ConnectorShape {
	protected var theShape = new Line2D.Float
 
// Command
 
 	def configure( style:Style, g:Graphics2D ) {
 	  
 	}
  
	def make( forShadow:Boolean ) {
	  
	}
 
	def cast( g:Graphics2D ) {
	  
	}
 
	def fill( g:Graphics2D, dynColor:Float ) {
	  
	}
 
	def stroke( g:Graphics2D ) {
	  
	}
 
	def decor( g:Graphics2D ) {
	  
	}
}