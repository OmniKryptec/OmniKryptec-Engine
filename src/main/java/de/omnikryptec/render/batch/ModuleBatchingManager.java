package de.omnikryptec.render.batch;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;

import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;

public class ModuleBatchingManager {
    
    public static enum QuadSide {
        TopLeft, TopRight, BotLeft, BotRight;
    }
    
    private static final QuadSide[] ARRANGED = { QuadSide.TopLeft, QuadSide.TopRight, QuadSide.BotLeft,
            QuadSide.TopRight, QuadSide.BotRight, QuadSide.BotLeft };
    private static final QuadSide[] SIDES = QuadSide.values();
    
    private Module[] modules;
    private float[] global;
    private float[][] local = new float[SIDES.length][];
    private int totalFloatsPerVertex;
    
    public ModuleBatchingManager(Module... modules) {
        this.modules = modules;
        int sideCount = 0;
        int globalCount = 0;
        for (Module m : modules) {
            if (m.sideIndependant()) {
                globalCount += m.size();
            } else {
                sideCount += m.size();
            }
        }
        this.totalFloatsPerVertex = globalCount + sideCount;
        global = new float[globalCount];
        for (int i = 0; i < local.length; i++) {
            local[i] = new float[sideCount];
        }
    }
    
    public VertexBufferLayout createLayout() {
        VertexBufferLayout layout = new VertexBufferLayout();
        for (Module m : modules) {
            if (m.sideIndependant()) {
                layout.push(Type.FLOAT, m.size(), false);
            }
        }
        for (Module m : modules) {
            if (!m.sideIndependant()) {
                layout.push(Type.FLOAT, m.size(), false);
            }
        }
        return layout;
    }
    
    public Module[] getModules() {
        return modules.clone();
    }
    
    public void issueVertices(Texture baseTexture, VertexManager manager) {
        manager.prepareNext(baseTexture, totalFloatsPerVertex * ARRANGED.length);
        int globalindex = 0;
        int localindex = 0;
        for (Module m : modules) {
            if (m.sideIndependant()) {
                m.visit(global, null, globalindex);
                globalindex += m.size();
            } else {
                for (int i = 0; i < SIDES.length; i++) {
                    m.visit(local[i], SIDES[i], localindex);
                }
                localindex += m.size();
            }
        }
        for (QuadSide q : ARRANGED) {
            manager.addData(global);
            manager.addData(local[q.ordinal()]);
        }
    }
    
    public void issuePreComputed(Texture baseTexture, VertexManager manager, float[] floats, int start, int length) {
        if (length % totalFloatsPerVertex != 0) {
            throw new IllegalArgumentException();
        }
        manager.prepareNext(baseTexture, length);
        manager.addData(floats, start, length);
    }
    
}
