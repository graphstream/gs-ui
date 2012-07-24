package org.graphstream.ui.hobgobelin.test

import org.graphstream.ui.hobgobelin.math.Matrix4
import org.sofa.math.Vector4
import org.graphstream.ui.geom.{Point3 => GSPoint3}
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

object TestMaths {
	def main(args:Array[String]) {
		(new TestMaths).test
	}
}

class TestMaths {
	@Test
	def test {
		val matrix = Matrix4(
					(2, 0, 0, 0),
					(0, 2, 0, 0),
					(0, 0, 2, 0),
					(0, 0, 0, 1))
		val p1 = new GSPoint3(1, 2 ,3)
		var result = matrix.fastMult(p1)
		
		assertEquals(p1, new GSPoint3(2,4,6))
		assertTrue(p1 == result)	// Compare the references, fastCopy acts in place.
		
		matrix.setIdentity
		matrix.setTranslation(2, 2, 2)
		val p2 = new GSPoint3(1, 1, 1)
		result = matrix.fastMult(p2)
		
		assertEquals(p2, new GSPoint3(3, 3, 3))
		
		matrix.setIdentity
		matrix.setScale(2, 2, 2)
		val p3 = new GSPoint3(2, 2, 2)
		result = matrix.fastMult(p3)
		
		assertEquals(p3, new GSPoint3(4, 4, 4))
	}
}