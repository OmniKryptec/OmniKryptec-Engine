package de.omnikryptec.core;

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.render.renderer.LocalRendererContext;
import de.omnikryptec.util.updater.Time;

public class Scene {
    
    private IUpdatable gameLogic;
    
    private LocalRendererContext renderer;
    private Game game;
    
    private int priority;
    
    Scene(LocalRendererContext context, Game game, int prio) {
        this.renderer = context;
        this.game = game;
        this.priority = prio;
    }
    
    public void updateScene(Time time) {
        if (hasGameLogic()) {
            gameLogic.update(time);
        }
    }
    
    public boolean hasGameLogic() {
        return gameLogic != null;
    }
    
    public void setGameLogic(IUpdatable updatable) {
        this.gameLogic = updatable;
    }
    
    public IUpdatable getGameLogic() {
        return gameLogic;
    }
    
    public LocalRendererContext getRendering() {
        return renderer;
    }
    
    public void setPriority(int i) {
        this.priority = i;
        game.notifyPriorityChange();
    }
    
    public int priority() {
        return priority;
    }
    
}
