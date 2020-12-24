package de.omnikryptec.reeee;

import java.util.Arrays;
import java.util.List;

import org.joml.Matrix3x2f;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.window.IWindow;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.render3.d2.BatchCache;
import de.omnikryptec.render3.d2.BatchManager2D;
import de.omnikryptec.render3.d2.InstanceData;
import de.omnikryptec.render3.d2.instanced.InstancedBatch2D;
import de.omnikryptec.render3.d2.instanced.InstancedData;
import de.omnikryptec.resource.helper.TextureHelper;
import de.omnikryptec.resource.loadervpc.ResourceManager;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Logger.LogType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.Settings;

public class Test3 extends Omnikryptec {
    
    public static void main(String[] args) {
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
        rm.load(false, false, "intern:/de/omnikryptec/resources/");
        TextureHelper texHelper = new TextureHelper(rm.getProvider());
        Texture t = texHelper.get("candara.png");
        BatchManager2D batch = new BatchManager2D();
        InstancedData[] ar = new InstancedData[10000];
        ar[0] = null;
        for (int i = 1; i < ar.length; i++) {
            Matrix3x2f trans = new Matrix3x2f();
            trans.setTranslation(-0.5f, -0.5f);
            //            trans.rotateLocal(3.14f / 4 + i);
            InstancedData d = new InstancedData();
            ar[i] = d;
            d.getTransform().set(trans);
            d.setUVAndTexture(t);
        }
        List<InstanceData> list = Arrays.asList(ar);
        batch.setInstance(new InstancedBatch2D(true));
        batch.drawList(InstancedBatch2D.class, list);
        List<BatchCache> bcl = batch.flush();
        batch.setInstance(new InstancedBatch2D(false));
        batch.setAutoclear(false);
        batch.drawCache(bcl);
        while (!window.isCloseRequested()) {
            LibAPIManager.instance().getGLFW().getRenderAPI().getSurface().clearColor(clearColor);
            Profiler.begin("test3");
            batch.flush();
            Profiler.end();
            updater.update();
        }
        System.out.println(Profiler.currentInfo());
        Omnikryptec.deinitialize();
    }
    
}
