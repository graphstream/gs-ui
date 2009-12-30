package org.graphstream.ui.j2dviewer.renderer

import java.awt.{Graphics2D, Color}

import org.graphstream.ui2.graphicGraph.{StyleGroup, GraphicGraph, GraphicElement}
import org.graphstream.ui.j2dviewer.util.{Camera, GradientFactory}

/**
 * Renderer for the graph background.
 * 
 * <p>
 * This class is not a StyleRenderer because the graph is not a GraphicElement.
 * </p>
 */
class GraphBackgroundRenderer( val graph:GraphicGraph, val style:StyleGroup ) extends GraphicElement.SwingElementRenderer {

	/**
     * Render a background indicating there is nothing to draw. 
	 */
	protected def displayNothingToDo( g:Graphics2D, w:Int, h:Int ) {
		val msg1 = "Graph width/height/depth is zero !!"
		val msg2 = "Place components using the 'xyz' attribute."
		
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
	def render( g:Graphics2D, camera:Camera, w:Int, h:Int ) {
		if( camera.metrics.diagonal == 0 || ( graph.getNodeCount == 0 && graph.getSpriteCount == 0 ) ) {
			displayNothingToDo( g, w, h )
		} else {
			renderGraphBackground( g, camera )
		}
	}

	protected def renderGraphBackground( g:Graphics2D, camera:Camera ) {
		import org.graphstream.ui2.graphicGraph.stylesheet.StyleConstants.FillMode._

		graph.getStyle.getFillMode match {
			case NONE                => {}
			case IMAGE_TILED         => fillBackground( g, camera )
			case IMAGE_SCALED        => fillBackground( g, camera )
			case IMAGE_SCALED_RATIO  => fillBackground( g, camera )
			case GRADIENT_DIAGONAL1  => fillGradient( g, camera )
			case GRADIENT_DIAGONAL2  => fillGradient( g, camera )
			case GRADIENT_HORIZONTAL => fillGradient( g, camera )
			case GRADIENT_VERTICAL   => fillGradient( g, camera )
			case GRADIENT_RADIAL     => fillGradient( g, camera )
			case DYN_PLAIN           => fillBackground( g, camera )
			case _                   => fillBackground( g, camera )
		}
	}

	protected def fillBackground( g:Graphics2D, camera:Camera ) {
		val style   = graph.getStyle 
		val metrics = camera.metrics

		g.setColor( style.getFillColor( 0 ) )
		g.fillRect( 0, 0,
			metrics.viewport.data(0).toInt,
			metrics.viewport.data(1).toInt )
	}

	protected def fillGradient( g:Graphics2D, camera:Camera ) {
		// TODO use a Shape of the Shape library to do this.
	  
		val style   = graph.getStyle 
		val metrics = camera.metrics

		if( style.getFillColors.size < 2 ) {
			fillBackground( g, camera )
		} else {
			val w = metrics.viewport.data(0).toInt 
			val h = metrics.viewport.data(1).toInt
   printf( "viewport = %s%n", metrics.viewport )
			
			g.setPaint( GradientFactory.gradientInArea( 0, 0, w, h, style ) )
			g.fillRect( 0, 0, w, h )
			g.setPaint( null )
		}
	}
}