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
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.opengl.texture.GLTexture;

public class GLFrameBuffer extends AutoDelete implements FrameBuffer {
    
    private static final Deque<GLFrameBuffer> history = new ArrayDeque<>();
    
    private int width;
    private int height;
    
    private int multisample;
    
    private FBTexture[] textures;
    
    private int[] renderbuffers;
    
    private final int pointer;
    
    public GLFrameBuffer(int width, int height, int multisample, FBTarget... targets) {
        this.pointer = GL30.glGenFramebuffers();
        this.width = width;
        this.height = height;
        this.multisample = multisample;
        if (multisample == 0) {
            textures = new FBTexture[targets.length];
        } else {
            renderbuffers = new int[targets.length];
        }
        init(targets);
    }
    
    private void init(FBTarget... targets) {
        bindFrameBuffer();
        IntBuffer drawBuffers = BufferUtils.createIntBuffer(targets.length);
        for (int i = 0; i < targets.length; i++) {
            if (!targets[i].isDepthAttachment) {
                drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0 + targets[i].attachmentIndex);
            }
        }
        drawBuffers.flip();
        GL20.glDrawBuffers(drawBuffers);
        int index = 0;
        for (FBTarget target : targets) {
            if (isRenderBuffer()) {
                int colorBuffer = GL30.glGenRenderbuffers();
                GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colorBuffer);
                GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, multisample,
                        OpenGLUtil.typeId(target.format), width, height);
                GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment(target), GL30.GL_RENDERBUFFER,
                        colorBuffer);
                renderbuffers[index] = colorBuffer;
            } else {
                FBTexture texture = new FBTexture();
                texture.bindTexture(0);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, OpenGLUtil.typeId(target.format), width, height, 0,
                        target.isDepthAttachment ? GL11.GL_DEPTH_COMPONENT : GL11.GL_RGBA,
                        target.isDepthAttachment ? GL11.GL_FLOAT : GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment(target), GL11.GL_TEXTURE_2D,
                        texture.textureId(), 0);
                textures[index] = texture;
            }
            index++;
        }
        unbindFrameBuffer();
    }
    
    private int attachment(FBTarget target) {
        if (target.isDepthAttachment) {
            return GL30.GL_DEPTH_ATTACHMENT;
        }
        return GL30.GL_COLOR_ATTACHMENT0 + target.attachmentIndex;
    }
    
    @Override
    public void bindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.pointer);
        GL11.glViewport(0, 0, width, height);
        history.push(this);
    }
    
    @Override
    public void unbindFrameBuffer() {
        if (history.peek() == this) {
            history.pop();
            if (history.size() == 0) {
                //TODO restore orgininal viewport
                GL11.glViewport(0, 0, 100, 100);
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
            } else if (history.size() > 0) {
                final GLFrameBuffer before = history.pop();
                before.bindFrameBuffer();
            }
        }
    }
    
    @Override
    public boolean isRenderBuffer() {
        return textures == null;
    }
    
    @Override
    public Texture getTexture(final int i) {
        return this.textures[i];
    }
    
    @Override
    protected void deleteRaw() {
        if (isRenderBuffer()) {
            for (int i : renderbuffers) {
                GL30.glDeleteRenderbuffers(i);
            }
        } else {
            for (final FBTexture t : this.textures) {
                if (t != null) {
                    t.delete();
                }
            }
        }
        GL30.glDeleteFramebuffers(this.pointer);
    }
    
    private class FBTexture extends GLTexture {
        
        private FBTexture() {
            super(GL11.GL_TEXTURE_2D);
        }
        
        @Override
        public int getWidth() {
            return GLFrameBuffer.this.width;
        }
        
        @Override
        public int getHeight() {
            return GLFrameBuffer.this.height;
        }
        
    }
    
    @Override
    public void resolveToScreen() {
        GLFrameBuffer last = null;
        if (!history.isEmpty()) {
            last = history.peek();
            last.unbindFrameBuffer();
        }
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, pointer);
        GL11.glDrawBuffer(GL11.GL_BACK);
        //TODO use original viewport
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        if (last != null) {
            last.bindFrameBuffer();
        } else {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }
    }
    
    @Override
    public void resolveToFrameBuffer(FrameBuffer target, int attachment, boolean resolveDepth) {
        GLFrameBuffer gltarget = (GLFrameBuffer) target;
        GLFrameBuffer last = null;
        if (!history.isEmpty()) {
            last = history.peek();
            last.unbindFrameBuffer();
        }
        if (attachment >= 0) {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, gltarget.pointer);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, pointer);
            GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + attachment);
            GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, gltarget.width, gltarget.height,
                    GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        }
        if (resolveDepth) {
            resolveDepth(gltarget);
        }
        if (last != null) {
            last.bindFrameBuffer();
        } else {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }
    }
    
    private void resolveDepth(GLFrameBuffer target) {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target.pointer);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, pointer);
        GL11.glReadBuffer(GL30.GL_DEPTH_ATTACHMENT);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, target.width, target.height, GL11.GL_DEPTH_BUFFER_BIT,
                GL11.GL_NEAREST);
        //GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0); //not needed, done above
    }
}
