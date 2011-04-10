package org.graphstream.ui.util

import org.graphstream.ui.sgeom.Point3

trait AttributeUtils {
	
    /**
     * Try to extract an array of 3D points from various sources. It actually works only for
     * arrays of Point3, or arrays of floats, doubles and integers.
     * @param an object.
     * @return An array of 3D points.
     */
 	protected def getPoints(values:AnyRef):Array[Point3] = {
 	    values match {
 			case b:Array[Point3]  => { b }
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
 			        		        b(i*3).asInstanceOf[Number].floatValue,
 			        		        b(i*3+1).asInstanceOf[Number].floatValue,
 			        		        b(i*3+2).asInstanceOf[Number].floatValue)
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
 			case x => {
 			    Console.err.println("Cannot interpret ui.points contents (%s)".format(x.getClass.getName))
 			    new Array[Point3](0)
 			}
 		}
 	}

 	/**
 	 * Compute the bounding box of the given set of points.
 	 * @param The set of points.
 	 * @return A 2-tuple with the minimum and maximum 3D points.
 	 */
    protected def boundingBoxOfPoints(points:Array[Point3]):(Point3, Point3) = {
        var minx = Float.MaxValue
        var miny = Float.MaxValue
        var minz = Float.MaxValue
        var maxx = Float.MinValue
        var maxy = Float.MinValue
        var maxz = Float.MinValue
        
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