package de.omnikryptec.core;

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.render.renderer.LocalRendererContext;
import de.omnikryptec.util.updater.Time;

public class Scene {

    private IUpdatable gameLogic;

    private final LocalRendererContext renderer;
    private final Game game;

    private int priority;

    Scene(final LocalRendererContext context, final Game game, final int prio) {
        this.renderer = context;
        this.game = game;
        setPriority(prio);
    }

    public void updateScene(final Time time) {
        if (hasGameLogic()) {
            this.gameLogic.update(time);
        }
    }

    public boolean hasGameLogic() {
        return this.gameLogic != null;
    }

    public void setGameLogic(final IUpdatable updatable) {
        this.gameLogic = updatable;
    }

    public IUpdatable getGameLogic() {
        return this.gameLogic;
    }

    public LocalRendererContext getRendering() {
        return this.renderer;
    }

    public void setPriority(final int i) {
        this.priority = i;
        this.game.notifyPriorityChange();
        this.renderer.setPriority(this.priority);
    }

    public int priority() {
        return this.priority;
    }

}
