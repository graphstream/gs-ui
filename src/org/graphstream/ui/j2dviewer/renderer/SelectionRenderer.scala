package org.graphstream.ui.j2dviewer.renderer

import java.awt.{Graphics2D, Color}
import java.awt.geom.Rectangle2D
import org.graphstream.ui2.graphicGraph.GraphicGraph
import org.graphstream.ui.j2dviewer.util.{Selection, Camera}

class SelectionRenderer( val selection:Selection, val graph:GraphicGraph ) {
	protected val shape        = new Rectangle2D.Float
	protected val linesColor   = new Color( 240, 240, 240 )
	protected val linesColorQ  = new Color(   0,   0,   0, 128 )
	protected val fillColor    = new Color(  50,  50, 200, 128 )
	protected val strokeColorQ = new Color(  50,  50, 200, 64 )
	protected val strokeColor  = new Color( 128, 128, 128 )
 
	/** Render the selection (in pixel units). */
	def render( g:Graphics2D, camera:Camera, panelWidth:Int, panelHeight:Int ) {
		if( selection.active && selection.x1 != selection.x2 && selection.y1 != selection.y2 ) {
			val quality = ( graph.hasAttribute( "ui.quality" ) || graph.hasAttribute( "ui.antialias" ) )
			var x1 = selection.x1
			var y1 = selection.y1
			var x2 = selection.x2
			var y2 = selection.y2
			var t = 0f
			
			if( x1 > x2 ) { t = x1; x1 = x2; x2 = t }
			if( y1 > y2 ) { t = y1; y1 = y2; y2 = t }
   
			if( quality )
			     g.setColor( linesColorQ )
			else g.setColor( linesColor ) 
   
			g.drawLine( 0, y1.toInt, panelWidth, y1.toInt )
			g.drawLine( 0, y2.toInt, panelWidth, y2.toInt )
			g.drawLine( x1.toInt, 0, x1.toInt, panelHeight )
			g.drawLine( x2.toInt, 0, x2.toInt, panelHeight )
	
			shape.setFrame( x1, y1, x2-x1, y2-y1 )

			if( quality ) {
				g.setColor( fillColor )
				g.fill( shape )
				g.setColor( strokeColorQ )
			} else {
				g.setColor( strokeColor )
			}
   
			g.draw( shape )
		}
	}
}