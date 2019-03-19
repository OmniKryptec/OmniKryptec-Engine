package de.omnikryptec.core.update;

import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBuffer;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.updater.Time;

public class UpdateableFactory {
    
    public static IECSManager createDefaultIECSManager() {
        return IECSManager.createDefault();
    }
    
    public static AbstractUpdateable createScreenClearTest() {
        return new AbstractUpdateable(false) {
            @Override
            public void update(final Time time, UpdatePass pass) {
                if (pass != UpdatePass.PRE) {
                    return;
                }
                if (time.opCount % 40 == 0) {
                    RenderAPI.get().setClearColor(new Color().randomizeRGB());
                }
                RenderAPI.get().clear(SurfaceBuffer.Color);
            }
        };
    }
    
}
