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
import org.graphstream.ui.graphicGraph.{GraphicElement, StyleGroup, GraphicEdge}
import org.graphstream.ui.j2dviewer.J2DGraphRenderer
import org.graphstream.ui.j2dviewer.Camera
import org.graphstream.ui.j2dviewer.renderer.shape._

class EdgeRenderer( styleGroup:StyleGroup ) extends StyleRenderer( styleGroup ) {
	var shape:Shape = null
	var arrow:AreaOnConnectorShape = null
  
	protected def setupRenderingPass( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
		shape = chooseShape( shape )
		arrow = chooseArrowShape( arrow )
	}
	
	protected def pushStyle( g:Graphics2D, camera:Camera, forShadow:Boolean ) {
	  	shape.configureForGroup( g, group, camera )
		
		if( arrow != null ) {
			arrow.configureForGroup( g, group, camera )
		}
	}
	
	protected def pushDynStyle( g:Graphics2D, camera:Camera, element:GraphicElement ) {
	}
	
	protected def renderElement( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val edge = element.asInstanceOf[GraphicEdge]
		val info = getOrSetEdgeInfo( element )
		
		shape.configureForElement( g, element, info, camera )
		shape.render( g, camera, element, info )
  
		if( edge.isDirected && arrow != null ) {
			arrow.theConnectorYoureAttachedTo( shape.asInstanceOf[Connector] /* !!!! Test this TODO ensure this !!! */ )
			arrow.configureForElement( g, element, info, camera )
		  	arrow.render( g, camera, element, info )
		}
	}
	
	protected def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
		val edge = element.asInstanceOf[GraphicEdge]
		val info = getOrSetEdgeInfo( element )
		
		shape.configureForElement( g, element, info, camera )
		shape.renderShadow( g, camera, element, info )
  
		if( edge.isDirected && arrow != null ) {
			arrow.theConnectorYoureAttachedTo( shape.asInstanceOf[Connector] /* !!!! Test this TODO ensure this !!! */ )
			arrow.configureForElement( g, element, info, camera )
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

 	protected def chooseShape( oldShape:Shape ):Shape = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape._
		group.getShape match {
			case LINE        => if( oldShape.isInstanceOf[LineShape] )          oldShape else new LineShape
		  	case ANGLE       => if( oldShape.isInstanceOf[AngleShape] )         oldShape else new AngleShape
    		case BLOB        => if( oldShape.isInstanceOf[BlobShape] )          oldShape else new BlobShape
		  	case CUBIC_CURVE => if( oldShape.isInstanceOf[CubicCurveShape] )    oldShape else new CubicCurveShape
		  	case FREEPLANE   => if( oldShape.isInstanceOf[FreePlaneEdgeShape] ) oldShape else new FreePlaneEdgeShape
    		case POLYLINE    => Console.err.printf( "** Sorry poly edge shape is not yet implemented **%n" );  new LineShape
		    case x           => throw new RuntimeException( "%s shape cannot be set for edges".format( x.toString ) )
		}
	}
  
 	protected def chooseArrowShape( oldArrow:AreaOnConnectorShape ):AreaOnConnectorShape = {
 		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.ArrowShape._
		group.getArrowShape match {
			case NONE    => null
			case ARROW   => if( oldArrow.isInstanceOf[ArrowOnEdge] )   oldArrow else new ArrowOnEdge
			case CIRCLE  => if( oldArrow.isInstanceOf[CircleOnEdge] )  oldArrow else new CircleOnEdge
			case DIAMOND => if( oldArrow.isInstanceOf[DiamondOnEdge] ) oldArrow else new DiamondOnEdge
			case IMAGE   => if( oldArrow.isInstanceOf[ImageOnEdge] )   oldArrow else new ImageOnEdge
		    case x       => throw new RuntimeException( "%s shape cannot be set for edge arrows".format( x.toString ) )
		}
 	}
}

object EdgeRenderer {
	def apply( style:StyleGroup, mainRenderer:J2DGraphRenderer ):StyleRenderer = {
		new EdgeRenderer( style )
	}
}