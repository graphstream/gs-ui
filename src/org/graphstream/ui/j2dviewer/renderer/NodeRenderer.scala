package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph.{GraphicElement, GraphicNode, StyleGroup}
import org.graphstream.ui.j2dviewer.{Camera, J2DGraphRenderer}
import org.graphstream.ui.j2dviewer.renderer.shape._

class NodeRenderer( styleGroup:StyleGroup ) extends StyleRenderer( styleGroup ) {
	protected var shape:AreaShape = null
 
	protected def setupRenderingPass( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
		shape = chooseShape
	}
	
	protected def pushStyle( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
		val size = group.getSize
		shape.configure( g, group, camera, null )
		shape.size( group, camera )
	}
	
	protected def pushDynStyle( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val size = group.getSize
		shape.configure( g, group, camera, element )
		shape.dynSize( group, camera, element )
	}
	
	protected def renderElement( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		shape.text = element.label
		val info = getOrSetNodeInfo( element )
		shape.position( info, element.getX, element.getY )
		shape.render( g, camera, element )
	}
	
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val info = getOrSetNodeInfo( element )
		shape.position( info, element.getX, element.getY )
		shape.renderShadow( g, camera, element )
	}
 
	protected def elementInvisible( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		// NOP
	}	
		
	/** Retrieve the node shared informations stored on the given node element.
	 * If such information is not yet present, add it to the element. 
	 * @param element The element to look for.
	 * @return The node information.
	 * @throws RuntimeException if the element is not a node.
	 */
	protected def getOrSetNodeInfo( element:GraphicElement ):NodeInfo= {
		if( element.isInstanceOf[GraphicNode] ) {
			var info = element.getAttribute( "j2dvni" ).asInstanceOf[NodeInfo]
			
			if( info eq null ) {
				info = new NodeInfo
				element.setAttribute( "j2dvni", info )
			}
			
			info
		} else {
			throw new RuntimeException( "Trying to get NodeInfo on non-node ..." )
		}
	}
 
	protected def chooseShape():AreaShape = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape._
		group.getShape match {
			case CIRCLE         => new CircleShape 
		  	case BOX            => new SquareShape
		  	case ROUNDED_BOX    => new RoundedSquareShape
		  	case DIAMOND        => new DiamondShape
		    case TRIANGLE       => new TriangleShape
		    case CROSS          => new CrossShape
		    case FREEPLANE      => new FreePlaneNodeShape
		  	// ------------------------------------------
		  	case POLYGON        => Console.err.printf( "** SORRY polygon shape not yet implemented **%n" );      new CircleShape
		    case TEXT_BOX       => Console.err.printf( "** SORRY text-box shape not yet implemented **%n" );     new SquareShape
		    case TEXT_PARAGRAPH => Console.err.printf( "** SORRY text-para shape not yet implemented **%n" );    new SquareShape
		    case TEXT_CIRCLE    => Console.err.printf( "** SORRY text-circle shape not yet implemented **%n" );  new CircleShape
		    case TEXT_DIAMOND   => Console.err.printf( "** SORRY text-diamond shape not yet implemented **%n" ); new CircleShape
		    case PIE_CHART      => Console.err.printf( "** SORRY pie-chart shape not yet implemented **%n" );    new CircleShape
		    case ARROW          => Console.err.printf( "** SORRY arrow shape not yet implemented **%n" );        new CircleShape
		    case IMAGES         => Console.err.printf( "** SORRY images shape not yet implemented **%n" );       new SquareShape 
		    case JCOMPONENT     => throw new RuntimeException( "WTF, jcomponent should have its own renderer" )
		    case x              => throw new RuntimeException( "%s shape cannot be set for nodes".format( x.toString ) )
		}
	}
}

object NodeRenderer {
	def apply( style:StyleGroup, mainRenderer:J2DGraphRenderer ):StyleRenderer = {
		if( style.getShape == org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape.JCOMPONENT )
		     new JComponentRenderer( style, mainRenderer )
		else new NodeRenderer( style )
	}
}

class NodeInfo {
	var theSize = new Point2( 0, 0 )
}