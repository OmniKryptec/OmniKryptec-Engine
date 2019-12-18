package de.omnikryptec.gui;

import java.util.List;

public interface GuiLayout {
    
    void doLayout(GuiComponent parent, List<GuiComponent> children);
    
}
