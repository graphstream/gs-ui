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
package org.graphstream.ui.j2dviewer.renderer.shape

import scala.math._

import java.awt._
import java.awt.geom._
import java.awt.font.TextLayout
import java.awt.image.BufferedImage

import org.graphstream.ui.j2dviewer.Camera
import org.graphstream.ui.util._
import org.graphstream.ui.sgeom._

import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

/**
 * Representation of the icon and text that can decorate any "decorated" shape.
 */
abstract class ShapeDecor {
	/**
	 * Render the decor inside the given box coordinates. The shape decor contains all the metrics
	 * to render the `iconAndText` icon and text. The coordinates (`x0`, `y0`) and (`x1`, `y1`)
	 * indicates the lower-left and upper-right coordinates of the area where the decor should be
	 * drawn. 
	 */
	def renderInside( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double )

	/**
	 * Render along the given line coordinates. The shape decor contains all the metrics
	 * to render the `iconAndText` icon and text. The coordinates (`x0`, `y0`) and (`x1`, `y1`)
	 * indicates the start and end points of the line to draw the text on.
	 */
	def renderAlong( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double  )
	
	/** Overall size (width and height) of the decor, taking into account the `iconAndText` as
	 *  well as the various metrics specified by the style. */
	def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Double,Double)
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
			  	case ALONG    => new AlongShapeDecor
			  	case _        => null
			} 
		}
	}
	
	/** A decor that does nothing. */
	class EmptyShapeDecor extends ShapeDecor {
		def renderInside( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {}
		def renderAlong( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
    }
 
	/** Base for shape decors that work in pixels units, not GU. */
	abstract class PxShapeDecor extends ShapeDecor {
		/* We choose here to replace the transform (GU->PX) into a the identity to draw
		 * The text and icon. Is this the best way ? Maybe should we merely scale the 
		 * font size to render the text at the correct size ? How to handle the icon in
		 * this case ? 
		 */
		protected def renderGu2Px( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x:Double, y:Double, angle:Double,
				positionPx:(Graphics2D, Point2D.Double,IconAndText,Double)=>Point2D.Double ) {
			var p  = camera.transform( x, y )
			val Tx = g.getTransform
			
			g.setTransform( new AffineTransform )
		
			p = positionPx( g, p, iconAndText, angle )
   
			iconAndText.render( g, camera, p.x, p.y )
			g.setTransform( Tx )
		}
	}
 
	class CenteredShapeDecor extends PxShapeDecor {
		def renderInside( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0 + (x1 - x0) / 2
			val cy = y0 + (y1 - y0) / 2

			renderGu2Px( g, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def renderAlong( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val dir = Vector2( x1-x0, y1-y0 )
			dir.scalarMult( 0.5f )
			
			renderGu2Px( g, camera, iconAndText, x0 + dir.x, y0 + dir.y, 0, positionTextAndIconPx )
		}
		protected def positionTextAndIconPx( g:Graphics2D, p:Point2D.Double, iconAndText:IconAndText, angle:Double ):Point2D.Double = {
			p.x = p.x - ( iconAndText.width / 2 + 1 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Double,Double) = {
				( camera.metrics.lengthToGu( iconAndText.width, Units.PX ),
				  camera.metrics.lengthToGu( iconAndText.height, Units.PX ) )
		}
	}
 
 	class AtLeftShapeDecor extends PxShapeDecor {
		def renderInside( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px( g, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def renderAlong( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0
			val cy = y0
			
			renderGu2Px( g, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
		protected def positionTextAndIconPx( g:Graphics2D, p:Point2D.Double, iconAndText:IconAndText, angle:Double ):Point2D.Double = {
			p.x = p.x - ( iconAndText.width + 2 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
	}
 
 	class AtRightShapeDecor extends PxShapeDecor {
		def renderInside( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x1
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px( g, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def renderAlong( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x1
			val cy = y1
			
			renderGu2Px( g, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		protected def positionTextAndIconPx( g:Graphics2D, p:Point2D.Double, iconAndText:IconAndText, angle:Double ):Point2D.Double = {
			p.x = p.x + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
	}
 
 	class LeftShapeDecor extends PxShapeDecor {
		def renderInside( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px( g, camera, iconAndText, cx, cy, 0, positionTextAndIconAreaPx )
		}
		def renderAlong( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			renderGu2Px( g, camera, iconAndText, x0, y0,  0, positionTextAndIconAlongPx )
		}
		protected def positionTextAndIconAreaPx( g:Graphics2D, p:Point2D.Double, iconAndText:IconAndText, angle:Double ):Point2D.Double = {
			p.x = p.x - ( iconAndText.width + 2 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		protected def positionTextAndIconAlongPx( g:Graphics2D, p:Point2D.Double, iconAndText:IconAndText, angle:Double ):Point2D.Double = {
			p.x = p.x + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
	}
 
 	class RightShapeDecor extends PxShapeDecor {
		def renderInside( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px( g, camera, iconAndText, cx, cy, 0, positionTextAndIconAreaPx )
		}
		def renderAlong( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			renderGu2Px( g, camera, iconAndText, x1, y1, 0, positionTextAndIconAlongPx )
		}
		protected def positionTextAndIconAreaPx( g:Graphics2D, p:Point2D.Double, iconAndText:IconAndText, angle:Double ):Point2D.Double = {
			p.x = p.x + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		protected def positionTextAndIconAlongPx( g:Graphics2D, p:Point2D.Double, iconAndText:IconAndText, angle:Double ):Point2D.Double = {
			p.x = p.x - ( iconAndText.width + 2 + iconAndText.padx )
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
	}
 
 	class UnderShapeDecor extends PxShapeDecor {
		def renderInside( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y0

			renderGu2Px( g, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def renderAlong( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val dir = Vector2( x1-x0, y1-y0 )
			dir.scalarMult( 0.5f )

			renderGu2Px( g, camera, iconAndText, x0+dir.x, y0+dir.y, 0, positionTextAndIconPx )
		}
		protected def positionTextAndIconPx( g:Graphics2D, p:Point2D.Double, iconAndText:IconAndText, angle:Double ):Point2D.Double = {
			p.x = p.x - ( iconAndText.width / 2 + 1 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height ) - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
	}
 
 	class AboveShapeDecor extends PxShapeDecor {
		def renderInside( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y1

			renderGu2Px( g, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def renderAlong( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val dir = Vector2( x1-x0, y1-y0 )
			dir.scalarMult( 0.5f )

			renderGu2Px( g, camera, iconAndText, x0+dir.x, y0+dir.y, 0, positionTextAndIconPx )
		}
		protected def positionTextAndIconPx( g:Graphics2D, p:Point2D.Double, iconAndText:IconAndText, angle:Double ):Point2D.Double = {
			p.x = p.x - ( iconAndText.width / 2 + 1 ) + iconAndText.padx
			p.y = p.y - iconAndText.pady
			p
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
	}
 	
 	class AlongShapeDecor extends PxShapeDecor {
		def renderInside( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {}
		def renderAlong( g:Graphics2D, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val dir = Vector2( x1-x0, y1-y0 )
			dir.scalarMult( 0.5f )
			val cx = x0 + dir.x
			val cy = y0 + dir.y
			dir.normalize
			var angle = acos( dir.dotProduct( 1, 0 ) )
		
			if( dir.y > 0 )			// The angle is always computed for acute angles
				angle = ( Pi - angle )
				
			if( angle > Pi/2 ) angle = ( Pi + angle )
				
			renderGu2Px( g, camera, iconAndText, cx, cy, angle, positionTextAndIconPx )
		}
		protected def positionTextAndIconPx( g:Graphics2D, p:Point2D.Double, iconAndText:IconAndText, angle:Double ):Point2D.Double = {
			g.translate( p.x, p.y )
			g.rotate( angle )
			g.translate( -iconAndText.width/2, +iconAndText.height/2 )
			new Point2D.Double( 0, 0 )
		}
		def size( g:Graphics2D, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
 	}
}

/**
 * A wrapper for a text and an icon.
 * 
 * <p>
 * The text is dynamic and therefore specified at each rendering. The icon is fixed by the style
 * and specified only at construction, excepted if the icon name is "dynamic" in which case it is
 * taken from the "ui.icon" attribute of the element.
 * </p>
 */
abstract class IconAndText( val text:TextBox, val padx:Double, val pady:Double ) {
	// padx and pady are in pixels.
	def setText( g:Graphics2D, text:String )
	def setIcon( g:Graphics2D, url:String )
	def render( g:Graphics2D, camera:Camera, xLeft:Double, yBottom:Double )
	def width:Double
	def height:Double
	def descent:Double = text.descent
	def ascent:Double = text.ascent
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
 
	class IconAndTextOnlyText( text:TextBox, padx:Double, pady:Double ) extends IconAndText( text, padx, pady ) {
		def setText( g:Graphics2D, text:String ) { this.text.setText( text, g ) }
		def setIcon( g:Graphics2D, url:String ) {}
		def render( g:Graphics2D, camera:Camera, xLeft:Double, yBottom:Double ) {
			this.text.render( g, xLeft, yBottom - descent )
		}
		def width:Double = text.width + padx*2
		def height:Double = text.ascent + text.descent + pady*2
	}
 
	class IconAtLeftAndText( var icon:BufferedImage, text:TextBox, padx:Double, pady:Double ) extends IconAndText( text, padx, pady ) {
		def setText( g:Graphics2D, text:String ) { this.text.setText( text, g ) }
		def setIcon( g:Graphics2D, url:String ) {
			icon = ImageCache.loadImage( url ) match {
				case x:Some[_] => x.get
				case _         => ImageCache.dummyImage
			}
		}
		def render( g:Graphics2D, camera:Camera, xLeft:Double, yBottom:Double ) {
			g.drawImage( icon, new AffineTransform( 1f, 0f, 0f, 1f, xLeft, (yBottom-(height/2))-(icon.getHeight/2) ), null )
			val th = text.ascent + text.descent
			val dh = if( icon.getHeight > th ) ( ( icon.getHeight - th ) / 2f ) else 0f
			this.text.render( g, xLeft + icon.getWidth + 5, yBottom - dh - descent )
		}
		def width:Double = text.width + icon.getWidth(null) + 5 + padx*2
		def height:Double = max( icon.getHeight(null), text.ascent + text.descent ) + pady*2
	}
}

/**
 * A simple wrapper for a font and a text string.
 */
class TextBox( val font:Font, val textColor:Color, val bgColor:Color ) {
	
	/** The text stored as a text layout. */
	var text:TextLayout = null
	
	/** The original text data. */
	protected var textData:String = null
	
	/** The bounds of the text stored when the text is changed. */
	var bounds:Rectangle2D = new Rectangle2D.Double( 0, 0, 0, 0 )
	
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
	  		this.bounds   = new Rectangle2D.Double( 0, 0, 0, 0 )
	  	}
	}
	
	/** Width of the text. */
 	def width:Double   = if( bounds != null ) bounds.getWidth else 0
 	
 	/** Height of the text. */
 	def height:Double  = if( bounds != null ) bounds.getHeight else 0
 	
 	/** Descent of the text. */
 	def descent:Double = if( text != null ) text.getDescent else 0
 	
 	/** Ascent of the text. */
 	def ascent:Double  = if( text != null ) text.getAscent else 0
 	
 	/** Renders the text at the given coordinates. */
 	def render( g:Graphics2D, xLeft:Double, yBottom:Double ) {
		if( text != null ) {
			if( bgColor ne null ) {
				val a = ascent
				val h = a + descent
				g.setColor( bgColor )
				g.fill( new Rectangle2D.Double( xLeft, yBottom-a, width+1, h ) )
			}
			
			g.setColor( textColor )
			text.draw( g, xLeft.toFloat, yBottom.toFloat )
		}
 	}
}

/**
 * Factory companion object for text boxes.
 */
object TextBox {
	def apply( style:Style ):TextBox = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextBackgroundMode._
		
		val fontName  = style.getTextFont
		val fontStyle = style.getTextStyle
		val fontSize  = style.getTextSize
		val textColor = style.getTextColor( 0 )
		var bgColor:Color = null

		style.getTextBackgroundMode match {
			case NONE  => {}
			case PLAIN => { bgColor = style.getTextBackgroundColor( 0 ) }
		}
		
		TextBox( fontName, fontStyle, fontSize.value.toInt, textColor, bgColor )
	}

	def apply( fontName:String, style:TextStyle, fontSize:Int, textColor:Color, bgColor:Color ):TextBox = {
		new TextBox( FontCache.getFont( fontName, style, fontSize ), textColor, bgColor )
	}
}