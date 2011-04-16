package org.graphstream.ui.j2dviewer

import org.graphstream.ui.sgeom.Point3
/**
 * The graphic driver.
 * 
 * The backend can be for example Java2D or OpenGL.
 */
abstract class Backend {
	// TODO, one day.
    // The goal is to replace the use of Java2D by the backend
    // Then to produce a new backend using OpenGL to accelerate
    // thinks, since Java is a big Mammoth that will never follow
    // actual technologies (on Linux, I doubt it will ever get
    // real good Java2D implementation).
    
    /**
     * Transform a point in graph units into pixel units.
     * @return the transformed point.
     */
    def transform(x:Double, y:Double, z:Double):Point3
    
    def inverseTransform(x:Double, y:Double, z:Double):Point3
    
    def transform(p:Point3):Point3
    
    def inverseTransform(p:Point3):Point3
    
    /**
     * Push the actual transformation on the matrix stack, installing
     * a copy of it on the top of the stack.
     */
    def pushTransform()
    
    /**
     * Pop the actual transformation of the matrix stack, installing
     * the previous one in the stack.
     */
    def popTransform()
    
    def setIdentity()
    
    def translate(tx:Double, ty:Double, tz:Double)
    
    def rotate(angle:Double, ax:Double, ay:Double, az:Double)
    
    def scale(sx:Double, sy:Double, sz:Double)
}

class BackendJ2D {
    
    
    def transform(x:Double, y:Double, z:Double):Point3
    
    def inverseTransform(x:Double, y:Double, z:Double):Point3
    
    def transform(p:Point3):Point3
    
    def inverseTransform(p:Point3):Point3
    
    /**
     * Push the actual transformation on the matrix stack, installing
     * a copy of it on the top of the stack.
     */
    def pushTransform()
    
    /**
     * Pop the actual transformation of the matrix stack, installing
     * the previous one in the stack.
     */
    def popTransform()
    
    def setIdentity()
    
    def translate(tx:Double, ty:Double, tz:Double)
    
    def rotate(angle:Double, ax:Double, ay:Double, az:Double)
    
    def scale(sx:Double, sy:Double, sz:Double)
    
}