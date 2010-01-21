package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D
import org.graphstream.ui.graphicGraph.{GraphicElement, StyleGroup, GraphicEdge}
import org.graphstream.ui.j2dviewer.util.Camera
import org.graphstream.ui.j2dviewer.renderer.shape._

class EdgeRenderer( styleGroup:StyleGroup ) extends StyleRenderer( styleGroup ) {
	var shape:ConnectorShape = null
	var arrow:AreaOnConnectorShape = null
  
	protected def setupRenderingPass( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
		shape = chooseShape
		arrow = chooseArrowShape
	}
	
	protected def pushStyle( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
	  	val size = group.getSize
		shape.configure( g, group, camera, null )
		shape.size( group, camera )
		arrow.configure( g, group, camera, null )
		arrow.sizeForEdgeArrow( group, camera )
	}
	
	protected def pushDynStyle( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	  	val size = group.getSize
		shape.configure( g, group, camera, element )
		shape.size( group, camera )
		arrow.configure( g, group, camera, element )
		arrow.sizeForEdgeArrow( group, camera )
	}
	
	protected def renderElement( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val edge = element.asInstanceOf[GraphicEdge]
		shape.text = element.label

		shape.endPoints( edge.from.getStyle, edge.to.getStyle, edge.isDirected, camera )
		shape.position( edge.from.getX, edge.from.getY, edge.to.getX, edge.to.getY, edge.multi, edge.getGroup )
		shape.setEdgeCtrlPoints( edge )// XXX HORROR, TERROR XXX
		shape.render( g, camera )
  
		if( edge.isDirected ) {
		  	arrow.connector( edge )
		  	arrow.direction( shape )
		  	arrow.position( edge.to.getX, edge.to.getY )
		  	arrow.render( g, camera )
		}
	}
	
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val edge = element.asInstanceOf[GraphicEdge]

		shape.endPoints( edge.from.getStyle, edge.to.getStyle, edge.isDirected, camera )
		shape.position( edge.from.getX, edge.from.getY, edge.to.getX, edge.to.getY, edge.multi, edge.getGroup )
		shape.setEdgeCtrlPoints( edge )// XXX HORROR, TERROR XXX
		shape.renderShadow( g, camera )
  
		if( edge.isDirected ) {
		  	arrow.connector( edge )
			arrow.direction( shape )
			arrow.position( edge.to.getX, edge.to.getY )
			arrow.renderShadow( g, camera )
		}
	}
 
	protected def elementInvisible( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	  
	}

 	protected def chooseShape():ConnectorShape = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape._
		group.getShape match {
			case LINE        => new LineShape
		  	case ANGLE       => Console.err.printf( "** Sorry angle edge shape is not yet implemented **%n" ); new LineShape
		  	case CUBIC_CURVE => Console.err.printf( "** Sorry cubic edge shape is not yet implemented **%n" ); new LineShape
    		case POLYLINE    => Console.err.printf( "** Sorry poly edge shape is not yet implemented **%n" );  new LineShape
    		case BLOB        => new BlobShape
		    case x           => throw new RuntimeException( "%s shape cannot be set for edges".format( x.toString ) )
		}
	}
  
 	protected def chooseArrowShape():AreaOnConnectorShape = {
 		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.ArrowShape._
		group.getArrowShape match {
			case NONE    => null
			case ARROW   => new ArrowOnEdge
			case CIRCLE  => Console.err.printf( "** Sorry circle arrow not yet implemented **" );  new ArrowOnEdge
			case DIAMOND => Console.err.printf( "** Sorry diamond arrow not yet implemented **" ); new ArrowOnEdge
			case IMAGE   => Console.err.printf( "** Sorry image arrow not yet implemented **" );   new ArrowOnEdge
		    case x       => throw new RuntimeException( "%s shape cannot be set for edge arrows".format( x.toString ) )
		}
 	}
}

object EdgeRenderer {
	def apply( style:StyleGroup, mainRenderer:J2DGraphRenderer ):StyleRenderer = {
		new EdgeRenderer( style )
	}
}