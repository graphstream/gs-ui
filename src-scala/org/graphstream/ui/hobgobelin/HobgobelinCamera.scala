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
import org.sofa.opengl.MatrixStack
import org.graphstream.ui.graphicGraph.stylesheet.Style
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units

class HobgobelinCamera(graph:GraphicGraph) extends BaseCamera(graph) {
	//------------------------------------------------------------------------
	// Attributes

	/** The graph-space -> pixel-space transformation. */
	val Tx:MatrixStack[Matrix4] = new MatrixStack[Matrix4](Matrix4())
	
	/** The inverse transform of Tx. */
	val xT:MatrixStack[Matrix4] = new MatrixStack[Matrix4](Matrix4())

	//------------------------------------------------------------------------
	// Access
	
	override def transformPxToGu(x:Double, y:Double):GSPoint3 = xT.top.fastMult(new GSPoint3(x, y, 0))
	
	override def transformGuToPx(x:Double, y:Double, z:Double):GSPoint3 = Tx.top.fastMult(new GSPoint3(x, y, z))
	
	override def transformPxToGu(p:GSPoint3):GSPoint3 = xT.top.fastMult(p)
	
	override def transformGuToPx(p:GSPoint3):GSPoint3 = Tx.top.fastMult(p)

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
	
	/** Push an orthographic view where units correspond to pixels. */ 
	def pushPXView(x:Double, y:Double, width:Double, height:Double) {
		setBounds
		setPadding
		metrics.setSurfaceViewport(x, y, width, height)
		Tx.push
		Tx.push
		Tx.top.orthographic(0, width, 0, height, -10000, 10000)
		xT.top.inverseOrthographic(0, width, 0, height, -10000, 10000)
	}

	/** Pop the PX view. */
	def popPXView() {
		Tx.pop
		xT.pop
	}
	
	/** Push an orthographic view where units correspond to graph units. */
	def pushGUView(x:Double, y:Double, width:Double, height:Double) {
		setBounds
		setPadding
		metrics.setSurfaceViewport(x, y, width, height)
		Tx.push
		xT.push
		
		if(autoFit) 
			pushAutoFitGUView()
		else pushUserGUView()
		
	}
	
	/** Pop the GU view. */
	def popGUView() {
		Tx.pop
		xT.pop
	}
	
	/** Push the Tx and xT matrices, execute the given code and pop these matrices. */
	def pushpop(code: =>Unit) {
		Tx.push
		xT.push
		code
		xT.pop
		xT.pop
	}
	
	protected def pushAutoFitGUView() {
		setRatio
		
		val padx   = metrics.lengthToGu(padding, 0)
		val pady   = if(padding.size > 1) metrics.lengthToGu(padding, 1) else padx
		val right  = metrics.size.data(0)/2 + padx
		val left   = -right
		val top    = metrics.size.data(1)/2 + pady
		val bottom = -top
		
		zoom = 1
		
		Tx.top.orthographic(left, right, bottom, top, -10000, 10000)
		xT.top.inverseOrthographic(left, right, bottom, top, -10000, 10000)

		metrics.loVisible.copy(metrics.lo)
		metrics.hiVisible.copy(metrics.hi)
	}
	
	protected def pushUserGUView() {
		
	}

	// -------------------------------------------------------------------------
	// Utility
	
	/** Choose the least ratio, in order to be able to convert lengths. */
	protected def setRatio() {
		val padXgu = paddingXgu * 2
		val padYgu = paddingYgu * 2
		val padXpx = paddingXpx * 2
		val padYpx = paddingYpx * 2
		
		var sx = (metrics.surfaceViewport(0)-padXpx) / (metrics.size.data(0)+padXgu)
		var sy = (metrics.surfaceViewport(1)-padYpx) / (metrics.size.data(1)+padYgu)
		
		if(sx>sy) sx = sy else sy = sx
		
		metrics.setRatioPx2Gu(sx)
	}
	
	protected def setCenter() {
		var tx = metrics.lo.x + (metrics.size.data(0) / 2)
		var ty = metrics.lo.y + (metrics.size.data(1) / 2)
		center.set(tx, ty, 0)		
	}
	
	protected def paddingXgu:Double =
		if (padding.units == Units.GU && padding.size() > 0) padding.get(0) else 0
	
	protected def paddingYgu:Double =
		if (padding.units == Units.GU && padding.size() > 1) padding.get(1) else paddingXgu
	
	protected def paddingXpx:Double =
		if (padding.units == Units.PX && padding.size() > 0) padding.get(0) else 0
	
	protected def paddingYpx:Double = 
		if (padding.units == Units.PX && padding.size() > 1) padding.get(1) else paddingXpx
}