package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Stroke, BasicStroke, Color}
import org.graphstream.ui2.graphicGraph.stylesheet.Style

abstract class ShapeStroke {
	def stroke( width:Float ):Stroke
}

object ShapeStroke {
	def strokeForArea( style:Style ):ShapeStroke = {
		import org.graphstream.ui2.graphicGraph.stylesheet.StyleConstants.StrokeMode._
		style.getStrokeMode match {
			case PLAIN  => new PlainShapeStroke
			case DOTS   => new PlainShapeStroke //DotsShapeStroke
			case DASHES => new PlainShapeStroke //DashesShapeStroke
			case _      => null
		}
	}
 
	def strokeForConnectorFill( style:Style ):ShapeStroke = {
		throw new RuntimeException( "TODO" )
	}
 
	def strokeForConnectorStroke( style:Style ):ShapeStroke = {
		strokeForArea( style )
	}
 
	def strokeColor( style:Style ):Color = {
		if( style.getStrokeMode != org.graphstream.ui2.graphicGraph.stylesheet.StyleConstants.StrokeMode.NONE ) {
			style.getStrokeColor( 0 )
		} else {
			null
		}
	}
 
	class PlainShapeStroke extends ShapeStroke {
		private[this] var oldWidth = 0f
		private[this] var oldStroke:Stroke = null
		
		def stroke( width:Float ):Stroke = {
			if( width == oldWidth ) {
				oldStroke
			} else {
				oldWidth  = width;
				oldStroke = new BasicStroke( width )
				oldStroke
			}
		}
	}
}