package de.omnikryptec.libapi.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import de.omnikryptec.libapi.exposed.AutoDelete;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.opengl.texture.GLTexture;

public class GLFrameBuffer extends AutoDelete implements FrameBuffer {
    
    private static final Deque<GLFrameBuffer> history = new ArrayDeque<>();
    
    private final int width;
    private final int height;
    
    private final int multisample;
    private final FBTarget[] targets;
    
    private FBTexture[] textures;
    private int[] renderbuffers;
    
    private final int pointer;
    
    public GLFrameBuffer(final int width, final int height, final int multisample, final int targets) {
        this.pointer = GL30.glGenFramebuffers();
        this.width = width;
        this.height = height;
        this.multisample = multisample;
        this.targets = new FBTarget[targets];
        if (multisample == 0) {
            this.textures = new FBTexture[targets];
        } else {
            this.renderbuffers = new int[targets];
        }
    }
    
    @Override
    public void assignTarget(final int index, final FBTarget target) {
        this.targets[index] = target;
        final IntBuffer drawBuffers = BufferUtils.createIntBuffer(this.targets.length);
        for (int i = 0; i < this.targets.length; i++) {
            if (this.targets[i] != null && !this.targets[i].isDepthAttachment) {
                drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0 + this.targets[i].attachmentIndex);
            }
        }
        drawBuffers.flip();
        GL20.glDrawBuffers(drawBuffers);
        if (isRenderBuffer()) {
            final int colorBuffer = GL30.glGenRenderbuffers();
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colorBuffer);
            GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, this.multisample,
                    OpenGLUtil.typeId(target.format), this.width, this.height);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment(target), GL30.GL_RENDERBUFFER, colorBuffer);
            this.renderbuffers[index] = colorBuffer;
        } else {
            final FBTexture texture = new FBTexture();
            texture.bindTexture(0);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, OpenGLUtil.typeId(target.format), this.width, this.height, 0,
                    target.isDepthAttachment ? GL11.GL_DEPTH_COMPONENT : GL11.GL_RGBA,
                    target.isDepthAttachment ? GL11.GL_FLOAT : GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment(target), GL11.GL_TEXTURE_2D,
                    texture.textureId(), 0);
            this.textures[index] = texture;
        }
        
    }
    
    private int attachment(final FBTarget target) {
        if (target.isDepthAttachment) {
            return GL30.GL_DEPTH_ATTACHMENT;
        }
        return GL30.GL_COLOR_ATTACHMENT0 + target.attachmentIndex;
    }
    
    @Override
    public void bindFrameBuffer() {
        if (this != history.peek()) {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.pointer);
            GL11.glViewport(0, 0, this.width, this.height);
            history.push(this);
        }
    }
    
    @Override
    public void unbindFrameBuffer() {
        if (history.peek() == this) {
            history.pop();
            if (history.size() == 0) {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
                RenderAPI.get().getWindow().refreshViewport();
            } else if (history.size() > 0) {
                final GLFrameBuffer before = history.pop();
                before.bindFrameBuffer();
            }
        }
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
    protected void deleteRaw() {
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
    }
    
    private void deleteRenderBuffer(final int index) {
        GL30.glDeleteRenderbuffers(this.renderbuffers[index]);
    }
    
    private void deleteTexture(final int index) {
        final FBTexture t = this.textures[index];
        if (t != null) {
            t.delete();
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
    public void resolveToScreen(int i) {
        GLFrameBuffer last = null;
        if (!history.isEmpty()) {
            last = history.peek();
            last.unbindFrameBuffer();
        }
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.pointer);
        if (i >= 0) {
            GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + i);
            GL11.glDrawBuffer(GL11.GL_BACK);
            GL30.glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, RenderAPI.get().getWindow().getBufferWidth(),
                    RenderAPI.get().getWindow().getBufferHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        } else if (i == -1) {
            resolveDepth(RenderAPI.get().getWindow().getBufferWidth(), RenderAPI.get().getWindow().getBufferHeight());
        }
        if (last != null) {
            last.bindFrameBuffer();
        } else {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }
    }
    
    @Override
    public void resolveToFrameBuffer(final FrameBuffer target, final int attachment) {
        final GLFrameBuffer gltarget = (GLFrameBuffer) target;
        GLFrameBuffer last = null;
        if (!history.isEmpty()) {
            last = history.peek();
            last.unbindFrameBuffer();
        }
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, gltarget.pointer);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.pointer);
        if (attachment >= 0) {
            GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + attachment);
            GL30.glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, gltarget.width, gltarget.height,
                    GL11.GL_COLOR_BUFFER_BIT /* Should work w/o? | GL11.GL_DEPTH_BUFFER_BIT */, GL11.GL_NEAREST);
        } else if (attachment == -1) {
            resolveDepth(gltarget.width, gltarget.height);
        }
        if (last != null) {
            last.bindFrameBuffer();
        } else {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }
    }
    
    private void resolveDepth(int targetwidth, int targetheight) {
        
        GL11.glReadBuffer(GL30.GL_DEPTH_ATTACHMENT);
        GL30.glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, targetwidth, targetheight, GL11.GL_DEPTH_BUFFER_BIT,
                GL11.GL_NEAREST);
        //GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0); //not needed, done in resolveToFrameBuffer
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
    public int size() {
        return this.targets.length;
    }
    
    @Override
    public FrameBuffer resizedClone(final int newWidth, final int newHeight) {
        final FrameBuffer fb = new GLFrameBuffer(newWidth, newHeight, this.multisample, this.targets.length);
        fb.assignTargets(this.targets);
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
}
