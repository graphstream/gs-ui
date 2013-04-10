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