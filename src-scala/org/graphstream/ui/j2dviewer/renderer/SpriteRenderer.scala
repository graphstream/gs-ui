package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D
import java.awt.geom.Point2D
import org.graphstream.ui.graphicGraph.{GraphicElement, GraphicSprite, StyleGroup}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.j2dviewer.{J2DGraphRenderer, Camera}
import org.graphstream.ui.j2dviewer.renderer.shape._

class SpriteRenderer( styleGroup:StyleGroup ) extends StyleRenderer( styleGroup ) {
	protected var shape:Shape = null
	
	protected def setupRenderingPass( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
		shape = chooseShape( shape )
	}
	
	protected def pushStyle( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
		shape.configureForGroup( g, group, camera )
	}
	
	protected def pushDynStyle( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	}
	
	protected def renderElement( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val sprite = element.asInstanceOf[GraphicSprite]
		val info   = getOrSetSpriteInfo( element )
		
		shape.configureForElement( g, element, info, camera )
		shape.render( g, camera, element, info )
	}
	
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val sprite = element.asInstanceOf[GraphicSprite]
		val pos    = camera.getSpritePosition( sprite, new Point2D.Float, StyleConstants.Units.GU )
		val info   = getOrSetSpriteInfo( element )

		shape.configureForElement( g, element, info, camera )
		shape.renderShadow( g, camera, element, info )
	}
 
	protected def elementInvisible( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	}
 
	/** Retrieve the node shared informations stored on the given node element.
	 * If such information is not yet present, add it to the element. 
	 * @param element The element to look for.
	 * @return The node information.
	 * @throws RuntimeException if the element is not a node.
	 */
	protected def getOrSetSpriteInfo( element:GraphicElement ):NodeInfo= {
		if( element.isInstanceOf[GraphicSprite] ) {
			var info = element.getAttribute( ElementInfo.attributeName ).asInstanceOf[NodeInfo]
			
			if( info eq null ) {
				info = new NodeInfo
				element.setAttribute( ElementInfo.attributeName, info )
			}
			
			info
		} else {
			throw new RuntimeException( "Trying to get NodeInfo on non-node ..." )
		}
	}

	protected def chooseShape( oldShape:Shape ):Shape = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape._
		group.getShape match {
			case CIRCLE         => if( oldShape.isInstanceOf[CircleShape] )           oldShape else new CircleShape 
		  	case BOX            => if( oldShape.isInstanceOf[OrientableSquareShape] ) oldShape else new OrientableSquareShape
		  	case ROUNDED_BOX    => if( oldShape.isInstanceOf[RoundedSquareShape] )    oldShape else new RoundedSquareShape
		  	case DIAMOND        => if( oldShape.isInstanceOf[DiamondShape] )          oldShape else new DiamondShape
		    case TRIANGLE       => if( oldShape.isInstanceOf[TriangleShape] )         oldShape else new TriangleShape
		    case CROSS          => if( oldShape.isInstanceOf[CrossShape] )            oldShape else new CrossShape
		    case ARROW          => if( oldShape.isInstanceOf[SpriteArrowShape] )      oldShape else new SpriteArrowShape
		    case FLOW           => if( oldShape.isInstanceOf[SpriteFlowShape] )       oldShape else new SpriteFlowShape
		  	// ------------------------------------------
		  	case POLYGON        => Console.err.printf( "** SORRY polygon shape not yet implemented **%n" );      new CircleShape
		    case TEXT_BOX       => Console.err.printf( "** SORRY text-box shape not yet implemented **%n" );     new SquareShape
		    case TEXT_PARAGRAPH => Console.err.printf( "** SORRY text-para shape not yet implemented **%n" );    new SquareShape
		    case TEXT_CIRCLE    => Console.err.printf( "** SORRY text-circle shape not yet implemented **%n" );  new CircleShape
		    case TEXT_DIAMOND   => Console.err.printf( "** SORRY text-diamond shape not yet implemented **%n" ); new CircleShape
		    case PIE_CHART      => if( oldShape.isInstanceOf[SpritePieChartShape] ) oldShape else new SpritePieChartShape
		    case IMAGES         => Console.err.printf( "** SORRY images shape not yet implemented **%n" );       new SquareShape 
		    case JCOMPONENT     => throw new RuntimeException( "WTF, jcomponent should have its own renderer" )
		    case x              => throw new RuntimeException( "%s shape cannot be set for sprites".format( x.toString ) )
		}
	}
}

object SpriteRenderer {
	def apply( style:StyleGroup, mainRenderer:J2DGraphRenderer ):StyleRenderer = {
		if( style.getShape == org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape.JCOMPONENT )
		     new JComponentRenderer( style, mainRenderer )
		else new SpriteRenderer( style )
	}
}