package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Image, Color, Graphics2D}
import java.awt.geom.{Ellipse2D, Line2D, Path2D, CubicCurve2D, Rectangle2D, RoundRectangle2D, RectangularShape}
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.j2dviewer.util.{GraphMetrics, Camera}
import org.graphstream.ui.j2dviewer.geom.Vector2

class BlobShape extends AreaConnectorShape {
	protected var theShape = new Path2D.Float
 
// Command
 
 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
 	  	configureFillable( style, camera, element )
 	  	configureShadowable( style, camera )
 	  	configureStrokable( style, camera )
 	  	configureDecorable( style, camera )
 	}
  
	protected def make( camera:Camera ) {
		if( isCurve )
		     makeOnCurve( camera )
		else makeOnLine( camera )
	}
 
	protected def makeOnLine( camera:Camera ) {
		var dir   = new Vector2( to.x - from.x, to.y - from.y )
		val perp1 = new Vector2( dir.y, -dir.x ); perp1.normalize	// 1/2 perp vector to the from point.
		val perp2 = new Vector2( perp1.x, perp1.y )					// 1/2 perp vector to the to point.
		val perpm = new Vector2( perp1.x, perp1.y )					// 1/2 perp vector to the middle point on the edge.
   
		perp1.scalarMult( theSourceSize/2f )
		perpm.scalarMult( theSize/2f )
   
		if( isDirected )
		     perp2.scalarMult( theSize/2f )
		else perp2.scalarMult( theTargetSize/2f )
		
		val t1 = 5f
		val t2 = 2.3f
		val m  = 1f
		theShape.reset
		theShape.moveTo( from.x + perp1.x, from.y + perp1.y )
		theShape.quadTo( from.x + dir.x/t1 + perpm.x*m, from.y + dir.y/t1 + perpm.y*m,
		                 from.x + dir.x/t2 + perpm.x,   from.y + dir.y/t2 + perpm.y )
		theShape.lineTo( to.x - dir.x/t2 + perpm.x, to.y - dir.y/t2 + perpm.y )
		theShape.quadTo( to.x - dir.x/t1 + perpm.x*m, to.y - dir.y/t1 + perpm.y*m,
		                 to.x + perp2.x, to.y + perp2.y )
		theShape.lineTo( to.x - perp2.x, to.y - perp2.y )
		theShape.quadTo( to.x - dir.x/t1 - perpm.x*m, to.y - dir.y/t1 - perpm.y*m,
		                 to.x - dir.x/t2 - perpm.x,   to.y - dir.y/t2 - perpm.y )
		theShape.lineTo( from.x + dir.x/t2 - perpm.x, from.y + dir.y/t2 - perpm.y )
		theShape.quadTo( from.x + dir.x/t1 - perpm.x*m, from.y + dir.y/t1 - perpm.y*m,
		                 from.x - perp1.x, from.y - perp1.y )
		theShape.closePath
	}
 
	protected def makeOnCurve( camera:Camera ) {
		if( isLoop )
		     makeLoop( camera )
		else makeMulti( camera )
	}
	
	protected def makeMulti( camera:Camera ) {
		var maindir = new Vector2( ctrl2.x - ctrl1.x, ctrl2.y - ctrl1.y )
		val perp1   = new Vector2( maindir.y, -maindir.x ); perp1.normalize	// 1/2 perp vector to the from point.
		val perp2   = new Vector2( perp1.x, perp1.y )						// 1/2 perp vector to the to point.
		val perpm   = new Vector2( perp1.x, perp1.y )						// 1/2 perp vector to the middle point on the edge.
		  
        val t = 5f
                                                       
        perp1.scalarMult( theSourceSize/2f )
        perpm.scalarMult( theSize/2f )
                 
        //   ctrl1           ctrl2
        //     x---t-------t---x
        //    /                 \
        //   /                   \
        //  X                     X
        // from                  to
            
		if( isDirected )
		     perp2.scalarMult( theSize/2f )	
		else perp2.scalarMult( theTargetSize/2f )
		  
        theShape.reset
        theShape.moveTo( from.x + perp1.x, from.y + perp1.y )
        
        theShape.quadTo( ctrl1.x + perpm.x, ctrl1.y + perpm.y,
                         ctrl1.x + maindir.x/t + perpm.x, ctrl1.y + maindir.y/t + perpm.y )
        theShape.lineTo( ctrl2.x - maindir.x/t + perpm.x, ctrl2.y - maindir.y/t + perpm.y )
        theShape.quadTo( ctrl2.x + perpm.x, ctrl2.y + perpm.y, to.x + perp2.x, to.y + perp2.y )
        
        theShape.lineTo( to.x - perp2.x, to.y - perp2.y )
        
        theShape.quadTo( ctrl2.x - perpm.x, ctrl2.y - perpm.y,
                         ctrl2.x - maindir.x/t - perpm.x, ctrl2.y - maindir.y/t - perpm.y )
        theShape.lineTo( ctrl1.x + maindir.x/t - perpm.x, ctrl1.y + maindir.y/t - perpm.y )
        theShape.quadTo( ctrl1.x - perpm.x, ctrl1.y - perpm.y, from.x - perp1.x, from.y - perp1.y )
        
        theShape.closePath
	}

	protected def makeLoop( camera:Camera ) {
	  	var dirFrom  = new Vector2( ctrl1.x - from.x, ctrl1.y - from.y )
		var dirTo    = new Vector2( ctrl2.x - to.x,   ctrl2.y - to.y )
	  	var mainDir  = new Vector2( ctrl2.x - ctrl1.x, ctrl2.y - ctrl1.y )
		var perpFrom = new Vector2( dirFrom.y, -dirFrom.x ); perpFrom.normalize
		var perpTo   = new Vector2( dirTo.y,   -dirTo.y );   perpTo.normalize
		var perpm    = new Vector2( mainDir.y, -mainDir.y ); perpm.normalize
			
		perpFrom.scalarMult( theSourceSize/4 )
		perpm.scalarMult( theSize/2 )
   
		val t = 5f
   
		if( isDirected )
		     perpTo.scalarMult( theSize/2 )
		else perpTo.scalarMult( theTargetSize/4 )
			
		theShape.reset
		theShape.moveTo( from.x - perpFrom.x, from.y - perpFrom.y )
		theShape.curveTo( ctrl1.x + perpm.x, ctrl1.y + perpm.y,
	                      ctrl2.x + perpm.x, ctrl2.y + perpm.y,
	                      to.x + perpTo.x, to.y + perpTo.y )
		theShape.lineTo( to.x - perpTo.x, to.y - perpTo.y )
		theShape.curveTo( ctrl2.x - perpm.x, ctrl2.y - perpm.y,
		                  ctrl1.x - perpm.x, ctrl1.y - perpm.y,
		                  from.x + perpFrom.x, from.y + perpFrom.y )
		theShape.closePath
		
	}

	protected def makeShadow( camera:Camera ) {
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement ) {
// 		makeShadow( camera )
// 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement ) {
 		make( camera )
 		stroke( g, theShape )
 		fill( g, theSize, theShape )
 		decor( g, camera, element, theShape )
// 		showCtrlPoints( g, camera )
	}
 
	protected def showCtrlPoints( g:Graphics2D, camera:Camera ) {
	   	val oval   = new Ellipse2D.Float
 		val color  = g.getColor
	 	val stroke = g.getStroke
	 	val px6    = camera.metrics.px1*6;
	   	val px3    = camera.metrics.px1*3
   
 		g.setColor( Color.RED )
 		oval.setFrame( from.x-px3, from.y-px3, px6, px6 )
 		g.fill( oval )

 		if( ctrl1 != null ) {
 			oval.setFrame( ctrl1.x-px3, ctrl1.y-px3, px6, px6 )
	 		g.fill( oval )
	 		oval.setFrame( ctrl2.x-px3, ctrl2.y-px3, px6, px6 )
	 		g.fill( oval )
	 		val line = new Line2D.Float
	 		line.setLine( ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y )
	 		g.setStroke( new java.awt.BasicStroke( camera.metrics.px1 ) )
	 		g.draw( line )
 		}

 		oval.setFrame( to.x-px3, to.y-px3, px6, px6 )
 		g.fill( oval )
 		g.setColor( color )
	 	g.setStroke( stroke )
	}
}