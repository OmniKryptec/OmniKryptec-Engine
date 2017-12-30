package omnikryptec.graphics;

import java.nio.FloatBuffer;

import org.joml.Math;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.Sprite;
import omnikryptec.resource.model.VertexArrayObject;
import omnikryptec.resource.texture.Texture;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.files.render.Shader2D;
import omnikryptec.util.Color;
import omnikryptec.util.exceptions.OmniKryptecException;

public class SpriteBatch {
	public static final int FLOATS_PER_VERTEX = 8;
	private static final int[] elementLengths = { 2, 4, 2 };
	private static final int vertices = 6;

	private static Shader2D defShaderInst;

	private static Shader2D defaultShader() {
		if (defShaderInst == null) {
			defShaderInst = new Shader2D();
		}
		return defShaderInst;
	}

	private FloatBuffer buffer;
	private Shader program;

	private VertexArrayObject vao;
	private int vertexcount = 0;
	private int drawcalls = 0;
	private int idx = 0;
	private int max = 0;
	private boolean drawing = false;
	private boolean caching = false;
	private Color color = new Color(1, 1, 1, 1);
	private Camera camera;
	private Texture texCur;
	
	public SpriteBatch(int size) {
		this(null, size);
	}
	
	public SpriteBatch() {
		this(null);
	}
	
	public SpriteBatch(Camera cam) {
		this(cam, 1000);
	}

	public SpriteBatch(Camera cam, int size) {
		this(cam, defaultShader(), size);
	}

	public SpriteBatch(Camera cam, Shader program, int size) {
		this.program = program;
		this.camera = cam;
		vao = VertexArrayObject.create();
		max = size * vertices;
		buffer = BufferUtils.createFloatBuffer(FLOATS_PER_VERTEX * max);
	}

	public Camera getCamera() {
		return camera;
	}

	public SpriteBatch setCamera(Camera cam) {
		if (drawing) {
			throw new OmniKryptecException("Can't change the camera while rendering!");
		} else {
			this.camera = cam;
		}
		return this;
	}

	public void drawTest() {
		vertex(0, 0, 1, 1, 1, 1, 0, 0);
		vertex(1, 0, 1, 1, 1, 1, 1, 0);
		vertex(1, 1, 1, 1, 1, 1, 1, 1);
	}

	private void checkFlush(Texture t) {
		if(idx >= max || t != texCur) {
			flush();
		}
		if(t!=null) {
			t.bindToUnitOptimized(0);
			texCur = t;
		}
	}

	public void begin() {
		begin(false);
	}
	
	public void begin(boolean onlyCache) {
		if (drawing||caching) {
			throw new OmniKryptecException("Can't start started rendering!");
		}
		idx = 0;
		vertexcount = 0;
		drawcalls = 0;
		if(!onlyCache) {
			GraphicsUtil.enableDepthTesting(false);
			program.start();
			program.onDrawBatchStart(this);
			drawing = true;
		}
		caching = true;
	}

	public void end() {
		if (!caching) {
			throw new OmniKryptecException("Can't stop stopped rendering!");
		}
		if(drawing) {
			flush();
			program.onDrawBatchEnd(this);
			GraphicsUtil.enableDepthTesting(true);
			drawing = false;
		}
		caching = false;
	}

	public void changeShader(Shader sh) {
		if(drawing) {
			flush();
			program.onDrawBatchEnd(this);
		}
		program = sh;
		if (drawing) {
			program.start();
			program.onDrawBatchStart(this);
		}
	}

	public Color color() {
		return color;
	}

	public void flush() {
		if (idx > 0 && drawing) {
			buffer.flip();
			vao.bind();
			vao.storeBufferf(idx, elementLengths, buffer);
			render();
			idx = 0;
			buffer.clear();
		}
	}

	private void render() {
		drawcalls++;
		vao.bind(0, 1, 2);
		OpenGL.gl11drawArrays(GL11.GL_TRIANGLES, 0, idx);
	}

	private void vertex(float x, float y, float r, float g, float b, float a, float u, float v) {
		if (!caching) {
			throw new OmniKryptecException("Rendering or caching is not active!");
		}
		vertexcount++;
		idx++;
		buffer.put(x).put(y).put(r).put(g).put(b).put(a).put(u).put(v);
	}

	public void drawPolygon(Texture t, float[] data, int vertexcount) {
		drawPolygon(t, data, 0, data.length, vertexcount);
	}

	public void drawPolygon(Texture t, float[] data, int start, int len, int vertexcount) {
		if((len-start)%FLOATS_PER_VERTEX!=0) {
			throw new OmniKryptecException("Floats per vertex are not correct.");
		}
		if(vertexcount>max) {
			throw new OmniKryptecException("Too many vertices!");
		}
		checkFlush(t);
		if (idx + vertexcount >= max) {
			flush();
		}
		idx += vertexcount;
		this.vertexcount += vertexcount;
		buffer.put(data, start, len);
	}

	public float[] getData() {
		buffer.flip();
		float[] array = new float[buffer.limit()];
		buffer.get(array);
		idx = 0;
		buffer.clear();
		return array;
	}

	public void draw(Sprite s){
		draw(s, false);
	}
	
	public void draw(Sprite s, boolean center) {
		if(s!=null&&s.getTexture()!=null) {
			color.setFrom(s.getColor());
			scaledWidth = s.getWidth();
			scaledHeight = s.getHeight();
			tmpPos = s.getTransform().getPosition(true);
			if(center){
				tmpPos.x -= scaledWidth/2;
				tmpPos.y -= scaledHeight/2;
			}
			draw(s.getTexture(), tmpPos.x, tmpPos.y, scaledWidth, 
					scaledHeight, scaledWidth*0.5f, scaledHeight*0.5f, s.getTransform().getRotation().x);
		}
	}
	
	public void draw(Texture tex, float x, float y) {
		draw(tex, x, y, tex.getWidth(), tex.getHeight());
	}

	public void draw(Texture tex, float x, float y, float originX, float originY, float rotationRadians) {
		draw(tex, x, y, tex.getWidth(), tex.getHeight(), originX, originY, rotationRadians);
	}

	private float x1, y1, x2, y2, x3, y3, x4, y4, scaleX, scaleY, cx, cy, p1x, p1y, p2x, p2y, p3x, p3y, p4x, p4y, r, g,
			b, a, u, v, u2, v2, scaledWidth, scaledHeight;
	private Vector2f tmpPos;
	
	public void draw(Texture tex, float x, float y, float width, float height) {
		draw(tex, x, y, width, height, x, y, 0f);
	}

	public void draw(Texture tex, float x, float y, float u, float v, float u2, float v2) {
		draw(tex, x, y, tex.getWidth(), tex.getHeight(), u, v, u2, v2);
	}

	public void draw(Texture tex, float x, float y, float width, float height, float u, float v, float u2, float v2) {
		draw(tex, x, y, width, height, x, y, 0, u, v, u2, v2);
	}

	public void draw(Texture tex, float x, float y, float width, float height, float originX, float originY,
			float rotationRadians) {
		if (tex != null) {
			u = tex.getUVs()[0];
			v = tex.getUVs()[1];
			u2 = tex.getUVs()[2];
			v2 = tex.getUVs()[3];
		} else {
			u = -1;
			v = -1;
			u2 = -1;
			v2 = -1;
		}
		draw(tex, x, y, width, height, originX, originY, rotationRadians, u, v, u2, v2);
	}

	public void draw(Texture tex, float x, float y, float width, float height, float originX, float originY,
			float rotationRadians, float u, float v, float u2, float v2) {
		checkFlush(tex);
		r = color.getR();
		g = color.getG();
		b = color.getB();
		a = color.getA();
		if (rotationRadians != 0) {
			scaleX = 1f;// width/tex.getWidth();
			scaleY = 1f;// height/tex.getHeight();

			cx = originX * scaleX;
			cy = originY * scaleY;

			p1x = -cx;
			p1y = -cy;
			p2x = width - cx;
			p2y = -cy;
			p3x = width - cx;
			p3y = height - cy;
			p4x = -cx;
			p4y = height - cy;

			final float cos = (float) Math.cos(rotationRadians);
			final float sin = (float) Math.sin(rotationRadians);

			x1 = x + (cos * p1x - sin * p1y) + cx; // TOP LEFT
			y1 = y + (sin * p1x + cos * p1y) + cy;
			x2 = x + (cos * p2x - sin * p2y) + cx; // TOP RIGHT
			y2 = y + (sin * p2x + cos * p2y) + cy;
			x3 = x + (cos * p3x - sin * p3y) + cx; // BOTTOM RIGHT
			y3 = y + (sin * p3x + cos * p3y) + cy;
			x4 = x + (cos * p4x - sin * p4y) + cx; // BOTTOM LEFT
			y4 = y + (sin * p4x + cos * p4y) + cy;
		} else {
			//Topleft
			x1 = x;
			y1 = y;
			//TopRight
			x2 = x + width;
			y2 = y;
			//BottomRight
			x3 = x + width;
			y3 = y + height;
			//BottomLeft
			x4 = x;
			y4 = y + height;
		}

		// top left, top right, bottom left
		vertex(x1, y1, r, g, b, a, u, v);
		vertex(x2, y2, r, g, b, a, u2, v);
		vertex(x4, y4, r, g, b, a, u, v2);

		// top right, bottom right, bottom left
		vertex(x2, y2, r, g, b, a, u2, v);
		vertex(x3, y3, r, g, b, a, u2, v2);
		vertex(x4, y4, r, g, b, a, u, v2);
	}

	public void fillRect(float x, float y, float width, float height) {
		draw(null, x, y, width, height);
	}

	public void drawRect(float x, float y, float width, float height) {
		drawRect(x, y, width, height, 1);
	}

	public void drawRect(float x, float y, float width, float height, float thickness) {
		draw(null, x, y, width, thickness);
		draw(null, x, y, thickness, height);
		draw(null, x + width - thickness, y, thickness, height);
		draw(null, x, y + height - thickness, width, thickness);
	}

	public void fill3DRect(float x, float y, float width, float height, float shadowOffset) {
		float[] cur = color.getArray();
		color.set(0, 0, 0, 1);
		fillRect(x + shadowOffset, y + shadowOffset, width, height);
		color.setFrom(cur);
		fillRect(x, y, width, height);
	}

	public void drawLine(float x1, float y1, float x2, float y2) {
		drawLine(x1, y1, x2, y2, 1);
	}

	public void drawLine(float x1, float y1, float x2, float y2, float thickness) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		float rad = (float) Math.atan2(dx, dy);
		draw(null, x1, y1, dist, thickness, 0, 0, rad);
	}

	public int getDrawCalls() {
		return drawcalls;
	}

	public int getVertexCount() {
		return vertexcount;
	}

	public boolean isDrawing() {
		return drawing;
	}

	public void clear() {
		if(caching) {
			buffer.clear();
			idx = 0;
		}
	}

}
