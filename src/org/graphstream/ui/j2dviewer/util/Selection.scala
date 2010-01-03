package org.graphstream.ui.j2dviewer.util

import org.graphstream.ui.geom.Point3

/**
 * A replesentation of the current selection.
 */
class Selection {
	var active = false
	
	var renderer:org.graphstream.ui.j2dviewer.renderer.SelectionRenderer = null

	private[this] val lo:Point3 = new Point3
	private[this] val hi:Point3 = new Point3
	
	def begins( x:Float, y:Float ) {
		lo.x = x
		lo.y = y
		hi.x = x
		hi.y = y
	}
 
 	def grows( x:Float, y:Float ) {
 		hi.x = x
 		hi.y = y
 	}
  
 	def x1:Float = lo.x
 	def y1:Float = lo.y
 	def x2:Float = hi.x
 	def y2:Float = hi.y
}