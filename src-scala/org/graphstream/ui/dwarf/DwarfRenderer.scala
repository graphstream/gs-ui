package org.graphstream.ui.dwarf

import org.graphstream.ui.swingViewer.GraphRendererBase
import org.graphstream.ui.swingViewer.LayerRenderer
import org.graphstream.ui.graphicGraph.GraphicGraph
import java.awt.Container
import java.util.ArrayList
import org.graphstream.ui.graphicGraph.GraphicElement
import org.graphstream.ui.swingViewer.util.Camera
import org.graphstream.ui.graphicGraph.GraphicElement.Skeleton
import org.graphstream.ui.swingViewer.basicRenderer.skeletons.NodeSkeleton
import org.graphstream.ui.swingViewer.basicRenderer.skeletons.EdgeSkeleton
import org.graphstream.ui.swingViewer.basicRenderer.skeletons.SpriteSkeleton
import org.graphstream.ui.swingViewer.util.FPSLogger
import java.awt.Graphics2D

class DwarfRenderer extends GraphRendererBase {
	/** Set the view on the view port defined by the metrics. */
	protected val camera = new DwarfCamera

	/** Specific renderer for nodes. */
	protected val nodeRenderer = new NodeRenderer

	/** Specific renderer for edges. */
	protected val edgeRenderer = new EdgeRenderer

	/** Specific renderer for sprites. */
	protected val spriteRenderer = new SpriteRenderer

	/** Render the background of the graph, before anything is drawn. */
	protected var backRenderer:LayerRenderer = null;

	/** Render the foreground of the graph, after anything is drawn. */
	protected var foreRenderer:LayerRenderer = null;

	/** Optional output log of the frame-per-second. */
	protected var fpsLog:FPSLogger = null;
	
	def open(graph:GraphicGraph, renderingSurface:Container):Unit = {
		super.open(graph, renderingSurface)
		graph.setSkeletonFactory(new DwarfSkeletonFactory)
	}

	def close():Unit = {
		if(fpsLog eq null) {
			fpsLog.close
			fpsLog = null
		}
		
		graph.setSkeletonFactory(null)
		super.close
	}

	def getCamera():Camera = camera

	def allNodesOrSpritesIn(x1:Double, y1:Double, x2:Double, y2:Double):ArrayList[GraphicElement] = camera.allNodesOrSpritesIn(graph, x1, y1, x2, y2)

	def findNodeOrSpriteAt(x:Double, y:Double):GraphicElement = camera.findNodeOrSpriteAt(graph, x, y)

	def render(g:Graphics2D, width:Int, height:Int):Unit = {
		if (graph eq null) {
			beginFrame
			
			if (camera.getGraphViewport eq null && camera.getMetrics.diagonal == 0
			&& (graph.getNodeCount == 0 && graph.getSpriteCount == 0)) {
				displayNothingToDo(g, width, height)
			} else {
				camera.setPadding(graph)
				camera.setViewport(width, height)
				renderGraph(g)
				renderSelection(g)
			}
			
			endFrame
		}
	}

	/** Create or remove the FPS logger and start measuring time if activated. */
	protected def beginFrame() {
		if(graph.hasLabel("ui.log")) {
			if(fpsLog eq null) {
				fpsLog = new FPSLogger(graph.getLabel("ui.log").toString)
			}
		} else {
			if(fpsLog ne null) {
				fpsLog.close
				fpsLog = null;
			}
		}
		
		if(fpsLog != null) {
			fpsLog.beginFrame
		}
	}
	
	/** End measuring frame time. */
	protected def endFrame() {
		if(fpsLog eq null) {
			fpsLog.endFrame
		}
	}

	def moveElementAtPx(element:GraphicElement, x:Double, y:Double) {
		val p = camera.transformPxToGu(x, y)
		element.move(p.x, p.y, element.getCenter.z)
	}

	/** Render the whole graph. */
	protected def renderGraph(g:Graphics2D) {
		setupGraphics(g)
		renderGraphBackground(g)
		renderBackLayer(g)
		camera.pushView(graph, g)
		renderGraphElements(g)
		renderGraphForeground(g)
		camera.popView(g)
		renderForeLayer(g)
	}

	protected void setupGraphics(Graphics2D g) {
		// XXX we do this at each frame !!! Why not doing this only when it changes !!! XXX
		if (graph.hasAttribute("ui.antialias")) {
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		if (graph.hasAttribute("ui.quality")) {
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		} else {
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		}
	}

	/**
	 * Render the background of the graph. This merely colors the background with the fill color.
	 */
	protected void renderGraphBackground(Graphics2D g) {
		StyleGroup group = graph.getStyle();

		g.setColor(group.getFillColor(0));
		g.fillRect(0, 0,
				(int) camera.getMetrics().viewport.data[0],
				(int) camera.getMetrics().viewport.data[1]);
	}

	/**
	 * Render the foreground of the graph. This draws a border if there is a stroke around the
	 * graph.
	 */
	protected void renderGraphForeground(Graphics2D g) {
		StyleGroup style = graph.getStyle();
		Rectangle2D rect = new Rectangle2D.Double();
		GraphMetrics metrics = camera.getMetrics();
		double px1 = metrics.px1;
		Value stroke = style.getShadowWidth();
		
		if (style.getStrokeMode() != StyleConstants.StrokeMode.NONE
		 && style.getStrokeWidth().value != 0) {
			rect.setFrame(metrics.lo.x, metrics.lo.y + px1, metrics.size.data[0] - px1, metrics.size.data[1] - px1);
			g.setStroke(new BasicStroke((float)metrics.lengthToGu(stroke)));
			g.setColor(graph.getStyle().getStrokeColor(0));
			g.draw(rect);
		}
	}
	
	/**
	 * Render each element of the graph.
	 */
	protected void renderGraphElements(Graphics2D g) {
		StyleGroupSet sgs = graph.getStyleGroups();

		if (sgs != null) {
			for (HashSet<StyleGroup> groups : sgs.zIndex()) {
				for (StyleGroup group : groups) {
					renderGroup(g, group);
				}
			}
		}
	}

	/**
	 * Render a style group.
	 */
	protected void renderGroup(Graphics2D g, StyleGroup group) {
		switch (group.getType()) {
			case NODE:   nodeRenderer.render(group, g, camera);   break;
			case EDGE:   edgeRenderer.render(group, g, camera);   break;
			case SPRITE: spriteRenderer.render(group, g, camera); break;
		}
	}

	protected void renderSelection(Graphics2D g) {
		if (selection != null && selection.x1 != selection.x2 && selection.y1 != selection.y2) {
			double t;
			double x1 = selection.x1;
			double y1 = selection.y1;
			double x2 = selection.x2;
			double y2 = selection.y2;
			double w  = camera.getMetrics().viewport.data[0];
			double h  = camera.getMetrics().viewport.data[1];

			if (x1 > x2) {
				t  = x1;
				x1 = x2;
				x2 = t;
			}
			if (y1 > y2) {
				t  = y1;
				y1 = y2;
				y2 = t;
			}

			Stroke s = g.getStroke();

			g.setStroke(new BasicStroke(1));
			g.setColor(new Color(222, 222, 222));
			g.drawLine(0, (int) y1, (int) w, (int) y1);
			g.drawLine(0, (int) y2, (int) w, (int) y2);
			g.drawLine((int) x1, 0, (int) x1, (int) h);
			g.drawLine((int) x2, 0, (int) x2, (int) h);
			g.setColor(new Color(250, 200, 0));
			g.drawRect((int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1));
			g.setStroke(s);
		}
	}

	protected void renderBackLayer(Graphics2D g) {
		if (backRenderer != null)
			renderLayer(g, backRenderer);
	}

	protected void renderForeLayer(Graphics2D g) {
		if (foreRenderer != null)
			renderLayer(g, foreRenderer);
	}

	protected void renderLayer(Graphics2D g, LayerRenderer renderer) {
		GraphMetrics metrics = camera.getMetrics();

		renderer.render(g, graph, metrics.ratioPx2Gu,
				(int) metrics.viewport.data[0], (int) metrics.viewport.data[1],
				metrics.loVisible.x, metrics.loVisible.y, metrics.hiVisible.x,
				metrics.hiVisible.y);
	}

	/**
	 * Show the center, the low and high points of the graph, and the visible
	 * area (that should always map to the window borders).
	 */
	protected void debugVisibleArea(Graphics2D g) {
		Rectangle2D rect = new Rectangle2D.Double();
		GraphMetrics metrics = camera.getMetrics();

		double x = metrics.loVisible.x;
		double y = metrics.loVisible.y;
		double w =  Math.abs(metrics.hiVisible.x - x);
		double h =  Math.abs(metrics.hiVisible.y - y);

		rect.setFrame(x, y, w, h);
		g.setStroke(new BasicStroke((float)(metrics.px1 * 4)));
		g.setColor(Color.RED);
		g.draw(rect);

		g.setColor(Color.BLUE);
		Ellipse2D ellipse = new Ellipse2D.Double();
		double px1 = metrics.px1;
		ellipse.setFrame(camera.getViewCenter().x - 3 * px1, camera.getViewCenter().y - 3 * px1, px1 * 6, px1 * 6);
		g.fill(ellipse);
		ellipse.setFrame(metrics.lo.x - 3 * px1, metrics.lo.y - 3 * px1, px1 * 6, px1 * 6);
		g.fill(ellipse);
		ellipse.setFrame(metrics.hi.x - 3 * px1, metrics.hi.y - 3 * px1, px1 * 6, px1 * 6);
		g.fill(ellipse);
	}

	public void screenshot(String filename, int width, int height) {
		if (graph != null) {
			if (filename.toLowerCase().endsWith("png")) {
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				renderGraph(img.createGraphics());

				File file = new File(filename);
				try {
					ImageIO.write(img, "png", file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (filename.toLowerCase().endsWith("bmp")) {
				BufferedImage img = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				renderGraph(img.createGraphics());

				File file = new File(filename);
				try {
					ImageIO.write(img, "bmp", file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {// if (filename.toLowerCase().endsWith("jpg") || filename.toLowerCase().endsWith("jpeg")) {
				BufferedImage img = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				renderGraph(img.createGraphics());

				File file = new File(filename);
				try {
					ImageIO.write(img, "jpg", file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setBackLayerRenderer(LayerRenderer renderer) {
		backRenderer = renderer;
	}

	public void setForeLayoutRenderer(LayerRenderer renderer) {
		foreRenderer = renderer;
	}

	// Style Group Listener

	public void elementStyleChanged(Element element, StyleGroup oldStyle,
			StyleGroup style) {
	}
	
	/** Factory for the skeletons adapted to this renderer. */
	class DwarfSkeletonFactory extends GraphicGraph.SkeletonFactory {
		def newNodeSkeleton():Skeleton = new NodeSkeleton

		def newEdgeSkeleton():Skeleton = new EdgeSkeleton

		def newSpriteSkeleton():Skeleton = new SpriteSkeleton
	}
}