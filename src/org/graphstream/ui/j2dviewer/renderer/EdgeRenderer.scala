package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D
import org.graphstream.ui.graphicGraph.{GraphicElement, StyleGroup, GraphicEdge}
import org.graphstream.ui.j2dviewer.J2DGraphRenderer
import org.graphstream.ui.j2dviewer.Camera
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
		
		if( arrow != null ) {
			arrow.configure( g, group, camera, null )
			arrow.sizeForEdgeArrow( group, camera )
		}
	}
	
	protected def pushDynStyle( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	  	val size = group.getSize
		shape.configure( g, group, camera, element )
		shape.dynSize( group, camera, element )
		
		if( arrow != null ) {
			arrow.configure( g, group, camera, element )
			arrow.sizeForEdgeArrow( group, camera )
		}
	}
	
	protected def renderElement( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val edge = element.asInstanceOf[GraphicEdge]
		val info = getOrSetEdgeInfo( element )
		
		shape.text = element.label
		
		shape.setupContents( g, camera, element, info )
		shape.endPoints( edge.from, edge.to, edge.isDirected, camera )
//		shape.endPoints( edge.from.getStyle, edge.to.getStyle, edge.isDirected, camera )
		shape.position( info, edge.from.getStyle, edge.from.getX, edge.from.getY, edge.to.getX, edge.to.getY, edge.multi, edge.getGroup )
		shape.render( g, camera, element, info )
  
		if( edge.isDirected && arrow != null ) {
		  	arrow.connector( edge )
		  	arrow.direction( shape )
		  	arrow.positionAndFit( g, camera, null, edge.to, edge.to.getX, edge.to.getY )
		  	arrow.render( g, camera, element, info )
		}
	}
	
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val edge = element.asInstanceOf[GraphicEdge]
		val info = getOrSetEdgeInfo( element )
		
		shape.setupContents( g, camera, element, info )
		shape.endPoints( edge.from, edge.to, edge.isDirected, camera )
//		shape.endPoints( edge.from.getStyle, edge.to.getStyle, edge.isDirected, camera )
		shape.position( info, edge.from.getStyle, edge.from.getX, edge.from.getY, edge.to.getX, edge.to.getY, edge.multi, edge.getGroup )
		shape.renderShadow( g, camera, element, info )
  
		if( edge.isDirected && arrow != null ) {
		  	arrow.connector( edge )
			arrow.direction( shape )
			arrow.positionAndFit( g, camera, null, edge.to, edge.to.getX, edge.to.getY )
			arrow.renderShadow( g, camera, element, info )
		}
	}
	
	/** Retrieve the shared edge informations stored on the given edge element.
	 * If such information is not yet present, add it to the element. 
	 * @param element The element to look for.
	 * @return The edge information.
	 * @throws RuntimeException if the element is not an edge.
	 */
	protected def getOrSetEdgeInfo( element:GraphicElement ):EdgeInfo= {
		if( element.isInstanceOf[GraphicEdge] ) {
			var info = element.getAttribute( ElementInfo.attributeName ).asInstanceOf[EdgeInfo]
			
			if( info eq null ) {
				info = new EdgeInfo
				element.setAttribute( ElementInfo.attributeName, info )
			}
			
			info
		} else {
			throw new RuntimeException( "Trying to get EdgeInfo on non-edge..." )
		}
	}
 
	protected def elementInvisible( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	  
	}

 	protected def chooseShape():ConnectorShape = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape._
		group.getShape match {
			case LINE        => new LineShape
		  	case ANGLE       => new AngleShape
    		case BLOB        => new BlobShape
		  	case CUBIC_CURVE => new CubicCurveShape
		  	case FREEPLANE   => new FreePlaneEdgeShape
    		case POLYLINE    => Console.err.printf( "** Sorry poly edge shape is not yet implemented **%n" );  new LineShape
		    case x           => throw new RuntimeException( "%s shape cannot be set for edges".format( x.toString ) )
		}
	}
  
 	protected def chooseArrowShape():AreaOnConnectorShape = {
 		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.ArrowShape._
		group.getArrowShape match {
			case NONE    => null
			case ARROW   => new ArrowOnEdge
			case CIRCLE  => new CircleOnEdge
			case DIAMOND => new DiamondOnEdge
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