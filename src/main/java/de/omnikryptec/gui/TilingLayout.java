package de.omnikryptec.gui;

import java.util.List;

public class TilingLayout implements GuiLayout {
    
    private int columns;//Spalten
    private int rows;//Zeilen
    
    public TilingLayout(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }
    
    @Override
    public void doLayout(GuiComponent parent, List<GuiComponent> children) {
        if (children.size() > rows * columns) {
            throw new IllegalStateException("not enough space");
        }
        float width = parent.getConstraints().getMaxWidth() / columns;
        float height = parent.getConstraints().getMaxWidth() / rows;
        for (int i = 0; i < children.size(); i++) {
            int xIndex = i % columns;
            int yIndex = i / rows;
            float x = xIndex * width + parent.getConstraints().getX();
            float y = yIndex * height + parent.getConstraints().getY();
            GuiConstraints childsConstraints = new GuiConstraints(x, y, width, height);
            children.get(i).setConstraints(childsConstraints);
        }
    }
    
}
