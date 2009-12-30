package org.graphstream.ui.j2dviewer.util

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.{File, IOException}
import java.net.URL
import scala.collection.mutable.HashMap

import javax.imageio.ImageIO;

/**
 * A simple cache for images to avoid reloading them constantly and to allow sharing.
 * 
 * TODO have a policy to release images if they have not been used for a given time.
 */
object ImageCache {
// Attribute
  
	/** The image cache. */
	protected val imageCache = new HashMap[String,BufferedImage]

	/** The dummy image used to mark a not found image (and avoid trying to reload it again and again). */
	protected var dummy:BufferedImage = null

// Construction
 
 	init
 
	protected def init() {
		val img = new BufferedImage( 16, 16, BufferedImage.TYPE_INT_RGB )
		val g2  = img.createGraphics
		
		g2.setColor( Color.RED )
		g2.drawRect( 0, 0, img.getWidth()-1, img.getHeight()-1 )
		g2.drawLine( 0, 0, img.getWidth()-1, img.getHeight()-1 )
		g2.drawLine( 0, img.getHeight()-1, img.getWidth()-1, 0 )
		
		dummy = img
	}
	
	/**
	 * Lookup an image based on its name, if found return it, else try to load it. If an image
	 * is not found once, the cache remembers it and will not try to reload it again if the
	 * same image is requested anew.
	 * @param fileNameOrUrl A file name or an URL pointing at the image.
	 * @return An image or null if the image cannot be found.
	 */
	def loadImage( fileNameOrUrl:String ):Option[BufferedImage] = loadImage( fileNameOrUrl, false )
	
	/**
	 * The same as {@link #loadImage(String)} but you can force the cache to try to reload
	 * an image that where not found before.
	 * @param fileNameOrUrl A file name or an URL pointing at the image.
	 * @param forceTryReload If true, try to reload an image that where not found before.
	 * @return An image or null if the image cannot be found.
	 */
	def loadImage( fileNameOrUrl:String, forceTryReload:Boolean ):Option[BufferedImage] = {
		imageCache.get( fileNameOrUrl ) match {
			case None => {
				val url = ClassLoader.getSystemClassLoader.getResource( fileNameOrUrl )
				var image:BufferedImage = null
				
				if( url != null ) {			// The image is in the class path.
					var i:BufferedImage = null
					try {
						image = ImageIO.read( url )
						imageCache.put( fileNameOrUrl, image )
					} catch { case e => e.printStackTrace() }
				} else {					// The image is in a file or on the network.
					try {
						val url = new URL( fileNameOrUrl )
						
						image = ImageIO.read( url )	// Try the network.
						imageCache.put( fileNameOrUrl, image )
					} catch {
						case _ => {
							try {
								image = ImageIO.read( new File( fileNameOrUrl ) )	// Try the file.
								imageCache.put( fileNameOrUrl, image )
							} catch {
								case _ => {
									image = dummy
									imageCache.put( fileNameOrUrl, image )
									System.err.printf( "Cannot read image '%s'%n", fileNameOrUrl )
								}
							}
						}
					}
				}
    
				new Some[BufferedImage]( image )
			} 
			case (x:Some[_]) => {
				if( x.get == dummy && forceTryReload ) {
					imageCache -= fileNameOrUrl
					loadImage( fileNameOrUrl )
				} else x
			}  
		}
	}
	
	/**
	 * A dummy 16x16 image used when an image cannot be loaded or found. 
	 * @return An image.
	 */
	def dummyImage:Image = dummy
}