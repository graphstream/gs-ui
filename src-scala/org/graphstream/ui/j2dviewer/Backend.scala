package org.graphstream.ui.j2dviewer

import java.awt.RenderingHints
import java.awt.geom.Point2D
import java.awt.geom.AffineTransform
import scala.collection.mutable.ArrayStack
import java.awt.Graphics2D
import org.graphstream.ui.sgeom.Point3
import org.graphstream.ui.j2dviewer.renderer.GraphBackgroundRenderer
import org.graphstream.ui.j2dviewer.renderer.shape.Shape
import org.graphstream.ui.graphicGraph.StyleGroup

/**
 * The graphic driver.
 * 
 * The back-end can be for example Java2D or OpenGL.
 */
abstract class Backend {
	// TODO, one day.
    // The goal is to replace the use of Java2D by the back-end
    // Then to produce a new back-end using OpenGL to accelerate
    // thinks, since Java is a big Mammoth that will never follow
    // actual technologies (on Linux, I doubt it will ever get
    // real good Java2D implementation).
    
    /** Setup the backend for a new rendering session. */
    def prepareNewFrame(g2:Graphics2D)
    
    /**
     * Transform a point in graph units into pixel units.
     * @return the transformed point.
     */
    def transform(x:Double, y:Double, z:Double):Point3
    
    /**
     * Pass a point in transformed coordinates (pixels) into the reverse transform (into
     * graph units).
     * @return the transformed point.
     */
    def inverseTransform(x:Double, y:Double, z:Double):Point3
    
    /** 
     * Transform a point in graph units into pixel units, the given point is transformed in place
     * and also returned. 
     */
    def transform(p:Point3):Point3
    
    /**
     * Transform a point in pixel units into graph units, the given point is transformed in
     * place and also returned.
     */
    def inverseTransform(p:Point3):Point3
    
    /**
     * Push the actual transformation on the matrix stack, installing
     * a copy of it on the top of the stack.
     */
    def pushTransform()
 
    /**
     * Begin the work on the actual transformation matrix.
     */
    def beginTransform

    /**
     * Make the top-most matrix as an identity matrix.
     */
    def setIdentity()
    
    /**
     * Multiply the to-most matrix by a translation matrix.
     */
    def translate(tx:Double, ty:Double, tz:Double)
    
    /**
     * Multiply the top-most matrix by a rotation matrix.
     */
    def rotate(angle:Double, ax:Double, ay:Double, az:Double)
    
    /**
     * Multiply the top-most matrix by a scaling matrix.
     */
    def scale(sx:Double, sy:Double, sz:Double)
    
    /**
     * End the work on the actual transformation matrix, installing it as the actual modelview
     * matrix. If you do not call this method, all the scaling, translation and rotation are
     * lost.
     */
    def endTransform
    
    /**
     * Pop the actual transformation of the matrix stack, restoring
     * the previous one in the stack.
     */
    def popTransform()

    /**
     * Enable or disable anti-aliasing.
     */
    def setAntialias(on:Boolean)
    
    /**
     * Enable or disable the hi-quality mode.
     */
    def setQuality(on:Boolean)

    /**
     * The Java2D graphics.
     */
    def graphics2D:Graphics2D
    
    def chooseNodeShape(oldShape:Shape, group:StyleGroup):Shape
    def chooseEdgeShape(oldShape:Shape, group:StyleGroup):Shape
    def chooseEdgeArrowShape(oldShape:Shape, group:StyleGroup):Shape
    def chooseSpriteShape(oldShape:Shape, group:StyleGroup):Shape
    def chooseGraphBackgroundRenderer():GraphBackgroundRenderer
}

/**
 * A full Java-2D rendering back-end.
 */
class BackendJ2D extends Backend {
	protected var g2:Graphics2D = null
    
    protected val matrixStack = new ArrayStack[AffineTransform]()
    
    protected var Tx:AffineTransform = null
    
    protected var xT:AffineTransform = null
    
    protected val dummyPoint = new Point2D.Double()
    
	def graphics2D:Graphics2D = g2
	
    override def prepareNewFrame(g2:Graphics2D) {
	    this.g2 = g2
        Tx = g2.getTransform
        matrixStack.clear
        matrixStack.push(Tx)
    }

	def beginTransform() {
	}
	
    def endTransform() {
        g2.setTransform(Tx)
        computeInverse
    }

    protected def computeInverse() {
        try {
        	xT = new AffineTransform(Tx)
        	xT.invert
        } catch {
            case _ => Console.err.println("Cannot inverse matrix")
        }
    }
    
    def transform(x:Double, y:Double, z:Double):Point3 = {
        dummyPoint.setLocation(x,y)
        Tx.transform(dummyPoint, dummyPoint)
        new Point3(dummyPoint.x, dummyPoint.y, 0)
    }
    
    def inverseTransform(x:Double, y:Double, z:Double):Point3 = {
        dummyPoint.setLocation(x, y)
        xT.transform(dummyPoint, dummyPoint)
        new Point3(dummyPoint.x, dummyPoint.y, 0)
    }
    
    def transform(p:Point3):Point3 = {
        dummyPoint.setLocation(p.x, p.y)
        Tx.transform(dummyPoint, dummyPoint)
        p.set(dummyPoint.x, dummyPoint.y, 0)
        p
    }
    
    def inverseTransform(p:Point3):Point3 = {
        dummyPoint.setLocation(p.x, p.y)
        xT.transform(dummyPoint, dummyPoint)
        p.set(dummyPoint.x, dummyPoint.y, 0)
        p
    }
    
    /**
     * Push the actual transformation on the matrix stack, installing
     * a copy of it on the top of the stack.
     */
    def pushTransform() {
        val newTx = new AffineTransform(Tx)
        matrixStack.push(newTx)
        g2.setTransform(newTx)
        Tx = newTx
        // xT inchanged, since newTx is a copy of Tx
    }
    
    /**
     * Pop the actual transformation of the matrix stack, installing
     * the previous one in the stack.
     */
    def popTransform() {
        assert(!matrixStack.isEmpty)
        matrixStack.pop
        //Tx = matrixStack.top
        g2.setTransform(matrixStack.top)
        //computeInverse
    }
    
    def setIdentity() {
        Tx.setToIdentity
    }
    
    def translate(tx:Double, ty:Double, tz:Double) {
        Tx.translate(tx, ty)
    }
    
    def rotate(angle:Double, ax:Double, ay:Double, az:Double) {
        Tx.rotate(angle)
    }
    
    def scale(sx:Double, sy:Double, sz:Double) {
        Tx.scale(sx, sy)
    }
    
    def setAntialias(on:Boolean) {
       import RenderingHints._
	   if(on) {
		   g2.setRenderingHint(KEY_STROKE_CONTROL,    VALUE_STROKE_PURE)
		   g2.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON)
		   g2.setRenderingHint(KEY_ANTIALIASING,      VALUE_ANTIALIAS_ON)
	   } else {
		   g2.setRenderingHint(KEY_STROKE_CONTROL,    VALUE_STROKE_DEFAULT)
		   g2.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_OFF)
		   g2.setRenderingHint(KEY_ANTIALIASING,      VALUE_ANTIALIAS_OFF)
	   }
        
    }
    
    def setQuality(on:Boolean) {
       import RenderingHints._
       if(on) {
		   g2.setRenderingHint(KEY_RENDERING,           VALUE_RENDER_SPEED)
		   g2.setRenderingHint(KEY_INTERPOLATION,       VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
		   g2.setRenderingHint(KEY_COLOR_RENDERING,     VALUE_COLOR_RENDER_SPEED)
		   g2.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_SPEED)
	   } else {
		   g2.setRenderingHint(KEY_RENDERING,           VALUE_RENDER_QUALITY)
		   g2.setRenderingHint(KEY_INTERPOLATION,       VALUE_INTERPOLATION_BICUBIC)
		   g2.setRenderingHint(KEY_COLOR_RENDERING,     VALUE_COLOR_RENDER_QUALITY)
		   g2.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY)
	   }
    }
    
    def chooseNodeShape(oldShape:Shape, group:StyleGroup):Shape = {
        import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape._
        import org.graphstream.ui.j2dviewer.renderer.shape._
		group.getShape match {
			case CIRCLE         => if(oldShape.isInstanceOf[CircleShape])         oldShape else new CircleShape 
		  	case BOX            => if(oldShape.isInstanceOf[SquareShape])         oldShape else new SquareShape
		  	case ROUNDED_BOX    => if(oldShape.isInstanceOf[RoundedSquareShape])  oldShape else new RoundedSquareShape
		  	case DIAMOND        => if(oldShape.isInstanceOf[DiamondShape])        oldShape else new DiamondShape
		    case TRIANGLE       => if(oldShape.isInstanceOf[TriangleShape])       oldShape else new TriangleShape
		    case CROSS          => if(oldShape.isInstanceOf[CrossShape])          oldShape else new CrossShape
		    case FREEPLANE      => if(oldShape.isInstanceOf[FreePlaneNodeShape])  oldShape else new FreePlaneNodeShape
		    case PIE_CHART      => if(oldShape.isInstanceOf[PieChartShape])       oldShape else new PieChartShape
		  	case POLYGON        => if(oldShape.isInstanceOf[PolygonShape])        oldShape else new PolygonShape
		  	// ------------------------------------------
		    case TEXT_BOX       => Console.err.printf( "** SORRY text-box shape not yet implemented **%n" );     new SquareShape
		    case TEXT_PARAGRAPH => Console.err.printf( "** SORRY text-para shape not yet implemented **%n" );    new SquareShape
		    case TEXT_CIRCLE    => Console.err.printf( "** SORRY text-circle shape not yet implemented **%n" );  new CircleShape
		    case TEXT_DIAMOND   => Console.err.printf( "** SORRY text-diamond shape not yet implemented **%n" ); new CircleShape
		    case ARROW          => Console.err.printf( "** SORRY arrow shape not yet implemented **%n" );        new CircleShape
		    case IMAGES         => Console.err.printf( "** SORRY images shape not yet implemented **%n" );       new SquareShape 
		  	// ------------------------------------------
		    case JCOMPONENT     => throw new RuntimeException("WTF, jcomponent should have its own renderer")
		    case x              => throw new RuntimeException("%s shape cannot be set for nodes".format(x.toString))
		}
    }

    def chooseEdgeShape(oldShape:Shape, group:StyleGroup):Shape = {
        import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape._
        import org.graphstream.ui.j2dviewer.renderer.shape._
		group.getShape match {
			case LINE        => if(oldShape.isInstanceOf[LineShape])                 oldShape else new LineShape
		  	case ANGLE       => if(oldShape.isInstanceOf[AngleShape])                oldShape else new AngleShape
    		case BLOB        => if(oldShape.isInstanceOf[BlobShape])                 oldShape else new BlobShape
		  	case CUBIC_CURVE => if(oldShape.isInstanceOf[CubicCurveShape])           oldShape else new CubicCurveShape
		  	case FREEPLANE   => if(oldShape.isInstanceOf[FreePlaneEdgeShape])        oldShape else new FreePlaneEdgeShape
    		case POLYLINE    => if(oldShape.isInstanceOf[PolylineEdgeShape])         oldShape else new PolylineEdgeShape
    		case SQUARELINE  => Console.err.printf("** SORRY square-line shape not yet implemented **"); new HorizontalSquareEdgeShape 
    		case LSQUARELINE => if(oldShape.isInstanceOf[HorizontalSquareEdgeShape]) oldShape else new LSquareEdgeShape 
    		case HSQUARELINE => if(oldShape.isInstanceOf[HorizontalSquareEdgeShape]) oldShape else new HorizontalSquareEdgeShape 
    		case VSQUARELINE => Console.err.printf("** SORRY square-line shape not yet implemented **"); new HorizontalSquareEdgeShape 
		    case x           => throw new RuntimeException("%s shape cannot be set for edges".format(x.toString))
		}

    }
    
    def chooseEdgeArrowShape(oldShape:Shape, group:StyleGroup):Shape = {
        import org.graphstream.ui.j2dviewer.renderer.shape._
 		import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.ArrowShape._
		group.getArrowShape match {
			case NONE    => null
			case ARROW   => if(oldShape.isInstanceOf[ArrowOnEdge])   oldShape else new ArrowOnEdge
			case CIRCLE  => if(oldShape.isInstanceOf[CircleOnEdge])  oldShape else new CircleOnEdge
			case DIAMOND => if(oldShape.isInstanceOf[DiamondOnEdge]) oldShape else new DiamondOnEdge
			case IMAGE   => if(oldShape.isInstanceOf[ImageOnEdge])   oldShape else new ImageOnEdge
		    case x       => throw new RuntimeException("%s shape cannot be set for edge arrows".format(x.toString))
		}        
    }

    def chooseSpriteShape(oldShape:Shape, group:StyleGroup):Shape = {
        import org.graphstream.ui.j2dviewer.renderer.shape._
        import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape._
		group.getShape match {
			case CIRCLE         => if(oldShape.isInstanceOf[CircleShape])           oldShape else new CircleShape 
		  	case BOX            => if(oldShape.isInstanceOf[OrientableSquareShape]) oldShape else new OrientableSquareShape
		  	case ROUNDED_BOX    => if(oldShape.isInstanceOf[RoundedSquareShape])    oldShape else new RoundedSquareShape
		  	case DIAMOND        => if(oldShape.isInstanceOf[DiamondShape])          oldShape else new DiamondShape
		    case TRIANGLE       => if(oldShape.isInstanceOf[TriangleShape])         oldShape else new TriangleShape
		    case CROSS          => if(oldShape.isInstanceOf[CrossShape])            oldShape else new CrossShape
		    case ARROW          => if(oldShape.isInstanceOf[SpriteArrowShape])      oldShape else new SpriteArrowShape
		    case FLOW           => if(oldShape.isInstanceOf[SpriteFlowShape])       oldShape else new SpriteFlowShape
		    case PIE_CHART      => if(oldShape.isInstanceOf[PieChartShape])         oldShape else new PieChartShape
		  	case POLYGON        => if(oldShape.isInstanceOf[PolygonShape])          oldShape else new PolygonShape
		  	// ------------------------------------------
		    case TEXT_BOX       => Console.err.printf( "** SORRY text-box shape not yet implemented **%n" );     new SquareShape
		    case TEXT_PARAGRAPH => Console.err.printf( "** SORRY text-para shape not yet implemented **%n" );    new SquareShape
		    case TEXT_CIRCLE    => Console.err.printf( "** SORRY text-circle shape not yet implemented **%n" );  new CircleShape
		    case TEXT_DIAMOND   => Console.err.printf( "** SORRY text-diamond shape not yet implemented **%n" ); new CircleShape
		    case IMAGES         => Console.err.printf( "** SORRY images shape not yet implemented **%n" );       new SquareShape 
		  	// ------------------------------------------
		    case JCOMPONENT     => throw new RuntimeException("WTF, jcomponent should have its own renderer")
		    case x              => throw new RuntimeException("%s shape cannot be set for sprites".format(x.toString))
		}
    }

    def chooseGraphBackgroundRenderer():GraphBackgroundRenderer = {
        null
    }
}