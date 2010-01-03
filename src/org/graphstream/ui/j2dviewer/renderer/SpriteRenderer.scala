package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D
import org.graphstream.ui2.graphicGraph.{GraphicElement, StyleGroup}
import org.graphstream.ui.j2dviewer.util.Camera

class SpriteRenderer( styleGroup:StyleGroup ) extends StyleRenderer( styleGroup ) {
	protected def setupRenderingPass( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
	  
	}
	
	protected def pushStyle( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
	  
	}
	
	protected def pushDynStyle( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	  
	}
	
	protected def renderElement( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	  
	}
	
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	  
	}
 
	protected def elementInvisible( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	  
	}
}

object SpriteRenderer {
	def apply( style:StyleGroup, mainRenderer:J2DGraphRenderer ):StyleRenderer = {
		new SpriteRenderer( style )
	}
}