package org.graphstream.ui.util

import scala.collection.mutable.WrappedArray
import org.graphstream.ui.geom.Point3

trait AttributeUtils {
    /** Try to extract an array of 3D points from various sources. It actually works only for
      * arrays of Point3, or arrays of floats, doubles and integers.
      * @param an object.
      * @return An array of 3D points. */
 	protected def getPoints(values:AnyRef):Array[Point3] = {
 	    values match {
 			case b:Array[Point3]  => if(b.size==0) Console.err.println("0 ui.point"); b
 			case b:WrappedArray[AnyRef] => getPoints(b.toArray)
 			case b:Array[AnyRef] => {
 			    if(b.size>0) {
 			        if(b(0).isInstanceOf[Point3]) {
 			            val res = new Array[Point3](b.size)
 			            for(i<- 0 until b.size) {
 			                res(i) = b(i).asInstanceOf[Point3]
 			            }
 			            res
 			        } else if(b(0).isInstanceOf[Number]) {
 			        	val size = b.length/3
 			        	val res  = new Array[Point3](size)
 			    
 			        	for(i <- 0 until size) {
 			        		res(i) = new Point3(
 			        		        b(i*3).asInstanceOf[Number].doubleValue,
 			        		        b(i*3+1).asInstanceOf[Number].doubleValue,
 			        		        b(i*3+2).asInstanceOf[Number].doubleValue)
 			        	}
 			        	res
 			        } else {
 			            Console.err.println("Cannot interpret ui.points elements type %s".format(b(0).getClass.getName))
 			            new Array[Point3](0)
 			        }
 			    } else {
 			        Console.err.println("ui.points array size is zero !!")
 			        new Array[Point3](0)
 			    }
 			}
 			case b:Array[Double] => {
 			    if(b.size>0) {
		        	val size = b.length/3
		        	val res  = new Array[Point3](size)
		        	for(i <- 0 until size) { res(i) = new Point3(b(i*3), b(i*3+1), b(i*3+2)) }
		        	res
 			    } else {
 			        Console.err.println("ui.points array size is zero !!")
 			        new Array[Point3](0)
 			    }
 			}
 			case b:Array[Float] => {
 			    if(b.size>0) {
		        	val size = b.length/3
		        	val res  = new Array[Point3](size)
		        	for(i <- 0 until size) { res(i) = new Point3(b(i*3), b(i*3+1), b(i*3+2)) }
		        	res
 			    } else {
 			        Console.err.println("ui.points array size is zero !!")
 			        new Array[Point3](0)
 			    } 			    
 			}
 			case x => {
 			    Console.err.println("Cannot interpret ui.points contents (%s)".format(x.getClass.getName))
 			    new Array[Point3](0)
 			}
 		}
 	}
 	 	
 	/** Try to extract an array of double values from various sources. */
 	protected def getDoubles(values:AnyRef):Array[Double] = {
 		values match {
 			case a:Array[AnyRef] => { 
 				val result = new Array[Double]( a.length )
 				a.map( { _ match {
 						case n:Number => n.doubleValue
 						case s:String => s.toDouble
 						case _        => 0.0
 					}
 				} )
 			}
 			case b:Array[Double]  => { b }
 			case b:Array[Float]   => { b.map { _.toDouble } }
 			case b:Array[Int]     => { b.map { _.toDouble } }
 			case c:String         => { c.split(',').map { _.toDouble } }
 			case x                => { System.err.println("cannot extract double values from array %s".format(x.getClass.getName)); Array[Double]( 0 ) }
 		}
 	}

 	/** Compute the bounding box of the given set of points.
 	  * @param The set of points.
 	  * @return A 2-tuple with the minimum and maximum 3D points. */
    protected def boundingBoxOfPoints(points:Array[Point3]):(Point3, Point3) = {
        var minx = Double.MaxValue
        var miny = Double.MaxValue
        var minz = Double.MaxValue
        var maxx = Double.MinValue
        var maxy = Double.MinValue
        var maxz = Double.MinValue
        
        points.foreach { p =>
        	minx = if(p.x<minx) p.x else minx
        	miny = if(p.y<miny) p.y else miny
        	minz = if(p.z<minz) p.z else minz
        	maxx = if(p.x>maxx) p.x else maxx
        	maxy = if(p.y>maxy) p.y else maxy
        	maxz = if(p.z>maxz) p.z else maxz
        }
        
        (new Point3(minx, miny, minz), new Point3(maxx, maxy, maxz))
    }
}