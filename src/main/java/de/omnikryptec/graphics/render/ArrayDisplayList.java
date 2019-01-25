package de.omnikryptec.graphics.render;

import java.util.ArrayList;

public class ArrayDisplayList<T> implements DisplayList {
    
    private ArrayList<T> arl = new ArrayList<>();
    
    @Override
    public void addObject(Object o) {
        arl.add((T) o);
    }
    
    public ArrayList<T> get() {
        return arl;
    }
    
}
