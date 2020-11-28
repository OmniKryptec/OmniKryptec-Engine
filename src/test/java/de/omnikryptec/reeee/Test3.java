package de.omnikryptec.reeee;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.function.Consumer;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.core.Scene;
import de.omnikryptec.core.Omnikryptec.LoaderSetting;
import de.omnikryptec.demo.RendererDemo;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.window.IWindow;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.objects.SimpleSprite;
import de.omnikryptec.render.renderer.Renderer;
import de.omnikryptec.render.renderer.Renderer2D;
import de.omnikryptec.render.renderer.ViewManager;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.render3.Batch2D;
import de.omnikryptec.render3.Batch2D.Target;
import de.omnikryptec.render3.InstancedRectData;
import de.omnikryptec.resource.MeshData.Primitive;
import de.omnikryptec.resource.loadervpc.ResourceManager;
import de.omnikryptec.resource.parser.shader.ShaderParser;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class Test3 extends Omnikryptec {
    
    public static void main(final String[] args) {
        Omnikryptec.initialize(new Settings<>(), RenderAPI.OpenGL, new Settings<>(), new Settings<>(), false, false);
        final IWindow window = LibAPIManager.instance().getGLFW().getRenderAPI().getWindow();
        window.setVisible(true);
        final WindowUpdater updater = new WindowUpdater(window);
        final Color clearColor = new Color();
        Profiler.setEnabled(true);
        ResourceManager rm = new ResourceManager();
        rm.addDefaultLoaders();
        rm.load(false, false, "intern:/de/omnikryptec/resources/glslmodules/shader2d.glsl");
        Batch2D batch = new Batch2D();
        InstancedRectData[] ar = new InstancedRectData[10000];
        for (int i = 0; i < ar.length; i++) {
            Matrix3x2f trans = new Matrix3x2f();
            trans.setTranslation(-0.5f, -0.5f);
            trans.rotateLocal(3.14f / 4 + i);
            InstancedRectData d = new InstancedRectData();
            ar[i] = d;
            d.transform = trans;
        }
        System.out.println(GL46.glGetInteger(GL46.GL_MAX_TEXTURE_IMAGE_UNITS));
        
        while (!window.isCloseRequested()) {
            LibAPIManager.instance().getGLFW().getRenderAPI().getSurface().clearColor(clearColor);
            batch.begin(Target.Render);
            Profiler.begin("test3");
            for (InstancedRectData d : ar) {
                batch.draw(d);
            }
            batch.end();
            Profiler.end();
            updater.update();
        }
        System.out.println(Profiler.currentInfo());
        Omnikryptec.deinitialize();
    }
    
}
