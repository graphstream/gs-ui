/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph.{GraphicElement, GraphicNode, StyleGroup}
import org.graphstream.ui.j2dviewer.{Camera, J2DGraphRenderer}
import org.graphstream.ui.j2dviewer.renderer.shape._
import org.graphstream.ui.sgeom.EdgePoints

class NodeRenderer( styleGroup:StyleGroup ) extends StyleRenderer( styleGroup ) {
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
		val info = getOrSetNodeInfo( element )
		shape.configureForElement( g, element, info, camera )
		shape.render( g, camera, element, info )
	}
	
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val info = getOrSetNodeInfo( element )
		shape.configureForElement( g, element, info, camera )
		shape.renderShadow( g, camera, element, info )
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
			case CIRCLE         => if( oldShape.isInstanceOf[CircleShape] )         oldShape else new CircleShape 
		  	case BOX            => if( oldShape.isInstanceOf[SquareShape] )         oldShape else new SquareShape
		  	case ROUNDED_BOX    => if( oldShape.isInstanceOf[RoundedSquareShape] )  oldShape else new RoundedSquareShape
		  	case DIAMOND        => if( oldShape.isInstanceOf[DiamondShape] )        oldShape else new DiamondShape
		    case TRIANGLE       => if( oldShape.isInstanceOf[TriangleShape] )       oldShape else new TriangleShape
		    case CROSS          => if( oldShape.isInstanceOf[CrossShape] )          oldShape else new CrossShape
		    case FREEPLANE      => if( oldShape.isInstanceOf[FreePlaneNodeShape] )  oldShape else new FreePlaneNodeShape
		    case PIE_CHART      => if( oldShape.isInstanceOf[PieChartShape] )       oldShape else new PieChartShape
		  	case POLYGON        => if( oldShape.isInstanceOf[PolygonShape] )        oldShape else new PolygonShape
		  	// ------------------------------------------
		    case TEXT_BOX       => Console.err.printf( "** SORRY text-box shape not yet implemented **%n" );     new SquareShape
		    case TEXT_PARAGRAPH => Console.err.printf( "** SORRY text-para shape not yet implemented **%n" );    new SquareShape
		    case TEXT_CIRCLE    => Console.err.printf( "** SORRY text-circle shape not yet implemented **%n" );  new CircleShape
		    case TEXT_DIAMOND   => Console.err.printf( "** SORRY text-diamond shape not yet implemented **%n" ); new CircleShape
		    case ARROW          => Console.err.printf( "** SORRY arrow shape not yet implemented **%n" );        new CircleShape
		    case IMAGES         => Console.err.printf( "** SORRY images shape not yet implemented **%n" );       new SquareShape 
		  	// ------------------------------------------
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

object ElementInfo {
	def attributeName = "j2dvi"
}

/** Elements of rendering that, contrary to the shapes, are specific to the element, not the style group. */
class ElementInfo {
	var iconAndText:IconAndText = null
}

/** Specific element info for nodes. */
class NodeInfo extends ElementInfo {
	var theSize = new Point2( 0, 0 )
}

/** Element information specific to the edges.
 *  Data stored on the edge to retrieve the edge points and various shared data between parts of the renderer. */
class EdgeInfo extends ElementInfo {
	val points = new EdgePoints( 4 )
	var isCurve = false
	var isMulti = 1
	var isLoop = false
}