package de.omnikryptec.graphics.render;

import java.util.ArrayList;

public class ArrayRenderList<T> implements RenderList<ArrayList<T>> {
    
    private ArrayList<T> arl = new ArrayList<>();
    
    @Override
    public void addObject(Object o) {
        arl.add((T) o);
    }
    
    @Override
    public ArrayList<T> get() {
        return arl;
    }
    
}
