/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
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

import org.graphstream.ui.geom.Point3
import java.awt.{Graphics, Graphics2D, Font, Color, RenderingHints}
import java.awt.event.{ActionListener, ActionEvent}
import java.awt.geom.{Point2D}
import javax.swing.{JComponent, JPanel, BorderFactory, JTextField, JButton, SwingConstants, ImageIcon}
import javax.swing.border.Border
import org.graphstream.ui.graphicGraph.{GraphicElement, GraphicNode, GraphicSprite, StyleGroup}
import org.graphstream.ui.graphicGraph.stylesheet.{Values, StyleConstants}
import org.graphstream.ui.j2dviewer.{J2DGraphRenderer, Camera, Backend}
import org.graphstream.ui.util.swing.{FontCache, ImageCache}
import org.graphstream.ui.j2dviewer.renderer.shape.swing._

/**
 * Renderer for nodes and sprites represented as Swing components.
 */
class JComponentRenderer(styleGroup:StyleGroup, val mainRenderer:J2DGraphRenderer) extends StyleRenderer(styleGroup) {
// Attribute

	/** The size of components. */
	protected var size:Values = null
	
	/** The size in PX of components. */
	protected var width:Int = 0
	
	/** The size in PX of components. */
 	protected var height:Int = 0
	
	/** Association between Swing components and graph elements. */
	protected val compToElement = new scala.collection.mutable.HashMap[JComponent,ComponentElement]

	/** The potential shadow. */
	protected var shadow:SquareShape = null
 
	protected var antialiasSetting:AnyRef = null

// Command
  
	protected def setupRenderingPass(bck:Backend, camera:Camera, forShadow:Boolean) {
		val metrics = camera.metrics
		val g       = bck.graphics2D

		size   = group.getSize
		width  = metrics.lengthToPx(size, 0).toInt
		height = if(size.size > 1) metrics.lengthToPx(size, 1).toInt else width
  
		if(group.getShadowMode != StyleConstants.ShadowMode.NONE)
		     shadow = new SquareShape
		else shadow = null
		
		antialiasSetting = g.getRenderingHint( RenderingHints.KEY_ANTIALIASING )
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF )
	}
 
	override protected def endRenderingPass(bck:Backend, camera:Camera, forShadow:Boolean) {
		bck.graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasSetting)
	} 
	
	protected def pushStyle(bck:Backend, camera:Camera, forShadow:Boolean) {
		if(shadow ne null) {
			shadow.configureForGroup(bck, group, camera)
//		  	shadow.configure(bck, group, camera, null)
//		  	shadow.size(group, camera)
		}
	}
	
	protected def pushDynStyle(bck:Backend, camera:Camera, element:GraphicElement) {
	}
	
	protected def renderElement(bck:Backend, camera:Camera, element:GraphicElement) {
		val ce = getOrEquipWithJComponent(element)

		ce.setVisible(true)
		ce.updatePosition(camera)
		ce.updateLabel

		if(ce.init == false)
		     checkStyle(camera, ce, true)
		else if(group.hasEventElements)
		     checkStyle(camera, ce, ! hadEvents)	// hadEvents allows to know if we just
		else checkStyle(camera, ce, hadEvents)		// changed the style due to an event	
	}												// and therefore must change the style.
													
	protected def renderShadow(bck:Backend, camera:Camera, element:GraphicElement) {
		if(shadow ne null) {
//			val pos = new Point2D.Double( element.getX, element.getY )
//
//			if( element.isInstanceOf[GraphicSprite] ) {
//				camera.getSpritePosition( element.asInstanceOf[GraphicSprite], pos, StyleConstants.Units.GU )
//			}
//			
////			shadow.setupContents( g, camera, element, null )
//			shadow.positionAndFit( g, camera, null, element, pos.x, pos.y )
			shadow.configureForElement(bck, element, null, camera)
			shadow.renderShadow(bck, camera, element, null)
		}
	}
 
	protected def elementInvisible(bck:Backend, camera:Camera, element:GraphicElement) {
		getOrEquipWithJComponent(element).setVisible(false)
	}
 
// Utility
	
 	def unequipElement(element:GraphicElement) {
		compToElement.get(element.getComponent.asInstanceOf[JComponent]) match {
			case e:ComponentElement => { e.detach }
			case _                  => {}
		}
	}

	/**
	 * Get the pair (swing component, graph element) corresponding to the given element. If the
	 * element is not yet associated with a Swing component, the binding is done.
	 */
	protected def getOrEquipWithJComponent(element:GraphicElement):ComponentElement = {
		import StyleConstants.JComponents._ 

		val component = element.getComponent.asInstanceOf[JComponent]
		var ce:ComponentElement = null
		
		if(component eq null) {
			group.getJComponent match {
				case BUTTON     => ce = new ButtonComponentElement(element, new JButton(""))
				case TEXT_FIELD => ce = new TextFieldComponentElement(element, new JTextField(""))
				case PANEL      => throw new RuntimeException("panel not yet available")
				case _          => throw new RuntimeException("WTF ?!?")
			}
			
			if( ce != null )
				compToElement.put(ce.jComponent, ce)
		} else {
			ce = compToElement.get(component).get
		}
		
		ce
	}

	protected def checkStyle(camera:Camera, ce:ComponentElement, force:Boolean) {
		if(force) {
			ce.checkIcon(camera)
			ce.checkBorder(camera, force)
			ce.setFill
			ce.setTextAlignment
			ce.setTextFont
		}
	}
 
// Nested classes
 
	/**
	 * Represents the link between a JComponent and a GraphicElement.
	 * 
	 * Each of these component elements receive the action events of their button/text-field (for panel
	 * the user is free to do whatever he wants). They are in charge of adding and removing the
	 * component in the rendering surface, etc.
	 * 
	 * These elements also allow to push and remove the style to Swing components. We try to do this
	 * only when the style potentially changed, not at each redraw.
	 */
 	abstract class ComponentElement(val element:GraphicElement) extends JPanel {
 	// Attribute
 	  
		/** Set to true if the element is not yet initialised with its style. */
		var init = false

	// Construction
 	
		setLayout(null)	// No layout in this panel, we set the component bounds ourselves.
 		mainRenderer.renderingSurface.add(this)
  
	// Access

		/** The Swing Component. */
		def jComponent:JComponent
	
		/** Set of reset the fill mode and colour for the Swing component. */
		def setFill() {
//			setBackground( group.getFillColor( 0 ) )
//			setOpaque( true )
//			if( group.getFillMode == StyleConstants.FillMode.PLAIN )
//				jComponent.setBackground( group.getFillColor( 0 ) )
		}
	
		/** Set or reset the text alignment for the Swing component. */
		def setTextAlignment()
		
		/** Set or reset the text font size, style and colour for the Swing component. */
		def setTextFont()
		
		/** Set or reset the label of the component. */
		def updateLabel()
	
		def setBounds(x:Int, y:Int, width:Int, height:Int, camera:Camera) {
			setBounds(x, y, width, height)
			
			var borderWidth:Int = 0
			
			if(group.getStrokeMode != StyleConstants.StrokeMode.NONE && group.getStrokeWidth.value > 0)
				borderWidth = camera.metrics.lengthToPx(group.getStrokeWidth).toInt

			jComponent.setBounds(borderWidth, borderWidth, width-(borderWidth*2), height-(borderWidth*2))
		}
	
		/**
		 * Detach the Swing component from the graph element, remove the Swing component from its
		 * Swing container and remove any listeners on the Swing component. The ComponentElement
		 * is not usable after this.
		 */
		def detach { mainRenderer.renderingSurface.remove(this) }

		/**
		 * Check the swing component follows the graph element position.
		 * @param camera The transformation from GU to PX.
		 */
		def updatePosition(camera:Camera) {
			element match {
				case e:GraphicNode   => positionNodeComponent(  element.asInstanceOf[GraphicNode],   camera)
				case e:GraphicSprite => positionSpriteComponent(element.asInstanceOf[GraphicSprite], camera)
				case _               => throw new RuntimeException("WTF ?")
			}
		}

	// Custom painting
	
		override def paint(g:Graphics) {
			paintComponent(g)	// XXX Remove this ??? XXX
			paintBorder(g)
			paintChildren(g)
		}
		
	// Command -- Utility, positioning
		
		protected def positionNodeComponent(node:GraphicNode, camera:Camera) {
			val pos = camera.transformGuToPx(node.getX, node.getY, 0)
	
			setBounds((pos.x-(width/2)).toInt, (pos.y-(height/2)).toInt, width, height, camera)
		}
		
		protected def positionSpriteComponent( sprite:GraphicSprite, camera:Camera ) {
			val pos = camera.getSpritePosition( sprite, new Point3, StyleConstants.Units.PX)
	
			setBounds((pos.x-(width/2)).toInt, (pos.y-(height/2)).toInt, width, height, camera)
		}

	// Command -- Utility, applying CSS style to Swing components
		
		def checkBorder(camera:Camera, force:Boolean) {
			if(force) {
				if(group.getStrokeMode != StyleConstants.StrokeMode.NONE && group.getStrokeWidth().value > 0)
			         setBorder(createBorder(camera))
				else setBorder(null)
			} else {
				updateBorder(camera)
			}
		}
		
		protected def createBorder( camera:Camera ):Border = {
			import StyleConstants.StrokeMode._

			val width:Int = camera.metrics.lengthToPx( group.getStrokeWidth ).toInt
			
			group.getStrokeMode match {
				case PLAIN  => BorderFactory.createLineBorder( group.getStrokeColor( 0 ), width )
				case DOTS   => throw new RuntimeException( "TODO create dots and dashes borders for component to respect stroke-mode." );
				case DASHES => throw new RuntimeException( "TODO create dots and dashes borders for component to respect stroke-mode." );
				case _      => null
			}
		}
		
		protected def updateBorder( camera:Camera ) {}
		
		def checkIcon( camera:Camera )
	}
  
    class TextFieldComponentElement( element:GraphicElement, val comp:JTextField ) extends ComponentElement( element ) with ActionListener {
	// Construction

		element.setComponent( comp )
		comp.addActionListener( this )
		add( comp )

	// Command
  
		override def detach() {
			super.detach
			comp.removeActionListener( this )
			remove( comp )
			element.setComponent( null )
	
			//component = null
			//element   = null
		}
	
		def actionPerformed( e:ActionEvent ) {
			element.label = comp.asInstanceOf[JTextField].getText
			element.setAttribute( "ui.label", element.label )
			element.setAttribute( "ui.clicked" )
	    }
	
		override def jComponent:JComponent = comp
		
		override def setTextAlignment() {
			import StyleConstants.TextAlignment._
			group.getTextAlignment match {
				case ABOVE    => comp.setHorizontalAlignment( SwingConstants.CENTER )
				case UNDER    => comp.setHorizontalAlignment( SwingConstants.CENTER )
				case ALONG    => comp.setHorizontalAlignment( SwingConstants.CENTER )
				case JUSTIFY  => comp.setHorizontalAlignment( SwingConstants.CENTER )
				case CENTER   => comp.setHorizontalAlignment( SwingConstants.CENTER )
				case AT_RIGHT => comp.setHorizontalAlignment( SwingConstants.RIGHT )
				case RIGHT    => comp.setHorizontalAlignment( SwingConstants.RIGHT )
				case AT_LEFT  => comp.setHorizontalAlignment( SwingConstants.LEFT )
				case LEFT     => comp.setHorizontalAlignment( SwingConstants.LEFT )
				case _        => {}
			}
		}
		
		override def setTextFont() {
			var font = if( ! group.getTextFont.equals( "default" ) )
			                FontCache.getFont( group.getTextFont, group.getTextStyle, group.getTextSize.value.toInt )
			           else FontCache.getDefaultFont( group.getTextStyle, group.getTextSize.value.toInt )
			
			comp.setFont( font )
			comp.setForeground( group.getTextColor( 0 ) )
		}
		
		override def updateLabel() {
			if( ! comp.hasFocus() )
				comp.setText( element.getLabel )
		}
	
		override def checkIcon( camera:Camera ) { /* NOP */ }
	}
    
    class ButtonComponentElement( element:GraphicElement, val comp:JButton ) extends ComponentElement( element ) with ActionListener {
	// Construction
    
		element.setComponent( comp )
		comp.addActionListener( this )
		add( comp)
      
    // Commands
    
		override def detach() {
			super.detach
			comp.removeActionListener( this )
			remove( comp)
			element.setComponent( null )
	
//			component = null;
//			element   = null;
		}
	
		def actionPerformed( e:ActionEvent ) {
			element.label = comp.getText
			element.setAttribute( "ui.label", element.label )
			element.setAttribute( "ui.clicked" )
			element.myGraph.setAttribute( "ui.clicked", element.getId )
	    }
	
		override def jComponent:JComponent = comp
	
		override def setTextAlignment() {
			import StyleConstants.TextAlignment._
			group.getTextAlignment match {
				case ALONG    => comp.setHorizontalAlignment( SwingConstants.CENTER )
				case JUSTIFY  => comp.setHorizontalAlignment( SwingConstants.CENTER )
				case CENTER   => comp.setHorizontalAlignment( SwingConstants.CENTER )
				case AT_RIGHT => comp.setHorizontalAlignment( SwingConstants.RIGHT )
				case RIGHT    => comp.setHorizontalAlignment( SwingConstants.RIGHT )
				case AT_LEFT  => comp.setHorizontalAlignment( SwingConstants.LEFT )
				case LEFT     => comp.setHorizontalAlignment( SwingConstants.LEFT )
				case ABOVE    => comp.setVerticalAlignment( SwingConstants.TOP )
				case UNDER    => comp.setVerticalAlignment( SwingConstants.BOTTOM )
				case _        => {}
			}
		}
	
		override def setTextFont() {
			val font = if( ! group.getTextFont().equals( "default" ) )
			     FontCache.getFont( group.getTextFont, group.getTextStyle, group.getTextSize.value.toInt )
			else FontCache.getDefaultFont( group.getTextStyle, group.getTextSize().value.toInt )
			
			comp.setFont( font )
			comp.setForeground( group.getTextColor( 0 ) )
		}
		
		override def updateLabel() {
			val label = element.getLabel
			
			if( label != null )
				comp.setText( label )
		}
	
		override def checkIcon( camera:Camera ) {
			import StyleConstants.IconMode._
		  
			if( group.getIconMode != StyleConstants.IconMode.NONE ) {
				val url   = group.getIcon
				val image = ImageCache.loadImage( url ).get
				
				if( image != null ) {
					comp.setIcon( new ImageIcon( image ) )
					
					group.getIconMode match {
						case AT_LEFT  => { comp.setHorizontalTextPosition( SwingConstants.RIGHT );  comp.setVerticalTextPosition( SwingConstants.CENTER ) }
						case AT_RIGHT => { comp.setHorizontalTextPosition( SwingConstants.LEFT  );  comp.setVerticalTextPosition( SwingConstants.CENTER ) }
						case ABOVE    => { comp.setHorizontalTextPosition( SwingConstants.CENTER ); comp.setVerticalTextPosition( SwingConstants.BOTTOM ) }
						case UNDER    => { comp.setHorizontalTextPosition( SwingConstants.CENTER ); comp.setVerticalTextPosition( SwingConstants.TOP )    }
						case _        => { throw new RuntimeException( "unknown image mode" ) }
					}
				}
			}
		}
    }
}