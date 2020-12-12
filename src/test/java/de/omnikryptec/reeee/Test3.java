package de.omnikryptec.reeee;

import java.util.Arrays;
import java.util.List;

import org.joml.Matrix3x2f;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.IWindow;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.render3.Batch2D;
import de.omnikryptec.render3.BatchCache;
import de.omnikryptec.render3.InstanceData;
import de.omnikryptec.render3.instancedrect.DefaultInstanceRectData;
import de.omnikryptec.render3.instancedrect.DefaultInstancedRectRenderer;
import de.omnikryptec.render3.instancedrect.InstancedRectBatchedRenderer;
import de.omnikryptec.render3.instancedrect.InstancedRectData;
import de.omnikryptec.resource.loadervpc.ResourceManager;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Logger.LogType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.Settings;

public class Test3 extends Omnikryptec {
    
    public static void main(final String[] args) {
        Omnikryptec.initialize(new Settings<>(), RenderAPI.OpenGL, new Settings<>(), new Settings<>(), false, false);
        Logger.setMinLogType(LogType.Debug);
        final IWindow window = LibAPIManager.instance().getGLFW().getRenderAPI().getWindow();
        window.setVisible(true);
        final WindowUpdater updater = new WindowUpdater(window);
        final Color clearColor = new Color();
        Profiler.setEnabled(true);
        ResourceManager rm = new ResourceManager();
        rm.addDefaultLoaders();
        rm.load(false, false, "intern:/de/omnikryptec/resources/glslmodules/shader2d.glsl");
        Batch2D batch = new Batch2D();
        DefaultInstanceRectData[] ar = new DefaultInstanceRectData[10000];
        ar[0] = null;
        for (int i = 1; i < ar.length; i++) {
            Matrix3x2f trans = new Matrix3x2f();
            trans.setTranslation(-0.5f, -0.5f);
            trans.rotateLocal(3.14f / 4 + i);
            DefaultInstanceRectData d = new DefaultInstanceRectData();
            ar[i] = d;
            d.transform = trans;
        }
        List<InstanceData> list = Arrays.asList(ar);
        batch.setInstance(DefaultInstancedRectRenderer.class, new DefaultInstancedRectRenderer(true));
        batch.begin();
        batch.drawList(DefaultInstancedRectRenderer.class, list);
        List<BatchCache> bcl = batch.end();
        batch.setInstance(DefaultInstancedRectRenderer.class, new DefaultInstancedRectRenderer(false));
        //OpenGLUtil.setPolyMode(PolyMode.LINE);
        while (!window.isCloseRequested()) {
            LibAPIManager.instance().getGLFW().getRenderAPI().getSurface().clearColor(clearColor);
            batch.begin();
            Profiler.begin("test3");
            batch.drawCache(bcl);
            //batch.drawList(DefaultInstancedRectRenderer.class, list);
            //            for (InstancedRectData d : ar) {
            //                batch.draw(d);
            //            }
            batch.end();
            Profiler.end();
            updater.update();
        }
        System.out.println(Profiler.currentInfo());
        Omnikryptec.deinitialize();
    }
    
}
