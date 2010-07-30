package org.graphstream.ui.j2dviewer.renderer.shape

import scala.math._

import java.awt.{Graphics2D, Color, Font}
import java.awt.geom.{AffineTransform, Rectangle2D, Point2D}
import java.awt.font.TextLayout
import java.awt.image.BufferedImage

import org.graphstream.ui.j2dviewer.Camera
import org.graphstream.ui.util.{FontCache, ImageCache, GraphMetrics}

import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

/**
 * Representation of the icon and text that can decorate any "decorated" shape.
 */
abstract class ShapeDecor {
	/**
	 * Render the decor at the given coordinates. The shape decor contains all the metrics to
	 * render the `iconAndText` icon and text. The coordinates (`x0`, `y0`) and (`x1`, `y1`)
	 * indicates the lower-left and upper-right coordinates of the area where the decor should be
	 * drawn. 
	 */
	def render( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Float, y0:Float, x1:Float, y1:Float )
	
	/** Overall size (width and height) of the decor, taking into account the `iconAndText` as
	 *  well as the various metrics specified by the style. */
	def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Float,Float)
}

/**
 * Companion object for text and icon decoration on shapes.
 */
object ShapeDecor {
	/** Generate a new icon and text specific to the given `element`, according to the given
	 *  `style` and `camera`. */
	def iconAndText( style:Style, camera:Camera, element:GraphicElement ):IconAndText = IconAndText( style, camera, element )
	
	def apply( style:Style ):ShapeDecor = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextMode._
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextAlignment._

		if( style.getTextMode == HIDDEN ) {
			new EmptyShapeDecor
		} else {
			style.getTextAlignment match {
				case CENTER   => new CenteredShapeDecor
				case LEFT     => new LeftShapeDecor
				case RIGHT    => new RightShapeDecor
				case AT_LEFT  => new AtLeftShapeDecor
				case AT_RIGHT => new AtRightShapeDecor
				case UNDER    => new UnderShapeDecor
			  	case ABOVE    => new AboveShapeDecor
			  	case JUSTIFY  => new CenteredShapeDecor
			  	case ALONG    => new CenteredShapeDecor
			  	case _        => null
			} 
		}
	}
	
	/** A decor that does nothing. */
	class EmptyShapeDecor extends ShapeDecor {
		def render( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Float, y0:Float, x1:Float, y1:Float ) {}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Float,Float) = ( 0, 0 )
    }
 
	/** Base for shape decors that work in pixels units, not GU. */
	abstract class PxShapeDecor extends ShapeDecor {
		/* We choose here to replace the transform (GU->PX) into a the identity to draw
		 * The text and icon. Is this the best way ? Maybe should we merely scale the 
		 * font size to render the text at the correct size ? How to handle the icon in
		 * this case ? 
		 */
		protected def renderGu2Px( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x:Float, y:Float )(  ) {
			var p  = camera.transform( x, y )
			val Tx = g.getTransform
			
			g.setTransform( new AffineTransform )
			//iconAndText.setText( g, text )
		
			p = positionTextAndIconPx( p, iconAndText )
   
			iconAndText.render( g, camera, p.x, p.y )
			g.setTransform( Tx )
		}
  
		protected def positionTextAndIconPx( p:Point2D.Float, iconAndText:IconAndText ):Point2D.Float
	}
 
	class CenteredShapeDecor extends PxShapeDecor {
		def render( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Float, y0:Float, x1:Float, y1:Float ) {
			val cx = x0 + (x1 - x0) / 2
			val cy = y0 + (y1 - y0) / 2

			renderGu2Px( g, camera, iconAndText, cx, cy )
		}
		protected def positionTextAndIconPx( p:Point2D.Float, iconAndText:IconAndText ):Point2D.Float = {
			p.x = p.x - ( iconAndText.width / 2 + 1 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Float,Float) = {
				//iconAndText.setText( g, text )
				( camera.metrics.lengthToGu( iconAndText.width, Units.PX ),
				  camera.metrics.lengthToGu( iconAndText.height, Units.PX ) )
		}
	}
 
 	class AtLeftShapeDecor extends PxShapeDecor {
		def render( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Float, y0:Float, x1:Float, y1:Float ) {
			val cx = x1
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px( g, camera, iconAndText, cx, cy )
		}
		protected def positionTextAndIconPx( p:Point2D.Float, iconAndText:IconAndText ):Point2D.Float = {
			p.x = p.x + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Float,Float) = ( 0, 0 )
	}
 
 	class LeftShapeDecor extends PxShapeDecor {
		def render( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Float, y0:Float, x1:Float, y1:Float ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px( g, camera, iconAndText, cx, cy )
		}
		protected def positionTextAndIconPx( p:Point2D.Float, iconAndText:IconAndText ):Point2D.Float = {
			p.x = p.x + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Float,Float) = ( 0, 0 )
	}
 
 	class AtRightShapeDecor extends PxShapeDecor {
		def render( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Float, y0:Float, x1:Float, y1:Float ) {
			val cx = x0
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px( g, camera, iconAndText, cx, cy )
		}
		protected def positionTextAndIconPx( p:Point2D.Float, iconAndText:IconAndText ):Point2D.Float = {
			p.x = p.x - ( iconAndText.width + 5 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Float,Float) = ( 0, 0 )
	}
 
 	class RightShapeDecor extends PxShapeDecor {
		def render( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Float, y0:Float, x1:Float, y1:Float ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px( g, camera, iconAndText, cx, cy )
		}
		protected def positionTextAndIconPx( p:Point2D.Float, iconAndText:IconAndText ):Point2D.Float = {
			p.x = p.x - ( iconAndText.width + 2 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Float,Float) = ( 0, 0 )
	}
 
 	class UnderShapeDecor extends PxShapeDecor {
		def render( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Float, y0:Float, x1:Float, y1:Float ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y0

			renderGu2Px( g, camera, iconAndText, cx, cy )
		}
		protected def positionTextAndIconPx( p:Point2D.Float, iconAndText:IconAndText ):Point2D.Float = {
			p.x = p.x - ( iconAndText.width / 2 + 1 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Float,Float) = ( 0, 0 )
	}
 
 	class AboveShapeDecor extends PxShapeDecor {
		def render( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Float, y0:Float, x1:Float, y1:Float ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y1

			renderGu2Px( g, camera, iconAndText, cx, cy )
		}
		protected def positionTextAndIconPx( p:Point2D.Float, iconAndText:IconAndText ):Point2D.Float = {
			p.x = p.x - ( iconAndText.width / 2 + 1 ) + iconAndText.padx
			p.y = p.y - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Float,Float) = ( 0, 0 )
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
abstract class IconAndText( val text:TextBox, val padx:Float, val pady:Float ) {
	// padx and pady are in pixels.
	def setText( g:Graphics2D, text:String )
	def setIcon( g:Graphics2D, url:String )
	def render( g:Graphics2D, camera:Camera, xLeft:Float, yBottom:Float )
	def width:Float
	def height:Float
	def descent:Float = text.descent
	def ascent:Float = text.ascent
}

/**
 * Companion object for text and icon.
 * 
 * <p>
 * Allows to create the icon and text bundle from a given style.
 * </p>
 */
object IconAndText {
	def apply( style:Style, camera:Camera, element:GraphicElement ):IconAndText = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.IconMode._
		var icon:BufferedImage = null
		val text = TextBox( style )
		val padd = style.getPadding
		val padx = camera.metrics.lengthToPx( padd, 0 )
		val pady = if(padd.size>1) camera.metrics.lengthToPx(padd,1) else padx
		
		if( style.getIconMode != IconMode.NONE ) {
			var url = style.getIcon
			
			if( url.equals( "dynamic" ) ) {
				if( element.hasLabel( "ui.icon" ) )
				      url = element.getLabel( "ui.icon" ).toString
				else url = null
			}
			
			if( url != null ) {
				icon = ImageCache.loadImage( url ) match {
					case x:Some[_] => x.get
					case _         => null
				}
			}
		}
  
		if( icon == null ) {
			new IconAndTextOnlyText( text, padx, pady )
		} else {
			style.getIconMode match {
			  case AT_LEFT  => new IconAtLeftAndText( icon, text, padx, pady )
			  case AT_RIGHT => new IconAtLeftAndText( icon, text, padx, pady )//IconAtRightAndText( icon, text ) TODO
			  case ABOVE    => new IconAtLeftAndText( icon, text, padx, pady )//IconAboveAndText( icon, text ) TODO
			  case UNDER    => new IconAtLeftAndText( icon, text, padx, pady )//IconUnderAndText( icon, text ) TODO
			  case _        => throw new RuntimeException( "WTF ?" )
			}
		}
	}
 
	class IconAndTextOnlyText( text:TextBox, padx:Float, pady:Float ) extends IconAndText( text, padx, pady ) {
		def setText( g:Graphics2D, text:String ) { this.text.setText( text, g ) }
		def setIcon( g:Graphics2D, url:String ) {}
		def render( g:Graphics2D, camera:Camera, xLeft:Float, yBottom:Float ) {
			this.text.render( g, xLeft, yBottom - descent )
		}
		def width:Float = text.width + padx*2
		def height:Float = text.ascent + text.descent + pady*2
	}
 
	class IconAtLeftAndText( var icon:BufferedImage, text:TextBox, padx:Float, pady:Float ) extends IconAndText( text, padx, pady ) {
		def setText( g:Graphics2D, text:String ) { this.text.setText( text, g ) }
		def setIcon( g:Graphics2D, url:String ) {
			icon = ImageCache.loadImage( url ) match {
				case x:Some[_] => x.get
				case _         => ImageCache.dummyImage
			}
		}
		def render( g:Graphics2D, camera:Camera, xLeft:Float, yBottom:Float ) {
			g.drawImage( icon, new AffineTransform( 1f, 0f, 0f, 1f, xLeft, (yBottom-(height/2))-(icon.getHeight/2) ), null )
			this.text.render( g, xLeft + icon.getWidth + 5, yBottom - descent )
		}
		def width:Float = text.width + icon.getWidth(null) + 5 + padx*2
		def height:Float = max( icon.getHeight(null), text.ascent + text.descent ) + pady*2
	}
}

/**
 * A simple wrapper for a font and a text string.
 */
class TextBox( val font:Font, val textColor:Color ) {
	
	/** The text stored as a text layout. */
	var text:TextLayout = null
	
	/** The original text data. */
	protected var textData:String = null
	
	/** The bounds of the text stored when the text is changed. */
	var bounds:Rectangle2D = new Rectangle2D.Float( 0, 0, 0, 0 )
	
	/** Changes the text and compute its bounds. This method tries to avoid recomputing bounds
	 *  if the text does not really changed. */
	def setText( text:String, g:Graphics2D ) {
	  	if( text != null ) {
	  		if( ( textData ne text ) || ( textData != text ) ) {
//Console.err.printf( "recomputing text '%s' != '%s' length%n", text, textData )
				textData    = text
	  			this.text   = new TextLayout( text, font, g.getFontRenderContext )
	  			this.bounds = this.text.getBounds
	  	  	}
	  	} else {
	  		this.textData = null
	  		this.text     = null
	  		this.bounds   = new Rectangle2D.Float( 0, 0, 0, 0 )
	  	}
	}
	
	/** Width of the text. */
 	def width:Float   = if( bounds != null ) bounds.getWidth.toFloat else 0
 	
 	/** Height of the text. */
 	def height:Float  = if( bounds != null ) bounds.getHeight.toFloat else 0
 	
 	/** Descent of the text. */
 	def descent:Float = if( text != null ) text.getDescent else 0
 	
 	/** Ascent of the text. */
 	def ascent:Float  = if( text != null ) text.getAscent else 0
 	
 	/** Renders the text at the given coordinates. */
 	def render( g:Graphics2D, xLeft:Float, yBottom:Float ) {
		if( text != null ) {
			g.setColor( textColor )
			text.draw( g, xLeft, yBottom )
		}
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