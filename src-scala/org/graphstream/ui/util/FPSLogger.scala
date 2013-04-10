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
import java.io.PrintStream
import scala.compat.Platform

/** Very simple logger for Frame-Per-Second measurements.
  * 
  * @param fileName The name of the file where measurements will be written, frame by frame. */
class FPSLogger(protected val fileName:String) {
    /** Start time for a frame. */
    protected var T1:Long = 0
    
    /** End Time for a frame. */
    protected var T2:Long = 0
    
    /** Output channel. */
    protected var out:PrintStream = null
    
    /** Start a new frame measurement. */
    def beginFrame() {
        T1 = Platform.currentTime
    }
    
    /** End a frame measurement and save the instantaneous FPS measurement in the file indicated at
      * construction time.
      * 
      * If the writer for the log file was not yet open, create it. You can use `close()` to ensure
      * the log file is flushed and closed.
      * 
      * The output is made of a descriptive header followed by a frame description on each line.
      * A frame description contains the instantaneous FPS measurement and the time in millisecond
      * the frame lasted. */
    def endFrame() {
        T2 = Platform.currentTime
        
        if(out eq null) {
            out = new PrintStream(fileName)
            out.println("# Each line is a frame.")
            out.println("# 1 FPS instantaneous frame per second")
            out.println("# 2 Time in milliseconds of the frame")
        }
        
        val time = T2 - T1
        val fps  = 1000.0 / time
        out.println("%.2f   %d".format(fps, time))
    }
    
    /** Ensure the log file is flushed and closed. Be careful, calling `endFrame()`
      * after `close()` will reopen the log file and erase prior measurements. */
    def close() {
        if(out ne null) {
            out.flush
            out.close
            out = null
        }
    }
}