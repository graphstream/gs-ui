package org.graphstream.ui.j2dviewer.util

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
	def getDefaultFont( style:TextStyle, size:Int ):Font = getFont( "SansSerif", style, size )
	
	/**
	 * Lookup a font, and if not found, try to load it, if still not available, return the
	 * default font.
	 * @param name The font name.
	 * @param style A style, taken from the styles available in the style sheets.
	 * @param size The font size in points.
	 * @return A font.
	 */
	def getFont( name:String, style:TextStyle, size:Int ):Font = {
		cache.get( name ) match {
		  case None      => { val slot = new FontSlot( name, style, size ); cache.put( name, slot ); slot.getFont( style, size ) }
		  case x:Some[_] => { x.get.getFont( style, size ) }
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
class FontSlot( val name:String, style:TextStyle, size:Int ) {
// attribute

	var normal:HashMap[Integer,Font] = null
	
	var bold:HashMap[Integer,Font] = null
	
	var italic:HashMap[Integer,Font] = null 
	
	var boldItalic:HashMap[Integer,Font] = null
	
// Construction
 
	insert( style, size )
	
// Command
 
	protected def mapFromStyle( style:TextStyle ):Map[Integer,Font] = style match { 
		case BOLD        => if( bold       == null ) bold       = new HashMap[Integer,Font]; bold
		case ITALIC      => if( italic     == null ) italic     = new HashMap[Integer,Font]; italic
		case BOLD_ITALIC => if( boldItalic == null ) boldItalic = new HashMap[Integer,Font]; boldItalic
		case NORMAL      => if( normal     == null ) normal     = new HashMap[Integer,Font]; normal
		case _           => if( normal     == null ) normal     = new HashMap[Integer,Font]; normal
	}
	
	protected def toJavaStyle( style:TextStyle ):Int = style match {
		case BOLD        => Font.BOLD 
		case ITALIC      => Font.ITALIC
		case BOLD_ITALIC => Font.BOLD + Font.ITALIC
		case NORMAL      => Font.PLAIN
		case _           => Font.PLAIN
	}
	
	def insert( style:TextStyle, size:Int ):Font =  insert( mapFromStyle( style ), toJavaStyle( style ), size )
	
	protected def insert( map:Map[Integer,Font], style:Int, size:Int ):Font = {
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