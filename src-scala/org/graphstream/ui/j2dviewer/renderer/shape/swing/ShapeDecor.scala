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
package org.graphstream.ui.j2dviewer.renderer.shape.swing

import scala.math._
import java.awt._
import java.awt.geom._
import java.awt.font.TextLayout
import java.awt.image.BufferedImage
import org.graphstream.ui.j2dviewer.Camera
import org.graphstream.ui.util._
import org.graphstream.ui.util.swing._
import org.graphstream.ui.geom._
import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._
import java.awt.font.FontRenderContext
import org.graphstream.ui.j2dviewer.Backend

/**
 * Representation of the icon and text that can decorate any "decorated" shape.
 */
abstract class ShapeDecor {
	/** Render the decoration inside the given box coordinates. The shape decoration contains all the metrics
	  * to render the `iconAndText` icon and text. The coordinates (`x0`, `y0`) and (`x1`, `y1`)
	  * indicates the lower-left and upper-right coordinates of the area where the decoration should be
	  * drawn. */
	def renderInside(b:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double )

	/** Render along the given line coordinates. The shape decoration contains all the metrics
	  * to render the `iconAndText` icon and text. The coordinates (`x0`, `y0`) and (`x1`, `y1`)
	  * indicates the start and end points of the line to draw the text on. */
	def renderAlong(b:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double  )
	
	/** Overall size (width and height) of the decoration, taking into account the `iconAndText` as
	 *  well as the various metrics specified by the style. */
	def size(b:Backend, camera:Camera, iconAndText:IconAndText ):(Double,Double)
}

/**
 * Companion object for text and icon decoration on shapes.
 */
object ShapeDecor {
	/** Generate a new icon and text specific to the given `element`, according to the given
	 *  `style` and `camera`. */
	def iconAndText(style:Style, camera:Camera, element:GraphicElement ):IconAndText = IconAndText( style, camera, element)
	
	def apply(style:Style):ShapeDecor = {
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
		def renderInside(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {}
		def renderAlong(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {}
		def size(backend:Backend, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
    }
 
	/** Base for shape decors that work in pixels units, not GU. */
	abstract class PxShapeDecor extends ShapeDecor {
		/* We choose here to replace the transform (GU->PX) into a the identity to draw
		 * The text and icon. Is this the best way ? Maybe should we merely scale the 
		 * font size to render the text at the correct size ? How to handle the icon in
		 * this case ? 
		 */
		protected def renderGu2Px(backend:Backend, camera:Camera, iconAndText:IconAndText, x:Double, y:Double, angle:Double,
				positionPx:(Backend, Point3, IconAndText, Double)=>Point3) {
		    var g  = backend.graphics2D
			var p  = camera.transformGuToPx( x, y, 0 )
			val Tx = g.getTransform

			g.setTransform( new AffineTransform )

			p = positionPx(backend, p, iconAndText, angle )
   
			iconAndText.render(backend, camera, p.x, p.y )
			g.setTransform( Tx )
		}
	}
 
	class CenteredShapeDecor extends PxShapeDecor {
		def renderInside(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0 + (x1 - x0) / 2
			val cy = y0 + (y1 - y0) / 2

			renderGu2Px(backend, camera, iconAndText, cx, cy, 0, positionTextAndIconPx)
		}
		def renderAlong(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val dir = new Vector2( x1-x0, y1-y0 )
			dir.scalarMult( 0.5f )
			
			renderGu2Px(backend, camera, iconAndText, x0 + dir.x, y0 + dir.y, 0, positionTextAndIconPx)
		}
		protected def positionTextAndIconPx(backend:Backend, p:Point3, iconAndText:IconAndText, angle:Double ):Point3 = {
			p.x = p.x - ( iconAndText.width / 2 + 1 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady*2
			p
		}
		def size(backend:Backend, camera:Camera, iconAndText:IconAndText ):(Double,Double) = {
				( camera.metrics.lengthToGu( iconAndText.width, Units.PX ),
				  camera.metrics.lengthToGu( iconAndText.height, Units.PX ) )
		}
	}
 
 	class AtLeftShapeDecor extends PxShapeDecor {
		def renderInside(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px(backend, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def renderAlong(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0
			val cy = y0
			
			renderGu2Px(backend, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def size(backend:Backend, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
		protected def positionTextAndIconPx(backend:Backend, p:Point3, iconAndText:IconAndText, angle:Double ):Point3 = {
			p.x = p.x - ( iconAndText.width + 2 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
	}
 
 	class AtRightShapeDecor extends PxShapeDecor {
		def renderInside(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x1
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px(backend, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def renderAlong(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x1
			val cy = y1
			
			renderGu2Px(backend, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		protected def positionTextAndIconPx(backend:Backend, p:Point3, iconAndText:IconAndText, angle:Double ):Point3 = {
			p.x = p.x + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size(backend:Backend, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
	}
 
 	class LeftShapeDecor extends PxShapeDecor {
		def renderInside(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px(backend:Backend, camera, iconAndText, cx, cy, 0, positionTextAndIconAreaPx )
		}
		def renderAlong(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			renderGu2Px(backend, camera, iconAndText, x0, y0,  0, positionTextAndIconAlongPx )
		}
		protected def positionTextAndIconAreaPx(backend:Backend, p:Point3, iconAndText:IconAndText, angle:Double ):Point3 = {
			p.x = p.x - ( iconAndText.width + 2 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		protected def positionTextAndIconAlongPx(backend:Backend, p:Point3, iconAndText:IconAndText, angle:Double ):Point3 = {
			p.x = p.x + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size(backend:Backend, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
	}
 
 	class RightShapeDecor extends PxShapeDecor {
		def renderInside(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y0 + ( y1 - y0 ) / 2

			renderGu2Px(backend:Backend, camera, iconAndText, cx, cy, 0, positionTextAndIconAreaPx )
		}
		def renderAlong(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			renderGu2Px(backend:Backend, camera, iconAndText, x1, y1, 0, positionTextAndIconAlongPx )
		}
		protected def positionTextAndIconAreaPx(backend:Backend, p:Point3, iconAndText:IconAndText, angle:Double ):Point3 = {
			p.x = p.x + iconAndText.padx
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		protected def positionTextAndIconAlongPx(backend:Backend, p:Point3, iconAndText:IconAndText, angle:Double ):Point3 = {
			p.x = p.x - ( iconAndText.width + 2 + iconAndText.padx )
			p.y = p.y + ( iconAndText.height / 2 ) - iconAndText.pady
			p
		}
		def size(backend:Backend, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
	}
 
 	class UnderShapeDecor extends PxShapeDecor {
		def renderInside(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y0

			renderGu2Px(backend, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def renderAlong(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val dir = new Vector2( x1-x0, y1-y0 )
			dir.scalarMult( 0.5f )

			renderGu2Px(backend, camera, iconAndText, x0+dir.x, y0+dir.y, 0, positionTextAndIconPx )
		}
		protected def positionTextAndIconPx(backend:Backend, p:Point3, iconAndText:IconAndText, angle:Double ):Point3 = {
			p.x = p.x - ( iconAndText.width / 2 + 1 ) + iconAndText.padx
			p.y = p.y + ( iconAndText.height ) - iconAndText.pady
			p
		}
		def size(backend:Backend, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
	}
 
 	class AboveShapeDecor extends PxShapeDecor {
		def renderInside(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val cx = x0 + ( x1 - x0 ) / 2
			val cy = y1

			renderGu2Px(backend, camera, iconAndText, cx, cy, 0, positionTextAndIconPx )
		}
		def renderAlong(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val dir = new Vector2( x1-x0, y1-y0 )
			dir.scalarMult( 0.5f )

			renderGu2Px(backend, camera, iconAndText, x0+dir.x, y0+dir.y, 0, positionTextAndIconPx )
		}
		protected def positionTextAndIconPx(backend:Backend, p:Point3, iconAndText:IconAndText, angle:Double ):Point3 = {
			p.x = p.x - ( iconAndText.width / 2 + 1 ) + iconAndText.padx
			p.y = p.y - iconAndText.pady
			p
		}
		def size(backend:Backend, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
	}
 	
 	class AlongShapeDecor extends PxShapeDecor {
		def renderInside(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {}
		def renderAlong(backend:Backend, camera:Camera, iconAndText:IconAndText, x0:Double, y0:Double, x1:Double, y1:Double ) {
			val dir = new Vector2( x1-x0, y1-y0 )
			dir.scalarMult( 0.5f )
			val cx = x0 + dir.x
			val cy = y0 + dir.y
			dir.normalize
			var angle = acos( dir.dotProduct( 1, 0 ) )
		
			if( dir.y > 0 )			// The angle is always computed for acute angles
				angle = ( Pi - angle )
				
			if( angle > Pi/2 ) angle = ( Pi + angle )
				
			renderGu2Px(backend, camera, iconAndText, cx, cy, angle, positionTextAndIconPx )
		}
		protected def positionTextAndIconPx(backend:Backend, p:Point3, iconAndText:IconAndText, angle:Double ):Point3 = {
		    val g = backend.graphics2D
			g.translate( p.x, p.y )
			g.rotate( angle )
			g.translate( -iconAndText.width/2, +iconAndText.height/2 )
			new Point3( 0, 0, 0 )
		}
		def size(backend:Backend, camera:Camera, iconAndText:IconAndText ):(Double,Double) = ( 0, 0 )
 	}
}

/**
 * A wrapper for a text and an icon.
 * 
 * The text is dynamic and therefore specified at each rendering. The icon is fixed by the style
 * and specified only at construction, excepted if the icon name is "dynamic" in which case it is
 * taken from the "ui.icon" attribute of the element.
 * 
 * The constructor parameters `offx`, `offy`, `padx` and `pady` are expressed in pixels. The
 * represent the offset of the text according to its origin and the padding around the text.
 * The `text` parameter is a `TextBox` that allows to wrap the text.
 * 
 * XXX TODO This should really be called "ElementContents".
 */
abstract class IconAndText(val text:TextBox, val offx:Double, val offy:Double, val padx:Double, val pady:Double) {
    /** Set the text string to paint. */
	def setText(backend:Backend, text:String)
	/** Set the icon image to draw, or null to remove the icon. */
	def setIcon(backend:Backend, url:String)
	/** Render the icon and text at the specified position. */
	def render(backend:Backend, camera:Camera, xLeft:Double, yBottom:Double)
	/** Overall width of the icon and text with all space and padding included. */
	def width:Double
	/** Overall height of the icon and text with all space and padding included. */
	def height:Double
	/** Overall descent of the icon and text with all space and padding included. */
	def descent:Double = text.descent
	/** Overall ascent of the icon and text with all space and padding included. */
	def ascent:Double = text.ascent
}

/**
 * Companion object for text and icon.
 * 
 * Allows to create the icon and text bundle from a given style.
 */
object IconAndText {
	def apply(style:Style, camera:Camera, element:GraphicElement):IconAndText = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.IconMode._
		var icon:BufferedImage = null
		val text = TextBox( camera, style )
		val padd = style.getPadding
		val off  = style.getTextOffset
		val padx = camera.metrics.lengthToPx(padd, 0)
		val pady = if(padd.size>1) camera.metrics.lengthToPx(padd,1) else padx
		val offx = camera.metrics.lengthToPx(off, 0)
		val offy = if(padd.size>1) camera.metrics.lengthToPx(off,1) else padx
		
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
			new IconAndTextOnlyText( text, offx, offy, padx, pady )
		} else {
			style.getIconMode match {
			  case AT_LEFT  => new IconAtLeftAndText( icon, text, offx, offy, padx, pady )
			  case AT_RIGHT => new IconAtLeftAndText( icon, text, offx, offy, padx, pady )//IconAtRightAndText( icon, text ) TODO
			  case ABOVE    => new IconAtLeftAndText( icon, text, offx, offy, padx, pady )//IconAboveAndText( icon, text ) TODO
			  case UNDER    => new IconAtLeftAndText( icon, text, offx, offy, padx, pady )//IconUnderAndText( icon, text ) TODO
			  case _        => throw new RuntimeException( "WTF ?" )
			}
		}
	}
 
	class IconAndTextOnlyText(text:TextBox, offx:Double, offy:Double, padx:Double, pady:Double) extends IconAndText(text, offx, offy, padx, pady) {
		def setText(backend:Backend, text:String) { this.text.setText(text, backend) }
		def setIcon(backend:Backend, url:String) {}
		def render(backend:Backend, camera:Camera, xLeft:Double, yBottom:Double) {
			this.text.render(backend, offx+xLeft, offy+yBottom - descent)
		}
		def width:Double = text.width + padx*2
		def height:Double = text.ascent + text.descent + pady*2
	}
 
	class IconAtLeftAndText(var icon:BufferedImage, text:TextBox, offx:Double, offy:Double, padx:Double, pady:Double) extends IconAndText(text, offx, offy, padx, pady) {
		def setText(backend:Backend, text:String) { this.text.setText(text, backend) }
		def setIcon(backend:Backend, url:String) {
			icon = ImageCache.loadImage(url) match {
				case x:Some[_] => x.get
				case _         => ImageCache.dummyImage
			}
		}
		def render(backend:Backend, camera:Camera, xLeft:Double, yBottom:Double) {
		    val g = backend.graphics2D
			g.drawImage(icon, new AffineTransform(1f, 0f, 0f, 1f, offx+xLeft, offy+(yBottom-(height/2))-(icon.getHeight/2)+pady), null)
		//	g.setColor(new Color(255,0,0,128))
		//	g.fillRect(xLeft.toInt, (yBottom-height).toInt, width.toInt, height.toInt)
			val th = text.ascent + text.descent
			val dh = if(icon.getHeight > th) ((icon.getHeight - th) / 2f) else 0f
			this.text.render(backend, offx+xLeft + icon.getWidth + 5, offy+yBottom - dh - descent)
		}
		def width:Double = text.width + icon.getWidth(null) + 5 + padx*2
		def height:Double = max(icon.getHeight(null), text.ascent + text.descent) + pady*2
	}
}


/** A simple wrapper for a font and a text string. */
abstract class TextBox {
	/** Width of the text. */
 	def width:Double
 	/** Height of the text. */
 	def height:Double
 	/** Descent of the text. */
 	def descent:Double
 	/** Ascent of the text. */
 	def ascent:Double
 	/** Renders the text at the given coordinates. */
 	def render(backend:Backend, xLeft:Double, yBottom:Double)
 	/** Set the text string to paint. */
	def setText(text:String, backend:Backend)
 	/** The text string. */
	def textData:String
}

/** A simple wrapper for a font and a text string. */
class SwingTextBox(val font:Font, val textColor:Color, val bgColor:Color, val rounded:Boolean, val padx:Double, val pady:Double) extends TextBox {
	/** The text stored as a text layout. */
	var text:TextLayout = null
	
	/** The original text data. */
	var textData:String = null
	
	/** The bounds of the text stored when the text is changed. */
	var bounds:Rectangle2D = new Rectangle2D.Double(0, 0, 0, 0)

	/** Changes the text and compute its bounds. This method tries to avoid recomputing bounds
	 *  if the text does not really changed. */
	def setText(text:String, backend:Backend) {
	  	if((text ne null) && (text.length > 0)) {
	  		if( (textData ne text) || (textData != text) ) {
//Console.err.printf( "recomputing text '%s' != '%s' length%n", text, textData )
	  		    // As the text is not rendered using the default affine transform, but using
	  		    // the identity transform, and as the FontRenderContext uses the current
	  		    // transform, we use a predefined default font render context initialized
	  		    // with an identity transform here.
				textData    = text
	  			this.text   = new TextLayout(text, font, TextBox.defaultFontRenderContext)
	  			this.bounds = this.text.getBounds
	  	  	}
	  	} else {
	  		this.textData = null
	  		this.text     = null
	  		this.bounds   = new Rectangle2D.Double(0, 0, 0, 0)
	  	}
	}
	
	/** Width of the text. */
 	def width:Double   = if(bounds ne null) bounds.getWidth else 0
 	
 	/** Height of the text. */
 	def height:Double  = if(bounds ne null) bounds.getHeight else 0
 	
 	/** Descent of the text. */
 	def descent:Double = if(text ne null) text.getDescent else 0
 	
 	/** Ascent of the text. */
 	def ascent:Double  = if(text ne null) text.getAscent else 0
 	
 	/** Renders the text at the given coordinates. */
 	def render(backend:Backend, xLeft:Double, yBottom:Double) {
		if(text ne null) {
			val g = backend.graphics2D
			if(bgColor ne null) {
				val a = ascent
				val h = a + descent
				g.setColor(bgColor)
				if(rounded)
				     g.fill(new RoundRectangle2D.Double(xLeft-padx, yBottom-(a+pady), width+1+(padx+padx), h+(pady+pady), 6, 6))
				else g.fill(new Rectangle2D.Double(xLeft-padx, yBottom-(a+pady), width+1+(padx+padx), h+(pady+pady)))
			}

			g.setColor( textColor )
			text.draw(g, xLeft.toFloat, yBottom.toFloat)
		}
 	}
}

/**
 * Factory companion object for text boxes.
 */
object TextBox {
    val defaultFontRenderContext = new FontRenderContext(new AffineTransform, true, true)
    
	def apply(camera:Camera, style:Style):TextBox = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextBackgroundMode._
		
		val fontName  = style.getTextFont
		val fontStyle = style.getTextStyle
		val fontSize  = style.getTextSize
		val textColor = style.getTextColor(0)
		var bgColor:Color = null
		var rounded = false

		style.getTextBackgroundMode match {
			case NONE       => {}
			case PLAIN      => { rounded = false; bgColor = style.getTextBackgroundColor(0) }
			case ROUNDEDBOX => { rounded = true;  bgColor = style.getTextBackgroundColor(0) }
		}
		
		val padding = style.getTextPadding
		val padx    = camera.metrics.lengthToPx(padding, 0)
		val pady    = if(padding.size>1) camera.metrics.lengthToPx(padding, 1) else padx
		TextBox(fontName, fontStyle, fontSize.value.toInt, textColor, bgColor, rounded, padx, pady)
	}

	def apply(fontName:String, style:TextStyle, fontSize:Int, textColor:Color, bgColor:Color, rounded:Boolean, padx:Double, pady:Double):TextBox = {
		new SwingTextBox(FontCache.getFont( fontName, style, fontSize ), textColor, bgColor, rounded, padx, pady)
	}
}