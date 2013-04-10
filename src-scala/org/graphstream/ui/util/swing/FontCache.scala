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

import java.awt.Font

import scala.collection.mutable.{Map, HashMap}

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextStyle
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextStyle._

/**
 * A cache for fonts.
 * 
 * <p>
 * This cache allows to avoid reloading fonts and allows to quickly lookup a font
 * based on its name, its style (bold, italic) and its size.
 * </p>
 */
object FontCache {
// Attribute
  
	/** The default font. */
	protected var defFont:Font = new Font( "SansSerif", Font.PLAIN, 11 )
	
	/** Cached fonts. */
	protected val cache = new HashMap[String,FontSlot]

// Access
 
	/**
	 * The default font.
	 * @return A font.
	 */
	def defaultFont:Font = defFont
	
	/**
	 * The default font with specific size and style. 
	 */
	def getDefaultFont(style:TextStyle, size:Int):Font = getFont( "SansSerif", style, size )
	
	/**
	 * Lookup a font, and if not found, try to load it, if still not available, return the
	 * default font.
	 * @param name The font name.
	 * @param style A style, taken from the styles available in the style sheets.
	 * @param size The font size in points.
	 * @return A font.
	 */
	def getFont(name:String, style:TextStyle, size:Int):Font = {
		cache.get( name ) match {
		  case None      => { val slot = new FontSlot(name, style, size); cache.put(name, slot); slot.getFont(style, size) }
		  case x:Some[_] => { x.get.getFont(style, size) }
		}
	}
 
// Nested classes
 
/**
 * simple container for a font name.
 * 
 * <p>
 * This container allows to group all the fonts that match a name. It stores
 * the font for sizes and styles.
 * </p> 
 */
class FontSlot(val name:String, style:TextStyle, size:Int) {
// attribute

	var normal:HashMap[java.lang.Integer,Font] = null
	
	var bold:HashMap[java.lang.Integer,Font] = null
	
	var italic:HashMap[java.lang.Integer,Font] = null 
	
	var boldItalic:HashMap[java.lang.Integer,Font] = null
	
// Construction
 
	insert( style, size )
	
// Command
 
	protected def mapFromStyle( style:TextStyle ):Map[java.lang.Integer,Font] = style match { 
		case BOLD        => if( bold       == null ) bold       = new HashMap[java.lang.Integer,Font]; bold
		case ITALIC      => if( italic     == null ) italic     = new HashMap[java.lang.Integer,Font]; italic
		case BOLD_ITALIC => if( boldItalic == null ) boldItalic = new HashMap[java.lang.Integer,Font]; boldItalic
		case NORMAL      => if( normal     == null ) normal     = new HashMap[java.lang.Integer,Font]; normal
		case _           => if( normal     == null ) normal     = new HashMap[java.lang.Integer,Font]; normal
	}
	
	protected def toJavaStyle( style:TextStyle ):Int = style match {
		case BOLD        => Font.BOLD 
		case ITALIC      => Font.ITALIC
		case BOLD_ITALIC => Font.BOLD + Font.ITALIC
		case NORMAL      => Font.PLAIN
		case _           => Font.PLAIN
	}
	
	def insert( style:TextStyle, size:Int ):Font =  insert( mapFromStyle( style ), toJavaStyle( style ), size )
	
	protected def insert( map:Map[java.lang.Integer,Font], style:Int, size:Int ):Font = {
		map.get( size ) match {
			case None => {
				val font = new Font( name, style, size )
				map.put( size, font )
				font
		  	}
			case x:Some[_] => {
				x.get
			} 
		}
	}
	
	def getFont( style:TextStyle, size:Int ):Font = {
		val map = mapFromStyle( style )
		
		map.get( size ) match {
		  case None      => insert( map, toJavaStyle( style ), size )
		  case x:Some[_] => x.get
		}
	}
}

}