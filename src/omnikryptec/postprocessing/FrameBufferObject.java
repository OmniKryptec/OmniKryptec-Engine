
package omnikryptec.postprocessing;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import omnikryptec.display.GameSettings;

public class FrameBufferObject {

	private final int width;
	private final int height;

	private int frameBuffer;

	private int colourTexture;
	private int depthTexture;

	private int depthBuffer;
	private int[] colBuffers;
	
	private int multisample = GameSettings.NO_MULTISAMPLING;
	private boolean multitarget = false;
	private int[] targets;
	
	public static enum DepthbufferType {
		NONE, DEPTH_TEXTURE, DEPTH_RENDER_BUFFER;
	}

	/**
	 * Creates an FBO of a specified width and height, with the desired type of
	 * depth buffer attachment.
	 * 
	 * @param width
	 *            - the width of the FBO.
	 * @param height
	 *            - the height of the FBO.
	 * @param depthBufferType
	 *            - an int indicating the type of depth buffer attachment that
	 *            this FBO should use.
	 */
	public FrameBufferObject(int width, int height, DepthbufferType type, int...targets) {
		this.width = width;
		this.height = height;
		this.targets = targets;
		this.multitarget = targets.length>1;
		initialiseFrameBuffer(type);
	}
	
	public FrameBufferObject(int width, int height, DepthbufferType type){
		this(width, height, type, GL30.GL_COLOR_ATTACHMENT0);
	}
	
	/**
	 * only for the engine
	 * 
	 * @param width
	 * @param height
	 * @param multisamples
	 */
	public FrameBufferObject(int width, int height, int multisamples, int ...targets) {
		this.width = width;
		this.height = height;
		this.multisample = multisamples;
		this.targets = targets;
		this.multitarget = targets.length>1;
		initialiseFrameBuffer(DepthbufferType.DEPTH_RENDER_BUFFER);
	}
	
	public FrameBufferObject(int width, int height, int multisamples){
		this(width, height, multisamples, GL30.GL_COLOR_ATTACHMENT0);
	}
	
	
	public boolean isMultisampled(){
		return multisample != GameSettings.NO_MULTISAMPLING;
	}
	
	public boolean isMultitarget(){
		return multitarget;
	}
	
	public int[] getTargets(){
		return targets;
	}
	
	/**
	 * Deletes the frame buffer and its attachments when the game closes.
	 */
	public void cleanUp() {
		GL30.glDeleteFramebuffers(frameBuffer);
		GL11.glDeleteTextures(colourTexture);
		GL11.glDeleteTextures(depthTexture);
		GL30.glDeleteRenderbuffers(depthBuffer);
		for(int i=0; i<colBuffers.length; i++){
			GL30.glDeleteRenderbuffers(colBuffers[i]);
		}
	}

	/**
	 * Binds the frame buffer, setting it as the current render target. Anything
	 * rendered after this will be rendered to this FBO, and not to the screen.
	 */
	public void bindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
	}

	/**
	 * Unbinds the frame buffer, setting the default frame buffer as the current
	 * render target. Anything rendered after this will be rendered to the
	 * screen, and not this FBO.
	 */
	public void unbindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	/**
	 * Binds the current FBO to be read from (not used in tutorial 43).
	 */
	public void bindToRead(int attachment) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL11.glReadBuffer(attachment);
	}

	/**
	 * @return The ID of the texture containing the colour buffer of the FBO.
	 */
	public int getColourTexture() {
		return colourTexture;
	}

	/**
	 * @return The texture containing the FBOs depth buffer.
	 */
	public int getDepthTexture() {
		return depthTexture;
	}

	public void resolveToFbo(FrameBufferObject out, int attachment) {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, out.frameBuffer);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL11.glReadBuffer(attachment);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, out.width, out.height,
				GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		unbindFrameBuffer();
	}

	public void resolveToScreen() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, Display.getWidth(), Display.getHeight(),
				GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		unbindFrameBuffer();
	}

	/**
	 * Creates the FBO along with a colour buffer texture attachment, and
	 * possibly a depth buffer.
	 * 
	 * @param type
	 *            - the type of depth buffer attachment to be attached to the
	 *            FBO.
	 */
	private void initialiseFrameBuffer(DepthbufferType type) {
		colBuffers = new int[targets.length];
		createFrameBuffer();
		if (multisample != GameSettings.NO_MULTISAMPLING) {
			for(int i=0; i<targets.length; i++){
				createMultisampleColourAttachment(targets[i]);
			}
		} else {
			for(int i=0; i<targets.length; i++){
				createTextureAttachment(targets[i]);
			}
		}
		if (type == DepthbufferType.DEPTH_RENDER_BUFFER) {
			createDepthBufferAttachment();
		} else if (type == DepthbufferType.DEPTH_TEXTURE) {
			createDepthTextureAttachment();
		}
		unbindFrameBuffer();
	}

	/**
	 * Creates a new frame buffer object and sets the buffer to which drawing
	 * will occur - colour attachment 0. This is the attachment where the colour
	 * buffer texture is.
	 * 
	 */
	private void createFrameBuffer() {
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		determineDrawBuffers();
	}

	private void determineDrawBuffers(){
		IntBuffer drawBuffers = BufferUtils.createIntBuffer(targets.length);
		for(int i=0; i<targets.length; i++){
			drawBuffers.put(targets[i]);
		}
		drawBuffers.flip();
		GL20.glDrawBuffers(drawBuffers);
	}
	
	
	private int createMultisampleColourAttachment(int attachment) {
		int colourBuffer = GL30.glGenFramebuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, multisample, GL11.GL_RGBA8, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_RENDERBUFFER,
				colourBuffer);
		return colourBuffer;
	}

	/**
	 * Creates a texture and sets it as the colour buffer attachment for this
	 * FBO.
	 */
	private int createTextureAttachment(int attachment) {
		int colourTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL11.GL_TEXTURE_2D, colourTexture,
				0);
		return colourTexture;
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a texture, which can later
	 * be sampled.
	 */
	private void createDepthTextureAttachment() {
		depthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT,
				GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a render buffer. This can't
	 * be used for sampling in the shaders.
	 */
	private void createDepthBufferAttachment() {
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		if (multisample != GameSettings.NO_MULTISAMPLING) {
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, multisample, GL14.GL_DEPTH_COMPONENT24, width,
					height);
		} else {
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height);
		}
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBuffer);
	}

}
