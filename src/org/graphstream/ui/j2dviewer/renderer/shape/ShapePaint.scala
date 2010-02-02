package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Paint, Color, GradientPaint, LinearGradientPaint, RadialGradientPaint, MultipleGradientPaint}
import java.awt.geom.RectangularShape

import org.graphstream.ui.graphicGraph.stylesheet.{Style, Colors}
import scala.collection.JavaConversions._
import org.graphstream.ScalaGS._

abstract class ShapePaint {
}

abstract class ShapeAreaPaint extends ShapePaint with Area {
	def paint( xFrom:Float, yFrom:Float, xTo:Float, yTo:Float ):Paint
	def paint( shape:java.awt.Shape ):Paint = {
		val s = shape.getBounds2D
		
		paint( s.getMinX.toFloat, s.getMinY.toFloat,
		       s.getMaxX.toFloat, s.getMaxY.toFloat )
	}
}

abstract class ShapeColorPaint extends ShapePaint {
	def paint( value:Float ):Paint
}

object ShapePaint {
	def apply( style:Style ):ShapePaint = apply( style, false )

	def apply( style:Style, forShadow:Boolean ):ShapePaint = {
		if( forShadow ) {
			import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.ShadowMode._
			style.getShadowMode match {
				case GRADIENT_VERTICAL   => new ShapeVerticalGradientPaint(   createColors( style, true ), createFractions( style, true ) )
				case GRADIENT_HORIZONTAL => new ShapeHorizontalGradientPaint( createColors( style, true ), createFractions( style, true ) )
				case GRADIENT_DIAGONAL1  => new ShapeDiagonal1GradientPaint(  createColors( style, true ), createFractions( style, true ) )
				case GRADIENT_DIAGONAL2  => new ShapeDiagonal2GradientPaint(  createColors( style, true ), createFractions( style, true ) )
				case GRADIENT_RADIAL     => new ShapeRadialGradientPaint(     createColors( style, true ), createFractions( style, true ) )
				case PLAIN               => new ShapePlainColorPaint(         style.getShadowColor( 0 ) )
				case _                   => null
			}
		} else {
			import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode._
			style.getFillMode match {
				case GRADIENT_VERTICAL   => new ShapeVerticalGradientPaint(   createColors( style, false ), createFractions( style, false ) )
				case GRADIENT_HORIZONTAL => new ShapeHorizontalGradientPaint( createColors( style, false ), createFractions( style, false ) )
				case GRADIENT_DIAGONAL1  => new ShapeDiagonal1GradientPaint(  createColors( style, false ), createFractions( style, false ) )
				case GRADIENT_DIAGONAL2  => new ShapeDiagonal2GradientPaint(  createColors( style, false ), createFractions( style, false ) )
				case GRADIENT_RADIAL     => new ShapeRadialGradientPaint(     createColors( style, false ), createFractions( style, false ) )
				case DYN_PLAIN           => new ShapeDynPlainColorPaint(      createColors( style, false) )
				case PLAIN               => new ShapePlainColorPaint(         style.getFillColor( 0 ) )
				case IMAGE_TILED         => null
				case IMAGE_SCALED        => null
	   			case IMAGE_SCALED_RATIO  => null
				case _                   => null
			}
		}
	}
 
// Utility
 
	/**
     * An array of floats regularly spaced in range [0,1], the number of floats is given by the
     * style fill-color count.
     * @param style The style to use.
	 */
	protected def createFractions( style:Style, forShadow:Boolean ):Array[Float] = {
		if( forShadow )
		     createFractions( style, style.getShadowColorCount )
		else createFractions( style, style.getFillColorCount )
	}
 
	protected def createFractions( style:Style, n:Int ):Array[Float] = {
		if( n < predefFractions.length ) {
			predefFractions(n)
		} else {
			val fractions = new Array[Float](n)
			val div       = 1f / ( n - 1)

			for( i <- 0 until n )
				fractions(i) = div * i
	
			fractions(0)   = 0f
			fractions(n-1) = 1f 
		
			fractions
		}
	}

	/**
     * The array of colors in the fill-color property of the style.
     * @param style The style to use.
     */
	protected def createColors( style:Style, forShadow:Boolean ):Array[Color] = {
		if( forShadow )
		     createColors( style, style.getShadowColorCount, style.getShadowColors )
		else createColors( style, style.getFillColorCount,   style.getFillColors )
	}
 
	def createColors( style:Style, n:Int, theColors:Colors ):Array[Color] = {
		val colors = new Array[Color]( n )
		var i      = 0

		theColors.foreach { color =>
			colors(i) = color
			i += 1
		}
  
		colors
	}

	def interpolateColor( colors:Array[Color], value:Float ):Color = {
	  	val v = if( value < 0 ) 0 else { if( value > 1 ) 1 else value }
		val n = colors.length
		var c = colors( 0 )
			
		if( v == 1 ) {
			c = colors( n-1 )	// Simplification, faster.
		} else if( v != 0 ) {	// If value == 0, color is already set above.
			var div = 1f / (n-1)
			val col = ( value / div ).toInt

			div = ( value - (div*col) ) / div
				
			val color0 = colors( col );
			val color1 = colors( col + 1 );
			val red    = ( (color0.getRed()  *(1-div)) + (color1.getRed()  *div) ) / 255f
			val green  = ( (color0.getGreen()*(1-div)) + (color1.getGreen()*div) ) / 255f
			val blue   = ( (color0.getBlue() *(1-div)) + (color1.getBlue() *div) ) / 255f
			val alpha  = ( (color0.getAlpha()*(1-div)) + (color1.getAlpha()*div) ) / 255f
					
			c = new Color( red, green, blue, alpha )
		}
 
		c
	}
 
	private[this] val predefFractions  = new Array[Array[Float]]( 11 )
	private[this] val predefFractions2 = Array( 0f, 1f )
	private[this] val predefFractions3 = Array( 0f, 0.5f, 1f)
	private[this] val predefFractions4 = Array( 0f, 0.33f, 0.66f, 1f )
	private[this] val predefFractions5 = Array( 0f, 0.25f, 0.5f, 0.75f, 1f )
	private[this] val predefFractions6 = Array( 0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f )
	private[this] val predefFractions7 = Array( 0f, 0.1666f, 0.3333f, 0.4999f, 0.6666f, 0.8333f, 1f )
	private[this] val predefFractions8 = Array( 0f, 0.1428f, 0.2856f, 0.4284f, 0.5712f, 0.7140f, 0.8568f, 1f )
	private[this] val predefFractions9 = Array( 0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, .75f, 0.875f, 1f )
	private[this] val predefFractions10= Array( 0f, 0.1111f, 0.2222f, 0.3333f, 0.4444f, 0.5555f, 0.6666f, 0.7777f, 0.8888f, 1f )
	
	val version   = System.getProperty( "java.version" )
	var version16 = false
		
	if( version.startsWith( "1." ) && version.length() >= 3 ) {
		val v = version.substring( 2, 3 )
		val n = Integer.parseInt( v )
			
		if( n >= 6 )
			version16 = true
	}
		
	predefFractions(0) = null
	predefFractions(1) = null
	predefFractions(2) = predefFractions2
	predefFractions(3) = predefFractions3
	predefFractions(4) = predefFractions4
	predefFractions(5) = predefFractions5
	predefFractions(6) = predefFractions6
	predefFractions(7) = predefFractions7
	predefFractions(8) = predefFractions8
	predefFractions(9) = predefFractions9
	predefFractions(10)= predefFractions10
 
// Real paint implementations
 
 	abstract class ShapeGradientPaint( colors:Array[Color], fractions:Array[Float] ) extends ShapeAreaPaint {
		def paint( xFrom:Float, yFrom:Float, xTo:Float, yTo:Float ):Paint = {
			var x0 = xFrom; var y0 = yFrom
            var x1 = xTo;   var y1 = yTo
                            
            if( x0 > x1 ) { val tmp = x0; x0 = x1; x1 = tmp }
            if( y0 > y1 ) { val tmp = y0; y0 = y1; y1 = tmp }
            if( x0 == x1 ) { x1 = x0 + 0.001f }
            if( y0 == y1 ) { y1 = y0 + 0.001f }
            
            
            realPaint( x0, y0, x1, y1 )
		}
  
		def realPaint( x0:Float, y0:Float, x1:Float, y1:Float ):Paint
 	}
  
	class ShapeVerticalGradientPaint( colors:Array[Color], fractions:Array[Float] ) extends ShapeGradientPaint( colors, fractions ) {
		def realPaint( x0:Float, y0:Float, x1:Float, y1:Float ):Paint = {
			if( version16 )
			     new LinearGradientPaint( x0, y0, x0, y1, fractions, colors )
			else new GradientPaint( x0, y0, colors(0), x0, y1, colors(1) )
		}
	}
  
	class ShapeHorizontalGradientPaint( colors:Array[Color], fractions:Array[Float] ) extends ShapeGradientPaint( colors, fractions ) {
		def realPaint( x0:Float, y0:Float, x1:Float, y1:Float ):Paint = {
			if( version16 )
			     new LinearGradientPaint( x0, y0, x1, y0, fractions, colors )
			else new GradientPaint( x0, y0, colors(0), x1, y0, colors(1) )
		}
	}
  
	class ShapeDiagonal1GradientPaint( colors:Array[Color], fractions:Array[Float] ) extends ShapeGradientPaint( colors, fractions ) {
		def realPaint( x0:Float, y0:Float, x1:Float, y1:Float ):Paint = {
			if( version16 )
			     new LinearGradientPaint( x0, y0, x1, y1, fractions, colors )
			else new GradientPaint( x0, y0, colors(0), x1, y1, colors(1) )
		}
	}
  
	class ShapeDiagonal2GradientPaint( colors:Array[Color], fractions:Array[Float] ) extends ShapeGradientPaint( colors, fractions ) {
		def realPaint( x0:Float, y0:Float, x1:Float, y1:Float ):Paint = {
			if( version16 )
			     new LinearGradientPaint( x0, y1, x1, y0, fractions, colors )
			else new GradientPaint( x0, y1, colors(0), x1, y0, colors(1) )
		}
	}
 
	class ShapeRadialGradientPaint( colors:Array[Color], fractions:Array[Float] ) extends ShapeGradientPaint( colors, fractions ) {
		def realPaint( x0:Float, y0:Float, x1:Float, y1:Float ):Paint = {
    		val w = ( x1 - x0 ) / 2
			val h = ( y1 - y0 ) / 2
			val cx = x0 + w
			val cy = y0 + h
			if( version16 )
			     new RadialGradientPaint( cx, cy, if( w > h ) w else h, cx, cy, fractions, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE )
			else new GradientPaint( x0, y0, colors(0), x1, y1, colors(1) )
		}
	}
	
   
	class ShapePlainColorPaint( val color:Color ) extends ShapeColorPaint {
		def paint( value:Float ):Paint = color
	}
 
	class ShapeDynPlainColorPaint( val colors:Array[Color] ) extends ShapeColorPaint {
		def paint( value:Float ):Paint = interpolateColor( colors, value )
		/*{
			val v = if( value < 0 ) 0 else { if( value > 1 ) 1 else value }
			val n = colors.length
			var c = colors( 0 )
			
			if( v == 1 ) {
				c = colors( n-1 )	// Simplification, faster.
			} else if( v != 0 ) {	// If value == 0, color is already set above.
				var div = 1f / (n-1)
				val col = ( value / div ).toInt

				div = ( value - (div*col) ) / div
				
				val color0 = colors( col );
				val color1 = colors( col + 1 );
				val red    = ( (color0.getRed()  *(1-div)) + (color1.getRed()  *div) ) / 255f
				val green  = ( (color0.getGreen()*(1-div)) + (color1.getGreen()*div) ) / 255f
				val blue   = ( (color0.getBlue() *(1-div)) + (color1.getBlue() *div) ) / 255f
				val alpha  = ( (color0.getAlpha()*(1-div)) + (color1.getAlpha()*div) ) / 255f
					
				c = new Color( red, green, blue, alpha )
			}
 
			c
		}*/
	}
}