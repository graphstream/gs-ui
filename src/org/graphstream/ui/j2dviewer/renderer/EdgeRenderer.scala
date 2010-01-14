package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D
import org.graphstream.ui2.graphicGraph.{GraphicElement, StyleGroup, GraphicEdge}
import org.graphstream.ui.j2dviewer.util.Camera
import org.graphstream.ui.j2dviewer.renderer.shape._

class EdgeRenderer( styleGroup:StyleGroup ) extends StyleRenderer( styleGroup ) {
	var shape = new LineShape
	var arrow = new ArrowOnEdge
  
	protected def setupRenderingPass( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
	  
	}
	
	protected def pushStyle( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
	  	val size = group.getSize
		shape.configure( g, group, camera )
		shape.size( group, camera )
		arrow.configure( g, group, camera )
		arrow.sizeForEdgeArrow( group, camera )
	}
	
	protected def pushDynStyle( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		pushStyle( g, camera, false )
	}
	
	protected def renderElement( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val edge = element.asInstanceOf[GraphicEdge]
		shape.text = element.label

		shape.targetNodeSize( edge.to.getStyle, camera )
		shape.position( edge.from.getX, edge.from.getY, edge.to.getX, edge.to.getY, edge.multi, edge.getGroup )
		shape.render( g, camera )
  
		if( edge.isDirected ) {
		  	arrow.theEdge = edge
		  	arrow.direction( shape )
		  	arrow.position( edge.to.getX, edge.to.getY )
		  	arrow.render( g, camera )
		}
	}
	
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val edge = element.asInstanceOf[GraphicEdge]

		shape.targetNodeSize( edge.to.getStyle, camera )
		shape.position( edge.from.getX, edge.from.getY, edge.to.getX, edge.to.getY, edge.multi, edge.getGroup )
		shape.renderShadow( g, camera )
  
		if( edge.isDirected ) {
			arrow.theEdge = edge
			arrow.direction( shape )
			arrow.position( edge.to.getX, edge.to.getY )
			arrow.renderShadow( g, camera )
		}
	}
 
	protected def elementInvisible( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	  
	}
}

object EdgeRenderer {
	def apply( style:StyleGroup, mainRenderer:J2DGraphRenderer ):StyleRenderer = {
		new EdgeRenderer( style )
	}
}