package de.omnikryptec.libapi.exposed.window;

import de.omnikryptec.util.settings.Defaultable;

public enum WindowSetting implements Defaultable{
    Width(800), Height(600), Fullscreen(false), Name("Display"), Resizeable(true), LockAspectRatio(false),
    /**
     * @see WindowInterfaceWIP#setVSync(boolean)
     * @see de.omnikryptec.core.scene.UpdateController#setSyncUpdatesPerSecond(int)
     * @see de.omnikryptec.core.scene.UpdateController#setAsyncUpdatesPerSecond(int)
     */
    VSync(true);
    
    private final Object def;
    
    WindowSetting(final Object def) {
        this.def = def;
    }
    
    @Override
    public <T> T getDefault() {
        return (T) this.def;
    }
}
