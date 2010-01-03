package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D
import org.graphstream.ui2.graphicGraph.{GraphicElement, StyleGroup}
import org.graphstream.ui.j2dviewer.util.Camera
import org.graphstream.ui.j2dviewer.renderer.shape._

class NodeRenderer( styleGroup:StyleGroup ) extends StyleRenderer( styleGroup ) {
	var shape = new RoundedSquareShape
//	var shape = new CircleShape
// 	var shape = new SquareShape
 
	protected def setupRenderingPass( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
	}
	
	protected def pushStyle( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
		val size = group.getSize
		shape.configure( g, group, camera )
		shape.size( group, camera )
	}
	
	protected def pushDynStyle( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val size = group.getSize
		shape.configure( g, group, camera )
		shape.size( group, camera )
	}
	
	protected def renderElement( g:Graphics2D, camera:Camera, element:GraphicElement ) {
Console.err.printf( "Render element %s (%s):%n", element.getId, element.label )
		shape.text = element.label
		shape.position( element.getX, element.getY )
		shape.render( g, camera )
	}
	
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		shape.position( element.getX, element.getY )
		shape.renderShadow( g, camera )
	}
 
	protected def elementInvisible( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		// NOP
	}
}

object NodeRenderer {
	def apply( style:StyleGroup ):NodeRenderer = {
		new NodeRenderer( style )
	}
}