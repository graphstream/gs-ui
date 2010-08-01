package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt.{Image, Color, Graphics2D}
import java.awt.geom.{Ellipse2D, Line2D, Path2D, CubicCurve2D, Rectangle2D, RoundRectangle2D, RectangularShape}
import org.graphstream.ui.j2dviewer.renderer.ElementInfo
import org.graphstream.ui.geom.Point2
import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.j2dviewer.Camera
import org.graphstream.ui.util.GraphMetrics
import org.graphstream.ui.sgeom.Vector2
import scala.math._

abstract class AreaConnectorShapeWithCubics extends AreaConnectorShape {
	protected var showControlPolygon = false
	
	protected def showCtrlPoints( g:Graphics2D, camera:Camera ) {
		if( showControlPolygon ) {
			val from   = info.points(0)
			val ctrl1  = info.points(1)
			val ctrl2  = info.points(2)
			val to     = info.points(3)
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
}

abstract class LineConnectorShapeWithCubics extends LineConnectorShape {
	protected var showControlPolygon = false
	
	protected def showCtrlPoints( g:Graphics2D, camera:Camera ) {
		if( showControlPolygon ) {
			val from   = info.points(0)
			val ctrl1  = info.points(1)
			val ctrl2  = info.points(2)
			val to     = info.points(3)
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
		 		line.setLine( from.x, from.y, ctrl1.x, ctrl1.y )
		 		g.draw( line )
		 		line.setLine( ctrl2.x, ctrl2.y, to.x, to.y )
		 		g.draw( line )
	 		}
	
	 		oval.setFrame( to.x-px3, to.y-px3, px6, px6 )
	 		g.fill( oval )
	 		g.setColor( color )
		 	g.setStroke( stroke )
		}
	}
}

/**
 * A blob-like shape.
 */
class BlobShape extends AreaConnectorShapeWithCubics {
	protected var theShape = new Path2D.Float
 
// Command
 
// 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
// 	  	configureFillable( style, camera, element )
// 	  	configureShadowable( style, camera )
// 	  	configureStrokable( style, camera )
// 	  	configureDecorable( style, camera )
// 	}
  
	protected def make( g:Graphics2D, camera:Camera ) {
		make( camera, 0, 0, 0, 0 )
	}
	
	protected def make( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		if( info.isCurve )
		     makeOnCurve( camera, sox, soy, swx, swy )
		else makeOnLine( camera, sox, soy, swx, swy )
	}
 
	protected def makeOnLine( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		val fromx = info.points(0).x + sox
		val fromy = info.points(0).y + soy
		val tox   = info.points(3).x + sox
		val toy   = info.points(3).y + soy
		val dir   = new Vector2( tox - fromx, toy - fromy )
		val perp1 = new Vector2( dir.y, -dir.x ); perp1.normalize	// 1/2 perp vector to the from point.
		val perp2 = new Vector2( perp1.x, perp1.y )					// 1/2 perp vector to the to point.
		val perpm = new Vector2( perp1.x, perp1.y )					// 1/2 perp vector to the middle point on the edge.
		val srcsz = min( theSourceSizeX, theSourceSizeY )
		val trgsz = min( theTargetSizeX, theTargetSizeY )
		
		perp1.scalarMult( (srcsz+swx)/2f )
		perpm.scalarMult( (theSize+swx)/2f )
   
		if( isDirected )
		     perp2.scalarMult( (theSize+swx)/2f )
		else perp2.scalarMult( (trgsz+swx)/2f )
		
		val t1 = 5f
		val t2 = 2.3f
		val m  = 1f
		theShape.reset
		theShape.moveTo( fromx + perp1.x, fromy + perp1.y )
		theShape.quadTo( fromx + dir.x/t1 + perpm.x*m, fromy + dir.y/t1 + perpm.y*m,
		                 fromx + dir.x/t2 + perpm.x,   fromy + dir.y/t2 + perpm.y )
		theShape.lineTo( tox - dir.x/t2 + perpm.x, toy - dir.y/t2 + perpm.y )
		theShape.quadTo( tox - dir.x/t1 + perpm.x*m, toy - dir.y/t1 + perpm.y*m,
		                 tox + perp2.x, toy + perp2.y )
		theShape.lineTo( tox - perp2.x, toy - perp2.y )
		theShape.quadTo( tox - dir.x/t1 - perpm.x*m, toy - dir.y/t1 - perpm.y*m,
		                 tox - dir.x/t2 - perpm.x,   toy - dir.y/t2 - perpm.y )
		theShape.lineTo( fromx + dir.x/t2 - perpm.x, fromy + dir.y/t2 - perpm.y )
		theShape.quadTo( fromx + dir.x/t1 - perpm.x*m, fromy + dir.y/t1 - perpm.y*m,
		                 fromx - perp1.x, fromy - perp1.y )
		theShape.closePath
	}
 
	protected def makeOnCurve( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		if( info.isLoop )
		     makeLoop( camera, sox, soy, swx, swy )
		else makeMulti( camera, sox, soy, swx, swy )
	}
	
	protected def makeMulti( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		val fromx = info.points(0).x + sox
		val fromy = info.points(0).y + soy
		val tox   = info.points(3).x + sox
		val toy   = info.points(3).y + soy
		val c1x   = info.points(1).x + sox
		val c1y   = info.points(1).y + soy
		val c2x   = info.points(2).x + sox
		val c2y   = info.points(2).y + soy
		val srcsz = min( theSourceSizeX, theSourceSizeY )
		val trgsz = min( theTargetSizeX, theTargetSizeY )

		val maindir = new Vector2( c2x - c1x, c2y - c1y )
		val perp1   = new Vector2( maindir.y, -maindir.x ); perp1.normalize	// 1/2 perp vector to the from point.
		val perp2   = new Vector2( perp1.x, perp1.y )						// 1/2 perp vector to the to point.
		val perpm   = new Vector2( perp1.x, perp1.y )						// 1/2 perp vector to the middle point on the edge.
		  
        val t = 5f
                                                       
        perp1.scalarMult( (srcsz+swx)/2f )
        perpm.scalarMult( (theSize+swx)/2f )
                 
        //   ctrl1           ctrl2
        //     x---t-------t---x
        //    /                 \
        //   /                   \
        //  X                     X
        // from                  to
            
		if( isDirected )
		     perp2.scalarMult( (theSize+swx)/2f )	
		else perp2.scalarMult( (trgsz+swx)/2f )
		  
        theShape.reset
        theShape.moveTo( fromx + perp1.x, fromy + perp1.y )
        
        theShape.quadTo( c1x + perpm.x, c1y + perpm.y,
                         c1x + maindir.x/t + perpm.x, c1y + maindir.y/t + perpm.y )
        theShape.lineTo( c2x - maindir.x/t + perpm.x, c2y - maindir.y/t + perpm.y )
        theShape.quadTo( c2x + perpm.x, c2y + perpm.y, tox + perp2.x, toy + perp2.y )
        
        theShape.lineTo( tox - perp2.x, toy - perp2.y )
        
        theShape.quadTo( c2x - perpm.x, c2y - perpm.y,
                         c2x - maindir.x/t - perpm.x, c2y - maindir.y/t - perpm.y )
        theShape.lineTo( c1x + maindir.x/t - perpm.x, c1y + maindir.y/t - perpm.y )
        theShape.quadTo( c1x - perpm.x, c1y - perpm.y, fromx - perp1.x, fromy - perp1.y )
        
        theShape.closePath
	}

	protected def makeLoop( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		val fromx = info.points(0).x + sox
		val fromy = info.points(0).y + soy
		val tox   = info.points(3).x + sox
		val toy   = info.points(3).y + soy
		val c1x   = info.points(1).x + sox
		val c1y   = info.points(1).y + soy
		val c2x   = info.points(2).x + sox
		val c2y   = info.points(2).y + soy
		val srcsz = min( theSourceSizeX, theSourceSizeY )
//		val trgsz = min( theTargetSizeX, theTargetSizeY )
		
		val dirFrom  = new Vector2( c1x - fromx, c1y - fromy );
		val dirTo    = new Vector2( tox - c2x, toy - c2y );
	  	val mainDir  = new Vector2( c2x - c1x, c2y - c1y )
	  	
	  	val perpFrom = new Vector2( dirFrom.y, -dirFrom.x ); perpFrom.normalize
		val mid1     = new Vector2( dirFrom ); mid1.sub( mainDir ); mid1.normalize
		val mid2     = new Vector2( mainDir ); mid2.sub( dirTo );   mid2.normalize
			
		perpFrom.scalarMult( (srcsz+swx)*0.3f )
		
		if( isDirected ) {
			mid1.scalarMult( (theSize+swx)*4f )
			mid2.scalarMult( (theSize+swx)*2f )
		} else {
			mid1.scalarMult( (theSize+swx)*4f )
			mid2.scalarMult( (theSize+swx)*4f )
		}
			
		theShape.reset
		theShape.moveTo( fromx + perpFrom.x, fromy + perpFrom.y )
		if( isDirected ) {
			theShape.curveTo( c1x + mid1.x, c1y + mid1.y, c2x + mid2.x, c2y + mid2.y, tox, toy )
			theShape.curveTo( c2x - mid2.x, c2y - mid2.y, c1x - mid1.x, c1y - mid1.y, fromx - perpFrom.x, fromy - perpFrom.y )
		} else {
			var perpTo = new Vector2( dirTo.y, -dirTo.x ); perpTo.normalize; perpTo.scalarMult( (srcsz+swx)*0.3f )
			theShape.curveTo( c1x + mid1.x, c1y + mid1.y, c2x + mid2.x, c2y + mid2.y, tox + perpTo.x, toy + perpTo.y )
			theShape.lineTo( tox - perpTo.x, toy - perpTo.y )
			theShape.curveTo( c2x - mid2.x, c2y - mid2.y, c1x - mid1.x, c1y - mid1.y, fromx - perpFrom.x, fromy - perpFrom.y )
		}
		theShape.closePath
	}

	protected def makeShadow( g:Graphics2D, camera:Camera ) {
		make( camera, theShadowOff.x, theShadowOff.y, theShadowWidth.x, theShadowWidth.y )
	}
	
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		makeShadow( g, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( g, camera )
 		stroke( g, theShape )
 		fill( g, theSize, theShape, camera )
 		decor( g, camera, info.iconAndText, element, theShape )

 		if( showControlPolygon ) {
	 		val c = g.getColor();
	 		val s = g.getStroke();
	 		g.setStroke( new java.awt.BasicStroke( camera.metrics.px1 ) )
	 		g.setColor( Color.red );
	 		g.draw( theShape );
	 		g.setStroke( s );
	 		g.setColor( c );
	 		showCtrlPoints( g, camera )
 		}
	}
}

/**
 * An angular shape.
 */
class AngleShape extends AreaConnectorShape {
	protected var theShape = new Path2D.Float
 
// Command
 
// 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
// 	  	configureFillable( style, camera, element )
// 	  	configureShadowable( style, camera )
// 	  	configureStrokable( style, camera )
// 	  	configureDecorable( style, camera )
// 	}
	
	protected def make( g:Graphics2D, camera:Camera ) {
		make( camera, 0, 0, 0, 0 )
	}
  
	protected def make( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		if( info.isCurve )
		     makeOnCurve( camera, sox, soy, swx, swy )
		else makeOnLine( camera, sox, soy, swx, swy )
	}
 
	protected def makeOnLine( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		val fromx = info.points(0).x + sox
		val fromy = info.points(0).y + soy
		val tox   = info.points(3).x + sox
		val toy   = info.points(3).y + soy
		val dir   = new Vector2( tox - fromx, toy - fromy )
		val perp  = new Vector2( dir.y, -dir.x ); perp.normalize	// 1/2 perp vector to the from point.
   
		perp.scalarMult( (theSize+swx)/2f )
   
		theShape.reset
		theShape.moveTo( fromx + perp.x, fromy + perp.y )
		if( isDirected ) {
		     theShape.lineTo( tox, toy )
		} else {
			theShape.lineTo( tox + perp.x, toy + perp.y )
			theShape.lineTo( tox - perp.x, toy - perp.y )
		}
		theShape.lineTo( fromx - perp.x, fromy - perp.y )
		theShape.closePath
	}
 
	protected def makeOnCurve( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		if( info.isLoop )
		     makeLoop( camera, sox, soy, swx, swy )
		else makeMulti( camera, sox, soy, swx, swy )
	}
	
	protected def makeMulti( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		val fromx   = info.points(0).x + sox
		val fromy   = info.points(0).y + soy
		val tox     = info.points(3).x + sox
		val toy     = info.points(3).y + soy
		val c1x     = info.points(1).x + sox
		val c1y     = info.points(1).y + soy
		val c2x     = info.points(2).x + sox
		val c2y     = info.points(2).y + soy
		val maindir = new Vector2( c2x - c1x, c2y - c1y )
		val perp    = new Vector2( maindir.y, -maindir.x ); perp.normalize	// 1/2 perp vector to the from point.
		val perp1   = new Vector2( perp.x, perp.y )							// 1/2 perp vector to the first control point.
		val perp2   = new Vector2( perp.x, perp.y )							// 1/2 perp vector to the second control point.

        perp.scalarMult( (theSize+swx) * 0.5f )
        
        if( isDirected ) {
        	perp1.scalarMult( (theSize+swx) * 0.4f )
        	perp2.scalarMult( (theSize+swx) * 0.2f )
        } else {
        	perp1.scalarMult( (theSize+swx) * 0.5f )
        	perp2.scalarMult( (theSize+swx) * 0.5f )
        }
                 
        //   ctrl1           ctrl2
        //     x---t-------t---x
        //    /                 \
        //   /                   \
        //  X                     X
        // from                  to
            
        theShape.reset
        theShape.moveTo( fromx + perp.x, fromy + perp.y )
        if( isDirected ) {
        	theShape.curveTo( c1x + perp1.x, c1y + perp1.y,
                              c2x + perp2.x, c2y + perp2.y,
                              tox, toy )
            theShape.curveTo( c2x - perp2.x, c2y - perp2.y,
                              c1x - perp1.x, c1y - perp1.y,
                              fromx - perp.x,  fromy - perp.y )
        } else {
        	theShape.curveTo( c1x + perp.x, c1y + perp.y,
                              c2x + perp.x, c2y + perp.y,
                              tox + perp.x, toy + perp.y )
            theShape.lineTo(  tox - perp.x, toy - perp.y )
            theShape.curveTo( c2x - perp.x, c2y - perp.y,
                              c1x - perp.x, c1y - perp.y,
                              fromx - perp.x, fromy - perp.y )
        }
        theShape.closePath
	}

	protected def makeLoop( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
	  	val fromx = info.points(0).x + sox
		val fromy = info.points(0).y + soy
		val tox   = info.points(3).x + sox
		val toy   = info.points(3).y + soy
		val c1x   = info.points(1).x + sox
		val c1y   = info.points(1).y + soy
		val c2x   = info.points(2).x + sox
		val c2y   = info.points(2).y + soy

		val dirFrom  = new Vector2( c1x - fromx, c1y - fromy );
		val dirTo    = new Vector2( tox - c2x, toy - c2y );
	  	val mainDir  = new Vector2( c2x - c1x, c2y - c1y )
	  	
	  	val perpFrom = new Vector2( dirFrom.y, -dirFrom.x ); perpFrom.normalize
		val mid1     = new Vector2( dirFrom ); mid1.sub( mainDir ); mid1.normalize
		val mid2     = new Vector2( mainDir ); mid2.sub( dirTo );   mid2.normalize
			
		perpFrom.scalarMult( theSize*0.5f )
		
		if( isDirected ) {
			mid1.scalarMult( theSize*0.8f )
			mid2.scalarMult( theSize*0.6f )
		} else {
			mid1.scalarMult( theSize*0.99f )
			mid2.scalarMult( theSize*0.99f )
		}
			
		theShape.reset
		theShape.moveTo( fromx + perpFrom.x, fromy + perpFrom.y )
		if( isDirected ) {
			theShape.curveTo( c1x + mid1.x, c1y + mid1.y, c2x + mid2.x, c2y + mid2.y, tox, toy )
			theShape.curveTo( c2x - mid2.x, c2y - mid2.y, c1x - mid1.x, c1y - mid1.y, fromx - perpFrom.x, fromy - perpFrom.y )
		} else {
			var perpTo = new Vector2( dirTo.y, -dirTo.x ); perpTo.normalize; perpTo.scalarMult( theSize*0.5f )
			theShape.curveTo( c1x + mid1.x, c1y + mid1.y, c2x + mid2.x, c2y + mid2.y, tox + perpTo.x, toy + perpTo.y )
			theShape.lineTo( tox - perpTo.x, toy - perpTo.y )
			theShape.curveTo( c2x - mid2.x, c2y - mid2.y, c1x - mid1.x, c1y - mid1.y, fromx - perpFrom.x, fromy - perpFrom.y )
		}
		theShape.closePath
	}

	protected def makeShadow( g:Graphics2D, camera:Camera ) {
		if( info.isCurve )
		     makeOnCurve( camera, theShadowOff.x, theShadowOff.y, theShadowWidth.x, theShadowWidth.y )
		else makeOnLine( camera, theShadowOff.x, theShadowOff.y, theShadowWidth.x, theShadowWidth.y )
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		makeShadow( g, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( g, camera )
 		stroke( g, theShape )
 		fill( g, theSize, theShape, camera )
 		decor( g, camera, info.iconAndText, element, theShape )
	}
}

/**
 * A cubic curve shape.
 */
class CubicCurveShape extends LineConnectorShapeWithCubics {
	protected var theShape = new Path2D.Float

// Command
 
// 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
// 	  	configureFillableConnector( style, camera, element )
// 	  	configureShadowableConnector( style, camera )
// 	  	configureStrokableConnector( style, camera )
// 	  	configureDecorable( style, camera )
// 	}
	
	protected def make( g:Graphics2D, camera:Camera ) {
		make( camera, 0, 0, 0, 0 )
	}
  
	protected def make( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		if( info.isMulti > 1 || info.isLoop )	// is a loop or a multi edge
		     makeMultiOrLoop( camera, sox, soy, swx, swy )
		else makeSingle( camera, sox, soy, swx, swy )	// is a single edge.
	}
 
	protected def makeSingle( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		val fromx   = info.points(0).x + sox
		val fromy   = info.points(0).y + soy
		val tox     = info.points(3).x + sox
		val toy     = info.points(3).y + soy
		val mainDir = new Vector2( info.points(0), info.points(3) )
		val length  = mainDir.length
		val angle   = mainDir.y / length
		var c1x     = 0f
		var c1y     = 0f
		var c2x     = 0f
		var c2y     = 0f
		
		if( angle > 0.707107f || angle < -0.707107f ) {
			// North or south.
			c1x = fromx + mainDir.x / 2
			c2x = c1x
			c1y = fromy
			c2y = toy
		} else {
			// East or west.
			c1x = fromx
			c2x = tox
			c1y = fromy + mainDir.y / 2
			c2y = c1y
		}

		theShape.reset
		theShape.moveTo( fromx, fromy )
		theShape.curveTo( c1x, c1y, c2x, c2y, tox, toy )

		// Set the connector as a curve.
		
		if( sox == 0 && soy == 0 ) {
			info.isCurve = true
			info.points(0).set( fromx, fromy )
			info.points(1).set( c1x, c1y )
			info.points(2).set( c2x, c2y )
			info.points(3).set( tox, toy )
		}
	}
 
	protected def makeMultiOrLoop( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		if( info.isLoop )
			 makeLoop( camera, sox, soy, swx, swy )
		else makeMulti( camera, sox, soy, swx, swy )
	}
	
	protected def makeMulti( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		val fromx   = info.points(0).x + sox
		val fromy   = info.points(0).y + soy
		val tox     = info.points(3).x + sox
		val toy     = info.points(3).y + soy
		val c1x     = info.points(1).x + sox
		val c1y     = info.points(1).y + soy
		val c2x     = info.points(2).x + sox
		val c2y     = info.points(2).y + soy

		theShape.reset
		theShape.moveTo( fromx, fromy )
		theShape.curveTo( c1x, c1y, c2x, c2y, tox, toy )
	}

	protected def makeLoop( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
	  	val fromx = info.points(0).x + sox
		val fromy = info.points(0).y + soy
		val tox   = info.points(3).x + sox
		val toy   = info.points(3).y + soy
		val c1x   = info.points(1).x + sox
		val c1y   = info.points(1).y + soy
		val c2x   = info.points(2).x + sox
		val c2y   = info.points(2).y + soy

		theShape.reset
		theShape.moveTo( fromx, fromy )
		theShape.curveTo( c1x, c1y, c2x, c2y, tox, toy )
	}

	protected def makeShadow( g:Graphics2D, camera:Camera ) {
		if( info.isCurve )
		     makeMultiOrLoop( camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth )
		else makeSingle( camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth )
	}
	
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		makeShadow( g, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( g, camera )
 		stroke( g, theShape )
 		fill( g, theSize, theShape )
 		decor( g, camera, info.iconAndText, element, theShape )
// 		showControlPolygon = true
// 		if( showControlPolygon ) {
//	 		val c = g.getColor();
//	 		val s = g.getStroke();
//	 		g.setStroke( new java.awt.BasicStroke( camera.metrics.px1 ) )
//	 		g.setColor( Color.red );
//	 		g.draw( theShape );
//	 		g.setStroke( s );
//	 		g.setColor( c );
//	 		showCtrlPoints( g, camera )
// 		}
	}
}

/**
 * A cubic curve shape that mimics freeplane edges.
 */
class FreePlaneEdgeShape extends LineConnectorShape {
	protected var theShape = new Path2D.Float

// Command
 
// 	def configure( g:Graphics2D, style:Style, camera:Camera, element:GraphicElement ) {
// 	  	configureFillableConnector( style, camera, element )
// 	  	configureShadowableConnector( style, camera )
// 	  	configureStrokableConnector( style, camera )
// 	  	configureDecorable( style, camera )
// 	}
	
	protected def make( g:Graphics2D, camera:Camera ) {
		make( camera, 0, 0, 0, 0 )
	}
  
	protected def make( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		if( info.isMulti > 1 || info.isLoop )	// is a loop or a multi edge
		     makeMultiOrLoop( camera, sox, soy, swx, swy )
		else makeSingle( camera, sox, soy, swx, swy )	// is a single edge.
	}
 
	protected def makeSingle( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		var fromx   = info.points(0).x + sox
		val fromy   = info.points(0).y + soy - theSourceSizeY/2
		var tox     = info.points(3).x + sox
		val toy     = info.points(3).y + soy - theTargetSizeY/2
		val length  = abs( info.points(3).x - info.points(0).x )
		var c1x     = 0f
		var c1y     = 0f
		var c2x     = 0f
		var c2y     = 0f
		
		if( info.points(0).x < info.points(3).x ) {
			// At right.
			fromx += theSourceSizeX/2
			tox   -= theTargetSizeX/2
			c1x    = fromx + length/3
			c2x    = tox - length/3
			c1y    = fromy
			c2y    = toy
		} else {
			// At left.
			fromx -= theSourceSizeX/2
			tox   += theTargetSizeX/2
			c1x    = fromx - length/3
			c2x    = tox + length/3
			c1y    = fromy
			c2y    = toy
		}

		theShape.reset
		theShape.moveTo( fromx, fromy )
		theShape.curveTo( c1x, c1y, c2x, c2y, tox, toy )

		// Set the connector as a curve.
		
		if( sox == 0 && soy == 0 ) {
			info.isCurve = true
			info.points(0).set( fromx, fromy )
			info.points(1).set( c1x, c1y )
			info.points(2).set( c2x, c2y )
			info.points(3).set( tox, toy )
		}
	}
 
	protected def makeMultiOrLoop( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		if( info.isLoop )
			 makeLoop( camera, sox, soy, swx, swy )
		else makeMulti( camera, sox, soy, swx, swy )
	}
	
	protected def makeMulti( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
		val fromx   = info.points(0).x + sox
		val fromy   = info.points(0).y + soy
		val tox     = info.points(3).x + sox
		val toy     = info.points(3).y + soy
		val c1x     = info.points(1).x + sox
		val c1y     = info.points(1).y + soy
		val c2x     = info.points(2).x + sox
		val c2y     = info.points(2).y + soy

		theShape.reset
		theShape.moveTo( fromx, fromy )
		theShape.curveTo( c1x, c1y, c2x, c2y, tox, toy )
	}

	protected def makeLoop( camera:Camera, sox:Float, soy:Float, swx:Float, swy:Float ) {
	  	val fromx = info.points(0).x + sox
		val fromy = info.points(0).y + soy
		val tox   = info.points(3).x + sox
		val toy   = info.points(3).y + soy
		val c1x   = info.points(1).x + sox
		val c1y   = info.points(1).y + soy
		val c2x   = info.points(2).x + sox
		val c2y   = info.points(2).y + soy

		theShape.reset
		theShape.moveTo( fromx, fromy )
		theShape.curveTo( c1x, c1y, c2x, c2y, tox, toy )
	}

	protected def makeShadow( g:Graphics2D, camera:Camera ) {
		if( info.isCurve )
		     makeMultiOrLoop( camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth )
		else makeSingle( camera, theShadowOff.x, theShadowOff.y, theShadowWidth, theShadowWidth )
	}
	
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		makeShadow( g, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( g, camera )
 		stroke( g, theShape )
 		fill( g, theSize, theShape )
 		decor( g, camera, info.iconAndText, element, theShape )
	}
}