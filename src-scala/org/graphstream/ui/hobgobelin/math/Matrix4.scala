package org.graphstream.ui.hobgobelin.math

import org.graphstream.ui.geom.{Point3 => GSPoint3}
import org.sofa.math.Matrix3
import org.sofa.math.ArrayMatrix3
import org.sofa.nio.DoubleBuffer
import org.sofa.math.NioBufferMatrix3

abstract class Matrix4 extends org.sofa.math.Matrix4 {
	def fastMult(p:GSPoint3):GSPoint3 = {
		var xx = 0.0
		var yy = 0.0
		var zz = 0.0
		var ww = 0.0
		
		xx += this(0, 0) * p.x
		xx += this(0, 1) * p.y
		xx += this(0, 2) * p.z
		xx += this(0, 3)
		
		yy += this(1, 0) * p.x
		yy += this(1, 1) * p.y
		yy += this(1, 2) * p.z
		yy += this(1, 3)
		
		zz += this(2, 0) * p.x
		zz += this(2, 1) * p.y
		zz += this(2, 2) * p.z
		zz += this(2, 3)
		
		ww += this(3, 0) * p.x
		ww += this(3, 1) * p.y
		ww += this(3, 2) * p.z
		ww += this(3, 3)
		
		p.set(xx/ww, yy/ww, zz/ww)
	}
}

class ArrayMatrix4 extends Matrix4 {
    type ArrayLike = Array[Double]
    type ReturnType = ArrayMatrix4
    val data = new Array[Double](16)
    def newInstance(w:Int, h:Int) = new ArrayMatrix4()
    override def toDoubleArray = data
    def top3x3:Matrix3 = ArrayMatrix3((this(0,0), this(0,1), this(0,2)),
                                      (this(1,0), this(1,1), this(1,2)),
                                      (this(2,0), this(2,1), this(2,2)))
}

class NioBufferMatrix4 extends Matrix4 {
    type ArrayLike = DoubleBuffer
    type ReturnType = NioBufferMatrix4
    val data = new DoubleBuffer(16)
    def newInstance(w:Int, h:Int) = new NioBufferMatrix4()    
    override def toDoubleBuffer = { data.rewind; data }
    def top3x3:Matrix3 = NioBufferMatrix3((this(0,0), this(0,1), this(0,2)),
                                          (this(1,0), this(1,1), this(1,2)),
                                          (this(2,0), this(2,1), this(2,2)))
}

object Matrix4 {
    def apply(row0:(Double,Double,Double,Double),
              row1:(Double,Double,Double,Double),
              row2:(Double,Double,Double,Double),
              row3:(Double,Double,Double,Double)):ArrayMatrix4 = ArrayMatrix4(row0, row1, row2, row3)
    def apply(other:Matrix4):ArrayMatrix4 = ArrayMatrix4(other)
    def apply():ArrayMatrix4 = ArrayMatrix4()
}

object ArrayMatrix4 {
    def apply(row0:(Double,Double,Double,Double),
              row1:(Double,Double,Double,Double),
              row2:(Double,Double,Double,Double),
              row3:(Double,Double,Double,Double)):ArrayMatrix4 = {
    	val result = new ArrayMatrix4()
    	result.row0 = row0
    	result.row1 = row1 
    	result.row2 = row2
    	result.row3 = row3
    	result
    }
    def apply(other:Matrix4):ArrayMatrix4 = {
        val result = new ArrayMatrix4()
        result.copy(other)
        result
    }
    def apply():ArrayMatrix4 = {
        val result = new ArrayMatrix4()
        result.setIdentity
        result
    }
}

object NioBufferMatrix4 {
    def apply(row0:(Double,Double,Double,Double),
              row1:(Double,Double,Double,Double),
              row2:(Double,Double,Double,Double),
              row3:(Double,Double,Double,Double)):NioBufferMatrix4 = {
    	val result = new NioBufferMatrix4()
    	result.row0 = row0
    	result.row1 = row1 
    	result.row2 = row2
    	result.row3 = row3
    	result
    }
    def apply(other:ArrayMatrix3):NioBufferMatrix4 = {
        val result = new NioBufferMatrix4()
        result.copy(other)
        result
    }
    def apply():NioBufferMatrix4 = {
        val result = new NioBufferMatrix4()
        result.setIdentity
        result
    }
}
