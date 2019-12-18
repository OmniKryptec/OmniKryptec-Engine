package de.omnikryptec.gui;

import java.util.List;

public class TilingLayout implements GuiLayout {
    
    private final int columns;//Spalten
    private final int rows;//Zeilen
    
    public TilingLayout(final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;
    }
    
    @Override
    public void doLayout(final GuiComponent parent, final List<GuiComponent> children) {
        if (children.size() > this.rows * this.columns) {
            throw new IllegalStateException("not enough space");
        }
        final float width = parent.getConstraints().getMaxWidth() / this.columns;
        final float height = parent.getConstraints().getMaxWidth() / this.rows;
        for (int i = 0; i < children.size(); i++) {
            final int xIndex = i % this.columns;
            final int yIndex = i / this.rows;
            final float x = xIndex * width + parent.getConstraints().getX();
            final float y = yIndex * height + parent.getConstraints().getY();
            final GuiConstraints childsConstraints = new GuiConstraints(x, y, width, height);
            children.get(i).setConstraints(childsConstraints);
        }
    }
    
}
