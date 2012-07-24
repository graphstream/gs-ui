package org.graphstream.ui.hobgobelin

import org.graphstream.ui.swingViewer.BaseCamera
import org.graphstream.ui.graphicGraph.GraphicGraph
import org.graphstream.ui.hobgobelin.math.Matrix4
import org.sofa.math.Point3
import org.graphstream.ui.geom.{Point3 => GSPoint3}
import org.sofa.math.Vector3
import org.graphstream.ui.graphicGraph.GraphicNode
import org.graphstream.ui.graphicGraph.GraphicSprite
import org.graphstream.ui.graphicGraph.GraphicElement

class HobgobelinCamera(graph:GraphicGraph) extends BaseCamera(graph) {
	//------------------------------------------------------------------------
	// Attributes

	/** The graph-space -> pixel-space transformation. */
	protected val Tx:Matrix4 = Matrix4()
	
	/** The inverse transform of Tx. */
	protected val xT:Matrix4 = null

	//------------------------------------------------------------------------
	// Access
	
	override def transformPxToGu(x:Double, y:Double):GSPoint3 = xT.fastMult(new GSPoint3(x, y, 0))
	
	override def transformGuToPx(x:Double, y:Double, z:Double):GSPoint3 = Tx.fastMult(new GSPoint3(x, y, z))
	
	override def transformPxToGu(p:GSPoint3):GSPoint3 = xT.fastMult(p)
	
	override def transformGuToPx(p:GSPoint3):GSPoint3 = Tx.fastMult(p)

	override def isNodeVisibleIn(node:GraphicNode, X1:Double, Y1:Double, X2:Double, Y2:Double):Boolean = {
		// TODO
		true
	}
	
	override def isSpriteVisibleIn(sprite:GraphicSprite, X1:Double, Y1:Double, X2:Double, Y2:Double):Boolean = {
		// TODO
		true
	}
	
	override def nodeContains(elt:GraphicElement, x:Double, y:Double):Boolean = {
		// TODO
		false
	}
	
	override def spriteContains(elt:GraphicElement, x:Double, y:Double):Boolean = {
		// TODO
		false
	}
	
	//------------------------------------------------------------------------
	// Commands
	
	override def pushView(g2:java.awt.Graphics2D, x:Double, y:Double, width:Double, height:Double) {
		// TODO
	}
	
	override def popView(g2:java.awt.Graphics2D) {
		// TODO
	}
}