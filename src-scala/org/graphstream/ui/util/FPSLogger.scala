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