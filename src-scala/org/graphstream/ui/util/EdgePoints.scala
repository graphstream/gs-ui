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

import org.graphstream.ui.geom._

/** A set of points defining the topology of an edge. */
class EdgePoints(n:Int) {
	protected var points = new Array[Point3](n)
	
	for(i <- 0 until size)
		points(i) = new Point3
	
	/** Number of points. */
	def size:Int = points.size

	/** Copy a set of points into this set (points are fully copied). */
	def copy(newPoints:Array[Point3]) { points = newPoints.map { p => new Point3(p) } }
	
	/** Set the `i`-th point of the set to be (`x`,`y`). */
	def set(i:Int, x:Double, y:Double, z:Double) = points(i).set( x, y, z)
	
	/** The `i`-th point of the set. */
	def get(i:Int):Point3 = points(i)
	
	def apply(i:Int):Point3 = points(i)
	
	def update(i:Int, coos:Point3) = points(i) = new Point3(coos.x, coos.y, coos.z) 
	
	override def toString():String = "pts(%s):%d".format(points.mkString(","), points.size)
}