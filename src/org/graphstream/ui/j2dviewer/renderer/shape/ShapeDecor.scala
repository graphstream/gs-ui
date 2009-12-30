package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Graphics2D, Color, Font}
import java.awt.geom.{AffineTransform, Rectangle2D}
import java.awt.font.TextLayout
import java.awt.image.BufferedImage

import org.graphstream.ui.j2dviewer.util.{FontCache, ImageCache}

import org.graphstream.ui2.graphicGraph.stylesheet.Style
import org.graphstream.ui2.graphicGraph.stylesheet.StyleConstants._

/**
 * Representation of the icon and text that can decorate any "decorated" shape.
 */
abstract class ShapeDecor {
	def render( g:Graphics2D, text:String, x0:Float, y0:Float, x1:Float, y1:Float )
}

/**
 * Companion object for text and icon decoration on shapes.
 */
object ShapeDecor {
	def apply( style:Style ):ShapeDecor = {
		import org.graphstream.ui2.graphicGraph.stylesheet.StyleConstants.TextMode._
		import org.graphstream.ui2.graphicGraph.stylesheet.StyleConstants.TextAlignment._

		if( style.getTextMode == HIDDEN ) {
			new EmptyShapeDecor
		} else {
			val iconAndText = IconAndText( style )
   
			style.getTextAlignment match {
				case CENTER   => new CenteredShapeDecor( iconAndText )
				case LEFT     => new CenteredShapeDecor( iconAndText )
				case RIGHT    => new CenteredShapeDecor( iconAndText )
				case AT_LEFT  => new CenteredShapeDecor( iconAndText )
				case AT_RIGHT => new CenteredShapeDecor( iconAndText )
				case UNDER    => new CenteredShapeDecor( iconAndText )
			  	case ABOVE    => new CenteredShapeDecor( iconAndText )
			  	case JUSTIFY  => new CenteredShapeDecor( iconAndText )
			  	case ALONG    => new CenteredShapeDecor( iconAndText )
			  	case _        => null
			} 
		}
	}
	
	class EmptyShapeDecor extends ShapeDecor {
		def render( g:Graphics2D, text:String, x0:Float, y0:Float, x1:Float, y1:Float ) {}
    }
 
	class CenteredShapeDecor( val iconAndText:IconAndText ) extends ShapeDecor {
		def render( g:Graphics2D, text:String, x0:Float, y0:Float, x1:Float, y1:Float ) {
			val cx = x0 + (x1 - x0) / 2
			val cy = y0 + (y1 - y0) / 2
			val w2 = iconAndText.width / 2
			val h2 = iconAndText.height / 2
   
			iconAndText.render( g, text, cx-w2, cy-h2 )
		}
	}
}

/**
 * A wrapper for a text and an icon.
 * 
 * <p>
 * The text is dynamic and therefore specified at each rendering. The icon is fixed by the style
 * and specified only at construction.
 * </p>
 */
abstract class IconAndText {
	def render( g:Graphics2D, text:String, xLeft:Float, yBottom:Float )
	def width:Float
	def height:Float
}

/**
 * Companion object for text and icon.
 * 
 * <p>
 * Allows to create the icon and text bundle from a given style.
 * </p>
 */
object IconAndText {
	def apply( style:Style ):IconAndText = {
		import org.graphstream.ui2.graphicGraph.stylesheet.StyleConstants.IconMode._
		var icon:BufferedImage = null
		val text = TextBox( style )
		
		if( style.getIconMode != IconMode.NONE ) {
			icon = ImageCache.loadImage( style.getIcon ) match {
				case x:Some[BufferedImage] => x.get
				case _                     => null
			}
		}
  
		if( icon == null ) {
			new IconAndTextOnlyText( text )
		} else {
			style.getIconMode match {
			  case AT_LEFT  => new IconAtLeftAndText( icon, text )
			  case AT_RIGHT => new IconAtLeftAndText( icon, text )//IconAtRightAndText( icon, text )
			  case ABOVE    => new IconAtLeftAndText( icon, text )//IconAboveAndText( icon, text )
			  case UNDER    => new IconAtLeftAndText( icon, text )//IconUnderAndText( icon, text )
			  case _        => throw new RuntimeException( "WTF ?" )
			}
		}
	}
 
	class IconAndTextOnlyText( val text:TextBox ) extends IconAndText {
		def render( g:Graphics2D, text:String, xLeft:Float, yBottom:Float ) {
			this.text.setText( text, g )
			this.text.render( g, xLeft, yBottom )
		}
		def width:Float = text.width
		def height:Float = text.height
	}
 
	class IconAtLeftAndText( val icon:BufferedImage, val text:TextBox ) extends IconAndText {
		def render( g:Graphics2D, text:String, xLeft:Float, yBottom:Float ) {
			this.text.setText( text, g )
			g.drawImage( icon, new AffineTransform( 1f, 0f, 0f, 1f, xLeft, yBottom ), null )
			this.text.render( g, xLeft, yBottom )
		}
		def width:Float = text.width + icon.getWidth(null) + 5
		def height:Float = Math.max( text.height, icon.getHeight(null) )
	}
}

/**
 * A simple wrapper for a font and a text string.
 */
class TextBox( val font:Font, val textColor:Color ) {
	var text:TextLayout = null
	var bounds:Rectangle2D = null
	def setText( text:String, g:Graphics2D ) {
	  	if( this.text != null && this.text != text ) {
	  		this.text = new TextLayout( text, font, g.getFontRenderContext )
	  		this.bounds = this.text.getBounds
	  	}
	}
 	def width:Float = bounds.getWidth.toFloat
 	def height:Float = bounds.getHeight.toFloat
 	def render( g:Graphics2D, xLeft:Float, yBottom:Float ) {
	  	text.draw( g, xLeft, yBottom )
 	}
}

/**
 * Factory companion object for text boxes.
 */
object TextBox {
	def apply( style:Style ):TextBox = {
		val fontName  = style.getTextFont
		val fontStyle = style.getTextStyle
		val fontSize  = style.getTextSize
		val textColor = style.getTextColor( 0 )
		TextBox( fontName, fontStyle, fontSize.value.toInt, textColor )
	}

	def apply( fontName:String, style:TextStyle, fontSize:Int, textColor:Color ):TextBox = {
		new TextBox( FontCache.getFont( fontName, style, fontSize ), textColor )
	}
}