package de.omnikryptec.libapi.opengl.framebuffer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;

import de.omnikryptec.libapi.exposed.Deletable;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.FrameBufferStack;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.texture.GLTexture;

public class GLFrameBuffer extends FrameBuffer {
    
    private static int multisampler = 0;
    
    private static void enableMultisamplingIfFirst() {
        if (multisampler == 0) {
            OpenGLUtil.setMultisample(true);
        }
        multisampler++;
    }
    
    private static void disableMultisamplingIfLast() {
        multisampler--;
        if (multisampler == 0) {
            OpenGLUtil.setMultisample(false);
        }
    }
    
    private final int width;
    private final int height;
    
    private final int multisample;
    private final FBTarget[] targets;
    
    private FBTexture[] textures;
    private int[] renderbuffers;
    
    private final int pointer;
    
    public GLFrameBuffer(final int width, final int height, final int multisample, final int targets,
            FrameBufferStack stack) {
        super(stack);
        this.pointer = GL30.glGenFramebuffers();
        this.width = width;
        this.height = height;
        this.multisample = multisample;
        this.targets = new FBTarget[targets];
        if (multisample == 0) {
            this.textures = new FBTexture[targets];
        } else {
            enableMultisamplingIfFirst();
            this.renderbuffers = new int[targets];
        }
    }
    
    @Override
    public void assignTarget(final int index, final FBTarget target) {
        this.targets[index] = target;
        final IntBuffer drawBuffers = BufferUtils.createIntBuffer(this.targets.length);
        for (int i = 0; i < this.targets.length; i++) {
            if (this.targets[i] != null && !this.targets[i].isDepthAttachment) {
                drawBuffers.put(OpenGLUtil.indexToAttachment(this.targets[i].attachmentIndex));
            }
        }
        drawBuffers.flip();
        GL20.glDrawBuffers(drawBuffers);
        if (isRenderBuffer()) {
            final int colorBuffer = GL30.glGenRenderbuffers();
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colorBuffer);
            GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, this.multisample,
                    OpenGLUtil.textureFormatId(target.format), this.width, this.height);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, OpenGLUtil.indexToAttachment(target.attachmentIndex),
                    GL30.GL_RENDERBUFFER, colorBuffer);
            this.renderbuffers[index] = colorBuffer;
        } else {
            final FBTexture texture = new FBTexture();
            texture.bindTexture(0);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, OpenGLUtil.textureFormatId(target.format), this.width, this.height,
                    0, target.isDepthAttachment ? GL11.GL_DEPTH_COMPONENT : GL11.GL_RGBA,
                    target.isDepthAttachment ? GL11.GL_FLOAT : GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, OpenGLUtil.indexToAttachment(target.attachmentIndex),
                    GL11.GL_TEXTURE_2D, texture.textureId(), 0);
            this.textures[index] = texture;
        }
    }
    
    @Override
    protected void bindRaw() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.pointer);
        GL11.glViewport(0, 0, this.width, this.height);
    }
    
    public void bindImageTexture(int imageUnit, int texIndex, int level, boolean layered, int layer, int access,
            FBAttachmentFormat format) {
        GL42.glBindImageTexture(imageUnit, textures[texIndex].pointer, level, layered, layer, GL15.GL_READ_WRITE,
                OpenGLUtil.textureFormatId(format));
        OpenGLUtil.flushErrors();
    }
    
    @Override
    public boolean isRenderBuffer() {
        return this.textures == null;
    }
    
    @Override
    public Texture getTexture(final int i) {
        return this.textures[i];
    }
    
    @Override
    public void deleteRaw() {
        if (isRenderBuffer()) {
            for (int i = 0; i < this.renderbuffers.length; i++) {
                deleteRenderBuffer(i);
            }
        } else {
            for (int i = 0; i < this.textures.length; i++) {
                deleteTexture(i);
            }
        }
        GL30.glDeleteFramebuffers(this.pointer);
        if (multisample > 0) {
            disableMultisamplingIfLast();
        }
    }
    
    private void deleteRenderBuffer(final int index) {
        GL30.glDeleteRenderbuffers(this.renderbuffers[index]);
    }
    
    private void deleteTexture(final int index) {
        final FBTexture t = this.textures[index];
        if (t != null) {
            t.deleteAndUnregister();
        }
    }
    
    private class FBTexture extends GLTexture {
        
        private FBTexture() {
            super(GL11.GL_TEXTURE_2D);
        }
        
        @Override
        public float getWidth() {
            return GLFrameBuffer.this.width;
        }
        
        @Override
        public float getHeight() {
            return GLFrameBuffer.this.height;
        }
        
    }
    
    @Override
    public void resolveToFrameBuffer(final FrameBuffer target, final int attachment) {
        target.bindAsTmp();
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.pointer);
        GL11.glReadBuffer(OpenGLUtil.indexToAttachment(attachment));
        int tx = 0;
        int ty = 0;
        int tw = target.getWidth();
        int th = target.getHeight();
        if (target instanceof GLScreenBuffer) {
            GL11.glDrawBuffer(GL11.GL_BACK);
            GLScreenBuffer s = (GLScreenBuffer) target;
            tx = s.viewport[0];
            ty = s.viewport[1];
        }
        GL30.glBlitFramebuffer(0, 0, this.width, this.height, tx, ty, tx + tw, ty + th,
                OpenGLUtil.indexToBufferBit(attachment), GL11.GL_NEAREST);
        target.unbindAsTmp();
    }
    
    @Override
    public FBTarget[] targets() {
        return this.targets.clone();
    }
    
    @Override
    public int multisamples() {
        return this.multisample;
    }
    
    @Override
    public int targetCount() {
        return this.targets.length;
    }
    
    @Override
    public FrameBuffer resizedClone(final int newWidth, final int newHeight) {
        final FrameBuffer fb = new GLFrameBuffer(newWidth, newHeight, this.multisample, this.targets.length,
                this.stack);
        fb.assignTargetsB(this.targets);
        return fb;
    }
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
    
    @Override
    public void clear(float r, float g, float b, float a, SurfaceBufferType... types) {
        bindAsTmp();
        OpenGLUtil.setClearColor(r, g, b, a);
        OpenGLUtil.clear(types);
        unbindAsTmp();
    }
}
