package de.omnikryptec.render.batch.module;

import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.render.batch.vertexmanager.VertexManager;

public class ModuleBatchingManager {

    public static enum QuadSide {
        TopLeft, TopRight, BotLeft, BotRight;
    }

    private static final QuadSide[] ARRANGED = { QuadSide.TopLeft, QuadSide.TopRight, QuadSide.BotLeft,
            QuadSide.TopRight, QuadSide.BotRight, QuadSide.BotLeft };
    private static final QuadSide[] SIDES = QuadSide.values();

    private final Module[] modules;
    private final float[] global;
    private final float[][] local = new float[SIDES.length][];
    private final int totalFloatsPerVertex;

    public ModuleBatchingManager(final Module... modules) {
        this.modules = modules;
        int sideCount = 0;
        int globalCount = 0;
        for (final Module m : modules) {
            if (m.sideIndependant()) {
                globalCount += m.size();
            } else {
                sideCount += m.size();
            }
        }
        this.totalFloatsPerVertex = globalCount + sideCount;
        this.global = new float[globalCount];
        for (int i = 0; i < this.local.length; i++) {
            this.local[i] = new float[sideCount];
        }
    }

    public VertexBufferLayout createLayout() {
        final VertexBufferLayout layout = new VertexBufferLayout();
        for (final Module m : this.modules) {
            if (m.sideIndependant()) {
                layout.push(Type.FLOAT, m.size(), false);
            }
        }
        for (final Module m : this.modules) {
            if (!m.sideIndependant()) {
                layout.push(Type.FLOAT, m.size(), false);
            }
        }
        return layout;
    }

    public Module[] getModules() {
        return this.modules.clone();
    }

    public int floatsPerVertex() {
        return this.totalFloatsPerVertex;
    }

    public void issueVertices(final Texture texture, final VertexManager manager) {
        manager.prepareNext(texture, this.totalFloatsPerVertex * ARRANGED.length);
        int globalindex = 0;
        int localindex = 0;
        for (final Module m : this.modules) {
            if (m.sideIndependant()) {
                m.visit(this.global, null, globalindex);
                globalindex += m.size();
            } else {
                for (int i = 0; i < SIDES.length; i++) {
                    m.visit(this.local[i], SIDES[i], localindex);
                }
                localindex += m.size();
            }
        }
        for (final QuadSide q : ARRANGED) {
            manager.addData(this.global);
            manager.addData(this.local[q.ordinal()]);
        }
    }

    public void issuePreComputed(final Texture texture, final VertexManager manager, final float[] floats,
            final int start, final int length) {
        if (length % this.totalFloatsPerVertex != 0) {
            throw new IllegalArgumentException();
        }
        manager.prepareNext(texture, length);
        manager.addData(floats, start, length);
    }

}
