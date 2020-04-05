package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.omnikryptec.gui.GuiRenderer;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.updater.Time;

public class ViewManager {

    private static final Comparator<Renderer> REND_COMP = (r1, r2) -> r1.priority() - r2.priority();

    public static interface EnvironmentKey {
    }

    public static enum GlobalEnvironmentKeys implements Defaultable, EnvironmentKey {
        ClearColor(new Color(0, 0, 0, 0));

        private final Object def;

        private GlobalEnvironmentKeys(final Object def) {
            this.def = def;
        }

        @Override
        public <T> T getDefault() {
            return (T) this.def;
        }
    }

    private View mainView;

    private final List<Renderer> renderers;
    private final Map<String, View> namedViews;

    private boolean isInAction;

    public ViewManager() {
        this.renderers = new ArrayList<>();
        this.namedViews = new HashMap<>();
        this.isInAction = false;
        this.mainView = new View();
    }

    public void addView(String name, View view) {
        this.namedViews.put(name, view);
    }

    public void removeView(String name) {
        this.namedViews.remove(name);
    }

    public void setMainView(View newView) {
        this.mainView = newView;
    }

    public View getMainView() {
        return this.mainView;
    }

    public void addRenderer(Renderer r) {
        this.renderers.add(r);
        this.renderers.sort(REND_COMP);
        r.init(this, LibAPIManager.instance().getGLFW().getRenderAPI());
    }

    public void removeRenderer(GuiRenderer renderer) {
        renderer.deinit(this, LibAPIManager.instance().getGLFW().getRenderAPI());
        this.renderers.remove(renderer);
    }

    //TODO pcfreak9000 include the renderapi in the ViewManager?

    public void renderInstance(Time time) {
        if (this.isInAction) {
            throw new IllegalStateException("Can't call renderInstance recursively");
        }
        this.isInAction = true;
        try {
            for (View view : this.namedViews.values()) {
                renderView(view, time);
            }
            renderView(this.mainView, time);
        } finally {
            this.isInAction = false;
        }
    }

    public void renderView(View view, Time time) {
        view.getTargetFbo().bindFrameBuffer();
        view.getTargetFbo().clearComplete(view.getEnvironment().get(GlobalEnvironmentKeys.ClearColor));
        for (Renderer renderer : this.renderers) {
            renderer.render(this, LibAPIManager.instance().getGLFW().getRenderAPI(), view.getProjection(),
                    view.getTargetFbo(), view.getEnvironment(), time);
        }
        view.getTargetFbo().unbindFrameBuffer();
    }

    public Renderer2D createAndAddRenderer2D() {
        Renderer2D r = new Renderer2D();
        addRenderer(r);
        return r;
    }

    public AdvancedRenderer2D createAndAddAdvancedRenderer2D() {
        AdvancedRenderer2D r = new AdvancedRenderer2D();
        addRenderer(r);
        return r;
    }

}
