/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.postprocessing.main;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.graphics.display.Display;
import de.omnikryptec.old.graphics.OpenGL;
import de.omnikryptec.old.resource.texture.Texture;
import de.omnikryptec.old.settings.GameSettings;
import de.omnikryptec.old.util.EnumCollection.DepthbufferType;
import de.omnikryptec.old.util.Instance;
import de.omnikryptec.old.util.logger.Logger;

public class FrameBufferObject extends Texture {

    private static List<FrameBufferObject> fbos = new ArrayList<>();
    private static ArrayDeque<FrameBufferObject> history = new ArrayDeque<>();

    private final int width;
    private final int height;

    private int frameBuffer;
    private int[] colBuffers;

    // private int depthTexture;
    private DepthTexture depthTexture = null;
    private int depthBuffer;

    private int multisample = GameSettings.NO_MULTISAMPLING;
    private boolean multitarget = false;

    private DepthbufferType type;
    private RenderTarget[] targets;

    public class DepthTexture extends Texture {

	private int depthTexture;

	private DepthTexture(int t) {
	    super("", true);
	    this.depthTexture = t;
	}

	@Override
	public DepthTexture delete() {
	    GL11.glDeleteTextures(depthTexture);
	    return this;
	}

	@Override
	protected void bindToUnit(int unit, int... info) {
	    OpenGL.gl13activeTextureZB(unit);
	    super.bindTexture(GL11.GL_TEXTURE_2D, depthTexture);
	}

	@Override
	public float getWidth() {
	    return width;
	}

	@Override
	public float getHeight() {
	    return height;
	}

    }

    /**
     * Creates an FBO of a specified width and height, with the desired type of
     * depth buffer attachment.
     *
     * @param width  - the width of the FBO.
     * @param height - the height of the FBO.
     * @param type   - an int indicating the type of depth buffer attachment that
     *               this FBO should use.
     */
    public FrameBufferObject(int width, int height, DepthbufferType type, RenderTarget... targets) {
	super("", true);
	this.width = width;
	this.height = height;
	this.targets = targets;
	this.multitarget = targets.length > 1;
	initialiseFrameBuffer(type);
    }

    public FrameBufferObject(int width, int height) {
	this(width, height, DepthbufferType.NONE);
    }

    public FrameBufferObject(int width, int height, DepthbufferType type) {
	this(width, height, type, new RenderTarget(GL30.GL_COLOR_ATTACHMENT0));
    }

    /**
     * only for the engine
     *
     * @param width
     * @param height
     * @param multisamples
     */
    public FrameBufferObject(int width, int height, int multisamples, RenderTarget... targets) {
	super("", true);
	this.width = width;
	this.height = height;
	this.multisample = multisamples;
	this.targets = targets;
	this.multitarget = targets.length > 1;
	initialiseFrameBuffer(DepthbufferType.DEPTH_RENDER_BUFFER);
    }

    public FrameBufferObject(int width, int height, int multisamples, RenderTarget[] add, RenderTarget... targets) {
	super("", true);
	this.width = width;
	this.height = height;
	this.multisample = multisamples;
	this.targets = new RenderTarget[add.length + targets.length];
	for (int i = 0; i < targets.length; i++) {
	    this.targets[i] = targets[i];
	}
	for (int i = 0; i < add.length; i++) {
	    this.targets[i + targets.length] = add[i];
	}
	this.multitarget = this.targets.length > 1;
	initialiseFrameBuffer(DepthbufferType.DEPTH_RENDER_BUFFER);
    }

    public FrameBufferObject(int width, int height, int multisamples) {
	this(width, height, multisamples, new RenderTarget(GL30.GL_COLOR_ATTACHMENT0));
    }

    public boolean isMultisampled() {
	return multisample != GameSettings.NO_MULTISAMPLING;
    }

    public boolean isMultitarget() {
	return multitarget;
    }

    public RenderTarget[] getTargets() {
	return targets;
    }

    /**
     * Deletes the frame buffer and its attachments when the game closes.
     */
    public FrameBufferObject delete() {
	if (type == DepthbufferType.DEPTH_RENDER_BUFFER) {
	    GL30.glDeleteRenderbuffers(depthBuffer);
	} else if (hasDepthTexture()) {
	    // depthTexture.delete(); //WHY THE FUCK DOES EVERYTHING FUCK UP WITH THIS?
	    depthTexture = null;
	}
	for (int i = 0; i < colBuffers.length; i++) {
	    if (multisample != GameSettings.NO_MULTISAMPLING) {
		GL30.glDeleteRenderbuffers(colBuffers[i]);
	    } else {
		GL11.glDeleteTextures(colBuffers[i]);
	    }
	}
	GL30.glDeleteFramebuffers(frameBuffer);
	return this;
    }

    /**
     * Binds the frame buffer, setting it as the current render target. Anything
     * rendered after this will be rendered to this FBO, and not to the screen.
     */
    public void bindFrameBuffer() {
	GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
	if (history.isEmpty()) {
	    OpenGL.gl11viewport(Display.calculateViewport(width, height));
	} else {
	    OpenGL.gl11viewport(0, 0, width, height);
	}
	history.push(this);
    }

    public static FrameBufferObject getBoundFBO() {
	return history.peek();
    }

    /**
     * Unbinds the frame buffer, setting the default frame buffer as the current
     * render target. Anything rendered after this will be rendered to the screen,
     * and not this FBO.
     */
    public void unbindFrameBuffer() {
	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	history.pop();
	if (!history.isEmpty()) {
	    history.pop().bindFrameBuffer();
	} else {
	    Display.setARViewPort();
	}
    }

    /**
     * Binds the current FBO to be read from (not used in tutorial 43).
     */
    public void bindToRead(int attachment) {
	Texture.unbindActive();
	GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
	GL11.glReadBuffer(attachment);
    }

    /**
     * @return The texture containing the FBOs depth buffer.
     */
    public DepthTexture getDepthTexture() {
	return depthTexture;
    }

    public boolean hasDepthTexture() {
	return depthTexture != null;
    }

    public DepthbufferType getDepthbufferType() {
	return type;
    }

    public int getTexture(int index) {
	if (multisample != GameSettings.NO_MULTISAMPLING) {
	    throw new IllegalStateException("This framebuffer is multisampled and has no textures.");
	}
	return colBuffers[index];
    }

    public void resolveToFbo(FrameBufferObject out, int attachment) {
	resolveToFbo(out, attachment, true);
    }

    public void resolveToFbo(FrameBufferObject out, int attachment, boolean depth) {
	GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, out.frameBuffer);
	GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
	GL11.glReadBuffer(attachment);
	GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, out.width, out.height,
		GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	if (depth) {
	    resolveDepth(out);
	}
    }

    public void resolveDepth(FrameBufferObject out) {
	GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, out.frameBuffer);
	GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
	GL11.glReadBuffer(GL30.GL_DEPTH_ATTACHMENT);
	GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, out.width, out.height, GL11.GL_DEPTH_BUFFER_BIT,
		GL11.GL_NEAREST);
	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void resolveToScreen() {
	GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
	GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
	GL11.glDrawBuffer(GL11.GL_BACK);
	GL30.glBlitFramebuffer(0, 0, width, height, Display.getViewportData()[0], Display.getViewportData()[1],
		Display.getViewportData()[2] + Display.getViewportData()[0],
		Display.getViewportData()[3] + Display.getViewportData()[1], GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    /**
     * Creates the FBO along with a colour buffer texture attachment, and possibly a
     * depth buffer.
     *
     * @param type - the type of depth buffer attachment to be attached to the FBO.
     */
    private void initialiseFrameBuffer(DepthbufferType type) {
	this.type = type;
	fbos.add(this);
	colBuffers = new int[targets.length];
	frameBuffer = GL30.glGenFramebuffers();
	bindFrameBuffer();
	determineDrawBuffers();
	if (multisample != GameSettings.NO_MULTISAMPLING) {
	    for (int i = 0; i < targets.length; i++) {
		colBuffers[i] = createMultisampleColourAttachment(targets[i].target, targets[i].extended); // ?
													   // GL30.GL_RGBA32F
													   // :
													   // GL11.GL_RGBA8);
	    }
	} else {
	    for (int i = 0; i < targets.length; i++) {
		colBuffers[i] = createTextureAttachment(targets[i].target, targets[i].extended); // ? GL30.GL_RGBA32F :
												 // GL11.GL_RGBA8);
	    }
	}
	if (type == DepthbufferType.DEPTH_RENDER_BUFFER) {
	    createDepthBufferAttachment();
	} else if (type == DepthbufferType.DEPTH_TEXTURE) {
	    createDepthTextureAttachment();
	}
	unbindFrameBuffer();
    }

    private void determineDrawBuffers() {
	IntBuffer drawBuffers = BufferUtils.createIntBuffer(targets.length);
	for (int i = 0; i < targets.length; i++) {
	    drawBuffers.put(targets[i].target);
	}
	drawBuffers.flip();
	GL20.glDrawBuffers(drawBuffers);
    }

    private int createMultisampleColourAttachment(int attachment, int level) {
	int colourBuffer = GL30.glGenRenderbuffers();
	GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer);
	GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, multisample, level, width, height);
	GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_RENDERBUFFER, colourBuffer);
	return colourBuffer;
    }

    /**
     * Creates a texture and sets it as the colour buffer attachment for this FBO.
     */
    private int createTextureAttachment(int attachment, int level) {
	int colourTexture = OpenGL.gl11genTextures();
	Texture.bindAndReset(GL11.GL_TEXTURE_2D, colourTexture);
	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, level, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
		(ByteBuffer) null);
	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL11.GL_TEXTURE_2D, colourTexture, 0);
	return colourTexture;
    }

    /**
     * Adds a depth buffer to the FBO in the form of a texture, which can later be
     * sampled.
     */
    private void createDepthTextureAttachment() {
	depthTexture = new DepthTexture(OpenGL.gl11genTextures());
	Texture.bindAndReset(GL11.GL_TEXTURE_2D, depthTexture.depthTexture);
	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT,
		GL11.GL_FLOAT, (ByteBuffer) null);
	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D,
		depthTexture.depthTexture, 0);
    }

    /**
     * Adds a depth buffer to the FBO in the form of a render buffer. This can't be
     * used for sampling in the shaders.
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

    private static final int[] ZERO_ARRAY = { 0 };

    /**
     * info[0] is the attachmentindex to use
     */
    @Override
    protected void bindToUnit(int unit, int... info) {
	if (info == null || info.length == 0) {
	    info = ZERO_ARRAY;
	}
	OpenGL.gl13activeTextureZB(unit);
	super.bindTexture(GL11.GL_TEXTURE_2D, getTexture(info[0]));
    }

    public static void cleanup() {
	for (int i = 0; i < fbos.size(); i++) {
	    fbos.get(i).delete();
	}
	fbos.clear();
	history.clear();
    }

    public final BufferedImage toBufferedImage() {
	return toBufferedImage(true);
    }

    public final BufferedImage toBufferedImage(boolean withTransparency) {
	// if(multisample != GameSettings.NO_MULTISAMPLING) {
	// throw new UnsupportedOperationException("Multisampled FBOs are not
	// supported!"); //DONE Weil muessen erst auf ein einziges gerendert werden
	// }
	FrameBufferObject my = new FrameBufferObject(this.width, this.height, DepthbufferType.NONE);
	this.resolveToFbo(my, GL30.GL_COLOR_ATTACHMENT0);
	try {
	    final FloatBuffer buffer = BufferUtils.createFloatBuffer(width * height * (withTransparency ? 4 : 3));
	    my.bindToRead(GL30.GL_COLOR_ATTACHMENT0);
	    GL11.glReadPixels(0, 0, width, height, (withTransparency ? GL11.GL_RGBA : GL11.GL_RGB), GL11.GL_FLOAT,
		    buffer);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	    buffer.rewind();
	    final int[] rgbArray = new int[width * height];
	    for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
		    final int r = (int) (buffer.get() * 255) << 16;
		    final int g = (int) (buffer.get() * 255) << 8;
		    final int b = (int) (buffer.get() * 255);
		    int a = (255 << 24);
		    if (withTransparency) {
			a = (int) (buffer.get() * 255) << 24;
		    }
		    final int i = ((height - 1) - y) * width + x;
		    rgbArray[i] = (r + g + b + a);
		}
	    }
	    final BufferedImage image = new BufferedImage(width, height,
		    (withTransparency ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB));
	    image.setRGB(0, 0, width, height, rgbArray, 0, width);
	    return image;
	} catch (Exception ex) {
	    if (Logger.isDebugMode()) {
		Logger.logErr("Error while converting FBO to BufferedImage: " + ex, ex);
	    }
	    return null;
	}
    }

    public final boolean saveAsScreenshotInFolder(AdvancedFile folder) {
	return saveAsScreenshotInFolder(folder, "screenshot");
    }

    public final boolean saveAsScreenshotInFolder(AdvancedFile folder, String name) {
	return saveAsScreenshotInFolder(folder, name, "png");
    }

    public final boolean saveAsScreenshotInFolder(AdvancedFile folder, String name, String format) {
	return saveAsScreenshotInFolder(folder, name, format, false);
    }

    public final boolean saveAsScreenshotInFolder(AdvancedFile folder, String name, String format,
	    boolean withTimestamp) {
	return saveAsScreenshotInFolder(folder, name, format, withTimestamp, true);
    }

    public final boolean saveAsScreenshotInFolder(AdvancedFile folder, String name, String format,
	    boolean withTimestamp, boolean withTransparency) {
	AdvancedFile file = null;
	if (withTimestamp) {
	    file = new AdvancedFile(false, folder, String.format("%s_%s.%s", name,
		    LocalDateTime.now().format(Instance.DATETIMEFORMAT_TIMESTAMP), format));
	} else {
	    int i = 1;
	    while (file == null || file.exists()) {
		file = new AdvancedFile(false, folder, String.format("%s_%d.%s", name, i, format));
		i++;
	    }
	}
	return saveAsScreenshot(file, format, withTransparency);
    }

    public final boolean saveAsScreenshot(AdvancedFile file) {
	return saveAsScreenshot(file, "png");
    }

    public final boolean saveAsScreenshot(AdvancedFile file, String format) {
	return saveAsScreenshot(file, format, true);
    }

    public final boolean saveAsScreenshot(AdvancedFile file, String format, boolean withTransparency) {
	try {
	    final BufferedImage image = toBufferedImage(withTransparency);
	    if (image != null) {
		file.createAdvancedFile();
		if (!file.exists() || !file.isFile()) {
		    return false;
		}
		ImageIO.write(image, format, file.createOutputstream(false));
		return true;
	    } else {
		return false;
	    }
	} catch (Exception ex) {
	    if (Logger.isDebugMode()) {
		Logger.logErr("Error while saving Screenshot: " + ex, ex);
	    }
	    return false;
	}
    }

    @Override
    public float getWidth() {
	return width;
    }

    @Override
    public float getHeight() {
	return height;
    }

}
