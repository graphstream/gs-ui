package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D
import org.graphstream.ui2.graphicGraph.{GraphicElement, StyleGroup, GraphicEdge}
import org.graphstream.ui.j2dviewer.util.Camera
import org.graphstream.ui.j2dviewer.renderer.shape._

class EdgeRenderer( styleGroup:StyleGroup ) extends StyleRenderer( styleGroup ) {
	var shape = new LineShape
  
	protected def setupRenderingPass( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
	  
	}
	
	protected def pushStyle( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
	  	val size = group.getSize
		shape.configure( g, group, camera )
		shape.size( group, camera )
	}
	
	protected def pushDynStyle( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		pushStyle( g, camera, false )
	}
	
	protected def renderElement( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val edge = element.asInstanceOf[GraphicEdge]
	  
		shape.text = element.label
		shape.position( edge.from.getX, edge.from.getY, edge.to.getX, edge.to.getY )
		shape.render( g, camera )
	}
	
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val edge = element.asInstanceOf[GraphicEdge]

		shape.position( edge.from.getX, edge.from.getY, edge.to.getX, edge.to.getY )
		shape.renderShadow( g, camera )
	}
 
	protected def elementInvisible( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	  
	}
}

object EdgeRenderer {
	def apply( style:StyleGroup, mainRenderer:J2DGraphRenderer ):StyleRenderer = {
		new EdgeRenderer( style )
	}
}