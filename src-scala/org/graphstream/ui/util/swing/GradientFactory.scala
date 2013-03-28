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
package org.graphstream.ui.util.swing

import java.awt.Color
import java.awt.GradientPaint
import java.awt.LinearGradientPaint
import java.awt.MultipleGradientPaint
import java.awt.Paint
import java.awt.RadialGradientPaint

import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode._

import scala.collection.JavaConversions._

@deprecated( "Use the ShapePaint class instead." )
object GradientFactory {
//@deprecated object GradientFactory {
// Access
  
  	/**
	 * Generate a gradient in the given pixel area following the given style. This produces a
	 * gradient only if the style fill-mode is compatible.
	 * @param x0 The left corner of the area.
	 * @param y0 The bottom corner of the area.
	 * @param width The area width.
	 * @param height The area height.
	 * @param style The style.
	 * @return A gradient paint or null if the style does not specify a gradient.
	 */
	def gradientInArea( x0:Int, y0:Int, width:Int, height:Int, style:Style ):Paint = {
		style.getFillMode match {
			case GRADIENT_VERTICAL =>
				linearGradientFromStyle( x0, y0+height, x0, y0, style )
			case GRADIENT_HORIZONTAL =>
				linearGradientFromStyle( x0, y0, x0+width, y0, style )
			case GRADIENT_DIAGONAL1 =>
				linearGradientFromStyle( x0, y0+height, x0+width, y0, style )
			case GRADIENT_DIAGONAL2 =>
				linearGradientFromStyle( x0, y0, x0+width, y0+height, style )
			case GRADIENT_RADIAL =>
				radialGradientFromStyle( x0+(width/2), y0+(height/2),
						if( width > height ) width/2 else height/2, style )
			case _ => null
		}
	}
  
	/**
	 * Generate a linear gradient between two given points corresponding to the given style. 
	 * @param x0 The start point abscissa.
	 * @param y0 The start point ordinate.
	 * @param x1 The end point abscissa.
	 * @param y1 The end point ordinate.
	 * @param style The style.
	 * @return A paint for the gradient or null if the style specifies no gradient (the fill mode
	 * is not a linear gradient or there is only one fill colour).
	 */
	def linearGradientFromStyle( x0:Double, y0:Double, x1:Double, y1:Double, style:Style ):Paint = {
		var paint:Paint = null
		val gradientPaint = () => {
			if( version16 )
			     new LinearGradientPaint( x0.toFloat, y0.toFloat, x1.toFloat, y1.toFloat, createFractions( style ), createColors( style ) )
			else new GradientPaint( x0.toFloat, y0.toFloat, style.getFillColor( 0 ), x1.toFloat, y1.toFloat, style.getFillColor( 1 ) )
		}
  
		if( style.getFillColorCount > 1 ) {
			style.getFillMode match {
				case GRADIENT_DIAGONAL1  => paint = gradientPaint()
				case GRADIENT_DIAGONAL2  => paint = gradientPaint()
				case GRADIENT_HORIZONTAL => paint = gradientPaint()
				case GRADIENT_VERTICAL   => paint = gradientPaint()
				case _                   => {}
			}
		}
		
		paint
	}

	def radialGradientFromStyle( cx:Double, cy:Double, radius:Double, style:Style ):Paint = radialGradientFromStyle( cx, cy, radius, cx, cy, style )
	
	/**
	 * Generate a radial gradient whose center is at (cx,cy) with the given radius. The
	 * focus (fx,fy) is the start position of the gradient in the circle. 
	 * @param cx The center point abscissa.
	 * @param cy The center point ordinate.
	 * @param fx The start point abscissa.
	 * @param fy The start point ordinate.
	 * @param radius The gradient radius.
	 * @param style The style.
	 * @return A paint for the gradient or null if the style specifies no gradient (the fill mode
	 * is not a radial gradient or there is only one fill colour).
	 */
	def radialGradientFromStyle( cx:Double, cy:Double, radius:Double, fx:Double, fy:Double, style:Style ):Paint = {
		var paint:Paint = null

		if( version16 ) {
			if( style.getFillColorCount > 1 && style.getFillMode == FillMode.GRADIENT_RADIAL ) {
				val fractions = createFractions( style )
				val colors    = createColors( style )
				paint         = new RadialGradientPaint( cx.toFloat, cy.toFloat, radius.toFloat, fx.toFloat, fy.toFloat, fractions, colors,
						MultipleGradientPaint.CycleMethod.NO_CYCLE )
			}
		}
		
		paint
	}

// Utility
 
	/**
     * An array of floats regularly spaced in range [0,1], the number of floats is given by the
     * style fill-color count.
     * @param style The style to use.
	 */
	protected def createFractions( style:Style ):Array[Float] = {
		val n = style.getFillColorCount

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
	protected def createColors( style:Style ):Array[Color] = {
		val colors = new Array[Color]( style.getFillColorCount )
		var i      = 0

		style.getFillColors.foreach { color =>
			colors(i) = color
			i += 1
		}
  
		colors
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
}