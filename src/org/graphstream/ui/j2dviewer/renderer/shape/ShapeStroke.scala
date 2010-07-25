package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Stroke, BasicStroke, Color}
import org.graphstream.ui.graphicGraph.stylesheet.Style

abstract class ShapeStroke {
	def stroke( width:Float ):Stroke
}

object ShapeStroke {
	def strokeForArea( style:Style ):ShapeStroke = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode._
		style.getStrokeMode match {
			case PLAIN  => new PlainShapeStroke
			case DOTS   => new PlainShapeStroke //DotsShapeStroke
			case DASHES => new PlainShapeStroke //DashesShapeStroke
			case _      => null
		}
	}
 
	def strokeForConnectorFill( style:Style ):ShapeStroke = {
		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode._
		style.getFillMode match {
			case PLAIN     => new PlainShapeStroke
			case DYN_PLAIN => new PlainShapeStroke
			case _         => new PlainShapeStroke
		}
	}
 
	def strokeForConnectorStroke( style:Style ):ShapeStroke = {
		strokeForArea( style )
	}
 
	def strokeColor( style:Style ):Color = {
		if( style.getStrokeMode != org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode.NONE ) {
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
				if( oldStroke == null ) oldStroke = new BasicStroke( width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL )	// WTF ??
				oldStroke
			} else {
				oldWidth  = width
				oldStroke = new BasicStroke( width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL )
				oldStroke
			}
		}
	}
}