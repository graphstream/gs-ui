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
package org.graphstream.ui.util

import org.graphstream.ui.geom.{Point3, Vector3}
import org.graphstream.ui.graphicGraph.stylesheet.{StyleConstants, Value, Values}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units

/**
 * Various geometric informations on the graphic graph.
 * 
 * <p>
 * This class provides metrics on the graphic graph and on the rendering canvas, and allow to
 * convert from graph metrics to canvas metrics and the reverse.
 * </p>
 * 
 * <p>
 * Here we call the canvas "view port" since this class allows to place a view port inside
 * the graph in order to zoom and pan the view.
 * </p>
 */
class GraphMetrics {
// Attribute
	
  	/** Graph lower position (bottom,left,front). */
  	val lo = new Point3
	
  	/** Graph higher position (top,right,back). */
  	val hi = new Point3 
	
  	/** The lowest visible point. */
  	val loVisible = new Point3
	
  	/** The highest visible point. */
  	val hiVisible = new Point3
	
  	/** Graph dimension. */
  	val size = new Vector3
	
  	/** The graph diagonal. */
  	var diagonal:Float = 1f
	
  	/** The view port size. */
  	val viewport = new Vector3
	
  	/** The scaling factor to pass from graph units to pixels. */
  	private[this] var ratioPx2Gu_p:Float = 1f
	
  	/** The length for one pixel, according to the current transformation. */
  	private[this] var px1_p:Float = 1f
	
// Construction
	
  	setDefaults
	
  	/**
  	 * Set defaults value in the lo, hi and size fields to (-1) and (1) respectively.
  	 */
  	 protected def setDefaults() {
  		lo.set(  -1, -1, -1 )
  		hi.set(   1,  1,  1 )
  		size.set( 2,  2,  2 )

  		diagonal     = 1
  		ratioPx2Gu_p = 1
  		px1_p        = 1
  	}
	
// Access -- Convert values
	
  	/**
  	 * Convert a value in given units to graph units.
  	 * @param value The value to convert.
  	 * @param units The units the value to convert is expressed in.
  	 * @return The value converted to GU.
  	 */
  	def lengthToGu(value:Float, units:Units):Float = units match {
  		case Units.PX       => (value-0.01f) / ratioPx2Gu
  		case Units.PERCENTS => ( diagonal * value )
  		case _              => value
  	}
	
  	/**
  	 * Convert a value in a given units to graph units.
  	 * @param value The value to convert (it contains its own units).
  	 */
  	def lengthToGu(value:Value):Float = lengthToGu( value.value, value.units )
	
  	/**
  	 * Convert one of the given values in a given units to graph units.
  	 * @param values The values set containing the value to convert (it contains its own units).
  	 * @param index Index of the value to convert.
  	 */
  	def lengthToGu(values:Values, index:Int):Float = lengthToGu( values.get( index ), values.units ) 

  	/**
  	 * Convert a value in a given units to pixels.
  	 * @param value The value to convert.
  	 * @param units The units the value to convert is expressed in.
  	 * @return The value converted in pixels.
  	 */
  	def lengthToPx(value:Float, units:Units):Float = units match {
  		case Units.GU       => (value-0.01f) * ratioPx2Gu
  		case Units.PERCENTS => (diagonal * value) * ratioPx2Gu
  		case _              => value
  	}
	
  	/**
  	 * Convert a value in a given units to pixels.
  	 * @param value The value to convert (it contains its own units).
  	 */
  	def lengthToPx(value:Value):Float = lengthToPx( value.value, value.units )
	
  	/**
  	 * Convert one of the given values in a given units pixels.
  	 * @param values The values set containing the value to convert (it contains its own units).
  	 * @param index Index of the value to convert.
  	 */
  	def lengthToPx(values:Values, index:Int ):Float = lengthToPx( values.get( index ), values.units )

  	def graphWidthGU:Float  = hi.x - lo.x
  	def graphHeightGU:Float = hi.y - lo.y
    def graphDepthGU:Float  = hi.z - lo.z
   
  	override def toString():String = {
  		val builder = new StringBuilder( "Graph Metrics :%n".format() )
		
  		builder.append( "        lo         = %s%n".format( lo ) )
  		builder.append( "        hi         = %s%n".format( hi ) )
  		builder.append( "        visible lo = %s%n".format( loVisible ) )
  		builder.append( "        visible hi = %s%n".format( hiVisible ) )
  		builder.append( "        size       = %s%n".format( size ) )
  		builder.append( "        diag       = %f%n".format( diagonal ) )
  		builder.append( "        viewport   = %s%n".format( viewport ) )
  		builder.append( "        ratio      = %fpx = 1gu%n".format( ratioPx2Gu_p ) )
  		builder.toString
  	}
  
  	/**
  	 * The scaling factor to pass from graph units to pixels.
  	 */
  	def ratioPx2Gu:Float = ratioPx2Gu_p
  
  	/**
  	 * The length for one pixel, according to the current transformation.
  	 */
  	def px1:Float = px1_p
	
// Command

  	/**
  	 * Set the output view port size in pixels. 
  	 * @param viewportWidth The width in pixels of the view port.
  	 * @param viewportHeight The width in pixels of the view port.
  	 */
  	def setViewport(viewportWidth:Float, viewportHeight:Float) {
  		viewport.set( viewportWidth, viewportHeight, 0 );
  	}
	
  	/**
  	 * The ratio to pass by multiplication from pixels to graph units. This ratio must be larger
  	 * than zero, else nothing is changed.
  	 * @param ratio The ratio.
  	 */
  	def ratioPx2Gu_=(ratio:Float) {
  		if( ratio > 0 ) {
  			ratioPx2Gu_p = ratio
  			px1_p        = 0.95f / ratioPx2Gu_p
  		}
  	}
	
  	/**
  	 * Set the graphic graph bounds (the lowest and highest points).
  	 * @param minx Lowest abscissa.
  	 * @param miny Lowest ordinate.
  	 * @param minz Lowest depth.
  	 * @param maxx Highest abscissa.
  	 * @param maxy Highest ordinate.
  	 * @param maxz Highest depth.
  	 */
  	def setBounds(minx:Float, miny:Float, minz:Float, maxx:Float, maxy:Float, maxz:Float) = {
  		lo.x = minx
  		lo.y = miny
  		lo.z = minz
  		hi.x = maxx
  		hi.y = maxy
  		hi.z = maxz
		
  		size.data(0) = hi.x - lo.x
  		size.data(1) = hi.y - lo.y
  		size.data(2) = hi.z - lo.z
  		diagonal     = Math.sqrt( size.data(0) * size.data(0) + size.data(1) * size.data(1) + size.data(2) * size.data(2) ).toFloat
  	}
}