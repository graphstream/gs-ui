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
package org.graphstream.ui.j2dviewer.renderer

import java.awt.{Graphics2D, Color, BasicStroke, TexturePaint}
import java.awt.image.BufferedImage
import java.awt.geom.{RectangularShape, Rectangle2D}


import org.graphstream.ui.graphicGraph.{StyleGroup, GraphicGraph, GraphicElement}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.j2dviewer.{Camera, Backend}
import org.graphstream.ui.util.swing.{GradientFactory, ImageCache}

/**
 * Renderer for the graph background.
 * 
 * This class is not a StyleRenderer because the graph is not a GraphicElement.
 * 
 * TODO XXX make this class an abstract one, and create several distinct back-ends.
 */
class GraphBackgroundRenderer(val graph:GraphicGraph, val style:StyleGroup)
	extends GraphicElement.SwingElementRenderer {

	/**
     * Render a background indicating there is nothing to draw. 
	 */
	protected def displayNothingToDo(bck:Backend, w:Int, h:Int) {
		val msg1 = "Graph width/height/depth is zero !!"
		val msg2 = "Place components using the 'xyz' attribute."
		val g    = bck.graphics2D
		    
		g.setColor( Color.WHITE )
		g.fillRect( 0, 0, w, h )
		g.setColor( Color.RED )
		g.drawLine( 0, 0, w, h )
		g.drawLine( 0, h, w, 0 )
		
		val msg1length = g.getFontMetrics().stringWidth( msg1 )
		val msg2length = g.getFontMetrics().stringWidth( msg2 )
		val x = w / 2
		val y = h / 2

		g.setColor( Color.BLACK )
		g.drawString( msg1, x - msg1length/2, y-20 )
		g.drawString( msg2, x - msg2length/2, y+20 )
	}

	/**
     * Render the graph background. 
     */
	def render(bck:Backend, camera:Camera, w:Int, h:Int) {
	    if((camera.graphViewport eq null) && camera.metrics.diagonal == 0
		&& (graph.getNodeCount == 0 && graph.getSpriteCount == 0)) {
			displayNothingToDo(bck, w, h)
		} else {
			renderGraphBackground(bck, camera)
			strokeGraph(bck, camera)
		}
	}

	protected def renderGraphBackground(bck:Backend, camera:Camera) {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode._

		val g = bck.graphics2D
		
		graph.getStyle.getFillMode match {
			case NONE                   => {}
			case IMAGE_TILED            => fillImageTiled(g, camera)
			case IMAGE_SCALED           => fillImageScaled(g, camera, 0)
			case IMAGE_SCALED_RATIO_MAX => fillImageScaled(g, camera, 1)
			case IMAGE_SCALED_RATIO_MIN => fillImageScaled(g, camera, 2)
			case GRADIENT_DIAGONAL1     => fillGradient(g, camera)
			case GRADIENT_DIAGONAL2     => fillGradient(g, camera)
			case GRADIENT_HORIZONTAL    => fillGradient(g, camera)
			case GRADIENT_VERTICAL      => fillGradient(g, camera)
			case GRADIENT_RADIAL        => fillGradient(g, camera)
			case DYN_PLAIN              => fillBackground(g, camera)
			case _                      => fillBackground(g, camera)
		}
	}

	protected def fillBackground(g:Graphics2D, camera:Camera) {
		val metrics = camera.metrics

		g.setColor( style.getFillColor( 0 ) )
		g.fillRect( 0, 0,
			metrics.viewport(2).toInt,
			metrics.viewport(3).toInt )
	}
	
	protected def fillCanvasBackground(g:Graphics2D, camera:Camera) {
		val metrics = camera.metrics

		g.setColor( style.getCanvasColor( 0 ) )
		g.fillRect( 0, 0,
			metrics.viewport(2).toInt,
			metrics.viewport(3).toInt )
	}
	
	protected def fillImageTiled(g:Graphics2D, camera:Camera) {
		val metrics = camera.metrics
		val px2gu   = metrics.ratioPx2Gu
		var img:BufferedImage = null
		
		ImageCache.loadImage( style.getFillImage ) match {
			case x:Some[BufferedImage] => { img = x.get }
			case _                     => { img = ImageCache.dummyImage }
		}

		// We offset the tilling so that the lower-left corner of the graph is at (0,0) 
		// in origin tile.
		
//		val padx  = metrics.lengthToPx( style.getPadding, 0 ) 
//		val pady  = metrics.lengthToPx( style.getPadding, 1 ) 
		val gw    = ( metrics.graphWidthGU  * px2gu )// + ( padx * 2 )	// consider the padding ???
		val gh    = ( metrics.graphHeightGU * px2gu )// + ( pady * 2 )	// probably not.
		val x     = ( metrics.viewport(2) / 2 ) - ( gw / 2 )
		val y     = metrics.viewport(3) - ( metrics.viewport(3) / 2 ) - ( gh / 2 )
		val paint = new TexturePaint( img, new Rectangle2D.Double( x, y, img.getWidth, img.getHeight) )
		val rect  = new Rectangle2D.Double( 0, 0, metrics.viewport(2), metrics.viewport(3) );
		
		g.setPaint( paint )
		g.fill( rect );
		g.setPaint( null )
	}
	
	protected def fillImageScaled( g:Graphics2D, camera:Camera, mode:Int ) {
		val metrics = camera.metrics
		val px2gu   = metrics.ratioPx2Gu
		var img:BufferedImage = null
		
		ImageCache.loadImage( style.getFillImage ) match {
			case x:Some[BufferedImage] => { img = x.get }
			case _                     => { img = ImageCache.dummyImage }
		}
		
		fillCanvasBackground( g, camera )

		val gw = ( metrics.graphWidthGU  * px2gu )
		val gh = ( metrics.graphHeightGU * px2gu )
		val x  = ( metrics.viewport(2) / 2 ) - ( gw / 2 )
		val y  = metrics.viewport(3) - ( metrics.viewport(3) / 2 ) - ( gh / 2 )

		mode match {
			case 0 => {	// Ratio
				g.drawImage( img,
					x.toInt, y.toInt, (x+gw).toInt, (y+gh).toInt,
					0, 0, img.getWidth, img.getHeight, null )
			}
			case 1 => {	// Ratio-max
				val ratioi = img.getWidth.toDouble / img.getHeight.toDouble
				val ratiog = gw / gh
				
				if(ratioi > ratiog) {
					val newgw = gh * ratioi
					val newx  = x - ((newgw-gw)/2)
					g.drawImage( img,
							newx.toInt, y.toInt, (newx+newgw).toInt, (y+gh).toInt,
							0, 0, img.getWidth, img.getHeight, null )
				} else {
					val newgh = gw / ratioi
					val newy  = y - ((newgh-gh)/2)
					g.drawImage(img,
							x.toInt, newy.toInt, (x+gw).toInt, (newy+newgh).toInt,
							0, 0, img.getWidth, img.getHeight, null)
				}
			}
			case 2 => {	// Ratio-min
				val ratioi = img.getWidth.toDouble / img.getHeight.toDouble
				val ratiog = gw / gh
				
				if( ratiog > ratioi ) {
					val newgw = gh * ratioi
					val newx  = x + ((gw-newgw)/2)
					g.drawImage( img,
							newx.toInt, y.toInt, (newx+newgw).toInt, (y+gh).toInt,
							0, 0, img.getWidth, img.getHeight, null )
				} else {
					val newgh = gw / ratioi
					val newy  = y + ((gh-newgh)/2)
					g.drawImage(img,
							x.toInt, newy.toInt, (x+gw).toInt, (newy+newgh).toInt,
							0, 0, img.getWidth, img.getHeight, null)
				}
			}
			case _ => { throw new RuntimeException("WTF??") }
		}
	}
 
	protected def strokeGraph(bck:Backend, camera:Camera) {
		val metrics = camera.metrics
		val g = bck.graphics2D

		if( style.getStrokeMode != StyleConstants.StrokeMode.NONE && style.getStrokeWidth.value > 0 ) {
			g.setColor( style.getStrokeColor( 0 ) )
			g.setStroke( new BasicStroke( metrics.lengthToGu( style.getStrokeWidth ).toFloat ) );
			val padx = metrics.lengthToPx( style.getPadding, 0 ).toInt
			val pady = if( style.getPadding.size > 1 ) metrics.lengthToPx( style.getPadding, 1 ).toInt else padx
			g.drawRect( padx, pady,
				metrics.viewport(2).toInt - padx*2,
				metrics.viewport(3).toInt - pady*2 )
		}
	}

	protected def fillGradient(g:Graphics2D, camera:Camera) {
		// TODO use a Shape of the Shape library to do this.
	  
		val metrics = camera.metrics

		if( style.getFillColors.size < 2 ) {
			fillBackground( g, camera )
		} else {
			val w = metrics.viewport(2).toInt 
			val h = metrics.viewport(3).toInt
//   printf( "viewport = %s%n", metrics.viewport )
			
			g.setPaint( GradientFactory.gradientInArea( 0, 0, w, h, style ) )
			g.fillRect( 0, 0, w, h )
			g.setPaint( null )
		}
	}
}