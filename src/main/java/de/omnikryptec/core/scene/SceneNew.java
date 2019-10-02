package de.omnikryptec.core.scene;

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.render.renderer.LocalRendererContext;
import de.omnikryptec.util.updater.Time;

public class SceneNew {
    
    private IUpdatable gameLogic;
    
    private LocalRendererContext renderer;
    
    public SceneNew(LocalRendererContext context) {
        this.renderer = context;
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
    
    public LocalRendererContext getRenderer() {
        return renderer;
    }
    
}
